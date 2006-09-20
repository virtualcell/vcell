package cbit.vcell.client.data;
import cbit.render.*;
import cbit.vcell.math.DataIdentifier;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.*;
import swingthreads.*;
import javax.swing.*;
import cbit.plot.*;
import cbit.vcell.server.*;
import cbit.vcell.simdata.gui.*;

import java.awt.*;
import java.util.*;
import cbit.vcell.client.*;
import cbit.vcell.desktop.controls.ClientPDEDataContext;
import cbit.vcell.export.gui.ExportMonitorPanel;
import cbit.util.*;
import cbit.vcell.geometry.Coordinate;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 6:03:07 AM)
 * @author: Ion Moraru
 */
public class PDEDataViewer extends DataViewer {
	//
	private JInternalFrame dataValueSurfaceViewerJIF = null;
	private cbit.vcell.geometry.gui.DataValueSurfaceViewer fieldDataValueSurfaceViewer = null;
	private MeshDisplayAdapter.MeshRegionSurfaces meshRegionSurfaces = null;
	private static final String   SHOW_MEMB_SURFACE_BUTTON_STRING = "Show Membrane Surfaces";
	private static final String UPDATE_MEMB_SURFACE_BUTTON_STRING = "Update Membrane Surfaces";
	//
	private static final int RESAMPLE_LIMIT = 500;
	private static final int RESAMPLE_START_INDEX = 0;
	private static final int RESAMPLE_STEP_INDEX = 1;
	private static final int RESAMPLE_END_INDEX = 2;
	private static final int RESAMPLE_NUMTP_INDEX = 3;
	//
	private cbit.vcell.simdata.PDEDataContext fieldPdeDataContext = null;
	private JButton ivjJButtonSpatial = null;
	private JButton ivjJButtonTime = null;
	private PDEDataContextPanel ivjPDEDataContextPanel1 = null;
	private PDEPlotControlPanel ivjPDEPlotControlPanel1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JPanel ivjExportData = null;
	private JPanel ivjJPanelButtons = null;
	private JTabbedPane ivjJTabbedPane1 = null;
	private JPanel ivjViewData = null;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private boolean ivjConnPtoP6Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private boolean ivjConnPtoP8Aligning = false;
	private NewPDEExportPanel ivjPDEExportPanel1 = null;
	private ExportMonitorPanel ivjExportMonitorPanel1 = null;
	private JButton ivjKymographJButton = null;
	private JPanel ivjJDialogContentPane = null;
	private JLabel ivjJLabel1 = null;
	private JPanel ivjJPanel1 = null;
	private JLabel ivjStartTimeJLabel = null;
	private JLabel ivjStepJLabel = null;
	private JButton ivjCancelJButton = null;
	private JDialog ivjResampleTimesJDialog = null;
	private JLabel ivjResampleResultsJLabel = null;
	private JLabel ivjResampleInfoJLabel = null;
	private JTextField ivjResampleStartTimeJTextField = null;
	private JTextField ivjResampleStepJTextField = null;
	private JLabel ivjResampleLimitJLabel = null;
	private Boolean ivjResampleCancelBoolean = null;
	private JButton ivjDoneJButton = null;
	private JLabel ivjEndTimeJLabel = null;
	private JButton ivjRefreshJButton = null;
	private boolean ivjConnPtoP9Aligning = false;
	private JLabel ivjResampleEndTimeJLabel = null;
	private JButton ivjJButtonSurfaces = null;
	private boolean ivjConnPtoP10Aligning = false;
	private PDEDataContext ivjpdeDataContext1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PDEDataViewer.this.getJButtonSpatial()) 
				connEtoC2(e);
			if (e.getSource() == PDEDataViewer.this.getJButtonTime()) 
				connEtoC3(e);
			if (e.getSource() == PDEDataViewer.this.getKymographJButton()) 
				connEtoC4(e);
			if (e.getSource() == PDEDataViewer.this.getCancelJButton()) 
				connEtoM1(e);
			if (e.getSource() == PDEDataViewer.this.getDoneJButton()) 
				connEtoM3(e);
			if (e.getSource() == PDEDataViewer.this.getRefreshJButton()) 
				connEtoC5(e);
			if (e.getSource() == PDEDataViewer.this.getJButtonSurfaces()) 
				connEtoC6(e);
			if (e.getSource() == PDEDataViewer.this.getJButtonStatistics()) 
				connEtoC9(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == PDEDataViewer.this && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP1SetSource();
			if (evt.getSource() == PDEDataViewer.this && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEPlotControlPanel1() && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP2SetSource();
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("displayAdapterService1"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1()) 
				connEtoC1(evt);
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("spatialSelection"))) 
				connPtoP8SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("slice"))) 
				connPtoP7SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEExportPanel1() && (evt.getPropertyName().equals("slice"))) 
				connPtoP7SetSource();
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEExportPanel1() && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP4SetSource();
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("normalAxis"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEExportPanel1() && (evt.getPropertyName().equals("normalAxis"))) 
				connPtoP6SetSource();
			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("displayAdapterService1"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == PDEDataViewer.this && (evt.getPropertyName().equals("dataViewerManager"))) 
				connPtoP9SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getPDEExportPanel1() && (evt.getPropertyName().equals("dataViewerManager"))) 
				connPtoP9SetSource();
			if (evt.getSource() == PDEDataViewer.this && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP10SetTarget();
			if (evt.getSource() == PDEDataViewer.this.getpdeDataContext1() && (evt.getPropertyName().equals("variableName"))) 
				connEtoC7(evt);
		};
	};
	private JButton ivjJButtonStatistics = null;

public PDEDataViewer() {
	super();
	initialize();
}

/**
 * Comment
 */
private void calcStatistics(final java.awt.event.ActionEvent actionEvent) {

	new Thread(
		new Runnable(){
			public void run(){
				//AsynchProgressPopup pp =
					//new AsynchProgressPopup(PDEDataViewer.this, "Fetching data...", "Retrieving Statistics variable '" + getPdeDataContext().getVariableName(), false, false);
				//try{
					//pp.start();
					calcStatistics2();
				//}finally{
					//pp.stop();
				//}

			}
		}
	).start();	
	
}


/**
 * Comment
 */
private void calcStatistics2() {

	int[] dataIndexes = null;
	String variableName = null;
	
	if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME_REGION)){

		if(
			PopupGenerator.showComponentOKCancelDialog(this,new JLabel("Confirm -- Values of 1.0 for var '"+getPdeDataContext().getVariableName()+"' will define volume ROI for Statistics"),"Volume ROI")
			== JOptionPane.CANCEL_OPTION){
				return;
		}
		//get Volume variable names
		Vector volVarnamesV = new Vector();
		DataIdentifier[] dids = getPdeDataContext().getDataIdentifiers();
		for(int i=0;i<dids.length;i+= 1){
			if(	dids[i].getVariableType().equals(VariableType.VOLUME) ||
				dids[i].getVariableType().equals(VariableType.VOLUME_REGION)){
				if(!dids[i].equals(getPdeDataContext().getDataIdentifier())){
					volVarnamesV.add(dids[i].getName());
				}
			}
		}

		if(volVarnamesV.size() > 0){
			String[] volVarnamesArr = new String[volVarnamesV.size()];
			volVarnamesV.copyInto(volVarnamesArr);
			variableName = (String)PopupGenerator.showListDialog(this,volVarnamesArr,"Chose Variable for which Statistics will be calculated");
			if(variableName == null){return;}
			
			int vaoiCount=0;
			double[] data = getPdeDataContext().getDataValues();
			for(int i=0;i<data.length;i+= 1){
				if(data[i] == 1.0){
					vaoiCount+= 1;
				}
			}
			if(vaoiCount != 0){
				int[] vaoiIndexes = new int[vaoiCount];
				vaoiCount = 0;
				for(int i=0;i<data.length;i+= 1){
					if(data[i] == 1.0){
						vaoiIndexes[vaoiCount] = i;
						vaoiCount+= 1;
					}
				}

				dataIndexes = vaoiIndexes;
			}else{
				PopupGenerator.showErrorDialog("No values of 1.0 found in ROI variable "+getPdeDataContext().getVariableName());
				return;
			}
		}else{
			PopupGenerator.showErrorDialog("No other volume variables found");
			return;
		}

	}else if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE_REGION)){
			if(getPdeDataContext().getCartesianMesh().getGeometryDimension() < 3){
				variableName = getPdeDataContext().getVariableName();
				SpatialSelection[] sl = getPDEDataContextPanel1().fetchSpatialSelections(getPDEDataContextPanel1().isSpatialSampling2D(),false,true);
				if(sl == null || sl.length == 0){
					PopupGenerator.showErrorDialog("Spatial Selections (line or points) are required to calculate Statistics on a membrane");
					return;
				}
				
				int maoiCount = 0;
				for(int i=0;i<sl.length;i+= 1){
					if(sl[i] instanceof SpatialSelectionMembrane){
						maoiCount+= ((SpatialSelectionMembrane)sl[i]).getIndexSamples().getSampledIndexes().length;
					}
				}
				if(maoiCount != 0){
					int[] maoiIndexes = new int[maoiCount];
					maoiCount = 0;
					for(int i=0;i<sl.length;i+= 1){
						if(sl[i] instanceof SpatialSelectionMembrane){
							int[] indexes = ((SpatialSelectionMembrane)sl[i]).getIndexSamples().getSampledIndexes();
							for(int j=0;j<indexes.length;j+= 1){
								maoiIndexes[maoiCount] = indexes[j];
								maoiCount+= 1;
							}
						}
					}

					dataIndexes = maoiIndexes;
				}
			}else{
				PopupGenerator.showInfoDialog("Use the Membrane Surface Viewer to calculate statistics for 3D membranes");
				return;
			}
	}

	if(dataIndexes != null){
		cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
			new cbit.util.TimeSeriesJobSpec(
				new String[] {variableName},
				new int[][]{dataIndexes},
				getPdeDataContext().getTimePoints()[0],1,getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1],
				true,false);

			plotStatistics(timeSeriesJobSpec);
	}else{
		PopupGenerator.showErrorDialog("Error: Couldn't find indexes to calulate statistics on");
	}
	
	
}


/**
 * connEtoC1:  (PDEDataContextPanel1.propertyChange.propertyChange(java.beans.PropertyChangeEvent) --> PDEDataViewer.updateDataSamplerContext(Ljava.beans.PropertyChangeEvent;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateDataSamplerContext(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonSpatial.action.actionPerformed(java.awt.event.ActionEvent) --> PDEDataViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showSpatialPlot();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (JButtonTime.action.actionPerformed(java.awt.event.ActionEvent) --> PDEDataViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showTimePlot();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (KymographJButton.action.actionPerformed(java.awt.event.ActionEvent) --> PDEDataViewer.showKymograph()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showKymograph();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (RefreshJButton.action.actionPerformed(java.awt.event.ActionEvent) --> PDEDataViewer.resampleRefresh()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.resampleRefresh();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JButtonSurfaces.action.actionPerformed(java.awt.event.ActionEvent) --> PDEDataViewer.showMembraneSurfaces(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showMembraneSurfaces(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (pdeDataContext1.variableName --> PDEDataViewer.pdeDataContext1_VariableName(Ljava.lang.String;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.pdeDataContext1_VariableName();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC8:  (pdeDataContext1.this --> PDEDataViewer.pdeDataContext1_VariableName()V)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		this.pdeDataContext1_VariableName();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (JButtonStatistics.action.actionPerformed(java.awt.event.ActionEvent) --> PDEDataViewer.calcStatistics(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calcStatistics(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ResampleCancelBoolean.Boolean(boolean))
 * @return java.lang.Boolean
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.Boolean connEtoM1(java.awt.event.ActionEvent arg1) {
	Boolean connEtoM1Result = null;
	try {
		// user code begin {1}
		// user code end
		connEtoM1Result = new Boolean(true);
		setResampleCancelBoolean(connEtoM1Result);
		connEtoM2(connEtoM1Result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoM1Result;
}

/**
 * connEtoM2:  ( (CancelJButton,action.actionPerformed(java.awt.event.ActionEvent) --> ResampleCancelBoolean,this).normalResult --> ResampleTimesJDialog.dispose()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.lang.Boolean result) {
	try {
		// user code begin {1}
		// user code end
		getResampleTimesJDialog().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (OKJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ResampleCancelBoolean.Boolean(boolean))
 * @return java.lang.Boolean
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.Boolean connEtoM3(java.awt.event.ActionEvent arg1) {
	Boolean connEtoM3Result = null;
	try {
		// user code begin {1}
		// user code end
		connEtoM3Result = new Boolean(false);
		setResampleCancelBoolean(connEtoM3Result);
		connEtoM4(connEtoM3Result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoM3Result;
}

/**
 * connEtoM4:  ( (OKJButton,action.actionPerformed(java.awt.event.ActionEvent) --> ResampleCancelBoolean,Boolean(boolean)).normalResult --> ResampleTimesJDialog.dispose()V)
 * @param result java.lang.Boolean
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.lang.Boolean result) {
	try {
		// user code begin {1}
		// user code end
		getResampleTimesJDialog().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP10SetSource:  (PDEDataViewer.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP10SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP10Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP10Aligning = true;
			if ((getpdeDataContext1() != null)) {
				this.setPdeDataContext(getpdeDataContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP10Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP10Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP10SetTarget:  (PDEDataViewer.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP10SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP10Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP10Aligning = true;
			setpdeDataContext1(this.getPdeDataContext());
			// user code begin {2}
			// user code end
			ivjConnPtoP10Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP10Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (PDEDataViewer.pdeDataContext <--> PDEDataContextPanel1.pdeDataContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setPdeDataContext(getPDEDataContextPanel1().getPdeDataContext());
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
 * connPtoP1SetTarget:  (PDEDataViewer.pdeDataContext <--> PDEDataContextPanel1.pdeDataContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			getPDEDataContextPanel1().setPdeDataContext(this.getPdeDataContext());
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
 * connPtoP2SetSource:  (PDEDataViewer.pdeDataContext <--> PDEPlotControlPanel1.pdeDataContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setPdeDataContext(getPDEPlotControlPanel1().getPdeDataContext());
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
 * connPtoP2SetTarget:  (PDEDataViewer.pdeDataContext <--> PDEPlotControlPanel1.pdeDataContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			getPDEPlotControlPanel1().setPdeDataContext(this.getPdeDataContext());
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
 * connPtoP3SetTarget:  (PDEDataContextPanel1.displayAdapterService1 <--> PDEPlotControlPanel1.displayAdapterService)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			getPDEPlotControlPanel1().setDisplayAdapterService(getPDEDataContextPanel1().getdisplayAdapterService1());
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
 * connPtoP4SetSource:  (PDEDataContextPanel1.pdeDataContext <--> PDEExportPanel1.pdeDataContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			getPDEDataContextPanel1().setPdeDataContext(getPDEExportPanel1().getPdeDataContext());
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
 * connPtoP4SetTarget:  (PDEDataContextPanel1.pdeDataContext <--> PDEExportPanel1.pdeDataContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			getPDEExportPanel1().setPdeDataContext(getPDEDataContextPanel1().getPdeDataContext());
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
 * connPtoP5SetTarget:  (PDEDataContextPanel1.displayAdapterService1 <--> PDEExportPanel1.displayAdapterService)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			getPDEExportPanel1().setDisplayAdapterService(getPDEDataContextPanel1().getdisplayAdapterService1());
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
 * connPtoP6SetSource:  (PDEDataContextPanel1.normalAxis <--> PDEExportPanel1.normalAxis)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			getPDEDataContextPanel1().setNormalAxis(getPDEExportPanel1().getNormalAxis());
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetTarget:  (PDEDataContextPanel1.normalAxis <--> PDEExportPanel1.normalAxis)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			getPDEExportPanel1().setNormalAxis(getPDEDataContextPanel1().getNormalAxis());
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP7SetSource:  (PDEDataContextPanel1.slice <--> PDEExportPanel1.slice)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			getPDEDataContextPanel1().setSlice(getPDEExportPanel1().getSlice());
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP7SetTarget:  (PDEDataContextPanel1.slice <--> PDEExportPanel1.slice)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			getPDEExportPanel1().setSlice(getPDEDataContextPanel1().getSlice());
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP8SetTarget:  (PDEDataContextPanel1.spatialSelection <--> PDEExportPanel1.selectedRegion)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			getPDEExportPanel1().setSelectedRegion(getPDEDataContextPanel1().getSpatialSelection());
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP9SetSource:  (PDEDataViewer.dataViewerManager <--> PDEExportPanel1.dataViewerManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			this.setDataViewerManager(getPDEExportPanel1().getDataViewerManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP9SetTarget:  (PDEDataViewer.dataViewerManager <--> PDEExportPanel1.dataViewerManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			getPDEExportPanel1().setDataViewerManager(this.getDataViewerManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the CancelJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 1:53:00 PM)
 */
