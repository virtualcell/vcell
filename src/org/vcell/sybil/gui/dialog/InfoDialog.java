package org.vcell.sybil.gui.dialog;

/*   Info Dialog  --- by Oliver Ruebenacker, UCHC --- September 2008 to February 2010
 *   A dialog for displaying information about Sybil.
 */

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class InfoDialog extends JDialog {
	
	public static interface Factory<D extends InfoDialog> { 
		public D create(Component component); 
	}

	public InfoDialog() { super(); }
	public InfoDialog(JFrame frameNew, String title) { super(frameNew, title, true); }
	public InfoDialog(JDialog dialogNew, String title) { super(dialogNew, title, true); }

	public abstract void showDialog();
	public abstract void finished();
	
}
