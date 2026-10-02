package org.cyuCBMclean.cyufriendsReload.ui.view

import org.bukkit.entity.Player
import org.cyuCBMclean.cyufriendsReload.CyufriendsReload
import org.cyuCBMclean.cyufriendsReload.extension.sendLang
import org.cyuCBMclean.cyufriendsReload.extension.uid
import org.cyuCBMclean.cyufriendsReload.integration.hook.CyuIdHook
import org.cyuCBMclean.cyufriendsReload.modules.chat.ChatModule
import org.cyuCBMclean.cyufriendsReload.modules.chat.gui.MessagesView
import org.cyuCBMclean.cyufriendsReload.modules.friend.FriendListStateStore
import org.cyuCBMclean.cyufriendsReload.modules.friend.FriendModule
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.AddFriendView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.BlacklistView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.FriendProfileSocialView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.FriendProfileView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.FriendTagFilterView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.FriendTagManageView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.FriendTimelineView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.FriendsListView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.OnlinePlayersView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.RecommendationsView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.RequestsView
import org.cyuCBMclean.cyufriendsReload.modules.friend.gui.SentRequestsView
import org.cyuCBMclean.cyufriendsReload.modules.group.GroupModule
import org.cyuCBMclean.cyufriendsReload.modules.group.gui.GroupListView
import org.cyuCBMclean.cyufriendsReload.modules.profile.ProfileModule
import org.cyuCBMclean.cyufriendsReload.modules.profile.gui.BirthdaysView
import org.cyuCBMclean.cyufriendsReload.modules.profile.gui.NotificationCenterView
import org.cyuCBMclean.cyufriendsReload.modules.profile.gui.ProfileHomeView
import org.cyuCBMclean.cyufriendsReload.modules.profile.gui.SettingsView
import org.cyuCBMclean.cyufriendsReload.modules.profile.gui.SocialSettingsView
import org.cyuCBMclean.cyufriendsReload.modules.social.SocialModule
import org.cyuCBMclean.cyufriendsReload.modules.social.gui.StatusView
import org.cyuCBMclean.cyufriendsReload.modules.social.gui.WallView
import org.cyuCBMclean.cyufriendsReload.ui.layout.GuiLoader
import org.cyuCBMclean.cyufriendsReload.ui.layout.GuiPattern
import org.cyuCBMclean.cyufriendsReload.ui.layout.ItemTemplate

object GuiRouter {

    inline fun openGui(
        player: Player,
        plugin: CyufriendsReload,
        fileName: String,
        fallbackTitle: String,
        replacements: Map<String, String> = emptyMap(),
        open: (GuiPattern, Map<Char, ItemTemplate>, String) -> Unit
    ): Boolean {
        val guiData = GuiLoader.load(plugin, fileName)
        if (guiData == null) {
            player.sendLang("gui-open-failed")
            return false
        }
        open(guiData.pattern, guiData.items, guiData.resolveTitle(player, fallbackTitle, replacements))
        return true
    }

