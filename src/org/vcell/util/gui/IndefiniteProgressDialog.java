package org.vcell.util.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Ellipse2D;

import javax.help.UnsupportedOperationException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;

@SuppressWarnings("serial")

public class IndefiniteProgressDialog extends ProgressDialog {
	/**
	 * size of main dialog
	 */
	//private final static int DIALOG_SIZE = 400;
	/**
	 * size of rotating dot panel
	 */
	private final static int GRAPHIC_SIZE = 120;
	/**
	 * update time, milliseconds
	 */
	private final static int INITIAL_UPDATE_TIME_MILLIS = 200; 
	/**
	 * update time not to exceed 
	 */
	private final static int LONGEST_UPDATE_TIME_MILLIS = 1200; 
	/**
	 * update time not to exceed 
	 */
	private final static int DELAY_ADJUST_RATE = 10; 

	private JLabel lblMessage;
	private Timer swingTimer;
	
	private static Logger lg = Logger.getLogger(IndefiniteProgressDialog.class);

	public IndefiniteProgressDialog(Frame owner) {
		super(owner);
		setLocationRelativeTo(null);
		setContentPane(new JPanel( ));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel msgPanel = new JPanel();
		getContentPane().add(msgPanel, BorderLayout.NORTH);
		
		lblMessage = new JLabel("Message");
		msgPanel.add(lblMessage);
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		
		swingTimer = new Timer(INITIAL_UPDATE_TIME_MILLIS, null);
		JPanel graphicPanel = new WorkingPanel(swingTimer);
		getContentPane().add(graphicPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton cButton = super.getCancelButton(); 
		panel.add(cButton);
		pack( );
	}
	
	@Override
	public void dispose( ) {
		swingTimer.stop();
		super.dispose( );
	}
	
	/**
	 * @throws UnsupportedOperationException (always)
	 */
	@Override
	public void setProgress(int progress) {
		throw new UnsupportedOperationException(getClass( ).getName() + " does not support setProgress");
	}

	@Override
	void setProgressBarString(String progressString) {
		setTitle(progressString);
	}

	@Override
	public void setMessage(String message) {
		Dimension before = lblMessage.getPreferredSize();
		lblMessage.setText(message);
		Dimension after = lblMessage.getPreferredSize();
		if (after.width > before.width) {
			pack( );
		}
		if (lg.isDebugEnabled()) {
			lg.debug ("msg current "+ lblMessage.getSize( ) );
			lg.debug ("msg preferred  " +  lblMessage.getPreferredSize() );
			lg.debug ("dlg current "+ getSize( ) );
			lg.debug ("dlg preferred  " +  getPreferredSize() );
		}
	}
	
	private static class WorkingPanel extends JPanel implements ComponentListener, ActionListener {
		/** step angle **/
		static final double INCREMENT = Math.PI / 20;
		/** how big the dot **/
		static final int DOT_DIAMETER = 10; 
		
		static final Color colors[] = {Color.BLACK,Color.BLUE,Color.CYAN,Color.DARK_GRAY,Color.GRAY,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,
				Color.ORANGE,Color.PINK,Color.RED,Color.PINK}; 
		
		/** of panel **/
		int width;
		/** of panel **/
		int height;
		/** of circle **/
		int radius;
		/** current position */
		double radians;
		/** index into #colors **/
		int colorIndex;
		/** at longest rate? **/
		boolean adjusting;
		
		final Timer swingTimer;
		
		WorkingPanel(Timer t) {
			//width - height -radius set in #componentResized 
			radians = 0;
			colorIndex = 0;
			adjusting = true;
			setPreferredSize(new Dimension(GRAPHIC_SIZE, GRAPHIC_SIZE));
			swingTimer = t; 
			swingTimer.addActionListener(this);
			swingTimer.setRepeats(true);
			swingTimer.start();
			this.addComponentListener(this);
			setBackground(Color.WHITE);
		}
		
		@Override
		public void paint(Graphics g) {
			double x = width/2 + radius * Math.sin(radians);
			double y = height/2+ radius * Math.cos(radians);

			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(colors[colorIndex]);
			g2d.clearRect(0, 0,width,height); 
			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, DOT_DIAMETER,DOT_DIAMETER); 
			g2d.fill(circle);
			if (++colorIndex == colors.length) {
				colorIndex = 0;
			}
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!isModelDialogPresent()) {
				radians -= INCREMENT;
				repaint( );
				if (adjusting) {
					final int currentDelay = swingTimer.getDelay();
					if (lg.isDebugEnabled()) {
						lg.debug("cd " + currentDelay);
					}
					if (currentDelay < LONGEST_UPDATE_TIME_MILLIS) {
						final int updated = Math.min(currentDelay + DELAY_ADJUST_RATE,LONGEST_UPDATE_TIME_MILLIS);
						swingTimer.setDelay(updated);
					}
					else {
						adjusting = false;
					}
				}
			}
		}
		
		/**
		 * @return true if visible dialog is modal
		 */
		private boolean isModelDialogPresent( ) {
			for (Window w: Window.getWindows()) {
				if (w.isShowing()) {
					Dialog d = BeanUtils.downcast(Dialog.class, w);
					if (d != null) {
						switch (d.getModalityType()) {
						case MODELESS:
							continue;
						default:
							return true;
						}
					}
				}
			}
			return false;
		}

		@Override
		public void componentHidden(ComponentEvent arg0) {
			swingTimer.stop( );
			
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			//don't care
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			width = getWidth();
			height = getHeight( );
			radius = 4 * Math.min(width,height)/10;
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			swingTimer.start( );
		}
	}
}
