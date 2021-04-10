package net.javadiscord.model;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.javadiscord.InsightsBot;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

@Slf4j
public class CacheSaveJob implements Job {
	private static final String RECORDS_DIR = "insights_bot_records";

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info("Saving cache to persistence system.");
		Set<Map.Entry<Long, GuildData>> guildDataList = InsightsBot.get().getGuildsCache().getAll();
		log.info("Saving data for {} guilds.", guildDataList.size());
		for (var entry : guildDataList) {
			Guild guild = InsightsBot.get().getJda().getGuildById(entry.getKey());
			if (guild == null) {
				log.error("Could not get guild from id {}.", entry.getKey());
				continue;
			}
			log.info("Saving guild data for guild \"{}\".", guild.getName());
			String sanitizedName = guild.getName().trim().toLowerCase().replaceAll("\\s+", "_");
			File guildDir = new File(RECORDS_DIR + File.separator + sanitizedName);
			if (!guildDir.exists() && !guildDir.mkdirs()) {
				log.error("Could not make directory for guild: {}", guildDir.getPath());
				continue;
			}
			String fileName = ZonedDateTime.now().getYear() + "_insights.csv";

			try {
				File file = new File(guildDir.getPath() + File.separator + fileName);
				CSVWriter writer = new CSVWriter(new FileWriter(file, true));
				StatefulBeanToCsv<GuildData> beanToCsv = new StatefulBeanToCsvBuilder<GuildData>(writer).build();
				beanToCsv.write(entry.getValue());
				writer.close();
				log.info("Wrote data to {}.", file.getAbsolutePath());
			} catch (IOException e) {
				log.error("Could not write to CSV: {}", e.getMessage());
			} catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
				e.printStackTrace();
			}
		}
		InsightsBot.get().getGuildsCache().clear();
		log.info("Cleared cache.");
	}
}
