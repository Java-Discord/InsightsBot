package net.javadiscord.data;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

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

	public void initializeScheduledJobs() throws SchedulerException {
		JobDetail job = JobBuilder.newJob(FullCacheFlushJob.class)
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
