package net.javadiscord.data;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Encapsulates the creation and configuration of the bot's data source.
 */
public class DataSourceProvider {
	public DataSource getDataSource() throws SQLException {
		MariaDbDataSource ds = new MariaDbDataSource(System.getenv("INSIGHTS_BOT_DB_URL"));
		ds.setUserName(System.getenv("INSIGHTS_BOT_DB_USER"));
		ds.setPassword(System.getenv("INSIGHTS_BOT_DB_PASS"));
		return ds;
	}
}
