package io.fnx.anonimatron.anonymizer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class ProjectNameAnonymizer extends AbstractMaStringAnonymizer {
    String[] names1;
    String[] names2;
    String[] names3;

    public ProjectNameAnonymizer() throws IOException {
       names1 = readLines("project.names.1");
       names2 = readLines("project.names.2");
       names3 = readLines("project.names.3");
    }

    @Override
    public String getType() {
        return "PROJECTNAME";
    }

    @Override
    public String getRandom() {
        return String.format("%s %s %s",names1[random.nextInt(names1.length)],names2[random.nextInt(names2.length)],names3[random.nextInt(names3.length)]);
    }
}
