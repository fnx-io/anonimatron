package io.fnx.anonimatron.anonymizer;

import java.util.Random;

/**
 * Czech company identification number (IČO): 8 digits, the last one a mod 11 check digit.
 */
final class Ico {

    private static final int[] WEIGHTS = {8, 7, 6, 5, 4, 3, 2};

    private Ico() {
    }

    static String generate(Random random) {
        StringBuilder digits = new StringBuilder(8);
        for (int i = 0; i < WEIGHTS.length; i++) {
            digits.append(random.nextInt(10));
        }
        return digits.append(checkDigit(digits)).toString();
    }

    static boolean isValid(String ico) {
        return ico.matches("\\d{8}") && ico.charAt(7) - '0' == checkDigit(ico.substring(0, 7));
    }

    private static int checkDigit(CharSequence first7) {
        int sum = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            sum += (first7.charAt(i) - '0') * WEIGHTS[i];
        }
        int remainder = sum % 11;
        if (remainder == 0) {
            return 1;
        }
        if (remainder == 1) {
            return 0;
        }
        return 11 - remainder;
    }
}
