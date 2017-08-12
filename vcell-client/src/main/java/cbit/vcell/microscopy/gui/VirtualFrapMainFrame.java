/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoableEdit;

import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.documentation.VcellHelpViewer;
import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.gui.VirtualFrapBatchRunFrame;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.VCellConfiguration;


/**
 * This the bottom container of virtual FRAP which contains menu bar, Frap Study Panel and status bar. 
 * @author Tracy LI
 * Created in Jan 2008.
 * @version 1.0
 */

/** The main frame of the application. */
@SuppressWarnings("serial")
public class VirtualFrapMainFrame extends LWTopFrame implements DropTargetListener, TopLevelWindow
{
	private final ChildWindowManager childWindowManager;
	
	private final static String HELP_VIEWER_CONTEXT_OBJECT = "Help Viewer Context Object";
	
	//the application has one local workspace and one FRAP workspace
	private LocalWorkspace localWorkspace = null;
	private FRAPSingleWorkspace frapWorkspace = null;
	private FRAPBatchRunWorkspace batchRunWorkspace = null;

	public static final boolean SAVE_COMPRESSED = true;

	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int APP_WIDTH = Math.min(SCREEN_WIDTH,1024);
	public static final int APP_HEIGHT = Math.min(SCREEN_HEIGHT,768);
	public static final int INI_FRAME_LOCX = (SCREEN_WIDTH - APP_WIDTH)/2;
	public static final int INI_FRAME_LOCY = (SCREEN_HEIGHT - APP_HEIGHT)/2;
	public static final Dimension INIT_WINDOW_SIZE = new Dimension(APP_WIDTH, APP_HEIGHT);

	private MenuHandler menuHandler = new MenuHandler();
	private static final String VERSION_NUMBER = "VFrap 1.1";
	public static final String BATCHRUN_VER_NUMBER = "VFRAP 1.1_Batch_Run";
	private static final String OPEN_ACTION_COMMAND = "Open vfrap";
	private static final String SAVE_ACTION_COMMAND = "Save";
	private static final String CLOSE_ACTION_COMMAND = "Close";
	private static final String SAVEAS_MATLAB_COMMAND = "Save Image Data to Matlab";
	private static final String SAVEAS_ACTION_COMMAND = "Save As...";
	//	private static final String PRINT_ACTION_COMMAND = "Print";
	private static final String EXIT_ACTION_COMMAND = "Exit";
	private static final String HELPTOPICS_ACTION_COMMAND = "Help Topics";
	private static final String ABOUT_ACTION_COMMAND = "About Virtual Frap";
	private static final String UNDO_ACTION_COMMAND = "Undo";
	private static final String PREFERENCE_ACTION_COMMAND = "Preference";
	private static final String BATCH_RUN_ACTION_COMMAND = "Batch Run";
	//used for work flow buttons
	public static final String LOAD_IMAGE_COMMAND = "Load FRAP images";
	public static final String DEFINE_ROI_COMMAND = "Define ROIs";
	public static final String CHOOSE_MODEL_COMMAND = "Choose model types";
	public static final String ESTIMATE_PARAM_COMMAND = "Estimate model parameters";
	//for 'run sim' and 'show result' buttons
	public static final String RUN_SIM_COMMAND = "Run Simulation";
	public static final String SHOW_SIM_RESULT_COMMAND = "Show sim result";

	private static final JMenuItem menuOpen= new JMenuItem(OPEN_ACTION_COMMAND,'O');
	private static final JMenuItem menuExit= new JMenuItem(EXIT_ACTION_COMMAND,'X');
	private static final JMenuItem msave = new JMenuItem(SAVE_ACTION_COMMAND,'S');
	private static final JMenuItem mclose = new JMenuItem(CLOSE_ACTION_COMMAND,BeanUtils.CLOSE_WINDOW_KEY_STROKE.getKeyChar());
	private static final JMenuItem msaveas = new JMenuItem(SAVEAS_ACTION_COMMAND);
	private static final JMenuItem msaveasMat = new JMenuItem(SAVEAS_MATLAB_COMMAND);
	private static final JMenuItem mHelpTopics = new JMenuItem(HELPTOPICS_ACTION_COMMAND);
	private static final JMenuItem mabout = new JMenuItem(ABOUT_ACTION_COMMAND);
	private static final JMenuItem mUndo = new JMenuItem(UNDO_ACTION_COMMAND);
	private static final JMenuItem mPreference = new JMenuItem(PREFERENCE_ACTION_COMMAND);
	private static final JMenuItem mBatchRun = new JMenuItem(BATCH_RUN_ACTION_COMMAND);

