package net.javadiscord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.command.CommandRegistry;
import net.javadiscord.command.HelpCommand;
import net.javadiscord.command.analytics.GetDailyAggregate;
import net.javadiscord.command.debug.CustomQueryCommand;
import net.javadiscord.command.analytics.JoinCountCommand;
import net.javadiscord.command.analytics.MessageCountCommand;
import net.javadiscord.command.debug.Tasks;
import net.javadiscord.command.debug.Uptime;
import net.javadiscord.data.GuildEventRecorderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

import static net.javadiscord.command.AdminCommand.ADMIN_IDS;

/**
 * Simple service that starts up the Discord Bot using a token provided as the
 * first command line argument.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BotInitializer implements CommandLineRunner {
	private final GuildEventRecorderService recorderService;
	private final CommandRegistry commandRegistry;

	// Autowired commands (which require persistence components)
	private final MessageCountCommand messageCountCommand;
	private final JoinCountCommand joinCountCommand;
	private final CustomQueryCommand customQueryCommand;
	private final Tasks tasksCommand;
	private final GetDailyAggregate getDailyAggregateCommand;

	@Override
	public void run(String... args) {
		String token = System.getenv("INSIGHTS_BOT_TOKEN");
		if (token == null || token.trim().isEmpty()) throw new IllegalArgumentException("Missing client token argument.");
		ADMIN_IDS.addAll(Arrays.stream(System.getenv("INSIGHTS_BOT_ADMINS").split("\\s*,\\s*")).map(Long::parseLong).collect(Collectors.toSet()));
		if (!ADMIN_IDS.isEmpty()) {
			log.info("Started with admin ids: {}", ADMIN_IDS.toString());
		}
		this.initializeCommands();
		log.info("Initialized commands.");
		this.initializeBot(token);
	}

	/**
	 * Initializes the various commands that can be executed.
	 */
	private void initializeCommands() {
		this.commandRegistry.register("help", new HelpCommand(this.commandRegistry), "Shows a list of commands.");
		// Debug commands.
		this.commandRegistry.register("uptime", new Uptime(), "Gets the duration that this bot has been online for.");
		this.commandRegistry.register("tasks", this.tasksCommand, "Gets a list of scheduled tasks the bot has planned.");
		this.commandRegistry.register("customQuery", this.customQueryCommand, "Executes a custom SQL query on the database.");

		// User-facing commands.
		this.commandRegistry.register("messageCount", this.messageCountCommand, "Gets the number of messages sent in a time interval.");
		this.commandRegistry.register("joinCount", this.joinCountCommand, "Gets the number of members that have joined in a time interval.");
		this.commandRegistry.register("getDailyAggregate", this.getDailyAggregateCommand, "Get aggregate data for a day.");

	}

	/**
	 * Starts the Discord bot using the given token, and begins the process of
	 * registering all event listeners.
	 * @param token The client token to use to create the bot.
	 */
	private void initializeBot(String token) {
		final GatewayDiscordClient client = DiscordClientBuilder.create(token).build()
				.login()
				.block(Duration.ofSeconds(60));
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
