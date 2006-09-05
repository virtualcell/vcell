package org.vcell.physics.component.gui;
import org.vcell.physics.component.StronglyConnectedComponent;
import org.vcell.physics.component.VarEquationAssignment;
/**
 * Insert the type's description here.
 * Creation date: (1/30/2006 2:53:25 PM)
 * @author: Jim Schaff
 */
public class OOModelingPanel extends javax.swing.JPanel {
	private cbit.gui.graph.SimpleGraphModelPanel ivjconnectivityGraphPanel = null;
	private javax.swing.JPanel ivjEquationPanel = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private cbit.gui.graph.SimpleGraphModelPanel ivjpartitionGraphPanel = null;
	private PhysicalModelGraphPanel ivjphysicalModelGraphPanel = null;
	private cbit.gui.graph.SimpleGraphModelPanel ivjsccGraphModelPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private org.vcell.physics.component.StronglyConnectedComponent[] fieldStronglyConnectedComponents = null;
	private org.vcell.physics.component.VarEquationAssignment[] fieldVarEquationAssignments = null;
	private javax.swing.JTextArea ivjEquationTextArea = null;
	private org.vcell.physics.math.BipartiteMatchings.Matching fieldBipartiteMatchings = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OOModelingPanel.this.getsccGraphModelPanel() && (evt.getPropertyName().equals("graph"))) 
				connEtoC1(evt);
			if (evt.getSource() == OOModelingPanel.this.getpartitionGraphPanel() && (evt.getPropertyName().equals("graph"))) 
				connEtoC2(evt);
			if (evt.getSource() == OOModelingPanel.this.getconnectivityGraphPanel() && (evt.getPropertyName().equals("graph"))) 
				connEtoC3(evt);
			if (evt.getSource() == OOModelingPanel.this.getphysicalModelGraphPanel() && (evt.getPropertyName().equals("model"))) 
				connEtoC4(evt);
			if (evt.getSource() == OOModelingPanel.this && (evt.getPropertyName().equals("stronglyConnectedComponents"))) 
				connEtoC5(evt);
			if (evt.getSource() == OOModelingPanel.this && (evt.getPropertyName().equals("varEquationAssignments"))) 
				connEtoC6(evt);
		};
	};

/**
 * OOModelingPanel constructor comment.
 */
public OOModelingPanel() {
	super();
	initialize();
}

/**
 * OOModelingPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public OOModelingPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * OOModelingPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public OOModelingPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * OOModelingPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public OOModelingPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (sccGraphModelPanel.graph --> OOModelingPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("sccGraphModelPanelGraph", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (partitionGraphPanel.graph --> OOModelingPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("partitionGraphPanelGraph", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (connectivityGraphPanel.graph --> OOModelingPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("connectivityGraphPanelGraph", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (physicalModelGraphPanel.model --> OOModelingPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("physicalModelGraphPanelModel", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (OOModelingPanel.stronglyConnectedComponents --> OOModelingPanel.displaySCC()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displaySCC();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (OOModelingPanel.varEquationAssignments --> OOModelingPanel.displaySCC()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displaySCC();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (OOModelingPanel.initialize() --> OOModelingPanel.oOModelingPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.oOModelingPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void displaySCC() {
	if (fieldStronglyConnectedComponents==null){
		getEquationTextArea().setText("no SCCs");
		return;
	}
	if (fieldVarEquationAssignments==null){
		getEquationTextArea().setText("no varEquationAssignments");
		return;
	}
	cbit.util.graph.Node[] sortedPartitionNodes = getPartitionGraphPanelGraph().topologicalSort();
	for (int i = 0; i < sortedPartitionNodes.length/2; i++){
		cbit.util.graph.Node temp = sortedPartitionNodes[i];
		sortedPartitionNodes[i] = sortedPartitionNodes[sortedPartitionNodes.length-1-i];
		sortedPartitionNodes[sortedPartitionNodes.length-1-i] = temp;
	}
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < fieldStronglyConnectedComponents.length; i++){
		StronglyConnectedComponent scc = fieldStronglyConnectedComponents[i];
//		buffer.append("component("+scc.getName()+") = "+scc.toString(fieldVarEquationAssignments)+"\n");
		//
		// print final equations for this SCC
		//
		for (int j = 0; j < sortedPartitionNodes.length; j++){
			if (scc.contains(sortedPartitionNodes[j].index)){
				VarEquationAssignment varEqnAssignment = (VarEquationAssignment)sortedPartitionNodes[j].getData();
				if (varEqnAssignment.getSolution()!=null){
					if (varEqnAssignment.isStateVariable()){
						buffer.append(scc.getName()+":\t"+varEqnAssignment.getSymbol().getName()+org.vcell.physics.component.Symbol.DERIVATIVE_SUFFIX+" = "+varEqnAssignment.getSolution().infix()+"\n");
					}else{
						buffer.append(scc.getName()+":\t"+varEqnAssignment.getSymbol().getName()+" = "+varEqnAssignment.getSolution().infix()+"\n");
					}
				}else{
					buffer.append(scc.getName()+":\tNOT SOLVED\t"+varEqnAssignment.getSymbol().getName()+" || "+varEqnAssignment.getEquation().infix()+"\n");
				}	
			}
		}
	}
	getEquationTextArea().setText(buffer.toString());
}


/**
 * Gets the bipartiteMatchings property (ncbc.physics2.BipartiteMatchings) value.
 * @return The bipartiteMatchings property value.
 * @see #setBipartiteMatchings
 */
