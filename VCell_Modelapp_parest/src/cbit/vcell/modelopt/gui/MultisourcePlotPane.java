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
package cbit.vcell.modelopt.gui;
import cbit.gui.DefaultListSelectionModelFixed;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:03:04 PM)
 * @author: Jim Schaff
 */
public class MultisourcePlotPane extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JList ivjJList1 = null;
	private MultisourcePlotListModel ivjmultisourcePlotListModel = null;
	private cbit.plot.PlotPane ivjplotPane = null;
	private cbit.vcell.modelopt.gui.DataSource[] fieldDataSources = null;
	private cbit.gui.DefaultListSelectionModelFixed ivjdefaultListSelectionModelFixed = null;
	private javax.swing.JScrollPane ivjReferenceDataListScrollPane = null;

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

/**
 * MultisourcePlotPane constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MultisourcePlotPane(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * MultisourcePlotPane constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MultisourcePlotPane(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * MultisourcePlotPane constructor comment.
 * @param isDoubleBuffered boolean
 */
public MultisourcePlotPane(boolean isDoubleBuffered) {
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
 * connEtoM1:  (MultisourcePlotPane.dataSources --> multisourcePlotListModel.dataSources)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmultisourcePlotListModel().setDataSources(this.getDataSources());
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
public cbit.vcell.modelopt.gui.DataSource[] getDataSources() {
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
private cbit.gui.DefaultListSelectionModelFixed getdefaultListSelectionModelFixed() {
	if (ivjdefaultListSelectionModelFixed == null) {
		try {
			ivjdefaultListSelectionModelFixed = new cbit.gui.DefaultListSelectionModelFixed();
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
			ivjmultisourcePlotListModel = new cbit.vcell.modelopt.gui.MultisourcePlotListModel();
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
	int firstIndex = listSelectionEvent.getFirstIndex();
	int lastIndex = listSelectionEvent.getLastIndex();
	if (firstIndex<0 || lastIndex<0){
		getplotPane().setPlot2D(new cbit.plot.Plot2D(new String[0],new cbit.plot.PlotData[0]));
	}

	//
	// make plotDatas for the reference data
	//
	java.util.Vector plotDataList = new java.util.Vector();
	java.util.Vector nameList = new java.util.Vector();
	java.util.Vector renderHintList = new java.util.Vector();

	
	
	for (int selectedIndex = 0; selectedIndex < getmultisourcePlotListModel().getSize(); selectedIndex++){
		if (((DefaultListSelectionModelFixed)listSelectionEvent.getSource()).isSelectedIndex(selectedIndex)){
			DataReference dataReference = (DataReference)getmultisourcePlotListModel().getElementAt(selectedIndex);
			if (dataReference.getDataSource().getSource() instanceof cbit.vcell.opt.ReferenceData){
				cbit.vcell.opt.ReferenceData referenceData = (cbit.vcell.opt.ReferenceData)dataReference.getDataSource().getSource();
				int timeIndex = referenceData.findColumn("t");
				if (timeIndex==-1){
					throw new RuntimeException("no time variable specified");
				}
				for (int i = 0; i < referenceData.getColumnNames().length; i++){
					if (i == timeIndex){
						continue;
					}
					if (referenceData.getColumnNames()[i].equals(dataReference.getIdentifier())){
						double[] independentValues = referenceData.getColumnData(timeIndex);
						double[] dependentValues = referenceData.getColumnData(i);
						cbit.plot.PlotData plotData = new cbit.plot.PlotData(independentValues, dependentValues);
						plotDataList.add(plotData);
						nameList.add("refData_"+referenceData.getColumnNames()[i]);
						renderHintList.add(new Integer(cbit.plot.Plot2D.RENDERHINT_DRAWPOINT));
						break;
					}
				}
			}else if (dataReference.getDataSource().getSource() instanceof cbit.vcell.simdata.ODESolverResultSet){
				cbit.vcell.simdata.ODESolverResultSet odeSolverResultSet = (cbit.vcell.simdata.ODESolverResultSet)dataReference.getDataSource().getSource();
				int t_index = odeSolverResultSet.findColumn("t");
				for (int i = 0; i < odeSolverResultSet.getColumnDescriptions().length; i++){
					if (i == t_index){
						continue;
					}
					if (odeSolverResultSet.getColumnDescriptions(i).getName().equals(dataReference.getIdentifier())){
						nameList.add("model_"+odeSolverResultSet.getColumnDescriptions(i).getName());
						double[] independentValues = odeSolverResultSet.extractColumn(t_index);
						double[] dependentValues = odeSolverResultSet.extractColumn(i);
						cbit.plot.PlotData plotData = new cbit.plot.PlotData(independentValues, dependentValues);
						plotDataList.add(plotData);
						renderHintList.add(new Integer(cbit.plot.Plot2D.RENDERHINT_DRAWLINE));
						break;
					}
				}
			}
		}
	}
	
	
	String[] labels = {"", "t", ""};	
	String[] names = (String[])cbit.util.BeanUtils.getArray(nameList,String.class);	
	cbit.plot.PlotData[] plotDatas = (cbit.plot.PlotData[])cbit.util.BeanUtils.getArray(plotDataList,cbit.plot.PlotData.class);
	boolean visibleFlags[] = new boolean[plotDatas.length];
	for (int i = 0; i < visibleFlags.length; i++){
		visibleFlags[i] = true;
	}
	int renderHints[] = new int[plotDatas.length];
	for (int i = 0; i < renderHints.length; i++){
		renderHints[i] = ((Integer)renderHintList.elementAt(i)).intValue();
	}

	cbit.plot.Plot2D plot2D = new cbit.plot.Plot2D(names,plotDatas,labels,visibleFlags,renderHints);
	getplotPane().setPlot2D(plot2D);

	return;
}


/**
 * Sets the dataSources property (cbit.vcell.modelopt.gui.DataSource[]) value.
 * @param dataSources The new value for the property.
 * @see #getDataSources
 */
public void setDataSources(cbit.vcell.modelopt.gui.DataSource[] dataSources) {
	cbit.vcell.modelopt.gui.DataSource[] oldValue = fieldDataSources;
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G710171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD09C67F5327E0CACA4108C92A85214F1E40754B8169BDCB32D26A12E52B8B113A809D25304A634D1EAFB2A26EAC3A7EAC7D3AF07B0A848923631A30F09CCECB90514E0A151D8A1A8616710C1621F73C1E2EC31F7375C9DDA6E4E3BFBBF9CA0753D6FE7EFB9764052444CBCF63F777D3D773E777B6D89B52FE51517DAEA842134DCE0FFEF178AC27BDB02F069412E17B9AE55E72F928CFFBF83F8DE
	38747EB1F8E6C15F111D762AEA61F30315504E0576CEE87F845EDF94768D967D92DE926C138BFDD9671F58347CBEEFF470FDAAC87B22F4821EA78172G074FE3FE5CFFC8BA4D70C58C4F2154A2888749186BD2AD4375C0FB92A08BA057CF47974073B859F9DD6BC5B6EEE2CB36D072E545729C2EE3D813C3E3004E9DE24FBA613FCA0F84F02FB4C2A74EC902F6BEGB2DFA68CBB1A6159B6F45D7DDB27432BB5051C12ACB74D78DD126C8FE8CD6E20373535BFA8EBDE559FD41C52106C5706C41F84F3348FF15DA2
	29122C83EBC0DBD09077E098774A0376FE0027F47C57A30837427B840026F07EAE7DCF9B23FFFC6E9161207B8B47CFA560AFD921FC65AAB17EB29A9CE52F1033F0F6224C8FC3DF0DEA2F2A87688310811281DEA5BAB05D718BF8F6B834AE7F4C0C5F579188F44AAE259F79057756D668E138914969D7DC02C04FF90754E8C24FF2C067E55F72BCAA136C8B7C4CF3C6BEA394FDF61B2542C4A6C559F3165DAA1545DEB5A6933A77BFA45C3BC8DD2C130B779E930CFB8BCF1F2FA8B7593B76EF4FD79CE66B9C53E2FB
	37E9F856EF1033BE8B6F25CC96947F96412FB26029FEBECCF4865B9950F7D4633AB1FE125352F76C9E213AF93B299CB29ECD36CC86A98D11E0BCAD17E7F16D5DC13A67F11D16D1024FB760A9AD0504C6EC97C1DFFB48DEC575CB36223D6E06F6BEC0A140E1006A303D2A9120BD4CF56C339DEFFDC49D9B92952DB36815DD12A25039F2A7BABAA922A9E22FCF55C41FD3320B0AD7F44812AA30B141CE1401E3513CA851FDA76C710E641394D1737AFD303AB8A3E9122252B1EBBA1F47B10AC4FAED5E9989B6188988
	FC5D0D1DAF42D396D56D5D00CB542458885AFFF7A74AA4A45AE1C5D8G5E29DCB23BD1DE1340FF84A0C9174349AEDC6F2A2478A43935354F6F3F9E8C58109321B00CF2BEC2FC47CEF84F8DF39927DFC6DCED44DE5592616BF4F73DE0D8476AF5A27922B24BED2C011CD78F0C77GA4C7386E7CE01C6B0E7539870446EF261B6ACEAA68F1DE046A4C7EC83C8D7D9FD99B69BB82FD27E7395F7F4B70F8825BB252F1ABF63F54F79CC838877A5BF2B66EF6772072AE6BC17EAE82DFB6GED164BFCD5D79D5561CED115
	38568EAA20F012ABF682144E3E596AB81A0E6BBC7C1E74A5433A39GC5G4722DC065F09F07E62675EAECFCBE578230FF58FAA2B109563159412FC1AD58AC8BE973C282CEF410AFBB1FB1E726BF35690FCG6C3F8B8CCE55719B2CE8232226A9DEC7D013C6A5CD737A5C317EADE4AFB5688844DC875FAB430A7C7943E07F7A78DDD6349D275F6713482169D3D87F1F905B15E4E986086E7287FD9A455F5B0DF94EF5E916790ECFF7BFAB206D4BC174C3B4F758A3E20294D03CB3208A684A0479A85A752929538699
	B5828EF320A1F83A225CB643EFF05BBC9B7D3410FFADE0B10BB1997726D876C5299D95C6E336C9E95AF1591C76AC62C398DE2534FE77F7680B4CB219B2B2E77E8A52BC33383F4F2BEA44ED53B17FF9668CBCDD5214884330EF9476A0D2A503AEFBA3120B5BD5DBAF52F684E8BEB1E72FBABB476D2A769D6C673E0C99D417DFE68BC15F2AB914E1B4D2E310C35B9C1561C93D7F3EC57D0F997A69BE47FB506ED469432084A1841D6ED57DAC2BFBDE086B3333F3257D27FB70DCE5033F1E1C47BDB74D72FC7558BC75
	073570EC846883C0591410F193D1BEAEE71E52350F0DAF1A27B49ED940F1CDF3FCDC7B82752D638BF47C6E85BA0E6E7B9F7AB86B8DBAEE62865DD73EC17725E77C4386BCC3124BCE0BA1B152240641F6DADB2F60B94BB4DF1D1D2AE46313EE703C74DEAF498320DD88F0788657512FBD477352346B59C251534D9509726E4A1B145E461BB19D957E3563D92A8B6B2F601EB2744D5F64FAF0F70FB1B6F6282A5F69A5E1191DC1E72FD107A96E1D5E9F43F34A2B28DAAFB81988BF2F7EDE249763B6C3B3F93CF96CD7
	42AD66763A4673DCF06132D186FBCFADD82A9248A015713E47A003FC59D6C1E969FC3FE991ADC14D0B31FC85DB2279581F13BC8DED70B02CDD3F4ADEC56D7127D174D11A472BD21BF8ADE43409C9983796A0DB9F5F9DC5DAE6A8FB0C3FFF6FE8E7638BBEF30F10D711ECB12BBDBC1F4A306CDDC579BA38AA46DFCE1623AC9F005239AEFA02493E658E504DBB783E0722A8B3482844D1A2E815791C176762711426874E98E92A94571952147C345B327B8ED6AB5E912329FE2B232CEDAB27E9EC0E53D4339A6CF4B5
	27692F66F08F59A878A6625D3BBC224FAD396A8D9654C4C2DB5322D36AC6D483BD5B4C71EE62CF0C8758FC4DEEDC2D6F23AC771B863BFEAE773CE814C15B4D8FCCE510F44AE3D93B1A721E335A20F7DB9CE58736F0991C19677AF384461CDB43E530EE1E6B8FEDBFB0FFF2899FBC0F38B195DFCBF95644723B79B5CBF3BDDAE33CB20DFB272E21B5C6390EBFCB703B8DF8CA6B7DDEFEDEF950D73B166745657D685BD6C1FBA7C0AEC08140A100936B38CF79E66F85E1053A2357876500CF14C797A590BC7F3F02F2
	00184F5AA9D7108E2295820FEB6AG5AD8BD6CE98518G50G52F49ADE3E3AA28D5D91D39A281C774E2ED4AF672EDBACEBBA6F4BAB4EAB0C1BC7F57771061F8826B183551365B395E1E3BEF3F89D2DD7EA5703AE815038E28F71FB101E757C4C3EB408F1EB9534F78264826C87A881283E936A1BBB394C262EF62EA4338E1E905A7C7AEBD7FCD247E531B19AAF0B3EBB9773EEAE434CA5734E446BFC5CBA144F5F8E93BE21EFA7C0AE40FE0092006AA460B3097319F775B7AB6A867A816011303514C1CA677BF337
	475F75A573627933A5197177708872A7C3DF92C09640DE00E20023GF59BB8FF477BAEFDC47E3AG578379154991B2DA4F4E5F1E9D1CDFF1DE7B064573E8AD1ECE7222F1681381A8CE71A0AE955A599BF84D765AB4628A20DD2C63FEC1F015F751F8CBF18FF94DEA5702383D0BB75CDE0D4CEC5EF40F3238F679ED2C0F406EB07A9357F6AF751B57F62F761B57F6BF6BFF1E62E18D634D8F6FEF68DF3E76EB6ECFDC7B4D8DA426F37500B91DC98366F4EE9ED81E4EED8309687CA9117F135ACB02C94DB9A04EC86C
	FCDE94FEB8FB1C0D99527CC367856E0F5D5D380609BF9EF5AAD0A5AD3AFB7EDD98758EF372463BF8AEBF9DA471965A59G79F7F1FDFCAF341CDD945E35D48FA92F5F98CCAC57EF8D1A4B35F890E922C9F53C6C1E9CBC2D77452C3FD713E6D0BCF44CE1B2DF93953734C476BFA273E9DF2279429066158E2F4B2B302A121E4B17DEBEC54ED5D5A515B8A2AB241E6CBC9E0D727368567C7D4D7AFEBD4309799F9CB6677F5D61447CCF8C2F4C3FB41C18FFF9F8E57E0343097413721AB6E76035AB466B67C692737AB7
	A3663C960CA466759BA3AB73DAB41218576F0D2C4C6B211165F9FDEF5EE8E797F4FC4E945E6748622CBF28015F5F083673577A3D4DA13007E300EB84E85FC8739DEA1F8F05D1372694F18636998DC8CE21A59967EE707039D9B89E60GC089C0CDB24F372F3E09769AE097AF2CB6EAC4BEC624A9C9414B8854C8D298442C1D66E09353469CECBE195214054FE4EE63D6F2DFDAG6DE200126425B6DD39E9A92E5684F74E84C773766F907FE25DC47376B29D7F94415B8CF81A4F87023C76F0C15FE31B74589A6231
	35F1333D0A0E7D67A97E0DEE88F0C91B394C7F2969D523CFEC9243DAEBEB279F6F0E7A6881D2FFF841DD496667EF667ED0F413FBA8E8D727C06D1C426FB6A2BF67F5FD4EFD77887B3FEFFE37D1ECDB303427D0D938D28CF5EF03230C6E7920076F19857D05A9087BFB0F318EC75E3F8F7F1A60C97BCFF90D7D283F8D02313FE3D1BF527B61B1E33F3A283F5CB01F7276781B1C37230F6F91640757A67CDEDA17CAF93232E75DD7B2CBA7368FBDCB657E3DC17DBE095CD920663636720BC34AFF5AB43FCF1ACF65B2
	370548B783E897G9CCC65E7BEC56255D1E857EC6138BD84D78F6DBE9DF7BC0238C9E8A769B8A7416D04F62E0E3B1F6076C13BD847558539DE15EC61F6F1B008F5EE5DD65E7E81E9637D99ED0702093EB35CAA609E6639DA4A9FED0F787C8E017920907791ADC0C78F0087A089A0DB677D5F165471AB811D774CED4E3B683E55F9541FDC70208EBD36157A13D29D5FC670878CF86ABB5CEE6EBB0A214FFAB72FC1775AD0A659505E87D084D082D0138635EB9A2FDBD2C64F89AB54ADB6FF208F8223ECC887A9FF39
	1E5B7B3E7D798AEE2339FF7127C25EFAEB42FBC5EB9A73BF6C19D710D90ACF4A5F209D79CB033EBDG87G8E818869762A53691C3FA7C69BA9FF5DAEAFD6522253C3EED5B9F3F62F940E21B12831FAF81A7356183ED42F5BCDF0740C9EA376EFCB0FBF3B2F917CE4FA7C597DA1444FCE063E7D69BCDEAE10D8AB86B53FCA4221AAD83711DAAD4C6F9ADA367131DF8C63BDC6C8D2B42FD314E9786C9415A13F6CF54E32F97FC86661CFE01274F9F5911423472FF823FE1F36F42640663691DF5301BA294F0D12EF23
	CE3F9C1C7175C8A268C44DF69C3BB5027E25F2BB9F77E8847DEFC041A02FC82ED1EFD452577DB6D9F79C463AG32487C3703F54B68CF6AF6AAF73A363D827398F2DB534C6B0287C35CA7BF351D4724A7C371B169730BE252AB6178180467916B3F9A0E0FC94F149A7B974271B1094F273A74EB0F203FF79A5EE75CFFAC3F1858872D7FD87720B67685DB625C3940E61EBBFF5DE6DEFF965A162F030BED096B60A7ED46EF206CB785E3F8B6F599762AB600C9G8DA0A9037B07603B2F8B44BF187806EE179BF3F77C
	A50646779F37A766751AFD655FEFF86C789D476157B47F8C2E4F71BEBB1E17668F90A44529F6141162F5FBB4BA1461E76D641B12B4C550AC17A03948EE60ABBFC34FA574BC73C0860F01DF8945746F89437BE468635745590FF58FD65B04493D3E9214E82C498A9BFFBFF771472BCD3FB5A3881954375430E772B319256C5BEF986BC463D78C430DC4133DFB44565B5551F7ED34EBE4302F2F33E364DAC75FD8476FC7F51AC6A39716195FB3B8527B0741811B598AF4FEB6FE56E3317312E464675CDC1F25AC81BF
	FD0C0F8983BF4919783B4666EB02CE5BCB445F7A24F09DAFAF1A2CDE30827C11CF7D7937DB9A6B1ADBFE55C0475AC86EB8A6299A7B0EAF71B8DEFB7FA7055A575AAD4FA720E5BF2361E8E62C0E0869C56E227B19F3BBA00759317C5DA07BFD43D8E2DB5CBD3632ADBEB0C63E33028D59E6832C267E33B10CA97E29A9D552ED7E1131EA9D572B8E79D5AF5662AC76CEA70EEF4544A6470197578E9E5F6AC2F54B70154A78263E6B0DC9E253004B5F417D5515717F0527DD1CE6199A26B5FE05F8ACC08EA97EG2481
	3394CB3E4231354E08F8F6457ABAA9632FAF338E5ED10F028B7639395EFD75BD235ED56F10ADBC0F0967210471A0FCA2F65614060D8E643D8570C3G9AC0B2C096C09EC0814041CF68FE79DA7DB27451821878E6C5747AB46A6F9EA77ECE58E92F5AD24A6F3677935CE940FD978E0C6A7F38AB267F7F3D1C7E3FFD6EDCE39D7CE37AFFA9043A51A561F7B42E9B751348239E2F2848D95FDB1398FB3CBED097D176C649A7FEAAD55A7F3A086B29CC4E6CDB9F2A8F1BFF2F78E6A299C0F43A82748EBA26A527E6935D
	7A1CED0E44B6314B3132CD5C67B00FCFB90E257169A107F9FCFA44E10CCF9437172D2B681741947F28836D075EC5750BBE51CDE2B72D41451F1F0849EF4C6B52BC94DFCBF2CA066F11F0FFE8535FF9BF755EGBCF37EC5A8D795610525E75A31796F1E4839E62D318816DE61D98F195CAB1407F175887E9CF2ABE8758B6A07428DBC874BDE8A54869AEE9AED6048BAE742D852360B7D96FBD73C8D34FC0032C986FC76AE2E6BEB5F7F65B267BCBAA34AB2E46757BBFCAEBC734ED9CD0A65C33F7EG57D7799856C9
	7B5ABFC03A278DF37E9FD0CB8788F3A617E59091GGE8AFGGD0CB818294G94G88G88G710171B4F3A617E59091GGE8AFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCA91GGGG
**end of data**/
}
}