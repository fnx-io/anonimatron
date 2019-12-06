package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class FirstNameAnonymizer extends AbstractMaStringAnonymizer {
    String[] names;

    public FirstNameAnonymizer() throws IOException {
       names = readLines("names.first");
    }

    @Override
    public String getType() {
        return "FIRSTNAME";
    }

    @Override
    public String getRandom() {
        return names[random.nextInt(names.length)];
    }
}
