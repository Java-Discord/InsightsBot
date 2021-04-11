package net.javadiscord.data.jobs;

import lombok.extern.slf4j.Slf4j;
import net.javadiscord.InsightsBot;
import net.javadiscord.data.GuildDataWriter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.SQLException;

/**
 * Job to flush a single guild's cache, where the guild id is provided by the
 * job data map.
 */
@Slf4j
public class GuildCacheFlushJob implements Job {
	@Override
	public void execute(JobExecutionContext context) {
		long guildId = context.getJobDetail().getJobDataMap().getLong("guildId");
		log.info("Flushing cache for guild \"{}\".", guildId);
		if (!InsightsBot.get().getGuildsCache().exists(guildId)) {
			log.warn("No cached data for guild, exiting.");
			return;
		}
		try (GuildDataWriter writer = new GuildDataWriter()) {
			writer.saveGuildData(guildId, InsightsBot.get().getGuildsCache().get(guildId));
		} catch (SQLException e) {
			log.error("Could not flush guild's cache.", e);
		}
	}
}
