package net.javadiscord.data.model.events;

import discord4j.core.event.domain.guild.BanEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.guild.UnbanEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
	public enum EventType {JOIN, LEAVE, BAN, UNBAN}

	/**
	 * The type of event that this is.
	 */
	@Enumerated(EnumType.STRING)
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

	public MembershipEvent(BanEvent banEvent) {
		this(
			banEvent.getGuildId().asLong(),
			banEvent.getUser().getId().asLong(),
			EventType.BAN
		);
	}

	public MembershipEvent(UnbanEvent unbanEvent) {
		this(
				unbanEvent.getGuildId().asLong(),
				unbanEvent.getUser().getId().asLong(),
				EventType.UNBAN
		);
	}
}
