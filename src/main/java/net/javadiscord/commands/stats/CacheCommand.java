package net.javadiscord.commands.stats;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.javadiscord.InsightsBot;
import net.javadiscord.commands.Command;
import net.javadiscord.model.GuildData;
import org.quartz.SchedulerException;

import java.time.format.DateTimeFormatter;

/**
 * A command for interacting with the bot's internal cache of data for a guild.
 *
 * <p>
 *     Offers the following functionalities:
 *     <ul>
 *         <li>Without any additional arguments, shows the current cache data
 *         for the guild.</li>
 *         <li>The <code>flush</code> sub-command will flush all cached data for
 *         the guild into the database, thus making it available for queries.</li>
 *     </ul>
 * </p>
 */
@Slf4j
public class CacheCommand implements Command {
	@Override
	public void handle(Message message, String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("flush")) {
			this.flush(message);
		} else {
			this.view(message);
		}
	}

	/**
	 * Invoked when a user requests to see the current cache data for their guild.
	 * @param message The message which triggered this command.
	 */
	private void view(Message message) {
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
	}

	/**
	 * Invoked when a user requests to flush current cache data to the database.
	 * @param message The message which triggered this command.
	 */
	private void flush(Message message) {
		if (!InsightsBot.get().getGuildsCache().exists(message.getGuild().getIdLong())) {
			message.reply("There is no cached data to flush.").queue();
			return;
		}
		log.info("Manually flushing cache for guild {}({}), triggered by {}.", message.getGuild().getId(), message.getGuild().getName(), message.getAuthor().getAsTag());
		try {
			InsightsBot.get().getJobManager().triggerGuildCacheFlush(message.getGuild().getIdLong());
			message.reply("Data for this guild has been flushed to the database.").queue();
		} catch (SchedulerException e) {
			log.error("Could not trigger guild cache flush.", e);
			message.reply("An error occurred and a guild cache flush could not be triggered.").queue();
		}
	}
}
