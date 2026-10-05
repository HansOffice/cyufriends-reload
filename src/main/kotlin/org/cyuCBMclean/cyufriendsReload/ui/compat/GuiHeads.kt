package org.cyuCBMclean.cyufriendsReload.ui.compat

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.cyuCBMclean.cyufriendsReload.CyufriendsReload
import org.cyuCBMclean.cyufriendsReload.core.config.Settings
import org.cyuCBMclean.cyufriendsReload.core.scheduler.CyuConcurrency
import org.cyuCBMclean.cyufriendsReload.extension.proxyModule
import org.cyuCBMclean.cyufriendsReload.integration.hook.CyuIdHook
import org.cyuCBMclean.cyufriendsReload.ui.view.CyuView
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.Base64
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GuiHeads {

    @Volatile
    private var cacheSize = Settings.guiHeadCacheSize
    @Volatile
    private var staticCache: Cache<String, ItemStack> = newCache(cacheSize)
    @Volatile
    private var skinPluginCache: Cache<String, String> = newSkinCache(cacheSize)
    private val urlPattern = Regex("\"url\"\\s*:\\s*\"([^\"]+)\"")
    private val unresolvedTokenPattern = Regex("%[A-Za-z0-9_\\-]+%")
    private val profileMethodCache: Cache<Class<*>, Method?> = Caffeine.newBuilder().maximumSize(128).build()
    private val setProfileMethodCache: Cache<String, Method?> = Caffeine.newBuilder().maximumSize(64).build()
    private val texturesMethodCache: Cache<Class<*>, Method?> = Caffeine.newBuilder().maximumSize(32).build()
    private val skinMethodCache: Cache<Class<*>, Method?> = Caffeine.newBuilder().maximumSize(32).build()
    private val propertiesMethodCache: Cache<Class<*>, Method?> = Caffeine.newBuilder().maximumSize(32).build()
    private val profileFieldCache = ConcurrentHashMap<Class<*>, Field>()
    private val pendingSkinLookups = ConcurrentHashMap.newKeySet<String>()

    private val playerProfileClass by lazy {
        runCatching { Class.forName("org.bukkit.profile.PlayerProfile") }.getOrNull()
    }
    private val playerTexturesClass by lazy {
        runCatching { Class.forName("org.bukkit.profile.PlayerTextures") }.getOrNull()
    }
    private val createPlayerProfileMethod by lazy {
        runCatching { Bukkit.getServer().javaClass.getMethod("createPlayerProfile", UUID::class.java, String::class.java) }.getOrNull()
    }
    private val profileGetTexturesMethod by lazy {
        playerProfileClass?.let { runCatching { it.getMethod("getTextures") }.getOrNull() }
    }
    private val texturesSetSkinMethod by lazy {
        playerTexturesClass?.let { runCatching { it.getMethod("setSkin", URL::class.java) }.getOrNull() }
    }
    private val skullMetaSetProfileMethod by lazy {
        playerProfileClass?.let { pClass ->
            runCatching { SkullMeta::class.java.getMethod("setOwnerProfile", pClass) }.getOrNull()
                ?: runCatching { SkullMeta::class.java.getMethod("setPlayerProfile", pClass) }.getOrNull()
        }
    }

    private val gameProfileConstructor by lazy {
        runCatching {
            val profileClass = Class.forName("com.mojang.authlib.GameProfile")
            profileClass.getConstructor(UUID::class.java, String::class.java)
        }.getOrNull()
    }
    private val propertyConstructor by lazy {
        runCatching {
            val propertyClass = Class.forName("com.mojang.authlib.properties.Property")
            propertyClass.getConstructor(String::class.java, String::class.java)
        }.getOrNull()
    }
    private val gameProfileGetProperties by lazy {
        runCatching {
            val profileClass = Class.forName("com.mojang.authlib.GameProfile")
            profileClass.getMethod("getProperties")
        }.getOrNull()
    }
    private val propertyMapPut by lazy {
        gameProfileGetProperties?.returnType?.let { clazz ->
            runCatching { clazz.getMethod("put", Any::class.java, Any::class.java) }.getOrNull()
        }
    }

    private val skinsRestorerStorageAndMethod by lazy {
        runCatching {
            val provider = Class.forName("net.skinsrestorer.api.SkinsRestorerProvider")
            val api = provider.getMethod("get").invoke(null) ?: return@runCatching null
            val storage = api.javaClass.getMethod("getPlayerStorage").invoke(api) ?: return@runCatching null
            val method = storage.javaClass.getMethod("getSkinForPlayer", UUID::class.java, String::class.java)
            storage to method
        }.getOrNull()
    }

    fun reload() {
        val size = Settings.guiHeadCacheSize
        if (size != cacheSize) {
            cacheSize = size
            staticCache = newCache(size)
            skinPluginCache = newSkinCache(size)
        } else {
            staticCache.invalidateAll()
            skinPluginCache.invalidateAll()
        }
    }

    fun apply(item: ItemStack, source: String?, viewer: Player? = null): ItemStack {
        val value = source?.trim()?.takeIf { it.isNotEmpty() } ?: return item
        val resolved = resolveAlias(value, viewer) ?: return item
        if (containsUnresolvedToken(resolved)) {
            val fallback = resolveAlias(Settings.guiOfflineHeadSource, viewer)
            return if (!fallback.isNullOrBlank() && fallback != resolved && !containsUnresolvedToken(fallback)) {
                apply(item, fallback, viewer)
            } else {
                item
            }
        }

        if (isStaticSource(resolved)) {
            val key = staticCacheKey(resolved, item)
            return staticCache.get(key) { buildStatic(item, resolved) }?.clone() ?: item
        }

        val head = ensureHead(item.clone())
        val meta = head.itemMeta as? SkullMeta ?: return head
        val player = Bukkit.getPlayerExact(resolved)
            ?: Bukkit.getOnlinePlayers().firstOrNull { it.name.equals(resolved, ignoreCase = true) }
        if (player != null) {
            when {
                applyProfileFrom(meta, player) -> Unit
                applySkinPluginProfile(meta, player.uniqueId, player.name, viewer) -> Unit
                else -> meta.owningPlayer = player
            }
        } else {
            val offlinePlayer = runCatching { Bukkit.getOfflinePlayer(resolved) }.getOrNull()
            if (offlinePlayer != null && (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline)) {
                when {
                    applyProfileFrom(meta, offlinePlayer) -> Unit
                    applySkinPluginProfile(meta, offlinePlayer.uniqueId, offlinePlayer.name ?: resolved, viewer) -> Unit
                    else -> meta.owningPlayer = offlinePlayer
                }
            } else {
                if (!applySkinPluginProfile(meta, null, resolved, viewer)) {
                    val fallback = resolveAlias(Settings.guiOfflineHeadSource, viewer)
                    if (!fallback.isNullOrBlank() && fallback != resolved) {
                        return apply(item, fallback, viewer)
                    }
                }
            }
        }
        head.itemMeta = meta
        return head
    }

    fun applyForUid(item: ItemStack, uid: String, viewer: Player? = null): ItemStack {
        CyuIdHook.getOnlinePlayer(uid)?.let { return apply(item, it.name, viewer) }
        val remote = CyufriendsReload.instance.proxyModule()?.remotePresence?.find(uid)
        if (!remote?.headSource.isNullOrBlank()) {
            return apply(item, remote?.headSource, viewer)
        }
        if (!remote?.name.isNullOrBlank()) {
            return apply(item, remote?.name, viewer)
        }
        val offline = CyuIdHook.getOfflinePlayer(uid)
        if (!offline?.name.isNullOrBlank()) {
            return apply(item, offline?.name, viewer)
        }
        return apply(item, Settings.guiOfflineHeadSource, viewer)
    }

    fun isStaticSource(value: String?): Boolean {
        val text = value?.trim()?.lowercase() ?: return false
        return text.startsWith("basehead-") || text.startsWith("base64-") || text.startsWith("url-") || text.startsWith("texture-") || text.startsWith("http://") || text.startsWith("https://") || isTextureHash(text)
    }

    private fun buildStatic(base: ItemStack, source: String): ItemStack {
        val head = ensureHead(base.clone())
        val meta = head.itemMeta as? SkullMeta ?: return head
        val value = source.trim()

        if (value.startsWith("basehead-", true) || value.startsWith("base64-", true)) {
            applyBase64(meta, value.substringAfter('-').trim())
        } else {
            applyUrl(meta, value.removePrefix("url-").removePrefix("texture-").trim())
        }

        head.itemMeta = meta
        return head
    }

    private fun ensureHead(item: ItemStack): ItemStack {
        if (item.itemMeta is SkullMeta) return item
        val material = Material.matchMaterial("PLAYER_HEAD") ?: Material.matchMaterial("SKULL_ITEM") ?: Material.STONE
        val head = ItemStack(material, item.amount)
        if (head.type.name.equals("SKULL_ITEM", true)) head.durability = 3
        if (item.hasItemMeta()) head.itemMeta = item.itemMeta
        return head
    }

    private fun applyUrl(meta: SkullMeta, url: String) {
        val normalized = normalizeTextureUrl(url) ?: return
        val json = "{\"textures\":{\"SKIN\":{\"url\":\"$normalized\"}}}"
        applyBase64(meta, Base64.getEncoder().encodeToString(json.toByteArray(StandardCharsets.UTF_8)))
    }

    private fun applyBase64(meta: SkullMeta, base64: String) {
        val value = base64.trim()
        if (value.isEmpty()) return
        val url = decodeTextureUrl(value)
        if (url != null && applyPlayerProfile(meta, url)) return
        applyGameProfile(meta, value)
    }

    private fun applyProfileFrom(meta: SkullMeta, owner: Any): Boolean {
        val profile = profileOf(owner) ?: return false
        if (!profileHasTexture(profile)) return false
        return applyProfileObject(meta, profile)
    }

    private fun profileOf(owner: Any): Any? {
        val method = profileMethodCache.get(owner.javaClass) { type ->
            listOf("getPlayerProfile", "getProfile")
                .firstNotNullOfOrNull { methodName -> runCatching { type.getMethod(methodName) }.getOrNull() }
        } ?: return null
        return runCatching { method.invoke(owner) }.getOrNull()
    }

    private fun profileHasTexture(profile: Any): Boolean {
        val skinUrl = runCatching {
            val textures = texturesMethodCache.get(profile.javaClass) { type ->
                runCatching { type.getMethod("getTextures") }.getOrNull()
            }?.invoke(profile) ?: return@runCatching null
            skinMethodCache.get(textures.javaClass) { type ->
                runCatching { type.getMethod("getSkin") }.getOrNull()
            }?.invoke(textures)
        }.getOrNull()
        if (skinUrl != null) return true

        return runCatching {
            val propertiesMethod = propertiesMethodCache.get(profile.javaClass) { type ->
                runCatching { type.getMethod("getProperties") }.getOrNull()
            } ?: return@runCatching false
            val properties = propertiesMethod.invoke(profile) ?: return@runCatching false
            val values = properties.javaClass.getMethod("get", Any::class.java).invoke(properties, "textures")
            values != null && values.toString().isNotBlank() && values.toString() != "[]"
        }.getOrDefault(false)
    }

    private fun applyProfileObject(meta: SkullMeta, profile: Any): Boolean {
        return runCatching {
            val cacheKey = "${meta.javaClass.name}:${profile.javaClass.name}"
            val method = setProfileMethodCache.get(cacheKey) {
                findProfileSetter(meta.javaClass, profile.javaClass)
            } ?: findProfileSetter(meta.javaClass, profile.javaClass) ?: return false
            method.invoke(meta, profile)
            true
        }.getOrDefault(false)
    }

    private fun findProfileSetter(metaClass: Class<*>, profileClass: Class<*>): Method? {
        return metaClass.methods.firstOrNull { method ->
            method.parameterTypes.size == 1 &&
                (method.name == "setOwnerProfile" || method.name == "setPlayerProfile") &&
                method.parameterTypes[0].isAssignableFrom(profileClass)
        } ?: SkullMeta::class.java.methods.firstOrNull { method ->
            method.parameterTypes.size == 1 &&
                (method.name == "setOwnerProfile" || method.name == "setPlayerProfile") &&
                method.parameterTypes[0].isAssignableFrom(profileClass)
        }
    }

    private fun applySkinPluginProfile(meta: SkullMeta, uuid: UUID?, name: String, viewer: Player?): Boolean {
        val value = skinPluginSource(uuid, name, viewer) ?: return false
        applyBase64(meta, value)
        return true
    }

    private fun skinPluginSource(uuid: UUID?, name: String, viewer: Player?): String? {
        val cleanName = name.trim().takeIf { it.isNotEmpty() } ?: return null
        val cacheKey = "${uuid ?: "none"}:$cleanName"
        val cached = skinPluginCache.getIfPresent(cacheKey)
        if (cached == null) preloadSkinPluginSource(cacheKey, uuid, cleanName, viewer)
        return cached?.takeIf { it.isNotBlank() }
    }

    private fun preloadSkinPluginSource(cacheKey: String, uuid: UUID?, name: String, viewer: Player?) {
        if (!Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
            skinPluginCache.put(cacheKey, "")
            return
        }
        if (!pendingSkinLookups.add(cacheKey)) return

        val plugin = CyufriendsReload.instance
        CyuConcurrency.scheduler.runAsync(plugin) {
            try {
                val value = resolveSkinsRestorerTexture(uuid, name).orEmpty()
                skinPluginCache.put(cacheKey, value)
                if (value.isNotBlank() && viewer != null) {
                    CyuConcurrency.scheduler.runEntity(plugin, viewer) {
                        if (!viewer.isOnline) return@runEntity
                        val holder = viewer.openInventory.topInventory.holder
                        if (holder is CyuView) holder.refreshOpenView()
                    }
                }
            } finally {
                pendingSkinLookups.remove(cacheKey)
            }
        }
    }

    private fun resolveSkinsRestorerTexture(uuid: UUID?, name: String): String? {
        val (storage, method) = skinsRestorerStorageAndMethod ?: return null
        return runCatching {
            val optional = method.invoke(storage, uuid ?: offlineUuid(name), name) ?: return null
            val property = optional.javaClass.getMethod("orElse", Any::class.java).invoke(optional, null) ?: return null
            property.javaClass.getMethod("getValue").invoke(property) as? String
        }.getOrNull()?.trim()?.takeIf { it.isNotEmpty() }
    }

    private fun offlineUuid(name: String): UUID {
        return UUID.nameUUIDFromBytes("OfflinePlayer:$name".toByteArray(StandardCharsets.UTF_8))
    }

    private fun applyPlayerProfile(meta: SkullMeta, url: String): Boolean {
        val createProfile = createPlayerProfileMethod ?: return false
        val getTextures = profileGetTexturesMethod ?: return false
        val setSkin = texturesSetSkinMethod ?: return false
        val setProfile = skullMetaSetProfileMethod ?: return false
        return runCatching {
            val profile = createProfile.invoke(Bukkit.getServer(), UUID.randomUUID(), "") ?: return false
            val textures = getTextures.invoke(profile) ?: return false
            setSkin.invoke(textures, URL(url))
            setProfile.invoke(meta, profile)
            true
        }.getOrDefault(false)
    }

    private fun applyGameProfile(meta: SkullMeta, base64: String) {
        val profileCtor = gameProfileConstructor ?: return
        val propCtor = propertyConstructor ?: return
        val getProps = gameProfileGetProperties ?: return
        val putProp = propertyMapPut ?: return
        runCatching {
            val profile = profileCtor.newInstance(UUID.randomUUID(), null)
            val property = propCtor.newInstance("textures", base64)
            val properties = getProps.invoke(profile)
            putProp.invoke(properties, "textures", property)
            profileField(meta).set(meta, profile)
        }
    }

    private fun profileField(meta: SkullMeta): Field {
        return profileFieldCache.computeIfAbsent(meta.javaClass) { clazz ->
            var type: Class<*>? = clazz
            while (type != null) {
                val field = runCatching { type.getDeclaredField("profile") }.getOrNull()
                if (field != null) {
                    field.isAccessible = true
                    return@computeIfAbsent field
                }
                type = type.superclass
            }
            throw NoSuchFieldException("profile")
        }
    }

    private fun normalizeTextureUrl(value: String): String? {
        val text = value.trim().takeIf { it.isNotEmpty() } ?: return null
        val url = if (text.startsWith("http://") || text.startsWith("https://")) text else "https://textures.minecraft.net/texture/$text"
        return if (url.startsWith("http://textures.minecraft.net/texture/")) {
            "https://textures.minecraft.net/texture/${url.substring("http://textures.minecraft.net/texture/".length)}"
        } else {
            url
        }
    }

    private fun decodeTextureUrl(base64: String): String? {
        return runCatching {
            val decoded = String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8)
            normalizeTextureUrl(urlPattern.find(decoded)?.groupValues?.getOrNull(1) ?: return null)
        }.getOrNull()
    }

    private fun staticCacheKey(source: String, item: ItemStack): String {
        val meta = item.itemMeta
        val displayName = meta?.takeIf { it.hasDisplayName() }?.displayName.orEmpty()
        val lore = meta?.lore?.joinToString("\u001F").orEmpty()
        val customModelData = customModelDataOf(meta)
        return listOf(
            source.trim().lowercase(),
            item.type.name,
            item.amount.toString(),
            item.durability.toString(),
            customModelData,
            displayName,
            lore
        ).joinToString("\u001E")
    }

    private fun customModelDataOf(meta: org.bukkit.inventory.meta.ItemMeta?): String {
        if (meta == null) return ""
        return runCatching {
            if (meta.hasCustomModelData()) meta.customModelData.toString() else ""
        }.getOrDefault("")
    }

    private fun isTextureHash(value: String): Boolean {
        return value.length in 32..128 && value.all { it in 'a'..'f' || it in '0'..'9' }
    }

    private fun containsUnresolvedToken(value: String): Boolean {
        return unresolvedTokenPattern.containsMatchIn(value)
    }

    private fun resolveAlias(value: String, viewer: Player?): String? {
        if (value.equals("self", true) || value.equals("me", true) || value.equals("viewer", true)) {
            return viewer?.name
        }
        if (value.equals("offline", true) || value.equals("default", true) || value.equals("unknown", true)) {
            return Settings.guiOfflineHeadSource.trim().takeIf { it.isNotEmpty() }
        }
        return value
    }

    private fun newCache(size: Long): Cache<String, ItemStack> {
        return Caffeine.newBuilder().maximumSize(size).build()
    }

    private fun newSkinCache(size: Long): Cache<String, String> {
        return Caffeine.newBuilder()
            .maximumSize(size)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build()
    }
}
