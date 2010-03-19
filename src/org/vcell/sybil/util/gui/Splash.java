package org.vcell.sybil.util.gui;

/*   Splash  --- by Oliver Ruebenacker, UCHC --- September 2008 to June 2009
 *   Generate a splash image with Sybil logo and version number
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import org.vcell.sybil.models.specs.SybilSpecs;

public class Splash extends BufferedImage {

	protected static int widthDefault = 700;
	protected static int heightDefault = 200;
	protected int width;
	protected int height;
	private Graphics graphics;
	
	public Splash() { this(widthDefault, heightDefault); }
		
	public Splash(int width, int height) {
		super(width, height, BufferedImage.TYPE_INT_RGB);
		this.width = width;
		this.height = height;
		graphics = getGraphics();
		fillBackground();
		drawBottomText(SybilSpecs.longText);
		drawCenterText(SybilSpecs.shortText);
		
	}

	private void fillBackground() {
		Color blue = new Color(0, 0, 255);
		graphics.setColor(blue);
		graphics.fillRect(0, 0, width, height);
	}

	private void drawBottomText(String text) {
		Color white = new Color(255, 255, 255);
		graphics.setColor(white);
		FontMetrics metrics = graphics.getFontMetrics();
		Rectangle2D bounds = metrics.getStringBounds(text, graphics);
		graphics.drawString(text, (int) (width - bounds.getWidth()) / 2, height - 20);
	}

	private void drawCenterText(String text) {
		Color yellow = new Color(255, 255, 0);
		graphics.setColor(yellow);
		Font bigFont = graphics.getFont().deriveFont(AffineTransform.getScaleInstance(3, 3));
		graphics.setFont(bigFont);
		FontMetrics metrics = graphics.getFontMetrics();
		Rectangle2D bounds = metrics.getStringBounds(text, graphics);
		graphics.drawString(text, (int) (width - bounds.getWidth()) / 2, 
				(int) (height - bounds.getHeight())/2 + metrics.getAscent());
	}

	static protected void writeFile(Splash splash, String fileName, String format) {
		System.out.println("Writing " + fileName);
		try { 
			ImageIO.write(splash, format, new File(fileName)); 
			System.out.println("Done writing " + fileName);
		} catch (Throwable t) { 
			t.printStackTrace(); 
		}		
	}
	
	public static void main(String[] args) {
		String fileBaseName = "bin/splash";
		writeFile(new Splash(), fileBaseName + ".png", "PNG");
		// writeFile(new Splash(), fileBaseName + ".gif", "GIF");
	}
	
	
}
