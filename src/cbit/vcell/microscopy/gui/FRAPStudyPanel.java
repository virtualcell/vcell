package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import cbit.gui.DialogUtils;
import cbit.gui.ZEnforcer;
import cbit.gui.graph.GraphPane;
import cbit.image.ImageException;
import cbit.sql.KeyValue;
import cbit.util.AsynchProgressPopup;
import cbit.util.BeanUtils;
import cbit.util.Compare;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.data.NewClientPDEDataContext;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.SimulationModelInfo;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.server.MergedDataManager;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.desktop.controls.DataManager;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.microscopy.ExternalDataInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FrapDataAnalysisResults;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.ROI;
import cbit.vcell.microscopy.FRAPStudy.FRAPModelParameters;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.server.VCDataIdentifier;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.ode.ODESolverResultSet;

public class FRAPStudyPanel extends JPanel implements PropertyChangeListener{
	//Following final static variables are added in Jan 2008.
	public final static int INDEX_TAB_IMAGES = 0;
	public final static int INDEX_TAB_FITCURVE = 1;
//	public final static int INDEX_TAB_SPATIALMODEL = 2;
	public final static int INDEX_TAB_FITSPATIALMODEL = 2;
	public final static int INDEX_TAB_REPORT = 3;
	
	public static final String FRAPDATAPANEL_TABNAME = "Images";
	
	private FRAPStudy frapStudy = null; 
	private FRAPDataPanel frapDataPanel = null;
	private LocalWorkspace localWorkspace = null;
	private JTabbedPane jTabbedPane = null;
	private FRAPParametersPanel frapParametersPanel = null;
//	private JPanel modelPanel = null;
	private JPanel fitSpatialModelPanel = null;
//	private JPanel reportPanel = null;
//	private JPanel reportBasePanel = null;
//	private JCheckBox showReportListCheckBox = null;
//	private JScrollPane scrollReportPane = null;
//	private MultisourcePlotPane[] spatialAnalysisList = null;
	private JPanel jPanel = null;
//	private GraphPane geometryGraphPane = null;
//	private JRadioButton spatial_twoAndHalfDimRadioButton = null;
//	private JRadioButton spatial_threeDimRadioButton = null;
//	private JRadioButton spatial_twoDimRadioButton = null;
//	private JPanel geometryControlsPanel = null;
	
	private PDEDataViewer pdeDataViewer = null;
	private PDEDataViewer flourDataViewer = null;
	
	private ResultsSummaryPanel resultsSummaryPanel;
	
	enum DisplayChoice { PDE,EXTTIMEDATA};

	private static final int USER_CHANGES_FLAG_ALL = 0xFFFFFFFF;
	private static final int USER_CHANGES_FLAG_ROI = 0x01;
	private static final int USER_CHANGES_FLAG_MODEL_PARAMS = 0x02;
	
	//Elements that change the Model
	public static class FrapChangeInfo{
		public final boolean bROIValuesChanged;
		public final boolean bROISizeChanged;
		public final boolean bDiffusionRateChanged;
		public final String diffusionRateString;
		public final boolean bBleachWhileMonitorChanged;
		public final String bleachWhileMonitorRateString;
		public final boolean bMobileFractionChanged;
		public final String mobileFractionString;
		public final boolean bSlowerChanged;
		public final String slowerString;
		public final boolean bStartIndexForRecoveryChanged;
		public final String startIndexForRecoveryString;
		public FrapChangeInfo(boolean bROIValuesChanged,boolean bROISizeChanged,
				boolean bDiffusionRateChanged,String diffusionRateString,
				boolean bBleachWhileMonitorChanged,String bleachWhileMonitorRateString,
				boolean bMobileFractionChanged,String mobileFractionString,
				boolean bSlowerChanged, String slowerString,
				boolean bStartIndexForRecoveryChanged,String startIndexForRecoveryString){
			this.bROIValuesChanged = bROIValuesChanged;
			this.bROISizeChanged = bROISizeChanged;
			this.bDiffusionRateChanged = bDiffusionRateChanged;
			this.diffusionRateString = diffusionRateString;
			this.bBleachWhileMonitorChanged = bBleachWhileMonitorChanged;
			this.bleachWhileMonitorRateString = bleachWhileMonitorRateString;
			this.bMobileFractionChanged = bMobileFractionChanged;
			this.mobileFractionString = mobileFractionString;
			this.bSlowerChanged = bSlowerChanged;
			this.slowerString = slowerString;
			this.bStartIndexForRecoveryChanged = bStartIndexForRecoveryChanged;
			this.startIndexForRecoveryString = startIndexForRecoveryString;
			//Don't forget to change 'hasAnyChanges' if adding new parameters
		}
		public boolean hasAnyChanges(){
			return bROIValuesChanged || bROISizeChanged ||
				bDiffusionRateChanged || bBleachWhileMonitorChanged ||
				bMobileFractionChanged || bSlowerChanged || bStartIndexForRecoveryChanged;
		}
		public boolean hasROIChanged(){
			return bROISizeChanged || bROIValuesChanged;
		}
		public String getChangeDescription(){
			return
			(bROIValuesChanged?"(Cell,Bleach or Backgroung ROI)":" ")+
			(bROISizeChanged?"(Data Dimension)":" ")+
			(bDiffusionRateChanged?"(Diffusion Rate)":" ")+
			(bBleachWhileMonitorChanged?"(BleachWhileMonitoring Rate)":" ")+
			(bMobileFractionChanged?"(Mobile Fraction)":" ")+
			(bSlowerChanged?"(Slower Rate)":" ")+
			(bStartIndexForRecoveryChanged?"(Start Index for Recovery)":" ");

		}
	};
	public static class SavedFrapModelInfo{
		public final KeyValue savedSimKeyValue;
		public final ROI lastCellROI;
		public final ROI lastBleachROI;
		public final ROI lastBackgroundROI;
		public final String lastBaseDiffusionrate;
		public final String lastBleachWhileMonitoringRate;
		public final String lastMobileFraction;
		public final String lastSlowerRate;
		public final String startingIndexForRecovery;
		public SavedFrapModelInfo(
			KeyValue savedSimKeyValue,
			ROI lastCellROI,
			ROI lastBleachROI,
			ROI lastBackgroundROI,
			String lastBaseDiffusionrate,
			String lastBleachWhileMonitoringRate,
			String lastMobileFraction,
			String lastSlowerRate,
			String startingIndexForRecovery){
			if(savedSimKeyValue == null){
				throw new IllegalArgumentException("SimKey cannot be null for a saved FrapModel.");
			}
			this.savedSimKeyValue = savedSimKeyValue;
			this.lastCellROI = lastCellROI;
			this.lastBleachROI = lastBleachROI;
			this.lastBackgroundROI = lastBackgroundROI;
			this.lastBaseDiffusionrate = lastBaseDiffusionrate;
			this.lastBleachWhileMonitoringRate = lastBleachWhileMonitoringRate;
			this.lastMobileFraction = lastMobileFraction;
			this.lastSlowerRate = lastSlowerRate;
			this.startingIndexForRecovery = startingIndexForRecovery;
		}

	};
	private FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfoNew2 = null;

	private static final String SAVE_FILE_OPTION = "Save";
			
	private class CurrentSimulationDataState{
		private final FrapChangeInfo frapChangeInfo;
		private final boolean areSimeFilesOK;
		private final boolean areExtDataFileOK;
		public CurrentSimulationDataState() throws Exception{
			if(getSavedFrapModelInfo() == null){
				throw new Exception("CurrentSimulationDataState can't be determined because no savedFrapModelInfo available");
			}
			frapChangeInfo = getChangesSinceLastSave();
			areSimeFilesOK = areSimulationFilesOKForSavedModel();
			areExtDataFileOK =
				areExternalDataOK(getLocalWorkspace(),
				getFrapStudy().getFrapDataExternalDataInfo(),
				getFrapStudy().getRoiExternalDataInfo());
		}
		public boolean isDataInvalidBecauseModelChanged(){
			return frapChangeInfo.hasAnyChanges();
		}
		public boolean isDataInvalidBecauseMissingOrCorrupt(){
			return !areSimeFilesOK || !areExtDataFileOK;
		}
		public boolean isDataValid(){
			if(!frapChangeInfo.hasAnyChanges() && areSimeFilesOK && areExtDataFileOK){
				return true;
			}
			return false;
		}
		public String getDescription(){
			if(isDataValid()){
				return "Simulation Data are valid.";
			}else if(isDataInvalidBecauseModelChanged()){
				return "Sim Data invalid because Model has changes including:\n"+frapChangeInfo.getChangeDescription();
			}else if(isDataInvalidBecauseMissingOrCorrupt()){
				return "Sim Data are missing or corrupt";
			}
			throw new RuntimeException("Unknown description");
		}
	};

	private	DataSetControllerImpl.ProgressListener progressListenerZeroToOne =
		new DataSetControllerImpl.ProgressListener(){
			public void updateProgress(double progress) {
				VirtualFrapMainFrame.updateProgress((int)Math.round(progress*100));
			}
		};

	public static final int CURSOR_CELLROI = 0;
	public static final int CURSOR_BLEACHROI = 1;
	public static final int CURSOR_BACKGROUNDROI = 2;
	public static final Cursor[] ROI_CURSORS = new Cursor[]{
		Cursor.getDefaultCursor(),
		Cursor.getDefaultCursor(),
		Cursor.getDefaultCursor()
	};
	
	private UndoableEditSupport undoableEditSupport =
		new UndoableEditSupport();
	
	public static final UndoableEdit CLEAR_UNDOABLE_EDIT =
		new AbstractUndoableEdit(){
					public boolean canUndo() {
						return false;
					}
					public String getUndoPresentationName() {
						return null;
					}
					public void undo() throws CannotUndoException {
						super.undo();
					}
				};

	public static class MultiFileImportInfo{
		public final boolean isTimeSeries;
		public final double timeInterval;
		public final double zInterval;
		public MultiFileImportInfo(boolean isTimeSeries,double timeInterval,double zInterval){
			this.isTimeSeries = isTimeSeries;
			this.timeInterval = timeInterval;
			this.zInterval = zInterval;
		}
	}
	
	/**
	 * This method initializes 
	 * 
	 */
	public FRAPStudyPanel() {
		super();
		initialize();
		loadROICursors();
	}
	
	public void addUndoableEditListener(UndoableEditListener undoableEditListener){
		undoableEditSupport.addUndoableEditListener(undoableEditListener);
		getFRAPDataPanel().getOverlayEditorPanelJAI().setUndoableEditSupport(undoableEditSupport);
	}	
	
