package cbit.vcell.microscopy.gui;

import java.awt.Font;
import java.io.File;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.resource.ResourceUtil;

public class VirtualFrapLoader {
	
	public static class CheckThreadViolationRepaintManager extends RepaintManager {
	    // it is recommended to pass the complete check  
	    private boolean completeCheck = true;

	    public boolean isCompleteCheck() {
	        return completeCheck;
	    }

	    public void setCompleteCheck(boolean completeCheck) {
	        this.completeCheck = completeCheck;
	    }

	    public synchronized void addInvalidComponent(JComponent component) {
	        checkThreadViolations(component);
	        super.addInvalidComponent(component);
	    }

	    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
	        checkThreadViolations(component);
	        super.addDirtyRegion(component, x, y, w, h);
	    }

	    private void checkThreadViolations(JComponent c) {
	        if (!SwingUtilities.isEventDispatchThread() && (completeCheck || c.isShowing())) {
	            Exception exception = new Exception();
	            boolean repaint = false;
	            boolean fromSwing = false;
	            StackTraceElement[] stackTrace = exception.getStackTrace();
	            for (StackTraceElement st : stackTrace) {
	                if (repaint && st.getClassName().startsWith("javax.swing.")) {
	                    fromSwing = true;
	                }
	                if ("repaint".equals(st.getMethodName())) {
	                    repaint = true;
	                }
	            }
	            if (repaint && !fromSwing) {
	                //no problems here, since repaint() is thread safe
	                return;
	            }
	            exception.printStackTrace();
	        }
	    }
	}
	
	//get paths
	//get current working directory
	//filefilters for VFrap
	public static final String VFRAP_EXTENSION = "vfrap";
	public static final String LSM_EXTENSION = "lsm";
	public static final String TIFF_EXTENSION = "tif";
	public static final String QT_EXTENSION = "mov";
	public final static  VirtualFrapMainFrame.AFileFilter filter_lsm = new VirtualFrapMainFrame.AFileFilter(LSM_EXTENSION,"Zeiss Lsm Images");
	public final static  VirtualFrapMainFrame.AFileFilter filter_tif = new VirtualFrapMainFrame.AFileFilter(TIFF_EXTENSION, "TIFF Images");
	public final static  VirtualFrapMainFrame.AFileFilter filter_vfrap = new VirtualFrapMainFrame.AFileFilter(VFRAP_EXTENSION,"Virtual FRAP Files");
	public final static  VirtualFrapMainFrame.AFileFilter filter_qt = new VirtualFrapMainFrame.AFileFilter(QT_EXTENSION,"Quick Time Movie Files");
    //create one instance of each kind of filechooser, so that it remembers the last time visited path. 
    public static JFileChooser openVFRAPFileChooser; 
    public static JFileChooser loadFRAPImageFileChooser;
    public static JFileChooser saveFileChooser; 
    public static JFileChooser multiOpenFileChooser; 
    public static JFileChooser saveMovieFileChooser;
    //set default font 
    public static Font defaultFont = new Font("Tahoma", Font.PLAIN, 11); 
    //set the only one instance of the main frame 
    public static VirtualFrapMainFrame mf; 
	
	public static void main(final String[] args)
	{
		try { 
			if(args.length != 0 && args.length != 1){
				System.out.println("Usage: progName [workingDirectory]");
				System.exit(1);
			}
			File wd = null;
			if (args.length == 0) {
				wd = ResourceUtil.userHome;
			} else {
				wd = new File(args[0]);
			}
			final File workingDirectory = wd;
			RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager()); 

			SwingUtilities.invokeLater(new Runnable(){public void run(){
				LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			    FRAPWorkspace frapWorkspace = new FRAPWorkspace();
			    
			    //Check swing availability 
			    String vers = System.getProperty("java.version"); 
			    if (vers.compareTo("1.1.2") < 0) 
			    { 
			    	System.out.println("!!!WARNING: Swing must be run with a 1.1.2 or higher version JVM!!!"); 
			    } 
			    /* Set Look and Feel */ 
			    try{
			    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    }catch(Exception e){
			    	throw new RuntimeException(e.getMessage(),e);
			    }
			    //set up file choosers 
			    openVFRAPFileChooser = new JFileChooser(); 
			    openVFRAPFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory())); 
			    openVFRAPFileChooser.addChoosableFileFilter(filter_vfrap); 
			    openVFRAPFileChooser.setAcceptAllFileFilterUsed(false);
			    loadFRAPImageFileChooser = new JFileChooser(); 
			    loadFRAPImageFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory())); 
			    loadFRAPImageFileChooser.addChoosableFileFilter(filter_tif);
			    loadFRAPImageFileChooser.addChoosableFileFilter(filter_lsm); 
			    saveFileChooser = new JFileChooser();
			    saveFileChooser.addChoosableFileFilter(filter_vfrap); 
			    saveFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    multiOpenFileChooser = new JFileChooser(); 
			    multiOpenFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    multiOpenFileChooser.addChoosableFileFilter(filter_tif);
			    multiOpenFileChooser.setMultiSelectionEnabled(true);
			    saveMovieFileChooser = new JFileChooser();
			    saveMovieFileChooser.addChoosableFileFilter(filter_qt);
			    saveMovieFileChooser.setAcceptAllFileFilterUsed(false);
			    saveMovieFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
	            
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
							
				mf = new VirtualFrapMainFrame(localWorkspace, frapWorkspace);
				mf.setMainFrameTitle("");
				mf.setVisible(true);
			
				//initialize FRAPStudy
				FRAPStudy fStudy = new FRAPStudy();
				frapWorkspace.setFrapStudy(fStudy, true);
			
				try {
					Thread.sleep(30);
				}catch (InterruptedException e){}
			}});
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}
	
//	public final static Object loadProperty(String propName)
//	{
//		Object result = "";
//		try { 
//		            
//            PropertyResourceBundle resources = (PropertyResourceBundle)
//                ResourceBundle.getBundle("cbit.vcell.microscopy.vfrap");
//            
//            result = resources.getObject(propName);
//        
//        } catch (MissingResourceException mre) {
//        	mre.printStackTrace(System.out);
//			PopupGenerator.showErrorDialog("New user Registration error:\n"+mre.getMessage());
////            System.exit(1);
//        }
//        return result;
//	}

}
