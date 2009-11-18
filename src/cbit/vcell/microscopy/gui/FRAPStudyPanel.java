package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
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
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.ISize;
import org.vcell.util.PropertyLoader;
import org.vcell.util.Range;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCDataIdentifier;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.image.ImageException;
import cbit.image.ImagePaneModel;
import cbit.rmi.event.ExportEvent;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.data.NewClientPDEDataContext;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.SimulationModelInfo;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.MovieSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.microscopy.ExternalDataInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.gui.choosemodelwizard.ChooseModel_ModelTypesDescriptor;
import cbit.vcell.microscopy.gui.choosemodelwizard.ChooseModel_RoiForErrorDescriptor;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_BackgroundROIDescriptor;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_BleachedROIDescriptor;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_CellROIDescriptor;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_CropDescriptor;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_Panel;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_SummaryDescriptor;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_CompareResultsDescriptor;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_OneDiffComponentDescriptor;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_ReacBindingDescriptor;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_TwoDiffComponentDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_FileTypeDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_FileTypePanel;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_MultiFileDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SingleFileDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryDescriptor;
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
import cbit.vcell.solver.SimulationSymbolTable;

public class FRAPStudyPanel extends JPanel implements PropertyChangeListener{
	
	public static final String FRAPSTUDYPANEL_TABNAME_IMAGES = "Images";
	public static final String FRAPSTUDYPANEL_TABNAME_IniParameters = "Initial Parameters";
	public static final String FRAPSTUDYPANEL_TABNAME_AdjParameters = "Adjust Parameters";
	public static final String FRAPSTUDYPANEL_TABNAME_2DResults = "2D Results";
	public static final String MODEL_TYPE_PURE_DIFFUSION = "Pure_Diffusion";
	public static final String MODEL_TYPE_REACTION_DIFFUSION = "Reaction_Diffusion";
	public static final String VFRAP_PREFIX_EXP = "Exp";
	public static final String VFRAP_PREFIX_MASK = "Mask";
	public static final String VFRAP_PREFIX_SIM = "Sim";
	public static final String[] VFRAP_DS_PREFIX = {VFRAP_PREFIX_EXP, VFRAP_PREFIX_MASK, VFRAP_PREFIX_SIM};
	
	//properties
	public static final String NEW_FRAPSTUDY_KEY = "NEW_FRAPSTUDY";
	public static final String SAVE_FILE_NAME_KEY = "SAVE_FILE_NAME";
	public static final String SIMULATION_KEY = "SIMULATION";
	public static final String LOAD_DATA_WIZARD_KEY = "LOAD_DATA_WIZARD";
	public static final String EST_PARAM_WIZARD_KEY = "EST_PARAM_WIZARD";
	public static final String FRAPSTUDY_KEY = "FRAPSTUDY";
	public static final String FRAPMODELS_KEY = "FRAPMODELS";
	public static final String BEST_MODEL_KEY = "BEST_MODEL";
	public static final String NEW_DOC_NAME_KEY = "NEW_DOC_NAME";
	
	private Expression Norm_Exp_Fluor = null;
	private Expression Norm_Sim = null;
	
	private static String Norm_Exp_Fluor_Str = "(("+VFRAP_PREFIX_MASK+".cell_mask*((max(("+VFRAP_PREFIX_EXP+".fluor-"+VFRAP_PREFIX_EXP+".bg_average),0)+1)/"+VFRAP_PREFIX_MASK+".prebleach_avg))*("+VFRAP_PREFIX_MASK+".cell_mask > 0))";
	private static String Norm_Sim_One_Diff_Str =VFRAP_PREFIX_SIM+"."+FRAPStudy.SPECIES_NAME_PREFIX_MOBILE+" + "+VFRAP_PREFIX_SIM+"."+FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE;
	private static String Norm_Sim_Two_Diff_Str =VFRAP_PREFIX_SIM+"."+FRAPStudy.SPECIES_NAME_PREFIX_MOBILE+" + "+VFRAP_PREFIX_SIM+"."+FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE+" + "+VFRAP_PREFIX_SIM+"."+FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE;
	
	public static final LineBorder TAB_LINE_BORDER = new LineBorder(new Color(153, 186,243), 3);
	
	private static final String REACTION_RATE_PREFIX = "J_";
	private static final String NORM_FLUOR_VAR = "Exp_norm_fluor";
	private static final String NORM_SIM_VAR = "Sim_norm_fluor";
		
	private FRAPWorkspace frapWorkspace = null;
	private FRAPDataPanel frapDataPanel = null;
	private LocalWorkspace localWorkspace = null;
	private JPanel leftPanel = null;
	private AnalysisProcedurePanel analysisProcedurePanel = null;
	private ResultDisplayPanel analysisRestultsPanel = null;
	private FRAPParametersPanel frapParametersPanel = null;
	
	private JButton showMovieButton = null;
	private JPanel simResultsViewPanel = null;
	private PDEDataViewer pdeDataViewer = null;
	private PDEDataViewer flourDataViewer = null;
	private JPanel fitSpatialModelPanel = null;
	private FRAPSimDataViewerPanel frapSimDataViewerPanel = null;
	//to store movie file info. movie file will be refreshed after each simulation run.
	private String movieURLString = null;
	private String movieFileString = null;
	
	private boolean isSetTabIdxFromSpatialAnalysis = false;
	
	//wizards created for new VFRAP version
	private Wizard loadFRAPDataWizard = null;
	private Wizard defineROIWizard = null;
	private Wizard modelTypeWizard = null;
	private Wizard estimateParamWizard = null;
	//some wizard panels that need to be declare here.
	private DefineROI_Panel imgPanel = null;
	private EstParams_OneDiffComponentDescriptor diffOneDescriptor = null;
	private EstParams_TwoDiffComponentDescriptor diffTwoDescriptor = null;
	private EstParams_ReacBindingDescriptor bindingDescriptor = null;
	private EstParams_CompareResultsDescriptor compareResultsDescriptor = null;
	//show result dialog
	JDialog result2DDialog = null;
	
	public class WorkFlowButtonHandler implements ActionListener
	{
	 	public void actionPerformed(ActionEvent e) 
	    {
	  	   	if(e.getSource() instanceof JButton)
		    {
	  	   		String commandStr = ((JButton)e.getSource()).getActionCommand();
	  	   		if(commandStr.equals(VirtualFrapMainFrame.LOAD_IMAGE_COMMAND))
	  	   		{
	  	   			final Wizard loadWizard = getLoadFRAPDataWizard();
	  	   			if(loadWizard != null)
	  	   			{
	  	   				ArrayList<AsynchClientTask> totalTasks = new ArrayList<AsynchClientTask>();
	  	   				//check if save is needed before loading data
	  	   				if(getFrapWorkspace().getFrapStudy().isSaveNeeded())
	  	   				{
	  	   					String choice = DialogUtils.showWarningDialog(FRAPStudyPanel.this, "There are unsaved changes. Save current document before loading new data?", new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
	  	   					if(choice.equals(UserMessage.OPTION_OK))
	  	   					{
	  	   						AsynchClientTask[] saveTasks = save();
	  	   						for(int i=0; i<saveTasks.length; i++)
	  	   						{
	  	   							totalTasks.add(saveTasks[i]);
	  	   						}
	  	   					}
	  	   				}
	  	   				
		  	   			AsynchClientTask showLoadWizardTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		  	    		{
		  	    			public void run(Hashtable<String, Object> hashTable) throws Exception
		  	    			{
		  	    				loadWizard.showModalDialog(new Dimension(460,300));
		  	    			}
		  	    		};
		  	    		
		  	    		AsynchClientTask afterCloseLoadWizardTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		  	    		{
		  	    			public void run(Hashtable<String, Object> hashTable) throws Exception
		  	    			{
		  	    				if(loadWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
			  	   				{
			  	   					getAnalysisProcedurePanel().setWorkFlowStage(AnalysisProcedurePanel.STAGE_DEFINE_ROIS);
			  	   					//set need save flag
			  	   					getFrapWorkspace().getFrapStudy().setSaveNeeded(true);
			  	   				}
		  	    			}
		  	    		};
		  	    		
		  	    		totalTasks.add(showLoadWizardTask);
		  	    		totalTasks.add(afterCloseLoadWizardTask);
		  	    		//dispatch
		  	    		ClientTaskDispatcher.dispatch(FRAPStudyPanel.this, new Hashtable<String, Object>(), totalTasks.toArray(new AsynchClientTask[totalTasks.size()]), false);
	  	   			}
	  	   		}
	  	   		else if(commandStr.equals(VirtualFrapMainFrame.DEFINE_ROI_COMMAND))
	  	   		{
	  	   			Wizard roiWizard = getDefineROIWizard();
	  	   			if(roiWizard != null)
	  	   			{
	  	   				//get old rois, image size and start index before showing wizard
		  	   			FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
		  	   			//rois
		  	   			ROI tempROI = null;
		  	   			ROI lastCellROI = null;
		  	   			ROI lastBleachROI = null;
		  	   			ROI lastBackgroundROI = null; 
		  	   			try {
			  	   			tempROI = fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
				  	   		lastCellROI = tempROI == null? null: new ROI(tempROI);
				  	   		tempROI = fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
				  			lastBleachROI = tempROI == null? null: new ROI(tempROI);
				  			tempROI = fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
				  			lastBackgroundROI = tempROI == null? null: new ROI(tempROI);
						} catch (ImageException e1) {
							e1.printStackTrace();
							DialogUtils.showErrorDialog(FRAPStudyPanel.this, "Having problem cloning ROIs, wizard has to be cancelled.");
						}

			  			//image size
			  			ISize lastImgISize = (lastCellROI == null? null:lastCellROI.getISize());
			  			//start index for recovery
	  	   				Integer oldStartIndexForRecovery = fStudy.getStartingIndexForRecovery();
	  	   				//show wizard
	  	   			    roiWizard.showModalDialog(new Dimension(640,670));
	  	   			    
		  	   			if(roiWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
	  	   				{
		  	   				FRAPStudy newFrapStudy = getFrapWorkspace().getFrapStudy();
		  	   				//check if one of images/rois/starting index for recovery is chaged. if so, rerun ref simulation by setting frapoptdata and storedrefdata to null.
	  	   					if(!Compare.isEqualOrNull(lastCellROI,newFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name())) ||
	  	   					   !Compare.isEqualOrNull(lastBleachROI,newFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name())) ||
	  	   					   !Compare.isEqualOrNull(lastBackgroundROI,newFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name())) ||
	  	   					   !Compare.isEqualOrNull(lastImgISize,newFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getISize()) ||
	  	   					   !Compare.isEqualOrNull(oldStartIndexForRecovery, newFrapStudy.getStartingIndexForRecovery()))
	  	   					{
	  	   						//set need save flag
	  	   						getFrapWorkspace().getFrapStudy().setSaveNeeded(true);
	  	   						//reset work flow stage if the last step has changed
	  	   						getAnalysisProcedurePanel().setWorkFlowStage(AnalysisProcedurePanel.STAGE_MODEL_TYPE);
	  	   						//reset data for reference simulation
	  	   						getFrapWorkspace().getFrapStudy().setRoiExternalDataInfo(null);
	  	   						getFrapWorkspace().getFrapStudy().setFrapOptData(null);
	  	   						getFrapWorkspace().getFrapStudy().setStoredRefData(null);
	  	   						//reset best model and updateUI
	  	   						getFrapWorkspace().getFrapStudy().setBestModelIndex(null);
	  	   						updateAnalysisResult(getFrapWorkspace().getFrapStudy());
	  	   					}
	  	   				}
	  	   			}
	  	   		}
	  	   		else if(commandStr.equals(VirtualFrapMainFrame.CHOOSE_MODEL_COMMAND))
	  	   		{
	  	   			Wizard modelTypeWizard = getChooseModelTypeWizard();
	  	   			FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
	  	   			if(modelTypeWizard != null)
	  	   			{
	  	   				//get old best model before the wizard runs. 
	  	   				Integer oldBestModelIndex = null;
	  	   				if(fStudy.getBestModelIndex() != null)
	  	   				{
	  	   					oldBestModelIndex = new Integer(fStudy.getBestModelIndex().intValue());
	  	   				}
	  	   				//get old selected models 
	  	   				int[] oldSelectedModelsArr = new int[fStudy.getSelectedModels().size()];
	  	   				for(int i=0; i<fStudy.getSelectedModels().size(); i++)
	  	   				{
	  	   					oldSelectedModelsArr[i]=fStudy.getSelectedModels().get(i).intValue();
	  	   				}
	  	   				//get old selected ROIs
	  	   				boolean[] oldSelectedROIs = null;
	  	   				if(fStudy.getSelectedROIsForErrorCalculation()!= null)
	  	   				{
	  	   					oldSelectedROIs = new boolean[fStudy.getSelectedROIsForErrorCalculation().length];
		  	   				for(int i=0; i<fStudy.getSelectedROIsForErrorCalculation().length; i++)
		  	   				{
		  	   					oldSelectedROIs[i] = fStudy.getSelectedROIsForErrorCalculation()[i];
		  	   				}
	  	   				}
	  	   				//show dialog
	  	   				modelTypeWizard.showModalDialog(new Dimension(550,420));
	  	   				
		  	   			if(modelTypeWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
	  	   				{
	  	   					//check if selected models have changed
	  	   					boolean modelsChanged = false;
	  	   					ArrayList<Integer> newSelectedModels = fStudy.getSelectedModels();
	  	   					if(oldSelectedModelsArr.length != newSelectedModels.size())
	  	   					{
	  	   						modelsChanged = true;
	  	   					}
	  	   					else
	  	   					{
	  	   						for(int i=0; i<oldSelectedModelsArr.length; i++)
	  	   						{
	  	   							boolean found = false;
	  	   							for(int j=0; j<newSelectedModels.size(); j++)
	  	   							{
	  	   								if(oldSelectedModelsArr[i] == newSelectedModels.get(j).intValue())
	  	   								{
	  	   									found = true;
	  	   									break;
	  	   								}
	  	   							}
	  	   							if(!found)
	  	   							{
	  	   								modelsChanged = true;
	  	   								break;
	  	   							}
	  	   						}
	  	   					}
	  	   					//check if selected ROIs have changed
	  	   					boolean selectedROIsChanged = false;
	  	   					boolean[] newSelectedROIs = fStudy.getSelectedROIsForErrorCalculation();
	  	   					if((oldSelectedROIs != null && newSelectedROIs ==null) ||
	  	   					   (oldSelectedROIs == null && newSelectedROIs !=null))
	  	   					{
	  	   						selectedROIsChanged = true;
	  	   					}
	  	   					else if(oldSelectedROIs != null && newSelectedROIs != null)
	  	   					{	
		  	   					if(oldSelectedROIs.length != newSelectedROIs.length)
		  	   					{
		  	   						selectedROIsChanged = true;
		  	   					}
		  	   					else
		  	   					{
			  	   					for(int i=0; i<FRAPData.VFRAP_ROI_ENUM.values().length; i++)
		  	   						{
	  	   								if(oldSelectedROIs[i] != newSelectedROIs[i])
	  	   								{
	  	   									selectedROIsChanged = true;
	  	   									break;
	  	   								}
		  	   						}
		  	   					}
	  	   					}
	  	   					//set need save flag
	  	   					if(modelsChanged || selectedROIsChanged)
	  	   					{
	  	   						fStudy.setSaveNeeded(true);
	  	   					}
	  	   					//if selected models have changed and the best model is not in the chosen models, reset best model and result display panel
	  	   					if(modelsChanged && oldBestModelIndex != null)
	  	   					{
		  	   					ArrayList<Integer> selectedModelIndexes = fStudy.getSelectedModels();
		  	   					boolean found = false;
		  	   					for(int i=0; i<selectedModelIndexes.size(); i++)
		  	   					{
		  	   						if(selectedModelIndexes.get(i).intValue() == oldBestModelIndex.intValue())
		  	   						{
		  	   							found = true;
		  	   							break;
		  	   						}
		  	   					}
		  	   					if(!found)
		  	   					{
			  	   					fStudy.setBestModelIndex(null);
		  	   						updateAnalysisResult(fStudy);
		  	   					}
	  	   					}
	  	   					//set workflow stage 
	  	   					getAnalysisProcedurePanel().setWorkFlowStage(AnalysisProcedurePanel.STAGE_EST_PARAMS);
	  	   				}
	  	   			}
	  	   		}
	  	   		else if(commandStr.equals(VirtualFrapMainFrame.ESTIMATE_PARAM_COMMAND))
	  	   		{
	  	   			showEstimateParamWizard();
	  	   		}
	  	   		
		    }
	    }
	}
	