	private void setSavedFrapModelInfo(SavedFrapModelInfo savedFrapModelInfo) throws Exception{
		try{
			undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
		}catch(Exception e){
			e.printStackTrace();
		}
		savedFrapModelInfoNew2 = savedFrapModelInfo;
		getFRAPParametersPanel().initializeSavedFrapModelInfo(getSavedFrapModelInfo(),getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps());
		applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_MODEL_PARAMS);
	}
	private SavedFrapModelInfo getSavedFrapModelInfo(){
		return savedFrapModelInfoNew2;
	}
	public static void loadROICursors(){
		for (int i = 0; i < ROI_CURSORS.length; i++) {
			String cursorFileName = null;
			if(i == CURSOR_CELLROI){
				cursorFileName = FRAPStudyPanel.class.getResource("/images/cursorCellROI.gif").getFile();
			}else if(i == CURSOR_BLEACHROI){
				cursorFileName = FRAPStudyPanel.class.getResource("/images/cursorBleachROI.gif").getFile();
			}else if(i == CURSOR_BACKGROUNDROI){
				cursorFileName = FRAPStudyPanel.class.getResource("/images/cursorBackgroundROI.gif").getFile();
			}
			ImageIcon imageIcon = new ImageIcon(cursorFileName);
			Image cellCursorImage = imageIcon.getImage();
			BufferedImage tempImage =
				new BufferedImage(cellCursorImage.getWidth(null),cellCursorImage.getHeight(null),BufferedImage.TYPE_INT_ARGB);
			tempImage.createGraphics().drawImage(
				cellCursorImage, 0, 0, tempImage.getWidth(), tempImage.getHeight(), null);
			//outline with black
			for (int y = 0; y < tempImage.getHeight(); y++) {
				for (int x = 0; x < tempImage.getWidth(); x++) {
//					System.out.print(Integer.toHexString(tempImage.getRGB(x, y))+" ");
					if((tempImage.getRGB(x, y)&0x00FFFFFF) == 0){
						tempImage.setRGB(x, y, 0x00000000);
						int xoff = x-1;
						if(xoff >= 0 && (tempImage.getRGB(xoff, y)&0x00FFFFFF) != 0){
							tempImage.setRGB(x, y, 0xFF000000);
						}
						xoff = x+1;
						if(xoff < tempImage.getWidth() && (tempImage.getRGB(xoff, y)&0x00FFFFFF) != 0){
							tempImage.setRGB(x, y, 0xFF000000);
						}
						int yoff = y-1;
						if(yoff >= 0 && (tempImage.getRGB(x,yoff)&0x00FFFFFF) != 0){
							tempImage.setRGB(x,y, 0xFF000000);
						}
						yoff = y+1;
						if(yoff < tempImage.getHeight() && (tempImage.getRGB(x,yoff)&0x00FFFFFF) != 0){
							tempImage.setRGB(x,y, 0xFF000000);
						}
	
					}else{
						tempImage.setRGB(x, y, 0xFFFFFFFF);
					}
				}
//				System.out.println();
			}
			ROI_CURSORS[i] = Toolkit.getDefaultToolkit().createCustomCursor(tempImage, new Point(0,0), "cellCursor");
		}
	}
	public static SavedFrapModelInfo createSavedFrapModelInfo(FRAPStudy frapStudy) throws Exception{

		KeyValue savedSimKey =
			(frapStudy.getBioModel() == null
				?LocalWorkspace.createNewKeyValue()
				:frapStudy.getBioModel().getSimulations()[0].getSimulationVersion().getVersionKey());
		FRAPData frapData = frapStudy.getFrapData();
		FRAPModelParameters frapModelParameters = frapStudy.getFrapModelParameters();
		
		ROI savedCellROI = null;
		ROI savedBleachROI = null;
		ROI savedBackgroundROI = null;
		if(frapData != null){
			savedCellROI = frapData.getRoi(RoiType.ROI_CELL);
			savedCellROI = (savedCellROI == null?null:new ROI(savedCellROI));
			savedBleachROI = frapData.getRoi(RoiType.ROI_BLEACHED);
			savedBleachROI = (savedBleachROI == null?null:new ROI(savedBleachROI));
			savedBackgroundROI = frapData.getRoi(RoiType.ROI_BACKGROUND);
			savedBackgroundROI = (savedBackgroundROI == null?null:new ROI(savedBackgroundROI));
		}
		String savedDiffusionRate = null;
		String savedBleachWhileMonitoringRate = null;
		String savedMobileFraction = null;
		String savedSlowerRate = null;
		String startingIndexForRecovery = null;
		if(frapModelParameters != null){
			savedDiffusionRate = frapModelParameters.diffusionRate;
			savedBleachWhileMonitoringRate = frapModelParameters.monitorBleachRate;
			savedMobileFraction = frapModelParameters.mobileFraction;
			savedSlowerRate = frapModelParameters.slowerRate;
			startingIndexForRecovery = frapModelParameters.startIndexForRecovery;
		}
		return new SavedFrapModelInfo(
				savedSimKey,
				savedCellROI,savedBleachROI,savedBackgroundROI,
				savedDiffusionRate,
				savedBleachWhileMonitoringRate,
				savedMobileFraction,
				savedSlowerRate,
				startingIndexForRecovery
			);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(727, 607));
        this.setMinimumSize(new Dimension(640, 480));
        this.add(getJTabbedPane(), BorderLayout.CENTER);
//        this.add(getJPanel(), BorderLayout.SOUTH);
			
	}

	/**
	 * This method initializes FRAPDataPanel	
	 * 	
	 * @return cbit.vcell.virtualmicroscopy.gui.FRAPDataPanel	
	 */
	private FRAPDataPanel getFRAPDataPanel() {
		if (frapDataPanel == null) {
			frapDataPanel = new FRAPDataPanel();
			frapDataPanel.getOverlayEditorPanelJAI().addPropertyChangeListener(this);
		}
		return frapDataPanel;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	protected JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab(FRAPDATAPANEL_TABNAME, null, getFRAPDataPanel(), null);
			jTabbedPane.addTab("Fit Recovery Curve", null, getFRAPParametersPanel(), null);
//			jTabbedPane.addTab("Generate Spatial Model", null, getModelPanel(), null);
			jTabbedPane.addTab("Fit Spatial Model", null, getFitSpatialModelPanel(), null);
			jTabbedPane.addTab("Report", null, getResultsSummaryPanel()/*getScrollReportPane()*/, null);
			jTabbedPane.setModel(
				new DefaultSingleSelectionModel(){
					@Override
					public void setSelectedIndex(int index) {
						try{
							if(updateTabbedView(getSelectedIndex(),index)){
								super.setSelectedIndex(index);
							}
						}catch(Exception e){
							DialogUtils.showWarningDialog(
								FRAPStudyPanel.this, "Can't switch view beacause:\n"+e.getMessage(),
								new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
						}finally{
							getFRAPDataPanel().getOverlayEditorPanelJAI().updateROICursor();
						}

					}
				}
			);
			jTabbedPane.setSelectedIndex(FRAPStudyPanel.INDEX_TAB_IMAGES);
		}
		return jTabbedPane;
	}

	private boolean checkROIConstraints() throws Exception{
		short[] cellPixels = getFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL).getPixelsXYZ();
		short[] bleachPixels = getFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED).getPixelsXYZ();
		short[] backgroundPixels = getFrapStudy().getFrapData().getRoi(RoiType.ROI_BACKGROUND).getPixelsXYZ();
		boolean bFixedBleach = false;
		boolean bFixedBackground = false;
		for (int i = 0; i < cellPixels.length; i++) {
			if(cellPixels[i] == 0 && bleachPixels[i] != 0){
				bFixedBleach = true;
				bleachPixels[i] = 0;
			}
			if(cellPixels[i] != 0 && backgroundPixels[i] != 0){
				bFixedBackground = true;
				backgroundPixels[i] = 0;
			}
		}
		if(bFixedBackground || bFixedBleach){
			final String FIX_AUTO = "Fix Automatically";
			String result = DialogUtils.showWarningDialog(this,
					(bFixedBleach?"Bleach ROI extends beyond Cell ROI":"")+
					(bFixedBackground &&bFixedBleach?" and" :"")+
					(bFixedBackground?"Background ROI overlaps Cell ROI":"")+
					".  Ensure that the Bleach ROI is completely inside the Cell ROI and the Background ROI is completely outside the Cell ROI.",
					new String[] {FIX_AUTO,UserMessage.OPTION_CANCEL}, FIX_AUTO);
			if(result != null && result.equals(FIX_AUTO)){
				if(bFixedBleach){
					UShortImage ushortImage =
						new UShortImage(bleachPixels,getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getX(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getY(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getZ());
					ROI newBleachROI = new ROI(ushortImage,RoiType.ROI_BLEACHED);
					getFrapStudy().getFrapData().addReplaceRoi(newBleachROI);
				}
				if(bFixedBackground){
					UShortImage ushortImage =
						new UShortImage(backgroundPixels,getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getX(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getY(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getZ());
					ROI newBackgroundROI = new ROI(ushortImage,RoiType.ROI_BACKGROUND);
					getFrapStudy().getFrapData().addReplaceRoi(newBackgroundROI);
				}
			}
			return true;
		}
		return false;
	}
	private boolean updateTabbedView(int exitTab,int enterTab) throws Exception{
		if(enterTab != FRAPStudyPanel.INDEX_TAB_IMAGES && (getFrapStudy() == null || getFrapStudy().getFrapData() == null)){
			throw new Exception("No document open.  Use 'File->Open' menu to open a new document");
		}
		try{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if(exitTab == FRAPStudyPanel.INDEX_TAB_IMAGES){
				applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ROI);
				if(getFrapStudy() != null && getFrapStudy().getFrapData() != null && checkROIConstraints()){
					return false;
				}
			}else if(exitTab == FRAPStudyPanel.INDEX_TAB_FITCURVE){
				applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_MODEL_PARAMS);
			}
			
			if(enterTab == FRAPStudyPanel.INDEX_TAB_FITCURVE){
				try{
					getFRAPParametersPanel().refreshFRAPModelParameterEstimates(getFrapStudy().getFrapData());
				}catch(Exception e){
					DialogUtils.showWarningDialog(this, 
							"Some FRAP Model Parameter Estimation help won't be available because:\n"+e.getMessage(),
							new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
				}
			}
//			else if(enterTab == FRAPStudyPanel.INDEX_TAB_SPATIALMODEL){
//				refreshBiomodel();
//			}
			else if(enterTab == FRAPStudyPanel.INDEX_TAB_FITSPATIALMODEL){
				refreshBiomodel();
				CurrentSimulationDataState currentSimulationDataState = null;
				if(getSavedFrapModelInfo() != null){
					currentSimulationDataState = new CurrentSimulationDataState();
				}
				if(currentSimulationDataState == null || !currentSimulationDataState.isDataValid()){
					final String RUN_SIM = "Run Simulation...";
					if(currentSimulationDataState != null && currentSimulationDataState.isDataInvalidBecauseModelChanged()){
						String result = DialogUtils.showWarningDialog(this,
							currentSimulationDataState.getDescription()+"\n"+
							"Simulation needs to be Run before viewing results, Run Simulation now?" ,
							new String[] {RUN_SIM,UserMessage.OPTION_CANCEL}, RUN_SIM);
						if(result == null || !result.equals(RUN_SIM)){
							return false;
						}
					
					}else if(currentSimulationDataState != null && currentSimulationDataState.isDataInvalidBecauseMissingOrCorrupt()){
						String expectedSimFileLocation =
							getLocalWorkspace().getDefaultSimDataDirectory();
						String result = DialogUtils.showWarningDialog(this,
							"Couldn't find all simulation data in directory:\n"+expectedSimFileLocation+"\n"+
							"Simulation needs to be Run before viewing results, Run Simulation now?" ,
							new String[] {RUN_SIM,UserMessage.OPTION_CANCEL}, RUN_SIM);
						if(result == null || !result.equals(RUN_SIM)){
							return false;
						}
					}
					runSimulation();
//					clearFitSpatialModelPanel();
					
					return false;
				}else{
					refreshPDEDisplay(DisplayChoice.PDE);
					refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);
				}
			}else if(enterTab == FRAPStudyPanel.INDEX_TAB_REPORT){
				refreshBiomodel();
				CurrentSimulationDataState currentSimulationDataState = new CurrentSimulationDataState();
				if(currentSimulationDataState.isDataValid()){
					if(!getResultsSummaryPanel().hasData()){
//						getReportPanel().removeAll();
						spatialAnalysis();
						return false;
					}
				}else{
					throw new Exception("Simulation Data are not valid. Simulation needs to be run.\n"+
						currentSimulationDataState.getDescription());
				}
			}
			return true;
		}finally{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getDefaultCursor());
			getFRAPDataPanel().getOverlayEditorPanelJAI().updateROICursor();
		}
		
	}

	/**
	 * Changed in Feb, 2008. GridBagLayout to BorderLayout.
	 * Set splitPane and put MultisourcePlotPane on top and the scrollText, equation and radio button at bottom	
	 * @return javax.swing.JPanel	
	 */
	private FRAPParametersPanel getFRAPParametersPanel() {
		if (frapParametersPanel == null) {
			frapParametersPanel = new FRAPParametersPanel();
		}
		return frapParametersPanel;
	}