public org.vcell.physics.math.BipartiteMatchings.Matching getBipartiteMatchings() {
	return fieldBipartiteMatchings;
}


/**
 * Return the connectivityGraphPanel property value.
 * @return cbit.gui.graph.SimpleGraphModelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.graph.SimpleGraphModelPanel getconnectivityGraphPanel() {
	if (ivjconnectivityGraphPanel == null) {
		try {
			ivjconnectivityGraphPanel = new cbit.gui.graph.SimpleGraphModelPanel();
			ivjconnectivityGraphPanel.setName("connectivityGraphPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjconnectivityGraphPanel;
}


/**
 * Method generated to support the promotion of the connectivityGraphPanelGraph attribute.
 * @return cbit.util.graph.Graph
 */
public cbit.util.graph.Graph getConnectivityGraphPanelGraph() {
	return getconnectivityGraphPanel().getGraph();
}


/**
 * Return the EquationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getEquationPanel() {
	if (ivjEquationPanel == null) {
		try {
			ivjEquationPanel = new javax.swing.JPanel();
			ivjEquationPanel.setName("EquationPanel");
			ivjEquationPanel.setLayout(new java.awt.BorderLayout());
			getEquationPanel().add(getEquationTextArea(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEquationPanel;
}

/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getEquationTextArea() {
	if (ivjEquationTextArea == null) {
		try {
			ivjEquationTextArea = new javax.swing.JTextArea();
			ivjEquationTextArea.setName("EquationTextArea");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEquationTextArea;
}

/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("Physical Model", null, getphysicalModelGraphPanel(), null, 0);
			ivjJTabbedPane1.insertTab("bipartite graph", null, getconnectivityGraphPanel(), null, 1);
			ivjJTabbedPane1.insertTab("partition graph", null, getpartitionGraphPanel(), null, 2);
			ivjJTabbedPane1.insertTab("connected components", null, getsccGraphModelPanel(), null, 3);
			ivjJTabbedPane1.insertTab("Equations", null, getEquationPanel(), null, 4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}


/**
 * Return the partitionGraphPanel property value.
 * @return cbit.gui.graph.SimpleGraphModelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.graph.SimpleGraphModelPanel getpartitionGraphPanel() {
	if (ivjpartitionGraphPanel == null) {
		try {
			ivjpartitionGraphPanel = new cbit.gui.graph.SimpleGraphModelPanel();
			ivjpartitionGraphPanel.setName("partitionGraphPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpartitionGraphPanel;
}


/**
 * Method generated to support the promotion of the partitionGraphPanelGraph attribute.
 * @return cbit.util.graph.Graph
 */
public cbit.util.graph.Graph getPartitionGraphPanelGraph() {
	return getpartitionGraphPanel().getGraph();
}


