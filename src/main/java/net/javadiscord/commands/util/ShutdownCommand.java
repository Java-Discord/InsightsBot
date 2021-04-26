package net.javadiscord.commands.util;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.javadiscord.InsightsBot;
import net.javadiscord.commands.Command;
import net.javadiscord.util.AdminChecker;

/**
 * Special command to shut down the bot. Only admins may use it.
 */
@Slf4j
public class ShutdownCommand implements Command {
	@Override
	public void handle(Message message, String[] args) {
		if (!AdminChecker.isAuthorAdmin(message)) {
			message.reply("You are not authorized to use this command.").queue();
			return;
		}
		log.info("Shutting down due to shutdown command issued by {}.", message.getAuthor().getAsTag());
		message.reply("Shutting down now.").complete();
		InsightsBot.get().getJda().shutdownNow();
		InsightsBot.get().getJobManager().shutdown();
	}
}
