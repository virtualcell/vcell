package cbit.vcell.client.data;
import cbit.vcell.math.VolVariable;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.DataAccessException;
import org.vcell.util.NumberUtils;
import org.vcell.util.Range;
import org.vcell.util.TSJobResultsNoStats;
import org.vcell.util.TSJobResultsSpaceStats;
import org.vcell.util.TimeSeriesJobResults;
import org.vcell.util.TimeSeriesJobSpec;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.ProgressDialogListener;
import org.vcell.util.gui.SwingDispatcherSync;

import cbit.image.DisplayAdapterService;
import cbit.plot.*;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.MessageEvent;
import cbit.vcell.simdata.gui.*;
import cbit.vcell.simdata.gui.SpatialSelection.SSHelper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;

import cbit.vcell.client.*;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.quicktime.MediaMethods;
import cbit.vcell.export.quicktime.MediaMovie;
import cbit.vcell.export.quicktime.MediaSample;
import cbit.vcell.export.quicktime.MediaTrack;
import cbit.vcell.export.quicktime.VideoMediaChunk;
import cbit.vcell.export.quicktime.VideoMediaSampleRaw;
import cbit.vcell.export.quicktime.atoms.UserDataEntry;
import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.geometry.gui.DataValueSurfaceViewer;
import cbit.vcell.geometry.gui.SurfaceCanvas;
import cbit.vcell.geometry.gui.SurfaceMovieSettingsPanel;
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
			final TSJobResultsSpaceStats tsjrss = (TSJobResultsSpaceStats)timeSeriesJobResults;
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
			final SymbolTableEntry[] finalSymbolTableEntries = symbolTableEntries;
			final boolean finalBVolume = bVolume;
			SwingUtilities.invokeLater(new Runnable(){public void run(){
			cbit.plot.PlotPane plotPane = new cbit.plot.PlotPane();
			plotPane.setPlot2D(
				new cbit.plot.SingleXPlot2D(finalSymbolTableEntries,"Time",
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
					"Statistics Plot for "+tsjrss.getVariableNames()[0]+(tsjrss.getTotalSpace() != null?" (ROI "+(finalBVolume?"volume":"area")+"="+tsjrss.getTotalSpace()[0]+")":""),
					"Time (s)",
					"[" + tsjrss.getVariableNames()[0] + "]"}));


			showComponentInFrame(plotPane,"Statistics: ("+tsjrss.getVariableNames()[0]+") "+
					(getSimulationModelInfo() != null?getSimulationModelInfo().getContextName()+" "+getSimulationModelInfo().getSimulationName():""));
			}});
		}
	};
	//
	private HashMap<VCDataJobID, AsynchProgressPopup> jobIDProgressHash =
		new HashMap<VCDataJobID, AsynchProgressPopup>();
	private HashMap<VCDataJobID, TimeSeriesJobResultsAction> jobIDActionHash =
		new HashMap<VCDataJobID, TimeSeriesJobResultsAction>();
	//
	private class StatsJobInfo{
		public DataIdentifier dataIdentifier;
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
	private NewPDEExportPanel ivjPDEExportPanel1 = null;
	private cbit.vcell.export.ExportMonitorPanel ivjExportMonitorPanel1 = null;
	private JButton ivjKymographJButton = null;
	private boolean ivjConnPtoP9Aligning = false;
	private JButton ivjJButtonSurfaces = null;
	private boolean ivjConnPtoP10Aligning = false;
	private PDEDataContext ivjpdeDataContext1 = null;
	private JButton ivjJButtonSnapshotROI = null;
	private BitSet volumeSnapshotROI;
	private String volumeSnapshotROIDescription;
	private BitSet membraneSnapshotROI;
	private String membraneSnapshotROIDescription;

	private static final String EXPORT_DATA_TABNAME = "Export Data";
	
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
			if (evt.getSource() == PDEDataViewer.this &&
				(evt.getPropertyName().equals(DataViewer.PROP_SIM_MODEL_INFO)
					||
					evt.getPropertyName().equals("pdeDataContext"))
			){
				if(getPdeDataContext() != null && getSimulationModelInfo() != null){
					getPDEDataContextPanel1().setDataInfoProvider(
							new DataViewer.DataInfoProvider(getPdeDataContext().getCartesianMesh(),getSimulationModelInfo()));
				}else{
					getPDEDataContextPanel1().setDataInfoProvider(null);
				}
			}
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
//			if (evt.getSource() == PDEDataViewer.this.getPDEDataContextPanel1() && (evt.getPropertyName().equals("spatialSelection"))) 
//				connPtoP8SetTarget();
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

public void setDataIdentifierFilter(PDEPlotControlPanel.DataIdentifierFilter dataIdentifierFilter){
	getPDEPlotControlPanel1().setDataIdentifierFilter(dataIdentifierFilter);
}

private StatsJobInfo calcStatsGetUserInfo() throws UserCancelException{
	
	StatsJobInfo statsJobInfo = null;
	//Get all variable names and sort by name
	TreeSet<DataIdentifier> dataIdentifierTreeSet =
		new TreeSet<DataIdentifier>(new Comparator<DataIdentifier>(){
			public int compare(DataIdentifier o1, DataIdentifier o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}});
	DataIdentifier[] dataIdentifierArr = getPdeDataContext().getDataIdentifiers();
	dataIdentifierTreeSet.addAll(Arrays.asList(dataIdentifierArr));

	DataIdentifier[] sortedDataIdentiferArr = dataIdentifierTreeSet.toArray(new DataIdentifier[0]);
	String[] volVarnamesArr = new String[sortedDataIdentiferArr.length];
	for (int i = 0; i < volVarnamesArr.length; i++) {
		volVarnamesArr[i] = sortedDataIdentiferArr[i].getName()+"  ("+sortedDataIdentiferArr[i].getVariableType().getTypeName()+")";
	}
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
	beginTimePanel.add(jcb_time_begin);
	beginTimePanel.add(beginLabel);
	JPanel endTimePanel = new JPanel();
	endTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	BoxLayout etBL = new BoxLayout(endTimePanel,BoxLayout.X_AXIS);
	endTimePanel.setLayout(etBL);
	JLabel endLabel = new JLabel("end Time");
	endTimePanel.add(jcb_time_end);
	endTimePanel.add(endLabel);
	jPanel.add(beginTimePanel);
	jPanel.add(endTimePanel);
	jPanel.setPreferredSize(new Dimension(350,200));

	while(true){
		int okCancel =
			PopupGenerator.showComponentOKCancelDialog(this, jPanel, "Calculate Statistics for: (Choose Variable and Times)");
		if(okCancel == JOptionPane.OK_OPTION){
			statsJobInfo = new StatsJobInfo();//User created Info
			statsJobInfo.dataIdentifier = (varList.getSelectedIndex() < 0?null:sortedDataIdentiferArr[varList.getSelectedIndex()]);
			statsJobInfo.beginTime = (Double)jcb_time_begin.getSelectedItem();
			statsJobInfo.endTime = (Double)jcb_time_end.getSelectedItem();
			if(statsJobInfo.dataIdentifier == null){//User Info Error
				statsJobInfo = null;
				PopupGenerator.showErrorDialog("Variable name must be selected from list");					
			}else if(statsJobInfo.beginTime > statsJobInfo.endTime){//User Info Error
				statsJobInfo = null;
				PopupGenerator.showErrorDialog("Desired begintime must be less than or equal to endtime");
			}else{
				break;//User Info OK
			}
		}else{
			throw UserCancelException.CANCEL_GENERIC;
		}
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
//					calcStatistics2();
					roiAction();
				}catch(Throwable e){
					PopupGenerator.showErrorDialog("Error calculating statistics\n"+e.getMessage());
				}
			}
		}
	).start();	
	
}


