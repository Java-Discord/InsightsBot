package net.javadiscord.util;

import net.dv8tion.jda.api.entities.Message;

public class AdminChecker {
	public static boolean isAuthorAdmin(Message message) {
		return System.getenv("INSIGHTS_BOT_ADMINS").contains(message.getAuthor().getId());
	}
}
