package net.javadiscord.data.model.events;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.javadiscord.data.model.GuildEvent;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Membership events are recorded when new members join a guild, or when members
 * leave a guild.
 */
@Entity
@Table(name = "membership_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MembershipEvent extends GuildEvent {
	public enum EventType {JOIN, LEAVE}

	private EventType eventType;

	public MembershipEvent(Long guildId, Long userId, EventType eventType) {
		super(guildId, userId);
		this.eventType = eventType;
	}

	public MembershipEvent(MemberJoinEvent joinEvent) {
		this(
			joinEvent.getGuildId().asLong(),
			joinEvent.getMember().getId().asLong(),
			EventType.JOIN
		);
	}

	public MembershipEvent(MemberLeaveEvent leaveEvent) {
		this(
			leaveEvent.getGuildId().asLong(),
			leaveEvent.getUser().getId().asLong(),
			EventType.LEAVE
		);
	}
}
