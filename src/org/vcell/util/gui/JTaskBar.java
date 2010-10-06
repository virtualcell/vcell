package org.vcell.util.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;


public class JTaskBar extends JPanel {
	class EqualSizesLayout implements LayoutManager, SwingConstants {

	    /** The number of pixels to place between components */
	    private int gap;

	    /** The alignment of the layout (LEFT or RIGHT) */
	    private int alignment;

	    /**
	     * Creates a layout manager with the given layout and gap.
	     * 
	     * @param alignment Either LEFT or RIGHT
	     * @param gap Pixels to put between components
	     */
	    public EqualSizesLayout(int alignment, int gap) {
	        this.gap = gap;
	        this.alignment = alignment;
	    }

	    /**
	     * Get the preferred size of the biggest component and the size of the
	     * overall layout.
	     * 
	     * @param children The children on this component.
	     * @return The preferred sizes of components and the overall layout.
	     */
	    private Dimension[] dimensions(Component children[]) {
	        int maxWidth = 0;
	        int maxHeight = 0;
	        int visibleCount = 0;
	        Dimension componentPreferredSize;

	        for (int i = 0, c = children.length; i < c; i++) {
	            if (children[i].isVisible()) {
	                componentPreferredSize = children[i].getPreferredSize();
	                maxWidth = Math.max(maxWidth, componentPreferredSize.width);
	                maxHeight = Math.max(maxHeight, componentPreferredSize.height);
	                visibleCount++;
	            }
	        }

	        int usedWidth = maxWidth * visibleCount + gap * (visibleCount - 1);
	        int usedHeight = maxHeight;
	        return new Dimension[] { new Dimension(maxWidth, maxHeight),
	                new Dimension(usedWidth, usedHeight), };
	    }

	    /**
	     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	     */
	    public void layoutContainer(Container container) {
	        Insets insets = container.getInsets();

	        Component[] children = container.getComponents();
	        Dimension dim[] = dimensions(children);

	        int allowedWidth = container.getWidth();
	        int maxWidth = dim[0].width;
	        int maxHeight = dim[0].height;
	        int usedWidth = dim[1].width;

	        // Narrow the components from their preferred size if we're limited for
	        // space
	        if (usedWidth > allowedWidth) {
	            usedWidth = allowedWidth;
	            maxWidth = (allowedWidth / children.length)
	                    - (gap * children.length);
	        }

	        switch (alignment) {
	        case LEFT:
	        case TOP:
	            for (int i = 0, c = children.length; i < c; i++) {
	                if (!children[i].isVisible())
	                    continue;
	                children[i].setBounds(insets.left + (maxWidth + gap) * i,
	                        insets.top, maxWidth, maxHeight);
	            }
	            break;
	        case RIGHT:
	        case BOTTOM:
	            for (int i = 0, c = children.length; i < c; i++) {
	                if (!children[i].isVisible())
	                    continue;
	                children[i].setBounds(container.getWidth() - insets.right
	                        - usedWidth + (maxWidth + gap) * i, insets.top,
	                        maxWidth, maxHeight);
	            }
	            break;
	        }
	    }

	    /**
	     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	     */
	    public Dimension minimumLayoutSize(Container c) {
	        return preferredLayoutSize(c);
	    }

	    /**
	     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	     */
	    public Dimension preferredLayoutSize(Container container) {
	        Insets insets = container.getInsets();

	        Component[] children = container.getComponents();
	        Dimension dim[] = dimensions(children);

	        int usedWidth = dim[1].width;
	        int usedHeight = dim[1].height;

	        return new Dimension(insets.left + usedWidth + insets.right, insets.top
	                + usedHeight + insets.bottom);
	    }

	    /**
	     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
	     *      java.awt.Component)
	     */
	    public void addLayoutComponent(String string, Component comp) {
	    }

	    /**
	     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	     */
	    public void removeLayoutComponent(Component c) {
	    }
	}
	
	/** The buttons on the task bar; only one selected at a time at most. */
	private ButtonGroup buttonGroup;

