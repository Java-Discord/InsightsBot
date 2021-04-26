package net.javadiscord.data;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Encapsulates the creation and configuration of the bot's data source.
 */
public class DataSourceProvider {
	/**
	 * Gets the data source. This users a simple data source that is newly
	 * initialized for each invocation, so be advised that the returned data
	 * source should be used as long as possible before closing.
	 * @return The data source.
	 * @throws SQLException If the data source could not be obtained.
	 */
	public DataSource getDataSource() throws SQLException {
		String url = System.getenv("INSIGHTS_BOT_DB_URL") + "?useUnicode=yes&characterEncoding=UTF-8";
		MariaDbDataSource ds = new MariaDbDataSource(url);
		ds.setUserName(System.getenv("INSIGHTS_BOT_DB_USER"));
		ds.setPassword(System.getenv("INSIGHTS_BOT_DB_PASS"));
		return ds;
	}
}
