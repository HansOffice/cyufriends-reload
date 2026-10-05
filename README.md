# CyuFriends-Reload 项目研发导航

Cyu 系列好友与轻社交核心插件。

为 Minecraft 服务器提供好友关系流转、自定义分组、好友传送、独立点对点私聊、离线留言中心、动态广场、主页留言墙、个人名片与生日祝福；支持三 Profile 多平台直连架构（Paper / Folia / Legacy）、字符布局数据驱动 GUI、异步头像预热、SkinsRestorer 桥接与跨服网桥中继。

## 仓库结构速查

* `pom.xml`：根 Maven 多模块构建配置，管理版本、三端构建 Profile 与依赖
* `src/main/kotlin/org/cyuCBMclean/cyufriendsReload/`：全平台通用核心业务源码
* `src/paper/kotlin/org/cyuCBMclean/cyufriendsReload/`：Paper 1.21+ 平台调度器与环境实现
* `src/folia/kotlin/org/cyuCBMclean/cyufriendsReload/`：Folia 1.21+ 局部区域调度器与环境实现
* `src/legacy/kotlin/org/cyuCBMclean/cyufriendsReload/`：Legacy (1.16.5-1.20) 平台兼容与 NBTAPI 适配
* `src/main/resources/`：默认配置（主配置、语言文件、音效配置与全部 GUI 菜单）
* `代理端/cyufriends-reload-proxy/`：Velocity 与 BungeeCord 独立跨服网络中继组件
* `附属/cyufriends-intimacy/`：好友亲密度与社交关系等级官方附属插件
* `用户文档/`：面向服主的纯黑盒使用指南 HTML
* `发行/`：发行脚本 `release.sh`、纯文本 `更新日志.md` 与发行 zip 产物

## 源码包地图与核心职责

```text
src/main/kotlin/org/cyuCBMclean/cyufriendsReload/
├─ CyufriendsReload.kt        插件主类，负责生命周期装配、模块加载、配置重载与平滑停机
├─ api/                       公共 API 接口、只读服务 CyuFriendsService 与好友事件模型
├─ core/
│  ├─ config/                 主配置装配、双版本协议升级与非斜体着色适配器 (ColorCompat)
│  ├─ database/               HikariCP 数据库管理器、SQLite/MySQL 存储抽象与 Schema 升级
│  └─ scheduler/              Paper/Folia 平台抽象调度器 (CyuConcurrency)
├─ command/                   领域指令树路由、权限过滤、Tab 呼吸感补全与低饱和帮助渲染器 (HelpRenderer)
├─ integration/               外部插件联动 (CyuID-Reload UID 扩展与 PlaceholderAPI 变量注册)
├─ modules/
│  ├─ friend/                 好友核心：建交流转、申请生命周期、黑名单、偏好设置与好友传送
│  ├─ group/                  好友分组：自定义分组、分组成员管理、标签颜色与批量迁移
│  ├─ chat/                   好友私聊：点对点私聊、快速回复、敏感词过滤与离线留言信箱
│  ├─ social/                 社区广场：全服/好友动态、留言墙、互动点赞评论与内容审核机制
│  ├─ profile/                个人名片：个性签名、生日广播与社交通知中心
│  └─ proxy/                  跨服网桥客户端：在线感知聚合、私聊转发与时钟偏差防重放
└─ ui/
   ├─ layout/                 字符布局引擎 (GuiPattern, GuiLoader, ItemTemplate, GuiTextFormatter)
   ├─ view/                   基础菜单视图 (CyuView, PaginatedView) 与菜单路由 (GuiRouter)
   ├─ compat/                 异步头颅材质加载 (GuiHeads) 与外部物品桥 (ExternalMenuItems, CraftEngineItems)
   └─ action/                 动作解析器 (ActionNode) 与点击事件枚举 (CyuClickType)
```

## 常用入口

| 命令 | 用途 | 权限节点 | 默认赋予 |
| --- | --- | --- | --- |
| `/friend` | 打开好友系统主界面（别名：`/cf`、`/cfs`、`/cyuf`） | `cyufriends.use` | 全体玩家 |
| `/friend help [分类] [页码]` | 查看帮助指南（分类导航、可点击回填与翻页） | `cyufriends.use` | 全体玩家 |
| `/friend add <玩家>` | 发送好友申请 | `cyufriends.use` | 全体玩家 |
| `/friend requests` | 查看待处理的好友申请列表 | `cyufriends.use` | 全体玩家 |
| `/friend group` | 打开好友分组管理界面 | `cyufriends.use` | 全体玩家 |
| `/friend tp <好友>` | 发起好友传送请求 | `cyufriends.use` | 全体玩家 |
| `/friend msg <好友> <内容>` | 发送好友私聊（离线自动存为留言） | `cyufriends.command.msg` | 全体玩家 |
| `/friend reply <内容>` | 快速回复最近一位私聊好友 | `cyufriends.command.reply` | 全体玩家 |
| `/friend messages` | 打开未读离线留言中心 | `cyufriends.command.messages` | 全体玩家 |
| `/status` | 打开动态广场主界面 | `cyufriends.command.status` | 全体玩家 |
| `/wall` | 打开个人主页留言墙 | `cyufriends.command.wall` | 全体玩家 |
| `/settings` | 打开个人社交与隐私设置面板 | `cyufriends.command.settings` | 全体玩家 |
| `/bio <签名>` | 设置个人名片个性签名 | `cyufriends.command.bio` | 全体玩家 |
| `/birthday <yyyy-MM-dd>` | 绑定个人生日日期 | `cyufriends.command.birthday` | 全体玩家 |
| `/friend reload` | 安全重载配置、语言、音效与菜单（管理员） | `cyufriends.admin` | OP |
| `/friend admin inspect <玩家>` | 全景透视玩家社交数据与互动时间线（管理员） | `cyufriends.admin` | OP |
| `/friend admin rebuild <玩家>` | 重建并刷新指定玩家数据缓存（管理员） | `cyufriends.admin` | OP |
| `/friend admin health` | 数据库连通、变量挂载与模块运行体检（管理员） | `cyufriends.admin` | OP |
| `/friend admin proxy` | 查看跨服网桥节点通信与时钟状态（管理员） | `cyufriends.admin` | OP |