	/**
	 * Generate a task bar based on the windows of this desktop.
	 * 
	 * @param desktop
	 *            The desktop to create a task bar for.
	 */
	public JTaskBar(final JDesktopPane desktop) {
		super();
		setLayout(new EqualSizesLayout(EqualSizesLayout.LEFT, 0));
		this.buttonGroup = new ButtonGroup();

		desktop.addContainerListener(new ContainerListener() {
			public void componentRemoved(ContainerEvent e) {
				desktop.revalidate();  // Redo layout to avoid weird position
			}

			public void componentAdded(ContainerEvent e) {
				if (e.getChild() instanceof JInternalFrame
						&& !contains((JInternalFrame) e.getChild())) {
					add(new TaskPaneAction((JInternalFrame) e.getChild()));
				}
			}
		});

		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			add(new TaskPaneAction(frames[i]));
		}
	}

	/**
	 * Determine if the given frame is already in the task bar.
	 * 
	 * @param frm
	 *            The frame to check for.
	 * @return True if already in task bar.
	 */
	protected boolean contains(JInternalFrame frm) {
		int limit = getComponentCount();
		for (int i = 0; i < limit; i++) {
			AbstractButton btn = (AbstractButton) getComponent(i);
			if (((TaskPaneAction) btn.getAction()).getFrame().equals(frm)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a button for a new window in the task bar.
	 * 
	 * @param act
	 *            The action for the button being created.
	 */
	protected void add(TaskPaneAction act) {
		TaskButton btn = new TaskButton(act);

		act.setButton(btn);
		this.buttonGroup.add(btn);
		add(btn);
	}

	/**
	 * Remove the button for this frame/action from the task bar.
	 * 
	 * @param act
	 *            The action for the button being created.
	 */
	protected void remove(Action act) {
		int limit = getComponentCount();
		for (int i = 0; i < limit; i++) {
			TaskButton btn = (TaskButton) getComponent(i);
			if (btn.getAction() == act) {
				remove(btn);
				this.buttonGroup.remove(btn);
				this.getLayout().layoutContainer(this.getParent());
				return;
			}
		}
	}

	private class TaskPaneAction extends AbstractAction implements InternalFrameListener {

		/** The frame for the task bar button */
		private JInternalFrame frm;

		/** The button on the task bar */
		private TaskButton btn;

		/**
		 * Create an action that dictates the behavior of the button for a given
		 * frame in the desktop.
		 * 
		 * @param frm
		 *            The frame for the task bar button
		 */
		public TaskPaneAction(JInternalFrame frm) {
			super(frm.getTitle(), frm.getFrameIcon());

			this.frm = frm;
			frm.addInternalFrameListener(this);
		}

		/**
		 * @param btn
		 *            The new button for this frame.
		 */
		public void setButton(TaskButton btn) {
			this.btn = btn;
			btn.setSelected(!frm.isIcon());
		}

		/**
		 * @return The frame being monitored.
		 */
		public JInternalFrame getFrame() {
			return frm;
		}

		/**
		 * Button was pushed so either select the window if not selected, or
		 * iconify it if selected.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				if (frm.isSelected() && !frm.isIcon()) {
					frm.setIcon(true);
				} else {
					if (frm.isIcon()) {
						frm.setIcon(false);
					} else {
						frm.setSelected(true);
					}
				}
			} catch (PropertyVetoException ex) {
				ex.printStackTrace();
			}
		}

		public void internalFrameOpened(InternalFrameEvent e) {
			btn.setSelected(true);
		}

		public void internalFrameIconified(InternalFrameEvent e) {
			e.getInternalFrame().setVisible(false); // Hide from view
			btn.setSelected(false);
		}
		
		public void internalFrameDeiconified(InternalFrameEvent e) {
			e.getInternalFrame().setVisible(true); // Show again
			btn.setSelected(true);
		}

		public void internalFrameDeactivated(InternalFrameEvent e) {
			btn.setSelected(false);
		}

		public void internalFrameClosing(InternalFrameEvent e) {
			btn.setSelected(false);
		}

		public void internalFrameClosed(InternalFrameEvent e) {
			remove(this);
		}

		public void internalFrameActivated(InternalFrameEvent e) {
			btn.setSelected(true);
		}
	}

	/**
	 * Custom button which places it's tooltip above itself. Otherwise the
	 * default tooltip position means it is covered by the Windows task bar when
	 * the application is maximized.
	 */
	private class TaskButton extends JToggleButton {
		TaskButton(TaskPaneAction action) {
			super(action);
			setHorizontalAlignment(SwingConstants.LEFT);
			setPreferredSize(new Dimension(160, 24));
			setToolTipText(action.getFrame().getTitle());
		}

		/**
		 * Regular tooltip placement hides the tooltip under the Windows taskbar
		 * when the main window is maximized. This will ensure it is visible.
		 * 
		 * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
		 */
		public Point getToolTipLocation(MouseEvent event) {
			Point location = ((Component) event.getSource()).getLocationOnScreen();
			location.y -= 20;
			location.x += 25;
			SwingUtilities.convertPointFromScreen(location, this);
			return location;
		}
	}
}
