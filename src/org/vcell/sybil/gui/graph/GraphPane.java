package org.vcell.sybil.gui.graph;

/*   GraphPane  --- by Oliver Ruebenacker, UCHC --- July 2007 to November 2009
 *   Creates a panel for the graph
 */

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.vcell.sybil.models.graph.GraphListener;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.graphlayout.LayoutType;

import org.vcell.sybil.gui.graph.layouter.Layouter;

public class GraphPane extends JPanel implements Scrollable, GraphListener<Shape, Graph> {

	private static final long serialVersionUID = -8351722066078284775L;
	private Graph graph;

	public GraphPane() {
		super();
		setName("GraphPane");
		setLayout(null);
		setBackground(java.awt.Color.WHITE);
		setSize(280, 280);
	}

	public void clear(java.awt.Graphics g) { super.paint(g); }
	public Graph graph() { return graph; }

	private javax.swing.JScrollPane getJScrollPaneParent() {
		if (getParent() instanceof javax.swing.JScrollPane){
			return (javax.swing.JScrollPane)getParent();
		} else if (getParent() instanceof javax.swing.JViewport && getParent().getParent() instanceof javax.swing.JScrollPane){
			return (javax.swing.JScrollPane)getParent().getParent();
		} else {
			return null;
		}
	}

	public java.awt.Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }

	public Dimension getPreferredSize() {
		if (graph!=null){
			Dimension prefSize = graph.getPreferedSize((java.awt.Graphics2D)getGraphics());
			if (getJScrollPaneParent()!=null){
				java.awt.Rectangle viewBorderBounds = getJScrollPaneParent().getViewportBorderBounds();
				prefSize = new Dimension(Math.max(viewBorderBounds.width,prefSize.width),
						Math.max(viewBorderBounds.height,prefSize.height));
			}
			return prefSize;
		}else{
			return super.getPreferredSize();
		}
	}

	public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
		if (javax.swing.SwingConstants.VERTICAL == orientation){
			return visibleRect.height/4;
		} else {
			return visibleRect.width/4;
		}
	}

	public boolean getScrollableTracksViewportHeight() { return false; }
	public boolean getScrollableTracksViewportWidth() { return false; }
	public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 1; }
	public boolean isFocusTraversable() { return true; }

	public void paintComponent(java.awt.Graphics argGraphics) {
		super.paintComponent(argGraphics);
		java.awt.Graphics2D g = (java.awt.Graphics2D)argGraphics;
		if (graph!=null){
			try { 
				graph.resize(g,this.getSize());
			} 
			catch (Exception e) { CatchUtil.handle(e); }
			graph.paint(g,this);
		}	
	}

	public void setGraph(Graph graphNew) {
		if (graph != null) { graph.listeners().remove(this); }
		if(graphNew instanceof UIGraph<?, ?>) { 
			graph = (Graph) graphNew; 
		} else {
			graph = null;
		}
		if (graph != null) { graph.listeners().add(this); }
		updateAll();
	}

	public void layoutGraph(LayoutType newLayout) { 
		try { 
			Layouter layouter = Layouter.registry.get(newLayout);
			if(layouter != null) { layouter.applyToGraph(graph()); } 
			updateView();
		} 
		catch (Exception e) { CatchUtil.handle(e); }
	}

	public void updateAll() {
		if (graph != null){
			if (getJScrollPaneParent() != null){
				invalidate();
				getJScrollPaneParent().revalidate();
			}
			repaint();
		}
	}

	// TODO get focus
	public void updateView() { updateAll(); }

}