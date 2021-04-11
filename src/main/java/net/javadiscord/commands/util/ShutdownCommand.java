package net.javadiscord.commands.util;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.javadiscord.InsightsBot;
import net.javadiscord.commands.Command;

@Slf4j
public class ShutdownCommand implements Command {
	@Override
	public void handle(Message message, String[] args) {
		log.info("Shutting down due to shutdown command issued by {}.", message.getAuthor().getAsTag());
		message.reply("Shutting down now.").complete();
		InsightsBot.get().getJda().shutdownNow();
		InsightsBot.get().getJobManager().shutdown();
	}
}
