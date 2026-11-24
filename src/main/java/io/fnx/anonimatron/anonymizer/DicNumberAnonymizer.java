package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.anonymizer.Prefetcher;

import java.util.Map;

/**
 * DIC: "CZ" + IČO becomes "CZ" + the anonymized IČO (so DIČ matches the ICO column), "CZ" + birth
 * number becomes "CZ" + a birth number of the same gender and similar age.
 */
public class DicNumberAnonymizer extends DeterministicAnonymizer implements Prefetcher {

    @Override
    public String getType() {
        return "DIC";
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
