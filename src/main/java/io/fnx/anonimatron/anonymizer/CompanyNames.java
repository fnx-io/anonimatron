package io.fnx.anonimatron.anonymizer;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Made-up Czech company names and the legal form of an original name.
 */
final class CompanyNames {

    private static final Pattern LEGAL_FORM = Pattern.compile(
            "(?iu)([\\s,]*)(spol\\.\\s*s\\s*r\\.\\s*o\\.?|s\\.\\s*r\\.\\s*o\\.?|a\\.\\s*s\\.?|v\\.\\s*o\\.\\s*s\\.?"
                    + "|k\\.\\s*s\\.?|o\\.\\s*p\\.\\s*s\\.?|z\\.\\s*s\\.?|z\\.\\s*ú\\.?|s\\.\\s*p\\.?|o\\.\\s*s\\.?"
                    + "|družstvo|státní podnik|příspěvková organizace|spolek|nadace|obec|město)\\s*$");
    private static final WordList PREFIXES = WordList.load("company.prefixes");
    private static final WordList SUFFIXES = WordList.load("company.suffixes");
    private static final WordList TRADES = WordList.load("company.trades");

    private CompanyNames() {
    }

    /** Position where the legal form (with its leading separator) starts, or -1. */
    static int legalFormStart(String name) {
        Matcher matcher = LEGAL_FORM.matcher(name);
        return matcher.find() ? matcher.start() : -1;
    }

    static String generate(Random random) {
        String compound = PREFIXES.pick(random) + SUFFIXES.pick(random);
        String surname = CzechNames.pickSurname(random).male;
        switch (random.nextInt(3)) {
            case 0:
                return compound;
            case 1:
                return compound + " " + surname;
            default:
                return TRADES.pick(random) + " " + surname;
        }
    }
}
