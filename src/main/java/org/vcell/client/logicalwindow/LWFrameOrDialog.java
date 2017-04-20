package org.vcell.client.logicalwindow;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentListener;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.vcell.client.logicalwindow.LWHandle.LWModality;

import cbit.vcell.client.ChildWindowManager;

/**
 * these are methods called by {@link ChildWindowManager.ChildWindow} that are implemented by
 * {@link JDialog} and {@link JFrame} -- see either class for description 
 */
public interface LWFrameOrDialog {

	//existing JFrame / JDialog methods
	public void addWindowListener(WindowListener windowListener);
	public void addWindowFocusListener(WindowFocusListener focusListener);
	public void addComponentListener(ComponentListener componentListener);
	public void dispose();
	public Container getContentPane();
	public Point getLocation();
	public Point getLocationOnScreen();
	public Container getParent();
	public String getTitle();
	public boolean isShowing();
	public boolean isVisible();
	public void pack();
	public void requestFocus();
	public boolean requestFocusInWindow();
	public void setAlwaysOnTop(boolean b);
	public void setLocation(int x, int y);
	public void setLocation(Point location);
	public void setLocationRelativeTo(Component parent);
	public void setPreferredSize(Dimension preferredSize);
	public void setResizable(boolean resizable);
	public void setSize(Dimension dim);
	public void setSize(int i, int j);
	public void setTitle(String title);
	public void setVisible(boolean b);
	public void toFront();
	//end of existing
	
	
	/**
	 * used to distinguish modality types 
	 * see {@link LWHandle#getLWModality()}
	 */
	public LWModality getLWModality( );
	/**
	 * @return implementation as {@link Window} 
	 */
	public Window self( );
	public LWTraits getTraits();
}
