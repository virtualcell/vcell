package cbit.vcell.namescope;

import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.DefaultTreeCellEditor;
import java.awt.Component;
import javax.swing.event.CellEditorListener;
import java.util.EventObject;
import javax.swing.JTree;
/**
 * Insert the type's description here.
 * Creation date: (5/6/2004 4:35:27 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SimContextNameScopePanel extends javax.swing.JPanel {
	private cbit.vcell.mapping.SimulationContext fieldSimContext = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTree ivjJTree1 = null;
	private javax.swing.JPanel ivjSimContextNameScopeDisplayPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimContextNameScopeCellRenderer ivjsimContextNameScopeCellRenderer1 = null;
	private SimContextNameScopeTreeModel ivjsimContextNameScopeTreeModel1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimContextNameScopePanel.this && (evt.getPropertyName().equals("simContext"))) 
				connEtoM1(evt);
			if (evt.getSource() == SimContextNameScopePanel.this.getsimContextNameScopeTreeModel1() && (evt.getPropertyName().equals("simulationContext"))) 
				connEtoM2(evt);
			if (evt.getSource() == SimContextNameScopePanel.this.getsimContextNameScopeTreeModel1() && (evt.getPropertyName().equals("simulationContext"))) 
				connEtoM3(evt);
		};
	};

class NameScopeTreeCellEditor implements TreeCellEditor {
		protected NameScopeTreeCellEditor () {
		} 
		public void addCellEditorListener(CellEditorListener l) {}
		public void cancelCellEditing() {
			// System.out.println("\n\nCancel!");
			Component comp = getsimContextNameScopeCellRenderer1().getTreeCellRendererComponent(
				getJTree1(),
				getJTree1().getLastSelectedPathComponent(),
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
				Component comp = getsimContextNameScopeCellRenderer1().getTreeCellRendererComponent(
				getJTree1(),
				getJTree1().getLastSelectedPathComponent(),
				true,
				true,
				true,
				0,
				true);
			System.out.println(comp);
			comp.requestFocus();
			if (comp instanceof javax.swing.JCheckBox) {
				// FireActionPerformed for the Jcheckbox component, need an action event to pass in as arg
				// MAY NOT BE NEEDED
				// ((javax.swing.JCheckBox)comp).fireActionPerformed
			}
			return comp;
			}
		}
/**
 * SimContextNameScopePanel constructor comment.
 */
public SimContextNameScopePanel() {
	super();
	initialize();
}
/**
 * SimContextNameScopePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SimContextNameScopePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * SimContextNameScopePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SimContextNameScopePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * SimContextNameScopePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SimContextNameScopePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  (SimContextNameScopePanel.initialize() --> SimContextNameScopePanel.setTableCellEditable()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
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
 * connEtoM1:  (SimContextNameScopePanel.simContext --> simContextNameScopeTreeModel1.simulationContext)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getsimContextNameScopeTreeModel1().setSimulationContext(this.getSimContext());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (simContextNameScopeTreeModel1.simulationContext --> simContextNameScopeTreeModel1.refreshTree()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getsimContextNameScopeTreeModel1().refreshTree();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (simContextNameScopeTreeModel1.simulationContext --> simContextNameScopeCellRenderer1.tableHash)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getsimContextNameScopeCellRenderer1().setTableHash(getsimContextNameScopeTreeModel1().getTableHash());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (simContextNameScopeTreeModel1.this <--> JTree1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setModel(getsimContextNameScopeTreeModel1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (simContextNameScopeCellRenderer1.this <--> JTree1.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setCellRenderer(getsimContextNameScopeCellRenderer1());
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
			getJScrollPane1().setViewportView(getSimContextNameScopeDisplayPanel());
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
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new javax.swing.JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}
/**
 * Gets the simContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simContext property value.
 * @see #setSimContext
 */
