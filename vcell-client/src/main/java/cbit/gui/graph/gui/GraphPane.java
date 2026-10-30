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
import org.vcell.util.gui.VCellIcons;
import cbit.vcell.client.laf.VCellFonts;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.util.function.IntConsumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import cbit.gui.graph.GraphContainerLayout;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphListener;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphModel.NotReadyException;
import cbit.gui.graph.GraphResizeManager;
import cbit.gui.graph.Shape;
import cbit.vcell.graph.ReactionContainerShape;

@SuppressWarnings("serial")
public class GraphPane extends JPanel implements GraphListener, MouseListener, Scrollable {

	private GraphModel graphModel = null;
	protected boolean needsLayout = true;

	// Camera: zoom (around the cursor with Cmd/Ctrl + scroll) and pan (middle-button drag, or Space + drag)
	private static final double WHEEL_ZOOM_FACTOR = 1.1; // per notch of scrolling
	private static final int MIN_ZOOM_PERCENT = 5, MAX_ZOOM_PERCENT = 1000, MAX_FIT_ZOOM_PERCENT = 200;
	private IntConsumer zoomApplier = null;
	private double fractionalZoom = Double.NaN; // smooth zooming between whole percents
	private boolean spaceDown = false;
	private Point panStartOnScreen = null, panStartView = null;
	private Cursor cursorBeforePan = null;

	public GraphPane() {
		super();
		initialize();
		addMouseListener(this);
//		setToolTipText("Graph Panel");
		ToolTipManager.sharedInstance().registerComponent(this);
		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		installZoomKeys();
	}

	public void clear(java.awt.Graphics g) {
		super.paint(g);
	}

	public Frame getFrame() {
		Component comp = getParent();
		while (comp!=null){
			if (comp instanceof Frame) {
				return (Frame) comp;
			}
			comp = comp.getParent();
		}
		return null;
	}

	public GraphModel getGraphModel() {
		return graphModel;
	}


	private JScrollPane getJScrollPaneParent() {
		if (getParent() instanceof JScrollPane){
			return (JScrollPane) getParent();
		} else if (getParent() instanceof JViewport && getParent().getParent() instanceof JScrollPane){
			return (JScrollPane) getParent().getParent();
		} else {
			return null;
		}
	}


