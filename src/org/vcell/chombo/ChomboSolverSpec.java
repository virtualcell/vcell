package org.vcell.chombo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SolverUtilities;


public class ChomboSolverSpec implements Matchable, Serializable, VetoableChangeListener {
	
	private static double defaultFillRatio = 0.9;
	
	private int maxBoxSize = 32;
	private double fillRatio = defaultFillRatio;
	private ArrayList<RefinementLevel> refinementLevelList = new ArrayList<RefinementLevel>();
	public static String PROPERTY_NAME_MAX_BOX_SIZE = "maxBoxSize";
	public static String PROPERTY_NAME_FILL_RATIO = "fillRatio";
	private transient PropertyChangeSupport propertyChange;
	private transient VetoableChangeSupport vetoChange;

	public ChomboSolverSpec(int maxBoxSize) {
		this.maxBoxSize = maxBoxSize;
	}
	
	public ChomboSolverSpec(ChomboSolverSpec css) {
		this.maxBoxSize = css.maxBoxSize;
		this.fillRatio = css.fillRatio;
		this.refinementLevelList = new ArrayList<RefinementLevel>();
		for (RefinementLevel rl : css.refinementLevelList)
		{
			refinementLevelList.add(new RefinementLevel(rl));
		}
	}
	
	public ChomboSolverSpec(int maxBoxSize, double fillRatio, ArrayList<RefinementLevel> refineLevelList) throws ExpressionException {
		super();
		this.maxBoxSize = maxBoxSize;
		this.fillRatio = fillRatio;
		refinementLevelList = refineLevelList;
		addVetoableChangeListener(this);
	}
	
	public ChomboSolverSpec(CommentStringTokenizer tokenizer) throws DataAccessException {
		super();
		readVCML(tokenizer);
	}	
	
	public static int getDefaultMaxBoxSize(int dimension)
	{
		return dimension == 2 ? 64 : 32;
	}
	
	public static double getDefaultFillRatio()
	{
		return defaultFillRatio;
	}
	
	public RefinementLevel getRefinementLevel(int index) {
		if (index >= refinementLevelList.size()) {
			return null;
		}
		return refinementLevelList.get(index);
	}

	public void addRefinementLevel(RefinementLevel rfl) {
		refinementLevelList.add(rfl);
	}
	
	public void deleteRefinementLevel() {
		refinementLevelList.remove(refinementLevelList.size() - 1);
	}

	public int getNumRefinementLevels() {
		return refinementLevelList.size();
	}
	
