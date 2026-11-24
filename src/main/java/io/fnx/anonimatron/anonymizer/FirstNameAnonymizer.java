package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * FIRSTNAME: Czech first name of the same gender; the same name always gets the same replacement.
 */
public class FirstNameAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "FIRSTNAME";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return new PersonNames(seeds()).firstNames(from);
    }
}
