package net.javadiscord.data.dao;

import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesMetricRepository extends GuildTimeIntervalMetricRepository<MessagesMetric> {
}
