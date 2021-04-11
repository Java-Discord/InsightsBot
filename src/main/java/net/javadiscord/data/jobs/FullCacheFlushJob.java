package net.javadiscord.data.jobs;

import lombok.extern.slf4j.Slf4j;
import net.javadiscord.InsightsBot;
import net.javadiscord.data.GuildDataWriter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.SQLException;

/**
 * A job which flushes the full cache of statistics to the database, for every
 * guild that's currently tracked. Upon successful data flushing, the cache will
 * be cleared.
 */
@Slf4j
public class FullCacheFlushJob implements Job {
	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		log.info("Flushing full cache to database...");
		if (InsightsBot.get().getGuildsCache().isEmpty()) {
			log.info("No cached data, skipping.");
			return;
		}
		try (GuildDataWriter writer = new GuildDataWriter()) {
			var guildDataList = InsightsBot.get().getGuildsCache().getAll();
			log.info("Saving data for {} guilds.", guildDataList.size());
			writer.saveAllGuildData(guildDataList);
			InsightsBot.get().getGuildsCache().clear();
			log.info("Cleared cache.");
		} catch (SQLException e) {
			log.error("Could not obtain connection to the data source.", e);
		}
	}
}
