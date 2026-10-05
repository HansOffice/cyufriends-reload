package org.cyuCBMclean.cyufriendsReload.core.config

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.Locale

class LanguageEngine(private val plugin: Plugin) {

    private val miniMessage = MiniMessage.miniMessage()
    private val messageCache = mutableMapOf<String, String>()
    private val missingKeysWarned = mutableSetOf<String>()

    fun initialize() {
        reload()
    }

    fun reload() {
        val langDir = File(plugin.dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }
        val legacyMessages = File(plugin.dataFolder, "messages.yml")
        val zhFile = File(langDir, "zh_cn.yml")
        if (!zhFile.exists()) {
            if (legacyMessages.exists()) {
                legacyMessages.copyTo(zhFile, overwrite = true)
            } else {
                plugin.saveResource("lang/zh_cn.yml", false)
            }
        }
        val enFile = File(langDir, "en_us.yml")
        if (!enFile.exists()) {
            plugin.saveResource("lang/en_us.yml", false)
        }

        val langCode = plugin.config.getString("language", "zh_cn")?.lowercase(Locale.ROOT) ?: "zh_cn"
        val activeFile = File(langDir, "$langCode.yml").let {
            if (it.exists()) it else zhFile
        }

        val yaml = YamlConfiguration().apply { load(activeFile) }
        val nextMessages = yaml.getKeys(true)
            .filter { yaml.isString(it) }
            .associateWith { yaml.getString(it)!! }
        require(nextMessages.isNotEmpty()) { "语言文件 ${activeFile.name} 没有可用语言键" }

        messageCache.clear()
        messageCache.putAll(nextMessages)
        missingKeysWarned.clear()
    }

    fun send(sender: CommandSender, key: String, vararg placeholders: TagResolver) {
        val raw = messageCache[key] ?: run {
            if (missingKeysWarned.add(key)) {
                plugin.logger.warning("语言文件缺少语言键: $key")
            }
            return
        }
        if (raw.isBlank()) return

        val prefix = messageCache["prefix"] ?: ""
        sendComponent(sender, deserializeSafely(prefix + raw, *placeholders))
    }

    fun sendRaw(sender: CommandSender, raw: String, vararg placeholders: TagResolver) {
        if (raw.isBlank()) return
        sendComponent(sender, deserializeSafely(raw, *placeholders))
    }

    fun component(key: String, placeholders: Map<String, String> = emptyMap(), includePrefix: Boolean = false): Component? {
        val raw = messageCache[key] ?: run {
            if (missingKeysWarned.add(key)) {
                plugin.logger.warning("语言文件缺少语言键: $key")
            }
            return null
        }
        if (raw.isBlank()) return null
        val content = if (includePrefix) (messageCache["prefix"] ?: "") + raw else raw
        return deserializeSafely(content, *toResolvers(placeholders))
    }

    private fun toResolvers(placeholders: Map<String, String>): Array<TagResolver> {
        return placeholders.map { Placeholder.unparsed(it.key, it.value) }.toTypedArray()
    }

    private fun deserializeSafely(raw: String, vararg placeholders: TagResolver): Component {
        return runCatching {
            miniMessage.deserialize(raw, *placeholders)
        }.getOrElse { exception ->
            plugin.logger.warning("无法解析语言文件中的 MiniMessage 文本：$raw")
            plugin.logger.warning("原因：${exception.message}")
            Component.text(raw)
        }
    }

    fun sendComponent(sender: CommandSender, component: Component) {
        if (sender is Audience) {
            sender.sendMessage(component)
        } else {
            sender.sendMessage(ColorCompat.serialize(component))
        }
    }
}
