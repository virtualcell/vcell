package cbit.vcell.client.data;
import cbit.vcell.simdata.*;
import swingthreads.*;
import javax.swing.*;
import cbit.plot.*;
import cbit.vcell.server.*;
import cbit.vcell.simdata.gui.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import cbit.vcell.client.*;
import cbit.util.*;
import cbit.vcell.client.server.*;
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
	private cbit.vcell.export.ExportMonitorPanel ivjExportMonitorPanel1 = null;
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
	private cbit.vcell.solver.Simulation fieldSimulation = null;

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
					try{
						calcStatistics2();
					}catch(Throwable e){
						PopupGenerator.showErrorDialog("Error calculating statistics\n"+e.getMessage());
					}
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
		cbit.vcell.geometry.surface.SurfaceCollection surfaceCollection = meshRegionSurfaces.getSurfaceCollection();

		//SurfaceNames
		final String[] surfaceNames = new String[meshRegionSurfaces.getSurfaceCollection().getSurfaceCount()];
		for (int i = 0; i < meshRegionSurfaces.getSurfaceCollection().getSurfaceCount(); i++){
			cbit.vcell.solvers.MembraneElement me = //Get the first element, any will do, all have same inside/outside volumeIndex
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

		cbit.vcell.geometry.surface.TaubinSmoothing taubinSmoothing = new cbit.vcell.geometry.surface.TaubinSmoothingWrong();
		cbit.vcell.geometry.surface.TaubinSmoothingSpecification taubinSpec = cbit.vcell.geometry.surface.TaubinSmoothingSpecification.getInstance(.3);
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
public cbit.vcell.export.ExportMonitorPanel getExportMonitorPanel() {
	return getExportMonitorPanel1();
}


/**
 * Return the ExportMonitorPanel1 property value.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.export.ExportMonitorPanel getExportMonitorPanel1() {
	if (ivjExportMonitorPanel1 == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.LineBorderBean();
			ivjLocalBorder1.setLineColor(java.awt.Color.blue);
			cbit.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.TitledBorderBean();
			ivjLocalBorder.setBorder(ivjLocalBorder1);
			ivjLocalBorder.setTitle("Export jobs");
			ivjExportMonitorPanel1 = new cbit.vcell.export.ExportMonitorPanel();
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
 * Gets the simulation property (cbit.vcell.solver.Simulation) value.
 * @return The simulation property value.
 * @see #setSimulation
 */
public cbit.vcell.solver.Simulation getSimulation() {
	return fieldSimulation;
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
		//cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[tsjs.getVariableNames().length];
		cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = null;
		if(tsjs.getVariableNames().length == 1){
			symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[3];//max.mean.min
		//for(int i=0;i<symbolTableEntries.length;i+= 1){
			try{
				symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(tsjs.getVariableNames()[0]);
				symbolTableEntries[1] = symbolTableEntries[0];
				symbolTableEntries[2] = symbolTableEntries[0];
			}catch(cbit.vcell.parser.ExpressionBindingException e){
				e.printStackTrace();
			}
		//}
		}
		cbit.plot.PlotPane plotPane = new cbit.plot.PlotPane();
		plotPane.setPlot2D(
			new cbit.plot.SingleXPlot2D(symbolTableEntries,"Time",
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
 * Sets the simulation property (cbit.vcell.solver.Simulation) value.
 * @param simulation The new value for the property.
 * @see #getSimulation
 */
public void setSimulation(cbit.vcell.solver.Simulation simulation) {
	cbit.vcell.solver.Simulation oldValue = fieldSimulation;
	fieldSimulation = simulation;
	firePropertyChange("simulation", oldValue, simulation);
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
							indices,accumDistances,true,getPdeDataContext().getTimePoint(),
							getSimulation().getMathDescription());
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
						cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[1];
						try{
							symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(getPdeDataContext().getVariableName());
						}catch(cbit.vcell.parser.ExpressionBindingException e){
							e.printStackTrace();
						}
						plotPane.setPlot2D(new Plot2D(symbolTableEntries,new String[] { getPdeDataContext().getVariableName() },new PlotData[] { plotData }, new String[] {"Values along curve", "Distance (\u00b5m)", "[" + getPdeDataContext().getVariableName() + "]"}));
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
					cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[plotNames.length];
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
						try{
							symbolTableEntries[i] = getSimulation().getMathDescription().getEntry(getPdeDataContext().getDataIdentifier().getName());
						}catch(cbit.vcell.parser.ExpressionBindingException e){
							e.printStackTrace();
						}
					}

					double[][] timeSeries = getTimeSeries(indices,startTimeAndStep[RESAMPLE_START_INDEX],(int)startTimeAndStep[RESAMPLE_STEP_INDEX],startTimeAndStep[RESAMPLE_END_INDEX]);
					PlotPane plotPane = new PlotPane();
					plotPane.setPlot2D(new SingleXPlot2D(symbolTableEntries,"Time", plotNames, timeSeries, new String[] {"Time series for " + getPdeDataContext().getVariableName(), "Time (s)", "[" + getPdeDataContext().getVariableName() + "]"}));
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
	cbit.vcell.geometry.surface.SurfaceCollection surfaceCollection = getDataValueSurfaceViewer().getSurfaceCollectionDataInfo().getSurfaceCollection();
	cbit.image.DisplayAdapterService das = getPDEDataContextPanel1().getdisplayAdapterService1();
	final int[][] surfaceColors = new int[surfaceCollection.getSurfaceCount()][];
	final double[][] surfaceDataValues = new double[surfaceCollection.getSurfaceCount()][];
	for(int i=0;i<surfaceCollection.getSurfaceCount();i+= 1){
		cbit.vcell.geometry.surface.Surface surface = surfaceCollection.getSurfaces(i);
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
			public cbit.vcell.geometry.Coordinate getCentroid(int surfaceIndex,int polygonIndex){
				return getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)].getCentroid();
			}
			public float getArea(int surfaceIndex,int polygonIndex){
				return getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)].getArea();
			}
			public cbit.vcell.render.Vect3d getNormal(int surfaceIndex,int polygonIndex){
				return getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)].getNormal();
			}
			public int getMembraneIndex(int surfaceIndex,int polygonIndex){
				return meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex);
			}
			public java.awt.Color getROIHighlightColor(){
				return new java.awt.Color(getPDEDataContextPanel1().getdisplayAdapterService1().getSpecialColors()[cbit.image.DisplayAdapterService.FOREGROUND_HIGHLIGHT_COLOR_OFFSET]);
			}
			public void showComponentInFrame(Component comp,String title){
				PDEDataViewer.this.showComponentInFrame(comp,title);
			}
			public cbit.util.TimeSeriesJobResults getTimeSeriesData(int[][] indices,boolean bAllTimes,boolean bTimeStats,boolean bSpaceStats) throws cbit.vcell.server.DataAccessException{
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
	D0CB838494G88G88GA5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8DD8D45735A837B5293765D67BEAA2CDEDCBABC9EDC3DA52504666FAFBE92FEDFD2D2D5377CCC2EFED1EE9E8E3D25BF0D352F293529C8115D1D4D4D4EC08CE92CDB092C44104A07E202028C4099045C8A283F3E0864E4C99E64E402028F76D1F354F1919B38C121A2F5F73F3FDE74E5A6BE76F35764F5AFF8729F8EB6252A539451234E429447F3D39C412EA26CA52299F4EFB87F15293732C12615F
	0BGEB25DDCB7261D984E91DB7D92CC552341182F85F866F857FE431EE025F6B256F3627FF86FEC4511382E92B6F3D79565175D877201E6574FD3FDC884F55GE500A3CF8D1EC5523BF28947B7F3BC4212DC40E5929A2FDC4AF1B30146C2728A7852CCC6DF8DCF16011BEAEBB8DD47E7E6C8D9BF71652EA2F28CB29126F05E197CE9131EDE5279DFC45767E8BE89CFA62415810E94BECFFA3C7BB5F8563574779E686EF2E995035D3222D4F4ABAE5923D5B86C1A3D363625213181FE1CF549C332CF1296D0F98F4B
	8514DF1212605D79A441FDB9C0F45860F78DC04B13081F52CF704970FBB64002A731DC7BDF2D6379EE3FFC2FB4FF0FBBFFCD14F2E5BD494AD37AA73DDCDE4F2FF3B7D39F9C2FA236EE0734910049G29G738132G1E273EEF59C34AD95F259DD15DEE55D36F759ED69C3E37D5072C406F5ADAC86138B3F2376AF3C892736F1764F2137C0C860C6F2E6B66E3B619D1053ECEBE738DE941BD3766AEB73149029917F38B3318AD36E569B6E13AFF97D5F7C3D6E8DD8C55FDCEB26ACE3D75013C25A63AAD9FFFA02F1D
	4BD18C3AE7E491DFEF98A43E0E075FCB38DC06FF0962979B702CDEFE15569972DE7054BC2B7294560D76551817667C2FC8C53B271A5AA1618771391627D89E96BD951E17F40548AE7EB35339CB64A51B62C78CF816175BE8BDA56F99105619BD4F4A6A57193D241DAE786FF956EC002227217DGB481B8G02CFE39D7B5C1EDDE32CE3ADF61FF6B860D29C5020986F192A0D7014031A4FFE5C63576C1EEE792C5D6732F7A932DF62B4972A088D3AC278BAE95D6F869D47E40F6C33EBAE5583526DEED913FDFEC663
	2ADACBE8FCB2CDEDF339E5D06076CAA8F720EABD71255D2F3D6B05A6AF6B94ACFD380A78E450FE96A402G784D6CF26D8DE22F84A8FF8AC01A30C39615F7D276F9E4253636D9D57B835EB6D292296899E267E7E95FB19F6897BF0DB63E7CA64135C13A728C4A59D93D4EA0274155CD32EF778DE39BFBFE30881EF1C0BF8DE0964042E7307E7C319D6BCF432A3B24727BEFB12DBF8D939E484BF906551B621C70F6D4C66513BC56C1DAA7C0DC8E767BF7855A2F23ED4FFAC6EFF7F1BC1F647734E74C5BA35371236B
	52315C70BB0F37A972FBE994FAE67747EA088F9321DC33G9664206DFB2AED2CAE9F367BE52C3D27FDD071E4076E89164F07148EEE5BBC2EEFB9573F92E08315A7C92BA82DA42D613A091E97G36CAECEC5D8B70B240F600D781F6G6C84F8DD58632FD4CF9664317459F95686G85E08A40AC008C00423F4E33D683F4GCCGC886188F1083306D39F956E6G8DE09A40EC0045G451245DA8760GE0FA927B3DCB2217E795AF43DADE2E8D86FB0617A9DAF90CFE58FB9DBE7E7F99186D1F23360FE6F7E26FCD5C
	66D81722595E4C6E3B42F43E7C8FD06EFF84E0ED3EE3AF3127BC8821DF036C15BD8E786167E95DFB099F787854238A9E9745FBE1BCBB828308DF60553D44BFF6CD73393A821A5CAAEB1A4B532B278FD1DD7EC017D79F8AD157553D44776A1007041B9C77713DE4AC68D6BD9E19F6BE0205275FC447A2D9115D1069A3EA4023B17C275EA071FA3FBC4C4742C9EFAC1348D8268448384AE26094BB29DFDE1F4B8DDD9A991A25459219C394771498EC948FB8924B2741F3BEG9BE706DE46F1E65B252FC9E9973D39E6
	B1D3426D13F22B17D8E8DF5F441FE41CE1F90AFB53BC6FB753B111637DAC2F2BB612F6909612537632DE4AF44224C6BAA37BE168D658A8FE2241E5D754DE163EFA6D86C37A9192E3A807D5D5116D9EA163DE9A03FB9DB211FFC475E860725DFC2CF82916645389799F811882407219DBC352F11C66034491D5D1182BA00D5953B3E834E77292E64F7970DC845010CBE8EE6AABB650C8390C26921E29B47D89118E111FB509276BF578099AB2E67B6501008C45A3C147C9617BBF572C8BCBBB4B6B81CB7FEB8D2987
	0AA1DE7166911D0DEE1C2765F0FD45002F84E88358C26CC569B6FB10EED61ECEB79FA0BB046EF7022EB81F49A94FE7F4AD790C4E361450FDCFC5BA0D534567B33969793C8ED13A1B3CC827407B840024250CAEE329116E78G5265AF33D8CB1769F4F54B989D4B5F33022EFD99536B05E7BCC04AB2E3B9FE61C33A391CAE0353E50750FD590FF4054B995D36650C6E50F2A3DDC0C33A8ECE27F13A78903AE38124CB62F433B85D4265C67BED9D9465AD007282549530720E9430729E32923AC784DD8260D3GACG
	A45EAB0274F2GC6D796C43AF8F8A7F1C592BCC9ECB84BEA147793C1B79F5E97DBC365952EC0F9B943C8E7839C09CF42653172FE719252552CE079EFD9414A6BDDE1AC2FF69969A683FE86409CCEDF3052D88FEAC6102E8C7007GBAD7B23A4415C63DB902EEA1CF4F06A709010B8B0DFAFFF48569369532743AC2C65FD6E8549BF79569628AF5B911F4555704BFG1F82E061F4C52B989D6B8BA7EFA3637B28EB9706DE122DDBDC6DA9607C4DA00B2DCFFC5DC57A358970BE8DA0F595766D3FD8056B93D37AE7C8
	13275E1F97ED5DE5AE4FD766AA3DEFF77EF2A80F751B4ABE2233F2B558FAB5761943EF9967C875FE3F5A6D2253B35E9715559A7BFE06FB3DF685BCFBDCBE3FF69C8667A076DBD535A43FE47E46515CBEE786B623FD7A6C0376E005FF8846605ADABEA8C83816F347A051C5E22D3655E813DB02F49E8F6FC9G33G184C5257DE92B6D75D952EAEF7C5E050DDE16F52AAE407CB2B2DBDEA6F56D4B2C7E332E77561BA517C55E857ABC70DF61D3566CA6E4AA8F6ADDC436CD93EC6376BC47FDF7258FCFDA31D2F9359
	8A24A781ACD8C36ACFEBBF9973C9DDE57A3A2BD05F64CB37CB1D375C96D51F2D083763A2DDDF4D393DDC1F470D7A02101E8240464C7B5D344D040C19B866CDD20DE35EA260593656E2ED8268D80B7C12470C1F657D497AC3D8473E7195E9667278DC33B5B267D793F23535AC4F53566AF9EF4978F52E8300657F3087733F98E8362D033E0966F1015718470EF5AC0F8970CCDE07F97C19E99E195CB5AA4A1D8D74B902E7D0A5EB9A8E175F2B58076B9DF62FA67BDAE55F202BDB5E0D7146F4F5C3B49A8965BFCA65
	137AD73D5EE2D556237CD35E7DA4CFBE55AB7B3461A3CE3B27D72E88FDADA3953F228BA29CFFC5CBC8CAA389F3F7083612F1E89FBC83F47D0394311546C8BEDE52326651656C5BF1167B6BAB6B0D7E2A3B7601293FA62EF166A62DE7FE1A3BDE7717637EDF67866FC7FFFD430B65AEA906582098E3A44F20B1C61A887894007945E8178D83FB60E92741D785FB34483E9E5567169D4CA076A1E82EA4639575B4990D41EC113C1546C9CEF528558BFD12DDE9D1D42D8C0A487D619B40FCAD821D368D182F977A4C72
	553E817B0E9FEFDB436512589405B2192F78C4DB867A248DD816FF7151F80E6523D566A1A52B37A7C35A568260A9598863A7C04BC6641F69A3E9FE4AA8BB4E483DB4A0E5BA1F96BA1DC01FA8F86CB45676AB2C8EB25CA7C3F00C7FA7FE64CF875E22CD483FDBB3EBABAC2D43B40D497B2206721AC0D61C1037B4C07AFD8F784F2E5487DDFE1435AB824F64F883A8E7A6484894F29E8C44EE0F7903315BE353A07A3F2C84E20792747FD3AEB37FA714207EEF8EFE38FA797809A2DEFFCE8E3B55DE1F5D6B048A4472
	D4B804F9CA86FD99A2CF1F73046729F4B30C431BB1CF7E218F172757ECA4CFFC96D556451EBB5858772DA19C7B5AB663D8F45796E37FD0E03F4934FF08FF2CB7B7FEB36B97D2B76B7D43DC689B960A7E611DA07AB987E82A7F066D6C40BB0522DE40ECAF20E88CFF4726955C372ADFB63467BF8551F68A48C87A9B5AEE2A574CF68BA01D156F1F0731FC1900E33236DF42BC15BD8F3867917FE301CFG1C65F934FF45E532CE6090E15A5BF60F3D57B027FDAFEABA137B054B187702D270EBA9667D1EFE333C87CB
	D1EF7E4807737BD31BC99F6963F369B3F28F7CF49AECEA9F41FC45034E59A2DFF31CE6EDA4C764EB4E074C576FFA2BF89BF9DBF6F7796C9E39B5606B31F74BFEBE0619326F937D7076AB68272297A0CEF98173656A0F5E2725DCA1711666F479190CA72EE0596300FF2610F176AA418F72ED0DD3F6374C727C582BDBA3E46F3EF856C0270F93DF3B0A32B3C1EE59962CDF53DFC4FCA7609236203D6FB49D13ACDBB0CFABDE7AF076FE7694D9D76C36AB5D2D9A0CD1FE4D559D5352385679711E70FD3A0CAD96319F
	10ED780D40762F3674113296EE35D8F10D1A614BA83E5400E736C9F121CF4BA1EDB2G1B17D49DA063E7963C97594016G07GBA8162ECB8CF7957DAD21FC65D53BA8E8E73F9E0F48ED94C433D25FDC4EF3FBC4C5F837B16CAFC7DCD58614EC1E207E936487292C861785906F4D6DE6BB0A9D73AAD5C8E6BA9FE11AD5C8E1F977DE5B624F53C086B7D5F6F0B36DF1A7622F91E187D3EFC10542D79C01389D0709244C2G07DEC25B656C0BE93B4620296DD83ECA7BC93EBADFB24F0399B03E57A8DF70BA78585834
	63BD63B9037C35A7F2092D4C66C9B3386C19AF69E393334909FAE213EC40973FEC311683B4GB8DEC61B9C2DAB0FE593EC785C982CCC1ABB56FE7A0417C34B4B78BE9613EFC6989FEB579B0FD6CA11EB8BFE171B8CC5953D8117B89741BB2C9668EA95961776AB4638B48D64E7G94EC03B1E89B0E1DBFD6F16C2C5E06F36E9DB45ED760FD824034ED58CE938F137510D4F837GAC82485F8E6D95208140399DEDBB256EF0AC5B566302FC1BAAFE9E89DFD4C75BB5F946E77333B17962361B597C118BE331B919B7
	901B7BD4055A1C5933F8G6D19309D6DF9094ED3AC703E88C0FA855B7B86F5FC65FAB9A6DF49ABE6FCFF08602B8C23B35777CC845F16307766B0B92C3EECE8A0754581E9F1G89G29G69GD9GA52FE2FDF934EEFF2C7AC2472CB67588601AD4355F242A703C26FA47D7469FC470D5043D573CEAD6D75634C6D95BBA9C50B4431E436B3E7036C856B6DAC02663D5EC0B378F621A60482BD8C7028363AB4F4D91FC6165C98DAB8F4BC3DB9F66E12E48C339EFFC7534B6A6DFFE1919CD35528AA946FA6AA9F9881A
	E0E3502B7AD80F07F6F63B4C7A3CD250D3D906F67E3868731A4A300D3ECE7B3CB8F8CFGC8A94332CB7E7115FDF5CC3EB9E5E6FC16F16AFBA8A65FC2D3FDC95A7874FDABA6DF36293E41407874CD0849D734430C6F5460785ACFC784DFF87BA95BE156FE4AC57BB934836B503A217165419641971E07E0D89ED89B784BE0F89B188CF429GF3F6E09B30F8B01F8BC5BE6F257ACADE9BFB3C467A7978C6524F1783DF93C0A74088C0A240AC00052FE1BFDF331F2CED0E251FE7931548DE1E6573DCF0FCF5C80E49A7
	6DB4637B6F883E3179EF79F0AC7F156CB42BC3BF7722EFAAF722EFBECDE5B9F70E5DB70CEF672571596A2D18FCA326367A59B8757DAAA65F94D3FD53AF0F4FB7F7C770057BA65954B70F7723EFE68B5F740E0CAF8F01883E7038AA47B48F4FB9B18F456518071DD746696718FC4D65E6FCCFC5700D2D4CF9D7E3595DE12A6F1B91FCE35377EFB1750D186A7B5A0B6353F7EF84DF38CF27141B7974B7E25CC806F4F6BE75B63AC71C816F5982B7C4FDDF7A3A45DA7DBA7AFEDB9FD977EA976F95741D44BD6C7DBB2E
	70731B04F6B41FDF8FB09D73A2F4647D1D6493E063C75D016867420E9CB0BF97F67C00793930538756B2BC48B01E024674F387C6BFB7F671005939B166239F50B85E8BB61E72BA7AED73907AED0640558689EEAE3CAF943EDBB3CCFC17A55EB7537749E20C7EF270FA4E620E85328CF50169386F6F2E43D897D85953E9FDAF2E30D84BAAB0BE785A41687ECDB9E86E5F6F9CB4776F2C03237BF77641E87EFD01667701DCB29F7085BC1A4BADD351ECEEA2874919CE08D44E84BCAC0CA1527D61EBC69246C6F30F92
	7FB50BB2DEB834C70A0C019A9DAECD752110B6596EBB5D53A396C6DA3DF2B7667BCCFDF49B1D2FB7375145FAF39B4975235B48DDBF3A0D9E55489EDD44D9C832D0AE611A7C1DEEAB27E95154165D924E717E34034428A673E6437A5545BEE39C6B849B12B370D360B923826358698A1E99C92B40395C8374FCC1B63C976D027A358B5B4E5CD157CD8E6D0A364E7D85BAB7EC5B4556F117887C9D945FE94033755DDDDE3C6324C01AE5977A7E6B07237B6E6E43663E3B77B0B96B63D7833EEEB95CBF5FBBDCA25274
	D87938A63B098B984D0FA93FE677754A917E7DB965E7E951789FBCCC4E28F4399CAE9FBFC5497CFA4DE3746B9E51F7EC0ED8F7B0C2F5E5B49B7F112E11B5D5065B78AF945FD699EE63388134F1A7244D2E44FD14E655182FAAD1D77F77C6FA1E942A3F6C27138C32E44A4B324D0BBC0D1AFAE437304F5606687EFA25415CDF658D517DD555905BDFF58D517DF524A1363F1A9B2235D9D6565F071415780D35231F7AB17EA85B0D6D68EE3AFF5F896FC100493B319DFDBDE2EECC7D381B79F6566EC89F73FD778123
	6EAAD1E74E1B6EFBD8381C0C28723AFDC6DF570832740BBEA1DB14E58A6D93AA77409C9520F38F16E56228FDC2F0CF347A1ACC5BF87C1E707A7AED0ACF5893DEDF2BC41F1084E985D5D8DF5F749B4B7026A85BF24D082F9678DF05605F90783B82C67CDE013FE950E873EAE1735D26B6D72AD8F9632A22597C7810C15E5B242D303E7A44F0781A83B9BFBA85644C28423EFA5790563134AA744BC374CCD5B63C97D5C3DFDD0DFE395FF46D7ED0B54BD7DBF5E47ED8DC71F32A43896941EA6CF7EFED0C5E0E3F50E8
	5E0E27B7C6EF47DFEF0C5D0E6FE90C5E0E6FEB0C5D0E7F23F174F67C6920518FFB843EF25808575B587F0DD85FA6904F6D1898551E378F23BDD32B314EF6DDB26A506BE67EE5E39DFCCB607F7332318E6E95F9FA225FACCFF3F8DE96186409497B6208D15E9BC2DE3E4BCCDEA617133FB71A3C2EABAB8C795EDD0978722BC6BDFA5B7CDD883EC660973EE8443FA5727528E9DBAB6179A90F1A2F3BFB489D9145BE2C86B418976E2567B644DD10C3705E8E9087B0E5AF0B0DD8ECF577909933FBFCF6B7D4999ABCCE
	FC0370FE5A093CD9C0DFG38B200A6G679B68DB155EDFB951E66F6A129DA4965B4D703FE46731649EFBC001B0D2754B273D7C1AA7608B5EA47233447E058564B11D15C3627EB950F4824481A482243D09BA5F89929E376A30ABC216B308EB748B855DDB4749D997FEEA18EEF7FA68623FB8076DA4769E3477358DFBE50E7BCC833927A5624C8A45CE22CC3CED425A1A6568AAE3DB6371AB635FD97002E49A276A9612F05C19AA4E98E73E097DDBAABD9BDCD6E3319E82E82F417EED0692DEA73CB5FABD18D093DA
	A792601D0DAFA7E8FF19DC434697A20B614FD27CEC831E0DBB8B6838C75E9710BC3C05F34FC7D41C0FA63D05F91ACE2E6272BA1BEC783D577B771E8F9A012F535FD09D2C4C1583D8E60BA8F35600DE4EE78D3FEF9E3871E57E52C720A3F2BFA967AD3DEE9555C60BEF365333155BEAD9FDDAA970D5945FE04033FAF6AF0D95493B83526654E2BD7BBE6D934A77C1BC308FF12F083510A6017376A1DD02407D22CF5451FD68AFEFC43B495A27172120AE34BC25F558EE8AE87B282CE379DEAA4A331E629B8CF8FE87
	5F0367385AA1EDD69D6669CB225D5876E31E3EE5E8AB7FEA787D56C750EE4EFC846D26097A21994A5B319F6DF017700DF3BF46F79F9B467EFF66FE6C7FCE53F3111970DEF0GECF6GEDD69D3137D5F3C077DD7301E875728C1BF39F886F7FBE20786001707EAFC3457EEFB224E59D407CAF267BDF6D87A11F87917736702DF690471C5F8E11759B8855F633F16999F116F63660FB5C0B32668B5CC95157AC849C637DB9358F49DF65A124DBC667AA8D705EA6F0EC7EC2F8BB856EF997412540FB1A40453910EE2E
	407D1579255EE2AD2FC7DCF7BF52555463B9454C9E1C0FCC84DCDABD1E597DDD859993757152BE241161125C25ECE6018873F5CDAF16A53B9EFD5D53CB5B58E1E857G1D07712CF170953C7712FC47D724891FB53F3F34306DCAEE70B03FBBF2D8BF4FB541736BDC26B35509BA53A13D68883169EFC24E31923F7873599ABFCBAF8EB98BCEE26A5312B17DE8C8BA496F1DCB0D694110F4E2236D1C1F156D275B31ECB9BFCD1114EFCF087ACD1C76A32CCCA37CB9B963F3CB12F7DE4EE375ECA5ED8F069023621C4B
	63D007A04CA1DF61507C958D47DBDBCE37B6DEBCFD6A6211E6784174A78E994F4A552C7818693DDF8229DC6F9C7E4C59F05B92E65733C3E8D729816DBAE1A85C2E1FDFE2344BF706426DBAAF4F183EE8A85C2ED53946743F85436D7AB037BBDFDF8A52333C3E004C7A0BE9D14FC6523E23A1DAFFD1C63F31525C905E6FD7D1FCE7C3F83FFFDFBF767B9A2425B7E09B7AF75AA66B9AAD5616C6449D70087EC260BE203884F8CFEE443E2E0576C1B361BD9DA0DB60BB5818446F661273408C7F9BCFF81FCEF849B33D
	51FC5FA6BB8ABE7A3871CC048EDC230F761D0E65E314CF206CA87171532AED941A6223AC4F0C765840E834DECE4BEC7CA8DD2B0A835C1423E8771EG7AE266D131A7CA6FA7E5403BF44CE2AD812886E8B90669EF51F4F2CF195CC1A677F2499DD4F24F136F9D06FC77A4036701701B65D10A02C7A86169557C49F4BC902283FD30B20A0CE83E89771155A0EF3D14BEC85B129C543C209BFE1DA259DDF40C58FD7FD08327CDB5497FF37CDF9E17B587E8881039A0E9B78B0E696BDCCCF672E9DD7622BE14DD59E4BE
	4745F351E6329B1AF4594C471F505BE0937AFD095E86856EC55186931A50172B69581E846FE98237196268FDED01CB0CE897E610E6D21EF0C8B221E1BA3EBBA69D31FCED2CAF11ED6FBB226D659F0F4CC78947317AF131AF34FE4CEB51FDB8DB78305CC48E0EB56843C3C0C3GFD58F6DC77A1AB7BA7A24E44F49A64EAA6BAD8FF1DC76F31CC6472B6897CA60ACFB360D9BF7EAE0DBB49FBB624251E90F1999D6B3CA7AD5689A791774320581BBD0975201D461359042699E229E644BBA81E5C25A77773495DF606
	FFDCA5BEA5F42BB9DFAC5F95B5DFEF1F1DC7E3FDEC63E359DF9FCB9DB2562575D22E96DA97B61D516B02C3453A50E41277703A5081B48430AEF81B753A40F42D93E3BC53F52CD55755B5003AC6CCF4AD0921EBF2042E5F8E137C7558953F3EB7790292DA563EB6DD7F479454BF4DC4FFF85F952EBFA5C27F9ED7282EFFF9D757B555053AE61968DA9E264B82B496032E0C88DD9B7AC3F5251C55F5DD6AC7DD19A63A2CE13A725F36D889202E1237435B731FA36E8E0D2DAE67770E0FEF7FF57331FE2246C957B35E
	8E5F2F2927784A374377EB14DE1C9F55C1DA625BF88762336748FAE67EA9184B8254G34GF88162CF61FEC37BB1F2FFE054F30EED2A37D99E14954336BB9F9B1C57DBBEB68F787EF21C07245E77F5E9664D8DD13FAD10F80A79BD0DBF4B9E795C9272E465FB65BCA9DFA624951E36D8AB811AGBAG3453D83E44E37C6E8CB936D02FE976EEA77DBA04B884611207F4B4D900665F47FC0F743393C1CE12107568B172CD2C33763E4813900044AB6E8CCB3F12436B4105087503187DAD6D6B6F916B9633CF0B73D2
	B42E4804777C96919BD29CB923CF6EF2B3DCDA7F07DDEBA17243F1C42FDE3FFF7921F5187579F90667FA6981A5F2DF618F622ED0E98B5BD72806E7D38B3B275059C2E85E75140C2217498E5058CB33F73539B405776B4564F3F938E76BA6B1374915A53E4F60394653432F57707407B71179B5BBA5422ED868FBE20F7611F29D1149BDC0EC97C3B2360B52BB27CB25DB8F652E0D52AEE6F05F4CEF51579E74F2B986C81FFACE76EB6C7D2F7EC89BEE5050BE856FC07FG508450718E1B5B31386CCA40B60A8D35F7
	0CF1D96FF1721B5FA4611FC04034A7AF2D545348E78CB83E6383F28FDC5CCB6784ACEDD6419A03BCFE9D1773A55273D018A62E10CAE847F9970C6B8CC537A93958DF0617E1A2AF4314F7F4BB32F5860F587F1D83E9DB29DE3015247FE375654177A5217761C2237F9A9EEC48FDB90ADE698C1F1F1D517596EA36BC26FB33B837BB9152191E7A0B040D79AEC5547F34B32C7E2743F37199567ED97DAF99357EB3190ECE3D2EBB772CE1F559F0CFE5B7CFFB3013141DA53C2DFAC877465359FE7A6FC33E05125F4A3F
	A9814F16D6DCEFFDC845B2B9DBF13DF223575886985DD362ECE9E26BE8F6AF6CA23FD9BF7945E10C1353DBB1CEFE61B2761DB92D58A7DA6999E6DB9B54F5G85A09EE086C09A40FC00AC0012F7A10686E8FA9779DE20FC8A3C4783A4834C85D88CD0F896E8811A4F323B728ABC634FA25F054BA970A4F2E81BEDE34FA67EACF9B732FFADB760ECED7A6F2C3631475F8494937A3498B262398DD85EBFC9733E0927619C9F7B6B75A67C9B8C3F0D717E5A6B48B70EAD6138D5B1F8C87BDBA36A723707A37BBBCD6C6D
	A61FE57D5DECF826F33F31F8F3C1D0441BE74D6378D5BCFFB45EBC87F5659C0BB709DD4A4F0547360DDEE2378655A3638DBB3E46D1C46430EF896AF76FD85ABF3FCAF471EFA308C456B6EE91ED28665CE8EDE3D7B7794DF8BEBB00BC5EF358171CF1686977A818BEC3244F167574B9AEF177E6D41D0F7668BCF70A7E2B24FDB41E95CEC942D862006177B143EFDF6F0D5F177A548D5541E247E967221F6B19FE4E7CDC4F574E191F057D6639514FFB5FFBAE7AF7C23FF74E781DD0966B7EA75D17E881DFA9G1381
	12GE636E35C5B5CCE3EDF46EEC207474F0D0EDE99577DD17F2E73514B7A4679585F6CBEF81E1CF169D2216E3B09FC44B71EA77E52D4AFC5B25C097344C6BED72FD3E3249C5FF21E54B7C56E21E806BB755EEA235DD8084E695FFD0F584085039B84AE2ECB7430C8AB90FAFAD9AC7413BE5BA8BE4675E15606975B08EEB233A001A343BD3D1A14090D2174B8A71F3BEDF0635CEDBE583B70BC0E176F3BF11F2361BC6ED9FD0A520E407B9400647338E7B567FD633E0EED67E953FD1D6C77AF654EBE4F7239683C9E
	F7E47BED7C1BFB9AADE369FBB0D78468G50GA682A48224824CFDCF4C9BBB4A2468F3A13689C76B054F6E72E82C2C7BB5AC6BA210E33B007BF83BBB491D3C30FD3CE3BE1763303D17ED64B11B5C2421CD5AAF20CD6E21B2A7437B8C00348B98DB5C96A032DB0157A96A799D7D12289FDF68589EAD7FD02BCFC0B6CEF775495DDA1B3DD774937E10F9DCBA68DAFCE174B115697D85DD1F11BAE02C81B884508E20GCCGC8GC886C883C887488448FF9F469600F200BA0096G477BE87B528FF60CE2FBEEB85D78
	A27FDBC24E3085C1DE7C7B23671F790D7DCDGE277C420CFFF9F6B28CD5451228F509F27E89DED0277CE00608768637BBA4C7EE606CC5BC3EDEDE4EC09E3494B83F8BEF6325071C76A73B9703E90A07B830CD99E89B99FDBF491660397C7AF9F63FBBAE43F2E86F81AE3703118E22EB8C75AF9114594C1F846DFE4F7D813AE526FF2DE0EBD8FBD3C0B78953FB18C939A1FC6E6C9A75A201BBC6A129587279BEC5F98C2A7FB8DA41C66C76F2FB350106164C4333DCB96F33115A7C95F2C6BE009BCDE593B0226015C
	901EFF7190BBB19C4E899EDF315E20275965F6E9A1328A2D466FA633AFEA79D1AC4E018FD039A1F38D1B0F74DD0D9E07A1F7C8EFFB55A8B31C06FD3B28FD4438D73B187B2238933FED142A1925170974843FB13D2E1325F70A74AD1AD93AA6523F2A1825CF94698B3DE66953C4FA27EA161EA2529FB1651FAD524B7B4C5253C53A375FACFDF1A71E3518B604EDDD3243B8E047361EA1765D3DF6EC076B683701E640FB9AC03A20DDA54E56E45A4D633F68B161DDA494666D72361076DC5685FDE157D85AF3C2483D
	0E9660F10C09EFE648F92DA070440F096FE7A1E7F99301A7F5CCFCA9A17D719C60D990030F757F3FF1C60F2F9EF3460E2F1E24E7D3E4080B4861D3067B0B134C19541E9E3FAC62389CE711409D773728FE97090AD85E1F0F790DA073B26F98A7DF57F57331B5736768F81459953E16DEC04F4C65F4052F25FFC644E3855DB0C6F763DA7AA768F7B3AD703E98204881FD86C09DC01B834765CF3BC60B09D8A4238F4B0C67DADFF4DF5E5C975B17137A4CE365DB7AA2E3655B7A4CE365443E48D879F6AE57A7AE49B1
	7C1DFD24FFE6F3EB7D3B024CC7C7E2FEE7435C376F0C136F13574D47FC7FAABD7F69F4045709CA0A579C61F5623BA2EE0D03348C872EB1FD127609CDB223E738C9945749F14CE63FD5227BF831925B47CFA966BEFEC609743164B67771B2F7240FD73AC99F5D8B71E23B20AB9238738237014AA438F3AE870D27997EF9778A0EEF1209C2443F68A6799A2238F63C03E6401D67381DEE5267C0111D2A8FF9AB5DE46D3A4725A86C3D1A5238BC508589FB3CC57368725A8D79BEA0F06705AF7676C77A27365F689FD6
	0F9E0AB827BB367A37F81CFC5B4759177D004D7F6470FA7BD30A1FA807575BB65117CD0134EC996364B2B1BEF674D82C5E1E30EF8574C40EC1CCCF97ED9F89BD617A7BA9BE29A7DCFF16E8B7A910D65C0B6D66E9957DD23F947D721C0AFEF9EE8962561A78EFE3087F381EE15493849DA93D980754DD4218E5D1AF46ACFD741B2AB6A744B6GADCE1CC377759B576E493CB85A59C8271359A75E993A774266B95B4BC2663206BB989829333CCD9A413339F31D185F5FEB084BF7613C69CFBDD16E9E533017714D93
	77A16BDCA86BDD8DF11A40B9FC623B8D8237C92C8FE6885CD4314ED85A07383F893AB6016B96EB7571827755FE1C2F4D6E437A76156B5E7F647151B8799E3BEEBED60FEF2671ECCEDFF87D1EC471857D61753BA408752E9852027D629BCAA1670696GBE33FF74380E556D3B55487A7E9D55D85F996E1B8311F4298311F47314C83A79CAA4DD06AB126EB1D7A45D0FCD5A63DC137EF429C7923FD79BFECFF55F78355C149B2A030DB51355686379D4B576F81E281A0F67F7281163791D2A79F84E6A8A0E672C6C2F
	87F4DB3FE1787D6F5A0D37FB7A8D55417AC27BAB243D71B5DA9A0A703F2F91DC2563B19461791A63566D708B436F77BE023A683F21BAD87BBBC85BFD3EE2315A949CDF9D34EED443FB1340CD57F0BFD4D1301FF29A6A48006177539FC1FDD9FFDDFF27F1FCBA1E0EB83FB9B63E02185F77B2672B3FEEBEB63EBCC4E31284A5FC5CF91462131470F147FBC944D510D66C46B96A4E8192AB8461FD8A40CCG8BC086C00E9B67288DDEF24FBA5A9C750C2A522DFFF21885662846A5FCDE46A9915F818B29131EE86708
	B3C71D2BED0B4AB75F74DB11B51C3EB9AA5FFB267ABAB93D96154FE57A9D50091CFEDAD43ED253EF0924F07A59D1792619FE53A11D53AF0E4A3755D41F24B27AE2B59A1F5374EECA99272F0B4A179C32D65542699C7C990C4A97CF7E34927FFD0B6177163F7BF71BA2F53FF9C3F530FEF547F0E4CCD4B1EC0C0978F77C874898657549BD324FA7BBDADD1748D84E6E5A29348F0F879BA62838FFF5D9A573DE367E027B8D7FCB7C6D1659C037G2040EB3116FA591DG36CFF91FE69BA54F55DE63793D0CCB3ABF7E
	E078BDF869467BE6528D5541578E862247E97B86E247E98D8366F15A090148B86D7400F91C766E00B1CEE375634E91F127580B6373F2F1A7A8C160FE6F473B1A0B842EC50C63D982B720A1DD43G627E0F20EB9138B89F52A58A5C43BE24CB96382A81244B9138A9620EE8018FF17728C857AEF045DE24F3885C9BC2DE2A40CD957252846EDA9F5EAFAD72A3EE21B883DBAAF07FC1679C8A3C47895CE13A3E15846F298277D537506197E7DB48F2A5EF1367D552D6BC7210CD7C6D098617DB7610BF58DDD63D372A
	5C36FBEF658EFE57CE7C3D2EA28D7C26617C7C03A0594B5077B569975769F9D2875B84BFCC3E5D4066463962DB29F19A2EC7E4523F07B4935EADG0BB471F707B4637EB5BB1F740391BC1FA4854C4FA7D9A57DBCFCF1C0BF67CF3F1594506388A6EFCBG65D51B484B0BA12F49A00F1F81A2C7D0386CDE8DE537C549AB4E9709ECE7A074CE42C8046C9581DD76E3C27644A832179B72BDADCCF6CA046CBDC33A6CEC1F385F9045A6234936C4487E2720AE7BCEF1CFE4C114FC9B65075F53480C101DB42C4B5E69C5
	59B9D1E4AF9DC5F651E0386C1517F5595F9079AE9DB4173DB20AEC7ADD60885935A33A6C3F08BBDBF5A6320DF9B633F70BC1B67FFECFFF643879DF7DB8EEF6G3D86108810BA484EEA723E409DFA8F657DABFA9E3F6746BC4E0D12C7E3BBD988B4D903FA798B06426F2124457C1B8845C321FA985F4FE37205FF3F1571AD8F791ED2655068EBD18DC3789D8A87DD3FED9F8A1F1738A95EB194BEAF79339F67A5DEC81BBF04EB02FF9A91679603E67B411987194D632E12B746F3BAA04EAD86316F1EFECDCFCF906B
	1BB3C61539F2094E73B7A1F321A98F564F0ADC1DE707D8DBAD9946FC3C132727AF906B22ADA21DDECB63699D224F1FA85227AE55537F1F60CF936977AD53534B44D94AEC11DEC6334CEC3ED941F12972920CA917F0DC3A750C789610BEAEB543144EB0AA218E3B026B640917F0DC1AC4652643FBA6C041E5746593EE4C0F6D32395D235B75A4DDEAE75FB8CCDB4F56EC949719EDBA64E3B2498F9B373730EFD400FC65B20E65C16A0749701EF49957AF7A48B28F2E3598FE6778EFFC7C3A61A3D8A3498E19EB27C0
	39E7DF9E4B390236103FBF108EBC0B4744770510B933B4E231960F0C05EFEB083EB260299B93DFC5485C3E85F89CE362FBB8E46E9B841E78B17159C37A41C460C99D93DFC6083EB94033E0CCFC2F074C19B301A77F4A186C99F3DF51FCFCF8BDA61F797850F35D7B187C1EB35DDFAC3992BEBE94D27C36AB616343AD629CE1A524CD39A23EEB95329F1A8D78422B235B090D59FB3D11E3FD33380BE23B4A467A9AF8B6DFB51E4F1D64E77BE29A7D76B16B1BBCE42A477B1FB5D7A2677445D7A277C37A8311F45EC0
	A4DD2A96C977DDAD126E8D93F97BCC64B57AA369CE78A36932CD76091EB559A78AFAA3692EF8A3691EB111A779A2699ACC76090E1B6C93D98D6B716B8C3FBF2F5C783EB575066AE0E5DE690934C311A7F27FDA7362986E00FABA6DAA0E35333C389FBC7FAA0E4B8F523F3FDBF84DE25D86D0F78D5B50A553EFE6466EB3A64664B36FB33E9F4257F6AD767DA456B71C24B12273DAF81F51C6715A3570BE6357BEF1768652B22EE1BC104E6AEC1C455A9907B6F8472F5721778C3F9F715D787A14F3C3F530FE6B897A
	BDE496B1A964488CDFBFDCCE5B2586F6080B43987117A43C62FC7F66A37118D863204C95DD647243A61A7E2B2BA7A0E2CD7E0334D4135636BA55A19B3F40E9A397DBB5E9B545114336A188F1F1D31316314BCC1A14CFEEBBE952F2FE01C913B6D2D27C3B2AB63C4929C9AB740B1B1AE4A517A5EC64DE045FA72D91B9BDCE0F7DAC9C2C2A4DFBE4FADE2E14FFDCDA4616630ECB79E4CD6D383414AE399D1772B4559BA45F334BB702FFC03ACAA7E7A1D8CDDA498E1CB30D26B47F90D81F34AEF2E52A7E533FFCB079
	E2C3F12E148B66F04A2C7CCB0748FEA835402D50DEFE28C91B5B1C32CF3659815A5B5BED64EF83583C2A4B23F163C37109CB690B6C359DD1839EF06D8B64C8284DEE5389ECF60FE3176A3391AACDFA31B5505B8B7E139D3CC29009B6051C7F36397CC413A63DF2A460A35F693751BFD96A37F9FD2AA350ED120D02C60F03E7C27C7C07B07C8D426A952FCF6D7203FBD707B64AA537EF7AF851242AC02ED4F0D4DA56E3D73CCEBB38161A31D9DA59C56BBA59140316D6C47FE06EAE605DC5F977B5CB3924A9BD2F63
	3B891E25C2036E1601446A307B7A2984124157AEC8569EDFEF97D520C939970EDEG35740DA417B7CBABB4274CEF3EEB52A6DDF48B599264897BE82D131E037FDE7ACF7CED54EB46FB5859B7F5C77D5EDB52CDEC7C18F913FE260AFF63A11774EDD900AF3A9977B08717103F9F9C6D6ECE2B5B2EA81ADD692F77B848BD1E43431A2C5F0D5B19CB647B1106A73234BD39D65ECFA24F7FGD0CB8788E76C8F3B75ACGGE017GGD0CB818294G94G88G88GA5FBB0B6E76C8F3B75ACGGE017GG8CGGGG
	GGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAFADGGGG
**end of data**/
}
}