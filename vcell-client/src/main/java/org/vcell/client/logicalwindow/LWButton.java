package org.vcell.client.logicalwindow;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.vcell.util.gui.VCellIcons;

/**
 * Button which pops up list of visible windows
 */
@SuppressWarnings("serial")
public class LWButton extends JButton implements ActionListener {
	final Container owner;
	
	/**
	 * icon used by both LWTopFrame menu (optionally) and this button
	 */
	static final Icon SHOW_WINDOW_MENU_ICON = VCellIcons.SHOW_WINDOWS;

	public LWButton(Container owner) {
		super(SHOW_WINDOW_MENU_ICON);
		this.owner = owner;
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JPopupMenu popup = new JPopupMenu( );
		for (JMenuItem mi : LWTopFrame.activeMenuItems())  {
			popup.add(mi);
		}
		Insets insets = owner.getInsets();
		popup.show(owner, getX() + insets.left, getY( ) + insets.top);
	}
}
