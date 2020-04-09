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
		for (String string : list) {
			s = s + string + sepa;
		}
		return s;
	}
	
	/**
	 * Charge les mots utilisés pour le jeu
	 * 
	 * @param fileName Fichier CSV contenant le fichier (sans extension)
	 * @return boolean si le chargement des mots a marché
	 */
	public static boolean loadWords(String fileName, List<String> words) {
		try {
			words = new ArrayList<>();
			words.addAll(LoadingUtil.loadCSV(fileName));
			return true;
		} catch (IOException e) {
			String[] er = { "ERROR" };
			words.addAll(Arrays.asList(er));
			return false;
		}
	}


}
