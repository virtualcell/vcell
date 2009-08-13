package cbit.vcell.microscopy.gui.estparamwizard;

import cbit.vcell.client.task.AsynchClientTask;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_TwoDiffComponentDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_DiffusionWithTwoDiffusingComponents";
    
    public EstParams_TwoDiffComponentDescriptor () {
        super(IDENTIFIER, new EstParams_TwoDiffComponentPanel());
    }

//    public Object getNextPanelDescriptor() {
//        return EstParams_ReacBindingInputDescriptor.IDENTIFIER;
//    }
//    
//    public Object getBackPanelDescriptor() {
//        return EstParams_OneDiffComponentDescriptor.IDENTIFIER;
//    } 
}
