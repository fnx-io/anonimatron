package com.rolfje.anonimatron.anonymizer;

/**
 * An anonymizer which derives its synonyms from the configured secret salt,
 * so that the same input gives the same output on every run without a synonym file.
 */
public interface SaltedAnonymizer extends Anonymizer {

    /**
     * Called once after registration with the salt from the configuration or command line.
     */
    void setSalt(String salt);
}
