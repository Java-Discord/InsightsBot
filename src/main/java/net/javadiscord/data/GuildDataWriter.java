package net.javadiscord.data;

import net.javadiscord.model.GuildData;
import net.javadiscord.util.SqlHelper;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class GuildDataWriter {
	private final Connection connection;

	public GuildDataWriter(Connection connection) {
		this.connection = connection;
	}

	public void saveGuildData(long guildId, GuildData data, ZonedDateTime start, ZonedDateTime end) throws SQLException {
		this.deleteStaleGuildData(guildId, start, end);
		PreparedStatement dataInsert = connection.prepareStatement(
				SqlHelper.load("sql/insert_guild_data.sql"),
				Statement.RETURN_GENERATED_KEYS
		);
		dataInsert.setTimestamp(1, Timestamp.from(start.toInstant().truncatedTo(ChronoUnit.SECONDS)));
		dataInsert.setTimestamp(2, Timestamp.from(end.toInstant().truncatedTo(ChronoUnit.SECONDS)));
		dataInsert.setLong(3, guildId);
		dataInsert.setInt(4, data.getMessagesCreated());
		dataInsert.setInt(5, data.getMessagesUpdated());
		dataInsert.setInt(6, data.getMessagesRemoved());
		dataInsert.setInt(7, data.getReactionsAdded());
		dataInsert.setInt(8, data.getReactionsRemoved());
		dataInsert.setInt(9, data.getMembersJoined());
		dataInsert.setInt(10, data.getMembersLeft());
		dataInsert.setInt(11, data.getMembersBanned());
		dataInsert.setInt(12, data.getMembersUnbanned());
		dataInsert.executeUpdate();
		ResultSet generatedKeys = dataInsert.getGeneratedKeys();
		if (!generatedKeys.next()) {
			throw new SQLException("Inserting data failed; no key generated.");
		}
		long dataId = generatedKeys.getLong(1);
		this.writeUserMessageCounts(data, dataId);
		this.writeEmojiCounts(data, dataId);
	}

	private void writeUserMessageCounts(GuildData data, long dataId) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(
				SqlHelper.load("sql/insert_guild_data_user_message_counts.sql")
		);
		for (Map.Entry<Long, Integer> userEntry : data.getUserMessageCounts().entrySet()) {
			stmt.setLong(1, dataId);
			stmt.setLong(2, userEntry.getKey());
			stmt.setInt(3, userEntry.getValue());
			stmt.executeUpdate();
		}
	}

	private void writeEmojiCounts(GuildData data, long dataId) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(SqlHelper.load("sql/insert_guild_data_reaction_counts.sql"));
		for (Map.Entry<String, Integer> entry : data.getEmojiCounts().entrySet()) {
			stmt.setLong(1, dataId);
			stmt.setString(2, entry.getKey());
			stmt.setInt(3, entry.getValue());
			stmt.executeUpdate();
		}
	}

	private void deleteStaleGuildData(long guildId, ZonedDateTime start, ZonedDateTime end) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(SqlHelper.load("sql/delete_stale_guild_data.sql"));
		stmt.setLong(1, guildId);
		stmt.setTimestamp(2, Timestamp.from(start.toInstant().truncatedTo(ChronoUnit.SECONDS)));
		stmt.setTimestamp(3, Timestamp.from(end.toInstant().truncatedTo(ChronoUnit.SECONDS)));
		stmt.executeUpdate();
		connection.commit();
	}
}
