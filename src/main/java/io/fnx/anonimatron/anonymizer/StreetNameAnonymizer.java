package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class StreetNameAnonymizer extends AbstractMaStringAnonymizer {
    String[] names;

    public StreetNameAnonymizer() throws IOException {
       names = readLines("address.street");
    }

    @Override
    public String getType() {
        return "STREET";
    }

    @Override
    public String getRandom() {
        return names[random.nextInt(names.length)];
    }
}
