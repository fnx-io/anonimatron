package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * BANKNAME: name of a Czech bank; the same value always gets the same replacement.
 */
public class BankNameAnonymizer extends DeterministicAnonymizer {

    private static final WordList NAMES = WordList.load("bank.names");

    @Override
    public String getType() {
        return "BANKNAME";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        return NAMES.pick(random(CzechText.normalize(from)));
    }
}
