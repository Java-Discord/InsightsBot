package net.javadiscord.model;

import lombok.Getter;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This cache stores and manages access to live information about guilds. It
 * contains an internal hashmap that stores a {@link GuildData} object for each
 * guild that an event has been observed in, since the last scheduled cache
 * flush.
 */
public class GuildsCache {
	private final Map<Long, GuildData> guildData;
	@Getter
	private ZonedDateTime lastClearedAt;

	/**
	 * Constructs a new cache with a concurrent hash map implementation and the
	 * current time taken as the time the cache was last cleared.
	 */
	public GuildsCache() {
		this.guildData = new ConcurrentHashMap<>();
		this.lastClearedAt = ZonedDateTime.now();
	}

	/**
	 * Gets the cached data for a guild, creating it if it doesn't exist yet.
	 * @param guildId The id of the guild to get cached data for.
	 * @return The cached data object for the guild.
	 */
	public GuildData get(long guildId) {
		return this.guildData.computeIfAbsent(guildId, gId -> new GuildData());
	}

	/**
	 * Convenience method to get cached guild data given a guild event.
	 * @param guildEvent The event from the guild to get cached data for.
	 * @return The cached data object for the guild.
	 */
	public GuildData get(GenericGuildEvent guildEvent) {
		return this.get(guildEvent.getGuild().getIdLong());
	}

	/**
	 * Checks if cached data exists for the given guild.
	 * @param guildId The guild to check for.
	 * @return True if there is cached data for that guild, or false otherwise.
	 */
	public boolean exists(long guildId) {
		return this.guildData.containsKey(guildId);
	}

	/**
	 * Clears all cached data for all guilds, and resets the internal timestamp
	 * that records the time at which data was last cleared.
	 */
	public void clear() {
		this.guildData.clear();
		this.lastClearedAt = ZonedDateTime.now();
	}

	/**
	 * @return A set of all entries from the internal map.
	 */
	public Set<Map.Entry<Long, GuildData>> getAll() {
		return new HashSet<>(this.guildData.entrySet());
	}

	/**
	 * @return True if this cache is empty, meaning there is no data for any
	 * guild cached yet, or false otherwise.
	 */
	public boolean isEmpty() {
		return this.guildData.isEmpty();
	}
}
