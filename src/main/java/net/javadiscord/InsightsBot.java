package net.javadiscord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point.
 */
@SpringBootApplication
public class InsightsBot {
	public static final String PREFIX = "!ib";
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
}
