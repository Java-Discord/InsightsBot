package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.javadiscord.command.Command;
import org.reactivestreams.Publisher;

public abstract class GuildSpecificCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		if (!event.getGuildId().isPresent()) {
			return event.getMessage().getChannel().flatMap(c -> c.createMessage("Missing guild id."));
		}
		return this.handle(event, event.getGuildId().get().asLong(), args);
	}

	protected abstract Publisher<?> handle(MessageCreateEvent event, long guildId, String[] args);
}