	public static JMenuBar mb = null;
	private static StatusBar statusBarNew = new StatusBar();
	public static ToolBar toolBar = null;
	public static FRAPStudyPanel frapStudyPanel = null;
	private UndoableEdit lastUndoableEdit;
	private PreferencePanel preferencePanel = null;
	private VirtualFrapBatchRunFrame batchRunFrame = null;
	private boolean bStandalone = true;
	//for drag and drop action 
	@SuppressWarnings("unused")
	private DropTarget dt;
	private final String menuDesc;
	//Inner class AFileFilter
	//This class implements both ava.io.FileFilter and javax.swing.filechooser.FileFilter.
	public static class AFileFilter extends FileFilter  implements java.io.FileFilter {

		private String[] extensions;
		private String description;

		/**
		 * Construct a file filter <p>
		 * @param extension         a file extension
		 * @param description       the description
		 */
		public AFileFilter(String extension, String description) {
			this(new String[] {extension}, description);
		}

		/**
		 * Construct a file filter 
		 * @param extensions        supported file extensions
		 * @param description       the description
		 */
		public AFileFilter(String[] extensions, String description) {
			this.extensions = extensions;
			this.description = description;
		}

		//Overrides abstract method in javax,swing.filechooser.FileFilter
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null) {
				if (findStringInArrayOf(extensions, extension) >= 0) {
					return true;
				}
				else {
					return false;
				}
			}

