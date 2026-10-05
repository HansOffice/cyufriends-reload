package org.cyuCBMclean.cyufriendsReload.integration.hook

import org.bukkit.Bukkit

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object IntimacyHook {

    private val serviceClass: Class<*>? by lazy {
        runCatching { Class.forName("org.cyuCBMclean.cyufriendsIntimacy.api.CyuIntimacyService") }.getOrNull()
    }

    private val snapshotMethod: Method? by lazy {
        serviceClass?.let { runCatching { it.getMethod("snapshot", String::class.java, String::class.java) }.getOrNull() }
    }

    private val propertyMethodCache = ConcurrentHashMap<String, Method>()

    fun snapshot(firstUid: String, secondUid: String): IntimacySnapshot? {
        val service = service() ?: return null
        val method = snapshotMethod ?: return null
        return runCatching {
            val raw = method.invoke(service, firstUid, secondUid) ?: return null
            IntimacySnapshot(
                points = raw.value<Int>("points") ?: 0,
                levelName = raw.value<String>("levelName") ?: "好友",
                levelColor = raw.value<String>("levelColor") ?: "&7",
                nextLevelName = raw.value<String>("nextLevelName"),
                nextLevelRemaining = raw.value<Int>("nextLevelRemaining") ?: 0,
                friendshipDays = raw.value<Int>("friendshipDays") ?: 0,
                rank = raw.value<Int>("rank")
            )
        }.getOrNull()
    }

    private fun service(): Any? {
        val clazz = serviceClass ?: return null
        return runCatching { Bukkit.getServicesManager().load(clazz) }.getOrNull()
    }

    private inline fun <reified T> Any.value(name: String): T? {
        val cacheKey = "${javaClass.name}#$name"
        val method = propertyMethodCache[cacheKey] ?: run {
            val resolved = javaClass.methods.firstOrNull { it.name == "get${name.replaceFirstChar(Char::uppercaseChar)}" && it.parameterCount == 0 }
                ?: javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 }
            if (resolved != null) {
                propertyMethodCache[cacheKey] = resolved
            }
            resolved
        } ?: return null
        return runCatching { method.invoke(this) as? T }.getOrNull()
    }
}

data class IntimacySnapshot(
    val points: Int,
    val levelName: String,
    val levelColor: String,
    val nextLevelName: String?,
    val nextLevelRemaining: Int,
    val friendshipDays: Int,
    val rank: Int?
)
