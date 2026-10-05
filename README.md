# CyuFriends Reload

[![Build](https://github.com/HansOffice/cyufriendsplus-reload/actions/workflows/build.yml/badge.svg)](https://github.com/HansOffice/cyufriendsplus-reload/actions/workflows/build.yml)

Cyu 系列好友与轻社交核心插件

为 Minecraft 服务器提供好友关系流转、分组标签、好友传送、点对点私聊、离线留言中心、动态广场、主页留言墙、个人名片与生日祝福；支持数据驱动 GUI、异步头像预热、SkinsRestorer 桥接与跨服网桥中继

## 核心特性

- **好友关系链**：申请/同意/拒绝/撤回、拉黑防护、好友上线通知与无缝好友传送（支持世界隔离）
- **分组与标签**：自定义好友分组、批量成员迁移、自选标签颜色与主标签高亮
- **私聊与留言**：独立私聊指令空间（不占用系统 `/msg`）、快速回复与持久化离线留言信箱
- **社区广场**：全服/好友圈动态短文、留言墙主页、互动点赞评论、置顶与内容审核机制
- **个人名片**：个性签名设置、生日提醒广播、隐私保护设置与未读社交通知中心
- **多平台原生**：提供 Paper、Folia 与 Legacy 三端原生构建，Folia 区域调度原生适配，杜绝主线程阻塞
- **外部材质兼容**：菜单图标支持原版材质、CustomModelData 资源包、ItemsAdder、Oraxen、Nexo 与 CraftEngine
- **跨服网桥中继**：无缝对接 Velocity 与 BungeeCord 配套网桥，支持跨服在线感知与时钟漂移自适应

## 常用入口

| 命令 | 用途 | 权限节点 |
| --- | --- | --- |
| `/friend` | 打开好友系统主界面（别名：`/cf`、`/cfs`、`/cyuf`） | `cyufriends.use` |
| `/friend add <玩家>` | 发送好友申请 | `cyufriends.use` |
| `/friend requests` | 查看待处理的好友申请列表 | `cyufriends.use` |
| `/friend group` | 打开好友分组管理界面 | `cyufriends.use` |
| `/friend tp <好友>` | 发起好友传送请求 | `cyufriends.use` |
| `/friend msg <好友> <内容>` | 发送好友私聊（离线自动存为留言） | `cyufriends.command.msg` |
| `/friend reply <内容>` | 快速回复最近一位私聊好友 | `cyufriends.command.reply` |
| `/friend messages` | 打开未读离线留言中心 | `cyufriends.command.messages` |
| `/status` | 打开动态广场主界面 | `cyufriends.command.status` |
| `/wall` | 打开个人主页留言墙 | `cyufriends.command.wall` |
| `/settings` | 打开个人社交与隐私设置面板 | `cyufriends.command.settings` |
| `/bio <签名>` | 设置个人名片个性签名 | `cyufriends.command.bio` |
| `/birthday <yyyy-MM-dd>` | 绑定个人生日日期 | `cyufriends.command.birthday` |
| `/friend reload` | 安全重载配置、语言、音效与菜单（管理员） | `cyufriends.admin` |
| `/friend admin inspect <玩家>` | 全景透视玩家社交数据与互动时间线（管理员） | `cyufriends.admin` |
| `/friend admin rebuild <玩家>` | 重建并刷新指定玩家数据缓存（管理员） | `cyufriends.admin` |
| `/friend admin health` | 数据库连通、变量挂载与模块运行体检（管理员） | `cyufriends.admin` |
| `/friend admin proxy` | 查看跨服网桥节点通信与时钟状态（管理员） | `cyufriends.admin` |

## 配置文件导航

| 文件 / 目录 | 用途说明 |
| --- | --- |
| `config.yml` | 模块开关、数据库连接（SQLite/MySQL）、默认好友配额、跨服网桥配置与时钟校验参数 |
| `lang/*.yml` | 多语言提示与反馈文案（zh_cn / en_us），支持在 config.yml 一键切换 |
| `sounds.yml` | 申请、私聊、交互成功与失败等各类动作的音效与音高 |
| `Permissions.yml` | 完整权限节点清单与 VIP 阶梯档位配置参考 |
| `Placeholder.yml` | PlaceholderAPI 变量列表与外部计分板/聊天格式接入参考 |
| `gui/*.yml` | 数据驱动菜单布局配置，覆盖好友列表、申请、黑名单、资料、动态、留言墙等全部交互界面 |
| `data/data.db` | SQLite 模式本地数据库文件（旧版根目录文件自动平滑迁移） |

## 占位符变量 (PlaceholderAPI)

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

## 源码结构地图

```text
src/main/kotlin/org/cyuCBMclean/cyufriendsReload/
├─ api/             公共 API 接口、服务定义与好友生命周期事件
├─ core/            生命周期装配、数据存储 (HikariCP/SQLite/MySQL)、平台调度与配置管理
├─ command/         指令路由、鉴权过滤与 Tab 补全
├─ integration/     外部插件联动 (CyuID / PlaceholderAPI / 材质库 Hook)
├─ modules/
│  ├─ friend/       好友核心：关系建立、申请流转、黑名单与好友传送
│  ├─ group/        好友分组：自定义分组、分组成员管理与批量迁移
│  ├─ chat/         好友私聊：点对点私聊、快速回复与离线留言信箱
│  ├─ social/       社区广场：全服/好友动态、留言墙、互动点赞与内容审核
│  ├─ profile/      个人名片：个性签名、生日广播与社交通知偏好
│  └─ proxy/        跨服网桥：跨服在线感知、私聊转发与时钟偏差防护
└─ ui/              数据驱动 GUI 引擎、异步头颅加载、外部材质桥与点击防抖
```

## 构建


```bash
# 构建 Paper 1.21+ 运行包（默认）
mvn clean package -Ppaper -DskipTests

# 构建 Spigot 1.16-1.20 运行包
mvn package -Plegacy -DskipTests

# 构建 Folia 1.21+ 运行包
mvn package -Pfolia -DskipTests
```

构建产物位于：
- `target/cyufriends-reload-paper-1.1.6.jar`
- `target/cyufriends-reload-folia-1.1.6.jar`
- `target/cyufriends-reload-legacy-1.1.6.jar`

编译时可选本地放入 `libs/cyuid-reload-paper-1.0.4.jar` 提供 UID 扩展支持；PlaceholderAPI 自动由 Maven 中央库解析

## 跨服架构与网桥

单服环境直接开箱即用，默认 `modules.proxy: false`

在 BungeeCord 或 Velocity 群组网络中：
1. 各子服使用相同的 `proxy.secret` 与 `database`（跨服模式必须共用 MySQL 数据库）
2. 代理端单独安装配套网桥 `cyufriends-reload-proxy`（私有网桥组件，不包含在本开源仓库内）
3. 若存在跨机器或虚拟机系统时钟漂移，可在配置中将 `max-clock-skew-seconds` 设为 `0` 关闭时间校验，此时插件会自动启用 120 秒安全窗口执行消息防重放

## 开发者 API

附属插件建议通过 Maven 引入 API 依赖：

```xml
<dependency>
    <groupId>org.cyuCBMclean</groupId>
    <artifactId>cyufriends-reload</artifactId>
    <version>1.1.6</version>
    <classifier>api</classifier>
    <scope>provided</scope>
</dependency>
```

API 详细使用指南与监听事件参考见 [`API.md`](API.md)

## 开源协作

欢迎提交 Issue 与 Pull Request 参与共建：

- 提交代码前请确认通过 `mvn package -DskipTests` 编译验证
- Kotlin 源码保持高内聚，遵循 0 注释自解释代码风格
- 配置文件与提示信息保持自然平实的 Minecraft 术语风格，末尾不加多余机械句号
