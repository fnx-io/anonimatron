package io.fnx.anonimatron.anonymizer;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CzechFakerTest {

    private static final String SALT = "faker-salt";

    /** Anonymizing the result again with the same row key must give the same result. */
    private static void assertIdempotent(BiFunction<String, Random, String> faker, List<String> originals) {
        for (int row = 0; row < 200; row++) {
            for (String original : originals) {
                String key = "table.column:" + row;
                String once = faker.apply(original, CzechFaker.random(SALT, key));
                String twice = faker.apply(once, CzechFaker.random(SALT, key));
                assertEquals(original + " -> " + once, once, twice);
            }
        }
    }

    @Test
    public void everythingIsIdempotent() {
        assertIdempotent(CzechFaker::firstNames, Arrays.asList("Jana", "Jan", "Dagmar", "jiří", "Jan Pavel", "Xaver"));
        assertIdempotent(CzechFaker::surnames, Arrays.asList("Novák", "Nováková", "Krejčí", "Svoboda", "Malá", "9999"));
        assertIdempotent(CzechFaker::people, Arrays.asList("Ing. Jana Nováková, Ph.D.", "Novák Jan, Petr Svoboda",
                "jednatel Karel Dvořák", "jan vzorový", "Marie Procházková - lesní práce"));
        assertIdempotent(CzechFaker::company, Arrays.asList("Lesy Šumava, s.r.o.", "Jan Novák", "Agrodružstvo Kvilda",
                "Dřevo Kašperky a.s.", "jan vzorový", "obec Kvilda"));
        assertIdempotent(CzechFaker::street, Arrays.asList("Nádražní", "1. máje 12", "Hlavní 12/3"));
        assertIdempotent(CzechFaker::phone, Arrays.asList("+420 777 123 456", "388111222", "602123456, 388111222",
                "neuvedeno", "038 012 045", "0 0 0", "00420 601 000 000"));
        assertIdempotent(CzechFaker::bankAccount, Arrays.asList("19-2000145399/0800", "5-123456789/0100", "123/0300",
                "SK31 1200", "000019-0123456789/0100", "012345-012 0100", "19-2000145399/ 0800"));
        assertIdempotent(CzechFaker::identifier, Arrays.asList("25596641", "6947", "00006947", "CZ25596641", "CZ 25596641",
                "CZ7655011237", "7655011237", "765501/1237", "455101123", "999999999", "X12345678", "neplátce", "9",
                "0123456/0123", "012 34 567", "CZ0123456789"));
        assertIdempotent(CzechFaker::birthNumber, Arrays.asList("801224/1236", "455101123", "9912311234", "nesmysl 12", "0001011234"));
        assertIdempotent(CzechFaker::maskDigits, Arrays.asList("C 12345 vedená u KS", "LL-2020/15", "xx", "0", "007",
                "ABC 0012/03/XY", "XX 00000000"));
    }

    @Test
    public void shapesAreKept() {
        Random random = CzechFaker.random(SALT, "les_supplier:1");
        assertTrue(Ico.isValid(CzechFaker.identifier("6947", random)));
        String dic = CzechFaker.identifier("CZ7655011237", random);
        RodneCislo rc = RodneCislo.parse(dic.substring(2));
        assertEquals(Gender.FEMALE, rc.gender);
        assertTrue(dic, rc.year >= 1975 && rc.year <= 1979);
        assertTrue(CzechFaker.company("Lesy Šumava, s.r.o.", random).endsWith(", s.r.o."));
        assertTrue(CzechNames.femaleFirstNames().contains(CzechFaker.firstNames("Jana", random)));
    }

    @Test
    public void differentRowsGetDifferentValues() {
        assertNotEquals(CzechFaker.identifier("25596641", CzechFaker.random(SALT, "t:1")),
                CzechFaker.identifier("25596641", CzechFaker.random(SALT, "t:2")));
    }
}
