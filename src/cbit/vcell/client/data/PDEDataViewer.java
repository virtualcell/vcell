package cbit.vcell.client.data;

import cbit.vcell.math.VolVariable;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.render.Vect3d;
import cbit.vcell.simdata.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
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
import org.vcell.util.TimeSeriesJobSpec;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;
import org.vcell.util.gui.FileFilters;
import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.LineBorderBean;
import org.vcell.util.gui.TitledBorderBean;
import org.vcell.util.gui.VCFileChooser;

import cbit.image.DisplayAdapterService;
import cbit.plot.*;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.DataJobSender;
import cbit.rmi.event.MessageEvent;
import cbit.vcell.simdata.gui.*;
import cbit.vcell.simdata.gui.SpatialSelection.SSHelper;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solvers.CartesianMesh;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;
import cbit.vcell.client.*;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.export.ExportMonitorPanel;
import cbit.vcell.export.quicktime.MediaMethods;
import cbit.vcell.export.quicktime.MediaMovie;
import cbit.vcell.export.quicktime.MediaSample;
import cbit.vcell.export.quicktime.MediaTrack;
import cbit.vcell.export.quicktime.VideoMediaChunk;
import cbit.vcell.export.quicktime.VideoMediaSampleRaw;
import cbit.vcell.export.quicktime.atoms.UserDataEntry;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.geometry.gui.DataValueSurfaceViewer;
import cbit.vcell.geometry.gui.SurfaceCanvas;
import cbit.vcell.geometry.gui.SurfaceMovieSettingsPanel;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.TaubinSmoothing;
import cbit.vcell.geometry.surface.TaubinSmoothingSpecification;
import cbit.vcell.geometry.surface.TaubinSmoothingWrong;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 6:03:07 AM)
 * @author: Ion Moraru
 */
public class PDEDataViewer extends DataViewer implements DataJobSender {
	private Vector<DataJobListener> dataJobListenerList = new Vector<DataJobListener>();
	 
	static String StringKey_timeSeriesJobResults =  "timeSeriesJobResults";
	static String StringKey_timeSeriesJobException =  "timeSeriesJobException";
	static String StringKey_timeSeriesJobSpec =  "timeSeriesJobSpec";
	
	public static class TimeSeriesDataJobListener implements DataJobListener {
		private ClientTaskStatusSupport clientTaskStatusSupport = null;
		private Hashtable<String, Object> hashTable = null;
		private VCDataJobID vcDataJobID = null;
		private double oldProgress = 0;
		
		public TimeSeriesDataJobListener(VCDataJobID id, Hashtable<String, Object> hash, ClientTaskStatusSupport ctss) {
			vcDataJobID = id;
			clientTaskStatusSupport = ctss;
			hashTable = hash;
		}			
			
		public void dataJobMessage(final DataJobEvent dje) {
			if (!dje.getVcDataJobID().equals(vcDataJobID)) {
				return;
			}
			switch (dje.getEventTypeID()) {
			case MessageEvent.DATA_START:
				break;
			case MessageEvent.DATA_PROGRESS:
				Double progress = dje.getProgress();
				if (progress != null && progress.doubleValue() > oldProgress) {
					oldProgress = progress.doubleValue();								
					clientTaskStatusSupport.setProgress(progress.intValue());
				}
				break;
			case MessageEvent.DATA_COMPLETE:
				hashTable.put(StringKey_timeSeriesJobResults, dje.getTimeSeriesJobResults());
				break;
			case MessageEvent.DATA_FAILURE:
				hashTable.put(StringKey_timeSeriesJobException,dje.getFailedJobException());
				break;
			}
		}
	};
	
