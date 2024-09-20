/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.gui;


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
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.MenuSelectionManager;

import cbit.gui.graph.GraphLayoutManager;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphView;
import cbit.gui.graph.Shape;
import cbit.gui.graph.actions.CartoonToolEditActions;
import cbit.gui.graph.actions.CartoonToolGroupActions;
import cbit.gui.graph.actions.CartoonToolMiscActions;
import cbit.gui.graph.actions.CartoonToolPaintingActions;
import cbit.gui.graph.actions.CartoonToolSaveAsImageActions;
import cbit.gui.graph.actions.GraphViewAction;
import cbit.gui.graph.groups.VCGroupManager;
import org.vcell.util.gui.GeneralGuiUtils;

public abstract class CartoonTool implements GraphView, MouseListener,
        MouseMotionListener, ActionListener, KeyListener {

    public static final boolean GROUPS_ARE_ENABLED = false;

    private GraphPane graphPane = null;
    private ButtonGroup buttonGroup = null;

    protected final GraphLayoutManager graphEmbeddingManager = new GraphLayoutManager();
    protected final VCGroupManager groupManager = new VCGroupManager(this);

    protected final List<GraphViewAction> groupActions =
            CartoonToolGroupActions.getDefaultActions(this);
    protected final List<GraphViewAction> paintingActions =
            CartoonToolPaintingActions.getDefaultActions(this);
    protected final List<GraphViewAction> miscActions =
            CartoonToolMiscActions.getDefaultActions(this);
    protected final List<GraphViewAction> editActions =
            CartoonToolEditActions.getDefaultActions(this);

    public enum Mode {
        SELECT("select"), FEATURE("feature"), SPECIES("species"), LINE("line"),
        LINEDIRECTED("lineDirected"), LINECATALYST("lineCatalyst"), STEP("step"), FLUX("flux"),
        SPLINE("spline"), ADDCP("addCP"), COMPLEX("complex"), BINDINGSITE("bindingSite"),
        INTERACTION("interaction"), STRUCTURE("structure"),
        GROUPMOLECULE("groupmolecule"), GROUPRULE("grouprule"), UNGROUP("ungroup"),
        HIGHLIGHTCATALYST("highlightCatalyst"),
        EQUALSIZE("equalSize"), SIZEBYWEIGHT("sizeByWeight"), SIZEBYLENGTH("sizeByLength");
        private final String actionCommand;

        Mode(String actionCommand) {
            this.actionCommand = actionCommand;
        }

        public String getActionCommand() {
            return this.actionCommand;
        }
    }

    // reaction and structure cartoon image menus
    JMenuItem saveAsImageMenu = new JMenuItem(CartoonToolSaveAsImageActions.getMenuAction(this));

    protected List<JMenuItem> miscMenuItems = new ArrayList<JMenuItem>();
    protected List<JMenuItem> editMenuItems = new ArrayList<JMenuItem>();
    protected List<JMenuItem> paintingMenuItems = new ArrayList<JMenuItem>();
    protected List<JMenuItem> groupMenuItems = new ArrayList<JMenuItem>();

    protected List<JMenuItem> menuItems = new ArrayList<JMenuItem>();

    public CartoonTool() {
        for (GraphViewAction miscAction : this.miscActions) {
            this.miscMenuItems.add(new JMenuItem(miscAction));
        }
        this.menuItems.addAll(this.miscMenuItems);
        for (GraphViewAction editAction : this.editActions) {
            this.editMenuItems.add(new JMenuItem(editAction));
        }
        this.menuItems.addAll(this.editMenuItems);
        this.menuItems.add(this.saveAsImageMenu);
//		for(GraphViewAction paintingAction : paintingActions) {
//			paintingMenuItems.add(new JMenuItem(paintingAction));
//		}
//		menuItems.addAll(paintingMenuItems);
        if (GROUPS_ARE_ENABLED) {
            for (GraphViewAction groupAction : this.groupActions) {
                this.groupMenuItems.add(new JMenuItem(groupAction));
            }
            this.menuItems.addAll(this.groupMenuItems);
        }
    }

    public int getWidth() {
        return this.graphPane.getWidth();
    }

    public int getHeight() {
        return this.graphPane.getHeight();
    }

    public void setSize(Dimension graphSize) {
        this.getGraphPane().setSize(graphSize);
        this.getGraphPane().setPreferredSize(graphSize);
        // update the window
        this.getGraphPane().invalidate();
        this.getGraphPane().getParent().revalidate();
    }

    public VCGroupManager getGroupManager() {
        return this.groupManager;
    }

    public final void actionPerformed(ActionEvent event) {
        if (this.getGraphModel() == null) {
            return;
        }
        Shape popupMenuShape = this.getGraphModel().getSelectedShape();
        this.menuAction(popupMenuShape, event.getActionCommand());
    }

    public ButtonGroup getButtonGroup() {
        return this.buttonGroup;
    }

    protected static final Container getDialogOwner(GraphPane graphPaneSeekingOwner) {
        if (graphPaneSeekingOwner == null) {
            return null;
        }

        Container dialogOwner = GeneralGuiUtils.findTypeParentOfComponent(
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

    public final static Mode getMode(String actionCommand) {
        for (Mode mode : Mode.values()) {
            if (mode.getActionCommand().equals(actionCommand)) {
                return mode;
            }
        }
        System.out.println("ERROR: action command " + actionCommand
                + " not defined");
        return null;
    }

    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            MenuSelectionManager.defaultManager().clearSelectedPath();
            this.getGraphModel().clearSelection();
        }
        if ((e.getKeyCode() == KeyEvent.VK_C)
                && ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
            this.menuAction(this.getGraphModel().getSelectedShape(), CartoonToolEditActions.Copy.MENU_ACTION);
        }
        if ((e.getKeyCode() == KeyEvent.VK_V)
                && ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
            this.menuAction(this.getGraphModel().getSelectedShape(), CartoonToolEditActions.PasteNew.MENU_ACTION);
        }
        if ((e.getKeyCode() == KeyEvent.VK_DELETE) || (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
            this.menuAction(this.getGraphModel().getSelectedShape(), CartoonToolEditActions.Delete.MENU_ACTION);
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

        if (this.getGraphPane() == null) {
            return;
        }
        if (shape != null) {

            List<JMenuItem> currentMenuList = new ArrayList<JMenuItem>();
            // Add stuff to menu based on state of cartoons
            for (JMenuItem menuItem : this.menuItems) {
                if (menuItem instanceof JMenu) { // accommodate the
                    // image-submenu
                    boolean menuNeeded = false;
                    for (int k = 0; k < ((JMenu) menuItem).getItemCount(); k++) {
                        JMenuItem subMenuItem = ((JMenu) menuItem).getItem(k);
                        if (this.shapeHasMenuAction(shape, subMenuItem.getActionCommand())) {
                            boolean actionEnabled = this.shapeHasMenuActionEnabled(
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
                if (this.shapeHasMenuAction(shape, menuItem.getActionCommand())) {
                    currentMenuList.add(menuItem);
                    menuItem.setEnabled(this.shapeHasMenuActionEnabled(shape,
                            menuItem.getActionCommand()));
                }
            }

            List<JMenuItem> editV = new ArrayList<JMenuItem>();
            JPopupMenu popupMenu = new javax.swing.JPopupMenu();
            popupMenu.setName("CartoonToolJPopupMenu");

            for (int i = 0; i < currentMenuList.size(); i += 1) {
                JMenuItem addableJMenuItem = currentMenuList.get(i);
                if (this.miscMenuItems.contains(addableJMenuItem)
                        || addableJMenuItem == this.saveAsImageMenu
                        || this.paintingMenuItems.contains(addableJMenuItem)
                        || this.groupMenuItems.contains(addableJMenuItem)) {
                    popupMenu.add(addableJMenuItem);
                } else if (this.editMenuItems.contains(addableJMenuItem)) {
                    editV.add(addableJMenuItem);
                } else {
                    throw new RuntimeException("Unknown JMenuItem=" + addableJMenuItem.toString());
                }
            }
            //
            if (editV.size() > 0) {
                for (int i = 0; i < editV.size(); i += 1) {
                    popupMenu.add(editV.get(i), i);
                }
                popupMenu.add(new JSeparator(), editV.size());
            }
            if (popupMenu != null && (popupMenu.getComponentCount() > 0)) {
                popupMenu.show(this.getGraphPane(), x, y);
            }
        }
    }

    protected Point screenToWorld(int x, int y) {
        if (this.getGraphModel() == null) {
            return null;
        }
        double zoomUnscaling = 100.0 / this.getGraphModel().getZoomPercent();
        return new Point((int) (x * zoomUnscaling), (int) (y * zoomUnscaling));
    }

    protected Point screenToWorld(Point screenPoint) {
        if (this.getGraphModel() == null) {
            return null;
        }
        double zoomUnscaling = 100.0 / this.getGraphModel().getZoomPercent();
        return new Point((int) (screenPoint.x * zoomUnscaling),
                (int) (screenPoint.y * zoomUnscaling));
    }

    public void setButtonGroup(ButtonGroup newButtonGroup) {
        this.buttonGroup = newButtonGroup;
        this.setMode(Mode.SELECT);
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
        this.updateButtonGroup(this.getButtonGroup(), newActionCommand);
        this.updateMode(newMode);
    }

    public final void setModeString(String newActionCommand) {
        Mode newMode = getMode(newActionCommand);
        this.updateButtonGroup(this.getButtonGroup(), newActionCommand);
        this.updateMode(newMode);
    }

    public abstract boolean shapeHasMenuAction(Shape shape, String menuAction);

    public abstract boolean shapeHasMenuActionEnabled(Shape shape,
                                                      String menuAction);

    public final void updateButtonGroup(ButtonGroup buttonGroup,
                                        String actionCommand) {
        if (buttonGroup == null) {
            return;
        }
        // if selected button does not have this action command, select the
        // first button we find with appropriate action command
        String currSelectedString = (buttonGroup.getSelection() != null ? buttonGroup
                .getSelection().getActionCommand() : null);
        if (currSelectedString != null) {
            if (currSelectedString.equals(actionCommand)) {
                return;
            }
        }
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        while (buttons.hasMoreElements()) {
            ButtonModel button = buttons.nextElement().getModel();
            if (button.getActionCommand().equals(actionCommand)) {
                button.setSelected(true);
                return;
            }
        }
        System.out.println("ERROR: button with actionCommand " + actionCommand + " not found");
    }

    public abstract void updateMode(Mode newMode);

    public void repaint() {
        this.graphPane.repaint();
    }

}
