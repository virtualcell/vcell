package cbit.vcell.modeldb;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class PublicationTableTest {

    @Test
    public void testParseBioModelRefs_simple() {
        String bmRefs = "[100;MyModel;200;jsmith;0;1]";
        BioModelReferenceRep[] refs = PublicationTable.parseBioModelRefs(bmRefs);
        assertEquals(1, refs.length);
        assertEquals("100", refs[0].getBmKey().toString());
        assertEquals("MyModel", refs[0].getName());
        assertEquals("200", refs[0].getOwner().getID().toString());
        assertEquals("jsmith", refs[0].getOwner().getName());
        assertEquals(0L, refs[0].getVersionFlag());
        assertEquals(1, refs[0].getPrivacy());
    }

    @Test
    public void testParseBioModelRefs_multiple() {
        String bmRefs = "[100;Model A;200;alice;0;1@@300;Model B;400;bob;2;0]";
        BioModelReferenceRep[] refs = PublicationTable.parseBioModelRefs(bmRefs);
        assertEquals(2, refs.length);
        assertEquals("Model A", refs[0].getName());
        assertEquals("Model B", refs[1].getName());
    }

    @Test
    public void testParseBioModelRefs_nameWithComma() {
        // This was the production bug: model names containing commas broke the old comma-based split
        String bmRefs = "[100;Smith, Jones 2024;200;jsmith;0;1@@300;Other Model;400;bob;2;0]";
        BioModelReferenceRep[] refs = PublicationTable.parseBioModelRefs(bmRefs);
        assertEquals(2, refs.length);
        assertEquals("Smith, Jones 2024", refs[0].getName());
        assertEquals("100", refs[0].getBmKey().toString());
        assertEquals("Other Model", refs[1].getName());
        assertEquals("300", refs[1].getBmKey().toString());
    }

    @Test
    public void testParseBioModelRefs_withoutPrivacy() {
        String bmRefs = "[100;MyModel;200;jsmith;0]";
        BioModelReferenceRep[] refs = PublicationTable.parseBioModelRefs(bmRefs);
        assertEquals(1, refs.length);
        assertNull(refs[0].getPrivacy());
    }

    @Test
    public void testParseBioModelRefs_nullAndEmpty() {
        assertEquals(0, PublicationTable.parseBioModelRefs(null).length);
        assertEquals(0, PublicationTable.parseBioModelRefs("").length);
    }

    @Test
    public void testParseMathModelRefs_simple() {
        String mmRefs = "[100;MyMathModel;200;jsmith;0]";
        MathModelReferenceRep[] refs = PublicationTable.parseMathModelRefs(mmRefs);
        assertEquals(1, refs.length);
        assertEquals("100", refs[0].getMmKey().toString());
        assertEquals("MyMathModel", refs[0].getName());
        assertEquals("jsmith", refs[0].getOwner().getName());
        assertEquals(0L, refs[0].getVersionFlag());
    }

    @Test
    public void testParseMathModelRefs_nameWithComma() {
        String mmRefs = "[100;Model, revised;200;jsmith;0@@300;Other;400;bob;2]";
        MathModelReferenceRep[] refs = PublicationTable.parseMathModelRefs(mmRefs);
        assertEquals(2, refs.length);
        assertEquals("Model, revised", refs[0].getName());
        assertEquals("Other", refs[1].getName());
    }

    @Test
    public void testParseMathModelRefs_nullAndEmpty() {
        assertEquals(0, PublicationTable.parseMathModelRefs(null).length);
        assertEquals(0, PublicationTable.parseMathModelRefs("").length);
    }
}
