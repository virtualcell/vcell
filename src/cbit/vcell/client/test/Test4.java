package cbit.vcell.client.test;
import cbit.vcell.client.desktop.biomodel.*;
import java.awt.*;
import javax.swing.*;
import cbit.gui.*;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 12:05:09 AM)
 * @author: Ion Moraru
 */
public class Test4 {

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
    // Insert code to start the application here.
    try {
        javax.swing.UIManager.setLookAndFeel(
            javax.swing.UIManager.getSystemLookAndFeelClassName());
		JFrame frame = new javax.swing.JFrame();
		BioModelEditor aBioModelEditor;
		aBioModelEditor = new BioModelEditor();
		frame.setContentPane(aBioModelEditor);
		frame.setSize(aBioModelEditor.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
//		aBioModelEditor.getjInternalFrameApplication().setBorder(null);
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		System.out.println(aBioModelEditor.getjInternalFrameApplication());

//System.out.println(javax.swing.JOptionPane.UNINITIALIZED_VALUE);
//System.out.println(cbit.vcell.client.PopupGenerator.showInputDialog(null, "test"));
/*		java.awt.Frame ff = null;
		javax.swing.JFrame f1 = new javax.swing.JFrame("frame 1");
		javax.swing.JFrame f2 = new javax.swing.JFrame("frame 2");
		final javax.swing.JDialog d1 = new javax.swing.JDialog(ff, "dialog 1", true);
		final javax.swing.JDialog d2 = new javax.swing.JDialog(ff, "dialog 2", true);
		final javax.swing.JDialog d3 = new javax.swing.JDialog(ff, "dialog 3", true);
		final javax.swing.JDialog d4 = new javax.swing.JDialog(ff, "dialog 4", true);
f1.setSize(300,200); f1.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
f2.setSize(400,300); f2.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
d1.setSize(500,400); d1.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
d2.setSize(200,100); d2.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
d3.setSize(100,100); d3.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
d4.setSize(400,50); d4.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
		f1.show();
		//cbit.gui.ZEnforcer.showOnTop(f1);
		f2.show();
		//cbit.gui.ZEnforcer.showOnTop(f2);
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							ZEnforcer.showModalDialogOnTop(d1);
						}
					});
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		});
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							ZEnforcer.showModalDialogOnTop(d2);
						}
					});
				} catch (Exception e) {
					System.out.println(e);
				}
				//d1.show();
			}
		});
		Thread t3 = new Thread(new Runnable() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							ZEnforcer.showModalDialogOnTop(d3);
						}
					});
				} catch (Exception e) {
					System.out.println(e);
				}
				//d1.show();
			}
		});
		t1.start();
		t2.start();
		t3.start();
		cbit.gui.ZEnforcer.showModalDialogOnTop(d4);
		//javax.swing.JOptionPane pane = new javax.swing.JOptionPane("test");
		//javax.swing.JDialog d = pane.createDialog(null, "t");
		//d.show();
		//d2.show();
		cbit.gui.ZEnforcer.showModalDialogOnTop(d2);
		//f1.dispose();*/
    } catch (Exception e) {
        System.out.println(e);
    }
}
}