			return false;
		}

		//The description of this filter
		public String getDescription() {
			String des = "";
			for (int i = 0; i < extensions.length; i++) {
				if (i > 0) des += ",";
				des += extensions[i];
			}
			return description + " (*." + des + ")";
		}

		/**
		 * Find str in strs.
		 * @param strs to find in
		 * @param str  to find for
		 * @return  index of strs if found, -1 otherwise
		 */
		private int findStringInArrayOf(String[] strs, String str) {
			if (str == null || strs == null) return -1;
			for (int i = 0; i < strs.length; i++)
				if (strs[i].compareTo(str) == 0)
					return i;
			return -1;
		}

		/**
		 * Get the extension of a file.
		 * @param f
		 * @return
		 */
		private String getExtension(File f) {
			String ext = "";
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}
	}

	//Inner class MenuHandler
	private class MenuHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof JMenuItem)
			{
				String arg=e.getActionCommand();
				// file menu
				if(arg.equals(OPEN_ACTION_COMMAND))
				{
					File inputFile = null;
					int option = VirtualFrapLoader.openVFRAPFileChooser.showOpenDialog(frapStudyPanel);
					if (option == JFileChooser.APPROVE_OPTION){
						inputFile = VirtualFrapLoader.openVFRAPFileChooser.getSelectedFile();
					}else{
						return;
					}

					AsynchClientTask[] openTasks = frapStudyPanel.open(inputFile);
					ClientTaskDispatcher.dispatch(VirtualFrapMainFrame.this, new Hashtable<String, Object>(), openTasks, true);

				}
				else if(arg.equals(SAVE_ACTION_COMMAND))
				{
					AsynchClientTask[] saveTasks = frapStudyPanel.save();
					ClientTaskDispatcher.dispatch(VirtualFrapMainFrame.this, new Hashtable<String, Object>(), saveTasks, true);
				}
				else if(arg.equals(SAVEAS_ACTION_COMMAND))
				{
					AsynchClientTask[] saveAsTasks = frapStudyPanel.saveAs();
					ClientTaskDispatcher.dispatch(VirtualFrapMainFrame.this, new Hashtable<String, Object>(), saveAsTasks, true);
				}
				else if(arg.equals(SAVEAS_MATLAB_COMMAND))
				{
					String matFileName = null;
					int option = VirtualFrapLoader.saveAsMatFileChooser.showSaveDialog(frapStudyPanel);
					if (option == JFileChooser.APPROVE_OPTION){
						matFileName = VirtualFrapLoader.saveAsMatFileChooser.getSelectedFile().getPath();
					}else{
						return;
					}
					if(matFileName.indexOf("."+VirtualFrapLoader.MAT_EXTENSION) < 0)
					{
						matFileName = matFileName + "."+VirtualFrapLoader.MAT_EXTENSION;
					}
					System.out.println("Writing FRAP Image data to " + matFileName);
					try {
						frapStudyPanel.saveAsMatLabFile(matFileName);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("Writing FRAP Image data is done.");
				}
				else if(arg.equals(EXIT_ACTION_COMMAND) || arg.equals(CLOSE_ACTION_COMMAND))
				{
					String text = "";
					if (frapWorkspace != null && frapWorkspace.getWorkingFrapStudy() != null && frapWorkspace.getWorkingFrapStudy().isSaveNeeded()) {
						text = "UnSaved changes will be lost!";
					}
					String result = DialogUtils.showWarningDialog(VirtualFrapMainFrame.this,"Exit Warning","Do you want to Exit Virtual Frap? " + text,new String[]{UserMessage.OPTION_EXIT, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_EXIT); 
					if (result == UserMessage.OPTION_EXIT)
					{
						if(bStandalone)
						{
							System.exit(0);//quit application
						}
						else //activated in VCell
						{
							VirtualFrapMainFrame.this.dispose();//dispose the frame
						}
					}
				}
				else if(arg.equals(HELPTOPICS_ACTION_COMMAND))
				{
					ChildWindow helpViewerWindow = childWindowManager.getChildWindowFromContext(HELP_VIEWER_CONTEXT_OBJECT);
					if(helpViewerWindow == null)
					{
						VcellHelpViewer hviewer = new VcellHelpViewer(VcellHelpViewer.VFRAP_DOC_URL);
						helpViewerWindow = getChildWindowManager().addChildWindow(hviewer,HELP_VIEWER_CONTEXT_OBJECT,"Virtual FRAP Help", false);
						BeanUtils.addCloseWindowKeyboardAction(hviewer);
						helpViewerWindow.setIsCenteredOnParent();
						helpViewerWindow.setPreferredSize(new Dimension(VcellHelpViewer.DEFAULT_HELP_DIALOG_WIDTH,VcellHelpViewer.DEFAULT_HELP_DIALOG_HEIGHT));
						helpViewerWindow.pack();
						helpViewerWindow.show();
					}
					else
					{
						helpViewerWindow.show();
					}

				}
				else if(arg.equals(ABOUT_ACTION_COMMAND))
				{
					DocumentWindow.showAboutBox(VirtualFrapMainFrame.this);
				}else if(arg.equals(UNDO_ACTION_COMMAND)){
					mUndo.setEnabled(false);
					lastUndoableEdit.undo();
					lastUndoableEdit = null;
				}
				else if(arg.equals(PREFERENCE_ACTION_COMMAND))
				{
					PreferencePanel panel = getPreferencePanel();
					if(!panel.isStatusSet())
					{
						panel.setIniStatus();
					}
					int Choice = DialogUtils.showComponentOKCancelDialog(VirtualFrapMainFrame.this, panel, "Preferences");
					if(Choice == JOptionPane.OK_OPTION)
					{
						VCellConfiguration.putValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, panel.getROIAssistType());
					}
				}
				else if(arg.equals(BATCH_RUN_ACTION_COMMAND))
				{
					getBatchRunFrame().setVisible(true);
					getBatchRunFrame().setState(JFrame.NORMAL);
					setBatchRunFrameTitle("");
				}
			}
		}
	}// end of inner class MenuHandler

	public VirtualFrapBatchRunFrame getBatchRunFrame()
	{
		if(batchRunFrame == null)
		{
			batchRunFrame = new VirtualFrapBatchRunFrame(localWorkspace, batchRunWorkspace);
		}
		return batchRunFrame;
	}

	public PreferencePanel getPreferencePanel()
	{
		if(preferencePanel == null)
		{
			preferencePanel = new PreferencePanel();
		}
		return preferencePanel;
	}

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
						DialogUtils.showErrorDialog(VirtualFrapMainFrame.this, "Exception: " + e5.getMessage());
						updateStatus("Exception: " + e5.getMessage());
					}
					break;
					//	  	   			case ToolBar.BUT_PRINT:
					//		  	   			if(frapStudyPanel.getJTabbedPane().getSelectedIndex() == FRAPStudyPanel.INDEX_TAB_FITCURVE)
					//		  	            {
					//		  	                statusBar.showStatus("Printing...");
					//		  	                Toolkit toolkit = Toolkit.getDefaultToolkit();
					//		  	                String name="Print from VFrap";
					//		  	                JobAttributes job=new JobAttributes();
					//		  	                PageAttributes page=new PageAttributes();
					//		  	                PrintJob pj=toolkit.getPrintJob(VirtualFrapMainFrame.this,name,job,page);
					//		  	                if (pj!=null)
					//		  	                {
					//		  	                   ((JPanel)(frapStudyPanel.getJTabbedPane().getSelectedComponent())).printAll(pj.getGraphics());
					//		  	                   pj.end();
					//		  	                }
					//		  	                statusBar.showStatus("File has been printed.");
					//		  	            }
					//		  	            else
					//		  	                statusBar.showStatus("This is not printable from VFrap!");
					//	  	   			break;
				case ToolBar.BUT_HELP:
					menuHandler.actionPerformed(new ActionEvent(mHelpTopics,0,HELPTOPICS_ACTION_COMMAND));
					break;
				default:
					break;
				}
			}
		}
	}// end of inner class ToolBarHandler


	// constructor
	public VirtualFrapMainFrame(LocalWorkspace localWorkspace, FRAPSingleWorkspace frapWorkspace, FRAPBatchRunWorkspace batchRunWorkspace, boolean bStandalone,DocumentWindowManager documentWindowManager)
	{
		super();
		childWindowManager = new ChildWindowManager(this);
		this.localWorkspace = localWorkspace;
		this.frapWorkspace = frapWorkspace;
		this.batchRunWorkspace = batchRunWorkspace;
		this.bStandalone = bStandalone;
		//showing the splash window for VirtualFrap standalone, don't show if activated from VCell
		if(bStandalone)
		{
			final URL splashImage = getClass().getResource("/images/splash.jpg");
			new SplashWindow(splashImage,this,3500);
			//get image file
			setIconImage(new ImageIcon(getClass().getResource("/images/logo.gif")).getImage());
		}
		//initialize components
		initiateComponents(documentWindowManager);
		SetupMenus();
		enableSave(false);
		System.out.println("current directory is:"+ localWorkspace.getDefaultWorkspaceDirectory());

		//set window size
		setSize(INIT_WINDOW_SIZE);
		setLocation(INI_FRAME_LOCX, INI_FRAME_LOCY);

		updateStatus("This is the main frame of Virtual Frap.");
		//to handle the close button of the frame
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if(!getBatchRunFrame().isVisible())
				{
					menuHandler.actionPerformed(new ActionEvent(menuExit,0,EXIT_ACTION_COMMAND));
				}
				else
				{
					String result = DialogUtils.showWarningDialog(VirtualFrapMainFrame.this, "Virtual Frap Batch Run frame is still open. Please close Batch Run Frame first! ", new String[]{UserMessage.OPTION_OK}, UserMessage.OPTION_OK); 
					if (result == UserMessage.OPTION_OK)
					{
						return;
					}
				}
			}
		});

		dt = new DropTarget(frapStudyPanel, this);
		setVisible(true);
		menuDesc = nextSequentialDescription("Virtual Frap");
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
	protected void initiateComponents(DocumentWindowManager documentWindowManager)
	{
		toolBar = new ToolBar();
		toolBar.setNewAndRunButtonVisible(false);
		ToolBarHandler th = new ToolBarHandler();
		toolBar.addToolBarHandler(th);
		mb = new JMenuBar();

		frapStudyPanel = new FRAPStudyPanel();
		frapStudyPanel.addUndoableEditListener(
				new UndoableEditListener(){
					public void undoableEditHappened(UndoableEditEvent e) {
						if(e.getEdit().canUndo()){
							lastUndoableEdit = e.getEdit();
							mUndo.setText(UNDO_ACTION_COMMAND+" "+e.getEdit().getUndoPresentationName());
							mUndo.setEnabled(true);
						}else{
							lastUndoableEdit = null;
							mUndo.setText(UNDO_ACTION_COMMAND);
							mUndo.setEnabled(false);
						}
					}
				}
		);
//		System.setProperty(PropertyLoader.primarySimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
//		System.setProperty(PropertyLoader.secondarySimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
		System.setProperty(PropertyLoader.exportBaseDirProperty, localWorkspace.getDefaultSimDataDirectory());
		System.setProperty(PropertyLoader.exportBaseURLProperty, "file://"+localWorkspace.getDefaultSimDataDirectory());

		frapStudyPanel.setLocalWorkspace(localWorkspace);
		frapStudyPanel.setFRAPWorkspace(frapWorkspace);
		frapStudyPanel.setDocumentWindowManager(documentWindowManager);
		//add components to the main frame
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(statusBarNew, BorderLayout.SOUTH);
		getContentPane().add(frapStudyPanel);
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
		menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		msave.addActionListener(menuHandler);
		fileMenu.add(msave);
		msave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		msaveas.addActionListener(menuHandler);
		fileMenu.add(msaveas);

		msaveasMat.addActionListener(menuHandler);
		fileMenu.add(msaveasMat);
		
		mclose.addActionListener(menuHandler);
		fileMenu.add(mclose);
		mclose.setAccelerator(BeanUtils.CLOSE_WINDOW_KEY_STROKE);

		fileMenu.addSeparator();

		//    mprint.addActionListener(menuHandler);
		//    mprint.setEnabled(false);
		//    fileMenu.add(mprint);
		//    mprint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_MASK));
		//
		//    fileMenu.addSeparator();

		
		menuExit.addActionListener(menuHandler);
		fileMenu.add(menuExit);

		//Edit menu
		//    JMenu editMenu =new JMenu("Edit");
		//    editMenu.setMnemonic('E');
		//    mb.add(editMenu);
		//   
		//    mUndo.setActionCommand(UNDO_ACTION_COMMAND);
		//    mUndo.addActionListener(menuHandler);
		//    mUndo.setAccelerator(KeyStroke.getKeyStroke(
		//            KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		//    mUndo.setEnabled(false);
		//    editMenu.add(mUndo);

		//Tools Menu
		JMenu toolsMenu =new JMenu("Tools");
		toolsMenu.setMnemonic('T');
		mb.add(toolsMenu);

		mPreference.addActionListener(menuHandler);
		toolsMenu.add(mPreference);

		mBatchRun.addActionListener(menuHandler);
		toolsMenu.add(mBatchRun);
		
		mb.add( createWindowMenu(true ) );

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

	//setTitle overrides the orginal function in java.awt.Frame
	//to show the software name and version.
	public void setMainFrameTitle(String str)
	{
		if(str.equals(""))
		{
			this.setTitle(VERSION_NUMBER);
		}
		else
		{
			this.setTitle(str + " - " + VERSION_NUMBER);
		}
	}

	public void setBatchRunFrameTitle(String str)
	{
		getBatchRunFrame().setBatchRunFrameTitle(str);
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
								if(VirtualFrapLoader.filter_vfrap.accept(inputFile)){
									bFileSelected = true;
									AsynchClientTask[] openTasks = frapStudyPanel.open(inputFile);
									ClientTaskDispatcher.dispatch(VirtualFrapMainFrame.this, new Hashtable<String, Object>(), openTasks, true);
			    				}
								else{
									String msg = "Unkown file type of "+ inputFile.getName() +". Virtual FRAP document names must have an extension of \'."+VirtualFrapLoader.VFRAP_EXTENSION + "\'.";
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

} // end of class MainFrame
