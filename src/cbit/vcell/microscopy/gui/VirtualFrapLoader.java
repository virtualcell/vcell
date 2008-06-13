package cbit.vcell.microscopy.gui;

import java.awt.Font;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import cbit.vcell.microscopy.FRAPStudy;

public class VirtualFrapLoader 
{
	//get paths
	public static final String appPath = System.getProperty("user.home") + System.getProperty("file.separator") + "VirtualMicroscopy" + System.getProperty("file.separator");
	//get current working directory
	//filefilters for VFrap
	public final static  VirtualFrapMainFrame.AFileFilter filter_lsm = new VirtualFrapMainFrame.AFileFilter("lsm","Zeiss Lsm Images");
	public final static  VirtualFrapMainFrame.AFileFilter filter_tif = new VirtualFrapMainFrame.AFileFilter("tif", "TIFF Images");
    
	public final static  VirtualFrapMainFrame.AFileFilter filter_xml = new VirtualFrapMainFrame.AFileFilter("xml","XML Files");
    //create one instance of each kind of filechooser, so that it remembers the last time visited path. 
    public static JFileChooser openFileChooser; 
    public static JFileChooser saveFileChooser; 
    public static JFileChooser multiOpenFileChooser; 
    //set default font 
    public static Font defaultFont = new Font("Tahoma", Font.PLAIN, 11); 
    //set the only one instance of the main frame 
    public static VirtualFrapMainFrame mf; 
	
	public static void main(String[] args)
	{
		try { 
		    FRAPStudy frapStudy = null; 
		    frapStudy = new FRAPStudy(); 
		
		    //Check swing availability 
		    String vers = System.getProperty("java.version"); 
		    if (vers.compareTo("1.1.2") < 0) 
		    { 
		    	System.out.println("!!!WARNING: Swing must be run with a 1.1.2 or higher version JVM!!!"); 
		    } 
		    /* Set Look and Feel */ 
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		    //set up file choosers 
		    openFileChooser = new JFileChooser(); 
		    openFileChooser.setCurrentDirectory(new File(appPath)); 
		    openFileChooser.addChoosableFileFilter(filter_lsm); 
		    openFileChooser.addChoosableFileFilter(filter_tif); 
		    openFileChooser.addChoosableFileFilter(filter_xml); 
		    saveFileChooser = new JFileChooser();
		    saveFileChooser.addChoosableFileFilter(filter_xml); 
		    saveFileChooser.setCurrentDirectory(new File(appPath));
		    multiOpenFileChooser = new JFileChooser(); 
		    multiOpenFileChooser.setCurrentDirectory(new File(appPath));
		    multiOpenFileChooser.setMultiSelectionEnabled(true);

            
            // setup component font
	        UIManager.put ("InternalFrame.titleFont", defaultFont);
	        UIManager.put ("ToolBar.font", defaultFont);
	        UIManager.put ("Menu.font", defaultFont);
	        UIManager.put ("MenuItem.font", defaultFont);
	        UIManager.put ("Button.font",defaultFont);
	        UIManager.put ("CheckBox.font",defaultFont);
	        UIManager.put ("RadioButton.font",defaultFont);
	        UIManager.put ("ComboBox.font",defaultFont);
	        UIManager.put ("TextField.font",defaultFont);
	        UIManager.put ("TextArea.font",defaultFont);
	        UIManager.put ("TabbedPane.font",defaultFont);
	        UIManager.put ("Panel.font",defaultFont);
	        UIManager.put ("Label.font",defaultFont);
	        UIManager.put ("List.font",defaultFont);
	        UIManager.put ("Table.font",defaultFont);
	        UIManager.put ("TitledBorder.font",defaultFont);
	        UIManager.put ("OptionPane.font",defaultFont);
	        UIManager.put ("FileChooser.font", defaultFont);
						
			mf = new VirtualFrapMainFrame();
			mf.setMainFrameTitle("");
			mf.setVisible(true);
			
			try {
				Thread.sleep(30);
			}catch (InterruptedException e){}
			
			mf.setFrapStudy(frapStudy);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

}
