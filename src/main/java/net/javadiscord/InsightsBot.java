package net.javadiscord;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.javadiscord.commands.CommandHandler;
import net.javadiscord.commands.info.HelpCommand;
import net.javadiscord.commands.info.StatusCommand;
import net.javadiscord.commands.stats.ViewCurrentCacheCommand;
import net.javadiscord.listeners.InsightsEventListener;
import net.javadiscord.model.CacheSaveJob;
import net.javadiscord.model.GuildsCache;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.security.auth.login.LoginException;
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
	private final JDA jda;

	private InsightsBot(String token) throws InterruptedException, LoginException {
		this.jda = JDABuilder.createLight(token)
				.addEventListeners(new InsightsEventListener(this))
				.build();
		this.jda.awaitReady();
		this.startedAt = ZonedDateTime.now();
		this.selfId = this.jda.getSelfUser().getIdLong();
		this.guildsCache = new GuildsCache();
		this.commandHandler = new CommandHandler();
		this.initializeCommands();
	}

	public Duration getUptime() {
		return Duration.between(this.startedAt, ZonedDateTime.now());
	}

	private void initializeCommands() {
		this.commandHandler.getCommandRegistry()
				.register("cache", new ViewCurrentCacheCommand())
				.register("status", new StatusCommand())
				.register("help", new HelpCommand());
	}

	public static void main(String[] args) throws SchedulerException {
		try {
			instance = new InsightsBot(System.getenv("INSIGHTS_BOT_TOKEN"));
		} catch (LoginException | InterruptedException e) {
			log.error("Could not login and start bot: {}", e.getMessage());
			System.exit(1);
		}
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler scheduler = sf.getScheduler();
		JobDetail job = JobBuilder.newJob(CacheSaveJob.class)
				.withIdentity("cache_save", "cache")
				.withDescription("Saves the current guild data caches to the database.")
				.build();
		Trigger trigger = TriggerBuilder.newTrigger()
				.forJob(job)
				.withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * * * ?"))
				.build();
		scheduler.scheduleJob(job, trigger);
		scheduler.start();
	}

	/**
	 * @return The singleton instance of the bot.
	 */
	public static InsightsBot get() {
		return instance;
	}
}
