package net.javadiscord.model;

import lombok.Getter;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This cache stores and manages access to live information about guilds.
 */
public class GuildsCache {
	private final Map<Long, GuildData> guildData;
	@Getter
	private ZonedDateTime lastClearedAt;

	public GuildsCache() {
		this.guildData = new ConcurrentHashMap<>();
		this.lastClearedAt = ZonedDateTime.now();
	}

	public GuildData get(long guildId) {
		if (!this.guildData.containsKey(guildId)) {
			this.guildData.put(guildId, new GuildData());
		}
		return this.guildData.get(guildId);
	}

	public GuildData get(GenericGuildEvent guildEvent) {
		return this.get(guildEvent.getGuild().getIdLong());
	}

	public void clear() {
		this.guildData.clear();
		this.lastClearedAt = ZonedDateTime.now();
	}

	public Set<Map.Entry<Long, GuildData>> getAll() {
		return this.guildData.entrySet();
	}
}
