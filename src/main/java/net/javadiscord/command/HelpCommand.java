package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.javadiscord.InsightsBot;
import org.reactivestreams.Publisher;

/**
 * Simple command that displays a help message.
 */
public class HelpCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return event.getMessage().getChannel().flatMap(c -> c.createEmbed(spec -> {
			spec.setTitle("Help - Commands & More");
			spec.setAuthor("InsightsBot", "https://github.com/Java-Discord/InsightsBot", null);
			spec.addField(this.formatCommand("help"), "Shows this message.", false);
			spec.addField(this.formatCommand("messageCount"), "Gets a count of messages in the past month.", false);
			long memUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long memUsageMb = memUsage / (1024 * 1024);
			spec.setFooter(String.format("Status: Memory usage: %dMB", memUsageMb), null);
		}))
				.flatMap(message -> event.getMessage().delete());
	}

	private String formatCommand(String command) {
		return String.format("`%s %s`", InsightsBot.PREFIX, command);
	}
}
