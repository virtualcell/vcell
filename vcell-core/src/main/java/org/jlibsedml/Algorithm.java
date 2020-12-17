package org.jlibsedml;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates  an Algorithm element in SED-ML. Equality of objects is based on the KISAO ID.
 * @author radams
 * @link http://www.ebi.ac.uk/compneur-srv/kisao/
 *
 */
public final class Algorithm extends SEDBase {
	private String kisaoID;
	private List<AlgorithmParameter> listOfAlgorithmParameters = new ArrayList<AlgorithmParameter>();
	
	public boolean accept(SEDMLVisitor visitor) {
	    return visitor.visit(this);
	}

	/**
	 * Getter for the KisaoID of the algorithm.
	 * @return the Kisao ID
	 */
	public final String getKisaoID() {
		return kisaoID;
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
	public Algorithm(String kisaoID) {
		super();
		Assert.checkNoNullArgs(kisaoID);
		Assert.stringsNotEmpty(kisaoID);
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Algorithm other = (Algorithm) obj;
		if (kisaoID == null) {
			if (other.kisaoID != null)
				return false;
		} else if (!kisaoID.equals(other.kisaoID))
			return false;
		return true;
	}

	@Override
	public String toString() {
	    String s = "Algorithm [kisaoID=" + kisaoID;
	    for(AlgorithmParameter ap : listOfAlgorithmParameters) {
	        s += " " + ap.toString() + " ";
	    }
	    s += "]";
	    return s;
	}



	@Override
	public String getElementName() {
		return SEDMLTags.ALGORITHM_TAG;
	}

	
	

}
