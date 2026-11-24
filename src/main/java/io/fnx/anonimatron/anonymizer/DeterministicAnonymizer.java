package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.anonymizer.SaltedAnonymizer;
import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * Base of the fnx anonymizers: the synonym is computed from the value and the secret salt, so the
 * same database anonymized twice with the same salt gives the same result without a synonym file.
 * Blank values stay blank.
 */
public abstract class DeterministicAnonymizer implements SaltedAnonymizer {

    private Seeds seeds;

    @Override
    public void setSalt(String salt) {
        this.seeds = new Seeds(salt);
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        return anonymize(from, size, shortlived, Collections.emptyMap());
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
        if (!(from instanceof String)) {
            throw new UnsupportedOperationException(
                    getType() + " can only anonymize text, got " + (from == null ? "null" : from.getClass().getName()));
        }
        String original = (String) from;
        String to = original.trim().isEmpty()
                ? original
                : generate(original, parameters == null ? Collections.emptyMap() : parameters);
        if (size > 0 && to.length() > size) {
            to = to.substring(0, size);
        }
        return new StringSynonym(getType(), original, to, shortlived);
    }

    /** Generates the synonym of a non-blank value. */
    protected abstract String generate(String from, Map<String, String> parameters);

    /** Random sequence for {@code value} within {@code domain}; equal inputs give equal sequences. */
    protected Random random(String domain, String value, int attempt) {
        return seeds().random(domain, value, attempt);
    }

    protected Random random(String value) {
        return random(getType(), value, 0);
    }

    Seeds seeds() {
        if (seeds == null) {
            throw new IllegalStateException(getType() + " needs a salt: set salt in the configuration or pass -salt.");
        }
        return seeds;
    }
}
