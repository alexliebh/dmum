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
		List<String> stringCsv = Files.readAllLines(Paths.get("res/" + name + ".csv"), StandardCharsets.UTF_8);
		words = stringCsv.get(0).split(",");

		return Arrays.asList(words);
	}

	public static final String listToString(List<String> list, String sepa) {
		String s = "";
		for (String string : list) {
			s = s + string + sepa;
		}
		return s;
	}

}
