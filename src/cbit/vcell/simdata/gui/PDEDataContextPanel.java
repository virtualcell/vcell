/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata.gui;

import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;

import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.NumberUtils;
import org.vcell.util.Range;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;
import cbit.image.gui.DisplayAdapterServicePanel;
import cbit.image.gui.ImagePlaneManager;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.PDEDataViewer.DataInfoProvider;
import cbit.vcell.geometry.ControlPointCurve;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.CurveSelectionCurve;
import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.PolyLine;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.geometry.Spline;
import cbit.vcell.geometry.gui.CurveEditorTool;
import cbit.vcell.geometry.gui.CurveRenderer;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.simdata.SimulationData.SolverDataType;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.CartesianMeshChombo;
import cbit.vcell.solvers.CartesianMeshChombo.StructureMetricsEntry;
import cbit.vcell.solvers.CartesianMeshMovingBoundary;
import cbit.vcell.util.FunctionRangeGenerator;
import cbit.vcell.util.FunctionRangeGenerator.FunctionStatistics;

/**
 * Insert the type's description here.
 * Creation date: (3/13/2001 12:53:10 PM)
 * @author: Frank Morgan
 */
public class PDEDataContextPanel extends javax.swing.JPanel implements CurveValueProvider {
	//
	private PDEDataViewer.DataInfoProvider dataInfoProvider;
	//
	private java.util.Vector<Curve> membraneSamplerCurves = null;
	private java.util.Hashtable<SampledCurve, int[]> membranesAndIndexes = null;
	private java.util.Hashtable<SampledCurve, Vector<Double>> contoursAndValues = null;
	private MeshDisplayAdapter meshDisplayAdapter = null;
	private ImagePlaneManagerPanel ivjImagePlaneManagerPanel = null;
	private PDEDataContext fieldPdeDataContext = null;
	private SpatialSelection fieldSpatialSelection = null;
	private int fieldSlice = 0;
	private int fieldNormalAxis = 0;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP2Aligning = false;
	private DisplayAdapterServicePanel ivjdisplayAdapterServicePanel1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private ImagePlaneManager ivjimagePlaneManager1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private DisplayAdapterService ivjdisplayAdapterService1 = null;
	private static int uniquCurveID = 0;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == PDEDataContextPanel.this.getImagePlaneManagerPanel() && (evt.getPropertyName().equals("displayAdapterServicePanel"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == PDEDataContextPanel.this.getImagePlaneManagerPanel() && (evt.getPropertyName().equals("imagePlaneManager"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == PDEDataContextPanel.this.getImagePlaneManagerPanel() && (evt.getPropertyName().equals("sourceDataInfo"))) 
				connEtoC10(evt);
			if (evt.getSource() == PDEDataContextPanel.this.getimagePlaneManager1() && (evt.getPropertyName().equals("slice"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == PDEDataContextPanel.this && (evt.getPropertyName().equals("slice"))) 
				connPtoP4SetSource();
			if (evt.getSource() == PDEDataContextPanel.this.getimagePlaneManager1() && (evt.getPropertyName().equals("normalAxis"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == PDEDataContextPanel.this && (evt.getPropertyName().equals("normalAxis"))) 
				connPtoP5SetSource();
			if (evt.getSource() == PDEDataContextPanel.this.getimagePlaneManager1() && (evt.getPropertyName().equals("imagePlaneData"))) 
				connEtoC3(evt);
			if (evt.getSource() == PDEDataContextPanel.this.getImagePlaneManagerPanel()) 
				connEtoC4(evt);
			if (evt.getSource() == PDEDataContextPanel.this.getImagePlaneManagerPanel() && (evt.getPropertyName().equals("curveRendererSelection"))) 
				connEtoC1(evt);
			if (evt.getSource() == PDEDataContextPanel.this) 
				connEtoC6(evt);
		};
	};
/**
 * PDEDataContextPanel2 constructor comment.
 */
public PDEDataContextPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 4:49:39 PM)
 */
private void colorMembraneCurvesPrivate(java.util.Hashtable<SampledCurve, int[]> curvesAndMembraneIndexes,MeshDisplayAdapter meshDisplayAdapter) {
	DisplayAdapterService das = getdisplayAdapterService1();
	if (curvesAndMembraneIndexes != null) {
		java.util.Enumeration<SampledCurve> keysEnum = curvesAndMembraneIndexes.keys();
		while (keysEnum.hasMoreElements()) {
			SampledCurve curve = keysEnum.nextElement();
			int[] membraneIndexes = curvesAndMembraneIndexes.get(curve);
			double[] membraneValues = null;
			if(membraneIndexes != null && getPdeDataContext().getDataValues() != null && getPdeDataContext().getDataIdentifier() != null){
				membraneValues =
					meshDisplayAdapter.getDataValuesForMembraneIndexes(membraneIndexes,
						(recodeDataForDomainInfo.isRecoded?recodeDataForDomainInfo.getRecodedDataForDomain():getPdeDataContext().getDataValues()),
						getPdeDataContext().getDataIdentifier().getVariableType());
			}
			if (membraneValues != null) {
				int[] valueColors = new int[membraneValues.length];
				for (int i = 0; i < membraneValues.length; i += 1) {
					valueColors[i] = das.getColorFromValue(membraneValues[i]);
				}
				getImagePlaneManagerPanel().getCurveRenderer().renderPropertySegmentColors(curve, valueColors);
				getImagePlaneManagerPanel().getCurveRenderer().renderPropertySegmentIndexes(curve, membraneIndexes);
			} else {
				getImagePlaneManagerPanel().getCurveRenderer().renderPropertySegmentColors(curve, null);
				getImagePlaneManagerPanel().getCurveRenderer().renderPropertySegmentIndexes(curve, null);
			}
		}
	}
}
/**
 * connEtoC1:  (ImagePlaneManagerPanel.curveRendererSelection --> PDEDataContextPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("curveRendererSelection", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (ImagePlaneManagerPanel.sourceDataInfo --> PDEDataContextPanel.updateContours()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateContours();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DisplayAdapterService1.this --> PDEDataContextPanel.initDisplayAdapterService(Lcbit.image.DisplayAdapterService;)V)
 * @param value cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(DisplayAdapterService value) {
	try {
		// user code begin {1}
		// user code end
		this.initDisplayAdapterService(getdisplayAdapterService1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (imagePlaneManager1.imagePlaneData --> PDEDataContextPanel.updateMembraneCurves()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateMembraneCurves();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ImagePlaneManagerPanel.propertyChange.propertyChange(java.beans.PropertyChangeEvent) --> PDEDataContextPanel.imagePlaneManagerPanel_PropertyChange(Ljava.beans.PropertyChangeEvent;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePlaneManagerPanel_PropertyChange(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (PDEDataContextPanel2.propertyChange.propertyChange(java.beans.PropertyChangeEvent) --> PDEDataContextPanel.createSpatialSample(Ljava.beans.PropertyChangeEvent;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.createSpatialSample(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (PDEDataContextPanel.initialize() --> ImagePlaneManagerPanel.curveValueProvider)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel().setCurveValueProvider(this);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (pdeDataContext1.sourceDataInfo --> ImagePlaneManagerPanel.sourceDataInfo)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		recodeDataForDomain();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private static class RecodeDataForDomainInfo{
	private double[] recodedDataForDomain;
	private Range recodedDataRange;
	boolean isRecoded;
	public RecodeDataForDomainInfo(boolean isRecoded,double[] recodedDataForDomain,Range recodedDataRange) {
		super();
		this.isRecoded = isRecoded;
		this.recodedDataForDomain = recodedDataForDomain;
		this.recodedDataRange = recodedDataRange;
	}
	public double[] getRecodedDataForDomain() {
		return recodedDataForDomain;
	}
	public Range getRecodedDataRange() {
		return recodedDataRange;
	}
	public boolean isRecoded() {
		return isRecoded;
	}
}
private double[] originalData = null;
private RecodeDataForDomainInfo recodeDataForDomainInfo = null;
private boolean bDataInfoProviderNull = true;
private Range lastFunctionStatisticsRange;
private BitSet inDomainBitSet;
public void recodeDataForDomain() {
	//This method recodes data (out of domain data is set to 'out of range' value that is displayed differently)
	// and calculates a domain aware min,max range for variables that have a domain
	//Volume display data is set here
	//Membrane BACKGROUND display data is set here
	//Membrane ELEMENT display data is set in colorMmebraneCurvesPrivate(...)
	//
	if (getPdeDataContext() == null) {
		return;
	}
	recodeDataForDomain0();
	VariableType vt = getPdeDataContext().getDataIdentifier().getVariableType();
	//This creates an appropriate volume data data holder for volume data types
	//or creates a dummy background for membrane data types
		SourceDataInfo recodedSourceDataInfo =
			calculateSourceDataInfo(getPdeDataContext().getCartesianMesh(),
					recodeDataForDomainInfo.getRecodedDataForDomain(), 
					getPdeDataContext().getDataIdentifier().getVariableType(),
					recodeDataForDomainInfo.getRecodedDataRange());
		getImagePlaneManagerPanel().setSourceDataInfo(recodedSourceDataInfo);
}
private void recodeDataForDomain0() {
	Domain varDomain = getPdeDataContext().getDataIdentifier().getDomain();
	double[] tempRecodedData = null;
	Range dataRange = null;
	VariableType vt = getPdeDataContext().getDataIdentifier().getVariableType();
	boolean bRecoding = getDataInfoProvider() != null && varDomain != null;	
	
	if (getPdeDataContext().getDataValues() != originalData ||
		recodeDataForDomainInfo == null ||
		((getDataInfoProvider() == null) != bDataInfoProviderNull) ||
		!Compare.isEqualOrNull(functionStatisticsRange, lastFunctionStatisticsRange)) {
		lastFunctionStatisticsRange = functionStatisticsRange;
		bDataInfoProviderNull = (getDataInfoProvider() == null);
		originalData = getPdeDataContext().getDataValues();
		tempRecodedData = originalData;
		
		double illegalNumber = Double.POSITIVE_INFINITY;
		if (bRecoding) {
			tempRecodedData = new double[originalData.length];
			System.arraycopy(originalData, 0, tempRecodedData, 0, tempRecodedData.length);
			for(int i = 0; i < originalData.length; i++){
				if(!Double.isNaN(originalData[i])){
					illegalNumber = Math.min(illegalNumber, originalData[i]);
				}
			}
		}
		illegalNumber-=1;//
		
		final CartesianMesh cartesianMesh = getPdeDataContext().getCartesianMesh();
		double minCurrTime = Double.POSITIVE_INFINITY;
		double maxCurrTime = Double.NEGATIVE_INFINITY;
		inDomainBitSet = new BitSet(tempRecodedData.length);
		inDomainBitSet.set(0, tempRecodedData.length, true);
		for (int i = 0; i < tempRecodedData.length; i ++) {
			if (bRecoding) {
				if(!isInDomain(cartesianMesh, varDomain, dataInfoProvider, i, vt)){
					tempRecodedData[i] = illegalNumber;
					inDomainBitSet.set(i, false);
				}
			}
			if(!Double.isNaN(tempRecodedData[i]) && tempRecodedData[i] != illegalNumber){
				minCurrTime = Math.min(minCurrTime, tempRecodedData[i]);
				maxCurrTime = Math.max(maxCurrTime, tempRecodedData[i]);
			}
		}
		if(!getdisplayAdapterService1().getAllTimes() || functionStatisticsRange == null){
			dataRange = new Range(minCurrTime,maxCurrTime);
		}else if(functionStatisticsRange != null){
			dataRange = functionStatisticsRange;
		}else{
			throw new RuntimeException("Unexpected state for range calculation");
		}
	}else{
		dataRange = recodeDataForDomainInfo.getRecodedDataRange();
		tempRecodedData = recodeDataForDomainInfo.getRecodedDataForDomain();
	}
	if (bRecoding) {
		recodeDataForDomainInfo = new RecodeDataForDomainInfo(true, tempRecodedData, dataRange);
	}else{
		recodeDataForDomainInfo = new RecodeDataForDomainInfo(false, tempRecodedData, dataRange);		
	}
}

public BitSet getInDomainBitSet(){
	return inDomainBitSet;
}
private static boolean isInDomain(CartesianMesh cartesianMesh,Domain varDomain,DataInfoProvider dataInfoProvider,int i,VariableType vt){
	if(!cartesianMesh.hasSubvolumeInfo()){
		return true;
	}
	if (vt.equals(VariableType.VOLUME) && !(cartesianMesh.isChomboMesh())) {
		int subvol = cartesianMesh.getSubVolumeFromVolumeIndex(i);
		if (varDomain != null &&
			dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol) != null &&
			!dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol).equals(varDomain.getName())) {
			return false;
		}
	} else if (vt.equals(VariableType.VOLUME_REGION)) {
		int subvol = cartesianMesh.getVolumeRegionMapSubvolume().get(i);
		if (varDomain != null &&
			dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol) != null &&
			!dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol).equals(varDomain.getName())) {
			return false;
		}
	} else if (vt.equals(VariableType.MEMBRANE) && !(cartesianMesh.isChomboMesh())) {
		int insideVolumeIndex = cartesianMesh.getMembraneElements()[i].getInsideVolumeIndex();
		int subvol1 =  cartesianMesh.getSubVolumeFromVolumeIndex(insideVolumeIndex);
		int outsideVolumeIndex = cartesianMesh.getMembraneElements()[i].getOutsideVolumeIndex();
		int subvol2 =  cartesianMesh.getSubVolumeFromVolumeIndex(outsideVolumeIndex);
		if (varDomain != null &&
			dataInfoProvider.getSimulationModelInfo().getMembraneName(subvol1, subvol2, true) != null &&
			!dataInfoProvider.getSimulationModelInfo().getMembraneName(subvol1, subvol2, true).equals(varDomain.getName())) {
			return false;
		}
	} else if (vt.equals(VariableType.MEMBRANE_REGION)) {
		int[] subvols = cartesianMesh.getMembraneRegionMapSubvolumesInOut().get(i);
		if (varDomain != null &&
			dataInfoProvider.getSimulationModelInfo().getMembraneName(subvols[0], subvols[1], true) != null &&
			!dataInfoProvider.getSimulationModelInfo().getMembraneName(subvols[0], subvols[1], true).equals(varDomain.getName())) {
			return false;
		}
	}
	return true;
}
private Range functionStatisticsRange;
public void setFunctionStatisticsRange(Range functionStatisticsRange){
	this.functionStatisticsRange = functionStatisticsRange;
}
/**
 * connPtoP2SetTarget:  (ImagePlaneManagerPanel.displayAdapterServicePanel <--> displayAdapterServicePanel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setdisplayAdapterServicePanel1(getImagePlaneManagerPanel().getDisplayAdapterServicePanel());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetTarget:  (ImagePlaneManagerPanel.imagePlaneManager <--> imagePlaneManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setimagePlaneManager1(getImagePlaneManagerPanel().getImagePlaneManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP4SetSource:  (imagePlaneManager1.slice <--> PDEDataContextPanel.slice)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getimagePlaneManager1() != null)) {
				getimagePlaneManager1().setSlice(this.getSlice());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP4SetTarget:  (imagePlaneManager1.slice <--> PDEDataContextPanel.slice)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getimagePlaneManager1() != null)) {
				this.setSlice(getimagePlaneManager1().getSlice());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetSource:  (imagePlaneManager1.normalAxis <--> PDEDataContextPanel.normalAxis)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getimagePlaneManager1() != null)) {
				getimagePlaneManager1().setNormalAxis(this.getNormalAxis());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetTarget:  (imagePlaneManager1.normalAxis <--> PDEDataContextPanel.normalAxis)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getimagePlaneManager1() != null)) {
				this.setNormalAxis(getimagePlaneManager1().getNormalAxis());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP6SetTarget:  (displayAdapterServicePanel1.displayAdapterService <--> displayAdapterService1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		setdisplayAdapterService1(getdisplayAdapterServicePanel1().getDisplayAdapterService());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void createSpatialSample(java.beans.PropertyChangeEvent propertyChangeEvent) {
	/* moved from PDEResultsPanel; comment below is from when it was there */
	//PDEResultsPanel and PDEDatacontextPanel should be merged into one
	//this was put here to allow spatialsamples to know when to be 2D projections
	if(propertyChangeEvent.getPropertyName().equals("curveRendererSelection")){
		SpatialSelection sl[] = null;
		if(	getImagePlaneManagerPanel() != null &&
			getImagePlaneManagerPanel().getCurveRenderer() != null &&
			getImagePlaneManagerPanel().getCurveRenderer().getSelection() != null &&
			getImagePlaneManagerPanel().getCurveRenderer().getSelection().getCurve().isValid()){
			sl = fetchSpatialSelections(getImagePlaneManagerPanel().getCurveRenderer().getSelection().getCurve(),false);
		}
		setSpatialSelection((sl!=null?sl[0]:null));
	}
}

/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 8:40:06 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
public void curveAdded(Curve curve) {	
	fireDataSamplers();	
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 7:42:59 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
public void curveRemoved(Curve curve) {
	fireDataSamplers();	
}

private SpatialSelection[] fetchSpatialSelections(Curve curveOfInterest,boolean bFetchOnlyVisible) {
	return fetchSpatialSelections0(curveOfInterest,bFetchOnlyVisible,getPdeDataContext().getDataIdentifier().getVariableType());
}

/**
 * Insert the method's description here.
 * Creation date: (6/28/2003 4:57:18 PM)
 * @return cbit.vcell.simdata.gui.SpatialSelection[]
 */
private SpatialSelection[] fetchSpatialSelections0(Curve curveOfInterest,boolean bFetchOnlyVisible,VariableType vt) {
	//
	java.util.Vector<SpatialSelection> spatialSelection = new java.util.Vector<SpatialSelection>();
	//
	if (getPdeDataContext() != null &&
		getPdeDataContext().getCartesianMesh() != null &&
		getImagePlaneManagerPanel() != null &&
		getImagePlaneManagerPanel().getCurveRenderer() != null){
		//
		CartesianMesh cm = getPdeDataContext().getCartesianMesh();
		Curve[] curves = getImagePlaneManagerPanel().getCurveRenderer().getAllCurves();
		//
		if(curves != null && curves.length > 0){
			for(int i = 0;i < curves.length;i+= 1){
				boolean bIsVisible = getImagePlaneManagerPanel().getCurveRenderer().getRenderPropertyVisible(curves[i]);
				if( (bFetchOnlyVisible && !bIsVisible) ||  
					(curveOfInterest != null && curves[i] != curveOfInterest)){
					continue;
				}
				//
				if(	(vt.equals(VariableType.POSTPROCESSING)  || vt.equals(VariableType.VOLUME) || vt.equals(VariableType.VOLUME_REGION)) &&
					curves[i] instanceof ControlPointCurve &&
					!(curves[i] instanceof CurveSelectionCurve) &&
					(	
						curves[i].getDescription() == null ||
						curves[i].getDescription().startsWith(CurveValueProvider.DESCRIPTION_VOLUME)
					) &&
					(membranesAndIndexes == null || !membranesAndIndexes.containsKey(curves[i]))){	//Volume
					//
					Curve samplerCurve = null;
//					if(isSpatial2D){
						samplerCurve = projectCurveOntoSlice(curves[i].getSampledCurve());
//					}else{
//						samplerCurve = curves[i];
//					}
					if(samplerCurve != null){
						samplerCurve.setDescription(curves[i].getDescription());
						spatialSelection.add(new SpatialSelectionVolume(new CurveSelectionInfo(samplerCurve),vt,cm));
					}

					
				}else if((vt.equals(VariableType.MEMBRANE) || vt.equals(VariableType.MEMBRANE_REGION)) && 
							membranesAndIndexes != null){											//Membrane
					//
					if(curves[i] instanceof CurveSelectionCurve){
						CurveSelectionCurve csCurve = (CurveSelectionCurve)curves[i];
						if(csCurve.getSourceCurveSelectionInfo().getCurve() instanceof ControlPointCurve){
							int[] csisegsel = csCurve.getSourceCurveSelectionInfo().getSegmentsInSelectionOrder();
							if(csisegsel != null){
								ControlPointCurve cscpcCurve = (ControlPointCurve)(csCurve.getSourceCurveSelectionInfo().getCurve());
								Curve[] membraneCurves = (Curve[])(membranesAndIndexes.keySet().toArray(new Curve[membranesAndIndexes.size()]));
								//See if CurveSelectionCurve matches controlpoints in space of a membrane we have
								for(int j =0;j < membraneCurves.length;j+= 1){
									if(membraneCurves[j] instanceof ControlPointCurve){//They should all be
										ControlPointCurve cpc = (ControlPointCurve)membraneCurves[j];
										boolean bSame = true;
										for(int k=0;k<csisegsel.length;k+= 1){
											if(	csisegsel[k] >= cpc.getControlPointCount() ||
												csisegsel[k] >= cscpcCurve.getControlPointCount() ||
												!Coordinate.get2DProjection(
													cpc.getControlPoint(csisegsel[k]),
													getNormalAxis()).equals(
												Coordinate.get2DProjection(
														cscpcCurve.getControlPoint(csisegsel[k]),
														getNormalAxis()))){
												//
												bSame = false;
												break;
											}
											
										}
										if(bSame){
											int[] mi = (int[])membranesAndIndexes.get(membraneCurves[j]);
											spatialSelection.add(new SpatialSelectionMembrane(
												new CurveSelectionInfo(membraneCurves[j],
															csisegsel[0],
															csisegsel[csisegsel.length-1],
															csCurve.getSourceCurveSelectionInfo().getDirectionNegative()),
													vt,cm,mi,csCurve));
										}
									}
								}
							}
						}
					}else if(curves[i] instanceof SinglePoint &&
							(	
								curves[i].getDescription() == null ||
								curves[i].getDescription().startsWith(CurveValueProvider.DESCRIPTION_MEMBRANE)
							)){
						CurveSelectionInfo[] csiArr = getImagePlaneManagerPanel().getCurveRenderer().getCloseCurveSelectionInfos(curves[i].getBeginningCoordinate());
						if(csiArr != null && csiArr.length > 0){
							for(int j =0;j<csiArr.length;j+= 1){
								if(membranesAndIndexes.containsKey(csiArr[j].getCurve())){
									CurveSelectionInfo closestCSI =
										getImagePlaneManagerPanel().getCurveRenderer().getClosestSegmentSelectionInfo(curves[i].getBeginningCoordinate(),csiArr[j].getCurve());
									int[] mi = (int[])membranesAndIndexes.get(csiArr[j].getCurve());
									spatialSelection.add(new SpatialSelectionMembrane(closestCSI,vt,cm,mi,(SinglePoint)curves[i]));
									break;
								}
							}
						}
					}
				}
			}			
		}
	}
	//
	if(spatialSelection.size() > 0){
		SpatialSelection[] ss = new SpatialSelection[spatialSelection.size()];
		spatialSelection.copyInto(ss);
		return ss;
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2003 4:57:18 PM)
 * @return cbit.vcell.simdata.gui.SpatialSelection[]
 */
public SpatialSelection[] fetchSpatialSelections(boolean bIgnoreSelection,boolean bFetchOnlyVisible) {
	if(	getImagePlaneManagerPanel() != null &&
		getImagePlaneManagerPanel().getCurveRenderer() != null){
		return fetchSpatialSelections(
			(bIgnoreSelection || getImagePlaneManagerPanel().getCurveRenderer().getSelection() == null
			?
			null
			:
			getImagePlaneManagerPanel().getCurveRenderer().getSelection().getCurve()
			)
			,bFetchOnlyVisible);
	}
	return null;
}

public SpatialSelection[] fetchSpatialSelectionsAll(VariableType vt) {
	if(	getImagePlaneManagerPanel() != null &&
		getImagePlaneManagerPanel().getCurveRenderer() != null){
		return fetchSpatialSelections0(null,false,vt);
	}
	return null;
}

public SpatialSelection[] fetchSpatialSelections(VariableType vt, boolean bIgnoreSelection,boolean bFetchOnlyVisible) {
	if(	getImagePlaneManagerPanel() != null &&
		getImagePlaneManagerPanel().getCurveRenderer() != null){
		CurveSelectionInfo selection = getImagePlaneManagerPanel().getCurveRenderer().getSelection();
		return fetchSpatialSelections0((bIgnoreSelection || selection == null ? null : selection.getCurve()), bFetchOnlyVisible, vt);		
	}
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 7:42:59 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
private void fireDataSamplers() {

	//fire "dataSamplers" if there are any
	boolean bTimeDataSamplerVisible = false;
	boolean bSpatialDataSamplerVisible = false;
	CurveRenderer cr = getImagePlaneManagerPanel().getCurveRenderer();
	if(cr != null){
		Curve[] curves = getImagePlaneManagerPanel().getCurveRenderer().getAllCurves();
		if(curves != null && curves.length > 0){
			for(int i=0;i<curves.length;i+= 1){
				if(membranesAndIndexes == null || !membranesAndIndexes.containsKey(curves[i])){
					boolean isCurveValidDataSampler = isValidDataSampler(curves[i]);
					bTimeDataSamplerVisible = bTimeDataSamplerVisible || (isCurveValidDataSampler && curves[i] instanceof SinglePoint);
					bSpatialDataSamplerVisible =
							bSpatialDataSamplerVisible || 
							(isCurveValidDataSampler && !(curves[i] instanceof SinglePoint)) &&
							cr.getSelection() != null &&
							cr.getSelection().getCurve() == curves[i];
				}
			}
		}
	}
	//
	firePropertyChange("timeDataSamplers",!bTimeDataSamplerVisible,bTimeDataSamplerVisible);
	firePropertyChange("spatialDataSamplers",!bSpatialDataSamplerVisible,bSpatialDataSamplerVisible);
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/26/2001 3:19:07 PM)
 * @return cbit.vcell.geometry.Curve[]
 */
public Curve[] getAllUserCurves() {
	return getImagePlaneManagerPanel().getCurveRenderer().getAllUserCurves();
}

/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 12:53:10 PM)
 * @return java.lang.String
 * @param csi cbit.vcell.geometry.CurveSelectionInfo
 */
public String getCurveValue(CurveSelectionInfo csi) {
	String infoS = null;
	if (csi.getType() == CurveSelectionInfo.TYPE_SEGMENT) {
		if (membranesAndIndexes != null) {
			java.util.Enumeration<SampledCurve> keysEnum = membranesAndIndexes.keys();
			while (keysEnum.hasMoreElements()) {
				Curve curve = (Curve) keysEnum.nextElement();
				if(csi.getCurve() == curve){
					int[] membraneIndexes = (int[]) membranesAndIndexes.get(curve);
					if(meshDisplayAdapter != null){
						double[] membraneValues =
								meshDisplayAdapter.getDataValuesForMembraneIndexes(
									membraneIndexes,
									getPdeDataContext().getDataValues(),
									getPdeDataContext().getDataIdentifier().getVariableType());
						if(membraneValues != null){
							Coordinate segmentWC = getPdeDataContext().getCartesianMesh().getCoordinateFromMembraneIndex(membraneIndexes[csi.getSegment()]);
							String xCoordString = NumberUtils.formatNumber(segmentWC.getX());
							String yCoordString = NumberUtils.formatNumber(segmentWC.getY());
							String zCoordString = NumberUtils.formatNumber(segmentWC.getZ());
							boolean bDefined = getDataInfoProvider() == null || getDataInfoProvider().isDefined(membraneIndexes[csi.getSegment()]);
							infoS = "("+xCoordString+","+yCoordString+","+zCoordString+")  ["+
										membraneIndexes[csi.getSegment()]+"]  Value = " +
										(bDefined ? membraneValues[csi.getSegment()] : "Undefined");
							if (getPdeDataContext().getCartesianMesh() != null && getPdeDataContext().getCartesianMesh().isChomboMesh())
							{
								if (bDefined && getDataInfoProvider() != null)
								{
									StructureMetricsEntry structure = ((CartesianMeshChombo)getDataInfoProvider().getPDEDataContext().getCartesianMesh()).getStructureInfo(getDataInfoProvider().getPDEDataContext().getDataIdentifier());
									if (structure != null)
									{
										infoS+= " || " + structure.getDisplayLabel();
									}
								}
							}
							else
							{
								if(getDataInfoProvider() != null){
									PDEDataViewer.MembraneDataInfo membraneDataInfo =
										getDataInfoProvider().getMembraneDataInfo(membraneIndexes[csi.getSegment()]);
									infoS+= "          ";
									infoS+= " \""+membraneDataInfo.membraneName+"\"";
									infoS+= " mrID="+membraneDataInfo.membraneRegionID;
								}
							
								String curveDescr = CurveRenderer.getROIDescriptions(segmentWC,getImagePlaneManagerPanel().getCurveRenderer());
								if(curveDescr != null){infoS+= "     "+curveDescr;}
							}
							break;
						}
					}
				}
			}
		}
	}
	return infoS;
}
/**
 * Return the displayAdapterService1 property value.
 * @return cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DisplayAdapterService getdisplayAdapterService1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterService1;
}
/**
 * Return the displayAdapterServicePanel1 property value.
 * @return cbit.image.DisplayAdapterServicePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DisplayAdapterServicePanel getdisplayAdapterServicePanel1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterServicePanel1;
}
/**
 * Return the imagePlaneManager1 property value.
 * @return cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePlaneManager getimagePlaneManager1() {
	// user code begin {1}
	// user code end
	return ivjimagePlaneManager1;
}
/**
 * Return the ImagePlaneManagerPanel1 property value.
 * @return cbit.image.ImagePlaneManagerPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePlaneManagerPanel getImagePlaneManagerPanel() {
	if (ivjImagePlaneManagerPanel == null) {
		try {
			ivjImagePlaneManagerPanel = new ImagePlaneManagerPanel();
			ivjImagePlaneManagerPanel.setName("ImagePlaneManagerPanel");
			ivjImagePlaneManagerPanel.setMode(1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePlaneManagerPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:10:48 PM)
 */
public CurveSelectionInfo getInitalCurveSelection(int tool, Coordinate wc) {
	//
	CurveSelectionInfo newCurveSelection = null;
	VariableType variableType = getPdeDataContext().getDataIdentifier().getVariableType();
	if(	variableType.equals(VariableType.MEMBRANE) || variableType.equals(VariableType.MEMBRANE_REGION)){
		
		if (getPdeDataContext().getCartesianMesh().isChomboMesh() && tool == CurveEditorTool.TOOL_POINT)
		{
			newCurveSelection = findChomboSinglePointSelectionInfoForPoint(wc);
		}
		else
		{
			CurveSelectionInfo[] closeCSI = getImagePlaneManagerPanel().getCurveRenderer().getCloseCurveSelectionInfos(wc);
			if(closeCSI != null){
				for(int i =0;i < closeCSI.length;i+= 1){
					if(membranesAndIndexes != null && membranesAndIndexes.containsKey(closeCSI[i].getCurve())){
						if (tool == CurveEditorTool.TOOL_LINE) {
							newCurveSelection = new CurveSelectionInfo(new CurveSelectionCurve((SampledCurve)(closeCSI[i].getCurve())));
						}else if(tool == CurveEditorTool.TOOL_POINT) {
							newCurveSelection = new CurveSelectionInfo(new CurveSelectionCurve((SampledCurve)(closeCSI[i].getCurve())));
							double dist = closeCSI[i].getCurve().getDistanceTo(wc);
							int segmentIndex = closeCSI[i].getCurve().pickSegment(wc, dist*1.1);
							Coordinate[] coordArr = closeCSI[i].getCurve().getSampledCurve().getControlPointsForSegment(segmentIndex);
							Coordinate middleCoord = new Coordinate((coordArr[0].getX()+coordArr[1].getX())/2,(coordArr[0].getY()+coordArr[1].getY())/2,(coordArr[0].getZ()+coordArr[1].getZ())/2);
							newCurveSelection = new CurveSelectionInfo(new SinglePoint(middleCoord));
						}
						break;
					}
				}
					
				}
			}
	}
	if(newCurveSelection != null){
		if(membraneSamplerCurves == null){
			membraneSamplerCurves = new java.util.Vector<Curve>();
		}
		membraneSamplerCurves.add(newCurveSelection.getCurve());
	}
	return newCurveSelection;
}
/**
 * Gets the normalAxis property (int) value.
 * @return The normalAxis property value.
 * @see #setNormalAxis
 */
public int getNormalAxis() {
	return fieldNormalAxis;
}
public int getViewZoom(){
	return getImagePlaneManagerPanel().getViewZoom();
}
/**
 * Gets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @return The pdeDataContext property value.
 * @see #setPdeDataContext
 */
public PDEDataContext getPdeDataContext() {
	return fieldPdeDataContext;
}
/**
 * Gets the slice property (int) value.
 * @return The slice property value.
 */
public int getSlice() {
	return fieldSlice;
}
/**
 * Gets the spatialSelection property (cbit.vcell.simdata.gui.SpatialSelection) value.
 * @return The spatialSelection property value.
 */
public SpatialSelection getSpatialSelection() {
	return fieldSpatialSelection;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Comment
 */
private void imagePlaneManagerPanel_PropertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
	if(propertyChangeEvent.getSource() == getImagePlaneManagerPanel()){
		//These properties are from generic propertyChangeEvents from the ImagePlaneMangerPanel.DisplayAdapterService
		//These are necessary so that curves are updated before a repaint that redraws them
		if( propertyChangeEvent.getPropertyName().equals(DisplayAdapterService.VALUE_DOMAIN_PROP) ||
			propertyChangeEvent.getPropertyName().equals("activeScaleRange") ||
			propertyChangeEvent.getPropertyName().equals("activeColorModelID")){
				//System.out.println("PDEDataContextPanel<--ImagePlaneMangerPanel.propertyChange="+propertyChangeEvent.getPropertyName());
				refreshColorCurves();
			}
	}
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getImagePlaneManagerPanel().addPropertyChangeListener(ivjEventHandler);
//	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP6SetTarget();
}
/**
 * Comment
 */
private void initDisplayAdapterService(DisplayAdapterService das) {
	das.setValueDomain(null);
	das.addColorModelForValues(
		DisplayAdapterService.createGrayColorModel(), 
		DisplayAdapterService.createGraySpecialColors(),
		DisplayAdapterService.GRAY);
	das.addColorModelForValues(
		DisplayAdapterService.createBlueRedColorModel(),
		DisplayAdapterService.createBlueRedSpecialColors(),
		DisplayAdapterService.BLUERED);
	das.setActiveColorModelID(DisplayAdapterService.BLUERED);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PDEDataContextPanel2");
		setLayout(new java.awt.BorderLayout());
		setSize(578, 487);
		add(getImagePlaneManagerPanel(), "Center");
		initConnections();
		connEtoM1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void initMeshDisplayAdapter() {
	if(getPdeDataContext() != null && getPdeDataContext().getCartesianMesh() != null){
		meshDisplayAdapter = new MeshDisplayAdapter(getPdeDataContext().getCartesianMesh());
	}else{
		meshDisplayAdapter = null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:10:48 PM)
 */
public boolean isAddControlPointOK(int tool, Coordinate wc,Curve addedToThisCurve) {
	//
	CurveRenderer curveR = getImagePlaneManagerPanel().getCurveRenderer();
	VariableType variableType = getPdeDataContext().getDataIdentifier().getVariableType();
	if(variableType.equals(VariableType.POSTPROCESSING) || variableType.equals(VariableType.VOLUME) || variableType.equals(VariableType.VOLUME_REGION)){
		return true;
	}

	if(variableType.equals(VariableType.MEMBRANE) || variableType.equals(VariableType.MEMBRANE_REGION)){
		CurveSelectionInfo[] closeCSI = curveR.getCloseCurveSelectionInfos(wc);
		if(closeCSI != null && closeCSI.length > 0){
			for(int i=0;i<closeCSI.length;i+= 1){
				if(tool == CurveEditorTool.TOOL_POINT && addedToThisCurve instanceof SinglePoint &&
					membranesAndIndexes != null && membranesAndIndexes.containsKey(closeCSI[i].getCurve())){
					return true;
				}else if(tool == CurveEditorTool.TOOL_LINE &&
							addedToThisCurve instanceof CurveSelectionCurve){
					Curve sourceCurve = ((CurveSelectionCurve)(addedToThisCurve)).getSourceCurveSelectionInfo().getCurve();
					if(sourceCurve == closeCSI[i].getCurve()){
						if(membranesAndIndexes != null && membranesAndIndexes.containsKey(sourceCurve)){
							return true;
						}
					}
				}
			}
		}
	}
	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 7:44:14 PM)
 * @return boolean
 * @param curve cbit.vcell.geometry.Curve
 */
private boolean isValidDataSampler(Curve curve) {
	if(!curve.isValid()){
		return false;
	}
	VariableType vt = getPdeDataContext().getDataIdentifier().getVariableType();
	boolean isCurveVisible = true;
	//
	if (vt.equals(VariableType.POSTPROCESSING) ||vt.equals(VariableType.VOLUME) || vt.equals(VariableType.VOLUME_REGION)) {
			//
			if(membraneSamplerCurves != null && membraneSamplerCurves.contains(curve)){isCurveVisible = false;}
			else{
				isCurveVisible = true;
				if(!(curve instanceof SinglePoint)){
					double zeroZ = Coordinate.convertAxisFromStandardXYZToNormal(
							curve.getSampledCurve().getControlPoint(0), Coordinate.Z_AXIS, getNormalAxis());
					for (int i = 1; i < curve.getSampledCurve().getControlPointCount(); i++) {
						double indexZ =Coordinate.convertAxisFromStandardXYZToNormal(
								curve.getSampledCurve().getControlPoint(i), Coordinate.Z_AXIS, getNormalAxis());
						if(zeroZ != indexZ){
							isCurveVisible = false;
						}
					}
				}
			}
			//
	} else if (vt.equals(VariableType.MEMBRANE) || vt.equals(VariableType.MEMBRANE_REGION)) {
			//
			if(membraneSamplerCurves != null && membraneSamplerCurves.contains(curve)) {
				isCurveVisible = (fetchSpatialSelections(curve,false) != null);
			} else {
				isCurveVisible = false;
			}
			//
	}
	//
	return isCurveVisible;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PDEDataContextPanel aPDEDataContextPanel;
		aPDEDataContextPanel = new PDEDataContextPanel();
		frame.setContentPane(aPDEDataContextPanel);
		frame.setSize(aPDEDataContextPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 1:05:56 PM)
 * @return cbit.vcell.geometry.ControlPointCurve
 * @param curve cbit.vcell.geometry.ControlPointCurve
 */
private ControlPointCurve projectCurveOntoSlice(ControlPointCurve curve) {
	//for SinglePoint(timepoint) and PolyLine(spatial) samplers(always stored in world coordinates),
	//convert the curve coordinates into view coordinates from the sliceviewer
	ControlPointCurve cpCurve = null;
	java.util.Vector<Coordinate> cpV = new java.util.Vector<Coordinate>();
	int normalAxis = getimagePlaneManager1().getNormalAxis();
	for(int i=0;i < curve.getControlPointCount();i+= 1){
		//convert curves that are always stored in world coordinates into coordinates that
		//represent how user sees them in the slice viewer
		double xCoord = Coordinate.convertAxisFromStandardXYZToNormal(curve.getControlPoint(i),Coordinate.X_AXIS,normalAxis);
		double yCoord = Coordinate.convertAxisFromStandardXYZToNormal(curve.getControlPoint(i),Coordinate.Y_AXIS,normalAxis);
		//Get z from slice
		double zCoord = Coordinate.convertAxisFromStandardXYZToNormal(getimagePlaneManager1().getWorldCoordinateFromUnitized2D(0,0),Coordinate.Z_AXIS,normalAxis);
		//These are now the real coordinates as they are viewed in the slice viewer
		//Coordinate newCoord = new Coordinate(xCoord,yCoord,zCoord);
		Coordinate newCoord = Coordinate.convertCoordinateFromNormalToStandardXYZ(xCoord,yCoord,zCoord,normalAxis);
		cpV.add(newCoord);
	}
	if(cpV.size() > 0){
		Coordinate[] cpArr = new Coordinate[cpV.size()];
		cpV.copyInto(cpArr);
		//Determine if curve has been projected down to a single point
		boolean bSinglePoint = true;
		for(int i=0;i<cpArr.length;i+= 1){
			if(i > 0 && !cpArr[i].equals(cpArr[i-1])){
				bSinglePoint = false;
				break;
			}
		}
		//if(curve instanceof SinglePoint){
		if(bSinglePoint){
			cpCurve = new SinglePoint(cpArr[0]);
		}else if(curve instanceof SampledCurve){
			cpCurve = new PolyLine(cpArr);
		}
	}
	return cpCurve;
}
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:10:48 PM)
 */
public boolean providesInitalCurve(int tool, Coordinate wc) {
	
	VariableType variableType = getPdeDataContext().getDataIdentifier().getVariableType();
	if(variableType.equals(VariableType.MEMBRANE) || variableType.equals(VariableType.MEMBRANE_REGION)){
			return true;
	}else{
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 4:49:39 PM)
 */
private void refreshColorCurves() {
    if (getPdeDataContext() != null) {
        if (meshDisplayAdapter != null) {
            colorMembraneCurvesPrivate(membranesAndIndexes, meshDisplayAdapter);
            //colorCurvesPrivate(contoursAndValues,meshDisplayAdapter);
        }
    }
}
/**
 * Set the displayAdapterService1 to a new value.
 * @param newValue cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void setdisplayAdapterService1(DisplayAdapterService newValue) {
	if (ivjdisplayAdapterService1 != newValue) {
		try {
			DisplayAdapterService oldValue = getdisplayAdapterService1();
			ivjdisplayAdapterService1 = newValue;
			connEtoC2(ivjdisplayAdapterService1);
			firePropertyChange("displayAdapterService1", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Set the displayAdapterServicePanel1 to a new value.
 * @param newValue cbit.image.DisplayAdapterServicePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdisplayAdapterServicePanel1(DisplayAdapterServicePanel newValue) {
	if (ivjdisplayAdapterServicePanel1 != newValue) {
		try {
			ivjdisplayAdapterServicePanel1 = newValue;
			connPtoP6SetTarget();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Set the imagePlaneManager1 to a new value.
 * @param newValue cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setimagePlaneManager1(ImagePlaneManager newValue) {
	if (ivjimagePlaneManager1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjimagePlaneManager1 != null) {
				ivjimagePlaneManager1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjimagePlaneManager1 = newValue;

			/* Listen for events from the new object */
			if (ivjimagePlaneManager1 != null) {
				ivjimagePlaneManager1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP4SetTarget();
			connPtoP5SetTarget();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Sets the normalAxis property (int) value.
 * @param normalAxis The new value for the property.
 * @see #getNormalAxis
 */
public void setNormalAxis(int normalAxis) {
	int oldValue = fieldNormalAxis;
	fieldNormalAxis = normalAxis;
	firePropertyChange("normalAxis", new Integer(oldValue), new Integer(normalAxis));
}
/**
 * Sets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @param pdeDataContext The new value for the property.
 * @see #getPdeDataContext
 */
public void setPdeDataContext(PDEDataContext pdeDataContext) {
	PDEDataContext oldValue = fieldPdeDataContext;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
	}
	fieldPdeDataContext = pdeDataContext;
	if (getPdeDataContext() != null) {
		getPdeDataContext().addPropertyChangeListener(ivjEventHandler);
	}
	if(!PDEDataViewer.isParameterScan(oldValue, getPdeDataContext())){
		initMeshDisplayAdapter();	
		if(ivjImagePlaneManagerPanel != null && ivjImagePlaneManagerPanel.getCurveRenderer() != null && getPdeDataContext() != null){
			ivjImagePlaneManagerPanel.getCurveRenderer().setCartesianMesh(getPdeDataContext().getCartesianMesh());
		}
		if (getPdeDataContext() != null) {
			recodeDataForDomain();
		}		
	}
}
/**
 * Sets the slice property (int) value.
 * @param slice The new value for the property.
 * @see #getSlice
 */
public void setSlice(int slice) {
	int oldValue = fieldSlice;
	fieldSlice = slice;
	firePropertyChange("slice", new Integer(oldValue), new Integer(slice));
}
/**
 * Sets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @param pdeDataContext The new value for the property.
 * @see #getPdeDataContext
 */
private void setSpatialSelection(SpatialSelection spatialSelection) {
	SpatialSelection oldValue = fieldSpatialSelection;
	fieldSpatialSelection = spatialSelection;
	firePropertyChange("spatialSelection", oldValue, spatialSelection);
}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2000 4:14:39 PM)
 */
private void updateContours() {
	//
	if (getPdeDataContext() == null ||
			getPdeDataContext().getDataIdentifier() == null) {
		return;
	}
	//Remove previous curves
	CurveRenderer curveRenderer = getImagePlaneManagerPanel().getCurveRenderer();
	if (contoursAndValues != null) {
		java.util.Enumeration<SampledCurve> keysEnum = contoursAndValues.keys();
		while (keysEnum.hasMoreElements()) {
			Curve curve = keysEnum.nextElement();
			curveRenderer.removeCurve(curve);
		}
	}
	//
	contoursAndValues = null;
	boolean hasValues = false;
	if(meshDisplayAdapter != null){
		VariableType variableType = getPdeDataContext().getDataIdentifier().getVariableType();
		if (variableType.equals(VariableType.VOLUME)) {
			//Get curves with no values for overlay on volume Data
			contoursAndValues = meshDisplayAdapter.getCurvesFromContours(null);
		} else if (variableType.equals(VariableType.CONTOUR)) {
			//get curves with values
			contoursAndValues = meshDisplayAdapter.getCurvesFromContours(getPdeDataContext().getDataValues());
			hasValues = true;
		}
	}
	//Add new curves to curveRenderer
	if (contoursAndValues != null) {
		java.util.Enumeration<SampledCurve> keysEnum = contoursAndValues.keys();
		while (keysEnum.hasMoreElements()) {
			Curve curve = (Curve) keysEnum.nextElement();
			//
			curveRenderer.addCurve(curve);
			//
			curveRenderer.renderPropertyEditable(curve, false);
			curveRenderer.renderPropertySelectable(curve, hasValues);
			if (hasValues) {
				curveRenderer.renderPropertySubSelectionType(curve, CurveRenderer.SUBSELECTION_SEGMENT);
			} else {
				curveRenderer.renderPropertyLineWidthMultiplier(curve, 3);
			}
		}
	}

}

public Hashtable<SampledCurve, int[]>[] getMembranesAndIndexes(){
	Hashtable<SampledCurve, int[]>[] membraneSlices = new Hashtable[getimagePlaneManager1().getSourceDataInfo().getZSize()];
	//meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
	int normalAxis = getImagePlaneManagerPanel().getImagePlaneManager().getNormalAxis();
	for (int i = 0; i < membraneSlices.length; i++) {
		membraneSlices[i] = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, i);
	}
	return membraneSlices;
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/00 3:15:49 PM)
 */
private void updateMembraneCurves() {
	//
	if (getPdeDataContext() == null ||
			getPdeDataContext().getDataIdentifier() == null) {
		return;
	}
	int normalAxis = getImagePlaneManagerPanel().getImagePlaneManager().getNormalAxis();
	int slice = getImagePlaneManagerPanel().getImagePlaneManager().getSlice();
	//Remove previous curves
	CurveRenderer curveRenderer = getImagePlaneManagerPanel().getCurveRenderer();	
	if (membranesAndIndexes != null) {
		java.util.Enumeration<SampledCurve> keysEnum = membranesAndIndexes.keys();
		while (keysEnum.hasMoreElements()) {
			Curve curve = keysEnum.nextElement();
			curveRenderer.removeCurve(curve);
		}
	}
	//
	membranesAndIndexes = null;
	boolean hasValues = false;
	if(meshDisplayAdapter != null){
		//Get new curves for slice and normalAxis
		VariableType variableType = getPdeDataContext().getDataIdentifier().getVariableType();
		if (variableType.equals(VariableType.VOLUME) || variableType.equals(VariableType.VOLUME_REGION)) {
			//Turn off showing Membrane values over mouse
			//getImagePlaneManagerPanel().setCurveValueProvider(null);
			//
			//GET CURVES WITH NO VALUES FOR OVERLAY ON VOLUME DATA
			membranesAndIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
		} else if (variableType.equals(VariableType.MEMBRANE)) {
			//Turn on showing Membrane values over mouse
			//getImagePlaneManagerPanel().setCurveValueProvider(this);
			//
			//GET CURVES WITH VALUES
			membranesAndIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
			hasValues = true;
		} else if (variableType.equals(VariableType.MEMBRANE_REGION)) {
			//Turn on showing Membrane values over mouse
			//getImagePlaneManagerPanel().setCurveValueProvider(this);
			//
			//GET CURVES WITH REGIONIDS
			membranesAndIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
			/*
			//RegionID values
			double[] regionValues = getPdeDataContext().getDataValues();
			java.util.Iterator regionIDCurveValues = membranesAndValues.values().iterator();
			//convert RegionID to value
			while(regionIDCurveValues.hasNext()){
				java.util.Vector regionIDValues = (java.util.Vector)regionIDCurveValues.next();
				for(int i = 0;i < regionIDValues.size();i+= 1){
					int regionID = (int)(((Double)(regionIDValues.elementAt(i))).doubleValue());
					Double decodedRegionValue = new Double(regionValues[regionID]);
					regionIDValues.setElementAt(decodedRegionValue,i);
				}
			}
			*/
			hasValues = true;
		}
	}
	//Add new curves to curveRenderer
	if (membranesAndIndexes != null) {
		java.util.Enumeration<SampledCurve> keysEnum = membranesAndIndexes.keys();
		while (keysEnum.hasMoreElements()) {
			Curve curve = keysEnum.nextElement();
			//
			curveRenderer.addCurve(curve);
			//
			curveRenderer.renderPropertyEditable(curve, false);
			curveRenderer.renderPropertySelectable(curve, false);
			if(!hasValues){
				curveRenderer.renderPropertyLineWidthMultiplier(curve,3);
			}
			//getImagePlaneManagerPanel().getCurveRenderer().renderPropertySelectable(curve, hasValues);
			//if (hasValues) {
				//getImagePlaneManagerPanel().getCurveRenderer().renderPropertySubSelectionType(curve, cbit.vcell.geometry.gui.CurveRenderer.SUBSELECTION_SEGMENT);
			//}else{
				//getImagePlaneManagerPanel().getCurveRenderer().renderPropertyLineWidthMultiplier(curve,3);
			//}
		}
	}
	//
	refreshColorCurves();
	//
	//Set visibility of curve samplers
	Curve[] curves = curveRenderer.getAllCurves();
	if(curves != null && curves.length > 0){
		for(int i=0;i<curves.length;i+= 1){
			if(membranesAndIndexes == null || !membranesAndIndexes.containsKey(curves[i])){
				boolean isCurveValidDataSampler = isValidDataSampler(curves[i]);
				curveRenderer.renderPropertyVisible(curves[i],isCurveValidDataSampler);
				curveRenderer.renderPropertyEditable(curves[i],(membraneSamplerCurves != null && membraneSamplerCurves.contains(curves[i])?false:true));
			}
		}
	}
	
	//See if we should keep selection
	if(curveRenderer.getSelection() != null){
		CurveSelectionInfo csi = curveRenderer.getSelection();
		curveRenderer.selectNothing();
		if(isValidDataSampler(csi.getCurve())){
			curveRenderer.setSelection(csi);
		}
	}
	//
	//
	fireDataSamplers();
}
public PDEDataViewer.DataInfoProvider getDataInfoProvider() {
	return dataInfoProvider;
}
public void setDataInfoProvider(PDEDataViewer.DataInfoProvider dataInfoProvider) {
	this.dataInfoProvider = dataInfoProvider;
	getImagePlaneManagerPanel().setDataInfoProvider(getDataInfoProvider());
	recodeDataForDomain();
}

public void setDescription(Curve curve){
	VariableType variableType = getPdeDataContext().getDataIdentifier().getVariableType();
	boolean isVolume = variableType.getVariableDomain().equals(VariableDomain.VARIABLEDOMAIN_VOLUME) || variableType.getVariableDomain().equals(VariableDomain.VARIABLEDOMAIN_POSTPROCESSING);
	curve.setDescription(
			(isVolume?CurveValueProvider.DESCRIPTION_VOLUME:CurveValueProvider.DESCRIPTION_MEMBRANE)+
			(curve instanceof SinglePoint
				?"p"
				:(curve instanceof PolyLine?"l":
				(curve instanceof Spline?"s":
				(curve instanceof CurveSelectionCurve?"l":"?"))))+
				uniquCurveID++
	);
}
/**
 * Comment
 */
private SourceDataInfo calculateSourceDataInfo(CartesianMesh mesh, double[] sdiData,VariableType sdiVarType,Range newRange) {
	SourceDataInfo sdi = null;
	//
	if (sdiVarType.equals(VariableType.VOLUME) || sdiVarType.equals(VariableType.POSTPROCESSING)) {
		//Set data to display
		int yIncr = mesh.getSizeX();
		int zIncr = mesh.getSizeX() * mesh.getSizeY();
		sdi = 
			new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				sdiData, 
				mesh.getExtent(), 
				mesh.getOrigin(), 
				newRange, 
				0, 
				mesh.getSizeX(), 
				1, 
				mesh.getSizeY(), 
				yIncr, 
				mesh.getSizeZ(), 
				zIncr); 
	} else if(sdiVarType.equals(VariableType.VOLUME_REGION)) {
		//
		double[] expandedVolumeRegionValues = new double[mesh.getSizeX()*mesh.getSizeY()*mesh.getSizeZ()];
		double[] volumeRegionDataValues = sdiData;
		for(int i = 0;i<expandedVolumeRegionValues.length;i+= 1){
			expandedVolumeRegionValues[i] = volumeRegionDataValues[mesh.getVolumeRegionIndex(i)];
		}
		//
		int yIncr = mesh.getSizeX();
		int zIncr = mesh.getSizeX() * mesh.getSizeY();
		sdi = 
			new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				expandedVolumeRegionValues, 
				mesh.getExtent(), 
				mesh.getOrigin(), 
				newRange, 
				0, 
				mesh.getSizeX(), 
				1, 
				mesh.getSizeY(), 
				yIncr, 
				mesh.getSizeZ(), 
				zIncr); 

	}else {// Membranes
		//Create placeholder SDI with null data
		sdi = 
			new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				null,
				mesh.getExtent(), 
				mesh.getOrigin(), 
				newRange, 
				0, 
				mesh.getSizeX(), 
				0, 
				mesh.getSizeY(), 
				0, 
				mesh.getSizeZ(), 
				0); 
	}
	if(mesh.isChomboMesh()){
		sdi.setIsChombo(true);
	}
	else if (mesh instanceof CartesianMeshMovingBoundary)
	{
		sdi.setSolverDataType(SolverDataType.MBSData);
	}
	return sdi;
}

@Override
public CurveSelectionInfo findChomboCurveSelectionInfoForPoint(CoordinateIndex ci) {
	if (getPdeDataContext().getCartesianMesh().isChomboMesh())
	{
		CartesianMeshChombo chomboMesh = (CartesianMeshChombo) getPdeDataContext().getCartesianMesh();
		int memIndex = chomboMesh.findMembraneIndexFromVolumeIndex(ci);
		if (memIndex >= 0)
		{
			for (Entry<SampledCurve, int[]> entry : membranesAndIndexes.entrySet())
			{
				SampledCurve sc = entry.getKey();
				int[] memIndexes = entry.getValue();
				for (int idx = 0; idx < memIndexes.length; ++ idx)
				{
					if (memIndexes[idx] == memIndex)
					{
						return new CurveSelectionInfo(sc, CurveSelectionInfo.TYPE_SEGMENT, idx);
					}
				}
			}
		}
	}
	return null;
}

public CurveSelectionInfo findChomboSinglePointSelectionInfoForPoint(Coordinate wc) {
	if (getPdeDataContext().getCartesianMesh().isChomboMesh())
	{
		CartesianMeshChombo chomboMesh = (CartesianMeshChombo) getPdeDataContext().getCartesianMesh();
		int memIndex = chomboMesh.findMembraneIndexFromVolumeCoordinate(wc);
		if (memIndex >= 0)
		{
			Coordinate coord = chomboMesh.getMembraneElements()[memIndex].getCentroid();
			return new CurveSelectionInfo(new SinglePoint(coord));
		}
	}
	return null;
}

}
