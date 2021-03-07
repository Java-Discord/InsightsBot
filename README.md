# InsightsBot
Discord bot for recording and analyzing user engagement, retention, and more.

### OAuth2 URL

Use this URL to add the bot to your server:

https://discord.com/api/oauth2/authorize?client_id=817842720848347137&permissions=650432&redirect_uri=https%3A%2F%2Fjavadiscord.net%2F&scope=bot

### Configuration
The application is configured for both a `development` and `production` spring profile.

For security reasons, the following properties must be declared as environment variables available to this program at runtime:

| Environment Variable   | Description                                                  |
| ---------------------- | ------------------------------------------------------------ |
| `INSIGHTS_BOT_DB_URL`  | The JDBC URL used to access the data source, usually of the form `jdbc:<db_type>://<ip>:<port>/<db_name>`. |
| `INSIGHTS_BOT_DB_USER` | The user with which the bot will access the data source.     |
| `INSIGHTS_BOT_DB_PASS` | The password for the above user.                             |
| `INSIGHTS_BOT_TOKEN`   | The Discord Bot token to use for the bot.                    |