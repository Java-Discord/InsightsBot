package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import net.javadiscord.data.aggregation.AggregateGenerator;
import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class GetDailyAggregateCommand implements Command {
	private final AggregateGenerator aggregateGenerator;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		if (!event.getGuildId().isPresent()) {
			return Messages.warn(event, "Guild id not present.");
		}
		LocalDate date;
		if (args.length < 1 || args[0].equalsIgnoreCase("today")) {
			date = LocalDate.now(ZoneOffset.UTC);
		} else {
			try {
				date = LocalDate.parse(args[0]);
			} catch (DateTimeParseException e) {
				return Messages.warn(event, "Invalid date. Should be YYYY-MM-DD format.");
			}
		}
		long guildId = event.getGuildId().get().asLong();
		MessagesMetric messagesMetric = this.aggregateGenerator.generateMessagesMetric(guildId, date, false);
		if (messagesMetric == null) {
			return Messages.warn(event, "No metrics to display.");
		}
		return Messages.respondWithEmbed(event, spec -> {
			spec.setTitle("Aggregate Message Metrics for " + date.format(DateTimeFormatter.ISO_LOCAL_DATE));
			spec.addField("Messages Created", String.valueOf(messagesMetric.getMessagesCreated()), true);
			spec.addField("Messages Deleted", String.valueOf(messagesMetric.getMessagesDeleted()), true);
			spec.addField("Messages Updated", String.valueOf(messagesMetric.getMessagesUpdated()), true);
			spec.addField("Messages Retained", String.valueOf(messagesMetric.getMessagesRetained()), true);
			spec.addField("Reactions Added", String.valueOf(messagesMetric.getReactionsAdded()), true);
			spec.addField("Reactions Removed", String.valueOf(messagesMetric.getReactionsRemoved()), true);
			spec.addField("Active Users", String.valueOf(messagesMetric.getActiveUsers()), true);
		});
	}
}
