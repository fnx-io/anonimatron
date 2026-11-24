package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.anonymizer.Prefetcher;

import java.util.Map;

/**
 * RC: valid, unique birth number (rodné číslo) of the same gender, born within two years of the
 * original, with or without the slash like the original.
 */
public class PersonalIdentificationNumberAnonymizer extends DeterministicAnonymizer implements Prefetcher {

    @Override
    public String getType() {
        return "RC";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return CzechIdentifiers.of(seeds()).birthNumber(from);
    }

    /** Reserves the original values found in the pre-scan, so that no replacement equals one of them. */
    @Override
    public void prefetch(Object sourceData) {
        if (sourceData instanceof String && !((String) sourceData).trim().isEmpty()) {
            CzechIdentifiers.of(seeds()).reserveBirthNumber((String) sourceData);
        }
    }
}
