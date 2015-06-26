package org.vcell.util.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")

@Deprecated
public class DefiniteProgressDialog extends ProgressDialog {
	
	private static final Font fontPercent = new Font("Tahoma", Font.PLAIN, 10);

	private JLabel lblMessage;
	private WorkingPanel graphicPanel;

	public DefiniteProgressDialog(Frame owner) {
		super(owner);
		setContentPane(new JPanel( ));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		graphicPanel = new WorkingPanel();
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
	public void setProgress(int progress) {
//		System.out.println("progress " + progress);
		graphicPanel.setAngle(progress);
		graphicPanel.repaint();
	}
	@Override
	void setProgressBarString(String progressString) {
		setTitle(progressString);
	}
	@Override
	public void setMessage(String s) {
		
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
	
	private static class WorkingPanel extends JPanel implements ComponentListener, ActionListener {

		int width;			// of panel
		int height;
		double angle;		// current position
		int progress;
		
		WorkingPanel() {
			this.addComponentListener(this);
		}
		
		public void setAngle(int progress) {
			this.progress = progress;
			this.angle = (double)progress * 3.6;
		}
		
		@Override
		public void paint(Graphics g) {

			Graphics2D g2d = (Graphics2D)g;
			Font fontOld = g2d.getFont();
			Color colorOld = g2d.getColor();
			Stroke oldStroke = g2d.getStroke();
			g2d.clearRect(0, 0, width, height);
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			final float StrokeWidth = 7.0f;
			final int circleOffset = 15;
			final double extent = 30;
			BasicStroke bs = new BasicStroke(StrokeWidth);
			g2d.setStroke(bs);				// background ring
			g2d.setPaint(Color.lightGray);
			Arc2D a2d = new Arc2D.Double(circleOffset, circleOffset, width-circleOffset*2, width-circleOffset*2, 0, 360, Arc2D.OPEN);
			g2d.draw(a2d);
			
			g2d.setPaint(Color.green);		// moving indicator
			bs = new BasicStroke(StrokeWidth-1);
			g2d.setStroke(bs);
//			double start = 270-5 -angle;
//			double end = 270-5 ;
//			a2d = new Arc2D.Double(circleOffset, circleOffset, width-circleOffset*2, width-circleOffset*2, start, end, Arc2D.OPEN);
			a2d = new Arc2D.Double(circleOffset, circleOffset, width-circleOffset*2, width-circleOffset*2, 0, angle, Arc2D.OPEN);
			g2d.draw(a2d);
			
			g2d.setStroke(oldStroke);		// borders of background ring
			g2d.setColor(Color.darkGray);
			Ellipse2D.Double c1 = new Ellipse2D.Double(circleOffset-3, circleOffset-3, width-(circleOffset-3)*2, width-(circleOffset-3)*2);
			g2d.draw(c1);
			Ellipse2D.Double c2 = new Ellipse2D.Double(circleOffset+3, circleOffset+3, width-(circleOffset+3)*2, width-(circleOffset+3)*2); 
			g2d.draw(c2);
						
			g2d.setStroke(oldStroke);
//			float size = fontOld.getSize();
//			Font f = fontOld.deriveFont(size-1);
			g2d.setFont(fontPercent);
			g2d.setColor(Color.black);
//			System.out.println("w: " + width + ", h: " + height);
			int x = width/3+2;
			if(progress < 10) {
				x += 4;
			} else if(progress == 100 && x > 2) {
				x -= 2;
			}
			g2d.drawString(progress+"%", x, height/2+2);
			
			g2d.setFont(fontOld);
			g2d.setColor(colorOld);
			g2d.setStroke(oldStroke);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
		}
		
		@Override
		public void componentHidden(ComponentEvent arg0) {
		}
		@Override
		public void componentMoved(ComponentEvent arg0) {
		}
		@Override
		public void componentResized(ComponentEvent arg0) {
			width = getWidth();
			height = getHeight();
			System.out.println(width + ", " + height);
		}
		@Override
		public void componentShown(ComponentEvent arg0) {
		}
	}
}