	public class ResultPanelButtonHandler implements ActionListener
	{
	 	public void actionPerformed(ActionEvent e) 
	    {
	  	   	if(e.getSource() instanceof JButton)
		    {
	  	   		String commandStr = ((JButton)e.getSource()).getActionCommand();
	  	   		if(commandStr.equals(VirtualFrapMainFrame.RUN_SIM_COMMAND))
	  	   		{
	  	   			runSimulationForSelectedModel();
	  	   		}
	  	   		else if(commandStr.equals(VirtualFrapMainFrame.SHOW_SIM_RESULT_COMMAND))
	  	   		{
	  	   			try {
						refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);
						BeanUtils.centerOnComponent(get2DResultDialog(), FRAPStudyPanel.this);
						get2DResultDialog().setVisible(true);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	  	   			
	  	   		}
		    }
	    }
	}
	
	enum DisplayChoice { PDE,EXTTIMEDATA};

	private static final int USER_CHANGES_FLAG_ALL = 0xFFFFFFFF;
	private static final int USER_CHANGES_FLAG_ROI = 0x01;
	private static final int USER_CHANGES_FLAG_INI_PARAMS = 0x02;
	private static final int USER_CHANGES_FLAG_ADJUST_PARAMS = 0x04;
	
	//Elements that change the Model
	public static class FrapChangeInfo{
		public final boolean bROIValuesChanged;
		public final boolean bROISizeChanged;
		public final boolean bFreeDiffRateChanged;
		public final String freeDiffRateString;
		public final boolean bFreeMFChanged;
		public final String freeMFString;
		public final boolean bComplexDiffRateChanged;
		public final String complexDiffRateString;
		public final boolean bComplexMFChanged;
		public final String complexMFString;
		public final boolean bBleachWhileMonitorChanged;
		public final String bleachWhileMonitorRateString;
		public final boolean bBSConcentrationChanged;
		public final String bsConcentrationString;
		public final boolean bOnRateChanged;
		public final String onRateString;
		public final boolean bOffRateChanged;
		public final String offRateString;
		public final boolean bStartIndexForRecoveryChanged;
		public final String startIndexForRecoveryString;
		public FrapChangeInfo(boolean bROIValuesChanged,boolean bROISizeChanged,
				boolean bFreeDiffRateChanged,String freeDiffRateString,
				boolean bFreeMFChanged,String freeMFString,
				boolean bComplexDiffRateChanged,String complexDiffRateString,
				boolean bComplexMFChanged,String complexMFString,
				boolean bBleachWhileMonitorChanged,String bleachWhileMonitorRateString,
				boolean bBSConcentrationChanged, String bsConcentrationString,
				boolean bOnRateChanged, String onRateString,
				boolean bOffRateChanged, String offRateString,
				boolean bStartIndexForRecoveryChanged,String startIndexForRecoveryString)
		{
			this.bROIValuesChanged = bROIValuesChanged;
			this.bROISizeChanged = bROISizeChanged;
			this.bFreeDiffRateChanged = bFreeDiffRateChanged;
			this.freeDiffRateString = freeDiffRateString;
			this.bFreeMFChanged = bFreeMFChanged;
			this.freeMFString = freeMFString;
			this.bComplexDiffRateChanged = bComplexDiffRateChanged;
			this.complexDiffRateString = complexDiffRateString;
			this.bComplexMFChanged = bComplexMFChanged;
			this.complexMFString = complexMFString;
			this.bBleachWhileMonitorChanged = bBleachWhileMonitorChanged;
			this.bleachWhileMonitorRateString = bleachWhileMonitorRateString;
			this.bBSConcentrationChanged = bBSConcentrationChanged;
			this.bsConcentrationString = bsConcentrationString;
			this.bOnRateChanged = bOnRateChanged;
			this.onRateString = onRateString;
			this.bOffRateChanged = bOffRateChanged;
			this.offRateString = offRateString;
			this.bStartIndexForRecoveryChanged = bStartIndexForRecoveryChanged;
			this.startIndexForRecoveryString = startIndexForRecoveryString;
			//Don't forget to change 'hasAnyChanges' if adding new parameters
		}
		
		public boolean hasAnyChanges(){
			return bROIValuesChanged || bROISizeChanged ||
				bFreeDiffRateChanged || bFreeMFChanged ||
				bComplexDiffRateChanged || bComplexMFChanged ||
				bBleachWhileMonitorChanged || bBSConcentrationChanged ||
				bOnRateChanged || bOffRateChanged ||
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
			(bFreeDiffRateChanged?"(Free Particle Diffusion Rate)":" ")+
			(bFreeMFChanged?"(Free Particle Mobile Fraction)":" ")+
			(bComplexDiffRateChanged?"(Binding Complex Diffusion Rate)":" ")+
			(bComplexMFChanged?"(Binding Complex Mobile Fraction)":" ")+
			(bBleachWhileMonitorChanged?"(BleachWhileMonitoring Rate)":" ")+
			(bBSConcentrationChanged?"(Binding Complex Concentration)":" ")+
			(bOnRateChanged?"(Reaction On Rate)":" ")+
			(bOffRateChanged?"(Reaction Off Rate)":" ")+
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
		public final String lastFreeDiffusionrate;
		public final String lastFreeMobileFraction;
		public final String lastComplexDiffusionRate;
		public final String lastComplexMobileFraction;
		public final String lastBleachWhileMonitoringRate;
		public final String lastBSConcentration;
		public final String reactionOnRate;
		public final String reactionOffRate;
		public final String startingIndexForRecovery;
		public SavedFrapModelInfo(
			KeyValue savedSimKeyValue,
			ROI cellROI,
			ROI bleachROI,
			ROI backgroundROI,
			String freeDiffusionrate,
			String freeMobileFraction,
			String complexDiffusionRate,
			String complexMobileFraction,
			String bleachWhileMonitoringRate,
			String bsConcentration,
			String onRate,
			String offRate,
			String startingIndexForRecovery)
		{
//			if(savedSimKeyValue == null){
//				throw new IllegalArgumentException("SimKey cannot be null for a saved FrapModel.");
//			}
			this.savedSimKeyValue = savedSimKeyValue;
			this.lastCellROI = cellROI;
			this.lastBleachROI = bleachROI;
			this.lastBackgroundROI = backgroundROI;
			this.lastFreeDiffusionrate = freeDiffusionrate;
			this.lastFreeMobileFraction = freeMobileFraction;
			this.lastComplexDiffusionRate = complexDiffusionRate;
			this.lastComplexMobileFraction = complexMobileFraction;
			this.lastBleachWhileMonitoringRate = bleachWhileMonitoringRate;
			this.lastBSConcentration = bsConcentration;
			this.reactionOnRate = onRate;
			this.reactionOffRate = offRate;
			this.startingIndexForRecovery = startingIndexForRecovery;
		}
	};
	private FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfoNew2 = null;

