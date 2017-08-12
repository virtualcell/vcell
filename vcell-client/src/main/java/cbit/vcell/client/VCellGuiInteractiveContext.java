package cbit.vcell.client;

import cbit.vcell.client.server.ClientServerManager.InteractiveContext;

public class VCellGuiInteractiveContext implements InteractiveContext {
	private org.vcell.client.logicalwindow.LWModelessWarning cantConnectWarning = null;
	private final cbit.vcell.client.TopLevelWindowManager topLevelWindowManager;
	
	public VCellGuiInteractiveContext(cbit.vcell.client.TopLevelWindowManager topLevelWindowManager) {
		this.topLevelWindowManager = topLevelWindowManager;
	}
	
	

	@Override
	public void showErrorDialog(String errorMessage) {
		cbit.vcell.client.PopupGenerator.showErrorDialog(topLevelWindowManager.getComponent(), errorMessage);
	}

	@Override
	public void showWarningDialog(String warningMessage) {
		cbit.vcell.client.PopupGenerator.showWarningDialog(topLevelWindowManager.getComponent(), warningMessage);
	}

	@Override
	public void clearConnectWarning() {
		if (cantConnectWarning != null) { //clear warning message if it is up
			cantConnectWarning.dispose();
			cantConnectWarning = null;
		}
	}

	@Override
	public void showConnectWarning(String message) {
		org.vcell.client.logicalwindow.LWContainerHandle lwParent = org.vcell.client.logicalwindow.LWNamespace.findLWOwner(topLevelWindowManager.getComponent());
		if (cantConnectWarning == null) {
			cantConnectWarning = new org.vcell.client.logicalwindow.LWModelessWarning(lwParent,message);
		}
		else {
			cantConnectWarning.setMessage(message);
		}
		cantConnectWarning.setVisible(true);
	}
	
}