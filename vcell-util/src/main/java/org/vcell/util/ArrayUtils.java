package org.vcell.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class ArrayUtils {

    public static <T> T[] addElement(T[] originalArray, T element) {
        T[] largerArray = Arrays.copyOf(originalArray, originalArray.length + 1);
        largerArray[originalArray.length] = element;
        return largerArray;
    }

    public static <T> T[] addElements(T[] firstArray, T[] appendingArray) {
        T[] largerArray = Arrays.copyOf(firstArray, firstArray.length + appendingArray.length);
        System.arraycopy(appendingArray, 0, largerArray, firstArray.length, appendingArray.length);
        return largerArray;
    }

    public static <T> boolean arrayContains(T[] objects, T object) {
        if (object == null || objects == null) return false;
        for (T obj : objects) if (object.equals(obj)) return true;
        return false;
    }

    public static <T> boolean arrayEquals(T[] a1, T[] a2) {
        return a1 == a2 || (a1 != null && a2 != null && Arrays.equals(a1, a2));
    }

    public static <T> T[] removeFirstInstanceOfElement(T[] array, T element) {
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

    /**
     * filter subtype out of a collection
     * @param clzz non null
     * @param coll non null
     * @return list containing elements from coll of type clzz
     */
    public static <T>  List<T> filterCollection(Class<T> clzz, Collection<?> coll) {
        Objects.requireNonNull(clzz);
        Objects.requireNonNull(coll);
        return coll.stream( )
                .filter(clzz::isInstance)
                .map(clzz::cast)
                .collect(Collectors.toList());
    }
}
