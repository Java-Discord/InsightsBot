package net.javadiscord.commands.stats;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.javadiscord.InsightsBot;
import net.javadiscord.commands.Command;
import net.javadiscord.model.GuildData;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ViewCurrentCacheCommand implements Command {
	@Override
	public void handle(Message message, String[] args) {
		String timeString = InsightsBot.get().getGuildsCache().getLastClearedAt().format(
				DateTimeFormatter.ofPattern("d MMMM, yyyy, HH:mm:ss v")
		);
		GuildData data = InsightsBot.get().getGuildsCache().get(message.getGuild().getIdLong());
		message.reply(new EmbedBuilder()
				.setTitle("Current Cached Guild Statistics")
				.addField("Messages Created", Integer.toString(data.getMessagesCreated()), true)
				.addField("Messages Updated", Integer.toString(data.getMessagesUpdated()), true)
				.addField("Messages Removed", Integer.toString(data.getMessagesRemoved()), true)
				.addField("Reactions Added", Integer.toString(data.getReactionsAdded()), true)
				.addField("Reactions Removed", Integer.toString(data.getReactionsRemoved()), true)
				.addField("Members Joined", Integer.toString(data.getMembersJoined()), true)
				.addField("Members Left", Integer.toString(data.getMembersLeft()), true)
				.addField("Members Banned", Integer.toString(data.getMembersBanned()), true)
				.addField("Members Unbanned", Integer.toString(data.getMembersUnbanned()), true)
				.setFooter("Since " + timeString)
				.build()
		).queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Cached User Message Counts");
		builder.setFooter("Since " + timeString);
		for (Map.Entry<Long, Integer> entry : data.getUserMessageCounts().entrySet()) {
			User user = message.getJDA().retrieveUserById(entry.getKey()).complete();
			if (user != null) {
				builder.addField(user.getAsTag(), Integer.toString(entry.getValue()), true);
			}
		}
		message.reply(builder.build()).queue();
	}
}
