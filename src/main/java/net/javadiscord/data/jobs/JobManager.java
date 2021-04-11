package net.javadiscord.data.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Manages the initialization and impromptu running of various jobs through a
 * Quartz scheduler.
 */
@Slf4j
public class JobManager {
	private final Scheduler scheduler;

	public JobManager() throws SchedulerException {
		SchedulerFactory sf = new StdSchedulerFactory();
		this.scheduler = sf.getScheduler();
	}

	public void initializeScheduledJobs() throws SchedulerException, ParseException {
		JobDetail job = JobBuilder.newJob(FullCacheFlushJob.class)
				.withIdentity("full_cache_flush", "cache")
				.withDescription("Saves the current guild data caches to the database.")
				.build();
		CronExpression expr = new CronExpression("1 0 0 * * ? *");
		Trigger cronTrigger = TriggerBuilder.newTrigger()
				.withIdentity("nightly")
				.withDescription("Triggers every night.")
				.withSchedule(CronScheduleBuilder.cronSchedule(expr))
				.build();
		ZonedDateTime nextFireTime = expr.getNextValidTimeAfter(Date.from(Instant.now())).toInstant().atZone(ZoneId.systemDefault());
		log.info("Initialized nightly full cache flush. Fires next at {}.", nextFireTime);
		scheduler.scheduleJob(job, cronTrigger);
		scheduler.start();
	}

	public void shutdown() {
		try {
			this.scheduler.shutdown(true);
		} catch (SchedulerException e) {
			log.error("Couldn't shutdown the scheduler.", e);
		}
	}

	/**
	 * Triggers an eager flush of cached data for a particular guild.
	 * @param guildId The id of the guild to flush data for.
	 * @throws SchedulerException If the job could not be scheduled.
	 */
	public void triggerGuildCacheFlush(long guildId) throws SchedulerException {
		JobDetail job = JobBuilder.newJob(GuildCacheFlushJob.class)
				.usingJobData("guildId", guildId)
				.withIdentity("guild_cache_flush")
				.storeDurably()
				.build();
		this.scheduler.addJob(job, true);
		this.scheduler.triggerJob(job.getKey());
	}
}