/**
 * Return the physicalModelGraphPanel property value.
 * @return ncbc.physics2.component.gui.PhysicalModelGraphPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PhysicalModelGraphPanel getphysicalModelGraphPanel() {
	if (ivjphysicalModelGraphPanel == null) {
		try {
			ivjphysicalModelGraphPanel = new org.vcell.physics.component.gui.PhysicalModelGraphPanel();
			ivjphysicalModelGraphPanel.setName("physicalModelGraphPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjphysicalModelGraphPanel;
}


/**
 * Method generated to support the promotion of the physicalModelGraphPanelModel attribute.
 * @return ncbc.physics2.component.Model
 */
public org.vcell.physics.component.Model getPhysicalModelGraphPanelModel() {
	return getphysicalModelGraphPanel().getModel();
}


/**
 * Return the sccGraphModelPanel property value.
 * @return cbit.gui.graph.SimpleGraphModelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.graph.SimpleGraphModelPanel getsccGraphModelPanel() {
	if (ivjsccGraphModelPanel == null) {
		try {
			ivjsccGraphModelPanel = new cbit.gui.graph.SimpleGraphModelPanel();
			ivjsccGraphModelPanel.setName("sccGraphModelPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjsccGraphModelPanel;
}


/**
 * Method generated to support the promotion of the sccGraphModelPanelGraph attribute.
 * @return cbit.util.graph.Graph
 */
public cbit.util.graph.Graph getSccGraphModelPanelGraph() {
	return getsccGraphModelPanel().getGraph();
}


/**
 * Gets the stronglyConnectedComponents property (ncbc.physics2.component.StronglyConnectedComponent[]) value.
 * @return The stronglyConnectedComponents property value.
 * @see #setStronglyConnectedComponents
 */
public org.vcell.physics.component.StronglyConnectedComponent[] getStronglyConnectedComponents() {
	return fieldStronglyConnectedComponents;
}


/**
 * Gets the varEquationAssignments property (ncbc.physics2.component.VarEquationAssignment[]) value.
 * @return The varEquationAssignments property value.
 * @see #setVarEquationAssignments
 */
public org.vcell.physics.component.VarEquationAssignment[] getVarEquationAssignments() {
	return fieldVarEquationAssignments;
}


/**
 * Gets the varEquationAssignments index property (ncbc.physics2.component.VarEquationAssignment) value.
 * @return The varEquationAssignments property value.
 * @param index The index value into the property array.
 * @see #setVarEquationAssignments
 */
