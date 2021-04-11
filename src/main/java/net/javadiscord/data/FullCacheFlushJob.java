package net.javadiscord.data;

import lombok.extern.slf4j.Slf4j;
import net.javadiscord.InsightsBot;
import net.javadiscord.model.GuildData;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

@Slf4j
public class FullCacheFlushJob implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		log.info("Flushing full cache to database...");
		if (InsightsBot.get().getGuildsCache().isEmpty()) {
			log.info("No cached data, skipping.");
			return;
		}
		try {
			Connection connection = InsightsBot.get().getDataSource().getConnection();
			connection.setAutoCommit(false);
			ZonedDateTime start = InsightsBot.get().getGuildsCache().getLastClearedAt();
			ZonedDateTime end = ZonedDateTime.now();
			Set<Map.Entry<Long, GuildData>> guildDataList = InsightsBot.get().getGuildsCache().getAll();
			log.info("Saving data for {} guilds.", guildDataList.size());
			GuildDataWriter writer = new GuildDataWriter(connection);
			for (var entry : guildDataList) {
				try {
					writer.saveGuildData(entry.getKey(), entry.getValue(), start, end);
					connection.commit();
				} catch (SQLException e) {
					log.error("Could not save guild data.", e);
					connection.rollback();
				}
			}
			connection.close();
			InsightsBot.get().getGuildsCache().clear();
			log.info("Cleared cache.");
		} catch (SQLException e) {
			log.error("Could not obtain connection to the data source.", e);
		}
	}
}
