package org.vcell.chombo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vcell.chombo.RefinementRoi.RoiType;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.SolverUtilities;


@SuppressWarnings("serial")
public class ChomboSolverSpec implements Matchable, Serializable, VetoableChangeListener {
	
	public static final int defaultRefineRatio = 2;
	
	private static double defaultFillRatio = 0.9;
	
	private int maxBoxSize = 32;
	private double fillRatio = defaultFillRatio;
	private List<RefinementRoi> refinementRoiList = new ArrayList<RefinementRoi>();
	public static String PROPERTY_NAME_MAX_BOX_SIZE = "maxBoxSize";
	public static String PROPERTY_NAME_FILL_RATIO = "fillRatio";
	public static String PROPERTY_NAME_ROI = "ROI";
	public static String PROPERTY_NAME_FINEST_VIEW_LEVEL = "bFinestViewLevel";

	private transient PropertyChangeSupport propertyChange;
	private transient VetoableChangeSupport vetoChange;
	private Integer viewLevel = null;  // if null, viewLeve is finest
	private boolean bSaveVCellOutput = true;
	private boolean bSaveChomboOutput = false;
	private List<Integer> refineRatioList = null;

	public ChomboSolverSpec(int maxBoxSize) {
		this.maxBoxSize = maxBoxSize;
	}
	
	public ChomboSolverSpec(ChomboSolverSpec css) {
		this.maxBoxSize = css.maxBoxSize;
		this.fillRatio = css.fillRatio;
		this.viewLevel = css.viewLevel;
		this.bSaveVCellOutput = css.bSaveVCellOutput;
		this.bSaveChomboOutput = css.bSaveChomboOutput;
		this.refinementRoiList = new ArrayList<RefinementRoi>();
		for (RefinementRoi roi : css.refinementRoiList)
		{
			refinementRoiList.add(new RefinementRoi(roi));
		}
	}
	
	public ChomboSolverSpec(int maxBoxSize, double fillRatio, Integer viewLevel, boolean bSaveVCellOutput, 
			boolean bSaveChomboOutput, List<Integer> refineRatioList) throws ExpressionException 
	{
		super();
		this.maxBoxSize = maxBoxSize;
		this.fillRatio = fillRatio;
		this.viewLevel = viewLevel;
		this.bSaveVCellOutput = bSaveVCellOutput;
		this.bSaveChomboOutput = bSaveChomboOutput;
		this.refineRatioList = refineRatioList;
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
	
	public List<RefinementRoi> getMembraneRefinementRois() {
		return getRefinementRois(RoiType.Membrane);
	}

	public List<RefinementRoi> getVolumeRefinementRois() {
		return getRefinementRois(RoiType.Volume);
	}
	
	public List<RefinementRoi> getRefinementRois() {
		return refinementRoiList;
	}
	
	public List<RefinementRoi> getRefinementRois(RoiType type) {
		List<RefinementRoi> l = new ArrayList<RefinementRoi>();
		for (RefinementRoi roi: refinementRoiList)
		{
			if (roi.getType() == type)
			{
				l.add(roi);
			}
		}
		return l;
	}
	
	public void addRefinementRoi(RefinementRoi roi) {
		List<RefinementRoi> oldValue = getRefinementRois(roi.getType());
		refinementRoiList.add(roi);
		List<RefinementRoi> newValue = getRefinementRois(roi.getType());
		firePropertyChange(PROPERTY_NAME_ROI, oldValue, newValue);
	}
	
	public void deleteRefinementRoi(RoiType roiType, int index) {
		List<RefinementRoi> oldValue = getRefinementRois(roiType);
		int cnt = 0;
		Iterator<RefinementRoi> iter = refinementRoiList.iterator();
		// remove the i-th element for that type
		while (iter.hasNext())
		{
			RefinementRoi roi = iter.next();
			if (roi.getType() == roiType)
			{
				if (index == cnt)
				{
					iter.remove();
					break;
				}
				++ cnt;
			}
		}
		List<RefinementRoi> newValue = getRefinementRois(roiType);
		firePropertyChange(PROPERTY_NAME_ROI, oldValue, newValue);
	}

	public int getNumRefinementLevels() {
		int numLevels = 0;
		for (RefinementRoi roi: refinementRoiList)
		{
			numLevels = Math.max(numLevels, roi.getLevel());
		}
		return numLevels;
	}
	
	public ISize getFinestSamplingSize(ISize coarsestSize) {
		return getLevelSamplingSize(coarsestSize, getNumRefinementLevels());
	}

	public ISize getLevelSamplingSize(ISize coarsestSize, int level) {
		int xsize = coarsestSize.getX();
		int ysize = coarsestSize.getY();
		int zsize = coarsestSize.getZ();
		int levelRefineRatio = (int)Math.pow(defaultRefineRatio, level);
		xsize = xsize * levelRefineRatio;
		ysize = ysize * levelRefineRatio;
		zsize = zsize * levelRefineRatio;
		return new ISize(xsize, ysize, zsize);
	}
	
	public ISize getViewLevelSamplingSize(ISize coarsestSize) 
	{
		return getLevelSamplingSize(coarsestSize, getViewLevel());
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
		if (!Compare.isEqual(chomboSolverSpec.viewLevel, viewLevel))
		{
			return false;
		}
		if (chomboSolverSpec.fillRatio != fillRatio)
		{
			return false;
		}
		if (chomboSolverSpec.bSaveVCellOutput != bSaveVCellOutput)
		{
			return false;
		}
		if (chomboSolverSpec.bSaveChomboOutput != bSaveChomboOutput)
		{
			return false;
		}
		if (chomboSolverSpec.getNumRefinementLevels() != getNumRefinementLevels()) {
			return false;
		}
		if (chomboSolverSpec.refinementRoiList.size() != refinementRoiList.size()) {
			return false;
		}
		for (int i = 0; i < refinementRoiList.size(); i ++) {
			if (!refinementRoiList.get(i).compareEqual(chomboSolverSpec.refinementRoiList.get(i))) {
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
		if (!isFinestViewLevel())
		{
			buffer.append("\t" + VCML.ViewLevel + " " + getViewLevel() + "\n");
		}
		buffer.append("\t" + VCML.SaveVCellOutput + " " + bSaveVCellOutput + "\n");
		buffer.append("\t" + VCML.SaveChomboOutput + " " + bSaveChomboOutput + "\n");
		buffer.append("\t" + VCML.MeshRefinement + " " + VCML.BeginBlock + "\n");
		for (RefinementRoi roi : refinementRoiList) {
			buffer.append(roi.getVCML());
		}
		buffer.append("\t" + VCML.EndBlock+"\n");
		buffer.append(VCML.EndBlock+"\n");
		return buffer.toString();
	}

	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		viewLevel = null;
		
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.ChomboSolverSpec)) {
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
			if (token.equalsIgnoreCase(VCML.MaxBoxSize))
			{
				token = tokens.nextToken();
				maxBoxSize = Integer.parseInt(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.ViewLevel))
			{
				token = tokens.nextToken();
				viewLevel = Integer.parseInt(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.FillRatio))
			{
				token = tokens.nextToken();
				fillRatio = Double.parseDouble(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.SaveVCellOutput))
			{
				token = tokens.nextToken();
				bSaveVCellOutput = Boolean.parseBoolean(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.SaveChomboOutput))
			{
				token = tokens.nextToken();
				bSaveChomboOutput = Boolean.parseBoolean(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.MeshRefinement))
			{
				readVCMLRefinementRois(tokens);
				continue;
			}
			throw new DataAccessException("unexpected token " + token); 
		}
	}
	
