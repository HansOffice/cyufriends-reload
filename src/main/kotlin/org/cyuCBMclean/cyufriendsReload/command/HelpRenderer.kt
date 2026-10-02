package org.cyuCBMclean.cyufriendsReload.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.cyuCBMclean.cyufriendsReload.CyufriendsReload

object HelpRenderer {

    private const val PAGE_SIZE = 8

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

    fun tabCompletions(sender: CommandSender, currentArg: String): List<String> {
        val categories = availableCategories(sender).map { it.id }
        val totalPages = maxOf(1, (availableHelp(sender).size + PAGE_SIZE - 1) / PAGE_SIZE)
        val pages = (1..totalPages).map(Int::toString)
        val candidates = pages + categories
        return candidates.filter { it.startsWith(currentArg, ignoreCase = true) }
    }

    fun render(sender: CommandSender, rawArg: String?) {
        val plugin = CyufriendsReload.instance
        val arg = rawArg?.trim()?.lowercase()
        val categories = availableCategories(sender)
        val matchedCategory = categories.firstOrNull { it.id.equals(arg, ignoreCase = true) }

        val categoryId = matchedCategory?.id
        val categoryName = matchedCategory?.name ?: "全指令总览"
        val pageArg = if (matchedCategory != null) null else arg?.toIntOrNull()

        val lines = availableHelp(sender, categoryId)
        val totalPages = maxOf(1, (lines.size + PAGE_SIZE - 1) / PAGE_SIZE)

        if (arg != null && matchedCategory == null && pageArg == null) {
            plugin.langEngine.sendRaw(sender, "<#FF6B6B>× 未知帮助主题或无效页码：<#D7DEE8>$rawArg</#D7DEE8></#FF6B6B>")
            return
        }

        val page = (pageArg ?: 1).coerceIn(1, totalPages)
        val start = (page - 1) * PAGE_SIZE
        val pageLines = lines.subList(start, minOf(start + PAGE_SIZE, lines.size))

        val audience = plugin.langEngine.audiences.sender(sender)

        audience.sendMessage(deserialize("<#3A4352><st>┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈</st></#3A4352>"))
        audience.sendMessage(deserialize("<gradient:#58C7FF:#7DE2B8><bold>CyuFriends 帮助指南</bold></gradient> <#3A4352>|</#3A4352> <#8A96A8>$categoryName</#8A96A8>"))

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
            audience.sendMessage(navLine)
        }

        for (line in pageLines) {
            val rawMsg = plugin.langEngine.component(line.key)
            val desc = rawMsg ?: deserialize(line.fallback)
            val entry = desc
                .clickEvent(ClickEvent.suggestCommand(line.suggestCommand))
                .hoverEvent(HoverEvent.showText(deserialize("<#8A96A8>点击在聊天框填入指令 <#D7DEE8>${line.command}</#D7DEE8></#8A96A8>")))
            audience.sendMessage(entry)
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
                audience.sendMessage(footer)
            } else {
                val pageInfo = "<#8A96A8>第 <#D7DEE8>$page</#D7DEE8>/<#D7DEE8>$totalPages</#D7DEE8> 页 <#3A4352>|</#3A4352> 使用 <#D7DEE8>/friend help ${if (categoryId != null) "$categoryId " else ""}$page</#D7DEE8> 翻页</#8A96A8>"
                audience.sendMessage(deserialize(pageInfo))
            }
        }

        audience.sendMessage(deserialize("<#3A4352><st>┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈</st></#3A4352>"))
    }

    private fun deserialize(text: String): Component {
        return runCatching { net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text) }.getOrElse { Component.text(text) }
    }
}