	private static final String SAVE_FILE_OPTION = "Save";
			
	private class CurrentSimulationDataState{
		private final FrapChangeInfo frapChangeInfo;
		private final boolean areSimFilesOK;
		private final boolean areExtDataFileOK;
//		private final boolean isRefDataFileOK;
		public CurrentSimulationDataState() throws Exception{
			if(getSavedFrapModelInfo() == null){
				throw new Exception("CurrentSimulationDataState can't be determined because no savedFrapModelInfo available");
			}
			frapChangeInfo = getChangesSinceLastSave();
			areSimFilesOK = areSimulationFilesOK(getLocalWorkspace(),null);
			areExtDataFileOK =
				areExternalDataOK(getLocalWorkspace(),
				getFrapWorkspace().getFrapStudy().getFrapDataExternalDataInfo(),
				getFrapWorkspace().getFrapStudy().getRoiExternalDataInfo());
//			isRefDataFileOK = areReferenceDataOK(getLocalWorkspace(), getFrapStudy().getRefExternalDataInfo());
		}
		public boolean isDataInvalidBecauseModelChanged(){
			return frapChangeInfo.hasAnyChanges();
		}
		public boolean isDataInvalidBecauseMissingOrCorrupt(){
			return !areSimFilesOK || !areExtDataFileOK;
		}
		public boolean isDataValid(){
			if(!frapChangeInfo.hasAnyChanges() && areSimFilesOK && areExtDataFileOK){
				return true;
			}
			return false;
		}
		//commented since it is not being used.
//		public String getDescription(){
//			if(isDataValid()){
//				return "Simulation Data are valid.";
//			}else if(isDataInvalidBecauseModelChanged()){
//				return "Sim Data invalid because Model has changes including:\n"+frapChangeInfo.getChangeDescription();
//			}else if(isDataInvalidBecauseMissingOrCorrupt()){
//				return "Sim Data are missing or corrupt";
//			}
//			throw new RuntimeException("Unknown description");
//		}
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
		loadROICursors();
		initialize();
	}
	
	public void addUndoableEditListener(UndoableEditListener undoableEditListener){
		undoableEditSupport.addUndoableEditListener(undoableEditListener);
		getFRAPDataPanel().getOverlayEditorPanelJAI().setUndoableEditSupport(undoableEditSupport);
	}	
	
	public void setSavedFrapModelInfo(SavedFrapModelInfo savedFrapModelInfo) throws Exception{
		try{
			undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
		}catch(Exception e){
			e.printStackTrace();
		}
		savedFrapModelInfoNew2 = savedFrapModelInfo;
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

		KeyValue savedSimKey = null;
		if(frapStudy != null && frapStudy.getBioModel() != null && frapStudy.getBioModel().getSimulations()!= null && frapStudy.getBioModel().getSimulations().length > 0)
		{
			savedSimKey = frapStudy.getBioModel().getSimulations()[0].getSimulationVersion().getVersionKey();
		}
		
		FRAPData frapData = frapStudy.getFrapData();
		
		ROI savedCellROI = null;
		ROI savedBleachROI = null;
		ROI savedBackgroundROI = null;
		if(frapData != null){
			savedCellROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			savedCellROI = (savedCellROI == null?null:new ROI(savedCellROI));
			savedBleachROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
			savedBleachROI = (savedBleachROI == null?null:new ROI(savedBleachROI));
			savedBackgroundROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
			savedBackgroundROI = (savedBackgroundROI == null?null:new ROI(savedBackgroundROI));
		}
		
		String lastFreeDiffusionrate = null;
		String lastFreeMobileFraction = null;
		String lastComplexDiffusionRate = null;
		String lastComplexMobileFraction = null;
		String lastBleachWhileMonitoringRate = null;
		String lastBSConcentration = null;
		String reactionOnRate = null;
		String reactionOffRate = null;
		String startingIndexForRecovery = null;
		
		return new SavedFrapModelInfo(savedSimKey, savedCellROI, savedBleachROI, savedBackgroundROI, lastFreeDiffusionrate, lastFreeMobileFraction, lastComplexDiffusionRate, lastComplexMobileFraction, lastBleachWhileMonitoringRate, lastBSConcentration, reactionOnRate, reactionOffRate, startingIndexForRecovery);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(727, 607));
        this.setMinimumSize(new Dimension(640, 480));
        FRAPDataPanel fDataPanel = getFRAPDataPanel();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLeftPanel(), fDataPanel);
        split.setDividerLocation(356);
        split.setDividerSize(2);
        this.add(split);
        iniConnection();
	}

	private void iniConnection()
	{
		WorkFlowButtonHandler wfButtonHandler = new WorkFlowButtonHandler();
		ResultPanelButtonHandler resultButtonHandler = new ResultPanelButtonHandler();
		((AnalysisProcedurePanel)analysisProcedurePanel).addButtonHandler(wfButtonHandler);
		((ResultDisplayPanel)analysisRestultsPanel).addButtonHandler(resultButtonHandler);
	}
	
	/**
	 * Left panel is to the left of the frapDataPanel(imagepanel).
	 * It contains workFlowPanel and resultsDispalyPanel.
	 */
	
