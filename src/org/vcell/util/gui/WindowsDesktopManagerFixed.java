package org.vcell.util.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import javax.swing.border.Border;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
/**
 * Insert the type's description here.
 * Creation date: (3/19/01 3:15:30 PM)
 * @author: Jim Schaff
 */
public class WindowsDesktopManagerFixed extends com.sun.java.swing.plaf.windows.WindowsDesktopManager {


/** This is an implementaion of the DesktopManager. It currently implements a
  * the basic behaviors for managing JInternalFrames in an arbitrary parent.
  * JInternalFrames that are not children of a JDesktop will use this component
  * to handle their desktop-like actions.
  * @see JDesktopPane
  * @see JInternalFrame
  * @version 1.24 04/22/99
  * @author David Kloba
  * @author Steve Wilson
  */
	public final static String PREVIOUS_BOUNDS_PROPERTY = "previousBounds";
	public final static String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";

	public final static int DEFAULT_DRAG_MODE = 0;
	public final static int OUTLINE_DRAG_MODE = 1;
	public final static int FASTER_DRAG_MODE = 2;

	public int dragMode = DEFAULT_DRAG_MODE;

	private JInternalFrame currentActiveFrame = null;

	 private transient Rectangle currentBounds = null;
	 private transient Graphics desktopGraphics = null;
	 private transient Rectangle desktopBounds = null;
	 private transient Rectangle[] floatingItems = {};


	private transient Point currentLoc = null;

	/* The frame which is currently selected/activated.
	 * We store this value to enforce MDI's single-selection model.
	 */
	public JInternalFrame currentFrame;
	public JInternalFrame initialFrame;	  

