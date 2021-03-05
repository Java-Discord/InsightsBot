package net.javadiscord.data.model.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.javadiscord.data.model.GuildEvent;

import javax.persistence.*;

@Entity
@Table(name = "message_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MessageEvent extends GuildEvent {
	public enum EventType {CREATE, UPDATE, DELETE}

	@Column(nullable = false)
	private Long channelId;

	@Column(nullable = false)
	private Long messageId;

	@Column
	private Integer messageLength;

	@Enumerated(EnumType.STRING)
	private EventType eventType;

	public MessageEvent(Long guildId, Long userId, Long channelId, Long messageId, Integer messageLength, EventType eventType) {
		super(guildId, userId);
		this.channelId = channelId;
		this.messageId = messageId;
		this.messageLength = messageLength;
		this.eventType = eventType;
	}

	public MessageEvent(MessageCreateEvent createEvent) {
		this(
			createEvent.getGuildId().orElseThrow().asLong(),
			createEvent.getMember().orElseThrow().getId().asLong(),
			createEvent.getMessage().getChannelId().asLong(),
			createEvent.getMessage().getId().asLong(),
			createEvent.getMessage().getContent().length(),
			EventType.CREATE
		);
	}

	public MessageEvent(MessageUpdateEvent updateEvent) {
		this(
			updateEvent.getGuildId().orElseThrow().asLong(),
			null,
			updateEvent.getChannelId().asLong(),
			updateEvent.getMessageId().asLong(),
			null,
			EventType.UPDATE
		);
	}

	public MessageEvent(MessageDeleteEvent deleteEvent) {
		this(
			deleteEvent.getGuildId().orElseThrow().asLong(),
			null,
			deleteEvent.getChannelId().asLong(),
			deleteEvent.getMessageId().asLong(),
			null,
			EventType.DELETE
		);
	}
}
