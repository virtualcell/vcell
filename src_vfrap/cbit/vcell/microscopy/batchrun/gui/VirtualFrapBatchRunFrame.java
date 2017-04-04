/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.documentation.VcellHelpViewer;
import org.vcell.optimization.ProfileData;
import org.vcell.util.PropertyLoader;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptFunctions;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.gui.chooseModelWizard.ModelTypesDescriptor;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.StatusBar;
import cbit.vcell.microscopy.gui.ToolBar;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.opt.Parameter;

/**
 * This the bottom container of virtual FRAP Batch Run 
 * which contains menu bar, FrapBatchRunPanel and status bar. 
 * @author Tracy LI
 * Created in Dec 2009.
 */
@SuppressWarnings("serial")
public class VirtualFrapBatchRunFrame extends LWTopFrame implements DropTargetListener, TopLevelWindow
{
	//the application has one local workspace and one FRAP workspace
	private LocalWorkspace localWorkspace = null;
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	
	private ChildWindowManager childWindowManager;
	
	private JSplitPane mainSplitPane = null;
	private BatchRunDetailsPanel detailsPanel = null;
	private BatchRunDisplayPanel displayPanel = null;
	
	public static final boolean SAVE_COMPRESSED = true;
	//divider location actually is relatively based on the top-left point(as 0,0) of the app window(not the screen window)
	public static final int iniDividerLocation = (VirtualFrapMainFrame.APP_HEIGHT*2)/3;

	private BatchRunMenuHandler menuHandler = new BatchRunMenuHandler();
	
	private static final String NEW_ACTION_COMMAND = "New vfbatch";
	private static final String OPEN_ACTION_COMMAND = "Open vfbatch";
	private static final String SAVE_ACTION_COMMAND = "Save";
	private static final String SAVEAS_ACTION_COMMAND = "Save As...";
	private static final String CLOSE_ACTION_COMMAND = "Close";
	private static final String HELPTOPICS_ACTION_COMMAND = "Help Topics";
	private static final String ABOUT_ACTION_COMMAND = "About Virtual Frap";
	private static final String VIEW_JOB_ACTION_COMMAND = "Job Status Panel";
	
	private static final JMenuItem menuNew = new JMenuItem(NEW_ACTION_COMMAND,'N');
	private static final JMenuItem menuOpen= new JMenuItem(OPEN_ACTION_COMMAND,'O');
	private static final JMenuItem menuClose= new JMenuItem(CLOSE_ACTION_COMMAND,'C');
	private static final JMenuItem msave = new JMenuItem(SAVE_ACTION_COMMAND,'S');
	private static final JMenuItem msaveas = new JMenuItem(SAVEAS_ACTION_COMMAND);
	private static final JMenuItem mHelpTopics = new JMenuItem(HELPTOPICS_ACTION_COMMAND);
	private static final JMenuItem mabout = new JMenuItem(ABOUT_ACTION_COMMAND);
	private static final JCheckBoxMenuItem mViewJob = new JCheckBoxMenuItem(VIEW_JOB_ACTION_COMMAND);
	
	public static JMenuBar mb = null;
	private static StatusBar statusBarNew = new StatusBar();
	public static ToolBar toolBar = null;
	private VcellHelpViewer hviewer = null;
	  
	//for opening batch run file, may be changed later
	ArrayList<String> frapFileList = null;
	String openErrMsg = "";
	//modeltype wizard
	private Wizard modelTypeWizard = null;
	//for drag and drop action 
	@SuppressWarnings("unused")
	private DropTarget dt;
	
	private final String menuDesc;
	