	/* The list of frames, sorted by order of creation.
	 * This list is necessary because by default the order of
	 * child frames in the JDesktopPane changes during frame
	 * activation (the activated frame is moved to index 0).
	 * We preserve the creation order so that "next" and "previous"
	 * frame actions make sense.
	 */
	public Vector childFrames = new Vector(1);

/**
 * WindowsDesktopManagerFixed constructor comment.
 */
public WindowsDesktopManagerFixed() {
	super();
}
	public void activateFrame(JInternalFrame f) {
		try {
			activateFrameSuper(f);

			// If this is the first activation, add to child list.
			if (childFrames.indexOf(f) == -1) {
				childFrames.addElement(f);
			}

			if (currentFrame != null && f != currentFrame) {
				// If the current frame is maximized, transfer that 
				// attribute to the frame being activated.
				if (currentFrame.isMaximum()) {
					currentFrame.setMaximum(false);
					f.setMaximum(true);
				}
				if (currentFrame.isSelected()) {
					currentFrame.setSelected(false);
				}
			}

			if (!f.isSelected()) {
				f.setSelected(true);
			}
			currentFrame = f;
		} catch (PropertyVetoException e) {}
	}
	/** This will activate <b>f</b> moving it to the front. It will
	  * set the current active frame (if any) IS_SELECTED_PROPERTY to false.
	  * There can be only one active frame across all Layers.
	  */
	public void activateFrameSuper(JInternalFrame f) {
		Container p = f.getParent();
		Component[] c;

		// fix for bug: 4162443
		if(p == null) {
			// If the frame is not in parent, it's icon maybe, check it
			p = f.getDesktopIcon().getParent();
			if(p == null)
				return;
		}
		// we only need to keep track of the currentActive InternalFrame, if any
		if (currentActiveFrame == null){
		  currentActiveFrame = f;
		}
		else if (currentActiveFrame != f) {
		  // if not the same frame as the current active
		  // we deactivate the current
		  if (currentActiveFrame.isSelected()) {
			try {
			  currentActiveFrame.setSelected(false);
			}
			catch(PropertyVetoException e2) {}
		  }
		  currentActiveFrame = f;
		}
		f.moveToFront();
	}
	/**
	 * Activate the next child JInternalFrame, as determined by
	 * the frames' Z-order.  If there is only one child frame, it
	 * remains activated.  If there are no child frames, nothing 
	 * happens.
	 */
	public void activateNextFrame() {
		switchFrame(true);
	}
	/** same as above but will activate a frame if none
	 *  have been selected
	 */
	public void activateNextFrame(JInternalFrame f){
	  initialFrame = f;
	  switchFrame(true);
	}
	/**
	 * Activate the previous child JInternalFrame, as determined by
	 * the frames' Z-order.  If there is only one child frame, it
	 * remains activated.  If there are no child frames, nothing 
	 * happens.
	 */
	public void activatePreviousFrame() {
		switchFrame(false);
	}
	// implements javax.swing.DesktopManager
	public void beginDraggingFrame(JComponent f) {
		setupDragMode(f);

		if (dragMode == FASTER_DRAG_MODE) {
		  floatingItems = findFloatingItems(f);
		  currentBounds = f.getBounds();
		  desktopBounds = f.getParent().getBounds();
		  desktopBounds.x = 0;
		  desktopBounds.y = 0;
		  desktopGraphics = f.getParent().getGraphics();
		  ((JInternalFrameEnhanced)f).isDragging = true;
		}

	}
	// implements javax.swing.DesktopManager
	public void beginResizingFrame(JComponent f, int direction) {
		setupDragMode(f);
	}
	public void closeFrame(JInternalFrame f) {
		activateNextFrame();
		childFrames.removeElement(f);
		closeFrameSuper(f);
	}
	/** Removes the frame, and if necessary the desktopIcon, from it's parent. */
	public void closeFrameSuper(JInternalFrame f) {
		if(f.getDefaultCloseOperation() == WindowConstants.DO_NOTHING_ON_CLOSE)/* ibm.947*/
		   return;                                               /* ibm.947*/
		if(f.getParent() != null) {
			Container c = f.getParent();
			c.remove(f);
			c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
		}
		removeIconFor(f);
		if(getPreviousBounds(f) != null)
			setPreviousBounds(f, null);
		if(wasIcon(f))
			setWasIcon(f, null);
	}
	// implements javax.swing.DesktopManager
	public void deactivateFrame(JInternalFrame f) {
	  if (currentActiveFrame == f)
		currentActiveFrame = null;
	}
	/** Removes the desktopIcon from it's parent and adds it's frame to the parent. */
	public void deiconifyFrame(JInternalFrame f) {
		JInternalFrame.JDesktopIcon desktopIcon;
		Dimension size;
		if(getPreviousBounds(f) != null && !f.isMaximum()) {     /* ibm.947*/
			setPreviousBounds(f, null);                          /* ibm.947*/
		}                                                        /* ibm.947*/

		desktopIcon = f.getDesktopIcon();
		if(desktopIcon.getParent() != null) {
			desktopIcon.getParent().add(f);
			removeIconFor(f);
			try { f.setSelected(true); } catch (PropertyVetoException e2) { }
		}
	}
	/**
	  * Moves the visible location of the frame being dragged
	  * to the location specified.  The means by which this occurs can vary depending
	  * on the dragging algorithm being used.  The actual logical location of the frame
	  * might not change until endDraggingFrame is called.
	  */
	public void dragFrame(JComponent f, int newX, int newY) {

		if (dragMode == OUTLINE_DRAG_MODE) {
			JDesktopPane desktopPane = getDesktopPane(f);
			if (desktopPane != null){
			  Graphics g = desktopPane.getGraphics();

			  g.setXORMode(Color.white);
			  if (currentLoc != null) {
				g.drawRect( currentLoc.x, currentLoc.y, f.getWidth()-1, f.getHeight()-1);
			  }
			  g.drawRect( newX, newY, f.getWidth()-1, f.getHeight()-1);
			  currentLoc = new Point (newX, newY);
			  g.setPaintMode();
			}
		} else if (dragMode == FASTER_DRAG_MODE) {
		  dragFrameFaster(f, newX, newY);
		} else {
			setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
		}
	}
  // =========== stuff for faster frame dragging ===================

