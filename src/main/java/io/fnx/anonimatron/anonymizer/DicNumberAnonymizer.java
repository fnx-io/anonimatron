package io.fnx.anonimatron.anonymizer;

import io.fnx.anonimatron.tools.IcoGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class DicNumberAnonymizer extends AbstractMaStringAnonymizer {

    @Override
    public String getType() {
        return "DIC";
    }

    @Override
    public String getRandom() {
        return IcoGenerator.generateDic();
    }
}
