package org.vcell.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import static org.vcell.util.CastingUtils.CastInfo;

import static org.junit.Assert.*;

@Category(Fast.class)
public class CastingUtilsTest {

    @Test
    public void downcastTest(){
        String str = "VCell";
        Object objStr = str.substring(0, 5); // Ensure second argument is equal to str.length()!
        String backStr = CastingUtils.downcast(String.class, objStr);
        assertSame(str, backStr);
        Integer backInt = CastingUtils.downcast(Integer.class, objStr);
        assertNull(backInt);
        assertNull(CastingUtils.downcast(Integer.class, null));
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
        assertEquals("java.lang.Double", badCast.actualName());
        assertEquals("cast from java.lang.Double to java.lang.Integer", badCast.castMessage());
        assertThrows(ProgrammingException.class, () -> badCast.get().toString());

        // Null Cast (bad cast)
        CastInfo<Integer> nullCast = CastingUtils.attemptCast(Integer.class, null);
        assertFalse(nullCast.isGood());
        assertEquals("java.lang.Integer", nullCast.requiredName());
        assertEquals("null", nullCast.actualName());
        assertEquals("cast from null to java.lang.Integer", nullCast.castMessage());
        assertThrows(ProgrammingException.class, () -> nullCast.get().toString());
    }
}
