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
package cbit.vcell.xml.merge.gui;
import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.merge.ChangedNodeInfo;
import cbit.vcell.xml.merge.DefaultXmlComparePolicy;
import cbit.vcell.xml.merge.NodeInfo;
import cbit.vcell.xml.merge.XmlComparePolicy;
import cbit.vcell.xml.merge.XmlTreeDiff;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.gui.DialogUtils;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
The panel that processes the XML compare and merge feature of VC
 * Creation date: (8/8/2000 11:50:44 AM) 
 * @author: 
 */
public class TMLPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTree ivjTree = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JButton ivjkeepModButton = null;
	private javax.swing.JButton ivjkeepBaseButton = null;
	private javax.swing.JEditorPane ivjBaselineEditorPane = null;
	private javax.swing.JLabel ivjBaselineLabel = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JEditorPane ivjModifiedEditorPane = null;
	private javax.swing.JLabel ivjModifiedLabel = null;
	private boolean ivjConnPtoP1Aligning = false;
	private NodeInfo ivjnodeInfo1 = null;
	private javax.swing.JButton ivjdisassociateButton = null;
	private javax.swing.JButton ivjassociate = null;
	private javax.swing.JLabel ivjBaselineVersionInfoLabel = null;
	private String ivjbaselineVersionDescription1 = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private String ivjmodifiedVersionDescription1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.JLabel ivjModifiedVersionInfoLabel = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;
	private NodeInfo fieldNodeInfo;
	private XmlTreeDiff fieldDiffTree;
	private String fieldBaselineVersionDescription = new String();
	private String fieldModifiedVersionDescription = new String();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TMLPanel.this.getkeepBaseButton()) 
				connEtoC1(e);
			if (e.getSource() == TMLPanel.this.getkeepModButton()) 
				connEtoC2(e);
			if (e.getSource() == TMLPanel.this.getdisassociateButton()) 
				connEtoC7(e);
			if (e.getSource() == TMLPanel.this.getassociate()) 
				connEtoC8(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == TMLPanel.this && (evt.getPropertyName().equals("nodeInfo"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == TMLPanel.this && (evt.getPropertyName().equals("baselineVersionDescription"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == TMLPanel.this && (evt.getPropertyName().equals("modifiedVersionDescription"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == TMLPanel.this.getBaselineVersionInfoLabel() && (evt.getPropertyName().equals("text"))) 
				connPtoP4SetSource();
			if (evt.getSource() == TMLPanel.this.getModifiedVersionInfoLabel() && (evt.getPropertyName().equals("text"))) 
				connPtoP5SetSource();
		};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == TMLPanel.this.getTree()) 
				connEtoM1(e);
			if (e.getSource() == TMLPanel.this.getTree()) 
				connEtoM2(e);
			if (e.getSource() == TMLPanel.this.getTree()) 
				connEtoC4(e);
			if (e.getSource() == TMLPanel.this.getTree()) 
				connEtoC3(e);
		};
	};

/**
 * TMLPanel constructor comment.
 */
public TMLPanel() {
	super();
	initialize();
}

/**
 * TMLPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public TMLPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * TMLPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public TMLPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * TMLPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public TMLPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * This function associates two NodeInfo nodes if they share the same type and parent.
 */
private void associate() {
	TreePath paths[] = getTree().getSelectionPaths();

	if (getTree().getSelectionCount()!=2) {
		throw new IllegalArgumentException("Invalid number of selected paths: "+getTree().getSelectionCount());
	}
	//get the two selected nodes
	NodeInfo firstNode = (NodeInfo)paths[0].getLastPathComponent();
	NodeInfo secondNode = (NodeInfo)paths[1].getLastPathComponent();
	NodeInfo parent = (NodeInfo)firstNode.getParent();

	if (firstNode.isAttribute() || secondNode.isAttribute()) {
		displayMessage(this, "Can only merge elements, not attributes.");
	}
	if (parent== secondNode.getParent()) {
		if ( firstNode.getName().equalsIgnoreCase(secondNode.getName()) ) {
			if (firstNode.getStatus()!= secondNode.getStatus()) {
				//merge the two nodes
				NodeInfo result = null;
				try {
					//this diff tree is different from the instance variable.
					XmlTreeDiff partialDiffTree = XmlTreeDiff.compareMerge(firstNode.toXmlString(),secondNode.toXmlString(), XmlTreeDiff.COMPARE_DOCS_SAVED, fieldDiffTree.getXmlComparePolicy());
					result = partialDiffTree.getMergedRootNode();
				}catch (Throwable e){
					//
					// can give more feedback ... like a JOptionPane.showMessage()
					//
					handleException(e);
				}
				if (result!= null) {
					//erase the original nodes
					((DefaultTreeModel)(getTree().getModel())).removeNodeFromParent(firstNode);
					((DefaultTreeModel)(getTree().getModel())).removeNodeFromParent(secondNode);
					//add the resulting node
					parent.add(result);
					((DefaultTreeModel)(getTree().getModel())).reload(parent);
				} else {
					displayMessage(this, "The merge operation could not be performed!");
				}
			} else {
				displayMessage(this, "Can only merge nodes with different status!");
			}
		} else {
		//they do not have the same type!
		displayMessage(this, "These nodes cannot be merged because they do not have the same type!");
		}
	} else {
		//they do not have the same parent!
		displayMessage(this, "These nodes cannot be merged because they do not share the same node parent!");
	}
}


/**
 * connEtoC1:  (keepBaseButton.action.actionPerformed(java.awt.event.ActionEvent) --> TMLPanel.keepBaseLine(Ljava.lang.Object;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.keepBaseLine(getTree().getLastSelectedPathComponent());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> TMLPanel.keepModified(Ljava.lang.Object;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.keepModified(getTree().getLastSelectedPathComponent());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (Tree.treeSelection.valueChanged(javax.swing.event.TreeSelectionEvent) --> TMLPanel.resetTextFieldAttributes()V)
 * @param arg1 javax.swing.event.TreeSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.TreeSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.resetTextFieldAttributes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (Tree.treeSelection.valueChanged(javax.swing.event.TreeSelectionEvent) --> TMLPanel.resetButtonEnables()V)
 * @param arg1 javax.swing.event.TreeSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.TreeSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.resetButtonEnables();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (nodeInfo1.this --> TMLPanel.resetButtonEnables()V)
 * @param value cbit.xml.merge.NodeInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(NodeInfo value) {
	try {
		// user code begin {1}
		// user code end
		this.resetButtonEnables();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (TMLPanel.initialize() --> TMLPanel.resetButtonEnables()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6() {
	try {
		// user code begin {1}
		// user code end
		this.resetButtonEnables();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (disassociateButton.action.actionPerformed(java.awt.event.ActionEvent) --> TMLPanel.disassociate(Ljava.lang.Object;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.disassociate(getTree().getLastSelectedPathComponent());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (associate.action.actionPerformed(java.awt.event.ActionEvent) --> TMLPanel.associate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.associate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (Tree.treeSelection.valueChanged(javax.swing.event.TreeSelectionEvent) --> BaselineLabelValue.valueText)
 * @param arg1 javax.swing.event.TreeSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(javax.swing.event.TreeSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getBaselineEditorPane().setText(this.getCurrentBaselineText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (Tree.treeSelection.valueChanged(javax.swing.event.TreeSelectionEvent) --> ModifiedLabelValue.valueText)
 * @param arg1 javax.swing.event.TreeSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(javax.swing.event.TreeSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getModifiedEditorPane().setText(this.getCurrentModifiedText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (TMLPanel.nodeInfo <--> nodeInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setnodeInfo1(this.getNodeInfo());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (TMLPanel.baselineVersionDescription <--> baselineVersionDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getbaselineVersionDescription1() != null)) {
				this.setBaselineVersionDescription(getbaselineVersionDescription1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (TMLPanel.baselineVersionDescription <--> baselineVersionDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setbaselineVersionDescription1(this.getBaselineVersionDescription());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (TMLPanel.modifiedVersionDescription <--> modifiedVersionDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getmodifiedVersionDescription1() != null)) {
				this.setModifiedVersionDescription(getmodifiedVersionDescription1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (TMLPanel.modifiedVersionDescription <--> modifiedVersionDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setmodifiedVersionDescription1(this.getModifiedVersionDescription());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetSource:  (baselineVersionDescription1.this <--> BaselineVersionInfoLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setbaselineVersionDescription1(getBaselineVersionInfoLabel().getText());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (baselineVersionDescription1.this <--> BaselineVersionInfoLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getbaselineVersionDescription1() != null)) {
				getBaselineVersionInfoLabel().setText(getbaselineVersionDescription1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetSource:  (modifiedVersionDescription1.this <--> ModifiedVersionInfoLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setmodifiedVersionDescription1(getModifiedVersionInfoLabel().getText());
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (modifiedVersionDescription1.this <--> ModifiedVersionInfoLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getmodifiedVersionDescription1() != null)) {
				getModifiedVersionInfoLabel().setText(getmodifiedVersionDescription1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Allows a node to split into two, thus the 'changed' node type can have their different attributes in two different elements. 
 */
private void disassociate(Object lastSelected) {
	if  (lastSelected instanceof NodeInfo) {
		NodeInfo selected= (NodeInfo)lastSelected;

		//for now, cannot split the root element.
		if (selected.isRoot()) {
			displayMessage(this, "Cannot split the root element.");
			return;
		}
		//for now, only split elements, not attributes.
		if (selected.isAttribute()) {
			displayMessage(this, "Can only split whole elements, not individual attributes.");
			return;
		}
		//create the two new nodes
		NodeInfo baseLineNode = extract(selected, NodeInfo.STATUS_REMOVED);
		NodeInfo modifiednode = extract(selected, NodeInfo.STATUS_NEW);
		//erase the original one
		NodeInfo parent = (NodeInfo)selected.getParent();
		((DefaultTreeModel)(getTree().getModel())).removeNodeFromParent(selected);
		//add the two new nodes
		parent.add(baseLineNode);
		parent.add(modifiednode);
		((DefaultTreeModel)(getTree().getModel())).reload(parent);
		
	}
}


	private void displayMessage(Component component, String message) {
 
		DialogUtils.showErrorDialog(this,message);
	}


/**
 * This method extracts from the given NodeInfo node, the nodes with the given status.
 * Creation date: (9/12/2001 4:40:12 PM)
 * @return cbit.xml.merge.NodeInfo
 * @param originalNode cbit.xml.merge.NodeInfo
 * @param status int
 */
private NodeInfo extract(NodeInfo original, int status) {
	 
    NodeInfo newNode = null;
    XmlComparePolicy xmlComparePolicy = fieldDiffTree.getXmlComparePolicy();
     if ( xmlComparePolicy.ignoreElement(original.getName()) || xmlComparePolicy.ignoreAttribute(null,original.getName())){
		 if (status == NodeInfo.STATUS_REMOVED) {                     //i.e. represents the baseline
			 status = NodeInfo.STATUS_NORMAL;
        	 newNode = new NodeInfo(original.getName(), original.getValue(), status, original.isAttribute());
		 } else if (status == NodeInfo.STATUS_NEW) {                  //i.e. represents the modified
			newNode = null;                                                //ignore version info for the modified
		 }
	 } else {                                                              //if not version type node, or if allowing version info
	    //process himself
	    if (original.getStatus() == NodeInfo.STATUS_CHANGED) {
	        //create the copy
	        if (status == NodeInfo.STATUS_REMOVED) {
	            newNode =
	                new NodeInfo(
	                    original.getName(),
	                    original.getValue(),
	                    status,
	                    original.isAttribute());
	        } else {
	            if (status == NodeInfo.STATUS_NEW) {
	                newNode =
	                    new NodeInfo(
	                        original.getName(),
	                        ((ChangedNodeInfo)original).getModified(),
	                        status,
	                        original.isAttribute());
	            } else {
	                throw new IllegalArgumentException("Invalid option: " + status);
	            }
	        }
	    } else {
	        if (original.getStatus() == status                          
	            || original.getStatus() == NodeInfo.STATUS_NORMAL
	            || original.getStatus() == NodeInfo.STATUS_PROBLEM) {
	            newNode =
	                new NodeInfo(
	                    original.getName(),
	                    original.getValue(),
	                    status,
	                    original.isAttribute());
	        }
	    }
	 }
    //Now process its children
    if (newNode != null) {
        Enumeration enum1 = original.children();

        //copy children
        while (enum1.hasMoreElements()) {
            NodeInfo temp = extract((NodeInfo) enum1.nextElement(), status);
            if (temp != null) {
                newNode.add(temp);
            }
        }
    }
   
        return newNode;
}


/**
 * Return the associate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getassociate() {
	if (ivjassociate == null) {
		try {
			ivjassociate = new javax.swing.JButton();
			ivjassociate.setName("associate");
			ivjassociate.setText("Associate");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjassociate;
}


/**
 * Return the BaselineEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getBaselineEditorPane() {
	if (ivjBaselineEditorPane == null) {
		try {
			ivjBaselineEditorPane = new javax.swing.JEditorPane();
			ivjBaselineEditorPane.setName("BaselineEditorPane");
			ivjBaselineEditorPane.setPreferredSize(new java.awt.Dimension(100, 50));
			ivjBaselineEditorPane.setMinimumSize(new java.awt.Dimension(100, 50));
			ivjBaselineEditorPane.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBaselineEditorPane;
}

/**
 * Return the BaselineLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBaselineLabel() {
	if (ivjBaselineLabel == null) {
		try {
			ivjBaselineLabel = new javax.swing.JLabel();
			ivjBaselineLabel.setName("BaselineLabel");
			ivjBaselineLabel.setText("Baseline:");
			ivjBaselineLabel.setForeground(new java.awt.Color(102,102,153));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBaselineLabel;
}

/**
 * Gets the baselineVersionDescription property (java.lang.String) value.
 * @return The baselineVersionDescription property value.
 * @see #setBaselineVersionDescription
 */
public java.lang.String getBaselineVersionDescription() {
	return fieldBaselineVersionDescription;
}


/**
 * Return the baselineVersionDescription1 property value.
 * @return java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.String getbaselineVersionDescription1() {
	// user code begin {1}
	// user code end
	return ivjbaselineVersionDescription1;
}


/**
 * Return the BaselineVersionInfoLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBaselineVersionInfoLabel() {
	if (ivjBaselineVersionInfoLabel == null) {
		try {
			ivjBaselineVersionInfoLabel = new javax.swing.JLabel();
			ivjBaselineVersionInfoLabel.setName("BaselineVersionInfoLabel");
			ivjBaselineVersionInfoLabel.setText("");
			ivjBaselineVersionInfoLabel.setMaximumSize(new java.awt.Dimension(100, 14));
			ivjBaselineVersionInfoLabel.setForeground(new java.awt.Color(102,102,153));
			ivjBaselineVersionInfoLabel.setPreferredSize(new java.awt.Dimension(100, 14));
			ivjBaselineVersionInfoLabel.setMinimumSize(new java.awt.Dimension(100, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBaselineVersionInfoLabel;
}

private String getCurrentBaselineText() {
	Object object = getTree().getLastSelectedPathComponent();
	String baselineText = "";
	if (object instanceof NodeInfo) {
		NodeInfo node = (NodeInfo) object;
		if (node != null && node.getStatus() != NodeInfo.STATUS_NEW) {
			baselineText = node.getValue() != null ? node.getValue() : "";
		}
	}
	return (baselineText);
}


private String getCurrentModifiedText() {
	Object object = getTree().getLastSelectedPathComponent();
	String modifiedText = "";

	if (object instanceof NodeInfo) {
		NodeInfo node = (NodeInfo) object;
		if (node.getStatus() == NodeInfo.STATUS_CHANGED ) {
			modifiedText = ((ChangedNodeInfo)node).getModified() != null ? ((ChangedNodeInfo)node).getModified() : "";
		} else {
			if (node.getStatus() == NodeInfo.STATUS_NEW) {
				modifiedText = node.getValue() != null ? node.getValue() : "";
			}
		}
	}
	return modifiedText;
}


/**
 * Return the disassociateButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getdisassociateButton() {
	if (ivjdisassociateButton == null) {
		try {
			ivjdisassociateButton = new javax.swing.JButton();
			ivjdisassociateButton.setName("disassociateButton");
			ivjdisassociateButton.setText("Disassociate");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjdisassociateButton;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintskeepBaseButton = new java.awt.GridBagConstraints();
			constraintskeepBaseButton.gridx = 0; constraintskeepBaseButton.gridy = 0;
			constraintskeepBaseButton.insets = new java.awt.Insets(5, 0, 2, 0);
			getJPanel1().add(getkeepBaseButton(), constraintskeepBaseButton);

			java.awt.GridBagConstraints constraintskeepModButton = new java.awt.GridBagConstraints();
			constraintskeepModButton.gridx = 1; constraintskeepModButton.gridy = 0;
			constraintskeepModButton.insets = new java.awt.Insets(3, 0, 0, 0);
			getJPanel1().add(getkeepModButton(), constraintskeepModButton);

			java.awt.GridBagConstraints constraintsdisassociateButton = new java.awt.GridBagConstraints();
			constraintsdisassociateButton.gridx = 3; constraintsdisassociateButton.gridy = 0;
			constraintsdisassociateButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getdisassociateButton(), constraintsdisassociateButton);

			java.awt.GridBagConstraints constraintsassociate = new java.awt.GridBagConstraints();
			constraintsassociate.gridx = 2; constraintsassociate.gridy = 0;
			constraintsassociate.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getassociate(), constraintsassociate);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsBaselineLabel = new java.awt.GridBagConstraints();
			constraintsBaselineLabel.gridx = 0; constraintsBaselineLabel.gridy = 0;
			constraintsBaselineLabel.insets = new java.awt.Insets(5, 7, 4, 5);
			getJPanel2().add(getBaselineLabel(), constraintsBaselineLabel);

			java.awt.GridBagConstraints constraintsModifiedLabel = new java.awt.GridBagConstraints();
			constraintsModifiedLabel.gridx = 0; constraintsModifiedLabel.gridy = 3;
			constraintsModifiedLabel.ipadx = 1;
			constraintsModifiedLabel.insets = new java.awt.Insets(5, 7, 5, 5);
			getJPanel2().add(getModifiedLabel(), constraintsModifiedLabel);

			java.awt.GridBagConstraints constraintsModifiedEditorPane = new java.awt.GridBagConstraints();
			constraintsModifiedEditorPane.gridx = 0; constraintsModifiedEditorPane.gridy = 4;
			constraintsModifiedEditorPane.gridwidth = 2;
			constraintsModifiedEditorPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsModifiedEditorPane.weightx = 1.0;
			constraintsModifiedEditorPane.weighty = 1.0;
			constraintsModifiedEditorPane.ipadx = 423;
			constraintsModifiedEditorPane.insets = new java.awt.Insets(5, 5, 8, 1);
			getJPanel2().add(getModifiedEditorPane(), constraintsModifiedEditorPane);

			java.awt.GridBagConstraints constraintsBaselineEditorPane = new java.awt.GridBagConstraints();
			constraintsBaselineEditorPane.gridx = 0; constraintsBaselineEditorPane.gridy = 1;
			constraintsBaselineEditorPane.gridwidth = 2;
			constraintsBaselineEditorPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsBaselineEditorPane.weightx = 1.0;
			constraintsBaselineEditorPane.weighty = 1.0;
			constraintsBaselineEditorPane.ipadx = 425;
			constraintsBaselineEditorPane.insets = new java.awt.Insets(5, 4, 4, 0);
			getJPanel2().add(getBaselineEditorPane(), constraintsBaselineEditorPane);

			java.awt.GridBagConstraints constraintsBaselineVersionInfoLabel = new java.awt.GridBagConstraints();
			constraintsBaselineVersionInfoLabel.gridx = 1; constraintsBaselineVersionInfoLabel.gridy = 0;
			constraintsBaselineVersionInfoLabel.ipadx = 357;
			constraintsBaselineVersionInfoLabel.insets = new java.awt.Insets(5, 7, 4, 1);
			getJPanel2().add(getBaselineVersionInfoLabel(), constraintsBaselineVersionInfoLabel);

			java.awt.GridBagConstraints constraintsModifiedVersionInfoLabel = new java.awt.GridBagConstraints();
			constraintsModifiedVersionInfoLabel.gridx = 1; constraintsModifiedVersionInfoLabel.gridy = 3;
			constraintsModifiedVersionInfoLabel.ipadx = 359;
			constraintsModifiedVersionInfoLabel.insets = new java.awt.Insets(5, 6, 5, 0);
			getJPanel2().add(getModifiedVersionInfoLabel(), constraintsModifiedVersionInfoLabel);

			java.awt.GridBagConstraints constraintsJSeparator1 = new java.awt.GridBagConstraints();
			constraintsJSeparator1.gridx = 0; constraintsJSeparator1.gridy = 2;
			constraintsJSeparator1.gridwidth = 2;
			constraintsJSeparator1.ipadx = 520;
			constraintsJSeparator1.insets = new java.awt.Insets(4, 6, 4, 2);
			getJPanel2().add(getJSeparator1(), constraintsJSeparator1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getTree());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}


/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getkeepBaseButton() {
	if (ivjkeepBaseButton == null) {
		try {
			ivjkeepBaseButton = new javax.swing.JButton();
			ivjkeepBaseButton.setName("keepBaseButton");
			ivjkeepBaseButton.setText("Keep Removed");
			ivjkeepBaseButton.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjkeepBaseButton;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getkeepModButton() {
	if (ivjkeepModButton == null) {
		try {
			ivjkeepModButton = new javax.swing.JButton();
			ivjkeepModButton.setName("keepModButton");
			ivjkeepModButton.setText("Keep New");
			ivjkeepModButton.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjkeepModButton;
}

/**
 * Return the ModifiedEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getModifiedEditorPane() {
	if (ivjModifiedEditorPane == null) {
		try {
			ivjModifiedEditorPane = new javax.swing.JEditorPane();
			ivjModifiedEditorPane.setName("ModifiedEditorPane");
			ivjModifiedEditorPane.setPreferredSize(new java.awt.Dimension(100, 50));
			ivjModifiedEditorPane.setMinimumSize(new java.awt.Dimension(100, 50));
			ivjModifiedEditorPane.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModifiedEditorPane;
}

/**
 * Return the ModifiedLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getModifiedLabel() {
	if (ivjModifiedLabel == null) {
		try {
			ivjModifiedLabel = new javax.swing.JLabel();
			ivjModifiedLabel.setName("ModifiedLabel");
			ivjModifiedLabel.setText("Modified:");
			ivjModifiedLabel.setForeground(new java.awt.Color(102,102,153));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModifiedLabel;
}

/**
 * Gets the modifiedVersionDescription property (java.lang.String) value.
 * @return The modifiedVersionDescription property value.
 * @see #setModifiedVersionDescription
 */
public java.lang.String getModifiedVersionDescription() {
	return fieldModifiedVersionDescription;
}


/**
 * Return the modifiedVersionDescription1 property value.
 * @return java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.String getmodifiedVersionDescription1() {
	// user code begin {1}
	// user code end
	return ivjmodifiedVersionDescription1;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getModifiedVersionInfoLabel() {
	if (ivjModifiedVersionInfoLabel == null) {
		try {
			ivjModifiedVersionInfoLabel = new javax.swing.JLabel();
			ivjModifiedVersionInfoLabel.setName("ModifiedVersionInfoLabel");
			ivjModifiedVersionInfoLabel.setText("");
			ivjModifiedVersionInfoLabel.setMaximumSize(new java.awt.Dimension(100, 14));
			ivjModifiedVersionInfoLabel.setForeground(new java.awt.Color(102,102,153));
			ivjModifiedVersionInfoLabel.setPreferredSize(new java.awt.Dimension(100, 14));
			ivjModifiedVersionInfoLabel.setMinimumSize(new java.awt.Dimension(100, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModifiedVersionInfoLabel;
}

/**
 * Gets the nodeInfo property (cbit.xml.merge.NodeInfo) value.
 * @return The nodeInfo property value.
 * @see #setNodeInfo
 */
public NodeInfo getNodeInfo() {
	return fieldNodeInfo;
}


/**
 * Return the nodeInfo1 property value.
 * @return cbit.xml.merge.NodeInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private NodeInfo getnodeInfo1() {
	// user code begin {1}
	// user code end
	return ivjnodeInfo1;
}


/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getTree() {
	if (ivjTree == null) {
		try {
			ivjTree = new javax.swing.JTree();
			ivjTree.setName("Tree");
			ivjTree.setShowsRootHandles(true);
			ivjTree.setModel(null);
			ivjTree.setCellRenderer(new cbit.vcell.xml.merge.gui.MyRenderer());
			ivjTree.setBounds(0, 0, 78, 72);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTree;
}

	public XmlTreeDiff getXmlTreeDiff() {

		return fieldDiffTree;
	}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getTree().addTreeSelectionListener(ivjEventHandler);
	getkeepBaseButton().addActionListener(ivjEventHandler);
	getkeepModButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getdisassociateButton().addActionListener(ivjEventHandler);
	getassociate().addActionListener(ivjEventHandler);
	getBaselineVersionInfoLabel().addPropertyChangeListener(ivjEventHandler);
	getModifiedVersionInfoLabel().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TMLPanel");
		setPreferredSize(new java.awt.Dimension(500, 600));
		setLayout(new java.awt.GridBagLayout());
		setSize(529, 608);
		setMinimumSize(new java.awt.Dimension(500, 600));

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.ipadx = 435;
		constraintsJScrollPane1.ipady = 544;
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
constraintsJPanel2.gridheight = 2;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		add(getJPanel2(), constraintsJPanel2);
		initConnections();
		connEtoC6();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	javax.swing.ToolTipManager.sharedInstance().registerComponent(getTree());
	// user code end
}

	private boolean isNormal(NodeInfo node) {

		if (node.getStatus() != NodeInfo.STATUS_NORMAL) {
			return false;
		} 
        for (Enumeration enumNodes = node.children();enumNodes.hasMoreElements();) {
        	boolean flag = isNormal((NodeInfo)enumNodes.nextElement());
        	if (!flag) {
				return false;
        	}
        }

        return true;
	}


/**
 * This method processes the subnodes of the given Node so that it keeps the baseline remaining nodes.
 * Creation date: (9/11/2001 4:55:11 PM)
 * @param object java.lang.Object
 */
private void keepBaseLine(Object object) {

    if (object !=null & object instanceof NodeInfo) {
        NodeInfo currentNode = (NodeInfo) object;
 
        if (currentNode.getStatus() != NodeInfo.STATUS_NEW) {
            //process himself 
            currentNode.setStatus(NodeInfo.STATUS_NORMAL);
            ((DefaultTreeModel) (getTree().getModel())).nodeChanged(
                currentNode);
            //process its subnodes
            LinkedList subNodes = new LinkedList();
            for (Enumeration enumNodes = currentNode.children(); enumNodes.hasMoreElements();) {
                subNodes.add(enumNodes.nextElement());
            }
            Iterator iterator = subNodes.iterator();
            while (iterator.hasNext()) {
                NodeInfo tempNode = (NodeInfo) iterator.next();
                keepBaseLine(tempNode);
            }
            //update parents of the change
            updateParents(currentNode.getParent(), currentNode);
        } else {
            NodeInfo parentNode = currentNode;

            //track the farest parent who has as a status of NEW
            while ((parentNode.getParent() != null)
                && (((NodeInfo) parentNode.getParent()).getStatus() == NodeInfo.STATUS_NEW)) {
                parentNode = (NodeInfo) parentNode.getParent();
            }
            //update parents of the change
            updateParents(parentNode, currentNode);
            
            //eliminate that node and its subtree (remove the node of interest, not the parent one).
            ( (DefaultTreeModel)(getTree().getModel())).removeNodeFromParent(currentNode);
        }
    } else {
        throw new IllegalArgumentException(
            "Keep Baseline found an unknown type of node:" + object.getClass().getName());
    }
}


/**
 * This method processes the subnodes of the given Node so that it keeps the Modified remaining nodes.
 * Creation date: (9/11/2001 4:55:11 PM)
 * @param object java.lang.Object
 */
private void keepModified(Object object) {
    if (object instanceof NodeInfo) {
        NodeInfo currentNode = (NodeInfo) object;

        if (currentNode.getStatus() != NodeInfo.STATUS_REMOVED) {
            //process himself
            currentNode.setStatus(NodeInfo.STATUS_NORMAL);
            if (currentNode instanceof ChangedNodeInfo){
	            String temp = ((ChangedNodeInfo)currentNode).getValue();
	            ((ChangedNodeInfo)currentNode).setValue( ((ChangedNodeInfo)currentNode).getModified() );
	            ((ChangedNodeInfo)currentNode).setModified(temp);
            }
            ((javax.swing.tree.DefaultTreeModel) (getTree().getModel())).nodeChanged(currentNode);
            //process its subnodes
            LinkedList subNodes = new LinkedList();
            for (Enumeration enumNodes = currentNode.children();enumNodes.hasMoreElements();) {
                subNodes.add(enumNodes.nextElement());
            }
            Iterator iterator = subNodes.iterator();
            while (iterator.hasNext()) {
                NodeInfo tempNode = (NodeInfo) iterator.next();
                keepModified(tempNode);
            }
            //update parents of the change 
            updateParents(currentNode.getParent(), currentNode);
        } else {
            NodeInfo parentNode = currentNode;

            //track the farest parent who has as a status of REMOVED
            while ((parentNode.getParent() != null)
                && (((NodeInfo) parentNode.getParent()).getStatus() == NodeInfo.STATUS_REMOVED)) {
                parentNode = (NodeInfo) parentNode.getParent();
            }
            //update parents of the change
            updateParents(parentNode, currentNode);
            
            //eliminate that node and its subtree (remove the node of interest, not the parent one).
            ((DefaultTreeModel)(getTree().getModel())).removeNodeFromParent(currentNode);
        }
    } else {
        throw new IllegalArgumentException(
            "KeepModified found an unknown type of node:" + object.getClass().getName());
    }
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		if (args.length < 2 || args.length > 3) {
			System.err.println("Usage: TMLPanel baselineFile modifiedFile ignoreVersion [true | false]");
			System.exit(0);
		}
		boolean ignoreVersion = true;
		if (args.length == 3) {
			ignoreVersion = new Boolean(args[2]).booleanValue();
		}
		String baselineXML = XmlUtil.getXMLString(args[0]);
		String modifiedXML = XmlUtil.getXMLString(args[1]);
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TMLPanel aTMLPanel = new TMLPanel();
		XmlTreeDiff diffTree = XmlTreeDiff.compareMerge(baselineXML, modifiedXML, XmlTreeDiff.COMPARE_DOCS_SAVED, new DefaultXmlComparePolicy());
		aTMLPanel.setXmlTreeDiff(diffTree);
		frame.setContentPane(aTMLPanel);
		frame.setSize(aTMLPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of TMLPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/17/2000 10:46:20 AM)
 * @return org.jdom.Element
 * @param node cbit.xml.merge.NodeInfo
 */
private Element nodeInfo2Element(NodeInfo node) {
	Element rootElement = new Element(node.getName());
	rootElement.setText(node.getValue());
	//
	for (java.util.Enumeration mychilds = node.children(); mychilds.hasMoreElements();) {
		NodeInfo tempnode = (NodeInfo) (mychilds.nextElement());
		if ( tempnode.isElement() ) {
			rootElement.addContent( nodeInfo2Element(tempnode) );
		} else if ( tempnode.isAttribute() ) {
			rootElement.setAttribute(tempnode.getName(), tempnode.getValue());
		}
	}
	//
	return rootElement;
}


//process events for loading the model displayed in the comparison panel
public String getComparisonResultAsXml() throws Exception {
	NodeInfo root = (NodeInfo)getTree().getModel().getRoot();
	String xmlStr = root.toXmlString();
	return xmlStr;
}

/**
 * Comment
 */
private void resetButtonEnables() {
	if (getNodeInfo()==null || getTree().getSelectionCount()==0 ){
		getkeepBaseButton().setEnabled(false);
		getkeepModButton().setEnabled(false);
		getdisassociateButton().setEnabled(false);
		getassociate().setEnabled(false);
		return;
	}

	Object object = getTree().getLastSelectedPathComponent();
	if (object instanceof NodeInfo && getTree().getSelectionCount()<3) {
		if ( ((NodeInfo)object).getStatus() != NodeInfo.STATUS_NORMAL) {
				getkeepBaseButton().setEnabled(true);
				getkeepModButton().setEnabled(true);
		}else{
			getkeepBaseButton().setEnabled(false);
			getkeepModButton().setEnabled(false);
		}
		//process dissasociate button
		if ((((NodeInfo)object).getStatus() == NodeInfo.STATUS_PROBLEM || ((NodeInfo)object).getStatus() == NodeInfo.STATUS_CHANGED) && ((NodeInfo)object).isElement()) {
			getdisassociateButton().setEnabled(true);				
		}else {
			getdisassociateButton().setEnabled(false);
		}
		//Process associate button
		if (getTree().getSelectionCount()==2) {
			getkeepBaseButton().setEnabled(false);
			getkeepModButton().setEnabled(false);
			getdisassociateButton().setEnabled(false);
			getassociate().setEnabled(true);		
		} else {
			getassociate().setEnabled(false);
		}
	} else {
		getassociate().setEnabled(false);
		getdisassociateButton().setEnabled(false);
	}	
}


/**
 * Comment
 */
private void resetTextFieldAttributes() {
	if (getCurrentBaselineText() == null || getCurrentBaselineText().length()==0){
		getBaselineEditorPane().setEnabled(false);
		getBaselineEditorPane().setBackground(getBackground());
	}else{
		getBaselineEditorPane().setEnabled(true);
		getBaselineEditorPane().setBackground(java.awt.Color.white);
	}
	if (getCurrentModifiedText() == null || getCurrentModifiedText().length()==0){
		getModifiedEditorPane().setEnabled(false);
		getModifiedEditorPane().setBackground(getBackground());
	}else{
		getModifiedEditorPane().setEnabled(true);
		getModifiedEditorPane().setBackground(java.awt.Color.white);
	}
}


/**
 * Sets the baselineVersionDescription property (java.lang.String) value.
 * @param baselineVersionDescription The new value for the property.
 * @see #getBaselineVersionDescription
 */
public void setBaselineVersionDescription(java.lang.String baselineVersionDescription) {
	String oldValue = fieldBaselineVersionDescription;
	fieldBaselineVersionDescription = baselineVersionDescription;
	firePropertyChange("baselineVersionDescription", oldValue, baselineVersionDescription);
}


/**
 * Set the baselineVersionDescription1 to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setbaselineVersionDescription1(java.lang.String newValue) {
	if (ivjbaselineVersionDescription1 != newValue) {
		try {
			String oldValue = getbaselineVersionDescription1();
			ivjbaselineVersionDescription1 = newValue;
			connPtoP2SetSource();
			connPtoP4SetTarget();
			firePropertyChange("modifiedVersionDescription", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Perform the setLineStyle method.
 * @param linestyle java.lang.String
 */
public void setLineStyle(String linestyle) {
	/* Perform the setLineStyle method. */
	getTree().putClientProperty("JTree.lineStyle", linestyle);
	getTree().updateUI();//repaint();
	return;
}


/**
 * Sets the modifiedVersionDescription property (java.lang.String) value.
 * @param modifiedVersionDescription The new value for the property.
 * @see #getModifiedVersionDescription
 */
public void setModifiedVersionDescription(java.lang.String modifiedVersionDescription) {
	String oldValue = fieldModifiedVersionDescription;
	fieldModifiedVersionDescription = modifiedVersionDescription;
	firePropertyChange("modifiedVersionDescription", oldValue, modifiedVersionDescription);
}


/**
 * Set the modifiedVersionDescription1 to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodifiedVersionDescription1(java.lang.String newValue) {
	if (ivjmodifiedVersionDescription1 != newValue) {
		try {
			String oldValue = getmodifiedVersionDescription1();
			ivjmodifiedVersionDescription1 = newValue;
			connPtoP3SetSource();
			connPtoP5SetTarget();
			firePropertyChange("modifiedVersionDescription", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Sets the nodeInfo property (cbit.xml.merge.NodeInfo) value.
 * @param nodeInfo The new value for the property.
 * @see #getNodeInfo
 */
private void setNodeInfo(NodeInfo nodeInfo) {

	NodeInfo oldValue = fieldNodeInfo;
	fieldNodeInfo = nodeInfo;
	getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	
	if (nodeInfo!=null){
		getTree().setModel(new DefaultTreeModel(getNodeInfo(), false));
	}else{
		getTree().setModel(null);
	}
	firePropertyChange("nodeInfo", oldValue, nodeInfo);
}


/**
 * Set the nodeInfo1 to a new value.
 * @param newValue cbit.xml.merge.NodeInfo
 *
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setnodeInfo1(NodeInfo newValue) {
	if (ivjnodeInfo1 != newValue) {
		try {
			cbit.vcell.xml.merge.NodeInfo oldValue = getnodeInfo1();
			ivjnodeInfo1 = newValue;
			connEtoC5(ivjnodeInfo1);
			firePropertyChange("nodeInfo", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

	public void setXmlTreeDiff(XmlTreeDiff diffTree) {

		if (diffTree == null) {
			throw new IllegalArgumentException("Invalid XmlTreeDiff object: " + diffTree);
		}
		fieldDiffTree = diffTree;
		setNodeInfo(fieldDiffTree.getMergedRootNode());
	}


	public boolean tagsResolved() {

		NodeInfo root = (NodeInfo)getTree().getModel().getRoot();
		
		return isNormal(root);	
	}


//parentNode is not necessarily the direct parent.
	private void updateParents(javax.swing.tree.TreeNode parentnode, javax.swing.tree.TreeNode node) {
 
		if (parentnode == null) {                            //nothing to update.
			return;
		}
     	boolean ok = true;
 
        //Check the children, unless there is no need to.
        if (parentnode != node) {
	        for (java.util.Enumeration mychilds = parentnode.children(); mychilds.hasMoreElements();) {
	        	NodeInfo childNode = (NodeInfo)mychilds.nextElement();
	        	if (childNode == node) {                   //same reference; the status of this one has already been set
					continue;
	        	}
	            if (childNode.getStatus()!= NodeInfo.STATUS_NORMAL) {
	                ok = false;
	                break;
	            }
	        }
        }
        //If evrything is OK, then change the status to Normal
        if (ok) {
            ((NodeInfo) parentnode).setStatus(NodeInfo.STATUS_NORMAL);
            ((javax.swing.tree.DefaultTreeModel) (getTree().getModel())).nodeChanged(parentnode);
            updateParents(parentnode.getParent(), parentnode);
        }
	}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G4A0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8DF8D45535A8A2C834399AB1EAD0D040D022860A85B5A016A0BF86CCB5AADA3C4D67455B74DE6C25ADED73FCE8695713C9A241C40992AC3628F90A1AD8448422D2241AC02CC1820CB00CC1E212B3F3CEE6A6194C8CB3A7A1815437764F5A674C4C19CC12DB6F7BDEBE56F74EDEFB6D3DFE765AEB7F1C3D87296255494585168AC9AAA816785F8785121451A5C93B3F3D75E244A5796C2512616F7FG
	BCAFD5E9C5701C8CF901157652F2A95FDE8269B9101E3342DE7AA7F85FACBDE94FD761A5861FEC481B7670775D0373F964A07259C0530F1DAD0367F3G9900A34FA2BF617F64592D9C1F4B71888596C94A21B4956736F1DC9324BBG9281A67B99FD86BCB7D24EE3776E61F42D1727C9794B0316E7C9BD06BA913686D859FD7CD9A9BDD51097A03C7EFE2E0C1751A01D8EC014AF145CE7DFA67456EE57C7CE07C75B595F635D592386DD6A5E3D36434DD65927FAA13F63B429E36C3992DAC6122AA03D06626E14C9
	3D2970BE9FE00540FF2D90FCCD50DE5A845091C4DD7E7E56BEAEEB4B395B25BC57120D1BE268529FE4BACC8E6A3A0CF95BD17892357BCB0708FDF3A1CF8A59CBABG76G34G74837C0536F765E7EF433341219D70777478FD8D0140FE2F92BC6CD7D4AF3C6F5D8BB99CF7CCF57A030AA431B6BDF0EE0709BC0381AB7779304BB11B246D4676CDBFB6D34A1DFD15E50309CDF2534ED912C24C96E9A15DA60C77A5B1F9E705427DAF1C779712113774782E8D45A63C37BD3CEBC3AE2FE72D01778E0D34F52D0334F5
	B93C9770FA997EA30A8F98704C9797D11FA169C448DB2B21EF34BC0732B497CD154AEB2FB635C34ADD0996DBAF13415B9BA94BA415541D524BF8D68BD966D1FC3E814FE439107AA9C9DB7B6C25934F58CB19FF3DF618744D557D76522281FB69B600BAG9BC0GE04CG7A186733F70768E3D6B9286D6F75F895B5A831322F9DFA911EEA3F96148F7AC21A6CF32AF6B968119DDEB5A4F11A3F9EA2B6F004152B213E6F849E1F2ABEB5A8EB9E3F8FEA17FBD4CD8D06984D5F8EBDCFE802AA4D35F9FAD4E050931030
	5E06C31B097D6410F6A220481A2AD3307C0307C81B7449F62891AA00F7E61723C7E8FF877D6783E48BBBAC26759DD20390B0766EED767B3BFB83B62209346DAC3173949ABBD6817D3A8134712123845781797DE7311E5F9FFA41D0CF2347C9441703835847BEF014933F817A49G99G2B4E227F7C2A857D27715999520E072EB4751FA4706512F35CEF4EC57623C6DABF113189725CG8967B056AF13DB06582F09ECE678C4AEAF0D6F91B40C473F8F19C7F18CFCA16FDBD827B10F59FD2E1534E19A683598A06F
	9C5A7E7D66CA664B7B65100A5EFBB4880E27AAFACBB0B9EFD02D5C36451C9F99AC4938C462B999ABDF8758427B9699FF5F617C89AFB2663C8A709E4087GEFG3CA5EC10C96BDE8FF2EDBFEFAFED8650GA682AC86D883D071353DF49F00821088B087A09720689BFB698EG9B40F900D400EC007CEF308DAFD66A8BF41FC09BC64A0F765A42E50F149B75C3392BB97DBB9176C67D06B3A67C7FG4C163954160591365C90E1CFAC33195BB2520E1BE8DD3A9D37C83A2F9AF9223D7FDF6B7E4F865677F69CA6F6D0
	7BD41F562886D41F82AFA11E377BB031A79FA7BA7D224C5FA8BEG634A818864A1017F78B031272CE9C10F23D7D30F2B1A667139747CCFA92FD02FA3208FC9486B48E1520E7EB3B02E887A6307C9CCF67AFDBE158681D104671F20E3026AD5FBC068837EDE1F4670270E1039F237BA40472476A3CFCBE4CC717612710D4DBFE74944CF82C1CF8F0496B2C4CA5EEF49F811F1EE2B41C65BGC76651757034FD0B717E4C6B986F371F3DD14AEC8FD84C66AEA953AE334C7916455A2CEF75F84FE4329EB5175DCE47A6
	0E8FB1D96FA8A6B81C9697D23AEF9A08ACBE980BC95354CA2CBFEC1664BAD20F8382295763D379A85A28061CC1CF00583016531DB8C768FA7C0A275323AAE6F4ACE67F76F3A267EA10FFBDC045A80C598F9CA579B8DE72E0FD406F753226A2720EA2763C44D1416D3183520D2318BD1F93796FB8B1FFA22467C465BFA4729713BCC3BE13E301150C1BA175F42F8A5398B2709F926D3E547AC2C41E1D7BG4B3F57CAFC40EB18B3CC3E0070AC6A4475519A60D784386DG8100248B785A0052BD2653DD40680AC65B
	CB2D23F53A2D9752B9208B69821017G10B61A6931FAB4778FCA770D9B69148B5978F37EC2D6EF6205467A5E77A0DD459898F7G1A47307A9247986BFBCA504D9F43463195E3426BEB9ACB68FE5805F4F9E35858371E57D7BE56C8E76BC63AE4C813F1926952B9DD65C5046E89C117B716652F9F4B66B565107F0A50A3530BF45E0B18DCE3AEE274B99799759D5703F479976975C453FD68C33A32F13C5D46B13A40B8A31D450FF4A9BCFF56B8337A668B3AAC48DFB5CEEFB7A941E867C0G69EA9258DC20A9412C
	5D2ACEA3DD8B648792747A12920C764B95F4E989ACBFB3014D89D7A7B07BB13A6F8711EE9D27AB3F184D9B43693CA1245BF331DE0F955E83979B695E5710EE14211E94839D0BC73F2BA6B1122E5D3B64BE39FFE7688C44793DFBBF2371092D59DFBBCFE29609D19997635A3C0B2E4B56C23A6492FBE965A598CF6FFF965766495DE9D26E63359BE26DB36C390459297592BD1EA62DEDF81A452AFD47894F091017FE894629031F9B57878D21105F6921CB939E8356FDEE0C378C77074F1F01E727A798528E420058
	0F7122709811172CDDB81A2F65EE21FA11381A89FC578154FC0F60FF65A0639071FD2627F3B76A19F4F61A54F165B5165298FAEA5FE37AA5FF4F304E7CE6DDB163E7F3A23FEC48AFCAC4FE17BB89BF62AB0CEE3D205B87B41A207B1BC268082FB03AF7BA11AE9DE872G7A7F05607FA4D798E45A8638FAG85000DA1491D75C456208AC3BD0F7BBB69A8FB40AD7BDC2AD2E5700E1DF40A3153E6A47E04202A992FFA0711D7A66056907C25486B1ECEA233861391E64B992F54F35A6C19D32592F92C45ECBFA771F1
	17E57B254C064D176A362C3842D1D8854074FF46057AA783CD3660F95A754F536FF91719FEC517E19B7C0C4AC07A57D640D9AF43B678C33B31DC8260E7812C388CE57C4C3D8B1EB2930EBD2CEA30539F6C61726D144FE8DC388603D45817173FCB66425D2A9AA00B2FE618B3D4B553E2DED0F167D1C7972872E135DD42FD1B291B0DF65F77ED1B295D93B639AD2BAFE376AECF526D1EBE5ED118B99E6DBE5D0DF6EF851A64A45469B98FE9EBB9682AE5B27E600D9AF83AD42D993A9E332E2AD8E54DFD405F93707B
	C01D2A6AE821193C5A9E14B77D01DB25162ECBCD65257AF0B9738D72A6267C42B29900495B6E41B62A3F9C5A67F2EC23EBEDE6EDB46BF254E729673F57C6DFBA300D8E7379DA2CB6E2F23E5E05F6DD8CB22C97F2DC5205F6E5F4BF6DC2FD6A465BCB3D63F1EF3662A372241EAE218C97D95EE6EDF020B7988461D0929B7448B06B9F07593F4F75A17A4B6FDFC67BEFEFCC157664F74734FF6AF8E6776C7186FFF9764962CCGA66F575DA86F36ABE0BEFE854A5B77FC38EC7B796CB736EC2C3ED73DE827C428EB71
	95E827A2A79993F0EE4DF0B7F9E97CF1FBC2CC2F5FB6987BC14606C4D33D5C53D3AC6B2FE07AECCF5675CA1962A8CC1F027D60F1AF6A2581CDFAB24AE16D79E7449FE6233F6CA56B5B209AD2E16525E91069237BFDD5863F195503F264018C5B2E4458247890EF83DC6215A2A67B7EF9725D5A7E97140FB45BBFBC2AD7E94015A415124972D7BF4A329864D8AFE499CF719E1FC77348DE4FD9352A1A517FDC5057DD85BEFE957659C51AD91FCD3D8AFDCA8E12BE27F8C2B21FB2440C0B0C4F04G7AD286543176AA
	14CBBA9D5E67BEBF0D72D4DDEDAF6D389A65F95CE3A6CF4A55A8CF8BAD278BD3EDE775958745588D34F9D7A35F694EBFCB032D83F13D1894C43FE6755DA76A5B1682FDAC856BBB2A107AE22F97518ED706C41F02320BD3D03F22B623FE6BD35878D7B9816B2F54F0FC6731D643FA6A0046BB816BA9ECB52BA7F5826E333DF59E3FC7E4CC6071E4023E5F398A5E738DE93AAE257B740F52B9DC49B5EC1FFE03407F0262AB8DF8A6DF0062C9FA8F64CD3C8677163F32133931F4AD749F00BA00E6GAFC06235B8A77D
	5E315DD21C3D7B03BE8D262C3277F858E6896E219FA7F669D687F87A136346F84D6CF03B0358A16DDA737DE0B3E0651650F21943A84762E1751746EF05C54FFFE6A9B409078456723A2509FABCE436FB2E0534EDBD60DB81B400C4007409E8376547F744339BFE476086E3BAFD699C19AD5C4E615A021533A8A363D72E0C0C5F6E4EF85F7F32A71A6D79B744AD37B6229C5FBB33117DD413759F096EFC9FED1CEC5CC7AB19443E3F54413389002D910F4838C66C1804EB242F7FE1DC8B4E5F74F54CB5D24AA47EBD
	E3126EC775FE5B86A6E3ED594B66B2FE22F8B4FF102CD4A11F495A670A5C73A3322E03FA4B2EC3D94FB8D0566D57E1DF1FF5122C17DBA15D8F1084108E10833086207CFA745B97ED9F4673DBBA945A7CEC76F7A0F247937D43B532F6FEA5EE391A6B434BB11D1BDC28F3537598DF9F0F2AEBD4C4D9F3997EF7D4391D916909571B795A2663AF1937A31B4508B6FC5DE95606F3204E2C6B318D6BBACA38BC2B2E47B6FC6B94E9432D1341B7811A815CG09GE9G5913318DDD363F8F318D8FG2E496F6F0EEEC534
	458C77485A710E386556CC8EAF47F41C5ECAF4AC1A8263AD409EG9BC0BFC0B2C0469454F12E6D63F8BA0A4F3D71FCD5750C4CCFC2D165A27DE46594B3DFFD4006ED3BF68A7AEAF557486C7CC15CF2D5D34D4ABD96D5EEE8BA7FBA2ADC244E75D34D5A752DB6522EAD102781A4822481E4832C86A83B815B356CC443B05AB53E7FDE58BDB2BDAF0FAA9729670E9B4C4A3D619DD9BB6E0EDB2E7186B33BDE57CE63BA647583A481248364G2C81A8CFC53BF61C589F4F2EE289837D85DFCD8C4B7DA0CA5E2159758F
	D165A26DDA15EAD66E361E1171DB9CD5AE4ADFCD79F579C6462FBF2ADCA43F96D3FE357E11717BB82ADCA43F00A93F7F9AA13FA76372CBC8B50BF9CFE9A46611F1F2F2AA463C6B83A36BABB36216DBE92A739727C726339AD5AED2673526BAD7F6214EA553D067526048F4FEA9EE397AE9E6659E9AA13F1F45AD57E24A6F5A5048785D9837DCA0029F7F3E5399637BCE336C406FBB9F3611F6A05FE992C4BB285A48648C45AD37E61A19AF6CEDC5990A3E8F6BEDFA5E6BA37A5D498D69B182772FF45E178269F401
	3B1D4E67F3A03DDA60D652FD3F326976526D53D127BC3A4E5F8369E6GC56057D2BC59232967FB798C5F67986C9C9529875F156961BA9921B9C69E63F15920BC8AA368E35145BA47454636CEFB6CF3A1BDF673F3A1A13B793910817B738C8FF598CFA3E27E8576414F0DDCECB7BBB772B25575E18BE907E02FCF73742814AD73E1351F6C5F4188FC2C574706E742B99439CFA061181F5BC1761F7A2763587E6A973B2468311DAD9E31921BAA870FF6F60A017DF8C0F5224CAF3644365F4BAD6676FB3D455CFE55AD
	035B2F36A5167D180D7ED3A3FB3FD167E80E400CCF42FD402E4E0D1C66934DFF38D6448257A7B15651471DC13F574B57512CBFAE10711CF672F47C666B68A06D138D69BC0075227F9C17C91F2FC83317D625693E3E0F3FDB53227D1F3553967A1DD0017CD1GA9G33GF252305D32DA626E2DFD52EF3A3746F418694271E5ED9A6A7086BD2BDBF5A374F500169BD107738E0CDB019B23659D83389400F4C39E13F15E49583E31702439EF5CFD124439103FB768D4A35B7F5E13DBC51EBE39BC2829BD2409994DCF
	E8F9CD8E3A54A87FF91416E7F9314A7F6749D8BE467C67D31751FFF609D8F5D82C7BB3CDEC3482F02B81569B72184FAEFBF1937579D0C88D5105F5232C491CD7255B406BC02D703FD3CEEC3B721B306D76371336EB02F487C07FCD58F6FF6BB09BF312EEE2F22442B36326F0F9F9DFBB953B6D761CB2EF3B0FCE45EE3BCFCE45EF3BA327E2375D09D3715B2E6D5460ED1767AE657634F6867DBD507BD939DBDAE36B1A51EA2E6B0FDAE36B3A24B53E2E77364656752156783ABE5ABA382E93BDC65F517D74A7B6B3
	FF4866BE308A1E79917E406A6B688A375DAE0E5F5A9D06BF00B62DED9B44FF5AE278CF5BA07E53B6847FE99B44FF5A0660BFED035BF4DE042EE803D6AF5A5A2A792D3502E7DAFBEC9B7C205D5C8637364736417C7678B6D8529E5B86596D71ED70D0BB515161D1BCC1FE42116978E60F511F6A04CDFE9906FFCF6027780C785D826F769B716F8B7CEB81A37E83612FFFB41DB3175C4CFCF45B4D5131165577F0A05CDFF7F37C25270DFC7688BEEF186E0555707A9BE372599FB4367DAE01AF8B99713A7E8F04616B
	847F7AB63339EE8B676B0567A8939912CDF0E9001BFF33D99F1E2899F93FA7F8FF5CEA46BB075711874FF5A6FC4A5223F115002BCBB763FD62BC714BC0D06DD403C1D5B96EB9CB66D3235249586523EB19A6F8372563F7525FF410334CDEF9405F2BC1BB4CA0F41D62DB4BE2204B85A8827C5699EC8F1C4D63EEB9C35A3DB3A877C0572593553219246C3D8EAC5B8F7409G69G59GEBE6A04FDFBBC9D9A86869696D2192B2796ED46839E42827E3A652DE708A319DBB8908E70D37507913E146D84B7029CDBA6D
	AE5E97D7F590FB08D8436728FC5FC99EEC1D18B8B35A6E6C9B6E43F48EBAF9A67B06DBA8702BA8FE16814F3E6D3ABB44F9BE485B7E83DC9FF65179ED8224D3856ED60A4B00F436407D0162F2A11DAFF0DFBBF13D39F5964ED5DFF6440E035B9D66F170ED07799A2146B1789AE10FA3D68CE7361D9D75FDE65FAC5D1E56D931ECFBA3EDDBE5963361A6011FCB710986BCBF3FC171A4BD8772EAEEC17BDCC1576369104E9238EC31C64F95384D943763077652469FA2EE46D7845782E92F40A9AE2CEF14409D9275A5
	8BDC32936B4B9338BC996BDB77C31CAFF6B8A36DD3B6DB37C9656CD876390D4EBF6BE6C75ED1DCC471565911F7941B5A710E229772F2E623CCD63A07D1B787741EA3FC2F8375318A5C3F9EA33849101EA3F03F32E159AC01BBFA824B2E90389E8D4BEE3F95F1AF76925C9EC8B7895C39DEACEB9338FDDDC817AAF0133A10EE1640B9683E11F49B3C5F06383BA9EE9B246B846ED53A375384698E015BC45BC503F41240BDF58AF9248A5CB86A8F7910AE3B9DF1F2AB722D94383BDA116F8E01FBCA702D9738475B11
	2FCD603A05AF3985EE090CFCB3G17FDBB3947DD6D11A40C7577F5D44253271E2994077E9ABDBD2A0F9C2529FA2F26E6C7657BB5B5556CBB7864B3467394FB1E99EDFA4704C0F986BFDF461F09FF1C406FDEDFAA635D6BD648CB4E4031203603FC5F90EBFAA2485E3D1F86BD4AFE5955CCC796361EFA186AC34AE7E760FA2A086EFB954D851D816A6662F77B7EB7713BFD7A7727CAB9BFB7BF433D4A763525F9AE13B3B057F09E6EEDC7A163196FC01E69101FB7171EA5F79A4F7C90DBAEACE27B45AC5F9616CF62
	9C394F2567DF9416CF64FDBD2CFCC6D8FE3121BC536D1EB7D03775774C123C370E09F98FFE47BC3EFF411F1577CEA8D813E2FD1A794241EE5D978EBB06628B4C2FFEAA232FA54C43F844762BD60A74FF50B439F340523E0E215E67B4424AF9C3BBFB42F80C9B910F42E15041FCC22677FB5C7EB321E3FE3F56A47B942F9AE2730FEB44FE536AF9B87F4816C97B75107D9EF8165CC14FD64B44B66B84CD8575E1276A759EA303EED08D32F9548EB197EA06F28AC092C06A9D6CAE871BAFA50BF38673811F83301670
	188FE359FC0C834B6958E30534A2F087BD228D67E39B7DC0A17DF0A52473814A6F04BAG2CG9AC04A1DC83700524D02F44E1D6CFE91390BC46EEF103B99EB85DD2DC26ED913F2035935A6A21F70A27CC5BC09D31E48EBB550B05ED64AFB53A065306F9953249F997721875B2FC61FA80D7211F32E58F36879B41E147F885A81607C0F28BFB89A876101F341E27EEC02B22DGDBF8BAG6F64GDE0160FFF30C7971BE4AFBB150677C88FD6F7754E7D54523116DE7C67770D7987356839D43A5D39FAADFGF2AFC0
	399DF1655EE8149B4A35AEB0488D6FC7395C0C47A2BA77B98F78048548778591FF67ACC05FBDC36932A01DABF0CFA8684FD2A67A5F7CF652D72D107682A4G24093CE5B40F5CFDA3775548DDAAF27F09659DE9A7F18469079BCF2C3C0CB7C6D94643C5F9609D6111462C416E3EEF16DC9DF8061545FBAF89C192EB3B6C0EF8ED077EC4579D208B01AD9C3FA2134D93F51F7B4DB1220BCA8FF1AB3CBDCB0EE3BCDC1509730F09BD3A8C1DA7861321B00E8CEB8DB2B0FED7E948EFFDA67A77BB3D61B6D86053790F6F
	C57EE58B235BEAC3847FCA20A1007C6B966A7CD95DEF85743A67F4E15D75A6F59747295B36B0523E83ED61FAFCF6DA92F90F36517EAE77C77477A768BC36836A72AEC4FB3C24503BE47E206A8A7AFBFD8A5A2E094EB3D300AED5503EA9470EE39F33B9G5066AE44F870FC6BF05A3364AEFBA98154F99B3C5FE3B16AFC5DD766F16CB69A1F2A003E6EAE742B51E4E949737F0B6A5281F95E3BB00E3CCE4B24C0BAD560DEE06B95C8675E05BA0C9EB42695F23F0825C349A2FB69FD9623CF7E0A5A298270150BF0EE
	FA5FA963DC3A7C9A2F855BBA12DF73A266BBAD0BA24FA6EBA1D2EFA2605396617A5D6779ABBC6DF2D77457C1C0DA037E009A548698165EBC6631A8F950FD0C58B1EC6A884B7DF45865589A351F4E13329669FD0361AF20789586BC3FC767407546AA48EBDC0CE7AC27E9E44E14866945G39G79G95CBE03E3384EDF93B075C719A743B1D4D9FE8D67BD42FE1FB031F1FF18CD7BF66978BB7603CFE4E3C1B240C0B9AE31EB5EEDC427CC1614F0C6C89856449745B53C774CB063CD9GABG56GC8CB6D25DB1722
	FEFF7470336B449D9AB4CDF63A690DD371C15A231E5151E4030E750F5F4AC3192BA06CGFE75CB75B15F866FAEF8E9A842BE7E6637A42E11035AACFD8B1D9BEB41DE1EFE1226FD3DDEAF7F5DAC5558F7EA4EB547BC575F31142F6B16C62EC3ED62FE6CE448DB39945721659B49EFF1187E865861811C1A73EFFB93453C247CEEDC0BAE2775B6C13A83207FEEBC67BB4F0934C9F7639AF2F5549A72F24938867C33923906AC884BEFD6A257101342729BDD11EBC8AC4F6C7A18ABFA5FF61DB8F71EF5B75B37DD854F
	7C3B59F992567E9D1EF86D0F73BD72AC4B0238172537FFDDD66438B6D7A3E38C5E102387FAC45EF52F12BC3C59971EE7BB7F02A14FF8081E65B7FC7002215E687CDD9DDB8D797C7E90595924EC386F6D5CEA60E1CE13380574514F0E2B81B9A8830FDA095D8BFAC2B13B97D41F0577068E3B4C72EDA2BFB16C7E31BB0B65A7E561F8D7C12EFBF3DD96BA085F46D83B5712357BFBB55574F7E6485DF33A6FC47F56FEEC06E3759F9E10A40CCB0F15E1DC5AF6738DD263CF9AAD2F4768F329D97C4EEA16BEDE35F456
	EDE4F55EFED617F96AD7C31119FF3B7A0A78D2276C8D1179872B2B7D3CDED77DF7207F682FA541EB5AB7BA2FC764214BDD6DB04A4D3FEBB9237B5DB431065F310C753BC6F836ACE3F76ED83FFBD69D4EFCBDGE547AC537BDD4A3248FE9758C04A61BDEB3C3C4072FE77663312385FAB6EB5303C598324DF996FA106171DC7FDC6E4CA12095D512EE512EE5731B60C7BAC6F5D82BDEF0606F9AC1EAE6A45F3952920D76C7A0FD948BBAB73F3F1CEB3F79976215EC21D76471F6B7CAADA6335F1EDDDB5AF275874F2
	C5513EF1406F7587214C2E9D945E5B0D65461D504B7D7A54D078455673B63B7E7E4897FA3D7FB124FAD93975AD86F906213F63CB3D5C236DC357FF79A93DDCC75C7E2B737BE52B2EEBF91BDE07FFC8F53079511D349C7FCD1EA079ADAD53B9B34C02BE033A585DDA1B6C92E586C2246F08EFD7ECAA4D73C605C85C9677FE69CFFB30EB5EECEE780D0370667B15067772C14FD07EF7015FBF79CEF9B07DE7EA313FA15E2A19FFC33CC3B37F0638D09B7C9C66B2AD766FF765E8465F6FE26D3E1C7EF6C545F2186783
	58G5CG6717631C382D77B5094E09CD66431FA8AEF216037CBA25A6FEBB32AF362ECDFD71FF537273BE3296F778A1FE76107A917FC59FE9AF4D9F20C806EB6DA3B68AFADCEE0D11F23C230F44832F5AC951439B633E12A5E11BE768EF432488DB7C266F35D87D821064CE8E73EA742DAF632EF952171B1D35F647AD770A29AFCF99769A0B7F96AB7D8D1A2C6511EB48CFA9FE457248B5E40E3E06043C46FB449EF8547757F8406A2BF4E3FDE9D0572AFBF00D60F6639AE1EBB62EA72E2234D6C83B81C6E51B0FF5
	31470500133C337A5FD171BEDACAB6729C63C2DCCEB62E01AE57423E45994E57628C170F418E5CEBAB79B12E2B5B94B2C72907F4AB40A8009400F40045GAB8156GC87742BA94208620912085408BF09EA089A075DE74417183550378A07FC0C8BBE5D07610EE49ED7CE3A75AB8836ADAFBAF6A3BC5C19BD75D07B6BEC03F7FF7C03A9FA0693E615AF8EF27A4FCB22B576893E9D0577C7B865FBFE665FE91F626A9874A6445A947FF73EE10F3EB0F34470FBF7F5E4E767EDD2AEDA00067EA5B497C545F5999D2C5
	1CDBC3774A996EE0486A8FF9482846721EF3907A900F856C2CEC66E6B2FF7478484A6AG7BF5DE21EB23D33757E1437B437F83635F2F7F87F83C9977CE2CF9393A91166B99F6B9966F1E213F9D346E3E48B83819620BF2A26360C491374AA1CF4B41B838CBB5633FB207796F9AF8CA771B457D0BCD6F00D7F05AAAF86EB3AD17B5E85C375E9F5DEF181F861558FDE5C0095FD7C6AB66E3F50292BDD67F0BE2BED6DF219847EA06CBD1C85CF5C1ACEB91F413846E2440CD25F5925C979EC5F38BFC9A150B601BD442
	D04C4328DCE7A82E0563E69BF0A7715C34C27AAF286C7687316C1D747758BABDDEAF1F07D19A0F8F3A3F3047D2AA23A7A09B643EC760CE0A76FAF05863A43F3BBC42F2FFD6C6569FEEE767466E0F6C8F19949F38BF32BF9CD430BF1C073CD57763F748FF23EB53468798BDBF3FC5F12DE138026EE83A273B0DF4FCDDC04F6B707B010677296E6FBE165D711D72606790683C249FF4CEF8GEDB8D3956762844E2260B928BC011B6C42326B842E400DE50B9EC45C6F9CD856A6F07F261F17FA90E3591D0AEE5F2506
	77234AF7EFEB77F74A037F06541B44E7795A140691065F567F2C0E47B002EBF3B57C3B63A4328C665FB8178CEBCFAA72DBD94603115FEFEBE99BAF86FC4E0338C713695679EDF54763B7583739758F326F1ABA3FEDF4DF3EE4053D34E2856E33BD9177CCC331246FF1C7722807FA1EAFB2FE937E996D3351672DEADCC35FABE15F6D597B83BDFA9D791DC32F63FF79C8B9767F3F7C7E74CC28A07D17D2F1A8A83D903DDF52F069A3BFC9EFEF2C30C816035253EE6A90872562B344E37A8312E481D7AD488F1D1668
	3EA8CBE9D259A1D58DD49ED3FB7CFD2A22C9A5B4F9C4BD83B90D066DC2CD5A58203FCB708FAB124829FC85C45AA4C4BAC8471CD5FD3B77967E6C06C20BD4847230B17820D4C486D290AB4844AB04B11888A795F1A0FF4591F5C727486809BB0385B1CA909A0B247F958B2C712FD0E00B0CB088D662DEC5F4FEAE3C898B267D6942724BF677DA2412FFC8CFF74A5E00DB86DD69EFEAB5CB258E2A349A84DACDAA27BF8477AE14FD17167D30D932B8C31A74979D6FA4F816FBBCA0BBD5A059284841EEDA83916545D3
	52464E204BC199E8126554BFCE81DB1AA259DB1B25E7B4B759GA111C0137E24D7EDA5CBAF1E71A1EDG690F1D1D938274AFC2CD23A970F8C53189CDF8EA4B106AA48D78F4D4330753047D8D878BCE0B1F0A7642BBFE925A9B25D13160EEA6B54AB9B44AA22E11053D735FB57B56787DF35DD8E74CF338F3D63239F0564A4833832F5279C851A376524AC7F02D6D57487F5990EB9F74F80F6C75EA32373B412710BD517D831A2A6F43ED6FA575071006E7323C3F7692393B8CE57EAFD0CB8788CC4AEFE7E2A1GG
	60EAGGD0CB818294G94G88G88G4A0171B4CC4AEFE7E2A1GG60EAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1CA1GGGG
**end of data**/
}
}