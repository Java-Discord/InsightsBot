package net.javadiscord.data.model.stats.time_interval_metrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Metric that stores how many messages were created in a guild in a timeframe.
 */
@Entity
@Table(name = "guild_time_interval_metrics_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class MessagesMetric extends GuildTimeIntervalMetric {
	private Long messagesCreated;
	private Long messagesDeleted;
	private Long messagesUpdated;
	private Long messagesRetained; // Messages that have been created and not yet deleted.
	private Long reactionsAdded;
	private Long reactionsRemoved;
	private Long activeUsers; // Number of users that created, updated, or deleted a message, or reaction.

	public MessagesMetric(Long guildId, Instant startTimestamp, Instant endTimestamp) {
		super(guildId, startTimestamp, endTimestamp);
	}
}
