package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoableEdit;

import org.vcell.util.PropertyLoader;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.gui.chooseModelWizard.ModelTypesDescriptor;
import cbit.vcell.microscopy.batchrun.gui.chooseModelWizard.RoiForErrorDescriptor;
import cbit.vcell.microscopy.gui.AboutDialog;
import cbit.vcell.microscopy.gui.HelpViewer;
import cbit.vcell.microscopy.gui.PreferencePanel;
import cbit.vcell.microscopy.gui.StatusBar;
import cbit.vcell.microscopy.gui.ToolBar;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame.MenuHandler;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame.ToolBarHandler;
import cbit.vcell.microscopy.gui.estparamwizard.MSEPanel;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_MultiFilePanel;
import cbit.vcell.opt.Parameter;

/**
 * This the bottom container of virtual FRAP Batch Run 
 * which contains menu bar, FrapBatchRunPanel and status bar. 
 * @author Tracy LI
 * Created in Dec 2009.
 */
public class VirtualFrapBatchRunFrame extends JFrame 
{
	//the application has one local workspace and one FRAP workspace
	private LocalWorkspace localWorkspace = null;
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	
	private JSplitPane mainSplitPane = null;
	private BatchRunDetailsPanel detailsPanel = null;
	private BatchRunDisplayPanel displayPanel = null;
	
	public static final boolean SAVE_COMPRESSED = true;
	//divider location actually is relatively based on the top-left point(as 0,0) of the app window(not the screen window)
	public static final int iniDividerLocation = (VirtualFrapMainFrame.appHeight*2)/3;

	private BatchRunMenuHandler menuHandler = new BatchRunMenuHandler();
	
	private static final String BATCHRUN_VER_NUMBER = "VFRAP 1.0_Batch_Run";
	private static final String OPEN_ACTION_COMMAND = "Open vfbatch";
	private static final String SAVE_ACTION_COMMAND = "Save";
	private static final String SAVEAS_ACTION_COMMAND = "Save As...";
	private static final String CLOSE_ACTION_COMMAND = "Close";
	private static final String HELPTOPICS_ACTION_COMMAND = "Help Topics";
	private static final String ABOUT_ACTION_COMMAND = "About Virtual Frap";
	private static final String VIEW_JOB_ACTION_COMMAND = "Job Status Panel";
//	private static final String MAIN_WINDOW_ACTION_COMMAND = "Main Window";
	
	private static final JMenuItem menuOpen= new JMenuItem(OPEN_ACTION_COMMAND,'O');
	private static final JMenuItem menuClose= new JMenuItem(CLOSE_ACTION_COMMAND,'C');
	private static final JMenuItem msave = new JMenuItem(SAVE_ACTION_COMMAND,'S');
	private static final JMenuItem msaveas = new JMenuItem(SAVEAS_ACTION_COMMAND);
	private static final JMenuItem mHelpTopics = new JMenuItem(HELPTOPICS_ACTION_COMMAND);
	private static final JMenuItem mabout = new JMenuItem(ABOUT_ACTION_COMMAND);
	private static final JCheckBoxMenuItem mViewJob = new JCheckBoxMenuItem(VIEW_JOB_ACTION_COMMAND);
//	private static final JMenuItem mMainWin = new JMenuItem(MAIN_WINDOW_ACTION_COMMAND);

	public static JMenuBar mb = null;
	private static StatusBar statusBarNew = new StatusBar();
	public static ToolBar toolBar = null;
	private HelpViewer hviewer = null;
	  
	//modeltype wizard
	private Wizard modelTypeWizard = null;
	
