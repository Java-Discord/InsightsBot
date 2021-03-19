package net.javadiscord.data;

import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.guild.BanEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.guild.UnbanEvent;
import discord4j.core.event.domain.message.*;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.command.CommandHandler;
import net.javadiscord.data.dao.GuildEventRepository;
import net.javadiscord.data.model.events.MembershipEvent;
import net.javadiscord.data.model.events.MessageEvent;
import net.javadiscord.data.model.events.ReactionEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static net.javadiscord.InsightsBot.PREFIX;

/**
 * Service that is responsible for recording data about events, and is the main
 * event handler for the Discord bot.
 */
@Slf4j
public class GuildEventRecorderService extends ReactiveEventAdapter {
	private final GuildEventRepository guildEventRepository;
	private final CommandHandler commandHandler;

	public GuildEventRecorderService(GuildEventRepository guildEventRepository, CommandHandler commandHandler) {
		this.guildEventRepository = guildEventRepository;
		this.commandHandler = commandHandler;
	}

	// ---- MESSAGE EVENTS ----
	@Override
	public Publisher<?> onMessageCreate(MessageCreateEvent event) {
		if (// Skip any messages sent by the bot itself.
				event.getMessage().getAuthor().isPresent()
				&& event.getMessage().getAuthor().get().getId().equals(event.getClient().getSelfId())
		) {
			return Mono.empty();
		}
		if (event.getGuildId().isPresent() && event.getMember().isPresent()) {
			// Check if a command for the bot itself was issued, and process that instead.
			if (event.getMessage().getContent().toLowerCase().startsWith(PREFIX)) {
				return this.commandHandler.handle(event);
			}
			return Mono.just(this.guildEventRepository.save(new MessageEvent(event)));
		}
		return Mono.empty();
	}

	@Override
	public Publisher<?> onMessageUpdate(MessageUpdateEvent event) {
		if (event.getGuildId().isPresent()) {
			return Mono.just(this.guildEventRepository.save(new MessageEvent(event)));
		}
		return Mono.empty();
	}

	@Override
	public Publisher<?> onMessageDelete(MessageDeleteEvent event) {
		boolean isMe = event.getMessage().isPresent() && event.getMessage().get().getAuthor().isPresent()
				&& event.getMessage().get().getAuthor().get().getId().equals(event.getClient().getSelfId());
		if (event.getGuildId().isPresent() && !isMe) {
			return Mono.just(this.guildEventRepository.save(new MessageEvent(event)));
		}
		return Mono.empty();
	}

	@Override
	public Publisher<?> onReactionAdd(ReactionAddEvent event) {
		if (event.getGuildId().isPresent() && !event.getUserId().equals(event.getClient().getSelfId())) {
			return Mono.just(this.guildEventRepository.save(new ReactionEvent(event)));
		}
		return Mono.empty();
	}

	@Override
	public Publisher<?> onReactionRemove(ReactionRemoveEvent event) {
		if (event.getGuildId().isPresent() && !event.getUserId().equals(event.getClient().getSelfId())) {
			return Mono.just(this.guildEventRepository.save(new ReactionEvent(event)));
		}
		return Mono.empty();
	}

	// ---- MEMBERSHIP EVENTS
	@Override
	public Publisher<?> onMemberJoin(MemberJoinEvent event) {
		return Mono.just(this.guildEventRepository.save(new MembershipEvent(event)));
	}

	@Override
	public Publisher<?> onMemberLeave(MemberLeaveEvent event) {
		return Mono.just(this.guildEventRepository.save(new MembershipEvent(event)));
	}

	@Override
	public Publisher<?> onBan(BanEvent event) {
		return Mono.just(this.guildEventRepository.save(new MembershipEvent(event)));
	}

	@Override
	public Publisher<?> onUnban(UnbanEvent event) {
		return Mono.just(this.guildEventRepository.save(new MembershipEvent(event)));
	}
}
