package be.alexandreliebh.picacademy.server.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import be.alexandreliebh.picacademy.data.util.LoadingUtil;

public class PicWordGenerator {

	private static final Random random = new Random();
	private static List<String> words;

	
	public static String getRandomWord() {
		int rIndex = random.nextInt(words.size());
		String word = words.get(rIndex);
		words.remove(rIndex);
		return word;
	}
	
	public static List<String> getRandomWords(int amount) {
		List<String> wor = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			int rIndex = random.nextInt(words.size());
			String word = words.get(rIndex);
			words.remove(rIndex);
			wor.add(word);
		}
		return wor;
	}
	
	
	/**
	 * Charge les mots utilisés pour le jeu
	 * 
	 * @param fileName Fichier CSV contenant le fichier (sans extension)
	 * @return boolean si le chargement des mots a marché
	 */
	public static boolean loadWords(String fileName) {
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
