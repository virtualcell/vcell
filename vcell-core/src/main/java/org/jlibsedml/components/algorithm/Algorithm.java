package org.jlibsedml.components.algorithm;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates  an Algorithm element in SED-ML. Equality of objects is based on the KISAO ID.
 * @author radams
 * @link http://www.ebi.ac.uk/compneur-srv/kisao/
 *
 */
public final class Algorithm extends SedBase {
	private String kisaoID;
	private final List<AlgorithmParameter> listOfAlgorithmParameters = new ArrayList<>();
	
	public boolean accept(SEDMLVisitor visitor) {
	    return visitor.visit(this);
	}

	/**
	 * Getter for the KisaoID of the algorithm.
	 * @return the Kisao ID
	 */
	public String getKisaoID() {
		return this.kisaoID;
	}
	
    public void addAlgorithmParameter(AlgorithmParameter algorithmParameter) {
        listOfAlgorithmParameters.add(algorithmParameter);
    }
    public List<AlgorithmParameter> getListOfAlgorithmParameters() {
        return listOfAlgorithmParameters;
    }
	

	/**
	 * Takes a non-null, non empty KisaoID. 
	 * @param kisaoID A <code>String</code>.
	 * @throws IllegalArgumentException if kisaoID is null or empty.
	 */
	public Algorithm(SId id, String name, String kisaoID) {
		super(id, name);
        SedGeneralClass.checkNoNullArgs(kisaoID);
        SedGeneralClass.stringsNotEmpty(kisaoID);
		this.kisaoID = kisaoID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kisaoID == null) ? 0 : kisaoID.hashCode());
		return result;
	}

	// We'll assume that Algorithms with the same kisaoID are equal regardless of the list of parameters
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
        if (!(obj instanceof Algorithm otherAlg)) return false;
		if (this.kisaoID == null) return otherAlg.kisaoID == null;
        else return this.kisaoID.equals(otherAlg.kisaoID);
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
        List<String> params = new ArrayList<>(), paramParams = new ArrayList<>();
        if (this.kisaoID != null)
            params.add(String.format("kisaoID=%s", this.kisaoID));
        for (AlgorithmParameter ap : this.listOfAlgorithmParameters)
            paramParams.add(ap.getId() != null ? ap.getIdAsString() : '[' + ap.parametersToString() + ']');
        if (!this.listOfAlgorithmParameters.isEmpty())
            params.add(String.format("algParams={%s}", String.join(", ", paramParams)));
        return super.parametersToString() + ", " + String.join(", ", params);
    }


	@Override
	public String getElementName() {
		return SedMLTags.ALGORITHM_TAG;
	}

	
	

}
