/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.xml.merge.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlReader;
import cbit.xml.merge.ChangedNodeInfo;
import cbit.xml.merge.NodeInfo;
import cbit.xml.merge.XmlTreeDiff;
import cbit.xml.merge.XmlTreeDiff.DiffConfiguration;

/**
The panel that processes the XML compare and merge feature of VC
 * Creation date: (8/8/2000 11:50:44 AM) 
 * @author: 
 */
@SuppressWarnings("serial")
public class TMLPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTree ivjTree = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JButton ivjkeepModButton = null;
	private javax.swing.JButton ivjkeepBaseButton = null;
	private javax.swing.JTextArea ivjBaselineEditorPane = null;
	private javax.swing.JLabel ivjBaselineLabel = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JTextArea ivjModifiedEditorPane = null;
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
	private NodeInfo fieldNodeInfo;
	private XmlTreeDiff fieldDiffTree;
	private String fieldBaselineVersionDescription = new String();
	private String fieldModifiedVersionDescription = new String();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;

	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener {
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
					XmlTreeDiff partialDiffTree = XmlHelper.compareMerge(firstNode.toXmlString(),secondNode.toXmlString(), DiffConfiguration.COMPARE_DOCS_SAVED, fieldDiffTree.isIgnoringVersionInfo());
					result = partialDiffTree.getMergedRootNode();
				}catch (Throwable e){
					//
					// can give more feedback ... like a JOptionPane.showMessage()
					//
					handleException(e);
				}
				if (result!= null) {
					//erase the original nodes
					DefaultTreeModel defaultTreeModel = (DefaultTreeModel)(getTree().getModel());
					defaultTreeModel.removeNodeFromParent(firstNode);
					defaultTreeModel.removeNodeFromParent(secondNode);
					//add the resulting node
					parent.add(result);
					defaultTreeModel.reload(parent);
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
 
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,message);
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

     if ( (original.getName().equals(XMLTags.VersionTag) || original.getName().equals(XMLTags.KeyValueAttrTag)) && 
	      fieldDiffTree.isIgnoringVersionInfo()){
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
private javax.swing.JTextArea getBaselineEditorPane() {
	if (ivjBaselineEditorPane == null) {
		try {
			ivjBaselineEditorPane = new javax.swing.JTextArea(3, 30);
			ivjBaselineEditorPane.setName("BaselineJTextArea");
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

//			java.awt.GridBagConstraints constraintskeepBaseButton = new java.awt.GridBagConstraints();
//			constraintskeepBaseButton.gridx = 0; constraintskeepBaseButton.gridy = 0;
//			constraintskeepBaseButton.insets = new java.awt.Insets(5, 0, 2, 0);
//			getJPanel1().add(getkeepBaseButton(), constraintskeepBaseButton);
//
//			java.awt.GridBagConstraints constraintskeepModButton = new java.awt.GridBagConstraints();
//			constraintskeepModButton.gridx = 1; constraintskeepModButton.gridy = 0;
//			constraintskeepModButton.insets = new java.awt.Insets(3, 0, 0, 0);
//			getJPanel1().add(getkeepModButton(), constraintskeepModButton);

			java.awt.GridBagConstraints constraintsdisassociateButton = new java.awt.GridBagConstraints();
			constraintsdisassociateButton.gridx = 0; constraintsdisassociateButton.gridy = 0;
			constraintsdisassociateButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getdisassociateButton(), constraintsdisassociateButton);

			java.awt.GridBagConstraints constraintsassociate = new java.awt.GridBagConstraints();
			constraintsassociate.gridx = 1; constraintsassociate.gridy = 0;
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
			constraintsBaselineLabel.insets = new java.awt.Insets(8,4,4,4);
			getJPanel2().add(getBaselineLabel(), constraintsBaselineLabel);

			java.awt.GridBagConstraints constraintsBaselineVersionInfoLabel = new java.awt.GridBagConstraints();
			constraintsBaselineVersionInfoLabel.gridx = 1; constraintsBaselineVersionInfoLabel.gridy = 0;
			constraintsBaselineVersionInfoLabel.insets = new java.awt.Insets(8,4,4,4);
			constraintsBaselineVersionInfoLabel.anchor = GridBagConstraints.LINE_START;
			constraintsBaselineVersionInfoLabel.weightx = 1.0;
			constraintsBaselineVersionInfoLabel.fill = GridBagConstraints.HORIZONTAL;
			getJPanel2().add(getBaselineVersionInfoLabel(), constraintsBaselineVersionInfoLabel);
			
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 2; constraintsBaselineVersionInfoLabel.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			getJPanel2().add(Box.createRigidArea(new Dimension(100, 0)), gbc);
			
			java.awt.GridBagConstraints constraintsBaselineEditorPane = new java.awt.GridBagConstraints();
			constraintsBaselineEditorPane.gridx = 0; constraintsBaselineEditorPane.gridy = 1;
			constraintsBaselineEditorPane.gridwidth = 3;
			constraintsBaselineEditorPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsBaselineEditorPane.weightx = 1.0;
			constraintsBaselineEditorPane.weighty = 1.0;
			constraintsBaselineEditorPane.insets = new java.awt.Insets(4,4,4,4);
			getJPanel2().add(new JScrollPane(getBaselineEditorPane()), constraintsBaselineEditorPane);

			java.awt.GridBagConstraints constraintsModifiedLabel = new java.awt.GridBagConstraints();
			constraintsModifiedLabel.gridx = 0; constraintsModifiedLabel.gridy = 3;
			constraintsModifiedLabel.insets = new java.awt.Insets(8,4,4,4);
			getJPanel2().add(getModifiedLabel(), constraintsModifiedLabel);

			java.awt.GridBagConstraints constraintsModifiedVersionInfoLabel = new java.awt.GridBagConstraints();
			constraintsModifiedVersionInfoLabel.gridx = 1; constraintsModifiedVersionInfoLabel.gridy = 3;
			constraintsModifiedVersionInfoLabel.insets = new java.awt.Insets(8,4,4,4);
			constraintsModifiedVersionInfoLabel.anchor = GridBagConstraints.LINE_START;
			constraintsModifiedVersionInfoLabel.weightx = 1.0;
			constraintsModifiedVersionInfoLabel.fill = GridBagConstraints.HORIZONTAL;
			getJPanel2().add(getModifiedVersionInfoLabel(), constraintsModifiedVersionInfoLabel);

			java.awt.GridBagConstraints constraintsModifiedEditorPane = new java.awt.GridBagConstraints();
			constraintsModifiedEditorPane.gridx = 0; constraintsModifiedEditorPane.gridy = 4;
			constraintsModifiedEditorPane.gridwidth = 3;
			constraintsModifiedEditorPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsModifiedEditorPane.weightx = 1.0;
			constraintsModifiedEditorPane.weighty = 1.0;
			constraintsModifiedEditorPane.insets = new java.awt.Insets(4,4,4,4);
			getJPanel2().add(new JScrollPane(getModifiedEditorPane()), constraintsModifiedEditorPane);
		} catch (java.lang.Throwable ivjExc) {
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
private javax.swing.JTextArea getModifiedEditorPane() {
	if (ivjModifiedEditorPane == null) {
		try {
			ivjModifiedEditorPane = new javax.swing.JTextArea(3, 30);
			ivjModifiedEditorPane.setName("ModifiedTextArea");
//			ivjModifiedEditorPane.setPreferredSize(new java.awt.Dimension(100, 50));
//			ivjModifiedEditorPane.setMinimumSize(new java.awt.Dimension(100, 50));
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
//			ivjModifiedLabel.setForeground(new java.awt.Color(102,102,153));
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
//			ivjModifiedVersionInfoLabel.setMaximumSize(new java.awt.Dimension(100, 14));
//			ivjModifiedVersionInfoLabel.setForeground(new java.awt.Color(102,102,153));
//			ivjModifiedVersionInfoLabel.setPreferredSize(new java.awt.Dimension(100, 14));
//			ivjModifiedVersionInfoLabel.setMinimumSize(new java.awt.Dimension(100, 14));
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
			ivjTree.setCellRenderer(new cbit.xml.merge.gui.MyRenderer());
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
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
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
 
        DefaultTreeModel defaultTreeModel = (DefaultTreeModel) (getTree().getModel());
		if (currentNode.getStatus() != NodeInfo.STATUS_NEW) {
            //process himself 
            currentNode.setStatus(NodeInfo.STATUS_NORMAL);
            defaultTreeModel.nodeChanged(currentNode);
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
            defaultTreeModel.removeNodeFromParent(currentNode);
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



	//process events for loading the model displayed in the comparison panel
	public VCDocument processComparisonResult() throws Exception {
		try { 
			NodeInfo root = (NodeInfo)getTree().getModel().getRoot();
			//if (!isNormal(root)) {
				//displayMessage(this, "Please resolve all tagged elements/attributes before proceeding.");
			//} 
			String xmlStr = root.toXmlString();
			//System.out.println(xmlStr);
			Element rootElement = (XmlUtil.stringToXML(xmlStr, null)).getRootElement();
			XmlReader reader = new XmlReader(true);            //?
			String rootName = rootElement.getName();
			Document doc = rootElement.getDocument();
			VCDocument vcDoc = null;
			if (rootName.equals(XMLTags.BioModelTag)) {
				String docSoftwareVersion = rootElement.getAttributeValue(XMLTags.SoftwareVersionAttrTag);
				vcDoc = reader.getBioModel(rootElement,(docSoftwareVersion==null?null:VCellSoftwareVersion.fromString(docSoftwareVersion)));
			} else if (rootName.equals(XMLTags.MathModelTag)) {
				vcDoc = reader.getMathModel(rootElement);
			} else if (rootName.equals(XMLTags.GeometryTag)) {
				vcDoc = reader.getGeometry(rootElement);
			} else {
				throw new Exception("Invalid root for the tree");
			}
			return vcDoc;
		} catch (java.lang.Exception ivjExc) {
			handleException(ivjExc);
			throw ivjExc;
		}
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
			cbit.xml.merge.NodeInfo oldValue = getnodeInfo1();
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
}