private BitSet getFillROI(SpatialSelectionVolume spatialSelectionVolume){
	if(spatialSelectionVolume.getCurveSelectionInfo().getCurve()instanceof SinglePoint){
		return null;
	}
	BitSet fillROI = null;
	SSHelper ssHelper = spatialSelectionVolume.getIndexSamples(0, 1);
	if(ssHelper != null &&
		ssHelper.getSampledIndexes()[0] ==
		ssHelper.getSampledIndexes()[ssHelper.getSampledIndexes().length-1]){
		Point projMin = null;
		Point projMax = null;
		Point[] projVolCI = new Point[ssHelper.getSampledIndexes().length];
		for (int i = 0; i < ssHelper.getSampledIndexes().length; i++) {
			CoordinateIndex vCI =
				getPdeDataContext().
				getCartesianMesh().getCoordinateIndexFromVolumeIndex(
					ssHelper.getSampledIndexes()[i]);
			int normalAxis = getPDEDataContextPanel1().getNormalAxis();
			projVolCI[i] =
				new Point(
					(int)Coordinate.convertAxisFromStandardXYZToNormal(
						vCI.x, vCI.y,vCI.z, Coordinate.X_AXIS, normalAxis),
					(int)Coordinate.convertAxisFromStandardXYZToNormal(
						vCI.x, vCI.y,vCI.z, Coordinate.Y_AXIS, normalAxis)
				);
			if(i==0){
				projMin = new Point(projVolCI[i]);
				projMax = new Point(projMin);
			}else{
				if(projVolCI[i].x < projMin.x){
					projMin.x = projVolCI[i].x;
				}
				if(projVolCI[i].y < projMin.y){
					projMin.y = projVolCI[i].y;
				}
				if(projVolCI[i].x > projMax.x){
					projMax.x = projVolCI[i].x;
				}
				if(projVolCI[i].y > projMax.y){
					projMax.y = projVolCI[i].y;
				}
			}
		}
//		System.out.println(projMin+" "+projMax);
		int UNMARKED = 0;
		int BOUNDARY_MARK = 1;
		//Create work area
		int[][] markers = new int[projMax.y-projMin.y+1][projMax.x-projMin.x+1];
		Vector<Vector<Point>> allSeedsV = new Vector<Vector<Point>>();
		allSeedsV.add(null);
		allSeedsV.add(null);
		//Mark boundary
		for (int i = 0; i < projVolCI.length; i++) {
			markers[projVolCI[i].y-projMin.y][projVolCI[i].x-projMin.x] = BOUNDARY_MARK;
		}
		//Create seeds around boundary
		for (int i = 0; i < projVolCI.length; i++) {
			if(projVolCI[i].x-1 >= projMin.x){
				int currentMark = markers[projVolCI[i].y-projMin.y][projVolCI[i].x-projMin.x-1];
				if(currentMark == UNMARKED){
					Vector<Point> newSeedV = new Vector<Point>();
					newSeedV.add(new Point(projVolCI[i].x-1,projVolCI[i].y));
					markers[projVolCI[i].y-projMin.y][projVolCI[i].x-projMin.x-1] = allSeedsV.size();
					allSeedsV.add(newSeedV);
				}
			}
			if(projVolCI[i].x+1 <= projMax.x){
				int currentMark = markers[projVolCI[i].y-projMin.y][projVolCI[i].x-projMin.x+1];
				if(currentMark == UNMARKED){
					Vector<Point> newSeedV = new Vector<Point>();
					newSeedV.add(new Point(projVolCI[i].x+1,projVolCI[i].y));
					markers[projVolCI[i].y-projMin.y][projVolCI[i].x-projMin.x+1] = allSeedsV.size();
					allSeedsV.add(newSeedV);
				}
			}
			if(projVolCI[i].y-1 >= projMin.y){
				int currentMark = markers[projVolCI[i].y-projMin.y-1][projVolCI[i].x-projMin.x];
				if(currentMark == UNMARKED){
					Vector<Point> newSeedV = new Vector<Point>();
					newSeedV.add(new Point(projVolCI[i].x,projVolCI[i].y-1));
					markers[projVolCI[i].y-projMin.y-1][projVolCI[i].x-projMin.x] = allSeedsV.size();
					allSeedsV.add(newSeedV);
				}
			}
			if(projVolCI[i].y+1 <= projMax.y){
				int currentMark = markers[projVolCI[i].y-projMin.y+1][projVolCI[i].x-projMin.x];
				if(currentMark == UNMARKED){
					Vector<Point> newSeedV = new Vector<Point>();
					newSeedV.add(new Point(projVolCI[i].x,projVolCI[i].y+1));
					markers[projVolCI[i].y-projMin.y+1][projVolCI[i].x-projMin.x] = allSeedsV.size();
					allSeedsV.add(newSeedV);
				}
			}
		}
		
//		System.out.println("Seeds");
//		for (int i = 0; i < markers.length; i++) {
//			for (int j = 0; j < markers[i].length; j++) {
//				System.out.print((markers[i][j] < 10?"0":"")+markers[i][j]+" ");
//			}
//			System.out.println();
//		}
		
		//Grow seeds
		for (int i = 2; i < allSeedsV.size(); i++) {
			while(allSeedsV.elementAt(i) != null && allSeedsV.elementAt(i).size()>0){
				Point currentPoint = allSeedsV.elementAt(i).remove(0);
				if(currentPoint.x-1 >= projMin.x){
					int currentMark = markers[currentPoint.y-projMin.y][currentPoint.x-projMin.x-1];
					if(currentMark == UNMARKED){
						allSeedsV.elementAt(i).add(new Point(currentPoint.x-1,currentPoint.y));
						markers[currentPoint.y-projMin.y][currentPoint.x-projMin.x-1] = i;
					}else if(currentMark != BOUNDARY_MARK && currentMark != i){
						for (int j = 0; j < allSeedsV.elementAt(currentMark).size(); j++) {
							if(!allSeedsV.elementAt(i).contains(allSeedsV.elementAt(currentMark).elementAt(j))){
								allSeedsV.elementAt(i).add(allSeedsV.elementAt(currentMark).elementAt(j));
								markers
								[allSeedsV.elementAt(currentMark).elementAt(j).y-projMin.y]
								[allSeedsV.elementAt(currentMark).elementAt(j).x-projMin.x] = i;
							}
						}
						allSeedsV.setElementAt(null, currentMark);
					}
				}
				if(currentPoint.x+1 <= projMax.x){
					int currentMark = markers[currentPoint.y-projMin.y][currentPoint.x-projMin.x+1];
					if(currentMark == UNMARKED){
						allSeedsV.elementAt(i).add(new Point(currentPoint.x+1,currentPoint.y));
						markers[currentPoint.y-projMin.y][currentPoint.x-projMin.x+1] = i;
					}else if(currentMark != BOUNDARY_MARK && currentMark != i){
						for (int j = 0; j < allSeedsV.elementAt(currentMark).size(); j++) {
							if(!allSeedsV.elementAt(i).contains(allSeedsV.elementAt(currentMark).elementAt(j))){
								allSeedsV.elementAt(i).add(allSeedsV.elementAt(currentMark).elementAt(j));
								markers
								[allSeedsV.elementAt(currentMark).elementAt(j).y-projMin.y]
								[allSeedsV.elementAt(currentMark).elementAt(j).x-projMin.x] = i;
							}
						}
						allSeedsV.setElementAt(null, currentMark);
					}
				}
				if(currentPoint.y-1 >= projMin.y){
					int currentMark = markers[currentPoint.y-projMin.y-1][currentPoint.x-projMin.x];
					if(currentMark == UNMARKED){
						allSeedsV.elementAt(i).add(new Point(currentPoint.x,currentPoint.y-1));
						markers[currentPoint.y-projMin.y-1][currentPoint.x-projMin.x] = i;
					}else if(currentMark != BOUNDARY_MARK && currentMark != i){
						for (int j = 0; j < allSeedsV.elementAt(currentMark).size(); j++) {
							if(!allSeedsV.elementAt(i).contains(allSeedsV.elementAt(currentMark).elementAt(j))){
								allSeedsV.elementAt(i).add(allSeedsV.elementAt(currentMark).elementAt(j));
								markers
								[allSeedsV.elementAt(currentMark).elementAt(j).y-projMin.y]
								[allSeedsV.elementAt(currentMark).elementAt(j).x-projMin.x] = i;
							}
						}
						allSeedsV.setElementAt(null, currentMark);
					}
				}
				if(currentPoint.y+1 <= projMax.y){
					int currentMark = markers[currentPoint.y-projMin.y+1][currentPoint.x-projMin.x];
					if(currentMark == UNMARKED){
						allSeedsV.elementAt(i).add(new Point(currentPoint.x,currentPoint.y+1));
						markers[currentPoint.y-projMin.y+1][currentPoint.x-projMin.x] = i;
					}else if(currentMark != BOUNDARY_MARK && currentMark != i){
						for (int j = 0; j < allSeedsV.elementAt(currentMark).size(); j++) {
							if(!allSeedsV.elementAt(i).contains(allSeedsV.elementAt(currentMark).elementAt(j))){
								allSeedsV.elementAt(i).add(allSeedsV.elementAt(currentMark).elementAt(j));
								markers
								[allSeedsV.elementAt(currentMark).elementAt(j).y-projMin.y]
								[allSeedsV.elementAt(currentMark).elementAt(j).x-projMin.x] = i;
							}
						}
						allSeedsV.setElementAt(null, currentMark);
					}
				}				
			}
			allSeedsV.setElementAt(null, i);
		}

		int[] encodeEdge = new int[allSeedsV.size()];
		for (int i = 0; i < encodeEdge.length; i++) {
			encodeEdge[i] = i;
		}
		int c= 0;
		while(true){
			if(c<markers.length){
				encodeEdge[markers[c][0]] = UNMARKED;
				encodeEdge[markers[c][markers[0].length-1]] = UNMARKED;
			}
			if(c<markers[0].length){
				encodeEdge[markers[0][c]] = UNMARKED;
				encodeEdge[markers[markers.length-1][c]] = UNMARKED;
			}
			c++;
			if(c>=markers.length && c>=markers[0].length){
				break;
			}
		}
		encodeEdge[1] = 1;//boundary
		
//		System.out.println("Distinct Areas");
//		for (int i = 0; i < markers.length; i++) {
//			for (int j = 0; j < markers[i].length; j++) {
//				System.out.print((encodeEdge[markers[i][j]] < 10?"0":"")+encodeEdge[markers[i][j]]+" ");
//			}
//			System.out.println();
//		}
		
		//Make BitSet
		fillROI = new BitSet(getPdeDataContext().getDataValues().length);
		CoordinateIndex coordinateIndex = new CoordinateIndex();
		for (int y = 0; y < markers.length; y++) {
			for (int x = 0; x < markers[y].length; x++) {
				if(encodeEdge[markers[y][x]] != UNMARKED){
					coordinateIndex.x = projMin.x+x;
					coordinateIndex.y = projMin.y+y;
					coordinateIndex.z = getPDEDataContextPanel1().getSlice();
					Coordinate.convertCoordinateIndexFromNormalToStandardXYZ(
						coordinateIndex, getPDEDataContextPanel1().getNormalAxis());
//					System.out.println(coordinateIndex);
					int volIndex =
						getPdeDataContext().getCartesianMesh().getVolumeIndex(coordinateIndex);
					fillROI.set(volIndex);
				}
			}
		}

	}
	return fillROI;
}

