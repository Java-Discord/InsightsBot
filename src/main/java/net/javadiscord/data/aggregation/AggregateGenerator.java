package net.javadiscord.data.aggregation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.data.dao.MembershipsMetricRepository;
import net.javadiscord.data.dao.MessagesMetricRepository;
import net.javadiscord.data.model.stats.time_interval_metrics.MembershipsMetric;
import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import net.javadiscord.util.SqlHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregateGenerator {
	private final JdbcTemplate jdbcTemplate;
	private final TimeIntervalAggregationService intervalAggregationService;
	private final MessagesMetricRepository messagesMetricRepository;
	private final MembershipsMetricRepository membershipsMetricRepository;

	/**
	 * Scheduled task that generates aggregate data for the previous day.
	 */
	@Scheduled(cron = "${insights-bot.tasks.daily-metric-generator}")
	@Transactional
	public void genAllDailyMetrics() {
		LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
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
			log.info("\tGenerating messages metric for guild {}.", guildId);
			this.generateMessagesMetric(guildId, start, end, true);
			log.info("\tGenerating memberships metric for guild {}.", guildId);
			this.generateMembershipsMetric(guildId, start, end, true);
		}
		double runtimeSeconds = (System.currentTimeMillis() - startMillis) / 1000.0;
		log.info(
				"Daily aggregate data generation for {} completed in {} seconds.",
				yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE),
				String.format("%.3f", runtimeSeconds)
		);
	}

	/**
	 * Generates an aggregate metric about the message stats of a guild between
	 * the given start and end timestamps. If a metric already exists for that
	 * interval, it will only be overwritten if overwrite is set to true.
	 * @param guildId The id of the guild to generate metrics for.
	 * @param start The start of the interval.
	 * @param end The end of the interval.
	 * @param overwrite Whether to overwrite existing metrics for the interval.
	 * @return The metric that was generated.
	 */
	@Transactional
	public MessagesMetric generateMessagesMetric(long guildId, Instant start, Instant end, boolean overwrite) {
		List<MessagesMetric> existingMetrics = this.messagesMetricRepository.findByGuildAndInterval(guildId, start, end);
		if (existingMetrics.size() > 0) {
			if (overwrite) {
				this.messagesMetricRepository.deleteAll(existingMetrics);
			} else {
				MessagesMetric latest = existingMetrics.remove(0);
				this.messagesMetricRepository.deleteAll(existingMetrics);
				return latest;
			}
		}
		return this.intervalAggregationService.generateMessagesMetric(guildId, start, end);
	}

	public MessagesMetric generateMessagesMetric(long guildId, LocalDate date, boolean overwrite) {
		Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant end = date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
		return this.generateMessagesMetric(guildId, start, end, overwrite);
	}

	@Transactional
	public MembershipsMetric generateMembershipsMetric(long guildId, Instant start, Instant end, boolean overwrite) {
		List<MembershipsMetric> existingMetrics = this.membershipsMetricRepository.findByGuildAndInterval(guildId, start, end);
		if (existingMetrics.size() > 0) {
			if (overwrite) {
				this.membershipsMetricRepository.deleteAll(existingMetrics);
			} else {
				MembershipsMetric latest = existingMetrics.remove(0);
				this.membershipsMetricRepository.deleteAll(existingMetrics);
				return latest;
			}
		}
		return this.intervalAggregationService.generateMembershipsMetric(guildId, start, end);
	}

	public MembershipsMetric generateMembershipsMetric(long guildId, LocalDate date, boolean overwrite) {
		Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant end = date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
		return this.generateMembershipsMetric(guildId, start, end, overwrite);
	}
}
