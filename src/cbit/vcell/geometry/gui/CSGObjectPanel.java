package cbit.vcell.geometry.gui;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;

public class CSGObjectPanel extends JPanel {
	public CSGObjectPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JTree tree = new JTree();
		add(tree);
	}

}
