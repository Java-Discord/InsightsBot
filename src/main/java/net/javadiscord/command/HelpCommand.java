package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class HelpCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return event.getMessage().getChannel().flatMap(c -> c.createEmbed(spec -> {
			spec.setTitle("InsightsBot Help");
			spec.setAuthor("InsightsBot", "https://github.com/Java-Discord/InsightsBot", null);
		}))
				.delayElement(Duration.ofSeconds(3))
				.flatMap(message -> Mono.zip(message.delete(), event.getMessage().delete()));
	}
}
