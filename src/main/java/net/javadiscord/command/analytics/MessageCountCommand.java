package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import net.javadiscord.data.dao.MessageEventRepository;
import net.javadiscord.data.model.events.MessageEvent;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class MessageCountCommand implements Command {
	private final MessageEventRepository messageEventRepository;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		if (event.getGuildId().isEmpty()) {
			return event.getMessage().getChannel().flatMap(c -> c.createMessage("Missing guild id."));
		}
		long guildId = event.getGuildId().get().asLong();
		Instant cutoff = LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC);
		long count = this.messageEventRepository.countAllByGuildIdEqualsAndTimestampAfterAndEventTypeEquals(guildId, cutoff, MessageEvent.EventType.CREATE);
		return event.getMessage().getChannel().flatMap(c -> c.createMessage(
				String.format("%d messages have been sent in this guild since %s", count, cutoff.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
		));
	}
}
