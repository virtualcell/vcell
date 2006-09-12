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
			cbit.gui.JTableFixed jTable = ((NameScopeParametersPanel)comp).getAJTable();
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
	D0CB838494G88G88G4B0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D45719C4511004A8A17EE0C2947FF6592E493ADBFA4A261E9E5BE3FB5C0DED71EC5A58AE3B25DB13A59B32CBD2327524EE77B203F1947C0B2421A7A492E3D3CC8CD1E3D7CBB4AB029B081A90256390D1F8B373E666411BF963FB8F9894E43F7B7766B13C47A82D1C731DB7777B6E3D5F4F7D3E6FFE773E872A5E1333295CD10DD079A6447E0E16A3D4F394210EFD57A50E4B0838AB11696FCD00
	DD68B9C785BCF300D63044DD3993E91A8B5AF950BE8B6DD76177EE945352FF87BFEC787801268DC5D6A5616365FCB613760B972B60399DA09FF078D921E07EAFFBEA983E1061B914BB90DACF7A3C6729E538D6E877G64G64A9347F9AF8EEA11C27B59EE37D3A6E4BC5E5FF2FBA3661F9CCF3F2B89025E35B59338E3DD83EA10AF939BDD5ECCC9A34D782E0651D68598FBF006771763E60C7BE2F24B78C78C4D9EE0888E1D173A9D13131710294EED6A23A9853BF8564850CEC97A2220C742BF85E799E97198766
	00B6D2B1CE75E3DE7950AEGA8B6700F06B03E85FEF78168AA576F44EF0FB379BD573F0A56873F35E5070DFE69AA55ABCF0D6B177A0E5779EB3296077C586625C02B565C1575GE781E400F40057088F9C733D834FA62F5E2C0443CA24A99ABDA57B55F30ADF1461F7E3A3D0986E3C68D3D4BFC2F41D3BBC87AC64198868385EDB9EC7ED127B81DF73E5679FC105DF1E6358ECE113425C6B0EA59A35458AADEE934AFB31AD6FC2ED2CCF0E65FD9119F923278FEF5EE4413B768707B71732F9AA74B86F63BADE6B27
	088F6C035F654C96947FBC4127187054BFE3840F5BD9C05B28F35F70EC67321C2DD804F69E1AEBE9072CEF26BBFA7A298C317EC4D9BE12705CCB7AA94F9D06AC1F927C9A931E4A321278A9EE1781ADBA602E247E55984071DA82ED57203B328E60980087C087C89B64BEF6076F7D1B7431F6C155CF75CB32DFD4919D5B687F95BCA15AD421AD2269C244A73A85D5923C3228A15627590FED609DB32E15783E8FF8FCACC6C4D550A5A58233C3486A222A51BE1F7BF761BE2AC8289D924438AE0423084F5B695F8DCF
	D95074BFC47D02AE46FBD03A6047EBB2A038E1C618GFED33B840358DE39207FCA0082438E05E43EB3228A692231712C227475C7BB30A628AEC67281499D6B217F46C1EEE3DF88637CC09F0E71F9F60BAF1B66E911FCD8FCC19D62B1D6C2FCA7837A67819445386F7C3B077BCE4B360751016F4F36741D8C70632AA16AB37B0692E3E883199B4BF796E872904F7BB77C9EBE1E71657395452CE36B56F347DC03874E2F1B9268FFAC8FEA6F35A48652C12FDC0055C35C661F0AF55407CF891A483D76B3959CCE7447
	D7004A59A255B23BF2B9B19E2F815EC037997AF410FEC54043F54DDDF99040831082108BD0F00D5B765B21E812797ED85D6DED7EA461ED47F747ED62CDED5E9240730B83E2C4EF9123E2448FBFB4C6BB9340F2303C90D00CB11F93FC9472C8B3842EE660AF86F02C8B3A2ECA5EFEDD3CA06A3A948946695D0417566F0D46D390679588E05D15C148A3C67F3EG0EC11F920908E4710DA10C9EA6B9C01445B0885D2C74C7F40A570238DE6A9307D88E9A8C3E04F08E117BF1BE23B548F2815BB72ACAE1F0A91C92D1
	6BF51C9F569AB594068D609F38162A855A416BBC46875F62B13E6F5ADF20155DD10755DE1535F426A363BA0D4BA55763B1CEE58A052CE50F10DC44709A1575071B30CCF6C511130CDB388D470EB63ECFB354D667713A2A22FA08769D69F85D3AEF07AA0AE44BB804F82CEE6D43F22E8279578114DE67317AEFA1CC67791185E933A24BF42900669F4676BCA1D61BEC629A2676EC07A7254F8819694B98BD6E1FCFC8BB312C6255FE9136A51C484F986B7A4F524B89B4B7DBE3CAFFCA42EBAC1B760013A31867BD92
	2F01CB99BF57884D33F5A314B76D575D4B7B251B68783904752B3D017B6D6E637DD6B27ABAF8968338007E06B15F3F987DC2B76273619C1FF2034ED7C866FBC4667D32EE50FC351C75DF854F86408F0F62FE21B06FD7BFCA75B8491E1ED133FCFBA33C5F6AD14A0F4BD7B4CA6523FE76337D584FC8DD5EAB8C883186ED90623731719362E734767E3053457AEF9865B5F6308B7B484194F7E5ABC0CF8A0F13355BF80DBD2BAF9795BEFDE8335D99A216C2654DCE0947C9C6E953CB5487BBE44C3390E8A5A95C7F04
	DE73BE5F24E90ACFA2A586DB7B1DFD66B82238D77A3642B3A0291A5E86819163FE72DA9F169757A08C4D6C913C5E08EE751CE28EB7444EABBF8AF01B21BB38EDC67E4FEC1B95BBC69C15B636B9F6873509670E38EDCEAA9D1B290CE9EF9E3DE5990DB0E735E79E590F71FCD94023G00660957FA370E4993F40F5A0DCE776E2278FE19185CA033DA7E61B7CEB25957BC32883904E98E2B33D57A53C107EB8A5569601438EE5577FB1D75GD436345EAAC47946204F72A97C8C197AFB5CDF95832A280530C2757B5D
	3C16BE5E474794C37F3A29DC1F45FDF8DF50C312C6F34F942FB977F8205FB0C00E517F31BE4CABCCAC45F5FB3E492CDB7E66F4CB5DC2FF1E65481B4AE2FAEADC372C05DE673205DC37CA194BF9A09572E1AA67BBC7463CF41C31DA85AD0438DECF987D33216F63295C96559F61A7595F90DF2376F0629CD47E61CEAEFF4A1ABCD4337BDECB79B1D4CD23F29F1996173FA8476BAC4B61725F9766FE138AFDD6CC6372EFA0F8A9A26912A0CB57E0E9281CBF7A8C5F09E822CE12B10E1453FE090808970F4A37E83759
	3E47C72FD84A1736A364A8E3F255CE0F4B37AC536BDC194965BB9361768AC11F1C69DC3E9F903C4F9429348EBB5919F8F6DDB7BDDED717CC9FDFE353B35DBF127C076E24E7BA2701FF1260F71A70D42EF097172B8EE829F772F348EF951CFF0B21DD1186B5874091008EGBD0D673D45FD786EE442F3DE9BC4239A91648BE38AADFE5EA2758254C62CFDD9B67B8E35430F83D6E7780CB4D64B240D378315FFF69447D3BE509F87A88128384BDD396FAEAE7BFAB9296C27E31632D3399E159265EA39EB2CACBD89ED
	BAEE7538F1EFA434D39346D1FD1E390A774DEC20658394G14G6C3C5BDDF9G207DEE2E57617007C9754285F6075264E379F2ECC54B641C511B6C5E46DA3F6C246322F7DB0DBB55B7B9FE6DC94725A570E3B5F7870EFF0B7A8267EFD6FF3F5D1DD8FF6740DC2BG5681507D74EF3DFCBFAD3D1B470ED743EBD5FF0F3B3285208BA086108D108FD0F88FDF2B547029E4EB55448FB030DE7C6778A5E33658A5CF4E06FB120EAB3B47EA5CBFCD125FB3C947D525DB45C0110E6D3A8FE8A781BCGBAC096C09E403AF4
	EE57F5496DEADC8D5D0CDD6706A7976BCB470DEBC8E817DA6A7907812C276BDE48D7G4781BCG43GD9GAB6E65FA3E9BB911CCCF966B4D00EB053307050ACC564FA313DB4B6E24638A6F9DBB0E5EFDDDA435FB9950DCB320AEA238A317B06EG34DBE6703B101FF461BAA9E4341FA1ED9C07342DF5F912703F994073DB61A90F74BF898F278D1E2E65C1457E6E6328E2FD77F1C2313E7BE8D6F6D1BC4CE13EE16574B34A44F7A397942B3B913AC67FC94E8FA9E02F8CE35DAE85793AE54F603E79D054DE1FA5D1
	EBFD3E943556E7F9F4E2FD7223F67A3CCE6C7EF73F5AC146EB1A281100E891F401517ED5FF85D95C6B60738962756BF352965667342E1CBBE470FD612A3D0E9B2FDA6B68D07139D3D37AD51F1828479635462045A335CD9743D89C5A67E5B2DE975420B84E8E3FA663A94DEE7CDE554ED6D457EF751AF5BDEC60077AB83EDDD75A318D689ED9FB593CC72E1CC16F32CB6009E672BD72CAB75FA3EBE672F37AF784DCF736C3BB8410B2134773573D936513AC6837EC6658D825FA491A7DDAE81A75DA8CE976EB1122
	A5DF0B3BB47B354850122F45BC8D1F333D12DFD259CD9535F52BECDE038F0CB5288ED772B58828CA98D6015DFF69763ABF27DB6B7EF35DDE77DF6A49F55F5CEF2F7B767E643A3F5AEF2D7BF743A65D1B633156BDE02FE3E040DAC7F940DEC7EDA0390E438376BACE9DC82EE37A4044311696B147D4BC56A21D780ED8960614FE9D72EA860E17E91DBC4ED6027F2F43F184786A8C9AEF34267D6BC1EC53GD4B64066C2D474A157FD646CF9090F5521FF9AC0AE40EA00628CFE8EFD378A4763A797FC2AA24B780E0D
	5D4FEEA3775778CA257DBE7CBD400291E832F955A01FF79634F381D683948338B239CC4153B84FDA5459F1CE085FCDF5DC666F526BB3F91E38FA854B55836D98C0C6A64F933E6E44BC111B994F8BABB26D4E47CDDEBCDFC1E66279788C4197E5A61E0F7FC1606763D220F55C4F773C6FFB791E17EF60DE75E31CEB961C1781687807BB79F7878781971A456D72D443EBE63BB48803FAE363A9855FDD1F258BCFED7282291370781CD95CA6EFFD0167DC876DD2G57ECFE0F97FB1B5F63AD7B33C52860C96BBB4E22
	0E91C75DECEA1F1659267B20F73CCE4A736AA54EB385687933B1EE41A5737D9436477B95341E257435E368586F77A3B35D59E526E3F937B84D745F0D21EFB20D273ABD769B2E5B464716A379AB29365F0020B96CDDA6FB56FDF7DEF9C9D67BCBF41E6FDDB2FFCB32738159D1EEB3CF979B9F1A93379135CFD6105BA7FB8E374F37030976F9F30C7E9B02097679DA39197ECEA851BEFFB5065E95CA344FB4E6BF9A8F1D17A75AB79F1FE3978FCF1278AA1E13988F7FC1709B66A4464360959E8F2E396ECAF9AE777D
	EB5D3C1EAFB0F0E5BDBCC64A6672387D18FCBFD413455B1F13B63E7F276D9F8AFF0ADA5B8A62BC1F3FEDBC30DD74AC2CFB093705618EE40D37BD77B9A703135027836095568EE5D13B73BE3BD18349ADA23B23DCC3F81C7673FC3942506D4B636E8D13819D770DC90EFB711647D17FFA0164301D7362FAD27CAF893E560427FE377F8B1EC76B0116BE0F5F35F8C649B7CF502EFAG4E4BG47813AGE28F703348D3A387D112F3F20792BD8B4505ECBADED07D86AECD6E7B25EFEC6679AA6F51AF217C69AD366FD4
	529F207E310CBD73574CAB47CFF66F0730FE05C05BGD07B203B7288C0ABC04F03DC3F069B8728FE788A3BC95785DF083CE9624A39A5F1B00E4605811DFB3B83CBCA6750EF7C861EEE2117D987CF2528FC06CFE00B7A4C5B8BC5CE0790B4567E21602D5A06660568E51E9774870DF37D95F22EB75AAF10B6FE87C25BA3D7EEA6E671784944AD65B16726F8581D6313812D63969BF70AEBC05675GD55910AF33697B9EDAD7D58A3C4F31EC2A4FD9761433F93DB66FA02E9149ED65A1DEBF3D6E63672CF468370774
	03BC0D78DF697FDA61E8DDF7BF1E16796ECF2A386F567E65E25472C40B63AD9B5FDD45642AFEA83EC7FA82C736D0993B7C24F6D47BC5BAEF4E20F96FBD36F50A65378AC4DFB6DF8FFB267F77BC76BD52AF3EC046FAD5DC0A7FFE7F0B5B1553637E51FADBF930F7492376671C502875B9A7B2EAFDEF220FCEFC4FB3BCEA7F8D4C54D173B7B06C1D0A9F57A0D930A6F9G6BGCAG50FC1E07F6213D0864A10B9CF45A9F4467AC7CC51F4E7917A1FBDDFF0112FF87D8D10E5F89FB95DDD742F8FE0E5FCAFCC5D72284
	C9F1BB4A310DD4A99852E9D706FF259C4702AC86887A60FC9CC7F7DE2E1BE01DEA67D31F24BEDE73960EA90B975BAD129605A39C7DD6186EE10FF573BDAC86F3AC1F4F4F8207FA70FDEF42D960E3D5721F920274B0C063FBF78F2FBB0B677333C0D38F1E33EE013B7298C07B82FE67F2C86039A0340057281210D80362399A902FB19F9492EBCCCE277E3F57E74E77B9B0EF5E020973AD751B1F093896DA8DFD578338F2A05F819C81B88B50832083248164G64832C81A882A88328D8682E2C81B83050385FF762
	330165DE85C8E6B862EC2AA0E1F7E3F2F4123A7DA44C53E54C3527625D8966EAD7E1A5D5FD08E24997BD4CEF9E72CFF03FF9935F30BE4C6A392058B194E57785FF6347B9DD8984B45108112F7AF79A38B62DDD51A4FC4FC0FDEF49E55C3FD9446FD8F96E9E944D393BF64FC987DF67443599DEC857AEF7E1BCF747F35D5AF1676B1B4B11BF1E64381A1E495511DFA17EDF38B0310E7CBA4197AFCC2CA3BF6E61B1D886B44FA2DEDF7C0FE0663F8270850BA676EDDA4B7C1E6C396B97D1BE94779141EDE4B8F6DFB6
	41370D5B83497D655500F52EFBBDB0BE576D8DD8673A7A00B957D15CFB64DB47A0440C4768F75840F59A3823E4CE0C3BA879751001BFC1644278D691B3B45EC1903986894E433F51B661BA996EDCG7BB0289CD2D4BE76FCG578D81C916E95BCD7AC8918881439E17080CD2D4B0495DE36062F2DF896235B276C09A420CA612B33971ED8879FE6882F40C8459374121BAF856AC06DC85608108822482E483AC87D83D186711E7027548BE0F500B1DF8CA22BE77359EE4787FAA536FF3BD373F16895CD69EB48E4E
	89BCB61CCE0EBBAF705858DF415F458BEF1B3E51A16EC5713531EDF1BCF7AF36F605A17BF87AE9A8F9BC15042C6369795078F87AF948BA1E7EABB4BE1E9C921DD7B5DEA2D13CCB423A50BB5DF3C2C488123A0A7EDFD6E6F9BFBC17BD0BB66928AE59E7331A0AF670AA21210D44C45140870D4E1FAEF6BAD0C59BFA096A5406AA30E0EDE893113B8DB9C127987E363BA281683E31FF54DC1BEF1866332D4D2B17523CD83FB43EEF50F8A9F6127FDDGFC4AD2637F0D1C6F594585D405974202AC6B025C57947163B3
	6629A1DD0C57274FBA717C9A6F43082CF6F4E239FBCDE37E9FD0CB87882A85E0592F92GGC0B7GGD0CB818294G94G88G88G4B0171B42A85E0592F92GGC0B7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6992GGGG
**end of data**/
}
}
