package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;

/**
 * Command that's called when an unknown keyword is entered following the bot's
 * prefix.
 */
public class UnknownCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return Messages.respondWithEmbed(event, spec -> {
			spec.setTitle("Unknown Command");
			spec.setDescription("The command you entered does not exist.");
		});
	}
}
