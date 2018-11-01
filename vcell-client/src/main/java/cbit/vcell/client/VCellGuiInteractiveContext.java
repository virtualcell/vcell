package cbit.vcell.client;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import cbit.vcell.client.server.ClientServerManager.InteractiveContext;

public class VCellGuiInteractiveContext implements InteractiveContext {
	private org.vcell.client.logicalwindow.LWModelessWarning cantConnectWarning = null;
	private final cbit.vcell.client.TopLevelWindowManager topLevelWindowManager;
	
	public VCellGuiInteractiveContext(cbit.vcell.client.TopLevelWindowManager topLevelWindowManager) {
		this.topLevelWindowManager = topLevelWindowManager;
	}
	
	

	@Override
	public void showErrorDialog(String errorMessage) {
		JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(dialog, errorMessage, "Error...", JOptionPane.ERROR_MESSAGE, null);
	}

	@Override
	public void showWarningDialog(String warningMessage) {
		JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(dialog, warningMessage, "Warning...", JOptionPane.WARNING_MESSAGE, null);
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