package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

/**
 * Handles the process of preparing the contents of a command message for use
 * with {@link Command}s, by splitting the content by whitespace and extracting
 * arguments.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
	private final CommandRegistry commandRegistry;

	/**
	 * Handles a root-level command from which we must extract a keyword and
	 * arguments, and discard the prefix.
	 * @param event The event to handle.
	 * @return The result of whatever command handles the event.
	 */
	public Publisher<?> handle(MessageCreateEvent event) {
		String[] words = event.getMessage().getContent().split("\\s+");
		if (words.length == 0) {
			return Mono.empty();
		}
		return this.handle(event, Arrays.copyOfRange(words, 1, words.length));
	}

	/**
	 * Handles any event, given a list of whitespace-separated words where the
	 * first word is assumed to be a command keyword.
	 * @param event The event to handle.
	 * @param words The list of words containing the keyword and arguments.
	 * @return The result of whatever command handles the event.
	 */
	public Publisher<?> handle(MessageCreateEvent event, String[] words) {
		if (words.length > 0) {
			String keyword = words[0].trim().toLowerCase();
			Command cmd = this.commandRegistry.get(keyword);
			if (cmd.getWhitelistedUserIds() != null && !cmd.getWhitelistedUserIds().isEmpty()) {
				Optional<User> optionalUser = event.getMessage().getAuthor();
				if (!optionalUser.isPresent() || !cmd.getWhitelistedUserIds().contains(optionalUser.get().getId().asLong())) {
					return event.getMessage().getChannel().flatMap(c -> c.createMessage("Command not permitted."));
				}
			}
			return cmd.handle(event, Arrays.copyOfRange(words, 1, words.length));
		}
		return Mono.empty();
	}
}
