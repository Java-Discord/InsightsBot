package net.javadiscord.command.analytics.graph;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GraphMessagesCommand implements Command {
	private final AsyncGraphGenerator asyncGraphGenerator;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		if (!event.getGuildId().isPresent()) {
			return Messages.warn(event, "Guild id not present.");
		}
		long guildId = event.getGuildId().get().asLong();
		this.asyncGraphGenerator.generateMessagesGraph(guildId, event.getMessage().getChannelId(), event.getMessage().getId());
		return Messages.respond(event, c -> c.createMessage(spec -> spec.setContent("Generating graph... this may take some time.")));
	}
}
