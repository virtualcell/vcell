package cbit.vcell.client.desktop.biomodel;

import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DocumentEditorTreeCellEditor extends DefaultTreeCellEditor {

	public DocumentEditorTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
		super(tree, renderer);
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		// TODO Auto-generated method stub
		return super.isCellEditable(event);
	}
}
