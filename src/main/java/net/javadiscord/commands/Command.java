package net.javadiscord.commands;

import net.dv8tion.jda.api.entities.Message;

public interface Command {
	void handle(Message message, String[] args);
}
