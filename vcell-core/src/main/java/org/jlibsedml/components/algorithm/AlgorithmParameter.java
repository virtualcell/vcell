package org.jlibsedml.components.algorithm;

import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

/**
 * An Algorithm Parameter for a specific Algorithm.
 * <br/>
 * This class does not currently verify the validity of a parameter for the
 *  given algorithm. 
 */
public class AlgorithmParameter extends SedBase {
    private String kisaoID;
    private String value;

    public AlgorithmParameter(String kisaoID, String value) {
        this(null, null, kisaoID, value);
    }

    public AlgorithmParameter(SId id, String name, String kisaoID, String value) {
        super(id, name);
        SedGeneralClass.checkNoNullArgs(kisaoID);
        SedGeneralClass.stringsNotEmpty(kisaoID);
        this.kisaoID = kisaoID;
        this.setValue(value);
    }

    public AlgorithmParameter clone() throws CloneNotSupportedException {
        AlgorithmParameter clone = (AlgorithmParameter) super.clone();
        clone.kisaoID = this.kisaoID;
        clone.value = this.value;
        return clone;
    }
    
    public void setKisaoID(String kisaoID) {
        this.kisaoID = kisaoID;
    }
    public String getKisaoID() {
        return kisaoID;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((kisaoID == null) ? 0 : kisaoID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AlgorithmParameter other = (AlgorithmParameter) obj;
        if (kisaoID == null) {
            if (other.kisaoID != null)
                return false;
        } else if (!kisaoID.equals(other.kisaoID))
            return false;
        return true;
    }

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.ALGORITHM_PARAMETER_TAG;
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
        List<String> params = new ArrayList<>();
        if (this.kisaoID != null) params.add(String.format("kisaoID=%s", this.kisaoID));
        if (this.value != null) params.add(String.format("value=%s", this.value));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}