public cbit.vcell.mapping.SimulationContext getSimContext() {
	return fieldSimContext;
}
/**
 * Return the simContextNameScopeCellRenderer1 property value.
 * @return cbit.vcell.namescope.SimContextNameScopeCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimContextNameScopeCellRenderer getsimContextNameScopeCellRenderer1() {
	if (ivjsimContextNameScopeCellRenderer1 == null) {
		try {
			ivjsimContextNameScopeCellRenderer1 = new cbit.vcell.namescope.SimContextNameScopeCellRenderer();
			ivjsimContextNameScopeCellRenderer1.setName("simContextNameScopeCellRenderer1");
			ivjsimContextNameScopeCellRenderer1.setText("simContextNameScopeCellRenderer1");
			ivjsimContextNameScopeCellRenderer1.setBounds(450, 221, 215, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjsimContextNameScopeCellRenderer1;
}
/**
 * Return the SimContextNameScopeDisplayPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSimContextNameScopeDisplayPanel() {
	if (ivjSimContextNameScopeDisplayPanel == null) {
		try {
			ivjSimContextNameScopeDisplayPanel = new javax.swing.JPanel();
			ivjSimContextNameScopeDisplayPanel.setName("SimContextNameScopeDisplayPanel");
			ivjSimContextNameScopeDisplayPanel.setLayout(new java.awt.GridBagLayout());
			ivjSimContextNameScopeDisplayPanel.setLocation(0, 0);

			java.awt.GridBagConstraints constraintsJTree1 = new java.awt.GridBagConstraints();
			constraintsJTree1.gridx = 0; constraintsJTree1.gridy = 0;
			constraintsJTree1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJTree1.weightx = 1.0;
			constraintsJTree1.weighty = 1.0;
			constraintsJTree1.insets = new java.awt.Insets(4, 4, 4, 4);
			getSimContextNameScopeDisplayPanel().add(getJTree1(), constraintsJTree1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimContextNameScopeDisplayPanel;
}
/**
 * Return the simContextNameScopeTreeModel1 property value.
 * @return cbit.vcell.namescope.SimContextNameScopeTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimContextNameScopeTreeModel getsimContextNameScopeTreeModel1() {
	if (ivjsimContextNameScopeTreeModel1 == null) {
		try {
			ivjsimContextNameScopeTreeModel1 = new cbit.vcell.namescope.SimContextNameScopeTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjsimContextNameScopeTreeModel1;
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
	this.addPropertyChangeListener(ivjEventHandler);
	getsimContextNameScopeTreeModel1().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimContextNameScopePanel");
		setLayout(new java.awt.BorderLayout());
		setSize(388, 535);
		add(getJScrollPane1(), "Center");
		initConnections();
		connEtoC1();
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
		SimContextNameScopePanel aSimContextNameScopePanel;
		aSimContextNameScopePanel = new SimContextNameScopePanel();
		frame.setContentPane(aSimContextNameScopePanel);
		frame.setSize(aSimContextNameScopePanel.getSize());
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
 * Sets the simContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simContext The new value for the property.
 * @see #getSimContext
 */
public void setSimContext(cbit.vcell.mapping.SimulationContext simContext) {
	cbit.vcell.mapping.SimulationContext oldValue = fieldSimContext;
	fieldSimContext = simContext;
	firePropertyChange("simContext", oldValue, simContext);
}
/**
 * Comment
 */
