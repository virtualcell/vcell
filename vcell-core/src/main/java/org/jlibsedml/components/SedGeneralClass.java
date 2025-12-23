package org.jlibsedml.components;

import org.jlibsedml.SEDMLVisitor;

public abstract class SedGeneralClass {
    /*
     * Checks any argument list for null and throws an IllegalArgumentException if they are.
     */
    protected static void checkNoNullArgs (Object ... args) {
        for (Object o : args) {
            if (o == null){
                throw new IllegalArgumentException();
            }
        }
    }

    /*
     * Throws IllegalArgumentException if strings are empty.
     */
    protected static void stringsNotEmpty(String ...args) {
        for (String o : args) {
            if (o.isEmpty()){
                throw new IllegalArgumentException();
            }
        }

    }

    /**
     * Provides a link between the object model and the XML element names
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    public abstract String getElementName();

    public  abstract boolean accept(SEDMLVisitor visitor);
}
