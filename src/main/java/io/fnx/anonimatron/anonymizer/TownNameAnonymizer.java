package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class TownNameAnonymizer extends AbstractMaStringAnonymizer {
    String[] names;

    public TownNameAnonymizer() throws IOException {
       names = readLines("address.town");
    }

    @Override
    public String getType() {
        return "TOWN";
    }

    @Override
    public String getRandom() {
        return names[random.nextInt(names.length)];
    }
}