	/**
	 * Returns the preferred size of the viewport for a view component.
	 * For example the preferredSize of a JList component is the size
	 * required to acommodate all of the cells in its list however the
	 * value of preferredScrollableViewportSize is the size required for
	 * JList.getVisibleRowCount() rows.   A component without any properties
	 * that would effect the viewport size should just return 
	 * getPreferredSize() here.
	 * 
	 * @return The preferredSize of a JViewport whose view is this Scrollable.
	 * @see JViewport#getPreferredSize
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		if (graphModel != null) {
			Dimension prefSize;
			try {
				prefSize = graphModel.getPreferedCanvasSize((Graphics2D)getGraphics());
			} catch (NotReadyException e) {
				return super.getPreferredSize();
			}
			if (getJScrollPaneParent()!=null){
				Rectangle viewBorderBounds = getJScrollPaneParent().getViewportBorderBounds();
				prefSize = new Dimension(Math.max(viewBorderBounds.width, prefSize.width),
						Math.max(viewBorderBounds.height, prefSize.height));
			}
			return prefSize;
		}else{
			return super.getPreferredSize();
		}

	}


	/**
	 * Components that display logical rows or columns should compute
	 * the scroll increment that will completely expose one block
	 * of rows or columns, depending on the value of orientation. 
	 * <p>
	 * Scrolling containers, like JScrollPane, will use this method
	 * each time the user requests a block scroll.
	 * 
	 * @param visibleRect The view area visible within the viewport
	 * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
	 * @param direction Less than zero to scroll up/left, greater than zero for down/right.
	 * @return The "block" increment for scrolling in the specified direction.
	 * @see JScrollBar#setBlockIncrement
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (SwingConstants.VERTICAL == orientation){
			return visibleRect.height/4;
		} else {
			return visibleRect.width/4;
		}
	}


	/**
	 * Return true if a viewport should always force the height of this 
	 * Scrollable to match the height of the viewport.  For example a 
	 * columnar text view that flowed text in left to right columns 
	 * could effectively disable vertical scrolling by returning
	 * true here.
	 * <p>
	 * Scrolling containers, like JViewport, will use this method each 
	 * time they are validated.  
	 * 
	 * @return True if a viewport should force the Scrollables height to match its own.
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}


	/**
	 * Return true if a viewport should always force the width of this 
	 * Scrollable to match the width of the viewport.  For example a noraml 
	 * text view that supported line wrapping would return true here, since it
	 * would be undesirable for wrapped lines to disappear beyond the right
	 * edge of the viewport.  Note that returning true for a Scrollable
	 * whose ancestor is a JScrollPane effectively disables horizontal
	 * scrolling.
	 * <p>
	 * Scrolling containers, like JViewport, will use this method each 
	 * time they are validated.  
	 * 
	 * @return True if a viewport should force the Scrollables width to match its own.
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}


	/**
	 * Components that display logical rows or columns should compute
	 * the scroll increment that will completely expose one new row
	 * or column, depending on the value of orientation.  Ideally, 
	 * components should handle a partially exposed row or column by 
	 * returning the distance required to completely expose the item.
	 * <p>
	 * Scrolling containers, like JScrollPane, will use this method
	 * each time the user requests a unit scroll.
	 * 
	 * @param visibleRect The view area visible within the viewport
	 * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
	 * @param direction Less than zero to scroll up/left, greater than zero for down/right.
	 * @return The "unit" increment for scrolling in the specified direction
	 * @see JScrollBar#setUnitIncrement
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// a scroll-wheel notch (3 units) moves a tenth of the view, whatever its size
		return Math.max(1, (SwingConstants.VERTICAL == orientation ? visibleRect.height : visibleRect.width) / 30);
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param o java.util.Observable
	 * @param obj java.lang.Object
	 */
	public void graphChanged(GraphEvent graphEvent) {
		updateAll();			
	}


	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in CartoonCanvas");
		exception.printStackTrace(System.out);
	}

	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("GraphPane");
			setLayout(null);
			setBackground(java.awt.Color.gray);
			setSize(150, 150);
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	/**
	 * Returns the value of a flag that indicates whether
	 * this component can be traversed using
	 * Tab or Shift-Tab keyboard focus traversal.  If this method
	 * returns "false", this component may still request the keyboard
	 * focus using <code>requestFocus()</code>, but it will not automatically
	 * be assigned focus during tab traversal.
	 * @return    <code>true</code> if this component is
	 *            focus-traverable; <code>false</code> otherwise.
	 * @since     JDK1.1
	 */

	//override isFocusTraversable() to return true (Canvas returns false by default) 
	//catch the mouse-down event on the component and invoke requestFocus() (to implement click-to-type for your component) 
	//when your component gets focus, provide visual feedback indicating it has the focus 

	@Override
	public boolean isFocusTraversable() {
		return true;
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		//override isFocusTraversable() to return true (Canvas returns false by default) 
		//catch the mouse-down event on the component and invoke requestFocus() (to implement click-to-type for your component) 
		//when your component gets focus, provide visual feedback indicating it has the focus 
		requestFocus();
	}

	public void mouseReleased(MouseEvent e) {}

	@Override
	public void paintComponent(Graphics argGraphics) {
		super.paintComponent(argGraphics);
		try {
			Graphics2D g = (Graphics2D) argGraphics;
			if (graphModel!=null){
//				if(needsLayout) {
					GraphContainerLayout containerLayout = graphModel.getContainerLayout();
					containerLayout.layout(graphModel, g, graphModel.getResizeManager().unzoom(getSize()));
					needsLayout = false;					
//				}
				if (graphModel.getTopShape()!=null){
					graphModel.paint(g);
				}
			}	
		}catch (Exception e){
			handleException(e);
		}		
	}

	public void setGraphModel(GraphModel argGraphModel) {

		if (this.graphModel != null){
			this.graphModel.removeGraphListener(this);
		}

		this.graphModel = argGraphModel;

		if (this.graphModel != null){
			this.graphModel.addGraphListener(this);
		}

		updateAll();
	}

	public void updateAll() {
		needsLayout = true;
		try {
			if (graphModel!=null){
				if (getJScrollPaneParent()!=null){
					invalidate();
					getJScrollPaneParent().revalidate();
				}
				repaint();
			}
		}catch (Exception e){
			handleException(e);
		}			
	}
	
	/**
	 * How a zoom change is applied; by default to this pane's diagram. A panel that shows several diagrams in turn
	 * here can apply it to all of them, so they stay alike.
	 */
	public void setZoomApplier(IntConsumer zoomApplier) {
		this.zoomApplier = zoomApplier;
	}

	/** The zoom buttons a diagram's toolbar can show. */
	public enum ZoomButton { ZOOM_IN, ZOOM_OUT, FIT, ACTUAL_SIZE }

	/** A toolbar button, the given size, that zooms this diagram. */
	public JButton createZoomButton(ZoomButton kind, Dimension size) {
		JButton button = GraphPane.createToolBarButton(size);
		String shortcut = KeyEvent.getModifiersExText(Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
		switch (kind) {
			case ZOOM_IN -> {
				button.setName("ZoomInButton");
				button.setIcon(VCellIcons.pathwayZoomInIcon);
				button.setToolTipText("Zoom in (" + shortcut + "+)");
				button.addActionListener(e -> zoomIn());
			}
			case ZOOM_OUT -> {
				button.setName("ZoomOutButton");
				button.setIcon(VCellIcons.pathwayZoomOutIcon);
				button.setToolTipText("Zoom out (" + shortcut + "-)");
				button.addActionListener(e -> zoomOut());
			}
			case FIT -> {
				button.setName("ZoomToFitButton");
				button.setText("Fit");
				VCellFonts.setCategory(button, VCellFonts.Category.CAPTION);
				button.setToolTipText("Zoom to fit the whole diagram");
				button.addActionListener(e -> zoomToFit());
			}
			case ACTUAL_SIZE -> {
				button.setName("ResetZoomButton");
				button.setText("1:1");
				VCellFonts.setCategory(button, VCellFonts.Category.CAPTION);
				button.setToolTipText("Zoom to 100% (" + shortcut + "+0)");
				button.addActionListener(e -> resetZoom());
			}
		}
		return button;
	}

	/** A "?" button, the given size, that shows how to zoom and pan a diagram; for a diagram's toolbar. */
	public static JButton createControlsHelpButton(Dimension size) {
		JButton button = GraphPane.createToolBarButton(size);
		button.setName("DiagramControlsHelpButton");
		button.setText("?");
		VCellFonts.setCategory(button, VCellFonts.Category.HEADING);
		button.setToolTipText("How to zoom and pan the diagram");
		button.addActionListener(e -> GraphPane.showControlsHelp(button));
		return button;
	}

	private static JButton createToolBarButton(Dimension size) {
		JButton button = new JButton();
		button.setPreferredSize(size);
		button.setMinimumSize(size);
		button.setMaximumSize(size);
		button.setMargin(new Insets(2, 2, 2, 2));
		return button;
	}

	/** Shows how to zoom and pan a diagram, in a pop-up below {@code invoker} (a help button, say). */
	public static void showControlsHelp(Component invoker) {
		String key = KeyEvent.getModifiersExText(Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
		String[][] controls = {
				{"Zoom in or out", key + " + scroll wheel (zooms around the pointer)"},
				{"Pan", "Drag with the middle mouse button, or hold Space and drag"},
				{"Scroll", "Scroll wheel; Shift + scroll wheel scrolls sideways"},
				{"Zoom in, out, to 100%", key + " +&nbsp;&nbsp;/&nbsp;&nbsp;" + key + " &minus;&nbsp;&nbsp;/&nbsp;&nbsp;" + key + " 0 &nbsp;(click the diagram first)"},
				{"Fit the whole diagram", "The Fit button"},
				{"Back to 100%", "The 1:1 button"},
		};
		StringBuilder html = new StringBuilder("<html><b>Diagram controls</b><table cellpadding='3'>");
		for (String[] control : controls) html.append("<tr><td><b>").append(control[0]).append("</b></td><td>").append(control[1]).append("</td></tr>");
		JLabel label = new JLabel(html.append("</table></html>").toString());
		label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
		JPopupMenu popup = new JPopupMenu();
		popup.add(label);
		popup.show(invoker, 0, invoker.getHeight());
	}

	/** Zooms in one step, keeping the middle of the view in place. */
	public void zoomIn() {
		try {
			zoomTo(GraphResizeManager.getNextHigherZoomPercent(getZoomPercent()), getViewCenter());
		} catch (GraphResizeManager.ZoomRangeException e) {
			// as far in as it goes
		}
	}

	/** Zooms out one step, keeping the middle of the view in place. */
	public void zoomOut() {
		try {
			zoomTo(Math.max(MIN_ZOOM_PERCENT, GraphResizeManager.getNextLowerZoomPercent(getZoomPercent())), getViewCenter());
		} catch (GraphResizeManager.ZoomRangeException e) {
			// as far out as it goes
		}
	}

	/** Back to 100%, keeping the middle of the view in place. */
	public void resetZoom() {
		zoomTo(100, getViewCenter());
	}

	/** Zooms so the whole diagram fits in the view (zooming in no further than 200%). */
	public void zoomToFit() {
		JViewport viewport = getViewport();
		if (graphModel == null || viewport == null || !(getGraphics() instanceof Graphics2D g)) return;
		try {
			Dimension diagram = graphModel.getResizeManager().unzoom(graphModel.getPreferedCanvasSize(g));
			Dimension view = viewport.getExtentSize();
			if (diagram.width <= 0 || diagram.height <= 0 || view.width <= 0 || view.height <= 0) return;
			double fit = Math.min(view.width / (double) diagram.width, view.height / (double) diagram.height);
			zoomTo((int) Math.floor(100 * fit), new Point(0, 0));
			viewport.setViewPosition(new Point(0, 0));
		} catch (NotReadyException e) {
			// nothing drawn yet
		}
	}

	private int getZoomPercent() {
		return graphModel == null ? 100 : graphModel.getResizeManager().getZoomPercent();
	}

	// Sets the zoom so the diagram point at anchor (in this pane) stays where it is in the view
	private void zoomTo(int zoomPercent, Point anchor) {
		int oldZoom = getZoomPercent();
		int newZoom = Math.max(MIN_ZOOM_PERCENT, Math.min(MAX_ZOOM_PERCENT, zoomPercent));
		if (graphModel == null || newZoom == oldZoom) return;
		JViewport viewport = getViewport();
		Point anchorInView = viewport == null ? anchor : new Point(anchor.x - viewport.getViewPosition().x, anchor.y - viewport.getViewPosition().y);
		double scale = newZoom / (double) oldZoom;
		try {
			if (zoomApplier != null) zoomApplier.accept(newZoom);
			else graphModel.getResizeManager().setZoomPercent(newZoom);
		} catch (GraphResizeManager.ZoomRangeException | NotReadyException e) {
			return;
		}
		if (viewport == null) return;
		// size the pane for the new zoom now, rather than at the queued revalidation, so the view can be placed on it
		getJScrollPaneParent().validate();
		setViewPosition(new Point((int) Math.round(anchor.x * scale) - anchorInView.x, (int) Math.round(anchor.y * scale) - anchorInView.y));
	}

	private JViewport getViewport() {
		return getParent() instanceof JViewport viewport ? viewport : null;
	}

	private Point getViewCenter() {
		JViewport viewport = getViewport();
		if (viewport == null) return new Point(getWidth() / 2, getHeight() / 2);
		Rectangle view = viewport.getViewRect();
		return new Point(view.x + view.width / 2, view.y + view.height / 2);
	}

	private void setViewPosition(Point position) {
		JViewport viewport = getViewport();
		Dimension view = viewport.getExtentSize();
		int x = Math.max(0, Math.min(position.x, getWidth() - view.width));
		int y = Math.max(0, Math.min(position.y, getHeight() - view.height));
		viewport.setViewPosition(new Point(x, y));
	}

	private void installZoomKeys() {
		int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
		bindZoomKey("GraphPane.zoomIn", this::zoomIn, shortcut, KeyEvent.VK_EQUALS, KeyEvent.VK_PLUS, KeyEvent.VK_ADD);
		bindZoomKey("GraphPane.zoomOut", this::zoomOut, shortcut, KeyEvent.VK_MINUS, KeyEvent.VK_SUBTRACT);
		bindZoomKey("GraphPane.resetZoom", this::resetZoom, shortcut, KeyEvent.VK_0, KeyEvent.VK_NUMPAD0);
	}

	private void bindZoomKey(String name, Runnable zoom, int modifiers, int... keyCodes) {
		for (int keyCode : keyCodes) getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(keyCode, modifiers), name);
		getActionMap().put(name, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoom.run();
			}
		});
	}

	// Cmd/Ctrl + scroll zooms around the cursor; any other scrolling goes to the scroll pane, as it would without this
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		int zoomModifiers = InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
		if (graphModel != null && (e.getModifiersEx() & zoomModifiers) != 0) {
			int zoom = getZoomPercent();
			double from = !Double.isNaN(fractionalZoom) && Math.round(fractionalZoom) == zoom ? fractionalZoom : zoom;
			fractionalZoom = Math.max(MIN_ZOOM_PERCENT, Math.min(MAX_ZOOM_PERCENT, from * Math.pow(WHEEL_ZOOM_FACTOR, -e.getPreciseWheelRotation())));
			zoomTo((int) Math.round(fractionalZoom), e.getPoint());
			e.consume();
			return;
		}
		JScrollPane scrollPane = getJScrollPaneParent();
		if (scrollPane != null) scrollPane.dispatchEvent(SwingUtilities.convertMouseEvent(this, e, scrollPane));
		else super.processMouseWheelEvent(e);
	}

	// A pan (middle-button drag, or Space + drag) is handled here, and never reaches the diagram's editing tools
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_PRESSED && getViewport() != null
				&& (SwingUtilities.isMiddleMouseButton(e) || (spaceDown && SwingUtilities.isLeftMouseButton(e)))) {
			requestFocus();
			panStartOnScreen = e.getLocationOnScreen();
			panStartView = getViewport().getViewPosition();
			cursorBeforePan = spaceDown ? cursorBeforePan : getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			return;
		}
		if (panStartOnScreen != null) {
			if (e.getID() == MouseEvent.MOUSE_RELEASED) {
				panStartOnScreen = null;
				setCursor(spaceDown ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : cursorBeforePan);
			}
			return;
		}
		super.processMouseEvent(e);
	}

	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		if (panStartOnScreen == null) {
			super.processMouseMotionEvent(e);
			return;
		}
		if (e.getID() != MouseEvent.MOUSE_DRAGGED) return;
		Point now = e.getLocationOnScreen();
		setViewPosition(new Point(panStartView.x - (now.x - panStartOnScreen.x), panStartView.y - (now.y - panStartOnScreen.y)));
	}

	@Override
	protected void processKeyEvent(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE && e.getID() != KeyEvent.KEY_TYPED) {
			boolean down = e.getID() == KeyEvent.KEY_PRESSED;
			if (down != spaceDown) setSpaceDown(down);
			e.consume();
			return;
		}
		super.processKeyEvent(e);
	}

	@Override
	protected void processFocusEvent(FocusEvent e) {
		if (e.getID() == FocusEvent.FOCUS_LOST && spaceDown) setSpaceDown(false); // a Space release elsewhere is never seen
		super.processFocusEvent(e);
	}

	// While Space is held the cursor shows that dragging pans
	private void setSpaceDown(boolean down) {
		spaceDown = down;
		if (panStartOnScreen != null) return;
		if (down) {
			cursorBeforePan = getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(cursorBeforePan);
		}
	}

	@Override
	public String getToolTipText() {
		return "Graph";
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if(graphModel == null) {
			return getToolTipText();
		}
		Point unzoomedMousePos = 
			graphModel.getResizeManager().unzoom(event.getPoint());
		Shape pickedShape = graphModel.pickWorld(unzoomedMousePos);
		if(pickedShape == null) {
			return getToolTipText();
		}
		if(pickedShape instanceof ReactionContainerShape){
			return ((ReactionContainerShape)pickedShape).getSpecialToolTipText(unzoomedMousePos);
		}
		return pickedShape.getLabel();
	}

}
