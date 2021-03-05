package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDeltaCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return null;
	}
}
