package io.fnx.anonimatron.anonymizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Non-empty lines of a UTF-8 resource next to this class.
 */
final class WordList {

    private final List<String> words;

    private WordList(List<String> words) {
        this.words = Collections.unmodifiableList(words);
    }

    static WordList load(String resource) {
        InputStream stream = WordList.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new IllegalStateException("Missing word list " + resource);
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read word list " + resource, e);
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("Empty word list " + resource);
        }
        return new WordList(lines);
    }

    List<String> words() {
        return words;
    }

    String pick(Random random) {
        return words.get(random.nextInt(words.size()));
    }
}
