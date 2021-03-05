package net.javadiscord.data.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * A guild event is a single occurrence of some simple user action, such as
 * sending a message, reacting to a message, joining a voice chat, etc.
 */
@Entity
@Table(name = "guild_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildEvent extends BaseEntity {
	/**
	 * The id (snowflake, as a long) of the guild where the event originated.
	 */
	@Column
	private Long guildId;

	/**
	 * The id (snowflake) of the user that triggered the event.
	 */
	@Column
	private Long userId;

	/**
	 * The id (snowflake) of the channel where this event happened.
	 */
	private Long channelId;

	/**
	 * The time at which this event happened.
	 */
	@CreationTimestamp
	private Instant timestamp;

	public GuildEvent(Long guildId, Long userId, Long channelId) {
		this.guildId = guildId;
		this.userId = userId;
		this.channelId = channelId;
	}
}
