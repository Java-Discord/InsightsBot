package net.javadiscord.command.debug;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.javadiscord.InsightsBot;
import net.javadiscord.command.Command;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;

public class Uptime implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		long uptimeMillis = System.currentTimeMillis() - InsightsBot.STARTED_AT;
		long seconds = (uptimeMillis / 1000) % 60;
		long minutes = (uptimeMillis / (1000 * 60)) % 60;
		long hours = (uptimeMillis / (1000 * 60 * 60)) % 24;
		String durationString = String.format(
				"%d:%02d:%02d",
				hours, minutes, seconds
		);
		return Messages.respondWithEmbed(event, spec -> {
			spec.setTitle("Uptime");
			spec.setDescription(durationString);
		});
	}
}