   private void dragFrameFaster(JComponent f, int newX, int newY) {

	  Rectangle previousBounds = new Rectangle(currentBounds.x,
											   currentBounds.y,
											   currentBounds.width,
											   currentBounds.height);

   // move the frame
	  currentBounds.x = newX;
	  currentBounds.y = newY;

	  if ( isFloaterCollision(previousBounds, currentBounds) ) {
		 setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
		 //  System.out.println("Bail");
		 return;
	  }

	  emergencyCleanup(f);


	// System.out.println(previousBounds);
	  Rectangle visBounds = previousBounds.intersection(desktopBounds);
	//  System.out.println(previousBounds);


	 // System.out.println(visBounds);

	  // blit the frame to the new location
	  desktopGraphics.copyArea(visBounds.x,
							   visBounds.y,
							   visBounds.width,
							   visBounds.height,
							   newX - previousBounds.x,
							   newY - previousBounds.y);


	  f.setBounds(currentBounds);

	  // fake out the repaint manager.  We'll take care of everything
	  RepaintManager currentManager = RepaintManager.currentManager(f);
	  JComponent parent = (JComponent)f.getParent();
	  currentManager.markCompletelyClean(parent);
	  currentManager.markCompletelyClean(f);

	  // compute the minimal newly exposed area
	  // if the rects intersect then we use computeDifference.  Otherwise
	  // we'll repaint the entire previous bounds
	  Rectangle[] dirtyRects = null;
	  if ( previousBounds.intersects(currentBounds) ) {
		  dirtyRects = SwingUtilities.computeDifference(previousBounds, currentBounds);
	  } else {
		  dirtyRects = new Rectangle[1];
		  dirtyRects[0] = previousBounds;
		  //  System.out.println("no intersection");
	  };

	  // Fix the damage
	  for (int i = 0; i < dirtyRects.length; i++) {
		 parent.paintImmediately(dirtyRects[i]);
	  }

	  // new areas of blit were exposed
	  if ( !(visBounds.equals(previousBounds)) ) {
		 dirtyRects = SwingUtilities.computeDifference(previousBounds, desktopBounds);
		 for (int i = 0; i < dirtyRects.length; i++) {
			dirtyRects[i].x += newX - previousBounds.x;
			dirtyRects[i].y += newY - previousBounds.y;
			((JInternalFrameEnhanced)f).isDragging = false;

			parent.paintImmediately(dirtyRects[i]);
			((JInternalFrameEnhanced)f).isDragging = true;

		   // System.out.println(dirtyRects[i]);
		 }

	  }
   }   
   /**
	 * This method is here to clean up problems associated
	 * with a race condition which can occur when the full contents
	 * of a copyArea's source argument is not available onscreen.
	 * This uses brute force to clean up in case of possible damage
	 */
   private void emergencyCleanup(final JComponent f) {

		if ( ((JInternalFrameEnhanced)f).danger ) {

		   SwingUtilities.invokeLater( new Runnable(){
									   public void run(){

									   ((JInternalFrameEnhanced)f).isDragging = false;
									   f.paintImmediately(0,0,
														  f.getWidth(),
														  f.getHeight());

										//finalFrame.repaint();
										((JInternalFrameEnhanced)f).isDragging = true;
										// System.out.println("repair complete");
									   }});

			 ((JInternalFrameEnhanced)f).danger = false;
		}

   }         
	// implements javax.swing.DesktopManager
	public void endDraggingFrame(JComponent f) {
		if ( dragMode == OUTLINE_DRAG_MODE && currentLoc != null) {
			setBoundsForFrame(f, currentLoc.x, currentLoc.y, f.getWidth(), f.getHeight() );
			currentLoc = null;
		} else if (dragMode == FASTER_DRAG_MODE) {
			currentBounds = null;
			desktopGraphics = null;
			desktopBounds = null;
			((JInternalFrameEnhanced)f).isDragging = false;
		}
	}
	// implements javax.swing.DesktopManager
	public void endResizingFrame(JComponent f) {
		if ( dragMode == OUTLINE_DRAG_MODE && currentBounds != null) {
			setBoundsForFrame(f, currentBounds.x, currentBounds.y, currentBounds.width, currentBounds.height );
			currentBounds = null;
		}
	}
   private Rectangle[] findFloatingItems(JComponent f) {
	  Container desktop = f.getParent();
	  Component[] children = desktop.getComponents();
	  int i = 0;
	  for (i = 0; i < children.length; i++) {
		 if (children[i] == f) {
			break;
		 }
	  }
	  // System.out.println(i);
	  Rectangle[] floaters = new Rectangle[i];
	  for (i = 0; i < floaters.length; i++) {
		 floaters[i] = children[i].getBounds();
	  }

	  return floaters;
   }
	/** The iconifyFrame() code calls this to determine the proper bounds
	  * for the desktopIcon.
	  */

