package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import net.javadiscord.InsightsBot;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;

import java.util.List;

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
			boolean authorIsAdmin = event.getMessage().getAuthor().isPresent()
					&& CommandHandler.ADMIN_IDS.contains(event.getMessage().getAuthor().get().getId().asLong());
			this.formatCommands(spec, authorIsAdmin);
		});
	}

	private void formatCommands(EmbedCreateSpec spec, boolean authorIsAdmin) {
		List<String> commands = this.registry.getCommands();
		for (String command : commands) {
			CommandData data = this.registry.getData(command);
			if (data.isAdminOnly() && !authorIsAdmin) continue;
			String usage = String.format("`%s %s`", InsightsBot.PREFIX, command);
			spec.addField(usage, data.getDescription(), false);
		}
	}
}
