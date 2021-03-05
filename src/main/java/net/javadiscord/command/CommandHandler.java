package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Handles the process of preparing the contents of a command message for use
 * with {@link Command}s, by splitting the content by whitespace and extracting
 * arguments.
 */
@Component
@RequiredArgsConstructor
public class CommandHandler {
	private final CommandRegistry commandRegistry;
	private final UnknownCommand unknownCommand = new UnknownCommand();

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
		String[] args = new String[words.length - 1];
		System.arraycopy(words, 1, args, 0, words.length - 1);
		return this.handle(event, args);
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
			String[] args = new String[words.length - 1];
			System.arraycopy(words, 1, args, 0, words.length - 1);
			return this.commandRegistry.get(keyword).orElse(unknownCommand).handle(event, args);
		}
		return Mono.empty();
	}
}
