package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class UnknownCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return event.getMessage().getChannel().flatMap(c -> c.createEmbed(spec -> {
			spec.setTitle("Unknown Command");
			spec.setAuthor("InsightsBot", null, null);
			spec.setDescription("The command you entered does not exist.");
		}))
				.delayElement(Duration.ofSeconds(3))
				.flatMap(message -> Mono.zip(message.delete(), event.getMessage().delete()));
	}
}