public org.vcell.physics.component.VarEquationAssignment getVarEquationAssignments(int index) {
	return getVarEquationAssignments()[index];
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getsccGraphModelPanel().addPropertyChangeListener(ivjEventHandler);
	getpartitionGraphPanel().addPropertyChangeListener(ivjEventHandler);
	getconnectivityGraphPanel().addPropertyChangeListener(ivjEventHandler);
	getphysicalModelGraphPanel().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("OOModelingPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(838, 762);
		add(getJTabbedPane1(), "Center");
		initConnections();
		connEtoC7();
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
		OOModelingPanel aOOModelingPanel;
		aOOModelingPanel = new OOModelingPanel();
		frame.setContentPane(aOOModelingPanel);
		frame.setSize(aOOModelingPanel.getSize());
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
public void oOModelingPanel_Initialize() {
	java.awt.event.ActionListener selectTask = new java.awt.event.ActionListener(){
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			try {
				if (getConnectivityGraphPanelGraph()!=null && getBipartiteMatchings()!=null){
					cbit.util.graph.Edge[] edges = getConnectivityGraphPanelGraph().getEdges();
					cbit.util.graph.Node[] nodes = getConnectivityGraphPanelGraph().getNodes();
					System.out.println("selecting matched edges and unmatched nodes");			
					cbit.gui.graph.SimpleGraphModel graphModel = getconnectivityGraphPanel().getSimpleGraphModel();
					for (int i = 0; i < edges.length; i++){
						if (getBipartiteMatchings().contains(edges[i].getNode1().index,edges[i].getNode2().index)){
							cbit.gui.graph.EdgeShape edgeShape = (cbit.gui.graph.EdgeShape)graphModel.getShapeFromModelObject(edges[i]);
							if (edgeShape!=null){
								edgeShape.select();
							}
						}
					}
					for (int i = 0; i < nodes.length; i++){
						if (!getBipartiteMatchings().contains(nodes[i].index)){
							cbit.gui.graph.ElipseShape nodeShape = (cbit.gui.graph.ElipseShape)graphModel.getShapeFromModelObject(nodes[i]);
							if (nodeShape!=null){
								nodeShape.select();
							}
						}
					}
					graphModel.fireGraphChanged();
				}
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
	};
	javax.swing.Timer timer = new javax.swing.Timer(10000,selectTask);
	timer.setRepeats(true);
	timer.setInitialDelay(10000);
	timer.setDelay(10000);
	timer.start();
}


/**
 * Sets the bipartiteMatchings property (ncbc.physics2.BipartiteMatchings) value.
 * @param bipartiteMatchings The new value for the property.
 * @see #getBipartiteMatchings
 */
public void setBipartiteMatchings(org.vcell.physics.math.BipartiteMatchings.Matching bipartiteMatchings) {
	org.vcell.physics.math.BipartiteMatchings.Matching oldValue = fieldBipartiteMatchings;
	fieldBipartiteMatchings = bipartiteMatchings;
	firePropertyChange("bipartiteMatchings", oldValue, bipartiteMatchings);
}


/**
 * Method generated to support the promotion of the connectivityGraphPanelGraph attribute.
 * @param arg1 cbit.util.graph.Graph
 */
public void setConnectivityGraphPanelGraph(cbit.util.graph.Graph arg1) {
	getconnectivityGraphPanel().setGraph(arg1);
}


/**
 * Method generated to support the promotion of the partitionGraphPanelGraph attribute.
 * @param arg1 cbit.util.graph.Graph
 */
public void setPartitionGraphPanelGraph(cbit.util.graph.Graph arg1) {
	getpartitionGraphPanel().setGraph(arg1);
}


/**
 * Method generated to support the promotion of the physicalModelGraphPanelModel attribute.
 * @param arg1 ncbc.physics2.component.Model
 */
public void setPhysicalModelGraphPanelModel(org.vcell.physics.component.Model arg1) {
	getphysicalModelGraphPanel().setModel(arg1);
}


/**
 * Method generated to support the promotion of the sccGraphModelPanelGraph attribute.
 * @param arg1 cbit.util.graph.Graph
 */
public void setSccGraphModelPanelGraph(cbit.util.graph.Graph arg1) {
	getsccGraphModelPanel().setGraph(arg1);
}


/**
 * Sets the stronglyConnectedComponents property (ncbc.physics2.component.StronglyConnectedComponent[]) value.
 * @param stronglyConnectedComponents The new value for the property.
 * @see #getStronglyConnectedComponents
 */
public void setStronglyConnectedComponents(org.vcell.physics.component.StronglyConnectedComponent[] stronglyConnectedComponents) {
	org.vcell.physics.component.StronglyConnectedComponent[] oldValue = fieldStronglyConnectedComponents;
	fieldStronglyConnectedComponents = stronglyConnectedComponents;
	firePropertyChange("stronglyConnectedComponents", oldValue, stronglyConnectedComponents);
}


/**
 * Sets the varEquationAssignments property (ncbc.physics2.component.VarEquationAssignment[]) value.
 * @param varEquationAssignments The new value for the property.
 * @see #getVarEquationAssignments
 */
public void setVarEquationAssignments(org.vcell.physics.component.VarEquationAssignment[] varEquationAssignments) {
	org.vcell.physics.component.VarEquationAssignment[] oldValue = fieldVarEquationAssignments;
	fieldVarEquationAssignments = varEquationAssignments;
	firePropertyChange("varEquationAssignments", oldValue, varEquationAssignments);
}


/**
 * Sets the varEquationAssignments index property (ncbc.physics2.component.VarEquationAssignment[]) value.
 * @param index The index value into the property array.
 * @param varEquationAssignments The new value for the property.
 * @see #getVarEquationAssignments
 */
public void setVarEquationAssignments(int index, org.vcell.physics.component.VarEquationAssignment varEquationAssignments) {
	org.vcell.physics.component.VarEquationAssignment oldValue = fieldVarEquationAssignments[index];
	fieldVarEquationAssignments[index] = varEquationAssignments;
	if (oldValue != null && !oldValue.equals(varEquationAssignments)) {
		firePropertyChange("varEquationAssignments", null, fieldVarEquationAssignments);
	};
}
}