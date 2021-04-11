# InsightsBot
Discord bot for recording and analyzing user engagement, retention, and more.

## Functionality

This bot, when present in your Guild, will silently collect anonymous statistics about messages sent, reactions used, members joined, and more. Every night, this data is flushed to a persistent database where it becomes available for use with some analytics commands the bot provides.

### Commands

* `help` - Shows a help message with information about commands.
* `status` - Displays the bot's status, including uptime, memory usage, and data source connectivity.
* `cache [flush]` - Shows the data that's currently cached for your guild. If the optional `flush` argument is given, the data in the cache is forcibly flushed to the database, overriding any previous data that is already there for the current day.
* `graph <list|{graph_name}> [start] [end]` - Renders a graph with some statistics. Use the `list` argument to show a list of supported graphs, or actually generate one of them by using its name as the first argument. An optional `start` and `end` can be provided, which specify the number of days to go back from today to reach the start and end of a desired range.
* `shutdown` - Admin-only command that stops the bot.

## Setup

### OAuth2 URL

Use this URL to add the bot to your server:

https://discord.com/api/oauth2/authorize?client_id=817842720848347137&permissions=650432&redirect_uri=https%3A%2F%2Fjavadiscord.net%2F&scope=bot

### Configuration

For security reasons, the following properties must be declared as environment variables available to this program at runtime:

| Environment Variable   | Description                                                  |
| ---------------------- | ------------------------------------------------------------ |
| `INSIGHTS_BOT_DB_URL`  | The JDBC URL used to access the data source.                 |
| `INSIGHTS_BOT_DB_USER` | The user with which the bot will access the data source.     |
| `INSIGHTS_BOT_DB_PASS` | The password for the above user.                             |
| `INSIGHTS_BOT_TOKEN`   | The Discord Bot token to use for the bot.                    |
| `INSIGHTS_BOT_ADMINS`  | (Optional) Comma-separated list of user ids for admin users. |