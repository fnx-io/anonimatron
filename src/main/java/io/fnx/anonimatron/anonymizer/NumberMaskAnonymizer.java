package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * NUMBERMASK: keeps letters and separators, replaces digits; unique, so it fits columns with a
 * unique constraint (licence numbers, permit numbers).
 */
public class NumberMaskAnonymizer extends DeterministicAnonymizer {

    private final UniqueValues values = new UniqueValues();

    @Override
    public String getType() {
        return "NUMBERMASK";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return values.get(from, attempt -> CzechText.maskDigits(from, random(getType(), from, attempt)));
    }
}