private void roiAction(){
	BeanUtils.setCursorThroughout(this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	try{
	final String[] ROI_COLUMN_NAMES = new String[] {"ROI source","ROI source name","ROI Description"};
	//final int AUX_INFO_INDEX = ROI_COLUMN_NAMES.length;
	final Vector<Object> auxInfoV = new Vector<Object>();
	
	final boolean isVolume =
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME) ||
		getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME_REGION);
	
	final DefaultTableModel tableModel = new DefaultTableModel(){
	    public boolean isCellEditable(int row, int column) {
	        return false;
	    }
	};
	for (int i = 0; i < ROI_COLUMN_NAMES.length; i++) {
		tableModel.addColumn(ROI_COLUMN_NAMES[i]);
	}
	//Add Snapshot ROI
	if((isVolume?volumeSnapshotROI:membraneSnapshotROI) != null){
		tableModel.addRow(
				new Object[] {
					(isVolume?"Volume":"Membrane")+" Variables and Functions",
					"Snapshot",
					(isVolume?volumeSnapshotROIDescription:membraneSnapshotROIDescription)+
					", (values = 1.0)"
				}
		);
		auxInfoV.add((isVolume?volumeSnapshotROI:membraneSnapshotROI));
	}
	//Add user ROIs
	SpatialSelection[] userROIArr =
		getPDEDataContextPanel1().fetchSpatialSelections(true,false);
	for (int i = 0; userROIArr != null && i < userROIArr.length; i += 1) {
		String descr =
			(isVolume
				?(userROIArr[i] instanceof SpatialSelectionVolume
					?((SpatialSelectionVolume)userROIArr[i]).getCurveSelectionInfo().getCurve().getDescription():null)
				:(userROIArr[i] instanceof SpatialSelectionMembrane
					?((SpatialSelectionMembrane)userROIArr[i]).getSelectionSource().getDescription():null));
		//Add Area User ROI
		BitSet fillBitSet = null;
		if(userROIArr[i] instanceof SpatialSelectionVolume){
			fillBitSet = getFillROI((SpatialSelectionVolume)userROIArr[i]);
			if(fillBitSet != null){
				tableModel.addRow(
						new Object[] {
							"User Defined",
							descr,
							"Area Enclosed Volume ROI"
						}
				);
				auxInfoV.add(fillBitSet);
			}
		}
		//Add Point and Line User ROI
		if(fillBitSet == null){
			tableModel.addRow(
					new Object[] {
						"User Defined",
						descr,
						(userROIArr[i].getCurveSelectionInfo().getCurve() instanceof SinglePoint?"Point":"Line")+
						(isVolume?" Volume ROI ":" Membrane ROI ")
							
					}
			);
			auxInfoV.add(userROIArr[i]);
		}
	}
	//Add sorted Geometry ROI
	HashMap<Integer,?> regionMapSubvolumesHashMap =
		(isVolume
			?getPdeDataContext().getCartesianMesh().getVolumeRegionMapSubvolume()
			:getPdeDataContext().getCartesianMesh().getMembraneRegionMapSubvolumesInOut());
	Set<?> regionMapSubvolumesEntrySet = regionMapSubvolumesHashMap.entrySet();
	Iterator<?> regionMapSubvolumesEntryIter = regionMapSubvolumesEntrySet.iterator();
	TreeSet<Object[]> sortedGeomROITreeSet =
		new TreeSet<Object[]>(
			new Comparator<Object[]>(){
				public int compare(Object[] o1, Object[] o2) {
					int result =
						((String)((Object[])o1[0])[1]).compareToIgnoreCase((String)((Object[])o2[0])[1]);
					if(result == 0){
						result =
							((Integer)((Entry<Integer, ?>)o1[1]).getKey()).compareTo(
								(Integer)((Entry<Integer, ?>)o2[1]).getKey());
					}
					return result;
				}
			}
		);
	while(regionMapSubvolumesEntryIter.hasNext()){
		Entry<Integer,?> regionMapSubvolumesEntry = (Entry<Integer, ?>) regionMapSubvolumesEntryIter.next();
//		((DefaultTableModel)roiTable.getModel()).addRow(
		sortedGeomROITreeSet.add(
				new Object[] {new Object[] {
					"Geometry",
					(isVolume
						?getSimulationModelInfo().getVolumeNamePhysiology(((Integer)regionMapSubvolumesEntry.getValue()))
						:getSimulationModelInfo().getMembraneName(
							((int[])regionMapSubvolumesEntry.getValue())[0],
							((int[])regionMapSubvolumesEntry.getValue())[1])
					),
					(isVolume?"(svID="+regionMapSubvolumesEntry.getValue()+" ":"(")+
					"vrID="+regionMapSubvolumesEntry.getKey()+") Predefined "+(isVolume?"volume":"membrane")+" region"
				},
				regionMapSubvolumesEntry}
		);
//		auxInfoV.add(regionMapSubvolumesEntry);
	}
	Iterator<Object[]> sortedGeomROIIter = sortedGeomROITreeSet.iterator();
	while(sortedGeomROIIter.hasNext()){
		Object[] sortedGeomROIObjArr = (Object[])sortedGeomROIIter.next();
		tableModel.addRow((Object[])sortedGeomROIObjArr[0]);
		auxInfoV.add(sortedGeomROIObjArr[1]);
	}
	SwingUtilities.invokeLater(new Runnable(){public void run(){
	final JTable roiTable = new JTable(tableModel);
	roiTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	ScopedExpressionTableCellRenderer.formatTableCellSizes(roiTable, null, null);
	
	JScrollPane scrollPane = new JScrollPane(roiTable);
	roiTable.setPreferredScrollableViewportSize(new Dimension(500, 250));

	final JPanel mainJPanel = new JPanel();
	BoxLayout mainBL = new BoxLayout(mainJPanel,BoxLayout.Y_AXIS);
	mainJPanel.setLayout(mainBL);
	
	JPanel timeJPanel = new JPanel();
	BoxLayout timeBL = new BoxLayout(timeJPanel,BoxLayout.X_AXIS);
	timeJPanel.setLayout(timeBL);
	double[] timePoints = getPdeDataContext().getTimePoints();
	Double[] timePointsD = new Double[timePoints.length];
	for(int i=0;i<timePoints.length;i+= 1){
		timePointsD[i] = new Double(timePoints[i]);
	}
	final JComboBox jcb_time_begin = new JComboBox(timePointsD);
	jcb_time_begin.setMaximumSize(new Dimension(100, 25));
	final JComboBox jcb_time_end = new JComboBox(timePointsD);
	jcb_time_end.setSelectedIndex(timePointsD.length-1);
	jcb_time_end.setMaximumSize(new Dimension(100, 25));
	timeJPanel.add(new JLabel("Begin Time:"));
	timeJPanel.add(jcb_time_begin);
	timeJPanel.add(new JLabel("End Time:"));
	timeJPanel.add(jcb_time_end);
	timeJPanel.setBorder(new EmptyBorder(4,4,4,4));
	
	JPanel okCancelJPanel = new JPanel();
	BoxLayout okCancelBL = new BoxLayout(okCancelJPanel,BoxLayout.X_AXIS);
	okCancelJPanel.setLayout(okCancelBL);
	final JButton okButton = new JButton("OK");
	okButton.setEnabled(false);
	okButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(((Double)jcb_time_begin.getSelectedItem()).compareTo((Double)jcb_time_end.getSelectedItem()) > 0){
				PopupGenerator.showErrorDialog("Selected 'Begin Time' must be less than or equal to 'End Time'");
				return;
			}
			int[] selectedRows = roiTable.getSelectedRows();
			if(selectedRows != null){
				try {
					BitSet dataBitSet = new BitSet(getPdeDataContext().getDataValues().length);
					//int SNAPSHOT_INDEX = 0;
					for (int i = 0; i < selectedRows.length; i++) {
						Object auxInfo = auxInfoV.elementAt(selectedRows[i]);
						if(auxInfo instanceof BitSet){
							dataBitSet.or((BitSet)auxInfo);
						}else if(auxInfo instanceof SpatialSelectionMembrane){
							int[] roiIndexes =
								((SpatialSelectionMembrane)auxInfo).getIndexSamples().getSampledIndexes();
							for (int j = 0; j < roiIndexes.length; j += 1) {
								dataBitSet.set(roiIndexes[j], true);
							}
						}else if(auxInfo instanceof SpatialSelectionVolume){
							int[] roiIndexes =
								((SpatialSelectionVolume)auxInfo).getIndexSamples(0,1).getSampledIndexes();
							for (int j = 0; j < roiIndexes.length; j += 1) {
								dataBitSet.set(roiIndexes[j], true);
							}
						}else if (auxInfo instanceof Entry){
							if(isVolume){
								int volumeRegionID = (Integer)((Entry<Integer, Integer>)auxInfo).getKey();
								dataBitSet.or(getPdeDataContext().getCartesianMesh().getVolumeROIFromVolumeRegionID(volumeRegionID));
							}else{
								int membraneRegionID = (Integer)((Entry<Integer, int[]>)auxInfo).getKey();
								dataBitSet.or(getPdeDataContext().getCartesianMesh().getMembraneROIFromMembraneRegionID(membraneRegionID));
							}
						}else if(auxInfo instanceof BitSet){
							dataBitSet.or((BitSet)auxInfo);
						}else{
							throw new Exception("ROI table, Unknown data type: "+auxInfo.getClass().getName());
						}
					}
					org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec =
						new org.vcell.util.TimeSeriesJobSpec(
							new String[] {getPdeDataContext().getDataIdentifier().getName()},
							new BitSet[] {dataBitSet},
							((Double)jcb_time_begin.getSelectedItem()).doubleValue(),
							1,
							((Double)jcb_time_end.getSelectedItem()).doubleValue(),
							true,false,
							VCDataJobID.createVCDataJobID(
									getDataViewerManager().getUser(),
									true));

					startTimeSeriesJob(timeSeriesJobSpec,new PlotSpaceStats(),false);
				} catch (Exception e1) {
					e1.printStackTrace();
					PopupGenerator.showErrorDialog("ROI Error.\n"+e1.getMessage());
				}
			}
			BeanUtils.dispose(mainJPanel);
		}});
	okCancelJPanel.add(okButton);
	roiTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e) {
					if(roiTable.getSelectedRows() != null && roiTable.getSelectedRows().length > 0){
						okButton.setEnabled(true);
					}else{
						okButton.setEnabled(false);
					}
				}
			}
	);
	
	JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			BeanUtils.dispose(mainJPanel);
		}});
	okCancelJPanel.add(cancelButton);
	okCancelJPanel.setBorder(new EmptyBorder(4,4,4,4));
	