	protected Rectangle getBoundsForIconOf(JInternalFrame f) {
	  //
	  // Get the parent bounds and child components.
	  //

	  Container c = f.getParent();
	  Rectangle parentBounds = c.getBounds();
	  Component [] components = c.getComponents();

	  //
	  // Get the icon for this internal frame and its preferred size
	  //

	  JInternalFrame.JDesktopIcon icon = f.getDesktopIcon();
	  Dimension prefSize = icon.getPreferredSize();

	  //
	  // Iterate through valid default icon locations and return the
	  // first one that does not intersect any other icons.
	  //

	  Rectangle availableRectangle = null;
	  JInternalFrame.JDesktopIcon currentIcon = null;

	  boolean leftToRight = f.getComponentOrientation().isLeftToRight(); // SwingUtilities.isLeftToRight(f);          //ibm.597
	  int x = (leftToRight) ? 0 : parentBounds.width - prefSize.width - 1; //ibm.597
	  int y = parentBounds.height - prefSize.height - 1;              //ibm.597
	  int w = prefSize.width;
	  int h = prefSize.height;

	  boolean found = false;

	  while (!found) {

		availableRectangle = new Rectangle(x, y, w, h);               //ibm.597

		found = true;
		for ( int i = 0; i<components.length; i++ ) {                 //ibm.597

		  //
		  // Get the icon for this component
		  //

		  currentIcon = null;                                         //ibm.597
		  if ( components[i] instanceof JInternalFrame ) {
			currentIcon =                                             //ibm.597
					((JInternalFrame)components[i]).getDesktopIcon(); //ibm.597
		  }
		  else if ( components[i] instanceof JInternalFrame.JDesktopIcon ){
			currentIcon = (JInternalFrame.JDesktopIcon)components[i];
		  }

		  //
		  // If this icon intersects the current                      //ibm.597
		  // location, get next location                              //ibm.597
		  //

		  if ( !currentIcon.equals(icon) ) {
			if ( availableRectangle.intersects(currentIcon.getBounds()) ) {
			  found = false;
			  break;
			}
		  }
		}

		if (leftToRight) {                                            //ibm.597
		  x += currentIcon.getBounds().width;
		  if ( x + w > parentBounds.width ) {
			x = 0;
			y -= h;
		  }
		}
		else {                                                        //ibm.597
		  x -= currentIcon.getBounds().width;                         //ibm.597
		  if ( x < 0 ) {                                              //ibm.597
			x = parentBounds.width - prefSize.width;                  //ibm.597
			y -= h;                                                   //ibm.597
		  }                                                           //ibm.597
		}                                                             //ibm.597
	  }                                                               //ibm.597

	  return availableRectangle;                                      //ibm.597
	}
	JDesktopPane getDesktopPane( JComponent frame ) {
		JDesktopPane pane = null;
		Component c = frame.getParent();

		// Find the JDesktopPane
		while ( pane == null ) {
			if ( c instanceof JDesktopPane ) {
				pane = (JDesktopPane)c;
			}
			else if ( c == null ) {
				break;
			}
			else {
				c = c.getParent();
			}
		}

		return pane;
	}
	protected Rectangle getPreviousBounds(JInternalFrame f)     {
		return (Rectangle)f.getClientProperty(PREVIOUS_BOUNDS_PROPERTY);
	}
	/** Removes the frame from it's parent and adds it's desktopIcon to the parent. */
	public void iconifyFrame(JInternalFrame f) {
		JInternalFrame.JDesktopIcon desktopIcon;
		Container c;

		if (getPreviousBounds(f) == null) {                      /* ibm.947*/
		   setPreviousBounds(f, f.getBounds());                  /* ibm.947*/
		}                                                        /* ibm.947*/

		desktopIcon = f.getDesktopIcon();
		if(!wasIcon(f)) {
			Rectangle r = getBoundsForIconOf(f);
			desktopIcon.setBounds(r.x, r.y, r.width, r.height);
			setWasIcon(f, Boolean.TRUE);
		}

		c = f.getParent();

		if (c instanceof JLayeredPane) {
			JLayeredPane lp = (JLayeredPane)c;
			int layer = lp.getLayer(f);
			lp.putLayer(desktopIcon, layer);
		}

		c.remove(f);
		c.add(desktopIcon);
		c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
		try { f.setSelected(false); } catch (PropertyVetoException e2) { }
	}
   private boolean isFloaterCollision(Rectangle moveFrom, Rectangle moveTo) {
	  if (floatingItems.length == 0) {
		// System.out.println("no floaters");
		 return false;
	  }

	  for (int i = 0; i < floatingItems.length; i++) {
		 boolean intersectsFrom = moveFrom.intersects(floatingItems[i]);
		 if (intersectsFrom) {
			return true;
		 }
		 boolean intersectsTo = moveTo.intersects(floatingItems[i]);
		 if (intersectsTo) {
			return true;
		 }
	  }

	  return false;
   }
	/** Resizes the frame to fill it's parents bounds. */
	public void maximizeFrame(JInternalFrame f) {

		Rectangle p;
		if(!f.isIcon()) {
		setPreviousBounds(f, f.getBounds());
			p = f.getParent().getBounds();
			try { f.setMaximum(true); } catch (PropertyVetoException e2) { }/* ibm.1130*/
		} else {
			 Container c = f.getDesktopIcon().getParent();
			if(c == null)
				return;
			p = c.getBounds();
			try { f.setIcon(false); } catch (PropertyVetoException e2) { }
		}
		setBoundsForFrame(f, 0, 0, p.width, p.height);
		try { f.setSelected(true); } catch (PropertyVetoException e2) { }

		removeIconFor(f);
	}
	/** Restores the frame back to it's size and position prior to a maximizeFrame()
	  * call.
	  */
	public void minimizeFrame(JInternalFrame f) {
		if(getPreviousBounds(f) != null) {
			Rectangle r = getPreviousBounds(f);
			setPreviousBounds(f, null);
			try { f.setSelected(true); } catch (PropertyVetoException e2) { }
			if(f.isIcon())
				try { f.setIcon(false); } catch (PropertyVetoException e2) { }
			setBoundsForFrame(f, r.x, r.y, r.width, r.height);
		}
		removeIconFor(f);
	}
	/** Normally this method will not be called. If it is, it
	  * try to determine the appropriate parent from the desktopIcon of the frame.
	  * Will remove the desktopIcon from it's parent if it successfully adds the frame.
	  */
	public void openFrame(JInternalFrame f) {
		if(f.getDesktopIcon().getParent() != null) {
			f.getDesktopIcon().getParent().add(f);
			removeIconFor(f);
		}
	}
	/** Convience method to remove the desktopIcon of <b>f</b> is necessary. */
	protected void removeIconFor(JInternalFrame f) {
		JInternalFrame.JDesktopIcon di = f.getDesktopIcon();
		Container c = di.getParent();
		if(c != null) {
			c.remove(di);
			c.repaint(di.getX(), di.getY(), di.getWidth(), di.getHeight());
		}
	}
	/** Calls setBoundsForFrame() with the new values. */
	public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {

		if ( dragMode == DEFAULT_DRAG_MODE || dragMode == FASTER_DRAG_MODE ) {
			setBoundsForFrame(f, newX, newY, newWidth, newHeight);
		} else {
			JDesktopPane desktopPane = getDesktopPane(f);
			if (desktopPane != null){
			  Graphics g = desktopPane.getGraphics();

			  g.setXORMode(Color.white);
			  if (currentBounds != null) {
				g.drawRect( currentBounds.x, currentBounds.y, currentBounds.width-1, currentBounds.height-1);
			  }
			  g.drawRect( newX, newY, newWidth-1, newHeight-1);
			  currentBounds = new Rectangle (newX, newY, newWidth, newHeight);
			  g.setPaintMode();
			}
		}

	}
	/** This moves the JComponent and repaints the damaged areas. */
	public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
		boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
		f.setBounds(newX, newY, newWidth, newHeight);
		if(didResize) {
			f.validate();
		}
	}
	/** Stores the bounds of the component just before a maximize call. */
	protected void setPreviousBounds(JInternalFrame f, Rectangle r)     {
//        if (r != null) {                                       /* ibm.947*/
			f.putClientProperty(PREVIOUS_BOUNDS_PROPERTY, r);
//        }                                                      /* ibm.947*/
	}
	private void setupDragMode(JComponent f) {
		JDesktopPane p = getDesktopPane(f);
		if (p != null){
		  String mode = (String)p.getClientProperty("JDesktopPane.dragMode");
		  if (mode != null && mode.equals("outline")) {
			dragMode = OUTLINE_DRAG_MODE;
		  } else if (mode != null && mode.equals("faster") && f instanceof JInternalFrame) {
			dragMode = FASTER_DRAG_MODE;
		  } else {
			dragMode = DEFAULT_DRAG_MODE;
		  }
		}
	}
	/** Sets that the component has been iconized and the bounds of the
	  * desktopIcon are valid.
	  */
	protected void setWasIcon(JInternalFrame f, Boolean value)  {
		if (value != null) {
			f.putClientProperty(HAS_BEEN_ICONIFIED_PROPERTY, value);
		}
	}
	private void switchFrame(boolean next) {
		if (currentFrame == null) {
	    // initialize first frame we find
	    if (initialFrame != null)
	      activateFrame(initialFrame);
			return;
		}

		int count = childFrames.size();
		if (count <= 1) {
			// No other child frames.
			return;
		}

		int currentIndex = childFrames.indexOf(currentFrame);
		if (currentIndex == -1) {
			// should never happen...
			return;
		}

		int nextIndex;
		if (next) {
			nextIndex = currentIndex + 1;
			if (nextIndex == count) {
				nextIndex = 0;
			}
		} else {
			nextIndex = currentIndex - 1;
			if (nextIndex == -1) {
				nextIndex = count - 1;
			}
		}
		JInternalFrame f = (JInternalFrame)childFrames.elementAt(nextIndex);
		activateFrame(f);
		currentFrame = f;
	}
	protected boolean wasIcon(JInternalFrame f) {
		return (f.getClientProperty(HAS_BEEN_ICONIFIED_PROPERTY) == Boolean.TRUE);
	}
}
