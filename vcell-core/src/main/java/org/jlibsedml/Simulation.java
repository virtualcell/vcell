package org.jlibsedml;

/**
 * Encapsulates a description of a simulation.
  * @author anu/radams
 *
 */
public abstract class Simulation extends AbstractIdentifiableElement {
	

	@Override
	public String toString() {
		return "Simulation [algorithm=" + algorithm + ", name=" + getName()
				+ ", getId()=" + getId() + "]";
	}


	
	private Algorithm algorithm;


	/**	  
	 * @param id A required <code>String</code> identifier for this element.
	 * @param name - optional, can be null.
	 * @param algorithm - not null.
	 * @throws IllegalArgumentException if  <code>id</code> is <code>null</code> or empty string.
	 */
	public Simulation(String id, String name,Algorithm algorithm) {
		super(id,name);
		if(SEDMLElementFactory.getInstance().isStrictCreation()){
		Assert.checkNoNullArgs(algorithm);
		}
		
	
		this.algorithm=algorithm;
	}
	

	
	/**
	 * Returns the {@link Algorithm} for this simulation
	 * @return the {@link Algorithm}
	 */
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Setter for the {@link Algorithm} element of this simulation
	 * @param algorithm a non-null {@link Algorithm}.
	 */
	public void setAlgorithm(Algorithm algorithm) {
		Assert.checkNoNullArgs(algorithm);
		this.algorithm = algorithm;
	}





	/**
	 * Getter for the type of this simulation.
	 * @return A <code>String</code>
	 */
	public abstract String getSimulationKind();
	
	public  boolean accept(SEDMLVisitor visitor){
        return visitor.visit(this);
    }



}