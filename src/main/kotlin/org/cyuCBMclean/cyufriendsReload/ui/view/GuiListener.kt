package org.cyuCBMclean.cyufriendsReload.ui.view

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GuiListener : Listener {

    private val lastClick = ConcurrentHashMap<UUID, Long>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onClick(event: InventoryClickEvent) {
        val top = event.view.topInventory
        val holder = top.holder as? CyuView ?: return
        val player = event.whoClicked as? Player ?: return
        if (!player.isOnline) return

        if (event.click == ClickType.DOUBLE_CLICK) {
            event.isCancelled = true
            return
        }

        val slot = event.rawSlot
        if (slot < 0) return

        if (slot >= top.size) {
            if (event.isShiftClick) {
                event.isCancelled = true
            }
            return
        }

        event.isCancelled = true
        val now = System.currentTimeMillis()
        val previous = lastClick[player.uniqueId]
        if (previous != null && now - previous < DEBOUNCE_MS) return
        lastClick[player.uniqueId] = now

        holder.handleClick(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDrag(event: InventoryDragEvent) {
        val top = event.view.topInventory
        val holder = top.holder as? CyuView ?: return
        if (event.rawSlots.any { it < top.size }) {
            event.isCancelled = true
        } else {
            holder.handleDrag(event)
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (holder is CyuView) {
            holder.onClose(event)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        lastClick.remove(event.player.uniqueId)
    }

    companion object {
        const val DEBOUNCE_MS = 250L
    }
}