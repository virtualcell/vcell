package cbit.vcell.microscopy.gui.estparamwizard;

import cbit.vcell.client.task.AsynchClientTask;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_ReacBindingInputDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_ReactionPlusBindingInput";
    
    public EstParams_ReacBindingInputDescriptor () {
        super(IDENTIFIER, new EstParams_ReacBindingInputPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return EstParams_ReacBindingDescriptor.IDENTIFIER;
    }
    
//    public Object getBackPanelDescriptor() {
//        return EstParams_TwoDiffComponentDescriptor.IDENTIFIER;
//    } 

}
