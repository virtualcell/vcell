package cbit.vcell.microscopy.gui.estparamwizard;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_BleachedROIDescriptor;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_CompareResultsDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_CompareResultsAmongSelectedModels";
	    
    public EstParams_CompareResultsDescriptor () {
        super(IDENTIFIER, new EstParams_CompareResultsPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }

//    public Object getBackPanelDescriptor() {
//        return EstParams_ReacBindingDescriptor.IDENTIFIER;
//    }  
}
