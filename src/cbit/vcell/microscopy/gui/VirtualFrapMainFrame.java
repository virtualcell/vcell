package cbit.vcell.microscopy.gui;

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

import cbit.gui.DialogUtils;
import cbit.util.AsynchProgressPopup;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.server.PropertyLoader;


/**
 * This the bottom container of virtual FRAP which contains menu bar, Frap Study Panel and status bar. 
 * @author Tracy LI
 * Created in Jan 2008.
 * @version 1.0
 */

/** The main frame of the application. */
public class VirtualFrapMainFrame extends JFrame
{
	private LocalWorkspace localWorkspace;
	
	public static final String ROIErrorString = 
		"'Cell','Bleach' and 'Background' ROIs are required for FRAP model.  "+
		"Create all ROIs under '"+FRAPStudyPanel.FRAPSTUDYPANEL_TABNAME_IMAGES+"' tab";

	public static final boolean SAVE_COMPRESSED = true;
	
	public static Dimension INIT_WINDOW_SIZE = 
		new Dimension(1024,768);
//		new Dimension(
//		  (int)(Toolkit.getDefaultToolkit().getScreenSize().width*.8),
//	  	  (int)(Toolkit.getDefaultToolkit().getScreenSize().height*.9));

	private MenuHandler menuHandler = new MenuHandler();
	private static final String VERSION_NUMBER = "VFrap 0.8  Alpha_1";
	private static final String OPEN_ACTION_COMMAND = "Open";
	private static final String SAVE_ACTION_COMMAND = "Save";
	private static final String SAVEAS_ACTION_COMMAND = "Save As...";
	private static final String IMPORTFILESERIES_ACTION_COMMAND = "Import file series ...";
	private static final String PRINT_ACTION_COMMAND = "Print";
	private static final String EXIT_ACTION_COMMAND = "Exit";
	private static final String HELPTOPICS_ACTION_COMMAND = "Help Topics";
	private static final String ABOUT_ACTION_COMMAND = "About Virtual Frap";
	private static final String UNDO_ACTION_COMMAND = "Undo";
	
	private static final JMenuItem menuOpen= new JMenuItem(OPEN_ACTION_COMMAND,'O');
	private static final JMenuItem menuExit= new JMenuItem(EXIT_ACTION_COMMAND,'X');
	private static final JMenuItem msave = new JMenuItem(SAVE_ACTION_COMMAND,'S');
	private static final JMenuItem msaveas = new JMenuItem(SAVEAS_ACTION_COMMAND);
	private static final JMenuItem mfileSeries = new JMenuItem(IMPORTFILESERIES_ACTION_COMMAND);
	private static final JMenuItem mprint = new JMenuItem(PRINT_ACTION_COMMAND);
	private static final JMenuItem mHelpTopics = new JMenuItem(HELPTOPICS_ACTION_COMMAND);
	private static final JMenuItem mabout = new JMenuItem(ABOUT_ACTION_COMMAND);
	private static final JMenuItem mUndo = new JMenuItem(UNDO_ACTION_COMMAND);

  public static JMenuBar mb = null;
  private static StatusBar statusBarNew = new StatusBar();
  public static ToolBar toolBar = null;
  public static FRAPStudyPanel frapStudyPanel = null;
  private UndoableEdit lastUndoableEdit;

