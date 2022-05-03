package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class CompanyNameAnonymizer extends AbstractMaStringAnonymizer {
    String[] names;
    String [] formManes;

    public CompanyNameAnonymizer() throws IOException {
       names = readLines("company.names");
       formManes = readLines("companyForm.names");
    }

    @Override
    public String getType() {
        return "COMPANYNAME";
    }

    @Override
    public String getRandom() {
        return String.format("%s %s %s",names[random.nextInt(names.length)],names[random.nextInt(names.length)], formManes[random.nextInt(formManes.length)]);
    }

}
