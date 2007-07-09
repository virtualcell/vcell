package cbit.vcell.client.bionetgen;
import java.util.Vector;

import org.vcell.util.gui.DefaultListSelectionModelFixed;

import cbit.plot.DataReference;
import cbit.plot.DataSource;

/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:03:04 PM)
 * @author: Jim Schaff
 */
public class BNGDataPlotPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JList ivjJList1 = null;
	private cbit.plot.PlotPane ivjplotPane = null;
	private org.vcell.util.gui.DefaultListSelectionModelFixed ivjdefaultListSelectionModelFixed = null;
	private javax.swing.JScrollPane ivjReferenceDataListScrollPane = null;
	private DataSource fieldDataSource = null;
	private BNGDataPlotListModel ivjbngDataPlotListModel = null;

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BNGDataPlotPanel.this && (evt.getPropertyName().equals("dataSource"))) 
				connEtoM2(evt);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == BNGDataPlotPanel.this.getdefaultListSelectionModelFixed()) 
				connEtoC1(e);
		};
	};

/**
 * MultisourcePlotPane constructor comment.
 */
public BNGDataPlotPanel() {
	super();
	initialize();
}

/**
 * MultisourcePlotPane constructor comment.
 * @param layout java.awt.LayoutManager
 */
public BNGDataPlotPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * MultisourcePlotPane constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public BNGDataPlotPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * MultisourcePlotPane constructor comment.
 * @param isDoubleBuffered boolean
 */
public BNGDataPlotPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2005 10:37:41 AM)
 */
public void clearSelection() {
	getdefaultListSelectionModelFixed().clearSelection();
}


/**
 * connEtoC1:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> MultisourcePlotPane.selectionModel1_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectionModel1_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (BNGDataPlotPanel.dataSource --> bngDataPlotListModel.dataSource)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getbngDataPlotListModel().setDataSource(this.getDataSource());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (bngDataPlotListModel.this <--> JList1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJList1().setModel(getbngDataPlotListModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (defaultListSelectionModelFixed.this <--> JList1.selectionModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getJList1().setSelectionModel(getdefaultListSelectionModelFixed());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the bngDataPlotListModel property value.
 * @return cbit.vcell.client.bionetgen.BNGDataPlotListModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BNGDataPlotListModel getbngDataPlotListModel() {
	if (ivjbngDataPlotListModel == null) {
		try {
			ivjbngDataPlotListModel = new cbit.vcell.client.bionetgen.BNGDataPlotListModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbngDataPlotListModel;
}


/**
 * Gets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @return The dataSource property value.
 * @see #setDataSource
 */
public DataSource getDataSource() {
	return fieldDataSource;
}


/**
 * Return the defaultListSelectionModelFixed property value.
 * @return cbit.util.DefaultListSelectionModelFixed
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.DefaultListSelectionModelFixed getdefaultListSelectionModelFixed() {
	if (ivjdefaultListSelectionModelFixed == null) {
		try {
			ivjdefaultListSelectionModelFixed = new org.vcell.util.gui.DefaultListSelectionModelFixed();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjdefaultListSelectionModelFixed;
}


/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJList1() {
	if (ivjJList1 == null) {
		try {
			ivjJList1 = new javax.swing.JList();
			ivjJList1.setName("JList1");
			ivjJList1.setBounds(0, 0, 255, 480);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJList1;
}

/**
 * Method generated to support the promotion of the listVisible attribute.
 * @return boolean
 */
public boolean getListVisible() {
	return getReferenceDataListScrollPane().isVisible();
}


