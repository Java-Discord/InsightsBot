package net.javadiscord.util;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class that provides methods for standardizing bot responses.
 */
public class Messages {
	private static final Color EMBED_COLOR = Color.of(47, 49, 54);
	private static final String SUCCESS_EMOJI = "✅";
	private static final String ERROR_EMOJI = "❌";

	public static Publisher<?> respond(MessageCreateEvent msgEvent, Function<MessageChannel,Mono<Message>> transformer) {
		return respond(msgEvent, transformer, SUCCESS_EMOJI);
	}

	private static Publisher<?> respond(MessageCreateEvent msgEvent, Function<MessageChannel,Mono<Message>> transformer, String emoji) {
		return msgEvent.getMessage()
				.getChannel().flatMap(transformer)
				.then(msgEvent.getMessage().addReaction(ReactionEmoji.unicode(emoji)));
	}

	public static Publisher<?> respondWithEmbed(MessageCreateEvent msgEvent, Consumer<EmbedCreateSpec> specConsumer) {
		return respond(msgEvent, c -> c.createEmbed(spec -> {
			spec.setColor(EMBED_COLOR);
			specConsumer.accept(spec);
		}));
	}

	public static Publisher<?> respondWithImage(MessageCreateEvent msgEvent, BufferedImage image, String format, String name) {
		return respond(msgEvent, c -> c.createMessage(spec -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, format, os);
				spec.addFile(name, new ByteArrayInputStream(os.toByteArray()));
			} catch (IOException e) {
				e.printStackTrace();
				spec.setContent("Could not write image.");
			}
		}));
	}

	public static Publisher<?> respondWithFile(MessageCreateEvent msgEvent, ByteArrayOutputStream out, String name) {
		return respond(msgEvent, c -> c.createMessage(spec -> {
			spec.addFile(name, new ByteArrayInputStream(out.toByteArray()));
		}));
	}

	public static Publisher<?> warn(MessageCreateEvent msgEvent, String warningMessage) {
		return respond(msgEvent, c -> c.createEmbed(spec -> {
			spec.setColor(EMBED_COLOR);
			spec.setDescription(warningMessage);
		}), ERROR_EMOJI);
	}
}
