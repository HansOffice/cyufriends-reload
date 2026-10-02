package org.cyuCBMclean.cyufriendsReload.command

data class HelpCategory(
    val id: String,
    val name: String,
    val permission: String? = null,
    val module: String? = null
)

data class HelpLine(
    val key: String,
    val fallback: String,
    val command: String,
    val suggestCommand: String,
    val permission: String? = null,
    val module: String? = null,
    val category: String = "basic"
)

val HELP_CATEGORIES = listOf(
    HelpCategory("basic", "基础"),
    HelpCategory("tag", "标签"),
    HelpCategory("group", "分组", module = "group"),
    HelpCategory("chat", "私聊", module = "chat"),
    HelpCategory("social", "广场", module = "social"),
    HelpCategory("profile", "主页", module = "profile"),
    HelpCategory("admin", "管理", permission = "cyufriends.admin")
)

val HELP_LINES = listOf(
    HelpLine("help-add", "<#D7DEE8>› /friend add [玩家] [附言...]</#D7DEE8> <#8A96A8>发送好友申请，可附带说明</#8A96A8>", "/friend add [玩家] [附言...]", "/friend add ", category = "basic"),
    HelpLine("help-revoke", "<#D7DEE8>› /friend revoke [玩家]</#D7DEE8> <#8A96A8>撤回一条仍在等待中的好友申请</#8A96A8>", "/friend revoke [玩家]", "/friend revoke ", category = "basic"),
    HelpLine("help-accept", "<#D7DEE8>› /friend accept [玩家]</#D7DEE8> <#8A96A8>同意好友申请</#8A96A8>", "/friend accept [玩家]", "/friend accept ", category = "basic"),
    HelpLine("help-deny", "<#D7DEE8>› /friend deny [玩家]</#D7DEE8> <#8A96A8>拒绝好友申请</#8A96A8>", "/friend deny [玩家]", "/friend deny ", category = "basic"),
    HelpLine("help-remove", "<#D7DEE8>› /friend remove [玩家]</#D7DEE8> <#8A96A8>解除好友关系</#8A96A8>", "/friend remove [玩家]", "/friend remove ", category = "basic"),
    HelpLine("help-block", "<#D7DEE8>› /friend block [玩家]</#D7DEE8> <#8A96A8>加入黑名单</#8A96A8>", "/friend block [玩家]", "/friend block ", category = "basic"),
    HelpLine("help-unblock", "<#D7DEE8>› /friend unblock [玩家]</#D7DEE8> <#8A96A8>移出黑名单</#8A96A8>", "/friend unblock [玩家]", "/friend unblock ", category = "basic"),
    HelpLine("help-tp", "<#D7DEE8>› /friend tp [好友]</#D7DEE8> <#8A96A8>按对方规则直接传送，或在需要确认时发起请求</#8A96A8>", "/friend tp [好友]", "/friend tp ", category = "basic"),
    HelpLine("help-tpaccept", "<#D7DEE8>› /friend tpaccept</#D7DEE8> <#8A96A8>在需要确认时接受传送请求</#8A96A8>", "/friend tpaccept", "/friend tpaccept", category = "basic"),
    HelpLine("help-tpdeny", "<#D7DEE8>› /friend tpdeny</#D7DEE8> <#8A96A8>在需要确认时拒绝传送请求</#8A96A8>", "/friend tpdeny", "/friend tpdeny", category = "basic"),
    HelpLine("help-tptoggle", "<#D7DEE8>› /friend tptoggle</#D7DEE8> <#8A96A8>轮换全局规则：允许直达 / 需要确认 / 拒绝传送</#8A96A8>", "/friend tptoggle", "/friend tptoggle", category = "basic"),
    HelpLine("help-note", "<#D7DEE8>› /friend note [好友] [备注]</#D7DEE8> <#8A96A8>设置好友备注</#8A96A8>", "/friend note [好友] [备注]", "/friend note ", category = "basic"),
    HelpLine("help-notedetail", "<#D7DEE8>› /friend notedetail [好友] [备注描述]</#D7DEE8> <#8A96A8>设置好友备注说明</#8A96A8>", "/friend notedetail [好友] [备注描述]", "/friend notedetail ", category = "basic"),
    HelpLine("help-pin", "<#D7DEE8>› /friend pin [好友]</#D7DEE8> <#8A96A8>将好友置顶</#8A96A8>", "/friend pin [好友]", "/friend pin ", category = "basic"),
    HelpLine("help-unpin", "<#D7DEE8>› /friend unpin [好友]</#D7DEE8> <#8A96A8>取消好友置顶</#8A96A8>", "/friend unpin [好友]", "/friend unpin ", category = "basic"),
    HelpLine("help-list", "<#D7DEE8>› /friend list</#D7DEE8> <#8A96A8>打开好友列表，并支持搜索、标签筛选与排序</#8A96A8>", "/friend list", "/friend list", category = "basic"),
    HelpLine("help-requests", "<#D7DEE8>› /friend requests</#D7DEE8> <#8A96A8>打开收到的好友申请列表，追加 chat 可直接在聊天栏点按处理</#8A96A8>", "/friend requests", "/friend requests", category = "basic"),
    HelpLine("help-sentrequests", "<#D7DEE8>› /friend sentrequests</#D7DEE8> <#8A96A8>查看并撤回我发出的好友申请，追加 chat 可直接点按撤回</#8A96A8>", "/friend sentrequests", "/friend sentrequests", category = "basic"),
    HelpLine("help-recommend", "<#D7DEE8>› /friend recommend</#D7DEE8> <#8A96A8>查看推荐好友，并可用 snooze / hide / restore 管理推荐</#8A96A8>", "/friend recommend", "/friend recommend", category = "basic"),
    HelpLine("help-timeline", "<#D7DEE8>› /friend timeline [好友]</#D7DEE8> <#8A96A8>查看与这位好友最近的私聊、动态与留言互动记录</#8A96A8>", "/friend timeline [好友]", "/friend timeline ", category = "basic"),

    HelpLine("help-tag", "<#D7DEE8>› /friend tag [好友] [标签]</#D7DEE8> <#8A96A8>设置好友标签</#8A96A8>", "/friend tag [好友] [标签]", "/friend tag ", category = "tag"),
    HelpLine("help-tags", "<#D7DEE8>› /friend tags [好友]</#D7DEE8> <#8A96A8>查看好友的全部标签</#8A96A8>", "/friend tags [好友]", "/friend tags ", category = "tag"),
    HelpLine("help-tagprimary", "<#D7DEE8>› /friend tagprimary [好友] [标签]</#D7DEE8> <#8A96A8>设置标签的主显示顺序</#8A96A8>", "/friend tagprimary [好友] [标签]", "/friend tagprimary ", category = "tag"),
    HelpLine("help-tagcolor", "<#D7DEE8>› /friend tagcolor [好友] [标签] [颜色]</#D7DEE8> <#8A96A8>为指定标签设置展示颜色</#8A96A8>", "/friend tagcolor [好友] [标签] [颜色]", "/friend tagcolor ", category = "tag"),
    HelpLine("help-untagcolor", "<#D7DEE8>› /friend untagcolor [好友] [标签]</#D7DEE8> <#8A96A8>清除指定标签的颜色</#8A96A8>", "/friend untagcolor [好友] [标签]", "/friend untagcolor ", category = "tag"),
    HelpLine("help-tagfilter", "<#D7DEE8>› /friend tagfilter [标签]</#D7DEE8> <#8A96A8>按标签筛选好友列表，留空时打开筛选面板</#8A96A8>", "/friend tagfilter [标签]", "/friend tagfilter ", category = "tag"),
    HelpLine("help-untag", "<#D7DEE8>› /friend untag [好友] [标签]</#D7DEE8> <#8A96A8>移除指定标签，留空则清空全部标签</#8A96A8>", "/friend untag [好友] [标签]", "/friend untag ", category = "tag"),

    HelpLine("help-group", "<#D7DEE8>› /friend group [好友] [分组]</#D7DEE8> <#8A96A8>设置好友分组</#8A96A8>", "/friend group [好友] [分组]", "/friend group ", module = "group", category = "group"),
    HelpLine("help-grouplist", "<#D7DEE8>› /friend grouplist</#D7DEE8> <#8A96A8>查看好友分组</#8A96A8>", "/friend grouplist", "/friend grouplist", module = "group", category = "group"),
    HelpLine("help-groupmoveall", "<#D7DEE8>› /friend groupmoveall [来源分组] [目标分组]</#D7DEE8> <#8A96A8>把整个分组批量移动到另一个分组；分组名含空格时用 -- 分隔</#8A96A8>", "/friend groupmoveall [来源分组] [目标分组]", "/friend groupmoveall ", module = "group", category = "group"),
    HelpLine("help-grouprules", "<#D7DEE8>› /friend grouprules [分组]</#D7DEE8> <#8A96A8>打开分组规则页；分组名像 show/set/cycle 时用 -- 分隔</#8A96A8>", "/friend grouprules [分组]", "/friend grouprules ", module = "group", category = "group"),

    HelpLine("help-chat", "<#D7DEE8>› /friend chat [好友]</#D7DEE8> <#8A96A8>打开私聊会话</#8A96A8>", "/friend chat [好友]", "/friend chat ", module = "chat", category = "chat"),
    HelpLine("help-msg", "<#D7DEE8>› /friend msg [好友] [内容]</#D7DEE8> <#8A96A8>发送私聊消息</#8A96A8>", "/friend msg [好友] [内容]", "/friend msg ", module = "chat", category = "chat"),
    HelpLine("help-reply", "<#D7DEE8>› /friend reply [内容]</#D7DEE8> <#8A96A8>回复上一位聊天对象</#8A96A8>", "/friend reply [内容]", "/friend reply ", module = "chat", category = "chat"),
    HelpLine("help-messages", "<#D7DEE8>› /friend messages</#D7DEE8> <#8A96A8>打开最近会话与未读私聊列表；追加 chat 可直接点按打开、已读、资料</#8A96A8>", "/friend messages", "/friend messages", module = "chat", category = "chat"),

    HelpLine("help-status", "<#D7DEE8>› /friend status</#D7DEE8> <#8A96A8>打开动态列表</#8A96A8>", "/friend status", "/friend status", module = "social", category = "social"),
    HelpLine("help-wall", "<#D7DEE8>› /friend wall [玩家]</#D7DEE8> <#8A96A8>打开留言板</#8A96A8>", "/friend wall [玩家]", "/friend wall ", module = "social", category = "social"),
    HelpLine("help-profilesocial", "<#D7DEE8>› /friend profilesocial [好友]</#D7DEE8> <#8A96A8>按好友单独控制动态与留言墙互动提醒</#8A96A8>", "/friend profilesocial [好友]", "/friend profilesocial ", module = "social", category = "social"),
    HelpLine("help-status-publish", "<#D7DEE8>› /status publish [public|friends|private] [内容]</#D7DEE8> <#8A96A8>发布动态</#8A96A8>", "/status publish [可见范围] [内容]", "/status publish public ", module = "social", category = "social"),
    HelpLine("help-status-comment", "<#D7DEE8>› /status comment [ID] [内容]</#D7DEE8> <#8A96A8>给动态添加评论</#8A96A8>", "/status comment [ID] [内容]", "/status comment ", module = "social", category = "social"),
    HelpLine("help-status-comments", "<#D7DEE8>› /status comments [ID]</#D7DEE8> <#8A96A8>查看动态评论</#8A96A8>", "/status comments [ID]", "/status comments ", module = "social", category = "social"),
    HelpLine("help-status-like", "<#D7DEE8>› /status like [ID]</#D7DEE8> <#8A96A8>给动态点赞或取消点赞</#8A96A8>", "/status like [ID]", "/status like ", module = "social", category = "social"),
    HelpLine("help-status-pin", "<#D7DEE8>› /status pin [ID]</#D7DEE8> <#8A96A8>置顶动态</#8A96A8>", "/status pin [ID]", "/status pin ", module = "social", category = "social"),
    HelpLine("help-wall-post", "<#D7DEE8>› /wall post [玩家] [内容]</#D7DEE8> <#8A96A8>发布留言，可选公开、好友可见或仅墙主可见</#8A96A8>", "/wall post [玩家] [内容]", "/wall post ", module = "social", category = "social"),
    HelpLine("help-wall-comment", "<#D7DEE8>› /wall comment [ID] [内容]</#D7DEE8> <#8A96A8>给留言添加评论</#8A96A8>", "/wall comment [ID] [内容]", "/wall comment ", module = "social", category = "social"),
    HelpLine("help-wall-comments", "<#D7DEE8>› /wall comments [ID]</#D7DEE8> <#8A96A8>查看留言评论</#8A96A8>", "/wall comments [ID]", "/wall comments ", module = "social", category = "social"),
    HelpLine("help-wall-commentpending", "<#D7DEE8>› /wall commentpending [留言ID]</#D7DEE8> <#8A96A8>查看一条留言下的待审评论，追加 chat 可直接点按审核</#8A96A8>", "/wall commentpending [留言ID]", "/wall commentpending ", module = "social", category = "social"),
    HelpLine("help-wall-commentapprove", "<#D7DEE8>› /wall commentapprove [评论ID]</#D7DEE8> <#8A96A8>通过一条待审评论</#8A96A8>", "/wall commentapprove [评论ID]", "/wall commentapprove ", module = "social", category = "social"),
    HelpLine("help-wall-commentreject", "<#D7DEE8>› /wall commentreject [评论ID]</#D7DEE8> <#8A96A8>拒绝并删除一条待审评论</#8A96A8>", "/wall commentreject [评论ID]", "/wall commentreject ", module = "social", category = "social"),
    HelpLine("help-wall-commentapproveall", "<#D7DEE8>› /wall commentapproveall [留言ID]</#D7DEE8> <#8A96A8>批量通过一条留言下的全部待审评论</#8A96A8>", "/wall commentapproveall [留言ID]", "/wall commentapproveall ", module = "social", category = "social"),
    HelpLine("help-wall-commentrejectall", "<#D7DEE8>› /wall commentrejectall [留言ID]</#D7DEE8> <#8A96A8>批量拒绝一条留言下的全部待审评论</#8A96A8>", "/wall commentrejectall [留言ID]", "/wall commentrejectall ", module = "social", category = "social"),
    HelpLine("help-wall-like", "<#D7DEE8>› /wall like [ID]</#D7DEE8> <#8A96A8>给留言点赞或取消点赞</#8A96A8>", "/wall like [ID]", "/wall like ", module = "social", category = "social"),
    HelpLine("help-wall-pin", "<#D7DEE8>› /wall pin [ID]</#D7DEE8> <#8A96A8>置顶留言或取消置顶</#8A96A8>", "/wall pin [ID]", "/wall pin ", module = "social", category = "social"),
    HelpLine("help-wall-pending", "<#D7DEE8>› /wall pending [玩家]</#D7DEE8> <#8A96A8>查看留言墙待审核列表，追加 chat 可直接点按通过或拒绝</#8A96A8>", "/wall pending [玩家]", "/wall pending ", module = "social", category = "social"),
    HelpLine("help-wall-approve", "<#D7DEE8>› /wall approve [ID]</#D7DEE8> <#8A96A8>通过一条待审核留言</#8A96A8>", "/wall approve [ID]", "/wall approve ", module = "social", category = "social"),
    HelpLine("help-wall-approveall", "<#D7DEE8>› /wall approveall [玩家]</#D7DEE8> <#8A96A8>批量通过指定留言墙的全部待审留言</#8A96A8>", "/wall approveall [玩家]", "/wall approveall ", module = "social", category = "social"),
    HelpLine("help-wall-reject", "<#D7DEE8>› /wall reject [ID]</#D7DEE8> <#8A96A8>拒绝并删除一条待审核留言</#8A96A8>", "/wall reject [ID]", "/wall reject ", module = "social", category = "social"),
    HelpLine("help-wall-rejectall", "<#D7DEE8>› /wall rejectall [玩家]</#D7DEE8> <#8A96A8>批量拒绝指定留言墙的全部待审留言</#8A96A8>", "/wall rejectall [玩家]", "/wall rejectall ", module = "social", category = "social"),

    HelpLine("help-gui", "<#D7DEE8>› /friend gui</#D7DEE8> <#8A96A8>打开个人主页</#8A96A8>", "/friend gui", "/friend gui", module = "profile", category = "profile"),
    HelpLine("help-settings", "<#D7DEE8>› /friend settings</#D7DEE8> <#8A96A8>打开个人设置</#8A96A8>", "/friend settings", "/friend settings", module = "profile", category = "profile"),
    HelpLine("help-socialsettings", "<#D7DEE8>› /friend socialsettings</#D7DEE8> <#8A96A8>细分控制动态和留言墙互动提醒</#8A96A8>", "/friend socialsettings", "/friend socialsettings", module = "profile", category = "profile"),
    HelpLine("help-notifications", "<#D7DEE8>› /friend notifications</#D7DEE8> <#8A96A8>打开通知工作台，直接处理申请、消息、审核、生日与推荐</#8A96A8>", "/friend notifications", "/friend notifications", module = "profile", category = "profile"),
    HelpLine("help-birthdays", "<#D7DEE8>› /friend birthdays</#D7DEE8> <#8A96A8>打开好友生日列表，快速前往资料、私聊或留言墙</#8A96A8>", "/friend birthdays", "/friend birthdays", module = "profile", category = "profile"),
    HelpLine("help-birthday", "<#D7DEE8>› /birthday [yyyy-MM-dd]</#D7DEE8> <#8A96A8>设置生日</#8A96A8>", "/birthday [yyyy-MM-dd]", "/birthday ", module = "profile", category = "profile"),
    HelpLine("help-profile-birthday", "<#D7DEE8>› /friend profile set birthday [yyyy-MM-dd]</#D7DEE8> <#8A96A8>设置生日</#8A96A8>", "/friend profile set birthday [yyyy-MM-dd]", "/friend profile set birthday ", module = "profile", category = "profile"),
    HelpLine("help-personal", "<#D7DEE8>› /friend personal [好友] [规则]</#D7DEE8> <#8A96A8>设置单个好友的传送、上下线与互动提醒规则</#8A96A8>", "/friend personal [好友] [规则]", "/friend personal ", module = "profile", category = "profile"),
    HelpLine("help-notify", "<#D7DEE8>› /friend notify</#D7DEE8> <#8A96A8>切换好友上下线提醒</#8A96A8>", "/friend notify", "/friend notify", module = "profile", category = "profile"),
    HelpLine("help-notifyme", "<#D7DEE8>› /friend notifyme</#D7DEE8> <#8A96A8>切换自己的上下线广播</#8A96A8>", "/friend notifyme", "/friend notifyme", module = "profile", category = "profile"),

    HelpLine("help-reload", "<#D7DEE8>› /friend reload</#D7DEE8> <#8A96A8>重载插件配置与语言文本</#8A96A8>", "/friend reload", "/friend reload", permission = "cyufriends.admin", category = "admin"),
    HelpLine("help-admin", "<#D7DEE8>› /friend admin [inspect|rebuild|proxy|cache|health|debug] [玩家]</#D7DEE8> <#8A96A8>检查玩家、缓存、代理、健康状态与调试输出</#8A96A8>", "/friend admin [项目] [玩家]", "/friend admin ", permission = "cyufriends.admin", category = "admin"),
    HelpLine("help-admin-legacy", "<#D7DEE8>› /friend admin legacy [inspect|import] [范围]</#D7DEE8> <#8A96A8>扫描旧版数据表，并将旧版好友数据保守迁入 reload 版</#8A96A8>", "/friend admin legacy [inspect|import] [范围]", "/friend admin legacy ", permission = "cyufriends.admin", category = "admin"),
    HelpLine("help-admin-moderation", "<#D7DEE8>› /friend admin moderation [玩家]</#D7DEE8> <#8A96A8>查看全局或指定玩家的审核队列与最近审核记录</#8A96A8>", "/friend admin moderation [玩家]", "/friend admin moderation ", permission = "cyufriends.admin", category = "admin")
)
