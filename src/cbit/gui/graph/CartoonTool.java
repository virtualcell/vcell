package cbit.gui.graph;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.ButtonGroupCivilized;
import org.vcell.util.gui.JDesktopPaneEnhanced;

import cbit.gui.graph.actions.CartoonToolEditActions;
import cbit.gui.graph.actions.CartoonToolMiscActions;
import cbit.gui.graph.actions.CartoonToolGroupActions;
import cbit.gui.graph.actions.CartoonToolPaintingActions;
import cbit.gui.graph.actions.CartoonToolSaveAsImageActions;
import cbit.gui.graph.actions.GraphViewAction;
import cbit.gui.graph.groups.VCGroupManager;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.MenuSelectionManager;

public abstract class CartoonTool implements GraphView, MouseListener,
		MouseMotionListener, ActionListener, KeyListener {

	private GraphPane graphPane = null;
	private ButtonGroupCivilized buttonGroup = null;
	
	protected final GraphEmbeddingManager graphEmbeddingManager = new GraphEmbeddingManager(this);
	protected final VCGroupManager groupManager = new VCGroupManager(this);

	protected final List<GraphViewAction> groupActions = 
		CartoonToolGroupActions.getDefaultActions(this);
	protected final List<GraphViewAction> paintingActions = 
		CartoonToolPaintingActions.getDefaultActions(this);
	protected final List<GraphViewAction> miscActions = 
		CartoonToolMiscActions.getDefaultActions(this);
	protected final List<GraphViewAction> editActions = 
		CartoonToolEditActions.getDefaultActions(this);
	protected final List<GraphViewAction> saveAsImageActions = 
		CartoonToolSaveAsImageActions.getDefaultActions(this);
	
	public static enum Mode { SELECT("select"), FEATURE("feature"), SPECIES("species"), LINE("line"), 
		LINEDIRECTED("lineDirected"), LINECATALYST("lineCatalyst"), STEP("step"), FLUX("flux"), 
		SPLINE("spline"), ADDCP("addCP"), COMPLEX("complex"), BINDINGSITE("bindingSite"), 
		INTERACTION("interaction");
	protected final String actionCommand;
	private Mode(String actionCommand) { this.actionCommand = actionCommand; }
	public String getActionCommand() { return actionCommand; }
	};

	private JPopupMenu lastJPopupMenu = null;
	private List<JMenuItem> lastMenuList = null;

	// reaction and structure cartoon image menus
	JMenuItem saveAsImageMenu = new JMenu(CartoonToolSaveAsImageActions.getMenuAction(this));

	protected List<JMenuItem> miscMenuItems = new ArrayList<JMenuItem>();
	protected List<JMenuItem> editMenuItems = new ArrayList<JMenuItem>();
	protected List<JMenuItem> paintingMenuItems = new ArrayList<JMenuItem>();
	protected List<JMenuItem> groupMenuItems = new ArrayList<JMenuItem>();

	protected List<JMenuItem> menuItems = new ArrayList<JMenuItem>();

	public CartoonTool() {
		for(GraphViewAction miscAction : miscActions) {
			miscMenuItems.add(new JMenuItem(miscAction));
		}
		menuItems.addAll(miscMenuItems);
		for(GraphViewAction editAction : editActions) {
			editMenuItems.add(new JMenuItem(editAction));
		}
		menuItems.addAll(editMenuItems);
		for(GraphViewAction saveAsImageAction : CartoonToolSaveAsImageActions.getDefaultActions(this)) {
			saveAsImageMenu.add(new JMenuItem(saveAsImageAction));
		}
		menuItems.add(saveAsImageMenu);
		for(GraphViewAction paintingAction : paintingActions) {
			paintingMenuItems.add(new JMenuItem(paintingAction));
		}
		menuItems.addAll(paintingMenuItems);
		for(GraphViewAction groupAction : groupActions) {
			groupMenuItems.add(new JMenuItem(groupAction));
		}
		menuItems.addAll(groupMenuItems);
	}

	public int getWidth() { return graphPane.getWidth(); }
	public int getHeight() { return graphPane.getHeight(); }
	
	public void setSize(Dimension graphSize) {
		getGraphPane().setSize(graphSize);
		getGraphPane().setPreferredSize(graphSize);
		// update the window
		getGraphPane().invalidate();
		((JViewport) getGraphPane().getParent()).revalidate();
	}
	
	public VCGroupManager getGroupManager() { return groupManager; }
	
	public final void actionPerformed(ActionEvent event) {
		if (getGraphModel() == null) {
			return;
		}
		Shape popupMenuShape = getGraphModel().getSelectedShape();
		menuAction(popupMenuShape, event.getActionCommand());
	}

	public ButtonGroupCivilized getButtonGroup() {
		return buttonGroup;
	}

	protected static final Container getDialogOwner(GraphPane graphPaneSeekingOwner) {
		if (graphPaneSeekingOwner == null) {
			return null;
		}

		Container dialogOwner = BeanUtils.findTypeParentOfComponent(
				graphPaneSeekingOwner, JDesktopPane.class);
		if (dialogOwner != null) {
			return dialogOwner;
		}

		dialogOwner = BeanUtils.findTypeParentOfComponent(
				graphPaneSeekingOwner, JFrame.class);
		if (dialogOwner != null) {
			return ((JFrame) dialogOwner).getContentPane();
		}
		return dialogOwner;
	}

	public abstract GraphModel getGraphModel();

	public GraphPane getGraphPane() {
		return this.graphPane;
	}

	protected JDesktopPaneEnhanced getJDesktopPane() {
		if (getGraphPane() == null) {
			return null;
		}
		return (JDesktopPaneEnhanced) BeanUtils.findTypeParentOfComponent(
				getGraphPane(), JDesktopPaneEnhanced.class);
	}

	public final static Mode getMode(String actionCommand) {
		for(Mode mode : Mode.values()) {
			if(mode.getActionCommand().equals(actionCommand)) {
				return mode;
			}
		}
		System.out.println("ERROR: action command " + actionCommand
				+ " not defined");
		return null;
	}

	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (lastJPopupMenu != null && lastJPopupMenu.isVisible()) {
				MenuSelectionManager.defaultManager().clearSelectedPath();
			} else {
				getGraphModel().clearSelection();
			}
		}
		if ((e.getKeyCode() == KeyEvent.VK_C)
				&& ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
			menuAction(getGraphModel().getSelectedShape(), CartoonToolEditActions.Copy.MENU_ACTION);
		}
		if ((e.getKeyCode() == KeyEvent.VK_X)
				&& ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
			menuAction(getGraphModel().getSelectedShape(), CartoonToolEditActions.Cut.MENU_ACTION);
		}
		if ((e.getKeyCode() == KeyEvent.VK_V)
				&& ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
			menuAction(getGraphModel().getSelectedShape(), CartoonToolEditActions.Paste.MENU_ACTION);
		}
		if ((e.getKeyCode() == KeyEvent.VK_DELETE)) {
			menuAction(getGraphModel().getSelectedShape(), CartoonToolEditActions.Delete.MENU_ACTION);
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	protected abstract void menuAction(Shape shape, String menuAction);

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseDragged(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	protected final void popupMenu(Shape shape, int x, int y) throws Exception {

		if (getGraphPane() == null) {
			return;
		}
		// TODO is this really more efficient?
		//
		// For efficiency, this will reuse last JPopupMenu if possible.
		// Could be made smarter and keep list of more than 1 previously
		// constructed JPopupMenus and reuse
		//
		if (shape != null) {

			List<JMenuItem> currentMenuList = new ArrayList<JMenuItem>();
			// Add stuff to menu based on state of cartoons
			for (JMenuItem menuItem : menuItems) {
				if (menuItem instanceof JMenu) { // accommodate the
					// image-submenu
					boolean menuNeeded = false;
					for (int k = 0; k < ((JMenu) menuItem).getItemCount(); k++) {
						JMenuItem subMenuItem = ((JMenu) menuItem).getItem(k);
						if (shapeHasMenuAction(shape, subMenuItem.getActionCommand())) {
							boolean actionEnabled = shapeHasMenuActionEnabled(
									shape, subMenuItem.getActionCommand());
							subMenuItem.setEnabled(actionEnabled);
							if (!menuNeeded) {
								menuNeeded = true;
							}
						}
					}
					if (menuNeeded) {
						menuItem.setEnabled(true);
						currentMenuList.add(menuItem);
					}
					continue;
				}
				if (shapeHasMenuAction(shape, menuItem.getActionCommand())) {
					currentMenuList.add(menuItem);
					menuItem.setEnabled(shapeHasMenuActionEnabled(shape,
							menuItem.getActionCommand()));
				}
			}
			// See if anything changed
			JPopupMenu popupMenu = null;
			//
			boolean bSame = true;
			if (lastMenuList == null || !lastMenuList.equals(currentMenuList)) {
				bSame = false;
			}
			if (bSame) {
				for (int i = 0; i < currentMenuList.size(); i += 1) {
					if ((lastMenuList.get(i)).isEnabled() != (currentMenuList.get(i)).isEnabled()) {
						bSame = false;
						break;
					}
				}
			}
			if (!bSame) {// Make new JPopupMenu if its different than the last
				List<JMenuItem> editV = new ArrayList<JMenuItem>();
				popupMenu = new javax.swing.JPopupMenu();
				popupMenu.setName("CartoonToolJPopupMenu");
				//
				for (int i = 0; i < currentMenuList.size(); i += 1) {
					JMenuItem addableJMenuItem = currentMenuList.get(i);
					if (miscMenuItems.contains(addableJMenuItem)
							|| addableJMenuItem == saveAsImageMenu
							|| paintingMenuItems.contains(addableJMenuItem)
							|| groupMenuItems.contains(addableJMenuItem)) {
						popupMenu.add(addableJMenuItem);
					} else if (editMenuItems.contains(addableJMenuItem)) {
						editV.add(addableJMenuItem);
					} else {
						throw new RuntimeException("Unknown JMenuItem="	+ lastMenuList.get(i).toString());
					}
				}
				//
				if (editV.size() > 0) {
					for (int i = 0; i < editV.size(); i += 1) {
						popupMenu.add(editV.get(i), i);
					}
					popupMenu.add(new JSeparator(), editV.size());
				}
				//
				lastMenuList = currentMenuList;
				lastJPopupMenu = popupMenu;
			}
			// Show menu if anything is in it
			if (lastJPopupMenu != null && (lastJPopupMenu.getComponentCount() > 0)) {
				// graphPane.add(popupMenu);//JPopupMenus don't need to add themselves to Components
				lastJPopupMenu.show(getGraphPane(), x, y);
			}
		}
	}

	protected Point screenToWorld(int x, int y) {
		if (getGraphModel() == null) {
			return null;
		}
		double zoomUnscaling = 100.0 / getGraphModel().getZoomPercent();
		return new Point((int) (x * zoomUnscaling), (int) (y * zoomUnscaling));
	}

	protected Point screenToWorld(Point screenPoint) {
		if (getGraphModel() == null) {
			return null;
		}
		double zoomUnscaling = 100.0 / getGraphModel().getZoomPercent();
		return new Point((int) (screenPoint.x * zoomUnscaling),
				(int) (screenPoint.y * zoomUnscaling));
	}

	public void setButtonGroup(ButtonGroupCivilized newButtonGroup) {
		buttonGroup = newButtonGroup;
		setMode(Mode.SELECT);
	}

	public void setGraphPane(GraphPane pane) {
		if (this.graphPane != null) {
			this.graphPane.removeMouseListener(this);
			this.graphPane.removeMouseMotionListener(this);
			this.graphPane.removeKeyListener(this);
		}
		this.graphPane = pane;
		if (this.graphPane != null) {
			this.graphPane.addMouseListener(this);
			this.graphPane.addMouseMotionListener(this);
			this.graphPane.addKeyListener(this);
		}
	}

	public final void setMode(Mode newMode) {
		String newActionCommand = newMode.getActionCommand();
		updateButtonGroup(getButtonGroup(), newActionCommand);
		updateMode(newMode);
	}

	public final void setModeString(String newActionCommand) {
		Mode newMode = getMode(newActionCommand);
		updateButtonGroup(getButtonGroup(), newActionCommand);
		updateMode(newMode);
	}

	public abstract boolean shapeHasMenuAction(Shape shape, String menuAction);

	public abstract boolean shapeHasMenuActionEnabled(Shape shape,
			String menuAction);

	public final void updateButtonGroup(ButtonGroupCivilized buttonGroup,
			String actionCommand) {
		if (buttonGroup == null) {
			return;
		}
		// if selected button does not have this action command, select the
		// first button we find with appropriate action command
		String currSelectedString = (buttonGroup.getSelection() != null ? buttonGroup
				.getSelection().getActionCommand(): null);
		if (currSelectedString != null) {
			if (currSelectedString.equals(actionCommand)) {
				return;
			}
		}
		Enumeration<AbstractButton> buttons = buttonGroup.getElements();
		while (buttons.hasMoreElements()) {
			ButtonModel button = buttons.nextElement().getModel();
			if (button.getActionCommand().equals(actionCommand)) {
				buttonGroup.setSelection(button);
				return;
			}
		}
		System.out.println("ERROR: button with actionCommand " + actionCommand + " not found");
		return;
	}

	public abstract void updateMode(Mode newMode);

	public void repaint() {
		graphPane.repaint();
	}
	
}