package org.vcell.client.logicalwindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.HeadlessException;

import javax.accessibility.AccessibleContext;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.plaf.FileChooserUI;

/**
 * replacement for file chooser that creates {@link LWDialog} instead of default {@link ModalityType#APPLICATION_MODAL} {@link JDialog}
 */
@SuppressWarnings("serial")
public class LWFileChooser extends JFileChooser {
	private static final String DEFAULT_TITLE = "File Chooser" ;
	
	private String menuDesc = DEFAULT_TITLE; 
	
	/**
	 * use title for menu description
	 * @param dialogTitle
	 */
	@Override
	public void setDialogTitle(String dialogTitle) {
		super.setDialogTitle(dialogTitle);
		menuDesc = dialogTitle != null ? dialogTitle : DEFAULT_TITLE;
	}

	@Override
	protected JDialog createDialog(Component parent) throws HeadlessException {
		LWContainerHandle lwParent = LWNamespace.findLWOwner(parent);
		//copied from Java source
		FileChooserUI ui = getUI();
		String title = ui.getDialogTitle(this);
		putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY,
				title);

		JDialog dialog = new ChooserDialog(lwParent, title);
		
		dialog.setComponentOrientation(this.getComponentOrientation());

		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(this, BorderLayout.CENTER);

		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations =
					UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				dialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
			}
		}
		dialog.pack();
		dialog.setLocationRelativeTo(parent);

		return dialog;	
	}
	
	/**
	 * uses {@link JFileChooser#getDialogTitle()} as menu description
	 */
	private class ChooserDialog extends LWDialog {

		@Override
		public String menuDescription() {
			return menuDesc;
		}

		public ChooserDialog(LWContainerHandle parent, String title) {
			super(parent,title);
		}
		
	}

}
