package com.dk.csvdiff.i18n;

import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Namespace for internationalization functions
 * 
 * @author darrenkennedy
 */
public class Messages {
	// Prevent instance creation
	private Messages() {}
	
	private static ResourceBundle resources = ResourceBundle.getBundle("com.dk.csvdiff.i18n.Messages");
	
	public static void write(Consumer<String> c, String message, Object... args) {
		c.accept(getMessage(message, args));
	}
	
	public static String getMessage(String message, Object... args) {
		return String.format(resources.getString(message), args);
	}
}
