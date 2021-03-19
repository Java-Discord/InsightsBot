package net.javadiscord.command.analytics.graph;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import net.javadiscord.data.aggregation.AggregateGenerator;
import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import net.javadiscord.util.Messages;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.TimeZone;

@Component
@RequiredArgsConstructor
public class GraphMessagesCommand implements Command {
	private final AggregateGenerator aggregateGenerator;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		if (!event.getGuildId().isPresent()) {
			return Messages.warn(event, "Guild id not present.");
		}
		long guildId = event.getGuildId().get().asLong();

		TimeSeries createdSeries = new TimeSeries("Created");
		TimeSeries deletedSeries = new TimeSeries("Deleted");
		TimeSeries updatedSeries = new TimeSeries("Updated");
		TimeSeries retainedSeries = new TimeSeries("Retained");
		TimeSeries reactionsAddedSeries = new TimeSeries("Reactions Added");
		TimeSeries reactionsRemovedSeries = new TimeSeries("Reactions Removed");
		TimeSeries activeUsersSeries = new TimeSeries("Active Users");

		LocalDate date = LocalDate.now(ZoneOffset.UTC);
		for (int i = 0; i < 15; i++) {
			MessagesMetric metric = this.aggregateGenerator.generateMessagesMetric(guildId, date, false);
			Day d = new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
			createdSeries.add(d, metric.getMessagesCreated());
			deletedSeries.add(d, metric.getMessagesDeleted());
			updatedSeries.add(d, metric.getMessagesUpdated());
			retainedSeries.add(d, metric.getMessagesRetained());
			reactionsAddedSeries.add(d, metric.getReactionsAdded());
			reactionsRemovedSeries.add(d, metric.getReactionsRemoved());
			activeUsersSeries.add(d, metric.getActiveUsers());
			date = date.minusDays(1);
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
		chart.getPlot().setBackgroundPaint(Color.WHITE);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ChartUtils.writeChartAsPNG(os, chart, 1000, 600);
			return Messages.respondWithFile(event, os, "message_metrics.png");
		} catch (IOException e) {
			e.printStackTrace();
			return Messages.warn(event, "Could not write chart as PNG: " + e.getMessage());
		}
	}
}