/**
 * Return the plotPane property value.
 * @return cbit.plot.PlotPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.plot.PlotPane getplotPane() {
	if (ivjplotPane == null) {
		try {
			ivjplotPane = new cbit.plot.PlotPane();
			ivjplotPane.setName("plotPane");
			ivjplotPane.setPreferredSize(new java.awt.Dimension(700, 700));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjplotPane;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getReferenceDataListScrollPane() {
	if (ivjReferenceDataListScrollPane == null) {
		try {
			ivjReferenceDataListScrollPane = new javax.swing.JScrollPane();
			ivjReferenceDataListScrollPane.setName("ReferenceDataListScrollPane");
			ivjReferenceDataListScrollPane.setAutoscrolls(true);
			ivjReferenceDataListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjReferenceDataListScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjReferenceDataListScrollPane.setPreferredSize(new java.awt.Dimension(274, 146));
			getReferenceDataListScrollPane().setViewportView(getJList1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReferenceDataListScrollPane;
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
	getdefaultListSelectionModelFixed().addListSelectionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP3SetTarget();
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
		setName("MultisourcePlotPane");
		setLayout(new java.awt.BorderLayout());
		setSize(568, 498);
		add(getplotPane(), "Center");
		add(getReferenceDataListScrollPane(), "West");
		initConnections();
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
		BNGDataPlotPanel aBNGDataPlotPanel;
		aBNGDataPlotPanel = new BNGDataPlotPanel();
		frame.setContentPane(aBNGDataPlotPanel);
		frame.setSize(aBNGDataPlotPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (9/1/2005 10:35:37 AM)
 */
public void selectAll() {
	getdefaultListSelectionModelFixed().setSelectionInterval(0,getbngDataPlotListModel().getSize()-1);
}


/**
 * Comment
 */
private void selectionModel1_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) throws Exception {
	int firstIndex = listSelectionEvent.getFirstIndex();
	int lastIndex = listSelectionEvent.getLastIndex();
	if (firstIndex<0 || lastIndex<0){
		return;
	}

	//
	// make plotDatas for the reference data
	//
	java.util.Vector<String> nameList = new java.util.Vector<String>();
	Vector<double[]> selectedValues = new Vector<double[]>();
	DataSource dataSource = getDataSource();
	int t_index = dataSource.findColumn("t");
	selectedValues.add(dataSource.getColumnData(t_index));
	
	for (int selectedIndex = 0; selectedIndex < getbngDataPlotListModel().getSize(); selectedIndex++){
		if (((DefaultListSelectionModelFixed)listSelectionEvent.getSource()).isSelectedIndex(selectedIndex)){
			DataReference dataReference = (DataReference)getbngDataPlotListModel().getElementAt(selectedIndex);
			DataSource referencedDataSource = dataReference.getDataSource();
			String[] colNames = referencedDataSource.getColumnNames();
			for (int i = 0; i < referencedDataSource.getColumnCount(); i++){
				if (i == t_index){
					continue;
				}
				if (colNames[i].equals(dataReference.getIdentifier())){
					nameList.add(colNames[i]);
					selectedValues.add(referencedDataSource.getColumnData(i));
					break;
				}
			}
		}
	}

	if (nameList.size() == 0) {
		return;
	}
	
	double[][] dataValues = new double[selectedValues.size()][];
	for (int i = 0; i < selectedValues.size(); i++){
		dataValues[i] = (double[])selectedValues.elementAt(i);
	}
	boolean visibleFlags[] = new boolean[dataValues.length];
	for (int i = 0; i < visibleFlags.length; i++){
		visibleFlags[i] = true;
	}
	
	String[] labels = {"", "t", ""};	
	String[] names = (String[])org.vcell.util.BeanUtils.getArray(nameList,String.class);	

	cbit.plot.Plot2D plot2D = new cbit.plot.SingleXPlot2D("Time", names, dataValues,labels, visibleFlags);
	getplotPane().setPlot2D(plot2D);

	return;
}


/**
 * Sets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @param dataSource The new value for the property.
 * @see #getDataSource
 */
public void setDataSource(DataSource dataSource) {
	DataSource oldValue = fieldDataSource;
	fieldDataSource = dataSource;
	firePropertyChange("dataSource", oldValue, dataSource);
}


/**
 * Method generated to support the promotion of the listVisible attribute.
 * @param arg1 boolean
 */
public void setListVisible(boolean arg1) {
	getReferenceDataListScrollPane().setVisible(arg1);
}
}