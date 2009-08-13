package cbit.vcell.microscopy.gui.choosemodelwizard;

import cbit.vcell.client.task.AsynchClientTask;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class ChooseModel_RoiForErrorDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "ChooseModel_RoiForError";
    
    public ChooseModel_RoiForErrorDescriptor () {
        super(IDENTIFIER, new ChooseModel_RoiForErrorPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    
    public String getBackPanelDescriptorID() {
        return ChooseModel_ModelTypesDescriptor.IDENTIFIER;
    }
}