  private MultiFileInputDialog multiFileDialog = null;
  
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
  public class MenuHandler implements ActionListener
  {
	  private void saveAndSaveAs(final boolean bSaveAs){
			final AsynchProgressPopup pp =
				new AsynchProgressPopup(
					VirtualFrapMainFrame.this,
					"Saving "+(bSaveAs?"New":"Current")+" FRAP document",
					"Working...",true,false
			);
			pp.start();
			new Thread(new Runnable(){public void run(){
		    	  try {
		    		  if(bSaveAs){
		    			  frapStudyPanel.saveAs();
		    		  }else{
		    			  frapStudyPanel.save();
		    		  }
				  }catch(UserCancelException e1){
					  //ignore
				  }catch(final Exception e){
					  pp.stop();
					  updateStatus((bSaveAs?"SaveAs":"Save")+" Error: " + e.getMessage());
					  try{
						  SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							  DialogUtils.showErrorDialog((bSaveAs?"SaveAs":"Save")+" Error: " + e.getMessage());
						  }});
					  }catch(Exception e2){
						  e2.printStackTrace();
					  }
				  }	finally{
					  pp.stop();
				  }
			}}).start();

	  }
 		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof JMenuItem)
		    {
			  String arg=e.getActionCommand();
			  // file menu
			  if(arg.equals(OPEN_ACTION_COMMAND))
		      {
				  File inputFile = null;
	  			  int option = VirtualFrapLoader.openFileChooser.showOpenDialog(frapStudyPanel);
	  			  if (option == JFileChooser.APPROVE_OPTION){
	  				  inputFile = VirtualFrapLoader.openFileChooser.getSelectedFile();
	  			  }else{
	  				  return;
	  			  }
	  			  try{
	  	  			frapStudyPanel.load(new File[] {inputFile},null);
	  			  }catch(UserCancelException e2){
	  				  //ignore
	  			  }catch(Exception e2){
	  				  e2.printStackTrace();
	  				  PopupGenerator.showErrorDialog(
	  					"Error opening file:\n"+inputFile.getAbsolutePath()+"\n"+e2.getMessage());
	  			  }
		      }
			  else if(arg.equals(SAVE_ACTION_COMMAND))
		      {
				  saveAndSaveAs(false);
//					final AsynchProgressPopup pp =
//						new AsynchProgressPopup(
//							VirtualFrapMainFrame.this,
//							"Saving current FRAP document",
//							"Working...",true,false
//					);
//					pp.start();
//					new Thread(new Runnable(){public void run(){
//				    	  try {
//						      frapStudyPanel.save();
//						  }catch(UserCancelException e1){
//							  //ignore
//						  }catch(Exception e){
//							  pp.stop();
//							  DialogUtils.showErrorDialog("Save Error: " + e.getMessage());
//							  updateStatus("Save Error: " + e.getMessage());
//						  }	finally{
//							  pp.stop();
//						  }
//					}}).start();
		      }
		      else if(arg.equals(SAVEAS_ACTION_COMMAND))
		      {
		    	  saveAndSaveAs(true);
//					final AsynchProgressPopup pp =
//						new AsynchProgressPopup(
//							VirtualFrapMainFrame.this,
//							"Saving New FRAP document",
//							"Working...",true,false
//					);
//					pp.start();
//					new Thread(new Runnable(){public void run(){
//				    	  try {
//						      frapStudyPanel.saveAs();
//						  }catch(UserCancelException e1){
//							  //ignore
//						  }catch(Exception e){
//							  pp.stop();
//							  DialogUtils.showErrorDialog("SaveAs Error: " + e.getMessage());
//							  updateStatus("SaveAs Error: " + e.getMessage());
//						  }	finally{
//							  pp.stop();
//						  }
//					}}).start();
		      }
		      else if(arg.equals(IMPORTFILESERIES_ACTION_COMMAND))
		      {
				  if (multiFileDialog != null)
				  {
					  multiFileDialog.setVisible(true);
				  }	 
				  else
				  {
					  multiFileDialog = new MultiFileInputDialog(VirtualFrapMainFrame.this);
					  multiFileDialog.setVisible(true);
				  }
		      }
		      else if(arg.equals(EXIT_ACTION_COMMAND))
			  {
		    	  int result = JOptionPane.showConfirmDialog(
		    			 VirtualFrapMainFrame.this, "Do you want to Exit Virtual Frap? UnSaved changes will be lost!","Exit?", JOptionPane.YES_NO_OPTION);
	    	      if (result == JOptionPane.YES_OPTION)
	    	      {
	    	    	  System.exit(0);
	    	      }
			  }
		      // Help menu
		      else if(arg.equals(HELPTOPICS_ACTION_COMMAND))
		      {
		    	  HelpViewer hviewer = new HelpViewer();
		      }
		      else if(arg.equals(ABOUT_ACTION_COMMAND))
		      {
		    	  new AboutDialog(getClass().getResource("/images/splash.jpg"), VirtualFrapMainFrame.this);
		      }else if(arg.equals(UNDO_ACTION_COMMAND)){
		    	  mUndo.setEnabled(false);
		    	  lastUndoableEdit.undo();
		    	  lastUndoableEdit = null;
		      }
		  }
	  }
  }// end of inner class MenuHandler
  
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
						    DialogUtils.showErrorDialog("Exception: " + e5.getMessage());
						    updateStatus("Exception: " + e5.getMessage());
						}
	  	   			break;
	  	   			case ToolBar.BUT_PRINT:
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
	  	   			break;
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
  public VirtualFrapMainFrame(LocalWorkspace localWorkspace)
  {
    super();
    this.localWorkspace = localWorkspace;
    //showing the splash window for VirtualFrap
    final URL splashImage = getClass().getResource("/images/splash.jpg");
    new SplashWindow(splashImage,this,3500);
    //get image file
    setIconImage(new ImageIcon(getClass().getResource("/images/logo.gif")).getImage());
    //initiate variables
    initiateComponents();
    SetupMenus();
    enableSave(false);
    System.out.println("current directory is:"+ localWorkspace.getDefaultWorkspaceDirectory());
        
    //set window size
    setSize(INIT_WINDOW_SIZE);
    setLocation(
    	(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
    	(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
    updateStatus("This is the main frame of Virtual Frap.");
	
    //to handle the close button of the frame
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(createAppCloser());
    
    setVisible(true);
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
//      statusBar = new StatusBar();
      toolBar = new ToolBar();
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
	  System.setProperty(PropertyLoader.localSimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
	  System.setProperty(PropertyLoader.secondarySimDataDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
	  System.setProperty(PropertyLoader.exportBaseDirProperty, localWorkspace.getDefaultWorkspaceDirectory());
	  System.setProperty(PropertyLoader.exportBaseURLProperty, "file://"+localWorkspace.getDefaultWorkspaceDirectory());
	  frapStudyPanel.setLocalWorkspace(localWorkspace);

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
    menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));

    msave.addActionListener(menuHandler);
    fileMenu.add(msave);
    msave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));

    msaveas.addActionListener(menuHandler);
    fileMenu.add(msaveas);

    fileMenu.addSeparator();
    
    mfileSeries.addActionListener(menuHandler);
    fileMenu.add(mfileSeries);
    
    fileMenu.addSeparator();

    mprint.addActionListener(menuHandler);
    mprint.setEnabled(false);
    fileMenu.add(mprint);
    mprint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_MASK));

    fileMenu.addSeparator();

    menuExit.addActionListener(menuHandler);
    fileMenu.add(menuExit);

    //Edit menu
    JMenu editMenu =new JMenu("Edit");
    editMenu.setMnemonic('E');
    mb.add(editMenu);
   
    mUndo.setActionCommand(UNDO_ACTION_COMMAND);
    mUndo.addActionListener(menuHandler);
    mUndo.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    mUndo.setEnabled(false);
    editMenu.add(mUndo);
    
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
    
    setJMenuBar(mb);
  } // end of setup menu

  public JMenuItem getSaveMenuItem()
  {
	  return msave;
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
	  		menuHandler.actionPerformed(new ActionEvent(menuExit,0,EXIT_ACTION_COMMAND));
	  }
  }

  public boolean close()
  {
      int v=JOptionPane.showConfirmDialog(this, "Do you want to Exit Virtual Frap? Files unsaved will be lost!","Exit?", JOptionPane.YES_NO_OPTION);
      if (v == JOptionPane.YES_OPTION)
      {
    	  return true;
      }
      else
      {
    	  return false;
      }
  }
  
  //basically set frapstudy to frapStudyPanel.
  public void setFrapStudy(FRAPStudy fstudy)
  {
	  frapStudyPanel.setFrapStudy(fstudy,true);
  }
  
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

} // end of class MainFrame