package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import net.javadiscord.data.dao.MessageEventRepository;
import net.javadiscord.data.model.events.MessageEvent;
import net.javadiscord.util.SqlScriptHelper;
import org.reactivestreams.Publisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class MessageCountCommand implements Command {
	private final MessageEventRepository messageEventRepository;
	private final JdbcTemplate jdbcTemplate;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		if (event.getGuildId().isEmpty()) {
			return event.getMessage().getChannel().flatMap(c -> c.createMessage("Missing guild id."));
		}
		long guildId = event.getGuildId().get().asLong();

		this.jdbcTemplate.query(connection -> {
			PreparedStatement stmt = connection.prepareStatement(SqlScriptHelper.load("sql/count_created_messages.sql"));
			stmt.setString(1, LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
			stmt.setString(2, LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
			return stmt;
		}, resultSet -> {
			long count = resultSet.getLong(1);
		});

		Instant cutoff = LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC);
		long count = this.messageEventRepository.countAllByGuildIdEqualsAndTimestampAfterAndEventTypeEquals(guildId, cutoff, MessageEvent.EventType.CREATE);
		return event.getMessage().getChannel().flatMap(c -> c.createMessage(
				String.format("%d messages have been sent in this guild since %s", count, cutoff.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
		));
	}
}
