package cbit.vcell.microscopy.gui.estparamwizard;

import cbit.vcell.client.task.AsynchClientTask;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_ReacBindingDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_ReactionPlusBinding";
    
    public EstParams_ReacBindingDescriptor () {
        super(IDENTIFIER, new EstParams_ReacBindingPanel());
    }
    
//    public Object getNextPanelDescriptor() {
//        return EstParams_CompareResultsDescriptor.IDENTIFIER;
//    }
    
    public String getBackPanelDescriptorID() {
        return EstParams_ReacBindingInputDescriptor.IDENTIFIER;
    }
}
