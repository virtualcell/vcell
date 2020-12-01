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

import java.awt.Font;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.vcell.client.logicalwindow.LWFileChooser;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.resource.ResourceUtil;

public class VirtualFrapLoader {
	
	//get paths
	//get current working directory
	//filefilters for VFrap
	public final static String VFRAP_EXTENSION = "vfrap";
	public final static String MAT_EXTENSION = "mat";
 	public final static String LSM_EXTENSION = "lsm";
	public final static String TIFF_EXTENSION = "tif";
	public final static String QT_EXTENSION = "mov";
	public final static String VFRAP_BATCH_EXTENSION = "vfbatch";
	public final static VirtualFrapMainFrame.AFileFilter filter_lsm = new VirtualFrapMainFrame.AFileFilter(LSM_EXTENSION,"Zeiss Lsm Images");
	public final static VirtualFrapMainFrame.AFileFilter filter_tif = new VirtualFrapMainFrame.AFileFilter(TIFF_EXTENSION, "TIFF Images");
	public final static VirtualFrapMainFrame.AFileFilter filter_vfrap = new VirtualFrapMainFrame.AFileFilter(VFRAP_EXTENSION,"Virtual FRAP Files");
	public final static VirtualFrapMainFrame.AFileFilter filter_mat = new VirtualFrapMainFrame.AFileFilter(MAT_EXTENSION,"Matlab data Files");
	public final static VirtualFrapMainFrame.AFileFilter filter_qt = new VirtualFrapMainFrame.AFileFilter(QT_EXTENSION,"Quick Time Movie Files");
	public final static VirtualFrapMainFrame.AFileFilter filter_vfbatch = new VirtualFrapMainFrame.AFileFilter(VFRAP_BATCH_EXTENSION,"Virtual FRAP BatchRun Files");
    //create one instance of each kind of filechooser, so that it remembers the last time visited path. 
    public static JFileChooser openVFRAPFileChooser; 
    public static JFileChooser openVFRAPBatchRunChooser;
    public static JFileChooser loadFRAPImageFileChooser;
    public static JFileChooser addDataFileChooser_batchRun;
    public static JFileChooser saveFileChooser; 
    public static JFileChooser saveAsMatFileChooser;
    public static JFileChooser saveFileChooser_batchRun;
    public static JFileChooser saveFileChooser_batchRunSaveSingleFileAs;
    public static JFileChooser multiOpenFileChooser; 
    public static JFileChooser saveMovieFileChooser;
    //set default font 
    public final static Font defaultFont = new Font("Tahoma", Font.PLAIN, 11); 
    //the only one instance of the main frame 
    public static VirtualFrapMainFrame mf; 
	//the function is called when activate in VCell or as standalone.
	public static void loadVFRAP(final String[] args, final boolean bStandalone,final DocumentWindowManager documentWindowManager)
	{
		try { 
			File wd = null;
			if (args == null || args.length == 0) {
				wd = ResourceUtil.getUserHomeDir();
			} else {
				wd = new File(args[0]);
			}
			final File workingDirectory = wd;

			SwingUtilities.invokeLater(new Runnable(){public void run(){
				LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			    FRAPSingleWorkspace frapWorkspace = new FRAPSingleWorkspace();
			    FRAPBatchRunWorkspace batchRunWorkspace = new FRAPBatchRunWorkspace();
			    
			    //Check swing availability 
			    String vers = System.getProperty("java.version"); 
			    if (vers.compareTo("1.1.2") < 0) 
			    { 
			    	System.out.println("!!!WARNING: Swing must be run with a 1.1.2 or higher version JVM!!!"); 
			    } 
			    if (bStandalone) {
					/* Set Look and Feel */
					try {
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (Exception e) {
						throw new RuntimeException(e.getMessage(), e);
					} 
				}
				//set up file choosers 
			    openVFRAPFileChooser = new LWFileChooser(); 
			    openVFRAPFileChooser.setDialogTitle("Open Virtual FRAP document (.vfrap)");
			    openVFRAPFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory())); 
			    openVFRAPFileChooser.addChoosableFileFilter(filter_vfrap); 
			    openVFRAPFileChooser.setAcceptAllFileFilterUsed(false);
			    openVFRAPBatchRunChooser = new LWFileChooser(); 
			    openVFRAPBatchRunChooser.setDialogTitle("Open Virtual FRAP batchrun document (.vfbatch)");
			    openVFRAPBatchRunChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory())); 
			    openVFRAPBatchRunChooser.addChoosableFileFilter(filter_vfbatch); 
			    openVFRAPBatchRunChooser.setAcceptAllFileFilterUsed(false);
			    loadFRAPImageFileChooser = new LWFileChooser(); 
			    loadFRAPImageFileChooser.setDialogTitle("Load FRAP image file");
			    loadFRAPImageFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory())); 
			    loadFRAPImageFileChooser.addChoosableFileFilter(filter_tif);
			    loadFRAPImageFileChooser.addChoosableFileFilter(filter_lsm); 
			    addDataFileChooser_batchRun = new LWFileChooser(); 
			    addDataFileChooser_batchRun.setDialogTitle("Add a Virtual FRAP docmument or FRAP image file to batchrun");
			    addDataFileChooser_batchRun.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    addDataFileChooser_batchRun.addChoosableFileFilter(filter_vfrap);
			    addDataFileChooser_batchRun.addChoosableFileFilter(filter_tif);
			    addDataFileChooser_batchRun.addChoosableFileFilter(filter_lsm);
			    saveFileChooser = new LWFileChooser();
			    saveFileChooser.setDialogTitle("Save Virtual FRAP document (.vfrap)");
			    saveFileChooser.addChoosableFileFilter(filter_vfrap); 
			    saveFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    saveAsMatFileChooser = new LWFileChooser();
			    saveAsMatFileChooser.setDialogTitle("Save image data to matlab file (.mat)");
			    saveAsMatFileChooser.addChoosableFileFilter(filter_mat); 
			    saveAsMatFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    saveFileChooser_batchRun = new LWFileChooser();
			    saveFileChooser_batchRun.setDialogTitle("Save Virtual FRAP batchrun document (.vfbatch)");
			    saveFileChooser_batchRun.addChoosableFileFilter(filter_vfbatch); 
			    saveFileChooser_batchRun.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    saveFileChooser_batchRunSaveSingleFileAs= new LWFileChooser();
			    saveFileChooser_batchRunSaveSingleFileAs.addChoosableFileFilter(filter_vfrap); 
			    saveFileChooser_batchRunSaveSingleFileAs.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    multiOpenFileChooser = new LWFileChooser(); 
			    multiOpenFileChooser.setDialogTitle("Open FRAP image series");
			    multiOpenFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
			    multiOpenFileChooser.addChoosableFileFilter(filter_tif);
			    multiOpenFileChooser.setMultiSelectionEnabled(true);
			    saveMovieFileChooser = new LWFileChooser();
			    saveMovieFileChooser.setDialogTitle("Save to quick time movie (.mov)");
			    saveMovieFileChooser.addChoosableFileFilter(filter_qt);
			    saveMovieFileChooser.setAcceptAllFileFilterUsed(false);
			    saveMovieFileChooser.setCurrentDirectory(new File(localWorkspace.getDefaultWorkspaceDirectory()));
	            
			    if(mf == null)
			    {
			    	mf = new VirtualFrapMainFrame(localWorkspace, frapWorkspace, batchRunWorkspace, bStandalone,documentWindowManager);
			    }
//				System.out.println(mf.getLocation().x + "---"+mf.getLocation().y);
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
	
	//loading as standalone. UIManager is needed when loading as standalone.
	public static void main(final String[] args)
	{
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
        
		RepaintManager.setCurrentManager(new VCellClient.CheckThreadViolationRepaintManager()); 

		loadVFRAP(args, true,null);
	}
}
