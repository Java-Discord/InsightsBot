package net.javadiscord;

import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.command.CommandHandler;
import net.javadiscord.data.dao.GuildEventRepository;
import net.javadiscord.data.model.events.MembershipEvent;
import net.javadiscord.data.model.events.MessageEvent;
import net.javadiscord.data.model.events.ReactionEvent;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuildEventRecorderService extends ReactiveEventAdapter {
	public static final String PREFIX = "!ib";

	private final GuildEventRepository guildEventRepository;
	private final CommandHandler commandHandler;

	@Override
	public Publisher<?> onMessageCreate(MessageCreateEvent event) {
		if (event.getGuildId().isPresent() && event.getMember().isPresent()) {
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
		if (event.getGuildId().isPresent()) {
			return Mono.just(this.guildEventRepository.save(new MessageEvent(event)));
		}
		return Mono.empty();
	}

	@Override
	public Publisher<?> onReactionAdd(ReactionAddEvent event) {
		if (event.getGuildId().isPresent()) {
			return Mono.just(this.guildEventRepository.save(new ReactionEvent(event)));
		}
		return Mono.empty();
	}

	@Override
	public Publisher<?> onReactionRemove(ReactionRemoveEvent event) {
		if (event.getGuildId().isPresent()) {
			return Mono.just(this.guildEventRepository.save(new ReactionEvent(event)));
		}
		return Mono.empty();
	}

	@Override
	public Publisher<?> onMemberJoin(MemberJoinEvent event) {
		return Mono.just(this.guildEventRepository.save(new MembershipEvent(event)));
	}

	@Override
	public Publisher<?> onMemberLeave(MemberLeaveEvent event) {
		return Mono.just(this.guildEventRepository.save(new MembershipEvent(event)));
	}
}
