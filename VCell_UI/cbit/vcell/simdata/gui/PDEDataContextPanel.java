package cbit.vcell.simdata.gui;

import org.vcell.util.Coordinate;

import cbit.vcell.math.VariableType;
import cbit.vcell.mesh.CartesianMesh;
import cbit.vcell.simdata.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.image.gui.DisplayAdapterServicePanel;
import cbit.image.gui.ImagePlaneManager;
import cbit.image.gui.ImagePlaneManagerPanel;
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
	cbit.vcell.simdata.DisplayAdapterService das = getdisplayAdapterService1();
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
private void connEtoC2(cbit.vcell.simdata.DisplayAdapterService value) {
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
	cbit.vcell.geometry.CurveRenderer cr = getImagePlaneManagerPanel().getCurveRenderer();
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
							String xCoordString = org.vcell.util.NumberUtils.formatNumber(segmentWC.getX());
							String yCoordString = org.vcell.util.NumberUtils.formatNumber(segmentWC.getY());
							String zCoordString = org.vcell.util.NumberUtils.formatNumber(segmentWC.getZ());
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
public cbit.vcell.simdata.DisplayAdapterService getdisplayAdapterService1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterService1;
}
/**
 * Return the displayAdapterServicePanel1 property value.
 * @return cbit.image.DisplayAdapterServicePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.gui.DisplayAdapterServicePanel getdisplayAdapterServicePanel1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterServicePanel1;
}
/**
 * Return the imagePlaneManager1 property value.
 * @return cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.gui.ImagePlaneManager getimagePlaneManager1() {
	// user code begin {1}
	// user code end
	return ivjimagePlaneManager1;
}
/**
 * Return the ImagePlaneManagerPanel1 property value.
 * @return cbit.image.ImagePlaneManagerPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.gui.ImagePlaneManagerPanel getImagePlaneManagerPanel() {
	if (ivjImagePlaneManagerPanel == null) {
		try {
			ivjImagePlaneManagerPanel = new cbit.image.gui.ImagePlaneManagerPanel();
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
public cbit.vcell.geometry.CurveSelectionInfo getInitalCurveSelection(int tool, Coordinate wc) {
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
		cbit.vcell.simdata.DisplayAdapterService.createGrayColorModel(), 
		cbit.vcell.simdata.DisplayAdapterService.createGraySpecialColors(),
		"Gray");
	das.addColorModelForValues(
		cbit.vcell.simdata.DisplayAdapterService.createBlueRedColorModel(),
		cbit.vcell.simdata.DisplayAdapterService.createBlueRedSpecialColors(),
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
public boolean isAddControlPointOK(int tool, Coordinate wc,Curve addedToThisCurve) {
	//
	cbit.vcell.geometry.CurveRenderer curveR = getImagePlaneManagerPanel().getCurveRenderer();
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
	if (vt.equals(cbit.vcell.math.VariableType.VOLUME) || vt.equals(cbit.vcell.math.VariableType.VOLUME_REGION)) {
			//
			if(membraneSamplerCurves != null && membraneSamplerCurves.contains(curve)){isCurveVisible = false;}
			else{isCurveVisible = true;}
			//
	} else if (vt.equals(cbit.vcell.math.VariableType.MEMBRANE) || vt.equals(cbit.vcell.math.VariableType.MEMBRANE_REGION)) {
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
public boolean providesInitalCurve(int tool, Coordinate wc) {
	
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
public void setdisplayAdapterService1(cbit.vcell.simdata.DisplayAdapterService newValue) {
	if (ivjdisplayAdapterService1 != newValue) {
		try {
			cbit.vcell.simdata.DisplayAdapterService oldValue = getdisplayAdapterService1();
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
private void setdisplayAdapterServicePanel1(cbit.image.gui.DisplayAdapterServicePanel newValue) {
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
private void setimagePlaneManager1(cbit.image.gui.ImagePlaneManager newValue) {
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
	if (getPdeDataContext() == null) {
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
		if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.math.VariableType.VOLUME)) {
			//Get curves with no values for overlay on volume Data
			contoursAndValues = meshDisplayAdapter.getCurvesFromContours(null);
		} else if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.math.VariableType.CONTOUR)) {
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
				getImagePlaneManagerPanel().getCurveRenderer().renderPropertySubSelectionType(curve, cbit.vcell.geometry.CurveRenderer.SUBSELECTION_SEGMENT);
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
	if (getPdeDataContext() == null) {
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
		if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.math.VariableType.VOLUME) ||
			getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.math.VariableType.VOLUME_REGION)) {
			//Turn off showing Membrane values over mouse
			//getImagePlaneManagerPanel().setCurveValueProvider(null);
			//
			//GET CURVES WITH NO VALUES FOR OVERLAY ON VOLUME DATA
			membranesAndIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
		} else if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.math.VariableType.MEMBRANE)) {
			//Turn on showing Membrane values over mouse
			//getImagePlaneManagerPanel().setCurveValueProvider(this);
			//
			//GET CURVES WITH VALUES
			membranesAndIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(normalAxis, slice);
			hasValues = true;
		} else if (getPdeDataContext().getDataIdentifier().getVariableType().equals(cbit.vcell.math.VariableType.MEMBRANE_REGION)) {
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
	cbit.vcell.geometry.CurveRenderer cr = getImagePlaneManagerPanel().getCurveRenderer();
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
	D0CB838494G88G88G5A0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF494D516A08D840CE3D41420D1A3BFA3C6B723B8E6C74E8E4E30BBF14DEAF4DDA7F3CE1C49590D8AA3AA63E4BD4C185D2DF4028492B488A233C60D8ACBD0A612A01293C17305FCC913F41A108F24132E249B7AC7F7B5C9A0A3FB6F7BD4D7F7D725E31F314F39276A5DF75F7D3D7B6E7B54EBE167C7711BF275BB85A1F71340FEC7F285A16DDBC1D8FBEA6EFC0E0BF11A8A85456FE3006D4226A379
	700C073A287BCC054542563182A82F04F2BF145F03779D0234B76E9BF85110A3C1DD4E40CB7FBA351CB94DDC4EE6D2FEAA37881EEF81A4838E1F79AE143FAEF7B743E7B0BC07DC3DA0E4921AFCFD8943B5C1F990A08EA049C569D3603905C81EDDDD45687AEFC890327F59235F06FC94BCB91439E95BB676AC957E141B6DC6D9FF519731B6BAA82F82C0637304DD478F427328614268B743C3B6297C5230E83717FBED8E0BD9B2170F7AEC555506064686A8543B1C12B8AE994CCE516E3E08BCFD7A8242C3902A20
	1CC3F02F1EC0B9AB61BD95A0CB467734A23E46E3AA6C81F0FB38ED47769DE53A77DCF9D8489A7D47ADEFEB58966521B6A5F97C3615FEB61477E7528F7F54027EDE8FF545DED3619E00A6GABC094407B247F7F2579B3F8568EC975AE0743652CF53B6B6C96CF3B4BA25A613D3A9AEA982ED39CF6F9AC02C07B78107E000ABED381EDD77B3D5BD11FA49C627D1D58F93F107160AD7A4DAABE49C838220F77D2DFA4FB7DBE2132AFEB4ACE778646E3206C5302D236302EF273A69559A54FD6EEDE4F788812DFF60D04
	FDBD5C04FDDD8A6F588E879145DFA4F81D82CFE373BFC94CE0F9A9549578F8EC743C45F5E94BDFAC94D7ACD475C36C4F2375F69F55C1678B566569F6641D6423B277493A3CC27085177CF82A4BD99227D89E043A38B1D3A10D2FA75BF02C260F1B8A33810AA6A046G9AG868124899EE33BDA3E18E60C994C9E294EE733DBC40FC05BBE59328B1EB046BC66E627D7B2BB07C51359E3B38F59C52F40E852DB5087C3816D1EA331BF8CB2CE08CE51E316ECAEA7F0B7BBC4C974F8A94D8BAD5B1146A312DA234DA102
	G07DB60FCDFE95981CF3B59ABF53BE1680BFE8ADA7FFA8B7649A53389B88283F827FE49A1FEDCG76A7822C127DF0319579350A9E48965555EDAE57851F5B081688C517514FB7105C118674EBA7380FB71EC41C816A2D17B91F0456F794FC9AEC43283E59B34147182D899239B08974F1G49G19G42959EC32F747098EA58F61FF0605FEED60D21980867BDD7E86C9C3D92BC1646088C545382F5BA0025GEB2E70B9600D93BD0C8F4AD69BDB49CC2749207AB88D7A50DC3233094B58B2859D0E213CA07C26B0
	7C5F127BF24400FD1985FA8A13264252C95E877F51DACAE33A4E6C95F994F7F8A0GC50B3FC7281E696DED709CA06FF44EEC87789A20110D2B7DG9DG0781BE85988378882099608300C3G97B1D681764A76E7933ED52053A0C094C092C0BA0070D7D3E1990081E09CA09EA085E083406E6FCC05B5GD6009800E400CC00022B3C6FAC2D87F3437BE73B867E83267B474C163D8A9B03E98FB11BF979EB951AC676ECE64FF666ABAD3D86946F43D350DDD43C0FCDA39E2697138FCE4BF7DA7107FEFB1F71FA0F79
	6DED8DDF23FF5FD5148BD5E836A97A040F139DD368259CA7FB27207BD4717E673F016F68187AF59B5AAADE921DD20368961D96F871323A4CB6541D4D83A3AE394D8B846F06F9239E9235D7462FEFC31F19A549E39B72C9E217A8C9B667283FBE1B48727A065C7EA9074BFA238D7B47B5867306CCAF34E34E9DF6B91DA2995CF293D62FBF09F9C2340B8ED03A5E65F3CA94DFF0925746974489B667941DFCD340B9436E43790B2EB7134C58F7EE0F4D81A98327C0E146D51C8F92743B95BEBA88B8DCB79B61E93D4A
	F3795827BC176F39FC0F307A1CDB2F36B609DDF63DBE65AA4D2B99D77D391C6A74DF9A3A67F5947871DE2AEB7DD64C7BAA4B5F4ABC52E65FF9742B5BA2AA6AAB84DA376B9B64EB31F95DF673C42D456C062938CB74DC328D0B0485270B2EC2DF03B7C6C5039DAA5A4DCEF875D4882C7E6C65DDDAFCAAF85E3E73945A1403FE1C8139F0864F5BD7BB301E4F1DACE1573B6CF65A2DD0E71D013E7F87B23F211FEA205CB3037A7EAD39FE381957A7C1B9A5243E30055727C3F9C3C87D0F65FAE1A66464197EFA2A275D
	D00C71A9DE7409306441C5C22B9CC3DE43BBC1F5A696CF347E0A8163492ED8DF68E6224CA3A7795E2A0C492BE14F7E19D4764EEB106EB519AE0D552F1DC96779FC287FD026DB5541698621BC8E90F78DDD8B2C89201BE360F44FB03A755730758223237A351D62F4C53368DA826BD17E1ED9ECADCB6804CECE671EC579C54D22F4CBE7A96DF8C426DBB30B4A4BE0F49BE6A9753B360B53656BA8DD090E52FD29D3528D98B9DD1B0E5AE951D1BD2398DD068E6976F5F33AD89D75DB120E6A39DAC775F34FC63A0DB2
	DD59ECBAC7574C2674C6F8164B7CA64EF03AD52C7E1959145F0659CA3F34774A7E1BC36D58B30775739CA5DDC19F27334C21FCA699DD8A2323F21F1469CA66B2FF4C2574EDF315F6AC6967F47639F4DD01F6A03DEEAE35034A6D9B60F48B981FC446B78D1EE5D1C0CF63609C27BB9AC5574246A8CA979F2534E347A0274B0A22FE49E1F4C573A89D4DD9FF580FF9146C674F1BAF19474B3DE3B097D4D737A8766D6F6495B07A32F9FCFF7E3B864CA103D09E87081947F36ED35B787EFC410584A1E3DD45E62D3307
	8426C76ABCFF4EBD302176CD1A2356F5224C227990EF73F9FEFA5D205CA354FA3D2EE19B591E30313D6C14B2A7D35C0AD3DB61B9E273F825E618B447F99EC8BA057A627E052159FE6EF9326744DCFA9064DA816267A37E93F20E03E31D5AB9FC085B99F3F919B0F8732D7AC28DBB336633F31EEB95F6FE37F19315B73F194BBB8A755235DC5EAF09BC4C99142E320553A582CD16CC67EAC13A75B2DD3ACCD7928D31914D695EEDC5BA9C1314EE104CEB582751C03386004E9F3FBCC9E2B7E00E62311278AD760D4D
	E9135AC52F35A1E00EA95BEF92781940629DB54C76547B978BC72F1E552B1D51C43DED5567C4D31F6C394E6F1B269B07727A81281E6FF7F0FB5CC013F09D57332AD9CDCFDA373C83ED152CB6AF556735DA25BE491B23D575315E9D2B4FE4FA947C482F4F17F78D65B55D4575F9DE562787E8E2G68FA644EC665FAA465C7F4EC66403378FA2E73F34D7FABCCB547F37D5D862EBF1579F786AE338CF88D4A7CE613BDFA48DAC00EE55FA95E473150A66DFAFE06D77B6DB72C9F9B54F4A9EBA3A7FB04EF39AA455758
	59D4467ECEAE23A086F2DB8C57AD26937D60F579BC43240B6842144ADF1307FA7B483981F69D90F9E97CD01ED91D5C5EF16017A473EC6D4276DEBBE8A0F0DFC981B85AFEE597EF3F965A165E405B3FEF44DC60F4F99CE6FB6D384D4BF9B40660A91FB9C6392F01C70C4C677356F7357AD156EBFBF7E09F9A3AB92F9460A35C48F9BDAC535172EF3B557CC663783E8F15F1DCE01E2D9A47516BC675D5B752789D3C519F47C9B0265648636AE39357A781E8B2EF643131739BFC12B538EC4B6099ED1DA64FF01DE624
	2E94F66F38CED5A7045DB7D1DDEAEE726BB499BF1497934FF5DAFC0647D28C50245C44FD3425D7CDBE2D7BD2350E723B544B79898BCC05D58B64F1C85A78478D366331795A36776458EC979DC39E282F77F9AE09A4C2A95F0ABE4E578DBC93E43E31FD479067F139C50FB4D1EFB5BBE1G85964BF012AD9F924DCEEF39A120269137F67B852E47C2D3135A58A6F962DCE04B3236291942E825FAEF6C63FD1E89BA6F3E196BFD2E3F0865831B59EE3BAC166D2778F97DE4DF075613C5B4703EE443E3C4660754FE4E
	4FG3C22E4FE0D8301312F9B607EDA8DB49BE53A1786F6F17E1D38237560B012B79B548F79ED2D82193FC5D52341B52365DD3011F50EFE2D40F48C1D87B6AA9868C33A267974AC2E9570AC6160ADFCCE741D63FADA8197FF8B57B3635C5F2EDFCF9F79CAE0672AE2175B0C9E6FB2BB5C766974A2777B51C12EE7BA68D83C106B39E4107B1D4AFA63F2B14F0D1516C6A76EF6A00869D94CFBF941677DCD8B7DE70B7D8BC34F9969B97859C6146DDEC84F4173E43C1560E7A870D4578D8DBCC6E220AEFBA1BF3BCD6E
	467932A79656BCG51GCB8156GE4457275DED5A71E01CDF9B65E8C933F4789BE8CB82C60E7541DA87702B8414A2FF5AAF39B7543CBCD6887E1D178F3D58E345D9FC93B5D5F239D4D177BFB155F667237376873B472E593635D33481FAF296FEC27F1FF9C8B7815G29GD9G7937F23F0DF59D8867B77E1D00B90C5A94539C19AF6EE87E3E3E206D2ADAA213F73CA5B2F93F09D05E7A88650D1C8C9EE3FBEE8D64D193D4266D2410F665C1E5CBD0BB9A97BF6B41BD5FA4544582A4812482ECGA83A4DD4F870B6
	9E9FEB3A0E050B8F12F20C2EFA40B539DC976A034F76E4DDBF6E88774DD25D46AF4236B35C26E663D7E49FEF05BA9DC09CC0B2C08640C60012B8EE63FB463AF0B6567283D023CBFED5B1146509886D7CE358F6DF4685362339BB5D0B675B7E6F6CE4A5D0DD5D2C3AA0E0F35A2F9A79BE3DAD0E6F530F12EF25BAA847812C0C635FB19EB2C4E64FAF4236CB0DD3EBF765D4E4E3E1DEC83B8F034AD92A723E680CCC5E3110F641E3AFC7D55E6FC25ACD4F1F9B43365BF93BDA3B1FF6C5A6AFA5EC3B832A72220D1149
	3BB5EC3B86D5F9266E4864D942366B3FDDAD2F5CCB4E73A4280B81C8GD8833096A07F8ED3E1599DBC2F580C789DEEBA39331624115004C2755CE70A4C3E43E15BB55D21E65F6764FCF1906AA68196GA481BC833081E0671D5C3EC74267CD792A40F472E64219484661CFC25A850F7B83F72A351B6C0DCC5E7C10F66541F12AAA6FC8DFE4729AC35A855B572FAA6F3FC35ACDAFEE0A7A433513D465BD9D361D3A3C4C306D22D5654D9F084C1F31A16D027D39D4D5DE59D9EC370A5525855184C3561DFC7E3CF2AE
	4279AC24DD301E9BD5752C9B0CCC1EA124DDF0DC974787362377ED4A4919FF99549D852038076B91B7886571F879BEC4A3B927164BCED2466F97347CFAC3CF103C69C2DC7C54FD6137716D46C8E5845FFDD1831A979F6D567E7E1E522D7E7D7D09EE756F6F4FF4EF27F860213C55456BB33A277EBE1F552D76FD7E83E24F33645E26476714EC8E1108256BC9F19C6FC4C0766E74B9E9EAC7495E607D2040678B5BD96C475571FCDE38EC2A94C26705C60BCDF2F9B89323E876F40C0C4813C217DB9C66BADFBC2D6D
	3F71536A7E7B6E343A7FF4272776DF74E9AD7FD19FBD360BFC0787162217CC5664G10563DA86159EE48DD0613B063897C4C717613DB98CD23642ABFAC4BFD2CC75B46349EF59B1F6D416FE3747CB2580E67FAF64BF57E8936D9929D288E3B63C15ACBE64F2898620717C9FBDA27557E759EADDFD1DBFBBA382D8649E52810F95774EA5BFA22D75D5676DEEDDB0D3D61ED9D68553655529B5ED6C7AFFE239B32D9EC9EF648C8ED7C44203431D24EAB990D4139B3C31113B62864A7FA9616C476DC79F7513330DC99
	7F7784DF2C4053B3323A937C2EF589544D380B1F672D8F506B105CA78F1CD246DF250C9F695C2A40D79C64780F3B14FC3E1469FF9F00BFAC63FFE1D4623F127157F5AB6436D74A3EFF28CFBB96D6752947424F7B34E361313E703170F41FF6AC7C3AAFFCAC7CFB5F54F1FF16DC66A7BE9871389C9079345D6181EDDB0F8E285BDAB720EDEB73C0F8DBBB8734EDBDB3905E56218175387F6D99E5FF7A7B7F41DEE5FC79634E5927447B6368F03F12CF050C5FB2906843CA06FFF4C0498769697A62DBF2A61F85711F
	FD97DFF3ACA5B8E1B13CAF6638D7892E944A879773317240B9A5CFFF4CCE8CAA71C7E4FCE39E5EB5331BA7DCBEC99092113F501F473F6156C03987E08640024574DBAE1DC7FFBC06B60D408E884205CCF0D1CB306D93753CEDB65097G2E8C2089403A046B38CF426F9D9AC7B82CBF9DE737A963124DBB546EBF10731DB4601739047B627747791AACFB89DFF37D6404A067301FA95E1BCEC43ACE1A8ED099669FD4867523092CCDA064ABCA1762B02DC597313AC7F85DA15DD2744FD324ECD94A7B604DF32C3E12
	561710F24AD27E8DC81747F371DA4015AE637D7EC2B9760FFCCF265CBCA6D5D75739700EDC9B8DA2FA86B7DED7405A77AC63E7F04B6B10E78C1493GD6AF63F7C8467F0F5FA1C9DC3ED8C8FBDE7D2ECC26712F7A74E5F42E49D92638177059D09E1519DD4FE5D6ADB7953A17137BAA8DBB05C03FBD19C7577E345E98D00F39F0F32E32FECEC0BD6ABBBBDFD91F9CD00FBE42F311F201FF67F8FCAF37AD6771A4417E10CE73BFC8494B593DAA76ACFDE2D1EED42C61CD5ADFDB0FE37FB8DC96F14F5DA83774B87F8F
	51CEA8CF92DC410952A9E246FAB765CB75FA32CF797D2578D63BBED7C32FB52CDD5A5DFE1FD31DDEBC7B8E0B3FF2483AA745B172F534ECBF17F1CCD44AB8F825CD739BCFFE823B13471E517F33084928BC271451E13748B258BEB9647B5D7446E7EC046D7EDA77FD5B51B54FF8BD46FEC3C270DAE8A6419B920257C209F5FC7C75C35D6A847E3D7058A86657BD77188AEBGFAG5CGB1G097770BDCA14087FCD1B726C4A68F2374164EDD7EC9B58B9FDFDE47F3DFAF4B30F7315BFDDA1A44FE9503C87367A9E5A
	47196C191C3AA8971F543EB4AB5AD73C42D4F8G40GE0851884D830025B17A6326FFA386D02DD16F958CAEE3A491BB81BB866C76364C4E369656328CF052CF37C18B2BE2B36DE23FAAF9AA1E18555F5550A60785CFB82674FFA913F3FF23EE3A277C5493DCB04120FEA745BB578E6B3FE25777A47165F97D74FA3B7767F28913CEBEFB21FD75DF136C0AA26DF760D66D1393D6D82668C633D5CEF3DA31FE871GA4FE6322B788F82CFFD7AF48F1BFD7717EC05D8FBFFFBE7E034A207EB8BE2A3D06ED9FD5DF43F6
	0D2A6F454F0CCEFDD6E19E557EAF01F5D479DF825AF713F61CD72C50F7BA008400D5GE9F2DF3EBE7A899D83AA715FE89945B5B47E93CE12778256A97EB7E18D7F7F39F5D65CD38D39A44965C07E9C7F2A957BCBF23989127DEF420ABE7258C62D92A5E578BFD9F14E320BA384CD6DDC64C05CFA302BA0115BF62D6D73A962145F8921D8B29DB01F25BACB61D986FC9AG2CGB3G96GA4822C86C817E5BCE5AB1BC286DD50909FFA4CB67EDF8747859C6BDEB6C658990CDD5B2F575A437BF5011D5C5585FF98A7
	5CEC1F32480EF2DCA3A3DED16E3FBB6C45B22E59EBF0F9ED3801E1F50E371431C7DD426F82BBB00E64BBCD641AB09D65347ED7CEED7DFF638C2F7F0BCE7538F859999A972FB95563E223D399976C7FA60C2FC7BEF4E27FB7F1617A096ED77C77BC697F54D79D46332B441705CD1230CB6BBA953FFFF2D5197BB74E9E564C7D297731796A3E607BA7B9C7D06F127BCD05D5777338EABB72972D3802115965B05B6D1259FE2156E9411CDDB7A1097E9C11DB057C3D1C06D5327F88D5215E6795ED7E9FD0CB87884210
	F4EC6894GG78BFGGD0CB818294G94G88G88G5A0171B44210F4EC6894GG78BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA295GGGG
**end of data**/
}
}