    private class BatchRunMenuHandler implements ActionListener
	{
	 	public void actionPerformed(ActionEvent e) 
	 	{
			if(e.getSource() instanceof JMenuItem)
		    {
				String arg=e.getActionCommand();
				// file menu
				if(arg.equals(NEW_ACTION_COMMAND))
			    {
					getBatchRunDetailsPanel().deleteAllBatchrunDocs();
					batchRunWorkspace.setBatchRunXmlFileName(null);
					setBatchRunFrameTitle("");
					statusBarNew.showStatus("");
					enableSave(false);
			    }
				else if(arg.equals(OPEN_ACTION_COMMAND))
			    {
					File inputFile = null;
		  			int option = VirtualFrapLoader.openVFRAPBatchRunChooser.showOpenDialog(VirtualFrapBatchRunFrame.this);
		  			if (option == JFileChooser.APPROVE_OPTION){
		  				inputFile = VirtualFrapLoader.openVFRAPBatchRunChooser.getSelectedFile();
		  			}else{
		  				return;
		  			}
		  			  
		  	  		AsynchClientTask[] openTasks = open(inputFile);
		    		ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), openTasks, true);
			    }
				else if(arg.equals(SAVE_ACTION_COMMAND))
			    {
					AsynchClientTask[] saveTasks = save();
		    		ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), saveTasks, true);
			    }
				else if(arg.equals(SAVEAS_ACTION_COMMAND))
				{
					AsynchClientTask[] saveAsTasks = saveAs();
		    		ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), saveAsTasks, true);
				}
				else if(arg.equals(CLOSE_ACTION_COMMAND))
				{
					String text = "";
					if (batchRunWorkspace != null && batchRunWorkspace.isSaveNeeded())
					{
						text = "UnSaved changes will be lost!";
					}
					String result = DialogUtils.showWarningDialog(VirtualFrapBatchRunFrame.this, "Do you want to CLOSE Virtual Frap Batch Run? " + text, new String[]{UserMessage.OPTION_CLOSE, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CLOSE); 
					if (result == UserMessage.OPTION_CLOSE)
					{
						VirtualFrapBatchRunFrame.this.dispose();
						childWindowManager.closeAllChildWindows();
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
						hviewer = new VcellHelpViewer(VcellHelpViewer.VFRAP_DOC_URL);
					}
					else
					{
						hviewer.setVisible(true);
					}
				}
				else if(arg.equals(ABOUT_ACTION_COMMAND))
				{
					DocumentWindow.showAboutBox(VirtualFrapBatchRunFrame.this);
				}
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
    				case ToolBar.BUT_NEW:
    					menuHandler.actionPerformed(new ActionEvent(menuNew,0,NEW_ACTION_COMMAND));
					break;
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
			  	   		if(batchRunWorkspace.getFrapStudies().size() > 0)
			  	   		{
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
									ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), tasks, false, true, null, true);
								}
				   			}
				   			batchRunWorkspace.setSaveNeeded(true);
			  	   		}
			  	   		else
			  	   		{
				  	   		DialogUtils.showErrorDialog(VirtualFrapBatchRunFrame.this, "There is no Dataset for batch run.");
				    		throw new RuntimeException("There are no Datasets for batch run.");
			  	   		}
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
	    childWindowManager = new ChildWindowManager(this);
	    
	    setIconImage(new ImageIcon(getClass().getResource("/images/logo.gif")).getImage());
	    //initialize components
	    initiateComponents();
	    SetupMenus();
	    enableSave(false);
	    
	    //set window size
	    setSize(VirtualFrapMainFrame.APP_WIDTH, VirtualFrapMainFrame.APP_HEIGHT);
	    setLocation(VirtualFrapMainFrame.INI_FRAME_LOCX, VirtualFrapMainFrame.INI_FRAME_LOCY);
	    updateStatus("Virtual Frap batch run interface.");
		
	    //to handle the close button of the frame
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    addWindowListener(createAppCloser());
	    dt = new DropTarget(getMainSplitPane(), this);
	    menuDesc = nextSequentialDescription("Virtual Batch Frame");
	  }// end of constructor

	  @Override
	public String menuDescription() {
		return menuDesc; 
	}

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
	      
