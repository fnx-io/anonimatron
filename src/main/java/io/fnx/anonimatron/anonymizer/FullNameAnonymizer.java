package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * FULLNAME: names of one or more people in free text ("Ing. Jan Novák", "Nováková Jana, Petr
 * Svoboda"). Titles and lower case words stay; names match FIRSTNAME and LASTNAME.
 */
public class FullNameAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "FULLNAME";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return new PersonNames(seeds()).people(from);
    }
}