	private void readVCMLRefinementRois(CommentStringTokenizer tokens) throws DataAccessException {
		// BeginToken
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
			throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.RefinementRoi)) 
			{
				RefinementRoi roi = new RefinementRoi(tokens);
				addRefinementRoi(roi);			
				continue;
			}
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				return;
			}
			throw new DataAccessException("unexpected token in refinement levels " + token); 
		}
		throw new DataAccessException("unclosed refinement level block");
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
	
	private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	private void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
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
		return refinementRoiList.size() > 0;
	}
	
	public int getViewLevel() {
		return viewLevel == null ? getNumRefinementLevels() : viewLevel;
	}

	public void setViewLevel(int viewLevel) {
		this.viewLevel = viewLevel;
	}

	public boolean isSaveVCellOutput() {
		return bSaveVCellOutput;
	}

	public void setSaveVCellOutput(boolean newValue) {
		boolean oldValue = this.bSaveVCellOutput;
		if (oldValue == newValue)
		{
			return;
		}
		this.bSaveVCellOutput = newValue;
	}

	public boolean isSaveChomboOutput() {
		return bSaveChomboOutput;
	}

	public void setSaveChomboOutput(boolean newValue) {
		boolean oldValue = this.bSaveChomboOutput;
		if (oldValue == newValue)
		{
			return;
		}
		this.bSaveChomboOutput = newValue;
	}

	public static ErrorTolerance getDefaultEBChomboSemiImplicitErrorTolerance() {
		return new ErrorTolerance(1e-9, 1e-8);
	}

	public boolean isFinestViewLevel() {
		return viewLevel == null;
	}

	public void setFinestViewLevel(boolean bFinestViewLevel) {
		Integer oldValue = this.viewLevel;
		this.viewLevel = null;
		firePropertyChange(PROPERTY_NAME_FINEST_VIEW_LEVEL, oldValue, this.viewLevel);
	}
	
	public List<Integer> getRefineRatioList()
	{
		List<Integer> ratios = new ArrayList<Integer>();
		if (refineRatioList != null && refineRatioList.size() > 0)
		{
			ratios.addAll(refineRatioList);
		}
		else
		{
			for (int i = 0;  i < getNumRefinementLevels(); ++ i)
			{
				ratios.add(defaultRefineRatio);
			}
		}
		return ratios;
	}
	
}
