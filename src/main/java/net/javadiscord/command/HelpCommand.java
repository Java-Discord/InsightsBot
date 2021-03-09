package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import net.javadiscord.InsightsBot;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;

/**
 * Simple command that displays a help message.
 */
public class HelpCommand implements Command {
	private final CommandRegistry registry;

	public HelpCommand(CommandRegistry registry) {
		this.registry = registry;
	}

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return Messages.respondWithEmbed(event, spec -> {
			spec.setTitle("Help - Commands & More");
			this.formatCommands(spec, "help", "messageCount", "joinCount", "getDailyAggregate");
			boolean authorIsAdmin = event.getMessage().getAuthor().isPresent()
					&& AdminCommand.isAdmin(event.getMessage().getAuthor().get().getId().asLong());
			if (authorIsAdmin) {
				this.formatCommands(spec, "tasks", "uptime", "customQuery");
			}
			long memUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long memUsageMb = memUsage / (1024 * 1024);
			spec.setFooter(String.format("Status: Memory usage: %dMB", memUsageMb), null);
		});
	}

	private void formatCommands(EmbedCreateSpec spec, String... commands) {
		for (String command : commands) {
			String usage = String.format("`%s %s`", InsightsBot.PREFIX, command);
			spec.addField(usage, this.registry.getDescription(command), false);
		}
	}
}
