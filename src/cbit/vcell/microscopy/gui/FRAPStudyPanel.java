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
import java.util.Hashtable;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import cbit.gui.DialogUtils;
import cbit.gui.graph.GraphPane;
import cbit.image.ImageException;
import cbit.sql.KeyValue;
import cbit.sql.Version;
import cbit.util.BeanUtils;
import cbit.util.Compare;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.data.NewClientPDEDataContext;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.SimulationModelInfo;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.server.MergedDataManager;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.desktop.controls.DataManager;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.microscopy.ExternalDataInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.NonGUIFRAPTest;
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

public class FRAPStudyPanel extends JPanel implements PropertyChangeListener {
	//Following final static variables are added in Jan 2008.
	public final static int INDEX_TAB_IMAGES = 0;
	public final static int INDEX_TAB_FITCURVE = 1;
	public final static int INDEX_TAB_SPATIALMODEL = 2;
	public final static int INDEX_TAB_FITSPATIALMODEL = 3;
	public final static int INDEX_TAB_REPORT = 4;
	
	public static final String FRAPDATAPANEL_TABNAME = "Images";
	
	public static final double Epsilon = 1e-8; // used to avoid dividing by 0
		
	private FRAPStudy frapStudy = null; 
	private FRAPDataPanel frapDataPanel = null;
	private LocalWorkspace localWorkspace = null;
	private JTabbedPane jTabbedPane = null;
	private FRAPParametersPanel frapParametersPanel = null;
	private JPanel modelPanel = null;
	private JPanel fitSpatialModelPanel = null;
	private JPanel reportPanel = null;
	private JPanel reportBasePanel = null;
	private JCheckBox showReportListCheckBox = null;
	private JScrollPane scrollReportPane = null;
	private MultisourcePlotPane[] spatialAnalysisList = null;
	private JPanel jPanel = null;
	private GraphPane geometryGraphPane = null;
	private JRadioButton spatial_twoAndHalfDimRadioButton = null;
	private JRadioButton spatial_threeDimRadioButton = null;
	private JRadioButton spatial_twoDimRadioButton = null;
	private JPanel geometryControlsPanel = null;
	
	private PDEDataViewer pdeDataViewer = null;
	private PDEDataViewer flourDataViewer = null;
	
