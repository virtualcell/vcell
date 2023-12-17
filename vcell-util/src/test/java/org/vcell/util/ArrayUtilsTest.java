package org.vcell.util;

import org.junit.jupiter.api.Tag;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import static org.junit.Assert.*;

@Tag("Fast")
public class ArrayUtilsTest {

    @Test
    public void addElementTest(){
        Integer[] testArray = new Integer[0];
        testArray = ArrayUtils.addElement(testArray, 7);
        assertSame(testArray.length, 1);
        Integer[] testArray2 = ArrayUtils.addElement(testArray, 4);
        assertSame(testArray.length, 1);
        assertSame(testArray2.length, 2);
        assertThrows(NullPointerException.class, () -> ArrayUtils.addElement(null, 2));
    }

    @Test
    public void addElementsTest(){
        Integer[] testArray = new Integer[0];
        Integer[] staticArray = new Integer[]{1, 2, 3, 4};
        testArray = ArrayUtils.addElements(testArray, staticArray);
        assertThrows(NullPointerException.class, () -> ArrayUtils.addElements(null, staticArray));
        Integer[] finalizedTestArray = testArray;
        assertThrows(NullPointerException.class, () -> ArrayUtils.addElements(finalizedTestArray, null));
    }

    @Test
    public void arrayContainsTest(){
        Integer[] staticArray = new Integer[]{1, 2, 3, 4};
        assertTrue(ArrayUtils.arrayContains(staticArray, 3));
        assertFalse(ArrayUtils.arrayContains(staticArray, 5));
        assertFalse(ArrayUtils.arrayContains(null, 3));
    }

    @Test
    public void firstIndexOfTest(){
        Integer[] testArray = new Integer[4];
        testArray[0] = 2;
        testArray[1] = 4;
        testArray[2] = 3;
        testArray[3] = 4;
        assertSame(ArrayUtils.firstIndexOf(testArray, 4), 1);
        testArray[1] = 1;
        assertSame(ArrayUtils.firstIndexOf(testArray, 4), 3);
        testArray[3] = 1;
        assertSame(ArrayUtils.firstIndexOf(testArray, 4), -1);
        assertSame(ArrayUtils.firstIndexOf(null, 4), -1);
    }

    @Test
    public void removeFirstInstanceOfElementTest(){
        Integer[] testArray = new Integer[4];
        testArray[0] = 2;
        testArray[1] = 4;
        testArray[2] = 3;
        testArray[3] = 4;
        testArray = ArrayUtils.removeFirstInstanceOfElement(testArray, 4);
        assertSame(testArray.length, 3);
        testArray = ArrayUtils.removeFirstInstanceOfElement(testArray, 4);
        assertSame(testArray.length, 2);
        Integer[] finalizedTestArray = testArray;
        assertThrows(RuntimeException.class, () -> ArrayUtils.removeFirstInstanceOfElement(finalizedTestArray, 4));
        assertNull(ArrayUtils.removeFirstInstanceOfElement(null, 4));
    }
}
