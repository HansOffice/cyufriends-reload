package org.cyuCBMclean.cyufriendsReload.core.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.inventory.meta.ItemMeta

import java.lang.reflect.Method

object ColorCompat {

    val rgbSupported: Boolean by lazy {
        val match = Regex("""^(\d+)\.(\d+)""").find(Bukkit.getBukkitVersion())
        val major = match?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 1
        val minor = match?.groupValues?.getOrNull(2)?.toIntOrNull() ?: 13
        major > 1 || minor >= 16
    }

    private val legacyRgb = LegacyComponentSerializer.builder()
        .character(LegacyComponentSerializer.SECTION_CHAR)
        .hexColors()
        .build()
    private val legacyNearest = LegacyComponentSerializer.legacySection()

    private val displayNameMethod: Method? by lazy {
        ItemMeta::class.java.methods.firstOrNull {
            it.name == "displayName" &&
                it.parameterTypes.size == 1 &&
                Component::class.java.isAssignableFrom(it.parameterTypes[0])
        }
    }

    private val loreMethod: Method? by lazy {
        ItemMeta::class.java.methods.firstOrNull {
            it.name == "lore" &&
                it.parameterTypes.size == 1 &&
                List::class.java.isAssignableFrom(it.parameterTypes[0])
        }
    }

    fun serialize(component: Component): String {
        return if (rgbSupported) legacyRgb.serialize(component) else legacyNearest.serialize(component)
    }

    fun renderMiniMessage(miniMessage: MiniMessage, raw: String, vararg placeholders: TagResolver): String {
        return runCatching {
            serialize(miniMessage.deserialize(raw, *placeholders))
        }.getOrElse {
            ChatColor.translateAlternateColorCodes('&', raw)
        }
    }

    fun renderGuiMiniMessage(miniMessage: MiniMessage, raw: String, vararg placeholders: TagResolver): String {
        return renderMiniMessage(miniMessage, raw, *placeholders)
    }

    fun parseMiniMessage(miniMessage: MiniMessage, raw: String, vararg placeholders: TagResolver): Component? {
        return runCatching {
            miniMessage.deserialize(raw, *placeholders)
        }.getOrNull()
    }

    fun applyGuiDisplayName(meta: ItemMeta, component: Component): Boolean {
        if (!rgbSupported) return false
        val method = displayNameMethod ?: return false
        return runCatching {
            method.invoke(meta, component)
            true
        }.getOrDefault(false)
    }

    fun applyGuiLore(meta: ItemMeta, components: List<Component>): Boolean {
        if (!rgbSupported) return false
        val method = loreMethod ?: return false
        return runCatching {
            method.invoke(meta, components)
            true
        }.getOrDefault(false)
    }
}
