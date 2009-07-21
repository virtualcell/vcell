package org.vcell.wizard;

/**
 * This class is created for the finish descriptor, which
 * takes "Finish" as identifier and it's conponent panel is null.
 */
public class FinishDescriptor extends WizardPanelDescriptor
{
	private static final String FINISH_IDENTIFIER = "Finish";
	
	public FinishDescriptor()
	{
		super(FINISH_IDENTIFIER, null);
	}
}