//	/**
//	 * This method initializes modelPanel	
//	 * 	
//	 * @return javax.swing.JPanel	
//	 */
//	private JPanel getModelPanel() {
//		if (modelPanel == null) {
//			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
//			gridBagConstraints10.gridx = 0;
//			gridBagConstraints10.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints10.gridy = 0;
//			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
//			gridBagConstraints4.gridx = 0;
//			gridBagConstraints4.gridheight = 1;
//			gridBagConstraints4.weightx = 0.0D;
//			gridBagConstraints4.weighty = 0.0D;
//			gridBagConstraints4.gridy = 2;
//			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
//			gridBagConstraints5.gridx = 0;
//			gridBagConstraints5.weightx = 1.0D;
//			gridBagConstraints5.weighty = 1.0D;
//			gridBagConstraints5.fill = GridBagConstraints.BOTH;
//			gridBagConstraints5.insets = new Insets(10, 10, 10, 10);
//			gridBagConstraints5.gridwidth = 2;
//			gridBagConstraints5.gridy = 1;
//			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
//			gridBagConstraints3.gridx = 1;
//			gridBagConstraints3.gridwidth = 1;
//			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints3.gridy = 2;
//			modelPanel = new JPanel();
//			modelPanel.setLayout(new GridBagLayout());
//			modelPanel.add(getGeometryControlsPanel(), gridBagConstraints10);
//			modelPanel.add(getGeometryGraphPane(), gridBagConstraints5);
////			modelPanel.add(getModelSpecPanel(), gridBagConstraints4);
//			//Commentted in Feb 2008. It can be found in menu and toolbar
////			modelPanel.add(getRefreshModelButton(), gridBagConstraints3);
//		}
//		return modelPanel;
//	}

	/**
	 * This method initializes fitSpatialModelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFitSpatialModelPanel() {
		if (fitSpatialModelPanel == null) {
			fitSpatialModelPanel = new JPanel();
			fitSpatialModelPanel.setLayout(new BorderLayout());
			fitSpatialModelPanel.add(getSplitDataViewers(), BorderLayout.CENTER);
		}
		return fitSpatialModelPanel;
	}

//	/**
//	 * This method initializes reportPanel	
//	 * 	
//	 * @return javax.swing.JPanel	
//	 */
//	private JPanel getReportPanel() {
//		if (reportPanel == null) {
//			reportPanel = new JPanel();
//			reportPanel.setLayout(new FlowLayout());
//			reportPanel.setMaximumSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.8), 5000));
//			reportPanel.setPreferredSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.8), 5000));
//		}
//		return reportPanel;
//	}

//	private JScrollPane getScrollReportPane()
//	{
//		if (scrollReportPane == null) {
//			scrollReportPane = new JScrollPane();
//			scrollReportPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//			scrollReportPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//			scrollReportPane.setMinimumSize(new Dimension(30, 75));
//			scrollReportPane.setViewportView(getReportPanel());
//		}
//		return scrollReportPane;
//	}
	
	private ResultsSummaryPanel getResultsSummaryPanel(){
		if(resultsSummaryPanel == null){
			resultsSummaryPanel = new ResultsSummaryPanel();
		}
		return resultsSummaryPanel;
	}
//	//refresh the report panel when spatial analysis is done.
//	private void refreshReportPanel()
//	{
//		getReportPanel().removeAll();
//		if(spatialAnalysisList != null && spatialAnalysisList.length > 0)
//		{
//			for(int i = 0; i< spatialAnalysisList.length; i++)
//			{
//				spatialAnalysisList[i].setPreferredSize(new Dimension(650,500));
//				getReportPanel().add(spatialAnalysisList[i]/*, gbConstraints[i]*/);
//			}
//		}
//		getReportBasePanel().removeAll();
//		getReportBasePanel().add(getShowReportListButton(), BorderLayout.NORTH);
//		getReportBasePanel().add(getReportPanel(), BorderLayout.CENTER);
//		getReportBasePanel().add(getResultsSummaryPanel(), BorderLayout.SOUTH);
//		JScrollPane baseScrollPane = ((JScrollPane)getJTabbedPane().getComponentAt(INDEX_TAB_REPORT));
//		baseScrollPane.setAutoscrolls(true);
//		//adjust report panel's width to scroll pane's width.
//		baseScrollPane.addComponentListener(new ComponentListener(){
//			public void componentHidden(ComponentEvent arg0) {
//				// TODO Auto-generated method stub
//			}
//			public void componentMoved(ComponentEvent arg0) {
//				// TODO Auto-generated method stub
//			}
//			public void componentResized(ComponentEvent arg0) {
//				getReportBasePanel().setMaximumSize(new Dimension(((int)(arg0.getComponent().getSize().getWidth()*0.8)), 5000));
//				getReportBasePanel().setPreferredSize(new Dimension(((int)(arg0.getComponent().getSize().getWidth()*0.8)), 5000));
//			}
//			public void componentShown(ComponentEvent arg0) {
//				// TODO Auto-generated method stub
//			}
//		});
//		((JScrollPane)getJTabbedPane().getComponentAt(INDEX_TAB_REPORT)).setViewportView(getReportBasePanel());
//		getShowReportListButton().setSelected(true); //show plot list by default
//	}
	
	private FRAPStudy getFrapStudy() {
		return frapStudy;
	}

	public void setFrapStudy(final FRAPStudy argFrapStudy,boolean bNew) {
		SwingUtilities.invokeLater(new Runnable(){public void run(){
			getResultsSummaryPanel().clearData();//spatialAnalysisList = null;
		}});
		VirtualFrapMainFrame.enableSave(!(argFrapStudy == null || argFrapStudy.getFrapData() == null));
		try{
			undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
		}catch(Exception e){
			e.printStackTrace();
		}
		FRAPStudy oldFrapStudy = this.frapStudy;
		if(oldFrapStudy != null){
			oldFrapStudy.removePropertyChangeListener(this);
		}
		this.frapStudy = argFrapStudy;
		frapStudy.addPropertyChangeListener(this);
		getFRAPDataPanel().setFrapStudyNew(frapStudy,bNew);
	}
	
	protected void crop(Rectangle cropRectangle) throws ImageException {
		if (getFrapStudy() == null || getFrapStudy().getFrapData()==null){
			return;
		}
		getFRAPDataPanel().getOverlayEditorPanelJAI().saveUserChangesToROI();
		FRAPData frapData = getFrapStudy().getFrapData();
		FRAPData newFrapData = frapData.crop(cropRectangle);
		FRAPStudy newFrapStudy = new FRAPStudy();
		newFrapStudy.setFrapData(newFrapData);
		newFrapStudy.setXmlFilename(getFrapStudy().getXmlFilename());
		newFrapStudy.setFrapDataExternalDataInfo(getFrapStudy().getFrapDataExternalDataInfo());
		newFrapStudy.setRoiExternalDataInfo(getFrapStudy().getRoiExternalDataInfo());
		setFrapStudy(newFrapStudy,false);
	}

	
//	/**
//	 * This method initializes jPanel	
//	 * 	
//	 * @return javax.swing.JPanel	
//	 */
//	private JPanel getJPanel() {
//		if (jPanel == null) {
//			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
//			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints2.gridx = 2;
//			gridBagConstraints2.gridy = 0;
//			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
//			gridBagConstraints1.gridx = 1;
//			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints1.gridy = 0;
//			jPanel = new JPanel();
//			jPanel.setLayout(new GridBagLayout());
//		}
//		return jPanel;
//	}

	
	public void save() throws Exception {
		if(SwingUtilities.isEventDispatchThread()){
			throw new Exception("Save not EventDispatchThread");
		}
		if(getFrapStudy().getFrapData() != null){
			saveAsInternal(getFrapStudy().getXmlFilename());
//			try{
//				refreshBiomodel();
//			}catch(Exception e){
//				//save whatever we have
//			}
//			AsynchClientTask saveTask = createSaveTask(false,true);
//			ClientTaskDispatcher.dispatch(
//				this, new Hashtable<String, Object>(),
//				new AsynchClientTask[] {saveTask}, false);
		}else{
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				DialogUtils.showErrorDialog("No FRAP Data exists to save");
			}});
		}
	}
	
	public void saveAs() throws Exception{
		if(SwingUtilities.isEventDispatchThread()){
			throw new Exception("SaveAs not EventDispatchThread");
		}
		if(getFrapStudy().getFrapData() != null){
			if(getJTabbedPane().getSelectedIndex() == INDEX_TAB_FITSPATIALMODEL ||
					getJTabbedPane().getSelectedIndex() == INDEX_TAB_REPORT){
				//SaveASNew has empty sim data so can't be viewing these tabs after save
				final String CONTINUE_SAVING = "Switch and continue SaveAsNew";
				String result = DialogUtils.showWarningDialog(this,
						"Simulation Data will be empty for the new document.  Switch to '"+FRAPDATAPANEL_TABNAME+"' tab before saving?" ,
						new String[] {CONTINUE_SAVING,UserMessage.OPTION_CANCEL}, CONTINUE_SAVING);
				if(result == null || !result.equals(CONTINUE_SAVING)){
					return;
				}
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){getJTabbedPane().setSelectedIndex(INDEX_TAB_IMAGES);}});
			}
			saveAsInternal(null);
