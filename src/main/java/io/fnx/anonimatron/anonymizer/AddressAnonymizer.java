package io.fnx.anonimatron.anonymizer;

import java.util.Map;
import java.util.Random;

/**
 * ADDRESS: whole address in one column, "town, street number".
 */
public class AddressAnonymizer extends DeterministicAnonymizer {

    private static final WordList STREETS = WordList.load("address.street");
    private static final WordList TOWNS = WordList.load("address.town");
    private static final int MAX_HOUSE_NUMBER = 150;

    @Override
    public String getType() {
        return "ADDRESS";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        Random random = random(CzechText.normalize(from));
        return TOWNS.pick(random) + ", " + STREETS.pick(random) + " " + (1 + random.nextInt(MAX_HOUSE_NUMBER));
    }
}
