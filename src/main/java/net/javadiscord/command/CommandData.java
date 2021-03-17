package net.javadiscord.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommandData {
	private final Command command;
	private final String description;
	private final boolean adminOnly;
}
