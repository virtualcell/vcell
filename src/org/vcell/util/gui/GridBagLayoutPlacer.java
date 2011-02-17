package org.vcell.util.gui;

import java.awt.GridBagConstraints;

public class GridBagLayoutPlacer {

	protected GridBagConstraints lastConstraints = placeAt(0, 0);
	
	public GridBagConstraints placeAt(int x, int y) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = x;
		constraints.gridy = y;
		lastConstraints = constraints;
		return constraints;
	}
	
	public GridBagConstraints placeAt(int x, int y, int width, int height) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = width;
		constraints.gridheight = height;
		lastConstraints = constraints;
		return constraints;
	}

	public GridBagConstraints placeBelowLast() {
		return placeAt(lastConstraints.gridx, lastConstraints.gridy + 1);
	}
	
	public static class Loop {
		
		protected final GridBagLayoutPlacer placer;
		protected int x, y, width, height, xStep, yStep;
		
		public Loop(GridBagLayoutPlacer placer, int xStart, int yStart, int xStep, int yStep, 
				int width, int height) {
			this.placer = placer;
			this.x = xStart;
			this.y = yStart;
			this.xStep = xStep;
			this.yStep = yStep;
			this.width = width;
			this.height = height;
		}
		
		public Loop(GridBagLayoutPlacer placer, int xStart, int yStart, int xStep, int yStep) {
			this(placer, xStart, yStart, xStep, yStep, 1, 1);
		}
		
		public GridBagConstraints next() {
			GridBagConstraints constraint = placer.placeAt(x, y, width, height);
			x += xStep;
			y += yStep;
			return constraint;
		}
		
	}
	
	public Loop createLoop(int x, int y, int xStep, int yStep) {
		return new Loop(this, x, y, xStep, yStep);
	}
	
	public Loop createLoop(int x, int y, int xStep, int yStep, int width, int height) {
		return new Loop(this, x, y, xStep, yStep, width, height);
	}
	
	public GridBagConstraints firstInNewLine() {
		if(lastConstraints == null) {
			return placeAt(0, 0);
		} else {
			return placeAt(0, lastConstraints.gridy + 1);
		}
	}
	
	public GridBagConstraints fillOutNewLine() {
		GridBagConstraints constraints = firstInNewLine();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		return constraints;
	}

	public GridBagConstraints nextInLine() {
		if(lastConstraints == null) {
			return placeAt(0, 0);
		} else {
			return placeAt(lastConstraints.gridx + 1, lastConstraints.gridy);
		}		
	}
	
	public GridBagConstraints nextInLineToEnd() {
		GridBagConstraints constraints = nextInLine();
		
		return constraints;
	}
	
}
