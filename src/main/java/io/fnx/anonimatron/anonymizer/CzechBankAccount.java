package io.fnx.anonimatron.anonymizer;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Czech bank account number "prefix-number/bank": prefix and number pass the mod 11 check,
 * the bank code stays.
 */
final class CzechBankAccount {

    private static final Pattern FORMAT = Pattern.compile("(?:(\\d{1,6})-)?(\\d{2,10})/(\\d{4})");
    private static final int[] WEIGHTS = {6, 3, 7, 9, 10, 5, 8, 4, 2, 1};

    private CzechBankAccount() {
    }

    /** A valid account with the same bank code and digit counts, or null when the value is not an account. */
    static String similar(String value, Random random) {
        Matcher matcher = FORMAT.matcher(value.trim());
        if (!matcher.matches()) {
            return null;
        }
        String prefix = matcher.group(1);
        String number = validNumber(matcher.group(2).length(), random);
        return (prefix == null ? "" : validNumber(prefix.length(), random) + "-") + number + "/" + matcher.group(3);
    }

    static boolean isValid(String value) {
        Matcher matcher = FORMAT.matcher(value.trim());
        return matcher.matches()
                && (matcher.group(1) == null || passesCheck(matcher.group(1)))
                && passesCheck(matcher.group(2));
    }

    private static String validNumber(int length, Random random) {
        while (true) {
            StringBuilder digits = new StringBuilder(length);
            digits.append(1 + random.nextInt(9));
            for (int i = 1; i < length; i++) {
                digits.append(random.nextInt(10));
            }
            if (passesCheck(digits.toString())) {
                return digits.toString();
            }
        }
    }

    private static boolean passesCheck(String digits) {
        int sum = 0;
        int offset = WEIGHTS.length - digits.length();
        for (int i = 0; i < digits.length(); i++) {
            sum += (digits.charAt(i) - '0') * WEIGHTS[offset + i];
        }
        return sum % 11 == 0;
    }
}
