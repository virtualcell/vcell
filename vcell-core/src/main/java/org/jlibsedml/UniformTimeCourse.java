package org.jlibsedml;


/**
 * Encapsulates a basic time course simulation of a model.
 *
 */
public final class UniformTimeCourse extends Simulation {
	  @Override
	public String toString() {
		return "UniformTimeCourse [initialTime=" + initialTime
				+ ", numberOfPoints=" + numberOfPoints + ", outputEndTime="
				+ outputEndTime + ", outputStartTime=" + outputStartTime
				+ ", " + getAlgorithm() + ", getId()=" + getId()
				+ "]";
	}
	  
	  /**
	   * Sets the initial time for this simulation.
	   * @param initialTime A <code>double</code>.
	   * @since 1.2.0
	   */
	  public void setInitialTime(double initialTime) {
        this.initialTime = initialTime;
    }

	  /**
       * Sets the output start  time for this simulation.
       * @param outputStartTime A <code>double</code>.
       * @since 1.2.0
       */
    public void setOutputStartTime(double outputStartTime) {
        this.outputStartTime = outputStartTime;
    }

    /**
     * Sets the output end  time for this simulation.
     * @param outputEndTime A <code>double</code>.
     * @since 1.2.0
     */
    public void setOutputEndTime(double outputEndTime) {
        this.outputEndTime = outputEndTime;
    }

    /**
     * Sets the number of output points for  this simulation.
     * @param numberOfPoints A <code>double</code>.
     * @since 1.2.0
     */
    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    @Override
		public String getElementName() {
			return SEDMLTags.SIM_UTC;
		}

	private double initialTime = 0.0;
	   private double outputStartTime = 0.0;
	   private double outputEndTime = 0.0;
	   private int numberOfPoints = 0;
	   
	   /**
	    * This constructor does not perform validation at this stage of the simulation configuration ( for example, 
	    * that outputStartTime < outputEndTime). This can be checked by validating the the SEDML document by a call to
	    * <pre>
	    *   doc.validate();
	    * </pre>
	    * @param id  a  mandatory, unique identifier for this element
	    * @param name optional, can be null.
	    * @param initialTime 
	    * @param outputStartTime
	    * @param outputEndTime
	    * @param numberOfPoints
	    */
	   public UniformTimeCourse(String id, String name, 
			   double initialTime, double outputStartTime, double outputEndTime, int numberOfPoints,Algorithm algorithm) {
		   super(id, name,algorithm);
		   this.initialTime = initialTime;
		   this.outputStartTime = outputStartTime;
		   this.outputEndTime = outputEndTime;
		   this.numberOfPoints = numberOfPoints;
	   }

	/**
	 * Getter for the initial time value, i.e., the value of <code>t</code> at the start of the simulation.
	 * @return a double
	 */
	public double getInitialTime() {
		return initialTime;
	}

	/**
	 * Getter for the time value at which output should be started
	 * @return a double
	 */
	public double getOutputStartTime() {
		return outputStartTime;
	}

	/**
	 * Getter for the time value at which output should be terminated.
	 * @return a double
	 */
	public double getOutputEndTime() {
		return outputEndTime;
	}

	/**
	 * Getter for the number of time-points in  the simulation.
	 * @return a double
	 */
	public int getNumberOfPoints() {
		return numberOfPoints;
	}
	
	/**
	 * @return {@link SEDMLTags#SIMUL_UTC_KIND}
	 */
	public String getSimulationKind() {
		return SEDMLTags.SIMUL_UTC_KIND;
	}
}	   
