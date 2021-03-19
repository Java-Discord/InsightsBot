package net.javadiscord.data.dao;

import net.javadiscord.data.model.stats.time_interval_metrics.MembershipsMetric;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipsMetricRepository extends GuildTimeIntervalMetricRepository<MembershipsMetric> {
}
