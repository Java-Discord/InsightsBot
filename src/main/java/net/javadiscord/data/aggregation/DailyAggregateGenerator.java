package net.javadiscord.data.aggregation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.data.dao.MessagesMetricRepository;
import net.javadiscord.data.model.stats.Metric;
import net.javadiscord.util.SqlHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyAggregateGenerator {
	private final JdbcTemplate jdbcTemplate;
	private final TimeIntervalAggregationService intervalAggregationService;
	private final MessagesMetricRepository messagesMetricRepository;

	/**
	 * Scheduled task that generates aggregate data for the previous day.
	 */
	@Scheduled(cron = "${insights-bot.tasks.daily-metric-generator}")
	@Transactional
	public void genAllDailyMetrics() {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		LocalDate yesterday = now.toLocalDate().minusDays(1);
		log.info("Generating daily aggregate data for {}.", yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE));
		Instant start = yesterday.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant end = yesterday.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
		log.info("Start: {}", start.toString());
		log.info("End: {}", end.toString());
		long startMillis = System.currentTimeMillis();
		List<Long> guildIds = this.jdbcTemplate.query(
				Objects.requireNonNull(SqlHelper.load("sql/find_guild_ids.sql")),
				(resultSet, i) -> resultSet.getLong(1),
				Timestamp.from(start), Timestamp.from(end)
		);
		for (Long guildId : guildIds) {
			this.generateDailyAggregate(guildId, start, end, false);
		}
		double runtimeSeconds = (System.currentTimeMillis() - startMillis) / 1000.0;
		log.info(
				"Daily aggregate data generation for {} completed in {} seconds.",
				yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE),
				String.format("%.3f", runtimeSeconds)
		);
	}

	@Transactional
	public Set<Metric> generateDailyAggregate(long guildId, Instant start, Instant end, boolean override) {
		Set<Metric> metrics = new HashSet<>();
		log.info("Generating data for guild {}:", guildId);
		if (this.messagesMetricRepository.existsByGuildAndInterval(guildId, start, end)) {
			if (!override) {
				log.info("\tDeleting preexisting messages metric for this day.");
				this.messagesMetricRepository.deleteByGuildAndInterval(guildId, start, end);
			}
			log.info("\tGuild already has messages metric for this day, skipping.");
		} else {
			metrics.add(this.intervalAggregationService.generateMessagesMetric(guildId, start, end));
			log.info("\tGenerated messages metric.");
		}
		return metrics;
	}
}
