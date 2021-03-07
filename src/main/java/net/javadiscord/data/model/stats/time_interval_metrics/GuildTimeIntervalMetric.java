package net.javadiscord.data.model.stats.time_interval_metrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.javadiscord.data.model.stats.Metric;

import javax.persistence.*;
import java.time.Instant;

/**
 * Represents some sort of aggregate metric that's been computed for a certain
 * guild.
 */
@Entity
@Table(name = "guild_time_interval_metrics")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class GuildTimeIntervalMetric extends Metric {
	/**
	 * The id of the guild that this metric belongs to.
	 */
	@Column
	private Long guildId;

	/**
	 * The timestamp (inclusive) at which this metric's time interval begins.
	 */
	@Column(nullable = false)
	private Instant startTimestamp;

	/**
	 * The timestamp (exclusive) at which this metric's time interval ends.
	 */
	@Column(nullable = false)
	private Instant endTimestamp;

	public GuildTimeIntervalMetric(Long guildId, Instant startTimestamp, Instant endTimestamp) {
		this.guildId = guildId;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}
}
