package asciiWorld;

public class TextHelper {
	
	/**
	 * 
	 * @param text
	 * @return Is the input text null, empty, or whitespace?
	 */
	public static Boolean isNullOrWhiteSpace(String text) {
		return (text == null) || (text.trim().length() == 0);
	}
}