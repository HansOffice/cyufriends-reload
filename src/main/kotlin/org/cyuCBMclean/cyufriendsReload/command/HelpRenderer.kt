package org.cyuCBMclean.cyufriendsReload.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.cyuCBMclean.cyufriendsReload.CyufriendsReload

object HelpRenderer {

    private const val PAGE_SIZE = 8
    private val miniMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()

    fun availableCategories(sender: CommandSender): List<HelpCategory> {
        val plugin = CyufriendsReload.instance
        return HELP_CATEGORIES.filter { cat ->
            (cat.permission == null || sender.hasPermission(cat.permission)) &&
            (cat.module == null || plugin.moduleManager.isEnabled(cat.module))
        }
    }

    fun availableHelp(sender: CommandSender, categoryId: String? = null): List<HelpLine> {
        val plugin = CyufriendsReload.instance
        return HELP_LINES.filter { line ->
            (categoryId == null || line.category.equals(categoryId, ignoreCase = true)) &&
            (line.permission == null || sender.hasPermission(line.permission)) &&
            (line.module == null || plugin.moduleManager.isEnabled(line.module))
        }
    }

    fun tabCompletions(sender: CommandSender, args: List<String>): List<String> {
        if (args.isEmpty()) return emptyList()
        val categories = availableCategories(sender)
        if (args.size <= 1) {
            val currentArg = args.getOrElse(0) { "" }
            val totalPages = maxOf(1, (availableHelp(sender).size + PAGE_SIZE - 1) / PAGE_SIZE)
            val pages = (1..totalPages).map(Int::toString)
            val catIds = categories.map { it.id }
            val candidates = pages + catIds
            return candidates.filter { it.startsWith(currentArg, ignoreCase = true) }
        }
        if (args.size == 2) {
            val catArg = args[0]
            val currentArg = args[1]
            val matchedCategory = categories.firstOrNull { it.id.equals(catArg, ignoreCase = true) }
            if (matchedCategory != null) {
                val lines = availableHelp(sender, matchedCategory.id)
                val totalPages = maxOf(1, (lines.size + PAGE_SIZE - 1) / PAGE_SIZE)
                val pages = (1..totalPages).map(Int::toString)
                return pages.filter { it.startsWith(currentArg, ignoreCase = true) }
            }
        }
        return emptyList()
    }

    fun tabCompletions(sender: CommandSender, currentArg: String): List<String> {
        return tabCompletions(sender, listOf(currentArg))
    }

