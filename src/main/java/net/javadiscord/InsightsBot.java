package net.javadiscord;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.javadiscord.commands.CommandHandler;
import net.javadiscord.commands.info.HelpCommand;
import net.javadiscord.commands.info.StatusCommand;
import net.javadiscord.commands.stats.CacheCommand;
import net.javadiscord.commands.stats.GraphCommand;
import net.javadiscord.commands.util.ShutdownCommand;
import net.javadiscord.data.DataSourceProvider;
import net.javadiscord.data.jobs.JobManager;
import net.javadiscord.listeners.InsightsEventListener;
import net.javadiscord.model.GuildsCache;

import javax.sql.DataSource;
import java.awt.*;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Main application entry point, and the object representing the bot instance
 * that exists during the application's lifetime.
 */
@Slf4j
public class InsightsBot {
	/**
	 * The color that is used for embed messages created by this bot.
	 */
	public static final Color EMBED_COLOR = new Color(47, 49, 54);

	/**
	 * The singleton instance of the bot.
	 */
	private static InsightsBot instance;

	/**
	 * The date and time at which this bot started.
	 */
	private final ZonedDateTime startedAt;

	/**
	 * This bot's own id.
	 */
	@Getter
	private final long selfId;

	/**
	 * The bot's cache of all guild data.
	 */
	@Getter
	private final GuildsCache guildsCache;

	/**
	 * The bot's command handler, which is responsible for holding registered
	 * commands and dispatching responses.
	 */
	@Getter
	private final CommandHandler commandHandler;

	/**
	 * The manager for all scheduled and impromptu Quartz-scheduled jobs.
	 */
	@Getter
	private final JobManager jobManager;

	/**
	 * The data source that this bot uses for persistence of cache data.
	 */
	@Getter
	private final DataSource dataSource;

	/**
	 * The JDA bot instance.
	 */
	@Getter
	private final JDA jda;

	/**
	 * Constructs the bot using the given access token.
	 * @param token The token to use to connect to Discord.
	 * @throws Exception If any startup method fails.
	 */
	private InsightsBot(String token) throws Exception {
		this.jda = JDABuilder.createLight(token)
				.enableIntents(
						GatewayIntent.GUILD_MEMBERS,
						GatewayIntent.GUILD_MESSAGES,
						GatewayIntent.GUILD_BANS,
						GatewayIntent.GUILD_MESSAGE_REACTIONS
				)
				.addEventListeners(new InsightsEventListener(this))
				.build();
		this.jda.awaitReady();
		this.startedAt = ZonedDateTime.now();
		this.selfId = this.jda.getSelfUser().getIdLong();
		this.guildsCache = new GuildsCache();
		this.commandHandler = new CommandHandler();
		this.initializeCommands();
		this.dataSource = new DataSourceProvider().getDataSource();
		this.jobManager = new JobManager();
		this.jobManager.initializeScheduledJobs();
	}

	/**
	 * @return The amount of time that this bot has been alive since it started.
	 */
	public Duration getUptime() {
		return Duration.between(this.startedAt, ZonedDateTime.now());
	}

	/**
	 * Initializes the bot's command handler with a registry of commands.
	 */
	private void initializeCommands() {
		this.commandHandler.getCommandRegistry()
				.register("cache", new CacheCommand())
				.register("status", new StatusCommand())
				.register("help", new HelpCommand())
				.register("shutdown", new ShutdownCommand())
				.register("graph", new GraphCommand());
	}

	/**
	 * The main method where the bot is started from.
	 * @param args Command line arguments. No arguments are used; all parameters
	 *             are passed via environment variables instead.
	 */
	public static void main(String[] args) {
		try {
			instance = new InsightsBot(System.getenv("INSIGHTS_BOT_TOKEN"));
		} catch (Exception e) {
			log.error("Could not login and start bot: {}", e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * @return The singleton instance of the bot.
	 */
	public static InsightsBot get() {
		return instance;
	}
}
