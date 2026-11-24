package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * LASTNAME: Czech surname in the same gender form; Novák and Nováková get the male and female
 * form of the same replacement.
 */
public class LastNameAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "LASTNAME";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return new PersonNames(seeds()).surnames(from);
    }
}
