package cbit.vcell.client;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.server.InteractiveClientServerContext;

public class VCellGuiInteractiveContext implements InteractiveClientServerContext {
	private org.vcell.client.logicalwindow.LWModelessWarning cantConnectWarning = null;
	private final cbit.vcell.client.TopLevelWindowManager topLevelWindowManager;
	
	public VCellGuiInteractiveContext(cbit.vcell.client.TopLevelWindowManager topLevelWindowManager) {
		this.topLevelWindowManager = topLevelWindowManager;
	}
	
	

	@Override
	public void showErrorDialog(String errorMessage) {
		// route through DialogUtils so the dialog is owned by the logical window (see showConnectWarning
		// below, which already does this) instead of a throw-away always-on-top JDialog.
		DialogUtils.showErrorDialog(topLevelWindowManager.getComponent(), errorMessage);
	}

	@Override
	public void showWarningDialog(String warningMessage) {
		DialogUtils.showWarningDialog(topLevelWindowManager.getComponent(), warningMessage);
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