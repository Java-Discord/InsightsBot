package net.javadiscord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.object.entity.Guild;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.stream.Collectors;

/**
 * Simple service that starts up the Discord Bot using a token provided as the
 * first command line argument.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BotInitializer implements CommandLineRunner {
	private final GuildEventRecorderService recorderService;

	@Override
	public void run(String... args) {
		if (args.length < 1 || args[0].isBlank()) throw new IllegalArgumentException("Missing client token argument.");
		this.initializeBot(args[0]);
	}

	/**
	 * Starts the Discord bot using the given token, and begins the process of
	 * registering all event listeners.
	 * @param token The client token to use to create the bot.
	 */
	private void initializeBot(String token) {
		final var client = DiscordClientBuilder.create(token).build()
				.login()
				.block(Duration.ofSeconds(5));
		if (client == null) {
			throw new RuntimeException("Could not build client and login.");
		}
		client.getSelf().subscribe(user -> log.info(
				"Discord bot client obtained: {}#{}",
				user.getUsername(),
				user.getDiscriminator()
		));
		client.getGuilds().buffer().subscribe(guilds -> log.info(
				"Active in the following guilds: {}",
				guilds.stream().map(Guild::getName).sorted().collect(Collectors.toList()))
		);
		client.getEventDispatcher().on(this.recorderService).subscribe();
		client.onDisconnect().block();
	}
}