    fun open(player: Player, menuId: String, args: List<String> = emptyList()): Boolean {
        val plugin = CyufriendsReload.instance
        val modules = plugin.moduleManager
        val cleanMenu = menuId.lowercase().replace('-', '_')

        return when (cleanMenu) {
            "profile_home", "home", "gui" -> {
                val profileModule = modules.getModule<ProfileModule>("profile") ?: return false
                openGui(player, plugin, "profile_home.yml", ViewTitles.profileHome(player.name)) { pattern, items, title ->
                    ProfileHomeView(player, pattern, items, plugin, profileModule, title).open()
                }
            }
            "friends_list", "friends", "list" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                val state = FriendListStateStore.get(player.uid).normalized()
                openGui(player, plugin, "friends_list.yml", ViewTitles.friendsList(state.filterTag), mapOf(
                    "%filter_tag%" to (state.filterTag ?: "全部好友"),
                    "%sort_mode%" to state.sortMode.displayName
                )) { pattern, items, title ->
                    FriendsListView(player, pattern, items, friendModule, state, title).open()
                }
            }
            "friend_profile", "profile" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                val target = args.getOrNull(0) ?: player.name
                val targetUid = CyuIdHook.getUidByName(target) ?: target
                val targetName = CyuIdHook.getName(targetUid) ?: target
                openGui(player, plugin, "friend_profile.yml", ViewTitles.friendProfile(targetName), mapOf("%target_name%" to targetName)) { pattern, items, title ->
                    FriendProfileView(player, pattern, items, friendModule, targetUid, targetName, title).open()
                }
            }
            "friend_profile_details", "profile_details", "details" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                val target = args.getOrNull(0) ?: player.name
                val targetUid = CyuIdHook.getUidByName(target) ?: target
                val targetName = CyuIdHook.getName(targetUid) ?: target
                openGui(player, plugin, "friend_profile_details.yml", ViewTitles.friendProfileDetails(targetName), mapOf("%target_name%" to targetName)) { pattern, items, title ->
                    FriendProfileView(player, pattern, items, friendModule, targetUid, targetName, title).open()
                }
            }
            "friend_profile_social", "profile_social" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                val target = args.getOrNull(0) ?: player.name
                openGui(player, plugin, "friend_profile_social.yml", ViewTitles.friendProfileSocial(target), mapOf("%target_name%" to target)) { pattern, items, title ->
                    FriendProfileSocialView(player, pattern, items, friendModule, target, title).open()
                }
            }
            "notification_center", "notifications" -> {
                val profileModule = modules.getModule<ProfileModule>("profile") ?: return false
                openGui(player, plugin, "notification_center.yml", ViewTitles.notificationCenter()) { pattern, items, title ->
                    NotificationCenterView(player, pattern, items, plugin, profileModule, title).open()
                }
            }
            "settings_panel", "settings" -> {
                val profileModule = modules.getModule<ProfileModule>("profile") ?: return false
                openGui(player, plugin, "settings_panel.yml", ViewTitles.settings()) { pattern, items, title ->
                    SettingsView(player, pattern, items, profileModule, title).open()
                }
            }
            "settings_social", "socialsettings" -> {
                val profileModule = modules.getModule<ProfileModule>("profile") ?: return false
                openGui(player, plugin, "settings_social.yml", ViewTitles.socialSettings()) { pattern, items, title ->
                    SocialSettingsView(player, pattern, items, profileModule, title).open()
                }
            }
            "birthdays_list", "birthdays" -> {
                val profileModule = modules.getModule<ProfileModule>("profile") ?: return false
                openGui(player, plugin, "birthdays_list.yml", ViewTitles.birthdays()) { pattern, items, title ->
                    BirthdaysView(player, pattern, items, plugin, profileModule, title).open()
                }
            }
            "requests_list", "requests" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                openGui(player, plugin, "requests_list.yml", ViewTitles.requestsList()) { pattern, items, title ->
                    RequestsView(player, pattern, items, friendModule, title).open()
                }
            }
            "sent_requests", "sentrequests" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                openGui(player, plugin, "sent_requests.yml", ViewTitles.sentRequestsList()) { pattern, items, title ->
                    SentRequestsView(player, pattern, items, friendModule, title).open()
                }
            }
            "blacklist", "blocks" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                openGui(player, plugin, "blacklist.yml", ViewTitles.blacklist()) { pattern, items, title ->
                    BlacklistView(player, pattern, items, friendModule, title).open()
                }
            }
            "online_players", "online" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                openGui(player, plugin, "online_players.yml", ViewTitles.onlinePlayers()) { pattern, items, title ->
                    OnlinePlayersView(player, pattern, items, friendModule, title).open()
                }
            }
            "friend_recommendations", "recommendations", "recommend" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                openGui(player, plugin, "friend_recommendations.yml", ViewTitles.friendRecommendations()) { pattern, items, title ->
                    RecommendationsView(player, pattern, items, friendModule, title).open()
                }
            }
            "friend_timeline", "timeline" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                val target = args.getOrNull(0) ?: player.name
                openGui(player, plugin, "friend_timeline.yml", ViewTitles.friendTimeline(target), mapOf("%target_name%" to target)) { pattern, items, title ->
                    FriendTimelineView(player, pattern, items, friendModule, target, title).open()
                }
            }
            "status_list", "status" -> {
                val socialModule = modules.getModule<SocialModule>("social") ?: return false
                openGui(player, plugin, "status_list.yml", ViewTitles.statusFeed()) { pattern, items, title ->
                    StatusView(player, pattern, items, socialModule, null, title).open()
                }
            }
            "wall_view", "wall" -> {
                val socialModule = modules.getModule<SocialModule>("social") ?: return false
                val target = args.getOrNull(0) ?: player.name
                openGui(player, plugin, "wall_view.yml", ViewTitles.wall(target), mapOf("%target_name%" to target)) { pattern, items, title ->
                    WallView(player, pattern, items, socialModule, target, title).open()
                }
            }
            "groups_list", "groups" -> {
                val groupModule = modules.getModule<GroupModule>("group") ?: return false
                openGui(player, plugin, "groups_list.yml", ViewTitles.friendGroups()) { pattern, items, title ->
                    GroupListView(player, pattern, items, groupModule, title).open()
                }
            }
            "messages_list", "messages" -> {
                val chatModule = modules.getModule<ChatModule>("chat") ?: return false
                openGui(player, plugin, "messages_list.yml", ViewTitles.unreadMessages()) { pattern, items, title ->
                    MessagesView(player, pattern, items, chatModule, title).open()
                }
            }
            "add_friend" -> {
                val target = args.getOrNull(0) ?: ""
                openGui(player, plugin, "add_friend.yml", ViewTitles.addFriend(target), mapOf("%target_name%" to target)) { pattern, items, title ->
                    AddFriendView(player, pattern, items, target, title).open()
                }
            }
            "friend_tag_filters", "tagfilters" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                val current = args.getOrNull(0)
                openGui(player, plugin, "friend_tag_filters.yml", ViewTitles.friendTagFilters(current)) { pattern, items, title ->
                    FriendTagFilterView(player, pattern, items, friendModule, current, title).open()
                }
            }
            "friend_tag_manage", "tagmanage" -> {
                val friendModule = modules.getModule<FriendModule>("friend") ?: return false
                val target = args.getOrNull(0) ?: player.name
                openGui(player, plugin, "friend_tag_manage.yml", ViewTitles.friendTagManage(target), mapOf("%target_name%" to target)) { pattern, items, title ->
                    FriendTagManageView(player, pattern, items, friendModule, target, title).open()
                }
            }
            else -> false
        }
    }
}
