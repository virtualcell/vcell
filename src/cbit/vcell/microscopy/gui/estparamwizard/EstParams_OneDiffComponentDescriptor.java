package cbit.vcell.microscopy.gui.estparamwizard;

import cbit.vcell.client.task.AsynchClientTask;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_OneDiffComponentDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_DiffusionWithOneDiffusingComponent";
    
    public EstParams_OneDiffComponentDescriptor () {
        super(IDENTIFIER, new EstParams_OneDiffComponentPanel());
    }

//    public Object getNextPanelDescriptor() {
//        return EstParams_TwoDiffComponentDescriptor.IDENTIFIER;
//    }
//    
//    public Object getBackPanelDescriptor() {
//        return null;
//    } 
	
}
