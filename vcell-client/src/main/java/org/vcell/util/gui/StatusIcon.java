package org.vcell.util.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;

import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;


public class StatusIcon implements javax.swing.Icon {

	private int width = 0;
	private int height = 0;
	SchedulerStatus status = SchedulerStatus.FAILED;
	
	public StatusIcon(int width, int height, SimulationJobStatus.SchedulerStatus status) {
		this.width = width;
		this.height = height;
		this.status = status;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if(c == null) {
			return;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
		Stroke strokeOld = g2.getStroke();
		Object hintOld = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		
		Color borderColor = Color.lightGray;
		Color insideColor = Color.white;
		switch(status) {
		case WAITING:
			borderColor = Color.lightGray;
			insideColor = Color.white;
			break;
		case QUEUED:
//			borderColor = Color.yellow.darker().darker();
			borderColor = Color.orange;
			insideColor = Color.lightGray;
			break;
		case DISPATCHED:
			borderColor = Color.magenta.darker();
			insideColor = Color.magenta;
			break;
		case RUNNING:
			borderColor = Color.blue;
			insideColor = Color.cyan;
			break;
		case COMPLETED:
			borderColor = Color.green.darker().darker();
			insideColor = Color.green;
			break;
		case STOPPED:
			borderColor = Color.orange.darker();
			insideColor = Color.yellow;
			break;
		case FAILED:
		default:
			borderColor = Color.red.darker().darker();
			insideColor = Color.red;
			break;
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(insideColor);
		g.fillRect(x,y,width,height);
		
		g2.setStroke(new BasicStroke(1.8f));
		g2.setPaint(borderColor);
		g.drawRect(x,y,width,height);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hintOld);
		g2.setStroke(strokeOld);
		g2.setColor(colorOld);
		g2.setPaint(paintOld);
	}
	
}