//	JPanel descriptionJPanel = new JPanel();
//	BoxLayout descriptionBL = new BoxLayout(descriptionJPanel,BoxLayout.Y_AXIS);
//	descriptionJPanel.setLayout(descriptionBL);
//	descriptionJPanel.add(new JLabel("Statistics will be calculated for all time points selected below."));
//	descriptionJPanel.add(new JLabel("Select 1 or more Regions of Interest (ROI) below."));
//	descriptionJPanel.add(new JLabel("Multiple ROI selections will be merged into 1 composite ROI."));
//	mainJPanel.add(descriptionJPanel);

	mainJPanel.add(timeJPanel);
	mainJPanel.add(scrollPane);
	mainJPanel.add(okCancelJPanel);

	showComponentInFrame(mainJPanel,
		"Calculate "+(isVolume?"volume":"membrane")+" statistics for '"+getPdeDataContext().getVariableName()+"'."+
		"  Choose times and 1 or more ROI(s).");

//	int result = DialogUtils.showComponentOKCancelDialog(requester, scrollPane, title);
//	if(result != JOptionPane.OK_OPTION){
//		throw UserCancelException.CANCEL_GENERIC;
//	}
	}});
	}finally{
		BeanUtils.setCursorThroughout(this, Cursor.getDefaultCursor());
	}

}
/**
 * Comment
 */
