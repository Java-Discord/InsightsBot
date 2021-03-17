package net.javadiscord.command.analytics;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import net.javadiscord.data.aggregation.DailyAggregateGenerator;
import net.javadiscord.data.dao.MessagesMetricRepository;
import net.javadiscord.data.model.stats.Metric;
import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GetDailyAggregateCommand implements Command {
	private final DailyAggregateGenerator dailyAggregateGenerator;
	private final MessagesMetricRepository messagesMetricRepository;

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
		Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant end = date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
		Set<Metric> metrics = this.dailyAggregateGenerator.generateDailyAggregate(guildId, start, end, true);
		MessagesMetric messagesMetric = null;
		for (Metric metric : metrics) {
			if (metric instanceof MessagesMetric) {
				messagesMetric = (MessagesMetric) metric;
				break;
			}
		}
		if (messagesMetric == null) {
			messagesMetric = this.messagesMetricRepository.findByGuildAndInterval(guildId, start, end).orElse(null);
			if (messagesMetric == null) {
				return Messages.warn(event, "No metrics to display.");
			}
		}
		MessagesMetric mm = messagesMetric;
		return Messages.respondWithEmbed(event, spec -> {
			spec.setTitle("Aggregate Message Metrics for " + date.format(DateTimeFormatter.ISO_LOCAL_DATE));
			spec.addField("Messages Created", String.valueOf(mm.getMessagesCreated()), true);
			spec.addField("Messages Deleted", String.valueOf(mm.getMessagesDeleted()), true);
			spec.addField("Messages Updated", String.valueOf(mm.getMessagesUpdated()), true);
			spec.addField("Messages Retained", String.valueOf(mm.getMessagesRetained()), true);
			spec.addField("Reactions Added", String.valueOf(mm.getReactionsAdded()), true);
			spec.addField("Reactions Removed", String.valueOf(mm.getReactionsRemoved()), true);
			spec.addField("Active Users", String.valueOf(mm.getActiveUsers()), true);
		});
	}
}
