package net.javadiscord.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
}
