package asciiWorld.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileHelper {
	
	public static String readToEnd(String path) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		StringBuilder sb = new StringBuilder();
		for (String s : lines) {
			sb.append(s).append("\n");
		}
		return sb.toString();
	}
}
