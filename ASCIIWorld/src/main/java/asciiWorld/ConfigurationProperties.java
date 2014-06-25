package asciiWorld;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class ConfigurationProperties {

	private static final String CONFIG_PATH = "config.properties";
	
	private Integer screenWidth;
	private Integer screenHeight;
	private Boolean showFPS;
	private Boolean fullscreen;
	
	public ConfigurationProperties() {
		InputStream input = ConfigurationProperties.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
		
		if (input == null) {
			System.out.println(MessageFormat.format("Unable to load file: {0}", CONFIG_PATH));
		}
		
		Properties properties = new Properties();
		try {
			properties.load(input);
		} catch (IOException e) {
			System.out.println("Unable to load config.properties; using default values.");
		}
		
		this.screenWidth = getProperty(properties, "screenWidth", 640, Integer.class);
		this.screenHeight = getProperty(properties, "screenHeight", 480, Integer.class);
		this.showFPS = getProperty(properties, "showFPS", false, Boolean.class);
		this.fullscreen = getProperty(properties, "fullscreen", false, Boolean.class);
		
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				System.err.println(MessageFormat.format("Unable to close file: {0}", CONFIG_PATH));
			}
		}
	}
	
	public Integer getScreenWidth() {
		return this.screenWidth;
	}
	
	public Integer getScreenHeight() {
		return this.screenHeight;
	}
	
	public Boolean getShowFPS() {
		return this.showFPS;
	}
	
	public Boolean getFullscreen() {
		return this.fullscreen;
	}
	
	private static <T> T getProperty(Properties properties, String propertyName, T defaultValue, Class<T> cls) {
		String valueText = properties.getProperty(propertyName);
		if (valueText == null) {
			System.err.println(MessageFormat.format("Property '{0}' is not set; using the default value.", propertyName));
			return defaultValue;
		} else {
			try {
				return Convert.changeType(valueText, cls);
			} catch (Exception e) {
				System.err.println(MessageFormat.format("Property '{0}' is not formatted correctly; using the default value.", propertyName));
				return defaultValue;
			}
		}
	}
}