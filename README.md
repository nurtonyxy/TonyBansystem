# TonyBansystem 🛡️

A powerful and user-friendly ban management system designed to keep your server safe. Developed by **nurtonyxy**.

## ✨ Features

* **Advanced Ban Management:** Issue permanent or temporary bans with ease.
* **Discord Integration:** Real-time notifications for bans and kicks via Discord Webhooks.
* **Player History:** Track previous offenses and warnings to identify repeat offenders.
* **Intuitive Commands:** Clean command structure for staff members.
* **Database Support:** Optimized MySQL/MariaDB integration for high performance and data integrity.

## 🚀 Installation

1.  **Download:** Clone the repository or download the latest release as a `.zip`.
    ```bash
    git clone [https://github.com/nurtonyxy/TonyBansystem.git](https://github.com/nurtonyxy/TonyBansystem.git)
    ```
2.  **Database:** Import the provided `.sql` file into your database.
3.  **Configuration:** Open the `config.lua` (or `.json`/`.yml`) file and enter your database credentials and Discord Webhook URL.
4.  **Start:** Add the resource to your server configuration file:
    ```cfg
    ensure TonyBansystem
    ```

## 🛠️ Commands

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/ban [ID] [Time] [Reason]` | Bans a player temporarily or permanently. | `admin.ban` |
| `/unban [ID/Identifier]` | Removes a ban from a specific player. | `admin.unban` |
| `/checkban [ID]` | Checks the current ban status of a player. | `admin.check` |

## ⚙️ Configuration Example

Here is a snippet of how the `config.lua` usually looks:
```lua
Config = {}
Config.Language = 'en' -- Options: 'de', 'en'
Config.Webhook = 'YOUR_DISCORD_WEBHOOK_URL'
Config.AdminGroup = 'admin'
