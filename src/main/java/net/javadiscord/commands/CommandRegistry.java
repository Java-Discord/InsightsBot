package net.javadiscord.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandRegistry {
	private final Map<String, Command> commands = new HashMap<>();

	public CommandRegistry register(String commandName, Command command) {
		this.commands.put(commandName, command);
		return this;
	}

	public Optional<Command> get(String commandName) {
		return Optional.ofNullable(this.commands.get(commandName));
	}
}