    fun render(sender: CommandSender, arg0: String? = null, arg1: String? = null) {
        val plugin = CyufriendsReload.instance
        val categories = availableCategories(sender)

        val tokens = when {
            arg1 != null -> listOfNotNull(arg0?.trim()?.takeIf { it.isNotBlank() }, arg1.trim().takeIf { it.isNotBlank() })
            arg0 != null && arg0.contains(" ") -> arg0.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            arg0 != null && arg0.isNotBlank() -> listOf(arg0.trim())
            else -> emptyList()
        }

        var categoryId: String? = null
        var categoryName = "全指令总览"
        var pageArg: Int? = null

        when (tokens.size) {
            0 -> {}
            1 -> {
                val token = tokens[0]
                val matched = categories.firstOrNull { it.id.equals(token, ignoreCase = true) }
                if (matched != null) {
                    categoryId = matched.id
                    categoryName = matched.name
                } else {
                    pageArg = token.toIntOrNull()
                    if (pageArg == null) {
                        plugin.langEngine.sendRaw(sender, "<#FF6B6B>× 未知帮助主题或无效页码：<#D7DEE8>$token</#D7DEE8></#FF6B6B>")
                        return
                    }
                }
            }
            else -> {
                val token0 = tokens[0]
                val token1 = tokens[1]
                val match0 = categories.firstOrNull { it.id.equals(token0, ignoreCase = true) }
                val match1 = categories.firstOrNull { it.id.equals(token1, ignoreCase = true) }
                val page0 = token0.toIntOrNull()
                val page1 = token1.toIntOrNull()

                if (match0 != null && page1 != null) {
                    categoryId = match0.id
                    categoryName = match0.name
                    pageArg = page1
                } else if (match1 != null && page0 != null) {
                    categoryId = match1.id
                    categoryName = match1.name
                    pageArg = page0
                } else {
                    val rawCombined = tokens.joinToString(" ")
                    plugin.langEngine.sendRaw(sender, "<#FF6B6B>× 未知帮助主题或无效页码：<#D7DEE8>$rawCombined</#D7DEE8></#FF6B6B>")
                    return
                }
            }
        }

        val lines = availableHelp(sender, categoryId)
        val totalPages = maxOf(1, (lines.size + PAGE_SIZE - 1) / PAGE_SIZE)
        val page = (pageArg ?: 1).coerceIn(1, totalPages)
        val start = (page - 1) * PAGE_SIZE
        val pageLines = lines.subList(start, minOf(start + PAGE_SIZE, lines.size))

        plugin.langEngine.sendComponent(sender, deserialize("<#3A4352><st>┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈</st></#3A4352>"))
        plugin.langEngine.sendComponent(sender, deserialize("<gradient:#58C7FF:#7DE2B8><bold>CyuFriends 帮助指南</bold></gradient> <#3A4352>|</#3A4352> <#8A96A8>$categoryName</#8A96A8>"))

        if (sender is Player) {
            var navLine = Component.empty().append(deserialize("<#8A96A8>专题分类: </#8A96A8>"))
            categories.forEachIndexed { index, cat ->
                if (index > 0) navLine = navLine.append(deserialize("<#3A4352> </#3A4352>"))
                val isSelected = cat.id == categoryId
                val color = if (isSelected) "#7DE2B8" else "#7DD3FC"
                val text = if (isSelected) "<$color><bold>[${cat.name}]</bold></$color>" else "<$color>[${cat.name}]</$color>"
                val button = deserialize(text)
                    .clickEvent(ClickEvent.runCommand("/friend help ${cat.id}"))
                    .hoverEvent(HoverEvent.showText(deserialize("<#8A96A8>点击查阅 <#D7DEE8>${cat.name}</#D7DEE8> 专题指令</#8A96A8>")))
                navLine = navLine.append(button)
            }
            if (categoryId != null) {
                navLine = navLine.append(deserialize("<#3A4352> </#3A4352>"))
                val allBtn = deserialize("<#8A96A8>[全部]</#8A96A8>")
                    .clickEvent(ClickEvent.runCommand("/friend help 1"))
                    .hoverEvent(HoverEvent.showText(deserialize("<#8A96A8>点击查阅全部指令总览</#8A96A8>")))
                navLine = navLine.append(allBtn)
            }
            plugin.langEngine.sendComponent(sender, navLine)
        }

        for (line in pageLines) {
            val rawMsg = plugin.langEngine.component(line.key)
            val desc = rawMsg ?: deserialize(line.fallback)
            val entry = desc
                .clickEvent(ClickEvent.suggestCommand(line.suggestCommand))
                .hoverEvent(HoverEvent.showText(deserialize("<#8A96A8>点击在聊天框填入指令 <#D7DEE8>${line.command}</#D7DEE8></#8A96A8>")))
            plugin.langEngine.sendComponent(sender, entry)
        }

        if (totalPages > 1) {
            if (sender is Player) {
                val prevPage = if (page > 1) page - 1 else totalPages
                val nextPage = if (page < totalPages) page + 1 else 1
                val cmdPrefix = if (categoryId != null) "/friend help $categoryId" else "/friend help"
                val prevBtn = deserialize("<#7DD3FC><bold>‹ 上一页</bold></#7DD3FC>")
                    .clickEvent(ClickEvent.runCommand("$cmdPrefix $prevPage"))
                    .hoverEvent(HoverEvent.showText(deserialize("<#8A96A8>点击查看第 <#D7DEE8>$prevPage</#D7DEE8> 页</#8A96A8>")))
                val nextBtn = deserialize("<#7DD3FC><bold>下一页 ›</bold></#7DD3FC>")
                    .clickEvent(ClickEvent.runCommand("$cmdPrefix $nextPage"))
                    .hoverEvent(HoverEvent.showText(deserialize("<#8A96A8>点击查看第 <#D7DEE8>$nextPage</#D7DEE8> 页</#8A96A8>")))
                val info = deserialize(" <#8A96A8>第 <#D7DEE8>$page</#D7DEE8>/<#D7DEE8>$totalPages</#D7DEE8> 页</#8A96A8> ")
                val footer = Component.empty().append(prevBtn).append(info).append(nextBtn)
                plugin.langEngine.sendComponent(sender, footer)
            } else {
                val pageInfo = "<#8A96A8>第 <#D7DEE8>$page</#D7DEE8>/<#D7DEE8>$totalPages</#D7DEE8> 页 <#3A4352>|</#3A4352> 使用 <#D7DEE8>/friend help ${if (categoryId != null) "$categoryId " else ""}$page</#D7DEE8> 翻页</#8A96A8>"
                plugin.langEngine.sendComponent(sender, deserialize(pageInfo))
            }
        }

        plugin.langEngine.sendComponent(sender, deserialize("<#3A4352><st>┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈</st></#3A4352>"))
    }

    private fun deserialize(text: String): Component {
        return runCatching { miniMessage.deserialize(text) }.getOrElse { Component.text(text) }
    }
}
