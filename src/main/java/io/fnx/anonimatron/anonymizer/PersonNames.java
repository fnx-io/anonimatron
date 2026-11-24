package io.fnx.anonimatron.anonymizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces Czech personal names word by word. Every anonymizer uses the same seeds for a first
 * name or a surname, so "Jan" in a first name column, "Jan Novák" in a full name column and
 * "Ing. Jan Novák" in a contact column all become the same person. The replacement keeps the
 * gender: Nováková gets the female form of the surname Novák gets.
 */
final class PersonNames {

    private static final String FIRST_NAME = "PERSON_FIRST_NAME";
    private static final String SURNAME = "PERSON_SURNAME";
    private static final Pattern WORD = Pattern.compile("\\p{L}+");
    /** Separates several people in one value ("Jan Novák, Petr Svoboda"). */
    private static final Pattern PERSON_SEPARATOR = Pattern.compile("[,;\\n/&+]");
    private static final Set<String> TITLES = new HashSet<>(Arrays.asList(
            "ing", "mgr", "bc", "ph", "phd", "dr", "csc", "drsc", "mudr", "judr", "rndr", "phdr", "paeddr",
            "mvdr", "mddr", "pharmdr", "thdr", "thlic", "doc", "prof", "dis", "ba", "bba", "mba", "msc", "ma",
            "llm", "arch", "art", "akad", "st", "ml", "dipl", "mph", "et", "pan", "pani", "sl"));

    private final Seeds seeds;

    PersonNames(Seeds seeds) {
        this.seeds = seeds;
    }

    String firstName(String word, Gender gender) {
        String key = CzechText.normalize(word);
        Random random = seeds.random(FIRST_NAME, gender + ":" + key, 0);
        String name = CzechNames.pickFirstName(gender, random);
        if (CzechText.normalize(name).equals(key)) {
            name = CzechNames.pickFirstName(gender, random);
        }
        return CzechText.matchCase(word, name);
    }

    String surname(String word, Gender gender) {
        String stem = CzechNames.surnameStem(word);
        Random random = seeds.random(SURNAME, stem, 0);
        CzechNames.Surname surname = CzechNames.pickSurname(random);
        if (CzechNames.surnameStem(surname.male).equals(stem)) {
            surname = CzechNames.pickSurname(random);
        }
        return CzechText.matchCase(word, surname.form(gender));
    }

    /** Replaces each surname word; the gender comes from each word's own form. */
    String surnames(String text) {
        return replaceWords(text, true, word -> surname(word, CzechNames.isFemaleSurname(word) ? Gender.FEMALE : Gender.MALE));
    }

    /** Replaces each first name word; the gender comes from the name itself. */
    String firstNames(String text) {
        return replaceWords(text, true, word -> firstName(word, CzechNames.genderOfFirstName(word)));
    }

    /**
     * Replaces the names of one or more people in free text. Titles, initials and lower case
     * words ("jednatel") stay; known first names become first names, other capitalised words
     * surnames, all in the gender of the person. Text written entirely in lower case is taken
     * as names only.
     */
    String people(String text) {
        StringBuilder result = new StringBuilder(text.length());
        Matcher separator = PERSON_SEPARATOR.matcher(text);
        int start = 0;
        while (separator.find()) {
            result.append(person(text.substring(start, separator.start()))).append(separator.group());
            start = separator.end();
        }
        return result.append(person(text.substring(start))).toString();
    }

    /**
     * True when the text contains a known Czech first name: capitalised, or in any case when the
     * whole text is written in lower case ("jan novák").
     */
    static boolean mentionsFirstName(String text) {
        boolean anyCase = text.equals(text.toLowerCase(java.util.Locale.ROOT));
        Matcher matcher = WORD.matcher(text);
        while (matcher.find()) {
            String word = matcher.group();
            if ((anyCase || Character.isUpperCase(word.charAt(0))) && CzechNames.isKnownFirstName(word)) {
                return true;
            }
        }
        return false;
    }

    private String person(String text) {
        boolean anyCase = names(text, false).isEmpty();
        Gender gender = genderOf(names(text, anyCase));
        return replaceWords(text, anyCase,
                word -> CzechNames.isKnownFirstName(word) ? firstName(word, gender) : surname(word, gender));
    }

    private static List<String> names(String text, boolean anyCase) {
        List<String> names = new ArrayList<>();
        Matcher matcher = WORD.matcher(text);
        while (matcher.find()) {
            if (isName(matcher.group(), anyCase)) {
                names.add(matcher.group());
            }
        }
        return names;
    }

    private static Gender genderOf(List<String> names) {
        for (String name : names) {
            if (!CzechNames.isKnownFirstName(name) && CzechNames.isFemaleSurname(name)) {
                return Gender.FEMALE;
            }
        }
        for (String name : names) {
            if (CzechNames.isKnownFirstName(name)) {
                return CzechNames.genderOfFirstName(name);
            }
        }
        return Gender.MALE;
    }

    private static boolean isName(String word, boolean anyCase) {
        return word.length() > 1
                && (anyCase || Character.isUpperCase(word.charAt(0)))
                && !TITLES.contains(CzechText.normalize(word));
    }

    private static String replaceWords(String text, boolean anyCase, UnaryOperator<String> replacement) {
        Matcher matcher = WORD.matcher(text);
        StringBuilder result = new StringBuilder(text.length());
        int last = 0;
        while (matcher.find()) {
            String word = matcher.group();
            result.append(text, last, matcher.start());
            result.append(isName(word, anyCase) ? replacement.apply(word) : word);
            last = matcher.end();
        }
        return result.append(text.substring(last)).toString();
    }
}
