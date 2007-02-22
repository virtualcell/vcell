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

//	try{//Temp for Igor
//		if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME)){
//			double[] data = getPdeDataContext().getDataValues();
//			double sum = 0;
//			double wsum = 0;
//			double totalSpace = 0;
//			for(int i = 0; i < data.length; i++){
//				sum+= data[i];
//				double space = getPdeDataContext().getCartesianMesh().calculateMeshElementVolumeFromVolumeIndex(i);
//				wsum+= data[i]*space;
//				totalSpace+= space;
//			}
//			System.out.println("Igor -- var="+getPdeDataContext().getVariableName()+" time="+getPdeDataContext().getTimePoint()+
//				"   weighted-sum="+wsum+" weighted-mean="+(wsum/totalSpace)+
//				"   sum="+sum+" mean="+(sum/data.length));
//		}
//	}catch(Throwable e){
//		System.out.println("Error calculating Igor sum\n"+e.getMessage());
//	}
		
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
				double[] sums = null;
				double[] wsums = null;
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
						sums = tsjrss.getUnweightedSum()[0];
						wsums = tsjrss.getWeightedSum()[0];
						for(int j=0;j<tsjrss.getTimes().length;j+= 1){
							wmeans[j] = tsjrss.getWeightedMean()[0][j]*tsjrss.getTotalSpace()[0];
						}
					}else{
						for(int j=0;j<tsjrss.getTimes().length;j+= 1){
							mins[j] = Math.min(mins[j],tsjrss.getMinimums()[0][j]);
							maxs[j] = Math.max(maxs[j],tsjrss.getMaximums()[0][j]);
							means[j]+= tsjrss.getUnweightedMean()[0][j];
							wmeans[j]+= tsjrss.getWeightedMean()[0][j]*tsjrss.getTotalSpace()[0];
							sums[j]+= tsjrss.getUnweightedSum()[0][j];
							wsums[j]+= tsjrss.getWeightedSum()[0][j];
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
						new double[][] {sums},
						new double[][] {wsums},
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
			symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[4];//max.mean.min,sum
		//for(int i=0;i<symbolTableEntries.length;i+= 1){
			try{
				symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(tsjs.getVariableNames()[0]);
				symbolTableEntries[1] = symbolTableEntries[0];
				symbolTableEntries[2] = symbolTableEntries[0];
				symbolTableEntries[3] = symbolTableEntries[0];
			}catch(cbit.vcell.parser.ExpressionBindingException e){
				e.printStackTrace();
			}
		//}
		}
		cbit.plot.PlotPane plotPane = new cbit.plot.PlotPane();
		plotPane.setPlot2D(
			new cbit.plot.SingleXPlot2D(symbolTableEntries,"Time",
			new String[] {
					"Max",
					(tsjrss.getWeightedMean() != null?"WeightedMean":"UnweightedMean"),
					"Min",
					(tsjrss.getWeightedSum() != null?"WeightedSum":"UnweightedSum")},
			new double[][] {
					tsjrss.getTimes(),
					tsjrss.getMaximums()[0],
					(tsjrss.getWeightedMean() != null?tsjrss.getWeightedMean()[0]:tsjrss.getUnweightedMean()[0]),
					tsjrss.getMinimums()[0],
					(tsjrss.getWeightedSum() != null?tsjrss.getWeightedSum()[0]:tsjrss.getUnweightedSum()[0])},
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
}