package org.vcell.util;

import org.junit.Test;

import static org.vcell.util.CastingUtils.CastInfo;

import static org.junit.Assert.*;

public class CastingUtilsTest {

    @Test
    public void downcastTest( ) {
        String str = "VCell";
        Object objStr = str.substring(0, 5); // Ensure second argument is equal to str.length()!
        String backStr = CastingUtils.downcast(String.class, objStr);
        assertSame(str, backStr);
        Integer backInt = CastingUtils.downcast(Integer.class, objStr);
        assertNull(backInt);
    }

    @Test
    public void attemptCastEtAllTest(){
        Object integer = Integer.parseInt("8");
        Object notInteger = Double.parseDouble("3.14");

        // Good Cast
        CastInfo<Integer> goodCast = CastingUtils.attemptCast(Integer.class, integer);
        assertTrue(goodCast.isGood());
        assertEquals("java.lang.Integer", goodCast.requiredName());
        assertEquals("java.lang.Integer", goodCast.actualName());
        assertEquals("cast from java.lang.Integer to java.lang.Integer", goodCast.castMessage());
        assertEquals("8", goodCast.get().toString());

        // Bad Cast
        CastInfo<Integer> badCast = CastingUtils.attemptCast(Integer.class, notInteger);
        assertFalse(badCast.isGood());
        assertEquals("java.lang.Integer", badCast.requiredName());
        assertNotEquals("java.lang.Integer", badCast.actualName());
        assertNotEquals("cast from java.lang.Integer to java.lang.Integer", badCast.castMessage());
        assertThrows(ProgrammingException.class, () -> badCast.get().toString());
    }
}
