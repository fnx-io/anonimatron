package io.fnx.anonimatron;

import io.fnx.anonimatron.anonymizer.CompanyNameAnonymizer;
import io.fnx.anonimatron.anonymizer.LoremAnonymizer;
import io.fnx.anonimatron.anonymizer.ProjectNameAnonymizer;
import io.fnx.anonimatron.tools.IcoGenerator;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 15:00
 */
public class MaAnonymizerTest extends TestCase {
    public void testIco() {
        assertTrue(IcoGenerator.generateIco().length()<=8);
        assertTrue(IcoGenerator.generateDic().length()<=10);
    }

    public void testCompanyName() throws IOException {
        CompanyNameAnonymizer ca = new CompanyNameAnonymizer();
        assertEquals(ca.getRandom().split(" ").length,3);
    }

    public void testProjectName() throws IOException {
        ProjectNameAnonymizer pa = new ProjectNameAnonymizer();
        assertTrue(pa.getRandom().split(" ").length>=3);
    }

    public void testLorem() throws IOException {
        LoremAnonymizer pa = new LoremAnonymizer();
        assertTrue(pa.getRandom(100).length()<=100);
    }
}
