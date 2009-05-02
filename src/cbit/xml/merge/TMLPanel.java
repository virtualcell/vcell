package cbit.xml.merge;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlReader;
import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XMLTags;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.ZEnforcer;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
	public static final String COMPARE_DOCS_SAVED = "compare_wth_saved_version";
	public static final String COMPARE_DOCS_OTHER = "compare_with_other";
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
					XmlTreeDiff partialDiffTree = XmlHelper.compareMerge(firstNode.toXmlString(),secondNode.toXmlString(), TMLPanel.COMPARE_DOCS_SAVED, fieldDiffTree.isIgnoringVersionInfo());
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
			ivjTree.setCellRenderer(new cbit.xml.merge.MyRenderer());
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
		XmlTreeDiff diffTree = cbit.vcell.xml.XmlHelper.compareMerge(baselineXML, modifiedXML, TMLPanel.COMPARE_DOCS_SAVED, ignoreVersion);
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
	public VCDocument processComparisonResult() throws Exception {
		try { 
			final String DEF_VCML_SL = "http://www.nrcam.uchc.edu/xml/biomodel.xsd";            //assuming its only used for VCML
			String schemaLocation = cbit.vcell.xml.XMLTags.VCML_NS + " " + 
			                        org.vcell.util.PropertyLoader.getProperty(org.vcell.util.PropertyLoader.vcmlSchemaUrlProperty, DEF_VCML_SL); 
			NodeInfo root = (NodeInfo)getTree().getModel().getRoot();
			//if (!isNormal(root)) {
				//displayMessage(this, "Please resolve all tagged elements/attributes before proceeding.");
			//} 
			String xmlStr = root.toXmlString();
			//System.out.println(xmlStr);
			Element rootElement = XmlUtil.stringToXML(xmlStr, null);
			XmlReader reader = new XmlReader(true);            //?
			String rootName = rootElement.getName();
			Document doc = rootElement.getDocument();
			VCDocument vcDoc = null;
			if (rootName.equals(XMLTags.BioModelTag)) {
				vcDoc = reader.getBioModel(rootElement);
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8DF8D45515C1C20D1A2E309F365125B52BF14BEED317F631CDA95B45B6EBF121CAA3D63A9BDADA75DB5A2225CA3729265B47A401840300841B2EE8530A9AB4E00020E9GCDC810C4469212818284484B4C1B1F4CEF5E3C7C8759F3FF4EFDEFE65E4CA4D95D7DB69F67FB6F1EFB6EBDBF775CF3FF5FA0D57CBE33E43D25C2125617C87C6F6D75121453A7C945B5E797A02E7E353CB24970778700ED52
	7D6F9743339572AAE76515ED157EEACFA9243BA15D7D493C325F427BF6A9F34FBA85DE6270D98AF93B329E9FCC4C67CABB7259C8531F5AD08E4FE781FAGC71E592F917EF7EE286478D40EC7D8EF112461EAC2F36F061D9C3796E8CA81D78750DA4D68FB6049841851D04F693AE74D15567D0BEA59CC6AB15409300817FD04BF2B242756276FA63C1E20F212B2BBA1ED83A04A97C96E519761F9385D6BFA475E6B51768E7BFDFB7D0A6AD29A9AEC9FD85B650062037C5257C99D8D9BCBE999C9DA8665E7D35C85B7
	29578A6F8E007157917F021760D7407BDA0052B7D017A32F9D6632F60DFDC5DA3E5FDF3CA50EAED5EFB09DDA5F50F51967DFE9799D357BBF1EA276CD053C39G8B81D681C8B5F9E5D5GAF50764EEDFF9D1ECD3DDAF3506F8F861AC22123BE077AC1502178603D2181F2B82EC33187D507A431B6AD58D0E3A2CFA2E065B6EC1CECB9E613398730FD33BB6E14727F61D3160DA6B6491FBBE6292FE136E82F51ED42F83F9B17F728A6527FA2F91F150C3CF79E7FEED109896F7A3A6F962572FAB28C3C175710363E1F
	7AC08E3C2F6775B2FCBE45D76E51714C97E50AA769BA484B58033E5175AC4AE2AD3ECD5A3A6F5326F64878C63A25F08F1321E2EF34AC07FC246E463D0C67EEA1CB97454FB16019ACFF20FECA52851057D21BD7467C6B1ECE52B7E76E4BAB4B8648854887A884285C1FD7D6339FFDEC7329B7A768E36D322A9D9D70789C0AAA3132771CFA9E1E4A30264AED0130A6876C4A99D975483DBEA5ACF11A7BCF919B74C614DBCEFD5F8EBC0EAB81C515B5CFB0G354BFEC5D354B023D9F9EA9B21D1951AEB737895E0608F
	C9D86F2327364353A7073553A1072CA9BA854BFF6C94E913C1798C54889540BB334B13B65A5FC1FF8700A66C606EA0751DD0D488988D8D56E050BB903291CD245C7D444EE7E86CC803774C7DE8631F1DA638528350E78FE0BD37F6BCE7282745E3A7624B6A887631875DDB095FGFDABC08FC05A81741F473B50FFDAB6FFC12AF970A6D37F4986DF1EF700794D6283517D68C734FEA263DA485BDA17D7D6DB07315E656E1AE03FEEBDE05E676A383CB43EC751B09EA99366D19287DF447B9656E94CE3F63FF81634
	E1BB686583C82FC35B7FEBC795736523F2D8C16FBD290263A98E3DA5181CCD3EF6EE5B924E6F99GB2AE11F8CE464A1781F6703EC5465FBD1CBF61C5461C5F839C84F89B60950057048DAE513A33C02E25G6BGF69E043E896083188D30906091007237E04C846886C8854886D88ED088306B6D3CB2AB0086B067EDEC43573C0D6BF51FC09BC64B0F765A41E50F169B75C339F7F37ABDD176C67DA6B3A67CFFGE64BD1EA4B22A8DBEE0C32A716594EED99ED47AD34AE5D0EBBA45DD70DBC515E7F573AFF54407A
	5E32CEE287E5D089E8ADCAC889B860A54C739E6AA476646304B3A84A2C2478900CAB4D904843827FF0A731272CE92A27F7C0D3BA95CD7384DCFA7ECFA92F70C0EFC89F121057931D249D03C3B02E887ADFF61218EC8F86828A8D8222884F7F959D93941F6287211B0383810D61FFE3A3F3E52FB242472422539BA4B2267886487846661F73E462A7A155630750C206C829221E0C977E0D15869B65C27FA573685570ACFC9B637D504B986FF70D7E2D3468F248E2B6F7493863C6CBF7BD0B3521FABD5EB319561DB6
	177DA9BAB6F1FC18497A35920243E9F191257B30094892003118B44DBE0975076D12DCC76A6905C06A738494BE0A36A8E13B6A89919B6E63F42747881DBF6870B8BD0A430C0E456CCFF591B9E7027CD9G8B6BB1E68F1EA179B8DE72E05D9C7479D8D3C1DEA135E73527025BE3B9245754B3FBBEAB7217087C16BFC1BC7DD3F4FE388F73FDA44F104F64301FA563E6D8699FD0E09AC3867E93225D7B4EBE9715F7067BG4BD74F929F7099668C2D8D0467EDBEDC9F4D86FE59GCB81AAA12F3E012F8DA85DD53F20
	EBE0F45970ACB050ADBAC4670081242B04F4ADC07BA126474CC35CBF8E933A1DC124ABBF4C461FDD07D93DF5070D75AD97F48B813F1C509EE675559DB1561791C2BA4791B60E0D9F092CEF6D91C2575E0FF469C7585817F5045517F344C8770420BB7C8E509868EC6F70F94DBB04EE36AA6AFB0765E73D4366B5B970FCC968F1A90CF4956FB239EA5EE574436F9A75FDC5C33AB96F6A754452FDCF504D6775AD6575D5B69A69B287102E311165F7B51A55F7FE9069C2101F5628375B5CC6231D1F9DC23A950DECAE
	3036512C5D6E91F4D29348542457D75FE4345F68B05235B731FCF7931B934EECE276E3F4ADA3C8174969F21A583CB112EE53A8522DB254D3C0E4B8EA24DBAE682A0F6A75B49E5569D8BCFAF2B70911F46D5EA78F4A43FB43C390679B9A5E2771092D599F2273EE9223FA0E625A7CF29F09A599101E873078A846536FEC463579EC6FDC29607803C5717699D69DE5F6AAEE56636978BF8D9531D8356A9C615982F936E60CD30F1DB52E8F1A4261205DC317A6BC864C69B246DB067B6B2ECD70F4FA543056868362B0
	460B4FF591F9495A052379DA6E24271C47DFB7704D84D851CC70699EB28E915FE7FA5A8F201EB3C76F10FAEE3A4572CC9CBDF736B07D8E3768FADA0F9CAC62FBAA021F867959AD486F4DBE420F788A233B258F699E811A1D47106EA1CA17A3681678104E86B469GD56F917C57DD958619F2813F9A209C725898325F5FC8E4D095986AF95C5FCBC759E6379CF0A90EEA03F76C25D30C3DB6A371B1025A4DF87D3B5B484B8D3859GB9021747CFE456E0B242EC7905179AB96D52BBEF13CA659996337D1C749F3BAC
	CB5FE3B6DC771EEE4B7287D7DA2AG187EF37D287F6163B0EFBF0EBC3784BEBA7D7E4BEB26DF76F1EC03E901F25E3F96812E60B8364166883B5436C22C8298EFC599D7877643D3E64231C73B22BA032A1F4B37D79E5238F0CD862930AF7F4B1BE4AE6CD51490D9FCD9E14ED0ED2545FC20625E133DFD204A215DE7A45C3739ED3B516E072FDEB235FB5A9637E5E6AB33F7CE2BEE772C65ABADB94B516E4702E87762363C3243ED28D3F61034352C3A76B1997F7E15DAF83A944D8ADD0FD9D7F1344B1A3BB9688F85
	8320CE756ED821193CDABD4A1B1DF71754557797267292505A181CF35AF4F9DB57FEEBE3B7G137757C1EC2355C0D375BE36D12F472C0D3A5EC7FD9C217FD99B1D6F45B67A004F5762359113736E905A5587B2E489B92A7B512E0CEE249F7549871A0A9338B7DB718EF9D2CF17D006CFD8DEE7ED50BC202AA09CCAE2039E99E17DCE187D87DC0750DF1EFE916D3F2BA5CB2ADF670DEBFF6B89E6F76D04EE7F25CB8F962DDA0AF16F8595654D851AC2A16F6036C8590E7259EFFC59D8FD7F28221D6A2C304637221D
	EE7310B18167568C579A2671476D89B33D1EE8B276031C0D6926FA39BF17E14932B2FD16DAF53DE6BE3C521275B07603EB4328574E8FE06C7AG65F8BC7CD1449FE623979A487AD6D5428A2C3CB48DB283F43F2F5A60B7FF56D00EF410A177830CCD16814497822E6E4FA2A68FFCF4725DF57985140FB45BFB9E4567E84215A4151249F22F104587F2E489D9EA87895E9370E89E596799D52AF7B37A69034247013662A47659940BD91F351EC41F12D552679C1E304C278CF163A2637376907AD28F5411F192657A
	6CD0E41F7B59904A338CE8CA5BD11E2C1019BC0D6DA8CF976D2B3AB03B4F307AEE9F96E3B7502637A35FE3FD7FA5A5DA8762FAF16F887AB52B4FBF027565C2DD05223E47E8FD71570BE807832322CF1D02BEF58A755B59EB54AF6B949B7F961F427A3FBE0A63BB0F35221EFC20296840FABEE7DA0F358377597245F9C4CF870FA79D7AFEE79A3C4FB124693A146E53BFC067CA73BA58BE7DC6017F8145AFB66019FC8A451374AA48EB69443D657597495CF8AE24F38172815681D458205F58F0CE7A663983D2123D
	7B360086D3D659579931D902FB686708DD3C4A88CF7F781CB1DEB3BB1C7690BB345B4C77034D00153353F26EC914E371F077F963D9E1713677ADC5A67110C0862FFB2ECD0F074CF6DF3AC45AF6B5600BCF43FC95208E40F69A6D96B8DF134CEEF80E418D464F5526E80B8DFD133585AB1759B7B5FEF7CE115FF7FC494E7F3453E6FB7EBFC8DAAEA32A9C5FBB33117DD413759F096EFC9F6DD645380FB66FB4BBFF490767DAG36C67C058B570825E7F00DF465BD63DAF06116ABF157C80DE778F946995D0F72DF7E
	E59113F1DF790B66B29EF3F83420CAD62A104FE4FDB1903D67C7E44D04FA671FC1D9DF72202CCB4FE0DF37751075F271D908CDG75GB600E10059GB9E751EF971CBF144CEF69D0E88B3259DFF3740EA77A07FFEA6D3CB0E93995E7A34BB11DCFF9D16735E7B13EDE9B082E2B3AAB322C398CB745145B9B15EE69B27335AD1D3FB3EFC7B60B91EDD869B5EB43EE28B35405ED5862AE657224F5E19BAE11C99BAE02F4BE40BA002D67A06EG3483E867308D1F39F0E402ED588C3856E0509B5B0AE80B9341293563
	392465E61F0BAC47F4BCE1A7BAE6C35EE200D5G05GD567E14D8550F39EF53CF8615DE4BA0A635EE43EDA941A1A1FEC0BA9976DA7A9674DFC75E68F36ED46F97455EF4554B5B1BBBF18345C3273E6652E76CFCD67CF46140B56F975F933F6DD62A06DAADD003192208E209DC0831889B07F82366B3C8BCD13E857647E7B87F5EAFA3E99D3AEDA4F65974C4AFDB3265C445A71BBC94B2D39E0E65723CE9A573B210FG548358G0681E68364F423DD0B3B0FA6332BD842C0FF41D79343F2BF88CF4D2EFF95D3AE5A
	2E4B3A4D4A593429716B0DA997632F267CCA8726466F7918F2517C240BE6656E1FA23F15C979D51A723BEEF0EA7CBE9DD3AE1ADF6DC5331877958B09F9E41CEC3D08B16F4850547A4A092465D2CCF57EC5CC390969DCB41CCC678CD31D7FAD04BA4F93BA7FEDCCDD935379ABC94B2DB655B9BCB2B5FE53121613AE19157B536854789DCFDA2EB20A9FBF5FF146B95F314A3DF83E5359CB5A011C5354DE42F6A89A1B1A1C5B12161BFD494C973EAFE44806FCF65F6BC9FA6E3475F2DED94DE544BDCC67FD0D103689
	5C22BE029B0674CC017BA11D6F4E0774524B28537768BAFF952457811477A07ED10AA7FBB464CC046CC1B03C37AF51BDAAD28F3E133A62512D3BEC1E47F824A664D194C59F0FAE5EBDAEB636ED3A987FDE48360B6677C2FEFB517CDE480B9737B1BC54E13C0D08792FDCCCFCEFE44FC533FBA3AFD2DD3FEBA16D208E84B40FDF21EC198FAB43E47F8EC6600E01G9B1E8967F074BE0104E3FE0A0B6CBFD57560587E4D0B7B2558311DAD9E31921BA22BA71DCEB130F70694BB4A3C60D2FC7B5DFD495CFE5F3AE4EE
	3FEFDFCAEC3F87AF45339F33513FEBE46FB7669E4D1FE146A761BEE0293F0C539C53029F6C933140F5AC4EBA3A532E86FDBE3E0E66E73E8A5E53BE5C03E73EE5AE52BE9A24538132C47FF94AC57A7CC2C8AFB37403C778FB01C95FE06D74D5FA9ED6AE6715D583B482F4818C4B58EE3E4BC977560E8D1B6E2DB19DCE87F0FC4910D1073D745E70B2C82F8610FAD10717BD9837AAFBE3652D81DCA3004D1047E43C54935FB7149EF35F707610B8978E8E28F6A53A7D551ECA1127CFAE5BB445CF1A18510C51721A2C
	3A14987F390616E7F9714A5F50934F47187FBC9A61BF7BC52CBA63C59B39CDECB48E3819GD906BC663377BE3F057AFCB82C0469423AC556E44E6B6B81832F66FD427F5A3D58F6B93D58F69D547F56C23A548E73F9BB36DD1B5BEC4C2937B3B92C706C31C74A4B7B1A9C3F6D72E573367B119C3F6DFEACA7EF3BA76478ED77ABB9F95BD9FA93375DB541E738BD5B1DEA508F3D1F15BBED0F2FEB375DDC57DEFBFCDDBD76643A2A76783A0E58136BFA0DBD312E8DA1236F68FE7AD90F19BFE85C875260B9A74A9FD8
	FD7F990A345DFE0E7FF2FF843E99ED7A20A3017FB862780FA3017FB8A660BF0E847E6318007FA809ED5A25C66A0AB6F8CAC5DB37EB4176FD022755995F861DCEF39B1CF34637C10FB3398DBC4E78B688B9135BE044C9F46C75B8BCAA3F6148F44C8D9B7D29CE5864864D08BFA8706F8E98718784FE4320917F1640AF0A403FAD7CF521454CDF67F19F4D354746DAD61FB694692F87B83EE6584827DE70D9E53A973602573FA6AE1FD54346365FAF70FFB7E2446B7A07C60D78BA413F2B57EC2EABB978F7B8702CF6
	444AF05884578EB807432C8FB70C99F99F943C9FB665BD4C6BC807E726891F79A63845004BB7653D291478E5C8D51C0A2AAA0ECE4FA819CFD5ABE46CFA39140CE7EB21DC21834FC91FF2133B4CBEF9A4B820C1BBD03A2087773EFD10CE854886D82430BDF0B60F7BA2EDF727AA7B216B52096AFCA73B8B06E52BA0DD87E08350GE6BB11E701171405029E7F001FCA48643B47C764AB843AD29752CEFB09580E5D84443B46BB9CE48CB64C9877B1FCD62BCE3B1F7745D497310708B5FC0E4A4FF615C46B44BAD72C
	5D59996E95BA87E8F531B35CA2013F16623B8CF8F6363B45A56E73C15ED2972E8F3751F5E42DBB2F4C6AC6DCB74575C0DA9338AC0ACB05749C015BA5561B0B5CB8D73D5B9DBF8EAEF61B47417B5C66EB04656E44EB04FCF73C984EECFBBE66FC669137EE4F82F7BC5BFE094E0D4BBD4C06DB847E9B94DFE740B35B56D1BCC9F7C35E8A8F5A27016E5F5B7A724AC2FDE2AD6FC41B258A5CFCEA5B6510DEA3F07FAC931C644DAB2B70A24E6F457A2A85EE03876BBBACF0DFF2E1FD6982F7039B6B4B7462FC7134B75A
	BE733D3ACD96FB6359679E6ACB795E68EF949F207882EF74B70ADDAE7CC63142876B9A9F4A54C3E54A07749A017BE7B76AD3A0F0FFCD75E9756715F57B91F717874B06846EF9610F6382773A854BAE953887A8EE9524578ADCFD9116AD9438DF06104E9AC0DCD7BF52F5895CA85D33198B699C017B814565C2BADF606E23383510AE8DA22E122EAFF7C23ADE603E500BBC2C82F7AB7507B9101EAFF0A5F6643BD8601CF6643BDC606AE5643BDA60B21D4837D060D204EFEE8DA14E63C63EBD0053C264BB6EEF132E
	4B63608AF795BC834AD0153874576271AB81F215267AE0EDEDCD55DB35353B59B9F8661071BEC57D26EBCC3FB1A1109362774B7833F047AD7C5B6BCFFB705B6B62FE7079FE9C8B7651F3EB3126A702B4B49CD7BD0E23324BCAC796361E7A81759BD2DE6B477554E63A6F158D69DC007CFEBC379FFE954F6D337F66B6E9CD33799D6ED536AB16F57DCC4ECAD5BF374F752F34B01E3F6EC31EB648CFD7896E0D083B334416F797337DE216FFB1A21F44B972BD171EFFE3C4BE1177650872F7C76417984AB35D163C02
	3A95AE19A7796ECA0D7B9D7CF22ED381FF4EFC64667575DB460A18AFBC562F7B42513E0978820F038A7ADAED986351F43ADF15A2521F24E9724D81CB6FF0CD74FBCEA3107AA6C247F86C1D920F22C950ED172A69F731E1F7F0A85C918CEA2DF240C1EEAD5279C7139B771BE606F17E91F6137673137D9EF84E8B933A6B5D44B6190266EBF48C32ABBEDF8799F4D5C5E57328875CB897DA8734659A4499G2B463E65E07325C3E23E64G7CB0C086C08E408A8D63C0BF5DC3AA00F479G625686C59B8EE09B6538C9
	BFCC01749C009C0095G85GBB87734A9A8711EEB1256B027470A07B3E08FC0BD4403F4D4890F48DEE721DB5A917482E0407B1CDF84DB160F2869217CF61B2C67A42B94AFBCB02F258770CE9520F0C7B5009762B51A71E097111D4EF7CB9341D46309C50B197E0579041EDF235A46001F341927EDC8B650A215C8E1E2E04774EA21DEE3BF42F4BFCFE3C127A278F680707445DE20AD39C9E0DECBFB33A47E50CF9D9C047F01F2173179CC8678E215CAFFA1249DDE6149B4A948F9B64067713DCEE466307F42E308B
	70354348772BEE1425FB98FD57C7E589C1BAD560FE6AC3FF1EBB0C7E3754C57AEA8124ABC620CE007691F10EC2734837EF64FBB572AD95797E09FFF7CD638852CFB61E90FE64D99127AC63B1C4F960B742D30DD9093EFD5FAE8D39718EAB0B77DB4974DAE2ED176FCE56F668CFF45D813A9058417163A3EC1E287BDC0E0B682250CB5C8E5E1E773BB09E260D607C23D153E5585317C80622A4B2E498E4E07CBAAD48AFEB847D7BA716C89B5887F47EA70B107FFC13365A9845FFB150ACB6704FB770E7F5AF99546B
	8E76E35D2BCD6AAEC9D2F7E10CFDE775C66A717810A4727E231776F7F9B82A3FBFCB67132523607F23E80F85FD74DB32202A385460C04001367BA51D37B6821DD5503E29440FE32DEC8EG342923988FF2FBA7531E73209C8154B9975E17D80CBAFFD9B60FE3775178348C6873C7512FDE9D1104ECB7B23D47C06FB10CA3B2AD5388E92B405D4B664C10CE9DC39DBE6DCC26434684BA4C03FA6E3798FD72AFE97CDA887845E3B8B73D7F02F1AE3D75961F855BBA1A5F3AB166BB52F8745D644AD1D2EF9D605B47F1
	7D7EDC688DF81E117BE2CF8781592E86C30A2A0DB0AC7D721847224F186EB5A782D66E73D3AC7748244B31B56AB4BA578F0D6BFD03612F2778F1831E2DB7367761FAA38D72560C639D4B36B1B2E7EA3F12D76683C88518833090E04595346505907946AB61391DAD9832AA030A4F303D417433442C4D1381730B3BB7623CFE7E023F13F2BE5192772E711AAB4C9F4ABFE44F6A07EFDECF1ECC3F7CF1FADF82723AG4681E6834C85D874A16AF7FB085FDDA76E5024E9325DCD3FB89587529EE5C8C7138DBABED7C9
	58BF0C7D1B6EEB813F559F6AE3FEA13C3B602529987B782BD7C9DCA39735795EBD9B3354811E4E246940004F47FFB7CBB1761D5AB1EB5CFB7D25D7793A6EEA74BA74E9B76E2735C2DE4AD5DC07EEADA33F45E17A9BE09F0C60541C1F6DB50B35E84ED5DC0B7653756DDAC817266415D52560BDDFD92CA16BD3F08DF9E3DF749AF2169431866C0BDEC32E0F48FF32AFFA8D7919087C1F7A235710D81E59B5AD903BEFFB2B38F79ECAE17B36E9701C13426E1330765F98CA567EB85FA34F79D0F6F10A5E7E79A95163
	5AD7B5B2466097F274C20F487B6C6FC99EFE599719E79BFF4E10E73CC44F721B5EFE4ED0EFEC7EFEFA46FCB472FBA43233C959B01ADF6D2DB470B027C95FC17A687B1DCAC8D6E56031CFE25F857D46ED76DD506A94710D2657AC3FD0647F2553183FF59A4B2F1F0663DD4E07123045EAA7715BB8EB77FDE46DFE30F6B77D1D99726DB95DCB24FFEB5FB543317AFFFED512B0AE7D309C63524E4F5FAE35BC54E2F9B9CE1F33CE635F2CCEB3FCB329D6717D068B6BF519974893111951167492DFF24A3EB019FF303A
	0A0A743A1EF6FF747A3FCA428E6755D62273CA1990AFC63B4CED141B6126FBE37B5DAD62DB016553D83FDB83CF699A764D9D6BF7967FE4666B15D036669A3D5FB5DE935D6FC29BC9B97C4E9ABFDEE0F9CF3E3AD9925F770A6F9AD85EBF0C10FEE57C8EB1326C826AB3A2D312CC6C0EF61D37C1372B5503F11F65BD30D14F6B33E09E0B27A9C5F82F428AFA452F7FBFA80A15496C47B22957E09F7A5DA61D765153BA3FEF76A6EB63FDF53BF93971AE3D5CE6A526DCF35097D4214C7E9A8A878FE039DB4F6965EE1B
	903F78FA5EF7DEFF7F79853D5E6BA721C745C53D5C1E24FDD3AFB7FAC9AFF7D3EC9C0A2B7F0FAE6B65861246AF1DDF092C6BDA5DBB59BA587C68C1DA0E7FA64F8879ADAD53B9B34C025E073A583735B659A54A3CB8CA7A0EB83BE2D3E91E37EB1444ED715DAF7DE98F7619B77FDE290F70E66FB38D6F3FCBF80772FF8A0C477E0F1587533FE3AC7E996239B173B3444BE366E7084AD862FB187E31783F5F250D99FF3F0B357B0F6C24ED96C27FDF81D08830F5BA445769B8A7360C7FD122F3E2137970B1070B5C65
	A03FCE29095F0E9C0F2F6B1A71643FE9790BF19200FA0390BF7D24FE44BFBDCE5ACB8B06A8126124AB44C62A47655698A947EF38C2777494A7C5CFEE0C231F67F25B7C51C9E2EC233045CDD77E982FDFG12FC13433C9AFDEBFD52B51DED3A59DD6BADC94B3DE46A4B2DD3DCC3BECA6768216951EB4847A9FEFCFA749AF2C81CD925C15E1A6938C7686C1BACFFD65F3782D8DFFBAA4C2FD3F10D30A508EB04C529381EB8C2FF5F23G52DBE76415D54FB09F6B620F8BE9A4E471716FAD71BDDA638C64F93397F143
	B3F08DB4CB0BB80BB35C2F4599AEAB4B76CE689E488CDCD78F52BD455510AE7E84488B5088E083708124G4C86188B3088E085409AG695A3C328A00DDG75G56EB51875F2267DE71FC109F9052CE294A9E52AD390D3F61C59B77C0DD9957223E1BFCE863E557220D6B69EF8D1526413C9B20BEED32B66EB0447099C5C61FE8073A9CE909770FD939BBA23E379B06B269C94AB17B7CA441FD481F5B13471F276CEC6F5F2558C6C27CDE59EF6CE4D296F4BA430A08F3450E2D825796EE8F06BDE4D4E3F92FBA89FD
	184782F6D7F651F6B2FF7484484A2A197DBA2F5075279E5DDE6B8C6FD7BC9F7F7877297F859E5F0E3937B231F27FB645F295C93F41B50F03DF2431A5B3ADBA8EAE20786C3468B8589C44BE1583F9BB2F43B8F848B48E27DC477CF7B6BC67DEE79677BFEB3AC7361053AE0367A326656EF3A65237603A58FE437C747A84F71BE639137715DB5C66E3F526BBF62C3E43EDBED6FF5EED9C2B793730F40D6802D856A5686E9238F382370056C9F0E7BD8E4DAD700B28DC845F2A9006083F074AB5C4F1DD9C374400BB07
	3F37CA6F6F014A6E20AA6E1453BDB7274767E3696FD19ACFG3A3F304777290C1E10EC107BE101BBA75AEB712477C2D9396FCE315C16243FA1E15E9F3269B8D0FEFDF4FF380B62AB2F0F6E8F6F7830BF6C023C346B719CF28EDD1B2E61747C8E9445955FE044AD6C0F253B3B5FC8473F892477F5587BA243FBDB70630FE53D9FAB8F26F3871D17D4014E35B7208D3F225F0B93385B7B709ED43A405D6545321982773CB8874E96383F71E059C201FBD43FAF150E316CCBBE5D3EDFB33C3778BEFEDBDF7CD8F9705F
	10FA1578ACDF1B52B04270BB07B76BF88CA3FCDD6D71C41EBB5E65A5F6E1FBD277A5BD7FCFF45E57139EFDFE1BC75B58877861F45C23D96153796D71A5631768ECAEAB1D1DEB6A7C6E27FEB18F708B53F11F2DB06979C609246FF1C773D88D75ECABB61E89BF6D17443BEC38FBF74CBB713D125410DE47CF8CF736FE5DB771BAB65333FF767F3F5C56FAA7D4107D18D492D62567E277CB1A7E62FB8FE5DFEE2930C816B6E9031BBAC41BD4B2C4BCE698E431002B2EDF976E17689EB4CBE9D27989C589D5F5A87E60
	20625024D21A7C33B284B9AD066DC2CDAAEB525FA5780795C964D63E83C45AA2C4EA23E34E2A41838DC58F5FDEE4110AC19EB68637C945E4A08531D4A6DE910C41C4B829188379AB092ABBB6C5C6CF5C99DC9F27842131C87ADF09409A7F0A8436588889309277AA22733311CD387E0E5FCE5FFA6301810BD47A1E3441A97BC2EE99F4253F29E5151E6925CAABAA50EA52D67AC3F0EFC259B7E959C3D649E28FEB528BBA5ECE70AC37B3A45B95A0A9F3482A1756C0C4F97E02D466D4DD3D1401A6D9AE3CF7815852
	94492E34CA1BB4B759G2111C07A2DDEF5BBD9FA710CC33481245FB81DB70768DF141AC6D360750A92931A48540E8955C99AF0C3CC33C752C47CCD868BCE0BC7C58D700EC7C28DB19A15886EE6D2231C9323AC669AD958BB7FDD332B46734F0299763877F03BE73139F0E8D6745D01FBCE900F4F867C62D93856FE7E8479BF9B626D03F67AE51FCF13FD5E2600036C099E9D5194FD9FEE4989D2FF98E9F8A64B4BBBC1646EB31479EFD0CB878825D7295CCFA1GG60EAGGD0CB818294G94G88G88GC0FBB0
	B625D7295CCFA1GG60EAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG09A1GGGG
**end of data**/
}
}