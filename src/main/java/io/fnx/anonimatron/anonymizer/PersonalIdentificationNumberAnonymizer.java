package io.fnx.anonimatron.anonymizer;

import io.fnx.anonimatron.tools.RcGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class PersonalIdentificationNumberAnonymizer  extends AbstractMaStringAnonymizer {

    @Override
    public String getType() {
        return "RC";
    }

    public String getRandom() {
        return RcGenerator.generateRc();
    }
}