private cbit.vcell.geometry.gui.DataValueSurfaceViewer getDataValueSurfaceViewer() {

	try{
	if(fieldDataValueSurfaceViewer == null){
		//Surfaces
		meshRegionSurfaces =
			new MeshDisplayAdapter(getPdeDataContext().getCartesianMesh()).generateMeshRegionSurfaces();
		SurfaceCollection surfaceCollection = meshRegionSurfaces.getSurfaceCollection();

		//SurfaceNames
		final String[] surfaceNames = new String[meshRegionSurfaces.getSurfaceCollection().getSurfaceCount()];
		for (int i = 0; i < meshRegionSurfaces.getSurfaceCollection().getSurfaceCount(); i++){
			cbit.vcell.mesh.MembraneElement me = //Get the first element, any will do, all have same inside/outside volumeIndex
				getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(i,0)];
			surfaceNames[i] =
				getSimulationModelInfo().getMembraneName(
					getPdeDataContext().getCartesianMesh().getSubVolumeFromVolumeIndex(me.getInsideVolumeIndex()),
					getPdeDataContext().getCartesianMesh().getSubVolumeFromVolumeIndex(me.getOutsideVolumeIndex())
				);		
		}

		//SurfaceAreas
		final Double[] surfaceAreas = new Double[meshRegionSurfaces.getSurfaceCollection().getSurfaceCount()];
		for (int i = 0; i < meshRegionSurfaces.getSurfaceCollection().getSurfaceCount(); i++){
			surfaceAreas[i] = new Double(getPdeDataContext().getCartesianMesh().getRegionMembraneSurfaceAreaFromMembraneIndex(meshRegionSurfaces.getMembraneIndexForPolygon(i,0)));
		}

		cbit.vcell.geometry.gui.DataValueSurfaceViewer fieldDataValueSurfaceViewer0 = new cbit.vcell.geometry.gui.DataValueSurfaceViewer();

		cbit.render.TaubinSmoothing taubinSmoothing = new cbit.render.TaubinSmoothingWrong();
		cbit.render.TaubinSmoothingSpecification taubinSpec = cbit.render.TaubinSmoothingSpecification.getInstance(.3);
		taubinSmoothing.smooth(surfaceCollection,taubinSpec);
		fieldDataValueSurfaceViewer0.init(
			meshRegionSurfaces.getSurfaceCollection(),
			getPdeDataContext().getCartesianMesh().getOrigin(),
			getPdeDataContext().getCartesianMesh().getExtent(),
			surfaceNames,
			surfaceAreas,
			getPdeDataContext().getCartesianMesh().getGeometryDimension()
		);
		
		dataValueSurfaceViewerJIF = new cbit.gui.JInternalFrameEnhanced("DataValueSurfaceViewer",true,true,true,true);
		dataValueSurfaceViewerJIF.setContentPane(fieldDataValueSurfaceViewer0);
		//dataValueSurfaceViewerJIF.pack();
		dataValueSurfaceViewerJIF.setSize(800,800);
		dataValueSurfaceViewerJIF.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
				getJButtonSurfaces().setText(SHOW_MEMB_SURFACE_BUTTON_STRING);
			};
		});		

		fieldDataValueSurfaceViewer = fieldDataValueSurfaceViewer0;
	}
	}catch(Exception e){
		PopupGenerator.showErrorDialog(e.getClass().getName()+"\n"+e.getMessage());
	}

	return fieldDataValueSurfaceViewer;
}


/**
 * Return the OKJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDoneJButton() {
	if (ivjDoneJButton == null) {
		try {
			ivjDoneJButton = new javax.swing.JButton();
			ivjDoneJButton.setName("DoneJButton");
			ivjDoneJButton.setText("Done");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDoneJButton;
}

/**
 * Return the EndTimeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndTimeJLabel() {
	if (ivjEndTimeJLabel == null) {
		try {
			ivjEndTimeJLabel = new javax.swing.JLabel();
			ivjEndTimeJLabel.setName("EndTimeJLabel");
			ivjEndTimeJLabel.setText("End Time");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndTimeJLabel;
}


/**
 * Return the ExportData property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getExportData() {
	if (ivjExportData == null) {
		try {
			ivjExportData = new javax.swing.JPanel();
			ivjExportData.setName("ExportData");
			ivjExportData.setLayout(new java.awt.BorderLayout());
			getExportData().add(getPDEExportPanel1(), "Center");
			getExportData().add(getExportMonitorPanel1(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportData;
}

/**
 * Method generated to support the promotion of the exportMonitorPanel attribute.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
public ExportMonitorPanel getExportMonitorPanel() {
	return getExportMonitorPanel1();
}


/**
 * Return the ExportMonitorPanel1 property value.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ExportMonitorPanel getExportMonitorPanel1() {
	if (ivjExportMonitorPanel1 == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.LineBorderBean();
			ivjLocalBorder1.setLineColor(java.awt.Color.blue);
			cbit.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.TitledBorderBean();
			ivjLocalBorder.setBorder(ivjLocalBorder1);
			ivjLocalBorder.setTitle("Export jobs");
			ivjExportMonitorPanel1 = new ExportMonitorPanel();
			ivjExportMonitorPanel1.setName("ExportMonitorPanel1");
			ivjExportMonitorPanel1.setPreferredSize(new java.awt.Dimension(453, 150));
			ivjExportMonitorPanel1.setBorder(ivjLocalBorder);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportMonitorPanel1;
}

/**
 * Return the JButtonSpatial property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSpatial() {
	if (ivjJButtonSpatial == null) {
		try {
			ivjJButtonSpatial = new javax.swing.JButton();
			ivjJButtonSpatial.setName("JButtonSpatial");
			ivjJButtonSpatial.setText("Show Spatial Plot");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSpatial;
}


/**
 * Return the JButtonStatistics property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonStatistics() {
	if (ivjJButtonStatistics == null) {
		try {
			ivjJButtonStatistics = new javax.swing.JButton();
			ivjJButtonStatistics.setName("JButtonStatistics");
			ivjJButtonStatistics.setText("Statistics");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonStatistics;
}


/**
 * Return the JButtonSurfaces property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSurfaces() {
	if (ivjJButtonSurfaces == null) {
		try {
			ivjJButtonSurfaces = new javax.swing.JButton();
			ivjJButtonSurfaces.setName("JButtonSurfaces");
			ivjJButtonSurfaces.setText("Show Membrane Surfaces");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSurfaces;
}


/**
 * Return the JButtonTime property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonTime() {
	if (ivjJButtonTime == null) {
		try {
			ivjJButtonTime = new javax.swing.JButton();
			ivjJButtonTime.setName("JButtonTime");
			ivjJButtonTime.setText("Show Time Plot");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonTime;
}


/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsResampleStartTimeJTextField = new java.awt.GridBagConstraints();
			constraintsResampleStartTimeJTextField.gridx = 0; constraintsResampleStartTimeJTextField.gridy = 4;
			constraintsResampleStartTimeJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsResampleStartTimeJTextField.weightx = 1.0;
			constraintsResampleStartTimeJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getResampleStartTimeJTextField(), constraintsResampleStartTimeJTextField);

			java.awt.GridBagConstraints constraintsResampleStepJTextField = new java.awt.GridBagConstraints();
			constraintsResampleStepJTextField.gridx = 1; constraintsResampleStepJTextField.gridy = 4;
			constraintsResampleStepJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsResampleStepJTextField.weightx = 1.0;
			constraintsResampleStepJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getResampleStepJTextField(), constraintsResampleStepJTextField);

			java.awt.GridBagConstraints constraintsResampleInfoJLabel = new java.awt.GridBagConstraints();
			constraintsResampleInfoJLabel.gridx = 0; constraintsResampleInfoJLabel.gridy = 0;
			constraintsResampleInfoJLabel.gridwidth = 3;
			constraintsResampleInfoJLabel.weightx = 1.0;
			constraintsResampleInfoJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getResampleInfoJLabel(), constraintsResampleInfoJLabel);

			java.awt.GridBagConstraints constraintsStartTimeJLabel = new java.awt.GridBagConstraints();
			constraintsStartTimeJLabel.gridx = 0; constraintsStartTimeJLabel.gridy = 3;
			constraintsStartTimeJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getStartTimeJLabel(), constraintsStartTimeJLabel);

			java.awt.GridBagConstraints constraintsStepJLabel = new java.awt.GridBagConstraints();
			constraintsStepJLabel.gridx = 1; constraintsStepJLabel.gridy = 3;
			constraintsStepJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getStepJLabel(), constraintsStepJLabel);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 1;
			constraintsJLabel1.gridwidth = 3;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsResampleLimitJLabel = new java.awt.GridBagConstraints();
			constraintsResampleLimitJLabel.gridx = 0; constraintsResampleLimitJLabel.gridy = 2;
			constraintsResampleLimitJLabel.gridwidth = 3;
			constraintsResampleLimitJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getResampleLimitJLabel(), constraintsResampleLimitJLabel);

			java.awt.GridBagConstraints constraintsResampleResultsJLabel = new java.awt.GridBagConstraints();
			constraintsResampleResultsJLabel.gridx = 0; constraintsResampleResultsJLabel.gridy = 5;
			constraintsResampleResultsJLabel.gridwidth = 3;
			constraintsResampleResultsJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsResampleResultsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getResampleResultsJLabel(), constraintsResampleResultsJLabel);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 6;
			constraintsJPanel1.gridwidth = 3;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsEndTimeJLabel = new java.awt.GridBagConstraints();
			constraintsEndTimeJLabel.gridx = 2; constraintsEndTimeJLabel.gridy = 3;
			constraintsEndTimeJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getEndTimeJLabel(), constraintsEndTimeJLabel);

			java.awt.GridBagConstraints constraintsResampleEndTimeJLabel = new java.awt.GridBagConstraints();
			constraintsResampleEndTimeJLabel.gridx = 2; constraintsResampleEndTimeJLabel.gridy = 4;
			constraintsResampleEndTimeJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsResampleEndTimeJLabel.weightx = 1.0;
			constraintsResampleEndTimeJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getResampleEndTimeJLabel(), constraintsResampleEndTimeJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Type a Start Time and/or Step");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDoneJButton = new java.awt.GridBagConstraints();
			constraintsDoneJButton.gridx = 0; constraintsDoneJButton.gridy = 0;
			constraintsDoneJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getDoneJButton(), constraintsDoneJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 2; constraintsCancelJButton.gridy = 0;
			constraintsCancelJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getCancelJButton(), constraintsCancelJButton);

			java.awt.GridBagConstraints constraintsRefreshJButton = new java.awt.GridBagConstraints();
			constraintsRefreshJButton.gridx = 1; constraintsRefreshJButton.gridy = 0;
			constraintsRefreshJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRefreshJButton(), constraintsRefreshJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelButtons() {
	if (ivjJPanelButtons == null) {
		try {
			ivjJPanelButtons = new javax.swing.JPanel();
			ivjJPanelButtons.setName("JPanelButtons");
			ivjJPanelButtons.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonSpatial = new java.awt.GridBagConstraints();
			constraintsJButtonSpatial.gridx = -1; constraintsJButtonSpatial.gridy = -1;
			constraintsJButtonSpatial.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelButtons().add(getJButtonSpatial(), constraintsJButtonSpatial);

			java.awt.GridBagConstraints constraintsJButtonTime = new java.awt.GridBagConstraints();
			constraintsJButtonTime.gridx = 1; constraintsJButtonTime.gridy = 0;
			constraintsJButtonTime.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelButtons().add(getJButtonTime(), constraintsJButtonTime);

			java.awt.GridBagConstraints constraintsKymographJButton = new java.awt.GridBagConstraints();
			constraintsKymographJButton.gridx = 2; constraintsKymographJButton.gridy = 0;
			constraintsKymographJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelButtons().add(getKymographJButton(), constraintsKymographJButton);

			java.awt.GridBagConstraints constraintsJButtonSurfaces = new java.awt.GridBagConstraints();
			constraintsJButtonSurfaces.gridx = 3; constraintsJButtonSurfaces.gridy = 0;
			constraintsJButtonSurfaces.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelButtons().add(getJButtonSurfaces(), constraintsJButtonSurfaces);

			java.awt.GridBagConstraints constraintsJButtonStatistics = new java.awt.GridBagConstraints();
			constraintsJButtonStatistics.gridx = 4; constraintsJButtonStatistics.gridy = 0;
			constraintsJButtonStatistics.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelButtons().add(getJButtonStatistics(), constraintsJButtonStatistics);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelButtons;
}

/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("View Data", null, getViewData(), null, 0);
			ivjJTabbedPane1.insertTab("Export Data", null, getExportData(), null, 1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}


/**
 * Return the KymographJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getKymographJButton() {
	if (ivjKymographJButton == null) {
		try {
			ivjKymographJButton = new javax.swing.JButton();
			ivjKymographJButton.setName("KymographJButton");
			ivjKymographJButton.setText("Show Kymograph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKymographJButton;
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
 * Return the PDEDataContextPanel1 property value.
 * @return cbit.vcell.simdata.gui.PDEDataContextPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.gui.PDEDataContextPanel getPDEDataContextPanel1() {
	if (ivjPDEDataContextPanel1 == null) {
		try {
			ivjPDEDataContextPanel1 = new cbit.vcell.simdata.gui.PDEDataContextPanel();
			ivjPDEDataContextPanel1.setName("PDEDataContextPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPDEDataContextPanel1;
}


/**
 * Return the PDEExportPanel1 property value.
 * @return cbit.vcell.client.data.NewPDEExportPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private NewPDEExportPanel getPDEExportPanel1() {
	if (ivjPDEExportPanel1 == null) {
		try {
			ivjPDEExportPanel1 = new cbit.vcell.client.data.NewPDEExportPanel();
			ivjPDEExportPanel1.setName("PDEExportPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPDEExportPanel1;
}


/**
 * Return the PDEPlotControlPanel1 property value.
 * @return cbit.vcell.simdata.gui.PDEPlotControlPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.gui.PDEPlotControlPanel getPDEPlotControlPanel1() {
	if (ivjPDEPlotControlPanel1 == null) {
		try {
			ivjPDEPlotControlPanel1 = new cbit.vcell.simdata.gui.PDEPlotControlPanel();
			ivjPDEPlotControlPanel1.setName("PDEPlotControlPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPDEPlotControlPanel1;
}


/**
 * Return the RefreshJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRefreshJButton() {
	if (ivjRefreshJButton == null) {
		try {
			ivjRefreshJButton = new javax.swing.JButton();
			ivjRefreshJButton.setName("RefreshJButton");
			ivjRefreshJButton.setText("Refresh");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshJButton;
}


/**
 * Return the CancelFactory property value.
 * @return java.lang.Boolean
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.Boolean getResampleCancelBoolean() {
	// user code begin {1}
	// user code end
	return ivjResampleCancelBoolean;
}

/**
 * Return the ResampleEndTimeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResampleEndTimeJLabel() {
	if (ivjResampleEndTimeJLabel == null) {
		try {
			ivjResampleEndTimeJLabel = new javax.swing.JLabel();
			ivjResampleEndTimeJLabel.setName("ResampleEndTimeJLabel");
			ivjResampleEndTimeJLabel.setBorder(new cbit.gui.LineBorderBean());
			ivjResampleEndTimeJLabel.setText("End Time");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResampleEndTimeJLabel;
}

/**
 * Return the InfoJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResampleInfoJLabel() {
	if (ivjResampleInfoJLabel == null) {
		try {
			ivjResampleInfoJLabel = new javax.swing.JLabel();
			ivjResampleInfoJLabel.setName("ResampleInfoJLabel");
			ivjResampleInfoJLabel.setText("There are XXX time points");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResampleInfoJLabel;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResampleLimitJLabel() {
	if (ivjResampleLimitJLabel == null) {
		try {
			ivjResampleLimitJLabel = new javax.swing.JLabel();
			ivjResampleLimitJLabel.setName("ResampleLimitJLabel");
			ivjResampleLimitJLabel.setText("Suggested Time Point limit is XXX");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResampleLimitJLabel;
}

/**
 * Return the ResultsJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResampleResultsJLabel() {
	if (ivjResampleResultsJLabel == null) {
		try {
			ivjResampleResultsJLabel = new javax.swing.JLabel();
			ivjResampleResultsJLabel.setName("ResampleResultsJLabel");
			ivjResampleResultsJLabel.setText("Current values produce XXX time points");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResampleResultsJLabel;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getResampleStartTimeJTextField() {
	if (ivjResampleStartTimeJTextField == null) {
		try {
			ivjResampleStartTimeJTextField = new javax.swing.JTextField();
			ivjResampleStartTimeJTextField.setName("ResampleStartTimeJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResampleStartTimeJTextField;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getResampleStepJTextField() {
	if (ivjResampleStepJTextField == null) {
		try {
			ivjResampleStepJTextField = new javax.swing.JTextField();
			ivjResampleStepJTextField.setName("ResampleStepJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResampleStepJTextField;
}

/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getResampleTimesJDialog() {
	if (ivjResampleTimesJDialog == null) {
		try {
			ivjResampleTimesJDialog = new javax.swing.JDialog();
			ivjResampleTimesJDialog.setName("ResampleTimesJDialog");
			ivjResampleTimesJDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjResampleTimesJDialog.setBounds(752, 487, 281, 205);
			ivjResampleTimesJDialog.setModal(true);
			getResampleTimesJDialog().setContentPane(getJDialogContentPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResampleTimesJDialog;
}

/**
 * Return the StartTimeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartTimeJLabel() {
	if (ivjStartTimeJLabel == null) {
		try {
			ivjStartTimeJLabel = new javax.swing.JLabel();
			ivjStartTimeJLabel.setName("StartTimeJLabel");
			ivjStartTimeJLabel.setText("Start Time");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeJLabel;
}


/**
 * Return the StepJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStepJLabel() {
	if (ivjStepJLabel == null) {
		try {
			ivjStepJLabel = new javax.swing.JLabel();
			ivjStepJLabel.setName("StepJLabel");
			ivjStepJLabel.setText("Step Count");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepJLabel;
}

/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 12:28:48 PM)
 * @return double[][]
 * @param indices int[]
 * @param startTime double
 * @param step int
 */
