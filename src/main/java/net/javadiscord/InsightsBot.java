package net.javadiscord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point.
 */
@SpringBootApplication
@EnableScheduling
public class InsightsBot {
	public static final String PREFIX = "!ib";
	public static final long STARTED_AT = System.currentTimeMillis();

	/**
	 * Starts the application.
	 * <p>
	 *     Note that it is important that we call {@link SpringApplication#run}
	 *     before initializing the bot, because Spring will automatically
	 *     configure the system's logging framework, among other things.
	 * </p>
	 * <p>
	 *     Upon startup, Spring will run the {@link BotInitializer}, which does
	 *     all the necessary startup tasks, like registering commands and
	 *     starting the Discord bot client.
	 * </p>
	 * @param args Command-line arguments. Should contain the bot's client token
	 *             or an exception will be thrown and the application stops.
	 */
	public static void main(String[] args) {
		SpringApplication.run(InsightsBot.class, args);
	}
}