    public class BatchRunMenuHandler implements ActionListener
	{
    	private void saveAndSaveAs(final boolean bSaveAs)
		{
	    	if(bSaveAs)
	    	{
//	    		AsynchClientTask[] saveAsTasks = frapStudyPanel.saveAs();
//	    		ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), saveAsTasks, true);
	    	}else{
//	    		AsynchClientTask[] saveTasks = frapStudyPanel.save();
//	    		ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), saveTasks, true);
	    		  
	    	}
        }
	 	public void actionPerformed(ActionEvent e) 
	 	{
			if(e.getSource() instanceof JMenuItem)
		    {
				String arg=e.getActionCommand();
				// file menu
				if(arg.equals(OPEN_ACTION_COMMAND))
			    {
					File inputFile = null;
//		  			int option = VirtualFrapLoader.openVFRAPFileChooser.showOpenDialog(frapStudyPanel);
//		  			if (option == JFileChooser.APPROVE_OPTION){
//		  				inputFile = VirtualFrapLoader.openVFRAPFileChooser.getSelectedFile();
//		  			}else{
//		  				return;
//		  			}
//		  			  
//		  	  		AsynchClientTask[] openTasks = frapStudyPanel.open(inputFile);
//		    		ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), openTasks, true);
			    }
				else if(arg.equals(SAVE_ACTION_COMMAND))
			    {
					saveAndSaveAs(false);
			    }
				else if(arg.equals(SAVEAS_ACTION_COMMAND))
				{
					saveAndSaveAs(true);
				}
				else if(arg.equals(CLOSE_ACTION_COMMAND))
				{
					String text = "";
					if (batchRunWorkspace != null)//TODO: need to check save flag
					{
						text = "UnSaved changes will be lost!";
					}
					String result = DialogUtils.showWarningDialog(VirtualFrapBatchRunFrame.this, "Do you want to CLOSE Virtual Frap Batch Run? " + text, new String[]{UserMessage.OPTION_CLOSE, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CLOSE); 
					if (result == UserMessage.OPTION_CLOSE)
					{
						VirtualFrapBatchRunFrame.this.dispose();
					}
				}
				else if(arg.equals(VIEW_JOB_ACTION_COMMAND))
				{
					if(mViewJob.isSelected())
					{
						getBatchRunDisplayPanel().showJobStatusPanel();
					}
					else
					{
						getBatchRunDisplayPanel().hideJobStatusPanel();
					}
				}
				// Help menu
				else if(arg.equals(HELPTOPICS_ACTION_COMMAND))
				{
					if(hviewer == null)
					{
						hviewer = new HelpViewer();
					}
					else
					{
						hviewer.setVisible(true);
					}
				}
				else if(arg.equals(ABOUT_ACTION_COMMAND))
				{
					new AboutDialog(getClass().getResource("/images/splash.jpg"), VirtualFrapBatchRunFrame.this);
				}
//				else if(arg.equals(MAIN_WINDOW_ACTION_COMMAND))
//				{
//					System.out.println("MAIN_WINDOW_ACTION_COMMAND command clicked.");
//				}
		    }
	 	}
	}// end of inner class BatchRunMenuHandler
	  
    //Inner class ToolBarHandler
    public class ToolBarHandler implements ActionListener
    {
    	public void actionPerformed(ActionEvent e) 
    	{
    		if(e.getSource() instanceof JButton)
    		{
    			int index = toolBar.findIndex(((JButton)e.getSource()));
    			switch(index)
    			{
    				case ToolBar.BUT_OPEN:
    					menuHandler.actionPerformed(new ActionEvent(menuOpen,0,OPEN_ACTION_COMMAND));
    					break;
		  	   		case ToolBar.BUT_SAVE:
		  	   			try {
		  	   				menuHandler.actionPerformed(new ActionEvent(msave,0,SAVE_ACTION_COMMAND));
		  	   			}catch(Exception e5){
		  	   				DialogUtils.showErrorDialog(VirtualFrapBatchRunFrame.this, "Exception: " + e5.getMessage());
		  	   				updateStatus("Exception: " + e5.getMessage());
		  	   			}
		  	   			break;
		  	   		case ToolBar.BUT_HELP:
		  	   			menuHandler.actionPerformed(new ActionEvent(mHelpTopics,0,HELPTOPICS_ACTION_COMMAND));
		  	   			break;
			  	   	case ToolBar.BUT_RUN:
			  	   		
				  	   	final Wizard typeWizard = getChooseModelTypeWizard();
			   			if(typeWizard != null)
			   			{
			   				typeWizard.showModalDialog(new Dimension(550,420));
			   				if(typeWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
			   				{
			   					//create and run the batch files
			   					ArrayList<AsynchClientTask> batchRunTaskList = getBatchRunTasks();
								//generate results
			   					
			   					
			   					AsynchClientTask displayTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
			   					{
			   						public void run(Hashtable<String, Object> hashTable) throws Exception
			   						{
			   							getBatchRunDetailsPanel().updateResultsInfo(true);
			   						}
			   					};
			   					
//			   					batchRunTaskList.add(arg0);
			   					batchRunTaskList.add(displayTask);
								//run tasks
			   					AsynchClientTask[] tasks = batchRunTaskList.toArray(new AsynchClientTask[batchRunTaskList.size()]);
								ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), tasks, true, false, false, null, true);
							}
			   			}
//			  	   		MessagePanel msgPanel = new MessagePanel("Start running c:\\VirtualMicroscopy\\test2.vfrap", false);
//			  	   		getBatchRunDisplayPanel().getJobStatusPanel().appendMessage(msgPanel);
//			  	   		
//			  	   		msgPanel = new MessagePanel("Saving information", true);
//			  	   		getBatchRunDisplayPanel().getJobStatusPanel().appendMessage(msgPanel);
//			  	   		int progCounter = 0;
//			  	   		msgPanel.setProgress(10);
			  	   		
//			  	   		msgPanel.setProgressCompleted();
		  	   			break;
		  	   		default:
		                break;
		  	   		}
			    }
		    }
	  }// end of inner class ToolBarHandler
	  
	  
	  // constructor
	  public VirtualFrapBatchRunFrame(LocalWorkspace localWorkspace, FRAPBatchRunWorkspace batchRunWorkspace)
	  {
	    super();
	    this.localWorkspace = localWorkspace;
	    this.batchRunWorkspace = batchRunWorkspace;
	    
	    setIconImage(new ImageIcon(getClass().getResource("/images/logo.gif")).getImage());
	    //initialize components
	    initiateComponents();
	    SetupMenus();
	    enableSave(false);
	    
	    //set window size
	    setSize(VirtualFrapMainFrame.appWidth, VirtualFrapMainFrame.appHeight);
	    setLocation(VirtualFrapMainFrame.iniFrameLocX, VirtualFrapMainFrame.iniFrameLocY);
	    updateStatus("Virtual Frap batch run interface.");
		
	    //to handle the close button of the frame
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    addWindowListener(createAppCloser());
	  }// end of constructor

