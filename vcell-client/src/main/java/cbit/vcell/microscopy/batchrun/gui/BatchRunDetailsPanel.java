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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptFunctions;
import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.BackgroundROIDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.BatchRunROIImgPanel;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.BleachedROIDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.CellROIDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.CropDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.FileSaveDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.FileSummaryDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.FileTypeDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.MultiFileDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.ROISummaryDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.RoiForErrorDescriptor;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.SingleFileDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_FileTypePanel;
import cbit.vcell.opt.Parameter;

@SuppressWarnings("serial")
public class BatchRunDetailsPanel extends JPanel implements ActionListener, PropertyChangeListener
{
	public URL[] iconFiles = {getClass().getResource("/images/add.gif"),
							  getClass().getResource("/images/delete_red.gif"),
							  getClass().getResource("/images/deleteAll.gif")};
	public String[] buttonLabels = {"Add a file to batch run", "Delete a file from batch run", "Delete all"};
	private ImageIcon[] icons = new ImageIcon[iconFiles.length];
	private JButton addButton, deleteButton, delAllButton;
	
	private Wizard batchRunAddDataWizard = null;
	private BatchRunROIImgPanel imgPanel = null;
	
	private JSplitPane leftSplit = null;
	// bottom component definition
	private JPanel botPanel = null;
	private JTextArea parameterTa = null;
	private JScrollPane tascrollPane = null;
	
	// top component definition
	private JPanel frapBatchRunViewPanel= null; 
	private JToolBar toolbar = null;
	private BatchRunTree frapBatchRunViewTree= null;
	private JScrollPane treeScrollPane = null;
	
	//tree handler
	private TreeHandler treeHandler = null;
	//batch run workspace
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	//local workspace
	private LocalWorkspace localWorkspace = null;

