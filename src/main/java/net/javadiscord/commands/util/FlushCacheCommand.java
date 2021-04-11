package net.javadiscord.commands.util;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.javadiscord.InsightsBot;
import net.javadiscord.commands.Command;
import org.quartz.SchedulerException;

@Slf4j
public class FlushCacheCommand implements Command {
	@Override
	public void handle(Message message, String[] args) {
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
