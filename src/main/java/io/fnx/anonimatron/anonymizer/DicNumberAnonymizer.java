package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * DIC: "CZ" + IČO becomes "CZ" + the anonymized IČO (so DIČ matches the ICO column), "CZ" + birth
 * number becomes "CZ" + a birth number of the same gender and similar age.
 */
public class DicNumberAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "DIC";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return CzechIdentifiers.of(seeds()).identifier(from);
    }
}