//private void calcStatistics2() {
//
//	final String[] ROI_COLUMN_NAMES = new String[] {"ROI source","ROI source name","ROI Description"};
//	BitSet dataBitSet = null;
//	StatsJobInfo statsJobInfo = null;
//	
//	//Get Time and variable data desired for statistics calculation
//	try {
//		statsJobInfo = calcStatsGetUserInfo();
//	}catch (UserCancelException e) {
//		return;
//	}
//
//	if(statsJobInfo.dataIdentifier.getVariableType().equals(VariableType.VOLUME) ||
//		statsJobInfo.dataIdentifier.getVariableType().equals(VariableType.VOLUME_REGION)){
//
////		final int DATA_DEFINED_ROI_INDEX = 0;
////		HashMap<Integer, Integer> volRegionMapSubVolHashMap =
////			getPdeDataContext().getCartesianMesh().getVolumeRegionMapSubvolume();
////		Object[][] roiS = new Object[volRegionMapSubVolHashMap.size()+1][ROI_COLUMN_NAMES.length];
////		roiS[DATA_DEFINED_ROI_INDEX][0] = "Volume Variables and Functions";
////		roiS[DATA_DEFINED_ROI_INDEX][1] = "Snapshot";
////		roiS[DATA_DEFINED_ROI_INDEX][2] = volumeSnapshotROIDescription;
////		Set<Entry<Integer, Integer>> volRegMapSubvolEntrySet = volRegionMapSubVolHashMap.entrySet();
////		Iterator<Entry<Integer, Integer>> iter = volRegMapSubvolEntrySet.iterator();
////		int geomVolROICount = 0;
////		int[] tableIndexMapVolRegionID = new int[roiS.length];
////		while(iter.hasNext()){
////			Entry<Integer, Integer> volRegMapSubvolEntry = iter.next();
////			roiS[geomVolROICount+1][0] = "Geometry";
////			roiS[geomVolROICount+1][1] = getSimulationModelInfo().getVolumeNamePhysiology(volRegMapSubvolEntry.getValue());//+"  (region ID="+volRegionMapSubVolArr[i].volumeRegionID+")";
////			roiS[geomVolROICount+1][2] = "Predefined volume region - (Region ID="+volRegMapSubvolEntry.getKey()+")";
////			tableIndexMapVolRegionID[geomVolROICount+1] = volRegMapSubvolEntry.getKey();
////			geomVolROICount+= 1;
////		}
//		//----------------------
////		roiS[0][1] = new String[] {"var1","var2","var3","var4"};
////		showTableList(this, "test",ROI_COLUMN_NAMES, roiS,ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,tableIndexMapVolRegionID);
//		showTableList();
//		//----------------------
////		int[] selArr = null;
////		try {
////			selArr = PopupGenerator.showComponentOKCancelTableList(this,
////					"Statistics Region Of Interest (Select 1 or more)",
////					ROI_COLUMN_NAMES, roiS,
////					ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
////		} catch (UserCancelException e) {
////			return;
////		}
////		
////		boolean hasDataDefinedROI = false;
////		if (selArr != null && selArr.length > 0) {
////			for (int i = 0; i < selArr.length; i++) {
////				if(selArr[i] == DATA_DEFINED_ROI_INDEX){
////					hasDataDefinedROI = true;
////					break;
////				}
////			}
////		}else{
////			PopupGenerator.showErrorDialog("No ROI selection defined");
////			return;
////		}
////		if (hasDataDefinedROI) {
////			//Count how many indexes are in the ROI
////			int vaoiCount = 0;
////			double[] data = getPdeDataContext().getDataValues();
////			for (int i = 0; i < data.length; i += 1) {
////				if (data[i] == 1.0) {
////					vaoiCount += 1;
////				}
////			}
////			if (vaoiCount == 0) {
////				PopupGenerator.showErrorDialog("No values of 1.0 found in Data defined ROI for variable "+ getPdeDataContext().getVariableName());
////				return;
////			}
////		}
////		//
////		//Data ROI
////		//
////		if(hasDataDefinedROI){
////			dataBitSet = new BitSet(getPdeDataContext().getDataValues().length);
////			for(int i=0;i<getPdeDataContext().getDataValues().length;i+= 1){
////				if(getPdeDataContext().getDataValues()[i] == 1.0){
////					dataBitSet.set(i, true);
////				}else{
////					dataBitSet.set(i, false);
////				}
////			}
////
////		}
////		//
////		//Geometric ROI
////		//
////		for (int i = 0; i < selArr.length; i++) {
////			if(selArr[i] != DATA_DEFINED_ROI_INDEX){
////				if(dataBitSet == null){
////					dataBitSet = new BitSet(getPdeDataContext().getDataValues().length);
////				}
////				int volumeRegionID = tableIndexMapVolRegionID[selArr[i]];
////				dataBitSet.or(getPdeDataContext().getCartesianMesh().getVolumeROIFromVolumeRegionID(volumeRegionID));
////			}
////		}
//
//	}else if(statsJobInfo.dataIdentifier.getVariableType().equals(VariableType.MEMBRANE) ||
//		statsJobInfo.dataIdentifier.getVariableType().equals(VariableType.MEMBRANE_REGION)){
//		if(getPdeDataContext().getCartesianMesh().getGeometryDimension() < 3){
//
//			SpatialSelection[] tempSelArr = getPDEDataContextPanel1().fetchSpatialSelections(getPDEDataContextPanel1().isSpatialSampling2D(),true,false);
//			int MEMBR_SPATIAL_SELECT_COUNT = 0;
//			Vector<SpatialSelectionMembrane> membrSpatialSelV = new Vector<SpatialSelectionMembrane>();
//			for (int i = 0; tempSelArr != null && i < tempSelArr.length; i += 1) {
//				if (tempSelArr[i] instanceof SpatialSelectionMembrane) {
//					membrSpatialSelV.add((SpatialSelectionMembrane)tempSelArr[i]);
//					MEMBR_SPATIAL_SELECT_COUNT+= 1;
//				}
//			}
//			
////			HashMap<Integer, int[]> membrRegionMapSubvolumesHashMap =
////				getPdeDataContext().getCartesianMesh().getMembraneRegionMapSubvolumesInOut();
////			Object[][] roiS = new Object[membrRegionMapSubvolumesHashMap.size()+MEMBR_SPATIAL_SELECT_COUNT][ROI_COLUMN_NAMES.length];
////			for (int i = 0; i < MEMBR_SPATIAL_SELECT_COUNT; i++) {
//////				CurveSelectionInfo csi = membrSpatialSelV.elementAt(i).getCurveSelectionInfo();
//////				Curve samplecurve = csi.getCurve();
//////				Coordinate beginCoord = samplecurve.getCoordinate(csi.getCurveUfromSelectionU(0.0));
////				roiS[i][0] = "User Defined";
////				roiS[i][1] = membrSpatialSelV.elementAt(i).getSelectionSource().getDescription();
////				roiS[i][2] = "Membrane ROI";			
////			}
////			Set<Entry<Integer, int[]>> membrRegionMapSubvolumesEntrySet = membrRegionMapSubvolumesHashMap.entrySet();
////			Iterator<Entry<Integer, int[]>> iter = membrRegionMapSubvolumesEntrySet.iterator();
////			int geomMembrROICount = 0;
////			int[] tableIndexMapMembRegionID = new int[roiS.length];
////			while(iter.hasNext()){
////				Entry<Integer, int[]> membrRegionMapSubvolumesEntry = iter.next();
////				roiS[geomMembrROICount+MEMBR_SPATIAL_SELECT_COUNT][0] = "Geometry";
////				roiS[geomMembrROICount+MEMBR_SPATIAL_SELECT_COUNT][1] = getSimulationModelInfo().getMembraneName(membrRegionMapSubvolumesEntry.getValue()[0], membrRegionMapSubvolumesEntry.getValue()[1]);
////				roiS[geomMembrROICount+MEMBR_SPATIAL_SELECT_COUNT][2] = "Predefined membrane region - (Region ID="+membrRegionMapSubvolumesEntry.getKey()+")";
////				tableIndexMapMembRegionID[geomMembrROICount+MEMBR_SPATIAL_SELECT_COUNT] = membrRegionMapSubvolumesEntry.getKey();
////				geomMembrROICount+= 1;
////			}
//			
//			showTableList();
////			showTableList(this, "test",ROI_COLUMN_NAMES, roiS,ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,tableIndexMapMembRegionID);
//
////			int[] selArr = null;
////			try {
////				selArr = PopupGenerator.showComponentOKCancelTableList(this,
////						"Statistics Region Of Interest (Select 1 or more)",
////						ROI_COLUMN_NAMES, roiS,
////						ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
////			} catch (UserCancelException e) {
////				return;
////			}
////
//////			if (MEMBR_SPATIAL_SELECT_COUNT > 0) {
//////				dataBitSet = new BitSet(getPdeDataContext().getDataValues().length);
//////				for (int i = 0; i < membrSpatialSelV.size(); i += 1) {
//////					int[] indexes = membrSpatialSelV.elementAt(i).getIndexSamples().getSampledIndexes();
//////					for (int j = 0; j < indexes.length; j += 1) {
//////						dataBitSet.set(indexes[j], true);
//////					}
//////				}
//////			}
////			//
////			//Geometric ROI
////			//
////			dataBitSet = new BitSet(getPdeDataContext().getDataValues().length);
////			for (int i = 0; i < selArr.length; i++) {
////				if(selArr[i] >= MEMBR_SPATIAL_SELECT_COUNT){
//////					if(dataBitSet == null){
//////						dataBitSet = new BitSet(getPdeDataContext().getDataValues().length);
//////					}
////					int membrRegionID = tableIndexMapMembRegionID[selArr[i]];
////					dataBitSet.or(getPdeDataContext().getCartesianMesh().getMembraneROIFromMembraneRegionID(membrRegionID));
////				}else{
////					int[] indexes = membrSpatialSelV.elementAt(selArr[i]).getIndexSamples().getSampledIndexes();
////					for (int j = 0; j < indexes.length; j += 1) {
////						dataBitSet.set(indexes[j], true);
////					}
////
////				}
////			}
//
//		}else{
//			PopupGenerator.showInfoDialog("Use the Membrane Surface Viewer to calculate statistics for 3D membranes");
//			return;
//		}
//	}
//
//	if(dataBitSet != null){
//		cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
//			new cbit.util.TimeSeriesJobSpec(
//				new String[] {statsJobInfo.dataIdentifier.getName()},
//				new BitSet[] {dataBitSet},
//				statsJobInfo.beginTime,1,statsJobInfo.endTime,
//				true,false,
//				VCDataJobID.createVCDataJobID(
//						getDataViewerManager().getUser(),
//						true));
//
//			startTimeSeriesJob(timeSeriesJobSpec,new PlotSpaceStats(),false);
//	}else{
//		PopupGenerator.showErrorDialog("Error: Couldn't find indexes to calculate statistics on");
//	}
//	
//	
//}


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


