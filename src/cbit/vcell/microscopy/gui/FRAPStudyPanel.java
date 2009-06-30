package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

import cbit.image.ImageException;
import cbit.rmi.event.ExportEvent;

import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ZEnforcer;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.PropertyLoader;
import org.vcell.util.Range;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCDataIdentifier;

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
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.desktop.controls.DataManager;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.MovieSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.microscopy.ExternalDataInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.ROI;
import cbit.vcell.microscopy.FRAPStudy.FRAPModelParameters;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VCData;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.simdata.gui.DisplayPreferences;
import cbit.vcell.simdata.gui.PDEPlotControlPanel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;

public class FRAPStudyPanel extends JPanel implements PropertyChangeListener{
	
	public static final String FRAPSTUDYPANEL_TABNAME_IMAGES = "Image";
	public static final String FRAPSTUDYPANEL_TABNAME_FITRECOVERYCURVE = "Initial Parameters";
	public static final String FRAPSTUDYPANEL_TABNAME_REPORT = "Adjust Parameters";
	public static final String FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS = "2D Results";
	public static final String VFRAP_PREFIX_EXP = "Exp";
	public static final String VFRAP_PREFIX_MASK = "Mask";
	public static final String VFRAP_PREFIX_SIM = "Sim";
	public static final String[] VFRAP_DS_PREFIX = {VFRAP_PREFIX_EXP, VFRAP_PREFIX_MASK, VFRAP_PREFIX_SIM};
	
	public static final LineBorder TAB_LINE_BORDER = new LineBorder(new Color(153, 186,243), 3);
	
	private static final String REACTION_RATE_PREFIX = "J_";
	private static final String NORM_FLUOR_VAR = "Exp_norm_fluor";
	private static final String NORM_SIM_VAR = "Sim_norm_fluor";
	private Expression Norm_Exp_Fluor = null;
	private Expression Norm_Sim = null;
	private static String Norm_Exp_Fluor_Str = "(("+VFRAP_PREFIX_MASK+".cell_mask*((max(("+VFRAP_PREFIX_EXP+".fluor-"+VFRAP_PREFIX_EXP+".bg_average),0)+1)/"+VFRAP_PREFIX_MASK+".prebleach_avg))*("+VFRAP_PREFIX_MASK+".cell_mask > 0))";
	private static String Norm_Sim_One_Diff_Str =VFRAP_PREFIX_SIM+".fluor_primary_mobile + "+VFRAP_PREFIX_SIM+".fluor_immobile";
	private static String Norm_Sim_Two_Diff_Str =VFRAP_PREFIX_SIM+".fluor_primary_mobile + "+VFRAP_PREFIX_SIM+".fluor_secondary_mobile + "+VFRAP_PREFIX_SIM+".fluor_immobile";
	
	//to store movie file info. movie file will be refreshed after each simulation run.
	private String movieURLString = null;
	private String movieFileString = null;
		
	private FRAPStudy frapStudy = null;
	private FRAPOptData frapOptData = null;
	private FRAPDataPanel frapDataPanel = null;
	private LocalWorkspace localWorkspace = null;
	private JTabbedPane jTabbedPane = null;
	private FRAPParametersPanel frapParametersPanel = null;
	//to make alternative view for comparing simulation results/frap data
//	final static String SPLITEDVIEW = "Parallel View";
//	final static String TABBEDVIEW = "Tabbed View";
	private JRadioButton tabRadio = null;
	private JRadioButton splitRadio = null;
	private JButton showMovieButton = null;
	private JPanel simResultsViewPanel = null;
	private PDEDataViewer pdeDataViewer = null;
	private PDEDataViewer flourDataViewer = null;
	private JPanel fitSpatialModelPanel = null;
	private FRAPSimDataViewerPanel frapSimDataViewerPanel = null;
	private boolean isSetTabIdxFromSpatialAnalysis = false;	
	private ResultsSummaryPanel resultsSummaryPanel;
	