	public class TimeSeriesDataRetrievalTask extends AsynchClientTask {
		public TimeSeriesDataRetrievalTask(String title) {
			super(title, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
		}
	
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			TimeSeriesJobSpec timeSeriesJobSpec = (TimeSeriesJobSpec)hashTable.get(StringKey_timeSeriesJobSpec);
			PDEDataViewer.this.getPdeDataContext().getTimeSeriesValues(timeSeriesJobSpec);
			DataJobListener djl = new TimeSeriesDataJobListener(timeSeriesJobSpec.getVcDataJobID(), hashTable, getClientTaskStatusSupport());
			try {
				PDEDataViewer.this.addDataJobListener(djl);
				while (true) {
					Throwable timeSeriesJobFailed = (Throwable)hashTable.get(StringKey_timeSeriesJobException);
					if (timeSeriesJobFailed != null) {
						throw new Exception(timeSeriesJobFailed.getMessage());
					}
					if (hashTable.get(StringKey_timeSeriesJobResults) != null) {
						break;
					}
					if (getClientTaskStatusSupport().isInterrupted()) {
						break;
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						if (getClientTaskStatusSupport().isInterrupted()) {
							throw UserCancelException.CANCEL_GENERIC;
						} else {
							throw e;
						}
					}
				}
			} finally {
				PDEDataViewer.this.removeDataJobListener(djl);
			}
		}
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
					(evt.getPropertyName().equals(DataViewer.PROP_SIM_MODEL_INFO) || evt.getPropertyName().equals("pdeDataContext"))) {
				if (getPdeDataContext() != null && getSimulationModelInfo() != null){
					getPDEDataContextPanel1().setDataInfoProvider(new DataViewer.DataInfoProvider(getPdeDataContext().getCartesianMesh(),getSimulationModelInfo()));
				} else {
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

/**
 * Comment
 */
private void calcStatistics(final ActionEvent actionEvent) {
	try {
		roiAction();
	}catch(Throwable e){
		PopupGenerator.showErrorDialog("Error calculating statistics\n"+e.getMessage());
	}
}


private BitSet getFillROI(SpatialSelectionVolume spatialSelectionVolume){
	if(spatialSelectionVolume.getCurveSelectionInfo().getCurve()instanceof SinglePoint){
		return null;
	}
	BitSet fillROI = null;
	SSHelper ssHelper = spatialSelectionVolume.getIndexSamples(0, 1);
	if(ssHelper != null && ssHelper.getSampledIndexes()[0] == ssHelper.getSampledIndexes()[ssHelper.getSampledIndexes().length-1]){
		Point projMin = null;
		Point projMax = null;
		Point[] projVolCI = new Point[ssHelper.getSampledIndexes().length];
		for (int i = 0; i < ssHelper.getSampledIndexes().length; i++) {
			CoordinateIndex vCI = getPdeDataContext().getCartesianMesh().getCoordinateIndexFromVolumeIndex(ssHelper.getSampledIndexes()[i]);
			int normalAxis = getPDEDataContextPanel1().getNormalAxis();
			projVolCI[i] = new Point((int)Coordinate.convertAxisFromStandardXYZToNormal(vCI.x, vCI.y,vCI.z, Coordinate.X_AXIS, normalAxis),
					(int)Coordinate.convertAxisFromStandardXYZToNormal(vCI.x, vCI.y,vCI.z, Coordinate.Y_AXIS, normalAxis));
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
					int volIndex = getPdeDataContext().getCartesianMesh().getVolumeIndex(coordinateIndex);
					fillROI.set(volIndex);
				}
			}
		}

	}
	return fillROI;
}

void plotSpaceStats (TSJobResultsSpaceStats tsjrss) {
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

	SymbolTableEntry[] symbolTableEntries = null;
	if(tsjrss.getVariableNames().length == 1){
		symbolTableEntries = new SymbolTableEntry[3/*4*/];//max.mean.min,sum
		try{
			if(getSimulation() != null && getSimulation().getMathDescription() != null){
				symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(tsjrss.getVariableNames()[0]);
			}else{
				symbolTableEntries[0] = new SimpleSymbolTable(tsjrss.getVariableNames()).getEntry(tsjrss.getVariableNames()[0]);
			}
			symbolTableEntries[1] = symbolTableEntries[0];
			symbolTableEntries[2] = symbolTableEntries[0];
		}catch(ExpressionBindingException e){
			e.printStackTrace();
		}
	}
	SymbolTableEntry[] finalSymbolTableEntries = symbolTableEntries;
	boolean finalBVolume = bVolume;
	PlotPane plotPane = new cbit.plot.PlotPane();
	plotPane.setPlot2D(
		new SingleXPlot2D(finalSymbolTableEntries,"Time",
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
}		


private void roiAction(){
	BeanUtils.setCursorThroughout(this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	try{
		final String[] ROI_COLUMN_NAMES = new String[] {"ROI source","ROI source name","ROI Description"};
		final Vector<Object> auxInfoV = new Vector<Object>();
		
		final DataIdentifier dataIdentifier = getPdeDataContext().getDataIdentifier();
		VariableType variableType = dataIdentifier.getVariableType();
		final boolean isVolume = variableType.equals(VariableType.VOLUME) || variableType.equals(VariableType.VOLUME_REGION);
		
		DefaultTableModel tableModel = new DefaultTableModel(){
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		for (int i = 0; i < ROI_COLUMN_NAMES.length; i++) {
			tableModel.addColumn(ROI_COLUMN_NAMES[i]);
		}
		//Add Snapshot ROI
		if((isVolume?volumeSnapshotROI:membraneSnapshotROI) != null){
			tableModel.addRow(new Object[] {(isVolume?"Volume":"Membrane")+" Variables and Functions",
						"Snapshot", (isVolume ? volumeSnapshotROIDescription : membraneSnapshotROIDescription) + ", (values = 1.0)"});
			auxInfoV.add((isVolume?volumeSnapshotROI:membraneSnapshotROI));
		}
		//Add user ROIs
		SpatialSelection[] userROIArr = getPDEDataContextPanel1().fetchSpatialSelections(true,false);
		for (int i = 0; userROIArr != null && i < userROIArr.length; i += 1) {
			String descr = null;
			boolean bPoint = false;
			if (isVolume) {
				if (userROIArr[i] instanceof SpatialSelectionVolume) {
					Curve curve = ((SpatialSelectionVolume)userROIArr[i]).getCurveSelectionInfo().getCurve();
					descr = curve.getDescription();
					if (curve instanceof SinglePoint) {
						bPoint = true;
					}
				}
			} else {
				if (userROIArr[i] instanceof SpatialSelectionMembrane) {
					SampledCurve selectionSource = ((SpatialSelectionMembrane)userROIArr[i]).getSelectionSource();
					descr = selectionSource.getDescription();
					if (selectionSource instanceof SinglePoint) {
						bPoint = true;
					}
				}
			}
			
			//Add Area User ROI
			BitSet fillBitSet = null;
			if(userROIArr[i] instanceof SpatialSelectionVolume){
				fillBitSet = getFillROI((SpatialSelectionVolume)userROIArr[i]);
				if(fillBitSet != null){
					tableModel.addRow(new Object[] {"User Defined",	descr, "Area Enclosed Volume ROI"});
					auxInfoV.add(fillBitSet);
				}
			}
			//Add Point and Line User ROI
			if(fillBitSet == null){
				tableModel.addRow(new Object[] {"User Defined", descr, (bPoint?"Point":"Line") + (isVolume?" Volume":" Membrane") + " ROI "} );
				auxInfoV.add(userROIArr[i]);
			}
		}
		//Add sorted Geometry ROI
		final CartesianMesh cartesianMesh = getPdeDataContext().getCartesianMesh();
		HashMap<Integer,?> regionMapSubvolumesHashMap = (isVolume ? cartesianMesh.getVolumeRegionMapSubvolume()	: cartesianMesh.getMembraneRegionMapSubvolumesInOut());
		Set<?> regionMapSubvolumesEntrySet = regionMapSubvolumesHashMap.entrySet();
		Iterator<?> regionMapSubvolumesEntryIter = regionMapSubvolumesEntrySet.iterator();
		TreeSet<Object[]> sortedGeomROITreeSet = new TreeSet<Object[]>(
			new Comparator<Object[]>(){
				public int compare(Object[] o1, Object[] o2) {
					int result = ((String)((Object[])o1[0])[1]).compareToIgnoreCase((String)((Object[])o2[0])[1]);
					if (result == 0){
						result = (((Entry<Integer, ?>)o1[1]).getKey()).compareTo(((Entry<Integer, ?>)o2[1]).getKey());
					}
					return result;
				}
			}
		);
		while(regionMapSubvolumesEntryIter.hasNext()){
			Entry<Integer,?> regionMapSubvolumesEntry = (Entry<Integer, ?>) regionMapSubvolumesEntryIter.next();
			sortedGeomROITreeSet.add(new Object[] {
				new Object[] { "Geometry",
					(isVolume ? getSimulationModelInfo().getVolumeNamePhysiology(((Integer)regionMapSubvolumesEntry.getValue()))
						: getSimulationModelInfo().getMembraneName(((int[])regionMapSubvolumesEntry.getValue())[0], ((int[])regionMapSubvolumesEntry.getValue())[1])),
					(isVolume ? "(svID="+regionMapSubvolumesEntry.getValue()+ " " : "(") + "vrID="+regionMapSubvolumesEntry.getKey()+") Predefined "
					+ (isVolume ? "volume" : "membrane") + " region"}, 
				regionMapSubvolumesEntry}
			);
		}
		Iterator<Object[]> sortedGeomROIIter = sortedGeomROITreeSet.iterator();
		while(sortedGeomROIIter.hasNext()){
			Object[] sortedGeomROIObjArr = (Object[])sortedGeomROIIter.next();
			tableModel.addRow((Object[])sortedGeomROIObjArr[0]);
			auxInfoV.add(sortedGeomROIObjArr[1]);
		}
		
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
									dataBitSet.or(cartesianMesh.getVolumeROIFromVolumeRegionID(volumeRegionID));
								}else{
									int membraneRegionID = (Integer)((Entry<Integer, int[]>)auxInfo).getKey();
									dataBitSet.or(cartesianMesh.getMembraneROIFromMembraneRegionID(membraneRegionID));
								}
							}else if(auxInfo instanceof BitSet){
								dataBitSet.or((BitSet)auxInfo);
							}else{
								throw new Exception("ROI table, Unknown data type: "+auxInfo.getClass().getName());
							}
						}
						TimeSeriesJobSpec timeSeriesJobSpec = new TimeSeriesJobSpec(
								new String[] {dataIdentifier.getName()}, new BitSet[] {dataBitSet},
								((Double)jcb_time_begin.getSelectedItem()).doubleValue(), 1,
								((Double)jcb_time_end.getSelectedItem()).doubleValue(),
								true,false, VCDataJobID.createVCDataJobID(getDataViewerManager().getUser(),	true));
						
						Hashtable<String, Object> hash = new Hashtable<String, Object>();
						hash.put(StringKey_timeSeriesJobSpec, timeSeriesJobSpec);
	
						AsynchClientTask task1 = new TimeSeriesDataRetrievalTask("Retrieve data for '" + dataIdentifier + "'");
						AsynchClientTask task2 = new AsynchClientTask("Showing stat for '" + dataIdentifier + "'", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
	
							@Override
							public void run(Hashtable<String, Object> hashTable) throws Exception {
								TSJobResultsSpaceStats tsJobResultsSpaceStats = (TSJobResultsSpaceStats)hashTable.get(StringKey_timeSeriesJobResults);
								plotSpaceStats(tsJobResultsSpaceStats);
							}
						};		
						ClientTaskDispatcher.dispatch(PDEDataViewer.this, hash, new AsynchClientTask[] { task1, task2 }, true, true, null);
					} catch (Exception e1) {
						e1.printStackTrace();
						PopupGenerator.showErrorDialog("ROI Error.\n"+e1.getMessage());
					}
				}
				BeanUtils.dispose(mainJPanel);
			}
		});
		okCancelJPanel.add(okButton);
		roiTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if(roiTable.getSelectedRows() != null && roiTable.getSelectedRows().length > 0){
					okButton.setEnabled(true);
				}else{
					okButton.setEnabled(false);
				}
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				BeanUtils.dispose(mainJPanel);
			}
		});
		okCancelJPanel.add(cancelButton);
		okCancelJPanel.setBorder(new EmptyBorder(4,4,4,4));
	
		mainJPanel.add(timeJPanel);
		mainJPanel.add(scrollPane);
		mainJPanel.add(okCancelJPanel);
	
		showComponentInFrame(mainJPanel,
			"Calculate "+(isVolume?"volume":"membrane")+" statistics for '"+getPdeDataContext().getVariableName()+"'."+
			"  Choose times and 1 or more ROI(s).");
	
	} finally {
		BeanUtils.setCursorThroughout(this, Cursor.getDefaultCursor());
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


public void dataJobMessage(final DataJobEvent dje) {
	for (DataJobListener djl : dataJobListenerList) {
		djl.dataJobMessage(dje);
	}
}
	

/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 1:53:00 PM)
 */