///**
// * connPtoP8SetTarget:  (PDEDataContextPanel1.spatialSelection <--> PDEExportPanel1.selectedRegion)
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connPtoP8SetTarget() {
//	/* Set the target from the source */
//	try {
//		if (ivjConnPtoP8Aligning == false) {
//			// user code begin {1}
//			// user code end
//			ivjConnPtoP8Aligning = true;
//			getPDEExportPanel1().setSelectedRegion(getPDEDataContextPanel1().getSpatialSelection());
//			// user code begin {2}
//			// user code end
//			ivjConnPtoP8Aligning = false;
//		}
//	} catch (java.lang.Throwable ivjExc) {
//		ivjConnPtoP8Aligning = false;
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}


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
	if(!(failException instanceof UserCancelException)){
		new Thread(new Runnable(){
			public void run() {
				PopupGenerator.showErrorDialog(
						"Error executing Data Job:\n"+
						(failException != null?failException.getMessage():"Unknown Reason"));
			}
		}).start();
	}

}
public void dataJobMessage(final DataJobEvent dje) {

	new Thread(new Runnable(){public void run(){
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
	}}).start();
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
			if(getSimulationModelInfo() != null){
				surfaceNames[i] =
				getSimulationModelInfo().getMembraneName(
					getPdeDataContext().getCartesianMesh().getSubVolumeFromVolumeIndex(me.getInsideVolumeIndex()),
					getPdeDataContext().getCartesianMesh().getSubVolumeFromVolumeIndex(me.getOutsideVolumeIndex())
				);
			}else{
				surfaceNames[i] = i+"";
			}
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
		
		dataValueSurfaceViewerJIF = new org.vcell.util.gui.JInternalFrameEnhanced("DataValueSurfaceViewer",true,true,true,true);
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
			org.vcell.util.gui.LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new org.vcell.util.gui.LineBorderBean();
			ivjLocalBorder1.setLineColor(java.awt.Color.blue);
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
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


//private JPanel getJPanelSnapshotROI(){
//	if(ivjJPanelSnapshotROI == null){
//		try{
//			ivjJPanelSnapshotROI = new JPanel();
//			BoxLayout mainBL = new BoxLayout(ivjJPanelSnapshotROI,BoxLayout.X_AXIS);
//			ivjJPanelSnapshotROI.setLayout(mainBL);
//			ivjJPanelSnapshotROI.add(getJButtonSnapshotROI());
////			ivjJCheckBoxSnapshotROI = new JCheckBox("showROI");
////			ivjJCheckBoxSnapshotROI.setEnabled(false);
////			ivjJCheckBoxSnapshotROI.addActionListener(new ActionListener(){
////				public void actionPerformed(ActionEvent e) {
////				}});
////			ivjJPanelSnapshotROI.add(ivjJCheckBoxSnapshotROI);
//			ivjJPanelSnapshotROI.setBorder(new CompoundBorder(new LineBorder(getForeground(),1),new EmptyBorder(1,1,1,1)));
//		}catch (java.lang.Throwable ivjExc) {
//				handleException(ivjExc);
//		}
//	}
//	return ivjJPanelSnapshotROI;
//}

private javax.swing.JButton getJButtonSnapshotROI() {
	if (ivjJButtonSnapshotROI == null) {
		try {
			ivjJButtonSnapshotROI = new javax.swing.JButton();
			ivjJButtonSnapshotROI.setName("JButtonSnapshotROI");
			ivjJButtonSnapshotROI.setText("Snapshot ROI");
			ivjJButtonSnapshotROI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					double[] dataValues = getPdeDataContext().getDataValues();
//					CartesianMesh cartMesh = getPdeDataContext().getCartesianMesh();
					boolean isVolumeType = 
						(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME) ||
						getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME_REGION));
//					double sum = 0;
//					double wsum = 0;
//					double min = dataValues[0];
//					double max = min;
//					double spaceSum = 0;
					BitSet snapshotROI = new BitSet(dataValues.length);
					for (int i = 0; i < dataValues.length; i++) {
						snapshotROI.set(i,(dataValues[i] == 1.0));
//						if(snapshotROI.get(i)){
//							sum+= dataValues[i];
//							if(isVolumeType){
//								spaceSum+= cartMesh.calculateMeshElementVolumeFromVolumeIndex(i);
//								wsum+= dataValues[i]*cartMesh.calculateMeshElementVolumeFromVolumeIndex(i);
//							}else{
//								spaceSum+= cartMesh.getMembraneElements()[i].getArea();
//								wsum+= dataValues[i]*cartMesh.getRegionMembraneSurfaceAreaFromMembraneIndex(i);
//							}
//							min = Math.min(min, dataValues[i]);
//							max = Math.max(max, dataValues[i]);
//						}
					}
					if(snapshotROI.cardinality() == 0){
						PopupGenerator.showInfoDialog((isVolumeType?"Volume":"Membrane")+" snapshot ROI cannot be updated.\n"+
								"No data values for variable '"+getPdeDataContext().getVariableName()+"'\n"+
								"at time '"+getPdeDataContext().getTimePoint()+"' have values equal to 1.0");
					}else{
						PopupGenerator.showInfoDialog((isVolumeType?"Volume":"Membrane")+" snapshot ROI updated.\n"+
								"Variable '"+getPdeDataContext().getVariableName()+"' "+
								"Time '"+getPdeDataContext().getTimePoint()+"'\n"+
								"Current Snapshot ROI:\n"+
								"Count = "+snapshotROI.cardinality()+" of "+dataValues.length+"\n"
//								(cartMesh.getGeometryDimension()<3?(cartMesh.getGeometryDimension()<2?"length":(isVolumeType?"area":"length")):(isVolumeType?"volume":"area"))+" = "+spaceSum+"\n"
//								"Minimum = "+min+"\n"+
//								"Maximum = "+max+"\n"+
//								"Mean = "+sum/snapshotROI.cardinality()+"\n"+
//								"Weighted Mean = "+wsum/spaceSum
								);						
					}
					if(isVolumeType){
						volumeSnapshotROI = snapshotROI;
						volumeSnapshotROIDescription =
							"Variable='"+getPdeDataContext().getVariableName()+"', Timepoint= "+getPdeDataContext().getTimePoint();
					}else{
						membraneSnapshotROI = snapshotROI;
						membraneSnapshotROIDescription =
							"Variable='"+getPdeDataContext().getVariableName()+"', Timepoint= "+getPdeDataContext().getTimePoint();
					}
				}});
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSnapshotROI;
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
			constraintsJButtonSpatial.gridx = 0; constraintsJButtonSpatial.gridy = 0;
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
			
			java.awt.GridBagConstraints constraintsJButtonSnapshotROI = new java.awt.GridBagConstraints();
			constraintsJButtonSnapshotROI.gridx = 5; constraintsJButtonSnapshotROI.gridy = 0;
			constraintsJButtonSnapshotROI.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelButtons().add(getJButtonSnapshotROI()/*getJPanelSnapshotROI()*/, constraintsJButtonSnapshotROI);
			
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
			ivjJTabbedPane1.insertTab(EXPORT_DATA_TABNAME, null, getExportData(), null, 1);
			ivjJTabbedPane1.addChangeListener(
				new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
						if(ivjJTabbedPane1.getSelectedIndex() == ivjJTabbedPane1.indexOfTab(EXPORT_DATA_TABNAME)){
							SpatialSelection[] spatialSelectionsVolume =
								getPDEDataContextPanel1().fetchSpatialSelectionsAll(VariableType.VOLUME);
							SpatialSelection[] spatialSelectionsMembrane =
								getPDEDataContextPanel1().fetchSpatialSelectionsAll(VariableType.MEMBRANE);
							getPDEExportPanel1().setSpatialSelections(spatialSelectionsVolume, spatialSelectionsMembrane);
						}
					}
				}
			);
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
//	connPtoP8SetTarget();
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
private String niceCoordinateString(org.vcell.util.Coordinate coord) {

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
			bInputBlocking,false,
			true,
			new ProgressDialogListener(){
				public void cancelButton_actionPerformed(EventObject newEvent) {
					dataJobFailed(tsjs.getVcDataJobID(), UserCancelException.CANCEL_GENERIC);
				}
			}
		);
	
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
protected void showComponentInFrame(final Component comp,final String title) {
	final JDesktopPane jDesktopPane = (JDesktopPane)BeanUtils.findTypeParentOfComponent(PDEDataViewer.this, JDesktopPane.class);
	if(jDesktopPane != null){
		final JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
		frame.getContentPane().add(comp);
		frame.pack();
		org.vcell.util.BeanUtils.centerOnComponent(frame,PDEDataViewer.this);
		DocumentWindowManager.showFrame(frame,jDesktopPane);		
	}else{
		final Frame dialogOwner = (Frame)BeanUtils.findTypeParentOfComponent(PDEDataViewer.this, Frame.class);
		final JDialog frame = new JDialog(dialogOwner,title);

		frame.getContentPane().add(comp);
		frame.pack();
		org.vcell.util.BeanUtils.centerOnComponent(frame,PDEDataViewer.this);
		frame.setVisible(true);
	}
}


