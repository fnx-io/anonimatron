package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * TOWN: town or village name from a list of Czech municipalities; the same value always gets the same replacement.
 */
public class TownNameAnonymizer extends DeterministicAnonymizer {

    private static final WordList NAMES = WordList.load("address.town");

    @Override
    public String getType() {
        return "TOWN";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return NAMES.pick(random(CzechText.normalize(from)));
    }
}
