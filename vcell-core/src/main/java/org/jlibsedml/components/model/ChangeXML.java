package org.jlibsedml.components.model;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Encapsulates a changeXML element in SED-ML.  Currently this is achieved by replacing
 * the 'target' content element with the 'newXML' content.
 * * @author anu/radams
 *
 */
public final class ChangeXML extends Change {
    private NewXML newXML;

    /**
     * Setter for  the {@link NewXML} for this object.
     *
     * @param newXML A non-null {@link NewXML} object.
     * @throws IllegalArgumentException if <code>newXML</code> is <code>null</code>.
     * @since 1.2.0
     */
    public void setNewXML(NewXML newXML) {
        this.newXML = newXML;
    }

    /**
     *
     * @param target A <code>XPathTarget</code>object
     * @param newXML A <code>String</code> of new XML
     */
    public ChangeXML(XPathTarget target, NewXML newXML) {
        this(null, null, target, newXML);
    }

    /**
     *
     * @param target A <code>XPathTarget</code>object
     * @param newXML A <code>String</code> of new XML
     */
    public ChangeXML(SId id, String name, XPathTarget target, NewXML newXML) {
        super(id, name, target);
        this.newXML = newXML;
    }

    public ChangeXML clone() throws CloneNotSupportedException {
        ChangeXML clone = (ChangeXML) super.clone();
        clone.newXML = this.newXML;
        return clone;
    }

    /**
     * Getter for the change kind.
     *
     * @return SEDMLTags.CHANGE_XML_KIND;
     */
    @Override
    public String getChangeKind() {
        return SedMLTags.CHANGE_XML_KIND;
    }

    /**
     * Getter for the new XML that replaces the old XML.
     *
     * @return a NewXML object
     */
    public NewXML getNewXML() {
        return this.newXML;
    }

    @Override
    public String getElementName() {
        return SedMLTags.CHANGE_XML;
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
    public String parametersToString(){
        if (this.newXML == null) return super.parametersToString();
        else return super.parametersToString() + ", newXML=[" + this.newXML.toString() + ']';
    }
}
