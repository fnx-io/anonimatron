package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.anonymizer.StringAnonymizer;
import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public abstract class AbstractMaStringAnonymizer extends StringAnonymizer {

    Random random = new Random();
    @Override
    public abstract String getType();


    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        String ramdomString;

        if (from == null) {
            ramdomString = null;
        } else if (from instanceof String) {
            ramdomString = getRandom();
        } else {
            throw new UnsupportedOperationException("Can not anonymize objects of type " + from.getClass());
        }

        return new StringSynonym(
                getType(),
                (String) from,
                ramdomString,
                shortlived
        );
    }

    protected String[] readLines(String fileName) throws IOException {
        InputStream inputStream = AbstractMaStringAnonymizer.class.getResourceAsStream(fileName);
        BufferedReader bufRead = new BufferedReader(new InputStreamReader(inputStream));

        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufRead.readLine()) != null) {
            lines.add(line);
        }
        bufRead.close();
        return lines.toArray(new String[0]);
    }

    public abstract String getRandom();
}
