package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import sun.net.ProgressListener;


import cbit.gui.DialogUtils;
import cbit.gui.graph.GraphPane;
import cbit.image.ImageException;
import cbit.sql.KeyValue;
import cbit.util.BeanUtils;
import cbit.util.Compare;
import cbit.util.FileUtils;
import cbit.util.NumberUtils;
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
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Equation;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.gui.ExpressionCanvas;
import cbit.vcell.microscopy.ExternalDataInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FrapDataAnalysisResults;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.ROI;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.server.VCDataIdentifier;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solvers.FVSolverStandalone;
//comments added Jan 2008, this is the bottom panel contains all other
//panels in a tabbedPane.
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
	private JPanel fitDataPanel = null;
	private JPanel modelPanel = null;
	private JPanel fitSpatialModelPanel = null;
	private JPanel reportPanel = null;
	private JPanel reportBasePanel = null;
	private JCheckBox showReportListCheckBox = null;
	private JScrollPane scrollReportPane = null;
	private MultisourcePlotPane[] spatialAnalysisList = null;
	private JTextPane statusTextPane = null;
	private JPanel statusTextScrollPanel = null; //Added Feb,2008
	private JSplitPane detailsPane = null;//Added ,2008
	private MultisourcePlotPane multisourcePlotPane = null;
	private JScrollPane fitRecoveryControlPane = null;
	private JButton updateFitRecoveryCurveButton = null;
	private JPanel jPanel = null;
	private JButton LoadButton = null;
	private GraphPane geometryGraphPane = null;
	private JPanel modelSpecPanel = null;
	private JLabel mobilePhase = null;
	private JLabel immobilePhase = null;
	private JLabel mobileFractionLabel = null;
	private JLabel immobileFractionLabel = null; 
	private JCheckBox slowFractionCheckBox = null;
	private JTextField slowDiffRateTextField = null;
	private JLabel slowDiffusionUnitsLabel = null;
	private JCheckBox bleachWhileMonitoringCheckBox = null;
	private JRadioButton spatial_twoAndHalfDimRadioButton = null;
	private JRadioButton spatial_threeDimRadioButton = null;
	private JRadioButton spatial_twoDimRadioButton = null;
	private JPanel geometryControlsPanel = null;
	
	private PDEDataViewer pdeDataViewer = null;
	private PDEDataViewer flourDataViewer = null;
	private JPanel fitCurveOptionPanel = null;
	private JPanel expressionPanel1 = null; //circular disk 
	private JPanel expressionPanel2 = null; //mobile fraction as an unknown parameter
	private JPanel expressionPanel3 = null; //half cell bleached
	private JRadioButton circularDiskRadioButton = null;
	private JRadioButton method2RadioButton = null;
	private JRadioButton method3RadioButton = null;
	private JTextField diffusionRateTextFieldNew = null;
	private JLabel diffusionUnitsLabel = null;
	private JLabel diffusionRateTitleLabel = null;
	private JTextField bleachWhileMonitoringTextField = null;
	private JLabel bleachWhileMonitoringUnitsLabel = null;
	
	enum DisplayChoice { PDE,EXTTIMEDATA};

	
	//Elements that change the Model
	public static class FrapChangeInfo{
		public final boolean bROIValuesChanged;
		public final boolean bROISizeChanged;
		public final boolean bDiffusionRateChanged;
		public final String diffusionRateString;
		public final boolean bBleachWhileMonitorChanged;
		public final String bleachWhileMonitorRateString;
		public FrapChangeInfo(boolean bROIValuesChanged,boolean bROISizeChanged,
				boolean bDiffusionRateChanged,String diffusionRateString,
				boolean bBleachWhileMonitorChanged,String bleachWhileMonitorRateString){
			this.bROIValuesChanged = bROIValuesChanged;
			this.bROISizeChanged = bROISizeChanged;
			this.bDiffusionRateChanged = bDiffusionRateChanged;
			this.diffusionRateString = diffusionRateString;
			this.bBleachWhileMonitorChanged = bBleachWhileMonitorChanged;
			this.bleachWhileMonitorRateString = bleachWhileMonitorRateString;
		}
		public boolean hasAnyChanges(){
			return bROIValuesChanged || bROISizeChanged || bDiffusionRateChanged || bBleachWhileMonitorChanged;
		}
		public boolean hasROIChanged(){
			return bROISizeChanged || bROIValuesChanged;
		}
		public String getChangeDescription(){
			return
			(bROIValuesChanged?"(Cell,Bleach or Backgroung ROI)":" ")+
			(bROISizeChanged?"(Data Dimension)":" ")+
			(bDiffusionRateChanged?"(Diffusion Rate)":" ")+
			(bBleachWhileMonitorChanged?"(BleachWhileMonitoring Rate)":" ");

		}
	};
	public static class SavedFrapModelInfo{
		public final KeyValue savedSimKeyValue;
		public final ROI lastCellROI;
		public final ROI lastBleachROI;
		public final ROI lastBackgroundROI;
		public final Double lastBaseDiffusionrate;
		public final Double lastBleachWhileMonitoringRate;
//		private FrapChangeInfo frapChangeInfo;
		public SavedFrapModelInfo(
			KeyValue savedSimKeyValue,
			ROI lastCellROI,
			ROI lastBleachROI,
			ROI lastBackgroundROI,
			Double lastBaseDiffusionrate,
			Double lastBleachWhileMonitoringRate){
			if(savedSimKeyValue == null){
				throw new IllegalArgumentException("SimKey cannot be null for a saved FrapModel.");
			}
			this.savedSimKeyValue = savedSimKeyValue;
			this.lastCellROI = lastCellROI;
			this.lastBleachROI = lastBleachROI;
			this.lastBackgroundROI = lastBackgroundROI;
			this.lastBaseDiffusionrate = lastBaseDiffusionrate;
			this.lastBleachWhileMonitoringRate = lastBleachWhileMonitoringRate;
		}

	};
	private FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfoNew = null;

	private static final String SAVE_FILE_OPTION = "Save";
	
	private ActionListener bleachMethodChangeActionListener =
		new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					fitBleaching();
				}catch(Exception e2){
					DialogUtils.showErrorDialog("Error calulating fit\n"+e2.getMessage());
				}
			}
		};

	private	DataSetControllerImpl.ProgressListener progressListenerNew =
		new DataSetControllerImpl.ProgressListener(){
			public void updateProgress(double progress) {
				VirtualFrapMainFrame.statusBar.showProgress((int)Math.round(progress*100));
			}
		};

	/**
	 * This method initializes 
	 * 
	 */
	public FRAPStudyPanel() {
		super();
		initialize();
	}
	
	public static SavedFrapModelInfo createSavedFrapModelInfo(FRAPStudy frapStudy) throws Exception{
		KeyValue savedSimKey = null;
		if(frapStudy.getBioModel() != null &&
			frapStudy.getBioModel().getSimulations() != null &&
			frapStudy.getBioModel().getSimulations().length > 0){
			savedSimKey = frapStudy.getBioModel().getSimulations()[0].getKey();
		}
		ROI savedCellROI = null;
		ROI savedBleachROI = null;
		ROI savedBackgroundROI = null;
		if(frapStudy.getFrapData() != null){
			savedCellROI = frapStudy.getFrapData().getRoi(RoiType.ROI_CELL);
			savedCellROI = (savedCellROI == null?null:new ROI(savedCellROI));
			savedBleachROI = frapStudy.getFrapData().getRoi(RoiType.ROI_BLEACHED);
			savedBleachROI = (savedBleachROI == null?null:new ROI(savedBleachROI));
			savedBackgroundROI = frapStudy.getFrapData().getRoi(RoiType.ROI_BACKGROUND);
			savedBackgroundROI = (savedBackgroundROI == null?null:new ROI(savedBackgroundROI));
		}
		Double savedDiffusionRate = null;
		Double savedBleachWhileMonitoringRate = null;
		if(frapStudy.getFrapDataAnalysisResults() != null){
			savedDiffusionRate = frapStudy.getFrapDataAnalysisResults().getRecoveryDiffusionRate();
			savedBleachWhileMonitoringRate = frapStudy.getFrapDataAnalysisResults().getBleachWhileMonitoringTau();
		}
		return new SavedFrapModelInfo(
				savedSimKey,
				savedCellROI,savedBleachROI,savedBackgroundROI,
				savedDiffusionRate,savedBleachWhileMonitoringRate
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
			jTabbedPane.addTab("Fit Recovery Curve", null, getFitDataPanel(), null);
			jTabbedPane.addTab("Generate Spatial Model", null, getModelPanel(), null);
			jTabbedPane.addTab("Fit Spatial Model", null, getFitSpatialModelPanel(), null);
			jTabbedPane.addTab("Report", null, getScrollReportPane(), null);
			jTabbedPane.setModel(
				new DefaultSingleSelectionModel(){
					@Override
					public void setSelectedIndex(int index) {
						try{
							if(updateTabbedView(index)){
								super.setSelectedIndex(index);
							}
						}catch(Exception e){
							DialogUtils.showWarningDialog(
								FRAPStudyPanel.this, "View unavailable beacuase:\n"+e.getMessage(),
								new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
						}
					}
				}
			);
			jTabbedPane.setSelectedIndex(FRAPStudyPanel.INDEX_TAB_IMAGES);
		}
		return jTabbedPane;
	}

	private boolean updateTabbedView(int tabIndex) throws Exception{
		if(tabIndex != FRAPStudyPanel.INDEX_TAB_IMAGES && (getFrapStudy() == null || getFrapStudy().getFrapData() == null)){
			throw new Exception("No document open.  Use 'File->Open' menu to open a new document");
		}
		try{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			switch (tabIndex){
				case FRAPStudyPanel.INDEX_TAB_IMAGES:
				break;
				case FRAPStudyPanel.INDEX_TAB_FITCURVE:
					FRAPStudyPanel.this.fitBleaching();
				break;
				case FRAPStudyPanel.INDEX_TAB_SPATIALMODEL:
					FRAPStudyPanel.this.fitBleaching();
					FRAPStudyPanel.this.refreshBiomodel();
				break;
				case FRAPStudyPanel.INDEX_TAB_FITSPATIALMODEL:
					int EXPECTED_SIM_FILE_COUNT = 9;//prebleach.fdat,postbleach.fdat,.vcg,.meshmetrics,.,mesh,.log,.fvinput,.functions,.zip
					FRAPStudyPanel.this.fitBleaching();
					FrapChangeInfo frapChangeInfo = FRAPStudyPanel.this.refreshBiomodel();
					boolean bSimNeedsRun = false;
					if(frapChangeInfo == null || frapChangeInfo.hasAnyChanges()){
						FRAPStudyPanel.this.clearFitSpatialModelPanel();
						bSimNeedsRun = true;//throw new Exception("Simulation needs to run beacuse model has changed.");
					}
					if(savedFrapModelInfoNew == null){
						bSimNeedsRun = true;
					}else{
						File[] simFiles = getSimulationFileNames(
							new File(getLocalWorkspace().getSimDataDirectory(),getLocalWorkspace().getOwner().getName()),
							savedFrapModelInfoNew.savedSimKeyValue);
						if(simFiles == null || simFiles.length != EXPECTED_SIM_FILE_COUNT){
							bSimNeedsRun = true;
						}
					}
					if(bSimNeedsRun){
						runSimulation();
						return false;
					}else{
						refreshPDEDisplay(DisplayChoice.PDE);
						refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);
					}
				break;
				case FRAPStudyPanel.INDEX_TAB_REPORT:
					getReportPanel().removeAll();
					spatialAnalysis();
				break;
				default:
				break;
			}
			return true;
		}finally{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getDefaultCursor());
		}
		
	}
	/**
	 * Changed in Feb, 2008. GridBagLayout to BorderLayout.
	 * Set splitPane and put MultisourcePlotPane on top and the scrollText, equation and radio button at bottom	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFitDataPanel() {
		if (fitDataPanel == null) {
			fitDataPanel = new JPanel();
			fitDataPanel.setLayout(new BorderLayout());
			JSplitPane fitRecoverySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,getPlotDetailsPane(),getFitRecoveryControlPanel());
			fitRecoverySplit.setDividerLocation((int)(VirtualFrapMainFrame.INIT_WINDOW_SIZE.width*.75));
			fitRecoverySplit.setDividerSize(3);
		    fitDataPanel.add(fitRecoverySplit,BorderLayout.CENTER);
		}
		return fitDataPanel;
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
			modelPanel.add(getModelSpecPanel(), gridBagConstraints4);
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
//		getScrollReportPane().setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
//		getJTabbedPane().setSelectedIndex(INDEX_TAB_REPORT);
	}
	
	/**
	 * This method initializes statusTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getStatusTextPane() {
		if (statusTextPane == null) {
			statusTextPane = new JTextPane();
			statusTextPane.setMinimumSize(new Dimension(6, 50));
			statusTextPane.setPreferredSize(new Dimension(6, 50));
			statusTextPane.setBackground(new Color(233,233,233));
			statusTextPane.setEditable(false);
		}
		return statusTextPane;
	}

	//Added in Feb, 2008. To make TextPane scrollable
	private JPanel getScrollStatusTextPanel()
	{
		if (statusTextScrollPanel == null) {
			statusTextScrollPanel = new JPanel();
			statusTextScrollPanel.setLayout(new BorderLayout());
			statusTextScrollPanel.add(new JLabel(" "), BorderLayout.NORTH);
			JScrollPane statusTextScrollPane = new JScrollPane();
			statusTextScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			statusTextScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			statusTextScrollPane.setMinimumSize(new Dimension(30, 75));
			statusTextScrollPane.setViewportView(getStatusTextPane());
			statusTextScrollPanel.add(statusTextScrollPane, BorderLayout.CENTER);
		}
		return statusTextScrollPanel;
	}
	
	//Added in Feb,2008. To put recovery controlPanel and the scrollTextPanel together.
	private JSplitPane getPlotDetailsPane()
	{
		if(detailsPane == null)
		{
			detailsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,getMultisourcePlotPane(),getScrollStatusTextPanel());
			detailsPane.setDividerLocation((int)(VirtualFrapMainFrame.INIT_WINDOW_SIZE.height*.6));
		}
		return detailsPane;
	}
	/**
	 * This method initializes multisourcePlotPane	
	 * 	
	 * @return cbit.plot.PlotPane	
	 */
	private MultisourcePlotPane getMultisourcePlotPane() {
		if (multisourcePlotPane == null) {
			multisourcePlotPane = new MultisourcePlotPane();
			multisourcePlotPane.setListVisible(false);
			multisourcePlotPane.add(getFitCurveOptionPanel(), BorderLayout.EAST);
		}
		return multisourcePlotPane;
	}

	/**
	 * Changed in Feb, 2008. Change GridBagLayout to Borderlayout,
	 * since we have only two stuff here.	
	 * @return javax.swing.JPanel	
	 */
	private JScrollPane getFitRecoveryControlPanel() {
		if (fitRecoveryControlPane == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(getCircularDiskRadioButton());
			bg.add(getGaussianSpotRadioButton());
			bg.add(getHalfCellRadioButton());
			
			JPanel panel = new JPanel(new GridBagLayout());
//			fitRecoveryControlPanel.add(getCircularDiskRadioButton(), gridBagConstraints1);
			panel.add(getExpressionPanel1(), gridBagConstraints1);
//			fitRecoveryControlPanel.add(getMethod2RadioButton(), gridBagConstraints3);
			panel.add(getExpressionPanel2(), gridBagConstraints2);
//			fitRecoveryControlPanel.add(getMethod3RadioButton(), gridBagConstraints5);
			panel.add(getExpressionPanel3(), gridBagConstraints3);
			
			fitRecoveryControlPane = new JScrollPane(panel);
			fitRecoveryControlPane.setAutoscrolls(true);
		}
		
		
		return fitRecoveryControlPane;
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

	private void displayFit(){
		FrapDataAnalysisResults fdar = null;
		try{
		if (frapStudy==null || frapStudy.getFrapDataAnalysisResults()==null){
			getMultisourcePlotPane().setDataSources(null);
		}else{
			fdar = frapStudy.getFrapDataAnalysisResults();
			
			double[] timeStamps = frapStudy.getFrapData().getImageDataset().getImageTimeStamps();
			double[] bleachRegionData = fdar.getBleachRegionData();
			int startIndexForRecovery = fdar.getStartingIndexForRecovery();
			Expression fittedCurve = fdar.getFitExpression();
			ReferenceData expRefData = new SimpleReferenceData(new String[] { "t", "fluor" }, new double[] { 1.0, 1.0 }, new double[][] { timeStamps, bleachRegionData });
			DataSource expDataSource = new DataSource(expRefData,"experiment");
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			try {
				fitOdeSolverResultSet.addFunctionColumn(new FunctionColumnDescription(fittedCurve,"recoveryFit",null,"recoveryFit",true));
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
			for (int i = startIndexForRecovery; i < timeStamps.length; i++) {
				fitOdeSolverResultSet.addRow(new double[] { timeStamps[i] });
			}
			//
			// extend if necessary to plot theoretical curve to 4*tau
			//
			double T = timeStamps[timeStamps.length-1];
			double deltaT = timeStamps[timeStamps.length-1]-timeStamps[timeStamps.length-2];
			while (T+deltaT < 6*fdar.getRecoveryTau()){
				fitOdeSolverResultSet.addRow(new double[] { T } );
				T += deltaT;
			}
			DataSource fitDataSource = new DataSource(fitOdeSolverResultSet, "fit");
			getMultisourcePlotPane().setDataSources(new DataSource[] {  expDataSource, fitDataSource } );
			getMultisourcePlotPane().selectAll();
						
			addStatus("estimated diffusion <"+NumberUtils.formatNumber(fdar.getRecoveryDiffusionRate(),3)+" [um2.s-1]> using D = w^2/(4*tau), with tau="+NumberUtils.formatNumber(fdar.getRecoveryTau(),3)+" [s], w <"+NumberUtils.formatNumber(fdar.getBleachWidth(),3)+" [um]> from bleach mask size");
		}
		}finally{
			updateFrapDataAnalysisGUI(fdar);
		}
	}
	
	private void addStatus(String text){
		getStatusTextPane().setText(getStatusTextPane().getText()+text+"\n");
	}
	private void setStatus(String text){
		getStatusTextPane().setText(text+"\n");
	}
	public void fitBleaching() throws Exception {
		getFRAPDataPanel().saveROI();
		getFrapStudy().setFrapDataAnalysisResults(null);
		if(getFrapStudy().getFrapData() != null){
			if(getFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED).isAllPixelsZero()){
				throw new Exception(
					OverlayEditorPanelJAI.INITIAL_BLEACH_AREA_TEXT+" ROI not defined.\n"+
					"Use ROI tools under '"+FRAPStudyPanel.FRAPDATAPANEL_TABNAME+"' tab to define.");
			}
	
			FrapDataAnalysisResults fdar =
				FRAPDataAnalysis.fitRecovery2(getFrapStudy().getFrapData(), getBleachType());
			getFrapStudy().setFrapDataAnalysisResults(fdar);
		}
		displayFit();
	}
	private int getBleachType()
	{
		if(getCircularDiskRadioButton().isSelected())
		{
			return FrapDataAnalysisResults.BleachType_CirularDisk;
		}
		else if(getGaussianSpotRadioButton().isSelected())
		{
			return FrapDataAnalysisResults.BleachType_GaussianSpot;
		}
		else if(getHalfCellRadioButton().isSelected())
		{
			return FrapDataAnalysisResults.BleachType_HalfCell;
		}
		
		throw new RuntimeException("Cannot determine Bleach Type");
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
		FrapChangeInfo frapChangeInfo = null;
		try{
			frapChangeInfo = refreshBiomodel();
		}catch(Exception e){
			//ignore
		}
		AsynchClientTask saveTask = createSaveTask(false, frapChangeInfo,true);
		ClientTaskDispatcher.dispatch(
			this, new Hashtable<String, Object>(),
			new AsynchClientTask[] {saveTask}, false);
	}
	
	public void saveAs() throws Exception{
		FrapChangeInfo frapChangeInfo = null;
		try{
			frapChangeInfo = refreshBiomodel();
		}catch(Exception e){
			//ignore
		}
		AsynchClientTask saveTask = createSaveTask(true, frapChangeInfo,true);
		ClientTaskDispatcher.dispatch(
			this, new Hashtable<String, Object>(),
			new AsynchClientTask[] {saveTask}, false);
	}
	private void saveAsInternal(FrapChangeInfo frapChangeInfo,String saveToFileName) throws Exception {

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
		saveProcedure(outputFile,frapChangeInfo,bSaveAs);
	}
	
	private void saveProcedure(File xmlFrapFileName,FrapChangeInfo frapChangeInfo,boolean bSaveAsNew) throws Exception{
//		boolean bForceImageDatasetSave = bSaveAs || (frapChangeInfo == null?false:frapChangeInfo.bImagaeDataChanged);
//		boolean bForceROISave = bSaveAs || (frapChangeInfo == null?false:frapChangeInfo.bROIChanged);
		VirtualFrapMainFrame.statusBar.showStatus("Saving file " + xmlFrapFileName.getAbsolutePath()+" ...");

		BioModel newBioModel = null;
		try{
			newBioModel = FRAPStudy.createNewBioModel(getFrapStudy(),
				getFrapStudy().getFrapDataAnalysisResults().getRecoveryDiffusionRate(),
				getFrapStudy().getFrapDataAnalysisResults().getBleachWhileMonitoringTau(),
				(bSaveAsNew || savedFrapModelInfoNew == null
					?LocalWorkspace.createNewKeyValue()
					:savedFrapModelInfoNew.savedSimKeyValue),
				getLocalWorkspace().getOwner());
			getFrapStudy().setBioModel(newBioModel);
		}catch(Exception e){
			getFrapStudy().setBioModel(null);
		}

		boolean originalSimFilesAreInvalid =
			(frapChangeInfo == null || frapChangeInfo.hasAnyChanges()) && !bSaveAsNew;
		originalSimFilesAreInvalid =  originalSimFilesAreInvalid || !areExternalDataOK();
		if(originalSimFilesAreInvalid){
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
			if(savedFrapModelInfoNew != null){
				File userDir = new File(localWorkspace.getSimDataDirectory(),localWorkspace.getOwner().getName());
				deleteSimulationFiles(userDir, savedFrapModelInfoNew.savedSimKeyValue);
			}
		}
		if(originalSimFilesAreInvalid || bSaveAsNew){
			//Replace with new external data IDs for use in 'run sim'
			getFrapStudy().setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
			getFrapStudy().setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
			
		}
		
		MicroscopyXmlproducer.writeXMLFile(getFrapStudy(), xmlFrapFileName, true,progressListenerNew,VirtualFrapMainFrame.SAVE_COMPRESSED);
		getFrapStudy().setXmlFilename(xmlFrapFileName.getAbsolutePath());
		savedFrapModelInfoNew = FRAPStudyPanel.createSavedFrapModelInfo(frapStudy);
		VirtualFrapMainFrame.statusBar.showStatus("File " + xmlFrapFileName.getAbsolutePath()+" has been saved.");
        VirtualFrapLoader.mf.setMainFrameTitle(xmlFrapFileName.getName());
        VirtualFrapMainFrame.statusBar.showProgress(0);
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
					//check if external log files for image dataset and ROI data exist.
					if(frapStudy.getFrapDataExternalDataInfo() !=null && frapStudy.getFrapDataExternalDataInfo().getFilename() !=null){
						File fileCheck = localWorkspace.getExternalDataFile(frapStudy.getFrapDataExternalDataInfo());
						if(!fileCheck.exists()){
							frapStudy.setFrapDataExternalDataInfo(null);
						}
					}
					if(frapStudy.getRoiExternalDataInfo() !=null && frapStudy.getRoiExternalDataInfo().getFilename() !=null){
						File fileCheck = localWorkspace.getExternalDataFile(frapStudy.getRoiExternalDataInfo());
						if(!fileCheck.exists()){
							frapStudy.setRoiExternalDataInfo(null);
						}
					}
					frapStudy.setXmlFilename(inFile.getAbsolutePath());
					savedFrapModelInfoNew = FRAPStudyPanel.createSavedFrapModelInfo(frapStudy);
					setFrapStudy(frapStudy,true);
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
//				if(FRAPStudyPanel.this.getFrapStudy().getFrapDataExternalDataInfo() != null && 
//					FRAPStudyPanel.this.getFrapStudy().getRoiExternalDataInfo() != null){	
//					if(FRAPStudyPanel.this.getSimulationLogFile(
//						FRAPStudyPanel.this.getFrapStudy().getBioModel().getSimulations()[0].getVersion().getVersionKey()).exists()){
//						DialogUtils.showInfoDialog(
//							"Couldn't find simulation log file. The simualtion has never run before or log file has been lost.");
//						}
//					}
				
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
		savedFrapModelInfoNew = null;//FRAPStudyPanel.createSavedFrapModelInfo(newFrapStudy);
		setFrapStudy(newFrapStudy, true);
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
	private AsynchClientTask createSaveTask(final boolean bSaveAs,final FrapChangeInfo frapChangeInfo,final boolean bRefreshUI){
		return new AsynchClientTask() {
			public String getTaskName() { return "Saving Current Workspace"; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				if(!bSaveAs){
					saveAsInternal(frapChangeInfo,getFrapStudy().getXmlFilename());
				}else{
					saveAsInternal(frapChangeInfo,null);
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
    		if(savedFrapModelInfoNew != null){
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
				saveTask = createSaveTask(result.equals(UserMessage.OPTION_SAVE_AS_NEW),frapChangeInfo,false);
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

	private boolean areSimulationFilesOK(){
		return true;
	}
	private boolean areExternalDataOK(){
		if(getFrapStudy().getFrapDataExternalDataInfo() == null){
			return false;
		}
		File[] frapDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(getLocalWorkspace(),
					getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier());
		for (int i = 0;frapDataExtDataFiles != null && i < frapDataExtDataFiles.length; i++) {
			if(!frapDataExtDataFiles[i].exists()){
				return false;
			}
		}
		if(getFrapStudy().getRoiExternalDataInfo() == null){
			return false;
		}
		File[] roiDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(getLocalWorkspace(),
					getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier());
		for (int i = 0;roiDataExtDataFiles != null && i < roiDataExtDataFiles.length; i++) {
			if(!roiDataExtDataFiles[i].exists()){
				return false;
			}
		}

		return true;
	}
	protected void runSimulation() throws Exception 
	{
		
		//save if the Model has changed
		AsynchClientTask saveTask = null;
		try{
			FrapChangeInfo frapChangeInfo = refreshBiomodel();
			boolean bNeedsExtData = !areExternalDataOK();
			if(frapChangeInfo.hasAnyChanges() || bNeedsExtData){
				boolean bSaveAsNew = false;
				if(frapChangeInfo.hasAnyChanges()){
		    		String saveMessage = null;
		    		if(savedFrapModelInfoNew != null){
		    			saveMessage = "Current document has changes that must be saved before running simulation.\n"+
		    				"Changes include "+frapChangeInfo.getChangeDescription();
		    		}else{
		    			saveMessage = "Current unsaved document must be saved before running simulation.";
		    		}

					String result = DialogUtils.showWarningDialog(
						this,
						saveMessage,
						(savedFrapModelInfoNew == null
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
				saveTask = createSaveTask(bSaveAsNew, frapChangeInfo,true);
			}
		}catch(Exception e){
			throw new Exception("Error running simulation:\n"+e.getMessage());
		}
		final AsynchClientTask finalSaveTask = saveTask;

		// show status
		VirtualFrapMainFrame.statusBar.showStatus("Simulation starting...");
		
		AsynchClientTask saveExternalDataTask = new AsynchClientTask() {
			public String getTaskName() { return "Saving External Data and ROI..."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				//Delete merged function file
				File mergedFuncFile = getMergedFunctionFile();
				if(mergedFuncFile != null && mergedFuncFile.isFile()){
					mergedFuncFile.delete();
				}
				//Save ext data if necessary
				if(!getFrapStudy().getExternalDataFile(getFrapStudy().getRoiExternalDataInfo()).exists()){
					getFrapStudy().saveROIsAsExternalData(localWorkspace,getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier());
				}
				if(!getFrapStudy().getExternalDataFile(getFrapStudy().getFrapDataExternalDataInfo()).exists()){
					getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier());
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
				File simulationDataDir = new File(
					getLocalWorkspace().getSimDataDirectory()+File.separator+
					getLocalWorkspace().getOwner().getName());
				FRAPStudy.runFVSolverStandalone(
					simulationDataDir,
					new StdoutSessionLog(getLocalWorkspace().getOwner().getName()),
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
				getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_FITSPATIALMODEL);
				VirtualFrapMainFrame.statusBar.showStatus("Simulation is done.");
				VirtualFrapMainFrame.statusBar.showProgress(0);
				//leave save status as it was. it should be disabled, since we run save() before starting the simulation.
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
				new MergedDataInfo(getLocalWorkspace().getOwner(),
					new VCDataIdentifier[]{
						getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
						getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier()});
			return
				new File(
					new File(getLocalWorkspace().getSimDataDirectory(),localWorkspace.getOwner().getName()),
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
			VCDataIdentifier vcDataId = new MergedDataInfo(getLocalWorkspace().getOwner(),dataIDs);
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
			if(savedFrapModelInfoNew == null){
				return null;
			}
			throw new Exception("Missing Frap Data Unexpected while refreshing BioModel");
		}
		ROI lastCellROI = null;
		ROI lastBleachROI = null;
		ROI lastBackgroundROI = null;
		Double lastBaseDiffusionrate = null;
		Double lastbleachWhileMonitoringRate = null;
		if(savedFrapModelInfoNew != null){
			lastCellROI = savedFrapModelInfoNew.lastCellROI;
			lastBleachROI = savedFrapModelInfoNew.lastBleachROI;
			lastBackgroundROI = savedFrapModelInfoNew.lastBackgroundROI;
			lastBaseDiffusionrate = savedFrapModelInfoNew.lastBaseDiffusionrate;
			lastbleachWhileMonitoringRate = savedFrapModelInfoNew.lastBleachWhileMonitoringRate;
		}
		String baseDiffusionRateString = null;
		if(getDiffusionRateTextFieldNew().getText() != null && getDiffusionRateTextFieldNew().getText().length()>0){
			baseDiffusionRateString = getDiffusionRateTextFieldNew().getText();
		}
		
		String bleachWhileMonitoringRateString = null;
		if(getBleachWhileMonitoringCheckBox().isSelected()){
			if(getBleachWhileMonitoringTextField().getText() != null && getBleachWhileMonitoringTextField().getText().length()>0){
				bleachWhileMonitoringRateString = getBleachWhileMonitoringTextField().getText();
			}
		}

		getFRAPDataPanel().saveROI();
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
		
		return new FrapChangeInfo(
				!bCellROISame || !bBleachROISame || !bBackgroundROISame,
				!bROISameSize,
				!isValueSameAsString(lastBaseDiffusionrate, baseDiffusionRateString),
				baseDiffusionRateString,
				!isValueSameAsString(lastbleachWhileMonitoringRate, bleachWhileMonitoringRateString),
				bleachWhileMonitoringRateString);
	}
	
	private boolean isValueSameAsString(Double doubleVal, String stringVal){
		try{
			Double stringToDouble = (stringVal == null?null:new Double(stringVal));
			return Compare.isEqualOrNull(doubleVal, stringToDouble);
		}catch(Exception e){
			return Compare.isEqualOrNull((doubleVal == null?null:doubleVal.toString()), stringVal);
		}
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
			try{
				baseDiffusionRate =
					(frapChangeInfo.diffusionRateString == null?null:new Double(frapChangeInfo.diffusionRateString));
			}catch(Exception e){
				throw new Exception("Error parsing value for Base Diffusion Rate. ("+frapChangeInfo.diffusionRateString+")");
			}
			if(baseDiffusionRate != null && baseDiffusionRate.doubleValue() < 0.0){
				throw new Exception("Base Diffusion Rate must be positive. ("+baseDiffusionRate.doubleValue()+")");			
			}

//			if(frapChangeInfo.hasROIChanged()){
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
//			}
			boolean bModelChanged = frapChangeInfo.hasAnyChanges() || getFrapStudy().getBioModel() == null;
			if(bModelChanged){
				BioModel bioModel = FRAPStudy.createNewBioModel(
					getFrapStudy(),
					baseDiffusionRate,
					bleachWhileMonitoringRate,
					(savedFrapModelInfoNew == null?null:savedFrapModelInfoNew.savedSimKeyValue),
					getLocalWorkspace().getOwner());
				getFrapStudy().setBioModel(bioModel);
				VirtualFrapMainFrame.statusBar.showStatus("New Frap Model created.");
			}
			if(bModelChanged || ((StructureMappingCartoon)(getGeometryGraphPane().getGraphModel())).getGeometryContext() == null){
				((StructureMappingCartoon)getGeometryGraphPane().getGraphModel()).
				setSimulationContext(getFrapStudy().getBioModel().getSimulationContexts()[0]);			
			}
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
	 * This method initializes modelSpecPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getModelSpecPanel() {
		if (modelSpecPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();//mobile fraction label
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();//mobile fraction percentage label
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 1;
			
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();//immobile fraction label
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.gridy = 1;
			
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();//immobile fration percentage label
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridy = 1;
			
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();//diffusion rate label
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.anchor = GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 2;
			
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();//diffusion rate text field
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = GridBagConstraints.EAST;
			gridBagConstraints6.gridx = 1;
			
			GridBagConstraints gridBagConstraints65 = new GridBagConstraints();//diffusion rate unit label
			gridBagConstraints65.gridx = 2;
			gridBagConstraints65.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints65.anchor = GridBagConstraints.EAST;
			gridBagConstraints65.gridy = 2;
						
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();//bleaching while monitoring check box
			gridBagConstraints7.weightx = 2.0;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.anchor = GridBagConstraints.EAST;
			gridBagConstraints7.gridy = 3;
			
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();//bleaching while monitoring rate text field
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 3;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.gridx = 1;
			
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();//bleaching while monitoring rate unit label
			gridBagConstraints9.gridx = 2;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.anchor = GridBagConstraints.EAST;
			gridBagConstraints9.gridy = 3;
			
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();//slower mobile fraction check box
			gridBagConstraints10.weightx = 2.0;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.EAST;
			gridBagConstraints10.gridy = 4;
			
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();//slower mobile fraction text field
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridy = 4;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.gridx = 1;
			
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();//slower mobile fraction unit label
			gridBagConstraints12.gridx = 2;
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.anchor = GridBagConstraints.EAST;
			gridBagConstraints12.gridy = 4;
			
			bleachWhileMonitoringUnitsLabel = new JLabel();
			bleachWhileMonitoringUnitsLabel.setText(" [s-1]   Please regenerate the spatial model once the bleaching rate has been changed.");
			
			diffusionRateTitleLabel = new JLabel();
			diffusionRateTitleLabel.setText("  Diffusion Rate");
			
			diffusionUnitsLabel = new JLabel();
			diffusionUnitsLabel.setText(" [um2.s-1]   Please regenerate the spatial model once the diffusion rate has been changed.");
			
			slowDiffusionUnitsLabel = new JLabel();
			slowDiffusionUnitsLabel.setText(" [um2.s-1]   Please regenerate the spatial model once the slower diffusion rate has been changed.");
			
			modelSpecPanel = new JPanel();
			modelSpecPanel.setLayout(new GridBagLayout());
			modelSpecPanel.add(getMobilePhase(), gridBagConstraints1);
			modelSpecPanel.add(getMobileFractionLabel(), gridBagConstraints2);
			
			modelSpecPanel.add(getImmobilePhase(), gridBagConstraints3);
			modelSpecPanel.add(getImmobileFractionLabel(), gridBagConstraints4);
			
			modelSpecPanel.add(diffusionRateTitleLabel, gridBagConstraints5);
			modelSpecPanel.add(getDiffusionRateTextFieldNew(), gridBagConstraints6);
			modelSpecPanel.add(diffusionUnitsLabel, gridBagConstraints65);
			
			modelSpecPanel.add(getBleachWhileMonitoringCheckBox(), gridBagConstraints7);
			modelSpecPanel.add(getBleachWhileMonitoringTextField(), gridBagConstraints8);
			modelSpecPanel.add(bleachWhileMonitoringUnitsLabel, gridBagConstraints9);
			
			modelSpecPanel.add(getSlowFractionCheckBox(), gridBagConstraints10);
			modelSpecPanel.add(getSlowDiffRateTextField(), gridBagConstraints11);
			modelSpecPanel.add(slowDiffusionUnitsLabel, gridBagConstraints12);
		}
		return modelSpecPanel;
	}

	/**
	 * This method initializes bleachWhileMonitoringCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getBleachWhileMonitoringCheckBox() {
		if (bleachWhileMonitoringCheckBox == null) {
			bleachWhileMonitoringCheckBox = new JCheckBox();
			bleachWhileMonitoringCheckBox.setText("Bleaching While Monitoring: bleaching rate");
			bleachWhileMonitoringCheckBox.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource().equals(bleachWhileMonitoringCheckBox)){
						if(bleachWhileMonitoringCheckBox.isSelected()){
							getBleachWhileMonitoringTextField().setEnabled(true);
						}
						else{
							getBleachWhileMonitoringTextField().setEnabled(false);
						}
					}
				}
			});
		}
		return bleachWhileMonitoringCheckBox;
	}

	/**
	 * This method initializes immoblePhaseCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JLabel getImmobilePhase() {
		if (immobilePhase == null) {
			immobilePhase = new JLabel("  Immobile Fraction");
		}
		return immobilePhase;
	}
	//the following four functions are added to show mobile and immobile fractions in spatial model panel 
	public JLabel getMobileFractionLabel() {
		if(mobileFractionLabel == null)
		{
			mobileFractionLabel = new JLabel("");
		}
		return mobileFractionLabel;
	}

//	public void setMobileFractionLabel(String fractionString) {
//		getMobileFractionLabel().setText(fractionString);
//	}

	public JLabel getImmobileFractionLabel() {
		if(immobileFractionLabel == null)
		{
			immobileFractionLabel = new JLabel("");
		}
		return immobileFractionLabel;
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

	/**
	 * This method initializes slowFractionCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getSlowFractionCheckBox() {
		if (slowFractionCheckBox == null) {
			slowFractionCheckBox = new JCheckBox();
			slowFractionCheckBox.setEnabled(false);
			slowFractionCheckBox.setText("Slower Mobile Fraction: slower diffucion rate");
		}
		return slowFractionCheckBox;
	}

	private JTextField getSlowDiffRateTextField() {
		if (slowDiffRateTextField == null) {
			slowDiffRateTextField = new JTextField();
			slowDiffRateTextField.setEnabled(false);
		}
		return slowDiffRateTextField;
	}
	
	/**
	 * This method initializes mobileFractionCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JLabel getMobilePhase() {
		if (mobilePhase == null) {
			mobilePhase = new JLabel("  Mobile Fraction");
		}
		return mobilePhase;
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
	
	/**
	 * This method initializes fitCurveOptionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFitCurveOptionPanel() {
		if (fitCurveOptionPanel == null) {
			fitCurveOptionPanel = new JPanel();
			fitCurveOptionPanel.setLayout(new GridBagLayout());
		}
		return fitCurveOptionPanel;
	}

	//Following three panels are used to display different curve fitting methods accroding to different bleaching types
	private JPanel getExpressionPanel1()
	{
		if(expressionPanel1 == null)
		{
			expressionPanel1 = new JPanel(new BorderLayout());
			JPanel canvasPanel = new JPanel(new BorderLayout());
			JPanel buttonPanel = new JPanel(new BorderLayout());
			ExpressionCanvas expCanvas = new ExpressionCanvas();
			expCanvas.setPreferredSize(new Dimension(100, 100));
			TitledBorder tb=new TitledBorder(new LineBorder(Color.darkGray),"Recovery Equation", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));

			//expression on canvas
			try{
				String[] prefixes = new String[] { "I(t) = ", "D = " };
				Expression[] expressions = new Expression[] { new Expression("If-(If-Io)*exp(-t/tau)"), new Expression("w^2/(4*tau)") };
				String[] suffixes = new String[] { "", "[um2.s-1]" };
				expCanvas.setExpressions(expressions,prefixes,suffixes);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
			}
			canvasPanel.add(expCanvas, BorderLayout.CENTER);
			canvasPanel.setBorder(tb);
			URL image = getClass().getResource("/images/circularDisk.gif");
			JLabel label = new JLabel(new ImageIcon(image));
			buttonPanel.add(getCircularDiskRadioButton(), BorderLayout.WEST);
			buttonPanel.add(label, BorderLayout.CENTER);
			expressionPanel1.add(new JLabel(" "), BorderLayout.WEST);
			expressionPanel1.add(new JLabel(" "), BorderLayout.SOUTH);
			expressionPanel1.add(buttonPanel,BorderLayout.NORTH);
			expressionPanel1.add(canvasPanel, BorderLayout.CENTER);
		}
		return expressionPanel1;
	}
	
	private JPanel getExpressionPanel2()
	{
		if(expressionPanel2 == null)
		{
			expressionPanel2 = new JPanel(new BorderLayout());
			JPanel canvasPanel = new JPanel(new BorderLayout());
			JPanel buttonPanel = new JPanel(new BorderLayout());
			ExpressionCanvas expCanvas = new ExpressionCanvas();
			expCanvas.setPreferredSize(new Dimension(100, 100));
			TitledBorder tb=new TitledBorder(new LineBorder(Color.darkGray),"Recovery Equation", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));

			//expression on canvas
			try{
				String[] prefixes = new String[] { "I(t) = ", "u(t)= ","D = " };
				Expression[] expressions = new Expression[] { new Expression("If*(1-fB)-(If*(1-fB)-Io)*(R*u+1-R)"), new Expression("(1+t/tau)^-1"), new Expression("w^2/(4*tau)") };
				String[] suffixes = new String[] { "", "", "[um2.s-1]" };
				expCanvas.setExpressions(expressions,prefixes,suffixes);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
			}
			canvasPanel.add(expCanvas, BorderLayout.CENTER);
			canvasPanel.setBorder(tb);
			URL image = getClass().getResource("/images/gaussianSpot.gif");
			JLabel label = new JLabel(new ImageIcon(image));
			buttonPanel.add(getGaussianSpotRadioButton(), BorderLayout.WEST);
			buttonPanel.add(label, BorderLayout.CENTER);
			expressionPanel2.add(new JLabel(" "), BorderLayout.WEST);
			expressionPanel2.add(new JLabel(" "), BorderLayout.SOUTH);
			expressionPanel2.add(buttonPanel, BorderLayout.NORTH);
			expressionPanel2.add(canvasPanel, BorderLayout.CENTER);
		}
		return expressionPanel2;
	}
	
	private JPanel getExpressionPanel3()
	{
		if(expressionPanel3 == null)
		{
			expressionPanel3 = new JPanel(new BorderLayout());
			JPanel canvasPanel = new JPanel(new BorderLayout());
			JPanel buttonPanel = new JPanel(new BorderLayout());
			ExpressionCanvas expCanvas = new ExpressionCanvas();
			expCanvas.setPreferredSize(new Dimension(100, 100));
			TitledBorder tb=new TitledBorder(new LineBorder(Color.darkGray),"Recovery Equation", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));

			//expression on canvas
			try{
				String[] prefixes = new String[] { "I(t) = ", "u(t)= ","D = " };
				Expression[] expressions = new Expression[] { new Expression("If*(1-fB)-(If*(1-fB)-Io)*(R*u+1-R)"), new Expression("exp(-t/tau)"), new Expression("tau*Pai^2/r^2") };
				String[] suffixes = new String[] { "", "", "[um2.s-1]" };
				expCanvas.setExpressions(expressions,prefixes,suffixes);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
			}
			canvasPanel.add(expCanvas, BorderLayout.CENTER);
			canvasPanel.setBorder(tb);
			URL image = getClass().getResource("/images/halfCell.gif");
			JLabel label = new JLabel(new ImageIcon(image));
			buttonPanel.add(getHalfCellRadioButton(), BorderLayout.WEST);
			buttonPanel.add(label, BorderLayout.CENTER);
			expressionPanel3.add(new JLabel(" "), BorderLayout.WEST);
			expressionPanel3.add(new JLabel(" "), BorderLayout.SOUTH);
			expressionPanel3.add(buttonPanel, BorderLayout.NORTH);
			expressionPanel3.add(canvasPanel, BorderLayout.CENTER);
		}
		return expressionPanel3;
	}
	
	
	/**
	 * This method initializes circularDiskRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getCircularDiskRadioButton() {
		if (circularDiskRadioButton == null) {
			circularDiskRadioButton = new JRadioButton();
			circularDiskRadioButton.setText(FrapDataAnalysisResults.BLEACH_TYPE_NAMES[FrapDataAnalysisResults.BleachType_CirularDisk]);
			circularDiskRadioButton.setSelected(true);
		}
		return circularDiskRadioButton;
	}

	private JRadioButton getGaussianSpotRadioButton() {
		if (method2RadioButton == null) {
			method2RadioButton = new JRadioButton();
			method2RadioButton.setText(FrapDataAnalysisResults.BLEACH_TYPE_NAMES[FrapDataAnalysisResults.BleachType_GaussianSpot]);
			method2RadioButton.setSelected(false);
		}
		return method2RadioButton;
	}
	
	private JRadioButton getHalfCellRadioButton() {
		if (method3RadioButton == null) {
			method3RadioButton = new JRadioButton();
			method3RadioButton.setText(FrapDataAnalysisResults.BLEACH_TYPE_NAMES[FrapDataAnalysisResults.BleachType_HalfCell]);
			method3RadioButton.setSelected(false);
		}
		return method3RadioButton;
	}
	
	/**
	 * This method initializes diffusionRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDiffusionRateTextFieldNew() {
		if (diffusionRateTextFieldNew == null) {
			diffusionRateTextFieldNew = new JTextField();
			diffusionRateTextFieldNew.setPreferredSize(new Dimension(120, 20));
		}
		return diffusionRateTextFieldNew;
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
	
	class CurveInfo {
		Double diffusionRate;
		RoiType roiType = null;
		
		CurveInfo(Double diffRate, RoiType roi){
			this.diffusionRate = diffRate;
			this.roiType = roi;
		}
		
		public boolean equals(Object obj){
			if (obj instanceof CurveInfo){
				CurveInfo ci = (CurveInfo)obj;
				if (ci.diffusionRate==null && diffusionRate==null){
					// ok
				}else if (ci.diffusionRate==null || diffusionRate==null){
					return false;
				}else if (!ci.diffusionRate.equals(diffusionRate)){
					return false;
				}
				if (ci.roiType!=roiType){
					return false;
				}
				return true;
			}
			return false;
		}
		
		public int hashCode(){
			return ((diffusionRate!=null)?(diffusionRate.hashCode()):(0)) + roiType.toString().hashCode();
		}
		public String getDisplayName(){
			if (diffusionRate==null){
				return "exp::"+roiType.toString();
			}else{
				return "sim_D_"+diffusionRate+"::"+roiType.toString();
			}
		}
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
		if(getFrapStudy().getFrapDataAnalysisResults() == null)
		{
			throw new Exception("Fitting recovery curve is required before running spatial analysis. Please go to \'Fit Recovery Curve\' tab to run curve fitting.");
		}
		
       	VirtualFrapMainFrame.statusBar.showStatus("Running spatial analysis ...");
       	// prepare hashtable for tasks
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
        
		// create tasks
		AsynchClientTask spatialAnalysisTask = new AsynchClientTask() {
			public String getTaskName() { return "Running spatial analysis."; }
			public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING; }
			public void run(java.util.Hashtable hash) throws Exception{
				Simulation sim = null;
				sim = frapStudy.getBioModel().getSimulations()[0];
				//
				// get data manager for simulation results
				//
				DataManager dataManager = getDataManager(sim);
				
				getFRAPDataPanel().saveROI();

				//
				// get List of variable names
				//
				ArrayList<Double> diffList = new ArrayList<Double>();
				ArrayList<String> pdeVarNameList = new ArrayList<String>();
				SubDomain subDomain = sim.getMathDescription().getSubDomain("cytosol");
				Enumeration<Equation> equEnum = subDomain.getEquations();
				while (equEnum.hasMoreElements()){
					Equation equ = equEnum.nextElement();
					if (equ instanceof PdeEquation){
						PdeEquation pde = (PdeEquation)equ;
						pdeVarNameList.add(pde.getVariable().getName());
						try {
							diffList.add(pde.getDiffusionExpression().evaluateConstant());
						} catch (DivideByZeroException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExpressionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				String[] varNames = pdeVarNameList.toArray(new String[pdeVarNameList.size()]);
				Double[] diffusionRates = diffList.toArray(new Double[diffList.size()]);
				
				//
				// list of rois to process
				//
				RoiType[] roiTypes = new RoiType[] {
						RoiType.ROI_BLEACHED,	
						RoiType.ROI_BLEACHED_RING1,	
						RoiType.ROI_BLEACHED_RING2,	
						RoiType.ROI_BLEACHED_RING3,	
						RoiType.ROI_BLEACHED_RING4,	
						RoiType.ROI_BLEACHED_RING5,
						RoiType.ROI_BLEACHED_RING6,
						RoiType.ROI_BLEACHED_RING7,
						RoiType.ROI_BLEACHED_RING8
				};
				
				//
				// get timing to compare experimental data with simulated results.
				//
				int startingIndexOfRecovery = getFrapStudy().getFrapDataAnalysisResults().getStartingIndexForRecovery();
				double[] timeStamps = getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps();
				double[] simTimes = dataManager.getDataSetTimes();
				double[] shiftedSimTimes = simTimes.clone();
				for (int j = 0; j < simTimes.length; j++) {
					shiftedSimTimes[j] = simTimes[j] + timeStamps[startingIndexOfRecovery];
				}
				
				//
				// for each simulation time step, get the data under each ROI <indexed by ROI, then diffusion
				//
				
				// preallocate arrays for all data first
				Hashtable<CurveInfo, double[]> curveHash = new Hashtable<CurveInfo, double[]>();
				ArrayList<int[]> nonZeroIndicesForROI = new ArrayList<int[]>();
				for (int i = 0; i < roiTypes.length; i++) {
					for (int j = 0; j < diffusionRates.length; j++) {
						curveHash.put(new CurveInfo(diffusionRates[j],roiTypes[i]), new double[simTimes.length]);
					}
					ROI roi_2D = getFrapStudy().getFrapData().getRoi(roiTypes[i]);
					nonZeroIndicesForROI.add(roi_2D.getRoiImages()[0].getNonzeroIndices());
				}
				//
				// collect data for experiment (over all ROIs)
				//
				for (int i = 0; i < roiTypes.length; i++) {
					ROI roi_2D = getFrapStudy().getFrapData().getRoi(roiTypes[i]);
					int[] roiIndices = roi_2D.getRoiImages()[0].getNonzeroIndices();
					double[] averageFluor = FRAPDataAnalysis.getAverageROIIntensity(frapStudy.getFrapData(), roiTypes[i]);
					double weight = 1.0/averageFluor[0];
					for (int j = 0; j < averageFluor.length; j++) {
						averageFluor[j] = averageFluor[j]*weight;
					}
					curveHash.put(new CurveInfo(null,roiTypes[i]), averageFluor);
				}
				
				//
				// get Simulation Data
				//
				// we want to update the loading progress every 2 seconds.
				long start = System.currentTimeMillis();
				long end;
				int totalLen = simTimes.length*varNames.length*roiTypes.length;
				VirtualFrapMainFrame.statusBar.showProgress(1);
				for (int i = 0; i < simTimes.length; i++) {
					for (int j = 0; j < varNames.length; j++) {
						SimDataBlock simDataBlock = dataManager.getSimDataBlock(varNames[j], simTimes[i]);
						double[] data = simDataBlock.getData();
						for (int k = 0; k < roiTypes.length; k++) {
							int[] roiIndices = nonZeroIndicesForROI.get(k);
							double accum = 0.0;
							for (int index = 0; index < roiIndices.length; index++) {
								accum += data[roiIndices[index]];
							}
							double[] values = curveHash.get(new CurveInfo(diffusionRates[j],roiTypes[k]));
							values[i] = accum/roiIndices.length;
							
							// calculate the progress after 2 seconds
							end = System.currentTimeMillis();
							if((end - start) >= 2000)
							{
								double currentLen = i*varNames.length*roiTypes.length + j*roiTypes.length + k;
								VirtualFrapMainFrame.statusBar.showProgress((int)Math.round((currentLen/totalLen) * 100));
								start = end; 
							}
						}
					}
				}
				
				//
				// display diffusion-centric plots
				//
				spatialAnalysisList = new MultisourcePlotPane[diffusionRates.length];
				for (int i = 0; i < diffusionRates.length; i++) {
					String title = "Experimental vs. Diffusion Rate "+diffusionRates[i];
					String[] expRefDataLabels = new String[roiTypes.length+1];
					double[] expRefDataWeights = new double[roiTypes.length+1];
					double[][] expRefDataColumns = new double[roiTypes.length+1][];
					expRefDataLabels[0] = "t";
					expRefDataWeights[0] = 1.0;
					expRefDataColumns[0] = timeStamps;
					for (int j = 0; j < roiTypes.length; j++) {
						expRefDataLabels[j+1] = "exp::"+roiTypes[j].toString();
						expRefDataWeights[j+1] = 1.0;
						expRefDataColumns[j+1] = curveHash.get(new CurveInfo(null,roiTypes[j])); // get experimental data for this ROI
					}
					ReferenceData expRefData = new SimpleReferenceData(expRefDataLabels, expRefDataWeights, expRefDataColumns);
					DataSource expDataSource = new DataSource(expRefData,"experiment");
					ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
					fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
					for (int j = 0; j < varNames.length; j++) {
						String name = "sim D="+diffusionRates[j]+"::"+roiTypes[j].toString();
						fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
					}
					//
					// populate time
					//
					for (int j = 0; j < simTimes.length; j++) {
						double[] row = new double[varNames.length+1];
						row[0] = shiftedSimTimes[j];
						fitOdeSolverResultSet.addRow(row);
					}
					//
					// populate values
					//
					for (int j = 0; j < roiTypes.length; j++) {
						double[] values = curveHash.get(new CurveInfo(diffusionRates[i],roiTypes[j])); // get simulated data for this ROI
						for (int k = 0; k < values.length; k++) {
							fitOdeSolverResultSet.setValue(k, j+1, values[k]);
						}
					}

					DataSource fitDataSource = new DataSource(fitOdeSolverResultSet, "fit");
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

	/**
	 * This method initializes bleachWhileMonitoringTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBleachWhileMonitoringTextField() {
		if (bleachWhileMonitoringTextField == null) {
			bleachWhileMonitoringTextField = new JTextField();
			bleachWhileMonitoringTextField.setEnabled(false);
		}
		return bleachWhileMonitoringTextField;
	}

	//Added Feb 2008. To update UI when new file is loaded.
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("frapDataAnalysisResults"))
		{
			setStatus(""); //clear the text area
//			getDiffusionRateTextField().setText("");
			if(evt.getSource() != null && evt.getSource() instanceof FRAPStudy)
			{
				if(((FRAPStudy)evt.getSource()).getFrapDataAnalysisResults() != null)
				{
					displayFit();
				}
			}
		}else if(evt.getSource() == getFRAPDataPanel().getOverlayEditorPanelJAI() && 
				evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
			try {
				crop((Rectangle) evt.getNewValue());
			} catch (Exception e) {
				PopupGenerator.showErrorDialog("Error Cropping:\n"+e.getMessage());
			}
		}
	}

	private void updateFrapDataAnalysisGUI(FrapDataAnalysisResults frapDataAnalysisResults){
		//
		//FrapDataAnalysis
		//
		getCircularDiskRadioButton().removeActionListener(bleachMethodChangeActionListener);
		getGaussianSpotRadioButton().removeActionListener(bleachMethodChangeActionListener);
		getHalfCellRadioButton().removeActionListener(bleachMethodChangeActionListener);
		try{
			final String DEFAULT_DIFFUSION_STRING = "";
			if(frapDataAnalysisResults != null){
				Double modelDiffusionRate = frapDataAnalysisResults.getRecoveryDiffusionRate();
				Double bleachWhileMonitorTau = frapDataAnalysisResults.getBleachWhileMonitoringTau();
				Double mobileFraction = frapDataAnalysisResults.getMobilefraction();
				getDiffusionRateTextFieldNew().setText((modelDiffusionRate == null?DEFAULT_DIFFUSION_STRING:modelDiffusionRate+""));
				getBleachWhileMonitoringCheckBox().setSelected(bleachWhileMonitorTau != null);
				getBleachWhileMonitoringTextField().setEnabled(bleachWhileMonitorTau != null);
				getBleachWhileMonitoringTextField().setText((bleachWhileMonitorTau != null?bleachWhileMonitorTau+"":""));
				getMobileFractionLabel().setText((mobileFraction!=null?NumberUtils.formatNumber(mobileFraction*100,4)+"%":""));
				getImmobileFractionLabel().setText((mobileFraction!=null?NumberUtils.formatNumber((1.0-mobileFraction)*100,4)+"%":""));			
				if(frapDataAnalysisResults.getBleachType() == FrapDataAnalysisResults.BleachType_CirularDisk){
					getCircularDiskRadioButton().setSelected(true);
				}else if(frapDataAnalysisResults.getBleachType() == FrapDataAnalysisResults.BleachType_GaussianSpot){
					getGaussianSpotRadioButton().setSelected(true);
				}else if(frapDataAnalysisResults.getBleachType() == FrapDataAnalysisResults.BleachType_HalfCell){
					getHalfCellRadioButton().setSelected(true);
				}
			}else{
				getMultisourcePlotPane().clearPane();
				getDiffusionRateTextFieldNew().setText(DEFAULT_DIFFUSION_STRING);
				getBleachWhileMonitoringCheckBox().setSelected(false);
				getBleachWhileMonitoringTextField().setText("");
				getBleachWhileMonitoringTextField().setEnabled(false);
				getMobileFractionLabel().setText("");
				getImmobileFractionLabel().setText("");
				getCircularDiskRadioButton().setSelected(true);
			}
		}finally{
			getCircularDiskRadioButton().addActionListener(bleachMethodChangeActionListener);
			getGaussianSpotRadioButton().addActionListener(bleachMethodChangeActionListener);
			getHalfCellRadioButton().addActionListener(bleachMethodChangeActionListener);
		}
	}
	private void refreshUI()
	{
		VirtualFrapMainFrame.enableSave(true);

		updateFrapDataAnalysisGUI(getFrapStudy().getFrapDataAnalysisResults());
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