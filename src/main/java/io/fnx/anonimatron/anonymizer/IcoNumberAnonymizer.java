package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * ICO: valid, unique IČO. A value shaped like a birth number (sole traders) becomes a birth number,
 * anything else keeps its shape with masked digits.
 */
public class IcoNumberAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "ICO";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return CzechIdentifiers.of(seeds()).identifier(from);
    }
}
