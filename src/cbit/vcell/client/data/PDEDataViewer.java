package cbit.vcell.client.data;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.simdata.*;

import javax.swing.*;

import cbit.image.DisplayAdapterService;
import cbit.plot.*;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.MessageEvent;
import cbit.vcell.server.*;
import cbit.vcell.simdata.gui.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.*;

import cbit.vcell.client.*;
import cbit.vcell.export.quicktime.MediaMethods;
import cbit.vcell.export.quicktime.MediaMovie;
import cbit.vcell.export.quicktime.MediaSample;
import cbit.vcell.export.quicktime.MediaTrack;
import cbit.vcell.export.quicktime.VideoMediaChunk;
import cbit.vcell.export.quicktime.VideoMediaSampleRaw;
import cbit.vcell.export.quicktime.atoms.UserDataEntry;
import cbit.vcell.geometry.gui.SurfaceCanvas;
import cbit.vcell.geometry.gui.SurfaceMovieSettingsPanel;
import cbit.util.*;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 6:03:07 AM)
 * @author: Ion Moraru
 */
public class PDEDataViewer extends DataViewer {
	//
	private class ThreadSafeDataJobInfo {
		public AsynchProgressPopup pp;
		TimeSeriesJobResultsAction timeSeriesJobResultsAction;
		public ThreadSafeDataJobInfo(AsynchProgressPopup pp,TimeSeriesJobResultsAction timeSeriesJobResultsAction){
			this.pp = pp;
			this.timeSeriesJobResultsAction = timeSeriesJobResultsAction;
		}
	};
	public interface TimeSeriesJobStarter {
		void startTimeSeriesJob(TimeSeriesJobSpec tsjs,TimeSeriesJobResultsAction resultsAction,boolean bInputBlocking);
	}
	public interface TimeSeriesJobResultsAction extends Runnable{
		void setTimeSeriesJobResults(TimeSeriesJobResults timeSeriesJobResults);
		void setTimeSeriesJobFailed(Throwable e);
	}
	private class PlotSpaceStats implements TimeSeriesJobResultsAction{
		private TimeSeriesJobResults timeSeriesJobResults;
		private Throwable timeSeriesJobFailed;
		public void setTimeSeriesJobResults(TimeSeriesJobResults timeSeriesJobResults){
			this.timeSeriesJobResults = timeSeriesJobResults;
		}
		public void setTimeSeriesJobFailed(Throwable e){
			timeSeriesJobFailed = e;
		}
		public void run() {
			if(timeSeriesJobFailed != null){
				return;//Do Nothing Special
			}
			TSJobResultsSpaceStats tsjrss = (TSJobResultsSpaceStats)timeSeriesJobResults;
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

			cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = null;
			if(tsjrss.getVariableNames().length == 1){
				symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[3/*4*/];//max.mean.min,sum
			//for(int i=0;i<symbolTableEntries.length;i+= 1){
				try{
					if(getSimulation() != null && getSimulation().getMathDescription() != null){
						symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(tsjrss.getVariableNames()[0]);
					}else{
						symbolTableEntries[0] = new SimpleSymbolTable(tsjrss.getVariableNames()).getEntry(tsjrss.getVariableNames()[0]);
					}
					symbolTableEntries[1] = symbolTableEntries[0];
					symbolTableEntries[2] = symbolTableEntries[0];
					//symbolTableEntries[3] = symbolTableEntries[0];
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
						"Min"/*,
						(tsjrss.getWeightedSum() != null?"WeightedSum":"UnweightedSum")*/},
				new double[][] {
						tsjrss.getTimes(),
						tsjrss.getMaximums()[0],
						(tsjrss.getWeightedMean() != null?tsjrss.getWeightedMean()[0]:tsjrss.getUnweightedMean()[0]),
						tsjrss.getMinimums()[0]/*,
						(tsjrss.getWeightedSum() != null?tsjrss.getWeightedSum()[0]:tsjrss.getUnweightedSum()[0])*/},
				new String[] {
					"Statistics Plot for "+tsjrss.getVariableNames()[0]+(tsjrss.getTotalSpace() != null?" (ROI "+(bVolume?"volume":"area")+"="+tsjrss.getTotalSpace()[0]+")":""),
					"Time (s)",
					"[" + tsjrss.getVariableNames()[0] + "]"}));


			showComponentInFrame(plotPane,"Statistics: ("+tsjrss.getVariableNames()[0]+") "+getSimulationModelInfo().getContextName()+" "+getSimulationModelInfo().getSimulationName());
		}
	};
	//
	private HashMap<VCDataJobID, AsynchProgressPopup> jobIDProgressHash =
		new HashMap<VCDataJobID, AsynchProgressPopup>();
	private HashMap<VCDataJobID, TimeSeriesJobResultsAction> jobIDActionHash =
		new HashMap<VCDataJobID, TimeSeriesJobResultsAction>();
	//
	private class StatsJobInfo{
		public String variableName;
		public Double beginTime;
		public Double endTime;
	};
	//
	private JInternalFrame dataValueSurfaceViewerJIF = null;
	private cbit.vcell.geometry.gui.DataValueSurfaceViewer fieldDataValueSurfaceViewer = null;
	private MeshDisplayAdapter.MeshRegionSurfaces meshRegionSurfaces = null;
	private static final String   SHOW_MEMB_SURFACE_BUTTON_STRING = "Show Membrane Surfaces";
	private static final String UPDATE_MEMB_SURFACE_BUTTON_STRING = "Update Membrane Surfaces";
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
	private boolean ivjConnPtoP9Aligning = false;
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