private DataValueSurfaceViewer getDataValueSurfaceViewer() {

	try{
	if(fieldDataValueSurfaceViewer == null){
		//Surfaces
		CartesianMesh cartesianMesh = getPdeDataContext().getCartesianMesh();
		meshRegionSurfaces = new MeshDisplayAdapter(cartesianMesh).generateMeshRegionSurfaces();
		SurfaceCollection surfaceCollection = meshRegionSurfaces.getSurfaceCollection();

		//SurfaceNames
		final String[] surfaceNames = new String[meshRegionSurfaces.getSurfaceCollection().getSurfaceCount()];
		for (int i = 0; i < meshRegionSurfaces.getSurfaceCollection().getSurfaceCount(); i++){
			cbit.vcell.solvers.MembraneElement me = //Get the first element, any will do, all have same inside/outside volumeIndex
				cartesianMesh.getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(i,0)];
			if(getSimulationModelInfo() != null){
				surfaceNames[i] = getSimulationModelInfo().getMembraneName(
					cartesianMesh.getSubVolumeFromVolumeIndex(me.getInsideVolumeIndex()),
					cartesianMesh.getSubVolumeFromVolumeIndex(me.getOutsideVolumeIndex())
				);
			}else{
				surfaceNames[i] = i+"";
			}
		}

		//SurfaceAreas
		final Double[] surfaceAreas = new Double[meshRegionSurfaces.getSurfaceCollection().getSurfaceCount()];
		for (int i = 0; i < meshRegionSurfaces.getSurfaceCollection().getSurfaceCount(); i++){
			surfaceAreas[i] = new Double(cartesianMesh.getRegionMembraneSurfaceAreaFromMembraneIndex(meshRegionSurfaces.getMembraneIndexForPolygon(i,0)));
		}

		DataValueSurfaceViewer fieldDataValueSurfaceViewer0 = new DataValueSurfaceViewer();

		TaubinSmoothing taubinSmoothing = new TaubinSmoothingWrong();
		TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.getInstance(.3);
		taubinSmoothing.smooth(surfaceCollection,taubinSpec);
		fieldDataValueSurfaceViewer0.init(
			meshRegionSurfaces.getSurfaceCollection(),
			cartesianMesh.getOrigin(),
			cartesianMesh.getExtent(),
			surfaceNames,
			surfaceAreas,
			cartesianMesh.getGeometryDimension()
		);
		
		dataValueSurfaceViewerJIF = new JInternalFrameEnhanced("DataValueSurfaceViewer",true,true,true,true);
		dataValueSurfaceViewerJIF.setContentPane(fieldDataValueSurfaceViewer0);
		//dataValueSurfaceViewerJIF.pack();
		dataValueSurfaceViewerJIF.setSize(800,800);
		dataValueSurfaceViewerJIF.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
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
			LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new LineBorderBean();
			ivjLocalBorder1.setLineColor(Color.blue);
			TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new TitledBorderBean();
			ivjLocalBorder.setBorder(ivjLocalBorder1);
			ivjLocalBorder.setTitle("Export jobs");
			ivjExportMonitorPanel1 = new ExportMonitorPanel();
			ivjExportMonitorPanel1.setName("ExportMonitorPanel1");
			ivjExportMonitorPanel1.setPreferredSize(new Dimension(453, 150));
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
			ivjJButtonSpatial = new JButton();
			ivjJButtonSpatial.setName("JButtonSpatial");
			ivjJButtonSpatial.setText("Show Spatial Plot");
			// user code begin {1}
			// user code end
		} catch (Throwable ivjExc) {
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

private javax.swing.JButton getJButtonSnapshotROI() {
	if (ivjJButtonSnapshotROI == null) {
		try {
			ivjJButtonSnapshotROI = new javax.swing.JButton();
			ivjJButtonSnapshotROI.setName("JButtonSnapshotROI");
			ivjJButtonSnapshotROI.setText("Snapshot ROI");
			ivjJButtonSnapshotROI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					double[] dataValues = getPdeDataContext().getDataValues();
					VariableType variableType = getPdeDataContext().getDataIdentifier().getVariableType();
					boolean isVolumeType = (variableType.equals(VariableType.VOLUME) ||	variableType.equals(VariableType.VOLUME_REGION));
					BitSet snapshotROI = new BitSet(dataValues.length);
					for (int i = 0; i < dataValues.length; i++) {
						snapshotROI.set(i,(dataValues[i] == 1.0));
					}
					String variableName = getPdeDataContext().getVariableName();
					double timePoint = getPdeDataContext().getTimePoint();
					if(snapshotROI.cardinality() == 0){
						PopupGenerator.showInfoDialog((isVolumeType?"Volume":"Membrane")+" snapshot ROI cannot be updated.\n"+
								"No data values for variable '"+variableName+"'\n"+
								"at time '"+timePoint+"' have values equal to 1.0");
					}else{
						PopupGenerator.showInfoDialog((isVolumeType?"Volume":"Membrane")+" snapshot ROI updated.\n"+
								"Variable '"+variableName+"' "+
								"Time '"+timePoint+"'\n"+
								"Current Snapshot ROI:\n"+
								"Count = "+snapshotROI.cardinality()+" of "+dataValues.length+"\n"
								);						
					}
					if(isVolumeType){
						volumeSnapshotROI = snapshotROI;
						volumeSnapshotROIDescription = "Variable='"+variableName+"', Timepoint= "+timePoint;
					}else{
						membraneSnapshotROI = snapshotROI;
						membraneSnapshotROIDescription = "Variable='"+variableName+"', Timepoint= "+timePoint;
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
			ivjPDEExportPanel1 = new NewPDEExportPanel();
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
private Simulation getSimulation() {
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
private String niceCoordinateString(Coordinate coord) {

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
	
	if(getPdeDataContext().getDataIdentifier() != null && 
			(getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE) ||
			getPdeDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE_REGION))){
		getJButtonSurfaces().setEnabled(true);
	}else{
		getJButtonSurfaces().setEnabled(false);
	}
}

/**
 * Sets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @param pdeDataContext The new value for the property.
 * @see #getPdeDataContext
 */
public void setPdeDataContext(PDEDataContext pdeDataContext) {

	if(dataValueSurfaceViewerJIF != null){
		dataValueSurfaceViewerJIF.dispose();
	}
	
	dataValueSurfaceViewerJIF = null;
	fieldDataValueSurfaceViewer = null;
	meshRegionSurfaces = null;
	
	PDEDataContext oldValue = fieldPdeDataContext;
	fieldPdeDataContext = pdeDataContext;
	firePropertyChange("pdeDataContext", oldValue, pdeDataContext);
}


/**
 * Set the pdeDataContext1 to a new value.
 * @param newValue cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setpdeDataContext1(PDEDataContext newValue) {
	if (ivjpdeDataContext1 != newValue) {
		try {
			PDEDataContext oldValue = getpdeDataContext1();
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
		BeanUtils.centerOnComponent(frame,PDEDataViewer.this);
		DocumentWindowManager.showFrame(frame,jDesktopPane);		
	}else{
		final Frame dialogOwner = (Frame)BeanUtils.findTypeParentOfComponent(PDEDataViewer.this, Frame.class);
		final JDialog frame = new JDialog(dialogOwner,title);

		frame.getContentPane().add(comp);
		frame.pack();
		BeanUtils.centerOnComponent(frame,PDEDataViewer.this);
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
			
			String title = "Kymograph: ";
			if (getSimulationModelInfo()!=null){
				title += getSimulationModelInfo().getContextName()+" "+getSimulationModelInfo().getSimulationName();
			}
			KymographPanel  kymographPanel = new KymographPanel(this, title);
			
			SymbolTable symbolTable;
			if(getSimulation() != null && getSimulation().getMathDescription() != null){
				symbolTable = getSimulation().getMathDescription();
			}else{
				symbolTable = new SimpleSymbolTable(new String[] {getPdeDataContext().getDataIdentifier().getName()});
			}
			
			kymographPanel.initDataManager(getDataViewerManager().getUser(), ((ClientPDEDataContext)getPdeDataContext()).getDataManager(),
				getPdeDataContext().getVariableName(), getPdeDataContext().getTimePoints()[0], 1,
				getPdeDataContext().getTimePoints()[getPdeDataContext().getTimePoints().length-1],
				indices,crossingMembraneIndices,accumDistances,true,getPdeDataContext().getTimePoint(),
				symbolTable);
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
	final String varName = getPdeDataContext().getVariableName();
	final double timePoint = getPdeDataContext().getTimePoint();
	final SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[1];
	try{
		if(getSimulation() != null && getSimulation().getMathDescription() != null){
			symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(varName);
		}
		if(symbolTableEntries[0] == null){
			symbolTableEntries[0] = new VolVariable(varName);
		}
	} catch (ExpressionBindingException e){
		e.printStackTrace();
	}
	
	AsynchClientTask task1 = new AsynchClientTask("Retrieving spatial series for variable '" + varName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// get plots, ignoring points
			PlotData[] plotDatas = new PlotData[sl.length];
			for (int i = 0; i < sl.length; i++){				
				PlotData plotData = getPdeDataContext().getLineScan(varName, timePoint, sl[i]);
				plotDatas[i] = plotData;								
			}
			hashTable.put("plotDatas", plotDatas);
		}
	};
				
	AsynchClientTask task2 = new AsynchClientTask("Showing spatial plot for variable" + varName, AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			PlotData[] plotDatas = (PlotData[])hashTable.get("plotDatas");
			for (PlotData plotData : plotDatas){
				if (plotData != null) {			
					PlotPane plotPane = new PlotPane();
					Plot2D plot2D = new Plot2D(symbolTableEntries, new String[] { varName },new PlotData[] { plotData },
								new String[] {"Values along curve", "Distance (\u00b5m)", "[" + varName + "]"});
					plotPane.setPlot2D(	plot2D);
					String title = "Spatial Plot: ("+varName+")";
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
		for (int i = 0; i < spatialSelectionArr.length; i++){
			if(spatialSelectionArr[i].isPoint() ||
				(spatialSelectionArr[i] instanceof SpatialSelectionMembrane &&
					((SpatialSelectionMembrane)spatialSelectionArr[i]).getSelectionSource() instanceof SinglePoint)){
				singlePointSSOnly.add(spatialSelectionArr[i]);
			}
		}
	}
	final String varName = getPdeDataContext().getVariableName();
	VariableType varType = getPdeDataContext().getDataIdentifier().getVariableType();	
	if(singlePointSSOnly.size() == 0){
		PopupGenerator.showErrorDialog(this, "No Time sampling points match DataType="+varType);
		return;
	}
	
	try {		
		int[] indices = null;
		//
		indices = new int[singlePointSSOnly.size()];
		final String[] plotNames = new String[singlePointSSOnly.size()];
		final SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[plotNames.length];
		for (int i = 0; i < singlePointSSOnly.size(); i++){
			Coordinate tp = null;
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
					symbolTableEntries[0] = getSimulation().getMathDescription().getEntry(varName);
				}
				if(symbolTableEntries[0] == null){
					symbolTableEntries[0] = new VolVariable(varName);
				}
			}catch(ExpressionBindingException e){
				e.printStackTrace();
			}
		}

		double[] timePoints = getPdeDataContext().getTimePoints();
		TimeSeriesJobSpec tsjs = new TimeSeriesJobSpec(	new String[] {varName},
				new int[][] {indices}, null, timePoints[0], 1, timePoints[timePoints.length-1],
				VCDataJobID.createVCDataJobID(getDataViewerManager().getUser(), true));

		if (!tsjs.getVcDataJobID().isBackgroundTask()){
			throw new RuntimeException("Use getTimeSeries(...) if not a background job");
		}
		
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		hash.put(StringKey_timeSeriesJobSpec, tsjs);
		
		AsynchClientTask task1 = new TimeSeriesDataRetrievalTask("Retrieving Data for '"+varName+"'...");	
		AsynchClientTask task2 = new AsynchClientTask("showing time plot for '"+varName+"'", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				TSJobResultsNoStats tsJobResultsNoStats = (TSJobResultsNoStats)hashTable.get(StringKey_timeSeriesJobResults);
				PlotPane plotPane = new PlotPane();
				SingleXPlot2D plot2D = new SingleXPlot2D(symbolTableEntries, "Time", plotNames,
					tsJobResultsNoStats.getTimesAndValuesForVariable(varName),
					new String[] {"Time series for " + varName, "Time (s)", "[" + varName + "]"});
				plotPane.setPlot2D(plot2D);
				String title = "Timeplot: ("+varName+")";
				if (getSimulationModelInfo()!=null){
					title += " "+getSimulationModelInfo().getContextName()+" "+getSimulationModelInfo().getSimulationName();
				}
				showComponentInFrame(plotPane, title);				
			}						
		};		
		ClientTaskDispatcher.dispatch(this, hash, new AsynchClientTask[] { task1, task2 }, true, true, null);
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
		getKymographJButton().setEnabled((getPdeDataContext().getTimePoints().length > 1) && ((Boolean)(propertyChangeEvent.getNewValue())).booleanValue());
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
	

	DataValueSurfaceViewer.SurfaceCollectionDataInfoProvider svdp = new DataValueSurfaceViewer.SurfaceCollectionDataInfoProvider(){
		private DisplayAdapterService updatedDAS = new DisplayAdapterService(getPDEDataContextPanel1().getdisplayAdapterService1());
		private String updatedVariableName = getPdeDataContext().getVariableName();
		private double updatedTimePoint = getPdeDataContext().getTimePoint();
		private double[] updatedVariableValues = getPdeDataContext().getDataValues();
		private VCDataIdentifier updatedVCDataIdentifier = getPdeDataContext().getVCDataIdentifier();
		public void makeMovie(SurfaceCanvas surfaceCanvas){
			makeSurfaceMovie(surfaceCanvas, updatedVariableValues.length,updatedVariableName,updatedDAS,updatedVCDataIdentifier);
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
		public Coordinate getCentroid(int surfaceIndex,int polygonIndex){
			return getPdeDataContext().getCartesianMesh().getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(surfaceIndex,polygonIndex)].getCentroid();
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
		public Color getROIHighlightColor(){
			return new Color(getPDEDataContextPanel1().getdisplayAdapterService1().getSpecialColors()[cbit.image.DisplayAdapterService.FOREGROUND_HIGHLIGHT_COLOR_OFFSET]);
		}
		public void showComponentInFrame(Component comp,String title){
			PDEDataViewer.this.showComponentInFrame(comp,title);
		}
		public void plotTimeSeriesData(
				int[][] indices,
				boolean bAllTimes,
				boolean bTimeStats,
				boolean bSpaceStats) throws DataAccessException{

				double[] timePoints = getPdeDataContext().getTimePoints();
				double beginTime = (bAllTimes?timePoints[0]:updatedTimePoint);
				double endTime = (bAllTimes?timePoints[timePoints.length-1]:beginTime);
				String[] varNames = new String[indices.length];
				for(int i=0;i<varNames.length;i+= 1){
					varNames[i] = updatedVariableName;
				}
				TimeSeriesJobSpec timeSeriesJobSpec =	new TimeSeriesJobSpec(varNames,indices,beginTime,1,endTime,bSpaceStats,bTimeStats,
							VCDataJobID.createVCDataJobID(getDataViewerManager().getUser(),	true));

				Hashtable<String, Object> hash = new Hashtable<String, Object>();
				hash.put(StringKey_timeSeriesJobSpec, timeSeriesJobSpec);

				AsynchClientTask task1 = new TimeSeriesDataRetrievalTask("Retrieve data");
				AsynchClientTask task2 = new AsynchClientTask("Showing surface", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						TSJobResultsSpaceStats tsJobResultsSpaceStats = (TSJobResultsSpaceStats)hashTable.get(StringKey_timeSeriesJobResults);
						plotSpaceStats(tsJobResultsSpaceStats);
					}
				};		
				ClientTaskDispatcher.dispatch(PDEDataViewer.this, hash, new AsynchClientTask[] { task1, task2 }, true, true, null);				
		}
	};

	getDataValueSurfaceViewer().setSurfaceCollectionDataInfoProvider(svdp);
	
	dataValueSurfaceViewerJIF.setTitle(
		(getSimulationModelInfo() != null?getSimulationModelInfo().getContextName()+"::"+"SIM("+getSimulationModelInfo().getSimulationName()+")::":"")+
		"VAR("+getPdeDataContext().getVariableName()+")::"+
		"TIME("+getPdeDataContext().getTimePoint()+")");
}

private void makeSurfaceMovie(final SurfaceCanvas surfaceCanvas,
		final int varTotalNumIndices, final String movieDataVarName, final DisplayAdapterService movieDAS,
		final VCDataIdentifier movieVCDataIdentifier){

	final SurfaceMovieSettingsPanel smsp = new SurfaceMovieSettingsPanel();
	final double[] timePoints = getPdeDataContext().getTimePoints();
	final int surfaceWidth = surfaceCanvas.getWidth();
	final int surfaceHeight = surfaceCanvas.getHeight();
	smsp.init(surfaceWidth, surfaceHeight, timePoints);
	
	while (true){
		if(PopupGenerator.showComponentOKCancelDialog(dataValueSurfaceViewerJIF, smsp, "Movie Settings for var "+movieDataVarName) != JOptionPane.OK_OPTION){
			return;
		}
		long movieSize =(smsp.getTotalFrames()*surfaceWidth*surfaceHeight*3);
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
	final String[] varNames = new String[] {movieDataVarName};
	int[] allIndices = new int[varTotalNumIndices];
	for (int i = 0; i < allIndices.length; i++) {
		allIndices[i] = i;
	}
	final TimeSeriesJobSpec timeSeriesJobSpec =	new TimeSeriesJobSpec(varNames,	new int[][] {allIndices},null,
			timePoints[beginTimeIndex], step, timePoints[endTimeIndex],
			VCDataJobID.createVCDataJobID(getDataViewerManager().getUser(),	true));
	
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	hash.put(StringKey_timeSeriesJobSpec, timeSeriesJobSpec);

	AsynchClientTask task1 = new TimeSeriesDataRetrievalTask("Retrieving data for variable '" + movieDataVarName + "'");
	AsynchClientTask task2 = new AsynchClientTask("select a file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCFileChooser fileChooser = new VCFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MOV);
			// Set the default file filter...
			fileChooser.setFileFilter(FileFilters.FILE_FILTER_MOV);
			// remove all selector
			fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());		    
			
			fileChooser.setDialogTitle("Saving surface movie");
			
			File selectedFile = null;
			while(true){
				if(fileChooser.showSaveDialog(PDEDataViewer.this) != JFileChooser.APPROVE_OPTION){
					return;
				}
				selectedFile = fileChooser.getSelectedFile();
				if (!selectedFile.getName().endsWith(".mov")) {
					selectedFile = new File(selectedFile.getAbsolutePath() + ".mov");
				}
				if (selectedFile.exists()){
					final String YES_RESULT = "Yes";
					String result = PopupGenerator.showWarningDialog(PDEDataViewer.this, "Overwrite exisitng file:\n"+selectedFile.getAbsolutePath()+"?", new String[] {YES_RESULT,"No"}, YES_RESULT);
					if (result != null && result.equals(YES_RESULT)){						
						break;
					}
				} else {
					break;
				}
			}
			hashTable.put("selectedFile", selectedFile);
		}							
	};
		
	AsynchClientTask task3 = new AsynchClientTask("create movie", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			File selectedFile = (File)hashTable.get("selectedFile");
			if (selectedFile == null) {
				return;
			}
			
			TSJobResultsNoStats tsJobResultsNoStats = (TSJobResultsNoStats)hashTable.get(StringKey_timeSeriesJobResults);
			
			double[][] timeSeries = tsJobResultsNoStats.getTimesAndValuesForVariable(movieDataVarName);			
			int[] singleFrame = new int[surfaceWidth*surfaceHeight];
			BufferedImage bufferedImage = new BufferedImage(surfaceWidth, surfaceHeight,BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g2D = bufferedImage.createGraphics();
			VideoMediaChunk[] chunks = new VideoMediaChunk[tsJobResultsNoStats.getTimes().length];	
			VideoMediaSampleRaw sample;
			int sampleDuration = 0;
			int timeScale = smsp.getFramesPerSecond();
			int bitsPerPixel = 32;
			boolean isGrayscale = false;
			DisplayAdapterService das = new DisplayAdapterService(movieDAS);
			int[][] origSurfacesColors = surfaceCanvas.getSurfacesColors();
			try {
				for (int t = 0; t < tsJobResultsNoStats.getTimes().length; t++) {
					getClientTaskStatusSupport().setMessage("Creating Movie... Progress "+NumberUtils.formatNumber(100.0*((double)t/(double)tsJobResultsNoStats.getTimes().length),3)+"%");
					
					double min = Double.POSITIVE_INFINITY;
					double max = Double.NEGATIVE_INFINITY;
					for (int index = 1; index < timeSeries.length; index++) {
						double v = timeSeries[index][t];
						if(!Double.isNaN(v) && !Double.isInfinite(v)){
							min = Math.min(min, v);
							max = Math.max(max, v);
						}
					}
					das.setValueDomain(new Range(min,max));
					if(das.getAutoScale()){
						das.setActiveScaleRange(new Range(min,max));
					}
					int[][] surfacesColors = new int[surfaceCanvas.getSurfaceCollection().getSurfaceCount()][];
					for (int i = 0; i < surfaceCanvas.getSurfaceCollection().getSurfaceCount(); i += 1) {
						Surface surface = surfaceCanvas.getSurfaceCollection().getSurfaces(i);
						surfacesColors[i] = new int[surface.getPolygonCount()];
						for (int j = 0; j < surface.getPolygonCount(); j += 1) {
							int membIndex = meshRegionSurfaces.getMembraneIndexForPolygon(i, j);
							surfacesColors[i][j] = das.getColorFromValue(timeSeries[membIndex+1][t]);
						}
					}
					surfaceCanvas.setSurfacesColors(surfacesColors);
					
					surfaceCanvas.paintImmediately(0, 0, surfaceWidth, surfaceHeight);
					surfaceCanvas.paint(g2D);
					bufferedImage.getRGB(0, 0, surfaceWidth, surfaceHeight, singleFrame, 0, surfaceWidth);
					sampleDuration = 1;
					ByteArrayOutputStream sampleBytes = new ByteArrayOutputStream();
					DataOutputStream sampleData = new DataOutputStream(sampleBytes);
					for (int j=0;j<singleFrame.length;j++){
						sampleData.writeInt(singleFrame[j]);
					}
					sampleData.close();
					byte[] bytes = sampleBytes.toByteArray();
					sample = new VideoMediaSampleRaw(surfaceWidth, surfaceHeight * varNames.length, sampleDuration,
							new MediaSample.MediaSampleStream(bytes),bytes.length,
							bitsPerPixel, isGrayscale);
					chunks[t] = new VideoMediaChunk(sample);
				}
			}finally{
				surfaceCanvas.setSurfacesColors(origSurfacesColors);
				surfaceCanvas.paintImmediately(0, 0, surfaceWidth, surfaceHeight);
			}
			MediaTrack videoTrack = new MediaTrack(chunks);
			MediaMovie newMovie = new MediaMovie(videoTrack, videoTrack.getDuration(), timeScale);
			newMovie.addUserDataEntry(new UserDataEntry("cpy", "©" + (new GregorianCalendar()).get(Calendar.YEAR) + ", UCHC"));
			newMovie.addUserDataEntry(new UserDataEntry("des", "Dataset name: " + movieVCDataIdentifier.getID()));
			newMovie.addUserDataEntry(new UserDataEntry("cmt", "Time range: " + timePoints[beginTimeIndex] + " - " + timePoints[endTimeIndex]));
			for (int k = 0; k < varNames.length; k ++) {
				String entryType = "v" + (k < 10 ? "0" : "") + k; // pad with 0 if k < 10
				UserDataEntry entry = new UserDataEntry(entryType,	"Variable name: " + varNames[k] + " min: " + das.getValueDomain().getMin() 
						+ " max: " + das.getValueDomain().getMax());
				newMovie.addUserDataEntry(entry);
			}
			getClientTaskStatusSupport().setMessage("Writing Movie to disk...");
			FileOutputStream fos = new FileOutputStream(selectedFile);
			DataOutputStream movieOutput = new DataOutputStream(new BufferedOutputStream(fos));
			MediaMethods.writeMovie(movieOutput, newMovie);
			movieOutput.close();
			fos.close();
		}
	};
	ClientTaskDispatcher.dispatch(this, hash, new AsynchClientTask[] { task1, task2, task3 }, true, true, null);
}

public void addDataJobListener(DataJobListener listener) {
	dataJobListenerList.add(listener);
	
}

public void removeDataJobListener(DataJobListener listener) {
	dataJobListenerList.remove(listener);
	
}
}