package asciiWorld;

public class Convert {
	public static <T> T changeType(Object obj, Class<T> type) throws Exception {
		if (type.isAssignableFrom(String.class)) {
			return type.cast(obj.toString());
		}
		throw new Exception(String.format("I do not understand this type: %s", type.getName()));
	}
}
