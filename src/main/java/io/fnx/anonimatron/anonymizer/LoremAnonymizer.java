package io.fnx.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:02
 */
public class LoremAnonymizer extends AbstractMaStringAnonymizer {
    String[] names;

    public LoremAnonymizer() throws IOException {
       names = readLines("lorem.txt");
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {

        int maxLenght = 25;
        if(parameters!=null && parameters.containsKey("maxLenght")){
            maxLenght = Integer.parseInt(parameters.get("maxLenght"));
        }

        String ramdomString;

            if (from == null) {
                ramdomString = null;
            } else if (from instanceof String) {
                ramdomString = getRandom(maxLenght);

                if (ramdomString.length() > size) {
                    throw new UnsupportedOperationException("Can not generate a random hex string with length " + size
                            + ". Generated String size is " + ramdomString.length() + " characters.");
                }
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



    public String getRandom(int size) {
        StringBuilder sb = new StringBuilder();
        String word = getRandom();
        word = ucfirst(word);
        while (sb.length()+word.length()<=size-1){
            String afterString;
            int after = random.nextInt(11);
            if(after<7){
                afterString=" ";
            } else if(after<=9){
                afterString=", ";
            } else {
                afterString=". ";
            }
            sb.append(word).append(afterString);
            word = afterString.startsWith(".")? ucfirst(getRandom()) : getRandom() ;
        }
        return sb.toString();
    }

    private String ucfirst(String word) {
        return word.substring(0,1).toLowerCase().concat(word.substring(1));
    }

    @Override
    public String getType() {
        return "LOREM";
    }

    @Override
    public String getRandom() {
        return names[random.nextInt(names.length)];
    }
}
