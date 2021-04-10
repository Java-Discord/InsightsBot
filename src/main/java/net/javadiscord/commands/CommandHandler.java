package net.javadiscord.commands;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;

/**
 * The command handler is responsible for inspecting and processing incoming
 * commands from messages.
 */
public class CommandHandler {
	private static final String DEFAULT_PREFIX = "!ib";

	@Getter
	private final CommandRegistry commandRegistry = new CommandRegistry();

	public boolean isCommand(Message message) {
		return message.getContentRaw().toLowerCase().startsWith(this.getPrefixForGuild(message.getGuild()));
	}

	private String getPrefixForGuild(Guild guild) {
		return DEFAULT_PREFIX;
	}

	public void handle(Message message) {
		String[] words = message.getContentRaw().split("\\s+");
		if (words.length < 2) {
			message.reply("Missing command.").queue();
			return;
		}
		// The command is always the second word, after the prefix.
		String command = words[1].trim().toLowerCase();
		String[] args = Arrays.copyOfRange(words, 2, words.length);
		this.commandRegistry.get(command).ifPresentOrElse(
				c -> c.handle(message, args),
				() -> message.reply("Unknown command.").queue()
		);
	}
}
