package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.BotInitializer;
import net.javadiscord.command.Command;
import org.reactivestreams.Publisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomQueryCommand implements Command {
	private final JdbcTemplate jdbcTemplate;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		int scriptStartIndex = -1;
		int scriptEndIndex = -1;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("```sql")) {
				scriptStartIndex = i;
			} else if (args[i].equalsIgnoreCase("```")) {
				scriptEndIndex = i;
				break;
			}
		}
		if (scriptStartIndex == -1 || scriptEndIndex == -1) {
			return event.getMessage().getChannel().flatMap(c -> c.createMessage("Invalid or missing SQL code block."));
		}

		String query = String.join("\n", Arrays.copyOfRange(args, scriptStartIndex + 1, scriptEndIndex));
		log.info("Executing custom user-submitted query:\n{}", query);
		StringBuilder sb = new StringBuilder();
		this.jdbcTemplate.query(query, resultSet -> {
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				sb.append(resultSet.getString(i)).append(", ");
			}
			sb.append("\n");
		});
		String msg = sb.toString();
		if (msg.length() > 2000) {
			msg = msg.substring(0, 1950) + "\n**Message truncated due length**";
		}

		String finalMsg = msg;
		return event.getMessage().getChannel().flatMap(c -> c.createMessage(finalMsg));
	}

	@Override
	public Set<Long> getWhitelistedUserIds() {
		return BotInitializer.ADMIN_IDS;
	}
}
