package net.javadiscord.util;

import discord4j.core.GatewayDiscordClient;
import lombok.Getter;
import lombok.Setter;

/**
 * Simple container component that holds the Discord client.
 */
public class DiscordClientHolder {
	@Getter
	private static final DiscordClientHolder instance = new DiscordClientHolder();

	@Setter
	private GatewayDiscordClient client;

	public GatewayDiscordClient getClient() {
		if (this.client == null) {
			throw new IllegalStateException("Client not yet initialized.");
		}
		return this.client;
	}
}
