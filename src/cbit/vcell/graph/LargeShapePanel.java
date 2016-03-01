package cbit.vcell.graph;

import javax.swing.JPanel;

import org.vcell.util.Displayable;

@SuppressWarnings("serial")
public class LargeShapePanel extends JPanel {
	
	// here we store the entity that needs to be displayed highlighted (selected object and maybe its container)
	
	// the entity that is selected (for example a molecule, component, a state, a pattern...) 
	// that and will be painted as "highlighted"
	private Displayable highlightedEntity = null;
	
	// used to draw the light blue border around the container (reactant pattern for example) that contains 
	// the selected entity (a state for example)
	private Displayable highlightedContainer = null;	
	
	public Displayable getHighlightedEntity() {
		return highlightedEntity;
	}
	public void setHighlightedEntity(Displayable highlightedEntity) {
		this.highlightedEntity = highlightedEntity;
	}
	
	public Displayable getHighlightedContainer() {
		return highlightedContainer;
	}
	public void setHighlightedContainer(Displayable highlightedContainer) {
		this.highlightedContainer = highlightedContainer;
	}
	
	

}
