package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class BankNameAnonymizer extends AbstractMaStringAnonymizer {
    String[] names;

    public BankNameAnonymizer() throws IOException {
       names = readLines("bank.names");
    }

    @Override
    public String getType() {
        return "BANKNAME";
    }

    @Override
    public String getRandom() {
        return names[random.nextInt(names.length)];
    }
}