	private JPanel getLeftPanel()
	{
		if(leftPanel == null)
		{
			leftPanel = new JPanel(new BorderLayout());
			leftPanel.setBorder(new LineBorder(new Color(153, 186,243), 2));
			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getAnalysisProcedurePanel(),getAnalysisResultsPanel());
	        split.setDividerLocation(112);
	        split.setDividerSize(2);
			leftPanel.add(split);
		}
		return leftPanel;
	}
	
	private AnalysisProcedurePanel getAnalysisProcedurePanel()
	{
		if(analysisProcedurePanel == null)
		{
			analysisProcedurePanel = new AnalysisProcedurePanel();
			analysisProcedurePanel.setWorkFlowStage(AnalysisProcedurePanel.STAGE_LOAD_FRAP);
		}
		return analysisProcedurePanel;
	}
	
	private ResultDisplayPanel getAnalysisResultsPanel()
	{
		if(analysisRestultsPanel == null)
		{
			analysisRestultsPanel = new ResultDisplayPanel();
		}
		return analysisRestultsPanel;
	}
	
	/**
	 * This method initializes FRAPDataPanel	
	 * 	
	 * @return cbit.vcell.virtualmicroscopy.gui.FRAPDataPanel	
	 */
	public FRAPDataPanel getFRAPDataPanel() {
		if (frapDataPanel == null) {
			frapDataPanel = new FRAPDataPanel(false);//the frap data panel in the main frame is not editable
			frapDataPanel.setBorder(TAB_LINE_BORDER);
//			frapDataPanel.getOverlayEditorPanelJAI().addPropertyChangeListener(this);
			
			Hashtable<String, Cursor> cursorsForROIsHash = new Hashtable<String, Cursor>();
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_CELLROI]);
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_BLEACHROI]);
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_BACKGROUNDROI]);
			frapDataPanel.getOverlayEditorPanelJAI().setCursorsForROIs(cursorsForROIsHash);
			
			OverlayEditorPanelJAI.CustomROIImport importVFRAPROI = new OverlayEditorPanelJAI.CustomROIImport(){
				public boolean importROI(File inputFile) throws Exception{
					try{
						if(!VirtualFrapLoader.filter_vfrap.accept(inputFile)){
							return false;
						}
						String xmlString = XmlUtil.getXMLString(inputFile.getAbsolutePath());
						MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);

						FRAPStudy importedFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(),null);
						VirtualFrapMainFrame.updateProgress(0);
						ROI roi = getFrapWorkspace().getFrapStudy().getFrapData().getCurrentlyDisplayedROI();
						ROI[] importedROIs = importedFrapStudy.getFrapData().getRois();
						if(importedFrapStudy.getFrapData() != null && importedROIs != null){
							if(!importedROIs[0].getISize().compareEqual(roi.getISize())){
								throw new Exception(
										"Imported ROI mask size ("+
										importedROIs[0].getISize().getX()+","+
										importedROIs[0].getISize().getY()+","+
										importedROIs[0].getISize().getZ()+")"+
										" does not match current Frap DataSet size ("+
										roi.getISize().getX()+","+
										roi.getISize().getY()+","+
										roi.getISize().getZ()+
										")");
							}
							for (int i = 0; i < importedROIs.length; i++) {
								getFrapWorkspace().getFrapStudy().getFrapData().addReplaceRoi(importedROIs[i]);
							}
							undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
						}
						return true;
					} catch (Exception e1) {
						throw new Exception("VFRAP ROI Import - "+e1.getMessage());
					}
				}
			};
			frapDataPanel.getOverlayEditorPanelJAI().setCustomROIImport(importVFRAPROI);
			//set display mode
			frapDataPanel.adjustComponents(OverlayEditorPanelJAI.DISPLAY_WITH_ROIS);
		}
		return frapDataPanel;
	}

	public JDialog get2DResultDialog()
	{
		if(result2DDialog == null)
		{
			result2DDialog = new JDialog(JOptionPane.getFrameForComponent(this));
			result2DDialog.setTitle("2D Simulation Results");
			result2DDialog.setLayout(new BorderLayout());
			result2DDialog.add(getSimResultsViewPanel(), BorderLayout.CENTER);
			result2DDialog.setModal(true);
			
			result2DDialog.setSize(new Dimension(1000,700));
		}
		return result2DDialog;
	}


	private FRAPSimDataViewerPanel getFRAPSimDataViewerPanel(){
		if(frapSimDataViewerPanel == null){
			frapSimDataViewerPanel = new FRAPSimDataViewerPanel();
			frapSimDataViewerPanel.setBorder(TAB_LINE_BORDER);
		}
		return frapSimDataViewerPanel;
	}
	
	private void checkROIExsitence()throws Exception
	{
		FRAPData frapData = getFrapWorkspace().getFrapStudy().getFrapData();
		ROI cellROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		ROI bleachROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		ROI bgROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
		String msg = "";
		if(cellROI.isAllPixelsZero())
		{
			msg = msg + "Cell ROI,";
		}
		if(bleachROI.isAllPixelsZero())
		{
			msg = msg + "Bleached ROI,";
		}
		if(bgROI.isAllPixelsZero())
		{
			msg = msg + "Background ROI,";
		}
		if(!msg.equals(""))
		{
			msg = msg.substring(0, msg.length()-1)+" have to be defined before performing any analysis.";
			throw new  Exception(msg);
		}
	}
	
	private boolean checkROIConstraints() throws Exception{
		FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
		short[] cellPixels = fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getPixelsXYZ();
		short[] bleachPixels = fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getPixelsXYZ();
		short[] backgroundPixels = fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).getPixelsXYZ();
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
							fStudy.getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getISize().getX(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getISize().getY(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getISize().getZ());
					ROI newBleachROI = new ROI(ushortImage,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
					fStudy.getFrapData().addReplaceRoi(newBleachROI);
				}
				if(bFixedBackground){
					UShortImage ushortImage =
						new UShortImage(backgroundPixels,
							fStudy.getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getISize().getX(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getISize().getY(),
							fStudy.getFrapData().getCurrentlyDisplayedROI().getISize().getZ());
					ROI newBackgroundROI = new ROI(ushortImage,FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
					fStudy.getFrapData().addReplaceRoi(newBackgroundROI);
				}
			}
			return true;
		}
		return false;
	}
	
	private void checkStartIndexforRecovery() throws Exception{
		FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
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
	
		
	public AsynchClientTask[] save() 
	{
		AsynchClientTask beforeSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(getFrapWorkspace().getFrapStudy() == null || getFrapWorkspace().getFrapStudy().getFrapData() == null)
				{
					throw new Exception("No FRAP Data exists to save");
				}else{
					File outputFile = null;
					String saveFileName = getFrapWorkspace().getFrapStudy().getXmlFilename();
		    		if(saveFileName == null)
		    		{
		    			int choice = VirtualFrapLoader.saveFileChooser.showSaveDialog(FRAPStudyPanel.this);
		    			if (choice == JFileChooser.APPROVE_OPTION)
		    			{
		    				saveFileName = VirtualFrapLoader.saveFileChooser.getSelectedFile().getPath();
		    			}
		    			if(saveFileName != null)
			    		{
		    				File tempOutputFile = new File(saveFileName);
				    		if(!VirtualFrapLoader.filter_vfrap.accept(tempOutputFile)){
		    					if(tempOutputFile.getName().indexOf(".") == -1){
		    						tempOutputFile = new File(tempOutputFile.getParentFile(),tempOutputFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
		    					}else{
		    						throw new Exception("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);//return?
		    					}
		    				}
				    		if(tempOutputFile.exists())
				    		{
				    			String overwriteChoice = DialogUtils.showWarningDialog(FRAPStudyPanel.this, "OverWrite file\n"+ tempOutputFile.getAbsolutePath(),
				    						new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
				    			if(overwriteChoice.equals(UserMessage.OPTION_CANCEL)){
				    				throw UserCancelException.CANCEL_GENERIC;//----? Should use "return"?
//				    				return;
				    			}
				    			else
				    			{
					    			//Remove overwritten vfrap document external and simulation files
					    			try{
					    				MicroscopyXmlReader.ExternalDataAndSimulationInfo externalDataAndSimulationInfo =
					    					MicroscopyXmlReader.getExternalDataAndSimulationInfo(tempOutputFile);
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
					    					tempOutputFile.getAbsolutePath()+"  "+e.getMessage());
					    				e.printStackTrace();
					    			}
				    			}
				    		}
				    		outputFile = tempOutputFile;
			    		}
		    		}
		    		else
		    		{
		    			outputFile = new File(saveFileName);
		    		}
		    		
		    		if(outputFile != null)
		    		{
		    			VirtualFrapMainFrame.updateStatus("Saving file " + outputFile.getAbsolutePath()+" ...");
		    			hashTable.put(FRAPStudyPanel.SAVE_FILE_NAME_KEY, outputFile);
		    		}
				}
			}
		};
		
		AsynchClientTask saveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
				saveProcedure(outFile, false, this.getClientTaskStatusSupport());
			}
		};
		
		AsynchClientTask afterSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
				VirtualFrapMainFrame.updateStatus("File " + outFile.getAbsolutePath()+" has been saved.");
		        VirtualFrapLoader.mf.setMainFrameTitle(outFile.getName());
		        VirtualFrapMainFrame.updateProgress(0);
			}
		};
		
		return new AsynchClientTask[]{beforeSaveTask, saveTask, afterSaveTask};
	}
	
	public AsynchClientTask[] saveAs()
	{
		AsynchClientTask beforeSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(getFrapWorkspace().getFrapStudy() == null || getFrapWorkspace().getFrapStudy().getFrapData() == null)
				{
					throw new Exception("No FRAP Data exists to save");
				}else{
					File outputFile = null;
					String saveFileName = null;
	    			int choice = VirtualFrapLoader.saveFileChooser.showSaveDialog(FRAPStudyPanel.this);
	    			if (choice == JFileChooser.APPROVE_OPTION)
	    			{
	    				saveFileName = VirtualFrapLoader.saveFileChooser.getSelectedFile().getPath();
	    			}
		    		if(saveFileName != null)
		    		{
		    			
			    		File tempOutputFile = new File(saveFileName);
			    		if(!VirtualFrapLoader.filter_vfrap.accept(tempOutputFile)){
	    					if(tempOutputFile.getName().indexOf(".") == -1){
	    						tempOutputFile = new File(tempOutputFile.getParentFile(),tempOutputFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
	    					}else{
	    						throw new Exception("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);//return?
	    					}
	    				}
			    		if(tempOutputFile.exists())
			    		{
			    			String overwriteChoice = DialogUtils.showWarningDialog(FRAPStudyPanel.this, "OverWrite file\n"+ tempOutputFile.getAbsolutePath(),
			    						new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
			    			if(overwriteChoice.equals(UserMessage.OPTION_CANCEL)){
			    				throw UserCancelException.CANCEL_GENERIC;//----? Should use "return"?
			    			}
			    			else
			    			{
				    			//Remove overwritten vfrap document external and simulation files
				    			try{
				    				MicroscopyXmlReader.ExternalDataAndSimulationInfo externalDataAndSimulationInfo =
				    					MicroscopyXmlReader.getExternalDataAndSimulationInfo(tempOutputFile);
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
				    					tempOutputFile.getAbsolutePath()+"  "+e.getMessage());
				    				e.printStackTrace();
				    			}
			    			}
			    		}
			    		outputFile = tempOutputFile;
		    		}
		    		if(outputFile != null)
		    		{
		    			VirtualFrapMainFrame.updateStatus("Saving file " + outputFile.getAbsolutePath()+" ...");
		    			hashTable.put(FRAPStudyPanel.SAVE_FILE_NAME_KEY, outputFile);
		    		}
				}
			}
		};
		
		AsynchClientTask saveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
				saveProcedure(outFile, true, this.getClientTaskStatusSupport());
			}
		};
		
		AsynchClientTask afterSaveAsTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//disable simulation result button if it was enabled, because we don't copy simulation files over
				getAnalysisResultsPanel().setResultsButtonEnabled(false);
				//update mainframe title and progress
				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
				VirtualFrapMainFrame.updateStatus("File " + outFile.getAbsolutePath()+" has been saved.");
		        VirtualFrapLoader.mf.setMainFrameTitle(outFile.getName());
		        VirtualFrapMainFrame.updateProgress(0);
			}
		};
		
		return new AsynchClientTask[]{beforeSaveTask, saveTask, afterSaveAsTask};
	}
	private void applyUserChangesToCurrentFRAPStudy(int applyUserChangeFlags) throws Exception{
		if((applyUserChangeFlags&USER_CHANGES_FLAG_ROI) != 0){
			getFRAPDataPanel().saveROI();
		}
		if((applyUserChangeFlags&USER_CHANGES_FLAG_INI_PARAMS) != 0){
			getFRAPParametersPanel().insertFRAPIniModelParametersIntoFRAPStudy(getFrapWorkspace().getFrapStudy());	
		}
		if((applyUserChangeFlags&USER_CHANGES_FLAG_ADJUST_PARAMS) != 0){
//			getResultsSummaryPanel().insertPureDiffusionParametersIntoFRAPStudy(getFrapWorkspace().getFrapStudy());
		}
	}
	
