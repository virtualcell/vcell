package cbit.vcell.client;

import cbit.vcell.client.server.ClientServerManager.InteractiveContextDefaultProvider;
import cbit.vcell.client.server.InteractiveClientServerContext;

public class VCellGuiInteractiveContextDefaultProvider implements InteractiveContextDefaultProvider {

	@Override
	public InteractiveClientServerContext getInteractiveContext() {
		return new VCellGuiInteractiveContext(TopLevelWindowManager.activeManager());
	}

}
