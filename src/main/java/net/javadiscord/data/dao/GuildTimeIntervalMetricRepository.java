package net.javadiscord.data.dao;

import net.javadiscord.data.model.stats.time_interval_metrics.GuildTimeIntervalMetric;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface GuildTimeIntervalMetricRepository<T extends GuildTimeIntervalMetric> extends BaseEntityRepository<T> {
	@Query("SELECT m FROM GuildTimeIntervalMetric m WHERE m.guildId = :guildId AND m.startTimestamp = :start AND m.endTimestamp = :end")
	Optional<T> findByGuildAndInterval(@Param("guildId") long guildId, @Param("start") Instant start, @Param("end") Instant end);

	@Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
			"FROM GuildTimeIntervalMetric m WHERE m.guildId = :guildId AND m.startTimestamp = :start AND m.endTimestamp = :end")
	boolean existsByGuildAndInterval(@Param("guildId") long guildId, @Param("start") Instant start, @Param("end") Instant end);

	@Modifying
	@Query("DELETE FROM GuildTimeIntervalMetric m WHERE m.guildId = :guildId AND m.startTimestamp = :start AND m.endTimestamp = :end")
	void deleteByGuildAndInterval(@Param("guildId") long guildId, @Param("start") Instant start, @Param("end") Instant end);
}
