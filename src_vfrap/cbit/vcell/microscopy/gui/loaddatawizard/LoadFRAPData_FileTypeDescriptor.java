/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.loaddatawizard;

import javax.swing.JPanel;

import org.vcell.wizard.WizardPanelDescriptor;

public class LoadFRAPData_FileTypeDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "LoadFRAPData_FileType";
    public JPanel fileTypePanel = new LoadFRAPData_FileTypePanel();
    public LoadFRAPData_FileTypeDescriptor() {
        super();
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(fileTypePanel);
    }
	   
//    public Object getNextPanelDescriptor() {
//    	if(getPanelComponent() instanceof LoadFRAPData_FileTypePanel)
//    	{
//    		if(((LoadFRAPData_FileTypePanel)getPanelComponent()).isMultipleFileSelected())
//    		{
//    			return LoadFRAPData_MultiFileDescriptor.IDENTIFIER;
//    		}
//    		else
//    		{
//    			return LoadFRAPData_SingleFileDescriptor.IDENTIFIER;
//    		}
//    	}
//        return TestPanel2Descriptor.IDENTIFIER;
//    }
    //This is to make sure that it backs to nowhere.
//    public Object getBackPanelDescriptor() {
//        return null;
//    }  
    
}