	enum DisplayChoice { PDE,EXTTIMEDATA};

	
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
			(bStartIndexForRecoveryChanged?"(Start Index Recovery Rate)":" ");

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
		private final Boolean areSimeFilesOK;
		private final Boolean areExtDataFileOK;
		public CurrentSimulationDataState() throws Exception{
			if(getSavedFrapModelInfo() != null){
				frapChangeInfo = getChangesSinceLastSave();
				areSimeFilesOK = areSimulationFilesOKForSavedModel();
				areExtDataFileOK =
					areExternalDataOK(getLocalWorkspace(),
					getFrapStudy().getFrapDataExternalDataInfo(),
					getFrapStudy().getRoiExternalDataInfo());
			}else{
				frapChangeInfo = null;
				areSimeFilesOK = null;
				areExtDataFileOK = null;
			}
		}
		public boolean isDataInvalidBecauseModelNotSaved(){
			return frapChangeInfo == null;
		}
		public Boolean isDataInvalidBecauseModelChanged(){
			if(frapChangeInfo == null){
				return null;
			}
			return frapChangeInfo.hasAnyChanges();
		}
		public Boolean isDataInvalidBecauseMissingOrCorrupt(){
			if(areSimeFilesOK == null){
				return null;
			}
			if(areSimeFilesOK){
				if(areExtDataFileOK == null){
					return null;
				}
				if(areExtDataFileOK){
					return false;
				}
				return true;
			}
			return true;
		}
		public boolean isDataValid(){
			if(frapChangeInfo != null && !frapChangeInfo.hasAnyChanges() &&
				areSimeFilesOK != null && areSimeFilesOK &&
				areExtDataFileOK != null && areExtDataFileOK){
				return true;
			}
			return false;
		}
		public String getDescription(){
			if(isDataValid()){
				return "Simulation Data are valid.";
			}else if(isDataInvalidBecauseModelNotSaved()){
				return "Sim Data invalid because Model is not saved";
			}else if(isDataInvalidBecauseModelChanged()){
				return "Sim Data invalid because Model has changed";
			}else if(isDataInvalidBecauseMissingOrCorrupt()){
				return "Sim Data are missing or corrupt";
			}
			throw new RuntimeException("Unknown description");
		}
	};

	private	DataSetControllerImpl.ProgressListener progressListenerNew =
		new DataSetControllerImpl.ProgressListener(){
			public void updateProgress(double progress) {
				VirtualFrapMainFrame.statusBar.showProgress((int)Math.round(progress*100));
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
	
	/**
	 * This method initializes 
	 * 
	 */
	public FRAPStudyPanel() {
		super();
		initialize();
		loadROICursors();
	}
	
	private void setSavedFrapModelInfo(SavedFrapModelInfo savedFrapModelInfo) throws Exception{
		savedFrapModelInfoNew2 = savedFrapModelInfo;
		getFRAPParametersPanel().initializeSavedFrapModelInfo(getSavedFrapModelInfo(),getFrapStudy().getFrapData());
		getFRAPParametersPanel().insertFRAPModelParametersIntoFRAPStudy(getFrapStudy());
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
        this.add(getJPanel(), BorderLayout.SOUTH);
			
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
			jTabbedPane.addTab("Generate Spatial Model", null, getModelPanel(), null);
			jTabbedPane.addTab("Fit Spatial Model", null, getFitSpatialModelPanel(), null);
			jTabbedPane.addTab("Report", null, getScrollReportPane(), null);
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

	private boolean updateTabbedView(int exitTab,int enterTab) throws Exception{
		if(enterTab != FRAPStudyPanel.INDEX_TAB_IMAGES && (getFrapStudy() == null || getFrapStudy().getFrapData() == null)){
			throw new Exception("No document open.  Use 'File->Open' menu to open a new document");
		}
		try{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if(exitTab == FRAPStudyPanel.INDEX_TAB_IMAGES){
				getFRAPDataPanel().saveROI();
			}else if(exitTab == FRAPStudyPanel.INDEX_TAB_FITCURVE){
				getFRAPParametersPanel().insertFRAPModelParametersIntoFRAPStudy(getFrapStudy());
			}
			
			if(enterTab == FRAPStudyPanel.INDEX_TAB_FITCURVE){
				try{
					getFRAPParametersPanel().refreshFRAPModelParameterEstimates(getFrapStudy().getFrapData());
				}catch(Exception e){
					DialogUtils.showWarningDialog(this, 
							"Some FRAP Model Parameter Estimation help won't be available because:\n"+e.getMessage(),
							new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
				}
			}else if(enterTab == FRAPStudyPanel.INDEX_TAB_SPATIALMODEL){
				refreshBiomodel();
			}else if(enterTab == FRAPStudyPanel.INDEX_TAB_FITSPATIALMODEL){
				refreshBiomodel();
				CurrentSimulationDataState currentSimulationDataState = new CurrentSimulationDataState();
				if(!currentSimulationDataState.isDataValid()){
					if(currentSimulationDataState.isDataInvalidBecauseMissingOrCorrupt() != null &&
						currentSimulationDataState.isDataInvalidBecauseMissingOrCorrupt()){
						final String RUN_SIM = "Run Simulation...";
						String expectedSimFileLocation =
							getLocalWorkspace().getDefaultSimDataDirectory();
						String result = DialogUtils.showWarningDialog(this,
							"Couldn't find all simulation data in directory:\n"+expectedSimFileLocation+"\n"+
							"Simulation needs to Run before viewing results, Run Simulation now?" ,
							new String[] {RUN_SIM,UserMessage.OPTION_CANCEL}, RUN_SIM);
						if(result == null || !result.equals(RUN_SIM)){
							return false;
						}
					}
					clearFitSpatialModelPanel();
					runSimulation();
					return false;
				}else{
					refreshPDEDisplay(DisplayChoice.PDE);
					refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);
				}
			}else if(enterTab == FRAPStudyPanel.INDEX_TAB_REPORT){
				refreshBiomodel();
				CurrentSimulationDataState currentSimulationDataState = new CurrentSimulationDataState();
				if(currentSimulationDataState.isDataValid()){
					getReportPanel().removeAll();
					spatialAnalysis();
				}else{
					throw new Exception("Simulation Data are not valid. Simulation needs to be run.\n"+
						currentSimulationDataState.getDescription());
				}
			}
			return true;
//			switch (tabIndex){
//				case FRAPStudyPanel.INDEX_TAB_IMAGES:
//				break;
//				case FRAPStudyPanel.INDEX_TAB_FITCURVE:
//					try{
//						getFRAPDataPanel().saveROI();
//						getFRAPParametersPanel().updateFrapDataAnalysis();
//					}catch(Exception e){
//						//ignore, show tab anyway
//					}
//				break;
//				case FRAPStudyPanel.INDEX_TAB_SPATIALMODEL:
//					refreshBiomodel();
//				break;
//				case FRAPStudyPanel.INDEX_TAB_FITSPATIALMODEL:
//					{
//						refreshBiomodel();
//						CurrentSimulationDataState currentSimulationDataState =
//							new CurrentSimulationDataState();
//						if(!currentSimulationDataState.isDataValid()){
//							if(currentSimulationDataState.isDataInvalidBecauseMissingOrCorrupt() != null &&
//								currentSimulationDataState.isDataInvalidBecauseMissingOrCorrupt()){
//								final String RUN_SIM = "Run Simulation...";
//								String expectedSimFileLocation =
//									getLocalWorkspace().getDefaultSimDataDirectory();;
//								String result = DialogUtils.showWarningDialog(this,
//									"Couldn't find all simulation data in directory:\n"+expectedSimFileLocation+"\n"+
//									"Simulation needs to Run before viewing results, Run Simulation now?" ,
//									new String[] {RUN_SIM,UserMessage.OPTION_CANCEL}, RUN_SIM);
//								if(result == null || !result.equals(RUN_SIM)){
//									return false;
//								}
//							}
//							clearFitSpatialModelPanel();
//							runSimulation();
//							return false;
//						}else{
//							refreshPDEDisplay(DisplayChoice.PDE);
//							refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);
//						}
//					}
//				break;
//				case FRAPStudyPanel.INDEX_TAB_REPORT:
//					{
//						refreshBiomodel();
//						CurrentSimulationDataState currentSimulationDataState =
//							new CurrentSimulationDataState();
//						if(currentSimulationDataState.isDataValid()){
//							getReportPanel().removeAll();
//							spatialAnalysis();
//						}else{
//							throw new Exception("Simulation Data are not valid. Simulation needs to be run.\n"+
//								currentSimulationDataState.getDescription());
//						}
//					}
//				break;
//				default:
//				break;
//			}
//			return true;
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

	/**
	 * This method initializes modelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getModelPanel() {
		if (modelPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridheight = 1;
			gridBagConstraints4.weightx = 0.0D;
			gridBagConstraints4.weighty = 0.0D;
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.weightx = 1.0D;
			gridBagConstraints5.weighty = 1.0D;
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridwidth = 1;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints3.gridy = 2;
			modelPanel = new JPanel();
			modelPanel.setLayout(new GridBagLayout());
			modelPanel.add(getGeometryControlsPanel(), gridBagConstraints10);
			modelPanel.add(getGeometryGraphPane(), gridBagConstraints5);
//			modelPanel.add(getModelSpecPanel(), gridBagConstraints4);
			//Commentted in Feb 2008. It can be found in menu and toolbar
//			modelPanel.add(getRefreshModelButton(), gridBagConstraints3);
		}
		return modelPanel;
	}

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

	/**
	 * This method initializes reportPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getReportPanel() {
		if (reportPanel == null) {
			reportPanel = new JPanel();
			reportPanel.setLayout(new FlowLayout());
			reportPanel.setMaximumSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.8), 5000));
			reportPanel.setPreferredSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.8), 5000));
		}
		return reportPanel;
	}

	private JScrollPane getScrollReportPane()
	{
		if (scrollReportPane == null) {
			scrollReportPane = new JScrollPane();
			scrollReportPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollReportPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollReportPane.setMinimumSize(new Dimension(30, 75));
			scrollReportPane.setViewportView(getReportPanel());
		}
		return scrollReportPane;
	}
	//refresh the report panel when spatial analysis is done.
	private void refreshReportPanel()
	{
		getReportPanel().removeAll();
		if(spatialAnalysisList != null && spatialAnalysisList.length > 0)
		{
			for(int i = 0; i< spatialAnalysisList.length; i++)
			{
				spatialAnalysisList[i].setPreferredSize(new Dimension(650,500));
				getReportPanel().add(spatialAnalysisList[i]/*, gbConstraints[i]*/);
			}
		}
		getReportBasePanel().removeAll();
		getReportBasePanel().add(getShowReportListButton(), BorderLayout.NORTH);
		getReportBasePanel().add(getReportPanel(), BorderLayout.CENTER);
		JScrollPane baseScrollPane = ((JScrollPane)getJTabbedPane().getComponentAt(INDEX_TAB_REPORT));
		baseScrollPane.setAutoscrolls(true);
		//adjust report panel's width to scroll pane's width.
		baseScrollPane.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}
			public void componentResized(ComponentEvent arg0) {
				getReportBasePanel().setMaximumSize(new Dimension(((int)(arg0.getComponent().getSize().getWidth()*0.8)), 5000));
				getReportBasePanel().setPreferredSize(new Dimension(((int)(arg0.getComponent().getSize().getWidth()*0.8)), 5000));
			}
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		((JScrollPane)getJTabbedPane().getComponentAt(INDEX_TAB_REPORT)).setViewportView(getReportBasePanel());
		getShowReportListButton().setSelected(true); //show plot list by default
	}
	
	private FRAPStudy getFrapStudy() {
		return frapStudy;
	}

	public void setFrapStudy(FRAPStudy frapStudy,boolean bNew) {
		this.frapStudy = frapStudy;
		frapStudy.addPropertyChangeListener(this);
		getFRAPDataPanel().setFrapStudyNew(frapStudy,bNew);
	}
	
	protected void crop(Rectangle cropRectangle) throws ImageException {
		if (getFrapStudy() == null || getFrapStudy().getFrapData()==null){
			return;
		}
		getFRAPDataPanel().getOverlayEditorPanelJAI().saveROItoWritebackBuffer();
		FRAPData frapData = getFrapStudy().getFrapData();
		FRAPData newFrapData = frapData.crop(cropRectangle);
		FRAPStudy newFrapStudy = new FRAPStudy();
		newFrapStudy.setFrapData(newFrapData);
		setFrapStudy(newFrapStudy,false);
	}

	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints1.gridy = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}

	
	public void save() throws Exception {
		try{
			refreshBiomodel();
		}catch(Exception e){
			//save whatever we have
		}
		AsynchClientTask saveTask = createSaveTask(false,true);
		ClientTaskDispatcher.dispatch(
			this, new Hashtable<String, Object>(),
			new AsynchClientTask[] {saveTask}, false);
	}
	
	public void saveAs() throws Exception{
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
			getJTabbedPane().setSelectedIndex(INDEX_TAB_IMAGES);
		}
		try{
			refreshBiomodel();
		}catch(Exception e){
			//Save whatever we have
		}
		AsynchClientTask saveTask = createSaveTask(true,true);
		ClientTaskDispatcher.dispatch(
			this, new Hashtable<String, Object>(),
			new AsynchClientTask[] {saveTask}, false);
	}
	private void saveAsInternal(String saveToFileName) throws Exception {

		getFRAPParametersPanel().insertFRAPModelParametersIntoFRAPStudy(frapStudy);
		boolean bSaveAs = saveToFileName == null;
		File outputFile = null;
		if(bSaveAs){
			int retval = VirtualFrapLoader.saveFileChooser.showSaveDialog(this);
			if (retval == JFileChooser.APPROVE_OPTION){
				String outputFileName = VirtualFrapLoader.saveFileChooser.getSelectedFile().getPath();
				if((outputFileName.length() < 5) || (outputFileName.substring(outputFileName.length()-4).compareTo(".xml") != 0))
	            {
					outputFileName=VirtualFrapLoader.saveFileChooser.getSelectedFile().getPath()+".xml";
	            }
				outputFile = new File(outputFileName);
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
			if(!DialogUtils.showWarningDialog(this, "OverWrite file\n"+outputFile.getAbsolutePath(),
			new String[] {OK_OPTION,"Cancel"}, "Cancel").equals(OK_OPTION)){
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
		saveProcedure(outputFile,bSaveAs);
	}
	
	private void saveProcedure(File xmlFrapFileName,boolean bSaveAsNew) throws Exception{
		VirtualFrapMainFrame.statusBar.showStatus("Saving file " + xmlFrapFileName.getAbsolutePath()+" ...");
		
		BioModel newBioModel = null;
		try{
			newBioModel = FRAPStudy.createNewBioModel(getFrapStudy(),
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
		

		boolean originalSimFilesAreInvalid = cleanupSimDataFilesIfNotOK();
		if(originalSimFilesAreInvalid || bSaveAsNew){
			//Replace with new external data IDs for use in 'run sim'
			getFrapStudy().setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
			getFrapStudy().setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
			
		}
		
		MicroscopyXmlproducer.writeXMLFile(getFrapStudy(), xmlFrapFileName, true,progressListenerNew,VirtualFrapMainFrame.SAVE_COMPRESSED);
		getFrapStudy().setXmlFilename(xmlFrapFileName.getAbsolutePath());
		setSavedFrapModelInfo(FRAPStudyPanel.createSavedFrapModelInfo(getFrapStudy()));
		VirtualFrapMainFrame.statusBar.showStatus("File " + xmlFrapFileName.getAbsolutePath()+" has been saved.");
        VirtualFrapLoader.mf.setMainFrameTitle(xmlFrapFileName.getName());
        VirtualFrapMainFrame.statusBar.showProgress(0);
	}
	private boolean cleanupSimDataFilesIfNotOK() throws Exception{
		CurrentSimulationDataState currentSimulationDataState =
			new CurrentSimulationDataState();
		if(!currentSimulationDataState.isDataValid()){
			//remove saved simulation files (they no longer apply to this model)
			ExternalDataInfo origImageDataExtDataInfo = getFrapStudy().getFrapDataExternalDataInfo();
			if(origImageDataExtDataInfo != null){
				FRAPStudy.deleteCanonicalExternalData(getLocalWorkspace(),
					getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier());
			}
			ExternalDataInfo origROIDataExtDataInfo = getFrapStudy().getRoiExternalDataInfo();
			if(origROIDataExtDataInfo != null){
				FRAPStudy.deleteCanonicalExternalData(getLocalWorkspace(),
					getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier());
			}
			File mergedFunctionFile = getMergedFunctionFile();
			if(mergedFunctionFile != null){
				mergedFunctionFile.delete();
			}
			if(getSavedFrapModelInfo() != null){
				File userDir =
					new File(getLocalWorkspace().getDefaultSimDataDirectory());
				deleteSimulationFiles(userDir, getSavedFrapModelInfo().savedSimKeyValue);
			}
			return true;
		}
		return false;
	}
	protected void load(final File inFile) throws Exception{
				
		AsynchClientTask saveTask = getSaveIfNeededTask();
		
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		
		// create tasks
		AsynchClientTask loadXmlTask = new AsynchClientTask() {
			public String getTaskName() { return "Loading XML file."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				try {
					String xmlString = XmlUtil.getXMLString(inFile.getAbsolutePath());
					MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
					FRAPStudy frapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null),progressListenerNew);
					if(!areExternalDataOK(getLocalWorkspace(),frapStudy.getFrapDataExternalDataInfo(),frapStudy.getRoiExternalDataInfo())){
						frapStudy.setFrapDataExternalDataInfo(null);
						frapStudy.setRoiExternalDataInfo(null);
					}
					frapStudy.setXmlFilename(inFile.getAbsolutePath());
					setFrapStudy(frapStudy,true);
					setSavedFrapModelInfo(FRAPStudyPanel.createSavedFrapModelInfo(frapStudy));
				} catch (Exception e) {
					VirtualFrapMainFrame.statusBar.showStatus("Failed loading file " + inFile.getAbsolutePath()+".");
					hash.put(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR,
						new Exception("Failed loading file " + inFile.getAbsolutePath(),e));
					e.printStackTrace();
				}
			}
			public boolean skipIfAbort() {
				return false;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return false;
			}
		};	
		
		AsynchClientTask afterLoadTask = new AsynchClientTask() {
			public String getTaskName() { return "Loading XML file."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_NONBLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				FRAPStudyPanel.this.refreshUI();				
				//give information after successfully loading the data
	            VirtualFrapMainFrame.statusBar.showStatus("File " + inFile.getAbsolutePath()+" has been loaded.");
	            VirtualFrapLoader.mf.setMainFrameTitle(inFile.getName());
	            VirtualFrapMainFrame.statusBar.showProgress(0);
	            getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_IMAGES);
			}
			public boolean skipIfAbort() {
				return false;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return false;
			}
		};
		AsynchClientTask[] tasks = null;
		if(saveTask != null){
			tasks = new AsynchClientTask[] {saveTask, loadXmlTask, afterLoadTask};
		}else{
			tasks = new AsynchClientTask[] {loadXmlTask, afterLoadTask};
		}
		ClientTaskDispatcher.dispatch(VirtualFrapLoader.mf,hash, tasks, false);  
	}
	
	//the following functions are written for VirtualFrapMainFrame to call
	//since it can not access FRAPDataPanel.
	protected void importFile(File inFile) throws Exception{
		importFileInternal(inFile,getSaveIfNeededTask(),createAfterImportTask(inFile));
	}

	protected void importFileInternal(final File inFile,AsynchClientTask saveTask,AsynchClientTask afterImportTask){
		Hashtable<String, Object> hash = new Hashtable<String, Object>();    		   		
		AsynchClientTask importImgTask = new AsynchClientTask() {
			public String getTaskName() { return "Importing images from external data source."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				try {
					FRAPData frapData = null;
					if(inFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)){
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
							frapData = 
								FRAPData.importFRAPDataFromVCellSimulationData(inFile,
									dataIdentifiers[selectedIndexArr[0]].getName());
						}else{
							throw UserCancelException.CANCEL_GENERIC;
						}
					}else{
        				ImageLoadingProgress imgProgress = new ImageLoadingProgress();
        				imgProgress.addPropertyChangeListener(getFRAPDataPanel());
            			ImageDataset imageDataset = ImageDatasetReader.readImageDataset(inFile.getAbsolutePath(), imgProgress);
            			frapData = FRAPData.importFRAPDataFromImageDataSet(imageDataset);
					}
					importDataSetup(frapData);
				} catch (Exception e2) {
                	VirtualFrapMainFrame.statusBar.showStatus("Failed loading file " + inFile.getAbsolutePath()+".");
                	e2.printStackTrace(System.out);
                	DialogUtils.showErrorDialog(getFRAPDataPanel(),e2.getMessage());
                }
			}
			public boolean skipIfAbort() {
				return false;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return false;
			}
		};	
        	
		AsynchClientTask[] tasks = null;
		if(saveTask != null){
			tasks = new AsynchClientTask[] {saveTask, importImgTask, afterImportTask};
		}else{
			tasks = new AsynchClientTask[] {importImgTask, afterImportTask};
		}
		ClientTaskDispatcher.dispatch(VirtualFrapLoader.mf,hash, tasks, false);           	
	}

	
	private void importDataSetup(FRAPData frapData) throws Exception{
		FRAPStudy newFrapStudy = new FRAPStudy();
		newFrapStudy.setFrapData(frapData);
		setFrapStudy(newFrapStudy, true);
		setSavedFrapModelInfo(null);
        frapStudy.setXmlFilename(null); //in case an xml file was loaded before this.
	}

	protected void importFileSeries(File[] inFiles, boolean isTimeSeries, double timeInterval, double zInterval) throws Exception{
		importFileSeriesInternal(
			inFiles, isTimeSeries, timeInterval, zInterval,getSaveIfNeededTask(),createAfterImportTask(inFiles[0]));
	}
	
	
	protected void importFileSeriesInternal(
			File[] files, boolean arg_isTimeSeries, double arg_timeInterval, double arg_zInterval,
			AsynchClientTask saveTask,AsynchClientTask afterImportTask){
			
			final File[] inputFiles = files;
			final boolean isTimeSeries = arg_isTimeSeries;
			final double tInterval = arg_timeInterval;
			final double zInterval = arg_zInterval;
			
	       	VirtualFrapMainFrame.statusBar.showStatus("Loading file series from directory" + inputFiles[0].getParent()+" ...");
	       	// prepare hashtable for tasks
			Hashtable<String, Object> hash = new Hashtable<String, Object>();
	        		    		
			// create tasks
			AsynchClientTask importImgTask = new AsynchClientTask() {
				public String getTaskName() { return "Importing images from external data source."; }
				public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
				public void run(java.util.Hashtable hash) throws Exception{
					try {
	    				ImageLoadingProgress imgProgress = new ImageLoadingProgress();
	        			imgProgress.addPropertyChangeListener(getFRAPDataPanel());
	        			ImageDataset imageDataset = ImageDatasetReader.readImageDatasetFromMultiFiles(inputFiles, imgProgress, isTimeSeries, tInterval, zInterval);
	        			FRAPData frapData = new FRAPData(imageDataset, new ROI.RoiType[] { RoiType.ROI_BLEACHED,RoiType.ROI_CELL,RoiType.ROI_BACKGROUND });
	        			frapData.setOriginalGlobalScaleInfo(null);
	        			importDataSetup(frapData);
	                } catch (Exception e2) {
	                	VirtualFrapMainFrame.statusBar.showStatus("Failed loading file series from directory " + inputFiles[0].getParent()+".");
	                	e2.printStackTrace(System.out);
	                	DialogUtils.showErrorDialog(getFRAPDataPanel(),e2.getMessage());
	                }
				}
				public boolean skipIfAbort() {
					return false;
				}
				public boolean skipIfCancel(UserCancelException e) {
					return false;
				}
			};	
	        	
			AsynchClientTask[] tasks = null;
	        if(saveTask != null){
	        	tasks = new AsynchClientTask[] {saveTask, importImgTask, afterImportTask};
	        }
	        else{
	        	tasks = new AsynchClientTask[] {importImgTask, afterImportTask};
	        }
			ClientTaskDispatcher.dispatch(VirtualFrapLoader.mf,hash, tasks, false);           	
		}

	
	private AsynchClientTask createAfterImportTask(final File inFile){
		return new AsynchClientTask() {
			public String getTaskName() { return "Import Done."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_NONBLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
                VirtualFrapMainFrame.statusBar.showStatus("File " + inFile.getAbsolutePath()+" has been loaded.");
                VirtualFrapLoader.mf.setMainFrameTitle(inFile.getName());
                VirtualFrapMainFrame.statusBar.showProgress(0);
                VirtualFrapMainFrame.frapStudyPanel.getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_IMAGES);
                refreshUI();
			}
			public boolean skipIfAbort() {
				return false;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return false;
			}
		};	

	}
	private AsynchClientTask createSaveTask(final boolean bSaveAs,final boolean bRefreshUI) throws Exception{
		return new AsynchClientTask() {
			public String getTaskName() { return "Saving Current Workspace"; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				if(bSaveAs){
					saveAsInternal(null);
				}else{
					saveAsInternal(getFrapStudy().getXmlFilename());
				}
				if(bRefreshUI){
					refreshUI();
				}
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};	
	}
	private AsynchClientTask getSaveIfNeededTask() throws Exception{
		AsynchClientTask saveTask =  null;
		boolean bNeedsSave = false;
		FrapChangeInfo frapChangeInfo = null;
		final String DONT_SAVE_CONTINUE = "Don't Save, Continue";
		try{
			frapChangeInfo = getChangesSinceLastSave();//refreshBiomodel();
			bNeedsSave = frapChangeInfo != null && frapChangeInfo.hasAnyChanges();
		}catch(Exception e){
			if(true){
				String errorDecision = PopupGenerator.showWarningDialog(this, null,
						new UserMessage(
							"Current document will be overwritten and can't be saved because:\n"+e.getMessage(),
							new String[] {DONT_SAVE_CONTINUE,UserMessage.OPTION_CANCEL},DONT_SAVE_CONTINUE),
						null);
				if(errorDecision.equals(UserMessage.OPTION_CANCEL)){
					throw UserCancelException.CANCEL_GENERIC;
				}else{
					return null;
				}
			}else{
				throw e;
			}
		}
    	if(bNeedsSave){
    		String saveMessage = null;
    		if(getSavedFrapModelInfo() != null){
    			saveMessage = "Changes in current document will be overwritten, Save current document?\n"+
    				"Changes include "+frapChangeInfo.getChangeDescription();
    		}else{
    			saveMessage = "Current unsaved document will be overwritten,  Save current document?";
    		}
			final String result = PopupGenerator.showWarningDialog(this, null,
					new UserMessage(saveMessage,
						new String[] {SAVE_FILE_OPTION,UserMessage.OPTION_SAVE_AS_NEW,DONT_SAVE_CONTINUE,UserMessage.OPTION_CANCEL},SAVE_FILE_OPTION),
					null);
			if(result.equals(SAVE_FILE_OPTION) || result.equals(UserMessage.OPTION_SAVE_AS_NEW)){
				saveTask = createSaveTask(result.equals(UserMessage.OPTION_SAVE_AS_NEW),false);
			}else if(result.equals(DONT_SAVE_CONTINUE)){
				saveTask = null;
			}else if(result.equals(UserMessage.OPTION_CANCEL)){
				throw UserCancelException.CANCEL_GENERIC;
			}else{
				throw new Exception("Unknown option in saveIfNeeded '"+result+"'");
			}
    	}
    	return saveTask;
	}
	
	protected void clearROI()
	{
		getFRAPDataPanel().clearROI();
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
	protected void runSimulation() throws Exception 
	{
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
		AsynchClientTask saveTask = null;
		try{
			FrapChangeInfo frapChangeInfo = refreshBiomodel();
			boolean bNeedsExtData =
				!areExternalDataOK(getLocalWorkspace(),
					frapStudy.getFrapDataExternalDataInfo(),frapStudy.getRoiExternalDataInfo());
			if(frapChangeInfo.hasAnyChanges() || bNeedsExtData){
				boolean bSaveAsNew = false;
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
						bSaveAsNew = false;
					}else if(result.equals(UserMessage.OPTION_SAVE_AS_NEW)){
						bSaveAsNew = true;
					}else{
						return;
					}
				}
				saveTask = createSaveTask(bSaveAsNew,true);
			}
		}catch(Exception e){
			throw new Exception("Error running simulation:\n"+e.getMessage());
		}

		// show status
		VirtualFrapMainFrame.statusBar.showStatus("Simulation starting...");
		
		AsynchClientTask saveExternalDataTask = new AsynchClientTask() {
			public String getTaskName() { return "Saving External Data and ROI..."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				if(cleanupSimDataFilesIfNotOK()){
					getFrapStudy().saveROIsAsExternalData(localWorkspace,
						getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
					getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
						getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
				}
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};

		AsynchClientTask runSolverTask = new AsynchClientTask() {
			public String getTaskName() { return "Running simulation."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{

				Simulation simulation =
					getFrapStudy().getBioModel().getSimulations()[0];
				FRAPStudy.runFVSolverStandalone(
					simulationDataDir,
					new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
					simulation,
					getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
					getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
					progressListenerNew);
				VirtualFrapMainFrame.statusBar.showProgress(100);
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		
		AsynchClientTask displaySimulatedDataTask = new AsynchClientTask() {
			public String getTaskName() { return "Displaying simulated data."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				refreshPDEDisplay(DisplayChoice.PDE);//upper data viewer has simulated data and masks
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		
		AsynchClientTask displayOriginalDataTask = new AsynchClientTask() {
			public String getTaskName() { return "Displaying original data."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);//lower data viewer has original data and masks
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		
		AsynchClientTask afterRunTask = new AsynchClientTask() {
			public String getTaskName() { return "Displaying original data."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_NONBLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				VirtualFrapMainFrame.statusBar.showStatus("Simulation is done.");
				VirtualFrapMainFrame.statusBar.showProgress(0);
				getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_FITSPATIALMODEL);
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		AsynchClientTask[] tasks = null;
		if(saveTask != null){
			tasks = new AsynchClientTask[] {saveTask, saveExternalDataTask, runSolverTask, displaySimulatedDataTask, displayOriginalDataTask, afterRunTask};
		}else{
			tasks = new AsynchClientTask[] {saveExternalDataTask, runSolverTask, displaySimulatedDataTask, displayOriginalDataTask, afterRunTask};
		}
		ClientTaskDispatcher.dispatch(VirtualFrapLoader.mf, hash, tasks, false);
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
	private File getMergedFunctionFile(){
		//Delete merged function file
		if(getFrapStudy().getFrapDataExternalDataInfo() != null &&
			getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier() != null){
			MergedDataInfo mergedDataInfo =
				new MergedDataInfo(LocalWorkspace.getDefaultOwner(),
					new VCDataIdentifier[]{
						getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
						getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier()});
			return
				new File(
					new File(getLocalWorkspace().getDefaultSimDataDirectory()),
					mergedDataInfo.getID()+SimDataConstants.FUNCTIONFILE_EXTENSION);
		}		
		return null;
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
			Expression norm_fluor = new Expression("((Data2.cell_mask*(Data1.fluor/(Data2.prebleach_avg + 1e-8)))*(Data2.cell_mask > 0))");
			AnnotatedFunction[] func = {new AnnotatedFunction("norm_fluor", norm_fluor, null, VariableType.VOLUME, false)};
			boolean isExisted = false;
			for(int i=0; i < pdeDataContext.getFunctions().length; i++)
			{
				if(func[0].compareEqual(pdeDataContext.getFunctions()[i]))
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
		if (getFrapStudy() == null || getFrapStudy().getFrapData() == null){
			if(getSavedFrapModelInfo() == null){
				return null;
			}
			throw new Exception("Missing Frap Data Unexpected while refreshing BioModel");
		}
		getFRAPDataPanel().saveROI();
		
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
//		return new FrapChangeInfo(
//				!bCellROISame || !bBleachROISame || !bBackgroundROISame,
//				!bROISameSize,
//				getFRAPParametersPanel().isUserDiffusionRateChanged(),
//				getFRAPParametersPanel().getUserDiffusionRateString(),
//				getFRAPParametersPanel().isUserMonitorBleachRateChanged(),
//				getFRAPParametersPanel().getUserMonitorBleachRateString(),
//				getFRAPParametersPanel().isUserMobileFractionChanged(),
//				getFRAPParametersPanel().getUserMobileFractionString(),
//				getFRAPParametersPanel().isUserSlowerRateChanged(),
//				getFRAPParametersPanel().getUserSlowerRateString(),
//				getFRAPParametersPanel().isUserStartingIndexForRecoveryChanged(),
//				getFRAPParametersPanel().getUserStartingIndexForRecoveryString());
	}
	
	protected FrapChangeInfo refreshBiomodel() throws Exception{
		try{

			VirtualFrapMainFrame.statusBar.showStatus("Refreshing Frap model...");
			//check if all the data for generating spatial model are ready.
			
			FrapChangeInfo frapChangeInfo = getChangesSinceLastSave();
			if(frapChangeInfo == null){
				return null;
			}
			
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
			getFRAPDataPanel().refreshDependentROIs_later();
			BioModel bioModel = FRAPStudy.createNewBioModel(
				getFrapStudy(),
				baseDiffusionRate,
				frapChangeInfo.bleachWhileMonitorRateString,
				(getSavedFrapModelInfo() == null?null:getSavedFrapModelInfo().savedSimKeyValue),
				LocalWorkspace.getDefaultOwner(),
				new Integer(frapChangeInfo.startIndexForRecoveryString));
			getFrapStudy().setBioModel(bioModel);
			VirtualFrapMainFrame.statusBar.showStatus("New Frap Model created.");
			((StructureMappingCartoon)getGeometryGraphPane().getGraphModel()).
			setSimulationContext(getFrapStudy().getBioModel().getSimulationContexts()[0]);			
			return frapChangeInfo;

		}catch(Exception e){
			VirtualFrapMainFrame.statusBar.showStatus("Frap Model refresh error.");
			throw e;
		}
	}	

	
	/**
	 * This method initializes geometryGraphPane	
	 * 	
	 * @return cbit.gui.graph.GraphPane	
	 */
	private GraphPane getGeometryGraphPane() {
		if (geometryGraphPane == null) {
			geometryGraphPane = new GraphPane();
			geometryGraphPane.setGraphModel(new StructureMappingCartoon());
		}
		return geometryGraphPane;
	}
	
	/**
	 * This method initializes spatial_twoAndHalfDimRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSpatial_twoAndHalfDimRadioButton() {
		if (spatial_twoAndHalfDimRadioButton == null) {
			spatial_twoAndHalfDimRadioButton = new JRadioButton();
			spatial_twoAndHalfDimRadioButton.setText("2 1/2 D");
		}
		return spatial_twoAndHalfDimRadioButton;
	}

	/**
	 * This method initializes spatial_threeDimRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSpatial_threeDimRadioButton() {
		if (spatial_threeDimRadioButton == null) {
			spatial_threeDimRadioButton = new JRadioButton();
			spatial_threeDimRadioButton.setText("3D");
		}
		return spatial_threeDimRadioButton;
	}

	/**
	 * This method initializes spatial_twoDimRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSpatial_twoDimRadioButton() {
		if (spatial_twoDimRadioButton == null) {
			spatial_twoDimRadioButton = new JRadioButton();
			spatial_twoDimRadioButton.setText("2D");
			spatial_twoDimRadioButton.setSelected(true);
		}
		return spatial_twoDimRadioButton;
	}

	/**
	 * This method initializes geometryControlsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGeometryControlsPanel() {
		if (geometryControlsPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = -1;
			gridBagConstraints8.gridx = -1;
			geometryControlsPanel = new JPanel();
			geometryControlsPanel.setLayout(new GridBagLayout());
			geometryControlsPanel.add(getSpatial_twoDimRadioButton(), new GridBagConstraints());
			geometryControlsPanel.add(getSpatial_twoAndHalfDimRadioButton(), new GridBagConstraints());
			geometryControlsPanel.add(getSpatial_threeDimRadioButton(), gridBagConstraints8);
		}
		return geometryControlsPanel;
	}

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
		//check if all the data are ready for spatial analysis.
		if (getFrapStudy() == null || getFrapStudy().getFrapData() == null || 
			getFrapStudy().getFrapData().getImageDataset().getISize().getXYZ() <=0 || 
			getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps().length <1) 
		{
			throw new Exception("Data have to be loaded before running spatial analysis.");
		}
		if(getFrapStudy().getFrapData().getRois().length <= 3)
		{
			throw new Exception("ROIs and ROI rings have to be created before running spatial analysis. Please go to \'Images\' tab to create ROIs and ROI rings."); 
		}
		if(getFrapStudy().getBioModel() == null || getFrapStudy().getBioModel().getSimulations() == null)
		{
			throw new Exception("Spatial model and simulation have to be created/recreated due to changes are made. Please go to \'Generate Spatial Model\' tab to create/recreate spatial model.");
		}
		if(getFrapStudy().getFrapModelParameters() == null)
		{
			throw new Exception("Model parameters are required before running spatial analysis. Please go to \'Fit Recovery Curve\' tab to run curve fitting.");
		}
		
       	VirtualFrapMainFrame.statusBar.showStatus("Running spatial analysis ...");
       	// prepare hashtable for tasks
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
        
		// create tasks
		AsynchClientTask spatialAnalysisTask = new AsynchClientTask() {
			public String getTaskName() { return "Running spatial analysis."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				
				DataSetControllerImpl.ProgressListener progressListener =
					new DataSetControllerImpl.ProgressListener(){
						public void updateProgress(double progress) {
							VirtualFrapMainFrame.statusBar.showProgress((int)Math.round((progress) * 100));
						}
					};
				Simulation frapSimulation = frapStudy.getBioModel().getSimulations()[0];
				DataManager simulationDataManager = getDataManager(frapSimulation);
				int startIndexForRecovery = new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery);
				double[] frapDataTimeStamps = getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps();
				FRAPStudy.SpatialAnalysisResults spatialAnalysisResults =
					FRAPStudy.spatialAnalysis(
						simulationDataManager,
						startIndexForRecovery,
						frapDataTimeStamps[startIndexForRecovery],
						frapSimulation.getMathDescription().getSubDomain(FRAPStudy.CYTOSOL_NAME),
						getFrapStudy().getFrapData(),
						progressListener);
				
//				NonGUIFRAPTest.dumpSpatialResults(spatialAnalysisResults, frapDataTimeStamps,
//						new File("C:\\temp\\guiSpatialResults.txt"));
				//
				// display diffusion-centric plots
				//
				ReferenceData[] referenceDataArr =
					spatialAnalysisResults.createReferenceDataForAllDiffusionRates(frapDataTimeStamps);
				ODESolverResultSet[] odeSolverResultSetArr =
					spatialAnalysisResults.createODESolverResultSetForAllDiffusionRates();
				spatialAnalysisList = new MultisourcePlotPane[spatialAnalysisResults.diffusionRates.length];
				for (int i = 0; i < spatialAnalysisResults.diffusionRates.length; i++) {
					String title = "Experimental vs. Diffusion Rate "+spatialAnalysisResults.diffusionRates[i];
					DataSource expDataSource = new DataSource(referenceDataArr[i],"experiment");
					DataSource fitDataSource = new DataSource(odeSolverResultSetArr[i], "fit");
					MultisourcePlotPane multisourcePlotPane = new MultisourcePlotPane();
					multisourcePlotPane.setDataSources(new DataSource[] {  expDataSource, fitDataSource } );
					multisourcePlotPane.selectAll();
					multisourcePlotPane.setBorder(new TitledBorder(new LineBorder(new Color(168,168,255)),title, TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 12)));
					spatialAnalysisList[i]=multisourcePlotPane;

				}
				VirtualFrapMainFrame.statusBar.showProgress(100);
			}
			public boolean skipIfAbort() {
				return false;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return false;
			}
		};
		
		AsynchClientTask afterAnalysisTask = new AsynchClientTask() {
			public String getTaskName() { return "Refreshing report panel."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				// show plot list by default 
				refreshReportPanel();
				// leave save status as it was
				// show message after running spatial analysis
				VirtualFrapMainFrame.statusBar.showProgress(0);
				VirtualFrapMainFrame.statusBar.showStatus("Spatial analysis is done.");
			}
			public boolean skipIfAbort() {
				return false;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return false;
			}
		};
		
		AsynchClientTask[] tasks = new AsynchClientTask[] { spatialAnalysisTask, afterAnalysisTask};
		ClientTaskDispatcher.dispatch(VirtualFrapLoader.mf,hash, tasks, false); 
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == getFRAPDataPanel().getOverlayEditorPanelJAI() && 
			evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
			try {
				crop((Rectangle) evt.getNewValue());
			} catch (Exception e) {
				PopupGenerator.showErrorDialog("Error Cropping:\n"+e.getMessage());
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
	
	public void clearFitSpatialModelPanel()
	{
		getFitSpatialModelPanel().removeAll();
		pdeDataViewer = null;
		flourDataViewer = null;
		getFitSpatialModelPanel().add(getSplitDataViewers(), "Center");
		if(getLocalWorkspace() != null)
		{
			//reset relations between the dataviewers and dataviewermanagers 
			setLocalWorkspace(getLocalWorkspace());
		}
	}
	
	//Added March 2008.
	public JCheckBox getShowReportListButton() {
		if(showReportListCheckBox == null){
			showReportListCheckBox = new JCheckBox("Show Plot List", true);
			showReportListCheckBox.setFont(new Font("Tahoma", Font.BOLD, 12));
			int len = showReportListCheckBox.getActionListeners().length;
			if(len > 0)
			{
				for(int i=(len-1); i<0; i--)
				{
					showReportListCheckBox.removeActionListener(showReportListCheckBox.getActionListeners()[i]);
				}
			}
			showReportListCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(spatialAnalysisList != null && spatialAnalysisList.length >0 )
					{
						if(showReportListCheckBox.isSelected())
						{
							for(int i=0; i<spatialAnalysisList.length; i++)
							{
								spatialAnalysisList[i].setListVisible(true);
								spatialAnalysisList[i].setPreferredSize(new Dimension(650,500));
								spatialAnalysisList[i].invalidate();
							}
						}
						else
						{
							for(int i=0; i<spatialAnalysisList.length; i++)
							{
								spatialAnalysisList[i].setListVisible(false);
								spatialAnalysisList[i].setPreferredSize(new Dimension(450,300));
								spatialAnalysisList[i].invalidate();
							}
						}
						
						getReportPanel().invalidate();
						getReportBasePanel().invalidate();
						getScrollReportPane().revalidate();
					}
				}
			});
		}
		return showReportListCheckBox;
	}
	//Added March 2008
	public JPanel getReportBasePanel() {
		if (reportBasePanel == null) {
			reportBasePanel = new JPanel();
			reportBasePanel.setLayout(new BorderLayout());
		}
		return reportBasePanel;
	}
}