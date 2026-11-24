package io.fnx.anonimatron.anonymizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * Hands out one generated value per key and never the same value for two keys. On a collision
 * the next attempt is tried, so the result stays deterministic as long as values are requested
 * in the same order.
 */
final class UniqueValues {

    private static final int MAX_ATTEMPTS = 1000;

    private final Map<String, String> byKey = new HashMap<>();
    private final Set<String> issued = new HashSet<>();

    synchronized String get(String key, IntFunction<String> candidate) {
        String known = byKey.get(key);
        if (known != null) {
            return known;
        }
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String value = candidate.apply(attempt);
            if (issued.add(value)) {
                byKey.put(key, value);
                return value;
            }
        }
        throw new IllegalStateException("No unique value found after " + MAX_ATTEMPTS + " attempts.");
    }
}
