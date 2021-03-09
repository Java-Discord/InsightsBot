package net.javadiscord.command.debug;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.javadiscord.InsightsBot;
import net.javadiscord.command.Command;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Uptime implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		long uptimeMillis = System.currentTimeMillis() - InsightsBot.STARTED_AT;
		Duration d = Duration.of(uptimeMillis, ChronoUnit.MILLIS);
		String durationString = String.format(
				"%d:%02d:%02d",
				d.toHours(), d.toMinutes(), d.getSeconds()
		);
		return Messages.respondWithEmbed(event, spec -> {
			spec.setTitle("Uptime");
			spec.setDescription(durationString);
		});
	}
}
