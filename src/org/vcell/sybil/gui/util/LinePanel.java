package org.vcell.sybil.gui.util;

/*   LinePanel  --- by Oliver Ruebenacker, UCHC --- June 2008
 *   An extension of JPanel to arrange components in lines (which are JPanels)
 */

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class LinePanel extends JPanel {

	private static final long serialVersionUID = -9180525908935446067L;

	public LinePanel() { init(); }
	public LinePanel(boolean isDoubleBuffered) { super(isDoubleBuffered); init(); }
	
	protected void init() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}
	
	public void addLine(JComponent comp) {
		JPanel line;
		if(comp instanceof JPanel) { line = (JPanel) comp; }
		else {
			line = new JPanel(new FlowLayout(FlowLayout.LEFT));
			comp.setAlignmentX(Component.LEFT_ALIGNMENT);
			line.add(comp);
		}
		line.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(line);
	}
		
	public void addLine(JComponent comp1, JComponent comp2) {
		JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
		comp1.setAlignmentX(Component.LEFT_ALIGNMENT);
		line.add(comp1);
		comp2.setAlignmentX(Component.LEFT_ALIGNMENT);
		line.add(comp2);
		line.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(line);
	}
		
	public void addLine(JComponent comp1, JComponent comp2, JComponent comp3) {
		JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
		comp1.setAlignmentX(Component.LEFT_ALIGNMENT);
		line.add(comp1);
		comp2.setAlignmentX(Component.LEFT_ALIGNMENT);
		line.add(comp2);
		comp3.setAlignmentX(Component.LEFT_ALIGNMENT);
		line.add(comp3);
		line.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(line);
	}
		
	public void addLine(Collection<JComponent> comps) {
		JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(JComponent comp : comps) { 
			comp.setAlignmentX(Component.LEFT_ALIGNMENT);
			line.add(comp); 
		}
		line.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(line);
		
	}
	
}
