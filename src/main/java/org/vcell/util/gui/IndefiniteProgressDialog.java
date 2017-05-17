package org.vcell.util.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.help.UnsupportedOperationException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.util.BeanUtils;

@SuppressWarnings("serial")

public class IndefiniteProgressDialog extends ProgressDialog implements ActionListener {
	private final static int INITIAL_UPDATE_TIME_MILLIS = 40; 	// update time, milliseconds
	private final static int SHOW_DELAY_TIME_MILLIS = 1000; 

	private JLabel lblMessage;
	private javax.swing.Timer displayTimer;
	private javax.swing.Timer workingPanelTimer;

	public IndefiniteProgressDialog(LWContainerHandle owner) {
		super(owner);
		setLocationRelativeTo(null);
		setContentPane(new JPanel( ));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		displayTimer = new Timer(SHOW_DELAY_TIME_MILLIS, this);
		workingPanelTimer = new Timer(INITIAL_UPDATE_TIME_MILLIS, null);
		JPanel graphicPanel = new WorkingPanel(workingPanelTimer);
		Dimension ps = new Dimension(GRAPHIC_SIZE, GRAPHIC_SIZE);
		graphicPanel.setPreferredSize(ps);
		graphicPanel.setBackground(Color.WHITE);
		getContentPane().add(graphicPanel);						// left
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		ps = new Dimension(DialogWidth, GRAPHIC_SIZE);
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
		lblMessage.setHorizontalAlignment(SwingConstants.LEFT);
		JButton cButton = super.getCancelButton(); 
		cancelPanel.add(cButton);

		pack( );
	}
	
	@Override
	public void setToVisible( )  {
		if (!isVisible()) {
			displayTimer.start();
		}
	}
	
	@Override
	public void setVisible(boolean b) {
		displayTimer.stop( );
		super.setVisible(b);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isVisible()) {
			displayTimer.stop( );
			super.setVisible(true);
		}
	}

	@Override
	public void dispose( ) {
		workingPanelTimer.stop();
		super.dispose( );
		if (LG.isDebugEnabled()) {
			LG.debug(getClass( ).getName() + " " + message + " disposed");
		}
		
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
	public void setMessageImpl(String s) {
		
//		DocumentWindow dw = getMainFrame();
//		if(dw != null) {
//			Runnable r = new StatusBarMessageThread(dw, s);
//			new Thread(r).start();
//		}

		int len = s.length();
		if(len > MaxLen) {
			s = s.substring(0,TruncHeaderLen) + ".." + s.substring(len-TruncTailLen, len);
		}
		lblMessage.setText(s);
	}
	
	private class WorkingPanel extends JPanel implements ComponentListener, ActionListener {

		final Timer swingTimer;
		static final double INCREMENT = Math.PI / 12;	// step angle

		int width;			// of panel
		int height;
		double radians;		// current position
		
		WorkingPanel(Timer t) {
			radians = Math.PI;
			swingTimer = t; 
			swingTimer.addActionListener(this);
			swingTimer.setRepeats(true);
			swingTimer.start();
			this.addComponentListener(this);
		}
		
		@Override
		public void paint(Graphics g) {
			// Arc2D with a fairly thick BasicStroke drawn with a GradientPaint.
			// we only use width, may want to adjust and use min between width and height
			
//			float x = (float) (width/2 + radius * Math.sin(radians));
//			float y = (float) (height/2+ radius * Math.cos(radians));

			Graphics2D g2d = (Graphics2D)g;
			Stroke oldStroke = g2d.getStroke();
			Paint oldPaint = g2d.getPaint();
			g2d.clearRect(0, 0, width, height);
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			final float StrokeWidth = 7.0f;
			final int circleOffset = 15;
			final double extent = 80;
			
			BasicStroke bs = new BasicStroke(StrokeWidth);
			g2d.setStroke(bs);				// background ring
			g2d.setPaint(Color.lightGray);
			Arc2D a2d = new Arc2D.Double(circleOffset, circleOffset, width-circleOffset*2, width-circleOffset*2, 0, 360, Arc2D.OPEN);
			g2d.draw(a2d);
			
			double r =  (width-circleOffset*2) / 2;
			float y = (float)(width/2 + r * Math.cos(radians));
			float x = (float)(width/2 + r * Math.sin(radians));
			Point2D center = new Point2D.Float(x, y);
			float rr = 55;
			Point2D focus = new Point2D.Float(x, y);
			float[] dist = {0.1f, 0.2f, 0.7f};
			Color[] colors = {Color.green, Color.green, Color.lightGray};
			RadialGradientPaint paint = new RadialGradientPaint(center, rr, focus, dist, colors, CycleMethod.NO_CYCLE);
			g2d.setPaint(paint);

//			g2d.setPaint(Color.gray);		// moving indicator
			bs = new BasicStroke(StrokeWidth-2);
			g2d.setStroke(bs);
			a2d = new Arc2D.Double(circleOffset, circleOffset, width-circleOffset*2, width-circleOffset*2, Math.toDegrees(radians), extent, Arc2D.OPEN);
			g2d.draw(a2d);
			
			g2d.setStroke(oldStroke);		// borders of background ring
			g2d.setColor(Color.darkGray);
			Ellipse2D.Double c1 = new Ellipse2D.Double(circleOffset-3, circleOffset-3, width-(circleOffset-3)*2, width-(circleOffset-3)*2);
			g2d.draw(c1);
			Ellipse2D.Double c2 = new Ellipse2D.Double(circleOffset+3, circleOffset+3, width-(circleOffset+3)*2, width-(circleOffset+3)*2); 
			g2d.draw(c2);
			
			g2d.setPaint(oldPaint);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!isModelDialogPresent()) {
				radians -= INCREMENT;
				repaint( );
			}
		}
		
		/**
		 * @return true if visible dialog is modal
		 */
		private boolean isModelDialogPresent( ) {
			for (Window w: Window.getWindows()) {
				if (w.isShowing()) {
					Dialog d = BeanUtils.downcast(Dialog.class, w);
					if (d != null && d != IndefiniteProgressDialog.this) {
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
		}
		@Override
		public void componentResized(ComponentEvent arg0) {
			width = getWidth();
			height = getHeight();
			//System.out.println(width + ", " + height);
		}
		@Override
		public void componentShown(ComponentEvent arg0) {
			swingTimer.start( );
		}
	}
}
