package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * PHONE: keeps the format, the country code and the first digit, replaces the rest
 * (see {@link PhoneNumbers}).
 */
public class PhoneAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "PHONE";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return PhoneNumbers.replace(from, random(from));
    }
}
