package org.cyuCBMclean.cyufriendsReload.modules.proxy

import java.util.concurrent.ConcurrentHashMap

class MessageDeduplicator(private val ttlMillis: Long) {

    private val seen = ConcurrentHashMap<String, Long>()

    @Volatile
    private var lastCleanup = 0L

    fun mark(messageId: String, now: Long = System.currentTimeMillis()): Boolean {
        if (now - lastCleanup > ttlMillis.coerceAtLeast(1000L)) {
            lastCleanup = now
            cleanup(now)
        }
        return seen.putIfAbsent(messageId, now) == null
    }

    private fun cleanup(now: Long) {
        seen.entries.removeIf { now - it.value > ttlMillis }
    }
}
