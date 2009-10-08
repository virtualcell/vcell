package cbit.vcell.modelopt.gui;
import java.awt.Color;
import java.util.Vector;

import org.vcell.util.Range;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultListSelectionModelFixed;

import cbit.plot.Plot2D;
import cbit.plot.Plot2DPanel;
import cbit.plot.PlotData;
import cbit.plot.PlotPane;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:03:04 PM)
 * @author: Jim Schaff
 */
public class MultisourcePlotPane extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JList ivjJList1 = null;
	private MultisourcePlotListModel ivjmultisourcePlotListModel = null;
	private PlotPane ivjplotPane = null;
	private DataSource[] fieldDataSources = null;
	private DefaultListSelectionModelFixed ivjdefaultListSelectionModelFixed = null;
	private javax.swing.JScrollPane ivjReferenceDataListScrollPane = null;
	
	private String refDataLabelPrefix = "refData_";
	private String modelDataLabelPrefix = "model_";

	private Color[] autoContrastColors;
	
class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MultisourcePlotPane.this && (evt.getPropertyName().equals("dataSources"))) 
				connEtoM1(evt);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == MultisourcePlotPane.this.getdefaultListSelectionModelFixed()) 
				connEtoC1(e);
		};
	};

/**
 * MultisourcePlotPane constructor comment.
 */
public MultisourcePlotPane() {
	super();
	initialize();
}

