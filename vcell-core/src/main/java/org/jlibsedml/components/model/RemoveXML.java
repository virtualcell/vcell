package org.jlibsedml.components.model;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.components.SId;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Class encapsulating a RemoveXML element to be applied to a model.
 *
 * @author radams
 *
 */
public class RemoveXML extends Change {

    /**
     *
     * @param target A non-null {@link XPathTarget} object
     */
    public RemoveXML(SId id, String name, XPathTarget target) {
        super(id, name, target);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getElementName() {
        return SedMLTags.REMOVE_XML;
    }

    @Override
    public final String getChangeKind() {
        return SedMLTags.REMOVE_XML_KIND;
    }

    public boolean accept(SEDMLVisitor visitor) {

        return visitor.visit(this);

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
        return super.parametersToString();
    }
}
