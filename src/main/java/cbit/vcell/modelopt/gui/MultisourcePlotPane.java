/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.vcell.util.BeanUtils;
import org.vcell.util.ColorUtil;
import org.vcell.util.Range;
import org.vcell.util.gui.DefaultListSelectionModelFixed;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.gui.PlotPane;
import cbit.vcell.modelopt.DataReference;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotListModel.SortDataReferenceHelper;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:03:04 PM)
 * @author: Jim Schaff
 */
public class MultisourcePlotPane extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JList<DataReference> ivjJList1 = null;
	private MultisourcePlotListModel ivjmultisourcePlotListModel = null;
	private PlotPane ivjplotPane = null;
	private DataSource[] fieldDataSources = null;
	private DefaultListSelectionModelFixed ivjdefaultListSelectionModelFixed = null;
	private javax.swing.JScrollPane ivjReferenceDataListScrollPane = null;
	
	private Color[] autoContrastColors;
	private JPanel panel;
	
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
		autoContrastColors = ColorUtil.generateAutoColor(getmultisourcePlotListModel().getSize(), getBackground(),new Integer(0));
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
private DataSource[] getDataSources() {
	return fieldDataSources;
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
private javax.swing.JList<DataReference> getJList1() {
	if (ivjJList1 == null) {
		try {
			ivjJList1 = new javax.swing.JList<DataReference>();
			ivjJList1.setName("JList1");
			ivjJList1.setBounds(0, 0, 255, 480);
			ivjJList1.setCellRenderer(new ListCellRenderer<DataReference>() {
				DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();

				private Boolean isEvenMatchedSet(int index0){
					if(ivjJList1.getModel().getSize() == 0){
						return null;
					}
					if(getmultisourcePlotListModel().getSortedDataReferences().get(index0).matchCount == null){
						//create matched set group counts (this happens 1 time)
						int masterMatchCount = 0;
						for(int i=0;i<ivjJList1.getModel().getSize();i++){
							DataSource dataSource = getmultisourcePlotListModel().getSortedDataReferences().get(i).dataReference.getDataSource();
							if(dataSource instanceof DataSource.DataSourceReferenceData){
								if(i != ivjJList1.getModel().getSize()-1){
									SortDataReferenceHelper mySortDataReferenceHelper = getmultisourcePlotListModel().getSortedDataReferences().get(i);
									SortDataReferenceHelper potentialMatchSortDataReferenceHelper = getmultisourcePlotListModel().getSortedDataReferences().get(i+1);
									if(potentialMatchSortDataReferenceHelper.getReferenceDataMappingSpec() != null && mySortDataReferenceHelper != null && potentialMatchSortDataReferenceHelper.getReferenceDataMappingSpec().getReferenceDataColumnName().equals(mySortDataReferenceHelper.dataReference.getIdentifier())){
										mySortDataReferenceHelper.matchCount = masterMatchCount;
										potentialMatchSortDataReferenceHelper.matchCount = masterMatchCount;
										masterMatchCount++;
									}else{
										mySortDataReferenceHelper.matchCount = -1;
									}
								}else{
									getmultisourcePlotListModel().getSortedDataReferences().get(i).matchCount = -1;
								}
							}else if(getmultisourcePlotListModel().getSortedDataReferences().get(i).matchCount == null){
								getmultisourcePlotListModel().getSortedDataReferences().get(i).matchCount = -1;
							}
						}
					}
					if(getmultisourcePlotListModel().getSortedDataReferences().get(index0).matchCount == -1){//not part of matched set
						return null;
					}
					return getmultisourcePlotListModel().getSortedDataReferences().get(index0).matchCount % 2  == 0;
				}
				@Override
				public Component getListCellRendererComponent(
						JList<? extends DataReference> list,
						DataReference value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component comp = defaultListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					try{
						if(bGroupingListSorter && comp instanceof JLabel){
							if (isSelected) {
								comp.setBackground(getJList1().getSelectionBackground());
								comp.setForeground(getJList1().getSelectionForeground());
							} else {
								Boolean evenMatch = isEvenMatchedSet(index);
								comp.setBackground(evenMatch == null || evenMatch ? getJList1().getBackground() : DefaultScrollTableCellRenderer.everyOtherRowColor);
								comp.setForeground(getJList1().getForeground());
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					return comp;
				}
			});
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

public int[] getUnsortedSelectedIndices()
{
	int[] selectedIndices = getJList1().getSelectedIndices();
	if(getmultisourcePlotListModel().getSortedDataReferences() != null){
		if(getmultisourcePlotListModel().getSortedDataReferences().size() != getJList1().getModel().getSize()){
			throw new RuntimeException(this.getClass().getName()+": sort size not match list size");
		}
		int[] unsortedSelectedIndices = new int[selectedIndices.length];
		for (int i = 0; i < selectedIndices.length; i++) {
			unsortedSelectedIndices[i] = getmultisourcePlotListModel().getSortedDataReferences().get(selectedIndices[i]).unsortedIndex;
		}
		return unsortedSelectedIndices;
	}
	return selectedIndices;
}

public void setUnsortedSelectedIndices(int[] unsortedSelectedIndices)
{
	int[] selectedIndices = null;
	if(unsortedSelectedIndices != null && getmultisourcePlotListModel().getSortedDataReferences() != null){
		if(getmultisourcePlotListModel().getSortedDataReferences().size() != getJList1().getModel().getSize()){
			throw new RuntimeException(this.getClass().getName()+": sort size not match list size");
		}
		int[] sortedSelectedIndices = new int[unsortedSelectedIndices.length];
		for (int i = 0; i < unsortedSelectedIndices.length; i++) {
			for (int j = 0; j < getmultisourcePlotListModel().getSortedDataReferences().size(); j++) {
				if(getmultisourcePlotListModel().getSortedDataReferences().get(j).unsortedIndex == unsortedSelectedIndices[i]){
					sortedSelectedIndices[i] = j;
					break;
				}
			}
		}
		selectedIndices = sortedSelectedIndices;
		
	}else{
		selectedIndices = unsortedSelectedIndices;
	}
	getJList1().setSelectedIndices(selectedIndices);
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
		} catch (java.lang.Throwable ivjExc) {
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
			getReferenceDataListScrollPane().setViewportView(getJList1());
		} catch (java.lang.Throwable ivjExc) {
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
		add(getPanel(), BorderLayout.WEST);
		add(getplotPane(), BorderLayout.CENTER);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
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
		getplotPane().setPlot2D(new Plot2D(null,null,new String[0],new PlotData[0]));
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
		String prefix = dataSource.getName() + ": ";// instanceof DataSource.DataSourceReferenceData ? refDataLabelPrefix : modelDataLabelPrefix;
		
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
				int unsortedSelecteIndex = (getmultisourcePlotListModel().getSortedDataReferences()==null?selectedIndex:getmultisourcePlotListModel().getSortedDataReferences().get(selectedIndex).unsortedIndex);
				colorV.add(autoContrastColors[unsortedSelecteIndex]);
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

	Plot2D plot2D = new Plot2D(null,null,names,plotDatas,labels,visibleFlags,renderHints);
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
	setDataSources(dataSources, null);
}

public void setDataSources(DataSource[] dataSources, Color[] colorArray) {
	DataSource[] oldValue = fieldDataSources;
	fieldDataSources = dataSources;
	
	firePropertyChange("dataSources", oldValue, dataSources);
	if(colorArray != null)
	{
		if(dataSources != null && colorArray.length != getmultisourcePlotListModel().getSize())
		{
			throw new IllegalArgumentException("Length of color arry doesn't match the size of MultiSoucePlotList.");
		}
		autoContrastColors = colorArray;
	}
	
}

/**
 * Method generated to support the promotion of the listVisible attribute.
 * @param arg1 boolean
 */
public void setListVisible(boolean arg1) {
	getReferenceDataListScrollPane().setVisible(arg1);
}

public void forceXYRange(Range xRange,Range yRange) {
	getplotPane().forceXYRange(xRange, yRange);
}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getReferenceDataListScrollPane(), BorderLayout.CENTER);
//			panel.add(getChckbxNewCheckBox(), BorderLayout.SOUTH);
		}
		return panel;
	}
//	private JCheckBox getChckbxNewCheckBox() {
//		if (chckbxNewCheckBox == null) {
//			chckbxNewCheckBox = new JCheckBox("Sort");
//			chckbxNewCheckBox.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					Object[] selectedValues = getJList1().getSelectedValues();
//					getmultisourcePlotListModel().setSort(chckbxNewCheckBox.isSelected());
//					if(selectedValues != null && selectedValues.length > 0){
//						int[] selectedIndices = new int[selectedValues.length];
//						for (int j = 0; j < selectedValues.length; j++) {
//							DataReference selectedDataReference = (DataReference)selectedValues[j];
//							for (int i = 0; i < getJList1().getModel().getSize(); i++) {
//								DataReference dataReference = (DataReference)((AbstractListModel)(getJList1().getModel())).getElementAt(i);
//								if(selectedDataReference.getIdentifier().equals(dataReference.getIdentifier()) && selectedDataReference.getDataSource().getName().equals(dataReference.getDataSource().getName())){
//									selectedIndices[j] = i;
//									break;
//								}
//							}
//						}
//						getJList1().setSelectedIndices(selectedIndices);
//					}
//				}
//			});
//		}
//		return chckbxNewCheckBox;
//	}
	
	private boolean bGroupingListSorter = false;
	public void setGroupingListSorter(Comparator<SortDataReferenceHelper> comparatorDataSource){
		bGroupingListSorter = (comparatorDataSource==null?false:true);
		getmultisourcePlotListModel().setSort(comparatorDataSource);
	}
}
