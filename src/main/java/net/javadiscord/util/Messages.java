package net.javadiscord.util;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public class Messages {

	public static Publisher<?> respond(MessageCreateEvent msgEvent, Function<MessageChannel,Mono<Message>> transformer) {
		return respond(msgEvent, transformer, "✅");
	}

	private static Publisher<?> respond(MessageCreateEvent msgEvent, Function<MessageChannel,Mono<Message>> transformer, String emoji) {
		return msgEvent.getMessage()
				.getChannel().flatMap(transformer)
				.then(msgEvent.getMessage().addReaction(ReactionEmoji.unicode(emoji)));
	}

	public static Publisher<?> respondWithEmbed(MessageCreateEvent msgEvent, Consumer<EmbedCreateSpec> specConsumer) {
		return respond(msgEvent, c -> c.createEmbed(specConsumer));
	}

	public static Publisher<?> warn(MessageCreateEvent msgEvent, String warningMessage) {
		return respond(msgEvent, c -> c.createEmbed(spec -> {
			spec.setDescription(warningMessage);
		}), "❌");
	}
}
