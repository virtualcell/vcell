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
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.event.CellEditorListener;
import java.util.EventObject;
import javax.swing.JTree;
import java.awt.Component;
import javax.swing.tree.TreeNode;
/**
 * Insert the type's description here.
 * Creation date: (4/12/2004 2:44:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ModelNameScopeDisplayPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjNameScopeDisplayTreePanel = null;
	private javax.swing.JTree ivjNameScopeTree = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private ModelNameScopeDisplayTreeModel ivjmodelNameScopeDisplayTreeModel1 = null;
	private ModelNameScopeDisplayCellRenderer ivjnameScopeDisplayCellRenderer1 = null;
	private cbit.vcell.model.Model fieldModel = null;

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.TreeExpansionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ModelNameScopeDisplayPanel.this && (evt.getPropertyName().equals("model"))) 
				connEtoM1(evt);
			if (evt.getSource() == ModelNameScopeDisplayPanel.this.getmodelNameScopeDisplayTreeModel1() && (evt.getPropertyName().equals("model"))) 
				connEtoM2(evt);
			if (evt.getSource() == ModelNameScopeDisplayPanel.this.getmodelNameScopeDisplayTreeModel1() && (evt.getPropertyName().equals("model"))) 
				connEtoM3(evt);
		};
		public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {};
		public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
			if (event.getSource() == ModelNameScopeDisplayPanel.this.getNameScopeTree()) 
				connEtoC1(event);
		};
	};

/**
 * NameScopeDisplayPanel constructor comment.
 */
public ModelNameScopeDisplayPanel() {
	super();
	initialize();
}

/**
 * NameScopeDisplayPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ModelNameScopeDisplayPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * NameScopeDisplayPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ModelNameScopeDisplayPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * NameScopeDisplayPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ModelNameScopeDisplayPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (NameScopeTree.treeExpanded.treeExpanded(javax.swing.event.TreeExpansionEvent) --> NameScopeDisplayPanel.nameScopeTree_TreeExpanded(Ljavax.swing.event.TreeExpansionEvent;)V)
 * @param arg1 javax.swing.event.TreeExpansionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.TreeExpansionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nameScopeTree_TreeExpanded(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (NameScopeDisplayPanel.initialize() --> NameScopeDisplayPanel.addTreeMouseListener()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.setTableCellEditable();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (ModelNameScopeDisplayPanel.model --> modelNameScopeDisplayTreeModel1.model)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmodelNameScopeDisplayTreeModel1().setModel(this.getModel());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (modelNameScopeDisplayTreeModel1.model --> modelNameScopeDisplayTreeModel1.refreshTree()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmodelNameScopeDisplayTreeModel1().refreshTree();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (modelNameScopeDisplayTreeModel1.model --> nameScopeDisplayCellRenderer1.tableHash)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getnameScopeDisplayCellRenderer1().setTableHash(getmodelNameScopeDisplayTreeModel1().getTableHash());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (nameScopeDisplayTreeModel.this <--> NameScopeTree.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getNameScopeTree().setModel(getmodelNameScopeDisplayTreeModel1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (nameScopeDisplayCellRenderer1.this <--> NameScopeTree.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getNameScopeTree().setCellRenderer(getnameScopeDisplayCellRenderer1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
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
			getJScrollPane1().setViewportView(getNameScopeDisplayTreePanel());
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
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}


/**
 * Return the modelNameScopeDisplayTreeModel1 property value.
 * @return cbit.vcell.namescope.ModelNameScopeDisplayTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ModelNameScopeDisplayTreeModel getmodelNameScopeDisplayTreeModel1() {
	if (ivjmodelNameScopeDisplayTreeModel1 == null) {
		try {
			ivjmodelNameScopeDisplayTreeModel1 = new cbit.vcell.namescope.ModelNameScopeDisplayTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmodelNameScopeDisplayTreeModel1;
}


/**
 * Return the nameScopeDisplayCellRenderer1 property value.
 * @return cbit.gui.NameScopeDisplayCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ModelNameScopeDisplayCellRenderer getnameScopeDisplayCellRenderer1() {
	if (ivjnameScopeDisplayCellRenderer1 == null) {
		try {
			ivjnameScopeDisplayCellRenderer1 = new cbit.vcell.namescope.ModelNameScopeDisplayCellRenderer();
			ivjnameScopeDisplayCellRenderer1.setName("nameScopeDisplayCellRenderer1");
			ivjnameScopeDisplayCellRenderer1.setText("modelNameScopeDisplayCellRenderer1");
			ivjnameScopeDisplayCellRenderer1.setBounds(580, 148, 239, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjnameScopeDisplayCellRenderer1;
}

/**
 * Return the NameScopeDisplayTreePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getNameScopeDisplayTreePanel() {
	if (ivjNameScopeDisplayTreePanel == null) {
		try {
			ivjNameScopeDisplayTreePanel = new javax.swing.JPanel();
			ivjNameScopeDisplayTreePanel.setName("NameScopeDisplayTreePanel");
			ivjNameScopeDisplayTreePanel.setLayout(new java.awt.GridBagLayout());
			ivjNameScopeDisplayTreePanel.setBounds(0, 0, 536, 645);

			java.awt.GridBagConstraints constraintsNameScopeTree = new java.awt.GridBagConstraints();
			constraintsNameScopeTree.gridx = 0; constraintsNameScopeTree.gridy = 0;
			constraintsNameScopeTree.fill = java.awt.GridBagConstraints.BOTH;
			constraintsNameScopeTree.weightx = 1.0;
			constraintsNameScopeTree.weighty = 1.0;
			constraintsNameScopeTree.insets = new java.awt.Insets(4, 4, 4, 4);
			getNameScopeDisplayTreePanel().add(getNameScopeTree(), constraintsNameScopeTree);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameScopeDisplayTreePanel;
}

/**
 * Return the NameScopeTree property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getNameScopeTree() {
	if (ivjNameScopeTree == null) {
		try {
			ivjNameScopeTree = new javax.swing.JTree();
			ivjNameScopeTree.setName("NameScopeTree");
			ivjNameScopeTree.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameScopeTree;
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
	getNameScopeTree().addTreeExpansionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getmodelNameScopeDisplayTreeModel1().addPropertyChangeListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("NameScopeDisplayPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(531, 647);
		add(getJScrollPane1(), "Center");
		initConnections();
		connEtoC2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ModelNameScopeDisplayPanel aModelNameScopeDisplayPanel;
		aModelNameScopeDisplayPanel = new ModelNameScopeDisplayPanel();
		frame.setContentPane(aModelNameScopeDisplayPanel);
		frame.setSize(aModelNameScopeDisplayPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void nameScopeTree_TreeExpanded(javax.swing.event.TreeExpansionEvent treeExpansionEvent) {
	System.out.println(treeExpansionEvent.getPath());
	// if ( ((TreeNode)treeExpansionEvent.getPath().getLastPathComponent()).g
}


/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


/**
 * Comment
 */
