package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class LastNameAnonymizer extends AbstractMaStringAnonymizer {
    String[] names;

    public LastNameAnonymizer() throws IOException {
       names = readLines("names.last");
    }

    @Override
    public String getType() {
        return "LASTNAME";
    }

    @Override
    public String getRandom() {
        return names[random.nextInt(names.length)];
    }
}