//			try{
//				refreshBiomodel();
//			}catch(Exception e){
//				//Save whatever we have
//			}
//			AsynchClientTask saveTask = createSaveTask(true,true);
//			ClientTaskDispatcher.dispatch(
//				this, new Hashtable<String, Object>(),
//				new AsynchClientTask[] {saveTask}, false);
		}else{
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				DialogUtils.showErrorDialog("No FRAP Data exists to save");
			}});
		}
	}
	private void applyUserChangesToCurrentFRAPStudy(int applyUserChangeFlags) throws Exception{
		if((applyUserChangeFlags&USER_CHANGES_FLAG_ROI) != 0){
			getFRAPDataPanel().saveROI();
		}
		if((applyUserChangeFlags&USER_CHANGES_FLAG_MODEL_PARAMS) != 0){
			getFRAPParametersPanel().insertFRAPModelParametersIntoFRAPStudy(frapStudy);	
		}
	}
	private void saveAsInternal(String saveToFileName) throws Exception {

		applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ALL);
		boolean bSaveAs = saveToFileName == null;
		File outputFile = null;
		if(bSaveAs){
			final int[] retvalArr = new int[1];
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				retvalArr[0] = VirtualFrapLoader.saveFileChooser.showSaveDialog(FRAPStudyPanel.this);
			}});
			if (retvalArr[0] == JFileChooser.APPROVE_OPTION){
				String outputFileName = VirtualFrapLoader.saveFileChooser.getSelectedFile().getPath();
				outputFile = new File(outputFileName);
				if(!VirtualFrapLoader.filter_vfrap.accept(outputFile)){
					if(outputFile.getName().indexOf(".") == -1){
						outputFile = new File(outputFile.getParentFile(),outputFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
					}else{
						throw new Exception("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);
					}
				}
			}else{
				throw UserCancelException.CANCEL_GENERIC;
			}
		}else{
			outputFile = new File(saveToFileName);
		}
		if(bSaveAs && outputFile.exists()){
			if(Compare.isEqualOrNull(outputFile.getAbsolutePath(), getFrapStudy().getXmlFilename())){
				throw new Exception("File name is same.  Use 'Save' to update current document file.");
			}
			final String OK_OPTION = "OK";
			final String[] resultArr = new String[1];
			final File outputFileFinal = outputFile;
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				resultArr[0] = DialogUtils.showWarningDialog(FRAPStudyPanel.this, "OverWrite file\n"+outputFileFinal.getAbsolutePath(),
						new String[] {OK_OPTION,"Cancel"}, "Cancel");
			}});
			if(!resultArr[0].equals(OK_OPTION)){
				throw UserCancelException.CANCEL_GENERIC;
			}
			//Remove overwritten vfrap document external and simulation files
			try{
				MicroscopyXmlReader.ExternalDataAndSimulationInfo externalDataAndSimulationInfo =
					MicroscopyXmlReader.getExternalDataAndSimulationInfo(outputFileFinal);
				removeExternalDataAndSimulationFiles(
						externalDataAndSimulationInfo.simulationKey,
						(externalDataAndSimulationInfo.frapDataExtDataInfo != null
							?externalDataAndSimulationInfo.frapDataExtDataInfo.getExternalDataIdentifier():null),
						(externalDataAndSimulationInfo.roiExtDataInfo != null
							?externalDataAndSimulationInfo.roiExtDataInfo.getExternalDataIdentifier():null),
						getLocalWorkspace());
			}catch(Exception e){
				System.out.println(
					"Error deleting externalData and simulation files for overwritten vfrap document "+
					outputFileFinal.getAbsolutePath()+"  "+e.getMessage());
				e.printStackTrace();
			}
		}
		saveProcedure(outputFile,bSaveAs);
	}
	
	private void saveProcedure(File xmlFrapFileName,boolean bSaveAsNew) throws Exception{
		VirtualFrapMainFrame.updateStatus("Saving file " + xmlFrapFileName.getAbsolutePath()+" ...");
		
		BioModel newBioModel = null;
		try{
			newBioModel = FRAPStudy.createNewBioModel(getFrapStudy(),
				FRAPStudy.DEFAULT_SPECIES_COUNT,
				new Double(getFrapStudy().getFrapModelParameters().diffusionRate),
				getFrapStudy().getFrapModelParameters().monitorBleachRate,
				(bSaveAsNew || getSavedFrapModelInfo() == null || getSavedFrapModelInfo().savedSimKeyValue == null
						?LocalWorkspace.createNewKeyValue()
						:getSavedFrapModelInfo().savedSimKeyValue),
				LocalWorkspace.getDefaultOwner(),
				new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
			getFrapStudy().setBioModel(newBioModel);
		}catch(Exception e){
			getFrapStudy().setBioModel(null);
		}
		

		if(bSaveAsNew || getSavedFrapModelInfo() == null || cleanupSavedSimDataFilesIfNotOK()){
			//Replace with new external data IDs for use in 'run sim'
			getFrapStudy().setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
			getFrapStudy().setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
			
		}
		
		MicroscopyXmlproducer.writeXMLFile(getFrapStudy(), xmlFrapFileName, true,progressListenerZeroToOne,VirtualFrapMainFrame.SAVE_COMPRESSED);
		getFrapStudy().setXmlFilename(xmlFrapFileName.getAbsolutePath());
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			try{
			setSavedFrapModelInfo(FRAPStudyPanel.createSavedFrapModelInfo(getFrapStudy()));	
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}});
		VirtualFrapMainFrame.updateStatus("File " + xmlFrapFileName.getAbsolutePath()+" has been saved.");
        VirtualFrapLoader.mf.setMainFrameTitle(xmlFrapFileName.getName());
        VirtualFrapMainFrame.updateProgress(0);
	}
	private boolean cleanupSavedSimDataFilesIfNotOK() throws Exception{
		CurrentSimulationDataState currentSimulationDataState =
			new CurrentSimulationDataState();
		if(!currentSimulationDataState.isDataValid()){
			//remove saved simulation files (they no longer apply to this model)
			ExternalDataIdentifier frapDataExtDataId =
				(getFrapStudy() != null && getFrapStudy().getFrapDataExternalDataInfo() != null
					?getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier():null);
			ExternalDataIdentifier roiExtDataId =
				(getFrapStudy() != null && getFrapStudy().getRoiExternalDataInfo() != null
					?getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier():null);
			KeyValue simulationKeyValue =
				(getSavedFrapModelInfo() != null?getSavedFrapModelInfo().savedSimKeyValue:null);
			removeExternalDataAndSimulationFiles(simulationKeyValue,frapDataExtDataId,roiExtDataId,getLocalWorkspace());
			return true;
		}
		return false;
	}
	
	private static void removeExternalDataAndSimulationFiles(
			KeyValue simulationKeyValue,
			ExternalDataIdentifier frapDataExtDataId,ExternalDataIdentifier roiExtDataId,
			LocalWorkspace localWorkspace) throws Exception{
		
		if(frapDataExtDataId != null){
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,frapDataExtDataId);
		}
		if(roiExtDataId != null){
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,roiExtDataId);
		}
		if(frapDataExtDataId != null && roiExtDataId != null){
			File mergedFunctionFile = 
				getMergedFunctionFile(frapDataExtDataId,roiExtDataId,
						new File(localWorkspace.getDefaultSimDataDirectory()));
			if(mergedFunctionFile != null){
				mergedFunctionFile.delete();
			}
		}
		if(simulationKeyValue != null){
			File userDir =
				new File(localWorkspace.getDefaultSimDataDirectory());
			deleteSimulationFiles(userDir, simulationKeyValue);
		}
	}
	
	private void clearCurrentLoadState() throws Exception{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			setFrapStudy(new FRAPStudy(), true);						
            VirtualFrapLoader.mf.setMainFrameTitle("");
            getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_IMAGES);
		}});
	}
	protected void load(final File[] inFileArr,final MultiFileImportInfo multiFileImportInfo) throws Exception{
				
		final String inFileDescription =
			(inFileArr.length == 1
				?"file "+inFileArr[0].getAbsolutePath()
				:"files from "+inFileArr[0].getParentFile().getAbsolutePath());

		final AsynchProgressPopup pp =
			new AsynchProgressPopup(
				FRAPStudyPanel.this,
				"Loading FRAP data...",
				"Working...",true,true
		);
		pp.start();
//		final boolean[] bTest = new boolean[1];
//		bTest[0] = true;
		new Thread(new Runnable(){
			public void run(){
//				try{
//					
//				}catch(UserCancelException e){
//					return;
//				}catch(Exception e){
//					//Shouldn't happen
//					DialogUtils.showErrorDialog("Error saving before run simulation\n"+e.getMessage());
//					return;
//				}

				try {
					saveIfNeeded();
//					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
//						setFrapStudy(new FRAPStudy(), true);						
//			            VirtualFrapLoader.mf.setMainFrameTitle("");
//			            getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_IMAGES);
//					}});

					
					final DataSetControllerImpl.ProgressListener loadFileProgressListener =
						new DataSetControllerImpl.ProgressListener(){
							public void updateProgress(double progress) {
								int percentProgress = (int)(progress*100);
								VirtualFrapMainFrame.updateProgress(percentProgress);
								pp.setProgress(percentProgress);
							}
					};

					final String LOADING_MESSAGE = "Loading "+inFileDescription+"...";
					VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
					pp.setMessage(LOADING_MESSAGE);
					
					FRAPStudy newFRAPStudy = null;
					SavedFrapModelInfo newSavedFrapModelInfo = null;
					String newVFRAPFileName = null;
					if(inFileArr.length == 1){
						File inFile = inFileArr[0];
						if(inFile.getName().endsWith("."+VirtualFrapLoader.VFRAP_EXTENSION) ||
							inFile.getName().endsWith(".xml")){
							clearCurrentLoadState();
							String xmlString = XmlUtil.getXMLString(inFile.getAbsolutePath());
							MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
							newFRAPStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null),loadFileProgressListener);
							if(!areExternalDataOK(getLocalWorkspace(),newFRAPStudy.getFrapDataExternalDataInfo(),newFRAPStudy.getRoiExternalDataInfo())){
								newFRAPStudy.setFrapDataExternalDataInfo(null);
								newFRAPStudy.setRoiExternalDataInfo(null);
							}
							newSavedFrapModelInfo = FRAPStudyPanel.createSavedFrapModelInfo(newFRAPStudy);
							newVFRAPFileName = inFile.getAbsolutePath();
						}else if(inFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)){
							DataIdentifier[] dataIdentifiers =
								FRAPData.getDataIdentiferListFromVCellSimulationData(inFile, 0);
							String[][] rowData = new String[dataIdentifiers.length][1];
							for (int i = 0; i < dataIdentifiers.length; i++) {
								if(dataIdentifiers[i].getVariableType().equals(VariableType.VOLUME)){
									rowData[i][0] = dataIdentifiers[i].getName();
								}
							}
							int[] selectedIndexArr =
								DialogUtils.showComponentOKCancelTableList(
									getFRAPDataPanel(),
									"Select Volume Variable",
									new String[] {"Volume Variable Name"},
									rowData, ListSelectionModel.SINGLE_SELECTION);
							if(selectedIndexArr != null && selectedIndexArr.length > 0){
								clearCurrentLoadState();
								FRAPData newFrapData = 
									FRAPData.importFRAPDataFromVCellSimulationData(inFile,
										dataIdentifiers[selectedIndexArr[0]].getName(),
										loadFileProgressListener);
								newFRAPStudy = new FRAPStudy();
								newFRAPStudy.setFrapData(newFrapData);
							}else{
								throw UserCancelException.CANCEL_GENERIC;
							}
						}else{
							clearCurrentLoadState();
							ImageLoadingProgress myImageLoadingProgress =
								new ImageLoadingProgress(){
									public void setSubProgress(double mbprog) {
										loadFileProgressListener.updateProgress(mbprog);
									}
							};
//	        				ImageLoadingProgress imgProgress = new ImageLoadingProgress();
//	        				imgProgress.addPropertyChangeListener(getFRAPDataPanel());
	            			ImageDataset imageDataset = ImageDatasetReader.readImageDataset(inFile.getAbsolutePath(), myImageLoadingProgress);
	            			FRAPData newFrapData = FRAPData.importFRAPDataFromImageDataSet(imageDataset);
							newFRAPStudy = new FRAPStudy();
							newFRAPStudy.setFrapData(newFrapData);
						}
					}else{
						clearCurrentLoadState();
	    				ImageLoadingProgress imgProgress = new ImageLoadingProgress();
	        			imgProgress.addPropertyChangeListener(getFRAPDataPanel());
	        			ImageDataset imageDataset = ImageDatasetReader.readImageDatasetFromMultiFiles(inFileArr, imgProgress, multiFileImportInfo.isTimeSeries, multiFileImportInfo.timeInterval, multiFileImportInfo.zInterval);
	        			FRAPData newFrapData = new FRAPData(imageDataset, new ROI.RoiType[] { RoiType.ROI_BLEACHED,RoiType.ROI_CELL,RoiType.ROI_BACKGROUND });
//	        			frapData.setOriginalGlobalScaleInfo(null);
						newFRAPStudy = new FRAPStudy();
						newFRAPStudy.setFrapData(newFrapData);
					}

					final FRAPStudy finalNewFrapStudy = newFRAPStudy;
					final SavedFrapModelInfo finalNewSavedFrapModelInfo = newSavedFrapModelInfo;
					final String finalNewVFRAPFileName = newVFRAPFileName;
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
						finalNewFrapStudy.setXmlFilename(finalNewVFRAPFileName);
						setFrapStudy(finalNewFrapStudy,true);
						try{
							setSavedFrapModelInfo(finalNewSavedFrapModelInfo);
						}catch(Exception e){
							throw new RuntimeException(e.getMessage());
						}
						
						FRAPStudyPanel.this.refreshUI();			

			            VirtualFrapLoader.mf.setMainFrameTitle((finalNewVFRAPFileName != null?finalNewVFRAPFileName:"New Document"));
					}});

		            VirtualFrapMainFrame.updateStatus("Loaded " + inFileDescription);
		            VirtualFrapMainFrame.updateProgress(0);
