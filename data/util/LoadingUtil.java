package be.alexandreliebh.picacademy.data.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class LoadingUtil {

	public static final List<String> loadCSV(String name) throws IOException {
		String[] words = {};
		try {
			List<String> stringCsv = Files.readAllLines(Paths.get("res/" + name + ".csv"), StandardCharsets.UTF_8);
			words = stringCsv.get(0).split(",");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Arrays.asList(words);
	}

}
