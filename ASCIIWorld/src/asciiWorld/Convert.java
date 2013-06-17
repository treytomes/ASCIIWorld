package asciiWorld;

import org.newdawn.slick.Color;

public class Convert {
	public static <T> T changeType(Object obj, Class<T> type) throws Exception {
		String value = obj.toString();
		if (type.isAssignableFrom(String.class)) {
			return type.cast(value);
		} else if (type.isAssignableFrom(Integer.class)) {
			return type.cast(Integer.parseInt(value));
		} else if (type.isAssignableFrom(Float.class)) {
			return type.cast(Float.parseFloat(value));
		} else if (type.isAssignableFrom(Boolean.class)) {
			return type.cast(Boolean.parseBoolean(value));
		} else if (type.isAssignableFrom(Color.class)) {
			return type.cast(stringToColor(value));
		}
		throw new Exception(String.format("I do not understand this type: %s", type.getName()));
	}
	
	public static String colorToString(final Color color) {
		return String.format("%s%s%s%s",
				Integer.toHexString(color.getAlpha()),
				Integer.toHexString(color.getRed()),
				Integer.toHexString(color.getGreen()),
				Integer.toHexString(color.getBlue()));
	}
	
	public static Color stringToColor(final String text) {
		return new Color(
				Integer.parseInt(text.substring(2, 4), 16),
				Integer.parseInt(text.substring(4, 6), 16),
				Integer.parseInt(text.substring(6, 8), 16),
				Integer.parseInt(text.substring(0, 2), 16));
	}
}