	//constructor
	public BatchRunDetailsPanel()
	{
		super();
	    //topTabPane.addMouseListener(th);
	    leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,getFrapBatchRunViewPanel(), getBottomPanel());
	    leftSplit.setDividerSize(2);
	    leftSplit.setDividerLocation(VirtualFrapBatchRunFrame.iniDividerLocation);
//	    leftSplit.setDividerLocation(Math.round(Toolkit.getDefaultToolkit().getScreenSize().height*1/2));
	    setLayout(new BorderLayout());
	    add(leftSplit, BorderLayout.CENTER);
	    setBorder(new EmptyBorder(0,0,0,0));
	    //addMouseListener(th);
	    setVisible(true);
	}

	// set up top component function
	public JPanel getFrapBatchRunViewPanel()
	{
		if(frapBatchRunViewPanel == null)
		{
			frapBatchRunViewPanel = new JPanel();
			
		    frapBatchRunViewPanel.setLayout(new BorderLayout());
		    frapBatchRunViewPanel.add(getTreeScrollPane(),BorderLayout.CENTER);
		    
		    //add toolbar
		    frapBatchRunViewPanel.add(getToolBar(), BorderLayout.SOUTH);
		}
	    return frapBatchRunViewPanel;
	}
	
	public JScrollPane getTreeScrollPane()
	{
		if(treeScrollPane == null)
		{
			treeScrollPane = new JScrollPane();
			frapBatchRunViewTree = new BatchRunTree();
		    frapBatchRunViewTree.setCellRenderer(new BatchRunTreeRenderer());
		    ToolTipManager.sharedInstance().registerComponent(frapBatchRunViewTree);
		    //set action listener
		    treeHandler = new TreeHandler();
		    frapBatchRunViewTree.addTreeSelectionListener(treeHandler);
		    frapBatchRunViewTree.addMouseListener(treeHandler);
	
		    treeScrollPane.getViewport().add(frapBatchRunViewTree);
		}
		return treeScrollPane;
	}
	
	public JToolBar getToolBar()
    {
    	if(toolbar == null)
    	{
	    	toolbar=new JToolBar();
	        for (int i = 0; i < buttonLabels.length; ++i)
	        {
	             icons[i]=new ImageIcon(iconFiles[i]);
	        }
	        addButton=new JButton(icons[0]);
	        addButton.setMargin(new Insets(0, 0, 0, 0));
	        addButton.setToolTipText(buttonLabels[0]);
	        addButton.setBorderPainted(false);
	        addButton.addActionListener(this);
	        deleteButton=new JButton(icons[1]);
	        deleteButton.setMargin(new Insets(0, 0, 0, 0));
	        deleteButton.setToolTipText(buttonLabels[1]);
	        deleteButton.setBorderPainted(false);
	        deleteButton.addActionListener(this);
	        delAllButton=new JButton(icons[2]);
	        delAllButton.setMargin(new Insets(0, 0, 0, 0));
	        delAllButton.setToolTipText(buttonLabels[2]);
	        delAllButton.setBorderPainted(false);
	        delAllButton.addActionListener(this);
	        
	        toolbar.add(addButton);
	        toolbar.add(deleteButton);
	        toolbar.add(delAllButton);
	        toolbar.setFloatable(false);
    	}
    	return toolbar;
       
    }//end of method setupToolBar()
	
	// set up bottom component function
	public JPanel getBottomPanel()
	{
		if(botPanel == null)
		{
			botPanel = new JPanel();
		    botPanel.setLayout(new BorderLayout());
		    botPanel.add(getParameterScrollPanel(),BorderLayout.CENTER);
		    TitledBorder tb=new TitledBorder(new EtchedBorder(),"Parameters:", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
		    botPanel.setBorder(tb);
		}
		return botPanel;
	}
	
	public JScrollPane getParameterScrollPanel()
	{
		if(tascrollPane == null)
		{
		    parameterTa= new JTextArea();
		    parameterTa.setFont(new Font("Tahoma", Font.PLAIN, 11));
		    tascrollPane=new JScrollPane(parameterTa);
		}
		return tascrollPane;
	}

	public void updateParameterDisplay()
	{
		double[][] statData = getBatchRunWorkspace().getStatisticsData();
		String paramStr = "Average parameter values among datasets:\n\n";
		for(int i = 0 ; i < BatchRunResultsParamTableModel.NUM_COLUMNS-2; i++)
		{
			if(statData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][i] == FRAPOptimizationUtils.largeNumber)
			{
				continue;
			}
			else if(batchRunWorkspace.getSelectedModel() == FRAPModel.IDX_MODEL_REACTION_OFF_RATE &&
					BatchRunResultsParamTableModel.COL_LABELS[i+1].equals(BatchRunResultsParamTableModel.COL_LABELS[BatchRunResultsParamTableModel.COLUMN_BS_CONCENTRATION]))
			{
				continue;
			}
			paramStr = paramStr + BatchRunResultsParamTableModel.COL_LABELS[i+1] + ": "
				      + statData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][i] + "\n";
		}
		parameterTa.setText(paramStr);
		parameterTa.setCaretPosition(0);
	}
	
	private void clearParameterDisplay()
	{
		parameterTa.setText("");
		parameterTa.setCaretPosition(0);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			try{
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }catch(Exception e){
		    	throw new RuntimeException(e.getMessage(),e);
		    }
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunDetailsPanel aPanel = new BatchRunDetailsPanel();
			frame.add(aPanel);
			frame.pack();
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
  	    if (source == addButton)
	    {
  	    	//check file before loading into batch run, if the file has .vfrap extension 
  	    	//check if it has the model that is required by the batch run, if no, run the reference simulation and get the parameters
  	    	final Wizard loadWizard = getAddFRAPDataWizard();
   			if(loadWizard != null)
   			{
   				loadWizard.showModalDialog(new Dimension(550,640));
   				//wizard has AsychClientTasks, therefore, we don't have to enclose the following code with client task again.
  	    		//code return 
				if(loadWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
   				{
					//get newly added frapstudy
					FRAPStudy fStudy = getBatchRunWorkspace().getWorkingFrapStudy();
					//check if the batch run has results already and the newly loaded data should have same model ready
					int batchRunSelectedModel = getBatchRunWorkspace().getSelectedModel();
					if(getBatchRunWorkspace().isBatchRunResultsAvailable() && fStudy.getModels()[batchRunSelectedModel] != null &&
					   fStudy.getFrapModel(batchRunSelectedModel).getModelParameters() != null)
					{
						Parameter[] parameters = fStudy.getFrapModel(batchRunSelectedModel).getModelParameters();
						double[][] fitData = null;
						if(batchRunSelectedModel == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT || batchRunSelectedModel == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
						{
							FRAPOptData optData= fStudy.getFrapOptData();
							//newly loaded frapstudy doesn't have estimation data ready, generate the data
							if(optData == null)
							{
								try {
									optData = new FRAPOptData(fStudy, parameters.length, getLocalWorkspace(), fStudy.getStoredRefData());
									fStudy.setFrapOptData(optData);
									fitData = optData.getFitData(parameters);
								} catch (Exception ex) {
									ex.printStackTrace(System.out);
									DialogUtils.showErrorDialog(BatchRunDetailsPanel.this, ex.getMessage());
								}
							} 
						}
						else if(batchRunSelectedModel == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
						{
							FRAPOptFunctions optFunc = fStudy.getFrapOptFunc();
							if(optFunc == null)
							{
								try {
									optFunc = new FRAPOptFunctions(fStudy);
									fStudy.setFrapOptFunc(optFunc);
									fitData = optFunc.getFitData(parameters);
								} catch (Exception ex2) {
									ex2.printStackTrace();
									DialogUtils.showErrorDialog(BatchRunDetailsPanel.this, ex2.getMessage() );
								} 
							}
						}
						fStudy.getModels()[batchRunSelectedModel].setData(fitData);
						//add new data into the frapStudy list in batch run.
						getBatchRunWorkspace().addFrapStudy(fStudy);
						//refresh the results
						getBatchRunWorkspace().refreshBatchRunResults();
					}
					else //batch run has no results, simply add the frapStudy into
					{
						getBatchRunWorkspace().addFrapStudy(fStudy);
					}
					//set save flag true when a doc has been added successfully
					getBatchRunWorkspace().setSaveNeeded(true);
					//update tree
					DefaultMutableTreeNode newNode = frapBatchRunViewTree.addBatchRunDocNode(new File(getBatchRunWorkspace().getWorkingFrapStudy().getXmlFilename()));
					//get the new tree node after sorting
					newNode = frapBatchRunViewTree.orderFRAPDocChildren(newNode);
					frapBatchRunViewTree.setSelectionPath(new TreePath(newNode.getPath()));
   				}
				else
				{
					//load data unsuccessfully, remove the displayed image
					getBatchRunWorkspace().clearWorkingSingleWorkspace();
					//clear tree selection
					frapBatchRunViewTree.clearSelection();
					//clear stored tree selection
					batchRunWorkspace.clearStoredTreeSelection();
				}
   			}
   			
        }
        else if(source == deleteButton)
        {
    		AsynchClientTask deleteFromWorkspaceTask = new AsynchClientTask("deleting file ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
	        		//remove tree node(doc node removable only)
	              	DefaultMutableTreeNode parent = frapBatchRunViewTree.removeCurrentNode();
	      			//handle differently for doc node and results node
	              	if(parent.equals(BatchRunTree.FRAP_BATCHRUN_DOC_NODE)) //doc node
	              	{
	              		//remove the data & displayed image
	      	        	getBatchRunWorkspace().removeFrapStudy(getBatchRunWorkspace().getWorkingFrapStudy());
	              	}
	              	else if(parent.equals(BatchRunTree.FRAP_BATCHRUN_RESULT_NODE))//results node
	              	{
	              		getBatchRunWorkspace().clearResultData();
	              	}
	              	getBatchRunWorkspace().clearWorkingSingleWorkspace();
    			}
    		};
    		
    		AsynchClientTask deleteFromUITask = new AsynchClientTask("deleting file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
	              	//clear tree selection
	      			frapBatchRunViewTree.clearSelection();
	      			if(batchRunWorkspace.getFrapStudies() == null || batchRunWorkspace.getFrapStudies().size() < 1)
	      			{
	      				frapBatchRunViewTree.clearAll();
	      			}
	      			else //if doc has been deleted successfully and frapStudyList still has doc, set save flag true.
	      			{
	      				batchRunWorkspace.setSaveNeeded(true);
	      			}
	      			batchRunWorkspace.clearStoredTreeSelection();
	      			//clear parameter display
	      			clearParameterDisplay();
    			}
    		};
    		ClientTaskDispatcher.dispatch(BatchRunDetailsPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{deleteFromWorkspaceTask, deleteFromUITask}, false, false, false, null, true);
			
        }
        else if(source == delAllButton)
        {
        	deleteAllBatchrunDocs();
        }
	}
	
	public void deleteAllBatchrunDocs()
	{
		//clear tree selection
    	frapBatchRunViewTree.clearAll();
    	batchRunWorkspace.clearStoredTreeSelection();
    	//remove the data & displayed image
    	getBatchRunWorkspace().removeAllFrapStudies();
    	getBatchRunWorkspace().clearWorkingSingleWorkspace();
    	//clear parameter display
		clearParameterDisplay();
	}
	
	
	public Wizard getAddFRAPDataWizard()
	{   // single/multipanel fires property change to frapstudyPanel after loaded a new exp dataset
		// it also fires property change to summaryPanel to varify info and modify frapstudy in frapstudypanel
		// then summarypanel fires varify change to frapstudypanel to set frapstudy(already changed in frapstudypanel when passing as paramter to 
		// summarypanel) to frapdatapanel.
		if(batchRunAddDataWizard == null)
		{
			batchRunAddDataWizard = new Wizard(JOptionPane.getFrameForComponent(this));
			batchRunAddDataWizard.getDialog().setTitle("Load FRAP Data");
	        
	        WizardPanelDescriptor fTypeDescriptor = new FileTypeDescriptor();
	        fTypeDescriptor.setNextPanelDescriptorID(SingleFileDescriptor.IDENTIFIER); //goes next to single file input by default
	        batchRunAddDataWizard.registerWizardPanel(FileTypeDescriptor.IDENTIFIER, fTypeDescriptor);
	        
	        WizardPanelDescriptor singleFileDescriptor = new SingleFileDescriptor();
	        batchRunAddDataWizard.registerWizardPanel(SingleFileDescriptor.IDENTIFIER, singleFileDescriptor);
	        ((SingleFileDescriptor)singleFileDescriptor).setBatchRunWorkspace(getBatchRunWorkspace());
	        ((SingleFileDescriptor)singleFileDescriptor).setLocalWorkspace(getLocalWorkspace());
	
	        WizardPanelDescriptor multiFileDescriptor = new MultiFileDescriptor();
	        batchRunAddDataWizard.registerWizardPanel(MultiFileDescriptor.IDENTIFIER, multiFileDescriptor);
	        ((MultiFileDescriptor)multiFileDescriptor).setBatchRunWorkspace(getBatchRunWorkspace());
	        
	        FileSummaryDescriptor fSummaryDescriptor = new FileSummaryDescriptor();
	        fSummaryDescriptor.setBackPanelDescriptorID(SingleFileDescriptor.IDENTIFIER); //goes back to single file input by default
	        batchRunAddDataWizard.registerWizardPanel(FileSummaryDescriptor.IDENTIFIER, fSummaryDescriptor);
	        fSummaryDescriptor.setBatchRunWorkspace(getBatchRunWorkspace());
	        
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
	        				fileTypeDescriptor.setNextPanelDescriptorID(SingleFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(SingleFileDescriptor.IDENTIFIER);
	        			}
	        			else
	        			{
	        				fileTypeDescriptor.setNextPanelDescriptorID(MultiFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(MultiFileDescriptor.IDENTIFIER);
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
	        				fileTypeDescriptor.setNextPanelDescriptorID(MultiFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(MultiFileDescriptor.IDENTIFIER);
	        			}
	        			else
	        			{
	        				fileTypeDescriptor.setNextPanelDescriptorID(SingleFileDescriptor.IDENTIFIER);
	        				fileSummaryDescriptor.setBackPanelDescriptorID(SingleFileDescriptor.IDENTIFIER);
	        			}
	        		}
				}
	        	
	        });
		}
		
		//use one panel for all the steps through out defining ROIs.
		imgPanel = new BatchRunROIImgPanel();
		imgPanel.setBatchRunWorkspace(getBatchRunWorkspace()); //batch run work space, no data yet.
		
		CropDescriptor cropDescriptor = new CropDescriptor(imgPanel);
		batchRunAddDataWizard.registerWizardPanel(CropDescriptor.IDENTIFIER, cropDescriptor);
		cropDescriptor.setBatchRunWorkspace(getBatchRunWorkspace());
        
        CellROIDescriptor cellROIDescriptor = new CellROIDescriptor(imgPanel);
        batchRunAddDataWizard.registerWizardPanel(CellROIDescriptor.IDENTIFIER, cellROIDescriptor);
        cellROIDescriptor.setBatchRunWorkspace(getBatchRunWorkspace());
        
        BleachedROIDescriptor bleachedROIDescriptor = new BleachedROIDescriptor(imgPanel);
        batchRunAddDataWizard.registerWizardPanel(BleachedROIDescriptor.IDENTIFIER, bleachedROIDescriptor);
        bleachedROIDescriptor.setBatchRunWorkspace(getBatchRunWorkspace());
        
        BackgroundROIDescriptor backgroundROIDescriptor = new BackgroundROIDescriptor(imgPanel);
        batchRunAddDataWizard.registerWizardPanel(BackgroundROIDescriptor.IDENTIFIER, backgroundROIDescriptor);
        backgroundROIDescriptor.setBatchRunWorkspace(getBatchRunWorkspace());
        
        ROISummaryDescriptor roiSummaryDescriptor = new ROISummaryDescriptor(imgPanel);
        batchRunAddDataWizard.registerWizardPanel(ROISummaryDescriptor.IDENTIFIER, roiSummaryDescriptor);
        roiSummaryDescriptor.setBatchRunWorkspace(getBatchRunWorkspace());
		
        WizardPanelDescriptor roiForErrorDescriptor = new RoiForErrorDescriptor();
        batchRunAddDataWizard.registerWizardPanel(RoiForErrorDescriptor.IDENTIFIER, roiForErrorDescriptor);
        ((RoiForErrorDescriptor)roiForErrorDescriptor).setBatchRunWorkspace(batchRunWorkspace);
        
        FileSaveDescriptor fileSaveDescriptor = new FileSaveDescriptor();
        batchRunAddDataWizard.registerWizardPanel(FileSaveDescriptor.IDENTIFIER, fileSaveDescriptor);
        fileSaveDescriptor.setBatchRunWorkspace(getBatchRunWorkspace());
        fileSaveDescriptor.setLocalWorkspace(localWorkspace);
        
        imgPanel.refreshUI();
        
		batchRunAddDataWizard.setCurrentPanel(FileTypeDescriptor.IDENTIFIER);//always start from the first page
        return batchRunAddDataWizard;
	}
	
	//set and get BatchRun Workspace
	public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}

	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
		FRAPBatchRunWorkspace oldBatchRunWorkspace = this.batchRunWorkspace;
		if(oldBatchRunWorkspace != null)
		{
			oldBatchRunWorkspace.removePropertyChangeListener(this);
		}
		batchRunWorkspace.addPropertyChangeListener(this);
		this.batchRunWorkspace = batchRunWorkspace;
	}
	
	//set and get Local Workspace
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}

	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
		treeHandler.setBatchRunWorkspace(getBatchRunWorkspace());
	}

	public void updateResultsInfo(boolean bNewlyCreated)
	{
		//clear results first
		if(bNewlyCreated)
		{
			clearResultsInfo();
		}
		else
		{
			//do the following two lines instead of calling batchRunWorkspace.clearResultData(). in order to not to clear average parameters 
			batchRunWorkspace.setAnalysisMSESummaryData(null);
			batchRunWorkspace.setStatisticsData(null);
			frapBatchRunViewTree.clearResults();
			batchRunWorkspace.clearStoredTreeSelection();
			clearParameterDisplay();
		}
		DefaultMutableTreeNode newNode = frapBatchRunViewTree.addBatchRunResultNode("Results Available");
		if(bNewlyCreated)
		{
			frapBatchRunViewTree.setSelectionPath(new TreePath(newNode.getPath()));
		}
	}
	
	public void clearResultsInfo()
	{
		batchRunWorkspace.clearResultData();//clear table data
		frapBatchRunViewTree.clearResults();
		batchRunWorkspace.clearStoredTreeSelection();
		clearParameterDisplay();
	}

	public void updateViewTreeForNewBatchRunFile(FRAPBatchRunWorkspace batchRunWorkspace)
	{
		frapBatchRunViewTree.clearAll();
		ArrayList<FRAPStudy> fStudyList = batchRunWorkspace.getFrapStudies();
		DefaultMutableTreeNode firstDocNode = null; 
		for(int i=0; i< fStudyList.size(); i++)
		{
			//update tree
			if(i==0)
			{
				firstDocNode = frapBatchRunViewTree.addBatchRunDocNode(new File(fStudyList.get(i).getXmlFilename()));
			}
			else
			{
				frapBatchRunViewTree.addBatchRunDocNode(new File(fStudyList.get(i).getXmlFilename()));
			}
		}
		if(batchRunWorkspace.isBatchRunResultsAvailable())
		{
			updateResultsInfo(false);
		}
		//set fist doc selected when opening a new Batch Run file
		frapBatchRunViewTree.setSelectionPath(new TreePath(firstDocNode.getPath()));
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_UPDATE_STATISTICS) ||
		   evt.getPropertyName().equals(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM))
		{
			updateParameterDisplay();
		}
		else if(evt.getPropertyName().equals(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_CLEAR_RESULTS))
		{
			clearResultsInfo();
		}
	}
	
}//end of class BatchRunDetailsFrame



