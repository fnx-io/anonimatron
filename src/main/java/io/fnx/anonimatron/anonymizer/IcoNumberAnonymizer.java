package io.fnx.anonimatron.anonymizer;

import io.fnx.anonimatron.tools.IcoGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class IcoNumberAnonymizer extends AbstractMaStringAnonymizer {

    @Override
    public String getType() {
        return "ICO";
    }

    @Override
    public String getRandom() {
        return IcoGenerator.generateIco();
    }
}