/**
 * Comment
 */
private void showKymograph() {
	//Collect all sample curves created by user
	SpatialSelection[] spatialSelectionArr = getPDEDataContextPanel1().fetchSpatialSelections(false,true);
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
	final SpatialSelection[] sl = getPDEDataContextPanel1().fetchSpatialSelections(false,true);
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
	
	final SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[1];
	try{
		if(getSimulation() != null && getSimulation().getMathDescription() != null){
			symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(getPdeDataContext().getVariableName());
		}
		if(symbolTableEntries[0] == null){
			symbolTableEntries[0] = new VolVariable(getPdeDataContext().getDataIdentifier().getName());
		}
	} catch (ExpressionBindingException e){
		e.printStackTrace();
	}
	
	AsynchClientTask task1 = new AsynchClientTask("Retrieving spatial series for variable '" + getPdeDataContext().getVariableName(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// get plots, ignoring points
			PlotData[] plotDatas = new PlotData[sl.length];
			for (int i = 0; i < sl.length; i++){				
				String varName = getPdeDataContext().getVariableName();
				double timePoint = getPdeDataContext().getTimePoint();
				PlotData plotData = getPdeDataContext().getLineScan(varName, timePoint, sl[i]);
				plotDatas[i] = plotData;								
			}
			hashTable.put("plotDatas", plotDatas);
		}
	};
				
	AsynchClientTask task2 = new AsynchClientTask("Showing spatial plot for variable" + getPdeDataContext().getVariableName(), AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			String varName = getPdeDataContext().getVariableName();
			PlotData[] plotDatas = (PlotData[])hashTable.get("plotDatas");
			for (PlotData plotData : plotDatas){
				if (plotData != null) {			
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
				}
			}
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);	
}


