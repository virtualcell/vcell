package org.vcell.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CastingUtilsTest {

    @Test
    public void downcastTest( ) {
        Random rng = new Random();
        String str = rng.nextInt() % 2 > 0 ? "GUI" : "CLI"; // Fake random to satisfy compiler's redundancy complaints
        Object objStr = str.substring(0,3);
        String backStr = CastingUtils.downcast(String.class, objStr);
        assertSame(str, backStr);
        Integer backInt = CastingUtils.downcast(Integer.class, objStr);
        assertNull(backInt);
    }
}
