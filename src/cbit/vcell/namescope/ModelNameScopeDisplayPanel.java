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
	D0CB838494G88G88GE4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD8D4571536552D5B25DFEDCB36185256CDE8CADA5AE5B7662B5B75EBCD3FE6EBB6FC0D19622E3638B19B32B52D5D38DD1A4F36EC774EB088E3B01AC8925A58C89AD2B592820A9111A8BF2381C5A5B2D1D492FDB08F98F94C9B5E3C61CF14BD677E3CF9B33C47A85B70FD673BF34FBD775EF34EBD675CF36F3B10521D0B8A1D2ED2C21C05047F9DF49212D7C7487ACD8F2588DC5DAE0707187EDE81
	F8169C39680632955AFC179D1EED246DC4B1543B213E62830767C5783D1D1CBEB1F090FE584C5388ED5BAFAC14E71EE72AD74CD3C46BDD03A5D0BE83F085F0D8E66C4279C303E59CBF176385B8DD04AC580DB4FFB5380363B6GCDB1605A812E6CE674E3D0B286665557F13A6ECF27133CFF56DCDBF09C531882D6713E793CACA73FF6266D413996D3BE31CFAD5495G943E0064DCB8GE5C347E07F115E1E00DEB552ABABCAD5D09A12433DEAC82E2FBF257AE565B454BB316E8D04C30AB45EA185E58547FF9D47
	4E9FAC26E391328E464D20380B63B81F9FFECF82A43FAE708D13085FG3F0B77BABC65FB050CEF6FEA60B2F4DD7BA6495EB7645EEAA3E35DDEA65B153DD199F3865738FEC75763BBFE54FBAA34AD8348865888D0D6697054813CC46D601FFADF073229C7EFD1070654E0D3A8542C78B5AAA87C2E2F07960EBBAB772A1A1F9036565F9A2C3460E7A6E07D165FF2BF2613747DE25DB34EFE03645C73572EA28B1D6424DFF3F5D4B2DD8CD4C6F54266F6AAF6F34F2F0C354B58394F93735CE3FF3B322050E26E645415
	8529FC1CCC535C6BAAF12DFFC0EDE0857CF6F2DDB07C0FA83E720DA81E5968950A473A975A525F903651750C60255DFD8759D673B9CBBD24FCB749D5D245F828280A67A50B5ADDC7951B73D90317B594BFE942B3DE1AA99E6B6950D67B2643436CEBF99F7A6CB22867G648114D4BBBC15G2DG5D554246367A5F3CC99B6B10B43DB992D07C32C6D85F657E97201447F4CDEA8B06F5A958AB1F133400542348E142E99E76238EFAE27A7D8B357DDE18639DB9A8EB129ED003B0BA782EAEEBE1C673089FE56A55E4
	5A6A8B001F6B52D00808F1FF625F8E25A2057577C2FEC91723942C7DC9BF2E4908F48EC6048160B7534BAF7B299EC17EE400B4C38FD7E59C6F242CC140282FEFD75541C8480712103CEA54F3190231E3897CDED1ADF47CDF83082B2BF1F8BAEA44B8FFA3BFE79A479B68C576A5EDDC78D8B60BB7C0BF9FE0F10D301D7F6C9236635D72F5D23972B6CB5BA97E424A0235B54CE6B656447B50DAE64740DF79BE0727F11F087DFD63DDB63E3538465A3710B73FCD1BF56C18321D2348864F627A4D4599266F4BD7D13E
	ABA057A4C06ABE21739C391C59F033941605551E514060E4FFF485981F0EC924C51DEF65BC6058A781DE85407D6A20A153E314F6B14C1383605E8F7E8360831884C883485AAFF45CBAB9A7CEB69123CE72F1375A48950FFB75A6F5F250F6AD86E3F8917AC7DDBFED125BAA1E4663365CA4AF4FDA60585AB87AF0BEF9C48E6ADEB9A4877D70A34C5BD677611CBCFE7429C61FC7A8BE847126859CBCEC609F6F43F9A4DD5782BD91DD6E14F5BD906C0F367F1C4E950E74042221CA4C155707B22B2390EF8C7ADF7521
	2F762A4120CC0D4468425BFFC3E3052C48C340F40B9A896A8C6F9C405CEAD09E67312AF0E09341D823C4B06EB13B4E14D057A1ADB08426072113F8EBB10E3C291419F414877603F9D79934D5560AD8B07A2A08859593F713E517C3AE2BBDAD654B1FF2A555B29FDDD49B0D850C2727862CF94F27B10B63430C574FBC73BC14F639D3816D17C67EGE5B00E268512303338281A2C559834AF346F04F248AAA173E932CCB7179A6E53A9B45FDB867CE7832C2F95BEBD9620FE4963A8F76696D5D158D2C15B1583284F
	57A6CACDBAF19FE07AEC071235EF1BB43727737628FD4AD737214D4843919936AF8C78A70DF5D52EBE9757F60E2FB1EB572F629AAB263D427B964E7952F5A3DF6673953F454A7237585C0CEE25C137G6A69G4BB9DD69C1C617F69069D2EF88BA7FC1965BA78FB27AA4A8DFB66814A9E35E03AC7E8B3A62BAC657DD07F48709205BD147662B2DE3742DF5ECDEC6D76494F46926F6AC1745507D23C117E322C3BE73B8DD47A1247B38CB50159CE271336290232F03320A885F796FBD68A3B4573FAA0DC8E3D561D1
	70737A7A9354A4D9AE3F106E47C81FFAC8646C772BE8CB59D0DF8F602E977E7470961133A78F2693DFE6F6015D39E4C7BD633731BE6ACF3B13C68B182D7EBB1DF38E34AD2897F67A64D5F35E50948E2B3D811A32F09B796220595F986E4E414DD0768534305E863EB3A66C696EC16497F39A0E667A48F3B710DBBF7B18FD13692CFBC268AC4D504D7563E65DAC5DFA5D75340DEEF238CE72CD3A29CBFA2A0071D8249C1A850FC6B8A0A21F75DEAF61BCB69CF6F8F4G96D33EC67101E0C08FC8CAE0C25E3D074D7B
	23B3AFD29F552983224C477C819DFF6F5EF30E1F916E585E48E5487A469D24E16A83175519ED7E5681D746E1A6DB7661280CAB9CEBDC39G0C3F5F9A7C15B7B8BCDD8DA24E340D18634C8240AF86D855A07817EF9C05D287F90F0D05A4D895FF1539325BE468D5F4372C7209E64CDF0FA1EA8F37074D55AD66D80C0417FD66415AEFE63443209FA6537DB7CAF86CDE8F7CD63CADF82E1C42FDCE9F8804D95DB705F43DA63BE27A7DC513D93FCB0A12AC75BB701594D7775BCC2FF35F0E6AF76923EBDCAB9E957A3D
	EDCA70128DB4A5C784AF65D4762198DB39C244DAF801EE52207DFE8CAD43ED0841B13E3F7E32196FE2E91EA55FC9CF743B969FE17C669CB1597E8F57385AFFA8782EF68A3EAB0E42796A28383718FB8869B539CF1343833888E8161C7FDC23CFB250E79D957C2BAE640F9AF12B949EB0649DA994746EC6072721D14CD1FA84CB1AFF9872BED998BF8613F9720210F9CE56E2D2367D1316B2A3680DCC565446284CEED827B2G16E7FEF15A19FDFDE3744CC01A261F9F58D976D08865A8EDE2E75982835FC27195A6
	BC137BC1D548DD8BEDA9CD627CF52F0A7BC4BE544B1A9D1EEA00D6008100794DA2BE57A87BC90273EDDB900E1DC1C9690CC99C45B9D341F9A15763751FA9E6DDB3BDC893D6F797E94DCC7EA54D53754078FFF598E3C696342F83A0ADA087C0DD0B60DD9BCA487B31B1CB5E99DFA72E47735555924B0B9ED767F705537AD54555D3627AB1F946B55C5FB321AD8BA09700F8A11EGB482783DC22E7B065ECEA897C6A81F5A8238D6482FDA62F3F403D7770D5949785C34FEAF475567FAE37B713C5E077B0DC5EE023E
	4DF37C39DAFC0E1F8AE3AD834886E0FB711C9131972F770AB3640353F81A661316322CCE582F621855FAED8863FAB5C0DB97C088A089A093A08BE043B131DE254166C46B55A48EE5BE5578E931E80C5F4FCF4DCE4E3B9376ABBEEE552F0F4C4EC674E97D62ED247C3815DE65886A358E5AFCG8A40FC008C0065G6B0F8B3D76858F5F249FB4D1B5CED7A86373AD676C7469CD582F649D2BFEBF1D65FC9B93762B301CEF32F0F673FDA2E13F06F72C566F7191DCBF9F3405G66832481AC874885A8EE956B37A431
	DF98571AB37B853BE33C171EAF4AE17CDAG067BCC90F1DDD08F350AB8F1B404F9D932D1EF2375D4A30EBC2C5A5D455D8A6078D6F8EE83FF16B96C6E7F18FE3F2D5A5F9D5D2FDA5F9DBD28DA5F9DBD2CBE4B70B00679A6DB342FD6E73EDBFAD4353ADBE2EBB4C64FD599202F25463A2D19906B365C30173D4376726C9B361627EE58DA1EA343B34BF3FC58CE1EBFD03DBF7042D65ABF9C1643B448F8A5DD62EDBF5171BEE85A3D989E5E0848AFBFF25D43E90E69EA4BBEE35E0FE976B2FEC23316F101066771309A
	51FA65F8B9BEC777D25696F51CB6DD9EC2F6984DA25ADF1734FEF91A9E3EC27B33B63B7E191A1D2E182C07EF18E545FBAE364FCF044C7B7C7AD676ED202C4D6129EC937B7CD2CD6C735EB6F1CF119C215FA0201E0C38B6613773C6E60AD75F833AD5ED31BE4978F3CD897EBAF435A33A963F8E5B2F05D337DE0BA25DFEAD1E51932F450B3A7DDA3C2CA7DE0BDDBA5EBF74847C810D5F74B1999DB1B2C66F9A3FC0CCEBF32A4618CB0D584BBE923116FDB2E2AF7B47A209E5CF0A584B7E59C8E259D3A3B35B611053
	AC6BBE837F0A4B0C5FEF606FA9FCDA682CCFD307C0EB7C4EEA44DEB7CF0DD86BA6FF44DEB7852309F5D3B2EA2F1B6D2309F533E3545AAE32943CFBD724F1B522C33CBD017E7518F1473E9E7C458D38CA0046934CBFD99E7FF7F43CBE581DC1451DA13917541E443EF787C55FC5C03F98A09BA08F207424B81F9F580C7EFB2233D7D395856FA4EB987ED393743E8414B7F9925FE4D45F00AA9FB7EFC20C3B8C6A59GF9G256D3057378B1E7A0FE17C35B8DBC4E7A262AD435F0744DB86DF3B08AB8F8DA3DFC9D0CF
	83D85AAE624AFDDAFCDC49EE0F4611F56DF6677462919CAF3FBD7E1C7E9C45171C0ABF275F9E9167749D50B66714588BD3C744DE18EB60960EA22E76B41C5981D87F2707445B0FEE40A51F96FA790F2A174CFA2912C6757A7AE6953F89343305E7BA699CAA667D33CE8B1DFC040E69BE63706CG28BDA36EBD477EA46EBDB36E3A0334E6D85F892F75DDF7351FE17A891D095E03EC98DC63E2F3BE9494F3E6C0FB6E99447DCF50FC97087A58DA484E502C7DCD555C0EBE3F07185B2518F664F7175B5C7E591876C2
	D3FFA65B032F8959729F4CA44A3DF3ED5F61D4F6707BF9DEAE48DD684C7D71C4819B6721CB6677BC5BEED7DCCE1BF166737E499DD19DB17D7C7E1A504F728E211F56EB717AF9A5C67E09EB537463B437E7DC0B574FA3B16D7FF6ADDEBFFF4975477C611B211976D9722E1DBF34E8745E6A5DF8FFB8C571956F467B434AE1635E8A5AD25E95363F0A668479D05FF6D6604E6B42C7EA4F8A3FFDE68463C917D1AF23F57C1E436A0BA271B9B8527EFFF37258BC7F9F264DF1335F6CEF86161EC5596F9971723AEE413F
	3039828E7301A69560F9DE4FBC4B74AEE8A01F2331C5665F81CABBF10E6F0F0AF8696E947AFBF6E8F66F24F64E32DF57AD77E3765599C4BB6A6A0C4A49705D946FB761195DFD5C0823A1E84B6E947725CFCC619ED467F3F8FCG8A40DC00B400E5BEF1C6993DD1CD921CE9FDEA289D128745F46C60EFDB0233FBBFF6DF1108D70B3F75D53264AF3C365F2032FD4CBE72F8397B3105CEAC19FC13CE142F62BD586B817CG13G8BG525F9372FD075E15614D8A18879C49255E817AE5CE88F7AEA00FC65118983031
	3F64C2CE79777E295720BCA7DD655A4121D4CD0CF0823628B3BDD7A1CB71C97DD11B39F62B3AE1F16187A19197163EE71C77B50C836B0CFAA72D6377A6D67F71702D1E4DF12CDB33653CDB1E63D662867F0621CD4F6DB605C59E356D1C43339B208340FF0EFDEFE339D6D097B41300DFG10893082E043B911433DB608FB62E973B7B4A2F22BDC63EEF65BF907E7A725A72418083F8DC72DF0AC672B402560F67DE809306B9DDF3B13F87F556BFA55462EDB4FB37D271C0F6E1F49417282462BF71C6615DAC4E663
	AE9AB56F4BF51BBFEA79BE84E1859FAF1717794F5F4E5F0ADD8892E3ADFBCC3FBFB9746738571959F66E7AD067E0BE7A0BA97BB34E2F262C4FB87FBBE5FD57D2C8E63E9B5AC26C5F9D1592733BA396CBBEDA0879490EAE07278E209BA084B037CB4428BB489F890DD1967169183F9F4FD8785AD297734FF35A4B1A64CC7CC673B6A73EC768D1F5DD9D4271857E7354D6F4B5C4117C2E051EC135C07F004EC8B97EAEA77A02A277D17462AE742347C365B32C5322AEE6134C46D753B8EB75BEC03CC3E2FBDBD5D86C
	ED79503F7602B8A354DC215FB2E24F886FE881FF33544F8E894C37C742A29FD5AE08B3C29A9DB38D6ACB8132AF083B1BE45D78C6F3C1642ED9FAFC6E7AF4CCEE7FDBBDBEF74539226D5511785C35A6A6376F0944672E22BF731DA7E26E08AAAE028DDD1CB9F6B31BDB3B89F32CF6209DGC882C883D8823082A097E0A3C049A507278220812083408FB086B09FA095A073123051009B4F9C16FBA0A039622921EAD2GCD1573F13C98F57ABD98E72DB1D626FB6F8CE3F5E8E08F1ABE4E3074851638CFAC34377BCF
	97A6367B5B8BD91E58AF7B46C37C1E61CB05386728FDFDE159702F3442ED862EAD5C210683F881416F5E06113EC546EF48A26E0F4A66383FE3E723CB58C17C5A10EE36F6155D51389F0D1335615965194D336CF76D167B31BC34CC276FF23B637353D7A8DE690E4FCF3FEA7870983465F40B3CE521EE1E3F71FD48D35F1F5936D90EC4E8EEBF70BE1B07618AA9EE8E4771B7201B6C6D65B71B925B0B3B48BACEEEAE1A9EA73796D94749670B4CF11261FEDF04FE5E8FBE53E5506DB4F0978CDC859D93F16783FEFD
	40403FCE79C2FC2B0C938AFCB565EB14623AB86E0089F701636A0B5006C16481D593FD0F96E14E5197D094D6EF21B401A03800210FD64AE3A0A41978BEE56022FC1FAC4635B276CF66423C6DECF1A9B13D6B22CF67BA01B0584F624537BD65D02642BAAE8148814883A87EGE2B4C0ADC07B87A20E2CAA5ECD6C63883BB00A06A4E6F3AFE3E862763F4B74BBB37C6167C18FFC28F3B0BF485405EF9CF69A5F99F5619BBFA362DD0374A7537BBAEADE8C3FE3ECCB94AF4C0B2F5D839EFBFFDA61C96CCF2BBD567E74
	08E73ABF65FA2C7D69271E697E74A49FD7B3BEDAB17CAFBDA88BBBCF1C1202D2BF4D49587F5CBD52B58AE5464FC921CE7EE879F4B9662DF3D8A3DBC51ED155C63DE26D487E7A02476EACF091F79B594424EAA3EEE42D0D94D24E5BC881C8B5067F45620E8136EF6C19B267759B67755A6675A917797F70DD0E6E9B4CDF8EDCC2FD2C833C7B0A700BF9975F30738B48A8BB07A4C551A5E530A9684733EB7338AEC7F35B7ACBB8FED85070C65656F489793EEA6A73FFD0CB878886D95DB0CB93GG9CB9GGD0CB81
	8294G94G88G88GE4FBB0B686D95DB0CB93GG9CB9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0593GGGG
**end of data**/
}
}