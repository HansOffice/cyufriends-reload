package org.cyuCBMclean.cyufriendsReload.ui.compat

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object ExternalMenuItems {

    private val prototypeCache = ConcurrentHashMap<String, ItemStack>()
    private val invokeNoArgMethods = ConcurrentHashMap<Pair<Class<*>, String>, Method?>()

    private val itemsAdderClass by lazy {
        runCatching { Class.forName("dev.lone.itemsadder.api.CustomStack") }.getOrNull()
    }
    private val itemsAdderGetInstance by lazy {
        itemsAdderClass?.let { runCatching { it.getMethod("getInstance", String::class.java) }.getOrNull() }
    }
    private val itemsAdderGetItemStack by lazy {
        itemsAdderClass?.let { runCatching { it.getMethod("getItemStack") }.getOrNull() }
    }

    private val oraxenClass by lazy {
        runCatching { Class.forName("io.th0rgal.oraxen.api.OraxenItems") }.getOrNull()
    }
    private val oraxenGetItemById by lazy {
        oraxenClass?.let { runCatching { it.getMethod("getItemById", String::class.java) }.getOrNull() }
    }

    private val nexoClass by lazy {
        runCatching { Class.forName("com.nexomc.nexo.api.NexoItems") }.getOrNull()
            ?: runCatching { Class.forName("com.nexomc.nexo.api.NexoItemsAPI") }.getOrNull()
    }
    private val nexoItemMethod by lazy {
        nexoClass?.let { clazz ->
            listOf("itemFromId", "getItemById", "getItem", "byId").firstNotNullOfOrNull { name ->
                runCatching { clazz.getMethod(name, String::class.java) }.getOrNull()
            }
        }
    }

    fun reload() {
        prototypeCache.clear()
        invokeNoArgMethods.clear()
    }

    fun build(spec: String?): ItemStack? {
        val value = spec?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        prototypeCache[value]?.let { return it.clone() }
        val lower = value.lowercase()
        val item = when {
            lower.startsWith("itemsadder:") -> itemsAdder(value.substringAfter(':'))
            lower.startsWith("ia:") -> itemsAdder(value.substringAfter(':'))
            lower.startsWith("oraxen:") -> oraxen(value.substringAfter(':'))
            lower.startsWith("ox:") -> oraxen(value.substringAfter(':'))
            lower.startsWith("nexo:") -> nexo(value.substringAfter(':'))
            lower.startsWith("craftengine:") -> CraftEngineItems.build(value.substringAfter(':'), null)
            lower.startsWith("ce:") -> CraftEngineItems.build(value.substringAfter(':'), null)
            else -> null
        } ?: return null
        if (prototypeCache.size < 512) prototypeCache[value] = item.clone()
        return item
    }

    fun isExternal(spec: String?): Boolean {
        val lower = spec?.trim()?.lowercase() ?: return false
        return lower.startsWith("itemsadder:")
            || lower.startsWith("ia:")
            || lower.startsWith("oraxen:")
            || lower.startsWith("ox:")
            || lower.startsWith("nexo:")
            || lower.startsWith("craftengine:")
            || lower.startsWith("ce:")
    }

    private fun itemsAdder(id: String): ItemStack? {
        if (!Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) return null
        val getInstance = itemsAdderGetInstance ?: return null
        val getItemStack = itemsAdderGetItemStack ?: return null
        return runCatching {
            val customStack = getInstance.invoke(null, id.trim()) ?: return null
            getItemStack.invoke(customStack) as? ItemStack
        }.getOrNull()?.clone()
    }

    private fun oraxen(id: String): ItemStack? {
        if (!Bukkit.getPluginManager().isPluginEnabled("Oraxen")) return null
        val getItemById = oraxenGetItemById ?: return null
        return runCatching {
            val builder = getItemById.invoke(null, id.trim()) ?: return null
            buildKnownItem(builder)
        }.getOrNull()
    }

    private fun nexo(id: String): ItemStack? {
        if (!Bukkit.getPluginManager().isPluginEnabled("Nexo")) return null
        val method = nexoItemMethod ?: return null
        return runCatching {
            val raw = method.invoke(null, id.trim()) ?: return null
            buildKnownItem(raw)
        }.getOrNull()
    }

    private fun buildKnownItem(raw: Any?): ItemStack? {
        val item = when (raw) {
            null -> null
            is ItemStack -> raw
            else -> {
                invokeNoArg(raw, "build") as? ItemStack
                    ?: invokeNoArg(raw, "getItemStack") as? ItemStack
                    ?: invokeNoArg(raw, "itemStack") as? ItemStack
            }
        }
        return item?.clone()
    }

    private fun invokeNoArg(target: Any, name: String): Any? {
        val method = invokeNoArgMethods.computeIfAbsent(target.javaClass to name) { (clazz, methodName) ->
            runCatching { clazz.getMethod(methodName) }.getOrNull()
        } ?: return null
        return runCatching { method.invoke(target) }.getOrNull()
    }
}
