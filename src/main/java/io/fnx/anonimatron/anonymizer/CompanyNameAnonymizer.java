package io.fnx.anonimatron.anonymizer;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * COMPANYNAME: company or sole trader name. A name with a legal form ("s.r.o.", "a.s.") gets a
 * unique made-up company name with the same legal form; a name of a person (sole traders are
 * registered under their name) is anonymized like FULLNAME, so it matches the person elsewhere.
 */
public class CompanyNameAnonymizer extends DeterministicAnonymizer {

    private static final Pattern LEGAL_FORM = Pattern.compile(
            "(?iu)([\\s,]*)(spol\\.\\s*s\\s*r\\.\\s*o\\.?|s\\.\\s*r\\.\\s*o\\.?|a\\.\\s*s\\.?|v\\.\\s*o\\.\\s*s\\.?"
                    + "|k\\.\\s*s\\.?|o\\.\\s*p\\.\\s*s\\.?|z\\.\\s*s\\.?|z\\.\\s*ú\\.?|s\\.\\s*p\\.?|o\\.\\s*s\\.?"
                    + "|družstvo|státní podnik|příspěvková organizace|spolek|nadace|obec|město)\\s*$");
    private static final WordList PREFIXES = WordList.load("company.prefixes");
    private static final WordList SUFFIXES = WordList.load("company.suffixes");
    private static final WordList TRADES = WordList.load("company.trades");

    private final UniqueValues companies = new UniqueValues();

    @Override
    public String getType() {
        return "COMPANYNAME";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        Matcher legalForm = LEGAL_FORM.matcher(from);
        if (legalForm.find()) {
            String base = from.substring(0, legalForm.start());
            String form = from.substring(legalForm.start());
            return base.trim().isEmpty() ? from : company(base) + form;
        }
        if (PersonNames.mentionsFirstName(from)) {
            return new PersonNames(seeds()).people(from);
        }
        return company(from);
    }

    private String company(String base) {
        String key = CzechText.normalize(base);
        return companies.get(key, key, attempt -> {
            Random random = random(getType(), key, attempt);
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
        });
    }
}
