package cbit.vcell.modelopt.gui;
import cbit.util.DefaultListSelectionModelFixed;
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
	private cbit.util.DefaultListSelectionModelFixed ivjdefaultListSelectionModelFixed = null;
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
private cbit.util.DefaultListSelectionModelFixed getdefaultListSelectionModelFixed() {
	if (ivjdefaultListSelectionModelFixed == null) {
		try {
			ivjdefaultListSelectionModelFixed = new cbit.util.DefaultListSelectionModelFixed();
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
		getplotPane().setPlot2D(new cbit.plot.Plot2D(null,new String[0],new cbit.plot.PlotData[0]));
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
			}else if (dataReference.getDataSource().getSource() instanceof cbit.vcell.solver.ode.ODESolverResultSet){
				cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = (cbit.vcell.solver.ode.ODESolverResultSet)dataReference.getDataSource().getSource();
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

	cbit.plot.Plot2D plot2D = new cbit.plot.Plot2D(null,names,plotDatas,labels,visibleFlags,renderHints);
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
	D0CB838494G88G88GF6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD0D46735CCCB72FC93A7319D5BE042345417B634450C2FCDDFED074E180E731EFD49EB8CC392BBA1AFF9AF66554C50D747B8AF760D535CDDD816CDD0C944A41DD0CBD2D3C191C1D090D1D916C5108596961154D5383BFB01557D4B3DF7E1C1541E73FD773BFBF7398B51E918B95C7B1D6F6F1C731D5F6FAED77127AC1341D841F18693277CB59BB82E7D98471D75F739982E65D01E05537CFD8470
	8EF742D48C4FEE684B3A18E7A967FEBCD38AEDB734BB217DBE3C6F63F2E78A0E42CB0AFD9E073ED37799EB975F6735985B47CC5AAF08E5705C83B08EB8FC669C427DDF97BFD070698A1E014148F14B2BF14C9E29D241ED03B125006B86F0D75371B2BCA981773436A8635CDF4966F67C3BE85C0D6BE856E4102F4C5D29BC2B387FB7E45560DE4DD219B2279E5AD3G48FC91B768E900E71B737A64D92F47AF57CDFB05C020AE98768901F0C42E1B0C7ADBDB7B228159AF052322D7F0864232138F89B02772B02EFB
	DBAAA56BF05C96D8B31B6036DF413D4661BD863062B0436F9FC3FCA13C979F4933D49EE17C1DB9542650BFFA6347DC7E71E0715E947CB59D21FC390F447973DDFB517887F296B5F3A8738C684B85588430832022B64F528470A151017A59437034FAE4DBB8988C072C11C8C740A776A13F705E5A8ABD8AEEC87006C59F4751F3BEA3556A50339850F93DF7BC0F4AA43B111DF94E50E3DC418F3FEEB46B4824A07B0651D1CBE5612B0D4B046E1D1EF26F745AC41DCC5C7B92275DBB3D7F59A2134E5ED9271FAD4AD0
	5649516C3D25964FFA62AA1E75D370EED0E4C171A241D79F0D6329FE6EA4BA036DF668FB64A8530D51BD0C96C771EA2E3C61C1DDB9E47CFC393134CE2121AE1916FFA4FA672823FBDAD4DA3ECE70D39ABC2565C30247F6BA74ED85207AF5FC9E6D55F18C6C87A08DA083E09DC0BE4056E3CC47E6E70FFDCE9DF372225C917587FC0248513947675E03279013C53EA7A449FC48AB0C70221F77848409D3461C1EC399F8926635935D77429E670510A07232BF9C02557920A08B22C447744C3D03E3C401748E7B0382
	EC900CF0EC5D4139FD708C7012FCB16263E5A1BE0276FFBA07B21966C7E0C5D8G5E29DC46EE223CCA6B73ACD5G7575CC8E79E43DDEC18C890156D6C7B8FCBD9A99C6CE3834FA14734344F7F4423B3B1E497872AD44ED00F6013A4E5BB75E552C536977A2793CB84BECACC0F4EA9B2E59G365E40F467FFC7196EF46EDE4355BE7BB5DD5D29E8FB36E83C016ACC2CA15906E2E4ED24AF8B7A56B7B03F7F7BAB23A9ECCBCB471D587DC25F717E55D4FB203FB5A9636EF68FAA6FDF5CC67EDE86DAF781D49EE7B26F
	3AD1C5F538031784263583A2A81C600B1FG2573013172A41AACAA8F19242F8956F5832483649CE7B27CE80C7117BC77EEF9DAA84356AFF48FAA2B36F90CD742349012BB05089072410B24745966F1AF451EA7426A1C9E020F007D5B4060A495BFB00FB64A4B326877C4E541A54832BFB4996F3FC47612221EC85CF5303D2E4EA3FF619930FFF53CF79EED479B8E0584F2E86A9425DFA036AB8404A090ED8BC7C3B245DF3F09F94EF5E1D6719D611BA59C5AFEA00AFE08668EEB79371187519F84D5C0D7469DECC4
	3B7E017401C6C679F86E105F6C04E7D9A3334D19BFB35BBCB877DDEE7DD8442897E3B23E7D00B150C86DA82DB1EE1B142671DB7A34CF909F2260A5CA6BCB67509769E5B2C5E44E7C8524B918586F70CBB2F15BF44C6F8E9C0027CF1860E1987639E08FA2D5B2284B9F93FC4C2E0C8624AD8BE8FE9C6029C6E6D77FFD8B7B19AFD38C4A968EA88BC1DFF5934A70E7D7ABB4F25852C8E538C76DFFAD21BF52946F277BBCFD9B6DCE92BE0B8A90C25069762AE7795C6DF7137AC614F3257D7FF99B4FB520715725A7F0
	4FCDE3ACDF4DE9227EF083BC7381F6G20ECB24938EF0E33F143A7A8DD0193F4FC7A89CAE356C99C77994F46EDBDC1FDABF7120EF71C2463683EAD9EB62E70A49DD75CCC772DE8267B52B3FE3D864F1064325778E9BED6A74D006D3436DE40F38E50FC35D2AED54657B7333C34FB06648150CE8348E8E6BA7A4CEE16172E3C1E4D0D8F6497254A3B57B6D3FA731B63BA1AF1E926086A024308FBD63464D98E36B0BD78145346C62BA4053DFE921615B3A8B5E8F51862F69B5E026704DF14649EF0B2B1F6DE7B8CC8
	AF46ED852D48631362135C9D66762A4633DC70E617D68639FBEF9A5FCEA10316964A7BE8CBDC86AB8E7FDF91252563CADD82ADD1590F31FC89DB2279586FC8BE06B61881EBEF822076B8BE0EBECA1E72CB54A6BCEE2DCD141E4A335482B8CF31714B461116A0E5CF61EF3B35DD997F54E32B39F56665C63D5AE36A518C236F9465EB7929B8FF1DAF3FE89C8520F4EE9CAFD3E4DFGE3AADA593E47081F040C0AF791C1CB0A4F79F4811E523466001626D27E9EDD1A163FB6E9F434AA35E2EB1C26754F3CE84CFF06
	5174904FE84A05B1BBD41A8C9E5CA32095FC9D716E36A9BEB4A9782AB596D4C7C2DBDD42A95991D5C34FF655281D78936D81B60C0DE8D7F3FC1E65CE570C286732FA1FD686ED372F6A4AE0595EA9E36DE94A7B7069388CC6017F00AA835E4374A78B46ECBE4DE430514374073637F976AC600365919F494935D4F9DBBC3FBB5836B05723B5C6AE71ADADED3446B02978FFA3F80786CFE96D1FE167B58EFD9B5AD8DE7CE4B17A366AB3F916CEGB7008C30926071B34C27BCECE8641628BBFAC2D08E047800ABA101
	E0793F9165GB1DFE9DFA4B119E88547625A29923431CD30E7A1C071D94899815A4EB29A8A0CCB52E00F6952C0657C6658D275327BEC222C693C5D63CB4DCBCB1AC7F577491A77B95D18016A2964B37DA55AFCA663AC2DD7B640F3B372CFE248AAB73B8759F6161D5983E60CDB5567604CG5CGB100CC00F5G8567184CEA0BBA16121915A50443E17555161C54B1D97CF201ACF61CCB64DDDF06858B669DC856797644F9144F6D67881F50578960860881E4822C83A8E8E7FC5EAABA3D24EE209FG9E89DB8B99
	24F4E671F7475FE38B66A57337CB17BF3F95792B30C2ED8B508D6083D886708840C6AB636F6922B31F13BF9B603AA13F52B9C20556C94F5D59C1F449F9DB2D0973E8AD5EC272A22E836296G456D1FC6DC8B341D9D2CE67BB939D715213DCC457D87412D6D2071166206E774EAD71C17603FBB6E2EC626FB84F577A8CAEA1B6EE2FD84658E23B8F5ED77C231FEED77DF457A355D2F0B5F21F8D8C3FB73437AFFDB3CF86D77C6F16A5A4FDA121A4E6E92FDBA7BCB74699CAED91CCEF7C9AABA7FC8647FEBF9BF27D3
	F37673C1C1B91FFD5C49F10BB246A9071D47B9660FA76D38060EBFF6F9C52812926E1E6FADC13D431CBC3F036572EBAE13F8EB831D851832B1FD7C06FBB13BC833AD54C34A6BAAF3EA39E61975657A9DB352C4136AE4592DB1FF2076452D3FC79602A89EBA66DF48FC1997A70585327F9919CF7BD24D7FD7B366159E3F4FAFAAD5A5BD17DF3C37171C2BA48992F1C41D10FAAA67E16259F95865F0DF033A5F2EF32979AFEA57675F521E1A7F727625797FC3FBEA7E2B5A17663F26BD15FED2DE1F5772EA0B73AAD9
	D373BAE75567B54D1A1A57FF30AE4D6B8AEBEADE9F34AE4DEB16F5F1DE3F61515AD9230A9F1071BEA7404F0623B23CF412BBFB1965B9AB419EF2G7281365AE83EC36D731FE7D037A6C4BE885B38A20217ABA0F3EB27595CEEE83B816681B281F2BBD93E5D7B895AEBC439F8213842DA64E3C81890C43C0CC00DA405C15C5AE98E76660CB687BBE827B4F563534EEC7C3E920C87B2341781E45A975A74DA9D5C869D5CE69D9C4D5BBFF213EFD8F61A37972978A3843FCB0327797C8FAF335A232CAB4F9269E23175
	C7EE96DB7381C7475EAF33EFF4BB81D7FF1E497CFF6ABE547A44BAFEC6EEED6D88635D11039EA075074F11980E73274EB3FFD89CA577D050DE873069BC3B5B087D055575B95FD94D8D776A5FED3CBCFC533875BC15C5D9F73C3E0ADDFB51C8779C1AE6FBF6C3FFDAB762522635F5B8727EAA7C2B03A76D7FC1C2BF6AEF8D276D5F12500F74FED62C6DFFEFC65BEF524C273CBD798963ED5713EB39400F52D3FEAF5D2870D428BCD7ECD9E598FEF4CE3933F8E5C03DCFA2F79628392D2D6C6210725F9CE377C987FB
	184C9FB611EF86501681167730B3DFC36255A3504ED5F1EF905CA6E86FD0F1CEB362CAAF008EDCE0386F91DCA7345DAAEEBB4185203DCC45F9AFB33D4A3C406C62109B6B5C0DEA3B0134717E0C36FFF2B955F706BB855CC3BFD74B7B3B6D111C5FA1D09F74141B79088220E39BC0F9AF489040594BF8DD39200EDF8A683C55F7B96F05BB1EC77D49F344EEA33D541F98D47CCB84BF2F41D35F71D614790EE550D7584BEA50088DE562F464D982G69G19G39G1B9D2CEE31F554F3CB54AD4361088382E3C0138E
	D27EAECD5F5D776DA74C4CC69F7F697738F577F6263CD7ACF4A87E27CF397FFEE515811F143FA2BB31D1689B851887D889108D303E0F71F7DFC7AD654F6E73E3A54DFB27482DAAE3EE44AF4C44519854A8EFEDB146DBFE5FC23D5E2A0323E734370444023E64337B0060CB7B134F6EDBA6F6F69550976BE771F2C3894A040F4AE10904C309AB6C477185A56C2E21C09D3B470C77985302A87B3DFC00064F8EDEF40683FE6F2CB22F06788B7C89CC7DG1BB7E9C6B9CE05C57FDCB8A4AF1C49A9F33FCC66DAD1A755
	39CF1811EEEFB8908D063A859EF4A2F7G477E4A0C7EE52DBA4ED20A7EB7A2E21097851F4BBFA7286BFEDCCA6ACEE71E258C205B0938394BD50B68CF0B134A1D2EBDE246BC065C56B4303AE03B1B796495CE96138EDFC90EC9CFA444A4771564180467916F3F7FCAF2CCFA53206DDFFFA5B9A631794A5DCA14D35FDF553C17C77FDEFEB1358F2A79C277D0FE73E0CB1DBB275974F367FBED7A7567F25B62F570CADB6ABA78E11B769BA87591BDDDF8B69BC19736G148E66D9AA816A8719FF28697C18A37EC147B7
	58FD13183B63AFB1E436FF19BDB52F7B6CCB7FFE23520E5FF1BCE1D98E87F1FD067F580E67A507A384C9F1076CA8A351BFB9A553218A7E281D44FEE10220E96CF711DC4481FCCD8D2A39040965837303AC866EAF096B5F814D7B2326AFDE9757FF21FBD01F70FA8939579723820DB5D9B35A5F4F353C75A55DEF4D885903543764AA4FDD7B9FB250B55B4DD8A7EA3FE2E8EEA46AC66CC343BDB62BE34CE59B9AF0B8BA2CC3E3D647A56B27AE1526074C0D0B4C6F9E986A796BC07F305E8AF47EEFD1FCCA6CBCA3E8
	79293F61A07776FA7C6CD078A89E0A7353B4043FEBFC350FD3E99BA57EB6A44CD43172222E538FD600BF7229BE5E5CD4DB5550F432060EFD70B24A7612A0494AF7FC0145714A6F7F93D77927F663BBA9E809A9B4BCB2942FA362FA71C6427D4CE698D3B834785D20724D22AB35AD0EF6ADED0BFC97794E8AB6B4BC9BD1EA6A09AEB40A704404A428B6FF3D2BDC4575C84E306447DA1C4A64DAACF5FC7BB231C94E85714D45625B2429EA913EDADC14EF6A3B1E6FA7B68D38A99773D73F6FBA824F917E1A1229E1DA
	939609478224D38CC7A08D1825D87295CED9ABEFG4FEE193A0E6D7C470B2C03F754AEF04121C926F7CFDF566ADD79C381A34BE312F948D4F848F6454F1A52F0EE90F9AFG7CCE004A61BCCB93C0B740B8008C30FCD8754B7D550B50C78BE0621BC55E9F12293F5BF38565158DEBFC45406E3677915CFA403D8487C6753F4C9557FF3BE9917DEF2E2F2F2D02FF0A7E271BD0B7EC82FEC7E33AD1500FBC2A710A0ADC69FB259FE30FBF846A42877CF36493BF152A72DB0C81DCCFD264CCE57493C21B72BB8167A729
	E4G51698250BB6039A6F865E1FED21DF3E1B035CD8C8EAEED93FF9D540FCF63038B6313B0289F1F2E8FEA639345C514F5C575B21862278751FE68DDD49F9F62A7C96C26B5B87F1732387CAE79FD729445D7921BD3705D826E8FED7ABB6F170B7A6119739B4EA4095C3B8B4F54FA7F2F1E4F996B2CB0F2469E2EE40ACC6E61CCB338FA8CFF8E79D5506AF7254F38DBF88E46DC8A5486EAEEEBEDE067BD5E1431E44BC56537589713ED2016646FCDA3D0170EB0DD1FB79DDD641CDDC1BE90006C7C3AB56443B36F18
	1505F8BE54D0026BCBEC0C52C97BDA489D7FB54D1C3F81D0CB878838F2932D9D91GGE8AFGGD0CB818294G94G88G88GF6FBB0B638F2932D9D91GGE8AFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD791GGGG
**end of data**/
}
}