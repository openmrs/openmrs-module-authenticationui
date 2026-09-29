package org.openmrs.module.authenticationui;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessagesTest {

	private static final Pattern ARGUMENT = Pattern.compile("\\{(\\d+)}");

	/**
	 * Unescaped single quotes in a message with arguments cause MessageFormat to drop the quote and
	 * render the argument placeholder literally.  Ensure every argument placeholder is substituted.
	 */
	@Test
	public void shouldSubstituteAllArgumentsInEveryLocale() throws Exception {
		File[] files = new File("src/main/resources").listFiles((dir, name) -> name.matches("messages.*\\.properties"));
		assertTrue(files != null && files.length > 0);
		List<String> failures = new ArrayList<>();
		for (File file : files) {
			Properties properties = new Properties();
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				properties.load(reader);
			}
			for (String key : properties.stringPropertyNames()) {
				String message = properties.getProperty(key);
				Matcher matcher = ARGUMENT.matcher(message);
				int numArgs = 0;
				while (matcher.find()) {
					numArgs = Math.max(numArgs, Integer.parseInt(matcher.group(1)) + 1);
				}
				if (numArgs > 0) {
					Object[] args = new Object[numArgs];
					for (int i = 0; i < numArgs; i++) {
						args[i] = "ARG" + i;
					}
					String formatted = MessageFormat.format(message, args);
					for (int i = 0; i < numArgs; i++) {
						if (!formatted.contains("ARG" + i)) {
							failures.add(file.getName() + ": " + key + " -> " + formatted);
						}
					}
				}
			}
		}
		assertTrue(failures.isEmpty(), "Messages with unsubstituted arguments: " + failures);
	}
}
