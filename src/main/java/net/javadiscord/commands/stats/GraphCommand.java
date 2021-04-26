package net.javadiscord.commands.stats;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.javadiscord.commands.Command;
import net.javadiscord.data.SimpleValueGraphGenerator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * The main command for rendering data in graphs and charts.
 * TODO: Split up graph types and specialty graphs.
 */
@Slf4j
public class GraphCommand implements Command {
	private static final String[] AVAILABLE_GRAPHS = {
			"messages", "active-users", "members", "top-users"
	};

	@Override
	public void handle(Message message, String[] args) {
		if (args.length < 1) {
			message.reply("Missing argument(s).").queue();
			return;
		}
		String subCommand = args[0].trim().toLowerCase();
		String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
		switch (subCommand) {
			case "list" -> {
				String graphs = Arrays.stream(AVAILABLE_GRAPHS).map(s -> "**" + s + "**").collect(Collectors.joining(", "));
				message.reply("The following graphs can be made: " + graphs).queue();
			}
			case "messages" -> this.makeSeriesGraph(message, subArgs, "sql/read/select_messages_data_in_range.sql", "Messages", "Created", "Edited", "Deleted");
			case "active-users" -> this.makeSeriesGraph(message, subArgs, "sql/read/select_active_users_in_range.sql", "Active Users", "Users");
			case "members" -> this.makeSeriesGraph(message, subArgs, "sql/read/select_members_data_in_range.sql", "Members", "Joined", "Left", "Banned", "Unbanned");
			case "top-users" -> this.makeBarGraph(message, subArgs, "sql/read/select_top_users_in_range.sql", "Top Users", "User", "Value");
			default -> message.reply("Unknown argument.").queue();
		}
	}

	private void makeSeriesGraph(Message message, String[] args, String sqlScript, String title, String... seriesTitles) {
		LocalDate end = LocalDate.now();
		LocalDate start = LocalDate.now().minusDays(30);
		if (args.length > 0) {
			Scanner s = new Scanner(args[0]);
			if (s.hasNextInt()) {
				start = LocalDate.now().minusDays(s.nextInt());
			}
		}
		if (args.length > 1) {
			Scanner s = new Scanner(args[1]);
			if (s.hasNextInt()) {
				end = LocalDate.now().minusDays(s.nextInt());
			}
		}
		if (!end.isAfter(start)) {
			message.reply("Invalid date range.").queue();
			return;
		}

		Optional<byte[]> optionalBytes = new SimpleValueGraphGenerator().generateTimeSeries(
				message.getGuild().getIdLong(),
				start,
				end,
				sqlScript,
				title,
				seriesTitles
		);
		if (optionalBytes.isPresent() && optionalBytes.get().length > 0) {
			message.reply(optionalBytes.get(), "graph.png").queue();
		} else {
			message.reply("No data to display.").queue();
		}
	}

	private void makeBarGraph(Message message, String[] args, String sqlScript, String title, String domainLabel, String rangeLabel) {
		LocalDate end = LocalDate.now();
		LocalDate start = LocalDate.now().minusDays(30);
		if (args.length > 0) {
			Scanner s = new Scanner(args[0]);
			if (s.hasNextInt()) {
				start = LocalDate.now().minusDays(s.nextInt());
			}
		}
		if (args.length > 1) {
			Scanner s = new Scanner(args[1]);
			if (s.hasNextInt()) {
				end = LocalDate.now().minusDays(s.nextInt());
			}
		}
		if (!end.isAfter(start)) {
			message.reply("Invalid date range.").queue();
			return;
		}
		Optional<byte[]> optionalBytes = new SimpleValueGraphGenerator().generateBar(
				message.getGuild().getIdLong(),
				start,
				end,
				sqlScript,
				title,
				domainLabel,
				rangeLabel
		);
		if (optionalBytes.isPresent() && optionalBytes.get().length > 0) {
			message.reply(optionalBytes.get(), "graph.png").queue();
		} else {
			message.reply("No data to display.").queue();
		}
	}
}
