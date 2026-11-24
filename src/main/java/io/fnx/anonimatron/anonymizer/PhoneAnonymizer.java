package io.fnx.anonimatron.anonymizer;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PHONE: keeps the format, the country code (+420, 00421) and the first digit of the number
 * (mobile 6/7 stays mobile), replaces the rest. Works on several numbers in one value.
 */
public class PhoneAnonymizer extends DeterministicAnonymizer {

    private static final Pattern NUMBER = Pattern.compile("(\\+|00)?(42[01])?([ ]?)(\\d)([\\d ]*\\d)?");

    @Override
    public String getType() {
        return "PHONE";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        Random random = random(from);
        Matcher matcher = NUMBER.matcher(from);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String international = (matcher.group(1) == null ? "" : matcher.group(1))
                    + (matcher.group(2) == null ? "" : matcher.group(2)) + matcher.group(3);
            String rest = matcher.group(5) == null ? "" : CzechText.maskDigits(matcher.group(5), random)
                    .replaceFirst("^[1-9]", String.valueOf(random.nextInt(10)));
            matcher.appendReplacement(result, Matcher.quoteReplacement(international + matcher.group(4) + rest));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
