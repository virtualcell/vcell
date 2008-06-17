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
public class HelpViewer extends JFrame {

/**
* Prints the usage info and exits.
*/
private static void printUsage(){
       System.out.println("\nUsage: "+ "java HelpViewer "+ "resourcepath helpsetname");
}

/* *
* Default constructor to create a Java helpset Viewer with
* the given helpset name in the given resource path.
*
* @param String resourcePath - the path of the dir or the jar file where the
* java help system files are located.
* @param String helpsetName - the helpset name without prefix.
* Prefix is assumed to be .hs
*/

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

    setLocation(480,120);
    setPreferredSize(new Dimension(850,800));
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