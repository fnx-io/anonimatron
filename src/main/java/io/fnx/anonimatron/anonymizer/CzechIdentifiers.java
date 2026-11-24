package io.fnx.anonimatron.anonymizer;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replaces IČO, DIČ and birth numbers so that each original gets its own unique replacement and
 * a DIČ follows its IČO ("CZ" + IČO) or birth number ("CZ" + rodné číslo). The state is shared
 * by every anonymizer with the same salt, because IČO and DIČ live in different columns.
 */
final class CzechIdentifiers {

    private static final Map<String, CzechIdentifiers> BY_SALT = new ConcurrentHashMap<>();
    private static final String DOMAIN = "CZECH_IDENTIFIER";

    private final Seeds seeds;
    private final UniqueValues icos = new UniqueValues();
    private final UniqueValues birthNumbers = new UniqueValues();
    private final UniqueValues others = new UniqueValues();

    private CzechIdentifiers(Seeds seeds) {
        this.seeds = seeds;
    }

    static CzechIdentifiers of(Seeds seeds) {
        return BY_SALT.computeIfAbsent(seeds.salt(), salt -> new CzechIdentifiers(seeds));
    }

    /**
     * Anonymizes IČO or DIČ by its shape: an optional country prefix ("CZ", "CZ "), then 8 digits
     * (IČO), a birth number, or anything else whose digits are masked.
     */
    String identifier(String value) {
        String trimmed = value.trim();
        int bodyStart = 0;
        while (bodyStart < trimmed.length() && !Character.isDigit(trimmed.charAt(bodyStart))) {
            bodyStart++;
        }
        String prefix = trimmed.substring(0, bodyStart);
        String body = trimmed.substring(bodyStart);
        if (body.matches("\\d{1,8}") && (prefix.isEmpty() || prefix.trim().matches("\\p{L}{2}"))) {
            return prefix + ico(body);
        }
        if (RodneCislo.parse(body) != null) {
            return prefix + birthNumber(body);
        }
        return others.get(trimmed, trimmed, attempt -> CzechText.maskDigits(trimmed, random("OTHER", trimmed, attempt)));
    }

    /** IČO; shorter numbers are IČO that lost their leading zeros. */
    String ico(String digits) {
        String key = String.format("%8s", digits).replace(' ', '0');
        return icos.get(key, key, attempt -> Ico.generate(random("ICO", key, attempt)));
    }

    /** Birth number of the same gender and similar age; a value that is not one gets a random one. */
    String birthNumber(String value) {
        String key = CzechText.digitsOnly(value);
        RodneCislo original = RodneCislo.parse(value);
        String digits = birthNumbers.get(key, key, attempt -> {
            Random random = random("RC", key, attempt);
            String generated = original != null
                    ? original.similar(random)
                    : RodneCislo.generate(random, 1960 + random.nextInt(40), random.nextBoolean() ? Gender.MALE : Gender.FEMALE, false);
            return CzechText.digitsOnly(generated);
        });
        boolean slash = original != null ? original.slash : value.contains("/");
        return slash ? digits.substring(0, 6) + "/" + digits.substring(6) : digits;
    }

    private Random random(String kind, String key, int attempt) {
        return seeds.random(DOMAIN + ":" + kind, key, attempt);
    }
}
