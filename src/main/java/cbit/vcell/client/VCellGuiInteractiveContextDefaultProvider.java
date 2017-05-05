package cbit.vcell.client;

import cbit.vcell.client.server.ClientServerManager.InteractiveContext;
import cbit.vcell.client.server.ClientServerManager.InteractiveContextDefaultProvider;

public class VCellGuiInteractiveContextDefaultProvider implements InteractiveContextDefaultProvider {

	@Override
	public InteractiveContext getInteractiveContext() {
		return new VCellGuiInteractiveContext(TopLevelWindowManager.activeManager());
	}

}
