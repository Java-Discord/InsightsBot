package net.javadiscord.data.model.events;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.javadiscord.data.model.GuildEvent;

import javax.persistence.*;

/**
 * Specific events that involve the adding or removing of a reaction from a
 * message.
 */
@Entity
@Table(name = "reaction_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReactionEvent extends GuildEvent {
	public enum EmojiType {CUSTOM, UNICODE}
	public enum EventType {ADD, REMOVE}

	@Column(nullable = false)
	private Long channelId;

	@Column(nullable = false)
	private Long messageId;

	@Column(length = 127)
	private String emoji;

	@Enumerated(EnumType.STRING)
	private EmojiType emojiType;

	@Enumerated(EnumType.STRING)
	private EventType eventType;

	public ReactionEvent(
			Long guildId,
			Long userId,
			Long channelId,
			Long messageId,
			String emoji,
			EmojiType emojiType,
			EventType eventType
	) {
		super(guildId, userId);
		this.channelId = channelId;
		this.messageId = messageId;
		this.emoji = emoji;
		this.emojiType = emojiType;
		this.eventType = eventType;
	}

	public ReactionEvent(ReactionAddEvent addEvent) {
		this(
			addEvent.getGuildId().orElseThrow(() -> new RuntimeException("Missing guild id.")).asLong(),
			addEvent.getUserId().asLong(),
			addEvent.getChannelId().asLong(),
			addEvent.getMessageId().asLong(),
			determineEmojiText(addEvent.getEmoji()),
			determineEmojiType(addEvent.getEmoji()),
			EventType.ADD
		);
	}

	public ReactionEvent(ReactionRemoveEvent removeEvent) {
		this(
			removeEvent.getGuildId().orElseThrow(() -> new RuntimeException("Missing guild id.")).asLong(),
			removeEvent.getUserId().asLong(),
			removeEvent.getChannelId().asLong(),
			removeEvent.getMessageId().asLong(),
			determineEmojiText(removeEvent.getEmoji()),
			determineEmojiType(removeEvent.getEmoji()),
			EventType.REMOVE
		);
	}

	private static String determineEmojiText(ReactionEmoji emoji) {
		if (emoji.asCustomEmoji().isPresent()) {
			return emoji.asCustomEmoji().get().getName();
		} else if (emoji.asUnicodeEmoji().isPresent()) {
			return emoji.asUnicodeEmoji().get().getRaw();
		}
		return null;
	}

	private static EmojiType determineEmojiType(ReactionEmoji emoji) {
		if (emoji.asCustomEmoji().isPresent()) {
			return EmojiType.CUSTOM;
		} else if (emoji.asUnicodeEmoji().isPresent()) {
			return EmojiType.UNICODE;
		}
		return null;
	}
}
