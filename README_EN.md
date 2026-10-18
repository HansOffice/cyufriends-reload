[简体中文](README.md) · **English**

# CyuFriends

[![Release](https://img.shields.io/github/v/release/HansOffice/cyufriends-reload?style=for-the-badge&logo=github&color=22c55e)](https://github.com/HansOffice/cyufriends-reload/releases)
[![Paper](https://img.shields.io/badge/Paper-1.21+-0284c7?style=for-the-badge&logo=paper&logoColor=white)](https://papermc.io)
[![Folia](https://img.shields.io/badge/Folia-Supported-7c3aed?style=for-the-badge)](https://papermc.io/software/folia)
[![Spigot](https://img.shields.io/badge/Spigot-1.16.5%20~%201.20.x-ed8b00?style=for-the-badge&logo=spigotmc&logoColor=white)](https://www.spigotmc.org)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-8%20%7C%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![License](https://img.shields.io/badge/License-MIT-2563eb?style=for-the-badge)](LICENSE)

Social and friends plugin for Minecraft servers, supporting Paper, Folia, and Spigot on 1.16.5 ~ 1.21+, featuring relationship workflows, group tags, friend teleportation, direct messaging, offline mailboxes, activity feed, profile wall, and birthday wishes

[Official Wiki](https://nexu.cyucloud.cn) · [Releases](https://github.com/HansOffice/cyufriends-reload/releases) · [Community Group](https://qm.qq.com/q/2sW504fPF2) · [Issue Tracker](https://github.com/HansOffice/cyufriends-reload/issues)

---

## Downloads

Precompiled jar files are available on [GitHub Releases](https://github.com/HansOffice/cyufriends-reload/releases):

| Platform | Jar File | Compatibility |
| --- | --- | --- |
| ![Paper](https://img.shields.io/badge/Paper-0284c7?style=flat-square&logo=paper&logoColor=white) | `cyufriends-reload-paper-1.1.8.jar` | Paper and Purpur 1.21+ |
| ![Folia](https://img.shields.io/badge/Folia-7c3aed?style=flat-square) | `cyufriends-reload-folia-1.1.8.jar` | Folia 1.21+ |
| ![Spigot](https://img.shields.io/badge/Spigot-ed8b00?style=flat-square&logo=spigotmc&logoColor=white) | `cyufriends-reload-legacy-1.1.8.jar` | Spigot and Bukkit 1.16.5 ~ 1.20.x |

---

## Features

### Relationships & Teleportation
- **Friend Workflow**: Handle requests, mutual confirmations, custom group tags, and blacklist rules
- **Friend Teleport**: Send direct teleport requests with anti-misclick cooldown confirmations
- **Profile Cards**: Customize signatures, broadcast birthdays, and view login interaction digests

### Direct Chat & Offline Mailbox
- **Direct Messaging**: Chat through dedicated subcommands with one-click quick reply to recent contacts
- **Offline Mailbox**: Automatically store messages when friends are offline and notify upon login
- **Privacy Controls**: Customize privacy toggles to block stranger requests or teleport prompts

### Activity Feed & Profile Wall
- **Server Feed**: Publish player updates in-game and interact with likes across the server
- **Profile Wall**: Personal profile space where friends can visit and leave comments
- **Menu Customization**: Data-driven chest GUI layouts with asynchronous head loading and custom items

---

## Commands & Permissions

| Command | Description | Permission | Default |
| --- | --- | --- | --- |
| `/friend` | Open friends system main menu | `cyufriends.use` | Everyone |
| `/friend help [category] [page]` | View available commands | `cyufriends.use` | Everyone |
| `/friend add <player>` | Send friend request | `cyufriends.use` | Everyone |
| `/friend requests` | View pending friend requests | `cyufriends.use` | Everyone |
| `/friend group` | Open friend group management menu | `cyufriends.use` | Everyone |
| `/friend tp <player>` | Request teleportation to friend | `cyufriends.use` | Everyone |
| `/friend msg <player> <text>` | Send direct message or save to offline mailbox | `cyufriends.command.msg` | Everyone |
| `/friend reply <text>` | Quick reply to last contact | `cyufriends.command.reply` | Everyone |
| `/friend messages` | Open unread offline mailbox | `cyufriends.command.messages` | Everyone |
| `/status` | Open activity feed | `cyufriends.command.status` | Everyone |
| `/wall` | Open personal profile wall | `cyufriends.command.wall` | Everyone |
| `/settings` | Open privacy and preference panel | `cyufriends.command.settings` | Everyone |
| `/bio <text>` | Update personal profile signature | `cyufriends.command.bio` | Everyone |
| `/birthday <yyyy-MM-dd>` | Bind personal birthday date | `cyufriends.command.birthday` | Everyone |
| `/friend reload` | Reload configuration, languages, and menus | `cyufriends.admin` | OP |
| `/friend admin inspect <player>` | Inspect player social records | `cyufriends.admin` | OP |
| `/friend admin rebuild <player>` | Rebuild player data cache | `cyufriends.admin` | OP |
| `/friend admin health` | Check database and module status | `cyufriends.admin` | OP |
| `/friend admin proxy` | Check network communication and clock status | `cyufriends.admin` | OP |

---

## Placeholders (PlaceholderAPI)

| Placeholder | Description |
| --- | --- |
| `%cyufriends_total_count%` | Total friends count of player |
| `%cyufriends_request_count%` | Pending friend requests count |
| `%cyufriends_offline_messages_count%` | Unread offline messages count |
| `%cyufriends_daily_requests_remaining%` | Daily friend requests remaining |
| `%cyufriends_note_<player>%` | Custom note name set for target friend |
| `%cyufriends_group_<player>%` | Custom group name of target friend |
| `%cyufriends_is_friend_<player>%` | Friendship check with target player |
| `%cyufriends_birthday_<player>%` | Birthday text of target player |
| `%rel_cyufriends_is_friend%` | Relational check returning localized state |
| `%rel_cyufriends_is_friend_bool%` | Relational check returning boolean true or false |

---

## Configuration

- `config.yml`: Language selection, module toggles, database connection, and quotas
- `lang/`: Localization files with built-in `zh_cn.yml` and `en_us.yml`
- `sounds.yml`: Audio effects for interactions and system notifications
- `gui/`: Chest menu layout configurations
- `data/`: Local SQLite database storage directory

---

## Building from Source

```bash
# Build Paper jar
mvn clean package -Ppaper -DskipTests

# Build Folia jar
mvn package -Pfolia -DskipTests

# Build 1.16.5~1.20.x jar
mvn package -Plegacy -DskipTests
```

---

## License

This project is licensed under the MIT License, see [`LICENSE`](LICENSE)
