package be.alexandreliebh.picacademy.data.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
		for (int i = 0; i < list.size(); i++) {
			s += list.get(i);
			if (i != list.size() - 1)
				s += sepa;
		}
		return s;
	}

	/**
	 * Charge les mots utilisés pour le jeu
	 * 
	 * @param fileName Fichier CSV contenant le fichier (sans extension)
	 * @return boolean si le chargement des mots a marché
	 */
	public static List<String> loadWords(String fileName) {
		List<String> words = new ArrayList<>();
		try {
			words.addAll(LoadingUtil.loadCSV(fileName));
			return words;
		} catch (IOException e) {
			String[] er = { "ERROR loading " + fileName };
			words.addAll(Arrays.asList(er));
			return words;
		}
	}

}