	public ISize getFinestSamplingSize(ISize coarsestSize) {
		int xsize = coarsestSize.getX();
		int ysize = coarsestSize.getY();
		int zsize = coarsestSize.getZ();
		for (RefinementLevel rfl : refinementLevelList) {
			xsize *= rfl.getRefineRatio();
			ysize *= rfl.getRefineRatio();
			zsize *= rfl.getRefineRatio();
		}
		return new ISize(xsize, ysize, zsize);
	}

	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return (true);
		}
		if (!(object instanceof ChomboSolverSpec)) {
			return false;
		}
		
		ChomboSolverSpec chomboSolverSpec = (ChomboSolverSpec) object;
		if (chomboSolverSpec.maxBoxSize != maxBoxSize)
		{
			return false;
		}
		if (chomboSolverSpec.fillRatio != fillRatio)
		{
			return false;
		}
		if (chomboSolverSpec.getNumRefinementLevels() != getNumRefinementLevels()) {
			return false;
		}
		for (int levelIndex = 0; levelIndex < refinementLevelList.size(); levelIndex ++) {
			if (!refinementLevelList.get(levelIndex).compareEqual(chomboSolverSpec.refinementLevelList.get(levelIndex))) {
				return false;
			}
		}				
		return true;
	}
	
	public String getVCML() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(VCML.ChomboSolverSpec + " " + VCML.BeginBlock + "\n");
		buffer.append("\t" + VCML.MaxBoxSize + " " + maxBoxSize + "\n");
		buffer.append("\t" + VCML.FillRatio + " " + fillRatio + "\n");
		buffer.append("\t" + VCML.MeshRefinement + " " + VCML.BeginBlock + "\n");
		for (RefinementLevel level : refinementLevelList) {
			buffer.append(level.getVCML());
		}
		buffer.append("\t" + VCML.EndBlock+"\n");
		buffer.append(VCML.EndBlock+"\n");
		return buffer.toString();
	}

	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.ChomboSolverSpec)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		boolean bMeshRefinementBeginToken = false;
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				if (!bMeshRefinementBeginToken)
				{
					break;
				}
			}
			else if (token.equalsIgnoreCase(VCML.MaxBoxSize))
			{
				token = tokens.nextToken();
				maxBoxSize = Integer.parseInt(token);
			}
			else if (token.equalsIgnoreCase(VCML.FillRatio))
			{
				token = tokens.nextToken();
				fillRatio = Double.parseDouble(token);
			}
			else if (token.equalsIgnoreCase(VCML.MeshRefinement))
			{
				// BeginToken
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
				}
				bMeshRefinementBeginToken = true;
			}
			else if (token.equalsIgnoreCase(VCML.RefinementLevel)) 
			{
				RefinementLevel rfl = new RefinementLevel(tokens);
				refinementLevelList.add(rfl);				
				continue;
			}
		}
	}

	public void setMaxBoxSize(int newValue) throws PropertyVetoException {
		int oldValue = maxBoxSize;
		fireVetoableChange(PROPERTY_NAME_MAX_BOX_SIZE, oldValue, newValue);
		this.maxBoxSize = newValue;
		firePropertyChange(PROPERTY_NAME_MAX_BOX_SIZE, oldValue, newValue);
	}

	public void setFillRatio(double newValue) {
		double oldValue = fillRatio;
		this.fillRatio = newValue;
		firePropertyChange(PROPERTY_NAME_FILL_RATIO, oldValue, newValue);
	}
	
	public int getMaxBoxSize()
	{
		return maxBoxSize;
	}
	
	public double getFillRatio()
	{
		return fillRatio;
	}
	
	private void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	private void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws PropertyVetoException {
		getVetoChange().fireVetoableChange(propertyName, oldValue, newValue);
	}

	private java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoChange().removeVetoableChangeListener(listener);
	}

	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PROPERTY_NAME_MAX_BOX_SIZE)) {
			int newValue = (Integer)evt.getNewValue();
			if (!SolverUtilities.isPowerOf2(newValue)) {
				throw new PropertyVetoException("Max box size must be an integer power of 2.", evt);
			}
		}
		
	}

	private java.beans.VetoableChangeSupport getVetoChange() {
		if (vetoChange == null) {
			vetoChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoChange;
	}

	public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoChange().addVetoableChangeListener(listener);
	}

	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}	
	
	public boolean hasRefinementRoi()
	{
		for (RefinementLevel rl : refinementLevelList)
		{
			if (rl.getRoiExpression() != null)
			{
				return true;
			}
		}
		return false;
	}

	public String getRefinementRoiDisplayLable() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < refinementLevelList.size(); ++ i)
		{
			RefinementLevel rl = refinementLevelList.get(i);
			if (rl.getRoiExpression() != null)
			{
				sb.append("Level " + (i+1)  + ": " + rl.getRoiExpression().infix() + ";  ");
			}
		}
		return sb.toString();
	}

	public String checkParamters() {
		String errorMessage = null;
		
		if (refinementLevelList.size() > 0)
		{
			int finestLevel = refinementLevelList.size() - 1;
			if (refinementLevelList.get(finestLevel).getRoiExpression() == null)
			{
				for (int i = 0; i < finestLevel; ++ i)
				{
					if (refinementLevelList.get(i).getRoiExpression() != null)
					{
						errorMessage = "Finest level must have ROI if not refining all membrane elements";
					}
				}
			}
		}
		return errorMessage;
	}
}
