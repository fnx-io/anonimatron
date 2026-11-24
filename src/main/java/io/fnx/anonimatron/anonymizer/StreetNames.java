package io.fnx.anonimatron.anonymizer;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Czech street names; a trailing house number ("Nádražní 12/3") keeps its shape with masked digits.
 */
final class StreetNames {

    private static final WordList STREETS = WordList.load("address.street");
    private static final Pattern HOUSE_NUMBER = Pattern.compile("^(.*?)(\\s+\\d[\\d/\\s\\p{L}]*)?$");

    private StreetNames() {
    }

    static String replace(String from, Random nameRandom, Random numberRandom) {
        Matcher matcher = HOUSE_NUMBER.matcher(from.trim());
        matcher.matches();
        String name = matcher.group(1);
        String number = matcher.group(2) == null ? "" : CzechText.maskDigits(matcher.group(2), numberRandom);
        if (name.isEmpty()) {
            return number.trim();
        }
        return STREETS.pick(nameRandom) + number;
    }

    static String pick(Random random) {
        return STREETS.pick(random);
    }
}
