package com.projetofinal.backend.client.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class I18n {

	public static final Locale LOCALE_PT = new Locale("pt", "BR");
	public static final Locale LOCALE_EN = Locale.US;

	private static Locale currentLocale = LOCALE_PT;
	private static ResourceBundle bundle;
	private static final List<Runnable> listeners = new ArrayList<>();

	static {
		loadBundle();
	}

	private I18n() {}

	private static void loadBundle() {
		try {
			bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
		} catch (MissingResourceException ex) {
			bundle = null;
		}
	}

	public static synchronized void setLocale(Locale locale) {
		if (locale == null || locale.equals(currentLocale)) {
			return;
		}
		currentLocale = locale;
		loadBundle();
		notifyListeners();
	}

	public static synchronized Locale getLocale() {
		return currentLocale;
	}

	public static synchronized void addListener(Runnable listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public static synchronized void removeListener(Runnable listener) {
		listeners.remove(listener);
	}

	private static synchronized void notifyListeners() {
		for (Runnable listener : new ArrayList<>(listeners)) {
			try {
				listener.run();
			} catch (Exception ignored) {
			}
		}
	}

	public static String get(String key) {
		if (bundle == null) {
			return key;
		}
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public static String get(String key, Object... args) {
		String pattern = get(key);
		if (args == null || args.length == 0) {
			return pattern;
		}
		try {
			return MessageFormat.format(pattern, args);
		} catch (IllegalArgumentException e) {
			return pattern;
		}
	}
}
