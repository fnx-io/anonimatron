package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class AddressAnonymizer extends AbstractMaStringAnonymizer {
    String[] streets;
    String[] towns;

    public AddressAnonymizer() throws IOException {
        streets = readLines("address.street");
        towns = readLines("address.town");
    }

    @Override
    public String getType() {
        return "ADDRESS";
    }

    @Override
    public String getRandom() {
        return String.format("%s, %s, %s",towns[random.nextInt(towns.length)],streets[random.nextInt(streets.length)],random.nextInt(150) );
    }
}
