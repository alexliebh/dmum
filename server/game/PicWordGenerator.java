package be.alexandreliebh.picacademy.server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe gérant la génération de mots au hasard
 * 
 * @author Alexandre Liebhaberg
 */
public class PicWordGenerator {

	private static final Random random = new Random();
	private final List<String> words;

	public PicWordGenerator(List<String> words) {
		this.words = words;
	}

	/**
	 * Récupère un mot au hazard dans la liste de mots puis le supprime pour ne pas
	 * avoir de mots choisis deux fois
	 * 
	 * @return un mot au hasard
	 */
	public String getRandomWord() {
		int rIndex = random.nextInt(words.size());
		String word = words.get(rIndex);
		words.remove(rIndex);
		return word;
	}

	/**
	 * Crée une liste avec un nombre spécifié de mots au hasard
	 * 
	 * @param amount nombre de mots à choisir au hasard
	 * @return liste de mots au hasard
	 */
	public List<String> getRandomWords(int amount) {
		List<String> wor = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			String word = getRandomWord();
			wor.add(word);
		}
		return wor;
	}
}
