package io.fnx.anonimatron.anonymizer;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Phone numbers keeping the format, the country code (+420, 00421) and the first digit of the
 * number (mobile 6/7 stays mobile). Works on several numbers in one value.
 */
final class PhoneNumbers {

    private static final Pattern NUMBER = Pattern.compile("(\\+|00)?(42[01])?([ ]?)(\\d)([\\d ]*\\d)?");

    private PhoneNumbers() {
    }

    static String replace(String from, Random random) {
        Matcher matcher = NUMBER.matcher(from);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String international = (matcher.group(1) == null ? "" : matcher.group(1))
                    + (matcher.group(2) == null ? "" : matcher.group(2)) + matcher.group(3);
            // Masked digits start every group with 1-9, so a number never turns into "00..."
            // that a second run would read as an international prefix.
            String rest = matcher.group(5) == null ? "" : CzechText.maskDigits(matcher.group(5), random);
            matcher.appendReplacement(result, Matcher.quoteReplacement(international + matcher.group(4) + rest));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
