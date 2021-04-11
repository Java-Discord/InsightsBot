package net.javadiscord.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.javadiscord.InsightsBot;
import net.javadiscord.commands.Command;

import java.sql.SQLException;
import java.time.Duration;

public class StatusCommand implements Command {
	@Override
	public void handle(Message message, String[] args) {
		Duration uptime = InsightsBot.get().getUptime();
		String uptimeString = String.format("%d:%02d:%02d", uptime.toHours(), uptime.toMinutesPart(), uptime.toSecondsPart());
		var embed = new EmbedBuilder()
				.addField("Uptime", uptimeString, true)
				.addField("Memory Usage", this.getMemoryUsage(), true)
				.addField("Data Source", this.getDataSourceStatus(), true)
				.build();
		message.reply(embed).queue();
	}

	private String getMemoryUsage() {
		long memUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long memUsageMb = memUsage / (1024 * 1024);
		return memUsageMb + "MB";
	}

	private String getDataSourceStatus() {
		try {
			boolean valid = InsightsBot.get().getDataSource().getConnection().isValid(500);
			if (valid) return "Online";
			return "Offline";
		} catch (SQLException e) {
			return "Error: " + e.getSQLState();
		}
	}
}