	  public static void updateStatus(final String newStatusMessage){
		  SwingUtilities.invokeLater(new Runnable(){public void run(){statusBarNew.showStatus(newStatusMessage);}});
	  }
	  public static void updateProgress(final int percentProgress){
		  SwingUtilities.invokeLater(new Runnable(){public void run(){statusBarNew.showProgress(percentProgress);}});	  
	  }
	  
	  public static void enableSave(boolean bEnable){
		  msave.setEnabled(bEnable);
		  msaveas.setEnabled(bEnable);
		  toolBar.getButtons()[ToolBar.BUT_SAVE].setEnabled(bEnable);
	  }
	  /**
	   * Initiation of the UI components that is shown in the main window
	   */
	  protected void initiateComponents()
	  {
	      toolBar = new ToolBar();
	      ToolBarHandler th = new ToolBarHandler();
	      toolBar.addToolBarHandler(th);
	      mb = new JMenuBar();

	      
		  System.setProperty(PropertyLoader.localSimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
		  System.setProperty(PropertyLoader.secondarySimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
		  System.setProperty(PropertyLoader.exportBaseDirProperty, localWorkspace.getDefaultSimDataDirectory());
	      System.setProperty(PropertyLoader.exportBaseURLProperty, "file://"+localWorkspace.getDefaultSimDataDirectory());

	      
	      //add components to the main frame
	      getContentPane().setLayout(new BorderLayout());
	      getContentPane().add(toolBar, BorderLayout.NORTH);
	      getContentPane().add(statusBarNew, BorderLayout.SOUTH);
	      getContentPane().add(getMainSplitPane());
	  }

	  public JSplitPane getMainSplitPane()
	  {
		  if(mainSplitPane == null)
		  {
			  mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getBatchRunDetailsPanel(), getBatchRunDisplayPanel());
			  mainSplitPane.setDividerLocation(300);
			  mainSplitPane.setDividerSize(2);
		  }
		  return mainSplitPane;
	  }
	  
	  public BatchRunDetailsPanel getBatchRunDetailsPanel()
	  {
		  if(detailsPanel == null)
		  {
			  detailsPanel = new BatchRunDetailsPanel();
			  detailsPanel.setBatchRunWorkspace(batchRunWorkspace);
			  detailsPanel.setLocalWorkspace(localWorkspace);
		  }
		  return detailsPanel;
	  }
	  
	  public BatchRunDisplayPanel getBatchRunDisplayPanel()
	  {
		  if(displayPanel == null)
		  {
			  displayPanel = new BatchRunDisplayPanel(this);
			  displayPanel.setBatchRunWorkspace(batchRunWorkspace);
		  }
		  return displayPanel;
	  }
	  
	  /**
	   * set up menus in the main frame
	   */
	  private void SetupMenus()
	  {
	    // File Menu
	    JMenu fileMenu =new JMenu("File");
	    fileMenu.setMnemonic('F');
	    mb.add(fileMenu);

	    menuOpen.addActionListener(menuHandler);
	    fileMenu.add(menuOpen);
	    menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));

