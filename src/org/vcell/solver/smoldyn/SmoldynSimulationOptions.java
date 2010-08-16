package org.vcell.solver.smoldyn;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class SmoldynSimulationOptions implements Serializable, Matchable, VetoableChangeListener {

	private Integer randomSeed = null;
	private double accuracy = 10.0;;
	private int gaussianTableSize = 4096;
	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoChange;
	
	public static final String PROPERTY_NAME_RANDOM_SEED = "randomSeed";
	public static final String PROPERTY_NAME_ACCURACY = "accuracy";
	public static final String PROPERTY_NAME_GAUSSIAN_TABLE_SIZE = "gaussianTableSize";
	
	public SmoldynSimulationOptions() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
	
	public SmoldynSimulationOptions(SmoldynSimulationOptions smoldynSimulationOptions) {
		this();
		randomSeed = smoldynSimulationOptions.randomSeed;
		accuracy = smoldynSimulationOptions.accuracy;
		gaussianTableSize = smoldynSimulationOptions.gaussianTableSize;
	}

	public SmoldynSimulationOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);
	}

	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof SmoldynSimulationOptions)) {
			return false;
		}
		SmoldynSimulationOptions smoldynSimulationOptions = (SmoldynSimulationOptions)obj;
		if (!Compare.isEqualOrNull(randomSeed, smoldynSimulationOptions.randomSeed)) {
			return false;
		}
		if (accuracy != smoldynSimulationOptions.accuracy) {
			return false;
		}
		if (gaussianTableSize != smoldynSimulationOptions.gaussianTableSize) {
			return false;
		}
		return true;
	}

	public final Integer getRandomSeed() {
		return randomSeed;
	}

	public final void setRandomSeed(Integer newValue) {
		Integer oldValue = this.randomSeed;
		this.randomSeed = newValue;
		firePropertyChange(PROPERTY_NAME_RANDOM_SEED, oldValue, newValue);
	}

	public final double getAccuracy() {
		return accuracy;
	}

	public final void setAccuracy(double newValue) {
		double oldValue = this.accuracy;		
		this.accuracy = newValue;
		firePropertyChange(PROPERTY_NAME_ACCURACY, oldValue, newValue);
	}

	public int getGaussianTableSize() {
		return gaussianTableSize;
	}

	public final void setGaussianTableSize(int newValue) throws PropertyVetoException {
		int oldValue = this.gaussianTableSize;
		fireVetoableChange(PROPERTY_NAME_GAUSSIAN_TABLE_SIZE, oldValue, newValue);
		this.gaussianTableSize = newValue;		
		firePropertyChange(PROPERTY_NAME_GAUSSIAN_TABLE_SIZE, oldValue, newValue);
	}

	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	
	public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoChange().addVetoableChangeListener(listener);
	}
	
	private java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	
	private java.beans.VetoableChangeSupport getVetoChange() {
		if (vetoChange == null) {
			vetoChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoChange;
	}
	
	private void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	private void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws PropertyVetoException {
		getVetoChange().fireVetoableChange(propertyName, oldValue, newValue);
	}
	
	public String getVCML() {		
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t" + VCML.SmoldynSimulationOptions + " " + VCML.BeginBlock + "\n");
		buffer.append("\t\t" + VCML.SmoldynSimulationOptions_accuracy + " " + accuracy + "\n");
		buffer.append("\t\t" + VCML.SmoldynSimulationOptions_gaussianTableSize + " " + gaussianTableSize + "\n");
		if (randomSeed != null) {
			buffer.append("\t\t" + VCML.SmoldynSimulationOptions_randomSeed + " " + randomSeed + "\n");			
		}
		buffer.append("\t" + VCML.EndBlock + "\n");
		
		return buffer.toString();
	}
	
	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_accuracy)) {
				token = tokens.nextToken();
				accuracy = Double.parseDouble(token);
			} else if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_randomSeed)) {
				token = tokens.nextToken();
				randomSeed = new Integer(token);
			} else if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_gaussianTableSize)) {
				token = tokens.nextToken();
				gaussianTableSize = Integer.parseInt(token);
			}  else { 
				throw new DataAccessException("unexpected identifier " + token);
			}
		}
	}

	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PROPERTY_NAME_GAUSSIAN_TABLE_SIZE)) {
			int newValue = (Integer)evt.getNewValue();
			if (!isPowerOf2(newValue)) {
				throw new PropertyVetoException("Gaussian table size must be an integer power of 2.", evt);
			}
		}
		
	}
	
	public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoChange().removeVetoableChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	private boolean isPowerOf2(int newValue) {
		int n = newValue;
		while (true) {
			int mod = n % 2;
			if (mod == 1) {
				return false;
			}
			n = n / 2;
			if (n == 1) {
				return true;
			}
		}
	}
	
	public void refreshDependencies() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
}
