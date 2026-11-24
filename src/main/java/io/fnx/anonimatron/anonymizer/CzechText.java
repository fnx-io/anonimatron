package io.fnx.anonimatron.anonymizer;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;

/**
 * Text helpers shared by the Czech anonymizers.
 */
final class CzechText {

    private CzechText() {
    }

    /** Lower case without diacritics, used as the key for seeding. */
    static String normalize(String text) {
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).trim();
    }

    /** Gives {@code generated} the letter case of {@code original} (all upper, all lower, or as is). */
    static String matchCase(String original, String generated) {
        boolean hasLetters = !original.toLowerCase(Locale.ROOT).equals(original.toUpperCase(Locale.ROOT));
        if (!hasLetters) {
            return generated;
        }
        if (original.length() > 1 && original.equals(original.toUpperCase(Locale.ROOT))) {
            return generated.toUpperCase(Locale.ROOT);
        }
        if (original.equals(original.toLowerCase(Locale.ROOT))) {
            return generated.toLowerCase(Locale.ROOT);
        }
        return generated;
    }

    /**
     * Replaces every digit with a random one; letters and separators stay. The first digit of each
     * run stays non-zero when it was, so that numbers keep their magnitude.
     */
    static String maskDigits(String text, Random random) {
        StringBuilder masked = new StringBuilder(text.length());
        boolean firstDigit = true;
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                boolean keepNonZero = firstDigit && c != '0';
                masked.append(keepNonZero ? (char) ('1' + random.nextInt(9)) : (char) ('0' + random.nextInt(10)));
                firstDigit = false;
            } else {
                masked.append(c);
                firstDigit = true;
            }
        }
        return masked.toString();
    }

    static String digitsOnly(String text) {
        return text.replaceAll("\\D", "");
    }
}
