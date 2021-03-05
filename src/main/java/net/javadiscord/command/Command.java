package net.javadiscord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.reactivestreams.Publisher;

public interface Command {

	Publisher<?> handle(MessageCreateEvent event, String[] args);
}
