package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * COMPANYNAME: company or sole trader name. A name with a legal form ("s.r.o.", "a.s.") gets a
 * unique made-up company name with the same legal form; a name of a person (sole traders are
 * registered under their name) is anonymized like FULLNAME, so it matches the person elsewhere.
 */
public class CompanyNameAnonymizer extends DeterministicAnonymizer {

    private final UniqueValues companies = new UniqueValues();

    @Override
    public String getType() {
        return "COMPANYNAME";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        int legalForm = CompanyNames.legalFormStart(from);
        if (legalForm >= 0) {
            String base = from.substring(0, legalForm);
            return base.trim().isEmpty() ? from : company(base) + from.substring(legalForm);
        }
        if (PersonNames.mentionsFirstName(from)) {
            return new PersonNames(seeds()).people(from);
        }
        return company(from);
    }

    private String company(String base) {
        String key = CzechText.normalize(base);
        return companies.get(key, key, attempt -> CompanyNames.generate(random(getType(), key, attempt)));
    }
}
