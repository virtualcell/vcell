package cbit.vcell.microscopy.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * The AbuoutDialog is the same as splash screen. It will only be
 * dissappeared by mouse clicking.
 *
 * @author Tracy LI
 * @version 1.0 Alpha
 */

public class AboutDialog extends JWindow
{
    public AboutDialog(URL filename, Frame f)
    {
        super(f);
        JLabel l = new JLabel(new ImageIcon(filename));
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize =
          Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
        addMouseListener(new MouseAdapter()
            {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		super.mouseClicked(e);
        		dispose();
        	}
            });
        setVisible(true);
    }
}