private double[][] getTimeSeries(int[] indices, double startTime, int step,double endTime) throws DataAccessException{
	
	cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
		new cbit.util.TimeSeriesJobSpec(
			new String[] {getPdeDataContext().getVariableName()},
			new int[][]{indices},
			startTime,step,endTime);
	cbit.util.TSJobResultsNoStats timeSeriesJobResults = (cbit.util.TSJobResultsNoStats)getPdeDataContext().getTimeSeriesValues(timeSeriesJobSpec);
	double[][] timeSeries = timeSeriesJobResults.getTimesAndValuesForVariable(getPdeDataContext().getVariableName());
	return timeSeries;
}


/**
 * Return the ViewData property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getViewData() {
	if (ivjViewData == null) {
		try {
			ivjViewData = new javax.swing.JPanel();
			ivjViewData.setName("ViewData");
			ivjViewData.setLayout(new java.awt.BorderLayout());
			getViewData().add(getPDEDataContextPanel1(), "Center");
			getViewData().add(getPDEPlotControlPanel1(), "West");
			getViewData().add(getJPanelButtons(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewData;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getPDEDataContextPanel1().addPropertyChangeListener(ivjEventHandler);
	getPDEPlotControlPanel1().addPropertyChangeListener(ivjEventHandler);
	getJButtonSpatial().addActionListener(ivjEventHandler);
	getJButtonTime().addActionListener(ivjEventHandler);
	getPDEExportPanel1().addPropertyChangeListener(ivjEventHandler);
	getKymographJButton().addActionListener(ivjEventHandler);
	getCancelJButton().addActionListener(ivjEventHandler);
	getDoneJButton().addActionListener(ivjEventHandler);
	getRefreshJButton().addActionListener(ivjEventHandler);
	getJButtonSurfaces().addActionListener(ivjEventHandler);
	getJButtonStatistics().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP8SetTarget();
	connPtoP7SetTarget();
	connPtoP4SetTarget();
	connPtoP6SetTarget();
	connPtoP5SetTarget();
	connPtoP9SetTarget();
	connPtoP10SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PDEDataViewer");
		setLayout(new java.awt.BorderLayout());
		setSize(725, 569);
		add(getJTabbedPane1(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PDEDataViewer aPDEDataViewer;
		aPDEDataViewer = new PDEDataViewer();
		frame.setContentPane(aPDEDataViewer);
		frame.setSize(aPDEDataViewer.getSize());
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
 * Insert the method's description here.
 * Creation date: (10/8/2004 7:09:31 AM)
 * @return java.lang.String
 * @param coord cbit.vcell.geometry.Coordinate
 */
private String niceCoordinateString(cbit.vcell.geometry.Coordinate coord) {

	//reduce fraction digits of the form XX.xxxxxxxxxxxxxxy
	//to something more reasonable

	final int MAX_CHARS = 3;
	String result = "";
	for(int i=0;i<3;i+= 1){
		double xyz = -1;
		if(i==0){xyz = coord.getX();}
		else if(i==1){xyz = coord.getY();}
		else if(i==2){xyz = coord.getZ();}
		
		result+= (i!=0?",":"")+(double)((int)(xyz * Math.pow(10,MAX_CHARS)))/Math.pow(10,MAX_CHARS);//NumberUtils.formatNumber(xyz,MAX_CHARS);
	}

	return result;
}


/**
 * Comment
 */
private void pdeDataContext1_VariableName() {

	if(getPdeDataContext() == null){
		return;
	}
	
	if(getPdeDataContext().getCartesianMesh().getGeometryDimension() < 3){
		if(getJButtonSurfaces().isVisible()){
			getJButtonSurfaces().setVisible(false);
			return;
		}
	}else{
		if(!getJButtonSurfaces().isVisible()){
			getJButtonSurfaces().setVisible(true);
		}
	}
	
	if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE_REGION)){

			getJButtonSurfaces().setEnabled(true);
	}else{
		getJButtonSurfaces().setEnabled(false);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2006 10:52:52 AM)
 */
