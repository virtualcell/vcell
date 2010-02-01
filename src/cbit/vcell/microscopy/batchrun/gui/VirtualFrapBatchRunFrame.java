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

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.AboutDialog;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.HelpViewer;
import cbit.vcell.microscopy.gui.PreferencePanel;
import cbit.vcell.microscopy.gui.StatusBar;
import cbit.vcell.microscopy.gui.ToolBar;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame.MenuHandler;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame.ToolBarHandler;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_MultiFilePanel;

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
	
	public static Dimension INIT_WINDOW_SIZE = new Dimension(1024, 768);

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
			  	   		MessagePanel msgPanel = new MessagePanel("Start running c:\\VirtualMicroscopy\\test2.vfrap", false);
			  	   		getBatchRunDisplayPanel().getJobStatusPanel().appendMessage(msgPanel);
			  	   		
			  	   		msgPanel = new MessagePanel("Saving information", true);
			  	   		getBatchRunDisplayPanel().getJobStatusPanel().appendMessage(msgPanel);
			  	   		int progCounter = 0;
			  	   		msgPanel.setProgress(10);

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
	    setSize(INIT_WINDOW_SIZE);
	    setLocation(
	    	(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
	    	(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
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
}
