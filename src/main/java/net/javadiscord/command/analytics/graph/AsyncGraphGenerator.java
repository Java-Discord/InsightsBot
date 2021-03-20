package net.javadiscord.command.analytics.graph;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.data.aggregation.AggregateGenerator;
import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import net.javadiscord.util.ChartStyler;
import net.javadiscord.util.DiscordClientHolder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncGraphGenerator {
	private final AggregateGenerator aggregateGenerator;

	@Async
	public void generateMessagesGraph(long guildId, Snowflake channelId, Snowflake messageId) {
		TimeSeries createdSeries = new TimeSeries("Created");
		TimeSeries deletedSeries = new TimeSeries("Deleted");
		TimeSeries updatedSeries = new TimeSeries("Updated");
		TimeSeries retainedSeries = new TimeSeries("Retained");
		TimeSeries reactionsAddedSeries = new TimeSeries("Reactions Added");
		TimeSeries reactionsRemovedSeries = new TimeSeries("Reactions Removed");
		TimeSeries activeUsersSeries = new TimeSeries("Active Users");

		LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);
		TemporalAmount interval = Duration.ofHours(1);
		int limit = 24 * 10;
		for (int i = 0; i < limit; i++) {
			Instant startTimestamp = timestamp.minus(interval).toInstant(ZoneOffset.UTC);
			Instant endTimestamp = timestamp.toInstant(ZoneOffset.UTC);
			log.info("Preparing message data for interval {} / {} for guild {}.\n\tBetween {} and {}.", i + 1, limit, guildId, startTimestamp, endTimestamp);
			MessagesMetric metric = this.aggregateGenerator.generateMessagesMetric(guildId, startTimestamp, endTimestamp, false);
			Hour t = new Hour(timestamp.getHour(), timestamp.getDayOfMonth(), timestamp.getMonthValue(), timestamp.getYear());
			createdSeries.add(t, metric.getMessagesCreated());
			deletedSeries.add(t, metric.getMessagesDeleted());
			updatedSeries.add(t, metric.getMessagesUpdated());
			retainedSeries.add(t, metric.getMessagesRetained());
			reactionsAddedSeries.add(t, metric.getReactionsAdded());
			reactionsRemovedSeries.add(t, metric.getReactionsRemoved());
			activeUsersSeries.add(t, metric.getActiveUsers());
			timestamp = timestamp.minus(interval);
		}
		TimeSeriesCollection collection = new TimeSeriesCollection(TimeZone.getTimeZone(ZoneOffset.UTC));
		collection.addSeries(createdSeries);
		collection.addSeries(deletedSeries);
		collection.addSeries(updatedSeries);
		collection.addSeries(retainedSeries);
		collection.addSeries(reactionsAddedSeries);
		collection.addSeries(reactionsRemovedSeries);
		collection.addSeries(activeUsersSeries);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Message Metrics",
				"Days",
				"Value",
				collection
		);
		ChartStyler.getInstance().style(chart);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ChartUtils.writeChartAsPNG(os, chart, 1000, 600);
			Optional<Message> optionalMessage = DiscordClientHolder.getInstance().getClient()
					.getMessageById(channelId, messageId).blockOptional();
			if (!optionalMessage.isPresent()) return;
			Optional<MessageChannel> optionalChannel = optionalMessage.get().getChannel().blockOptional();
			if (!optionalChannel.isPresent()) return;
			optionalChannel.get().createMessage(spec -> {
				spec.setMessageReference(messageId);
				spec.addFile("message_metrics.png", new ByteArrayInputStream(os.toByteArray()));
			}).block();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