private void plotStatistics(TimeSeriesJobSpec tsjs) {

	final int BATCH_THRESHOLD = 10000;
	final int BATCH_SIZE = 2500;
	cbit.util.TimeSeriesJobResults tsjr = null;
	try{
		AsynchProgressPopup pp =
			new AsynchProgressPopup(
				PDEDataViewer.this,
				"Fetching data...",
				"Retrieving Statistics variable '" + tsjs.getVariableNames()[0],
				false,
				(tsjs.getIndices()[0].length <= BATCH_THRESHOLD?false:true)
				);
		pp.start();
		try{
			if(tsjs.getIndices()[0].length <= BATCH_THRESHOLD){
				tsjr = (cbit.util.TSJobResultsSpaceStats)getPdeDataContext().getTimeSeriesValues(tsjs);
			}else{
				double totalSpace = 0;
				double[] times = null;
				double[] mins = null;
				double[] maxs = null;
				double[] means = null;;
				double[] wmeans = null;
				for(int i=0;i<tsjs.getIndices()[0].length;i+=BATCH_SIZE){
					pp.setProgress((int)(100*((float)i/(float)tsjs.getIndices()[0].length)));
					int[] indexes_batch = new int[Math.min(BATCH_SIZE,(tsjs.getIndices()[0].length-i))];
					System.arraycopy(tsjs.getIndices()[0],i,indexes_batch,0,indexes_batch.length);
					cbit.util.TimeSeriesJobSpec tsjs_batch =
						new cbit.util.TimeSeriesJobSpec(
							tsjs.getVariableNames(),
							new int[][]{indexes_batch},
							getPdeDataContext().getTimePoints()[0],1,getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1],
							true,false);
					cbit.util.TSJobResultsSpaceStats tsjrss = (cbit.util.TSJobResultsSpaceStats)getPdeDataContext().getTimeSeriesValues(tsjs_batch);
					if(i==0){
						times = tsjrss.getTimes();
						mins = tsjrss.getMinimums()[0];
						maxs = tsjrss.getMaximums()[0];
						means = tsjrss.getUnweightedMean()[0];
						wmeans = new double[tsjrss.getTimes().length];
						for(int j=0;j<tsjrss.getTimes().length;j+= 1){
							wmeans[j] = tsjrss.getWeightedMean()[0][j]*tsjrss.getTotalSpace()[0];
						}
					}else{
						for(int j=0;j<tsjrss.getTimes().length;j+= 1){
							mins[j] = Math.min(mins[j],tsjrss.getMinimums()[0][j]);
							maxs[j] = Math.max(maxs[j],tsjrss.getMaximums()[0][j]);
							means[j]+= tsjrss.getUnweightedMean()[0][j];
							wmeans[j]+= tsjrss.getWeightedMean()[0][j]*tsjrss.getTotalSpace()[0];
						}
					}
					totalSpace+= tsjrss.getTotalSpace()[0];
				}
				for(int i=0;i<means.length;i+= 1){
					means[i]/= tsjs.getIndices()[0].length;
					wmeans[i]/= totalSpace;
				}
				tsjr =
					new TSJobResultsSpaceStats(
						tsjs.getVariableNames(),
						tsjs.getIndices(),
						times,
						new double[][] {mins},
						new double[][] {maxs},
						new double[][] {means},
						new double[][] {wmeans},
						new double[] {totalSpace}
						);
			}
		}finally{
			pp.stop();
		}
		//plotStatistics(tsjrss);
	}catch(Exception e){
		PopupGenerator.showErrorDialog("Error generating stats\n"+e.getClass().getName()+"\n"+e.getMessage());
		return;
	}

	//cbit.util.TimeSeriesJobResults tsjr = getPdeDataContext().getTimeSeriesValues(timeSeriesJobSpec);
	//
	if(tsjr instanceof TSJobResultsSpaceStats){
		TSJobResultsSpaceStats tsjrss = (TSJobResultsSpaceStats)tsjr;
		//Determine if Volume or Membrane
		DataIdentifier[] diArr = getPdeDataContext().getDataIdentifiers();
		boolean bVolume = true;
		for(int i=0;i<diArr.length;i+= 1){
			if(diArr[i].getName().equals(tsjrss.getVariableNames()[0])){
				if(diArr[i].getVariableType().equals(VariableType.MEMBRANE) || diArr[i].getVariableType().equals(VariableType.MEMBRANE_REGION)){
					bVolume = false;
					break;
				}
			}
		}
		cbit.plot.PlotPane plotPane = new cbit.plot.PlotPane();
		plotPane.setPlot2D(
			new cbit.plot.SingleXPlot2D("Time",
			new String[] {"Max",(tsjrss.getWeightedMean() != null?"WeightedMean":"UnweightedMean"),"Min"},
			new double[][] {tsjrss.getTimes(),tsjrss.getMaximums()[0],(tsjrss.getWeightedMean() != null?tsjrss.getWeightedMean()[0]:tsjrss.getUnweightedMean()[0]),tsjrss.getMinimums()[0]},
			new String[] {
				"Statistics Plot for "+tsjrss.getVariableNames()[0]+(tsjrss.getTotalSpace() != null?" (ROI "+(bVolume?"volume":"area")+"="+tsjrss.getTotalSpace()[0]+")":""),
				"Time (s)",
				"[" + tsjrss.getVariableNames()[0] + "]"}));


		showComponentInFrame(plotPane,"Statistics");
		//JInternalFrame frame =
			//new JInternalFrame("Statistics", true, true, true, true);
		//frame.getContentPane().add(plotPane);
		//frame.pack();
		//cbit.util.BeanUtils.centerOnComponent(frame,this);
		//getDataViewerManager().showDataViewerPlotsFrames(new JInternalFrame[] {frame});

	}else{
		//if(tsjr instanceof TSJobResultsNoStats){
			//TSJobResultsNoStats tsjrns = (TSJobResultsNoStats)tsjr;
			//for(int i=0;i<tsjrns.getVariableNames().length;i+= 1){
				//System.out.println(tsjrns.getVariableNames()[i]);
				//double[][] timesAndVals = tsjrns.getTimesAndValuesForVariable(tsjrns.getVariableNames()[i]);
				//for(int j=0;j<timesAndVals.length;j+= 1){
					//System.out.println(timesAndVals[0][j]+","+timesAndVals[1][j]);
				//}
			//}
		//}else if(tsjr instanceof TSJobResultsTimeStats){
			//TSJobResultsTimeStats tsjrts = (TSJobResultsTimeStats)tsjr;
			//if(tsjrts.isValuesAreSpaceStats()){
				//for(int i=0;i<tsjrts.getVariableNames().length;i+= 1){
					////for(int j=0;j<tsjrts.getWeightedMeans()[i].length;j+= 1){
						//System.out.println(tsjrts.getVariableNames()[i]+" "+tsjrts.getWeightedMeans()[i][0]);
					////}
				//}
			//}else{
				//for(int i=0;i<tsjrts.getVariableNames().length;i+= 1){
					//for(int j=0;j<tsjrts.getWeightedMeans()[i].length;j+= 1){
						//System.out.println(tsjrts.getIndices()[i][j]+","+tsjrts.getUnweightedMeans()[i][j]);
					//}
				//}
			//}
		//}
		PopupGenerator.showInfoDialog("Sorry, Display of this datatype has yet to be Implemented!");
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 11:46:00 AM)
 */
private double[] resample() {

	if(getPdeDataContext().getTimePoints().length <= RESAMPLE_LIMIT){
		return new double[]{
				getPdeDataContext().getTimePoints()[0],
				1,
				getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1],
				getPdeDataContext().getTimePoints().length
			};
	}
	
	int step = (int)Math.ceil((double)getPdeDataContext().getTimePoints().length/(double)RESAMPLE_LIMIT);
	double startTime = getPdeDataContext().getTimePoints()[0];
	double endTime = getPdeDataContext().getTimePoints()[((getPdeDataContext().getTimePoints().length-1)/step)*step];
	int numTimePoints = (getPdeDataContext().getTimePoints().length/step) + 1;
	getResampleStartTimeJTextField().setText(startTime+"");
	getResampleStepJTextField().setText(step+"");
	getResampleEndTimeJLabel().setText(endTime+"");
	
	double[] params = resampleRefresh();
	
	//if(getPdeDataContext().getTimePoints().length > limit){
		boolean bError = false;
		getResampleInfoJLabel().setText("There are "+getPdeDataContext().getTimePoints().length+" time points");
		getResampleLimitJLabel().setText("Time Points limit is "+RESAMPLE_LIMIT);
		do{
			if(params == null){
				getResampleResultsJLabel().setText("Error, make sure numbers are typed correctly");
			}
			//else{
				//getResampleResultsJLabel().setText("Current values produce "+(int)params[RESAMPLE_NUMTP_INDEX]+" time points");
			//}
			//bError = false;
			setResampleCancelBoolean(null);
			getResampleTimesJDialog().show();
			if(getResampleCancelBoolean() == null || getResampleCancelBoolean().booleanValue()){
				return null;
			}
			params = resampleRefresh();
			if(params == null){
				continue;
			}
			//try{
				//startTime = Double.parseDouble(getResampleStartTimeJTextField().getText());
				//step = Integer.parseInt(getResampleStepJTextField().getText());
			//}catch(NumberFormatException e){
				//bError = true;
				//continue;
			//}
			//int startIndex = 0;
			//if(startTime <= getPdeDataContext().getTimePoints()[0]){
				//startTime = getPdeDataContext().getTimePoints()[0];
				//startIndex = 0;
			//}else if(startTime >= getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1]){
				//startTime = getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1];
				//startIndex = getPdeDataContext().getTimePoints().length-1;
			//}else{
				//for(int i=0;i<getPdeDataContext().getTimePoints().length;i+= 1){
					//if(getPdeDataContext().getTimePoints()[i] == startTime){
						//startIndex = i;
						//break;
					//}else if(getPdeDataContext().getTimePoints()[i] > startTime){
						//startTime = getPdeDataContext().getTimePoints()[i-1];
						//startIndex = i-1;
					//}
				//}
			//}
			//numTimePoints = ((getPdeDataContext().getTimePoints().length-(int)params[START_INDEX_INDEX])/step) + 1;
		}while((int)params[RESAMPLE_NUMTP_INDEX] > RESAMPLE_LIMIT);
	//}
	return params;
}


/**
 * Comment
 */
private double[] resampleRefresh() {

	String startTimeS = getResampleStartTimeJTextField().getText();
	startTimeS = (startTimeS != null && startTimeS.length() > 0?startTimeS:null);
	if(startTimeS == null){
		throw new NumberFormatException();
	}
	String stepS = getResampleStepJTextField().getText();
	stepS = (stepS != null && stepS.length() > 0?stepS:null);
	String endTimeS = null;
	//String endTimeS = getResampleEndTimeJTextField().getText();
	//endTimeS = (endTimeS != null && endTimeS.length() > 0?endTimeS:null);

	double startTime = -1;
	int step = -1;
	double endTime = -1;
	int numTimePoints = -1;
	
	try{
		startTime = Double.parseDouble(startTimeS);
		step = (stepS != null?Integer.parseInt(stepS):0);
		//endTime = (endTimeS != null?Double.parseDouble(endTimeS):0);
	}catch(NumberFormatException e){
		return null;
	}

	
	int startIndex = 0;
	if(startTime <= getPdeDataContext().getTimePoints()[0]){
		startTime = getPdeDataContext().getTimePoints()[0];
		startIndex = 0;
	}else if(startTime >= getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1]){
		startTime = getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1];
		startIndex = getPdeDataContext().getTimePoints().length-1;
	}else{
		for(int i=0;i<getPdeDataContext().getTimePoints().length;i+= 1){
			if(getPdeDataContext().getTimePoints()[i] == startTime){
				startIndex = i;
				break;
			}else if(getPdeDataContext().getTimePoints()[i] > startTime){
				startTime = getPdeDataContext().getTimePoints()[i-1];
				startIndex = i-1;
				break;
			}
		}
	}

	int extra = (((getPdeDataContext().getTimePoints().length-startIndex)%step) != 0?1:0);
	int numSteps = ((getPdeDataContext().getTimePoints().length-startIndex)/step);
	numTimePoints =  numSteps + extra;
	if(numTimePoints > RESAMPLE_LIMIT){
		numTimePoints = RESAMPLE_LIMIT;
	}
	int endIndex = startIndex + step*(numTimePoints-1);
	endTime = getPdeDataContext().getTimePoints()[endIndex];
	
	double[] params = new double[] {startTime,step,endTime,numTimePoints};

	getResampleEndTimeJLabel().setText(params[RESAMPLE_END_INDEX]+"");
	getResampleResultsJLabel().setText("Current values produce "+(int)params[RESAMPLE_NUMTP_INDEX]+" time points");
	//getResampleResultsJLabel().setText(startIndex+" "+endIndex+" "+step+" "+(int)params[RESAMPLE_NUMTP_INDEX]);

	return params;
	

}


/**
 * Sets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @param pdeDataContext The new value for the property.
 * @see #getPdeDataContext
 */
