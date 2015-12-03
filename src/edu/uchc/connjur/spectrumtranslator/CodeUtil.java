// $Id: CodeUtil.java,v 1.19 2013/03/18 19:55:57 gerard Exp $
package edu.uchc.connjur.spectrumtranslator;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Code utilities for display of data and filtering of collections
 *
 * @author vyas, gerard
 *
 */
public class CodeUtil {
    public static final String NEWLINE = System.getProperty("line.separator");

    /**
     * nicely print an array of integers
     *
     * @param ints
     * @return formatted string representation of each integer
     */
    public static String prettyPrint(int[] ints) {
        StringBuilder sb = new StringBuilder("{");
        if (ints != null) {
            //for loop handles zero length array
            for (int i = 0; i < 1; i++) {
                sb.append(ints[i]);
            }
            for (int i = 1; i < ints.length; i++) {
                sb.append(',');
                sb.append(ints[i]);
            }
        }
        else {
            sb.append("null");
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * nicely print an array of Objects
     *
     * @param array
     *            of objects
     * @return formatted string representation of each integer
     */
    public static String prettyPrint(Object[] array) {
        String s = "";
        int i = 0;
        for (Object k : array) {
            s += i + " " + k;
            if (i < array.length - 1) {
                s += ",";
            }
            i++;
        }
        return s;
    }

    /**
     * nicely print map
     *
     * @param map
     * @return formatted string representation of map
     */
    public static String prettyPrint(Map <? , ? > map) {
        String x = "";
        int i = 0;
        Set<?> set = map.keySet();
        for (Object key : set) {
            Object value = map.get(key);
            x += ++i + " : " + key + " = " + value + NEWLINE;
        }

        return x;
    }

    /**
     * print bytes human style; value > 0.99 with kilo, mega, giga, or tera
     * prefix
     *
     * @param bytes
     * @return reformatted string
     */
    public static String humanBytePrint(long bytes) {
        String prefixes[] = { "", "kilo", "mega", "giga", "tera" };
        int prefixIndex = 0;
        double b = bytes;
        while (b > 1024 && prefixIndex < prefixes.length - 1) {
            b /= 1024;
            prefixIndex++;
        }
        DecimalFormat df = new DecimalFormat("#,###.## ");

        return df.format(b) + prefixes[prefixIndex] + "bytes";
    }

    /**
     * invoke all accessible no-argument methods with return values on Object o
     * and display the results. Warning: this may cause unwanted side-effects if
     * class has no-arg setters
     *
     * @param o
     *            Object to query
     * @param numberParents number of superclasses in inheritance hierarchy to go up
     * @return new line separated list of results
     */
    public static String callStatusMethods(Object o, int numberParents) {
        try {
            StringBuffer buffer = new StringBuffer();
            Class<?> oClass = o.getClass();
            for (int p = 0; p <= numberParents && oClass != null; p++) {
                buffer.append(oClass.getName());
                buffer.append(" methods");
                buffer.append(NEWLINE);
                Method methods[];
                methods = oClass.getDeclaredMethods();
                for (Method m : methods) {
                    boolean noParams = m.getParameterTypes().length == 0;
                    int modifiers = m.getModifiers();
                    boolean isPublic = Modifier.isPublic(modifiers);
                    boolean isNotStatic = !Modifier.isStatic(modifiers);
                    boolean hasReturn = m.getReturnType() != Void.TYPE;
                    if (isPublic && isNotStatic && noParams && hasReturn) {
                        buffer.append(m.getName());
                        buffer.append("() = ");
                        Object result;
                        try {
                            result = m.invoke(o, (Object[]) null);
                        }
                        catch (Exception e) {
                            result = e;
                        }
                        if (result != null) {
                            if (!result.getClass().isArray()) {
                                buffer.append(result.toString());
                            }
                            else {
                                buffer.append(prettyPrint((Object[]) result));
                            }
                        }
                        else {
                            buffer.append("null");
                        }
                        buffer.append(NEWLINE);
                    }
                }
                buffer.append(NEWLINE);
                oClass = oClass.getSuperclass();
            }
            return buffer.toString();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * does string start with any of prefixes?
     * @param stub not null
     * @param prefixes not null
     * @return true if does
     */
    private static boolean startsWith(String stub, Collection<String> prefixes) {
    	Objects.requireNonNull(stub);
    	Objects.requireNonNull(prefixes);
    	for (String p : prefixes) {
    		if (stub.startsWith(p)) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * invoke all accessible no-argument methods with return values on Object o
     * and display the results. Warning: this may cause unwanted side-effects if
     * class has no-arg setters
     *
     * @param o
     *            Object to query
     * @param numberParents number of superclasses in inheritance hierarchy to go up
     * @param prefixes prefixes methods must start with
     * @return new line separated list of results
     */
    public static String callStatusMethods(Object o, int numberParents, Collection<String> prefixes) {
        try {
            StringBuffer buffer = new StringBuffer();
            Class<?> oClass = o.getClass();
            for (int p = 0; p <= numberParents && oClass != null; p++) {
                buffer.append(oClass.getName());
                buffer.append(" methods");
                buffer.append(NEWLINE);
                Method methods[];
                methods = oClass.getDeclaredMethods();
                for (Method m : methods) {
                    boolean noParams = m.getParameterTypes().length == 0;
                    int modifiers = m.getModifiers();
                    boolean isPublic = Modifier.isPublic(modifiers);
                    boolean isNotStatic = !Modifier.isStatic(modifiers);
                    boolean hasReturn = m.getReturnType() != Void.TYPE;
                    if (isPublic && isNotStatic && noParams && hasReturn) {
                        final String name = m.getName();
                        if (startsWith(name,prefixes)) {
                        buffer.append(name);
                        buffer.append("() = ");
                        Object result;
                        try {
                            result = m.invoke(o, (Object[]) null);
                        }
                        catch (Exception e) {
                            result = e;
                        }
                        if (result != null) {
                            if (!result.getClass().isArray()) {
                                buffer.append(result.toString());
                            }
                            else {
                                buffer.append(prettyPrint((Object[]) result));
                            }
                        }
                        else {
                            buffer.append("null");
                        }
                        buffer.append(NEWLINE);
                    }
                    }
                }
                buffer.append(NEWLINE);
                oClass = oClass.getSuperclass();
            }
            return buffer.toString();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * return string representation of object via reflection source
     * http://www.geekyramblings.org/2004/12/13/dump-details-of-java-object/
     *
     * @param o
     * @return String representation of object
     */
    public static String dump(Object o) {
        StringBuffer buffer = new StringBuffer();
        Class<?> oClass = o.getClass();
        buffer.append(oClass.getName());
        if (oClass.isArray()) {
            buffer.append("[");
            for (int i = 0; i < Array.getLength(o); i++) {
                if (i < 0) {
                    buffer.append(",");
                }
                Object value = Array.get(o, i);
                buffer.append(value.getClass().isArray() ? dump(value) : value);
            }
            buffer.append("]");
        }
        else {

            buffer.append("{");
            while (oClass != null) {
                Field[] fields = oClass.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (buffer.length() < 1) {
                        buffer.append(",");
                    }
                    fields[i].setAccessible(true);
                    buffer.append(fields[i].getName());
                    buffer.append("=");
                    try {
                        Object value = fields[i].get(o);
                        if (value != null) {
                            buffer
                            .append(value.getClass().isArray() ? dump(value)
                                    : value);
                        }
                    }
                    catch (IllegalAccessException e) {
                    }
                    buffer.append(" ,");
                }
                oClass = oClass.getSuperclass();
            }
            buffer.append("}");
        }
        return buffer.toString();
    }

    public static interface Predicate<T> {
        boolean apply(T t);
    }

    /**
     * duplicate a Collection with objects which pass a certain functionality
     * test
     *
     * @param <T>
     *            generic type of collection
     * @param collection
     *            input
     * @param predicate
     *            test to apply
     * @return new Collection of same type as input
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> filter(Collection<T> collection,
                                           Predicate<T> predicate) throws Exception {
        Class<?> noarg[] = new Class<?>[0];
        Constructor<?> c = (Constructor<T>) collection.getClass()
                           .getConstructor(noarg);
        Collection<T> newColl = (Collection<T>) c.newInstance(new Object[0]);
        for (T value : collection) {
            if (predicate.apply(value)) {
                newColl.add(value);
            }
        }
        return newColl;
    }

    /**
     * find first object in collection which matches condition
     * @param <T>
     * @param collection
     * @param predicate
     * @return first object which satisfies predicate or null
     * @throws Exception
     */
    public static <T> T find(Collection<T> collection, Predicate<T> predicate) {
        for (T obj: collection) {
            if (predicate.apply(obj)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Copy collection to another collection of same type container only
     * specific types
     *
     * @param <T>
     *            type of collection
     * @param <U>
     *            type of filtered type
     * @param collection
     *            collection to duplicate/filter
     * @param clazz
     *            class to select for
     * @return new collection
     * @throws Exception
     *             reflection exceptions
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Collection<U> filter(Collection<T> collection,
            Class<U> clazz) throws Exception {
        Class<?> noarg[] = new Class<?>[0];
        Constructor<?> c = (Constructor<T>) collection.getClass()
                           .getConstructor(noarg);
        Collection<U> newColl = (Collection<U>) c.newInstance(new Object[0]);
        for (Object value : collection) {
            if (clazz.isInstance(value)) {
                newColl.add((U) value);
            }
        }
        return newColl;
    }

    /**
     * return elements of collection as separated String
     *
     * @param <T>
     *            type of separator
     * @param coll
     * @param separator
     * @return "seperator" separated String
     */
    public static <T> String join(Collection<?> coll, T separator) {
        if (coll.isEmpty()) {
            return new String("");
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> iter = coll.iterator();
        sb.append(iter.next());
        while (iter.hasNext()) {
            sb.append(separator);
            sb.append(iter.next());
        }
        return sb.toString();
    }
    /**
     * return elements of Object array as separated String
     *
     * @param <T>
     *            type of separator
     * @param ary array to join
     * @param separator
     * @return "separator" separated String
     */
    public static <T> String join(Object ary[], T separator) {
        if (ary == null || ary.length == 0) {
            return new String("");
        }
        StringBuilder sb = new StringBuilder();
        for (Object o: ary) {
            sb.append(o);
            sb.append(separator);
        }
        return sb.toString();
    }

    /**
     * return first element from collection
     *
     * @param <T>
     *            type of collection
     * @param collection
     *            to search
     * @return first Element
     */
    public static <T> T first(Collection<T> collection) {
        for (T t : collection) {
            return t;
        }
        return null;
    }

    /**
     * pair class
     *
     * @author gerard
     *         http://stackoverflow.com/questions/156275/what-is-the-equivalent
     *         -of-the-c-pairl-r-in-java
     *
     * @param <A>
     * @param <B>
     */
    public static class Pair<A, B> {
        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public int hashCode() {
            int hashFirst = first != null ? first.hashCode() : 0;
            int hashSecond = second != null ? second.hashCode() : 0;

            return (hashFirst + hashSecond) * hashSecond + hashFirst;
        }

        public boolean equals(Pair<A, B> other) {
            if (!(other instanceof Pair <? , ? >)) {
                return false;
            }
            return ((this.first == other.first || (this.first != null
                                                   && other.first != null && this.first.equals(other.first))) && (this.second == other.second || (this.second != null
                                                           && other.second != null && this.second.equals(other.second))));
        }

        public String toString() {
            return "(" + first + ", " + second + ")";
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }
    }

    /**
     * recursive delete directory or file)
     *
     * @param file
     *            directory or file to delete
     */
    public static void recursiveDelete(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                recursiveDelete(child);
            }
        }
        file.delete();
    }

    /**
     * return true if x and y are different, based on "equals".  Safely handles null
     * values
     * @param x
     * @param y
     * @return false if both null; true if one null, otherwise returns x.equals(y)
     */
    public static boolean areDifferent(Object x, Object y) {
        if (x != null) {
            return !x.equals(y);
        }
        //x is null
        return y != null;
    }

    /**
     * return true if x and y are equal, based on "equals", including both null
     * @param x
     * @param y
     * @return true if equals or both null
     */
    public static boolean areEqual(Object x, Object y) {
        return !areDifferent(x, y);
    }

    /**
     * return true if and y are with precision of each other
     * @param x
     * @param y
     * @param precision
     * @return true if within precision of each other
     */
    public static boolean areEqual(float x, float y, float precision) {
        if (x == y) {
            return true;
        }
        final float delta = x - y;
        return Math.abs(delta) <= precision;
    }

    /**
     * find field via Reflection without throwing exception if not present
     * @param object to search
     * @param name to find
     * @return Field object or null if field not present
     */
    public  static Field findDeclaredField(Object object, String name) {
        for (Field f : object.getClass().getDeclaredFields()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * utility method to display camel case a little nicer
     * @param s
     * @return human formatted String
     */
    public static String splitCamelCase(String s) {
        //http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
        return s.replaceAll(
                   String.format("%s|%s|%s",
                                 "(?<=[A-Z])(?=[A-Z][a-z])",
                                 "(?<=[^A-Z])(?=[A-Z])",
                                 "(?<=[A-Za-z])(?=[^A-Za-z])"
                                ),
                   " "
               );
    }

    /**
     * attempt to convert object to specified type
     * @param clzz to convert to
     * @param obj to convert
     * @return converted type or null if obj is not a clzz
     */
    @SuppressWarnings("unchecked")
    public static <T> T dynamicCast(Class<T> clzz, Object obj) {
        if (obj != null) {
            if (clzz.isAssignableFrom(obj.getClass())) {
                return (T) obj;
            }
        }
        return null;
    }

    /**
     * implement {@link Comparator} for ints; replace with Integer.compare in JDK 7
     * @param x
     * @param y
     * @return -1, 1, or 0
     */
    public static int compare(int x, int y) {
        if (x < y) {
            return -1;
        }
        if (x > y) {
            return 1;
        }
        return 0;
    }

}
/*
 * $Log: CodeUtil.java,v $
 * Revision 1.19  2013/03/18 19:55:57  gerard
 * int compare (remove when ported to JDK 7)
 *
 * Revision 1.18  2013/02/19 16:07:02  gerard
 * add  dynamic cast
 *
 * Revision 1.17  2012/08/29 12:12:27  gerard
 * areEqual bug fix
 *
 * Revision 1.16  2012/03/28 14:52:22  gerard
 * bug fix equality checks for dimensions; add precision allowance
 *
 * Revision 1.15  2012/03/20 17:03:11  gerard
 * move utility method from Interface
 *
 * Revision 1.14  2011/11/29 16:34:00  gerard
 * revamp prettyPrint for int[]
 *
 * Revision 1.13  2011/11/15 22:54:33  gerard
 * add findDeclaredField
 *
 * Revision 1.12  2011/11/03 21:17:31  gerard
 * remove duplicate $Id tag in source file
 *
 * Revision 1.11  2011/10/04 15:50:59  gerard
 * nonuniform updates
 *
 * Revision 1.10  2011/09/29 11:47:58  gerard
 * javadoc spelling error
 *
 * Revision 1.9  2011/09/22 22:29:53  gerard
 * add find method
 *
 * Revision 1.8  2011/09/15 11:46:27  gerard
 * add areEqual
 *
 * Revision 1.7  2011/08/15 12:53:18  gerard
 * clean up javadoc errors
 *
 * Revision 1.6  2011/03/22 22:28:56  gerard
 * add first scale point to GUI, fix format and CVS keywords
 *
 * Revision 1.5  2011/03/11 18:09:27  gerard
 * add String array join
 *
 * Revision 1.4  2011/03/01 13:09:07  gerard
 * add areDifferent
 *
 * Revision 1.3  2011/01/11 17:08:55  gerard
 * add callStatusMethods
 * Revision 1.2 2010/10/21 23:58:32 gerard add first
 * method
 *
 * Revision 1.1.1.1 2010/10/09 20:05:08 gerard Refactored Spectrum Translator
 * (formerly NMRFileConverter).
 *
 * Revision 1.5 2010/07/30 21:13:42 gerard reformat to standardized style
 *
 * Revision 1.4 2010/07/30 21:09:01 gerard add recursive delete
 *
 * Revision 1.3 2010/07/06 16:13:19 gerard bug fix in join; delete lines
 * removing elements from collection: not supported by all Collections
 *
 * Revision 1.2 2010/06/04 16:34:50 gerard Get source and destination names f
 *
 * Revision 1.1 2010/04/16 14:37:03 gerard move CodeUtil to main package
 *
 * Revision 1.12 2010/04/09 17:21:40 vyas *** empty log message ***
 *
 * Revision 1.11 2010/03/29 23:26:47 gerard add java doc
 *
 * Revision 1.10 2010/03/27 17:22:19 gerard add filter functions
 *
 * Revision 1.9 2010/02/15 16:06:06 vyas *** empty log message ***
 *
 * Revision 1.8 2010/01/28 17:43:19 gerard merge array print methods
 *
 * Revision 1.7 2010/01/22 16:51:42 vyas *** empty log message ***
 *
 * Revision 1.6 2010/01/20 19:53:23 vyas *** empty log message ***
 *
 * Revision 1.5 2010/01/13 15:54:43 vyas *** empty log message ***
 *
 * Revision 1.4 2010/01/04 19:51:33 gerard initialize log4j in Converter class
 *
 * Revision 1.3 2010/01/04 19:19:04 vyas *** empty log message ***
 *
 * Revision 1.2 2009/12/16 16:01:00 gerard fix javadoc warning
 *
 * Revision 1.1 2009/11/15 15:30:26 gerard developer tools
 */