//		  System.setProperty(PropertyLoader.primarySimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
//		  System.setProperty(PropertyLoader.secondarySimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
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

	    menuNew.addActionListener(menuHandler);
	    fileMenu.add(menuNew);
	    menuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
	    
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
	    
	    mb.add( createWindowMenu(true) );
	    
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
	  
	  public Wizard getChooseModelTypeWizard()
	  {
	      if(modelTypeWizard == null)
		  {
			  modelTypeWizard = new Wizard(JOptionPane.getFrameForComponent(this));
			  modelTypeWizard.getDialog().setTitle("Choose Model Type");
	        
	          WizardPanelDescriptor modelTypesDescriptor = new ModelTypesDescriptor();
	          modelTypeWizard.registerWizardPanel(ModelTypesDescriptor.IDENTIFIER, modelTypesDescriptor);
	          ((ModelTypesDescriptor)modelTypesDescriptor).setBatchRunWorkspace(batchRunWorkspace);
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
			for(int i=0; i<batchRunWorkspace.getFrapStudies().size(); i++)
			{
				final int finalIdx = i;
				AsynchClientTask message1Task = new AsynchClientTask("Preparing for parameter estimation ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						appendJobStatus("<html><br>Running "+(batchRunWorkspace.getFrapStudies().get(finalIdx)).getXmlFilename()+"</html>", false);
					}
				};
				
				AsynchClientTask saveTask = new AsynchClientTask("Preparing for parameter estimation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = (batchRunWorkspace.getFrapStudies().get(finalIdx));
					
						if(fStudy.hasDiffusionOnlyModel())
						{
							//check external data info. If not existing,  we have to run ref simulation.
							if(!FRAPWorkspace.areExternalDataOK(localWorkspace,fStudy.getFrapDataExternalDataInfo(), fStudy.getRoiExternalDataInfo()))
							{
								//if external files are missing/currupt or ROIs are changed, create keys and save them
								fStudy.setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
								fStudy.setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
								fStudy.saveROIsAsExternalData(localWorkspace, fStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
								fStudy.saveImageDatasetAsExternalData(localWorkspace, fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
							}
						}
					}
				};
				
				AsynchClientTask message2Task = new AsynchClientTask("Running reference simulation ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = (batchRunWorkspace.getFrapStudies().get(finalIdx));
						
						if(fStudy.hasDiffusionOnlyModel())
						{ 
							MessagePanel msgPanel = appendJobStatus("Running reference simulation ...", true);
							hashTable.put("runRefStatus", msgPanel);
						}
						else if(fStudy.hasReactionOnlyOffRateModel())
						{
//							MessagePanel msgPanel = appendJobStatus("...", true);
//							hashTable.put("runRefStatus", msgPanel);
						}
					}
				};
				
				AsynchClientTask runRefSimTask = new AsynchClientTask("Running reference simulation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = (batchRunWorkspace.getFrapStudies().get(finalIdx));
						if(fStudy.hasDiffusionOnlyModel())
						{
							MessagePanel msgPanel = (MessagePanel)hashTable.get("runRefStatus");
							//run ref sim
							if(fStudy.getStoredRefData() != null)//if ref data is stored ,we don't have to re-run
							{
								fStudy.setFrapOptData(new FRAPOptData(fStudy, FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF, localWorkspace, fStudy.getStoredRefData()));
							}
							else
							{
								fStudy.setFrapOptData(new FRAPOptData(fStudy, FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF, localWorkspace, msgPanel));
							}
						}
						else if(fStudy.hasReactionOnlyOffRateModel())
						{
							if(fStudy.getFrapOptFunc() == null)
							{
								fStudy.setFrapOptFunc(new FRAPOptFunctions(fStudy));
							}
						}
					}
				};
				
				AsynchClientTask message3Task = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = (batchRunWorkspace.getFrapStudies().get(finalIdx));
						if(fStudy.hasDiffusionOnlyModel())
						{
							MessagePanel msgPanel = (MessagePanel)hashTable.get("runRefStatus");
							msgPanel.setProgressCompleted();
						}
						MessagePanel msgPanel1 = appendJobStatus("Running optimization ...", true);
						hashTable.put("optimizationStatus", msgPanel1);
					}
				};
				
				AsynchClientTask runOptTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = (batchRunWorkspace.getFrapStudies().get(finalIdx));
						
						ArrayList<Integer> models = fStudy.getSelectedModels();
						if(models.size() == 1)
						{
							for(int i = 0; i<models.size(); i++)
							{
								int model = (models.get(i)).intValue();
								if(model == batchRunWorkspace.getSelectedModel())
								{
									if(model == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
									{
//										if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() == null)
//										{//always run
											fStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF);
											Parameter[] initialParams = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT],fStudy.getStartingIndexForRecovery());
											//set model parameters and data
											Parameter[] bestParameters = fStudy.getFrapOptData().getBestParamters(initialParams, fStudy.getSelectedROIsForErrorCalculation());
											double[][] fitData = fStudy.getFrapOptData().getFitData(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].setModelParameters(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].setData(fitData);
//										}
									}
									else if(model == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
									{
//										if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() == null)
//										{//always run
											fStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF);
											Parameter[] initialParams = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS],fStudy.getStartingIndexForRecovery());
											//set model parameters and data
											Parameter[] bestParameters = fStudy.getFrapOptData().getBestParamters(initialParams, fStudy.getSelectedROIsForErrorCalculation());
											double[][] fitData = fStudy.getFrapOptData().getFitData(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].setModelParameters(bestParameters);
											fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].setData(fitData);