private StatsJobInfo calcStatsGetUserInfo(){
	
	StatsJobInfo statsJobInfo = null;
	//get Volume variable names
	TreeSet<String> volVarNamesTreeSet =
		new TreeSet<String>(new Comparator<String>(){
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}});
	DataIdentifier[] dids = getPdeDataContext().getDataIdentifiers();
	for(int i=0;i<dids.length;i+= 1){
		if(	dids[i].getVariableType().equals(VariableType.VOLUME) ||
			dids[i].getVariableType().equals(VariableType.VOLUME_REGION)){
			if(!dids[i].equals(getPdeDataContext().getDataIdentifier())){
				volVarNamesTreeSet.add(dids[i].getName());
			}
		}
	}
	if(volVarNamesTreeSet.size() > 0){
		String[] volVarnamesArr = volVarNamesTreeSet.toArray(new String[0]);
//		volVarnamesV.copyInto(volVarnamesArr);
		double[] timePoints = getPdeDataContext().getTimePoints();
		Double[] timePointsD = new Double[timePoints.length];
		for(int i=0;i<timePoints.length;i+= 1){
			timePointsD[i] = new Double(timePoints[i]);
		}
		//
		//Show GUI
		//
		JPanel jPanel = new JPanel();
		BoxLayout mainBL = new BoxLayout(jPanel,BoxLayout.Y_AXIS);
		jPanel.setLayout(mainBL);
		JList varList = new JList(volVarnamesArr);
		varList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane jsp = new JScrollPane(varList);
		jPanel.add(jsp);
		JComboBox jcb_time_begin = new JComboBox(timePointsD);
		jcb_time_begin.setMaximumSize(new Dimension(100, 25));
		JComboBox jcb_time_end = new JComboBox(timePointsD);
		jcb_time_end.setSelectedIndex(timePointsD.length-1);
		jcb_time_end.setMaximumSize(new Dimension(100, 25));
		JPanel beginTimePanel = new JPanel();
		beginTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		BoxLayout btBL = new BoxLayout(beginTimePanel,BoxLayout.X_AXIS);
		beginTimePanel.setLayout(btBL);
		JLabel beginLabel = new JLabel("begin Time");
//		beginLabel.setMinimumSize(new Dimension(40,25));
		beginTimePanel.add(jcb_time_begin);
		beginTimePanel.add(beginLabel);
		JPanel endTimePanel = new JPanel();
		endTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		BoxLayout etBL = new BoxLayout(endTimePanel,BoxLayout.X_AXIS);
		endTimePanel.setLayout(etBL);
		JLabel endLabel = new JLabel("end Time");
//		endLabel.setMinimumSize(new Dimension(40,25));
		endTimePanel.add(jcb_time_end);
		endTimePanel.add(endLabel);
		jPanel.add(beginTimePanel);
		jPanel.add(endTimePanel);

		while(true){
			int okCancel =
				PopupGenerator.showComponentOKCancelDialog(this, jPanel, "Choose Volume Variable and times");
			if(okCancel == JOptionPane.OK_OPTION){
				statsJobInfo = new StatsJobInfo();//User created Info
				statsJobInfo.variableName = (String)varList.getSelectedValue();
				statsJobInfo.beginTime = (Double)jcb_time_begin.getSelectedItem();
				statsJobInfo.endTime = (Double)jcb_time_end.getSelectedItem();
				if(statsJobInfo.variableName == null){//User Info Error
					statsJobInfo = null;
					PopupGenerator.showErrorDialog("Volume variable name must be selected from list");					
				}else if(statsJobInfo.beginTime > statsJobInfo.endTime){//User Info Error
					statsJobInfo = null;
					PopupGenerator.showErrorDialog("Desired begintime must be less than or equal to endtime");
				}else{
					break;//User Info OK
				}
			}else{
				statsJobInfo = null;//User cancelled
				break;
			}
		}
//		if(PopupGenerator.showComponentOKCancelDialog(
//			this, jPanel, "Choose Variables and times") ==
//				JOptionPane.OK_OPTION){
//			statsJobInfo = new StatsJobInfo();//User created Info
//			statsJobInfo.variableName = (String)varList.getSelectedValue();
//			statsJobInfo.beginTime = (Double)jcb_time_begin.getSelectedItem();
//			statsJobInfo.endTime = (Double)jcb_time_end.getSelectedItem();
//		}else{
//			statsJobInfo = null;//User cancelled
//		}
	}else{
		statsJobInfo = new StatsJobInfo();//No Volume variables available
	}
	return statsJobInfo;
}
/**
 * Comment
 */
