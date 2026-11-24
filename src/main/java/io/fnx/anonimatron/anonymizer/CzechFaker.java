package io.fnx.anonimatron.anonymizer;

import java.util.Random;

/**
 * Czech fake data for anonymizing database rows by their primary key: every method takes the
 * current value only for its shape (gender, legal form, format, length) and draws the rest from
 * the given random sequence, typically {@link #random(String, String)} of salt and row key.
 * <p>
 * The shape of a result is the shape of its input, so applying a method to its own result with
 * the same random sequence gives the same result again: anonymizing twice changes nothing.
 * Uniqueness is up to the caller (retry with another sequence).
 */
public final class CzechFaker {

    private static final WordList TOWNS = WordList.load("address.town");
    private static final WordList BANKS = WordList.load("bank.names");
    private static final int MAX_HOUSE_NUMBER = 150;
    private static final int MAX_MASK_ATTEMPTS = 100;

    private CzechFaker() {
    }

    /** Random sequence derived from HMAC-SHA256(salt, key). */
    public static Random random(String salt, String key) {
        return new Seeds(salt).random("ROW", key, 0);
    }

    /** First names of the same gender. */
    public static String firstNames(String original, Random random) {
        return new PersonNames(random).firstNames(original);
    }

    /** Surnames in the same gender form. */
    public static String surnames(String original, Random random) {
        return new PersonNames(random).surnames(original);
    }

    /** Names of one or more people in text; titles and lower case words stay. */
    public static String people(String original, Random random) {
        return new PersonNames(random).people(original);
    }

    /**
     * Company name keeping the legal form; a name of a person (sole trader) is replaced like
     * {@link #people(String, Random)}.
     */
    public static String company(String original, Random random) {
        int legalForm = CompanyNames.legalFormStart(original);
        if (legalForm >= 0) {
            String base = original.substring(0, legalForm);
            return base.trim().isEmpty() ? original : CompanyNames.generate(random) + original.substring(legalForm);
        }
        if (PersonNames.mentionsFirstName(original)) {
            return people(original, random);
        }
        return CompanyNames.generate(random);
    }

    /** Street name; a trailing house number keeps its shape. */
    public static String street(String original, Random random) {
        return StreetNames.replace(original, random, random);
    }

    public static String town(Random random) {
        return TOWNS.pick(random);
    }

    public static String bankName(Random random) {
        return BANKS.pick(random);
    }

    /** Whole address in one value: "town, street number". */
    public static String address(Random random) {
        return TOWNS.pick(random) + ", " + StreetNames.pick(random) + " " + (1 + random.nextInt(MAX_HOUSE_NUMBER));
    }

    /** Phone numbers keeping format, country code and first digit. */
    public static String phone(String original, Random random) {
        return PhoneNumbers.replace(original, random);
    }

    /** Czech account passing the mod 11 check with the same bank code; other formats masked. */
    public static String bankAccount(String original, Random random) {
        String account = CzechBankAccount.similar(original, random);
        return account != null ? account : CzechText.maskDigits(original, random);
    }

    /**
     * IČO or DIČ by its shape: optional country prefix ("CZ", "CZ "), then 8 digits or fewer
     * (a valid 8 digit IČO), a birth number (see {@link #birthNumber(String, Random)}), or
     * anything else with masked digits.
     */
    public static String identifier(String original, Random random) {
        String trimmed = original.trim();
        int bodyStart = 0;
        while (bodyStart < trimmed.length() && !Character.isDigit(trimmed.charAt(bodyStart))) {
            bodyStart++;
        }
        String prefix = trimmed.substring(0, bodyStart);
        String body = trimmed.substring(bodyStart);
        if (body.matches("\\d{1,8}") && (prefix.isEmpty() || prefix.trim().matches("\\p{L}{2}"))) {
            return prefix + Ico.generate(random);
        }
        if (RodneCislo.parse(body) != null) {
            return prefix + birthNumber(body, random);
        }
        return prefix + maskNotBirthNumber(body, random);
    }

    /**
     * Valid birth number of the same gender, born in the same five year period, with or without
     * the slash like the original; a value that is not a birth number gets masked digits.
     */
    public static String birthNumber(String original, Random random) {
        RodneCislo parsed = RodneCislo.parse(original.trim());
        return parsed != null ? parsed.similar(random) : maskNotBirthNumber(original.trim(), random);
    }

    /**
     * Masked digits that do not form a birth number, so that a second run takes the same branch.
     */
    private static String maskNotBirthNumber(String value, Random random) {
        String masked = CzechText.maskDigits(value, random);
        for (int attempt = 0; attempt < MAX_MASK_ATTEMPTS && RodneCislo.parse(masked) != null; attempt++) {
            masked = CzechText.maskDigits(value, random);
        }
        return masked;
    }

    /** Digits replaced, letters and separators kept. */
    public static String maskDigits(String original, Random random) {
        return CzechText.maskDigits(original, random);
    }
}
