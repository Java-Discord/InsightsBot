package net.javadiscord.data.model.stats.time_interval_metrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Records the number of users that have joined a guild in a time interval.
 */
@Entity
@Table(name = "guild_time_interval_metrics_memberships")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class MembershipsMetric extends GuildTimeIntervalMetric {
	private Long memberJoinCount;
	private Long memberLeaveCount;
	private Long memberBanCount;
	private Long memberUnbanCount;
	private Long totalMemberCount;

	public MembershipsMetric(Long guildId, Instant startTimestamp, Instant endTimestamp) {
		super(guildId, startTimestamp, endTimestamp);
	}
}
