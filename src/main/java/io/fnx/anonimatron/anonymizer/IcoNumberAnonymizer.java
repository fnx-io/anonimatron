package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.anonymizer.Prefetcher;

import java.util.Map;

/**
 * ICO: valid, unique IČO. A value shaped like a birth number (sole traders) becomes a birth number,
 * anything else keeps its shape with masked digits.
 */
public class IcoNumberAnonymizer extends DeterministicAnonymizer implements Prefetcher {

    @Override
    public String getType() {
        return "ICO";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return CzechIdentifiers.of(seeds()).identifier(from);
    }

    /** Reserves the original values found in the pre-scan, so that no replacement equals one of them. */
    @Override
    public void prefetch(Object sourceData) {
        if (sourceData instanceof String && !((String) sourceData).trim().isEmpty()) {
            CzechIdentifiers.of(seeds()).reserveIdentifier((String) sourceData);
        }
    }
}