//					if(bTest[0]){throw new Exception("test exception");}

				}catch(UserCancelException uce){
					//ignore
				}catch(final Exception e){
					pp.stop();
					try{
						SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							setFrapStudy(new FRAPStudy(), true);						
				            VirtualFrapLoader.mf.setMainFrameTitle("");
						}});
					}catch(Exception e2){
						e.printStackTrace();
					}
					VirtualFrapMainFrame.updateProgress(0);
					VirtualFrapMainFrame.updateStatus("Failed loading " + inFileDescription+".");
					e.printStackTrace();
					try{
						SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							DialogUtils.showErrorDialog("Failed loading " + inFileDescription+":\n"+e.getMessage());
						}});
						}catch(Exception e2){
							e2.printStackTrace();
						}				
				}finally{
					pp.stop();
				}
		}}).start();
	}
	
	private void saveIfNeeded() throws Exception{
		if(getFrapStudy() == null || getFrapStudy().getFrapData() == null){
			return;
		}
		boolean bNeedsSave = false;
		FrapChangeInfo frapChangeInfo = null;
		final String DONT_SAVE_CONTINUE = "Don't Save, Continue";
		try{
			frapChangeInfo = getChangesSinceLastSave();//refreshBiomodel();
			bNeedsSave = frapChangeInfo != null && frapChangeInfo.hasAnyChanges();
		}catch(Exception e){
			String errorDecision = PopupGenerator.showWarningDialog(this, null,
				new UserMessage(
					"Current document will be overwritten and can't be saved because:\n"+e.getMessage(),
					new String[] {DONT_SAVE_CONTINUE,UserMessage.OPTION_CANCEL},DONT_SAVE_CONTINUE),
				null);
			if(errorDecision.equals(UserMessage.OPTION_CANCEL)){
				throw UserCancelException.CANCEL_GENERIC;
			}else{
				return;
			}
		}
    	if(bNeedsSave){
    		final String[] saveMessageArr = new String[1];
    		if(getSavedFrapModelInfo() != null){
    			saveMessageArr[0] = "Changes in current document will be overwritten, Save current document?\n"+
    				"Changes include "+frapChangeInfo.getChangeDescription();
    		}else{
    			saveMessageArr[0] = "Current unsaved document will be overwritten,  Save current document?";
    		}
			final String[] resultArr = new String[1];
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				resultArr[0] = PopupGenerator.showWarningDialog(FRAPStudyPanel.this, null,
						new UserMessage(saveMessageArr[0],
							new String[] {SAVE_FILE_OPTION,UserMessage.OPTION_SAVE_AS_NEW,DONT_SAVE_CONTINUE,UserMessage.OPTION_CANCEL},SAVE_FILE_OPTION),
						null);
			}});
			if(resultArr[0].equals(SAVE_FILE_OPTION)){
				save();
			}else if(resultArr[0].equals(UserMessage.OPTION_SAVE_AS_NEW)){
				saveAs();
			}else if(resultArr[0].equals(DONT_SAVE_CONTINUE)){
			}else if(resultArr[0].equals(UserMessage.OPTION_CANCEL)){
				throw UserCancelException.CANCEL_GENERIC;
			}else{
				throw new Exception("Unknown option in saveIfNeeded '"+resultArr[0]+"'");
			}
    	}
	}

	private Boolean areSimulationFilesOKForSavedModel(){
		if(getSavedFrapModelInfo() == null){
			return null;
		}
		String[] EXPECTED_SIM_EXTENSIONS =
			new String[] {
				SimDataConstants.ZIPFILE_EXTENSION,//may be more than 1 for big files
				SimDataConstants.FUNCTIONFILE_EXTENSION,
				".fvinput",
				SimDataConstants.LOGFILE_EXTENSION,
				SimDataConstants.MESHFILE_EXTENSION,
				".meshmetrics",
				".vcg",
				SimDataConstants.FIELDDATARESAMP_EXTENSION,//prebleach avg
				SimDataConstants.FIELDDATARESAMP_EXTENSION//postbleach avg
			};
		File[] simFiles = getSimulationFileNames(
			new File(getLocalWorkspace().getDefaultSimDataDirectory()),
			getSavedFrapModelInfo().savedSimKeyValue);
		//prebleach.fdat,postbleach.fdat,.vcg,.meshmetrics,.mesh,.log,.fvinput,.functions,.zip
		if(simFiles == null || simFiles.length < EXPECTED_SIM_EXTENSIONS.length){
			return false;
		}
		for (int i = 0; i < EXPECTED_SIM_EXTENSIONS.length; i++) {
			boolean bFound = false;
			for (int j = 0; j < simFiles.length; j++) {
				if(simFiles[j] != null && simFiles[j].getName().endsWith(EXPECTED_SIM_EXTENSIONS[i])){
					simFiles[j] = null;
					bFound = true;
					break;
				}
			}
			if(!bFound){
				return false;
			}
		}
		return true;
	}
	private static boolean areExternalDataOK(
			LocalWorkspace localWorkspace,ExternalDataInfo imgDataExtDataInfo,ExternalDataInfo roiExtDataInfo){
		if(imgDataExtDataInfo == null || imgDataExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] frapDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					imgDataExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;frapDataExtDataFiles != null && i < frapDataExtDataFiles.length; i++) {
			if(!frapDataExtDataFiles[i].exists()){
				return false;
			}
		}
		if(roiExtDataInfo == null || roiExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] roiDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					roiExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;roiDataExtDataFiles != null && i < roiDataExtDataFiles.length; i++) {
			if(!roiDataExtDataFiles[i].exists()){
				return false;
			}
		}

		return true;
	}
	
	private void genericProgress(final int numSeconds,final boolean[] genericProgressStopSignal){
		genericProgressStopSignal[0] = false;
		VirtualFrapMainFrame.updateProgress(0);
		Thread incrThread = new Thread(new Runnable(){
			public void run(){
				int counter = 0;
				while(true){
					if(genericProgressStopSignal[0]){
						VirtualFrapMainFrame.updateProgress(100);
						return;
					}
					counter++;
					VirtualFrapMainFrame.updateProgress(counter);
					if(counter == 100){
						return;
					}
					try{
						Thread.sleep(10*numSeconds);
					}catch(InterruptedException e){
						return;
					}
				}
			}
		});
		incrThread.start();
	}
	
	protected void runSimulation() throws Exception{
		
		
		
		final File simulationDataDir =
			new File(getLocalWorkspace().getDefaultSimDataDirectory());
		if(!simulationDataDir.exists()){
			final String CREATE_OPTION = "Create Directory";
			String result = DialogUtils.showWarningDialog(this,
					"Simulation data directory '"+
					getLocalWorkspace().getDefaultSimDataDirectory()+
					"' does not exits.  Create Simulation data directory now?",
					new String[] {CREATE_OPTION,UserMessage.OPTION_CANCEL}, CREATE_OPTION);
			if(result == null || !result.equals(CREATE_OPTION)){
				return;
			}
			if(!simulationDataDir.mkdirs()){
				DialogUtils.showWarningDialog(this,
						"Failed to create Simulation Data Directory\n"+
						simulationDataDir.getAbsolutePath()+"\n Simulation cannot run.",
						new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
				return;
			}
		}
		//save if the Model has changed
		final Boolean[] bSaveAsNew = new Boolean[] {null};
		try{
			FrapChangeInfo frapChangeInfo = refreshBiomodel();
			boolean bNeedsExtData =
				!areExternalDataOK(getLocalWorkspace(),
					frapStudy.getFrapDataExternalDataInfo(),frapStudy.getRoiExternalDataInfo());
			if(frapChangeInfo.hasAnyChanges() || bNeedsExtData){
				bSaveAsNew[0] = new Boolean(false);
				if(frapChangeInfo.hasAnyChanges()){
		    		String saveMessage = null;
		    		if(getSavedFrapModelInfo() != null){
		    			saveMessage = "Current document has changes that must be saved before running simulation.\n"+
		    				"Changes include "+frapChangeInfo.getChangeDescription();
		    		}else{
		    			saveMessage = "Current unsaved document must be saved before running simulation.";
		    		}

					String result = DialogUtils.showWarningDialog(
						this,
						saveMessage,
						(getSavedFrapModelInfo() == null
							?new String[] {UserMessage.OPTION_SAVE_AS_NEW,UserMessage.OPTION_CANCEL}
							:new String[] {SAVE_FILE_OPTION,UserMessage.OPTION_SAVE_AS_NEW,UserMessage.OPTION_CANCEL})
						
						,UserMessage.OPTION_OK);
					if(result.equals(SAVE_FILE_OPTION)){
						bSaveAsNew[0] = new Boolean(false);
					}else if(result.equals(UserMessage.OPTION_SAVE_AS_NEW)){
						bSaveAsNew[0] = new Boolean(true);
					}else{
						return;
					}
				}
			}
		}catch(Exception e){
			throw new Exception("Error checking save before running simulation:\n"+e.getMessage());
		}

//		final boolean[] genericProgressStopSignal = new boolean[1];

		final AsynchProgressPopup pp =
			new AsynchProgressPopup(
				FRAPStudyPanel.this,
				"Running FRAP Model Simulation",
				"Working...",true,true
		);
		pp.start();
		
		new Thread(new Runnable(){
			public void run(){
				try {
					if(bSaveAsNew[0] != null){
						final String SAVING_BEFORE_RUN_MESSAGE = "Saving before simulation runs...";
						VirtualFrapMainFrame.updateStatus(SAVING_BEFORE_RUN_MESSAGE);
						pp.setMessage(SAVING_BEFORE_RUN_MESSAGE);
						if(bSaveAsNew[0]){
							saveAs();
						}else {
							save();
						}
					}
					// Reset spatial analysis
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
						getResultsSummaryPanel().clearData();//spatialAnalysisList = null;
					}});
					final String SAVING_EXT_DATA_MESSAGE = "Saving ROI and initial conditions...";
					VirtualFrapMainFrame.updateStatus(SAVING_EXT_DATA_MESSAGE);
					pp.setMessage(SAVING_EXT_DATA_MESSAGE);
					VirtualFrapMainFrame.updateProgress(0);
//					genericProgress(10,genericProgressStopSignal);
					if(getSavedFrapModelInfo() == null || cleanupSavedSimDataFilesIfNotOK()){
					getFrapStudy().saveROIsAsExternalData(localWorkspace,
						getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
					getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
						getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
					}
//					genericProgressStopSignal[0] = true;
					
					DataSetControllerImpl.ProgressListener runSimProgressListener =
						new DataSetControllerImpl.ProgressListener(){
							public void updateProgress(double progress) {
								int percentProgress = (int)(progress*100);
								VirtualFrapMainFrame.updateProgress(percentProgress);
								pp.setProgress(percentProgress);
							}
					};
					final String RUNNING_SIM_MESSAGE = "Running simulation...";
					VirtualFrapMainFrame.updateStatus(RUNNING_SIM_MESSAGE);
					pp.setMessage(RUNNING_SIM_MESSAGE);
					Simulation simulation =
						getFrapStudy().getBioModel().getSimulations()[0];
					FRAPStudy.runFVSolverStandalone(
						simulationDataDir,
						new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
						simulation,
						getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
						getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
						runSimProgressListener);
					
//					VirtualFrapMainFrame.updateStatus("Updating Simulation Data display.");
//					genericProgress(3,genericProgressStopSignal);
//					refreshPDEDisplay(DisplayChoice.PDE);//upper data viewer has simulated data and masks
//					refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);//lower data viewer has original data and masks
//					genericProgressStopSignal[0] = true;
					
					final String FINISHED_MESSAGE = "Finished simulation, updating display...";
					VirtualFrapMainFrame.updateStatus(FINISHED_MESSAGE);
					pp.setMessage(FINISHED_MESSAGE);
					VirtualFrapMainFrame.updateProgress(100);
					pp.setProgress(100);
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
						getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_FITSPATIALMODEL);
						VirtualFrapMainFrame.updateProgress(0);
						VirtualFrapMainFrame.updateStatus("");
					}});
					
				}catch(UserCancelException uce){
					//ignore
				}catch (final Exception e) {
					pp.stop();
//					genericProgressStopSignal[0] = true;
					VirtualFrapMainFrame.updateProgress(0);
					VirtualFrapMainFrame.updateStatus("Error running simulation: "+e.getMessage());
					e.printStackTrace();
					try{
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
						DialogUtils.showErrorDialog("Error running simulation:\n"+e.getMessage());
					}});
					}catch(Exception e2){
						e2.printStackTrace();
					}
				}finally{
					pp.stop();
				}

			}
		}).start();
	}
	
	private static File[] getSimulationFileNames(File rootDir,KeyValue simKey){
		final String deleteTheseSimID = Simulation.createSimulationID(simKey);
		return
			rootDir.listFiles(
				new FileFilter(){
					public boolean accept(File pathname) {
						if (pathname.getName().startsWith(deleteTheseSimID)){
							return true;
						}
						return false;
					}
				}
			);
	}
	private static void deleteSimulationFiles(File rootDir,KeyValue simKey){
		File[] oldSimFilesToDelete = getSimulationFileNames(rootDir,simKey);
		for (int i = 0; oldSimFilesToDelete != null && i < oldSimFilesToDelete.length; i++) {
			System.out.println("delete "+oldSimFilesToDelete[i].delete()+"  "+oldSimFilesToDelete[i].getAbsolutePath());
		}
	}
	private static File getMergedFunctionFile(
		ExternalDataIdentifier frapDataExtDataId,ExternalDataIdentifier roiExtDataId,
		File simDataDirectory){
		MergedDataInfo mergedDataInfo =
			new MergedDataInfo(LocalWorkspace.getDefaultOwner(),
				new VCDataIdentifier[]{frapDataExtDataId,roiExtDataId});
		return
			new File(simDataDirectory,
				mergedDataInfo.getID()+SimDataConstants.FUNCTIONFILE_EXTENSION);
	}
	
	
	protected void refreshPDEDisplay(DisplayChoice choice) throws Exception {
		Simulation sim = null;
		if (getFrapStudy()==null || getFrapStudy().getBioModel()==null || getFrapStudy().getBioModel().getSimulations()==null){
			return;
		}else{
			sim = getFrapStudy().getBioModel().getSimulations()[0];
		}
		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier());
		}
		
		DataManager dataManager = null;
		if (choice == DisplayChoice.PDE){
			int jobIndex = 0;
			SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
			dataManager = new PDEDataManager(getLocalWorkspace().getVCDataManager(), simJob.getVCDataIdentifier());
			PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);

			getPDEDataViewer().setSimulation(sim);
			getPDEDataViewer().setPdeDataContext(pdeDataContext);
			SimulationModelInfo simModelInfo =
				new SimulationWorkspaceModelInfo(
						getFrapStudy().getBioModel().getSimulationContext(sim),
						sim.getName()
				);
			getPDEDataViewer().setSimulationModelInfo(simModelInfo);
			try {
				getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getPDEDataViewer());
				((VirtualFrapWindowManager)getPDEDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getPDEDataViewer().repaint();
		}
		else if (choice == DisplayChoice.EXTTIMEDATA){
			
			ExternalDataIdentifier timeSeriesExtDataID = getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier();
			ExternalDataIdentifier maskExtDataID = getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier();
			VCDataIdentifier[] dataIDs = new VCDataIdentifier[] {timeSeriesExtDataID, maskExtDataID};
			VCDataIdentifier vcDataId = new MergedDataInfo(LocalWorkspace.getDefaultOwner(),dataIDs);
			dataManager = new MergedDataManager(getLocalWorkspace().getVCDataManager(),vcDataId);
			PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);
			// add function to display normalized fluorence data 
			Expression norm_fluor = new Expression("((Data2.cell_mask*(Data1.fluor/Data2.prebleach_avg))*(Data2.cell_mask > 0))");
			AnnotatedFunction[] func = {new AnnotatedFunction("norm_fluor", norm_fluor, null, VariableType.VOLUME, false)};
			boolean isExisted = false;
			for(int i=0; i < pdeDataContext.getFunctions().length; i++)
			{
				if(func[0].getName().equals(pdeDataContext.getFunctions()[i].getName()))
				{
					isExisted = true;
					break;
				}
			}
			if(!isExisted)
			{
				pdeDataContext.addFunctions(func, new boolean[] {false});
				pdeDataContext.refreshIdentifiers();
			}
			
			getFlourDataViewer().setSimulation(sim);
			getFlourDataViewer().setPdeDataContext(pdeDataContext);
			SimulationModelInfo simModelInfo =
				new SimulationWorkspaceModelInfo(
						getFrapStudy().getBioModel().getSimulationContext(sim),
						sim.getName()
				);
			getFlourDataViewer().setSimulationModelInfo(simModelInfo);
			try {
				getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getFlourDataViewer());
				((VirtualFrapWindowManager)getFlourDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getFlourDataViewer().repaint();
		}
	}
	
	private FrapChangeInfo getChangesSinceLastSave() throws Exception{
//		if (getFrapStudy() == null || getFrapStudy().getFrapData() == null){
//			if(getSavedFrapModelInfo() == null){
//				return null;
//			}
//			throw new Exception("Missing Frap Data Unexpected while refreshing BioModel");
//		}
		
		applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ALL);
		
		ROI lastCellROI = null;
		ROI lastBleachROI = null;
		ROI lastBackgroundROI = null;
		if(getSavedFrapModelInfo() != null){
			lastCellROI = getSavedFrapModelInfo().lastCellROI;
			lastBleachROI = getSavedFrapModelInfo().lastBleachROI;
			lastBackgroundROI = getSavedFrapModelInfo().lastBackgroundROI;
		}

		boolean bROISameSize =
			Compare.isEqualOrNull(
				getFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL).getISize(),
				(lastCellROI == null?null:lastCellROI.getISize()));
		boolean bCellROISame =
			Compare.isEqualOrNull(lastCellROI,getFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL));
		boolean bBleachROISame =
			Compare.isEqualOrNull(lastBleachROI,getFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED));
		boolean bBackgroundROISame =
			Compare.isEqualOrNull(lastBackgroundROI,getFrapStudy().getFrapData().getRoi(RoiType.ROI_BACKGROUND));
		
		return getFRAPParametersPanel().createCompleteFRAPChangeInfo(getSavedFrapModelInfo(),
				bCellROISame,bBleachROISame,bBackgroundROISame,bROISameSize);
	}
	
	protected FrapChangeInfo refreshBiomodel() throws Exception{
		try{

			//check if all the data for generating spatial model are ready.
			
			FrapChangeInfo frapChangeInfo = getChangesSinceLastSave();
			
			Double bleachWhileMonitoringRate = null;
			try{
				bleachWhileMonitoringRate =
					(frapChangeInfo.bleachWhileMonitorRateString == null?null:new Double(frapChangeInfo.bleachWhileMonitorRateString));
			}catch(Exception e){
				throw new Exception("Error parsing value for Bleach While Monitor. ("+frapChangeInfo.bleachWhileMonitorRateString+")");
			}
			if(bleachWhileMonitoringRate != null && bleachWhileMonitoringRate.doubleValue() < 0.0){
				throw new Exception("Bleach While Monitor must be positive. ("+bleachWhileMonitoringRate.doubleValue()+")");			
			}

			Double baseDiffusionRate = null;
			if(frapChangeInfo.diffusionRateString == null){
				throw new Exception("Diffusion Rate must be set to a value greater than 0.");
			}
			try{
				baseDiffusionRate =
					(frapChangeInfo.diffusionRateString == null?null:new Double(frapChangeInfo.diffusionRateString));
			}catch(Exception e){
				throw new Exception("Error parsing value for Base Diffusion Rate. ("+frapChangeInfo.diffusionRateString+")");
			}
			if(baseDiffusionRate != null && baseDiffusionRate.doubleValue() < 0.0){
				throw new Exception("Base Diffusion Rate must be positive. ("+baseDiffusionRate.doubleValue()+")");			
			}

			if(getFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED).isAllPixelsZero()){
					throw new Exception(VirtualFrapMainFrame.ROIErrorString);
			}
			if(getFrapStudy().getFrapData().getRoi(RoiType.ROI_BACKGROUND).isAllPixelsZero()){
					throw new Exception(VirtualFrapMainFrame.ROIErrorString);
			}
			if(getFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL).isAllPixelsZero()){
				throw new Exception(VirtualFrapMainFrame.ROIErrorString);
			}
			Point internalVoidLocation =
				ROI.findInternalVoid(getFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL));
			if(internalVoidLocation != null){
				throw new Exception("CELL ROI has unfilled internal void area at image location "+
						"x="+internalVoidLocation.x+",y="+internalVoidLocation.y+"\n"+
						"Use ROI editing tools to completely define the CELL ROI");
			}
			Point[] distinctCellAreaLocations =
				ROI.checkContinuity(getFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL));
			if(distinctCellAreaLocations != null){
				throw new Exception("CELL ROI has at least 2 discontinuous areas at image locations \n"+
						"x="+distinctCellAreaLocations[0].x+",y="+distinctCellAreaLocations[0].y+
						" and "+
						"x="+distinctCellAreaLocations[1].x+",y="+distinctCellAreaLocations[1].y+"\n"+
				"Use ROI editing tools to define a single continuous CELL ROI");				
			}
			getFrapStudy().refreshDependentROIs();
			BioModel bioModel = FRAPStudy.createNewBioModel(
				getFrapStudy(),
				FRAPStudy.DEFAULT_SPECIES_COUNT,
				baseDiffusionRate,
				frapChangeInfo.bleachWhileMonitorRateString,
				(getSavedFrapModelInfo() == null?null:getSavedFrapModelInfo().savedSimKeyValue),
				LocalWorkspace.getDefaultOwner(),
				new Integer(frapChangeInfo.startIndexForRecoveryString));
			getFrapStudy().setBioModel(bioModel);
