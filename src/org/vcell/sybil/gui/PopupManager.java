package org.vcell.sybil.gui;

/*   GUIManager  --- by Oliver Ruebenacker, UCHC --- October 2007
 *   Manages the popup menu
 */

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.vcell.sybil.actions.ActionMap;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.graph.edit.GraphPopupActions;
import org.vcell.sybil.gui.graph.GraphPane;
import org.vcell.sybil.util.enumerations.SmartEnum;


public class PopupManager implements MouseListener {

	protected JPopupMenu menu = new JPopupMenu("Edit");

	public PopupManager(ActionMap actionMapNew) {
		SmartEnum<BaseAction> actions = actionMapNew.actions(GraphPopupActions.class);
		while(actions.hasMoreElements()) {
			menu.add(new JMenuItem(actions.nextElement()));
			if(actions.isAtInternalBoundary()) { menu.addSeparator(); }
		}
	}

	public void mouseClicked(MouseEvent event) { considerShowingPopup(event); }
	public void mouseEntered(MouseEvent event) { considerShowingPopup(event); }
	public void mouseExited(MouseEvent event) { considerShowingPopup(event); }
	public void mousePressed(MouseEvent event) { considerShowingPopup(event); }
	public void mouseReleased(MouseEvent event) { considerShowingPopup(event); }

	protected void considerShowingPopup(MouseEvent event) {
		if(event.isPopupTrigger()) { showPopup(event); }
	}

	protected void showPopup(MouseEvent event) {
		Component comp = event.getComponent();
		if(comp instanceof GraphPane) { 
			GraphPane pane = (GraphPane) comp;
			pane.graph().setChoice(event.getPoint());
		}
		menu.show(event.getComponent(), event.getX(), event.getY());
	}

}
