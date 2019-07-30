package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;

import java.sql.Date;
import java.util.Map;

/**
 * Provides functionality for consitently anonymizing a piece of data.
 * <p>
 * Implementations of this interface must make sure that anonymization is done
 * in a reproducable manner. That is, if A transforms into B, it has to
 * consistently do so on each and every call.
 * <p>
 * By doing this, anonimatron can guarantee that data is transformed
 * consistently accross all tables of the database, and referential constraints
 * can be re-enforced after anonymization.
 */
public interface Anonymizer {

    /**
     * @return The ID or name of this anonymizer, as used in the XML
     * configuration file. This is generally something along the lines
     * of "LASTNAME" or "UNEVENNUMBER". Please see the output of the
     * -configexample command line option when running Anonimatron.
     */
    String getType();

    /**
     * Anonymizes the given data into a non-tracable, non-reversible synonym,
     * and does it consistently, so that A always translates to B.
     *
     * @param from       the data to be anonymized, usually passed in as a
     *                   {@link String}, {@link Integer}, {@link Date} or other classes
     *                   which can be stored in a single JDBC database column.
     * @param size       the optional maximum size of the generated value
     * @param shortlived indicates that the generated synonym must have the
     *                   {@link Synonym#isShortLived()} boolean set
     * @return a {@link Synonym}
     */
    Synonym anonymize(Object from, int size, boolean shortlived);

    /**
     * Anonymizes the given data into a non-tracable, non-reversible synonym
     * with the provided parameters, and does it consistently, so that A
     * always translates to B.
     * @param from       the data to be anonymized, usually passed in as a
     *                   {@link String}, {@link Integer}, {@link java.sql.Date} or other classes
     *                   which can be stored in a single JDBC database column.
     * @param size       the optional maximum size of the generated value
     * @param shortlived indicates that the generated synonym must have the
     *                   {@link Synonym#isShortLived()} boolean set
     * @param parameters the parameters to be used by the anonymizer
     * @return a {@link Synonym}
     */
    default Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
        return anonymize(from, size, shortlived);
    }

}
