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