public void setPdeDataContext(cbit.vcell.simdata.PDEDataContext pdeDataContext) {

	if(dataValueSurfaceViewerJIF != null){
		dataValueSurfaceViewerJIF.dispose();
	}
	
	dataValueSurfaceViewerJIF = null;
	fieldDataValueSurfaceViewer = null;
	meshRegionSurfaces = null;
	
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
			connPtoP10SetSource();
			connEtoC8(ivjpdeDataContext1);
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
 * Set the CancelFactory to a new value.
 * @param newValue java.lang.Boolean
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setResampleCancelBoolean(java.lang.Boolean newValue) {
	if (ivjResampleCancelBoolean != newValue) {
		try {
			ivjResampleCancelBoolean = newValue;
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
 * Insert the method's description here.
 * Creation date: (2/26/2006 2:24:21 PM)
 */
private void showComponentInFrame(Component comp,String title) {
	
	JInternalFrame frame =
		new JInternalFrame(title, true, true, true, true);
	frame.getContentPane().add(comp);
	frame.pack();
	cbit.util.BeanUtils.centerOnComponent(frame,this);
	getDataViewerManager().showDataViewerPlotsFrames(new JInternalFrame[] {frame});
	
}


/**
 * Comment
 */
private void showKymograph() {
	//Collect all sample curves created by user
	SpatialSelection[] spatialSelectionArr = getPDEDataContextPanel1().fetchSpatialSelections(getPDEDataContextPanel1().isSpatialSampling2D(),false,true);
	final Vector lineSSOnly = new Vector();
	if (spatialSelectionArr != null && spatialSelectionArr.length > 0) {
		//
		for (int i = 0; i < spatialSelectionArr.length; i++){
			if(spatialSelectionArr[i].isPoint() ||
				(	spatialSelectionArr[i] instanceof SpatialSelectionMembrane && 
					((SpatialSelectionMembrane)spatialSelectionArr[i]).getSelectionSource() instanceof cbit.vcell.geometry.SinglePoint)){
			}else{
				lineSSOnly.add(spatialSelectionArr[i]);
			}
		}
	}
	//
	if(lineSSOnly.size() == 0){
		PopupGenerator.showErrorDialog(this, "No line samples match DataType="+getPdeDataContext().getDataIdentifier().getVariableType());
		return;
	}
	//
	final double[] startTimeAndStep = resample();
	if(startTimeAndStep == null){
		return;
	}
	//
	final JInternalFrame jif = (JInternalFrame)BeanUtils.findTypeParentOfComponent(this, JInternalFrame.class);
	GlassPane gp = new GlassPane(true);
	gp.setPaint(true);
	jif.setGlassPane(gp);
	jif.setIconifiable(false);
	jif.setClosable(false);
	jif.setMaximizable(false);
	gp.setVisible(true);
	// create thread that gets it and updates the gui when done
	SwingWorker worker = new SwingWorker() {
		Vector plotFrames = new Vector();
		Exception exc = null;
		//AsynchProgressPopup pp = new AsynchProgressPopup(PDEDataViewer.this, "Fetching data...", "Retrieving time series for variable '" + getPdeDataContext().getVariableName(), false, false);
		public Object construct() {
			try {
				//pp.start();
				VariableType varType = getPdeDataContext().getDataIdentifier().getVariableType();
				if(lineSSOnly.size() > 0){
					int[] indices = null;
					double[] accumDistances = null;
					for (int i = 0; i < lineSSOnly.size(); i++){
						cbit.vcell.geometry.Coordinate tp = null;
						if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
							SpatialSelectionVolume ssv = (SpatialSelectionVolume)lineSSOnly.get(i);
							SpatialSelection.SSHelper ssh = ssv.getIndexSamples(0.0,1.0);
							indices = ssh.getSampledIndexes();
							accumDistances = ssh.getWorldCoordinateLengths();
						}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
							SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)lineSSOnly.get(i);
							SpatialSelection.SSHelper ssh = ssm.getIndexSamples();
							indices = ssh.getSampledIndexes();
							accumDistances = ssh.getWorldCoordinateLengths();
						}

						KymographPanel  kymographPanel = new KymographPanel();
						JInternalFrame frame = new JInternalFrame("LINESCAN-TIME PLOTS", true, true, true, true);
						frame.setContentPane(kymographPanel);
						frame.pack();
						BeanUtils.centerOnComponent(frame, jif);
						plotFrames.add(frame);
						
						kymographPanel.initDataManager(
							((ClientPDEDataContext)getPdeDataContext()).getDataManager(),
							getPdeDataContext().getVariableName(),
							startTimeAndStep[RESAMPLE_START_INDEX],(int)startTimeAndStep[RESAMPLE_STEP_INDEX],startTimeAndStep[RESAMPLE_END_INDEX],
							indices,accumDistances,true,getPdeDataContext().getTimePoint());
					}
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
				exc = e;
			}
			return null;
		}
		public void finished() {
			// look for errors and notify
			if (exc != null) {
				PopupGenerator.showErrorDialog(PDEDataViewer.this, "Failed to retrieve data:\n"+exc.getMessage());
			}
			// pass it to the manager
			//pp.stop();
			if(plotFrames.size() != 0){
				JInternalFrame[] plotFramesArr = new JInternalFrame[plotFrames.size()];
				plotFrames.copyInto(plotFramesArr);
				getDataViewerManager().showDataViewerPlotsFrames(plotFramesArr);
				for(int i=0;i<plotFramesArr.length;i+= 1){
					((KymographPanel)plotFramesArr[i].getContentPane()).zoomToFill();
				}
			}
			// unblock
			jif.setGlassPane(new JPanel());
			jif.getGlassPane().setVisible(false);
			((JPanel)jif.getGlassPane()).setOpaque(false);
			jif.setGlassPane(new JPanel());
			jif.getGlassPane().setVisible(false);
			((JPanel)jif.getGlassPane()).setOpaque(false);
			jif.setIconifiable(true);
			jif.setClosable(true);
			jif.setMaximizable(true);
		}
	};
	worker.start();
}


/**
 * Comment
 */
private void showMembraneSurfaces(java.awt.event.ActionEvent actionEvent){

	if(getDataValueSurfaceViewer() == null){
		return;
	}
	getJButtonSurfaces().setText(UPDATE_MEMB_SURFACE_BUTTON_STRING);
	updateDataValueSurfaceViewer();
	getDataViewerManager().showDataViewerPlotsFrames(new JInternalFrame[] {dataValueSurfaceViewerJIF});
}


/**
 * Comment
 */
private void showSpatialPlot() {
	// check selections
	final SpatialSelection[] sl = getPDEDataContextPanel1().fetchSpatialSelections(getPDEDataContextPanel1().isSpatialSampling2D(),false,true);
	if (sl == null) {
		PopupGenerator.showErrorDialog(this, "Nothing selected!");
		return;
	}
	for (int i = 0; i < sl.length; i++){
		if (sl[i].isPoint()) {
			PopupGenerator.showErrorDialog(this, "One or more selections are single points - no spatial plot will be produced for those selections");
			break;
		}
	}
	// block
	final JInternalFrame jif = (JInternalFrame)BeanUtils.findTypeParentOfComponent(this, JInternalFrame.class);
	GlassPane gp = new GlassPane(true);
	gp.setPaint(true);
	jif.setGlassPane(gp);
	jif.setIconifiable(false);
	jif.setClosable(false);
	jif.setMaximizable(false);
	gp.setVisible(true);
	// create thread that gets it and updates the gui when done
	SwingWorker worker = new SwingWorker() {
		Hashtable failures = new Hashtable();
		Vector plotFrames = new Vector();
		AsynchProgressPopup pp = new AsynchProgressPopup(PDEDataViewer.this, "Fetching data...", "Retrieving spatial series for variable '" + getPdeDataContext().getVariableName(), false, false);
		public Object construct() {
			pp.start();
			// get plots, ignoring points
			for (int i = 0; i < sl.length; i++){
				try {
					if (! sl[i].isPoint()) {
						PlotData plotData = getPdeDataContext().getLineScan(getPdeDataContext().getVariableName(), getPdeDataContext().getTimePoint(), sl[i]);
						PlotPane plotPane = new PlotPane();
						plotPane.setPlot2D(new Plot2D(new String[] { getPdeDataContext().getVariableName() },new PlotData[] { plotData }, new String[] {"Values along curve", "Distance (\u00b5m)", "[" + getPdeDataContext().getVariableName() + "]"}));
						JInternalFrame frame = new JInternalFrame("SPATIAL PLOT for "+getPdeDataContext().getVariableName(), true, true, true, true);
						frame.setContentPane(plotPane);
						frame.pack();
						BeanUtils.centerOnComponent(frame, jif);
						plotFrames.add(frame);
					}
				} catch (Exception exc) {
					exc.printStackTrace(System.out);
					failures.put(sl[i], exc);
				}
			}
			return null;
		}
		public void finished() {
			// look for errors and notify
			if (! failures.isEmpty()) {
				Enumeration en = failures.keys();
				SpatialSelection selection = null;
				String sls = "Failed to retrieve data for selections:\n";
				String errors = "\nErrors:\n";
				while (en.hasMoreElements()) {
					selection = (SpatialSelection)en.nextElement();
					sls += sl+"\n";
					errors += ((Exception)failures.get(selection)).getMessage()+"\n";
				}
				PopupGenerator.showErrorDialog(PDEDataViewer.this, sls + errors);
			}
			// create array and pass'em to the manager
			pp.stop();
			if(plotFrames.size() != 0){
				getDataViewerManager().showDataViewerPlotsFrames((JInternalFrame[])BeanUtils.getArray(plotFrames, JInternalFrame.class));
			}
			// unblock
			jif.setGlassPane(new JPanel());
			jif.getGlassPane().setVisible(false);
			((JPanel)jif.getGlassPane()).setOpaque(false);
			jif.setIconifiable(true);
			jif.setClosable(true);
			jif.setMaximizable(true);
		}
	};
	worker.start();
}


/**
 * Comment
 */
private void showTimePlot() {
	//Collect all sample curves created by user
	SpatialSelection[] spatialSelectionArr = getPDEDataContextPanel1().fetchSpatialSelections(true,true,true);
	final Vector singlePointSSOnly = new Vector();
	if (spatialSelectionArr != null && spatialSelectionArr.length > 0) {
		//
		for (int i = 0; i < spatialSelectionArr.length; i++){
			if(spatialSelectionArr[i].isPoint() ||
				(	spatialSelectionArr[i] instanceof SpatialSelectionMembrane && 
					((SpatialSelectionMembrane)spatialSelectionArr[i]).getSelectionSource() instanceof cbit.vcell.geometry.SinglePoint)){
				singlePointSSOnly.add(spatialSelectionArr[i]);
			}
		}
	}
	//
	final double[] startTimeAndStep = resample();
	if(startTimeAndStep == null){
		return;
	}
	//
	if(singlePointSSOnly.size() == 0){
		PopupGenerator.showErrorDialog(this, "No Time sampling points match DataType="+getPdeDataContext().getDataIdentifier().getVariableType());
		return;
	}
	//
	final JInternalFrame jif = (JInternalFrame)BeanUtils.findTypeParentOfComponent(this, JInternalFrame.class);
	GlassPane gp = new GlassPane(true);
	gp.setPaint(true);
	jif.setGlassPane(gp);
	jif.setIconifiable(false);
	jif.setClosable(false);
	jif.setMaximizable(false);
	gp.setVisible(true);
	// create thread that gets it and updates the gui when done
	SwingWorker worker = new SwingWorker() {
		Vector plotFrames = new Vector();
		Exception exc = null;
		AsynchProgressPopup pp = new AsynchProgressPopup(PDEDataViewer.this, "Fetching data...", "Retrieving time series for variable '" + getPdeDataContext().getVariableName(), false, false);
		public Object construct() {
			try {
				pp.start();
				VariableType varType = getPdeDataContext().getDataIdentifier().getVariableType();
				if(singlePointSSOnly.size() > 0){
					int[] indices = null;
					String[] plotNames = null;
					//
					indices = new int[singlePointSSOnly.size()];
					plotNames = new String[singlePointSSOnly.size()];
					for (int i = 0; i < singlePointSSOnly.size(); i++){
						cbit.vcell.geometry.Coordinate tp = null;
						if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
							SpatialSelectionVolume ssv = (SpatialSelectionVolume)singlePointSSOnly.get(i);
							indices[i] = ssv.getIndex(0);
							tp = ssv.getCurveSelectionInfo().getCurve().getBeginningCoordinate();
						}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
							SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)singlePointSSOnly.get(i);
							indices[i] = ssm.getIndex(0);
							double midU = ssm.getCurveSelectionInfo().getCurveUfromSelectionU(.5);
							tp = ((cbit.vcell.geometry.SampledCurve)ssm.getCurveSelectionInfo().getCurve()).coordinateFromNormalizedU(midU);
						}
						plotNames[i] = "P["+i+"] ("+niceCoordinateString(tp)+")";
					}

					double[][] timeSeries = getTimeSeries(indices,startTimeAndStep[RESAMPLE_START_INDEX],(int)startTimeAndStep[RESAMPLE_STEP_INDEX],startTimeAndStep[RESAMPLE_END_INDEX]);
					PlotPane plotPane = new PlotPane();
					plotPane.setPlot2D(new SingleXPlot2D("Time", plotNames, timeSeries, new String[] {"Time series for " + getPdeDataContext().getVariableName(), "Time (s)", "[" + getPdeDataContext().getVariableName() + "]"}));
					JInternalFrame frame = new JInternalFrame("TIME PLOT"+(singlePointSSOnly.size() > 1?"S":"")+" for "+getPdeDataContext().getVariableName(), true, true, true, true);
					frame.setContentPane(plotPane);
					frame.pack();
					BeanUtils.centerOnComponent(frame, jif);
					plotFrames.add(frame);
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
				exc = e;
			}
			return null;
		}
		public void finished() {
			// look for errors and notify
			if (exc != null) {
				PopupGenerator.showErrorDialog(PDEDataViewer.this, "Failed to retrieve data:\n"+exc.getMessage());
			}
			// pass it to the manager
			pp.stop();
			if(plotFrames.size() != 0){
				JInternalFrame[] plotFramesArr = new JInternalFrame[plotFrames.size()];
				plotFrames.copyInto(plotFramesArr);
				getDataViewerManager().showDataViewerPlotsFrames(plotFramesArr);
			}
			// unblock
			jif.setGlassPane(new JPanel());
			jif.getGlassPane().setVisible(false);
			((JPanel)jif.getGlassPane()).setOpaque(false);
			jif.setGlassPane(new JPanel());
			jif.getGlassPane().setVisible(false);
			((JPanel)jif.getGlassPane()).setOpaque(false);
			jif.setIconifiable(true);
			jif.setClosable(true);
			jif.setMaximizable(true);
		}
	};
	worker.start();
}


/**
 * Comment
 */
