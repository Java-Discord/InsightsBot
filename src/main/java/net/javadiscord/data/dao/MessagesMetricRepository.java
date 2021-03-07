package net.javadiscord.data.dao;

import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface MessagesMetricRepository extends BaseEntityRepository<MessagesMetric> {
	boolean existsByGuildIdEqualsAndStartTimestampEqualsAndEndTimestampEquals(
			long guildId,
			Instant start,
			Instant end
	);
}
