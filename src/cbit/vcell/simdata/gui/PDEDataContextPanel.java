package cbit.vcell.simdata.gui;

import cbit.vcell.simdata.*;
import cbit.image.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.image.SourceDataInfo;
import cbit.vcell.geometry.*;
/**
 * Insert the type's description here.
 * Creation date: (3/13/2001 12:53:10 PM)
 * @author: Frank Morgan
 */
public class PDEDataContextPanel extends javax.swing.JPanel implements CurveValueProvider {
	//
	private java.util.Vector membraneSamplerCurves = null;
	private java.util.Hashtable membranesAndIndexes = null;
	private java.util.Hashtable contoursAndValues = null;
	private MeshDisplayAdapter meshDisplayAdapter = null;
	private ImagePlaneManagerPanel ivjImagePlaneManagerPanel = null;
	private cbit.vcell.simdata.PDEDataContext fieldPdeDataContext = null;
	private SpatialSelection fieldSpatialSelection = null;
	private int fieldSlice = 0;
	private int fieldNormalAxis = 0;
	private boolean ivjConnPtoP1Aligning = false;
	private PDEDataContext ivjpdeDataContext1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP2Aligning = false;
	private DisplayAdapterServicePanel ivjdisplayAdapterServicePanel1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private ImagePlaneManager ivjimagePlaneManager1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private DisplayAdapterService ivjdisplayAdapterService1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == PDEDataContextPanel.this && (evt.getPropertyName().equals("pdeDataContext"))) 
				connEtoC5(evt);
			if (evt.getSource() == PDEDataContextPanel.this && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP1SetTarget();
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
			if (evt.getSource() == PDEDataContextPanel.this.getpdeDataContext1() && (evt.getPropertyName().equals("sourceDataInfo"))) 
				connEtoM5(evt);
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
private void colorMembraneCurvesPrivate(java.util.Hashtable curvesAndMembraneIndexes,MeshDisplayAdapter meshDisplayAdapter) {
	cbit.image.DisplayAdapterService das = getdisplayAdapterService1();
	if (curvesAndMembraneIndexes != null) {
		java.util.Enumeration keysEnum = curvesAndMembraneIndexes.keys();
		while (keysEnum.hasMoreElements()) {
			cbit.vcell.geometry.Curve curve = (cbit.vcell.geometry.Curve) keysEnum.nextElement();
			int[] membraneIndexes = (int[]) curvesAndMembraneIndexes.get(curve);
			double[] membraneValues = null;
			if(membraneIndexes != null && getPdeDataContext().getDataValues() != null && getPdeDataContext().getDataIdentifier() != null){
				membraneValues = meshDisplayAdapter.getDataValuesForMembraneIndexes(membraneIndexes,getPdeDataContext().getDataValues(),getPdeDataContext().getDataIdentifier().getVariableType());
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
private void connEtoC2(cbit.image.DisplayAdapterService value) {
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
 * connEtoC5:  (PDEDataContextPanel.pdeDataContext --> PDEDataContextPanel.initMeshDisplayAdapter()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.initMeshDisplayAdapter();
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
 * connEtoC7:  (pdeDataContext1.this --> PDEDataContextPanel.slice2dEnable()V)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		this.slice2dEnable();
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
 * connEtoM2:  (pdeDataContext1.this --> ImagePlaneManagerPanel.sourceDataInfo)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getpdeDataContext1() != null)) {
			getImagePlaneManagerPanel().setSourceDataInfo(getpdeDataContext1().getSourceDataInfo());
		}
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
		getImagePlaneManagerPanel().setSourceDataInfo(getpdeDataContext1().getSourceDataInfo());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (PDEDataContextPanel2.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getpdeDataContext1() != null)) {
				this.setPdeDataContext(getpdeDataContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (PDEDataContextPanel2.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setpdeDataContext1(this.getPdeDataContext());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
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
		CurveSelectionInfo csi = (CurveSelectionInfo)propertyChangeEvent.getNewValue();
		createSpatialSelection((csi == null || !(csi.getCurve() instanceof CurveSelectionCurve)) && isSpatialSampling2D());
		boolean bSpatProj =
			getpdeDataContext1() != null &&
			getpdeDataContext1().getSourceDataInfo() != null &&
			getpdeDataContext1().getSourceDataInfo().getZSize() > 1;
		boolean always2D =
			csi != null && 
			(csi.getCurve() instanceof CurveSelectionCurve || csi.getCurve() instanceof SinglePoint);
		getImagePlaneManagerPanel().getSpatialProjectionJCheckBox().setEnabled(!always2D && csi != null && bSpatProj);
		//getImagePlaneManagerPanel().getSpatialProjectionJCheckBox().setSelected(((csi != null && always2D) || userPrefer2D) || !bSpatProj);
	}
}
/**
 * Comment
 */
public void createSpatialSelection(boolean isSpatialSampling2D) {
	SpatialSelection sl[] = null;
	if(	getImagePlaneManagerPanel() != null &&
		getImagePlaneManagerPanel().getCurveRenderer() != null &&
		getImagePlaneManagerPanel().getCurveRenderer().getSelection() != null &&
		getImagePlaneManagerPanel().getCurveRenderer().getSelection().getCurve().isValid()){
		sl = fetchSpatialSelections(getImagePlaneManagerPanel().getCurveRenderer().getSelection().getCurve(),isSpatialSampling2D,false);
	}
	setSpatialSelection((sl!=null?sl[0]:null));
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 8:40:06 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
public void curveAdded(cbit.vcell.geometry.Curve curve) {
	
	fireDataSamplers();	
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 7:42:59 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
public void curveRemoved(cbit.vcell.geometry.Curve curve) {

	fireDataSamplers();	
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2003 4:57:18 PM)
 * @return cbit.vcell.simdata.gui.SpatialSelection[]
 */
private SpatialSelection[] fetchSpatialSelections(Curve curveOfInterest,boolean isSpatial2D,boolean bFetchOnlyVisible) {
	//
	java.util.Vector spatialSelection = new java.util.Vector();
	//
	if (getPdeDataContext() != null &&
		getPdeDataContext().getCartesianMesh() != null &&
		getImagePlaneManagerPanel() != null &&
		getImagePlaneManagerPanel().getCurveRenderer() != null){
		//
		VariableType vt = getPdeDataContext().getDataIdentifier().getVariableType();
		cbit.vcell.solvers.CartesianMesh cm = getPdeDataContext().getCartesianMesh();
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
				if(	(vt.equals(VariableType.VOLUME) || vt.equals(VariableType.VOLUME_REGION)) &&
					curves[i] instanceof ControlPointCurve &&
					!(curves[i] instanceof CurveSelectionCurve) &&
					(membranesAndIndexes == null || !membranesAndIndexes.containsKey(curves[i]))){	//Volume
					//
					Curve samplerCurve = null;
					if(isSpatial2D){
					samplerCurve = projectCurveOntoSlice(curves[i].getSampledCurve());
					}else{
						samplerCurve = curves[i];
					}
					if(samplerCurve != null){
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
					}else if(curves[i] instanceof SinglePoint){
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
public SpatialSelection[] fetchSpatialSelections(boolean isSpatial2D,boolean bIgnoreSelection,boolean bFetchOnlyVisible) {
	if(	getImagePlaneManagerPanel() != null &&
		getImagePlaneManagerPanel().getCurveRenderer() != null){
		return fetchSpatialSelections(
			(bIgnoreSelection || getImagePlaneManagerPanel().getCurveRenderer().getSelection() == null
			?
			null
			:
			getImagePlaneManagerPanel().getCurveRenderer().getSelection().getCurve()
			)
			,isSpatial2D,bFetchOnlyVisible);
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
	cbit.vcell.geometry.gui.CurveRenderer cr = getImagePlaneManagerPanel().getCurveRenderer();
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
public String getCurveValue(cbit.vcell.geometry.CurveSelectionInfo csi) {
	String infoS = null;
	if (csi.getType() == cbit.vcell.geometry.CurveSelectionInfo.TYPE_SEGMENT) {
		if (membranesAndIndexes != null) {
			java.util.Enumeration keysEnum = membranesAndIndexes.keys();
			while (keysEnum.hasMoreElements()) {
				cbit.vcell.geometry.Curve curve = (cbit.vcell.geometry.Curve) keysEnum.nextElement();
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
							String xCoordString = cbit.util.NumberUtils.formatNumber(segmentWC.getX());
							String yCoordString = cbit.util.NumberUtils.formatNumber(segmentWC.getY());
							String zCoordString = cbit.util.NumberUtils.formatNumber(segmentWC.getZ());
							infoS = "("+xCoordString+","+yCoordString+","+zCoordString+")  ["+
										membraneIndexes[csi.getSegment()]+"]  Value = " +
										membraneValues[csi.getSegment()];
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
public cbit.image.DisplayAdapterService getdisplayAdapterService1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterService1;
}
/**
 * Return the displayAdapterServicePanel1 property value.
 * @return cbit.image.DisplayAdapterServicePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.DisplayAdapterServicePanel getdisplayAdapterServicePanel1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterServicePanel1;
}
/**
 * Return the imagePlaneManager1 property value.
 * @return cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.ImagePlaneManager getimagePlaneManager1() {
	// user code begin {1}
	// user code end
	return ivjimagePlaneManager1;
}
/**
 * Return the ImagePlaneManagerPanel1 property value.
 * @return cbit.image.ImagePlaneManagerPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.ImagePlaneManagerPanel getImagePlaneManagerPanel() {
	if (ivjImagePlaneManagerPanel == null) {
		try {
			ivjImagePlaneManagerPanel = new cbit.image.ImagePlaneManagerPanel();
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
public cbit.vcell.geometry.CurveSelectionInfo getInitalCurveSelection(int tool, cbit.vcell.geometry.Coordinate wc) {
	//
	CurveSelectionInfo newCurveSelection = null;
	if(	getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE_REGION)){
		//
			CurveSelectionInfo[] closeCSI = getImagePlaneManagerPanel().getCurveRenderer().getCloseCurveSelectionInfos(wc);
			if(closeCSI != null){
				for(int i =0;i < closeCSI.length;i+= 1){
					if(membranesAndIndexes != null && membranesAndIndexes.containsKey(closeCSI[i].getCurve())){
						if (tool == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_LINE) {
							newCurveSelection = new CurveSelectionInfo(new CurveSelectionCurve((SampledCurve)(closeCSI[i].getCurve())));
						}else if(tool == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_POINT) {
							newCurveSelection = new CurveSelectionInfo(new SinglePoint());
						}
						break;
					}
				}
				
			}
	}
	if(newCurveSelection != null){
		if(membraneSamplerCurves == null){
			membraneSamplerCurves = new java.util.Vector();
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
/**
 * Gets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @return The pdeDataContext property value.
 * @see #setPdeDataContext
 */
public cbit.vcell.simdata.PDEDataContext getPdeDataContext() {
	return fieldPdeDataContext;
}
/**
 * Return the pdeDataContext1 property value.
 * @return cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.PDEDataContext getpdeDataContext1() {
	// user code begin {1}
	// user code end
	return ivjpdeDataContext1;
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
		if( propertyChangeEvent.getPropertyName().equals("valueDomain") ||
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
	connPtoP1SetTarget();
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
		cbit.image.DisplayAdapterService.createGrayColorModel(), 
		cbit.image.DisplayAdapterService.createGraySpecialColors(),
		"Gray");
	das.addColorModelForValues(
		cbit.image.DisplayAdapterService.createBlueRedColorModel(),
		cbit.image.DisplayAdapterService.createBlueRedSpecialColors(),
		"BlueRed");
	das.setActiveColorModelID("BlueRed");
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
public boolean isAddControlPointOK(int tool, cbit.vcell.geometry.Coordinate wc,Curve addedToThisCurve) {
	//
	cbit.vcell.geometry.gui.CurveRenderer curveR = getImagePlaneManagerPanel().getCurveRenderer();
	if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME) ||
	getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME_REGION)){
		return true;
	}

	if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE) ||
	getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE_REGION)){
		CurveSelectionInfo[] closeCSI = curveR.getCloseCurveSelectionInfos(wc);
		if(closeCSI != null && closeCSI.length > 0){
			for(int i=0;i<closeCSI.length;i+= 1){
				if(tool == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_POINT && addedToThisCurve instanceof SinglePoint &&
					membranesAndIndexes != null && membranesAndIndexes.containsKey(closeCSI[i].getCurve())){
					return true;
				}else if(tool == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_LINE &&
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
 * Creation date: (7/17/2003 8:32:10 AM)
 * @return boolean
 */
public boolean isSpatialSampling2D() {
	return !getImagePlaneManagerPanel().getSpatialProjectionJCheckBox().isSelected();
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
	if (vt.equals(cbit.vcell.simdata.VariableType.VOLUME) || vt.equals(cbit.vcell.simdata.VariableType.VOLUME_REGION)) {
			//
			if(membraneSamplerCurves != null && membraneSamplerCurves.contains(curve)){isCurveVisible = false;}
			else{isCurveVisible = true;}
			//
	} else if (vt.equals(cbit.vcell.simdata.VariableType.MEMBRANE) || vt.equals(cbit.vcell.simdata.VariableType.MEMBRANE_REGION)) {
			//
			if(membraneSamplerCurves != null && membraneSamplerCurves.contains(curve)){isCurveVisible = (fetchSpatialSelections(curve,false,false) != null);}
			else{isCurveVisible = false;}
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
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
private void newVariables(String[] variableNames) throws Exception {
	// initializing default display states
	if (getpdeDataContext1() != null) {
		getdisplayAdapterService1().clearMarkedStates();
		getpdeDataContext1().setTimePoint(getpdeDataContext1().getTimePoints()[0]);
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
	java.util.Vector cpV = new java.util.Vector();
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
public boolean providesInitalCurve(int tool, cbit.vcell.geometry.Coordinate wc) {
	
	if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE_REGION)){
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
public void setdisplayAdapterService1(cbit.image.DisplayAdapterService newValue) {
	if (ivjdisplayAdapterService1 != newValue) {
		try {
			cbit.image.DisplayAdapterService oldValue = getdisplayAdapterService1();
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
private void setdisplayAdapterServicePanel1(cbit.image.DisplayAdapterServicePanel newValue) {
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
private void setimagePlaneManager1(cbit.image.ImagePlaneManager newValue) {
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
public void setPdeDataContext(cbit.vcell.simdata.PDEDataContext pdeDataContext) {
	cbit.vcell.simdata.PDEDataContext oldValue = fieldPdeDataContext;
	fieldPdeDataContext = pdeDataContext;
	firePropertyChange("pdeDataContext", oldValue, pdeDataContext);
}
/**
 * Set the pdeDataContext1 to a new value.
 * @param newValue cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setpdeDataContext1(cbit.vcell.simdata.PDEDataContext newValue) {
	if (ivjpdeDataContext1 != newValue) {
		try {
			cbit.vcell.simdata.PDEDataContext oldValue = getpdeDataContext1();
			/* Stop listening for events from the current object */
			if (ivjpdeDataContext1 != null) {
				ivjpdeDataContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjpdeDataContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjpdeDataContext1 != null) {
				ivjpdeDataContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM2(ivjpdeDataContext1);
			connEtoC7(ivjpdeDataContext1);
			firePropertyChange("pdeDataContext", oldValue, newValue);
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
 * Comment
 */
private void slice2dEnable() {
	boolean bSpatProj =
		getpdeDataContext1() != null &&
		getpdeDataContext1().getSourceDataInfo() != null &&
		getpdeDataContext1().getSourceDataInfo().getZSize() > 1;

	//getImagePlaneManagerPanel().getSpatialProjectionJCheckBox().setSelected(!bSpatProj);
	getImagePlaneManagerPanel().getSpatialProjectionJCheckBox().setEnabled(bSpatProj);
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
	if (contoursAndValues != null) {
		java.util.Enumeration keysEnum = contoursAndValues.keys();
		while (keysEnum.hasMoreElements()) {
			cbit.vcell.geometry.Curve curve = (cbit.vcell.geometry.Curve) keysEnum.nextElement();
			getImagePlaneManagerPanel().getCurveRenderer().removeCurve(curve);
		}
	}
	//
	contoursAndValues = null;
	boolean hasValues = false;
	if(meshDisplayAdapter != null){
		if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.simdata.VariableType.VOLUME)) {
			//Get curves with no values for overlay on volume Data
			contoursAndValues = meshDisplayAdapter.getCurvesFromContours(null);
		} else if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.simdata.VariableType.CONTOUR)) {
			//get curves with values
			contoursAndValues = meshDisplayAdapter.getCurvesFromContours(getPdeDataContext().getDataValues());
			hasValues = true;
		}
	}
	//Add new curves to curveRenderer
	if (contoursAndValues != null) {
		java.util.Enumeration keysEnum = contoursAndValues.keys();
		while (keysEnum.hasMoreElements()) {
			cbit.vcell.geometry.Curve curve = (cbit.vcell.geometry.Curve) keysEnum.nextElement();
			//
			getImagePlaneManagerPanel().getCurveRenderer().addCurve(curve);
			//
			getImagePlaneManagerPanel().getCurveRenderer().renderPropertyEditable(curve, false);
			getImagePlaneManagerPanel().getCurveRenderer().renderPropertySelectable(curve, hasValues);
			if (hasValues) {
				getImagePlaneManagerPanel().getCurveRenderer().renderPropertySubSelectionType(curve, cbit.vcell.geometry.gui.CurveRenderer.SUBSELECTION_SEGMENT);
			} else {
				getImagePlaneManagerPanel().getCurveRenderer().renderPropertyLineWidthMultiplier(curve, 3);
			}
		}
	}

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
	if (membranesAndIndexes != null) {
		java.util.Enumeration keysEnum = membranesAndIndexes.keys();
		while (keysEnum.hasMoreElements()) {
			cbit.vcell.geometry.Curve curve = (cbit.vcell.geometry.Curve) keysEnum.nextElement();
			getImagePlaneManagerPanel().getCurveRenderer().removeCurve(curve);
		}
	}
	//
	membranesAndIndexes = null;
	boolean hasValues = false;
	if(meshDisplayAdapter != null){
		//Get new curves for slice and normalAxis
		if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.simdata.VariableType.VOLUME) ||
			getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.simdata.VariableType.VOLUME_REGION)) {
			//Turn off showing Membrane values over mouse
			//getImagePlaneManagerPanel().setCurveValueProvider(null);
			//
			//GET CURVES WITH NO VALUES FOR OVERLAY ON VOLUME DATA
			membranesAndIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
		} else if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.simdata.VariableType.MEMBRANE)) {
			//Turn on showing Membrane values over mouse
			//getImagePlaneManagerPanel().setCurveValueProvider(this);
			//
			//GET CURVES WITH VALUES
			membranesAndIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
			hasValues = true;
		} else if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.simdata.VariableType.MEMBRANE_REGION)) {
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
		java.util.Enumeration keysEnum = membranesAndIndexes.keys();
		while (keysEnum.hasMoreElements()) {
			cbit.vcell.geometry.Curve curve = (cbit.vcell.geometry.Curve) keysEnum.nextElement();
			//
			getImagePlaneManagerPanel().getCurveRenderer().addCurve(curve);
			//
			getImagePlaneManagerPanel().getCurveRenderer().renderPropertyEditable(curve, false);
			getImagePlaneManagerPanel().getCurveRenderer().renderPropertySelectable(curve, false);
			if(!hasValues){getImagePlaneManagerPanel().getCurveRenderer().renderPropertyLineWidthMultiplier(curve,3);}
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
	cbit.vcell.geometry.gui.CurveRenderer cr = getImagePlaneManagerPanel().getCurveRenderer();
	if(cr != null){
		Curve[] curves = getImagePlaneManagerPanel().getCurveRenderer().getAllCurves();
		if(curves != null && curves.length > 0){
			for(int i=0;i<curves.length;i+= 1){
				if(membranesAndIndexes == null || !membranesAndIndexes.containsKey(curves[i])){
					boolean isCurveValidDataSampler = isValidDataSampler(curves[i]);
					cr.renderPropertyVisible(curves[i],isCurveValidDataSampler);
					cr.renderPropertyEditable(curves[i],(membraneSamplerCurves != null && membraneSamplerCurves.contains(curves[i])?false:true));
				}
			}
		}
	}
	//See if we should keep selection
	if(getImagePlaneManagerPanel().getCurveRenderer().getSelection() != null){
		CurveSelectionInfo csi = getImagePlaneManagerPanel().getCurveRenderer().getSelection();
		getImagePlaneManagerPanel().getCurveRenderer().selectNothing();
		if(isValidDataSampler(csi.getCurve())){
			getImagePlaneManagerPanel().getCurveRenderer().setSelection(csi);
		}
	}
	//
	//
	fireDataSamplers();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD0DCD516F1AA31F08BEBE2CDD4A64699CB63ACAAEAE6065538151A4DEEE5E7B24A168C933762CCDC99A763E0D5D4D6B3EE543875E850C0CC10A408B822C60D9322088472C1FE61EBA07CBA501006B4502F3B9F5050BF3A9F81E2DC771C7BF97D3A7BBD9A3BC62ACE3DFE671CFBFE775CF3BF6FA2943EFF4B2BD906C2C148FAD5E0FF273384A1750CA06CFDA17BDE0E2BBE1A16A7287EBEGB8A09C
	591F834FF620D50C27659588497F178B6F96F84F0177376077A16187DF2DE9009FBAFAE420D91DB73C38381E1F74F0BDFB48FBED67BEF86683D881074F2423283F3F33186117B3BC07AC03A04415A20F27330461B201A797F02DG66D24A6F03A7B5E0C5CDB56333DC17A86CFC50EF580FF2D4B2B9243236BB59733070D2561AE3286BC6E2A736A903F787GBA1FAD94FDD9814FBA6374C4035D66144BAF59A5172BBC60F4BBC4D9AC1F18F5565498DB5BDA6125456B1125F959A8FAA4D771A7A873714EDCA2C390
	52C15EDA023B3B8F75D860378C907F8947978DA2BE83FE8B1F266595FF4AFD2BBFDA47ECB7FFF53F3025521D73060EEF951FD21F4C1F86FD5B61FA4C70B66907A7FBB15E2B00369EA085A09320208C7281609D527FCF77FC824FA61B5C62F53B3D1EA61F2F596570F7FB9D128BFE5754G05617AA43B576F90845A47B7F716E958339850F6F7FD6BF6B4A609D53C3F137A6E91367D7C86439E0D18ECCB7C4A50DEC6E3E1AD8B46046A5E2E2BFBD9D9E8BE066A3EA028F517BD7AC8762B9A3A5BFF79C876AAA6A7D1
	25BB238C7B7A03FE6C6B4D709B5B3D0E7DCE701F93FC59E7C1BC4D4D1F131C4177AE202D7B0C6706B91F5B5215F32BD0F07CC64DB8A47CDB3C21301C5AD0D69EEE4BCAB34AB617D31DC795DB1288FE1D8ACFEDA9A1F90A6F391F43F82CC84B2379756CF99C2B4B0E27652D81C886C881588110DB1916D7D249F36CD46F67CB4CB12368171BE71DAE076497E85BE7FB0B6089E34CAFF6F882326831CB03225FA95ADCD2C0E0BC3BFAB1863610F62F105C37030E3312C7720B32536B816922DB12A5FF00723CE6BC00
	BCFE09D0CDCE37848A5CBE014B4DB79E0227CB8C4883BE987AD21003520B0C58A71744C110888260B70DCB896953BA705F8460D062F09D1157A97921DA5454F4F93D5333BE93FAA2A4D7E21CDFA635E39EFE2F2C64B1AEB6A1EE97522B381C4D4603AAB92DCEBB1AAF7A977898BB5A8F05DCB8827C2DGD600B80044AA1EC34F18F98E356E3FDBA8FB64FA4D9C5AG791CD2C5F3E7FBD578D8BAC9F4201D7BCE24651581F48138CE70B96001BEB31393D725BD362C4C26A3E174D69D7E48DA12266848DB048F47D0
	F6987E55A87273153E3CFAG7BB29E7CCA8458FC0277C1767943B4271B4500443338578F89A8B902BDC26D3C415C854F91721B4E195DG35GEDECDC9D836885B8897091409C407BG9DG6F82D4814CE02E82FC2C787FB3A2B79DEC4ABD89B59D408C308CA091A095E0974061D3E9F96DGBE0084000DG99G852753722A812CGF1G6BCE733E7BF030A2ABFAFC8E68605FE536FF40FC79D865E3B8EF9573193F57EA703431E787FBF633D86959B5227AEDDF04EFC675FBEE897930341C1CD9D26C74728F6376
	8E1375960B5B9BBA3146783E29FAFFDD03E73F2ACF78B8B93408DD6AF17271A2FC9F29FE3F7DF7089D9DD3AF1EC7DF25CB12C7EE15FC124781BF820CA674216DEC9E9877AAEDDEA3F89F4C9BADD028838AFEDF9F46CC14E53F53B6ABCB7D12ACBBBD93C1FAE19F6A8A4C5AFC41A9076BFA278F7B47BB87730642FF388FEB2E5D6B71C8E4F0ABCD987DC3D273A517648623DB3C339E1962CB7BF1EDBCADAD30B9272C7FB581678C57AC4EDFF43D39CE443E73791DEEA899B8858A2555B89F9C6CAAD645E8AB0CCFDC
	B76F06E7C1B52F65F39F71DAFE64729D4246B11FC1EBED12F07B778D3EEADAD717D787EBB93569DD13366D9F11390761835456163DD877B5163F1559244D51A90C2B4FA129684785CAABEAC0398EE7406792971A9C228F2662FE49FF49E91708884E97DF0D3106E8CCC8C697903AC58F7C749F9798FD74F2111E1C633CEE7F8BD9332C85DF578324D6733A7DE39352795C498AF60B5765225D8A3402AFB076D7487C06F14A00364297B476798A7D8393271B615D97C17F35C2DF866F8991F46FG27A7427BC6951D
	5A99BFD00079A94D4CCA306441C5C22712C3BF98B898C69BE479C469B78DE0BE39D46B0B329A547997B35FDB25B2FD996C19D3C3F5EFA0FC6B06B85FFC8D252F2C2173FC92BC5FD378AE8FF33E5CDA18E7GDAEB69DA40D52B66EB3D48792EB03ED535EC3D407828FDFBAD1CAF39162EA5100E7AD3EA59DA16707DC261AB2E2372AA6AA8DFD71D5A0F05914E672A237A16B33E04BA35FD9D231CAF0971EDE2FC5BC2785E9963FCBB6B281F7B6A291D957514EFF9BD727DC961EB2C27F1B357D3BBA76B29FD458D48
	F71FD5690FFABAC7E7B07E5D70ACD7641DF6F0BEC78325DFE92072929A54F1F9DDD26257C07DC8E9E07D9C4277CB05EF5F99AA6748994A67BB43EA8451FB6DB8675B44685B987F4EB3EABF44894ED758C8579568877217B5D2BF285E37A7B9DFDDA315E3EA24FC7370CC85E0F9B0457936B7523570EE46575E247663B78ADFFC930D4B5AA64A174C78E84DFA7E9856D1321F1F92AF09736501B9188BEAEA4E29766DD73AF299FFEA935F1FA776E28D49ED86DB812A1BF94D7D4DFE3EBFDFB51DA8647EEECB365E59
	0331195AA1B787EBEE728DF35934C66519D1E7B250B6B5737AD4B4285EA3B48582DE3B13ECCF58587E77C1F5CD263887877742F35C698F489DB0E94E73BA10BA0876627E052159FE6EB6B2D7E1AD5D8AFA8BDAA02EAD08CFA7F3950EF56A273D0A7B3972726D02757A550657F57C0CEFE167BCADC1BF17971E4C267A5ACDDC5FF6201734F2FDABCC288FEB86654BD478CC40932F70FDB600FC2B943E9B86B85FA660592D707D1670611824FC6F8D6DE3FDFA228D46A9G1DBFD68C210D21F3944F1524B358B7CE0F
	D36E1682132DA1F3CC6931C1011F895CFA2811791EF24F2DC25DB723862DB31A38B7A68DEB5BE8CCD25A0231C97F6DE3061DG544EC70738BF45DF2665993F64F63E502FE5A725B90651D7F952992076BC572436E77D1EF8CDFBA67FB1419077A535E35D17C1FBD21EF84C107E84376736E1EE0FF096729E002EC7C60C6A75086FAC9D1BEB61397EAC3779A653DF054566F8EEFF79C5EEBF55693E48F526022C5CF6AE2FE5G57F191EB81A5172BAC3C0F9B214DFCBBBF43EBBA53407A31D54B1652AEF232C76416
	EBF254E2E7D39DFFD6F42C8379990AEDBDA3980700F756EFA7DDC49726D47F26EC34FB161C9BE05781D300668F15F96B887777F0874C8F9DDCE65EA8368F38408201472AB284C75B7BC7F97B1550F633527E51D12C859E2F5FAD3A1A661D81AEA3FBAC9CCF65340EA9FDFD8E7A7A9C177354401BFA7D28587570D8E89F66DB39AC9F48C9D4E44DDAB99FFD3F592A95B71A47F73F274E63DCF105E69E47BFB5E1C8BFC773B737B318476DDB9EB3D83670BCFEDC314788BCF11DBCB78A9B70C956600AAF6FB974ED3A
	B244EDDA1612AC949F3AD653A6040D4C168C15CD05B02ECA1531E5F570DC2A6E0279360B4768D71A7AA96DB9CD9A15F7C2627292C1D63AA26FA6A9BFE45CE0BB1E1B4F6DFFCB494DEE49ED73833DE556FFC9A299CA65BE2B48AD6E061A544D658E0E57E37F7A3DBE49AFAF34CC0A9E98C0212F25B849165BA45193A8B706D05AF0EBF7CC60F65CB8582EB536C91D988BEDD95A3538D0982D546E7B46F91F47014D9B953B5F1D58476A01D3F4B9AFCB2547A87E6C8459572177E4918D32AFB971981145619FA6383C
	DDA02B22074B4B1E8C4D7D66C99E2FC96039C5613BE332084B6F439D2D9F0711325920F148696A94487CADE9BA8D2119A86F070D2CE722D6056935CD01988D8C74A1DD537CE192578AF8163035074F09D5D35C4E02DE18B7FB391D2B277EFE7DFA6174A9011D2BCA7DBE91A35EAF3AFD2E2574A20F7BCB0A1D4B4046750A1D62B40FBB5575726582DE9BAB9DED9E5C6DC0925333182B3A424F7BB3FB03E70BB94648F3C6FA8EDEE2C45D45C6FA8E1E2D607FC67025AABC35751EDE1EA355C0DBE364E737DB07F13E
	944E431A876084C097008BA07EBCDF6F19AF60995822E7639DB0717BBD904310438AFEC6EDC63D535282FBBFE8D657B69A07BB7AB18E0967231F2BF2206D6EA76DB6FE0BF634DE9E9BD6FF1B4BB9F04E102DD3AFB319EC21AFD8AFE96C2E31607E38917096G99A09EA0290F476DC6F3D93438716F84ACE054279ED3EC3130183EEDACE83B97E25417B3901B3EB58331693BB3C6FDC7064247D8CADF280C0C30F75A2EB222DDF9587B3E7E50F6B4AF9EA7FB7AA3C0EB84B08348G89G49GDB7BF9FEECB757C74B
	8FD2F2CC5E964035FB3D53AD61E7FB0A2D0FC7589AD1B7B4FDFCBAEA3BDD1ABE8E12730382936C0781DA812CG4B81EE8158E462BE1E9EEA0E66E393BFGB5F9151F9A0E32BAB19C1B1F77C7ED374D945A0E566E2D81BC5F8EFEE7A7AB011A1A8E4D85811B532EB772FD7ACE935F273FF41EFCC39D00D881D88678F70C4045587C39AAEABBF9C02B5D97165846425908F66F053D47EB6AFBBAC6FDAFC7348B9FFBEBB575A50E44964F7B22365B2029EFFEB4B6FDD7C7ED37C5D35F393158741923365B21292F509A
	1B3E8F23364B99542AAB8F1173469220D5839881DCGAB8112GD287F9DD39E6883F43AD25F6B611B292D9D0281DFF0A513F6723364B54742F53067E65DEC84BBB82D087E086388210G306182776F77516B26F2D5E0A9F5F35C915BB814A35A050F7BAD97345A55C83169EB0FE8D79E1E271A7A7E92233E6C71E87E6518355AED08E837343CF9B0EA3B92CDFDABA7E2537723286DCEE86AEB1F0CAD1E03916D42635925296FC9524E41E873E1BC6190EF66736797D3B14EE7916D426D3CC553CEE1BAB6FD79916D
	4273FAFD18BEFA5FA6011C792782EDBBG454D76A0AEF7A8AD6F7010F29F42C84E291577B2720E5FAF68FBF22FB9CC5FD281E5C66321BA7EA3E69D61F7DF34005645BF8C6BFFFF4F98567E7E7E4C307677771D4387A89EE428EFF5F17A6E61453F4F67DC547ABE7FAE7167BF8D385E734FFAE427DBA2EA69FAD21A47BB91D03D7BE6BD34342366C078FED0607345B5F66C4749A1BEAF2C3ED8A9C44E8BED8E276C75F3A1A6C974770E0FAB13C23FCF32F31B2F336847AF41229D3F9FD93463375632F87C12ACFA71
	23B17AF591798E8FA225G192C4981A0257DD946335D083B8CBDB063897C4C7162D09E63E9133DADA7953DCF0D687B18B9226D638BA378FD0C1EDF067B714AC831C28BCE309D3264C6F3589D0FD1ECAF0B7E89A9A28E7BC70B951ADE7B22D13DD831FB89435CD7236CB59ED7E4DB4774FD1D98537655BD266F2BBC965D572F4774FDDDBE965D57EB47709B1D4D69F07A59A1A375B1BD4447CA252E2CB606574E652A1A1420D11F68D918075CCFCD9A22E7E1D98AFE0E60572B70740CCC6863F72DB7812DF4181F67
	5DF9D1EDD71552A7BE0BBA7FAA957C11113DAA7C718A0EFFFCC4AD670442FF6F289AFFD241DFB526461FD2701DD6155E6ECAA5760FD875F3E11BD5BB971E306A6742D3566839701CD5BF97DE34C64F850338F85E1710FBC7A48663FE2F9BB21F369B30697BEA31E97BEA33697B6A34C57755EF5377F541965D576F5934737EE6073ABF037DBFA32973AB18F7E563EAFCB00F1ED7633B0FAB78DFCD04463012613F19D44BC1FE3A3EF8051C4947C37E2F9966EB8E1B93F109703EDE415DCDF01B61FD2BB2D6FCD3EA
	19411CBDBD2D461F8E66FE975EB5F3098B5ED9D990CC97495D8F65CED9864895GD78AD0F711FE4B257368CF675027F15881C13A10892E4202ED63FBF85BB5403F8EA095A09365DA380DC7E57C5E21F304437A73DA7BFEF5DE32F9077A7DD3F23EB38F7262ACBC96771E67EB32B5963E66127B842506FD2D7A7DDADF2C6B2425G55717EF72A0346315009EB93C879631BAD78BFB76214205096B82D2AEC846373FD723EEF047741A6CE2F24745F11F75F887F8674CC972F45AB8137F904777B136558BF4ABD19F2
	F1CE2E29E976629D39AE1AC4748C6ED4F7AEEBAF0C72B3B8C7B74A2C06F7A34064A83FC3B27FB7FE07A469A7378A3B9F513EAB13EE7A5A30EC144EB5EBC7035F828477E3862A73E7BDDCE7BA500B4748FD151EC2A1B4EE8FE75335BF259F6CD55331966E49D253DBC368E86F0A9CB57DD2889DE30467A265827F4E7150475C375D8F2D93DC7F34DC77FF102CE36CDE95FB2E5C7E43AC5F131733E9FFEDA67B893757A125903D7FE5647FC334815E0FD84938A5774D75F22640CA65D23B9E3E287E7ED2305AE54852
	314B455A4DDB03B127B65D6EBC48722F9C2AEE0FB4C73E0E169E63BA6AA5350E0A2F3AF43F71A4B1599B58F3771B2B190E4C909D3DAE0722030E331D915F6F16B6BE8D5D31352B7D566D681A67D48F667E8EEB78DA688C416F32062F05263A79784B91E12C087CFB61088B6BEB8A3CE7G88B648F300EAG130D6FD1922770FF5396BD3BB2F9FDDDB0F93BD45B86F6CE5F935B7FDE7D6B9E1E67497FFC27307E6AD65DFBE013B65A47F16CD97A479FE661137A1769C67F5683ED8B40AE0082BB64B7C01D1D7B17B9
	453E6B61368BF6D922FD125CF4D3B6F1CEE9AE084649096652DD67511E630A4D374C297333FA6F77B46FC5A3986D54D607BDBCBF7FF89E674F96893F3FF239F3920FC549DD3789A56FB79A8E6848DD4364ED368747D6B096BF4625931B8FCFCD63DD7BC1F1CAF347F98ECAB17D32EF92A71476577830E66CD662F6F97AC3BD990044EFDC7486814F753A9EC1497BB655EFDF77F7BFFFDE731D6A207198F7692FE15DAE6DB5EC40253D97DFF0ADFED6F115CB7FFF8962DC6A7FA520FD37E686671582C7DADE990091
	4081B06FD06E003ABF24E3C0A37F5B9C933806467F0413153D00FB117F1B70C47F7F39BC8F6E29ECDED9763AD1BE473F61417E123DBE02E47FB7614198791D9313B2E5E5783FFAF04EF2C96384CD7DFC400F38D56057BA45373BBC1FAE12277CCE884512690045ECC760B0BCD3C14E8E008289562DGF5GA600C900E59257715F1E52C5F450858D0921DFF4727FF5385607E3BD404688BB0319510F6BDDB351631ABCC36E6AC2BCCC8BBE36CFF9E08675F84747831252FF3F18A9D0F09D8123B760448D8C237973
	5539C7C3426F827BB10F14BBCD641AB09D65147EBF81FD7B7FB7905D7EBD32F6DE6C17A37362202C1D974532BAAF587F1BB039FE6550097D3F090C6BA73ADF895E73247F277E7B57716CAA69E961D5D9A8523BCE456F1FFC232E7D3BD658F5EB3FBC4E662B71707BA76F64235D1BG1FBE4E73EAFA7FE7FAF985A3335FAD3ADC32681AEE72B830E6B7AF48D230C69C4EC779814E430814F6B49F6D1ED23579FFD0CB8788E68DC33F4694GG78BFGGD0CB818294G94G88G88GD6FBB0B6E68DC33F4694GG
	78BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGG95GGGG
**end of data**/
}
}