private void calcStatistics(final java.awt.event.ActionEvent actionEvent) {

	new Thread(
		new Runnable(){
			public void run(){
				try{
					calcStatistics2();
				}catch(Throwable e){
					PopupGenerator.showErrorDialog("Error calculating statistics\n"+e.getMessage());
				}
			}
		}
	).start();	
	
}


/**
 * Comment
 */
private void calcStatistics2() {

	BitSet dataBitSet = null;
	String variableName = null;
	double finalBeginTime = getPdeDataContext().getTimePoints()[0];
	double finalEndtime = getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1];
	if(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME_REGION)){

		if(
			PopupGenerator.showComponentOKCancelDialog(this,new JLabel("Confirm -- Values of 1.0 for var '"+getPdeDataContext().getVariableName()+"' will define volume ROI for Statistics"),"Volume ROI")
			== JOptionPane.CANCEL_OPTION){
				return;
		}
		//Count how many indexes are in the ROI
		int vaoiCount=0;
		double[] data = getPdeDataContext().getDataValues();
		for(int i=0;i<data.length;i+= 1){
			if(data[i] == 1.0){
				vaoiCount+= 1;
			}
		}
		if(vaoiCount == 0){
			PopupGenerator.showErrorDialog("No values of 1.0 found in ROI variable "+getPdeDataContext().getVariableName());
			return;
		}
		//Get Time and variable data desired for statistics calculation
		StatsJobInfo statsJobInfo = calcStatsGetUserInfo();
		if(statsJobInfo == null){return;}//User cancelled
		
		//Create dataIndexes array
		if(statsJobInfo.variableName != null){//User selected volume variable
			variableName = statsJobInfo.variableName;
			finalBeginTime = statsJobInfo.beginTime;
			finalEndtime = statsJobInfo.endTime;
			
//			int[] vaoiIndexes = new int[vaoiCount];
//			vaoiCount = 0;
			dataBitSet = new BitSet(data.length);
			for(int i=0;i<data.length;i+= 1){
				if(data[i] == 1.0){
//					vaoiIndexes[vaoiCount] = i;
					dataBitSet.set(i, true);
//					vaoiCount+= 1;
				}else{
					dataBitSet.set(i, false);
				}
			}

//			dataIndexes = vaoiIndexes;
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
					dataBitSet = new BitSet(getPdeDataContext().getDataValues().length);
//					int[] maoiIndexes = new int[maoiCount];
//					maoiCount = 0;
					for(int i=0;i<sl.length;i+= 1){
						if(sl[i] instanceof SpatialSelectionMembrane){
							int[] indexes = ((SpatialSelectionMembrane)sl[i]).getIndexSamples().getSampledIndexes();
							for(int j=0;j<indexes.length;j+= 1){
									dataBitSet.set(indexes[j], true);
//								maoiIndexes[maoiCount] = indexes[j];
//								maoiCount+= 1;
							}
						}
					}

//					dataIndexes = maoiIndexes;
				}
			}else{
				PopupGenerator.showInfoDialog("Use the Membrane Surface Viewer to calculate statistics for 3D membranes");
				return;
			}
	}

	if(dataBitSet != null){
		cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
			new cbit.util.TimeSeriesJobSpec(
				new String[] {variableName},
				new BitSet[] {dataBitSet},
				finalBeginTime,1,finalEndtime,
				true,false,
				VCDataJobID.createVCDataJobID(
						getDataViewerManager().getUser(),
						true));

			startTimeSeriesJob(timeSeriesJobSpec,new PlotSpaceStats(),false);
	}else{
		PopupGenerator.showErrorDialog("Error: Couldn't find indexes to calculate statistics on");
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

private synchronized ThreadSafeDataJobInfo getThreadSafeDataJobInfo(VCDataJobID vcDataJobID){
	if(!jobIDProgressHash.containsKey(vcDataJobID)){
		return null;
	}
	return new ThreadSafeDataJobInfo(jobIDProgressHash.get(vcDataJobID),jobIDActionHash.get(vcDataJobID));
}
private synchronized void dataJobCleanup(VCDataJobID vcDataJobID){
	jobIDProgressHash.remove(vcDataJobID);
	jobIDActionHash.remove(vcDataJobID);
}
private synchronized void dataJobFailed(VCDataJobID vcDataJobID,final Exception failException){
	final ThreadSafeDataJobInfo threadSafeDataJobInfo = getThreadSafeDataJobInfo(vcDataJobID);
	if(threadSafeDataJobInfo != null){
		dataJobCleanup(vcDataJobID);
		SwingUtilities.invokeLater(new Runnable(){public void run(){threadSafeDataJobInfo.pp.stop();}});
		if(threadSafeDataJobInfo.timeSeriesJobResultsAction != null){
			threadSafeDataJobInfo.timeSeriesJobResultsAction.setTimeSeriesJobFailed((failException != null?failException:new Exception("Data Job Failed")));
			new Thread(threadSafeDataJobInfo.timeSeriesJobResultsAction).start();
		}
	}
	new Thread(new Runnable(){
		public void run() {
			PopupGenerator.showErrorDialog(
					"Error executing Data Job:\n"+
					(failException != null?failException.getMessage():"Unknown Reason"));
		}
	}).start();

}
public void dataJobMessage(final DataJobEvent dje) {

	final ThreadSafeDataJobInfo threadSafeDataJobInfo = getThreadSafeDataJobInfo(dje.getVcDataJobID());
	if(threadSafeDataJobInfo == null){
		return;
	}
	if(dje.getEventTypeID() == MessageEvent.DATA_FAILURE){
		dataJobFailed(dje.getVcDataJobID(),dje.getFailedJobException());
	}else{
		if(!(dje.getEventTypeID() == MessageEvent.DATA_COMPLETE)){
			SwingUtilities.invokeLater(new Runnable(){public void run(){threadSafeDataJobInfo.pp.setMessage("Progress ("+NumberUtils.formatNumber(dje.getProgress(),3)+"%)");}});
			return;
		}
		dataJobCleanup(dje.getVcDataJobID());
		SwingUtilities.invokeLater(new Runnable(){public void run(){threadSafeDataJobInfo.pp.stop();}});
		threadSafeDataJobInfo.timeSeriesJobResultsAction.setTimeSeriesJobResults(dje.getTimeSeriesJobResults());
		new Thread(threadSafeDataJobInfo.timeSeriesJobResultsAction).start();
	}
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
 * Gets the simulation property (cbit.vcell.solver.Simulation) value.
 * @return The simulation property value.
 * @see #setSimulation
 */
public cbit.vcell.solver.Simulation getSimulation() {
	return fieldSimulation;
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
		frame.setVisible(true);
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
	
	if(getPdeDataContext().getCartesianMesh() != null && getPdeDataContext().getCartesianMesh().getGeometryDimension() < 3){
		if(getJButtonSurfaces().isVisible()){
			getJButtonSurfaces().setVisible(false);
			return;
		}
	}else{
		if(!getJButtonSurfaces().isVisible()){
			getJButtonSurfaces().setVisible(true);
		}
	}
	
	if(getPdeDataContext().getDataIdentifier() != null && (getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE_REGION))){

			getJButtonSurfaces().setEnabled(true);
	}else{
		getJButtonSurfaces().setEnabled(false);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2006 10:52:52 AM)
 */
private void startTimeSeriesJob(final TimeSeriesJobSpec tsjs,TimeSeriesJobResultsAction resultsAction,boolean bInputBlocking){

	if(tsjs.getVcDataJobID() == null || !tsjs.getVcDataJobID().isBackgroundTask()){
		throw new RuntimeException("Use getTimeSeries(...) if not a background job");
	}
	AsynchProgressPopup pp = null;
	try{
		pp = new AsynchProgressPopup(PDEDataViewer.this,
			"Retrieving Data for '"+tsjs.getVariableNames()[0]+"' jobID("+tsjs.getVcDataJobID().getJobID()+")",
			"Sending request to data server...",
			bInputBlocking,false);
	
		jobIDProgressHash.put(tsjs.getVcDataJobID(),pp);
		if(resultsAction != null){
			jobIDActionHash.put(tsjs.getVcDataJobID(), resultsAction);
		}
		final AsynchProgressPopup finalPP = pp;
		SwingUtilities.invokeLater(new Runnable(){public void run(){finalPP.start();}});
		new Thread(new Runnable(){public void run(){
			try {
				getPdeDataContext().getTimeSeriesValues(tsjs);
			} catch (DataAccessException e) {
				dataJobFailed(tsjs.getVcDataJobID(), e);
			}
		}}).start();
	}catch(Exception e){
		final AsynchProgressPopup finalPP = pp;
		if(pp != null){SwingUtilities.invokeLater(new Runnable(){public void run(){finalPP.stop();}});}
		dataJobFailed(tsjs.getVcDataJobID(), e);
	}
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
	
	final JInternalFrame frame =
		new JInternalFrame(title, true, true, true, true);
	frame.getContentPane().add(comp);
	frame.pack();
	cbit.util.BeanUtils.centerOnComponent(frame,this);
	SwingUtilities.invokeLater(new Runnable(){public void run(){getDataViewerManager().showDataViewerPlotsFrames(new JInternalFrame[] {frame});}});
	
}


/**
 * Comment
 */
private void showKymograph() {
	//Collect all sample curves created by user
	SpatialSelection[] spatialSelectionArr = getPDEDataContextPanel1().fetchSpatialSelections(getPDEDataContextPanel1().isSpatialSampling2D(),false,true);
	final Vector<SpatialSelection> lineSSOnly = new Vector<SpatialSelection>();
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
	
	try {
		VariableType varType = getPdeDataContext().getDataIdentifier().getVariableType();
		if(lineSSOnly.size() > 0){
			int[] indices = null;
			int[] crossingMembraneIndices = null;
			double[] accumDistances = null;
			for (int i = 0; i < lineSSOnly.size(); i++){
				if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
					SpatialSelectionVolume ssv = (SpatialSelectionVolume)lineSSOnly.get(i);
					SpatialSelection.SSHelper ssh = ssv.getIndexSamples(0.0,1.0);
					indices = ssh.getSampledIndexes();
					crossingMembraneIndices = ssh.getMembraneIndexesInOut();
					accumDistances = ssh.getWorldCoordinateLengths();
				}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
					SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)lineSSOnly.get(i);
					SpatialSelection.SSHelper ssh = ssm.getIndexSamples();
					indices = ssh.getSampledIndexes();
					accumDistances = ssh.getWorldCoordinateLengths();
				}
	
				KymographPanel  kymographPanel = new KymographPanel();
				String title = "Kymograph: ";
				if (getSimulationModelInfo()!=null){
					title += getSimulationModelInfo().getContextName()+" "+getSimulationModelInfo().getSimulationName();
				}
				showComponentInFrame(kymographPanel, title);
				SymbolTable symbolTable;
				if(getSimulation() != null && getSimulation().getMathDescription() != null){
					symbolTable = getSimulation().getMathDescription();
				}else{
					symbolTable = new SimpleSymbolTable(new String[] {getPdeDataContext().getDataIdentifier().getName()});
				}
				
				kymographPanel.initDataManager(
					getDataViewerManager().getUser(),
					((ClientPDEDataContext)getPdeDataContext()).getDataManager(),
					getPdeDataContext().getVariableName(),
					getPdeDataContext().getTimePoints()[0],
					1,
					getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1],
					indices,crossingMembraneIndices,accumDistances,true,getPdeDataContext().getTimePoint(),
					symbolTable,
					new PDEDataViewer.TimeSeriesJobStarter(){
						public void startTimeSeriesJob(
								TimeSeriesJobSpec tsjs,
								TimeSeriesJobResultsAction resultsAction,
								boolean inputBlocking) {
							PDEDataViewer.this.startTimeSeriesJob(tsjs, resultsAction, inputBlocking);
						}
					});
			}
		}
	} catch (Exception e) {
		PopupGenerator.showErrorDialog(this.getClass().getName()+".showKymograph: "+e.getMessage());
		e.printStackTrace(System.out);
	}

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
	
	// get plots, ignoring points
	for (int i = 0; i < sl.length; i++){
		try {
			if (! sl[i].isPoint()) {
				final cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[1];
				try{
					if(getSimulation() != null && getSimulation().getMathDescription() != null){
						symbolTableEntries[0] =
							getSimulation().getMathDescription().getEntry(getPdeDataContext().getVariableName());
					}
					if(symbolTableEntries[0] == null){
						symbolTableEntries[0] =
							new VolVariable(getPdeDataContext().getDataIdentifier().getName());
					}
				}catch(cbit.vcell.parser.ExpressionBindingException e){
					e.printStackTrace();
				}
				
				final int finalI = i;
				new Thread(
					new Runnable(){
						public void run(){
							AsynchProgressPopup pp = new AsynchProgressPopup(PDEDataViewer.this, "Fetching data...", "Retrieving spatial series for variable '" + getPdeDataContext().getVariableName(), false, false);
							try{
								pp.start();
								String varName = getPdeDataContext().getVariableName();
								double timePoint = getPdeDataContext().getTimePoint();
								PlotData plotData = getPdeDataContext().getLineScan(varName, timePoint, sl[finalI]);
								PlotPane plotPane = new PlotPane();
								plotPane.setPlot2D(
										new Plot2D(
											symbolTableEntries,
											new String[] { varName },new PlotData[] { plotData },
											new String[] {"Values along curve", "Distance (\u00b5m)", "[" + varName + "]"}));
								String title = "Line Plot: ("+varName+")";
								if (getSimulationModelInfo()!=null){
									title += " "+getSimulationModelInfo().getContextName()+" "+getSimulationModelInfo().getSimulationName();
								}
								showComponentInFrame(plotPane, title);
							}catch(Exception e){
								pp.stop();
								PopupGenerator.showErrorDialog("Show Spatial Plot error:\n"+e.getMessage());
							}finally{
								pp.stop();
							}
						}
					}
				).start();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			PopupGenerator.showErrorDialog("Spatial Plot error:\n"+exc.getMessage());
		}
	}
}