public Color[] getAutoContrastColorsInListOrder(){
	return autoContrastColors.clone();
}
private void createAutoContrastColors(){
	if(getmultisourcePlotListModel() == null || getmultisourcePlotListModel().getSize() <=0){
		return;
	}
	if(autoContrastColors == null || getmultisourcePlotListModel().getSize() > autoContrastColors.length){
		autoContrastColors = Plot2DPanel.generateAutoColor(getmultisourcePlotListModel().getSize(), getBackground(),new Integer(0));
	}
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
 * connEtoM1:  (MultisourcePlotPane.dataSources --> multisourcePlotListModel.dataSources)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmultisourcePlotListModel().setDataSources(this.getDataSources());
		createAutoContrastColors();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (multisourcePlotListModel.this <--> JList1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJList1().setModel(getmultisourcePlotListModel());
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
 * Gets the dataSources property (cbit.vcell.modelopt.gui.DataSource[]) value.
 * @return The dataSources property value.
 * @see #setDataSources
 */
public DataSource[] getDataSources() {
	return fieldDataSources;
}


/**
 * Gets the dataSources index property (cbit.vcell.modelopt.gui.DataSource) value.
 * @return The dataSources property value.
 * @param index The index value into the property array.
 * @see #setDataSources
 */
public DataSource getDataSources(int index) {
	return getDataSources()[index];
}


/**
 * Return the defaultListSelectionModelFixed property value.
 * @return cbit.util.DefaultListSelectionModelFixed
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DefaultListSelectionModelFixed getdefaultListSelectionModelFixed() {
	if (ivjdefaultListSelectionModelFixed == null) {
		try {
			ivjdefaultListSelectionModelFixed = new DefaultListSelectionModelFixed();
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
 * Return the multisourcePlotListModel property value.
 * @return cbit.vcell.modelopt.gui.MultisourcePlotListModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MultisourcePlotListModel getmultisourcePlotListModel() {
	if (ivjmultisourcePlotListModel == null) {
		try {
			ivjmultisourcePlotListModel = new MultisourcePlotListModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmultisourcePlotListModel;
}


/**
 * Return the plotPane property value.
 * @return cbit.plot.PlotPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PlotPane getplotPane() {
	if (ivjplotPane == null) {
		try {
			ivjplotPane = new PlotPane();
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
	this.addPropertyChangeListener(ivjEventHandler);
	getdefaultListSelectionModelFixed().addListSelectionListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP3SetTarget();
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
		MultisourcePlotPane aMultisourcePlotPane;
		aMultisourcePlotPane = new MultisourcePlotPane();
		frame.setContentPane(aMultisourcePlotPane);
		frame.setSize(aMultisourcePlotPane.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
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
public void select(String[] columnNames) {
	for (int j = 0; j < getJList1().getModel().getSize(); j ++) {
		DataReference listElement = (DataReference)getJList1().getModel().getElementAt(j);
		
		for (int i = 0; i < columnNames.length; i ++) {
			if (columnNames[i].equals("t")) {
				continue;
			}			
			
			if (listElement.getIdentifier().equals(columnNames[i])) {
				getdefaultListSelectionModelFixed().addSelectionInterval(j,j);
				break;	
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2005 10:35:37 AM)
 */
public void selectAll() {
	getdefaultListSelectionModelFixed().setSelectionInterval(0,getmultisourcePlotListModel().getSize()-1);
}


/**
 * Comment
 */
private void selectionModel1_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) throws Exception {
	if(listSelectionEvent.getValueIsAdjusting() == true){
		return;
	}
	int firstIndex = listSelectionEvent.getFirstIndex();
	int lastIndex = listSelectionEvent.getLastIndex();
	if (firstIndex<0 || lastIndex<0){
		getplotPane().setPlot2D(new Plot2D(null,new String[0],new PlotData[0]));
	}

	//
	// make plotDatas for the reference data
	//
	Vector<PlotData> plotDataList = new Vector<PlotData>();
	Vector<String> nameList = new Vector<String>();
	Vector<Integer> renderHintList = new Vector<Integer>();

	Vector<Color> colorV = new Vector<Color>();
	
	int[] selectedIndices = getJList1().getSelectedIndices();
	for (int ii = 0; ii < selectedIndices.length; ii++){
		int selectedIndex = selectedIndices[ii];
		DataReference dataReference = (DataReference)getmultisourcePlotListModel().getElementAt(selectedIndex);
		DataSource dataSource = dataReference.getDataSource();
		String prefix = dataSource instanceof DataSource.DataSourceReferenceData ? refDataLabelPrefix : modelDataLabelPrefix;
		
		String[] columnNames = dataSource.getColumnNames();
		int timeIndex = dataSource.getTimeColumnIndex();
		if (timeIndex==-1){
			throw new RuntimeException("no time variable specified");
		}
		for (int i = 0; i < columnNames.length; i++){
			if (i == timeIndex){
				continue;
			}
			if (columnNames[i].equals(dataReference.getIdentifier())){
				double[] independentValues = dataSource.getColumnData(timeIndex);
				double[] dependentValues = dataSource.getColumnData(i);
				PlotData plotData = new PlotData(independentValues, dependentValues);
				plotDataList.add(plotData);
				colorV.add(autoContrastColors[selectedIndex]);
				nameList.add(prefix+columnNames[i]);
				renderHintList.add(dataSource.getRenderHints());
				break;
			}
		}
	}
	
	String[] labels = {"", "t", ""};	
	String[] names = (String[])BeanUtils.getArray(nameList,String.class);	
	PlotData[] plotDatas = (PlotData[])BeanUtils.getArray(plotDataList,PlotData.class);
	boolean visibleFlags[] = new boolean[plotDatas.length];
	for (int i = 0; i < visibleFlags.length; i++){
		visibleFlags[i] = true;
	}
	int renderHints[] = new int[plotDatas.length];
	for (int i = 0; i < renderHints.length; i++){
		renderHints[i] = ((Integer)renderHintList.elementAt(i)).intValue();
	}

	Plot2D plot2D = new Plot2D(null,names,plotDatas,labels,visibleFlags,renderHints);
	Color[] colorArr = colorV.toArray(new Color[0]);
//	if(colorV.size() == plot2D.getNumberOfPlots()){
//		colorArr = colorV.toArray(new Color[0]);
//	}
	getplotPane().setPlot2D(plot2D,colorArr);

	return;
}


/**
 * Sets the dataSources property (cbit.vcell.modelopt.gui.DataSource[]) value.
 * @param dataSources The new value for the property.
 * @see #getDataSources
 */
public void setDataSources(DataSource[] dataSources) {
	DataSource[] oldValue = fieldDataSources;
	fieldDataSources = dataSources;
	firePropertyChange("dataSources", oldValue, dataSources);
}


/**
 * Sets the dataSources index property (cbit.vcell.modelopt.gui.DataSource[]) value.
 * @param index The index value into the property array.
 * @param dataSources The new value for the property.
 * @see #getDataSources
 */
public void setDataSources(int index, DataSource dataSources) {
	DataSource oldValue = fieldDataSources[index];
	fieldDataSources[index] = dataSources;
	if (oldValue != null && !oldValue.equals(dataSources)) {
		firePropertyChange("dataSources", null, fieldDataSources);
	};
}


/**
 * Method generated to support the promotion of the listVisible attribute.
 * @param arg1 boolean
 */
public void setListVisible(boolean arg1) {
	getReferenceDataListScrollPane().setVisible(arg1);
}

public String getRefDataLabelPrefix() {
	return refDataLabelPrefix;
}

public void setRefDataLabelPrefix(String refDataLabelPrefix) {
	this.refDataLabelPrefix = refDataLabelPrefix;
}

public String getModelDataLabelPrefix() {
	return modelDataLabelPrefix;
}

public void setModelDataLabelPrefix(String modelDataLabelPrefix) {
	this.modelDataLabelPrefix = modelDataLabelPrefix;
}
public void forceXYRange(Range xRange,Range yRange) {
	getplotPane().forceXYRange(xRange, yRange);
}
}