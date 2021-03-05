package net.javadiscord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.object.entity.Guild;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.util.stream.Collectors;

/**
 * Main application entry point.
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class InsightsBot implements CommandLineRunner {
	private final GuildEventRecorderService recorderService;

	/**
	 * Starts the application.
	 * <p>
	 *     Note that it is important that we call {@link SpringApplication#run}
	 *     before initializing the bot, because Spring will automatically
	 *     configure the system's logging framework, among other things.
	 * </p>
	 * @param args Command-line arguments. Should contain the bot's client token
	 *             or an exception will be thrown and the application stops.
	 */
	public static void main(String[] args) {
		SpringApplication.run(InsightsBot.class, args);
	}

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
		client.getSelf().subscribe(user -> log.info("Client obtained: {}#{}", user.getUsername(), user.getDiscriminator()));
		client.getGuilds().buffer().subscribe(guilds -> log.info("Active in the following guilds: {}", guilds.stream().map(Guild::getName).collect(Collectors.toList())));
		client.getEventDispatcher().on(this.recorderService).subscribe();
		client.onDisconnect().block();
	}
}