public void setTableCellEditable() {
	DefaultTreeCellEditor editor = new DefaultTreeCellEditor(
	getNameScopeTree(),
	getnameScopeDisplayCellRenderer1(),
	new TreeCellEditor() {
	/** Not implemented. */
		public void addCellEditorListener(CellEditorListener l) {}
		public void cancelCellEditing() {
			System.out.println("\n\nCancel!");
			Component comp = getnameScopeDisplayCellRenderer1().getTreeCellRendererComponent(
				getNameScopeTree(),
				getNameScopeTree().getLastSelectedPathComponent(),
				true,
				true,
				true,
				0,
				true);
			System.out.println(comp);
			org.vcell.util.gui.JTableFixed jTable = ((NameScopeParametersPanel)comp).getAJTable();
			jTable.editingCanceled(null);
			// jTable.editingStopped(null);
			jTable.clearSelection();
			
			}
		public Object getCellEditorValue() {return null;}
		public boolean isCellEditable(EventObject anEvent) {return true;}
		public void removeCellEditorListener(CellEditorListener l) {}
		public boolean shouldSelectCell(EventObject anEvent) {return true;}
		public boolean stopCellEditing() {System.out.println("\n\nStop!"); return true;}
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected,
				boolean expanded,
				boolean leaf, int row) {
				Component comp = getnameScopeDisplayCellRenderer1().getTreeCellRendererComponent(
				getNameScopeTree(),
				getNameScopeTree().getLastSelectedPathComponent(),
				true,
				true,
				true,
				0,
				true);
			System.out.println(comp);
			comp.requestFocus();
			return comp;
			}
		}
	);       
	getNameScopeTree().setCellEditor(editor);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G650171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD894C716EA70AF99958DAA9A93D1C9425DE20E3DA35F71F9FCB9EFBF77BBEF63E54DAE174D6E71ED484564CC564B72E5C9AEFB47EE1A81858595836A2CA2E0E2A23008C0CCB4289B915CC0444805D90237FC2E8D5340C0B3BD74744020A8772ADED7CF4F504D2037617B5ED7D32FDED53D776A3DD72F2A8B2E30A4EAFB06351063B236F36A5F698C0ECBBB43F1FB4A3746B0DC382BBD17537D9D85
	58479DF5E5C19985ED310F356766F31557F3209E8F75B2289F045F85DC4D753E537043E41E8B5066BD7C66EB134F7356EDB6CFB62D3F591787659E00844011B2CBA2736FF65AD4FC320AE710E165382D14264CD924621A20FE83A09AA0C1C27A4DD06E24B34F2CBB2352F5AE0C61523EA7DBF713F1F4E3B228F6E3DF3BDA16F27F15116EA6F37D0FB3CF6DE3017A86GA2FCA617FC6DE3A84F358E747E2E3B4B29D48DF78B22D865628785CF3764966A6A2EC88EC17C926AED245E687438C5FE3415F789A274738D
	113117B9F368D820C328E7498437F9104C178875A400ED9A7E2F5D845F843FEFG78E4A663796367D499BAEE7DAD3725773BBB771A4898AE23EC8932DF46520A2E4C5F507578E41C68BD8D5AEC1E765CCA00D6GB7C0B840E1EA87676FD4C0D95F25B4C80303122B5E6D3EA8BAE4AAA87C2E2B039695771550AD498E0E43357E5AD9E9404FE4007D043B6E07BA0979082DFB6CD7CFF049FF33441AED20136418DB56988F6AE23D472F931C7B915339D3BC01F699B877571CFEEE6E555A6C6D86F397BDD71B1D260E
	132378673E201035EEF41135AE035F992AAE905FCA71E1BABC5A68F354E6C8FD8534F1DEE69B9DFB98AFADD92B397C1A2506FA087C0E456A70A28FE35EE0DEAE8E1131E33CB867BE0D17B60ACF5261111784EA27244E8D3767DAG503EBE4BA0BEEB9BE94FBD85508260G88830882089FE1B6B66B4E49A95AD8ABAFAB973DCE51A1489C767DEC7CG1402CF1179E617C761DD5DC2BBAFBB79AED170F0AACD53B851C1D7C03FAB54763BE10E4F8517A0730AD3F24168603B0AA0FB1026FD1C4854AD8B34556E84BF
	D778C1B747463DBEDEG2548FB14BF389D3CA278A9303DE71C2C49B05F8EA342G709B75A2DA299EC17EEDG691A9EDE2463DD96E48898F5F5AD12B460F55B09A4DC0B0F6879C99ABB7261F7190F6938AF136042219E63E3635838F7F563B4BA3B897B3CBC4AFC6C8BEAD309C01F8210B74AEC67758EE6BB0D3B57F2157F325850F642410E4F0C224D340E867BD0BB5AB10C6B033648D1967B1387BBCCFCABEF54583788EF044729470EB7CD6748B641E3DC1FDA1CC1FDFFD9C064CBG1E13G52C6194E5BC08754
	06AF729E01D96DD5998CCEF078D7G79EC9AA234C467FBD59E485817815E87A07B55E9CD26EB14B66FA67890C0A7C098C0ACC092C0BA4031DBCC47CF3927854946E254E5F55C3DA6F285635E1F22CECE1B2E450B813CB07D93DD676A64B60A2741385DD364E51F818E5726B9034CA78C8BAE25D1F08BAE877C7028ED2D99E4CEB5FE74C8DA1FF60AF7C33CE9G8777E8784E8CB28F2FA832334B2B88ED0222B8DD3D7EF6871D4B636DF27BC3951BEB00A6D15288449B0D5E1DC1FC35DBF23984EAA4DA9735DD2631
	C29005C1E03AC1723A94447B2CA4379A90C655D8B5E65D4111D8A3FAC95CC33B0E63092E5D32F390CC0F04CEEE45AD92C7B6E979965DA7GC772AEB714E137D8AC98F91F4502E3B77F02DB7FA73755E8CF0BFCF401F54BAD745137EF79E38172B418E94C3B0746AC956FC1DE976D598F25D96E14C97BC5F3C720F48551B4C0927695D9D4D910EBB45A83ADA5D08E9AA5E4F6D9906866D2237A746CC24227ED8CF2A00046B16653BF27F9920B232AB3B7C82208CB85ED89E3C41F8F398AF5BA699CC3FDC6C3096D33
	5C7AF66EB6367B6D53DE10CFECC698728A30FD1100FFD9DB57BF963C9B54562E2EB136F79710B596F5FB450A5BE44E05C3DA3E2C4EF7C3ADFD37F1EE243BACB33A2DD0676E3467165EC1BA718E52255FA1F46FFA98DD629D0C6DC9F710FE8BC031C677EF9ADD53B846FFC6F7E39C696247895DDA0551A963B81FC5250F9A47F9116E361751F1537C6D2434CD5353558CB33AD3BABA42E70BCA97B30D507DCA23F3CC4378B9265207C3D945B15F79C5B971911A6B7773433C2F4AB382FEDED77785B5C94C65BF694F
	D1695326311C3D0B66E51553410EGBA27B3FFFAF6B74B59A386E23864D7EB324D4EA54AF464B7F23A5F1F62FFDE3F83ED75597DE44E6721ED6BF4E627BFAD50678D759E0F546D24A90BEAA3738A757E0638C5053B206CF14A9E2599FC47476CA9321070CBF29A952D6AA3AD6B9CF777E79F3DEF224EFE60E2BACB57F4F37B77FA5DA46E3DED4DB5514D29996A19ED06DFB747866D5948E3367869BD702805830E65336BE81ECB460C00F9B681E0CCB9AA933C5365D41C3C683CA91415633CBF39FA107A28C29D10
	48FC4961D4486F3272F6750C303A6002AA4326A7D6F367462FDB0D4EECF37676D9336EC359AA6F734B583428AB3393G79D3E4461F8FE8626EE3F166950FBE4EEC85FCDED8FBEEF598637FDF654F08FD013C17FCEE9ED645D1252F14698C3D0A6E16D5F656CC7257CB84D52E5A432E6A86FDACA604FF326B87EB194AE8E7C1BFA853D9CF1E9A3B9B015FB10D67559E324FA9FDCE8F563FC7693AF5F6057AFD23DE2F5F04EC0B21FE7B9E0F34460721DE13437C7ABD352AAB33E99553EF2E42F8291C89E716190C17
	A58A11FDB040D61ED358DA2CG3AA40D763CD7CF0B381EG9C723D36D84FF78EBF5310EF4B2B3D563CD9486F29D9FE3E6340A6B6E8F671181771BD86B47133583DC5582704DE96FAE44153C7960118254A7F7F8E33BE5B007E58EC467F4B4304BFEA44CD3C27CF13F75BA8236F845A0859EC0E425F1112669F1A3C424D60B1D06631EBCC66E91B62B9DB41BCC31929CEE6232CE9337DB20FC5F5E50683E01EF936BF784C5EB847FFE66818B3717C00E7D9BCBF0BF370AC1B2961D3A8FECC07C7397B86195C96E8CB
	1D434EDF51B632CF5867C27E84B087A08AE0BDC04ADC961FCB77FF4405B85FB63B6058696245360044D10D494F6DA773C22E2756FF32DF2FEB54430FDCC6F79769F3D17E7C7BA76A8179FF7790099947207D82C087009BA07CFE46BBFFA0A46F17FC063CA3DF6B0702790A8B62E5D3D09D7BBDB321DFD5D0BDB5281FFA9EBECC76771C874016814E81F4G0C81C482A4BE40640ABBF0BE24DCA4C259258640B5C1FE55901C23EB3C5E146FCD46B9937A958755139F886C276675F6325F9864A64437559C7F28BBB8
	47CF0331EC9648F181F0AFBE22303D3851424E10751ED077EF4632DC8D59EF4CE234DEAF9411750A0036B8000DGDBGF266417A81B44DE36B15F970E228752AE707B23B247DB4D8B464F71FF2EFF216046CF7E31ED13F64897D26E6A32FCD6897ECA33EF9C6FA3597933D06C3DBAC40860094002C79105F83B44EE7FA7D434133D37403FA2A46098AC5BEE33D77264FF5A17BB9669B753BB1FCEF7339C2769BB31C6F0D51FB1B0F3B992ADF44FC2375CBAAA16B978BED9B81D2G52495AAD001886F0E381DB3F47
	C37B05F62DB939DF609D63D774CC630371ADG08BBA992DC9C54B7AE705F67113CEC1BD6FF0556C90CD175ECFA97F7B7C046B742639C337FACF3185D7F21FE575A4C6F0E62ED46F7C76BEC46F7C76BED7B908FE368EF32D97BC65B64F7CB1BEDC6F7CB38C62F53F3D5D6F8FBEEE1B8DB37B6092DDBE9B83317C3074C65B9FA48D81E63070C65B9F1E8F2F9BEBEE4A64F912A77FFBA30177677F8848F8DB20D3C422BED7F2E107B208977E2647046317C72BF07F62AB417942921D61BD7BEECAE6368E1E399478F13
	7338C7724A5DC2309C338E5B34B63F63B4AB42A0E187E996507E8AAF778A937430047647B6337EAB8F1B698AE57D362C17155CF361BEFF50255F679B43715B009B4A30F0364FDFF333FDFEC5B83B2728216728A428EFA338054CEF5F1BF4FFAB823A6A0501BE097C79BC0C3FD6C5EA752F452BC566EB11DAE43C96EF95192F452F0AC22F450EA366EB317BC86835B8F0045CBFF4B99DCED93D69C3991B943D0C7E3B46822FEEED2E54E8F3DDAFB617DDA8B616FD2058DCF639B8346C370A4DE51FD19CDA76870AA7
	37431782E42D5570CB0775780FB4FC794DDC26339ED99A842D29F7D6A5663AF93944D8B7BFABB1574D9BA521F5735FA5663A31160656CDEE2931DDB87B495D3B480FCADE85626DC262DF537A19DFB6C23D93A08CA0F2A17AA766715F9EA16375406E8CAAEEF38B5D1CE591697BFB1175FD9B6873GD7895082A0AEE267730FF7917F7D222DDB16C4115CC956A0FE3BC46FCBC0F9C90B481B0C1263D0D5476D1758383687E1CC0096G914072A063297792093F86E78B7FCC9CFB4BF055455EB244BE48620AA8913E
	36C0BD9D20B00245156B6E6038D299610FA397A24C4E690F52B8E50F88BE277F9545BBA2024F692735FBA4855A1E0FE0FB61278A5B8B4FADE6383DB42F31C0BD8AG7B477433379F71005B36186965152A43FA3DD471A3CADD5DC509FC93E84105C71D24527C0974BF36046924F8000C5989F5854032045DFB7ABEE4771E310F2D66B63FECFCA71CE23FED0DDE027A5938C4F7E7D5511509F30AA21BB3EBA948371460160B7A3BC0220F3D5B718C0D6D1B825A094F17F37A76DF873493FE0FE7695B2B87756D5BF5
	7DD13627BFE03225BF9D4709CF06193E4389DB2A5E4F2BE569B34BB272A3DBF760B85F7F237EBDCF7EF2511AE1B2CE0A5AFF5BD23F0ED0BF619253CFE9A4534FD3D230FE0E86481FAACD50CF063E7DBDF730FEDE88E8373B0375B3D7559F7A035D703E06C1C72419BF24D07F92A3037D61B50A9F0B8C7687D9526E2D20ADB512593E17668476E56D39FD4B98AEED087908E599735BEF517B57B82D7EA42D136FB9D8BF6F894E41896D7FB7A78F4C735BA74CB155EF76D3014265741D0D522856B799704FECAED305
	9420C983582F56F316235E99CD8177A175C9C17D8EA052B94E0BACDEF6AEE77AFBE2427D6554F412F80F7D5E3C6BFEE8DF29B406456964C47CAFA9BED107C73BBBB6406268C6E82BFC085D17BEDFC67620F0284782ECGC886C887302DE0E7141E6355DC08B32DDDF237C072A06A0E9DA85F10F8EF6F477EA11B452B78F5FF49A54CEAB47D86D539826D23C5AD93B6AD4BA0A54AB7D0C1649B0336C800C400A4002DG5C43CC3EA70ED722FC64B3919C49796EBE7AE50E89576E94C67CE81298605873FFCBB845B1
	520EFFGE5BB5F2FEA078CA549EC04AFE00B3A5A558FD90A1D6F5556FE31F4373A4138B060E2F12170E16D3CCF730C8BDABD15564977A62C7B243BBD1B13316E4E16975E759CF793B7500FAD6E09395D9A6DEE358FF81E8690831078B0FEEF43DCABD7E6B4C9005F8A10738864FEGCD0F309C6E038132A7FE297F0646315CEA3AF6B75B8774A5149E628C477ED2BFB342E14ED7F002536C7A45BCE657C55FDA43B57E28517A3E09DDC7BD027ACFFD443FFF06F51E5A093C2E2377BB0A6C95F05C28917D3EFCE657
	8C4377A1844AD662F8675452724EB2752D58DB831C36166F68FED78C7CB96EF5A6371D06EFF48E75BDDF197999E7EB1971996775B2633B163432496F06524B4C5F9DE5166B5F9DE1AC992AA2790982EB928E908F3091A0F9A50BD18B4B5F63E80CB208CF179C3D640CC5DEDBAAECFE2542DC56DB95215FE806D5107788DD1222C803E4FC063F3F022C17A239A9D23DEB29A0BA121D3DFD8A122A7845954497C42107227322089F4DF015CE32CEEF2BB609B67EE39AE70D5E8730F7C83837BD6DE6FB1B9D6630C431
	B3C24D8DF2E79BF4C678DCF6BAAE723DF8C8C05FAE98E2796806A8F6C62827EF8E5221DE388A764BD56C6E26C6563E512CE2392BD38E4EDDF383F27B0764605C154C65EF7FE7B9B8F72D89486D5F1103F3D7569FFDE7D6409D519870953EEA72580DB6777D13A4470A865A75GDBG528172D7C3DC84B887508A60G988308G0881C884588C1082108610330659E84789F266B05C838129AA1E9A2A4CBB09292AFCFCDCC3F4DA84631C5146DAF96237130C55AA03BD484AA8E2698BACF61FD8E5EE77532AC25B7D
	5CAA4C93FB857B28DB3DE718DFC5769C292747A3E87E75E0D53E06EB7634CA9EA73900C03BDD69A2748D8279064C627E08200F7BC5A5972C4C8E025726E38D2EDD589AFF5C774749E7A71C5B2796DFDF3847FE36895FFDC3816621CFD27FC9D8931C1FBEC5719B568467271F8FB19F5E8CED2722D95E72C940B759C84047C6CFEE5B18A3BDCC776B7551B88F62D6D35C73AA8E577125136676725A495076720B1346F17217A7A74649DF1FB40E13D9557AB809381D55444FFB41E7BAB43ABD9A6E1A06AB285E2962
	3EF6BA14BE8D7F1B6ADDAA3EC9A093B2FCC9F5B635C6026BD0F147F438EBAA2E3C1A58B0085CA7492CEFD5B549B9FA1C220875DACA63F4018BE87AB8CDF9F43AF99D5F67B41C1F6F3A9A32D65A7E09AE2C36555794F23AF7DD7469DC9B903AFAB1DE2C2DAD05B28D56B17FD130950096009BGBEG8BC07423AC0E7CE3CD99E79EC77042489F1250667EEE08536CFF036E771521EFBE8F6A79C667C0BF38A2B35FB81B21FDE71419EF7C0CE36F9A788FF56F6B28F9A13E48375B0FE766252EDDC22D39BF7DFDEDE8
	FF5AD8EB6CCF4F54CE74279F569A7B530FEBA77A53CF55F1E56D239562375492D970BCF105F7713DB4A7437F397B0EB882E56CFFF05B956EBD43274B81EF1DBDB237176599D54D54ABD206BF2A4BFCE9CD26154BEA66F620D44DDC96E12D195BCEB9EF66B2C1AA9F79AF162CG40FD23FCDC1F573FBD335BB42FCFFDCC7D9F3E477D7B867A0B0D5E37DDGFC67634CAF7ADCA74C7C82B24A36C1DE9495DE9C28F7B94859756228A278F35BA392995F43E854C6B516C8046FFEDD1F7F83D0CB87887A17E371D393G
	G9CB9GGD0CB818294G94G88G88G650171B47A17E371D393GG9CB9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0D93GGGG
**end of data**/
}
}