//	private AsynchClientTask[] saveAsInternal(String saveToFileName, KeyValue argKeyValue) /*throws Exception*/
//	{
//
////		applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ALL);
//		boolean bSaveAs = saveToFileName == null;
//		File outputFile = null;
//		if(bSaveAs){
//			final int[] retvalArr = new int[1];
//			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
//				retvalArr[0] = VirtualFrapLoader.saveFileChooser.showSaveDialog(FRAPStudyPanel.this);
//			}});
//			if (retvalArr[0] == JFileChooser.APPROVE_OPTION){
//				String outputFileName = VirtualFrapLoader.saveFileChooser.getSelectedFile().getPath();
//				outputFile = new File(outputFileName);
//				if(!VirtualFrapLoader.filter_vfrap.accept(outputFile)){
//					if(outputFile.getName().indexOf(".") == -1){
//						outputFile = new File(outputFile.getParentFile(),outputFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
//					}else{
//						throw new Exception("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);
//					}
//				}
//			}else{
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//		}else{
//			outputFile = new File(saveToFileName);
//		}
//		if(bSaveAs && outputFile.exists()){
//			if(Compare.isEqualOrNull(outputFile.getAbsolutePath(), getFrapWorkspace().getFrapStudy().getXmlFilename())){
//				throw new Exception("File name is same.  Use 'Save' to update current document file.");
//			}
//			final String OK_OPTION = "OK";
//			final String[] resultArr = new String[1];
//			final File outputFileFinal = outputFile;
//			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
//				resultArr[0] = DialogUtils.showWarningDialog(FRAPStudyPanel.this, "OverWrite file\n"+outputFileFinal.getAbsolutePath(),
//						new String[] {OK_OPTION,"Cancel"}, "Cancel");
//			}});
//			if(!resultArr[0].equals(OK_OPTION)){
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//			//Remove overwritten vfrap document external and simulation files
//			try{
//				MicroscopyXmlReader.ExternalDataAndSimulationInfo externalDataAndSimulationInfo =
//					MicroscopyXmlReader.getExternalDataAndSimulationInfo(outputFileFinal);
//				FRAPStudy.removeExternalDataAndSimulationFiles(
//						externalDataAndSimulationInfo.simulationKey,
//						(externalDataAndSimulationInfo.frapDataExtDataInfo != null
//							?externalDataAndSimulationInfo.frapDataExtDataInfo.getExternalDataIdentifier():null),
//						(externalDataAndSimulationInfo.roiExtDataInfo != null
//							?externalDataAndSimulationInfo.roiExtDataInfo.getExternalDataIdentifier():null),
//						getLocalWorkspace());
//			}catch(Exception e){
//				System.out.println(
//					"Error deleting externalData and simulation files for overwritten vfrap document "+
//					outputFileFinal.getAbsolutePath()+"  "+e.getMessage());
//				e.printStackTrace();
//			}
//		}
//		saveProcedure(outputFile,bSaveAs,argKeyValue);
//	}
	
	private void saveProcedure(File xmlFrapFile, boolean bSaveAs, ClientTaskStatusSupport progressListener) throws Exception
	{
		if(bSaveAs)
		{
			//remove simulation key from simulation, save as doesn't copy simulation files over
			FRAPStudy fStudy = getFrapWorkspace().getFrapStudy(); 
			if(fStudy.getBioModel() != null && fStudy.getBioModel().getSimulations() != null && fStudy.getBioModel().getSimulations().length > 0 &&
			   fStudy.getBioModel().getSimulations()[0] != null)
			{
				Simulation oldSim = fStudy.getBioModel().getSimulations()[0];
				fStudy.getBioModel().removeSimulation(oldSim);
			}
		}
		//save
		MicroscopyXmlproducer.writeXMLFile(getFrapWorkspace().getFrapStudy(), xmlFrapFile, true,progressListener,VirtualFrapMainFrame.SAVE_COMPRESSED);
		getFrapWorkspace().getFrapStudy().setXmlFilename(xmlFrapFile.getAbsolutePath());
		getFrapWorkspace().getFrapStudy().setSaveNeeded(false);
	}
	
	private boolean cleanupSavedSimDataFilesIfNotOK() throws Exception{
		CurrentSimulationDataState currentSimulationDataState =
			new CurrentSimulationDataState();
		if(!currentSimulationDataState.isDataValid()){
			//remove saved simulation files (they no longer apply to this model)
			ExternalDataIdentifier frapDataExtDataId =
				(getFrapWorkspace().getFrapStudy() != null && getFrapWorkspace().getFrapStudy().getFrapDataExternalDataInfo() != null
					?getFrapWorkspace().getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier():null);
			ExternalDataIdentifier roiExtDataId =
				(getFrapWorkspace().getFrapStudy() != null && getFrapWorkspace().getFrapStudy().getRoiExternalDataInfo() != null
					?getFrapWorkspace().getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier():null);
			KeyValue simulationKeyValue =
				(getSavedFrapModelInfo() != null?getSavedFrapModelInfo().savedSimKeyValue:null);
			FRAPStudy.removeExternalDataAndSimulationFiles(simulationKeyValue,frapDataExtDataId,roiExtDataId,getLocalWorkspace());
			return true;
		}
		return false;
	}
	
	//open .vfrap document
	public AsynchClientTask[] open(final File inFile) 
	{
		final String LOADING_MESSAGE = "Loading "+inFile.getAbsolutePath()+"...";
		ArrayList<AsynchClientTask> totalTasks = new ArrayList<AsynchClientTask>(); 
				 
	    //check if save is needed before loading data
	    if(getFrapWorkspace().getFrapStudy().isSaveNeeded())
	    {
			String choice = DialogUtils.showWarningDialog(FRAPStudyPanel.this, "There are unsaved changes. Save current document before loading new document?", new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
			if(choice.equals(UserMessage.OPTION_OK))
			{
				AsynchClientTask[] saveTasks = save();
				for(int i=0; i<saveTasks.length; i++)
				{
					totalTasks.add(saveTasks[i]);
				}
			}
	    }
	    
		AsynchClientTask preOpenTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
			}
		};
		totalTasks.add(preOpenTask);
		
		AsynchClientTask loadTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{ 
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{

					
					FRAPStudy newFRAPStudy = null;
					String newVFRAPFileName = null;
					
					if(inFile.getName().endsWith("."+VirtualFrapLoader.VFRAP_EXTENSION) || inFile.getName().endsWith(".xml")) //.vfrap
					{
						String xmlString = XmlUtil.getXMLString(inFile.getAbsolutePath());
						MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
						newFRAPStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(),this.getClientTaskStatusSupport());
						if(!areExternalDataOK(getLocalWorkspace(),newFRAPStudy.getFrapDataExternalDataInfo(),newFRAPStudy.getRoiExternalDataInfo()))
						{
							newFRAPStudy.setFrapDataExternalDataInfo(null);
							newFRAPStudy.setRoiExternalDataInfo(null);
						}
						newVFRAPFileName = inFile.getAbsolutePath();
					}
					//for loaded file
					newFRAPStudy.setXmlFilename(newVFRAPFileName);
					hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
			}
		};
		totalTasks.add(loadTask);
		
		AsynchClientTask updateUIAfterLoadingTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = (FRAPStudy)hashTable.get(FRAPStudyPanel.NEW_FRAPSTUDY_KEY);
				getFrapWorkspace().setFrapStudy(fStudy,true);
				String fName = "";
				if(fStudy != null && fStudy.getXmlFilename() != null && fStudy.getXmlFilename().length() > 0)
				{
					fName = fStudy.getXmlFilename();
				}
				VirtualFrapLoader.mf.setMainFrameTitle(fName);
				VirtualFrapMainFrame.updateStatus("Loaded " + fName);
	            VirtualFrapMainFrame.updateProgress(0);
			}
		};
		totalTasks.add(updateUIAfterLoadingTask);
		
		return totalTasks.toArray(new AsynchClientTask[totalTasks.size()]);
	}
	
	public static boolean areSimulationFilesOK(LocalWorkspace localWorkspace,KeyValue key){
		
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
		File[] simFiles = FRAPStudy.getSimulationFileNames(	new File(localWorkspace.getDefaultSimDataDirectory()),	key);
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
	
	public static boolean areExternalDataOK(LocalWorkspace localWorkspace,ExternalDataInfo imgDataExtDataInfo,ExternalDataInfo roiExtDataInfo)
	{
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
	
	private void showEstimateParamWizard() 
	{
		//check if frapOpt data is null? if yes, run ref simulation. 
		//check if parameters of each selected models are there, if not, run analytic solution, get best parameters and store parameters
		AsynchClientTask saveTask1 = new AsynchClientTask("Preparing for parameters estimation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
				if(fStudy.hasOptModel())
				{
					//create optdata
					if(fStudy.getFrapOptData() == null)
					{
						if(fStudy.getStoredRefData() != null)
						{
							getFrapWorkspace().getFrapStudy().setFrapOptData(new FRAPOptData(fStudy, FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), fStudy.getStoredRefData()));
						}
						else
						{
							//reference data is null, it is not stored, we have to run ref simulation then
							//check external data info
							if(!areExternalDataOK(getLocalWorkspace(),fStudy.getFrapDataExternalDataInfo(), fStudy.getRoiExternalDataInfo()))
							{
								//if external files are missing/currupt or ROIs are changed, create keys and save them
								fStudy.setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
								fStudy.setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
								fStudy.saveROIsAsExternalData(localWorkspace, fStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
								fStudy.saveImageDatasetAsExternalData(localWorkspace, fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
							}
							//run ref sim
							fStudy.setFrapOptData(new FRAPOptData(fStudy, FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, localWorkspace, this.getClientTaskStatusSupport()));
						}
					}
				}
			}
		
		};
		
		AsynchClientTask runOptTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
				if(fStudy.hasOptModel())
				{
					ArrayList<Integer> models = fStudy.getSelectedModels();
					for(int i = 0; i<models.size(); i++)
					{
						if(((Integer)models.get(i)).intValue() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
						{
							if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() == null)
							{
								fStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF);
								Parameter[] initialParams = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]);
								Parameter[] bestParameters = fStudy.getFrapOptData().getBestParamters(initialParams, fStudy.getSelectedROIsForErrorCalculation());
								fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].setModelParameters(bestParameters);
							}
							
							
						}
						else if(((Integer)models.get(i)).intValue() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
						{
							if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() == null)
							{
								fStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF);
								Parameter[] initialParams = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]);
								Parameter[] bestParameters = fStudy.getFrapOptData().getBestParamters(initialParams, fStudy.getSelectedROIsForErrorCalculation());
								fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].setModelParameters(bestParameters);
							}
						}
					}
				}
				hashTable.put(FRAPSTUDY_KEY, fStudy);
			}
		
		};

		AsynchClientTask showDialogTask = new AsynchClientTask("Showing estimation results ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//get frap models  before the wizard
				FRAPStudy fStudy = (FRAPStudy)hashTable.get(FRAPSTUDY_KEY);
				FRAPModel[] oldFrapModels = new FRAPModel[FRAPModel.NUM_MODEL_TYPES];
				for(int i=0; i<fStudy.getModels().length; i++)
				{
					if(fStudy.getModels()[i] != null)
					{
						FRAPModel  model = fStudy.getModels()[i];
						oldFrapModels[i] = new FRAPModel(new String(model.getModelIdentifer()), model.duplicateParameters(), null, model.duplicateTimePoints());
					}
				}
				//get best model index before the wizard
				Integer oldBestModelIndex = null;
				if(fStudy.getBestModelIndex() != null)
				{
					oldBestModelIndex = new Integer(fStudy.getBestModelIndex().intValue());
					//put old best model index into hashtable
					hashTable.put(BEST_MODEL_KEY, oldBestModelIndex);
				}
				//put old frapmodels in hashtable
				hashTable.put(FRAPMODELS_KEY, oldFrapModels);
				
				//show wizard
				Wizard estParamWizard = getEstimateParametersWizard();
				estParamWizard.showModalDialog(new Dimension(1000,700));
				//put wizard in hashtable in order to check return code in next task
				hashTable.put(EST_PARAM_WIZARD_KEY, estParamWizard);
			}
		
		};
		
		AsynchClientTask updateSaveStatusTask = new AsynchClientTask("...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				Wizard estParamWizard = (Wizard)hashTable.get(EST_PARAM_WIZARD_KEY);
				//get old values from hashtable
				FRAPModel[] oldFrapModels = (FRAPModel[])hashTable.get(FRAPMODELS_KEY);
				Integer oldBestModelIndex = (Integer)hashTable.get(BEST_MODEL_KEY);
				//get new values
				Integer newBestModelIndex = getFrapWorkspace().getFrapStudy().getBestModelIndex();
				if(estParamWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
				{
					if(!getFrapWorkspace().getFrapStudy().areFRAPModelsEqual(oldFrapModels) ||
					   (oldBestModelIndex == null && newBestModelIndex != null) ||
					   (oldBestModelIndex != null && newBestModelIndex == null) ||
					   (oldBestModelIndex != null && newBestModelIndex != null && oldBestModelIndex.intValue() != newBestModelIndex.intValue()))
					{
						getFrapWorkspace().getFrapStudy().setSaveNeeded(true);
					}
				}
			}
		
		};
		
		//dispatch
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{saveTask1, runOptTask, showDialogTask, updateSaveStatusTask}, false);
	}
	
	private void showMovie()
	{
		AsynchClientTask createMovieTask = new AsynchClientTask("Buffering movie data...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(movieURLString != null && movieFileString != null)
				{
					return;
				}
				//create export specs
				Simulation sim = null;
				FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
				if (fStudy==null || fStudy.getBioModel()==null || fStudy.getBioModel().getSimulations()==null || fStudy.getBioModel().getSimulations().length < 1 ){
					return;
				}else{
					sim = fStudy.getBioModel().getSimulations()[0];
				}
				FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
				FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
				for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
					fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier());
				}
				ExternalDataIdentifier timeSeriesExtDataID = fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier();
				ExternalDataIdentifier maskExtDataID = fStudy.getRoiExternalDataInfo().getExternalDataIdentifier();
				//add sim
				int jobIndex = 0;
				SimulationJob simJob = new SimulationJob(sim,jobIndex,fieldDataIdentifierSpecs);
								
				VCDataIdentifier[] dataIDs = new VCDataIdentifier[] {timeSeriesExtDataID, maskExtDataID, simJob.getVCDataIdentifier()};
				VCDataIdentifier vcDataId = new MergedDataInfo(LocalWorkspace.getDefaultOwner(),dataIDs, VFRAP_DS_PREFIX);
								
				PDEDataManager dataManager = new PDEDataManager(getLocalWorkspace().getVCDataManager(),vcDataId);
				PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);
						
				int format = ExportConstants.FORMAT_QUICKTIME;
				String[] variableNames = new String[]{NORM_FLUOR_VAR, NORM_SIM_VAR};
				VariableSpecs variableSpecs = new VariableSpecs(variableNames, ExportConstants.VARIABLE_MULTI);
						
				int endTimeIndex = (int)Math.round(sim.getSolverTaskDescription().getTimeBounds().getEndingTime()/sim.getSolverTaskDescription().getTimeStep().getDefaultTimeStep());
				TimeSpecs timeSpecs = new TimeSpecs(0, endTimeIndex, pdeDataContext.getTimePoints(), ExportConstants.TIME_RANGE);
				int geoMode = ExportConstants.GEOMETRY_SLICE;
				GeometrySpecs geometrySpecs = new GeometrySpecs(null, Coordinate.Z_AXIS, 0, geoMode);
				
				double duration = 10000; //10s
				DisplayPreferences pref1 = new DisplayPreferences("BlueRed", new Range(0.01,1.1),false);
				DisplayPreferences pref2 = new DisplayPreferences("BlueRed", new Range(0.01,1.1),false);
				DisplayPreferences[] displayPref = new DisplayPreferences[]{pref1, pref2};
				int imageScale = 1;
				int membraneScale = 1;
				int scaleMode = ImagePaneModel.MESH_MODE;
