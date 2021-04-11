package net.javadiscord.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All the data that's updated in real-time for a single guild in which the bot
 * operates.
 */
@Getter
public class GuildData {
	private final LocalDate date;

	private int messagesCreated;
	private int messagesUpdated;
	private int messagesRemoved;

	private int reactionsAdded;
	private int reactionsRemoved;

	private int membersJoined;
	private int membersLeft;
	private int membersBanned;
	private int membersUnbanned;
	private int memberCount;

	private final Map<Long, Integer> userMessageCounts = new ConcurrentHashMap<>();
	private final Map<String, Integer> emojiCounts = new ConcurrentHashMap<>();

	public GuildData() {
		this.date = LocalDate.now();
	}

	public void incrementMessagesCreated() {
		this.messagesCreated++;
	}

	public void incrementMessagesUpdated() {
		this.messagesUpdated++;
	}

	public void incrementMessagesRemoved() {
		this.messagesRemoved++;
	}

	public void incrementReactionsAdded() {
		this.reactionsAdded++;
	}

	public void incrementReactionsRemoved() {
		this.reactionsRemoved++;
	}

	public void incrementMembersJoined() {
		this.membersJoined++;
	}

	public void incrementMembersLeft() {
		this.membersLeft++;
	}

	public void incrementMembersBanned() {
		this.membersBanned++;
	}

	public void incrementMembersUnbanned() {
		this.membersUnbanned++;
	}

	public void setMemberCount(int count) {
		this.memberCount = count;
	}

	public void incrementUserMessageCount(long userId) {
		if (!this.userMessageCounts.containsKey(userId)) {
			this.userMessageCounts.put(userId, 1);
		} else {
			this.userMessageCounts.put(userId, this.userMessageCounts.get(userId) + 1);
		}
	}

	public void incrementEmojiCount(String emoji) {
		if (!this.emojiCounts.containsKey(emoji)) {
			this.emojiCounts.put(emoji, 1);
		} else {
			this.emojiCounts.put(emoji, this.emojiCounts.get(emoji) + 1);
		}
	}
}
