package net.javadiscord.data.dao;

import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessagesMetricRepository extends GuildTimeIntervalMetricRepository<MessagesMetric> {
	@Query("SELECT m FROM MessagesMetric m " +
			"WHERE m.guildId = :guildId AND m.startTimestamp = :start AND m.endTimestamp = :end " +
			"ORDER BY m.createdAt DESC")
	List<MessagesMetric> findByGuildAndInterval(@Param("guildId") long guildId, @Param("start") Instant start, @Param("end") Instant end);
}
