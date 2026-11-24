package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * STREET: street name from a list of Czech streets; a trailing house number ("Nádražní 12/3")
 * keeps its shape with masked digits.
 */
public class StreetNameAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "STREET";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        String name = from.trim().replaceFirst("\\s+\\d.*$", "");
        return StreetNames.replace(from, random(CzechText.normalize(name)), random(from));
    }
}
