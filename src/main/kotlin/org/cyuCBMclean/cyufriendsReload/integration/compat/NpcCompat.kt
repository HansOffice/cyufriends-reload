package org.cyuCBMclean.cyufriendsReload.integration.compat

import org.bukkit.entity.Entity

import java.lang.reflect.Method

object NpcCompat {

    private val citizensRegistry by lazy {
        runCatching {
            val api = Class.forName("net.citizensnpcs.api.CitizensAPI")
            api.getMethod("getNPCRegistry").invoke(null)
        }.getOrNull()
    }

    private val isNpcMethod: Method? by lazy {
        citizensRegistry?.let { registry ->
            runCatching { registry.javaClass.getMethod("isNPC", Entity::class.java) }.getOrNull()
        }
    }

    fun isNpc(entity: Entity): Boolean {
        if (entity.hasMetadata("NPC")) return true
        val registry = citizensRegistry ?: return false
        val method = isNpcMethod ?: return false
        return runCatching {
            method.invoke(registry, entity) == true
        }.getOrDefault(false)
    }
}
