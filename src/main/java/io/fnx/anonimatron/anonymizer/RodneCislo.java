package io.fnx.anonimatron.anonymizer;

import java.time.YearMonth;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Czech birth number (rodné číslo): YYMMDD plus a 3 digit suffix (born before 1954) or a 4 digit
 * suffix making the whole number divisible by 11. Women have 50 added to the month.
 */
final class RodneCislo {

    private static final Pattern FORMAT = Pattern.compile("(\\d{2})(\\d{2})(\\d{2})(/?)(\\d{3,4})");
    private static final int FIRST_TEN_DIGIT_YEAR = 1954;
    private static final int YEAR_SPREAD = 2;

    final int year;
    final Gender gender;
    final boolean slash;

    private RodneCislo(int year, Gender gender, boolean slash) {
        this.year = year;
        this.gender = gender;
        this.slash = slash;
    }

    /** Year, gender and format of a birth number, or null when the value is not one. */
    static RodneCislo parse(String value) {
        Matcher matcher = FORMAT.matcher(value.trim());
        if (!matcher.matches()) {
            return null;
        }
        int yy = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        boolean tenDigits = matcher.group(5).length() == 4;
        Gender gender = month > 50 ? Gender.FEMALE : Gender.MALE;
        int baseMonth = month % 50 > 20 ? month % 50 - 20 : month % 50;
        if (baseMonth < 1 || baseMonth > 12) {
            return null;
        }
        int year = tenDigits && yy < 54 ? 2000 + yy : 1900 + yy;
        return new RodneCislo(year, gender, !matcher.group(4).isEmpty());
    }

    /**
     * A new valid birth number of the same gender, born within two years of the original and
     * in the same format.
     */
    String similar(Random random) {
        int newYear = year - YEAR_SPREAD + random.nextInt(2 * YEAR_SPREAD + 1);
        if (year >= FIRST_TEN_DIGIT_YEAR) {
            newYear = Math.max(newYear, FIRST_TEN_DIGIT_YEAR);
        } else {
            newYear = Math.min(newYear, FIRST_TEN_DIGIT_YEAR - 1);
        }
        return generate(random, newYear, gender, slash);
    }

    static String generate(Random random, int year, Gender gender, boolean slash) {
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(YearMonth.of(year, month).lengthOfMonth());
        String date = String.format("%02d%02d%02d", year % 100, gender == Gender.FEMALE ? month + 50 : month, day);
        String suffix = String.format("%03d", random.nextInt(1000));
        if (year >= FIRST_TEN_DIGIT_YEAR) {
            long first9 = Long.parseLong(date + suffix);
            suffix += (first9 % 11) % 10;
        }
        return date + (slash ? "/" : "") + suffix;
    }

    static boolean isValid(String value) {
        RodneCislo parsed = parse(value);
        if (parsed == null) {
            return false;
        }
        String digits = CzechText.digitsOnly(value);
        if (digits.length() == 9) {
            return parsed.year < FIRST_TEN_DIGIT_YEAR;
        }
        long first9 = Long.parseLong(digits.substring(0, 9));
        return (first9 % 11) % 10 == digits.charAt(9) - '0';
    }
}
