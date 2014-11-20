package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class SwingPaintDemo {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
		
	private static void createAndShowGUI() {
		System.out.println("Created GUI on EDT? "+ SwingUtilities.isEventDispatchThread());
		JFrame f = new JFrame("Swing Paint Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(250,250);
		f.add(new MyPanel());
        f.pack();
		f.setVisible(true);
	}
}

class MyPanel extends JPanel {
	
	MolecularType mt = new MolecularType("egfr");
	SpeciesGlyph speciesGlyph = new SpeciesGlyph(50, 50, mt);

	public MyPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				moveSquare(e.getX(),e.getY());
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				moveSquare(e.getX(),e.getY());
			}
		});
	}
	
	private void moveSquare(int x, int y) {
		// current square state, stored as final variables to avoid repeat invocations of the same methods.
		final int CURR_X = speciesGlyph.getX();
		final int CURR_Y = speciesGlyph.getY();
		final int CURR_W = speciesGlyph.getWidth();
		final int CURR_H = speciesGlyph.getHeight();
		final int OFFSET = 1;
		
		if ((CURR_X!=x) || (CURR_Y!=y)) {
			repaint(CURR_X,CURR_Y,CURR_W+OFFSET,CURR_H+OFFSET);	// repaint background over the old square location.
			speciesGlyph.setX(x);			// Update coordinates.
			speciesGlyph.setY(y);
			repaint(speciesGlyph.getX(), speciesGlyph.getY(), 		// repaint the square at the new location.
					speciesGlyph.getWidth()+OFFSET, speciesGlyph.getHeight() + OFFSET);
			} 
		}

	public Dimension getPreferredSize() {
		return new Dimension(250,200);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawString("This is my custom Panel!", 10, 20);
		speciesGlyph.paintSpecies(g);
	}
}

class SpeciesGlyph{
	private static final int componentSeparation = 3;		// distance between components
	private static final int componentDiameter = 14;		// diameter of the component
	private static final int baseWidth = 35;
	private static final int baseHeight = 28;
	private static final int cornerArc = 25;

	private int xPos = 0;
	private int yPos = 0;
		private int width = baseWidth;
	private int height = baseHeight;

	MolecularType mt = null;

	public SpeciesGlyph(int xPos, int yPos, MolecularType mt) {
		this.mt = mt;
		this.xPos = xPos;
		this.yPos = yPos;
		
		width = baseWidth + mt.componentList.size() * (componentDiameter + componentSeparation);	// adjusted for # of components
		height = baseHeight + componentDiameter / 2;
	}
	
	public void setX(int xPos){ 
		this.xPos = xPos;
	}
	public int getX(){
		return xPos;
	}
	public void setY(int yPos){
		this.yPos = yPos;
	}
	public int getY(){
		return yPos;
	}
	public int getWidth(){
		return width;
	} 
	public int getHeight(){
		return height;
	}
	
	public void paintSpecies(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint p = new GradientPaint(xPos, yPos, Color.GRAY, xPos, yPos + baseHeight/2, Color.WHITE, true);
		g2.setPaint(p);

		RoundRectangle2D rect = new RoundRectangle2D.Float(xPos, yPos, width, baseHeight, cornerArc, cornerArc);
		g2.fill(rect);

		RoundRectangle2D inner = new RoundRectangle2D.Float(xPos+1, yPos+1, width-2, baseHeight-2, cornerArc-3, cornerArc-3);
		g2.setPaint(Color.GRAY);
		g2.draw(inner);
		g2.setPaint(Color.DARK_GRAY);
		g2.draw(rect);
		
		g2.drawString(mt.name, xPos+8, yPos+baseHeight-8);
		
		for(int i=0; i<mt.componentList.size(); i++) {
			paintComponent(g, mt.componentList.size()-i);
		}
	}
	public void paintComponent(Graphics g, int index) {
		int fromRight = index*(componentDiameter + componentSeparation);
		int x = xPos + width - fromRight - 5;		// we compute distance from right end
		int y = yPos + baseHeight - componentDiameter/2;
		
		g.setColor(Color.YELLOW);
		g.fillOval(x, y, componentDiameter, componentDiameter);			// g.fillRect(xPos, yPos, width, height);
		g.setColor(Color.BLACK);
		g.drawOval(x, y, componentDiameter, componentDiameter);
	}

}

class MolecularType {
	String name = "no name";
	List<String> componentList = new ArrayList<String> ();
	
	public MolecularType(String name) {
		this.name = name;
		componentList.add("s");
		componentList.add("t");
		componentList.add("tyr");
	}
	
	public String getName() {
		return name;
	}
	public List<String> getComponentList() {
		return componentList;
	}
	
}