//										}
									}
									else if(model == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
									{
										Parameter[] bestParameters = fStudy.getFrapOptFunc().getBestParamters(fStudy.getFrapData(), null, true);
										double[][] fitData = fStudy.getFrapOptFunc().getFitData(bestParameters);
										fStudy.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].setModelParameters(bestParameters);
										fStudy.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].setData(fitData);
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
				
				AsynchClientTask message4Task = new AsynchClientTask("Evaluating confidence intervals for parameters ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						MessagePanel msgPanel = (MessagePanel)hashTable.get("optimizationStatus");
						msgPanel.setProgressCompleted();
						
						MessagePanel msgPanel2 = appendJobStatus("Evaluating confidence intervals for parameters ...", true);
						hashTable.put("evaluateCI", msgPanel2);
					}
				};
				
				AsynchClientTask evaluateCITask = new AsynchClientTask("Evaluating confidence intervals for parameters ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						FRAPStudy fStudy = (batchRunWorkspace.getFrapStudies().get(finalIdx));
						MessagePanel msgPanel = (MessagePanel)hashTable.get("evaluateCI");
						//evaluate confidence intervals
						ArrayList<Integer> models = fStudy.getSelectedModels();
						if(models.size() == 1)
						{
							for(int i = 0; i<models.size(); i++)
							{
								int model = (models.get(i)).intValue();
								if(model == batchRunWorkspace.getSelectedModel())
								{
									if(model == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
									{
										if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() != null)
										{
											ProfileData[] profileData = fStudy.getFrapOptData().evaluateParameters(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters(), msgPanel);
											fStudy.setProfileData_oneDiffComponent(profileData);
										}
									}
									else if(model == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
									{
										if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() != null)
										{
											ProfileData[] profileData = fStudy.getFrapOptData().evaluateParameters(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters(), msgPanel);
											fStudy.setProfileData_twoDiffComponents(profileData);
										}
									}
									else if(model == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
									{
										if(fStudy.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters() != null)
										{
											ProfileData[] profileData = fStudy.getFrapOptFunc().evaluateParameters(fStudy.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters(), msgPanel);
											fStudy.setProfileData_reactionOffRate(profileData);
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
				
				AsynchClientTask message5Task = new AsynchClientTask("Evaluating confidence intervals for parameters ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
				{
					public void run(Hashtable<String, Object> hashTable) throws Exception
					{
						MessagePanel msgPanel = (MessagePanel)hashTable.get("evaluateCI");
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
				batchRunTaskList.add(evaluateCITask);
				batchRunTaskList.add(message5Task);
			}
			
			return batchRunTaskList;
	  }
	  
	public AsynchClientTask[] save() 
	{
		ArrayList<AsynchClientTask> saveTasks = new ArrayList<AsynchClientTask>();
		AsynchClientTask beforeSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(batchRunWorkspace.getFrapStudies() == null || batchRunWorkspace.getFrapStudies().size() < 1)
				{
					throw new Exception("No Data exists to save");
				}else{
					File outputFile = null;
					String saveFileName = batchRunWorkspace.getBatchRunXmlFileName();
		    		if(saveFileName == null)
		    		{
		    			int choice = VirtualFrapLoader.saveFileChooser_batchRun.showSaveDialog(VirtualFrapBatchRunFrame.this);
		    			if (choice != JFileChooser.APPROVE_OPTION) {
		    				throw UserCancelException.CANCEL_FILE_SELECTION;
		    			}
		    			saveFileName = VirtualFrapLoader.saveFileChooser_batchRun.getSelectedFile().getPath();		    			
		    			if(saveFileName != null)
			    		{
		    				File tempOutputFile = new File(saveFileName);
				    		if(!VirtualFrapLoader.filter_vfbatch.accept(tempOutputFile)){
		    					if(tempOutputFile.getName().indexOf(".") == -1){
		    						tempOutputFile = new File(tempOutputFile.getParentFile(),tempOutputFile.getName()+"."+VirtualFrapLoader.VFRAP_BATCH_EXTENSION);
		    					}else{
		    						throw new Exception("Virtual FRAP Batchrun document names must have an extension of ."+VirtualFrapLoader.VFRAP_BATCH_EXTENSION);//return?
		    					}
		    				}
				    		if(tempOutputFile.exists())
				    		{
				    			String overwriteChoice = DialogUtils.showWarningDialog(VirtualFrapBatchRunFrame.this, "OverWrite file\n"+ tempOutputFile.getAbsolutePath(),
				    						new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
				    			if(overwriteChoice.equals(UserMessage.OPTION_CANCEL)){
				    				throw UserCancelException.CANCEL_GENERIC;
				    			}
				    			else
				    			{
					    			//Remove single vfrap files in the overwritten batchRun
					    			try{
					    				// TODO 
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
		    			updateStatus("Saving file " + outputFile.getAbsolutePath()+" ...");
		    			hashTable.put(FRAPStudyPanel.SAVE_FILE_NAME_KEY, outputFile);
		    		}
				}
			}
		};
		saveTasks.add(beforeSaveTask);
		//add saving each single vfrap files in the batchrun
		saveTasks.add(batchRunWorkspace.getSaveSingleFilesTask());
		//write batch run file
		saveTasks.add(batchRunWorkspace.getSaveBatchRunFileTask());
		
		AsynchClientTask afterSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
				VirtualFrapBatchRunFrame.updateStatus("File " + outFile.getAbsolutePath()+" has been saved.");
		        VirtualFrapLoader.mf.setBatchRunFrameTitle(outFile.getName());
			}
		};
		saveTasks.add(afterSaveTask);
		return saveTasks.toArray(new AsynchClientTask[saveTasks.size()]);
	}

	public AsynchClientTask[] saveAs()
	{
		ArrayList<AsynchClientTask> saveAsTasks = new ArrayList<AsynchClientTask>();
		AsynchClientTask beforeSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(batchRunWorkspace.getFrapStudies() == null || batchRunWorkspace.getFrapStudies().size() < 1)
				{
					throw new Exception("No FRAP Data exists to save");
				}else{
					File outputFile = null;
					String saveFileName = null;
	    			int choice = VirtualFrapLoader.saveFileChooser_batchRun.showSaveDialog(VirtualFrapBatchRunFrame.this);
	    			if (choice != JFileChooser.APPROVE_OPTION)
	    			{
	    				throw UserCancelException.CANCEL_FILE_SELECTION;
	    			}
	    			saveFileName = VirtualFrapLoader.saveFileChooser_batchRun.getSelectedFile().getPath();	
		    		if(saveFileName != null)
		    		{
			    		File tempOutputFile = new File(saveFileName);
			    		if(!VirtualFrapLoader.filter_vfbatch.accept(tempOutputFile)){
	    					if(tempOutputFile.getName().indexOf(".") == -1){
	    						tempOutputFile = new File(tempOutputFile.getParentFile(),tempOutputFile.getName()+"."+VirtualFrapLoader.VFRAP_BATCH_EXTENSION);
	    					}else{
	    						throw new Exception("Virtual FRAP Batchrun document names must have an extension of ."+VirtualFrapLoader.VFRAP_BATCH_EXTENSION);
	    					}
	    				}
			    		if(tempOutputFile.exists())
			    		{
			    			String overwriteChoice = DialogUtils.showWarningDialog(VirtualFrapBatchRunFrame.this, "OverWrite file\n"+ tempOutputFile.getAbsolutePath(),
			    						new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
			    			if(overwriteChoice.equals(UserMessage.OPTION_CANCEL)){
			    				throw UserCancelException.CANCEL_GENERIC;
			    			}
			    			else
			    			{
			    				//Remove single vfrap files in the overwritten batchRun
				    			try{
				    				//TODO
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
		    			updateStatus("Saving file " + outputFile.getAbsolutePath()+" ...");
		    			hashTable.put(FRAPStudyPanel.SAVE_FILE_NAME_KEY, outputFile);
		    		}
				}
			}
		};
		saveAsTasks.add(beforeSaveTask);
		//add saving each single vfrap files in the batchrun
		//to see if users want to overwrite 
		String choice = DialogUtils.showWarningDialog(VirtualFrapBatchRunFrame.this, "All \'.vfrap\' files in the batchrun will have to be saved to new copies.\nIf you just want to overwrite the existing \'.vfrap\' files in the batchrun, \nPlease cancel this action and use \'save\' function instead.", new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
		if(choice.equals(UserMessage.OPTION_OK))
		{
			ArrayList<FRAPStudy> frapStudies = batchRunWorkspace.getFrapStudies();
			for(FRAPStudy fStudy:frapStudies)
			{
				String fileName = fStudy.getXmlFilename();
				VirtualFrapLoader.saveFileChooser_batchRunSaveSingleFileAs.setDialogTitle("Save "+fileName+" as...");
				VirtualFrapLoader.saveFileChooser_batchRunSaveSingleFileAs.setSelectedFile(new File(new File(fileName).getName()));
				int option = VirtualFrapLoader.saveFileChooser_batchRunSaveSingleFileAs.showSaveDialog(VirtualFrapBatchRunFrame.this);
				if(option == JFileChooser.APPROVE_OPTION)
				{
					String fileStr = VirtualFrapLoader.saveFileChooser_batchRunSaveSingleFileAs.getSelectedFile().getAbsolutePath();
					File tempFile = new File(fileStr);
		    		if(!VirtualFrapLoader.filter_vfrap.accept(tempFile)){
    					if(tempFile.getName().indexOf(".") == -1){
    						tempFile = new File(tempFile.getParentFile(),tempFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
    					}else{
    						DialogUtils.showErrorDialog(this, "Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);
    						throw new RuntimeException("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);//return?
    					}
    				}
					fStudy.setXmlFilename(tempFile.getAbsolutePath());
				}
				else
				{
					throw UserCancelException.CANCEL_GENERIC;
				}
			}
			
			saveAsTasks.add(batchRunWorkspace.getSaveSingleFilesTask());
		}
		else
		{
			throw UserCancelException.CANCEL_GENERIC;
		}
		//write batch run file
		saveAsTasks.add(batchRunWorkspace.getSaveBatchRunFileTask());

		AsynchClientTask afterSaveAsTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//update tree
				getBatchRunDetailsPanel().updateViewTreeForNewBatchRunFile(batchRunWorkspace);
				//update title bar and status bar
				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
				VirtualFrapBatchRunFrame.updateStatus("File " + outFile.getAbsolutePath()+" has been saved.");
		        VirtualFrapLoader.mf.setBatchRunFrameTitle(outFile.getName());
			}
		};
		saveAsTasks.add(afterSaveAsTask);
		return saveAsTasks.toArray(new AsynchClientTask[saveAsTasks.size()]);
	}

	public AsynchClientTask[] open(final File inFile) 
	{
		
		ArrayList<AsynchClientTask> totalTasks = new ArrayList<AsynchClientTask>(); 
				 
		//check if save is needed before loading batch run file
	    if(batchRunWorkspace.isSaveNeeded())
	    {
			String choice = DialogUtils.showWarningDialog(VirtualFrapBatchRunFrame.this, "There are unsaved changes. Save current document before loading new document?", new String[]{FRAPStudyPanel.SAVE_CONTINUE_MSG, FRAPStudyPanel.NO_THANKS_MSG}, FRAPStudyPanel.SAVE_CONTINUE_MSG);
			if(choice.equals(FRAPStudyPanel.SAVE_CONTINUE_MSG))
			{
				AsynchClientTask[] saveTasks = save();
				for(int i=0; i<saveTasks.length; i++)
				{
					totalTasks.add(saveTasks[i]);
				}
			}
	    }
		
		
		AsynchClientTask preOpenTask = new AsynchClientTask("Loading "+inFile.getAbsolutePath()+"...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				VirtualFrapBatchRunFrame.updateStatus("Loading "+inFile.getAbsolutePath()+"...");
			}
		};
		totalTasks.add(preOpenTask);
		
		//load frap batch run file
		totalTasks.add(batchRunWorkspace.getLoadBatchRunFileTask(inFile));
		
		//load each single vfrap file
		totalTasks.add(batchRunWorkspace.getLoadSingleFilesTask(localWorkspace));
		
		//after loading task
		AsynchClientTask updateUIAfterLoadingTask = new AsynchClientTask("Updating User Interface ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPBatchRunWorkspace tempBatchRunWorkspace = (FRAPBatchRunWorkspace)hashTable.get(FRAPBatchRunWorkspace.BATCH_RUN_WORKSPACE_KEY);
				//if loaded successfully, update the batchrunworkspace with the temporary one.
				batchRunWorkspace.update(tempBatchRunWorkspace);
				//update tree
				getBatchRunDetailsPanel().updateViewTreeForNewBatchRunFile(batchRunWorkspace);
				//update ui
				VirtualFrapLoader.mf.setBatchRunFrameTitle(batchRunWorkspace.getBatchRunXmlFileName());
				VirtualFrapBatchRunFrame.updateStatus("Loaded " + tempBatchRunWorkspace.getBatchRunXmlFileName());
			}
		};
		totalTasks.add(updateUIAfterLoadingTask);
		
		return totalTasks.toArray(new AsynchClientTask[totalTasks.size()]);
	}
	
	public void setBatchRunFrameTitle(String str)
	{
	    if(str.equals(""))
		{
		    setTitle(VirtualFrapMainFrame.BATCHRUN_VER_NUMBER);
		}
		else
		{
		    setTitle(str + " - " + VirtualFrapMainFrame.BATCHRUN_VER_NUMBER);
		}
	}
	
	//the following methods are used to implement the DropTargetListener
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			// get the dropped object and try to figure out what it is
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			boolean bFileSelected = false;
			if(flavors.length > 0) 
			{
				for(DataFlavor flavor:flavors)
				{
					// Check for file lists specifically
					if (flavor.isFlavorJavaFileListType()) {
						// Accept copy and move drops...
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						@SuppressWarnings("unchecked")
						List<File> tData = (List<File>)tr.getTransferData(flavor);
						for(Object dataElement:tData)
						{
							if(dataElement instanceof File)
							{
								File inputFile = (File)dataElement;
								//check file extension, we'll open only .vfrap
								if(VirtualFrapLoader.filter_vfbatch.accept(inputFile)){
									bFileSelected = true;
									AsynchClientTask[] openTasks = open(inputFile);
									ClientTaskDispatcher.dispatch(VirtualFrapBatchRunFrame.this, new Hashtable<String, Object>(), openTasks, true);
			    				}
								else{
									String msg = "Unkown file type of " + inputFile.getName() +". Virtual FRAP Batchrun document names must have an extension of \'."+VirtualFrapLoader.VFRAP_BATCH_EXTENSION + "\'.";
									DialogUtils.showErrorDialog(this, msg);
		    						return;
		    					}
								
							}
							if(bFileSelected)
							{
								break;
							}
						}
					}
					// If we made it this far, everything worked.
					dtde.dropComplete(true);
					return;
					// other cases can be....
	//				else if (flavor.isFlavorSerializedObjectType()) {
	//					java object
	//				}
	//				else if (flavor.isRepresentationClassInputStream()) {
	//					input stream
	//				}
				}
			}
			// the user must not have dropped a file list
			System.out.println("Drop failed: " + dtde);
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			dtde.rejectDrop();
		}

	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	public TopLevelWindowManager getTopLevelWindowManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateConnectionStatus(ConnectionStatus connectionStatus) {
		// TODO Auto-generated method stub
		
	}

	public void updateMemoryStatus(long freeBytes, long totalBytes) {
		// TODO Auto-generated method stub
		
	}

	public void updateWhileInitializing(int i) {
		// TODO Auto-generated method stub
		
	}

	public ChildWindowManager getChildWindowManager() {
		return childWindowManager;
	}
}
