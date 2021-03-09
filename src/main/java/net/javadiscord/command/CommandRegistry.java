package net.javadiscord.command;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that keeps track of a string-indexed map of commands. Each command
 * is registered with a lowercase keyword.
 */
@Component
public class CommandRegistry {
	private final Map<String, CommandData> commandMap;
	private final CommandData unknownCommandData = new CommandData(new UnknownCommand(), "Unknown command.");

	public CommandRegistry() {
		this.commandMap = new ConcurrentHashMap<>();
	}

	public void register(String keyword, Command command) {
		this.register(keyword, command, "A command.");
	}

	public void register(String keyword, Command command, String description) {
		this.commandMap.put(keyword.trim().toLowerCase(), new CommandData(command, description));
	}

	public Command get(String keyword) {
		return this.getData(keyword).getCommand();
	}

	public String getDescription(String keyword) {
		return this.getData(keyword).getDescription();
	}

	public CommandData getData(String keyword) {
		return this.commandMap.getOrDefault(keyword.toLowerCase(), this.unknownCommandData);
	}

	@Override
	public String toString() {
		return "CommandRegistry{" +
				"commandMap=" + commandMap +
				'}';
	}
}
