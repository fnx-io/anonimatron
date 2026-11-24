package io.fnx.anonimatron.anonymizer;

import java.util.Map;
import java.util.Random;

/**
 * LOREM: lorem ipsum shorter than the parameter {@code maxLength} (default 25;
 * {@code maxLenght} is accepted for older configurations).
 */
public class LoremAnonymizer extends DeterministicAnonymizer {

    private static final WordList WORDS = WordList.load("lorem.txt");
    private static final int DEFAULT_MAX_LENGTH = 25;

    @Override
    public String getType() {
        return "LOREM";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        int maxLength = maxLength(parameters);
        Random random = random(from);
        StringBuilder text = new StringBuilder();
        String word = capitalize(WORDS.pick(random));
        while (text.length() + word.length() < maxLength) {
            int pause = random.nextInt(11);
            String separator = pause < 7 ? " " : pause < 10 ? ", " : ". ";
            text.append(word).append(separator);
            word = separator.startsWith(".") ? capitalize(WORDS.pick(random)) : WORDS.pick(random);
        }
        return text.toString().trim();
    }

    private static int maxLength(Map<String, String> parameters) {
        String value = parameters.getOrDefault("maxLength", parameters.get("maxLenght"));
        return value == null ? DEFAULT_MAX_LENGTH : Integer.parseInt(value);
    }

    private static String capitalize(String word) {
        return word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
