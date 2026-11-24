package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.anonymizer.Prefetcher;

import java.util.Map;

/**
 * NUMBERMASK: keeps letters and separators, replaces digits; unique, so it fits columns with a
 * unique constraint (licence numbers, permit numbers).
 */
public class NumberMaskAnonymizer extends DeterministicAnonymizer implements Prefetcher {

    private final UniqueValues values = new UniqueValues();

    @Override
    public String getType() {
        return "NUMBERMASK";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return values.get(from, from, attempt -> CzechText.maskDigits(from, random(getType(), from, attempt)));
    }

    /** Reserves the original values found in the pre-scan, so that no replacement equals one of them. */
    @Override
    public void prefetch(Object sourceData) {
        if (sourceData instanceof String && !((String) sourceData).trim().isEmpty()) {
            values.reserve((String) sourceData);
        }
    }
}
