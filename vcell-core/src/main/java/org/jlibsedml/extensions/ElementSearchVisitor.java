package org.jlibsedml.extensions;

import org.jlibsedml.components.SedBase;
import org.jlibsedml.SEDMLVisitor;

/**
 * This class searches the object structure for an element with its value of an id attribute
 *  equal to that passed into this object's constructor.
 * @author radams
 *
 */

public class ElementSearchVisitor extends SEDMLVisitor {
    String searchTerm;
    SedBase foundElement;

    /**
     * @param searchTerm A non-null <code>String</code> of an element identifier with which  to search the object structure
     */
    public ElementSearchVisitor(String searchTerm) {
        super();
        this.searchTerm = searchTerm;
    }

    /**
     * Gets either the found element, or <code>null</code> if no element was found
     *  with the ID specified in the constructor.<br/>
     *  Before calling <code>accept()</code>, this method will always return <code>null</code>.
     * @return A {@link SedBase} or <code>null</code>.
     */
    public SedBase getFoundElement(){
        return this.foundElement;
    }

    // returns boolean to  visit() methods
    boolean checkID(SedBase aie) {
        // `true`   => 'keep searching'
        // `false`  => 'found element with that id, stop search'
        if(!this.searchTerm.equals(aie.getId())) return true;
        this.foundElement = aie;
        return false;
    }

    /**
     * Resets the search. After calling this method,
     * <pre>
     * getFoundElement()
     * </pre>
     * will return <code>null</code>.
     */
    public void clear(){
        this.foundElement = null;
    }

    public boolean visit(SedBase sedBase){
        return this.checkID(sedBase);
    }

}
