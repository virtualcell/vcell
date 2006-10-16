/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.namescope;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model;
import cbit.vcell.desktop.BioModelNode;
import javax.swing.JTree;

import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;
import org.vcell.expression.ui.ScopedExpression;

import cbit.gui.JTableFixed;
/**
 * Insert the type's description here.
 * Creation date: (4/12/2004 3:27:37 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ModelNameScopeDisplayCellRenderer extends cbit.vcell.desktop.VCellBasicCellRenderer {
	private NameScopeParametersPanel tablePanel = null;
	private java.util.Hashtable tableHash = null;
/**
 * NameScopeDisplayCellRenderer constructor comment.
 */
public ModelNameScopeDisplayCellRenderer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/14/2004 4:07:12 PM)
 * @return javax.swing.JPanel
 */
private String getExpressionString(IExpression expression, NameScope nameScope) {
	String str = null;
	try {
		ScopedExpression scopedExp = new org.vcell.expression.ui.ScopedExpression(expression, nameScope);
		str = scopedExp.toString();
	} catch (Exception e) {
		e.printStackTrace(System.out);
		str = "Error!";
	}
	
	return str;
}
/**
 * Gets the tableHash property (java.util.Hashtable) value.
 * @return The tableHash property value.
 * @see #setTableHash
 */
public java.util.Hashtable getTableHash() {
	return tableHash;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

	tree.setRowHeight(0);
	tablePanel = new NameScopeParametersPanel();

	
	if (!leaf && expanded) {
		setIcon(fieldFolderOpenIcon);
	}else if (!leaf && !expanded) {
		setIcon(fieldFolderClosedIcon);
	}

	try {
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			boolean bLoaded = false;

			if (node.getUserObject() instanceof BioModel) {
				BioModel bioModel = (BioModel)node.getUserObject();
				this.setText(bioModel.getName() /*+" : "+model.getVersion().getDate()*/);
				return this;
			} else if (node.getUserObject() instanceof String) {
				this.setText((String)node.getUserObject());
				return this;
			} else if (node.getUserObject() instanceof Model.ModelParameter[]) {
				tablePanel.getTableHeaderLabel().setText("Model Parameters");
				JTableFixed aJTable = (JTableFixed)tableHash.get(node.getUserObject());
				if (aJTable != null) {
					tablePanel.setAJTable(aJTable);
				} else {
					return this;
				}
			} else if (node.getUserObject() instanceof Structure) {
				Structure struct = (Structure)node.getUserObject();
				this.setText(struct.getName());
				return this;
			} else if (node.getUserObject() instanceof ReactionStep) {
				this.setText(((ReactionStep)node.getUserObject()).getName());
				return this;
			} else if (node.getUserObject() instanceof Kinetics) {
				Kinetics kinetics = (Kinetics)node.getUserObject();
				tablePanel.getTableHeaderLabel().setText("Reaction Parameters (Kinetics and Unresolved)");
				ReactionStep reactStep = ((Kinetics)node.getUserObject()).getReactionStep();
				JTableFixed aJTable = (JTableFixed)tableHash.get(reactStep);
				if (aJTable != null) {
					tablePanel.setAJTable(aJTable);
				} else {
					return this;
				}
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}

	return tablePanel;
}
/**
 * Sets the tableHash property (java.util.Hashtable) value.
 * @param tableHash The new value for the property.
 * @see #getTableHash
 */
public void setTableHash(java.util.Hashtable newTableHash) {
	tableHash = newTableHash;
}
}