private void updateDataSamplerContext(java.beans.PropertyChangeEvent propertyChangeEvent) {
	//
	if(propertyChangeEvent.getPropertyName().equals("timeDataSamplers")){
		getJButtonTime().setEnabled(((Boolean)(propertyChangeEvent.getNewValue())).booleanValue());
	}else if(propertyChangeEvent.getPropertyName().equals("spatialDataSamplers")){
		getJButtonSpatial().setEnabled(((Boolean)(propertyChangeEvent.getNewValue())).booleanValue());
		getKymographJButton().setEnabled(
			(getPdeDataContext().getTimePoints().length > 1) &&
			((Boolean)(propertyChangeEvent.getNewValue())).booleanValue()
			);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 2:00:05 PM)
 */
private void updateDataValueSurfaceViewer() {

	if(getDataValueSurfaceViewer() == null){
		return;
	}
	//SurfaceColors and DataValues
	SurfaceCollection surfaceCollection = getDataValueSurfaceViewer().getSurfaceCollectionDataInfo().getSurfaceCollection();
	cbit.vcell.simdata.DisplayAdapterService das = getPDEDataContextPanel1().getdisplayAdapterService1();
	final int[][] surfaceColors = new int[surfaceCollection.getSurfaceCount()][];
	final double[][] surfaceDataValues = new double[surfaceCollection.getSurfaceCount()][];
	for(int i=0;i<surfaceCollection.getSurfaceCount();i+= 1){
		Surface surface = surfaceCollection.getSurfaces(i);
		surfaceColors[i] = new int[surface.getPolygonCount()];
		surfaceDataValues[i] = new double[surface.getPolygonCount()];
		for(int j=0;j<surface.getPolygonCount();j+= 1){
			surfaceDataValues[i][j] = getPdeDataContext().getDataValues()[meshRegionSurfaces.getMembraneIndexForPolygon(i,j)];
			surfaceColors[i][j] = das.getColorFromValue(surfaceDataValues[i][j]);
		}
	}
	

	cbit.vcell.geometry.gui.DataValueSurfaceViewer.SurfaceCollectionDataInfoProvider svdp =
		new cbit.vcell.geometry.gui.DataValueSurfaceViewer.SurfaceCollectionDataInfoProvider(){
			public double getValue(int surfaceIndex,int polygonIndex){
				return getPdeDataContext().getDataValues()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)];
			}
			public String getValueDescription(int surfaceIndex,int polygonIndex){
				return getPdeDataContext().getVariableName();
			}
			public int[][] getSurfacePolygonColors(){
				return surfaceColors;
			}
			public Vect3d getCentroid(int surfaceIndex,int polygonIndex){
				Coordinate centroid = getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)].getCentroid();
				return new Vect3d(centroid.getX(),centroid.getY(),centroid.getZ());
			}
			public float getArea(int surfaceIndex,int polygonIndex){
				return getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)].getArea();
			}
			public Vect3d getNormal(int surfaceIndex,int polygonIndex){
				return getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)].getNormal();
			}
			public int getMembraneIndex(int surfaceIndex,int polygonIndex){
				return meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex);
			}
			public java.awt.Color getROIHighlightColor(){
				return new java.awt.Color(getPDEDataContextPanel1().getdisplayAdapterService1().getSpecialColors()[cbit.vcell.simdata.DisplayAdapterService.FOREGROUND_HIGHLIGHT_COLOR_OFFSET]);
			}
			public void showComponentInFrame(Component comp,String title){
				PDEDataViewer.this.showComponentInFrame(comp,title);
			}
			public cbit.util.TimeSeriesJobResults getTimeSeriesData(int[][] indices,boolean bAllTimes,boolean bTimeStats,boolean bSpaceStats) throws cbit.util.DataAccessException{
				try{
					double beginTime = (bAllTimes?getPdeDataContext().getTimePoints()[0]:getPdeDataContext().getTimePoint());
					double endTime = (bAllTimes?getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1]:beginTime);
					String[] varNames = new String[indices.length];
					for(int i=0;i<varNames.length;i+= 1){
						varNames[i] = getPdeDataContext().getVariableName();
					}
					final cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
						new cbit.util.TimeSeriesJobSpec(varNames,indices,beginTime,1,endTime,bSpaceStats,bTimeStats);

					new Thread(
						new Runnable(){
							public void run(){
								plotStatistics(timeSeriesJobSpec);
							}
						}
					).start();
					//
					return null;
				}catch(Throwable e){
					PopupGenerator.showErrorDialog(e.getMessage());
					return null;
				}
			}
	};

	getDataValueSurfaceViewer().setSurfaceCollectionDataInfoProvider(svdp);
	
	dataValueSurfaceViewerJIF.setTitle(
		getSimulationModelInfo().getContextName()+"::"+
		"SIM("+getSimulationModelInfo().getSimulationName()+")::"+
		"VAR("+getPdeDataContext().getVariableName()+")::"+
		"TIME("+getPdeDataContext().getTimePoint()+")");

	
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G350171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8BF4D45555F0D4EC29520ADFD1239F55D431CD5B34C5CB5B541F36E925957BD125AD563425FE31D2C5FFF445B62D29257566850184889081A16A28BC820490A0C80C918389C2B0C0480CA1C000CCB2B719C96EBCB2F3A719C010FF1F47BE774E1DBB1990CBD7574FE22FBBF71F7DB8E76F735867F5A3953F1E101715DDACC9D9F9927F37A7CB122AEE11A4651E83B7A1EE7A1F1B8BA45D3FB7G16CB
	2541DCF826C2DA69F55602A269C1B59F5E67417B4CEB2D852B6177CA695E46549B61C794BD13A16D52BDFF348F2F6705A328E7A1FD3F2A2F901ECB815281C71EB5994D207F563EB59CDF1C417088D959007B8B211952D742F1F1C0938FB093E08E271F8F4FC5D4733555D51C2E6D46C4A963E77E6CA5C40ECEA6C25BDF98EF9CFFDA249733CA7FCAF47D1E661370A4C3DABAGA9FC0E74CC67961267265E6E3D1D9DAE353C3FD3D61472CE45A5FB54F207DD35D7D7B7559F28079FADAEF9C076CB52A4AAEF03BF1F
	7203EF603D628502BBCDF54C05F7895E573C00787EEE02D76177B800C9AFE0395E5BD443735DFA613BD27EDFFF3AE8D914F2CDFD0115E74E8BDA394A36F5643CC2FD3067983135A53339209E2095E088E0A240D400F55477FB0F12F26E6BD06B3CEE3757334F675B2FB87CC73C8ED9015F55551042F14DF227576F10A4665FA77A4ACC72B39CB03E67AF1B0F59A4F1977ABA29799B526CEF5D1C3D5044A63393AFE44F4AE4361817295904695E620F263B70EF61F5B1DC7789C92FFBFA7B2E05F9A63A679C593550
	4265D47ECD536D7B9B717543348E5860F796174B7033A9FE0A8E4F6A251D62497BCCC82BFC916BC66BD24CCBE36E1DD2510EDBCC6D907F63F15971AF32BCCCFE5118177B1CC4766C97194E6DA2AF3F25787A3FEBF81617A1DACF49FB9224157E233900552F5A63241DCE7AE7F3C18AC0AAC0BAC051CB4D85E5G35AFE19D7B74516DA32CE3CDF63F3ABF68D29C502098EF6D31D57014C32A5F5E608928F6CF275CE2773B6C9D0A9C10B8CD43B1E2030EB03E23346EF7020E0332C7765BD5175783526DEED9157D81
	C6F36258F2C26317E92A4D6516C1015BA7215CF347D612F6EC8F289F7920494B9A85CB6FBEC6FC52EFEF8109A0GFEB33B381B093D5A217CA10031428EFF2072BE147D9ED9292EEE74FAFB03BE9BA909147A923173BE5AF7CC045FD3DEC29B3B2C84D7A2D98BAA81181CD71AD66864543BBAC9766D7EC1ECE30F749541739050BBG46GA482307A73DCAB561F7AA5DF174A9E3949347E349E5D35F09A7050B6451FDABB7A8315CF72181BE5ADA885B88440523A7BDAAF23ED0F613217727CA672F7078E9F590744
	DD160E053A5FB93CCD115FF9D168195D9F35919FBA21DCE3GA6E5216DEDCD96D617775B83B2565EE3FE28F832C373844BE70A330D5BB6076BDB4875138174E5AACF1216D0DAC9DA46F593BD2F823CAE3131F5B740DBG9BGB681EC86588A30CD58A30D6A198AF91C83D0188DF585208DE08CC09240AC0079G6BF32C850DGAA40C400E9G73G0AF32D85B5G8E00F1GD3GE6836466D98B4A725037D7F77B322217E789AF43F2DE2E17F576B416A9DAF974FE58FD99BE7E7F99186D9F27360FE6F7E26F555C
	66D81722595E4C6E5B8DBA5F7A8FA877FF8230B6FF78B831275C8F21DF3D6C13BD8E789160E9470F93BF7071294BABF8CED03C8F4633BA98C08282FF66B8710FDDD57D2E0E20AADBE5D5F5F93A35F4196A8A84BBFC5AD0083A146344775E818F89B7B92E6FB8998BBA3D9E0FCCBB9F41425383F4AC1295598D192E7386BDAA438FB613F83DD79E64E36145668592994B14A099D7D98CBC59CE6A174F6FF2C317C606E6E9CA9E19C3347A5768ECE4839C09654742F3E29E0EB383EF61B8337E7CD72414F33EEC3318
	A97E6E9B326767313E3EE801B64E30BCF92C66F9D769184871811657A52BC8BBB00464343D2C14521DB029111A65G8C5D8A9B458F573B6C0A371B25AFDD7E32AE3D0E44984AFE2FD7116D9EA1633B3944AFBE07CC6457F9BDAA38FC879FAB0ADAC8BEABA07F75GED8BF02CC83712F49C2779A0D167D59466AAC8E376AC8D33273A0059F3A2BCA78394AEA4B45DDD45BA1A691CA6931EC3B47D97A25D866F4AC2162E55615F5B48189D107B02B2940F849D9F8A5F4F312DB0243570FA40526759C8BDD0F471CAD5
	BE513949057334E9DC5FECF8E68214C07AEB00E7F4FF97F4E372B53A0970BBA50CEEFA8F524D4EE7F2B272995D1AC50CEE6EA2C217500BF4B50B981DED91131B300855A1C6572DA0DD65E29877G14450CAEE9311EEE2B9B69E6GFE0E0EAE370051317C7D2F203394B03DD5703481048A746578168769A6F03AA4CE1792C6F70D9769E6F13AF91CAE3FD0CFF74207F46B8B99DDCDA123330551ED9673E605530D61F40905FA7BE579C5F981BF87A0F789ABEF7D92D65E7C25046EA7022E9D70A10078A5AC5ECB05
	E786G23DB2CA21DEDA90BAB94F81258F04CD23D3CA4C1B7915E27AC8D17B7EBA94AFBAC08F4F317327844A80F15B72E9F6924E5AC7FEB163172D6AD531737E5G69DA816F83984F69E7AE53570335A124CB87FCFE91642D0851B90B74FA9F93F409BCBD25084540330B74FA938711EE9ECF4FDD4E68CB166B75F61CC73AC34BB5B911F42BAE88FFGBE84904F6525F2BA56970EDFCF4677E157AEF43DA4DB3778B11DFB907E6295D63EBE71DC1074EB0D706EG98DA01FD7BAF16607A44045EC4E912FD6742E86B
	AE93D630FCA52F507A760C74FD8BD83FF96A84511989E979AB314F6C7EC8BFC75A9788F8BBDDF4FA467B22D5AD7A3E1F615635AC06E7174B9FD09BE0F08EE13F75C68B49AF193FF1B437CFF35FABE81F9EFB3FBDD49E9800B1383A1A8F8A922E6534D31B10D82BE4A55A6418CA6771702EG0C81E0B2CB363CA9EC6EF5173BBA5C6541FEF7393DC3AD179DAE353A7A83FB276AA5F3B4A67B76AEDCA71A38926DFA71833DDD27AE3B18DD90452E33D6B2FBE62C546C1A7814EDA11B2FE7F695F259154510CF00C945
	247E6C6EA6E3BE292BCCDF67AE54B77E7C5DD27BCD37C555B73718376317B5FDE59FE766B1FDCDAE54D78B69A1GBEE63A88ED781809E35E2FAE68473C4940B38F20E8954CD1D6A17F9FCC79D95E7F342F967B30EF5CA9A5AF9C17ED36C6667CF2FCF64DAABE27DC25653DF6D2C70E8D0065FFFD8F66FF8A504C83E0F934DC5467F17DEA1647F6F82A2BB10F5F6CB14BA3137B3C10BB8E6827891E56DE322661F085FC0AFDF01F436ED3E53FD576773BBA659D98EFDC5B7BF2B49A8965BF58DB486B74FC10DD3986
	65D7A86F11BC793DBE592F8E56B96D1EEE39BC7C3514D47C728E08F08265CDE1A987C8183BD93415B9356F42B3C857BFC8912DB4C672731216B68EAF675D4DAD5CDFF72E547B2B6652D9D3FF0DDD664CD656B0BFCDD823794BF6D3C70E73A67457F8854B1D86B4EBDE4198695EECFD0C548478904044D750AEAFA8BB61E92741D7B9FBB4497EAE2F5FADBB18C16C8350DCC9464B7751E4B48633C5526BB4CEF2FA872CBE68136CCA1362D5CB21085C9FB6B766EBB2681CAB72F5FD39D93EACEB316FF8E07DB2AE17
	4426A814494C7708368C744ADAAC0B57C363B9168F2B4CC3CAD6EF8BFA7575F692702481642E83DB2DC37E5BBCA4ADC099E5C7335CCD83D226F33650D98174CE41D367A5798DA82C8EB21C9206E37CC9DE64CFG5ED4413F52E756D6D85ADE53B4A6AF4E07720ACAE0FCACC1F9CF79C83F6F817F5915FDA1D7GE5AD6FB362191C66BE149387B212051C6F7545EE0FCF7645EE0FDB7C687FF410DD7AAA7A7F539B4D7C5F7EAA6A3F517F496A65B3070BF87D79F0506D6D765BFDCE28C0ACCF4F09BC2920AFC96469
	8BBD46BC4D01347C57B0CF9F85BED91E36D8C81E78AC2A3403BDB73331EFC2G473E1257F0AC7A7AEB7A7EA15FFE2DE97FB06E696EEC5BEB2CDF98FACD6B9F26C25FB0D3748F3BD47473B420197F9A36333D478BC53D0059DED0D1997ECB2B97F35FFA83322EBDBF22226DAADF073677BA5A6ED917196DA6C1BAABDFEF904B178CB8A6EBE1BF66A91D60AC087F0A403783AE5E0276AF9AA06B848E91269D31FB6C5D3AB9EDCD54F4A67752G66FDA6484CFF83731EAF1B653D768D547B6440A7737BDFDEA1FD241F
	4F251B65AE7869545934AE047932014EF1A2DF410F4D5A48B4112F2F06BED93E1E6C5E455B4891595D6137FBE4EB505FE56F1483FC8CB3E5FFD774438B8751CF29102722B7B1DF9FF6C76F537EEB1044DB2A5395E0B2FEB108E5BF847CF1C2C655F9026F675B9AC76DEE19657969CD2FC7485EF12EC5C7270D93571FC75949A0B77DCD2CDFFFDB0B785237204FF98B6D7DC027193D635F42BC352F7BE4767E47D1322E58E9D7BA2DAA0CD1815555995352385679C044BEDD52DB5AFEC04ADB11FB83EC7F2A033625
	D9100EEB548C2FD07C9C9D1E59A6A308BE4D00345675D63E677246A9B2FECE0577D400F4007C8DE0CF00C39BF01E721596D21F065D53EAG0779BDB0BA07AD66615E52892237D79E646F1DA772A43E7EA66CA0D3BBB8B6186F0504B8FE1CAE1D1577FE5AEFA5ECB05A616794BFF903518E9F097EB2855256EF44757E3B3A226D170E5DE81EA7E63F9BCE133AB591E81281E6824C85485F0436FB66C4CC5B9D88195A0E656B1FB4DF251B4C73E0860C2F301B7055DE869F9B1BB61F521FB348DDFEB81B584AEC1E64
	633263B6E9E39333C95599E21394404F8648GA8AC3596141522CDAADB4BE259849BBEB786ABD30BAB56FEFAE3E9F8F9991FA3A61F4F40475A752A8FAA24483505004BCD0622726E20CB1C0B609DD693F4358A0BCB3F99524725E3C1FE9240CC00B925B8F6968C6158B93F94675C6EA1D2BFABB7DB8B9A819C1B311DFE6EACD98F990277F80049GB3G52818A37G6F96346D552D7BE359F69FAE485B3C62E71DF1D19DED3725E7F4B62F0A49F7E80B194D1FB8BD921B13F98331395F2BD01BB3FB36DEC4FB36EF
	C1FB7E60A231E7BC3CCF86183E855B7B8B23ACD7DEEFAC3E34ADE6FC8FC770E59A684C757DBE026FB543FB7156F0BED6DFF25AC9FDA90334C3G6DGC3G89GD3815236E2FDF924753DD875050ED9B6EF9D608EF93D3DA6D5056775E6E5F4E51C9441D7EEF8174A4C6A4AB2EB14352D7DC1D5556DB974FDECEC0BE4EDE38D48ACAB433678F4B62E895617E19DB9659EDDF91C91FC46728C994A4357A54BB18F93C49E16FAC6D7CFCBE2724DB03529DAD2AE45D8CFBDAA8FC093BC9072F97D2C47C3BBEF134D7A3C
	B920A7D358392E975BE851B6EC234F52351DC3705E8E905A06E57F43A84B1E9E13EF7CB6B33EFB3D2353179213AF51D45F27FC2353B7A1A6DF0A293E561867514C75F54564CBB555D791915B0E2C7D6C0D60B3361F74EDE66D67879B317D6417E39D4A98E59E32833172D0DB9E4E475AC0DD2F318D34825D904078F2EC8333FAB01F09A21F37D2FDE9861943816B674F5BC9BF1F81FCC55BA1EE872887F0820C81C85C0E7D7C4613E4EDF3A47DBC1B28C47672AC1F6F28232BC387E372CD5FEE46773B883E11796F
	6988BE237F52361B55217542B7195B51B73EA011D5D1F139B178127E5159EADDCC3E7A8AB33EEF0ED25F77E37235196A3BF6E0F43E79FC841F51B7EA0519EF5E1751B763AA50B78D21516561C4841FB12E1AE61A07240FB18F33C59E168C0E524FB1790AF71871BD9A41B732B27FB1024FE877B2D3FDB71E9F1D3EBBA2780C7A6ACD753D34F6F47A162C0B6553369DE6BE7DE7A77AD405F4F6BE75B13A9F1A846FA98277FF68FD02B970BEFF877A5E59CF563DACBB715DCF5FC95C435EDF8C9A4FEF925A61FCFEB9
	40F42C6CB76A48799749A740460FAD27221F8B5BF14A7CDC581ED36667425EBB351C61C1067E94B4269FB8B57C3931A3274C4E0DB19F8DD13FD5010D5BF6225FFE65C73F7984AE032E4BCF0077C4613B87D5623B29627DD7743DD50C5127554BB90BBB92182A2B8BCC077AAF57212F8B2C6C77517D785920BBFDA74687371C0E6E5F5BCF1B7BF752E9F37FFE7574707E3D77F4B47F3ECA737B6BECB29F7087BD2A4BADD351ECEEA2074819CE08D41A039E9646906981631A11043151FD9D44FF453B300C8DEDBB25
	489868004325FA7DA844A65B7D473A3A44420855A7F7E23EEB4FC4375141B366B6BAF2465CC656B3435B68741961ED7414CA7668A24EC21205F2895764BF6BDA44E91AD4EF538E8967F8FF5ECCE2D413F933EE7D6A2EFEFD9CDB81B6A4E7605B60695B05F16CD3A1BCB3B2F6974E6556DEA03ECB0177D40074DD58F60A06069B6B72AB232DF3BFCE5743CAAA59BAEE1640BFC37125BABCDB5F8D8C619D27CAC80B2FC45F471F0D6E3BBB4E1A7B6E6E33642CCF409B74F74AC67FA41DDDA35234D839C1155D44850C
	665B14DF357B3B65887FCE257CACAD9A7FCF4E12B3AA9DAE074B4FCFD1B23F5AFB74FE5DA97A0EB3970733717C28B65E79B131D951EE230D5F2578125DC69BCF3B08B6AE0534F13BF19F253CD71F2FDD222EBE380A1EA7056AAF8768A403AC197232ACD01067006A2D5BA16C3324BD3A3F0A5B4D7D3536BD3A3F5EE80F6D2FAD6D517D3523BD363F2A5B2335D9D6561F071515780D3523E592469F693B319D7DA50B566F372D8535G2DEFE3BBFAAE5B4C473E3719EF473C9D69E33E6F6E566B5EA56A4C8365E672
	62391C2428728EF8743E2E92E5596A44BEA165EDAC0B0D1E8D4A047722BDB0C75D03E5B9B46CDAEA6D1EE87555CE4F445876986BEB8F45376FB15657BF77E2FDD5A0ED669E2C2F2FF875E558A34A76F4983ED2603F6F53635F96789B7A74785D825F6D575B3CD2587C7E0DE6652E2CE265BDD4954D66DB83BAF9C7C8DBE1FDF5EE503866C04E0F36019CDF95765597FA310E0D2DC23FD45233ECA9701E8A10DE05FE790D695AF87EBBACDFA56FC4660F459513E9CCD58169356FE03FBB469E3D9DDFEFB7EF47B75A
	233763F8FB6CF67CC5FB74F61CE80F5D0E6F318F5F0EFD2A5E8FBB85FEC5D00F575A58E6D3FB5A389D1DD16D7969A05AF3689D2C33757DFA9DDA5DFC32DFDF875F917829837ABA38DB6429DEB64B53F81E17C9A6F9E27262C2FAF9EF8BF977FCECA6AF194B1991D5DE7D60E2DD3EF7D4A0FE69F93D9E2DEDCE8F43D7897C5B6B7478F7C43EF29CE679CA637949081A2F3F78499D9145BE688D2AD0DF2AC9BBAB8CE0BB4B07F78B40A10036EA969B31586A1E81B2E6F7796DEE28B2B4F8ECFA17707ED2C55E29C0BF
	93A01D4804340AF745B93737C9FBBCEC33F7F4488E920B6DE0784F7B69F9AC394B9ED4A00C748664E3BEFE4D9370B3297CEF08B34D71704EF4BEA36EDA9457C03F8BF0884089B03686F59650F3C1EE2F432E88D9EF2A38C61FA8685EE9A0E7DD7829E13A5D69210B7F629C3613583B5F5EE39B74499CF7E3BD3927A5624CF245CE22CC3CED425A5A8FBA16685B9A0FDF997F567CD7A553B8D5331004634E6F7A45B9249A6C5FB2693920F4F84FFF8F7C759E76EF73A256FE2A5E536AC163FB61F5229D5E597872B9
	9A5B2A6F31710548E2785BA8FE1C8E4F469D8B451377C910163F97671E1B06F0BE2A6C45BCBDB5A409BA1B2E7BBDB8742F1E8F6A0169782FAB2A0315D93D08E50E97E52E7AD8AB67BE5D6F1FDD3C72E5FE625F2023D8B25633E9FB353A153AB7DAFC6322735FF9FBD9FDAA9078810AAFFCDF4333FA76F79AAB1277B2C89B7FBE56333C9CE2738CF84F2DC5DCCF8E7A21C8609EABC73AF601FBFF3B282335682F8A2731BCD3EB35B24C2C8DAF4F1CDAECB753D97CD74B721DA74A73AB0AAF5C2761D9F9CE7560B9AE
	8B240D5907F9BA570BF91A3BCF5C41D634FA63537D7EDB6F15776F8A654A371B5F52BD2D627D5602757B05EFB6219DAA76E3FCD793447EBFEEBF76FF0BE87F178C6FB381666EC71B45C5445DD21D663B623AE875B211460B25F5467EEF8A455756997B3FEA9776FF2D10B6358E731FCE77D8AC75104FFA449D973E2D2947B16701GD93F01D0ED879B17FEAF4E520E93FC35C256C4017BE28F4ACA849C631DCC6DC37217A968CE537D0442835602128308CB111137D4602653B57F903C0F9538ED1710EE02402DF5
	D09C3CE7885CA58752C99F60B9454AG4EC71AGB77683BC337BE4B99993357152BE201261125C25ECE401887375D0G4B1272817A7A2BD4E6A63C979D84BD87712CF1E8A35EFBC97A521DD27C99737BCBE9360B593587795D11033A3BA35BBAF2184ECC95F5A6C0FA6AC1E2530DC17D39CA62976230F2D2F7E9FAEBD8BA09290FC97A746B4252C9FE3F1627CF1F9A16CEEC34017333327D4F86AC5B7C7F19ACA95F9E93751BB816C32CCC757CE9F968562C3209CD8BD8BD7B895DA356059865A7DC9E07F7G429C
	72958EB5D0DE5FE0EDBAE6BDF06E5851F3F50D700369CF98501F152BDAFC35693DDF82C3DC6FF87E9C77522DD94C2EC54217538F21DD77860CF67D6FAC3DDDFC8123DD9F4E51275F99B05AF5D736BEFDD640E85747395DF92CC7573DD5FFD0665FDC98F6FFD0EA0856DFA8747CF3F103315F9F20785286E33F7FCFF10E3B8652929A308D658C929B659E36962CB90C382B4F0B7EC2606E2438F6F8D78FE3DFB70146E0F1701EG10A27089941F496F661273408C3F710231CFA73C6419F058FC5FA6A58ABE7A3871
	FE048EDC230F761D0E05A314CFA07DB02DC3C316E1E8E673BCB35A6BAF8EC7DB75A123E5B65ECA572A8E812E6DC3347BD7335097F19F229D3D74FED2923CCF87C883188F30269153AF5274C6FE87195C4BA5F7D049BDCF161E152D37D1924F8361B74B63748F0777C19ACF1F4F1FCC475AB09D680302A8B22279466823C5BAF9AB25129C5A166410DA85F917240E9EF815A6B7923BDF15D34FE907CC4A75927F1743E50D879A82E4AEC85A4D24C6ED1D0B493E2DD7135D329DE5E71A4846B33D51E4979E51E4B39F
	9FF40AB6F8847D7EA317E8038277388B5BE07B917465C3F4BCD660FD6C91ED0D096068FDED01EB6F99C9BCC5E44422D1CCE878FCEAC4BAE279DADFDFA25BDEFF2F053F4FB049C79A47317A717C46707A71FDC56743CD68438C93B9B856208F730FC29BBA2A7930642866C3BE478A9A7B7152231A5C1A23517A716F53FE39094BDBAD703F207831BABC6B4717D1BCF9CF013421E3D8B7EE21B1DED51335203189F14B5DE2EF36896B41B7583AA421814890787BA81E5C25A77773495DF6062FF5911F923A251CAF16
	6FD2C7C093DEEFCED29D5846C7323FBE12BA242FCBAB25773D61F5A14F235585E7AF56052263316B42FA20A100F5216A38D697182E0CG0E71CC57EE2F26AB3987F5551B684A0A212BB5C25783C1123FAE3B925076A67DF2F8D9DBFD1A7EC7E5546FB051EF6C3B0C7AC3917A7F7763F0DD977AB4DD7517D0579893DD8B8D3A6201A6DE27ABA9C2578EC7382E5B7D1A2E1B9D28AB59C457A2032E99C0B3C327AB6D3831BDD7C71CD19CD9DDBEB4CA3E2BAE1B0F7593C3744E4D3C63467D1AEBA9BE7338F13FE63218
	9F65B6DB8B1C4DF88762AA3A7EBA835E6782C8D6980BG2AGECD65CEF7820135C9F98761C234D6BEB147BE5C5376D4E4A770F18E7944DFCA6C9BFDA087310A977FDD5CA7ED4FD54EF8BB82D4C6FE379B3F9462DD964494AD7CC77F112A1ED96C0A6C0118D6AB6C00D8D4B7739CEFEF7069CDB58272A76CEA77DBA04B884611287B4B4D900665F47246B23CDA0C791329E69A45F44EA3177C41E04GA4DEF1E7D87A159CDE8FDE3E6CFA407A7A977A312F9FE7936725689902E4F81FA1F097A90E1C51A7F73999AE
	B774C957DA08FCA3AE59260F87DF7D44BA4C7A7C9C5DF32534F6B0F2DFE147A02E534F3131FD057970AC7A085DD3A87D08505CFAFE4DB0FA795DEEBA7FD16D9DB6172A70FEBD1BFC8A0427F73AC94CEDF2E5096FB3F8AE71F463759A1E7E78EAB23FE627C458958BEDCFEC6BGA9D71DCC6E81E23B981031DD14FC6DAE2964755A6C65D15A056FA36607099FE9EB8FDA3992AF12BE75049CD0597BFEA95206A7A5344FCC10B117582C855ACE8B1B5B31386CC7D916E1ECD853220F4B3A9B48EFFE1304FF8281537E
	F43EC0CBA31FB160783633649E3838174E8978BEDC7EB21DBCFE9D1773A53C3FDC17A62E10CAE84707CF6B57990AEED332313FB416210917212DC533A3DBE738B787578747C35A6BD42FC467F2780F55171F8CCAC26F6305FA7F55BFDA1F7DD6943D53393EB5A7B43DA5F7D5AEE23ABF3D9DF5B7C1BA53337D825140FC67F3226F461EE075BF811ED3CE30764F6AFF2AEB387A4FE4B68FE9F55D39F3992B4B3AFBAABBF85A236D246CAC610857C33AB71E4E765393943D2E99BC9F392DB05E3462FA6B6DE2BF3D22
	9557ABFF5D2BEF833C6C1B104E59BA1C5DDF3CC8FE33FE32B508F1F2C2AB46490F0A3EF3DAAB76097753F354F3613D682435209240866083988BB091E0AAC09AC086C051A964FB1472D5423B8DC08518GB085E096C086C071E9F6D73E921E365348777240E4F892B9344D1EE44FA27ECCBB99593FE668F0A41F78FB2A896DF0D0E9C2BFB6068C9B37815F6F26F9DF4D53F00E0F7D75CA937E17F53F75717E724B48B70EADC65C1298BC247DAD93F5599B0C6C6FD244BD9375B46B6F4641B3017B0D451B25EE91EF
	1EB60F6317707C51F893E852CE33F81358A56334B136BD60A3F62B77FAE43CE1477029C5C48E7B1620F6770E25FDE69351453F0DA092D95B38D534A129ED3836716AA5721B5F539377B32A5A30AF291534747CAECC770974AFE7E96941CB626E4D30BAFF162D71BC60C01E34E1F91EA5C5663144552A767BFA5D6F6F842E7C3E5443D7D4870B9D2F8BC4BF57F3E3407CDC4FAD81733330DF888CFF5E7B6EC0746F04A685745F89E5316E77F3C99C33668C74DFG2DG8AC05C990CFB2BD5727DB2F6935A98BF9FF0
	F44B386E0F7ADF8DC6AF6B7AE06CEFF6EF8B12B3AE9DDE287BEEA29F713B0244DF2A57C7118CD7C54F9079DD5DCE1511F27CDEBAA6A8F297C5B3DCCD7FD23D5DD8084E696B7A098DDCB038C16062BACF8F0BD80150534DE221258316E1FC0C6B432C8D4F33905DE4E6C182C706FBF1A9A9939BC369F1CEBEF7BBB908F33709E06FD9E7F03C7C62A06EF3941E45BD2B07A9EDBD3C378128E7F14FEA7A997D3E0EE56BB153FD1D4CB36733471DE5791CFCD60BBB72A16EE075A05FC64AB8875224F3908B8254GB481
	B88106GA61C937346D02994FDAE44B661E83D705BDD9E151575AA894BBA99644CBD077BF8BB685DEB43BE5EC13F4B315F5E4DB67218CD9E1450A616F634498BD4E6AB3C7BG4636E3EC71987DC395D660F50AFADE7D11A46A4775218D51728F357AB0E463D8C70F5C295A6C5D221F78EE0EFE9E17G3A26348FBF26B23DF28B99D726836D9C00FCBB44ECG15G0DG6DGAA40D8008400E40099GE9G99G399D90C78114F5206D971F5FBC0C6D3961B4630B7C0F5B2E4FFFAD4833F58C1FFF6637F8A77A4D89
	74899DD8C7531C680F548E7447D23A66D85489F1A8C0EDA77A783EB6333F19A15376D0DD9D99DB62D8122A60795856CE5411C83F79B59E5E9381D2BA45B7C9427669D381BF2FF37872F13BEC5273C98E08679C4373311862D3629CE929034594357034B9589DD645C1E81E6B0FBD8F5D3F1D78953FB18C939A3FCAE6C907ED50CDFE60129587276BEFDD95C6A77BF4A41C6627E7D668E848F0F238515EA10B39D8410724EF56F430C49E2F6CDECC53C0EE984FE7BD44CE8C07F302E7962F5469E9F439DDEA182C42
	C57A6FA633AFEA85D0AC27F9EAAF159BB6573078C95FF5406350658E69AD1B74B20DB46C5BC5ABC27A3D5AA95C97339D78ED2379EE3374F4117E08C71F1EAB3374D299531B5DE669B5A27D8ED37EA611FEDB0FD93AC324FF3E57ACBDA452B718720F9369EB3B4C5293C47A7D8E3374A9B21E3518AB4EFECF871C5405ED3DC24136DE55056D70ACBDBF6E037731G890276249B633F64AE7378AFFACC380F04E23CDD5E9E561E53C1D6EE77C85A73F3E1670757GCF5908781604FD532896F8ECA362BBFFC94F6784
	1E2191714D88BBEBBB9EF8A645606377DCF22347D7971BE347D71FEA265F490538089CBE6563A17D463B372BABA00BB8EEFCF31140B5841A3C8197090AD85E4F453C0BEBDEE677A8793EF459FCEC4D3C0EFE7BAC395B38165EC871533A0DEB690F08F8ECA624D5BAF1ADFD5DC9520F47437B9400D400F400DC173520440563721B5661E2A29649E843B263D9E90B6E4B3536583EFC4BE69EABEF36C5464A65B673D839529699AB3F43657A45A5B906FF5FC67AE7B637563EAB48FC746908731BA3736DCD2364FB68
	3279186FFB69794F8A1731CE2894DF63B25609FF0838759024A539F00D69A13AC6D8544368795DD90AAB65B87E7794DA227BB82EA5360FBF53E26E639BDAA2FDBC21455C473735C47A780E9652C7F7C33C58AA68A6895CC9017B8A15C9F0A7DC8E9ACFB37CB7E93E887E10CC94A27E5BB4DF8394574AF17769F0A7B96E07AD244F01A2BB3DFE641D56C2562E3BDC0A425E27D39A17873AA0E10F1F53BC3AFCF6DD3EFFA9F0A705AF6E5A9E691F446DFA7F70F1A6621C6E486ADF52A8797EF859FC2CFEDECDE36B7A
	9EE33D3D1E621BFA0C7536580575368D52D2FAB0CE7E3D78FE547ADEEBC1D52F61DB813D11E39053D3C23F115B5EEB543F016215DE237EEF78D0FF8852E677E23B392F977D32AF8F7D7243DE744BCBD942FF1BCC7C37C96FBF2EA708FAEA950815940CC37EAA4E30CED6B0E6D9CF3F77B8975EA5B74C2B5CB8076E69552F5D13F9F134331195EEE69F1BBBFC6F054DF3B614064DE5F5F7B0B0D2E7F9EB8861595C89EE4C6FAE8F62E638F15E74672EA8F70FE9584B788A3D98EF65FAD056E7C48CD6A3F0ED2A78EE
	03407DC22C8FA6895C4F44BA639C0DD75015F891F7DC2C9D5A846EC5874E5746F9459D4C185F00B3EFA773C74937BB627BE8A3EBDF3F24716CB42F31FE7F0E62E7FA0D755B21E03D1B8DE935BEAC6F59306F19CD82FC32EF78380E556DEF77C656777BFA75751D5FD3B021DBECC2373EAB12EEF3D7A45D0D17A269EE3E94C97704A312EE2EA332BFEDB8AF095F4D3A5F3F9B3C72EB3999D7D487DF43BC99FDBC5FFAB276F8DEF952FCBC2FBE99B91E3FFF52FCBCBFF0D2BF1E333277E5E936BE2F7B1D13F5656DDE
	F2C5F5303E503E1134B73EC6CBC3917E77B5C2CBB4BC06A2BCDFE3DD1A9DEE547DDE6B3A72B659F5C5F5706F3250F9798C68DF667AF0FC3D092E4B4D077722BE441D7161FEE8E59F76D3EF76E976582A7BBD45F765ED7373FF030EA99167B7C7467763D1727D7D3279587892CFE31276BE633873C50AD77A0C634E16FE91D7C15A6CBE1C237E10FE1B3A56EFADE88308830887C882186647B9EA6AC9F24FBA5A9C35596B25DB7F64B08B4CD175CB783C0CC7A36E020655C9FF34F344D543FEE3F2DED43E65265F0A
	14820C3EB8900D6FF3915F94A5D04A69EB227259CD4F34B5F1FAC7D43EDF1B5ED98FF17AF1D179A23FF3CBA00153CF091ECFD3FD53B97D6C28FCAD265F6DCE677439EAB43E1742562A56F03AB27E2C0D4A172A3BE378386EF74F953D8B48F4DC73EF38EF98F4C746C4675D7A18082F3B3B499865734BDD325FAFBB2C2E73E4ACE7F76D56D3FA9B5830DD457D2BA4A719773275975CEF38DF1C7B9987F413GE6824CD1591DG36CF297AAC4364F93E2ABF3FF7FB3F660FAF6BFED7C4FC9F6DDF6F1B06AB2A032F9D
	8C738D3607CF450E53D2CF1947E90F1D0A0C531EB8E59E27BDFDCA9F27317AA107441D62A00E4F47BC08EB9738D2AF5E551CACF01F75A25DD4013B5B0DF4057D08DBA0642D9138100AF4CE017B0D78C61FAAF04F0ABB2253846E2838EFBAD3601E6CC5BA4BG628EAAC8D7A6F0A1A1EFC860FEA36E1C0E95B8F7975EAFCD9538073A10EE0E402D22F7CEABC35602C3A19173D0BA855E07846EC541BBB604E7DBDA491FC366ED72BAA7E9AB9EF940A27E76C43D4BADFB489F6CAE2D5C3D2B4C32E3F745E6B6667EDF
	3A87CA78D2C1D651A04E4F4F0648DE06362FC93F38CE4F13BA58A678FE726D86BE3FF761FC7950203807CA7F9ED29C3C4783CC9E94FF7747275F3FE6671306C2F8BEE97A20797924C512F69EFE7620F64E1FFEABE9D00BA3183CAE8F4A1BEFA2AFA7063C2273467BD4974EC8C276A4AF4AAEB99F7DCE841E252AB89FFEA721BEC2766DE7B5592F0AFCB7C5113DD017EF07C1F6A8C276CF5BB559692A385FE0A2BBA7066C788859AFF7E832E5F1A7E5D214FC6B659B6FE9A4C7482E6A54E41FD5D07634A832730611
	1D9AA17B8E074EA662FE4B1CA8328B2248265F850E107D102C497EE3974A4E3DE0FECFE7B8FB2F39E0BCF337CD0E9CB76B45F7565783FD8D0093E06882BB2B49475B2E70FBA84FF7E9F9DC5E0DF91C90A50F7AF61288B4D3AFE8651F294BA39B7B1E0F397EBB5B200747E0B1790C5FEF6531F7585F7348B4A9039E8A0770BB942F5175DB4B10F1DE3209624B060C731249DE1C17D4C15A44A1DC937CFAC81CDB3CE8369F1C7EBE33F9B1696AF8DFF90BC71CDB3C08FD779E17167E08D873748DAB730E9E0D672810
	19E84A037573473D9A4F9B62BB91E9A29FFFD434741B45DA691A0FB1BD5F23255F2008335CA2FD2BD7A7DF7B4E18C8BF6A5352D7F5E3FA0AC83F074E55194D95994725CCC82F380463524D4D62DBC05A385488D3BA5D2804BA264838CE6E3C0463529F285C84F8CF8618F989FD39DF64E76EA5F33BC7376BACFA370BFD63B0E5A5DB33D1DCE43669108F4AA4BFBC96226324A54EDAD09907E379CE6A6FD6F8D762F07DE2BD35937B3DCD777BBB5EFF431951FF030EFB43665AA1A8773838111CAB1898B657CEG1E
	A9A3627BED58DCFBBA704C9E911FB5CCDFBA7064DEB5923E0DE16BA5EB0027ECC4FC7F9DB6E72E859E5B0878A6046D8BB801E7E8E4798C53B79EF8A60D08EFDD371EAF99F8E60C084FEF7A2DB9BD180F8FF1B1794C47076F4564B3829B87DA685F92CE3B4AB8BE1C2378F9D799470739B20E8F191056F6B5366303E1F6CA817C2C2B0737939B333BDC11E37DC5F16FE56E55EC2C172E319694DF23BF1F5BC07FAE29CA02F4897B26974871E95E7FBC980A1C53BF940A5C8F7916A712AE5993C9773CB7126E2F5EC8
	3A9E93F9DE93F96F85A3697687A3696EB559A77A2E49BE515714C83A490A09DE93F97BCD646D6C0E245B539D39CF54C6E3AC76DB567DFECE3E72FD6B422EAB3FB6F456448E1DE1F6E06D62EE71B7324B201EBA2E413176D9976E87CF3C8647659B695F5F1D856F73GF247E09B7ADA4C3F59E65EE7FCAFA61FF91F7127B03E12B1316FA7313EA10F460895E30CFD46D20A2F99E36CB3EE710833B71016B4866301C5F47D2068DAEBC16935E803822FD6070AF53F3F6039727569BBD7D487BF7FCA3FE34DA2A6059C
	19616B07D55496B5E007C357E20C58B1A0893E818F0947449A87E5AE6FA0179FD65374D97E43B079C9FAD64AD32565D627F74042AFF0DA4845D6D5DACAF16430ED98C2DC5CD425856CB213AA65125BCE2A3410DFE0D225D514947F2E2A85EFF22A52E26D6226AAADA217A5AC64DEC440AFAD93B9ED2047FE527AF7D567BCF1D7CE36145BA0ADE04BF18DD2AED9D3EB10726812DB031423FAFDA19283666AA150A7FDCCA7E7E1D8D5AAE0874E19C6D31A7F882CDFDA9139B2356FF33FF9B469DCFDF136148D66F04A
	2C7CF983E4BF14DA60E6E8AFBFD125D7ECCE59AFDB6CG2D2D2D96723781ACBE2F4B23F263C37109CB690B6C3354F9039EF06D2B64C82845EE5188ACF60FE33B57EFA1D42A7406B5585D8D7E139D3CC2900996051C7F363882C413AAED2C8B7A49F77AAD74CF1686ACBE3F57916CB449C67E810F03E7C27C7C0FB07C9542EA952F475B91G77AE8DEF14D9F72F3E266806DD41ECA97F83E9C117DD71B96D60DAEA46C629200356F532A987AD2D087E415C6D403B1D723E5BA8E5132634CE43F792BCCB05865DA983
	49A2075D5FCBA5108CEEB9ADAD6A72F7F7D0852A14FD7A035320163E116432C6E9316A14794DF7D5DA2D09EEA2DB02BC61DDDA6B2417603F0F7E93FF9B75127E9EF666351DD13F7726DC47460F386B34B3D5ECCF73F5FA0FF7AA60D32F43BDCCEF107C7D60E8F7F72CEE3B2228F625F71F47C16E716C9FD4E56DEE5CFAFA87AC00B4BC111F5D205F046E51717CBFD0CB8788DA6019066CACGGE017GGD0CB818294G94G88G88G350171B4DA6019066CACGGE017GG8CGGGGGGGGGGGGG
	GGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA6ADGGGG
**end of data**/
}
}