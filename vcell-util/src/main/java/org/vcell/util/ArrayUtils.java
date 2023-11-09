package org.vcell.util;

import com.sun.istack.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class ArrayUtils {

    public static <T> T[] addElement(T[] originalArray, T element) {
        if (originalArray == null) return null;
        T[] largerArray = Arrays.copyOf(originalArray, originalArray.length + 1);
        largerArray[originalArray.length] = element;
        return largerArray;
    }

    public static <T> T[] addElements(T[] firstArray, T[] appendingArray) {
        if (firstArray == null) return null;
        if (appendingArray == null) return firstArray;
        T[] largerArray = Arrays.copyOf(firstArray, firstArray.length + appendingArray.length);
        System.arraycopy(appendingArray, 0, largerArray, firstArray.length, appendingArray.length);
        return largerArray;
    }

    public static <T> boolean arrayContains(T[] subjectArray, T wantedElement) {
        if (subjectArray == null || wantedElement == null ) return false;
        for (T element : subjectArray) if (wantedElement.equals(element)) return true;
        return false;
    }

    public static <T> int firstIndexOf(T[] subjectArray, T wantedElement){
        if (subjectArray == null) return -1;
        for (int i = 0; i < subjectArray.length; i++){
            if (wantedElement.equals(subjectArray[i]))
                return i;
        }
        return -1;
    }

    public static <T> T[] removeFirstInstanceOfElement(T[] array, T element) {
        if (array == null) return null;
        @SuppressWarnings("unchecked")
        T[] newArray = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
        try {
            for (int i = 0, j = 0; i < array.length; i++, j++){
                if (element.equals(array[i]) && i == j) { // i == j to prevent more than just first occurrence
                    j--;
                } else {
                    newArray[j] = array[i];
                }
            }
            return newArray;
        } catch (IndexOutOfBoundsException e){
            throw new RuntimeException("Error removing " + element + ": element not in object array");
            // Todo: consider explicitly throwing IndexOutOfBoundsException instead of implicit runtime exception.
        }
    }
}
