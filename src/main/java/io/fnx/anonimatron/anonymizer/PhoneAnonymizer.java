package io.fnx.anonimatron.anonymizer;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class PhoneAnonymizer extends AbstractMaStringAnonymizer {

    @Override
    public String getType() {
        return "PHONE";
    }


    @Override
    public String getRandom() {
        StringBuilder numero = new StringBuilder();
        for (int i = 0; i<8;i++) numero.append(random.nextInt(10));
        return String.format("+02%s",numero);

    }

}
