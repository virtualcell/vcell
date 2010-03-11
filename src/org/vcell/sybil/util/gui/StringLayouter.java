package org.vcell.sybil.util.gui;

/*   StringLayouter  --- by Oliver Ruebenacker, UCHC --- October 2008
 *   Calculate the measures for an Icon with a string in the center
 */

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class StringLayouter {

	public static class Layout {
		
		protected String string;
		protected int x, y, w, h;
		
		public Layout(String string, int x, int y, int w, int h) {
			this.string = string;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		public String string() { return string; }
		public int x() { return x; }
		public int y() { return y; }
		public int w() { return w; }
		public int h() { return h; }
		
	}
	
	public static class Padding {
		public int left, right, top, bottom;
	}
	
	public FontMetrics metrics;
	public String string = "";
	public Padding padding = new Padding();
	
	public StringLayouter(Graphics graphics) { this(graphics.getFontMetrics()); }
	public StringLayouter(Graphics graphics, Font font) { this(graphics.getFontMetrics(font)); }
	public StringLayouter(FontMetrics metrics) { this.metrics = metrics; }
	
	public FontMetrics metrics() { return metrics; }

	public void setAllPadding(int pad) {
		padding.left = pad;
		padding.right = pad;
		padding.top = pad;
		padding.bottom = pad;
	}
	
	public Layout layout() {
		return new Layout(string, padding.left, metrics.getAscent() + padding.top, 
				metrics.stringWidth(string) + padding.left + padding.right,
				metrics.getHeight() + padding.top + padding.bottom);
	}
	
}
