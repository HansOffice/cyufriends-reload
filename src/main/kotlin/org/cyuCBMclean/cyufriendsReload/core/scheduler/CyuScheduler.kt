package org.cyuCBMclean.cyufriendsReload.core.scheduler

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin

interface CyuTask {
    fun cancel()
}

interface CyuScheduler {
    fun runAsync(plugin: Plugin, task: () -> Unit): CyuTask
    fun runLaterAsync(plugin: Plugin, delayTicks: Long, task: () -> Unit): CyuTask
    fun runTimerAsync(plugin: Plugin, delayTicks: Long, periodTicks: Long, task: () -> Unit): CyuTask
    fun runGlobal(plugin: Plugin, task: () -> Unit): CyuTask
    fun runRegion(plugin: Plugin, location: Location, task: () -> Unit): CyuTask
    fun runEntity(plugin: Plugin, entity: Entity, task: () -> Unit): CyuTask
    fun teleport(entity: Entity, location: Location, callback: (Boolean) -> Unit)
    fun cancelAll(plugin: Plugin)
}

object CyuConcurrency {
    val isFolia: Boolean = PlatformSchedulerFactory.isFolia

    fun bandName(): String = PlatformSchedulerFactory.bandName()

    val scheduler: CyuScheduler by lazy(PlatformSchedulerFactory::create)

    fun teleport(entity: Entity, location: Location, callback: (Boolean) -> Unit) {
        scheduler.teleport(entity, location, callback)
    }
}
