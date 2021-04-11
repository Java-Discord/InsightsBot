package net.javadiscord.data;

import lombok.extern.slf4j.Slf4j;
import net.javadiscord.InsightsBot;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.SQLException;
import java.time.ZonedDateTime;

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
		try {
			GuildDataWriter writer = new GuildDataWriter(InsightsBot.get().getDataSource().getConnection());
			ZonedDateTime start = InsightsBot.get().getGuildsCache().getLastClearedAt();
			ZonedDateTime end = ZonedDateTime.now();
			writer.saveGuildData(guildId, InsightsBot.get().getGuildsCache().get(guildId), start, end);
		} catch (SQLException e) {
			log.error("Could not flush guild's cache.", e);
		}
	}
}
