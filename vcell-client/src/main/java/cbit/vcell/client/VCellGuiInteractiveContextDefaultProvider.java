package cbit.vcell.client;

import org.vcell.api.server.ClientServerManager.InteractiveContextDefaultProvider;
import cbit.vcell.client.server.InteractiveClientServerContext;

public class VCellGuiInteractiveContextDefaultProvider implements InteractiveContextDefaultProvider {

	@Override
	public InteractiveClientServerContext getInteractiveContext() {
		return new VCellGuiInteractiveContext(TopLevelWindowManager.activeManager());
	}

}
