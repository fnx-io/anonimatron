package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * RC: valid, unique birth number (rodné číslo) of the same gender, born within two years of the
 * original, with or without the slash like the original.
 */
public class PersonalIdentificationNumberAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "RC";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return CzechIdentifiers.of(seeds()).birthNumber(from);
    }
}