/**
 * Comment
 */
private void showTimePlot() {
	//Collect all sample curves created by user
	SpatialSelection[] spatialSelectionArr = getPDEDataContextPanel1().fetchSpatialSelections(true,true);
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
				org.vcell.util.Coordinate tp = null;
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
								new SwingDispatcherSync (){
									public Object runSwing() throws Exception{
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
										return null;
									}
								}.dispatchConsumeException();

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
	

	DataValueSurfaceViewer.SurfaceCollectionDataInfoProvider svdp =
		new DataValueSurfaceViewer.SurfaceCollectionDataInfoProvider(){
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
			public org.vcell.util.Coordinate getCentroid(int surfaceIndex,int polygonIndex){
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
					final org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec =
						new org.vcell.util.TimeSeriesJobSpec(
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
		(getSimulationModelInfo() != null?getSimulationModelInfo().getContextName()+"::"+"SIM("+getSimulationModelInfo().getSimulationName()+")::":"")+
		"VAR("+getPdeDataContext().getVariableName()+")::"+
		"TIME("+getPdeDataContext().getTimePoint()+")");

	
}
private void makeSurfaceMovie(
		final SurfaceCanvas surfaceCanvas,
		final int varTotalNumIndices, final String movieDataVarName, final DisplayAdapterService movieDAS,
		final VCDataIdentifier movieVCDataIdentifier){

	final SurfaceMovieSettingsPanel smsp = new SurfaceMovieSettingsPanel();
	smsp.init(surfaceCanvas.getWidth(), surfaceCanvas.getHeight(), getPdeDataContext().getTimePoints());
	while(true){
		if(PopupGenerator.showComponentOKCancelDialog(dataValueSurfaceViewerJIF, smsp, "Movie Settings for var "+movieDataVarName) != JOptionPane.OK_OPTION){
			return;
		}
		long movieSize =(smsp.getTotalFrames()*surfaceCanvas.getWidth()*surfaceCanvas.getHeight()*3);
		long rawDataSize = (smsp.getTotalFrames()*varTotalNumIndices*8);//raw data size;
		if(movieSize+rawDataSize > 50000000){
			final String YES_RESULT = "Yes";
			String result = PopupGenerator.showWarningDialog(this,
				"Movie processing will require at least "+(movieSize+rawDataSize)/1000000+" mega-bytes of memory.\nMovie size will be "+(movieSize >= 1000000?movieSize/1000000+" mega-bytes.":movieSize/1000.0+" kilo-bytes.")+" Continue?", new String[] {YES_RESULT,"No"}, YES_RESULT);
			if(result != null && result.equals(YES_RESULT)){
				break;
			}
		}else{
			break;
		}
	}
	final int beginTimeIndex = smsp.getBeginTimeIndex();
	final int endTimeIndex = smsp.getEndTimeIndex();
	final int step = smsp.getSkipParameter()+1;
	
	new Thread(new Runnable(){public void run(){
	try {
		final String[] varNames = new String[] {movieDataVarName};
		int[] allIndices = new int[varTotalNumIndices];
		for (int i = 0; i < allIndices.length; i++) {
			allIndices[i] = i;
		}
		final org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec =
			new org.vcell.util.TimeSeriesJobSpec(
				varNames,
				new int[][] {allIndices},null,
				getPdeDataContext().getTimePoints()[beginTimeIndex],
				step,
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
					JFileChooser jfChooser = new JFileChooser();
					while(true){
						if(jfChooser.showSaveDialog(PDEDataViewer.this) != JFileChooser.APPROVE_OPTION){
							return;
						}
						if(jfChooser.getSelectedFile().exists()){
							final String YES_RESULT = "Yes";
							String result = PopupGenerator.showWarningDialog(PDEDataViewer.this, "Overwrite exisitng file:\n"+jfChooser.getSelectedFile().getAbsolutePath()+"?", new String[] {YES_RESULT,"No"}, YES_RESULT);
							if(result != null && result.equals(YES_RESULT)){
								break;
							}
						}else{
							break;
						}
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
						VideoMediaChunk[] chunks = new VideoMediaChunk[timeSeriesJobResults.getTimes().length];	
						VideoMediaSampleRaw sample;
						int sampleDuration = 0;
//						int timeScale = 1000;
						int timeScale = smsp.getFramesPerSecond();
						int bitsPerPixel = 32;
						boolean isGrayscale = false;
//						final double[] allTimes = new double[endTimeIndex-beginTimeIndex+1];
//						double interval = allTimes[endTimeIndex] - allTimes[beginTimeIndex];
//						double duration = interval*100;
						DisplayAdapterService das = new DisplayAdapterService(movieDAS);
						int[][] origSurfacesColors = surfaceCanvas.getSurfacesColors();
						try {
							for (int t = 0; t < timeSeriesJobResults.getTimes().length; t++) {
//								if(bCancelFlag[0]){System.out.println("Cancelled...");return;}
								final int finalT = t;
								SwingUtilities.invokeLater(new Runnable(){public void run(){pp.setMessage("Creating Movie... Progress "+NumberUtils.formatNumber(100.0*((double)finalT/(double)timeSeriesJobResults.getTimes().length),3)+"%");}});
								
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

						pp.setMessage("Writing Movie to disk...");
						FileOutputStream fos = new FileOutputStream(jfChooser.getSelectedFile());
						DataOutputStream movieOutput = new DataOutputStream(new BufferedOutputStream(fos));
						MediaMethods.writeMovie(movieOutput, newMovie);
						movieOutput.close();
//						fos.write(data);
						fos.close();

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