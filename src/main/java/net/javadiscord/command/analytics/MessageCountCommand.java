package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.TemporalExpressionParser;
import net.javadiscord.util.SqlHelper;
import org.reactivestreams.Publisher;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageCountCommand extends GuildSpecificCommand {
	private final JdbcTemplate jdbcTemplate;
	private final TemporalExpressionParser temporalExpressionParser;

	@Override
	protected Publisher<?> handle(MessageCreateEvent event, long guildId, String[] args) {
		Pair<LocalDateTime, LocalDateTime> range = this.temporalExpressionParser.parse(args, Duration.ofDays(365));
		final String start = range.getFirst().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		final String end = range.getSecond().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		Long[] userBlacklist = new Long[]{event.getClient().getSelfId().asLong()};

		Long count = this.jdbcTemplate.query(connection -> {
			PreparedStatement stmt = connection.prepareStatement(SqlHelper.load("sql/count_messages.sql"));
			stmt.setLong(1, guildId);
			stmt.setString(2, Arrays.stream(userBlacklist).map(Object::toString).collect(Collectors.joining(", ")));
			stmt.setString(3, start);
			stmt.setString(4, end);
			return stmt;
		}, resultSet -> {
			if (resultSet.next()) {
				return resultSet.getLong(1);
			}
			return null;
		});
		return event.getMessage().getChannel().flatMap(c -> c.createMessage(
				String.format("%d messages have been sent in this guild from %s to %s.", count, start, end)
		));
	}
}
