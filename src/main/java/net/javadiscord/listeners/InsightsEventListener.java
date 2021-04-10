package net.javadiscord.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.javadiscord.InsightsBot;
import net.javadiscord.model.GuildData;
import org.jetbrains.annotations.NotNull;

/**
 * The main listener that collects incoming statistical data.
 */
public class InsightsEventListener extends ListenerAdapter {
	private final InsightsBot bot;

	public InsightsEventListener(InsightsBot bot) {
		this.bot = bot;
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		if (event.getAuthor().getIdLong() == bot.getSelfId() || event.isWebhookMessage() || event.getAuthor().isBot()) {
			return;
		}
		if (bot.getCommandHandler().isCommand(event.getMessage())) {
			bot.getCommandHandler().handle(event.getMessage());
			return;
		}
		GuildData data = bot.getGuildsCache().get(event);
		data.incrementMessagesCreated();
		data.incrementUserMessageCount(event.getAuthor().getIdLong());
	}

	@Override
	public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
		if (event.getAuthor().getIdLong() == bot.getSelfId() || event.getAuthor().isBot()) {
			return;
		}
		bot.getGuildsCache().get(event).incrementMessagesUpdated();
	}

	@Override
	public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
		bot.getGuildsCache().get(event).incrementMessagesRemoved();
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
		if (event.getUser().getIdLong() == bot.getSelfId() || event.getUser().isBot()) {
			return;
		}
		bot.getGuildsCache().get(event).incrementReactionsAdded();
	}

	@Override
	public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
		User user = event.retrieveUser().complete();
		if (user.getIdLong() == bot.getSelfId() || user.isBot()) {
			return;
		}
		bot.getGuildsCache().get(event).incrementReactionsRemoved();
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		if (event.getUser().getIdLong() == bot.getSelfId() || event.getUser().isBot()) {
			return;
		}
		bot.getGuildsCache().get(event).incrementMembersJoined();
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
		if (event.getUser().getIdLong() == bot.getSelfId() || event.getUser().isBot()) {
			return;
		}
		bot.getGuildsCache().get(event).incrementMembersLeft();
	}

	@Override
	public void onGuildBan(@NotNull GuildBanEvent event) {
		if (event.getUser().isBot()) {
			return;
		}
		bot.getGuildsCache().get(event).incrementMembersBanned();
	}

	@Override
	public void onGuildUnban(@NotNull GuildUnbanEvent event) {
		if (event.getUser().isBot()) {
			return;
		}
		bot.getGuildsCache().get(event).incrementMembersUnbanned();
	}
}
