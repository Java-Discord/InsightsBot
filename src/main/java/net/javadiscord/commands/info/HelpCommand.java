package net.javadiscord.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
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
				.addField(this.formatCommand("help", message), "Shows this message.", false)
				.addField(this.formatCommand("status", message), "Shows the status of this bot.", false)
				.addField(this.formatCommand("cache [flush]", message), "View cached data for this guild, or flush it to the database, if **flush** argument is given.", false)
				.addField(this.formatCommand("graph <list|messages|active-users|members> [start] [end]", message), "Generate graphs for saved data. Optional start and end can be specified as number of days before today.", false)
				.build()
		).queue();
	}

	private String formatCommand(String command, Message message) {
		return '`' + InsightsBot.get().getCommandHandler().getPrefixForGuild(message.getGuild()) + ' ' + command + '`';
	}
}