//			((StructureMappingCartoon)getGeometryGraphPane().getGraphModel()).
//			setSimulationContext(getFrapStudy().getBioModel().getSimulationContexts()[0]);			
			return frapChangeInfo;

		}catch(Exception e){
			throw e;
		}
	}	

	
//	/**
//	 * This method initializes geometryGraphPane	
//	 * 	
//	 * @return cbit.gui.graph.GraphPane	
//	 */
//	private GraphPane getGeometryGraphPane() {
//		if (geometryGraphPane == null) {
//			geometryGraphPane = new GraphPane();
//			geometryGraphPane.setGraphModel(new StructureMappingCartoon());
//		}
//		return geometryGraphPane;
//	}
	
//	/**
//	 * This method initializes spatial_twoAndHalfDimRadioButton	
//	 * 	
//	 * @return javax.swing.JRadioButton	
//	 */
//	private JRadioButton getSpatial_twoAndHalfDimRadioButton() {
//		if (spatial_twoAndHalfDimRadioButton == null) {
//			spatial_twoAndHalfDimRadioButton = new JRadioButton();
//			spatial_twoAndHalfDimRadioButton.setText("2 1/2 D");
//		}
//		return spatial_twoAndHalfDimRadioButton;
//	}
//
//	/**
//	 * This method initializes spatial_threeDimRadioButton	
//	 * 	
//	 * @return javax.swing.JRadioButton	
//	 */
//	private JRadioButton getSpatial_threeDimRadioButton() {
//		if (spatial_threeDimRadioButton == null) {
//			spatial_threeDimRadioButton = new JRadioButton();
//			spatial_threeDimRadioButton.setText("3D");
//		}
//		return spatial_threeDimRadioButton;
//	}
//
//	/**
//	 * This method initializes spatial_twoDimRadioButton	
//	 * 	
//	 * @return javax.swing.JRadioButton	
//	 */
//	private JRadioButton getSpatial_twoDimRadioButton() {
//		if (spatial_twoDimRadioButton == null) {
//			spatial_twoDimRadioButton = new JRadioButton();
//			spatial_twoDimRadioButton.setText("2D");
//			spatial_twoDimRadioButton.setSelected(true);
//		}
//		return spatial_twoDimRadioButton;
//	}

