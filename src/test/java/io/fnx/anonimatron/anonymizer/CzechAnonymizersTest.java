package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.configuration.Column;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class CzechAnonymizersTest {

    private static final String SALT = "test-salt";

    private static <T extends DeterministicAnonymizer> T salted(T anonymizer, String salt) {
        anonymizer.setSalt(salt);
        return anonymizer;
    }

    private static <T extends DeterministicAnonymizer> T salted(T anonymizer) {
        return salted(anonymizer, SALT);
    }

    private static String anonymize(DeterministicAnonymizer anonymizer, String value) {
        return (String) anonymizer.anonymize(value, 0, false).getTo();
    }

    @Test
    public void sameSaltGivesSameResultAcrossInstances() {
        for (DeterministicAnonymizer anonymizer : Arrays.asList(new FullNameAnonymizer(), new CompanyNameAnonymizer(),
                new StreetNameAnonymizer(), new PhoneAnonymizer(), new EmailAnonymizer(), new BankAccountAnonymizer())) {
            anonymizer.setSalt(SALT);
        }
        assertEquals(anonymize(salted(new FullNameAnonymizer()), "Jan Novák"), anonymize(salted(new FullNameAnonymizer()), "Jan Novák"));
        assertEquals(anonymize(salted(new EmailAnonymizer()), "jan@novak.cz"), anonymize(salted(new EmailAnonymizer()), "jan@novak.cz"));
        assertEquals(anonymize(salted(new StreetNameAnonymizer()), "Nádražní 12"), anonymize(salted(new StreetNameAnonymizer()), "Nádražní 12"));
    }

    @Test
    public void differentSaltGivesDifferentResult() {
        assertNotEquals(anonymize(salted(new EmailAnonymizer(), "one"), "jan@novak.cz"),
                anonymize(salted(new EmailAnonymizer(), "two"), "jan@novak.cz"));
    }

    @Test
    public void refusesToWorkWithoutSalt() {
        assertThrows(IllegalStateException.class, () -> anonymize(new FirstNameAnonymizer(), "Jan"));
    }

    @Test
    public void blankValuesStayBlank() {
        assertEquals("", anonymize(salted(new FullNameAnonymizer()), ""));
        assertEquals("  ", anonymize(salted(new IcoNumberAnonymizer()), "  "));
    }

    @Test
    public void firstNameKeepsGenderAndCase() {
        FirstNameAnonymizer anonymizer = salted(new FirstNameAnonymizer());
        for (String woman : Arrays.asList("Jana", "Marie", "Dagmar", "Ludmila")) {
            String name = anonymize(anonymizer, woman);
            assertTrue(woman + " -> " + name, CzechNames.femaleFirstNames().contains(name));
            assertNotEquals(woman, name);
        }
        for (String man : Arrays.asList("Jan", "Jiří", "Josef")) {
            assertTrue(CzechNames.maleFirstNames().contains(anonymize(anonymizer, man)));
        }
        String upper = anonymize(anonymizer, "PETR");
        assertEquals(upper.toUpperCase(), upper);
    }

    @Test
    public void maleAndFemaleSurnameGetTheSameFamily() {
        LastNameAnonymizer anonymizer = salted(new LastNameAnonymizer());
        assertFamily(anonymize(anonymizer, "Novák"), anonymize(anonymizer, "Nováková"));
        assertFamily(anonymize(anonymizer, "Svoboda"), anonymize(anonymizer, "Svobodová"));
        assertFamily(anonymize(anonymizer, "Novotný"), anonymize(anonymizer, "Novotná"));
        assertFamily(anonymize(anonymizer, "Hájek"), anonymize(anonymizer, "Hájková"));
    }

    private static void assertFamily(String male, String female) {
        CzechNames.Surname family = CzechNames.surnames().stream().filter(s -> s.male.equals(male)).findFirst()
                .orElseThrow(() -> new AssertionError(male + " is not a male surname"));
        assertEquals(family.female, female);
    }

    @Test
    public void fullNameMatchesSeparateColumnsAndKeepsTitles() {
        String full = anonymize(salted(new FullNameAnonymizer()), "Ing. Jana Nováková, Ph.D.");
        String first = anonymize(salted(new FirstNameAnonymizer()), "Jana");
        String last = anonymize(salted(new LastNameAnonymizer()), "Nováková");
        assertEquals("Ing. " + first + " " + last + ", Ph.D.", full);
    }

    @Test
    public void fullNameHandlesSurnameFirstAndSeveralPeople() {
        FullNameAnonymizer anonymizer = salted(new FullNameAnonymizer());
        String first = anonymize(salted(new FirstNameAnonymizer()), "Jana");
        String last = anonymize(salted(new LastNameAnonymizer()), "Nováková");
        String petr = anonymize(salted(new FirstNameAnonymizer()), "Petr");
        String svoboda = anonymize(salted(new LastNameAnonymizer()), "Svoboda");
        assertEquals(last + " " + first + ", " + petr + " " + svoboda,
                anonymize(anonymizer, "Nováková Jana, Petr Svoboda"));
        assertEquals("jednatel " + petr + " " + svoboda, anonymize(anonymizer, "jednatel Petr Svoboda"));
    }

    @Test
    public void lowerCaseFullNameIsStillAnonymized() {
        String result = anonymize(salted(new FullNameAnonymizer()), "jan novák");
        assertFalse(result, result.contains("jan") || result.contains("novák"));
    }

    @Test
    public void companyKeepsLegalFormAndIsUnique() {
        CompanyNameAnonymizer anonymizer = salted(new CompanyNameAnonymizer());
        String company = anonymize(anonymizer, "Lesy Šumava, s.r.o.");
        assertTrue(company, company.endsWith(", s.r.o."));
        assertFalse(company, company.contains("Šumava"));
        assertTrue(anonymize(anonymizer, "Dřevo Kašperky a.s.").endsWith(" a.s."));
        Set<String> names = new HashSet<>();
        for (int i = 0; i < 2000; i++) {
            names.add(anonymize(anonymizer, "Firma " + i + " spol. s r.o."));
        }
        assertEquals(2000, names.size());
    }

    @Test
    public void soleTraderNameIsAnonymizedAsPerson() {
        assertEquals(anonymize(salted(new FullNameAnonymizer()), "Jan Novák"),
                anonymize(salted(new CompanyNameAnonymizer()), "Jan Novák"));
    }

    @Test
    public void icoIsValidAndUnique() {
        IcoNumberAnonymizer anonymizer = salted(new IcoNumberAnonymizer(), "ico-salt");
        Set<String> icos = new HashSet<>();
        for (int i = 0; i < 5000; i++) {
            String ico = anonymize(anonymizer, String.format("%08d", i * 1997));
            assertTrue(ico, Ico.isValid(ico));
            icos.add(ico);
        }
        assertEquals(5000, icos.size());
        assertEquals(anonymize(anonymizer, "00006947"), anonymize(anonymizer, "6947"));
        assertTrue(Ico.isValid("00006947"));
        assertFalse(Ico.isValid("00006948"));
    }

    @Test
    public void dicFollowsIco() {
        String ico = anonymize(salted(new IcoNumberAnonymizer()), "25596641");
        assertEquals("CZ" + ico, anonymize(salted(new DicNumberAnonymizer()), "CZ25596641"));
        assertEquals("CZ " + ico, anonymize(salted(new DicNumberAnonymizer()), "CZ 25596641"));
    }

    @Test
    public void dicOfPersonIsBirthNumberOfSameGenderAndAge() {
        String dic = anonymize(salted(new DicNumberAnonymizer()), "CZ7655011237");
        assertTrue(dic, dic.matches("CZ\\d{10}"));
        RodneCislo rc = RodneCislo.parse(dic.substring(2));
        assertEquals(Gender.FEMALE, rc.gender);
        assertTrue(dic, Math.abs(rc.year - 1976) <= 2);
        assertTrue(dic, RodneCislo.isValid(dic.substring(2)));
    }

    @Test
    public void birthNumberKeepsFormatGenderAndAge() {
        PersonalIdentificationNumberAnonymizer anonymizer = salted(new PersonalIdentificationNumberAnonymizer());
        String male = anonymize(anonymizer, "801224/1236");
        assertTrue(male, male.matches("\\d{6}/\\d{4}"));
        assertTrue(male, RodneCislo.isValid(male));
        assertEquals(Gender.MALE, RodneCislo.parse(male).gender);
        assertTrue(male, Math.abs(RodneCislo.parse(male).year - 1980) <= 2);

        String old = anonymize(anonymizer, "455101123");
        assertTrue(old, old.matches("\\d{9}"));
        assertEquals(Gender.FEMALE, RodneCislo.parse(old).gender);
        assertTrue(old, RodneCislo.parse(old).year < 1954);
        assertNotEquals("801224/1236", male);
    }

    @Test
    public void birthNumbersAreUnique() {
        PersonalIdentificationNumberAnonymizer anonymizer = salted(new PersonalIdentificationNumberAnonymizer(), "rc-salt");
        Set<String> numbers = new HashSet<>();
        for (int i = 0; i < 3000; i++) {
            numbers.add(anonymize(anonymizer, String.format("8001%02d%04d", 1 + i % 28, i)));
        }
        assertEquals(3000, numbers.size());
    }

    @Test
    public void otherIdentifiersKeepShape() {
        String foreign = anonymize(salted(new IcoNumberAnonymizer()), "X12345678");
        assertTrue(foreign, foreign.matches("X\\d{8}"));
    }

    @Test
    public void bankAccountStaysValidWithSameBank() {
        assertTrue(CzechBankAccount.isValid("19-2000145399/0800"));
        String account = anonymize(salted(new BankAccountAnonymizer()), "19-2000145399/0800");
        assertTrue(account, account.matches("\\d{2}-\\d{10}/0800"));
        assertTrue(account, CzechBankAccount.isValid(account));
        assertNotEquals("19-2000145399/0800", account);
    }

    @Test
    public void phoneKeepsFormatAndPrefix() {
        String phone = anonymize(salted(new PhoneAnonymizer()), "+420 777 123 456");
        assertTrue(phone, phone.matches("\\+420 7\\d\\d \\d{3} \\d{3}"));
        String plain = anonymize(salted(new PhoneAnonymizer()), "602123456, 388111222");
        assertTrue(plain, plain.matches("6\\d{8}, 3\\d{8}"));
    }

    @Test
    public void emailGoesToReservedDomain() {
        EmailAnonymizer anonymizer = salted(new EmailAnonymizer());
        String emails = anonymize(anonymizer, "jan.novak@seznam.cz; Petr@Firma.cz");
        assertTrue(emails, emails.matches("anonym\\.[a-z0-9]{10}@example\\.com; anonym\\.[a-z0-9]{10}@example\\.com"));
        assertEquals(anonymize(anonymizer, "petr@firma.cz"), emails.substring(emails.indexOf("; ") + 2));
    }

    @Test
    public void numberMaskIsUnique() {
        NumberMaskAnonymizer anonymizer = salted(new NumberMaskAnonymizer());
        Set<String> values = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            String value = anonymize(anonymizer, "LL-" + (100 + i));
            assertTrue(value, value.matches("LL-[1-9]\\d\\d"));
            values.add(value);
        }
        assertEquals(500, values.size());
    }

    @Test
    public void numberMaskNeverReturnsTheOriginal() {
        NumberMaskAnonymizer anonymizer = salted(new NumberMaskAnonymizer(), "local-test-salt-1234");
        for (int i = 1; i < 90; i++) {
            assertNotEquals(String.valueOf(i), anonymize(anonymizer, String.valueOf(i)));
        }
    }

    @Test
    public void lowerCaseSoleTraderIsAnonymizedAsPerson() {
        assertEquals(anonymize(salted(new FullNameAnonymizer()), "jan vzorový"),
                anonymize(salted(new CompanyNameAnonymizer()), "jan vzorový"));
        String person = anonymize(salted(new CompanyNameAnonymizer()), "jan vzorový");
        assertEquals(person.toLowerCase(), person);
    }

    @Test
    public void streetKeepsHouseNumberShape() {
        String street = anonymize(salted(new StreetNameAnonymizer()), "Nádražní 1234/5");
        assertTrue(street, street.matches(".+ [1-9]\\d{3}/[1-9]"));
    }

    @Test
    public void loremRespectsMaxLength() {
        LoremAnonymizer anonymizer = salted(new LoremAnonymizer());
        Map<String, String> parameters = Collections.singletonMap("maxLenght", "40");
        String text = (String) anonymizer.anonymize("Poznámka s Janem Novákem", 0, false, parameters).getTo();
        assertTrue(text, text.length() < 40 && !text.isEmpty());
    }

    @Test
    public void serviceRegistersAnonymizersByClassAndPassesSalt() throws Exception {
        AnonymizerService service = new AnonymizerService();
        service.registerAnonymizers(Collections.singletonList(FullNameAnonymizer.class.getName()));
        service.setSalt(SALT);
        Column column = new Column();
        column.setType("FULLNAME");
        assertEquals(anonymize(salted(new FullNameAnonymizer()), "Jan Novák"), service.anonymize(column, "Jan Novák").getTo());
    }
}
