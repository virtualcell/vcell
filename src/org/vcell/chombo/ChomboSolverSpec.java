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

import org.apache.commons.lang3.builder.EqualsBuilder;
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
	public static final int BLOCK_FACTOR = 8;
	
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
	private boolean bActivateFeatureUnderDevelopment = false;
	private double smallVolfracThreshold = 0;
	private List<Integer> refineRatioList = null;
	private List<TimeInterval> timeIntervalList = new ArrayList<TimeInterval>();

	public ChomboSolverSpec(int maxBoxSize) {
		this.maxBoxSize = maxBoxSize;
	}
	
	public ChomboSolverSpec(ChomboSolverSpec css) {
		this.maxBoxSize = css.maxBoxSize;
		this.fillRatio = css.fillRatio;
		this.viewLevel = css.viewLevel;
		this.bSaveVCellOutput = css.bSaveVCellOutput;
		this.bSaveChomboOutput = css.bSaveChomboOutput;
		this.bActivateFeatureUnderDevelopment = css.bActivateFeatureUnderDevelopment;
		this.smallVolfracThreshold = css.smallVolfracThreshold;
		for (TimeInterval ti : css.timeIntervalList)
		{
			timeIntervalList.add(new TimeInterval(ti));
		}
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
		
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(bActivateFeatureUnderDevelopment, chomboSolverSpec.bActivateFeatureUnderDevelopment)
			.append(smallVolfracThreshold, chomboSolverSpec.smallVolfracThreshold);
		if (!equalsBuilder.isEquals())
		{
			return false;
		}
		
		if (timeIntervalList.size() != chomboSolverSpec.timeIntervalList.size())
		{
			return false;
		}
		for (int i = 0; i < timeIntervalList.size(); i ++) {
			if (!timeIntervalList.get(i).compareEqual(chomboSolverSpec.timeIntervalList.get(i))) {
				return false;
			}
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
		buffer.append("\t" + VCML.ActivateFeatureUnderDevelopment + " " + bActivateFeatureUnderDevelopment + "\n");
		buffer.append("\t" + VCML.SmallVolfracThreshold + " " + smallVolfracThreshold + "\n");
		
		buffer.append("\t" + VCML.TimeBounds + " " + VCML.BeginBlock + "\n");
		for (TimeInterval ti : timeIntervalList)
		{
			buffer.append(ti.getVCML());
		}
		buffer.append("\t" + VCML.EndBlock+"\n");
		
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
			}
			else if (token.equalsIgnoreCase(VCML.ViewLevel))
			{
				token = tokens.nextToken();
				viewLevel = Integer.parseInt(token);
			}
			else if (token.equalsIgnoreCase(VCML.FillRatio))
			{
				token = tokens.nextToken();
				fillRatio = Double.parseDouble(token);
			}
			else if (token.equalsIgnoreCase(VCML.SaveVCellOutput))
			{
				token = tokens.nextToken();
				bSaveVCellOutput = Boolean.parseBoolean(token);
			}
			else if (token.equalsIgnoreCase(VCML.SaveChomboOutput))
			{
				token = tokens.nextToken();
				bSaveChomboOutput = Boolean.parseBoolean(token);
			}
			else if (token.equalsIgnoreCase(VCML.ActivateFeatureUnderDevelopment))
			{
				token = tokens.nextToken();
				bActivateFeatureUnderDevelopment = Boolean.parseBoolean(token);
			}
			else if (token.equalsIgnoreCase(VCML.SmallVolfracThreshold))
			{
				token = tokens.nextToken();
				smallVolfracThreshold = Double.parseDouble(token);
			}
			else if (token.equalsIgnoreCase(VCML.MeshRefinement))
			{
				readVCMLRefinementRois(tokens);
			}
			else if (token.equalsIgnoreCase(VCML.TimeBounds))
			{
				readVCMLTimeBounds(tokens);
			}
			else
			{
				throw new DataAccessException("unexpected token " + token);
			}
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
			}
			else if (token.equalsIgnoreCase(VCML.EndBlock)) {
				return;
			}
			else {
				throw new DataAccessException("unexpected token in refinement levels " + token);
			}
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

	public List<TimeInterval> getTimeIntervalList() {
		return timeIntervalList;
	}
	
	public void addTimeInterval(TimeInterval ti) throws IllegalArgumentException
	{
		if (timeIntervalList.size() == 0)
		{
			if (ti.getStartingTime() != 0)
			{
				throw new IllegalArgumentException("The starting time of the first time interval must be 0.");
			}
		}
		else
		{
			int num = timeIntervalList.size();
			if (ti.getStartingTime() != timeIntervalList.get(num - 1).getEndingTime())
			{
				throw new IllegalArgumentException("The starting time of a time interval must be the same as the ending time of the previous interval.");
			}
		}
		timeIntervalList.add(ti);
	}
	
	public TimeInterval getLastTimeInterval()
	{
		int num = timeIntervalList.size();
		assert(num > 0);
		return timeIntervalList.get(num - 1);
	}
	
	public void addNewTimeInterval()
	{
		double startingTime = getLastTimeInterval().getEndingTime();
		TimeInterval ti = new TimeInterval(startingTime, -1, -1, -1);
		timeIntervalList.add(ti);
	}
	
	public void removeTimeInterval()
	{
		// always remove the last one
		timeIntervalList.remove(timeIntervalList.size() - 1);
	}

	private void readVCMLTimeBounds(CommentStringTokenizer tokens) throws DataAccessException {
		// BeginToken
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
			throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.TimeInterval)) 
			{
				TimeInterval ti = new TimeInterval(tokens);
				timeIntervalList.add(ti);			
			}
			else if (token.equalsIgnoreCase(VCML.EndBlock)) {
				return;
			}
			else
			{
				throw new DataAccessException("unexpected token in time bounds " + token);
			}
		}
		throw new DataAccessException("unclosed refinement level block");
	}

	public double getEndingTime() 
	{
		return getLastTimeInterval().getEndingTime();
	}

	public boolean isActivateFeatureUnderDevelopment() {
		return bActivateFeatureUnderDevelopment;
	}

	public void setActivateFeatureUnderDevelopment(boolean bActivateFeatureUnderDevelopment) {
		this.bActivateFeatureUnderDevelopment = bActivateFeatureUnderDevelopment;
	}

	public double getSmallVolfracThreshold() {
		return smallVolfracThreshold;
	}

	public void setSmallVolfracThreshold(double smallVolfracThreshold) {
		this.smallVolfracThreshold = smallVolfracThreshold;
	}
}