public void setTableCellEditable() {
	DefaultTreeCellEditor editor = new DefaultTreeCellEditor(
		getJTree1(),
		getsimContextNameScopeCellRenderer1(),
		new SimContextNameScopePanel.NameScopeTreeCellEditor()	);       
	getJTree1().setCellEditor(editor);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC1FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D46715EE8F661854BD21ADC9C9566DB12929ECCAA2691A1613FA9A53B5DDB661ECDD8E495AC677E874EC5C0D5D382D27E5FBBC6DB7FF4E8C51C0A2B6F6A5895634C1A588029191151FD1C07CA101A0AAF1F9B28F18B84C0C6F3DE1864138777B39EF9E439BC6ED651CFB5EFC777B3B7F5F7D6EFB10127766BAAC5692C2AC8EA27E8EDA8859FE10107FAC581106383A8F725C4470379360CD72B6
	3143338D7A221F65390B4963DF38205D876D8DFDF96E376177D6720D506C8370A341BEDE685BF957027B275F67243E0F13356B144D70FC83209FF0741979815D3FD359AE70A9820FE03192B23B1C0E3922148A5CFA986382DC9BC0DFB99F2F411393B0333ECE0C6B7BCA8659701CE25DC257B12C0910AF669608E7997935E56EEE3AD79A23134E290276B0GE55EC6FEF638861E8D1DD7060E8EDC76E9152383325FDF9910C6E4F5A098126B6B3BFCA3AD4100A6C735B3006C2248CEA9A07BC9699E3A6EF2454556
	A1E48D2C1941F04DC13AD7BF7C0E82246EC17CBDAA452F035F763DF96E523D485F118F9A847D3D635FA3797BC76CC589782B5D4B796A5B9B63AFEBE4057577CC9777D8284C5321EF9140D2008DGA595F96EDA009D4C863EC2764033693256929C9989861AC221E63FD7B99D744AFE78DDDF8FBD827709BC90D43C04F0BDBF28D41850B39D70F99937BC0F4BA42386F51E794963E4795F5FEFF51A48E4F9463875D485170537A2A6933E772A04FB27D4CC364949FB1FA3463DCBDBDF34B9CC762E2BFD5116AE5649
	B46C3D26026A7A3B4C86F26137C5480263FF4870659F46705CBE5B981E369B21EF5E07E89B3DEFA0AD276C8F13626A2F1B4AA17D07332D2ECAC1C3E5BCADEFE8F46DD315FC4FA21D16F799DEB360B9AD294CCEE9FB8E74ED5F1767667675B59B7557B4E8E78164G2C84A0D5E04BGD5D5E8E3BFA57BEE5246BAA5C5EB8E7B7CDED9A1FC6E57AC3F03A7F81BA23587D4CD8A8C48BD126213AE7BE51508B1F318FEAFCF1A7790337D815823D58E480A24790281D89DDCD61395150FF95472A69D23482C375B87BE2E
	C9A3A1026BFE4732951EFEC955BE8DF9A5CD0E0D607DCFD928CEC6259ED89196005FDCAE0B6DD4DE1D407FB0C0D4174381ABDD2FC3D6A0DC54571F8A862F04C35D149312D3C565DC40E247EC78BD2F8AE53C48C1F11B2B735C6FD763BAFE6BDB06F5BC3E81CA3E240C210FADE236D387637BGE6D5236D7CD7AF5A0EE74BE32462057BCCEDA7936CF8E1B537195C6AF89F4AE1EBD37AB6C0DF49FE0C7BE7023D6887E2DFDCEFD63539EF5DFA6C98527720715591577F676E41653D47C9792B853EBA81C27BD16673
	2DE55C061BA5D5C62BBD2B0041495E1886B81DA5DA2910AB52C971D4872F83EC5179F91F0D1B85FBE481AC83A0B5F96EF200CE00E88D4A76FEB514E43DBF177744B2FF126D1DE85F22BB34B717795F5A687A72289C50BCF2C88EF86107AA7A6659A89DA2AE8C8675B90FB2FC8862C88BB82E2A6333EC5457A5CDD3FC17431A5CA5EB1AAFB0946B4FE6FB29614B21D888423D1EE671BE9801B8220FDFE223BEB8908C84E426FCFD0A687F910B8132DF9E8122DB026100467139F61AAFDD1147C48CDAEA5FC4E88C71
	07E9BC63B9C816C4659BD2FCA3E0D2B4A41275B5B4BE94295B8DB21A8BB81ACBAD0167329A747148AE7471772F7D9DD97CFFA12B59D9157E483D563ADA6E1727EAE3BE4EE9FA46E1CE7BBF32D8A470AA2775A78ECAD32224484666BD34057A0EBAF5CC8B64D61FD03DAA32D24D47DE6FFE47FCEC37A24B6C4828A6682B83AECA279F681F8110DE0B3E5A320976E3FC94CE5A92747B392A20EF7381AA4FAD219203CC32EA39BC8B60497BAF29467E6E833CBFE61FD54EE2CA2BFCB5AC4331C483F9072E57034E3762
	7AFA040EF97FB1A755315FF0862CE5FBDE54B087CE977BE59D60F1B667G5F1B0F5B9746F1359F457A697354C7FC5C120F6838557A38E1B1EE86BCD38132G5E55577B4EA80EABBE98DB0F46787203FC3D1403F45C97919C57F810472BDEB15E8F4FCAEAF7F5F4DCDD9447ADBD4879D8AB1E244EC85F4F75F121BA3E9F52B72B0E53476D6C973B291D313C7CF3E9D40AD62A91705F7A7A134C4EF86EBD263A447839F518E3BFB2CAEDE499345783388E211F7C4B964C31532EE410025697EC096E90E507B83D1EC3
	B1BF19F8BAE263B678181B6E1982FDE907507E1EF29A4F79A6D58D8E78D80AA1F46FF3997D0863822EC2F88E7A94D5EB870708221DE8AECAAF4DC184DA48E3E83C1E5C6ABD4568EEC45CD79E3D0AB24B50E5F37D04D1B60B0A2EDBDFCFA01B15C2A624BEA61B653BFEE563B44E5AF97016E9545DDC641EA9ACA7256BB5429ED1G9EA7DE558AA74589FEC6EDA55B42EFF2FC584FC42EF70BDC7E31F79B856D390FBFCCDC52CC2B595DEA76FF8CD93384CF4B8C3CEDFEE105358C0053569B5ECC783EE507418F8E63
	9DB265909D2F48030A2C8ED3064AF777E0AE3DF994672442781C43480FBACA4F85ED5827725833F84298FBC88364FFGED8DB87EEFC669DEA3CCD2485B4F1B0C3CE5BBE71B72B6BCBF5D5A57A0FC3AA146DB6A6A9556F92B113717F4BA73E14C66A3386FE0046E25510855A6294384791A9F41719E98BBF184E5D1F214BE5979C6D0C7E55178B5B87D9367117E99398B49762DFFEDCABF0505C7B85D4B0F4468DF76728A6B3A1711FEA90AF6D3F1B44F6DBD0A7467B03CAF6053FC125FF78DD443697C69D9DA93D1
	E50D85E36AA9473DBEC6A2D59E276F612DC679B65C38E4CA5F2C22E16B1C23A2469D0D51B7AFFF05B5BB9F695BB10672AABE86FABD0674FD0B61878C1E4A7330AF54783B6B0CE3313CBA6D58549C1B5F69FE4F7412F10C5F69ECBAFE9743E79B701C2EEF6BFA4F013E0AC63C070C317BD4AA34B38116GAC875888D05A04F1EF230B56CE263D673503B7AA81495FB5A951427B96138164C6227DDD3751F6389C96DCB53B4357B509DC26E92A9CB87D2F50829769077E8900B400CC00DC1D76A305C9E9BF9EB5251D
	53F50F96CF573AB8DADC4D135BFCDE7A14F96F4635AB6266F1FE0E58693969013EFE00A8C09AC0B6C0BEC0C1B37215F263F0D23EE8025D9DEC9A9071F2F2C6AB683C94CED637B1674F1FF45E769633F9EF0D5E5EFEE5C967D5456DA7F26EEE6A7FA679850D5FA27F760E45675FED30169FE086GBFCFFFB50167E9FA8B7A4EC2875555D2E82F8330FB725CE5G9E00FE00948F6A6A459B4D49F45504979850977E1C2AB2A10BD537A94357124E1B63B11B77D86476767BFE52F98BBDE6BE305FC9651A8BFDEB8148
	F15081C0A3C09F400C63A857BDA429DC7552504D48F5A8FAFB3E9E1EB22FB22E1DFE5C0C4F79AE4AE7967465822C81A0A7A09FGE884701EC0BE2F13A34978943E5E8238B638FB1830A8E85DB9F6FB3A5C17F4DE4A094973F86D6BBE163B4F013EACG0ED34294178F6DF5A7309672DE0466C9452D585E4D5A548FF97B09515EA47B5F8C50754D70FC0F677EA2FB58926039AE3FE4C9DC7B385BE2DE7B3857E2DE7B380F55B9818FEB98AB2CFA1D45B2FDED64910BD9ED046B68C76C7ED08E722AEBC53D6DD5D0EF
	1ED6344D572C09797925551C1F0DD6F3FE2C366979A9CCD86BF907497D1FFED744662B2A2CB20770C81AA47ADE513691133A8E3D1F904CDF1F515CE24CF1ADF83ADA5F37551E1847B3F6F39E3B6D745E2986434A009C4F47C57BF63DAF66296D1ABCC24961E33CEC3EA6A9C37294B9DCE173F9DF22791ABD112CB82F77050D3C6E57716D23086F54021DD4867C0C3C90B11E11432D3C161D864F0CD6BCA3670C61993938956F692FCF503C33G5A45EDF96E72B674670D93534513C698575DB659D7B9DFBFF0A456
	45338EF3DDBC6FC82C0BBCC7F2DD3C64C82C0B179D49F5712A035E33AF7B3CBEC5D42A382C37C70CBA2851F530A672BA6AE0D0890E009678BA8D4E443C37B84DF9EFF7A6663D53191C77734E443C77BB13736EF31A737E0D28017716182FBD61CA4CE3364B1C479F389273782CABB90F7F6CCA4C638B2E64BC7E1BEBFADF6B9DB37AD44C57B2D5DAA376CBE3413086F1350D7A4BBF683560E1E84FG48GD854467D0D6734CFB07B98044C8636698A4983A42B1D4E3DBB0CF3CB21DD8550899082C8ED47FB685E4D
	549FCFF68DA8C13F1F5658C4FDF645D5D627G619634536F8118FA443A4D7ABB7E061330A6C088A095A06BA452B4F41C46D913BCBB3693415A54D7A378AEFD69C90C938B587D5D5581B9B4C0DD8746090726649D1D9D3138606DC8F4BF3E4162CE34A37EFEFCB7434F6A083F9F3FB9017763F4685B5801E75E566BF866751FC25CAB810A4B027692GBE7FD5953FBBD886386253A817FF2F5CE114CB2594516A6B1B0334F6FD0AAB1E4B24C6F30979ED27D1A6922B114D00F6BAC056E92C63C57F04F53C4CEFBDCC
	56351857B8D7F6DF37661C6672D9F7BAD62FD8B232424A77DC9046BD4B4FGFFE7A8EEC558D81F227258E76779AC6FAF9AB576D33B5FCD0C7DCD137AA93D5470E27D6A24FE07E1BE676D79BFA2EF9B1F4FA27EA7D392FE8312F1C63C4B944F54B58FD86A0A46EDFC1D9FDFB4FECBD27C205FEAC9300E7DAC1FDFFCB6A6A3AE1FE1856563B90B7239DF09174F4EC97C67A87172793E45587FEBA5DEBE8BA675D728717219A964477D616B535E29A64EA67207DD2C7E14FAB65E9FBEE47839E7637D6149A87AC39674
	15F4226DE70FE1BE9F55F17B4751C766F4225F4E1C2071E4315E3E1735E97D1F375F1E78CB645AE6905B336A0E6DC165D27AB165FD4FF50F4065F7CE15BD5A1CCD40DA983391E01BE897FF4C650EE336125F3058A20B9A254676589644F8697D98F97B6FA9F543E440677D76B66755CD29E7A583EEDFB52C161EED6013639B98FE09814F6DEEC843B83A947AEABF41DA4B73B6764D9334978264822C8130F741F95105F711275DD5A449BD39BB98BA8549055FF03D607CADCCDA4F0A879EE71EF1E23CDA785423A4
	7BAECF42F7AA35DD5CBE3A4533FC7583967A647CDDE2F9E48A744D85D882309CE0BD002B9B79F396D6F07EE8893BC9532401E1762689196B714911981AA686FC6D3F22E90648E7CE977E911EBD5267C2BAF4292002AB1C04A36A6C654FA149691606F45D0F5C7237DDBCAEBC9E4138D05A2D5F6B47583DDEEF5730B6FD87A2EAC6511B71D9BA7FF67C166F11FFD3FBA43A47A7831E472D52EB0A9A50BA9BE0A1C0CEB7FF5F43732A76F19C33D27033C1BCCBBE45FC6D01AA2ABCD62D2C467C6962F53CE755423877
	58B8481D887E2DBFE60663F95DA60662363BEAB35AEE69373FC9BC7F6A316ECAE03B7ECFB9DD0BBE0D1D1139CA190D53D891E439239216793AF3A34633372E70CB265FAAB0FE45FA2E9E7E5C386DC171BD5221B0517555EC78BD225D29189E330F3B6E689E5C8F732779866625845F402CCA708D4CABC93E01F9ED1AEFE0FEB9699B989EAFB29CB487E9841D74814CGC8834868515FBD587FC0D89CB209C1473DC3741EC53F6853F07F21E96E6C81C7726FGA38E7ACE78F2D05302A3F4FD44DFF7D0FDE94190C3
	0A7A9A2B81A83E21E10D8F9578D96C9B973FBC48504BFA289FBD9BA91BC6CFCB04CD0A3B7AAE6AD3A6AF36BDBEB584D7B87E2DB0BF435C57708CABBB17676EBD07F7016AFEDA6F0D3B8B34AABEEF33B444AF835C3FBF3E06F9E76AB93C8B5CE0EB66C0FBA5C041B92C398447B1969477E20EBAFFBCBE8725EBD5924CB1FFB29E1FE3E2BF377F7E6B46F85F866B7675CE9FEF395DECF252DCA884E3E783E48164GAC8758G60BA8FB483D48134817483E8G33G66GE482AC8648BF0FF6566122F78353338A10C2
	F04C589449C74DCD5061E75FE12C05F56C97F02D2F3A76CE33D627821AD434B10EE5DF746077E553586E77EE42F69773EF2B40663A47C2B25629E8CC8F8E8E2A326EA34FB90AF5DC3B5A99D4FD344E40ED6F65889D5FA253F72C983BA332B1F6173E57E8C5BD476B667D8BDCF71D97E231BB966B0A2E5DDEBE784EED4E6B3965F9BCDF7C85337F148B71F964C606CF3D901FC74E54FDF08E74110B18DFE863467D3D000FDE1C5E36F9AE93E1E76E6C0BFC9F0E1BE038F9824775783F535468F6BA135B4B9E27F92C
	2BF2CE0DF5871C66316E3053986BB82E51C97DFC88FC26D79FF7DC471D57F1A7591A94F74E675506C92CD6D8A870EDB25D9071BD0C2E884375E2FD5B00BB2F578A298D834B43C185674ACE1AB78C7A7CFE2C9552B13EG3800AE0FG235197128CF42BBAAEC677D5975515FE86F29796FDD1D789B1FC9B423E9F6A020101A19EAF56384B60399874389C007465393783D4GF8GFA81C2FD98C7FE63AAA7096388AF6C44C29237397F39C6F47B370E47FEFF795A1D4FE5665D51BD389FFCF99CFD03A6049C17BA0E
	3E315B0E6F6225BF993E51E16645712551ADB1BC1A9766AE6EE95EAF39137B53EF5D667E64A81C6ACF0505667ED4DCB8551F369572F5957DA592476FA8243C701A6EE9A9A08D313C0A7FDFD67697B4114D7C99F1E824AC59E7332AC20AB0CB28ECE7BE31F234265E367A1BB6AB313713CD1C27F6E22704359387233B1D580027A87D365B9E877C5C58FD43181B974C9CC8181BAF7A0C4745251F454E8D6EAFEFD8587FAEDC4AF317DFC23F38C4BECC64971095F60DC8FE3FA6792FB4853C740E59BC2649317CF4AB
	BB0FD49CA3BAC5BCE56FBEBFB74C79FFD0CB87886960D1442692GGC0B7GGD0CB818294G94G88G88GC1FBB0B66960D1442692GGC0B7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6092GGGG
**end of data**/
}
}
