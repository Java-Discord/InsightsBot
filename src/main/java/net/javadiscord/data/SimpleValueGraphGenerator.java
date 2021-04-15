package net.javadiscord.data;

import lombok.extern.slf4j.Slf4j;
import net.javadiscord.InsightsBot;
import net.javadiscord.util.ChartStyler;
import net.javadiscord.util.SqlHelper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
public class SimpleValueGraphGenerator {

	public Optional<byte[]> generateTimeSeries(long guildId, LocalDate start, LocalDate end, String sqlScript, String title, String... seriesTitles) {
		var results = this.getSeriesData(guildId, start, end, sqlScript, seriesTitles.length);
		if (results.isEmpty()) {
			return Optional.empty();
		}

		LocalDate date = LocalDate.from(start);
		List<TimeSeries> series = new ArrayList<>(seriesTitles.length);
		for (String seriesTitle : seriesTitles) {
			series.add(new TimeSeries(seriesTitle));
		}
		while (!date.isAfter(end)) {
			Day day = new Day(java.util.Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant()));
			List<Number> resultValues = results.getOrDefault(date, Collections.nCopies(series.size(), 0));
			for (int i = 0; i < series.size(); i++) {
				series.get(i).add(day, resultValues.get(i));
			}
			date = date.plusDays(1);
		}

		TimeSeriesCollection collection = new TimeSeriesCollection(TimeZone.getTimeZone(ZoneOffset.UTC));
		series.forEach(collection::addSeries);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				title,
				"Days",
				"Value",
				collection
		);
		ChartStyler.getInstance().style(chart);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ChartUtils.writeChartAsPNG(os, chart, 1000, 600);
			return Optional.of(os.toByteArray());
		} catch (IOException e) {
			log.error("Exception while writing chart as PNG.", e);
			return Optional.empty();
		}
	}

	private Map<LocalDate, List<Number>> getSeriesData(long guildId, LocalDate start, LocalDate end, String sqlScript, int valuesCount) {
		Map<LocalDate, List<Number>> results = new HashMap<>();
		try (Connection con = InsightsBot.get().getDataSource().getConnection()) {
			con.setReadOnly(true);
			PreparedStatement stmt = con.prepareStatement(SqlHelper.load(sqlScript));
			stmt.setLong(1, guildId);
			stmt.setDate(2, Date.valueOf(start));
			stmt.setDate(3, Date.valueOf(end));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LocalDate date = rs.getDate(1).toLocalDate();
				List<Number> values = new ArrayList<>(valuesCount);
				for (int i = 2; i < 2 + valuesCount; i++) {
					values.add(rs.getInt(i));
				}
				results.put(date, values);
			}
		} catch (SQLException e) {
			log.error("Exception occurred while fetching data.", e);
		}
		return results;
	}

	public Optional<byte[]> generateBar(long guildId, LocalDate start, LocalDate end, String sqlScript, String title, String domainLabel, String rangeLabel) {
		var results = this.getBarData(guildId, start, end, sqlScript);
		if (results.isEmpty()) {
			return Optional.empty();
		}
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		for (Map.Entry<String, Number> entry : results.entrySet()) {
			ds.addValue(entry.getValue(), entry.getKey(), "single");
		}
		JFreeChart barChart = ChartFactory.createBarChart(
				title,
				domainLabel,
				rangeLabel,
				ds
		);
		ChartStyler.getInstance().style(barChart);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ChartUtils.writeChartAsPNG(os, barChart, 1000, 600);
			return Optional.of(os.toByteArray());
		} catch (IOException e) {
			log.error("Exception while writing chart as PNG.", e);
			return Optional.empty();
		}
	}

	private Map<String, Number> getBarData(long guildId, LocalDate start, LocalDate end, String sqlScript) {
		Map<String, Number> results = new HashMap<>();
		try (Connection con = InsightsBot.get().getDataSource().getConnection()) {
			con.setReadOnly(true);
			PreparedStatement stmt = con.prepareStatement(SqlHelper.load(sqlScript));
			stmt.setLong(1, guildId);
			stmt.setDate(2, Date.valueOf(start));
			stmt.setDate(3, Date.valueOf(end));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String resultName = rs.getString(1);
				int value = rs.getInt(2);
				results.put(resultName, value);
			}
		} catch (SQLException e) {
			log.error("Exception occurred while fetching data.", e);
		}
		return results;
	}
}
