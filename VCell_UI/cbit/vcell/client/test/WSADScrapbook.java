package cbit.vcell.client.test;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 1:48:21 AM)
 * @author: Ion Moraru
 */
public class WSADScrapbook {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	// Insert code to start the application here.
	
	try {
cbit.vcell.desktop.BioModelDbTreePanel bmt = new cbit.vcell.desktop.BioModelDbTreePanel();
bmt.setPreferredSize(new java.awt.Dimension(200, 400));
javax.swing.JOptionPane op = new javax.swing.JOptionPane(bmt, javax.swing.JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Open", "Cancel"});
javax.swing.JDialog d = op.createDialog(null, "Select BioModel:");
d.setResizable(false);
d.show();
System.out.println(op.getValue());
	} catch (Throwable exc) {
		System.out.println(exc);
	} finally {
		//System.exit(0);
	}
}
}