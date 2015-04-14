package org.vcell.util.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.help.UnsupportedOperationException;
import javax.swing.BoxLayout;
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
	private final static int GRAPHIC_SIZE = 60;
	/**
	 * update time, milliseconds
	 */
	private final static int INITIAL_UPDATE_TIME_MILLIS = 60; 		// 400
	/**
	 * update time not to exceed 
	 */
	private final static int LONGEST_UPDATE_TIME_MILLIS = 60; 		// 1200
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
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		
		
		
		swingTimer = new Timer(INITIAL_UPDATE_TIME_MILLIS, null);
		JPanel graphicPanel = new WorkingPanel(swingTimer);
		Dimension ps = new Dimension(GRAPHIC_SIZE, GRAPHIC_SIZE);
		graphicPanel.setPreferredSize(ps);
		getContentPane().add(graphicPanel);						// left
		
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		ps = new Dimension(350, GRAPHIC_SIZE);
		rightPanel.setPreferredSize(ps);
		getContentPane().add(rightPanel);
		
		
		JPanel msgPanel = new JPanel();
		msgPanel.setBackground(Color.white);
		rightPanel.add(msgPanel);
		
		JPanel cancelPanel = new JPanel();
		cancelPanel.setBackground(Color.white);
		rightPanel.add(cancelPanel);
		
		
		lblMessage = new JLabel("Message");
		msgPanel.add(lblMessage);
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

		JButton cButton = super.getCancelButton(); 
		cancelPanel.add(cButton);

		
		
//		getContentPane().setLayout(new BorderLayout(0, 0));
//		
//		JPanel msgPanel = new JPanel();
//		getContentPane().add(msgPanel, BorderLayout.NORTH);
//		
//		lblMessage = new JLabel("Message");
//		msgPanel.add(lblMessage);
//		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
//		
//		swingTimer = new Timer(INITIAL_UPDATE_TIME_MILLIS, null);
//		JPanel graphicPanel = new WorkingPanel(swingTimer);
//		getContentPane().add(graphicPanel, BorderLayout.CENTER);
//		
//		JPanel panel = new JPanel();
//		getContentPane().add(panel, BorderLayout.SOUTH);
//		
//		JButton cButton = super.getCancelButton(); 
//		panel.add(cButton);
		
		
		
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
		System.out.println("ProgressStr: " + progressString);				// WORKING...
	}

	@Override
	public void setMessage(String message) {
		System.out.println("Message: " + message);		// Importing document
		Dimension before = lblMessage.getPreferredSize();
		lblMessage.setText(message);
		// TODO: just truncate the message here
//		Dimension after = lblMessage.getPreferredSize();
//		if (after.width > before.width) {
//			pack( );
//		}
//		if (lg.isDebugEnabled()) {
//			lg.debug ("msg current "+ lblMessage.getSize( ) );
//			lg.debug ("msg preferred  " +  lblMessage.getPreferredSize() );
//			lg.debug ("dlg current "+ getSize( ) );
//			lg.debug ("dlg preferred  " +  getPreferredSize() );
//		}
	}
	
	private static class WorkingPanel extends JPanel implements ComponentListener, ActionListener {
		/** step angle **/
		static final double INCREMENT = Math.PI / 8;		// 20
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
		boolean adjustingRate;
		
		final Timer swingTimer;
		
		WorkingPanel(Timer t) {
			//width - height -radius set in #componentResized 
			radians = Math.PI;
			colorIndex = 0;
			adjustingRate = true;
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
			// Arc2D with a fairly thick BasicStroke drawn with a GradientPaint.
			// loading circle animation
			
//			float x = (float) (width/2 + radius * Math.sin(radians));
//			float y = (float) (height/2+ radius * Math.cos(radians));

			Graphics2D g2d = (Graphics2D)g;
			g2d.clearRect(0, 0,width,height);
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			
			Stroke oldStroke = g2d.getStroke();
//			Point2D center = new Point2D.Float(x, y);
//			float rr = 40f;
//			Point2D focus = new Point2D.Float(x, y);
//			float[] dist = {0.1f, 1.0f};
//			Color[] colors = {Color.blue, Color.lightGray};
//			RadialGradientPaint paint = new RadialGradientPaint(center, rr, focus, dist, colors, CycleMethod.REFLECT);

			BasicStroke bs = new BasicStroke(7.0f);
			g2d.setStroke(bs);
			g2d.setPaint(Color.lightGray);
			Arc2D a2d = new Arc2D.Double(15,15,width-30,width-30, 0,360, Arc2D.OPEN);
			g2d.draw(a2d);

			
			g2d.setPaint(Color.gray);
			bs = new BasicStroke(5.0f);
			g2d.setStroke(bs);

			a2d = new Arc2D.Double(15,15,width-30,width-30,radians*57.296,20, Arc2D.OPEN);
			g2d.draw(a2d);
			
			g2d.setStroke(oldStroke);
			g2d.setColor(Color.darkGray);
			Ellipse2D.Double c1 = new Ellipse2D.Double(12,12,width-24,width-24); 
			g2d.draw(c1);
			Ellipse2D.Double c2 = new Ellipse2D.Double(18,18,width-36,width-36); 
			g2d.draw(c2);

		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!isModelDialogPresent()) {
				radians -= INCREMENT;
				repaint( );
				if (adjustingRate) {
					final int currentDelay = swingTimer.getDelay();
					if (lg.isDebugEnabled()) {
						lg.debug("cd " + currentDelay);
					}
					if (currentDelay < LONGEST_UPDATE_TIME_MILLIS) {
						final int updated = Math.min(currentDelay + DELAY_ADJUST_RATE,LONGEST_UPDATE_TIME_MILLIS);
						swingTimer.setDelay(updated);
					}
					else {
						adjustingRate = false;
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
			height = getHeight();
			radius = 3 * Math.min(width,height)/10;
			System.out.println("w: " + width + ",  h: " + height + ",  r: " + radius);
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			swingTimer.start( );
		}
	}
}
