# CyuFriends Reload

Cyu 系列好友与社交核心插件。

为 Minecraft 服务器提供好友关系流转、自定义分组、好友传送、点对点私聊、离线留言中心、动态广场、主页留言墙、个人名片与生日祝福；支持字符布局自定义菜单与双语提示。

## 常用入口

| 命令 | 用途 |
| --- | --- |
| `/friend` | 打开好友系统主界面 |
| `/friend help [分类] [页码]` | 查看帮助说明（条目可点击回填，按权限过滤） |
| `/friend add <玩家>` | 发送好友申请 |
| `/friend requests` | 查看待处理的好友申请列表 |
| `/friend group` | 打开好友分组管理界面 |
| `/friend tp <好友>` | 发起好友传送请求 |
| `/friend msg <好友> <内容>` | 发送好友私聊（离线自动存为留言） |
| `/friend reply <内容>` | 快速回复最近一位私聊好友 |
| `/friend messages` | 打开未读离线留言中心 |
| `/status` | 打开动态广场主界面 |
| `/wall` | 打开个人主页留言墙 |
| `/settings` | 打开个人社交与隐私设置面板 |
| `/bio <签名>` | 设置个人名片个性签名 |
| `/birthday <yyyy-MM-dd>` | 绑定个人生日日期 |
| `/friend reload` | 重载配置、语言、音效与菜单（管理员） |
| `/friend admin inspect <玩家>` | 查看玩家社交数据与互动记录（管理员） |
| `/friend admin rebuild <玩家>` | 重建并刷新指定玩家数据缓存（管理员） |
| `/friend admin health` | 检查数据库连通与模块运行状态（管理员） |
| `/friend admin proxy` | 查看跨服网桥节点通信与时钟状态（管理员） |

| 权限 | 用途 | 默认赋予 |
| --- | --- | --- |
| `cyufriends.use` | 好友基础功能（打开主界面、添加好友、申请列表、分组管理、好友传送） | 全体玩家 |
| `cyufriends.command.msg` | 好友私聊与离线留言 | 全体玩家 |
| `cyufriends.command.reply` | 快速回复最近私聊好友 | 全体玩家 |
| `cyufriends.command.messages` | 打开未读留言中心 | 全体玩家 |
| `cyufriends.command.status` | 浏览与发布动态广场 | 全体玩家 |
| `cyufriends.command.wall` | 查看与维护个人主页留言墙 | 全体玩家 |
| `cyufriends.command.settings` | 调整个人社交与隐私偏好 | 全体玩家 |
| `cyufriends.command.bio` | 设置个人名片个性签名 | 全体玩家 |
| `cyufriends.command.birthday` | 绑定与更新个人生日日期 | 全体玩家 |
| `cyufriends.admin` | 全部管理命令（重载 / 检查 / 调试 / 数据管理） | OP |

## 配置文件

| 文件 / 目录 | 用途 |
| --- | --- |
| `config.yml` | 语言选择、模块开关、数据库连接、默认配额与跨服网桥参数 |
| `lang/zh_cn.yml` | 中文提示信息与语言文本（默认） |
| `lang/en_us.yml` | 英文提示信息与语言文本（`config.yml` 的 `language` 切换） |
| `sounds.yml` | 交互动作与系统提示音效配置 |
| `Permissions.yml` | 权限节点与 VIP 阶梯参考 |
| `Placeholder.yml` | PlaceholderAPI 变量列表与外部计分板接入参考 |
| `gui/` | 字符布局 GUI 菜单（支持原版材质、外部物品、玩家头颅、CustomModelData 与点击动作） |
| `data/data.db` | SQLite 模式本地数据库文件 |

## 变量支持 (PlaceholderAPI)

| 占位符 | 说明 |
| --- | --- |
| `%cyufriends_total_count%` | 当前玩家好友总数 |
| `%cyufriends_request_count%` | 当前玩家待处理申请数 |
| `%cyufriends_offline_messages_count%` | 当前玩家未读离线留言条数 |
| `%cyufriends_daily_requests_remaining%` | 当前玩家今日剩余可申请次数 |
| `%cyufriends_note_<player>%` | 当前玩家为指定好友设置的备注名 |
| `%cyufriends_group_<player>%` | 指定好友所在的自定义分组名 |
| `%cyufriends_is_friend_<player>%` | 指定玩家是否为当前玩家好友（返回 是 / 否） |
| `%cyufriends_birthday_<player>%` | 指定玩家的生日文本 |
| `%rel_cyufriends_is_friend%` | 双向关系变量：判断目标双方是否为好友 |
| `%rel_cyufriends_is_friend_bool%` | 双向关系变量：返回布尔值（true / false） |

## 源码结构

```text
src/main/kotlin/org/cyuCBMclean/cyufriendsReload/
├─ api/         公共 API 接口、服务定义与好友生命周期事件
├─ core/        生命周期装配、数据存储 (HikariCP/SQLite/MySQL) 与平台调度
├─ command/     指令路由、权限过滤、Tab 补全与帮助渲染器
├─ integration/ 外部插件联动 (CyuID-Reload / PlaceholderAPI)
├─ modules/
│  ├─ friend/   好友核心：关系建立、申请流转、黑名单与好友传送
│  ├─ group/    好友分组：自定义分组、分组成员管理与批量迁移
│  ├─ chat/     好友私聊：点对点私聊、快速回复与离线留言信箱
│  ├─ social/   社区广场：全服/好友动态、留言墙、互动点赞与内容审核
│  ├─ profile/  个人名片：个性签名、生日广播与社交通知偏好
│  └─ proxy/    跨服网桥：跨服在线感知、私聊转发与时钟偏差防护
└─ ui/          数据驱动 GUI 引擎、异步头颅加载、外部材质桥与点击防抖
```

## 构建

```bash
# 构建 Paper 1.21+ 运行包（默认）
mvn clean package -Ppaper -DskipTests

# 构建 Folia 1.21+ 运行包
mvn package -Pfolia -DskipTests

# 构建 Spigot 1.16-1.20 运行包（需 NBTAPI）
mvn package -Plegacy -DskipTests
```

构建产物位于：
- `target/cyufriends-reload-paper-1.1.6.jar`
- `target/cyufriends-reload-folia-1.1.6.jar`
- `target/cyufriends-reload-legacy-1.1.6.jar`