## 配置文件导航

| 文件 / 目录 | 用途说明 |
| --- | --- |
| `config.yml` | 模块开关、数据库连接（SQLite/MySQL）、默认好友配额、跨服网桥配置与双版本协议 |
| `lang/zh_cn.yml` | 中文提示信息与反馈文案（默认） |
| `lang/en_us.yml` | 英文提示信息与反馈文案（通过 `config.yml` 的 `language` 切换） |
| `sounds.yml` | 申请、私聊、交互成功与失败等各类动作的音效与音高 |
| `Permissions.yml` | 完整权限节点清单与 VIP 阶梯档位配置参考 |
| `Placeholder.yml` | PlaceholderAPI 变量列表与外部计分板/聊天格式接入参考 |
| `gui/*.yml` | 字符画布局菜单配置，覆盖好友列表、申请、黑名单、资料、动态、留言墙等全部交互界面 |
| `data/data.db` | SQLite 模式本地数据库文件 |

## 变量支持 (PlaceholderAPI)

插件原生注册 `cyufriends` 前缀，并向前兼容旧版 `friends` 前缀：

| 占位符 | 说明 |
| --- | --- |
| `%cyufriends_total_count%` | 当前玩家拥有的好友总数 |
| `%cyufriends_request_count%` | 当前玩家收到的待处理申请数 |
| `%cyufriends_offline_messages_count%` | 当前玩家未读离线留言条数 |
| `%cyufriends_daily_requests_remaining%` | 当前玩家今日剩余可申请次数 |
| `%cyufriends_note_<player>%` | 当前玩家为指定好友设置的备注名 |
| `%cyufriends_group_<player>%` | 指定好友所在的自定义分组名 |
| `%cyufriends_is_friend_<player>%` | 指定玩家是否为当前玩家好友（返回 是 / 否） |
| `%cyufriends_birthday_<player>%` | 指定玩家的生日文本 |
| `%rel_cyufriends_is_friend%` | 双向关系变量：判断目标双方是否为好友 |
| `%rel_cyufriends_is_friend_bool%` | 双向关系变量：返回布尔值（true / false） |

## 核心业务链路

1. **好友申请流转**：玩家发起申请 -> 校验好友上限、每日申请配额与黑名单 -> 异步写入数据库 -> 目标玩家在线时即时推送并播放音效，离线则存为待处理记录
2. **私聊与离线留言**：发送好友私聊 -> 检测目标是否在线（本服或跨服网桥） -> 在线直接送达并更新快速回复上下文，离线自动转存入持久化留言信箱并标记未读
3. **跨服网桥中继**：子服通过 `cyufriends:gateway` 通道向代理端发送带 HMAC 签名的握手与数据包 -> 代理端校验时间戳偏差（若设为 0 则启用 120 秒防重放窗口）并维护全网在线快照 -> 定向路由跨服私聊与传送请求
4. **GUI 引擎与非斜体渲染**：YAML 字符网格由 `GuiLoader` 编译 -> `ItemTemplate` 解析材质、头颅与 MiniMessage -> `ColorCompat` 强制注入非斜体样式 (`decorationIfAbsent(ITALIC, false)`)，彻底杜绝菜单文字倾斜 -> 内置 250ms 点击防抖与分级容器保护

## 改需求去哪

* 改好友建交、申请与黑名单：`src/main/kotlin/.../modules/friend/`
* 改好友分组与批量迁移：`src/main/kotlin/.../modules/group/`
* 改私聊转发与离线留言：`src/main/kotlin/.../modules/chat/`
* 改动态广场与主页留言墙：`src/main/kotlin/.../modules/social/`
* 改个人签名与生日提醒：`src/main/kotlin/.../modules/profile/`
* 改跨服网桥客户端：`src/main/kotlin/.../modules/proxy/`，代理端本体位于 `代理端/cyufriends-reload-proxy/`
* 改菜单布局、槽位与动作：`src/main/resources/gui/` 与 `src/main/kotlin/.../ui/`
* 改命令路由与帮助排版：`src/main/kotlin/.../command/` 与 `FriendCommands.kt`
* 改数据库读写与表结构：`src/main/kotlin/.../core/database/`

## 构建与多平台

```bash
# 构建 Paper 1.21+ 运行包（默认）
mvn clean package -Ppaper -DskipTests

# 构建 Folia 1.21+ 运行包
mvn package -Pfolia -DskipTests

# 构建 Spigot 1.16-1.20 运行包（需 NBTAPI）
mvn package -Plegacy -DskipTests
```

构建产物位于：
* `target/cyufriends-reload-paper-1.1.6.jar`
* `target/cyufriends-reload-folia-1.1.6.jar`
* `target/cyufriends-reload-legacy-1.1.6.jar`

执行根目录发布脚本可一条龙完成三端构建与平铺打包：
```bash
bash 发行/release.sh
```
产物将自动归档至 `发行/cyufriends-reload-1.1.6.zip`
