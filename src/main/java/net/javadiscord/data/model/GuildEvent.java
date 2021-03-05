package net.javadiscord.data.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

/**
 * A guild event is a single occurrence of some simple user action, such as
 * sending a message, reacting to a message, joining a voice chat, etc.
 */
@Entity
@Table(name = "guild_events")
@Inheritance(strategy = InheritanceType.JOINED)
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
	 * The time at which this event happened.
	 */
	@CreationTimestamp
	@Column(updatable = false)
	private Instant timestamp;

	public GuildEvent(Long guildId, Long userId) {
		this.guildId = guildId;
		this.userId = userId;
		this.timestamp = Instant.now();
	}
}
