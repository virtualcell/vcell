package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

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
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.SingleFileDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_FileTypePanel;


public class BatchRunDetailsPanel extends JPanel implements ActionListener
{
	public URL[] iconFiles = {getClass().getResource("/images/add.gif"),
							  getClass().getResource("/images/delete.gif"),
							  getClass().getResource("/images/deleteAll.gif")};
	public String[] buttonLabels = {"Add a file to batch run", "Delete a file from batch run", "Delete all"};
	private ImageIcon[] icons = new ImageIcon[iconFiles.length];
	private JButton addButton, deleteButton, delAllButton;
	
	private Wizard batchRunAddDataWizard = null;
//	private Wizard batchRunDefineROIWizard = null;
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
	    leftSplit.setDividerLocation(Math.round(Toolkit.getDefaultToolkit().getScreenSize().height*1/2));
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
	        //System.out.println(buttonLabels.length);
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
  	    	final Wizard loadWizard = getAddFRAPDataWizard();
   			if(loadWizard != null)
   			{
//   				ArrayList<AsynchClientTask> totalTasks = new ArrayList<AsynchClientTask>();
   				//check if save is needed before loading data
//	   				if(getBatchRunWorkspace().isSaveNeeded())
//	   				{
//	   					String choice = DialogUtils.showWarningDialog(this.getParent(), "There are unsaved changes. Save current document before loading new data?", new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
//	   					if(choice.equals(UserMessage.OPTION_OK))
//	   					{
//	   						AsynchClientTask[] saveTasks = save();
//	   						for(int i=0; i<saveTasks.length; i++)
//	   						{
//	   							totalTasks.add(saveTasks[i]);
//	   						}
//	   					}
//	   				}
	   				
//  	   			AsynchClientTask showLoadWizardTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
//  	    		{
//  	    			public void run(Hashtable<String, Object> hashTable) throws Exception
//  	    			{
  	    				loadWizard.showModalDialog(new Dimension(550,640));
//  	    			}
//  	    		};
  	    		
//  	    		AsynchClientTask afterCloseLoadWizardTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
//  	    		{
//  	    			public void run(Hashtable<String, Object> hashTable) throws Exception
//  	    			{
//  	    				if(loadWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
//	  	   				{
////	  	   					getAnalysisProcedurePanel().setWorkFlowStage(AnalysisProcedurePanel.STAGE_DEFINE_ROIS);
//	  	   					//set need save flag
////	  	   					getFrapWorkspace().getFrapStudy().setSaveNeeded(true);
//	  	   				}
//  	    			}
//  	    		};
  	    		
//  	    		totalTasks.add(showLoadWizardTask);
//  	    		totalTasks.add(afterCloseLoadWizardTask);
  	    		//dispatch
//  	    		ClientTaskDispatcher.dispatch(this.getParent(), new Hashtable<String, Object>(), totalTasks.toArray(new AsynchClientTask[totalTasks.size()]), false);;
  	    		//code return 
				if(loadWizard.getReturnCode() == Wizard.FINISH_RETURN_CODE)
   				{
					//add to frapList in batchrunworkspace
					getBatchRunWorkspace().addFrapStudy(getBatchRunWorkspace().getWorkingFrapStudy());
					//update tree
					DefaultMutableTreeNode newNode = frapBatchRunViewTree.addBatchRunDocNode(new File(getBatchRunWorkspace().getWorkingFrapStudy().getXmlFilename()));
					frapBatchRunViewTree.setSelectionPath(new TreePath(newNode.getPath()));
   				}
				else
				{
					//load data unsuccessfully, remove the displayed image
					getBatchRunWorkspace().clearWorkingSingleWorkspace();
					//clear tree selection
					frapBatchRunViewTree.clearSelection();
				}
   			}
   			
        }
        else if(source == deleteButton)
        {
        	//remove tree node
        	frapBatchRunViewTree.removeCurrentNode();
        	//remove the data & displayed image
        	getBatchRunWorkspace().clearWorkingSingleWorkspace();
			//clear tree selection
			frapBatchRunViewTree.clearSelection();
        }
        else if(source == delAllButton)
        {
        	System.out.println("Delete all button pressed.");
        	frapBatchRunViewTree.clear();
        }
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

	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
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

}//end of class BatchRunDetailsFrame



