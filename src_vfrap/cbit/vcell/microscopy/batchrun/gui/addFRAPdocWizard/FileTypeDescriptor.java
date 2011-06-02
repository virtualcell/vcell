package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import javax.swing.JPanel;

import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_FileTypePanel;

public class FileTypeDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "BATCHRUN_FileType";
    public JPanel fileTypePanel = new LoadFRAPData_FileTypePanel();
    public FileTypeDescriptor() {
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