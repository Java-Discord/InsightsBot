package net.javadiscord.command;

import discord4j.common.util.Snowflake;

import java.util.HashSet;
import java.util.Set;

/**
 * An admin command is only allowed to be run by those who are in the ADMIN_IDS
 * set.
 */
public abstract class AdminCommand implements Command {
	public static final Set<Long> ADMIN_IDS = new HashSet<>();

	@Override
	public Set<Long> getWhitelistedUserIds() {
		return ADMIN_IDS;
	}

	public static boolean isAdmin(long id) {
		return ADMIN_IDS.contains(id);
	}
}
