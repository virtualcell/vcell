package org.vcell.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;
import org.vcell.util.matrix.ArrayMatrix;

import static org.junit.Assert.*;

@Category(Fast.class)
public class ArrayMatrixTest {

    @Test
    public void checkConstructorsTest(){
        ArrayMatrix<String> stringMatrix0 = new ArrayMatrix<>();
        ArrayMatrix<String> stringMatrix1 = new ArrayMatrix<>("m1");
        ArrayMatrix<String> stringMatrix2 = new ArrayMatrix<>("m2", 3, 3);
    }

    @Test
    public void checkDimensionsTest(){
        ArrayMatrix<String> stringMatrix = new ArrayMatrix<>("", 3, 3);
        assertSame(stringMatrix.getWidth(), 0);
        assertSame(stringMatrix.getLength(), 0);
    }

}
