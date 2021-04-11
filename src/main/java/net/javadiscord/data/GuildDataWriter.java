package net.javadiscord.data;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.javadiscord.InsightsBot;
import net.javadiscord.model.GuildData;
import net.javadiscord.util.SqlHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * This writer is responsible for saving guild data to the database. A new
 * writer instance should be obtained for each isolated action to be performed,
 * as the writer may modify the connection it uses for performance purposes.
 */
@Slf4j
public class GuildDataWriter implements AutoCloseable {
	private final Connection connection;

	public GuildDataWriter(Connection connection) {
		this.connection = connection;
	}

	public GuildDataWriter() throws SQLException {
		this(InsightsBot.get().getDataSource().getConnection());
	}

	/**
	 * Saves all data for all guilds in the given set. All data is saved in a
	 * single transaction, so if an error occurs, all operations are rolled back.
	 * @param entries The set of guildId->guildData entries to save.
	 * @throws SQLException If an error occurs when saving.
	 */
	public void saveAllGuildData(Set<Map.Entry<Long, GuildData>> entries) throws SQLException {
		this.connection.setAutoCommit(false);
		try {
			for (var entry : entries) {
				Guild guild = InsightsBot.get().getJda().getGuildById(entry.getKey());
				if (guild != null) {
					entry.getValue().setMemberCount(guild.getMemberCount());
					this.saveGuildData(entry.getKey(), entry.getValue());
				} else {
					log.warn("Did not save data for unknown guild {}.", entry.getKey());
				}
			}
			this.connection.commit();
		} catch (SQLException e) {
			this.connection.rollback();
			throw e;
		}
	}

	/**
	 * Saves guild data to the database.
	 * @param guildId The id of the guild that data is being saved for.
	 * @param data The data container object.
	 * @throws SQLException If an error occurs when saving.
	 */
	public void saveGuildData(long guildId, GuildData data) throws SQLException {
		this.deleteStaleGuildData(guildId, data.getDate());
		PreparedStatement dataInsert = connection.prepareStatement(
				SqlHelper.load("sql/update/insert_guild_data.sql"),
				Statement.RETURN_GENERATED_KEYS
		);
		dataInsert.setDate(1, Date.valueOf(data.getDate()));
		dataInsert.setLong(2, guildId);
		dataInsert.setInt(3, data.getMessagesCreated());
		dataInsert.setInt(4, data.getMessagesUpdated());
		dataInsert.setInt(5, data.getMessagesRemoved());
		dataInsert.setInt(6, data.getReactionsAdded());
		dataInsert.setInt(7, data.getReactionsRemoved());
		dataInsert.setInt(8, data.getMembersJoined());
		dataInsert.setInt(9, data.getMembersLeft());
		dataInsert.setInt(10, data.getMembersBanned());
		dataInsert.setInt(11, data.getMembersUnbanned());
		dataInsert.setInt(12, data.getMemberCount());
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
				SqlHelper.load("sql/update/insert_guild_data_user_message_counts.sql")
		);
		for (Map.Entry<Long, Integer> userEntry : data.getUserMessageCounts().entrySet()) {
			stmt.setLong(1, dataId);
			stmt.setLong(2, userEntry.getKey());
			stmt.setInt(3, userEntry.getValue());
			stmt.executeUpdate();
		}
	}

	private void writeEmojiCounts(GuildData data, long dataId) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(SqlHelper.load("sql/update/insert_guild_data_reaction_counts.sql"));
		for (Map.Entry<String, Integer> entry : data.getEmojiCounts().entrySet()) {
			stmt.setLong(1, dataId);
			stmt.setString(2, entry.getKey());
			stmt.setInt(3, entry.getValue());
			stmt.executeUpdate();
		}
	}

	private void deleteStaleGuildData(long guildId, LocalDate date) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(SqlHelper.load("sql/update/delete_stale_guild_data.sql"));
		stmt.setLong(1, guildId);
		stmt.setDate(2, Date.valueOf(date));
		stmt.executeUpdate();
	}

	@Override
	public void close() throws SQLException {
		this.connection.close();
	}
}