	    msave.addActionListener(menuHandler);
	    fileMenu.add(msave);
	    msave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));

	    msaveas.addActionListener(menuHandler);
	    fileMenu.add(msaveas);

	    fileMenu.addSeparator();
	    
	    menuClose.addActionListener(menuHandler);
	    fileMenu.add(menuClose);

	    //View Menu
	    JMenu viewMenu =new JMenu("View");
	    viewMenu.setMnemonic('V');
	    mb.add(viewMenu);

	    mViewJob.setSelected(true);
	    mViewJob.addActionListener(menuHandler);
	    viewMenu.add(mViewJob);
	    
	    //Help Menu
	    JMenu helpMenu =new JMenu("Help");
	    helpMenu.setMnemonic('H');
	    mb.add(helpMenu);


	    mHelpTopics.addActionListener(menuHandler);
	    helpMenu.add(mHelpTopics);
	    
	    helpMenu.addSeparator();
	    
	    mabout.addActionListener(menuHandler);
	    helpMenu.add(mabout);
	    
	    setJMenuBar(mb);
	  } // end of setup menu
	  
	  public void setViewJobMenuSelected(boolean isSelected)
	  {
		  mViewJob.setSelected(isSelected);
	  }
	  
	  /**
	  * Before shuting down the running application, a good
	  * implementation would at least check to see if a save
	  * is needed.
	  */
	  protected WindowAdapter createAppCloser()
	  {
	      return new AppCloser();
	  }

	  protected final class AppCloser extends WindowAdapter
	  {
	      public void windowClosing(WindowEvent e)
	      {
		  		menuHandler.actionPerformed(new ActionEvent(menuClose,0,CLOSE_ACTION_COMMAND));
		  }
	  }
	  
	  //setTitle overrides the orginal function in java.awt.Frame
	  //to show the software name and version.
	  public void setBatchRunTitle(String str)
	  {
		  if(str.equals(""))
		  {
			  this.setTitle(BATCHRUN_VER_NUMBER);
		  }
		  else
		  {
			  this.setTitle(str + " - " + BATCHRUN_VER_NUMBER);
		  }
	  }
	  
	  public Wizard getChooseModelTypeWizard()
	  {
	      if(modelTypeWizard == null)
		  {
			  modelTypeWizard = new Wizard(JOptionPane.getFrameForComponent(this));
			  modelTypeWizard.getDialog().setTitle("Choose Model Type");
	        
	          WizardPanelDescriptor modelTypesDescriptor = new ModelTypesDescriptor();
	          modelTypeWizard.registerWizardPanel(ModelTypesDescriptor.IDENTIFIER, modelTypesDescriptor);
	          ((ModelTypesDescriptor)modelTypesDescriptor).setBatchRunWorkspace(batchRunWorkspace);
	        
	          WizardPanelDescriptor roiForErrorDescriptor = new RoiForErrorDescriptor();
	          modelTypeWizard.registerWizardPanel(RoiForErrorDescriptor.IDENTIFIER, roiForErrorDescriptor);
	          ((RoiForErrorDescriptor)roiForErrorDescriptor).setBatchRunWorkspace(batchRunWorkspace);
		  }
		  //always start from the first page
		  modelTypeWizard.setCurrentPanel(ModelTypesDescriptor.IDENTIFIER);
          return modelTypeWizard;
	  }
	  
	  private MessagePanel appendJobStatus(String msg, boolean bProgress)
	  {
		  MessagePanel msgPanel = new MessagePanel(msg, bProgress);
		  getBatchRunDisplayPanel().getJobStatusPanel().appendMessage(msgPanel);
		  return msgPanel;
	  }
	  
	  private ArrayList<AsynchClientTask> getBatchRunTasks()
	  {
		  //to run batch files
			ArrayList<AsynchClientTask> batchRunTaskList = new ArrayList<AsynchClientTask>();
			for(int i=0; i<batchRunWorkspace.getFrapStudyList().size(); i++)
			{
				final int finalIdx = i;
				AsynchClientTask message1Task = new AsynchClientTask("Preparing for parameter estimation ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						appendJobStatus("Running "+((FRAPStudy)batchRunWorkspace.getFrapStudyList().get(finalIdx)).getXmlFilename(), false);
					}
				};
				
				AsynchClientTask saveTask = new AsynchClientTask("Preparing for parameter estimation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = ((FRAPStudy)batchRunWorkspace.getFrapStudyList().get(finalIdx));
//						
						//reference data is null, it is not stored, we have to run ref simulation then
						//check external data info
						if(!FRAPWorkspace.areExternalDataOK(localWorkspace,fStudy.getFrapDataExternalDataInfo(), fStudy.getRoiExternalDataInfo()))
						{
							//if external files are missing/currupt or ROIs are changed, create keys and save them
							fStudy.setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
							fStudy.setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
							fStudy.saveROIsAsExternalData(localWorkspace, fStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
							fStudy.saveImageDatasetAsExternalData(localWorkspace, fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
						}
					}
				};
				
				AsynchClientTask message2Task = new AsynchClientTask("Running reference simulation ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						MessagePanel msgPanel = appendJobStatus("Running reference simulation ...", true);
						hashTable.put("runRefStatus", msgPanel);
					}
				};
				
				AsynchClientTask runRefSimTask = new AsynchClientTask("Running reference simulation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = ((FRAPStudy)batchRunWorkspace.getFrapStudyList().get(finalIdx));
						MessagePanel msgPanel = (MessagePanel)hashTable.get("runRefStatus");
						//run ref sim
						fStudy.setFrapOptData(new FRAPOptData(fStudy, FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, localWorkspace, msgPanel));
					}
				};
				
				AsynchClientTask message3Task = new AsynchClientTask("Running reference simulation ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						MessagePanel msgPanel = (MessagePanel)hashTable.get("runRefStatus");
						msgPanel.setProgress(100);
						msgPanel.setProgressCompleted();
						
						MessagePanel msgPanel1 = appendJobStatus("Running optimization ...", true);
						hashTable.put("optimizationStatus", msgPanel1);
					}
				};
				
				AsynchClientTask runOptTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = ((FRAPStudy)batchRunWorkspace.getFrapStudyList().get(finalIdx));
						
						ArrayList<Integer> models = fStudy.getSelectedModels();
						if(models.size() == 1)
						{
							for(int i = 0; i<models.size(); i++)
							{
								int model = ((Integer)models.get(i)).intValue();
								if(model == batchRunWorkspace.getSelectedModel())
								{
									if(model == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
									{
										if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() == null)
										{
											fStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF);
											Parameter[] initialParams = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]);
											//set model parameters and data
											Parameter[] bestParameters = fStudy.getFrapOptData().getBestParamters(initialParams, fStudy.getSelectedROIsForErrorCalculation());
											double[][] fitData = fStudy.getFrapOptData().getFitData(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].setModelParameters(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].setData(fitData);
										}
										
										
									}
									else if(model == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
									{
										if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() == null)
										{
											fStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF);
											Parameter[] initialParams = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]);
											//set model parameters and data
											Parameter[] bestParameters = fStudy.getFrapOptData().getBestParamters(initialParams, fStudy.getSelectedROIsForErrorCalculation());
											double[][] fitData = fStudy.getFrapOptData().getFitData(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].setModelParameters(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].setData(fitData);
										}
									}
								}
								else{
									throw new Exception("Selected model for batch run is not the same as selected model in FRAP doc " + fStudy.getXmlFilename());
								}
							}
						}
						else{
							throw new Exception("Selected model size exceed 1");
						}
					}
				};
				
				AsynchClientTask message4Task = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						MessagePanel msgPanel = (MessagePanel)hashTable.get("optimizationStatus");
						msgPanel.setProgressCompleted();
					}
				};
				
				batchRunTaskList.add(message1Task);
				batchRunTaskList.add(saveTask);
				batchRunTaskList.add(message2Task);
				batchRunTaskList.add(runRefSimTask);
				batchRunTaskList.add(message3Task);
				batchRunTaskList.add(runOptTask);
				batchRunTaskList.add(message4Task);
			}
			
			return batchRunTaskList;
	  }
}
