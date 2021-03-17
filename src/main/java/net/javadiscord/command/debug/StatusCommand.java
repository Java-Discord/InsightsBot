package net.javadiscord.command.debug;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.InsightsBot;
import net.javadiscord.command.Command;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Returns some information about the status of the bot.
 */
@Component
@RequiredArgsConstructor
public class StatusCommand implements Command {
	private final DataSource dataSource;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return event.getClient().getGuilds().buffer().flatMap(guilds -> Messages.respondWithEmbed(event, spec -> {
			spec.addField("Uptime", this.getUptimeString(), true);
			spec.addField("Memory Usage", this.getMemoryUsage(), true);
			spec.addField("Datasource", this.getDatasourceStatus(), true);
			spec.addField("Active In", guilds.size() + " guilds", true);
		}));
	}

	private String getUptimeString() {
		long uptimeMillis = System.currentTimeMillis() - InsightsBot.STARTED_AT;
		long seconds = (uptimeMillis / 1000) % 60;
		long minutes = (uptimeMillis / (1000 * 60)) % 60;
		long hours = (uptimeMillis / (1000 * 60 * 60)) % 24;
		return String.format(
				"%d:%02d:%02d",
				hours, minutes, seconds
		);
	}

	private String getMemoryUsage() {
		long memUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long memUsageMb = memUsage / (1024 * 1024);
		return memUsageMb + "MB";
	}

	private String getDatasourceStatus() {
		try {
			if (this.dataSource.getConnection().isValid(1)) {
				return "Online";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Offline";
	}
}
