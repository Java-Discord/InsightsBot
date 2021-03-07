package net.javadiscord.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlHelper {
	public static String load(String resourceName) {
		InputStream is = SqlHelper.class.getClassLoader().getResourceAsStream(resourceName);
		if (is == null) {
			return null;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			return br.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			return null;
		}
	}

	public static PreparedStatement loadMulti(Connection connection, String resourceName, String... params) throws SQLException {
		String sql = Objects.requireNonNull(load(resourceName));
		for (String param : params) {
			sql = sql.replaceFirst("\\?", param);
		}
		String[] statements = sql.split(";");
		for (int i = 0; i < statements.length - 1; i++) {
			connection.prepareStatement(statements[i]).execute();
		}
		return connection.prepareStatement(statements[statements.length - 1]);
	}

	public static String wrapStr(String s) {
		return "'" + s + "'";
	}
}