//				int scaleMode = ImagePaneModel.NORMAL_MODE;
				MovieSpecs mSpec = new MovieSpecs(duration,
												 true,
												 displayPref,
												 ExportConstants.RAW_RGB,
												 ExportConstants.NO_MIRRORING,
												 false,
												 imageScale,
												 membraneScale,
												 scaleMode);
				mSpec.setViewZoom(1);
				ExportSpecs exSpecs = new ExportSpecs(vcDataId, format, variableSpecs, timeSpecs, geometrySpecs, mSpec);
				// pass the request
				ExportEvent exportEvt = ((VirtualFrapWindowManager)getFlourDataViewer().getDataViewerManager()).startExportMovie(exSpecs);
				hashTable.put("ExportEvt", exportEvt);
			}
		};
		AsynchClientTask showMovieTask = new AsynchClientTask("Showing movie ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING)
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				ExportEvent exportEvt = (ExportEvent)hashTable.get("ExportEvt");
				//show movie if successfully exported
				if(exportEvt != null)
				{
					final String fileURLString = System.getProperty(PropertyLoader.exportBaseURLProperty) + exportEvt.getJobID() + ".mov";
					final String fileString = System.getProperty(PropertyLoader.exportBaseDirProperty) + exportEvt.getJobID() + ".mov";
					movieURLString = fileURLString;
					movieFileString = fileString;
				}
				JMFPlayer.showMovieInDialog(get2DResultDialog(),movieURLString, movieFileString);
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{createMovieTask,showMovieTask}, false);
	}
	
	
	protected void refreshPDEDisplay(DisplayChoice choice) throws Exception {
		Simulation sim = null;
		FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
		if (fStudy==null || fStudy.getBioModel()==null || fStudy.getBioModel().getSimulations()==null || fStudy.getBioModel().getSimulations().length < 1){
			return;
		}else{
			sim = fStudy.getBioModel().getSimulations()[0];
		}
		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier());
		}
		PDEDataViewer flourViewer = getFlourDataViewer();/*, getFlourDataViewer2()*/
		PDEDataManager dataManager = null;

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
							((dataidentifier.getName().indexOf(NORM_FLUOR_VAR)!= -1)||
							 (dataidentifier.getName().indexOf(NORM_SIM_VAR)!= -1));
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
			
			ExternalDataIdentifier timeSeriesExtDataID = fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier();
			ExternalDataIdentifier maskExtDataID =fStudy.getRoiExternalDataInfo().getExternalDataIdentifier();
			//add sim
			int jobIndex = 0;
			SimulationJob simJob = new SimulationJob(sim,jobIndex,fieldDataIdentifierSpecs);
							
			VCDataIdentifier[] dataIDs = new VCDataIdentifier[] {timeSeriesExtDataID, maskExtDataID, simJob.getVCDataIdentifier()};
			VCDataIdentifier vcDataId = new MergedDataInfo(LocalWorkspace.getDefaultOwner(),dataIDs, VFRAP_DS_PREFIX);
							
			dataManager = new PDEDataManager(getLocalWorkspace().getVCDataManager(),vcDataId);
			PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);
			// add function to display normalized fluorence data 
			Norm_Exp_Fluor = new Expression(Norm_Exp_Fluor_Str);
			SimulationSymbolTable simSymbolTable = simJob.getSimulationSymbolTable();
			if(simSymbolTable.getVariable(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE) == null)//one diffusing component
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
			SimulationModelInfo simModelInfo =	new SimulationWorkspaceModelInfo(fStudy.getBioModel().getSimulationContext(sim),sim.getName());
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
					getFrapWorkspace().getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getISize(),
				(lastCellROI == null?null:lastCellROI.getISize()));
		boolean bCellROISame =
			Compare.isEqualOrNull(lastCellROI,getFrapWorkspace().getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
		boolean bBleachROISame =
			Compare.isEqualOrNull(lastBleachROI,getFrapWorkspace().getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()));
		boolean bBackgroundROISame =
			Compare.isEqualOrNull(lastBackgroundROI,getFrapWorkspace().getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()));
		
		String userStartingIndexForRecovery = getFRAPParametersPanel().getUserStartIndexForRecoveryString();
		
		return null;
	}
	
	protected /*FrapChangeInfo*/void refreshBiomodel() throws Exception{
		try{

			//check if all the data for generating spatial model are ready.
//			FrapChangeInfo frapChangeInfo = getChangesSinceLastSave();
			
			FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
			
			if (fStudy == null){
				throw new Exception("No FrapStudy is defined");
			}
			if (fStudy.getFrapData()==null){
				throw new Exception("No FrapData is defined");
			}
			if (fStudy.getFrapData().getImageDataset()==null){
				throw new Exception("No ImageDataSet is defined");
			}
			
			fStudy.refreshDependentROIs();
						
			/*Parameter[] parameters = getResultsSummaryPanel().getReactionDiffusionPanel().getCurrentParameters();
			if(parameters != null)
			{
				BioModel newBioModel = FRAPStudy.createNewSimBioModel(
						fStudy,
						parameters,
						null, 
						(getSavedFrapModelInfo() == null || getSavedFrapModelInfo().savedSimKeyValue == null?null:getSavedFrapModelInfo().savedSimKeyValue),
						LocalWorkspace.getDefaultOwner(),
						new Integer(fStudy.getStartingIndexForRecovery()));
				fStudy.setBioModel(newBioModel);
			}*/
						
//			return frapChangeInfo;

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
		getFRAPDataPanel().getOverlayEditorPanelJAI().setDefaultImportDirAndFilters(
			new File(getLocalWorkspace().getDefaultWorkspaceDirectory()),
			new FileFilter[] {VirtualFrapLoader.filter_vfrap});

		try {
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getPDEDataViewer2());
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
	
	private PDEDataManager getDataManager(Simulation sim) throws Exception{
		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],getFrapWorkspace().getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier());
		}		
		int jobIndex = 0;
		SimulationJob simJob = new SimulationJob(sim,jobIndex,fieldDataIdentifierSpecs);
		PDEDataManager dataManager = new PDEDataManager(getLocalWorkspace().getVCDataManager(), simJob.getVCDataIdentifier());
		return dataManager;
	}
	
	
	public void propertyChange(PropertyChangeEvent evt) 
	{
		
		if(evt.getPropertyName().equals(FRAPWorkspace.PROPERTY_CHANGE_FRAPSTUDY_NEW) || evt.getPropertyName().equals(FRAPWorkspace.PROPERTY_CHANGE_FRAPSTUDY_UPDATE))
		{
			if(evt.getNewValue() instanceof FRAPStudy)
			{
				FRAPStudy fStudy = (FRAPStudy)evt.getNewValue();
				//set status for save button
				VirtualFrapMainFrame.enableSave(!(fStudy == null || fStudy.getFrapData() == null));
				//update analysis stage and results
				updateWorkflowStage(fStudy);
				updateAnalysisResult(fStudy);
				//undoable support
				try{
					undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
				}catch(Exception e){
					e.printStackTrace(System.out);
				}
			}
		}
		if(evt.getPropertyName().equals(FRAPWorkspace.PROPERTY_CHANGE_BEST_MODEL))
		{
			if(evt.getNewValue() instanceof Integer)
			{
				int newBestModelIdx = ((Integer)evt.getNewValue()).intValue();
				((ResultDisplayPanel)getAnalysisResultsPanel()).setBestModel(newBestModelIdx, getLocalWorkspace());
			}
		}
	}

	public void refreshUI()
	{
		VirtualFrapMainFrame.enableSave(true);
		//TODO: not sure why we need to do refreshBioModel and clear the model again here???
//		try{
//			refreshBiomodel();
//		}catch(Exception e){
//			getFrapStudy().clearBioModel();
//		}

	}
	
	private void runSimulationForSelectedModel() 
	{
		AsynchClientTask prepareRunBindingReactionTask = new AsynchClientTask("Preparing to run simulation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
					FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
										
					boolean bExtDataOK = !areExternalDataOK(getLocalWorkspace(), fStudy.getFrapDataExternalDataInfo(),fStudy.getRoiExternalDataInfo());
					{
						if(!bExtDataOK)
						{
							refreshBiomodel();//refresh rois
							//if external files are missing/currupt or ROIs are changed, create keys and save them
							fStudy.setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(getLocalWorkspace(), FRAPStudy.IMAGE_EXTDATA_NAME));
							fStudy.setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(getLocalWorkspace(), FRAPStudy.ROI_EXTDATA_NAME));
							try {
								fStudy.saveROIsAsExternalData(getLocalWorkspace(), fStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
								fStudy.saveImageDatasetAsExternalData(getLocalWorkspace(), fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								((ResultDisplayPanel)getAnalysisResultsPanel()).setResultsButtonEnabled(false);
							}
						}
					}
					
//						//save model do it later. 
//						String savedFileName = (fStudy.getXmlFilename() == null || fStudy.getXmlFilename().length()<1)? null:fStudy.getXmlFilename();
//						try{
////							refreshBiomodel();
////							createBioModel();
//							
//							
//							//Need a file name to proceed to simulation
//							if(savedFileName == null)
//							{
//								String saveMessage = "Current unsaved document must be saved before running simulation.";
//								String result = DialogUtils.showWarningDialog(FRAPStudyPanel.this, saveMessage, new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
//								if(result.equals(UserMessage.OPTION_CANCEL)){
//									return;
//								}
//								else
//								{
//									if(!bExtDataOK)
//									saveAs();
//								}
//							}
//							else
//							{
//								save();
//							}
//								
//							
//						}catch(Exception e){
//							throw new Exception("Error checking save before running simulation:\n"+e.getMessage());
//						}
			}
			
		};
		//-------------------------------------------------------------------------
		
		AsynchClientTask runReactionBindingTask = new AsynchClientTask("Running simulation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
				Parameter[] bestParams = fStudy.getModels()[fStudy.getBestModelIndex().intValue()].getModelParameters();
				if(bestParams.length < 3)
				{
					return;
				}
				else
				{
					KeyValue oldSimKey = null;
					if(fStudy != null && fStudy.getBioModel() != null && fStudy.getBioModel().getSimulations()!=null &&
							fStudy.getBioModel().getSimulations().length > 0 && fStudy.getBioModel().getSimulations()[0].getVersion()!= null &&
							fStudy.getBioModel().getSimulations()[0].getVersion().getVersionKey() != null)
					{
						oldSimKey = fStudy.getBioModel().getSimulations()[0].getVersion().getVersionKey();
					}
					
					BioModel bioModel = null;
	
					try{
						bioModel = FRAPStudy.createNewSimBioModel(fStudy, 
																  bestParams, 
								                                  null, 
								                                  LocalWorkspace.createNewKeyValue(), 
								                                  LocalWorkspace.getDefaultOwner(),
								                                  fStudy.getStartingIndexForRecovery());
						//run simulation
						FRAPStudy.runFVSolverStandalone(
							new File(getLocalWorkspace().getDefaultSimDataDirectory()),
							new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
							bioModel.getSimulations(0),
							fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),
							fStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),
							this.getClientTaskStatusSupport(), false);
	
						
						fStudy.setBioModel(bioModel);
						FRAPStudy.removeSimulationFiles(oldSimKey, getLocalWorkspace());
					}catch(Exception e){
						if(bioModel != null && bioModel.getSimulations() != null){
							FRAPStudy.removeExternalDataAndSimulationFiles(
								bioModel.getSimulations()[0].getVersion().getVersionKey(), null, null, getLocalWorkspace());
							((ResultDisplayPanel)getAnalysisResultsPanel()).setResultsButtonEnabled(false);
						}
						throw e;
					}
				}
			}
		};
		
		AsynchClientTask updateUITask = new AsynchClientTask("Simulation Done.", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				VirtualFrapMainFrame.updateStatus("Simulation Done.");
				((ResultDisplayPanel)getAnalysisResultsPanel()).setResultsButtonEnabled(true);
			}
		};
		
		//dispatch
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{prepareRunBindingReactionTask, runReactionBindingTask, updateUITask}, false);
	}

	private boolean areReactionDiffusionParametersChangedSinceLastRun(Parameter[] parameters) throws NumberFormatException 
	{
		SavedFrapModelInfo savedInfo = getSavedFrapModelInfo();
		if(parameters[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() != Double.parseDouble(savedInfo.lastFreeDiffusionrate))
		{
			return true;
		}
		if(parameters[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() != Double.parseDouble(savedInfo.lastFreeMobileFraction))
		{
			return true;
		}
		if(parameters[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess() != Double.parseDouble(savedInfo.lastComplexDiffusionRate))
		{
			return true;
		}
		if(parameters[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess() != Double.parseDouble(savedInfo.lastComplexMobileFraction))
		{
			return true;
		}
		if(parameters[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() != Double.parseDouble(savedInfo.lastBleachWhileMonitoringRate))
		{
			return true;
		}
		if(parameters[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess() != Double.parseDouble(savedInfo.lastBSConcentration))
		{
			return true;
		}
		if(parameters[FRAPModel.INDEX_ON_RATE].getInitialGuess() != Double.parseDouble(savedInfo.reactionOnRate))
		{
			return true;
		}
		if(parameters[FRAPModel.INDEX_OFF_RATE].getInitialGuess() != Double.parseDouble(savedInfo.reactionOffRate))
		{
			return true;
		}
		return false;
	}

	public void applySavedParameters(FRAPStudy frapStudy)
	{
		/*if(frapStudy != null && frapStudy.getFrapModelParameters() != null)
		{
			if(frapStudy.getFrapData() != null && frapStudy.getFrapData().getImageDataset() != null &&
					frapStudy.getFrapData().getImageDataset().getImageTimeStamps() != null)
			{
				if(frapStudy.getFrapModelParameters().getIniModelParameters() != null)
				{
					getFRAPParametersPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getIniModelParameters(),frapStudy.getFrapData().getImageDataset().getImageTimeStamps());
				}
				else
				{
					getFRAPParametersPanel().updateSavedParameters(null,frapStudy.getFrapData().getImageDataset().getImageTimeStamps());
				}
			}
			else //clear the text fields
			{
				if(frapStudy.getFrapModelParameters().getIniModelParameters() != null)
				{
					getFRAPParametersPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getIniModelParameters(), null);
				}
				else
				{
					getFRAPParametersPanel().updateSavedParameters(null, null);
				}
			}
			if(frapStudy.getFrapModelParameters().getPureDiffModelParameters() != null)
			{
				getResultsSummaryPanel().getPureDiffusionPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getPureDiffModelParameters());
			}
			else //clear the text fields
			{
				getResultsSummaryPanel().getPureDiffusionPanel().updateSavedParameters(null);
			}
			if(frapStudy.getFrapModelParameters().getReacDiffModelParameters() != null)
			{
				getResultsSummaryPanel().getReactionDiffusionPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getReacDiffModelParameters());
			}
			else //clear the text fields
			{
				getResultsSummaryPanel().getReactionDiffusionPanel().updateSavedParameters(null);
			}
		}
		else //model parameters is null, clear all the text fields
		{
			if(frapStudy.getFrapData() != null && frapStudy.getFrapData().getImageDataset() != null &&
					frapStudy.getFrapData().getImageDataset().getImageTimeStamps() != null)
			{
				getFRAPParametersPanel().updateSavedParameters(null,frapStudy.getFrapData().getImageDataset().getImageTimeStamps());
			}
			else
			{
				getFRAPParametersPanel().updateSavedParameters(null, null);
			}
			getResultsSummaryPanel().getReactionDiffusionPanel().updateSavedParameters(null);
			getResultsSummaryPanel().getPureDiffusionPanel().updateSavedParameters(null);
		}*/
	}
	
	public Wizard getLoadFRAPDataWizard()
	{   // single/multipanel fires property change to frapstudyPanel after loaded a new exp dataset
		// it also fires property change to summaryPanel to varify info and modify frapstudy in frapstudypanel
		// then summarypanel fires varify change to frapstudypanel to set frapstudy(already changed in frapstudypanel when passing as paramter to 
		// summarypanel) to frapdatapanel.
		if(loadFRAPDataWizard == null)
		{
			loadFRAPDataWizard = new Wizard(JOptionPane.getFrameForComponent(this));
			loadFRAPDataWizard.getDialog().setTitle("Load FRAP Data");
	        
	        WizardPanelDescriptor fTypeDescriptor = new LoadFRAPData_FileTypeDescriptor();
	        fTypeDescriptor.setNextPanelDescriptorID(LoadFRAPData_SingleFileDescriptor.IDENTIFIER); //goes next to single file input by default
	        loadFRAPDataWizard.registerWizardPanel(LoadFRAPData_FileTypeDescriptor.IDENTIFIER, fTypeDescriptor);
	        
	        WizardPanelDescriptor singleFileDescriptor = new LoadFRAPData_SingleFileDescriptor();
	        loadFRAPDataWizard.registerWizardPanel(LoadFRAPData_SingleFileDescriptor.IDENTIFIER, singleFileDescriptor);
	        ((LoadFRAPData_SingleFileDescriptor)singleFileDescriptor).setFrapWorkspace(getFrapWorkspace());
	
	        WizardPanelDescriptor multiFileDescriptor = new LoadFRAPData_MultiFileDescriptor();
	        loadFRAPDataWizard.registerWizardPanel(LoadFRAPData_MultiFileDescriptor.IDENTIFIER, multiFileDescriptor);
	        ((LoadFRAPData_MultiFileDescriptor)multiFileDescriptor).setFrapWorkspace(getFrapWorkspace());
	        
	        LoadFRAPData_SummaryDescriptor fSummaryDescriptor = new LoadFRAPData_SummaryDescriptor();
	        fSummaryDescriptor.setBackPanelDescriptorID(LoadFRAPData_SingleFileDescriptor.IDENTIFIER); //goes back to single file input by default
	        loadFRAPDataWizard.registerWizardPanel(LoadFRAPData_SummaryDescriptor.IDENTIFIER, fSummaryDescriptor);
	        fSummaryDescriptor.setFrapWorkspace(getFrapWorkspace());
	        
	        final WizardPanelDescriptor fileTypeDescriptor =  fTypeDescriptor;
	        final WizardPanelDescriptor fileSummaryDescriptor = fSummaryDescriptor;
	        //actionListener to single file input radio button
	        //this radio button affects the wizard series. especially on the next of file type and the back of summary 
	        ((LoadFRAPData_FileTypePanel)fTypeDescriptor.getPanelComponent()).getSingleFileButton().addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) 
	        	{
	        		if(e.getSource() instanceof JRadioButton)
	        		{
	        			if(((JRadioButton)e.getSource()).isSelected())
	        			{
	        				fileTypeDescriptor.setNextPanelDescriptorID(LoadFRAPData_SingleFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(LoadFRAPData_SingleFileDescriptor.IDENTIFIER);
	        			}
	        			else
	        			{
	        				fileTypeDescriptor.setNextPanelDescriptorID(LoadFRAPData_MultiFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(LoadFRAPData_MultiFileDescriptor.IDENTIFIER);
	        			}
	        		}
				}
	        	
	        });
	        //actionListener to multiple file input radio button
	        //this radio button affects the wizard series. especially on the next of file type and the back of summary
	        ((LoadFRAPData_FileTypePanel)fTypeDescriptor.getPanelComponent()).getMultipleFileButton().addActionListener(new ActionListener(){
	        	public void actionPerformed(ActionEvent e) 
	        	{
	        		if(e.getSource() instanceof JRadioButton)
	        		{
	        			if(((JRadioButton)e.getSource()).isSelected())
	        			{
	        				fileTypeDescriptor.setNextPanelDescriptorID(LoadFRAPData_MultiFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(LoadFRAPData_MultiFileDescriptor.IDENTIFIER);
	        			}
	        			else
	        			{
	        				fileTypeDescriptor.setNextPanelDescriptorID(LoadFRAPData_SingleFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(LoadFRAPData_SingleFileDescriptor.IDENTIFIER);
	        			}
	        		}
				}
	        	
	        });
		}
		loadFRAPDataWizard.setCurrentPanel(LoadFRAPData_FileTypeDescriptor.IDENTIFIER);//always start from the first page
        return loadFRAPDataWizard;
	}
	
	public Wizard getDefineROIWizard()
	{
		if(defineROIWizard == null)
		{
			//use one panel for all the steps.
			imgPanel = new DefineROI_Panel();
			imgPanel.setFrapWorkspace(getFrapWorkspace());
		
			defineROIWizard = new Wizard(JOptionPane.getFrameForComponent(this));
			defineROIWizard.getDialog().setTitle("Define ROIs");
	        
			DefineROI_CropDescriptor cropDescriptor = new DefineROI_CropDescriptor(imgPanel);
	        defineROIWizard.registerWizardPanel(DefineROI_CropDescriptor.IDENTIFIER, cropDescriptor);
	        
	        DefineROI_CellROIDescriptor cellROIDescriptor = new DefineROI_CellROIDescriptor(imgPanel);
	        defineROIWizard.registerWizardPanel(DefineROI_CellROIDescriptor.IDENTIFIER, cellROIDescriptor);
	
	        DefineROI_BleachedROIDescriptor bleachedROIDescriptor = new DefineROI_BleachedROIDescriptor(imgPanel);
	        defineROIWizard.registerWizardPanel(DefineROI_BleachedROIDescriptor.IDENTIFIER, bleachedROIDescriptor);
	        
	        DefineROI_BackgroundROIDescriptor backgroundROIDescriptor = new DefineROI_BackgroundROIDescriptor(imgPanel);
	        defineROIWizard.registerWizardPanel(DefineROI_BackgroundROIDescriptor.IDENTIFIER, backgroundROIDescriptor);
	        
	        DefineROI_SummaryDescriptor ROISummaryDescriptor = new DefineROI_SummaryDescriptor(imgPanel);
	        defineROIWizard.registerWizardPanel(DefineROI_SummaryDescriptor.IDENTIFIER, ROISummaryDescriptor);
	        ROISummaryDescriptor.setFrapWorkspace(getFrapWorkspace());
		}
		//always start from the first page
		defineROIWizard.setCurrentPanel(DefineROI_CropDescriptor.IDENTIFIER);
		imgPanel.refreshUI();
	
        return defineROIWizard;
	}
	
	public Wizard getChooseModelTypeWizard()
	{
		if(modelTypeWizard == null)
		{
			modelTypeWizard = new Wizard(JOptionPane.getFrameForComponent(this));
			modelTypeWizard.getDialog().setTitle("Choose Model Type");
	        
	        WizardPanelDescriptor modelTypesDescriptor = new ChooseModel_ModelTypesDescriptor();
	        modelTypeWizard.registerWizardPanel(ChooseModel_ModelTypesDescriptor.IDENTIFIER, modelTypesDescriptor);
	        ((ChooseModel_ModelTypesDescriptor)modelTypesDescriptor).setFrapWorkspace(getFrapWorkspace());
	        
	        WizardPanelDescriptor roiForErrorDescriptor = new ChooseModel_RoiForErrorDescriptor();
	        modelTypeWizard.registerWizardPanel(ChooseModel_RoiForErrorDescriptor.IDENTIFIER, roiForErrorDescriptor);
	        ((ChooseModel_RoiForErrorDescriptor)roiForErrorDescriptor).setFrapWorkspace(getFrapWorkspace());
		}
		//always start from the first page
		modelTypeWizard.setCurrentPanel(ChooseModel_ModelTypesDescriptor.IDENTIFIER);
        return modelTypeWizard;
	}

	public Wizard getEstimateParametersWizard()
	{
		FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
		if(fStudy == null || fStudy.getSelectedModels().size() ==0)
		{
			return null;
		}
		else
		{
			if(estimateParamWizard == null) 
			{
				estimateParamWizard = new Wizard(JOptionPane.getFrameForComponent(this));
				estimateParamWizard.getDialog().setTitle("Estimate Parameters");
				//initialize the wizard panels
				diffOneDescriptor = new EstParams_OneDiffComponentDescriptor();
				diffOneDescriptor.setFrapWorkspace(getFrapWorkspace());
				diffTwoDescriptor = new EstParams_TwoDiffComponentDescriptor();
				diffTwoDescriptor.setFrapWorkspace(getFrapWorkspace());
				
				bindingDescriptor = new EstParams_ReacBindingDescriptor();
				bindingDescriptor.setFrapWorkspace(getFrapWorkspace());
				bindingDescriptor.setLocalWorkspace(getLocalWorkspace());
				compareResultsDescriptor = new EstParams_CompareResultsDescriptor();
				compareResultsDescriptor.setFrapWorkspace(getFrapWorkspace());
			}
				
			//Dynamicly link the wizard pages according to the selected models.
			estimateParamWizard.clearAllPanels(); //clear all first
			if(fStudy.getSelectedModels().size() == 1)
			{
				if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null)
				{
			        estimateParamWizard.registerWizardPanel(EstParams_OneDiffComponentDescriptor.IDENTIFIER, diffOneDescriptor);
			        diffOneDescriptor.setNextPanelDescriptorID(EstParams_CompareResultsDescriptor.IDENTIFIER);
			        diffOneDescriptor.setBackPanelDescriptorID(null);
			        
			        //the last one should always be the compare page	        
			        estimateParamWizard.registerWizardPanel(EstParams_CompareResultsDescriptor.IDENTIFIER, compareResultsDescriptor);
			        compareResultsDescriptor.setBackPanelDescriptorID(EstParams_OneDiffComponentDescriptor.IDENTIFIER);
				}
				else if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
				{
			        estimateParamWizard.registerWizardPanel(EstParams_TwoDiffComponentDescriptor.IDENTIFIER, diffTwoDescriptor);
			        diffTwoDescriptor.setBackPanelDescriptorID(null);
			        diffTwoDescriptor.setNextPanelDescriptorID(EstParams_CompareResultsDescriptor.IDENTIFIER);
			        
			        //the last one should always be the compare page	        
			        estimateParamWizard.registerWizardPanel(EstParams_CompareResultsDescriptor.IDENTIFIER, compareResultsDescriptor);
			        compareResultsDescriptor.setBackPanelDescriptorID(EstParams_TwoDiffComponentDescriptor.IDENTIFIER);
				}
				else if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_BINDING] != null)
				{
			        estimateParamWizard.registerWizardPanel(EstParams_ReacBindingDescriptor.IDENTIFIER, bindingDescriptor);
			        bindingDescriptor.setBackPanelDescriptorID(null);
			        bindingDescriptor.setNextPanelDescriptorID(EstParams_CompareResultsDescriptor.IDENTIFIER);
			       
			        //the last one should always be the compare page	        
			        estimateParamWizard.registerWizardPanel(EstParams_CompareResultsDescriptor.IDENTIFIER, compareResultsDescriptor);
			        compareResultsDescriptor.setBackPanelDescriptorID(EstParams_ReacBindingDescriptor.IDENTIFIER);
				}
			}
			else if(fStudy.getSelectedModels().size() == 2)
			{
				if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null &&
				   fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
				{
			        estimateParamWizard.registerWizardPanel(EstParams_OneDiffComponentDescriptor.IDENTIFIER, diffOneDescriptor);
			        diffOneDescriptor.setBackPanelDescriptorID(null);
			        diffOneDescriptor.setNextPanelDescriptorID(EstParams_TwoDiffComponentDescriptor.IDENTIFIER);
			        estimateParamWizard.registerWizardPanel(EstParams_TwoDiffComponentDescriptor.IDENTIFIER, diffTwoDescriptor);
			        diffTwoDescriptor.setBackPanelDescriptorID(EstParams_OneDiffComponentDescriptor.IDENTIFIER);
			        diffTwoDescriptor.setNextPanelDescriptorID(EstParams_CompareResultsDescriptor.IDENTIFIER);
			        
  			        //the last one should always be the compare page	        
			        estimateParamWizard.registerWizardPanel(EstParams_CompareResultsDescriptor.IDENTIFIER, compareResultsDescriptor);
			        compareResultsDescriptor.setBackPanelDescriptorID(EstParams_TwoDiffComponentDescriptor.IDENTIFIER);
				}
				else if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null &&
						fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_BINDING] != null)
				{
			        estimateParamWizard.registerWizardPanel(EstParams_OneDiffComponentDescriptor.IDENTIFIER, diffOneDescriptor);
			        diffOneDescriptor.setBackPanelDescriptorID(null);
			        diffOneDescriptor.setNextPanelDescriptorID(EstParams_ReacBindingDescriptor.IDENTIFIER);
			        estimateParamWizard.registerWizardPanel(EstParams_ReacBindingDescriptor.IDENTIFIER, bindingDescriptor);
			        bindingDescriptor.setBackPanelDescriptorID(EstParams_OneDiffComponentDescriptor.IDENTIFIER);
			        bindingDescriptor.setNextPanelDescriptorID(EstParams_CompareResultsDescriptor.IDENTIFIER);
			        
			        //the last one should always be the compare page	        
			        estimateParamWizard.registerWizardPanel(EstParams_CompareResultsDescriptor.IDENTIFIER, compareResultsDescriptor);
			        compareResultsDescriptor.setBackPanelDescriptorID(EstParams_ReacBindingDescriptor.IDENTIFIER);
				}
				else if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null &&
						fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_BINDING] != null)
				{
			        estimateParamWizard.registerWizardPanel(EstParams_TwoDiffComponentDescriptor.IDENTIFIER, diffTwoDescriptor);
			        diffTwoDescriptor.setBackPanelDescriptorID(null);
			        diffTwoDescriptor.setNextPanelDescriptorID(EstParams_ReacBindingDescriptor.IDENTIFIER);
			        estimateParamWizard.registerWizardPanel(EstParams_ReacBindingDescriptor.IDENTIFIER, bindingDescriptor);
			        bindingDescriptor.setBackPanelDescriptorID(EstParams_TwoDiffComponentDescriptor.IDENTIFIER);
			        bindingDescriptor.setNextPanelDescriptorID(EstParams_CompareResultsDescriptor.IDENTIFIER);
			       
			        //the last one should always be the compare page	        
			        estimateParamWizard.registerWizardPanel(EstParams_CompareResultsDescriptor.IDENTIFIER, compareResultsDescriptor);
			        compareResultsDescriptor.setBackPanelDescriptorID(EstParams_ReacBindingDescriptor.IDENTIFIER);
				}
			}
			else if(fStudy.getSelectedModels().size() == 3)
			{
		        estimateParamWizard.registerWizardPanel(EstParams_OneDiffComponentDescriptor.IDENTIFIER, diffOneDescriptor);
		        diffOneDescriptor.setBackPanelDescriptorID(null);
		        diffOneDescriptor.setNextPanelDescriptorID(EstParams_TwoDiffComponentDescriptor.IDENTIFIER);
		        
		        estimateParamWizard.registerWizardPanel(EstParams_TwoDiffComponentDescriptor.IDENTIFIER, diffTwoDescriptor);
		        diffTwoDescriptor.setBackPanelDescriptorID(EstParams_OneDiffComponentDescriptor.IDENTIFIER);
		        diffTwoDescriptor.setNextPanelDescriptorID(EstParams_ReacBindingDescriptor.IDENTIFIER);
		        
		        estimateParamWizard.registerWizardPanel(EstParams_ReacBindingDescriptor.IDENTIFIER, bindingDescriptor);
		        bindingDescriptor.setBackPanelDescriptorID(EstParams_TwoDiffComponentDescriptor.IDENTIFIER);
		        bindingDescriptor.setNextPanelDescriptorID(EstParams_CompareResultsDescriptor.IDENTIFIER);
		        
		        //the last one should always be the compare page	        
		        estimateParamWizard.registerWizardPanel(EstParams_CompareResultsDescriptor.IDENTIFIER, compareResultsDescriptor);
		        compareResultsDescriptor.setBackPanelDescriptorID(EstParams_ReacBindingDescriptor.IDENTIFIER);
			}
			
			//each time when load the wizard, it should start from the first possible page.
			if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null)
			{
				estimateParamWizard.setCurrentPanel(EstParams_OneDiffComponentDescriptor.IDENTIFIER);
				return estimateParamWizard;
			}
			else if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
			{
				estimateParamWizard.setCurrentPanel(EstParams_TwoDiffComponentDescriptor.IDENTIFIER);
				return estimateParamWizard;
			}
			else if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_BINDING] != null)
			{
				estimateParamWizard.setCurrentPanel(EstParams_ReacBindingDescriptor.IDENTIFIER);
				return estimateParamWizard;
			}
			else //this should not happen though
			{
				return estimateParamWizard;
			}
		}
	}

	//update workflow stage after .vfrap file is loaded
	private void updateWorkflowStage(FRAPStudy frapStudy) 
	{
		if(frapStudy.getSelectedModels() != null && frapStudy.getSelectedModels().size() > 0 &&
		   frapStudy.getSelectedROIsForErrorCalculation() != null && frapStudy.getSelectedROIsForErrorCalculation().length > 0)
		{
			getAnalysisProcedurePanel().setWorkFlowStage(AnalysisProcedurePanel.STAGE_EST_PARAMS);
		}
		else if(frapStudy.getStartingIndexForRecovery() != null)
		{
			getAnalysisProcedurePanel().setWorkFlowStage(AnalysisProcedurePanel.STAGE_MODEL_TYPE);
		}
		else if(frapStudy != null && frapStudy.getFrapData() != null && frapStudy.getFrapData().getImageDataset() != null &&
		   frapStudy.getFrapData().getImageDataset().getAllImages().length > 0)
		{
			getAnalysisProcedurePanel().setWorkFlowStage(AnalysisProcedurePanel.STAGE_DEFINE_ROIS);
		}
	}
	
	private void updateAnalysisResult(FRAPStudy frapStudy)
	{
		Integer bestIdx = frapStudy.getBestModelIndex();
		if(bestIdx != null)
		{
			FRAPModel bestModel = frapStudy.getFrapModel(bestIdx);
			if(bestModel != null)
			{
				getAnalysisResultsPanel().setBestModel(bestIdx, getLocalWorkspace());
			}
		}
		else
		{
			getAnalysisResultsPanel().clearBestModel();
			getAnalysisResultsPanel().clearResultTable();
			getAnalysisResultsPanel().setRunSimButtonEnable(false);
			getAnalysisResultsPanel().setResultsButtonEnabled(false);
		}
	}
	
	public void setFRAPWorkspace(FRAPWorkspace arg_frapWorkspace) {
		FRAPWorkspace oldWorkspace = frapWorkspace;
		if(oldWorkspace != null)
		{
			oldWorkspace.removePropertyChangeListener(this);
		}
		frapWorkspace = arg_frapWorkspace;
		frapWorkspace.addPropertyChangeListener(this);
		//set FRAPWorkspace to other panels that are initialized here 
		getFRAPDataPanel().setFRAPWorkspace(arg_frapWorkspace);
		getAnalysisResultsPanel().setFRAPWorkspace(arg_frapWorkspace);
	}
	
	public FRAPWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
}