	public interface NewFRAPFromParameters{
		void create(Parameter[] newParameters);
	}
	
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
		public final boolean bSecondRateChanged;
		public final String secondRateString;
		public final boolean bSecondFractionChanged;
		public final String secondFractionString;
		public final boolean bStartIndexForRecoveryChanged;
		public final String startIndexForRecoveryString;
		public FrapChangeInfo(boolean bROIValuesChanged,boolean bROISizeChanged,
				boolean bDiffusionRateChanged,String diffusionRateString,
				boolean bBleachWhileMonitorChanged,String bleachWhileMonitorRateString,
				boolean bMobileFractionChanged,String mobileFractionString,
				boolean bSecondRateChanged, String secondRateString,
				boolean bSecondFractionChanged, String secondFractionString,
				boolean bStartIndexForRecoveryChanged,String startIndexForRecoveryString)
		{
			this.bROIValuesChanged = bROIValuesChanged;
			this.bROISizeChanged = bROISizeChanged;
			this.bDiffusionRateChanged = bDiffusionRateChanged;
			this.diffusionRateString = diffusionRateString;
			this.bBleachWhileMonitorChanged = bBleachWhileMonitorChanged;
			this.bleachWhileMonitorRateString = bleachWhileMonitorRateString;
			this.bMobileFractionChanged = bMobileFractionChanged;
			this.mobileFractionString = mobileFractionString;
			this.bSecondRateChanged = bSecondRateChanged;
			this.secondRateString = secondRateString;
			this.bSecondFractionChanged = bSecondFractionChanged;
			this.secondFractionString = secondFractionString;
			this.bStartIndexForRecoveryChanged = bStartIndexForRecoveryChanged;
			this.startIndexForRecoveryString = startIndexForRecoveryString;
			//Don't forget to change 'hasAnyChanges' if adding new parameters
		}
		public boolean hasAnyChanges(){
			return bROIValuesChanged || bROISizeChanged ||
				bDiffusionRateChanged || bBleachWhileMonitorChanged ||
				bMobileFractionChanged || bSecondRateChanged || bSecondFractionChanged ||
				bStartIndexForRecoveryChanged;
		}
		public boolean hasStartingIdxChanged()
		{
			return bStartIndexForRecoveryChanged;
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
			(bSecondRateChanged?"(Secondary Diffusion Rate)":" ")+
			(bSecondFractionChanged?"(Secondary Mobile Fraction Rate)":" ")+
			(bStartIndexForRecoveryChanged?"(Start Index for Recovery)":" ");

		}
		public String getROIOrStartingIdxChangeInfo(){
			return
			(bROIValuesChanged?"Cell,Bleach or Backgroung ROI":" ")+
			(bROISizeChanged?"Data Dimension":" ")+
			(bStartIndexForRecoveryChanged?"Start Index for Recovery":" ");
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
		public final String lastSecondRate;
		public final String lastSecondFraction;
		public final String startingIndexForRecovery;
		public SavedFrapModelInfo(
			KeyValue savedSimKeyValue,
			ROI lastCellROI,
			ROI lastBleachROI,
			ROI lastBackgroundROI,
			String lastBaseDiffusionrate,
			String lastBleachWhileMonitoringRate,
			String lastMobileFraction,
			String lastSecondRate,
			String lastSecondFraction,
			String startingIndexForRecovery)
		{
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
			this.lastSecondRate = lastSecondRate;
			this.lastSecondFraction = lastSecondFraction;
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
				return "Simulation has never run or Simulation files are corrupt.";
			}
			throw new RuntimeException("Unknown description");
		}
	};

	private	DataSetControllerImpl.ProgressListener progressListenerZeroToOne =
		new DataSetControllerImpl.ProgressListener(){
			public void updateProgress(double progress) {
				VirtualFrapMainFrame.updateProgress((int)Math.round(progress*100));
			}
			public void updateMessage(String message) {
				//ignore
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
			URL cursorImageURL = null;
			if(i == CURSOR_CELLROI){
				cursorImageURL = FRAPStudyPanel.class.getResource("/images/cursorCellROI.gif");
			}else if(i == CURSOR_BLEACHROI){
				cursorImageURL = FRAPStudyPanel.class.getResource("/images/cursorBleachROI.gif");
			}else if(i == CURSOR_BACKGROUNDROI){
				cursorImageURL = FRAPStudyPanel.class.getResource("/images/cursorBackgroundROI.gif");
			}
			ImageIcon imageIcon = new ImageIcon(cursorImageURL);
			Image cellCursorImage = imageIcon.getImage();
			BufferedImage tempImage =
				new BufferedImage(cellCursorImage.getWidth(null),cellCursorImage.getHeight(null),BufferedImage.TYPE_INT_ARGB);
			tempImage.createGraphics().drawImage(
				cellCursorImage, 0, 0, tempImage.getWidth(), tempImage.getHeight(), null);
			//outline with black
			for (int y = 0; y < tempImage.getHeight(); y++) {
				for (int x = 0; x < tempImage.getWidth(); x++) {
//						System.out.print(Integer.toHexString(tempImage.getRGB(x, y))+" ");
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
//					System.out.println();
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
		String savedSecondRate = null;
		String savedSecondFraction = null;
		String startingIndexForRecovery = null;
		if(frapModelParameters != null){
			savedDiffusionRate = frapModelParameters.diffusionRate;
			savedBleachWhileMonitoringRate = frapModelParameters.monitorBleachRate;
			savedMobileFraction = frapModelParameters.mobileFraction;
			savedSecondRate = frapModelParameters.secondRate;
			savedSecondFraction = frapModelParameters.secondFraction;
			startingIndexForRecovery = frapModelParameters.startIndexForRecovery;
		}
		return new SavedFrapModelInfo(
				savedSimKey,
				savedCellROI,savedBleachROI,savedBackgroundROI,
				savedDiffusionRate,
				savedBleachWhileMonitoringRate,
				savedMobileFraction,
				savedSecondRate,
				savedSecondFraction,
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
			frapDataPanel.setBorder(TAB_LINE_BORDER);
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
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_IMAGES, null, getFRAPDataPanel(), null);
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_FITRECOVERYCURVE, null, getFRAPParametersPanel(), null);
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_REPORT, null, getResultsSummaryPanel(), null);
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS, null, getSimResultsViewPanel(), null);
			jTabbedPane.setModel(
				new DefaultSingleSelectionModel(){
					@Override
					public void setSelectedIndex(int index) {
						try{
							String exitTabTitle = (getSelectedIndex() == -1?null:getJTabbedPane().getTitleAt(getSelectedIndex()));
							if(updateTabbedView(exitTabTitle,getJTabbedPane().getTitleAt(index))){
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
			jTabbedPane.setSelectedIndex(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_IMAGES));
			jTabbedPane.setEnabledAt(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS), false);
		}
		return jTabbedPane;
	}

	private FRAPSimDataViewerPanel getFRAPSimDataViewerPanel(){
		if(frapSimDataViewerPanel == null){
			frapSimDataViewerPanel = new FRAPSimDataViewerPanel();
			frapSimDataViewerPanel.setBorder(TAB_LINE_BORDER);
		}
		return frapSimDataViewerPanel;
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
						new UShortImage(bleachPixels,
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getX(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getY(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getZ());
					ROI newBleachROI = new ROI(ushortImage,RoiType.ROI_BLEACHED);
					getFrapStudy().getFrapData().addReplaceRoi(newBleachROI);
				}
				if(bFixedBackground){
					UShortImage ushortImage =
						new UShortImage(backgroundPixels,
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
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
	
	private void checkStartIndexforRecovery() throws Exception{
		if(getFrapStudy() != null && getFrapStudy().getFrapModelParameters() != null &&
			getFrapStudy().getFrapModelParameters().startIndexForRecovery != null){
			
			int startIndexForRecoveryInt = Integer.parseInt(getFrapStudy().getFrapModelParameters().startIndexForRecovery);
			if(startIndexForRecoveryInt > 0){
				return;
			}
		}
		throw new Exception("Starting Index for Recovery cannot be the first time point.  PRE-BLEACH data is required for anlaysis.");
	}
	
	private boolean updateTabbedView(String exitTabTitle,String enterTabTitle) throws Exception{
		if(!enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IMAGES) && (getFrapStudy() == null || getFrapStudy().getFrapData() == null)){
			throw new Exception("No document open.  Use 'File->Open' menu to open a new document");
		}
		try{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if(exitTabTitle != null){
				if(exitTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IMAGES)){
					applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ROI);
					if(getFrapStudy() != null && getFrapStudy().getFrapData() != null && checkROIConstraints()){
						return false;
					}
				}else if(exitTabTitle.equals(FRAPSTUDYPANEL_TABNAME_FITRECOVERYCURVE)){
					applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_MODEL_PARAMS);
				}
			}
			if(enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_FITRECOVERYCURVE)){
				try{
					getFRAPParametersPanel().refreshFRAPModelParameterEstimates(getFrapStudy().getFrapData());
				}catch(Exception e){
					DialogUtils.showWarningDialog(this, 
							"Some FRAP Model Parameter Estimation help won't be available because:\n"+e.getMessage(),
							new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
				}
			}
			else if(enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_REPORT)){
				if(!isSetTabIdxFromSpatialAnalysis)
				{
					checkStartIndexforRecovery();
					refreshBiomodel();
					CurrentSimulationDataState currentSimulationDataState = null;
					if(getSavedFrapModelInfo() != null){
						currentSimulationDataState = new CurrentSimulationDataState();
					}
					if(currentSimulationDataState != null)//model has been saved before
					{
						//if ROIs or starting index for recovery have changed
						if(currentSimulationDataState.frapChangeInfo != null && 
						   (currentSimulationDataState.frapChangeInfo.hasROIChanged() || currentSimulationDataState.frapChangeInfo.hasStartingIdxChanged()))
						{
							String choice = DialogUtils.showWarningDialog(this, currentSimulationDataState.frapChangeInfo.getROIOrStartingIdxChangeInfo()+" have been changed.\n Firstly, reference simulation need to be rerun.\n Secondly, 2D Results(if any) is out of synchronization too. \n Please click 'Create 2D Simulation' button in 'Adjust Parameters' tab to update the 2D Results.",
									                      new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
							if(choice.equals(UserMessage.OPTION_OK))//users choose to rerun reference sim
							{
								//to have external files saved again, if the files are corrupt or ROIs have been changed
								if(!currentSimulationDataState.areExtDataFileOK ||
								   (currentSimulationDataState.frapChangeInfo != null && currentSimulationDataState.frapChangeInfo.hasROIChanged()))
								{
									//if external files are missing/currupt or ROIs are changed, create keys and save them
									getFrapStudy().setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
									getFrapStudy().setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
									getFrapStudy().saveROIsAsExternalData(localWorkspace,
											getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
									getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
											getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
									
								}
								//New external data files have been saved already(see above)					
								//ROIs or starting index has changed. We have to update saved frap model info.
								setSavedFrapModelInfo(FRAPStudyPanel.createSavedFrapModelInfo(getFrapStudy()));
								spatialAnalysis(null, true);
							}
							else // not to rerun ref sim, then the tab won't be changed 
							{
								return false;
							}
						}
						else
						{
							//to have external files saved again, if the files are corrupt 
							if(!currentSimulationDataState.areExtDataFileOK)
							{
								//if external files are missing/currupt or ROIs are changed, create keys and save them
								getFrapStudy().setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
								getFrapStudy().setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
								getFrapStudy().saveROIsAsExternalData(localWorkspace,
										getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
								getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
										getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
								
							}
							if(frapOptData == null)
							{   //have to create frapOptData if it is null.
								spatialAnalysis(null, false);
							}
							else
							{
								return true;
							}
						}
					}
					else //current sim status is null, model hasn't been saved or run, the ref sim must run
					{
						spatialAnalysis(null, true);
	 				}
					return false;
				}
			}else if(enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS)){
				CurrentSimulationDataState currentSimulationDataState = null;
				if(getSavedFrapModelInfo() != null)
				{
					currentSimulationDataState = new CurrentSimulationDataState();
				
					if(currentSimulationDataState != null)//model has been saved before
					{
						//if ROIs or starting index for recovery have changed
						if(currentSimulationDataState.frapChangeInfo != null && 
						   (currentSimulationDataState.frapChangeInfo.hasROIChanged() || currentSimulationDataState.frapChangeInfo.hasStartingIdxChanged()))
						{
							DialogUtils.showWarningDialog(currentSimulationDataState.frapChangeInfo.getROIOrStartingIdxChangeInfo()+" have been changed.\n 2D Results is out of synchronization. \n You may want to click 'Create 2D Simulation' button in 'Adjust Parameters' tab to update the 2D Results.");
						}
					}
										
					if(areSimulationFilesOKForSavedModel())
					{
						refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);
					}else{
						DialogUtils.showErrorDialog("Simulation Data are not found to view 2D spatial results. \nSimulation has never been run or simulation files are corrupt.\n" +
													"Please click 'Create 2D Simulation' button in 'Adjust Parameters' tab to run the simulation.");
						return false;
					}
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

	/**
	 * This method initializes fitSpatialModelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFitSpatialModelPanel() {
		if (fitSpatialModelPanel == null) {
			fitSpatialModelPanel = new JPanel();
			fitSpatialModelPanel.setLayout(new BorderLayout());
			fitSpatialModelPanel.add(getFRAPSimDataViewerPanel(), BorderLayout.CENTER);
		}
		return fitSpatialModelPanel;
	}
	
	/**
	 * This method initializes fitSpatialModelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSimResultsViewPanel() {
		if (simResultsViewPanel == null) {
			simResultsViewPanel = new JPanel();
			JPanel radioPanel = new JPanel();
			radioPanel.setLayout(new FlowLayout());
			showMovieButton = new JButton("Show Movie with Exp and Sim Comparison");
			showMovieButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					showMovie();
				}
			});
			radioPanel.add(showMovieButton);
			simResultsViewPanel.setLayout(new BorderLayout());
			simResultsViewPanel.add(radioPanel, BorderLayout.NORTH);
			simResultsViewPanel.add(getFitSpatialModelPanel(), BorderLayout.CENTER);
		}
		return simResultsViewPanel;
	}
	
	private ResultsSummaryPanel getResultsSummaryPanel(){
		if(resultsSummaryPanel == null){
			resultsSummaryPanel = new ResultsSummaryPanel();
		}
		return resultsSummaryPanel;
	}
	
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
		newFrapStudy.setStoredRefData(getFrapStudy().getStoredRefData());
		setFrapStudy(newFrapStudy,false);
	}
	
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
	
	public boolean saveAs() throws Exception{
		if(SwingUtilities.isEventDispatchThread()){
			throw new Exception("SaveAs not EventDispatchThread");
		}
		if(getFrapStudy().getFrapData() != null){
			if(getJTabbedPane().getTitleAt(getJTabbedPane().getSelectedIndex()).equals(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS) ||
				getJTabbedPane().getTitleAt(getJTabbedPane().getSelectedIndex()).equals(FRAPSTUDYPANEL_TABNAME_REPORT)){
				//SaveASNew has empty sim data so can't be viewing these tabs after save
				final String CONTINUE_SAVING = "Switch and continue SaveAsNew";
				String result = DialogUtils.showWarningDialog(this,
						"Simulation Data will be empty for the new document.  Switch to '"+FRAPSTUDYPANEL_TABNAME_IMAGES+"' tab before saving?" ,
						new String[] {CONTINUE_SAVING,UserMessage.OPTION_CANCEL}, CONTINUE_SAVING);
				if(result == null || !result.equals(CONTINUE_SAVING)){
					return false;
				}
				SwingUtilities.invokeAndWait(
					new Runnable(){
						public void run(){getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_IMAGES));}
					}
				);
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
		return true;
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
				FRAPStudy.removeExternalDataAndSimulationFiles(
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
	
	private void saveProcedure(File xmlFrapFile,boolean bSaveAsNew) throws Exception{
		VirtualFrapMainFrame.updateStatus("Saving file " + xmlFrapFile.getAbsolutePath()+" ...");
		
		BioModel newBioModel = null;
		try{
			newBioModel = FRAPStudy.createNewBioModel(getFrapStudy(),
				getFrapStudy().getFrapModelParameters().diffusionRate,
				getFrapStudy().getFrapModelParameters().monitorBleachRate,
				getFrapStudy().getFrapModelParameters().mobileFraction,
				getFrapStudy().getFrapModelParameters().secondRate==null?null:new Double(getFrapStudy().getFrapModelParameters().secondRate),
				getFrapStudy().getFrapModelParameters().secondFraction,
				null, 
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
		
		MicroscopyXmlproducer.writeXMLFile(getFrapStudy(), xmlFrapFile, true,progressListenerZeroToOne,VirtualFrapMainFrame.SAVE_COMPRESSED);
		getFrapStudy().setXmlFilename(xmlFrapFile.getAbsolutePath());
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			try{
			setSavedFrapModelInfo(FRAPStudyPanel.createSavedFrapModelInfo(getFrapStudy()));	
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}});
		VirtualFrapMainFrame.updateStatus("File " + xmlFrapFile.getAbsolutePath()+" has been saved.");
        VirtualFrapLoader.mf.setMainFrameTitle(xmlFrapFile.getName());
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
			FRAPStudy.removeExternalDataAndSimulationFiles(simulationKeyValue,frapDataExtDataId,roiExtDataId,getLocalWorkspace());
			return true;
		}
		return false;
	}
	
	private void clearCurrentLoadState() throws Exception{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			setFrapStudy(new FRAPStudy(), true);						
            VirtualFrapLoader.mf.setMainFrameTitle("");
            getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_IMAGES));
		}});
	}
	protected void load(final File[] inFileArr,final MultiFileImportInfo multiFileImportInfo) throws Exception{
				
		final String inFileDescription =
			(inFileArr.length == 1
				?"file "+inFileArr[0].getAbsolutePath()
				:"files from "+inFileArr[0].getParentFile().getAbsolutePath());

		final AsynchProgressPopup_orig pp =
			new AsynchProgressPopup_orig(
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
							public void updateMessage(String message) {
								//ignore
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
						}else{//.lsm
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
					}else{//multiple file series
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
						jTabbedPane.setEnabledAt(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS), false);
						finalNewFrapStudy.setXmlFilename(finalNewVFRAPFileName);
						setFrapStudy(finalNewFrapStudy,true);
						try{
							setSavedFrapModelInfo(finalNewSavedFrapModelInfo);
						}catch(Exception e){
							throw new RuntimeException(e.getMessage());
						}
						if(!jTabbedPane.isEnabledAt(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS)) && 
							getSavedFrapModelInfo()!= null && areSimulationFilesOKForSavedModel())
						{
							jTabbedPane.setEnabledAt(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS), true);
						}
						FRAPStudyPanel.this.refreshUI();			

			            VirtualFrapLoader.mf.setMainFrameTitle((finalNewVFRAPFileName != null?finalNewVFRAPFileName:"New Document"));
					}});

		            VirtualFrapMainFrame.updateStatus("Loaded " + inFileDescription);
		            VirtualFrapMainFrame.updateProgress(0);
		            //after loading new file, we need to set frapOptData to null.
		            frapOptData = null;
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
		File[] simFiles = FRAPStudy.getSimulationFileNames(
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
	
	private static boolean areReferenceDataOK(LocalWorkspace localWorkspace,ExternalDataInfo refDataExtDataInfo)
	{
		if(refDataExtDataInfo == null || refDataExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] referenceDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					refDataExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;referenceDataExtDataFiles != null && i < referenceDataExtDataFiles.length; i++) {
			if(!referenceDataExtDataFiles[i].exists()){
				return false;
			}
		}
		return true;
	}
	public static String convertDoubletoString(double doubleValue){
		String doubleString = doubleValue+"";
		return doubleString;
	}

	protected void runSimulation(final boolean bSpatialAnalysis) throws Exception{
		
		
		
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

		final AsynchProgressPopup_orig pp =
			new AsynchProgressPopup_orig(
				FRAPStudyPanel.this,
				"Running FRAP Model Simulation",
				"Working...",true,true
		);
		pp.start();
		new Thread(new Runnable(){
			public void run(){
				try {
					boolean executed = false;
					if(bSaveAsNew[0] != null){
						final String SAVING_BEFORE_RUN_MESSAGE = "Saving before simulation runs...";
						VirtualFrapMainFrame.updateStatus(SAVING_BEFORE_RUN_MESSAGE);
						pp.setMessage(SAVING_BEFORE_RUN_MESSAGE);
						if(bSaveAsNew[0]){
							executed = saveAs();
						}else {
							save();
						}
					}
					if(bSaveAsNew[0] != null && bSaveAsNew[0] && !executed) //cancelled saveas, then nothing should happen.
					{
						pp.stop();
					}
					else
					{
						// Reset spatial analysis
						SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							getResultsSummaryPanel().clearData();//spatialAnalysisList = null;
						}});
						final String SAVING_EXT_DATA_MESSAGE = "Saving ROI and initial conditions...";
						VirtualFrapMainFrame.updateStatus(SAVING_EXT_DATA_MESSAGE);
						pp.setMessage(SAVING_EXT_DATA_MESSAGE);
						VirtualFrapMainFrame.updateProgress(0);
//						genericProgress(10,genericProgressStopSignal);
						if(getSavedFrapModelInfo() == null || cleanupSavedSimDataFilesIfNotOK()){
							getFrapStudy().saveROIsAsExternalData(localWorkspace,
								getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
							getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
								getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
						}
//						genericProgressStopSignal[0] = true;
						
						final double RUN_SIM_PROGRESS_FRACTION = (bSpatialAnalysis?.2:1.0);
						DataSetControllerImpl.ProgressListener runSimProgressListener =
							new DataSetControllerImpl.ProgressListener(){
								public void updateProgress(double progress) {
									int percentProgress = (int)(progress*100*RUN_SIM_PROGRESS_FRACTION);
									VirtualFrapMainFrame.updateProgress(percentProgress);
									pp.setProgress(percentProgress);
								}
								public void updateMessage(String message) {
									VirtualFrapMainFrame.updateStatus(message);
									pp.setMessage(message);
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
							runSimProgressListener, false);
						
						if(!bSpatialAnalysis){
							pp.stop();
							SwingUtilities.invokeAndWait(new Runnable(){public void run(){
								try{
									VirtualFrapMainFrame.updateStatus("Updating Simulation Data display.");
									refreshPDEDisplay(DisplayChoice.PDE);//upper data viewer has simulated data and masks
									refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);//lower data viewer has original data and masks
									getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS));
									VirtualFrapMainFrame.updateProgress(0);
									VirtualFrapMainFrame.updateStatus("");
								}catch(Exception e){
									throw new RuntimeException("Error Updating simData display.  "+e.getMessage(),e);
								}
							}});
							return;
						}
						
						final String FINISHED_MESSAGE = "Finished simulation, running spatial analysis...";
						VirtualFrapMainFrame.updateStatus(FINISHED_MESSAGE);
						pp.setMessage(FINISHED_MESSAGE);
//						VirtualFrapMainFrame.updateProgress(100);
//						pp.setProgress(100);
						
						
						DataSetControllerImpl.ProgressListener optimizationProgressListener =
							new DataSetControllerImpl.ProgressListener(){
								public void updateProgress(double progress) {
									if(progress == 1.0){
										VirtualFrapMainFrame.updateProgress(0);
										pp.stop();
										return;
									}
									int percentProgress = (int)(100*(RUN_SIM_PROGRESS_FRACTION+progress*(1-RUN_SIM_PROGRESS_FRACTION)));
									VirtualFrapMainFrame.updateProgress(percentProgress);
									pp.setProgress(percentProgress);
								}
								public void updateMessage(String message) {
									VirtualFrapMainFrame.updateStatus(message);
									pp.setMessage(message);
								}
						};
						//enable 2D results tab after successfully running the simulation
						spatialAnalysis(optimizationProgressListener, false);
						if(!jTabbedPane.isEnabledAt(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS)) && 
							getSavedFrapModelInfo()!= null && areSimulationFilesOKForSavedModel())
						{
							SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							try{
								jTabbedPane.setEnabledAt(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_SPATIALRESULTS), true);
							}catch(Exception e){
								throw new RuntimeException("Error when disable 2D results tab: "+e.getMessage(),e);
							}
							}});
						}
						
						//remove movie file and reset movie file strings
						if(movieFileString != null)
						{
							new File(movieFileString).delete();
						}
						movieURLString = null;
						movieFileString = null;
					}		
				}catch(UserCancelException uce){
					pp.stop();
					return;
				}catch (final Exception e) {
					pp.stop();
//					genericProgressStopSignal[0] = true;
					VirtualFrapMainFrame.updateProgress(0);
					VirtualFrapMainFrame.updateStatus("Error running simulation/spatial analysis: "+e.getMessage());
					e.printStackTrace();
					try{
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
						DialogUtils.showErrorDialog("Error running simulation/spatial analysis:\n"+e.getMessage());
					}});
					}catch(Exception e2){
						e2.printStackTrace();
					}
				}finally{
//					pp.stop();
				}
			}
		}).start();
	}	
	
	private void showMovie()
	{
		if(movieURLString != null && movieFileString != null)
		{
			JMFPlayer.showMovieInFrame(movieURLString, movieFileString);
			return;
		}
		final AsynchProgressPopup_orig pp =
			new AsynchProgressPopup_orig(
				FRAPStudyPanel.this,
				"Buffering movie data...",
				"Working...",true,false
		);
		pp.start();		
		new Thread(new Runnable()
		{
			public void run()
			{
		
				try{
					//create export specs
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
					ExternalDataIdentifier timeSeriesExtDataID = getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier();
					ExternalDataIdentifier maskExtDataID = getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier();
					//add sim
					int jobIndex = 0;
					SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
									
					VCDataIdentifier[] dataIDs = new VCDataIdentifier[] {timeSeriesExtDataID, maskExtDataID, simJob.getVCDataIdentifier()};
					VCDataIdentifier vcDataId = new MergedDataInfo(LocalWorkspace.getDefaultOwner(),dataIDs, VFRAP_DS_PREFIX);
									
					DataManager dataManager = new MergedDataManager(getLocalWorkspace().getVCDataManager(),vcDataId);
					PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);
							
					int format = ExportConstants.FORMAT_QUICKTIME;
					String[] variableNames = new String[]{NORM_FLUOR_VAR, NORM_SIM_VAR};
					VariableSpecs variableSpecs = new VariableSpecs(variableNames, ExportConstants.VARIABLE_MULTI);
							
					int endTimeIndex = (int)Math.round(sim.getSolverTaskDescription().getTimeBounds().getEndingTime()/sim.getSolverTaskDescription().getTimeStep().getDefaultTimeStep());
					TimeSpecs timeSpecs = new TimeSpecs(0, endTimeIndex, pdeDataContext.getTimePoints(), ExportConstants.TIME_RANGE);
					int geoMode = ExportConstants.GEOMETRY_SLICE;
					GeometrySpecs geometrySpecs = new GeometrySpecs(null, Coordinate.Z_AXIS, 0, geoMode);
					
					double duration = 10000; //10s
					DisplayPreferences pref1 = new DisplayPreferences("BlueRed", new Range(0.01,1.1),true);
					DisplayPreferences pref2 = new DisplayPreferences("BlueRed", new Range(0.01,1.1),true);
					DisplayPreferences[] displayPref = new DisplayPreferences[]{pref1, pref2};
					MovieSpecs mSpec = new MovieSpecs(duration,
													 true,
													 displayPref,
													 ExportConstants.RAW_RGB,
													 ExportConstants.NO_MIRRORING,
													 false);
					ExportSpecs exSpecs = new ExportSpecs(vcDataId, format, variableSpecs, timeSpecs, geometrySpecs, mSpec);
					// pass the request
					ExportEvent exportEvt = ((VirtualFrapWindowManager)getFlourDataViewer().getDataViewerManager()).startExportMovie(exSpecs);
					//show movie if successfully exported
					if(exportEvt != null)
					{
						final String fileURLString = System.getProperty(PropertyLoader.exportBaseURLProperty) + exportEvt.getJobID() + ".mov";
						final String fileString = System.getProperty(PropertyLoader.exportBaseDirProperty) + exportEvt.getJobID() + ".mov";
						movieURLString = fileURLString;
						movieFileString = fileString;
						try{
							SwingUtilities.invokeAndWait(new Runnable(){public void run(){
								JMFPlayer.showMovieInFrame(fileURLString, fileString);
							}});
						}catch(Exception e2){
							e2.printStackTrace();
						}
					}
				}catch(Exception e)
				{
					e.printStackTrace(System.out);
					DialogUtils.showErrorDialog("Error making the movie!\n"+e.getMessage());
					pp.stop();
				}
				finally
				{
					pp.stop();
				}
			}
		}).start();
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
		PDEDataViewer flourViewer = getFlourDataViewer();/*, getFlourDataViewer2()*/
		DataManager dataManager = null;

		if (choice == DisplayChoice.EXTTIMEDATA){
			
				flourViewer.setSimulation(null);
				flourViewer.setPdeDataContext(null);
				
				flourViewer.setDataIdentifierFilter(
				new PDEPlotControlPanel.DataIdentifierFilter(){
					private String ALL_DATAIDENTIFIERS = "All";
					private String EXP_NORM_FLUOR = "Exp. Norm. Fluor";
					private String SIM_NORM_FLUOR = "Sim. Norm. Fluor";
					private String DEFAULT_VIEW = "Default View (more...)";
					private String[] filterSetNames = new String[] {ALL_DATAIDENTIFIERS, EXP_NORM_FLUOR, SIM_NORM_FLUOR, DEFAULT_VIEW};
					public boolean accept(String filterSetName,DataIdentifier dataidentifier) {
						if(filterSetName.equals(ALL_DATAIDENTIFIERS)){
							return true;
						}else if(filterSetName.equals(EXP_NORM_FLUOR)){
							return
								dataidentifier.getName().indexOf(NORM_FLUOR_VAR) != -1;
						}else if(filterSetName.equals(SIM_NORM_FLUOR)){
							boolean a = (dataidentifier.getName().indexOf(REACTION_RATE_PREFIX) == -1) &&
							((dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED)!= -1) ||
									(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE)!= -1) ||
									(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_MOBILE)!= -1) ||
									(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE)!= -1));
							return a;
						}else if(filterSetName.equals(DEFAULT_VIEW)){
							boolean a = (dataidentifier.getName().indexOf(REACTION_RATE_PREFIX) == -1) &&
							(//(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED)!= -1) ||
									(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE)!= -1) ||
									(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_MOBILE)!= -1) ||
									(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE)!= -1))||
									(dataidentifier.getName().indexOf(NORM_FLUOR_VAR)!= -1)||
									(dataidentifier.getName().indexOf(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED)!= -1);
							return a;
						}
						throw new IllegalArgumentException("DataIdentifierFilter: Unknown filterSetName "+filterSetName);
					}
					public String getDefaultFilterName() {
						return DEFAULT_VIEW;
					}
					public String[] getFilterSetNames() {
						return filterSetNames;
					}
					public boolean isAcceptAll(String filterSetName){
						return filterSetName.equals(ALL_DATAIDENTIFIERS);
					}
				}
			);
			
			ExternalDataIdentifier timeSeriesExtDataID = getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier();
			ExternalDataIdentifier maskExtDataID = getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier();
			//add sim
			int jobIndex = 0;
			SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
							
			VCDataIdentifier[] dataIDs = new VCDataIdentifier[] {timeSeriesExtDataID, maskExtDataID, simJob.getVCDataIdentifier()};
			VCDataIdentifier vcDataId = new MergedDataInfo(LocalWorkspace.getDefaultOwner(),dataIDs, VFRAP_DS_PREFIX);
							
			dataManager = new MergedDataManager(getLocalWorkspace().getVCDataManager(),vcDataId);
			PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);
			// add function to display normalized fluorence data 
			Norm_Exp_Fluor = new Expression(Norm_Exp_Fluor_Str);
			if(sim.getVariable(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE) == null)//one diffusing component
			{
				Norm_Sim = new Expression(Norm_Sim_One_Diff_Str);
			}
			else // two diffusing components
			{
				Norm_Sim = new Expression(Norm_Sim_Two_Diff_Str);
			}
			AnnotatedFunction[] func = {new AnnotatedFunction(NORM_FLUOR_VAR, Norm_Exp_Fluor, null, VariableType.VOLUME, false),
										new AnnotatedFunction(NORM_SIM_VAR, Norm_Sim, null, VariableType.VOLUME, false)};
			
			
			VCData mergeData = localWorkspace.getDataSetControllerImpl().getVCData(vcDataId);			
			
			for(int k=0; k<func.length; k++)
			{
				mergeData.addFunction(func[k], true);

			}
			pdeDataContext.refreshIdentifiers();
			flourViewer.setSimulation(sim);
			flourViewer.setPdeDataContext(pdeDataContext);
			SimulationModelInfo simModelInfo =
				new SimulationWorkspaceModelInfo(
						getFrapStudy().getBioModel().getSimulationContext(sim),
						sim.getName()
				);
			flourViewer.setSimulationModelInfo(simModelInfo);
			try {
				getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(flourViewer);
				((VirtualFrapWindowManager)flourViewer.getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flourViewer.repaint();
			
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
			
			Double bleachWhileMonitoringRate = null;
			if(frapChangeInfo.bleachWhileMonitorRateString == null){
				throw new Exception("bleach while monitoring rate must be set to a value equal or greater than 0.");
			}
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
			Double secDiffusionRate = null;
			if(frapChangeInfo.diffusionRateString == null){
				throw new Exception("Diffusion Rate must be set to a value greater than 0.");
			}
			try{
				baseDiffusionRate =
					(frapChangeInfo.diffusionRateString == null?null:new Double(frapChangeInfo.diffusionRateString));
			}catch(Exception e){
				throw new Exception("Error parsing value for primary Diffusion Rate. ("+frapChangeInfo.diffusionRateString+")");
			}
			if(baseDiffusionRate != null && baseDiffusionRate.doubleValue() < 0.0){
				throw new Exception("Primary Diffusion Rate must be positive. ("+baseDiffusionRate.doubleValue()+")");			
			}
			
			try{
				secDiffusionRate =
					(frapChangeInfo.secondRateString == null?null:new Double(frapChangeInfo.secondRateString));
			}catch(Exception e){
				throw new Exception("Error parsing value for secondary Diffusion Rate. ("+frapChangeInfo.secondRateString+")");
			}
			if(secDiffusionRate != null && secDiffusionRate.doubleValue() < 0.0){
				throw new Exception("Secondary Diffusion Rate must be positive. ("+secDiffusionRate.doubleValue()+")");			
			}

			
			getFrapStudy().refreshDependentROIs();
			BioModel bioModel = FRAPStudy.createNewBioModel(
				getFrapStudy(),
				frapChangeInfo.diffusionRateString,
				frapChangeInfo.bleachWhileMonitorRateString,
				frapChangeInfo.mobileFractionString,
				secDiffusionRate,
				frapChangeInfo.secondFractionString,
				null,
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

	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}

	public void setLocalWorkspace(LocalWorkspace arg_localWorkspace) {
		this.localWorkspace = arg_localWorkspace;
		getFRAPDataPanel().setLocalWorkspace(localWorkspace);
		try {
//			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getPDEDataViewer());
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getPDEDataViewer2());
//			((VirtualFrapWindowManager)getPDEDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			((VirtualFrapWindowManager)getPDEDataViewer2().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getFlourDataViewer());
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getFlourDataViewer2());
			((VirtualFrapWindowManager)getFlourDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			((VirtualFrapWindowManager)getFlourDataViewer2().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
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

	private PDEDataViewer getPDEDataViewer2()
	{
		if (pdeDataViewer == null) {
			pdeDataViewer = new PDEDataViewer();
			VirtualFrapWindowManager dataViewerManager = new VirtualFrapWindowManager();
			try {
				pdeDataViewer.setDataViewerManager(dataViewerManager);
				dataViewerManager.addDataJobListener(pdeDataViewer);
				dataViewerManager.addExportListener(pdeDataViewer);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pdeDataViewer;
	}

	private PDEDataViewer getFlourDataViewer() {
		return getFRAPSimDataViewerPanel().getOriginalDataViewer();
	}
	
	private PDEDataViewer getFlourDataViewer2()
	{
		if (flourDataViewer == null) {
			flourDataViewer = new PDEDataViewer();
			VirtualFrapWindowManager dataViewerManager = new VirtualFrapWindowManager();
			try {
				flourDataViewer.setDataViewerManager(dataViewerManager);
				dataViewerManager.addDataJobListener(flourDataViewer);
				dataViewerManager.addExportListener(flourDataViewer);
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
		PDEDataViewer pdeViewer = getPDEDataViewer2();
		JScrollPane upScroll = new JScrollPane(pdeViewer);
		upScroll.setAutoscrolls(true);
		upPanel.setLayout(new BorderLayout());
		upPanel.add(upScroll, BorderLayout.CENTER);
		upPanel.add(new JLabel("  "), BorderLayout.WEST);
		upPanel.add(new JLabel("  "), BorderLayout.EAST);
		upPanel.add(new JLabel(" "), BorderLayout.NORTH);
		upPanel.add(new JLabel(" "), BorderLayout.SOUTH);
		upPanel.setBorder(new TitledBorder(new LineBorder(new Color(168,168,255)),"Simulation Results", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 12)));
		
		
		JPanel botPanel = new JPanel();		
		PDEDataViewer fluorViewer = getFlourDataViewer2();
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
	    split.setDividerLocation(((int)(VirtualFrapMainFrame.INIT_WINDOW_SIZE.getHeight()/2))-85);
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
	
//	private double[] getPreBleachAverageXYZ(Integer startingIndexForRecovery /*boolean bUserDefined*/) throws Exception{
////		int startingIndexForRecovery = -1;
////		if(bUserDefined){
////			startingIndexForRecovery = Integer.parseInt(getFrapStudy().getFrapModelParameters().startIndexForRecovery);
////		}else{
////			startingIndexForRecovery = FRAPDataAnalysis.getRecoveryIndex(getFrapStudy().getFrapData());
////		}
//		if(startingIndexForRecovery != null/* && getFrapStudy().getRoiExternalDataInfo() == null*//* || !bUserDefined*/){
//			return getFrapStudy().calculatePreBleachAverageXYZ(getFrapStudy().getFrapData(),startingIndexForRecovery);
//		}else{
//			VCDataManager testVCDataManager = getLocalWorkspace().getVCDataManager();
//			return testVCDataManager.getSimDataBlock(
//					getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();
//		}
//	}
	
	private boolean isSimLogFileExist(Simulation sim)
	{
		File logFile = new File(getLocalWorkspace().getDefaultSimDataDirectory(), sim.createSimulationID(sim.getKey())+"_0_.log");
		return logFile.exists();
	}
	
	public void spatialAnalysis(final DataSetControllerImpl.ProgressListener progressListener,final boolean bRefSimulation) throws Exception{
		
		final AsynchProgressPopup_orig pp =
			(progressListener != null?null:
			new AsynchProgressPopup_orig(
				FRAPStudyPanel.this,
				"Running FRAP Spatial Analysis",
				"Working...",true,true)
			);
		if(pp != null){pp.start();}
		final FRAPStudy fStudy = getFrapStudy();
		new Thread(new Runnable(){public void run(){
			try{
				if(getSavedFrapModelInfo()==null)//has not been saved yet
				{
					String saveMessage = "Current unsaved document must be saved before running analysis.";
					String choice = DialogUtils.showWarningDialog(FRAPStudyPanel.this, saveMessage,
							        new String[] {UserMessage.OPTION_SAVE_AS_NEW,UserMessage.OPTION_CANCEL}, UserMessage.OPTION_SAVE_AS_NEW);
					if(choice.equals(UserMessage.OPTION_CANCEL))
					{
						pp.stop();
						return;
					}
					else
					{
						pp.setMessage("Saving model information...");
						saveAs();//for new document the external data info. is created in saveProcedure.
						getFrapStudy().saveROIsAsExternalData(localWorkspace,
								getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
						getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
								getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery));
					}
				}
				final double SPATIAL_ANLYSIS_PROGRESS_FRACTION = .5;
				final double SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM = 1;
				
				DataSetControllerImpl.ProgressListener runspatialAnalysisProgressListener =
					new DataSetControllerImpl.ProgressListener(){
						public void updateProgress(double progress) {
							if(pp != null){
								int percentProgress = (int)(progress*100*SPATIAL_ANLYSIS_PROGRESS_FRACTION);
								if(!bRefSimulation && fStudy.getStoredRefData()!= null)
								{
									percentProgress = (int)(progress*100*SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM);
								}
								VirtualFrapMainFrame.updateProgress(percentProgress);
								pp.setProgress(percentProgress);
							}
							if(progressListener != null){
								if(!bRefSimulation && fStudy.getStoredRefData()!= null)
								{
									progressListener.updateProgress(progress*SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM);
								}
								else
								{
									progressListener.updateProgress(progress*SPATIAL_ANLYSIS_PROGRESS_FRACTION);
								}
							}
						}
						public void updateMessage(String message) {
							if(pp != null){
								VirtualFrapMainFrame.updateStatus(message);
								pp.setMessage(message);
							}
							if(progressListener != null){
								progressListener.updateMessage(message);
							}
						}
				};

				VCDataManager testVCDataManager = getLocalWorkspace().getVCDataManager();
				//the prebleachAverage
				final int startIndexForRecovery = new Integer(getFrapStudy().getFrapModelParameters().startIndexForRecovery).intValue();
				double[] prebleachAverage = FRAPStudy.calculatePreBleachAverageXYZ(getFrapStudy().getFrapData(), startIndexForRecovery);
				
				DataManager simulationDataManager = null;
				if(frapStudy.getBioModel() != null && frapStudy.getBioModel().getSimulations()!=null &&
						frapStudy.getBioModel().getSimulations().length > 0 )
				{
					FRAPStudy f=frapStudy;
					Simulation frapSimulation = frapStudy.getBioModel().getSimulations()[0];
					if(isSimLogFileExist(frapSimulation)) // use this condition to know whether the simulation has run or not.
					{
						simulationDataManager = getDataManager(frapSimulation);
						try{
							if(simulationDataManager.getDataSetTimes() == null)
							{
								simulationDataManager = null;
							}
						}catch(Exception e)
						{
							simulationDataManager = null;
						}
					}
				}
				
				
				FRAPStudy.SpatialAnalysisResults spatialAnalysisResults = null; 
				final double[] frapDataTimeStamps = getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps();
				
				spatialAnalysisResults =
					FRAPStudy.spatialAnalysis(
						simulationDataManager,
						startIndexForRecovery,
						frapDataTimeStamps[startIndexForRecovery],
						getFrapStudy().getFrapModelParameters().diffusionRate,
						getFrapStudy().getFrapModelParameters().mobileFraction,
						getFrapStudy().getFrapModelParameters().monitorBleachRate,
						/*frapSimulation.getMathDescription().getSubDomain(FRAPStudy.CYTOSOL_NAME),*/
						getFrapStudy().getFrapData(),
						prebleachAverage,
						runspatialAnalysisProgressListener);
			
				//Optimization
				DataSetControllerImpl.ProgressListener optimizationProgressListener =
					new DataSetControllerImpl.ProgressListener(){
						public void updateProgress(double progress) {
							if(pp != null){
								int percentProgress = (int)(50+progress*100*(1-SPATIAL_ANLYSIS_PROGRESS_FRACTION));
								if(frapOptData != null && !bRefSimulation)
								{
									percentProgress = (int)(50+progress*100*(1-SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM));
								}
								VirtualFrapMainFrame.updateProgress(percentProgress);
								pp.setProgress(percentProgress);
							}
							if(progressListener != null){
								if(frapOptData != null && !bRefSimulation)
								{
									progressListener.updateProgress(.5+progress*(1-SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM));
								}
								else
								{
									progressListener.updateProgress(.5+progress*(1-SPATIAL_ANLYSIS_PROGRESS_FRACTION));
								}
							}
						}
						public void updateMessage(String message) {
							if(pp != null){
								VirtualFrapMainFrame.updateStatus(message);
								pp.setMessage(message);
							}
							if(progressListener != null){
								progressListener.updateMessage(message);
							}
						}
				};
//				if(bRefSimulation || frapOptData == null)
//				{
//					frapOptData = new FRAPOptData(getFrapStudy(), FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), optimizationProgressListener, bRefSimulation);
//				}
//				if(progressListener != null){
//					progressListener.updateProgress(1.0);
//				}

				if(bRefSimulation)
				{
					frapOptData = new FRAPOptData(getFrapStudy(), FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), optimizationProgressListener);
				}
				else
				{
					if(frapOptData == null)
					{
						if(getFrapStudy().getStoredRefData() != null)
						{
							frapOptData = new FRAPOptData(getFrapStudy(), FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), getFrapStudy().getStoredRefData());
						}
						else
						{
							//reference data is null, it is not stored, we have to run ref simulation then
							frapOptData = new FRAPOptData(getFrapStudy(), FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), optimizationProgressListener);
						}
					}
				}
				
				if(progressListener != null){
					progressListener.updateProgress(1.0);
				}
				NewFRAPFromParameters newFRAPFromParameters =
					new NewFRAPFromParameters (){
						public void create(Parameter[] newParameters) {
							createNewFRAPFromParameters(newParameters);
						}
					};
					
				//Report initialization
				getResultsSummaryPanel().setData(newFRAPFromParameters,frapOptData,
						spatialAnalysisResults,frapDataTimeStamps,startIndexForRecovery,
						new Double(getFrapStudy().getFrapModelParameters().diffusionRate),(simulationDataManager==null)?false:true);
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){
					isSetTabIdxFromSpatialAnalysis = true;
					getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_REPORT));
					isSetTabIdxFromSpatialAnalysis = false;
				}});
				
				
				VirtualFrapMainFrame.updateProgress(0);
				VirtualFrapMainFrame.updateStatus("Finished Spatial analysis.");
			}catch(final Exception e){
				if(pp != null){pp.stop();}
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
				if(pp != null){pp.stop();}
			}
		}}).start();
	}

	private void createNewFRAPFromParameters(Parameter[] newParameters){
		String msg = "The CURRENT FRAP model will be using new parameters:\n"+
					 "Primary diffusion Rate="+newParameters[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX].getInitialGuess()+"\n"+
					 "Primary mobile Fraction="+newParameters[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX].getInitialGuess()+"\n"+	
					 "Monitor Bleach Rate="+newParameters[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess();
		if(newParameters.length == 5)
		{
			msg = msg + "\nSecondary diffusion Rate="+newParameters[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX].getInitialGuess()+"\n"+
						"Secondary mobile Fraction="+newParameters[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX].getInitialGuess();
			            
		}
		String result = DialogUtils.showWarningDialog(this,msg,new String[] {UserMessage.OPTION_OK,UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);

		if(result == null || !result.equals(UserMessage.OPTION_OK)){
			return;
		}
		
		try {
			String secRate = null;
			String secFraction = null;
			if(newParameters.length == 5)
			{
				secRate = newParameters[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX].getInitialGuess()+"";
				secFraction = newParameters[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX].getInitialGuess()+"";
			}
 			getFRAPParametersPanel().changeCoreFRAPModelParameters( 
					newParameters[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX].getInitialGuess()+"",
					newParameters[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX].getInitialGuess()+"",
					newParameters[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess()+"",
					secRate,
					secFraction
			);
			runSimulation(true);
		} catch (Exception e) {
			if(!(e instanceof UserCancelException)){
				e.printStackTrace();
				DialogUtils.showErrorDialog("Creating New FRAP failed.  "+e.getMessage());
			}
		}

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
				JDialog roiDialog = new JDialog((JFrame)BeanUtils.findTypeParentOfComponent(this, JFrame.class));
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
				roiDialog.setSize(400,200);
				ZEnforcer.showModalDialogOnTop(roiDialog, this);
				
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
	
}