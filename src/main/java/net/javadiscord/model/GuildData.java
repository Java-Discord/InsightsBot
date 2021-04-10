package net.javadiscord.model;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All the data that's updated in real-time for a single guild in which the bot
 * operates.
 */
@Getter
public class GuildData {
	private int messagesCreated;
	private int messagesUpdated;
	private int messagesRemoved;

	private int reactionsAdded;
	private int reactionsRemoved;

	private int membersJoined;
	private int membersLeft;
	private int membersBanned;
	private int membersUnbanned;

	private final Map<Long, Integer> userMessageCounts = new ConcurrentHashMap<>();

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

	public void incrementUserMessageCount(long userId) {
		if (!this.userMessageCounts.containsKey(userId)) {
			this.userMessageCounts.put(userId, 0);
		}
		this.userMessageCounts.put(userId, this.userMessageCounts.get(userId) + 1);
	}
}