/**
 * Comment
 */
private void showTimePlot() {
	//Collect all sample curves created by user
	SpatialSelection[] spatialSelectionArr = getPDEDataContextPanel1().fetchSpatialSelections(true,true,true);
	final Vector<SpatialSelection> singlePointSSOnly = new Vector<SpatialSelection>();
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
	if(singlePointSSOnly.size() == 0){
		PopupGenerator.showErrorDialog(this, "No Time sampling points match DataType="+getPdeDataContext().getDataIdentifier().getVariableType());
		return;
	}
	
	try {
		VariableType varType = getPdeDataContext().getDataIdentifier().getVariableType();
		if(singlePointSSOnly.size() > 0){
			int[] indices = null;
			//
			indices = new int[singlePointSSOnly.size()];
			final String[] plotNames = new String[singlePointSSOnly.size()];
			final cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[plotNames.length];
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
					if(getSimulation() != null && getSimulation().getMathDescription() != null){
						symbolTableEntries[0] =
							getSimulation().getMathDescription().getEntry(getPdeDataContext().getDataIdentifier().getName());
					}
					if(symbolTableEntries[0] == null){
						symbolTableEntries[0] =
							new VolVariable(getPdeDataContext().getDataIdentifier().getName());
					}
				}catch(cbit.vcell.parser.ExpressionBindingException e){
					e.printStackTrace();
				}
			}
	
			PDEDataViewer.this.startTimeSeriesJob(
					new TimeSeriesJobSpec(
							new String[] {getPdeDataContext().getVariableName()},
							new int[][] {indices},null,
							getPdeDataContext().getTimePoints()[0],
							1,
							getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1],
							VCDataJobID.createVCDataJobID(
									getDataViewerManager().getUser(),
									true)
					),
					new PDEDataViewer.TimeSeriesJobResultsAction(){
						private Throwable timeSeriesJobFailed;
						private TSJobResultsNoStats tsJobResultsNoStats;
						public void setTimeSeriesJobFailed(Throwable e) {
							timeSeriesJobFailed = e;
						}
						public void setTimeSeriesJobResults(TimeSeriesJobResults timeSeriesJobResults) {
							this.tsJobResultsNoStats = (TSJobResultsNoStats)timeSeriesJobResults;
						}
						public void run() {
							if(timeSeriesJobFailed != null){
								PopupGenerator.showErrorDialog("showTimePlot failed:\n"+timeSeriesJobFailed.getMessage());
							}else{
								PlotPane plotPane = new PlotPane();
								plotPane.setPlot2D(
										new SingleXPlot2D(
												symbolTableEntries,
												"Time",
												plotNames,
												tsJobResultsNoStats.getTimesAndValuesForVariable(tsJobResultsNoStats.getVariableNames()[0]),
												new String[] {"Time series for " + getPdeDataContext().getVariableName(), "Time (s)", "[" + tsJobResultsNoStats.getVariableNames()[0] + "]"}));
								String title = "Timeplot: ("+tsJobResultsNoStats.getVariableNames()[0]+")";
								if (getSimulationModelInfo()!=null){
									title += " "+getSimulationModelInfo().getContextName()+" "+getSimulationModelInfo().getSimulationName();
								}
								showComponentInFrame(plotPane, title);							
							}
						}
					},false);
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
	}

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
			private DisplayAdapterService updatedDAS = new DisplayAdapterService(getPDEDataContextPanel1().getdisplayAdapterService1());
			private String updatedVariableName = getPdeDataContext().getVariableName();
			private double updatedTimePoint = getPdeDataContext().getTimePoint();
			private double[] updatedVariableValues = getPdeDataContext().getDataValues();
			private VCDataIdentifier updatedVCDataIdentifier = getPdeDataContext().getVCDataIdentifier();
			public void makeMovie(SurfaceCanvas surfaceCanvas){
				makeSurfaceMovie(surfaceCanvas,
					updatedVariableValues.length,updatedVariableName,updatedDAS,updatedVCDataIdentifier);
			}
			public double getValue(int surfaceIndex,int polygonIndex){
				return updatedVariableValues[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)];
			}
			public String getValueDescription(int surfaceIndex,int polygonIndex){
				return updatedVariableName;
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
			public void plotTimeSeriesData(
					int[][] indices,
					boolean bAllTimes,
					boolean bTimeStats,
					boolean bSpaceStats) throws DataAccessException/*,DataValueSurfaceViewer.UnsynchronizedDataException*/{
				
//				try{
//					if(!updatedVariableName.equals(getPdeDataContext().getVariableName())){
//						throw new DataValueSurfaceViewer.UnsynchronizedDataException("SurfaceViewer variable name not match DataViewer variable name");
//					}
					double beginTime = (bAllTimes?getPdeDataContext().getTimePoints()[0]:updatedTimePoint);
					double endTime = (bAllTimes?getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1]:beginTime);
					String[] varNames = new String[indices.length];
					for(int i=0;i<varNames.length;i+= 1){
						varNames[i] = updatedVariableName;
					}
					final cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
						new cbit.util.TimeSeriesJobSpec(
								varNames,indices,beginTime,1,endTime,bSpaceStats,bTimeStats,
								VCDataJobID.createVCDataJobID(
										getDataViewerManager().getUser(),
										true)
								);

//					new Thread(
//						new Runnable(){
//							public void run(){
								startTimeSeriesJob(timeSeriesJobSpec,new PlotSpaceStats(),false);
//							}
//						}
//					).start();
					//
//					return null;
//				}catch(Throwable e){
//					PopupGenerator.showErrorDialog(e.getMessage());
//					return null;
//				}
			}
	};

	getDataValueSurfaceViewer().setSurfaceCollectionDataInfoProvider(svdp);
	
	dataValueSurfaceViewerJIF.setTitle(
		getSimulationModelInfo().getContextName()+"::"+
		"SIM("+getSimulationModelInfo().getSimulationName()+")::"+
		"VAR("+getPdeDataContext().getVariableName()+")::"+
		"TIME("+getPdeDataContext().getTimePoint()+")");

	
}
private void makeSurfaceMovie(
		final SurfaceCanvas surfaceCanvas,
		final int varTotalNumIndices, final String movieDataVarName, final DisplayAdapterService movieDAS,
		final VCDataIdentifier movieVCDataIdentifier){

	final SurfaceMovieSettingsPanel smsp = new SurfaceMovieSettingsPanel();
	smsp.init(surfaceCanvas.getWidth(), surfaceCanvas.getHeight(), getPdeDataContext().getTimePoints());
	if(PopupGenerator.showComponentOKCancelDialog(dataValueSurfaceViewerJIF, smsp, "Movie Settings for var "+movieDataVarName) != JOptionPane.OK_OPTION){
		return;
	}
	final int beginTimeIndex = smsp.getBeginTimeIndex();
	final int endTimeIndex = smsp.getEndTimeIndex();

	new Thread(new Runnable(){public void run(){
	try {
		final String[] varNames = new String[] {movieDataVarName};
		int[] allIndices = new int[varTotalNumIndices];
		for (int i = 0; i < allIndices.length; i++) {
			allIndices[i] = i;
		}
		final cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
			new cbit.util.TimeSeriesJobSpec(
				varNames,
				new int[][] {allIndices},null,
				getPdeDataContext().getTimePoints()[beginTimeIndex],
				1,
				getPdeDataContext().getTimePoints()[endTimeIndex],
				VCDataJobID.createVCDataJobID(
						getDataViewerManager().getUser(),
						true));
		startTimeSeriesJob(timeSeriesJobSpec,
			new TimeSeriesJobResultsAction(){
				private TimeSeriesJobResults timeSeriesJobResults;
				private Throwable timeSeriesJobFailed;
				public void setTimeSeriesJobResults(TimeSeriesJobResults timeSeriesJobResults) {
					this.timeSeriesJobResults = timeSeriesJobResults;
				}
				public void setTimeSeriesJobFailed(Throwable e) {
					timeSeriesJobFailed = e;
				}
				public void run() {
					if(timeSeriesJobFailed != null){
						return;//Do Nothing Special
					}

					final AsynchProgressPopup pp = new AsynchProgressPopup(PDEDataViewer.this,
							"Creating Movie...","Creating Movie...",
							true,false);
					try{
						SwingUtilities.invokeLater(new Runnable(){public void run(){pp.start();}});
						double[][] timeSeries = ((TSJobResultsNoStats)timeSeriesJobResults).getTimesAndValuesForVariable(movieDataVarName);
	
						int[] singleFrame = new int[surfaceCanvas.getWidth()*surfaceCanvas.getHeight()];
						BufferedImage bufferedImage = new BufferedImage(surfaceCanvas.getWidth(), surfaceCanvas.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
						Graphics2D g2D = bufferedImage.createGraphics();
						VideoMediaChunk[] chunks = new VideoMediaChunk[endTimeIndex - beginTimeIndex + 1];	
						VideoMediaSampleRaw sample;
						int sampleDuration = 0;
//						int timeScale = 1000;
						int timeScale = smsp.getFramesPerSecond();
						int bitsPerPixel = 32;
						boolean isGrayscale = false;
						final double[] allTimes = new double[endTimeIndex-beginTimeIndex+1];
//						double interval = allTimes[endTimeIndex] - allTimes[beginTimeIndex];
//						double duration = interval*100;
						DisplayAdapterService das = new DisplayAdapterService(movieDAS);
						int[][] origSurfacesColors = surfaceCanvas.getSurfacesColors();
						try {
							for (int t = 0; t < allTimes.length; t++) {
								final int finalT = t;
								SwingUtilities.invokeLater(new Runnable(){public void run(){pp.setMessage("Creating Movie... Progress "+NumberUtils.formatNumber(100.0*((double)finalT/(double)allTimes.length),3)+"%");}});
								
								double min = Double.POSITIVE_INFINITY;
								double max = Double.NEGATIVE_INFINITY;
								for (int index = 1; index < timeSeries.length; index++) {
									if(!Double.isNaN(timeSeries[index][t]) && !Double.isInfinite(timeSeries[index][t])){
										min = Math.min(min, timeSeries[index][t]);
										max = Math.max(max, timeSeries[index][t]);
									}
								}
								das.setValueDomain(new Range(min,max));
								if(das.getAutoScale()){
									das.setActiveScaleRange(new Range(min,max));
								}
								int[][] surfacesColors = new int[surfaceCanvas.getSurfaceCollection().getSurfaceCount()][];
								for (int i = 0; i < surfaceCanvas.getSurfaceCollection().getSurfaceCount(); i += 1) {
									cbit.vcell.geometry.surface.Surface surface = surfaceCanvas.getSurfaceCollection().getSurfaces(i);
									surfacesColors[i] = new int[surface.getPolygonCount()];
									for (int j = 0; j < surface.getPolygonCount(); j += 1) {
										int membIndex = meshRegionSurfaces.getMembraneIndexForPolygon(i, j);
										surfacesColors[i][j] = das.getColorFromValue(timeSeries[membIndex+1][t]);
									}
								}
								surfaceCanvas.setSurfacesColors(surfacesColors);
								
								surfaceCanvas.paintImmediately(0, 0, surfaceCanvas.getWidth(), surfaceCanvas.getHeight());
								surfaceCanvas.paint(g2D);
								bufferedImage.getRGB(0, 0, surfaceCanvas.getWidth(), surfaceCanvas.getHeight(), singleFrame, 0, surfaceCanvas.getWidth());
//						ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
//						GIFOutputStream gifOut = new GIFOutputStream(bytesOut);
//						GIFUtils.GIFImage gifImage = new GIFUtils.GIFImage(singleFrame, surfaceCanvas.getWidth());
//						gifImage.write(gifOut);
//						gifOut.close();
//						byte[] data = bytesOut.toByteArray();
//								if (t == endTimeIndex){
//									//Keep the last non-zero duration
//									//sampleDuration = 0;
//								}else{
									sampleDuration = 1;
////									sampleDuration = (int)Math.ceil((allTimes[t + 1] - allTimes[t]) / interval * duration);
//								}
								ByteArrayOutputStream sampleBytes = new ByteArrayOutputStream();
								DataOutputStream sampleData = new DataOutputStream(sampleBytes);
								for (int j=0;j<singleFrame.length;j++){
									sampleData.writeInt(singleFrame[j]);
								}
								sampleData.close();
								byte[] bytes = sampleBytes.toByteArray();
//						sample = new VideoMediaSampleRaw(surfaceCanvas.getWidth(), surfaceCanvas.getHeight() * varNames.length, sampleDuration, bytes, bitsPerPixel, isGrayscale);
								sample = new VideoMediaSampleRaw(surfaceCanvas.getWidth(), surfaceCanvas.getHeight() * varNames.length, sampleDuration,
										new MediaSample.MediaSampleStream(bytes),bytes.length,
										bitsPerPixel, isGrayscale);
								chunks[t] = new VideoMediaChunk(sample);
							}
						}finally{
							surfaceCanvas.setSurfacesColors(origSurfacesColors);
							surfaceCanvas.paintImmediately(0, 0, surfaceCanvas.getWidth(), surfaceCanvas.getHeight());
						}
						MediaTrack videoTrack = new MediaTrack(chunks);
						MediaMovie newMovie = new MediaMovie(videoTrack, videoTrack.getDuration(), timeScale);
						newMovie.addUserDataEntry(new UserDataEntry("cpy", "©" + (new GregorianCalendar()).get(Calendar.YEAR) + ", UCHC"));
						newMovie.addUserDataEntry(new UserDataEntry("des", "Dataset name: " + movieVCDataIdentifier.getID()));
						newMovie.addUserDataEntry(new UserDataEntry("cmt", "Time range: " + getPdeDataContext().getTimePoints()[beginTimeIndex] + " - " + getPdeDataContext().getTimePoints()[endTimeIndex]));
						for (int k=0;k<varNames.length;k++) {
							String entryType;
							if (k < 10) entryType = "v0" + k;
							else entryType = "v" + k;
							newMovie.addUserDataEntry(new UserDataEntry(entryType,
								"Variable name: " + varNames[k] +
								"\nmin: " + das.getValueDomain().getMin() +
								"\nmax: " + das.getValueDomain().getMax()
								));
						}
	//					ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	//					byte[] data = bytesOut.toByteArray();
						JFileChooser jfChooser = new JFileChooser();
						int result = jfChooser.showSaveDialog(PDEDataViewer.this);
						if(result == JFileChooser.APPROVE_OPTION){
							pp.setMessage("Writing Movie to disk...");
							FileOutputStream fos = new FileOutputStream(jfChooser.getSelectedFile());
							DataOutputStream movieOutput = new DataOutputStream(new BufferedOutputStream(fos));
							MediaMethods.writeMovie(movieOutput, newMovie);
							movieOutput.close();
	//						fos.write(data);
							fos.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
						PopupGenerator.showErrorDialog("Error getting movie data\n"+e.getMessage());
					}finally{
						if(pp != null){pp.stop();}
					}
				}
			},true//blocking
		);
		
	} catch (Exception e) {
		e.printStackTrace();
		PopupGenerator.showErrorDialog("Error getting movie data\n"+e.getMessage());
	}
	}}).start();
}
}