package asciiWorld;

public class Convert {
	public static <T> T changeType(Object obj, Class<T> type) throws Exception {
		String value = obj.toString();
		if (type.isAssignableFrom(String.class)) {
			return type.cast(value);
		} else if (type.isAssignableFrom(Integer.class)) {
			return type.cast(Integer.parseInt(value));
		}
		throw new Exception(String.format("I do not understand this type: %s", type.getName()));
	}
}
