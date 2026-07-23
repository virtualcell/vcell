package cbit.vcell.client;

import cbit.vcell.client.desktop.ACLEditor;

import javax.swing.*;
import java.awt.*;

public class AccessPermissionDialogProxy {
	private final JOptionPane mainPane;

	public JDialog dialog;


	public AccessPermissionDialogProxy(String title, final ACLEditor aclEditor, Component requester) {
		// Set up buttons for interactive enabling
		javax.swing.JButton parentConfirmChangesJButton = aclEditor.regenerateParentConfirmChangesJButton();
		javax.swing.JButton parentCancelChangesJButton = aclEditor.regenerateParentCancelChangesJButton();

		this.mainPane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null,
				new Object[]{parentConfirmChangesJButton, parentCancelChangesJButton});
		parentConfirmChangesJButton.addActionListener(e -> this.mainPane.setValue(parentConfirmChangesJButton));
		parentCancelChangesJButton.addActionListener(e -> this.mainPane.setValue(parentCancelChangesJButton));
		aclEditor.setPreferredSize(new java.awt.Dimension(300, 400));
		this.mainPane.setMessage("");
		this.mainPane.setMessage(aclEditor);
		this.mainPane.setValue(null);
		this.dialog = this.mainPane.createDialog(requester, title);
		this.dialog.setResizable(true);
		this.dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public Object getUserResult(){
		return this.mainPane.getValue();
	}
}
