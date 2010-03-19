package org.vcell.sybil.gui.space;

/*   GUIScrollSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to January 2009
 *   An interface for scroll panes
 */

import javax.swing.JScrollPane;

import org.vcell.sybil.util.ui.UIScrollSpace;

public class GUIScrollSpace<P extends JScrollPane> extends GUISpace<P> implements UIScrollSpace {

	public GUIScrollSpace(P newPane) { super(newPane); }

	public P scrollPane() { return component(); }

}
