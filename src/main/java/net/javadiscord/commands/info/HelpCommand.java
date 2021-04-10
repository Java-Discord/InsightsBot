package net.javadiscord.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.javadiscord.InsightsBot;
import net.javadiscord.commands.Command;

public class HelpCommand implements Command {
	@Override
	public void handle(Message message, String[] args) {
		message.reply(new EmbedBuilder()
				.setTitle("Insights Bot - Help")
				.setColor(InsightsBot.EMBED_COLOR)
				.setDescription("The following are a list of commands you may use.")
				.addField("help", "Shows this message.", false)
				.addField("status", "Shows the status of this bot.", false)
				.build()
		).queue();
	}
}
