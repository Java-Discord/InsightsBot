package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.javadiscord.command.Command;
import net.javadiscord.command.CommandHandler;
import net.javadiscord.command.CommandRegistry;
import net.javadiscord.command.HelpCommand;
import net.javadiscord.command.analytics.graph.GraphMessagesCommand;
import org.reactivestreams.Publisher;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * A generic parent command for graphic sets of data. Contains its own command
 * registry to handle specific types of graph requests.
 */
@Component
public class GraphCommand implements Command {
	private final CommandHandler commandHandler;

	public GraphCommand(ApplicationContext applicationContext) {
		CommandRegistry commandRegistry = new CommandRegistry();
		commandRegistry.register("help", new HelpCommand(commandRegistry), "Shows help information.");
		commandRegistry.register(
				"messages",
				applicationContext.getBean(GraphMessagesCommand.class),
				"Show a graph of messages over time."
		);
		this.commandHandler = new CommandHandler(commandRegistry);
	}

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return this.commandHandler.handle(event, args);
	}
}
