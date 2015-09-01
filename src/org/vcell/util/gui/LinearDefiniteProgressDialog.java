package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;

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

public class LinearDefiniteProgressDialog extends ProgressDialog implements ActionListener {
	private final static int INITIAL_UPDATE_TIME_MILLIS = 40; 	// update time, milliseconds
	private final static int SHOW_DELAY_TIME_MILLIS = 1000; 

	private JLabel lblMessage;
	private Timer displayTimer;
	private Timer workingPanelTimer;
	private WorkingPanel graphicPanel;
	
	private String currentMessage = "Working";

	public LinearDefiniteProgressDialog(LWContainerHandle owner) {
		super(owner);
		setLocationRelativeTo(null);
		setContentPane(new JPanel( ));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		Dimension ps = new Dimension(DialogWidth, 70);
		getContentPane().setPreferredSize(ps);
		
		JPanel msgPanel = new JPanel();
		msgPanel.setBackground(Color.white);
		getContentPane().add(msgPanel);

		displayTimer = new Timer(SHOW_DELAY_TIME_MILLIS, this);
		workingPanelTimer = new Timer(INITIAL_UPDATE_TIME_MILLIS, null);
		
		graphicPanel = new WorkingPanel(workingPanelTimer);
		ps = new Dimension(DialogWidth, 30);
		graphicPanel.setPreferredSize(ps);
		graphicPanel.setBackground(Color.WHITE);
		getContentPane().add(graphicPanel);
		
		JPanel cancelPanel = new JPanel();
		cancelPanel.setBackground(Color.white);
		getContentPane().add(cancelPanel);
		
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
	}
	/**
	 * @throws UnsupportedOperationException (always)
	 */
	@Override
	public void setProgress(int progress) {
//		System.out.println("progress " + progress);
		graphicPanel.setProgress(progress);
		graphicPanel.repaint();
		lblMessage.setText(currentMessage + "  (" + graphicPanel.getProgress() + "%)");
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
		currentMessage = s;
		lblMessage.setText(currentMessage + "  (" + graphicPanel.getProgress() + "%)");
	}
	
	// ===========================================================================================================
	// draws the progress bar
	// the timer is used to display the watchdog animation - that is, a small colored image moving along the 
	// progress bar area to show that the system is not crashed
	private class WorkingPanel extends JPanel implements ComponentListener, ActionListener {

		final Timer swingTimer;
		static final double INCREMENT = 1;		// 5% increment for the moving light

		int width;				// of panel
		int height;
		
		int progress;			// position (in percent) of the right end of progress bar
		int curx = 0;			// position (in percent) of the moving highlight
		int pauseTicks = 1;		// number of timer ticks we wait before we start again the watchdog animation

		WorkingPanel(Timer t) {
			swingTimer = t; 
			swingTimer.addActionListener(this);
			swingTimer.setRepeats(true);
			swingTimer.start();
			this.addComponentListener(this);
		}
		
		public void setProgress(int progress) {
//			System.out.println("progress: " + progress);
			this.progress = progress;
		}
		public int getProgress() {
			return progress;
		}

		@Override
		public void paint(Graphics g) {

			Graphics2D g2d = (Graphics2D)g;
			Font fontOld = g2d.getFont();
			Color colorOld = g2d.getColor();
			Stroke oldStroke = g2d.getStroke();
			Paint oldPaint = g2d.getPaint();
			
			g2d.clearRect(0, 0, width, height);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			final int width = 330;
			final int height = 16;
			int pady = 4;
			double padx = 12;
			double position = (double)progress * (double)width / 100.0;

			final Shape contour = new Rectangle2D.Double( padx-1, (double)pady-1, (double)(width+1), (double)(height-2*pady+1));
			final Shape empty = new Rectangle2D.Double( padx, (double)pady, (double)width, (double)(height-2*pady));
			double extent = position+1;		// at 0% we draw a 1 pixel wide green line to show activity has started
			if(extent>width) {
				extent = width;				// we correct that for 100% because otherwise green bar would be 1 pixel too wide
			}
			final Shape filled = new Rectangle2D.Double( padx, (double)pady, extent, (double)(height-2*pady));
//			System.out.println(progress + "%, position: " + position + ", extent: " + extent);
			
			g2d.setPaint(Color.gray);
			g2d.draw(contour);
			g2d.setPaint(Color.white);
			g2d.fill(empty);

			Paint greentowhite = new GradientPaint(0, 0, Color.white, (int)extent, 0, Color.green);				// paint for progress bar
			g2d.setPaint(greentowhite);
			g2d.fill(filled);		// draw the progress bar
			
			double dcurx_start = curx * (double)width / 100.0;
			if(pauseTicks == 0 && curx <= progress && dcurx_start < extent) {
				Paint greentowhite2 = new GradientPaint(0, 0, Color.white, (int)dcurx_start, 0, Color.green);	// paint for watchdog inner bar
				g2d.setPaint(greentowhite2);
				Shape watchdog = new Rectangle2D.Double( padx, (double)pady+2, dcurx_start, (double)(height-2*pady-4));
				g2d.fill(watchdog);	// draw the watchdog animation
			}
			
//			Font fontPercent = new Font("Tahoma", Font.PLAIN, 17);
//			g2d.setStroke(oldStroke);
//			g2d.setFont(fontPercent);
//			g2d.setColor(Color.black);
//			int x = width/2-5;
//			g2d.drawString(progress+"%", x, height/2+6);
			
			if(pauseTicks == 0) {
				curx += INCREMENT;
			}
			if(curx > 100 || curx > progress) {
				curx = 0;
				if(progress < 6) {
					pauseTicks = 40;
				} else if (progress < 15 && progress >= 6) {
					pauseTicks = 20;
				} else {
					pauseTicks = 10;
				}
			}
			g2d.setPaint(oldPaint);
			g2d.setFont(fontOld);
			g2d.setColor(colorOld);
			g2d.setStroke(oldStroke);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(pauseTicks >0) {
				pauseTicks--;
			}
			if (!isModelDialogPresent()) {
				repaint();
			}
		}
		
		/**
		 * @return true if visible dialog is modal
		 */
		private boolean isModelDialogPresent( ) {
			for (Window w: Window.getWindows()) {
				if (w.isShowing()) {
					Dialog d = BeanUtils.downcast(Dialog.class, w);
					if (d != null && d != LinearDefiniteProgressDialog.this) {
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
