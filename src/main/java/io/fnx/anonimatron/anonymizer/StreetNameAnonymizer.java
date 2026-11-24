package io.fnx.anonimatron.anonymizer;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * STREET: street name from a list of Czech streets; a trailing house number ("Nádražní 12/3")
 * keeps its shape with masked digits.
 */
public class StreetNameAnonymizer extends DeterministicAnonymizer {

    private static final WordList STREETS = WordList.load("address.street");
    private static final Pattern HOUSE_NUMBER = Pattern.compile("^(.*?)(\\s+\\d[\\d/\\s\\p{L}]*)?$");

    @Override
    public String getType() {
        return "STREET";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        Matcher matcher = HOUSE_NUMBER.matcher(from.trim());
        matcher.matches();
        String name = matcher.group(1);
        String number = matcher.group(2) == null ? "" : CzechText.maskDigits(matcher.group(2), random(from));
        if (name.isEmpty()) {
            return number.trim();
        }
        return STREETS.pick(random(CzechText.normalize(name))) + number;
    }
}
