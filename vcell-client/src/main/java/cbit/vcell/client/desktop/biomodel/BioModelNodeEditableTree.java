package cbit.vcell.client.desktop.biomodel;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import cbit.vcell.desktop.BioModelNode;

public class BioModelNodeEditableTree extends JTree {
	@Override
	public boolean isPathEditable(TreePath path) {
		Object object = path.getLastPathComponent();
		return object instanceof BioModelNode;
	}
}