package be.alexandreliebh.picacademy.server.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import be.alexandreliebh.picacademy.data.util.LoadingUtil;

public class PicWordGenerator {

	private Random random;
	private List<String> words;

	
	public PicWordGenerator() {
		this.random = new Random();
	}

	public String getRandomWord() {
		int rIndex = random.nextInt(this.words.size());
		String word = this.words.get(rIndex);
		this.words.remove(rIndex);
		return word;
	}
	
	public List<String> getRandomWords(int amount) {
		List<String> wor = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			int rIndex = random.nextInt(this.words.size());
			String word = this.words.get(rIndex);
			this.words.remove(rIndex);
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
	public boolean loadWords(String fileName) {
		try {
			this.words = new ArrayList<>();
			this.words.addAll(LoadingUtil.loadCSV(fileName));
			return true;
		} catch (IOException e) {
			String[] er = { "ERROR" };
			this.words.addAll(Arrays.asList(er));
			return false;
		}
	}

}
