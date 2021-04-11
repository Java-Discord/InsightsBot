package net.javadiscord;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.javadiscord.commands.CommandHandler;
import net.javadiscord.commands.info.HelpCommand;
import net.javadiscord.commands.info.StatusCommand;
import net.javadiscord.commands.stats.ViewCurrentCacheCommand;
import net.javadiscord.commands.util.FlushCacheCommand;
import net.javadiscord.commands.util.ShutdownCommand;
import net.javadiscord.data.DataSourceProvider;
import net.javadiscord.data.JobManager;
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
	public static final Color EMBED_COLOR = new Color(47, 49, 54);
	private static InsightsBot instance;

	private final ZonedDateTime startedAt;
	@Getter
	private final long selfId;
	@Getter
	private final GuildsCache guildsCache;
	@Getter
	private final CommandHandler commandHandler;
	@Getter
	private final JobManager jobManager;
	@Getter
	private final DataSource dataSource;
	@Getter
	private final JDA jda;

	private InsightsBot(String token) throws Exception {
		this.jda = JDABuilder.createLight(token)
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

	public Duration getUptime() {
		return Duration.between(this.startedAt, ZonedDateTime.now());
	}

	private void initializeCommands() {
		this.commandHandler.getCommandRegistry()
				.register("cache", new ViewCurrentCacheCommand())
				.register("flush", new FlushCacheCommand())
				.register("status", new StatusCommand())
				.register("help", new HelpCommand())
				.register("shutdown", new ShutdownCommand());
	}

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
