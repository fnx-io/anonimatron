package io.fnx.anonimatron.anonymizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Czech first names and surnames: the lists to pick from, gender of an original name and
 * the male/female surname forms.
 */
final class CzechNames {

    private static final List<String> MALE_FIRST = distinct(WordList.load("names.first.male").words());
    private static final List<String> FEMALE_FIRST = distinct(WordList.load("names.first.female").words());
    private static final List<Surname> SURNAMES = surnames(WordList.load("names.last").words());
    private static final Set<String> KNOWN_MALE = normalized(MALE_FIRST);
    private static final Set<String> KNOWN_FEMALE = normalized(FEMALE_FIRST);

    private CzechNames() {
    }

    static boolean isKnownFirstName(String word) {
        String key = CzechText.normalize(word);
        return KNOWN_MALE.contains(key) || KNOWN_FEMALE.contains(key);
    }

    static boolean isKnownFemaleFirstName(String word) {
        return KNOWN_FEMALE.contains(CzechText.normalize(word));
    }

    /** Gender of a first name: from the lists, otherwise female when it ends with "a". */
    static Gender genderOfFirstName(String word) {
        String key = CzechText.normalize(word);
        if (KNOWN_FEMALE.contains(key)) {
            return Gender.FEMALE;
        }
        if (KNOWN_MALE.contains(key)) {
            return Gender.MALE;
        }
        return key.endsWith("a") ? Gender.FEMALE : Gender.MALE;
    }

    /** Gender of a surname: female forms end with "á" (Nováková, Novotná). */
    static boolean isFemaleSurname(String word) {
        String lower = word.toLowerCase(java.util.Locale.ROOT);
        return lower.endsWith("á") || CzechText.normalize(word).endsWith("ova");
    }

    /**
     * Key shared by the male and female form of a surname, so that Novák and Nováková
     * (or Svoboda and Svobodová) get the same replacement.
     */
    static String surnameStem(String word) {
        String key = CzechText.normalize(word);
        if (key.endsWith("ova") && key.length() > 4) {
            return key.substring(0, key.length() - 3);
        }
        if (key.endsWith("ek") || key.endsWith("ec") || key.endsWith("el")) {
            return key.substring(0, key.length() - 2) + key.charAt(key.length() - 1);
        }
        if ((key.endsWith("a") || key.endsWith("y")) && key.length() > 2) {
            return key.substring(0, key.length() - 1);
        }
        return key;
    }

    static String pickFirstName(Gender gender, Random random) {
        List<String> names = gender == Gender.FEMALE ? FEMALE_FIRST : MALE_FIRST;
        return names.get(random.nextInt(names.size()));
    }

    static Surname pickSurname(Random random) {
        return SURNAMES.get(random.nextInt(SURNAMES.size()));
    }

    static List<String> maleFirstNames() {
        return MALE_FIRST;
    }

    static List<String> femaleFirstNames() {
        return FEMALE_FIRST;
    }

    static List<Surname> surnames() {
        return SURNAMES;
    }

    /** Male and female form of one surname. */
    static final class Surname {
        final String male;
        final String female;

        Surname(String male, String female) {
            this.male = male;
            this.female = female;
        }

        String form(Gender gender) {
            return gender == Gender.FEMALE ? female : male;
        }
    }

    static String femaleForm(String male) {
        if (male.endsWith("ý")) {
            return male.substring(0, male.length() - 1) + "á";
        }
        if (male.endsWith("a")) {
            return male.substring(0, male.length() - 1) + "ová";
        }
        if (male.endsWith("ek")) {
            return male.substring(0, male.length() - 2) + "ková";
        }
        return male + "ová";
    }

    private static List<Surname> surnames(List<String> lines) {
        List<Surname> surnames = new ArrayList<>();
        for (String line : distinct(lines)) {
            String[] forms = line.split(";");
            String male = forms[0].trim();
            String female = forms.length > 1 ? forms[1].trim() : femaleForm(male);
            surnames.add(new Surname(male, female));
        }
        return Collections.unmodifiableList(surnames);
    }

    private static List<String> distinct(List<String> words) {
        return Collections.unmodifiableList(new ArrayList<>(new LinkedHashSet<>(words)));
    }

    private static Set<String> normalized(List<String> words) {
        Set<String> keys = new HashSet<>();
        for (String word : words) {
            keys.add(CzechText.normalize(word));
        }
        return Collections.unmodifiableSet(keys);
    }
}
