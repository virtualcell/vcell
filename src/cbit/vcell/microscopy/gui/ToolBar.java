package cbit.vcell.microscopy.gui;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * This toolbar contains all the short cut buttons including open, save , print,
 * crop, refresh, curve fitting, runing simulation, report and help etc.
 *
 * @author Tracy Li
 * Created in Jan 2008.
 * @version 1.0 
 */


public class ToolBar extends JToolBar {
    // the image of the shortcut button appearance
	public final static int BUT_OPEN = 0;
	public final static int BUT_SAVE = 1;
	public final static int BUT_PRINT = 2;
	public final static int BUT_HELP = 3;
    private URL[] iconFiles = {getClass().getResource("/images/open.gif"),
							   getClass().getResource("/images/save.gif"),
							   getClass().getResource("/images/printer.gif"),
							   getClass().getResource("/images/help.gif")};

    private String[] buttonLabels = {"Open File", "Save File", "Print","Help"};
    //icon objects for initializing the image button
    private ImageIcon[] icons = new ImageIcon[iconFiles.length];
    private JButton[] buttons = new JButton[iconFiles.length];
    
    //constructor
    public ToolBar() {
        // add short cut bottons on toolbar
        for (int i = 0; i < buttonLabels.length; ++i) {
            //icons[i] = JarReader.getJarImage(iconFiles[i].toString());
            icons[i] = new ImageIcon(iconFiles[i]);
            buttons[i] = new JButton(icons[i]);
            buttons[i].setMargin(new Insets(0, 0, 0, 0));
            buttons[i].setSize(100, 100);
            buttons[i].setToolTipText(buttonLabels[i]);
            if (i == BUT_PRINT) buttons[i].setEnabled(false);
            if (i == BUT_HELP) addSeparator();
            add(buttons[i]);
            if(i == BUT_SAVE){
            	buttons[i].setEnabled(false);
            }
        }
    }// end of constructor
    
    public JButton[] getButtons()
    {
    	return buttons;
    }
    
    //Find button's index in bar
    public int findIndex(JButton butt)
    {
        for(int i=0;i<buttons.length;i++)
        {
            if(butt==buttons[i])
            {
            	return i;
            }
        }
        return -1;
    }// end of method findIndex()
    
    public void addToolBarHandler(ActionListener th)
    {
    	for(int i=0;i<buttons.length;i++)
    	{
    		buttons[i].addActionListener(th);
    	}
    }// end of method addToolBarHandler()
    
} // End of class ToolBar