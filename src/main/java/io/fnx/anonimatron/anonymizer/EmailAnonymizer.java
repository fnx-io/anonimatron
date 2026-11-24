package io.fnx.anonimatron.anonymizer;

import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EMAIL: every address in the value becomes a unique address in the reserved domain example.com,
 * so that nothing can be delivered to a real person. Separators between addresses stay.
 */
public class EmailAnonymizer extends DeterministicAnonymizer {

    private static final Pattern ADDRESS = Pattern.compile("[^\\s,;<>@]+@[^\\s,;<>@]+");
    private static final String DOMAIN = "example.com";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LOCAL_LENGTH = 10;

    private final UniqueValues addresses = new UniqueValues();

    @Override
    public String getType() {
        return "EMAIL";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        Matcher matcher = ADDRESS.matcher(from);
        if (!matcher.find()) {
            return address(from);
        }
        matcher.reset();
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(address(matcher.group())));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String address(String original) {
        String key = original.trim().toLowerCase(Locale.ROOT);
        return addresses.get(key, attempt -> {
            Random random = random(getType(), key, attempt);
            StringBuilder local = new StringBuilder("anonym.");
            for (int i = 0; i < LOCAL_LENGTH; i++) {
                local.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
            }
            return local + "@" + DOMAIN;
        });
    }
}
