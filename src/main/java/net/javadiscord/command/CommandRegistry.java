package net.javadiscord.command;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandRegistry {
	private final Map<String, Command> commandMap;

	public CommandRegistry() {
		this.commandMap = new ConcurrentHashMap<>();
	}

	public void register(String keyword, Command command) {
		this.commandMap.put(keyword.trim().toLowerCase(), command);
	}

	public Optional<Command> get(String keyword) {
		return Optional.ofNullable(this.commandMap.get(keyword.toLowerCase()));
	}
}
