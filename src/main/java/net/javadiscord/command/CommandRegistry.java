package net.javadiscord.command;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry that keeps track of a string-indexed map of commands. Each command
 * is registered with a lowercase keyword.
 */
public class CommandRegistry {
	/**
	 * The mapping of commands according to their names. Maps to a
	 * {@link CommandData} object which contains the command itself, and some
	 * additional metadata.
	 */
	private final Map<String, CommandData> commandMap;
	private final CommandData unknownCommandData = new CommandData(new UnknownCommand(), "Unknown command.", false);

	public CommandRegistry() {
		this.commandMap = new ConcurrentHashMap<>();
	}

	public void register(String keyword, CommandData commandData) {
		this.commandMap.put(keyword.trim().toLowerCase(), commandData);
	}

	public void register(String keyword, Command command, String description) {
		this.register(keyword, new CommandData(command, description, false));
	}

	public Command get(String keyword) {
		return this.getData(keyword).getCommand();
	}

	public CommandData getData(String keyword) {
		return this.commandMap.getOrDefault(keyword.toLowerCase(), this.unknownCommandData);
	}

	public List<String> getCommands() {
		return this.commandMap.keySet().stream().sorted().collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "CommandRegistry{" +
				"commandMap=" + commandMap +
				'}';
	}
}