//	/**
//	 * This method initializes geometryControlsPanel	
//	 * 	
//	 * @return javax.swing.JPanel	
//	 */
//	private JPanel getGeometryControlsPanel() {
//		if (geometryControlsPanel == null) {
//			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
//			gridBagConstraints8.anchor = GridBagConstraints.WEST;
//			gridBagConstraints8.gridy = -1;
//			gridBagConstraints8.gridx = -1;
//			geometryControlsPanel = new JPanel();
//			geometryControlsPanel.setLayout(new GridBagLayout());
//			geometryControlsPanel.add(getSpatial_twoDimRadioButton(), new GridBagConstraints());
//			geometryControlsPanel.add(getSpatial_twoAndHalfDimRadioButton(), new GridBagConstraints());
//			geometryControlsPanel.add(getSpatial_threeDimRadioButton(), gridBagConstraints8);
//		}
//		return geometryControlsPanel;
//	}

	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}

	public void setLocalWorkspace(LocalWorkspace arg_localWorkspace) {
		this.localWorkspace = arg_localWorkspace;
		getFRAPDataPanel().setLocalWorkspace(localWorkspace);
		try {
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getPDEDataViewer());
			((VirtualFrapWindowManager)getPDEDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getFlourDataViewer());
			((VirtualFrapWindowManager)getFlourDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes PDEDataViewer	
	 * 	
	 * @return cbit.vcell.client.data.PDEDataViewer	
	 */
	private PDEDataViewer getPDEDataViewer() {
		if (pdeDataViewer == null) {
			pdeDataViewer = new VFrapPDEDataViewer();
			VirtualFrapWindowManager dataViewerManager = new VirtualFrapWindowManager();
			try {
				pdeDataViewer.setDataViewerManager(dataViewerManager);
				dataViewerManager.addDataJobListener(pdeDataViewer);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pdeDataViewer;
	}

	private PDEDataViewer getFlourDataViewer() {
		if (flourDataViewer == null) {
			flourDataViewer = new VFrapPDEDataViewer();
			VirtualFrapWindowManager dataViewerManager = new VirtualFrapWindowManager();
			try {
				flourDataViewer.setDataViewerManager(dataViewerManager);
				dataViewerManager.addDataJobListener(flourDataViewer);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return flourDataViewer;
	}
	//Added Feb, 2008.
	private JSplitPane getSplitDataViewers()
	{
		//Added margins in both viewers to make it look nicer
		JPanel upPanel = new JPanel();
		PDEDataViewer pdeViewer = getPDEDataViewer();
		pdeViewer.setPreferredSize(new Dimension(700,450));
		JScrollPane upScroll = new JScrollPane(pdeViewer);
		upScroll.setAutoscrolls(true);
		upPanel.setLayout(new BorderLayout());
		upPanel.add(upScroll, BorderLayout.CENTER);
		upPanel.add(new JLabel("  "), BorderLayout.WEST);
		upPanel.add(new JLabel("  "), BorderLayout.EAST);
		upPanel.add(new JLabel(" "), BorderLayout.NORTH);
		upPanel.add(new JLabel(" "), BorderLayout.SOUTH);
		upPanel.setBorder(new TitledBorder(new LineBorder(new Color(168,168,255)),"Simulation Results and Masks", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 12)));
		
		
		JPanel botPanel = new JPanel();		
		PDEDataViewer fluorViewer = getFlourDataViewer();
		fluorViewer.setPreferredSize(new Dimension(700,450));
		JScrollPane botScroll = new JScrollPane(fluorViewer);
		botPanel.setAutoscrolls(true);
		botPanel.setLayout(new BorderLayout());
		botPanel.add(botScroll, BorderLayout.CENTER);
		botPanel.add(new JLabel("  "), BorderLayout.WEST);
		botPanel.add(new JLabel("  "), BorderLayout.EAST);
		botPanel.add(new JLabel(" "), BorderLayout.NORTH);
		botPanel.add(new JLabel(" "), BorderLayout.SOUTH);
		botPanel.setBorder(new TitledBorder(new LineBorder(new Color(168,168,255)),"Original Virtual Microscopy Data and Masks", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 12)));
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upPanel, botPanel);
	    split.setDividerLocation(((int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2))-90);
	    split.setDividerSize(4);
	    return split;
	}
	
	private DataManager getDataManager(Simulation sim) throws Exception{
		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],frapStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier());
		}		
		int jobIndex = 0;
		SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
		DataManager dataManager = new PDEDataManager(getLocalWorkspace().getVCDataManager(), simJob.getVCDataIdentifier());
		return dataManager;
	}
	
	public void spatialAnalysis() throws Exception{
		
		final AsynchProgressPopup pp =
			new AsynchProgressPopup(
				FRAPStudyPanel.this,
				"Running FRAP Spatial Analysis",
				"Working...",true,true
		);
		pp.start();

		new Thread(new Runnable(){public void run(){
			try{
				DataSetControllerImpl.ProgressListener runspatialAnalysisProgressListener =
					new DataSetControllerImpl.ProgressListener(){
						public void updateProgress(double progress) {
							int percentProgress = (int)(progress*100);
							VirtualFrapMainFrame.updateProgress(percentProgress);
							pp.setProgress(percentProgress);
						}
				};
				Simulation frapSimulation = frapStudy.getBioModel().getSimulations()[0];
				DataManager simulationDataManager = getDataManager(frapSimulation);
				final int startIndexForRecovery = new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery);
				final double[] frapDataTimeStamps = getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps();
				final FRAPStudy.SpatialAnalysisResults spatialAnalysisResults =
					FRAPStudy.spatialAnalysis(
						simulationDataManager,
						startIndexForRecovery,
						frapDataTimeStamps[startIndexForRecovery],
						frapSimulation.getMathDescription().getSubDomain(FRAPStudy.CYTOSOL_NAME),
						getFrapStudy().getFrapData(),
						runspatialAnalysisProgressListener);
				
				getResultsSummaryPanel().setData(
						spatialAnalysisResults,frapDataTimeStamps,startIndexForRecovery,
						new Double(getFrapStudy().getFrapModelParameters().diffusionRate));
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){
					getJTabbedPane().setSelectedIndex(INDEX_TAB_REPORT);
				}});
				
				
		//		NonGUIFRAPTest.dumpSpatialResults(spatialAnalysisResults, frapDataTimeStamps,
		//				new File("C:\\temp\\guiSpatialResults.txt"));
//				//
//				// display diffusion-centric plots
//				//
//				final ReferenceData[] referenceDataArr =
//					spatialAnalysisResults.createReferenceDataForAllDiffusionRates(frapDataTimeStamps);
//				final ODESolverResultSet[] odeSolverResultSetArr =
//					spatialAnalysisResults.createODESolverResultSetForAllDiffusionRates();
//				
//				
//				final MultisourcePlotPane[] spatialAnalysisListFinal = new MultisourcePlotPane[spatialAnalysisResults.diffusionRates.length];
//				for (int i = 0; i < spatialAnalysisResults.diffusionRates.length; i++) {
//					final String title = "Experimental vs. Diffusion Rate "+spatialAnalysisResults.diffusionRates[i];
//					final DataSource expDataSource = new DataSource(referenceDataArr[i],"experiment");
//					final DataSource fitDataSource = new DataSource(odeSolverResultSetArr[i], "fit");
//					final int diffusionRateIndex = i;
//					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
//						MultisourcePlotPane multisourcePlotPane = new MultisourcePlotPane();
//						multisourcePlotPane.setDataSources(new DataSource[] {  expDataSource, fitDataSource } );
//						multisourcePlotPane.selectAll();
//						multisourcePlotPane.setBorder(new TitledBorder(new LineBorder(new Color(168,168,255)),title, TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 12)));						
//						spatialAnalysisListFinal[diffusionRateIndex]=multisourcePlotPane;		
//					}});
//				}
//				spatialAnalysisList = spatialAnalysisListFinal;
//				VirtualFrapMainFrame.updateProgress(100);
//				// show plot list by default
//				SwingUtilities.invokeAndWait(new Runnable(){public void run(){
//					refreshReportPanel();					
//				}});
				VirtualFrapMainFrame.updateProgress(0);
				VirtualFrapMainFrame.updateStatus("Finished Spatial analysis.");
			}catch(final Exception e){
				pp.stop();
				VirtualFrapMainFrame.updateProgress(0);
				VirtualFrapMainFrame.updateStatus("Error running spatial analysis: "+e.getMessage());
				e.printStackTrace();
				try{
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){
					DialogUtils.showErrorDialog("Error running spatial analysis:\n"+e.getMessage());
				}});
				}catch(Exception e2){
					e2.printStackTrace();
				}
		}finally{
				pp.stop();
			}
		}}).start();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == getFRAPDataPanel().getOverlayEditorPanelJAI()){
			if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
				try {
					crop((Rectangle) evt.getNewValue());
				} catch (Exception e) {
					PopupGenerator.showErrorDialog("Error Cropping:\n"+e.getMessage());
				}
			}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_AUTOROI_PROPERTY)){
				if(!getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getROIType().equals(RoiType.ROI_CELL) &&
					getFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL).isAllPixelsZero()
					){
					DialogUtils.showInfoDialog("Define '"+OverlayEditorPanelJAI.WHOLE_CELL_AREA_TEXT+"'"+
							" ROI using ROI Tools or '"+OverlayEditorPanelJAI.ROI_ASSIST_TEXT+"'"+
							" before using '"+OverlayEditorPanelJAI.ROI_ASSIST_TEXT+"' to define Bleach or Backgroun ROIs");
					return;
				}
				JDialog roiDialog = new JDialog();
				roiDialog.setTitle("Create Region of Interest (ROI) using intensity thresholding");
				roiDialog.setModal(true);
				ROIAssistPanel roiAssistPanel = new ROIAssistPanel();
				ROI originalROI = null;
				try{
					originalROI = new ROI(getFrapStudy().getFrapData().getCurrentlyDisplayedROI());
				}catch(Exception e){
					e.printStackTrace();
					//can't happen
				}
				roiAssistPanel.init(roiDialog,originalROI,
						getFrapStudy().getFrapData(),getFRAPDataPanel().getOverlayEditorPanelJAI());
				roiDialog.setContentPane(roiAssistPanel);
				roiDialog.pack();
