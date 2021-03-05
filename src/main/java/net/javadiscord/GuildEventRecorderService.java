package net.javadiscord;

import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.data.dao.GuildEventRepository;
import net.javadiscord.data.model.GuildEvent;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuildEventRecorderService extends ReactiveEventAdapter {
	private final GuildEventRepository guildEventRepository;

	@Override
	public Publisher<?> onReactionAdd(ReactionAddEvent event) {
		log.info("Reaction added.");
		Long guildId = (event.getGuildId().isPresent()) ? event.getGuildId().get().asLong() : null;
		Long userId = event.getUserId().asLong();
		Long channelId = event.getChannelId().asLong();
		this.guildEventRepository.save(new GuildEvent(
				guildId,
				userId,
				channelId
		));
		return Mono.empty();
	}

	@Override
	public Publisher<?> onReactionRemove(ReactionRemoveEvent event) {
		log.info("Reaction removed.");
		return Mono.empty();
	}
}
