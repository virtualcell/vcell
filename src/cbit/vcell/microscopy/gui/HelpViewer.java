package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.net.URL;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.ImageIcon;
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
public class HelpViewer extends JFrame 
{
	public static final int DEFAULT_HELP_DIALOG_WIDTH = 900;
	public static final int DEFAULT_HELP_DIALOG_HIGHT = 700;
	public static final int DEFAULT_HELP_DIALOG_LOCX = (VirtualFrapMainFrame.SCREEN_WIDTH - DEFAULT_HELP_DIALOG_WIDTH)/2;
	public static final int DEFAULT_HELP_DIALOG_LOCY = (VirtualFrapMainFrame.SCREEN_HEIGHT - DEFAULT_HELP_DIALOG_HIGHT)/2;
	public HelpViewer() {
		super("Virtual Frap Help");
		setIconImage(new ImageIcon(getClass().getResource("/images/logo.gif")).getImage());

		URL resourceURL = HelpViewer.class.getResource("/doc/HelpSet.hs");

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


		setLocation(DEFAULT_HELP_DIALOG_LOCX,DEFAULT_HELP_DIALOG_LOCY);
		setPreferredSize(new Dimension(DEFAULT_HELP_DIALOG_WIDTH,DEFAULT_HELP_DIALOG_HIGHT));
		pack();
		setVisible(true);
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) { }

		new HelpViewer();
	}

}