package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * Commands must simply declare the logic for the {@link Command#handle} method,
 * and can be added to a {@link CommandRegistry} for use in the application.
 */
public interface Command {
	/**
	 * Handles an invocation of this command.
	 * @param event The event which caused this command to be called.
	 * @param args The list of arguments provided to the command.
	 * @return An asynchronous publisher that completes when the command is
	 * handled successfully.
	 */
	Publisher<?> handle(MessageCreateEvent event, String[] args);

	/**
	 * @return A list of ids of users that are allowed to use the command.
	 */
	default @Nullable Set<Long> getWhitelistedUserIds() {
		return null;
	}
}
