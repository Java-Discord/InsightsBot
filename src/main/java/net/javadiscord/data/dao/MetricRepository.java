package net.javadiscord.data.dao;

import net.javadiscord.data.model.stats.Metric;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricRepository extends BaseEntityRepository<Metric> {
}
