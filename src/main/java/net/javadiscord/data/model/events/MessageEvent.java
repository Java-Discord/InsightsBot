package net.javadiscord.data.model.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "message_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MessageEvent extends GuildEvent {
	private static final int PREFIX_LENGTH = 6;

	public enum EventType {CREATE, UPDATE, DELETE}

	@Column(nullable = false)
	private Long channelId;

	@Column(nullable = false)
	private Long messageId;

	@Column
	private Integer messageLength;

	@Column(length = 64)
	private String prefix;

	@Enumerated(EnumType.STRING)
	private EventType eventType;

	public MessageEvent(Long guildId, Long userId, Long channelId, Long messageId, Integer messageLength, String prefix, EventType eventType) {
		super(guildId, userId);
		this.channelId = channelId;
		this.messageId = messageId;
		this.messageLength = messageLength;
		this.prefix = prefix;
		this.eventType = eventType;
	}

	public MessageEvent(MessageCreateEvent createEvent) {
		this(
			createEvent.getGuildId().orElseThrow(() -> new RuntimeException("Missing guild id.")).asLong(),
			createEvent.getMember().orElseThrow(() -> new RuntimeException("Missing member.")).getId().asLong(),
			createEvent.getMessage().getChannelId().asLong(),
			createEvent.getMessage().getId().asLong(),
			createEvent.getMessage().getContent().length(),
			createEvent.getMessage().getContent().substring(0, Math.min(createEvent.getMessage().getContent().length(), PREFIX_LENGTH)),
			EventType.CREATE
		);
	}

	public MessageEvent(MessageUpdateEvent updateEvent) {
		this(
			updateEvent.getGuildId().orElseThrow(() -> new RuntimeException("Missing guild id.")).asLong(),
			null,
			updateEvent.getChannelId().asLong(),
			updateEvent.getMessageId().asLong(),
			null,
			null,
			EventType.UPDATE
		);
	}

	public MessageEvent(MessageDeleteEvent deleteEvent) {
		this(
			deleteEvent.getGuildId().orElseThrow(() -> new RuntimeException("Missing guild id.")).asLong(),
			null,
			deleteEvent.getChannelId().asLong(),
			deleteEvent.getMessageId().asLong(),
			null,
			null,
			EventType.DELETE
		);
	}
}
