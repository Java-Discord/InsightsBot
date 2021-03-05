package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommandHandler {
	private final CommandRegistry commandRegistry;
	private final UnknownCommand unknownCommand = new UnknownCommand();

	public Publisher<?> handle(MessageCreateEvent event) {
		String[] words = event.getMessage().getContent().split("\\s+");
		if (words.length == 0) {
			return Mono.empty();
		}
		String[] args = new String[words.length - 1];
		System.arraycopy(words, 1, args, 0, words.length - 1);
		return this.handle(event, args);
	}

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
