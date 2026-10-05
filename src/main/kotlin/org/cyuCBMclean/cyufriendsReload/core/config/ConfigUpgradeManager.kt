package org.cyuCBMclean.cyufriendsReload.core.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ConfigUpgradeManager {
    const val CURRENT_CONFIG_VERSION = 116
    const val CURRENT_CONFIG_LAYOUT = 1

    private val ignoredPaths = setOf("config-version", "config-layout")
    private val langResources = listOf("lang/zh_cn.yml", "lang/en_us.yml")

    data class UpgradeResult(
        val upgraded: Boolean,
        val backupFolder: File? = null,
        val failed: Boolean = false,
        val migratedValues: Int = 0
    )

    fun prepare(plugin: JavaPlugin): UpgradeResult {
        val dataFolder = plugin.dataFolder
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) return UpgradeResult(upgraded = false)

        val config = YamlConfiguration.loadConfiguration(configFile)
        val version = config.getInt("config-version", 0)
        val layout = config.getInt("config-layout", 0)
        if (version >= CURRENT_CONFIG_VERSION && layout >= CURRENT_CONFIG_LAYOUT) {
            return UpgradeResult(upgraded = false)
        }

        return runCatching {
            val backupFolder = createBackupFolder(plugin, dataFolder)
            backupRootFile(dataFolder, backupFolder, "config.yml")
            backupRootFile(dataFolder, backupFolder, "sounds.yml")

            val legacyMessages = File(dataFolder, "messages.yml")
            if (legacyMessages.exists()) {
                legacyMessages.copyTo(File(backupFolder, "messages.yml"), overwrite = false)
            }
            backupLang(dataFolder, backupFolder)
            backupGui(dataFolder, backupFolder)

            val templateResource = plugin.getResource("config.yml")
                ?: throw IllegalStateException("插件 jar 中缺少 config.yml")
            val templateText = templateResource.use { it.reader(Charsets.UTF_8).readText() }
            val editor = TemplateValueWriter(templateText)

            var migratedCount = 0
            for ((path, value) in config.getValues(true)) {
                if (value is ConfigurationSection || path in ignoredPaths) continue
                if (editor.set(path, value)) {
                    migratedCount++
                }
            }
            editor.set("config-version", CURRENT_CONFIG_VERSION)
            editor.set("config-layout", CURRENT_CONFIG_LAYOUT)

            val tempConfigFile = File(dataFolder, ".config.yml.$CURRENT_CONFIG_VERSION.tmp")
            tempConfigFile.writeText(editor.text(), Charsets.UTF_8)
            replaceTemplate(tempConfigFile, configFile)

            for (langPath in langResources) {
                val liveLangFile = File(dataFolder, langPath)
                val resource = plugin.getResource(langPath) ?: continue
                val langTemplateText = resource.use { it.reader(Charsets.UTF_8).readText() }
                if (liveLangFile.exists()) {
                    val liveLang = YamlConfiguration.loadConfiguration(liveLangFile)
                    val langEditor = TemplateValueWriter(langTemplateText)
                    for ((path, value) in liveLang.getValues(true)) {
                        if (value is ConfigurationSection) continue
                        langEditor.set(path, value)
                    }
                    val tempLangFile = File(dataFolder, ".${langPath.replace('/', '_')}.$CURRENT_CONFIG_VERSION.tmp")
                    tempLangFile.writeText(langEditor.text(), Charsets.UTF_8)
                    replaceTemplate(tempLangFile, liveLangFile)
                } else {
                    liveLangFile.parentFile?.mkdirs()
                    val tempLangFile = File(dataFolder, ".${langPath.replace('/', '_')}.$CURRENT_CONFIG_VERSION.tmp")
                    tempLangFile.writeText(langTemplateText, Charsets.UTF_8)
                    replaceTemplate(tempLangFile, liveLangFile)
                }
            }

            writeUpgradeNote(backupFolder, plugin, version, layout, migratedCount)

            plugin.logger.info("检测到旧版配置（版本: ${if (version > 0) version else "未标记"}, 布局: $layout），已备份至 backup/${backupFolder.name}")
            plugin.logger.info("已自动迁移并继承 $migratedCount 项自定义参数，升级至配置版本 $CURRENT_CONFIG_VERSION (布局协议 $CURRENT_CONFIG_LAYOUT)")
            UpgradeResult(upgraded = true, backupFolder = backupFolder, migratedValues = migratedCount)
        }.getOrElse { ex ->
            plugin.logger.severe("配置升级未完成，原文件备份已保留在 backup 目录：${ex.message}")
            UpgradeResult(upgraded = false, failed = true)
        }
    }

    private fun backupRootFile(dataFolder: File, backupFolder: File, fileName: String) {
        val file = File(dataFolder, fileName)
        if (file.exists()) file.copyTo(File(backupFolder, fileName), overwrite = false)
    }

    private fun backupLang(dataFolder: File, backupFolder: File) {
        val langFolder = File(dataFolder, "lang")
        if (!langFolder.exists()) return
        val backupLangFolder = File(backupFolder, "lang")
        if (!backupLangFolder.exists() && !backupLangFolder.mkdirs()) {
            throw IllegalStateException("无法创建语言文件备份目录 ${backupLangFolder.path}")
        }
        langFolder.listFiles { file -> file.isFile && file.extension.equals("yml", true) }
            ?.forEach { file -> file.copyTo(File(backupLangFolder, file.name), overwrite = false) }
    }

    private fun backupGui(dataFolder: File, backupFolder: File) {
        val guiFolder = File(dataFolder, "gui")
        if (!guiFolder.exists()) return
        val backupGuiFolder = File(backupFolder, "gui")
        if (!backupGuiFolder.exists() && !backupGuiFolder.mkdirs()) {
            throw IllegalStateException("无法创建界面备份目录 ${backupGuiFolder.path}")
        }
        guiFolder.listFiles { file -> file.isFile && file.extension.equals("yml", true) }
            ?.forEach { file -> file.copyTo(File(backupGuiFolder, file.name), overwrite = false) }
    }

    private fun replaceTemplate(source: File, target: File) {
        try {
            Files.move(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun writeUpgradeNote(
        backupFolder: File,
        plugin: JavaPlugin,
        version: Int,
        layout: Int,
        migratedCount: Int
    ) {
        File(backupFolder, "upgrade-note.txt").writeText(
            buildString {
                appendLine("CyuFriends-Reload 已为 ${plugin.description.version} 生成新的默认配置、语言文件与界面")
                appendLine("旧配置文件与界面已完整备份至当前目录")
                appendLine("主配置与语言文件已自动继承历史自定义参数（共继承 $migratedCount 项），无需手动重新配置")
                appendLine("数据库与本地存储数据不受任何影响：sqlite 与 mysql 数据会继续使用")
                appendLine("如果你曾深度定制过界面布局，可对照 backup 里的 gui 文件将自定义按钮调整回 plugins/cyufriends-reload/gui")
                appendLine()
                appendLine("旧配置版本: ${if (version > 0) version else "未标记"}")
                appendLine("旧布局协议: $layout")
                appendLine("新配置版本: $CURRENT_CONFIG_VERSION")
                appendLine("新布局协议: $CURRENT_CONFIG_LAYOUT")
            },
            Charsets.UTF_8
        )
    }

    private fun createBackupFolder(plugin: JavaPlugin, dataFolder: File): File {
        val backupRoot = File(dataFolder, "backup")
        if (!backupRoot.exists()) backupRoot.mkdirs()

        val timestamp = SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.ROOT).format(Date())
        var folder = File(backupRoot, "${plugin.description.version}-$timestamp")
        var index = 2
        while (folder.exists()) {
            folder = File(backupRoot, "${plugin.description.version}-$timestamp-$index")
            index++
        }
        if (!folder.mkdirs()) {
            throw IllegalStateException("无法创建备份目录 ${folder.path}")
        }
        return folder
    }

    private class TemplateValueWriter(source: String) {
        private val lines = source.replace("\r\n", "\n").split('\n').toMutableList()

        fun set(path: String, value: Any): Boolean {
            val index = findPath(path) ?: return false
            val indent = indentation(lines[index])
            val encoded = encode(value)
            if (encoded.size == 1) {
                val separator = lines[index].indexOf(':')
                val comment = inlineComment(lines[index].substring(separator + 1))
                lines[index] = lines[index].substring(0, separator + 1) + " " + encoded[0] + comment
                return true
            }

            val separator = lines[index].indexOf(':')
            lines[index] = lines[index].substring(0, separator + 1)
            val nextKey = nextSibling(index, indent)
            var contentEnd = nextKey
            while (contentEnd > index + 1 && lines[contentEnd - 1].trim().let { it.isEmpty() || it.startsWith("#") }) {
                contentEnd--
            }
            repeat(contentEnd - index - 1) { lines.removeAt(index + 1) }
            lines.addAll(index + 1, encoded.drop(1).map { " ".repeat(indent) + it })
            return true
        }

        fun text(): String = lines.joinToString("\n").trimEnd() + "\n"

        private fun findPath(path: String): Int? {
            val expected = path.split('.')
            val stack = ArrayList<Pair<Int, String>>()
            for ((index, line) in lines.withIndex()) {
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("- ")) continue
                val separator = trimmed.indexOf(':')
                if (separator <= 0) continue
                val indent = indentation(line)
                while (stack.isNotEmpty() && stack.last().first >= indent) stack.removeAt(stack.lastIndex)
                val key = trimmed.substring(0, separator).trim().trim('\'', '"')
                if (stack.map { it.second } + key == expected) return index
                stack += indent to key
            }
            return null
        }

        private fun nextSibling(index: Int, baseIndent: Int): Int {
            for (cursor in index + 1 until lines.size) {
                val trimmed = lines[cursor].trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("- ")) continue
                if (indentation(lines[cursor]) <= baseIndent) return cursor
            }
            return lines.size
        }

        private fun encode(value: Any): List<String> {
            return when (value) {
                is String -> listOf(quoteScalar(value))
                is Boolean, is Number -> listOf(value.toString())
                is List<*> -> {
                    if (value.isEmpty()) {
                        listOf("[]")
                    } else {
                        listOf("") + value.map { item -> "  - ${encodeListItem(item)}" }
                    }
                }
                is Map<*, *> -> {
                    val yaml = YamlConfiguration()
                    value.forEach { (key, item) ->
                        if (key != null && item != null) yaml.set(key.toString(), item)
                    }
                    listOf("") + yaml.saveToString().trimEnd().lines().map { "  $it" }
                }
                else -> {
                    val yaml = YamlConfiguration()
                    yaml.set("value", value)
                    yaml.saveToString().trimEnd().lines().mapIndexed { index, line ->
                        if (index == 0) line.substringAfter(':').trimStart() else line
                    }.let { encoded ->
                        if (encoded.size == 1) encoded else listOf("") + encoded.drop(1)
                    }
                }
            }
        }

        private fun encodeListItem(item: Any?): String {
            return when (item) {
                null -> "null"
                is String -> quoteScalar(item)
                is Boolean, is Number -> item.toString()
                else -> quoteScalar(item.toString())
            }
        }

        private fun quoteScalar(value: String): String {
            val escaped = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
            return "\"$escaped\""
        }

        private fun inlineComment(valueText: String): String {
            val commentIndex = valueText.indexOf(" #")
            return if (commentIndex >= 0) valueText.substring(commentIndex) else ""
        }

        private fun indentation(line: String): Int = line.indexOfFirst { !it.isWhitespace() }.let { if (it < 0) 0 else it }
    }
}
