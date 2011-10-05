package org.vcell.documentation;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * This helpviewer enables navigate virtual frap help through table of contents. 
 * The contents are displayed as html files which enable hyperlinks.
 * In addition, the helpviewer provides word search.
 * JavaHelp map, TOC, index, and HelpSet files have to be created to make helpviewer work. 
 * @author Tracy LI
 * Created in June 2008.
 * @version 1.0
 */
@SuppressWarnings("serial")
public class VcellHelp extends JFrame 
{
	public static final int DEFAULT_HELP_DIALOG_WIDTH = 900;
	public static final int DEFAULT_HELP_DIALOG_HIGHT = 700;
	private static final int DEFAULT_HELP_DIALOG_LOCX = 300;
	private static final int DEFAULT_HELP_DIALOG_LOCY = 200;
	
	public VcellHelp() {
		super("Virtual Cell Help");
//		setIconImage(new ImageIcon("//vcell.gif").getImage());

		URL resourceURL = VcellHelp.class.getResource("/vcellDoc/HelpSet.hs");

		Container contentPane = getContentPane();
		//URL hsURL;
		HelpSet hs;
		try {
			// get the system class loader
			ClassLoader cl = this.getClass().getClassLoader();
			// create helpset
			hs = new HelpSet(cl, resourceURL);
			JHelp jhelp = new JHelp(hs);
			contentPane.setLayout(new BorderLayout());
			contentPane.add(jhelp);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
//				System.exit(0);
				VcellHelp.this.dispose();
			}
		});
		
		setLocation(DEFAULT_HELP_DIALOG_LOCX,DEFAULT_HELP_DIALOG_LOCY);
		setPreferredSize(new Dimension(DEFAULT_HELP_DIALOG_WIDTH,DEFAULT_HELP_DIALOG_HIGHT));
		pack();
		setVisible(true);
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) { }

		new VcellHelp();
	}

}