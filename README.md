**简体中文** · [English](README_EN.md)

# CyuFriends · 好友与社交核心

[![Release](https://img.shields.io/github/v/release/HansOffice/cyufriends-reload?style=for-the-badge&logo=github&color=22c55e)](https://github.com/HansOffice/cyufriends-reload/releases)
[![Paper](https://img.shields.io/badge/Paper-1.21+-0284c7?style=for-the-badge&logo=paper&logoColor=white)](https://papermc.io)
[![Folia](https://img.shields.io/badge/Folia-Supported-7c3aed?style=for-the-badge)](https://papermc.io/software/folia)
[![Spigot](https://img.shields.io/badge/Spigot-1.16.5%20~%201.20.x-ed8b00?style=for-the-badge&logo=spigotmc&logoColor=white)](https://www.spigotmc.org)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-8%20%7C%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![License](https://img.shields.io/badge/License-MIT-2563eb?style=for-the-badge)](LICENSE)

用于 Minecraft 服务端的好友与社交核心插件，适用于 1.16.5 ~ 1.21+，支持 Paper、Folia 与 Spigot，包含好友流转、分组标签、好友传送、点对点私聊、离线留言中心、动态广场、主页留言墙、个人名片与生日祝福

[官方文档与 Wiki](https://nexu.cyucloud.cn) · [版本下载 (Releases)](https://github.com/HansOffice/cyufriends-reload/releases) · [CyuCloud服主技术交流群](https://qm.qq.com/q/2sW504fPF2) · [提交问题](https://github.com/HansOffice/cyufriends-reload/issues)

---

## 运行包

预编译运行包可前往 [GitHub Releases](https://github.com/HansOffice/cyufriends-reload/releases) 直接获取：

| 服务端核心 | 运行包 | 适用版本 |
| --- | --- | --- |
| ![Paper](https://img.shields.io/badge/Paper-0284c7?style=flat-square&logo=paper&logoColor=white) | `cyufriends-reload-paper-1.1.8.jar` | Paper 及 Purpur 1.21 及以上版本 |
| ![Folia](https://img.shields.io/badge/Folia-7c3aed?style=flat-square) | `cyufriends-reload-folia-1.1.8.jar` | Folia 1.21 及以上版本 |
| ![Spigot](https://img.shields.io/badge/Spigot-ed8b00?style=flat-square&logo=spigotmc&logoColor=white) | `cyufriends-reload-legacy-1.1.8.jar` | Spigot 及 Bukkit 1.16.5 ~ 1.20.x 版本 |

---

## 内容介绍

### 关系管理与传送
- **好友流转**：提供好友申请、双向确认、自定义分组标签与黑名单拦截
- **好友传送**：支持发起定向传送请求与防误触确认机制
- **资料名片**：支持自定义个性签名、生日广播与进服互动摘要

### 私聊与离线留言
- **点对点私聊**：提供独立聊天子指令与最近联系人一键快速回复
- **离线信箱**：好友离线自动转存留言，上线自动提醒并支持界面翻阅
- **隐私偏好**：支持自定义接收开关，按需关闭陌生人申请或传送打扰

### 动态广场与留言墙
- **全服动态**：支持在游戏内发布个人动态，与全服玩家点赞互动
- **主页留言墙**：玩家专属个人空间主页，好友可随心拜访留言
- **界面定制**：支持字符布局箱子菜单定制、头颅异步加载与材质桥接

---

## 指令

| 指令 | 效果 | 权限 |
| --- | --- | --- |
| `/friend` | 打开好友系统主界面 | 全体玩家 |
| `/friend help [分类] [页码]` | 查看可用命令指引 | 全体玩家 |
| `/friend add <玩家>` | 发送好友申请 | 全体玩家 |
| `/friend requests` | 查看待处理的好友申请列表 | 全体玩家 |
| `/friend group` | 打开好友分组管理界面 | 全体玩家 |
| `/friend tp <好友>` | 发起好友传送请求 | 全体玩家 |
| `/friend msg <好友> <内容>` | 发送好友私聊，离线自动存入留言信箱 | 全体玩家 |
| `/friend reply <内容>` | 快速回复最近一位私聊好友 | 全体玩家 |
| `/friend messages` | 打开未读离线留言中心 | 全体玩家 |
| `/status` | 打开动态广场主界面 | 全体玩家 |
| `/wall` | 打开个人主页留言墙 | 全体玩家 |
| `/settings` | 打开个人社交与隐私设置面板 | 全体玩家 |
| `/bio <签名>` | 设置个人名片个性签名 | 全体玩家 |
| `/birthday <yyyy-MM-dd>` | 绑定个人生日日期 | 全体玩家 |
| `/friend reload` | 重载配置、语言、音效与菜单 | 管理员 |
| `/friend admin inspect <玩家>` | 查看玩家社交数据与互动记录 | 管理员 |
| `/friend admin rebuild <玩家>` | 重建并刷新指定玩家数据缓存 | 管理员 |
| `/friend admin health` | 检查数据库连通与模块运行状态 | 管理员 |
| `/friend admin proxy` | 查看跨服节点通信与时钟状态 | 管理员 |

---

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

---

## 配置

- `config.yml`：语言选择、功能模块开关、数据库连接与配额参数
- `lang/`：多语言提示文本，默认内置 `zh_cn.yml` 与 `en_us.yml`
- `sounds.yml`：交互动作与系统提示音效配置
- `gui/`：字符布局箱子菜单配置目录
- `data/`：本地 SQLite 数据库文件存储目录

---

## 构建

```bash
# 构建 Paper 运行包
mvn clean package -Ppaper -DskipTests

# 构建 Folia 运行包
mvn package -Pfolia -DskipTests

# 构建 1.16.5~1.20.x 运行包
mvn package -Plegacy -DskipTests
```

---

## 协议

本项目采用 MIT 开源许可协议，详情参见 [`LICENSE`](LICENSE)
