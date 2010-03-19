package org.vcell.sybil.gui.space;

/*   GUIGraphSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   A GUISpace for a SybilGraph
 */

import org.vcell.sybil.models.graph.GraphListener;
import org.vcell.sybil.util.event.Bounded;
import org.vcell.sybil.util.graphlayout.LayoutType;
import org.vcell.sybil.util.ui.UIModelGraphSpace;
import org.vcell.sybil.gui.space.GUISpace;
import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.GraphEditorPanel;
import org.vcell.sybil.gui.graph.Shape;

public class GUIGraphSpace<P extends GraphEditorPanel> extends GUISpace<P> 
implements UIModelGraphSpace<Shape, Graph> {

	protected Bounded<GraphListener<Shape, Graph>> graphViewer;
	
	protected GUIGraphSpace(P newPanel) { 
		super(newPanel); 
		graphViewer = new Bounded<GraphListener<Shape, Graph>>();
		graphViewer.setValue(graphPanel().graphPane());
	}
	
	public GraphEditorPanel graphPanel() { return component(); }
	public Graph graph() { return graphPanel().graph(); }
	public void layoutGraph(LayoutType layoutType) { graphPanel().graphPane().layoutGraph(layoutType); }
	public void setZoomPercent(final int newZoomPercent) { graph().setZoomPercent(newZoomPercent); }
	public int zoomPercent() { return graph().zoomPercent(); }

	public Bounded<GraphListener<Shape, Graph>> graphViewer() { return graphViewer; }

	public void updateUI() { graphPanel().revalidate(); }
	
}