//				roiDialog.setPreferredSize(new Dimension(300,400));
//				roiDialog.setMinimumSize(new Dimension(300,400));
				roiDialog.setSize(400,300);
//				BeanUtils.centerOnComponent(roiDialog, this);
				ZEnforcer.showModalDialogOnTop(roiDialog, this);
//				roiDialog.setVisible(true);
				
				if(!originalROI.compareEqual(getFrapStudy().getFrapData().getCurrentlyDisplayedROI())){
					final ROI finalOriginalROI = originalROI;
					undoableEditSupport.postEdit(
						new AbstractUndoableEdit(){
							public boolean canUndo() {
								return true;
							}
							public String getUndoPresentationName() {
								return "ROI Threshold "+finalOriginalROI.getROIType().name();
							}
							public void undo() throws CannotUndoException {
								super.undo();
								getFrapStudy().getFrapData().addReplaceRoi(finalOriginalROI);
							}
						}
					);					
				}else{
					undoableEditSupport.postEdit(CLEAR_UNDOABLE_EDIT);
				}

				
//				try{
//					FrapDataAnalysisResults frapDataAnalysisResults =
//						FRAPDataAnalysis.fitRecovery2(getFrapStudy().getFrapData(),FrapDataAnalysisResults.BleachType_CirularDisk);
//					int startingIndexForRecovery = frapDataAnalysisResults.getStartingIndexForRecovery();
//					System.out.println("Starting index for recoverty="+startingIndexForRecovery);
//					int numTimes = getFrapStudy().getFrapData().getImageDataset().getSizeT();
//					long[] timeAvg = new long[getFrapStudy().getFrapData().getImageDataset().getISize().getXYZ()];
//					for (int t = 0; t < numTimes; t++) {
//						int pixelIndex = 0;
//						for (int z = 0; z < getFrapStudy().getFrapData().getImageDataset().getSizeZ(); z++) {
//							UShortImage timePointDataImage = getFrapStudy().getFrapData().getImageDataset().getImage(z, 0, t);
//							for (int y = 0; y < timePointDataImage.getNumY(); y++) {
//								for (int x = 0; x < timePointDataImage.getNumX(); x++) {
//									timeAvg[pixelIndex]+= timePointDataImage.getPixel(x,y,0)&0x0000FFFF;
//									pixelIndex++;  
//								}
//							}
//						}
//					}
//					int[] bins = new int[65536];
//					for (int i = 0; i < timeAvg.length; i++) {
//						bins[(int)(timeAvg[i]/numTimes)]++;
//					}
//					for (int i = 0; i < bins.length; i++) {
//						if(bins[i] != 0){
//							System.out.print(i+" ");
//							for (int j = 0; j < bins[i]; j++) {
//								if(j%1 == 0){
//									System.out.print("*");
//								}
//							}
//							System.out.println();
//						}
//					}
//					System.out.println("Total pixels ="+getFrapStudy().getFrapData().getImageDataset().getISize()+" t="+getFrapStudy().getFrapData().getImageDataset().getSizeT());
//				}catch(Exception e){
//					e.printStackTrace();
//					DialogUtils.showErrorDialog("Auto ROI error:"+e.getMessage());
//				}
			}
		}
	}

	private void refreshUI()
	{
		VirtualFrapMainFrame.enableSave(true);
		
		try{
			refreshBiomodel();
		}catch(Exception e){
			getFrapStudy().clearBioModel();
		}

	}
	
//	//Added March 2008.
//	public JCheckBox getShowReportListButton() {
//		if(showReportListCheckBox == null){
//			showReportListCheckBox = new JCheckBox("Show Plot List", true);
//			showReportListCheckBox.setFont(new Font("Tahoma", Font.BOLD, 12));
//			int len = showReportListCheckBox.getActionListeners().length;
//			if(len > 0)
//			{
//				for(int i=(len-1); i<0; i--)
//				{
//					showReportListCheckBox.removeActionListener(showReportListCheckBox.getActionListeners()[i]);
//				}
//			}
//			showReportListCheckBox.addActionListener(new java.awt.event.ActionListener() {
//				public void actionPerformed(java.awt.event.ActionEvent e) {
//					if(spatialAnalysisList != null && spatialAnalysisList.length >0 )
//					{
//						if(showReportListCheckBox.isSelected())
//						{
//							for(int i=0; i<spatialAnalysisList.length; i++)
//							{
//								spatialAnalysisList[i].setListVisible(true);
//								spatialAnalysisList[i].setPreferredSize(new Dimension(650,500));
//								spatialAnalysisList[i].invalidate();
//							}
//						}
//						else
//						{
//							for(int i=0; i<spatialAnalysisList.length; i++)
//							{
//								spatialAnalysisList[i].setListVisible(false);
//								spatialAnalysisList[i].setPreferredSize(new Dimension(450,300));
//								spatialAnalysisList[i].invalidate();
//							}
//						}
//						
//						getReportPanel().invalidate();
//						getReportBasePanel().invalidate();
//						getScrollReportPane().revalidate();
//					}
//				}
//			});
//		}
//		return showReportListCheckBox;
//	}
//	//Added March 2008
//	public JPanel getReportBasePanel() {
//		if (reportBasePanel == null) {
//			reportBasePanel = new JPanel();
//			reportBasePanel.setLayout(new BorderLayout());
//		}
//		return reportBasePanel;
//	}
}