package net.javadiscord.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SqlHelper {
	public static String load(String resourceName) {
		InputStream is = SqlHelper.class.getClassLoader().getResourceAsStream(resourceName);
		if (is == null) {
			return null;
		}
		try {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			return null;
		}
	}
}
