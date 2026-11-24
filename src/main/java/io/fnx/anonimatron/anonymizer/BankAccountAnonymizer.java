package io.fnx.anonimatron.anonymizer;

import java.util.Map;

/**
 * BANKACCOUNT: Czech account "prefix-number/bank" passing the mod 11 check, same bank code;
 * other formats (IBAN, foreign) keep their shape with masked digits.
 */
public class BankAccountAnonymizer extends DeterministicAnonymizer {

    @Override
    public String getType() {
        return "BANKACCOUNT";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        String account = CzechBankAccount.similar(from, random(from));
        return account != null ? account : CzechText.maskDigits(from, random(from));
    }
}
