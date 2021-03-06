package net.javadiscord.command;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The temporal expression parser can parse certain expressions from an array of
 * string arguments, to build a temporal range with which to query data.
 */
@Component
public class TemporalExpressionParser {
	public Pair<LocalDateTime, LocalDateTime> parse(String[] args, Duration defaultBackRange) {
		try {
			return this.parse(args);
		} catch (Exception e) {
			return Pair.of(
					LocalDateTime.now().minus(defaultBackRange).truncatedTo(ChronoUnit.SECONDS),
					LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
			);
		}
	}

	public Pair<LocalDateTime, LocalDateTime> parse(String[] args) {
		if (args.length < 2) {
			throw new IllegalArgumentException("Missing at least a from value.");
		}
		long fromValue = Long.parseLong(args[0]);
		ChronoUnit fromUnit = ChronoUnit.valueOf(args[1].trim().toUpperCase());
		LocalDateTime from = LocalDateTime.now().plus(fromValue, fromUnit).truncatedTo(ChronoUnit.SECONDS);
		LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		if (args.length < 4) {
			return Pair.of(from, to);
		}
		long toValue = Long.parseLong(args[2]);
		ChronoUnit toUnit = ChronoUnit.valueOf(args[3].trim().toUpperCase());
		return Pair.of(from, to.plus(toValue, toUnit));
	}
}
