package io.fnx.anonimatron.anonymizer;

import java.util.Map;
import java.util.Random;

/**
 * PROJECTNAME: made-up project name of three words.
 */
public class ProjectNameAnonymizer extends DeterministicAnonymizer {

    private static final WordList FIRST = WordList.load("project.names.1");
    private static final WordList SECOND = WordList.load("project.names.2");
    private static final WordList THIRD = WordList.load("project.names.3");

    @Override
    public String getType() {
        return "PROJECTNAME";
    }

    @Override
    protected String generate(String from, Map<String, String> parameters) {
        Random random = random(from);
        return FIRST.pick(random) + " " + SECOND.pick(random) + " " + THIRD.pick(random);
    }
}
