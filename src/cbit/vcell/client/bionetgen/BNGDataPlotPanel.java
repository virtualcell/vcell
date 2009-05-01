package cbit.vcell.client.bionetgen;
import java.util.Vector;
import cbit.util.DefaultListSelectionModelFixed;
import cbit.vcell.modelopt.gui.MultisourcePlotListModel;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.DataReference;

/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:03:04 PM)
 * @author: Jim Schaff
 */
public class BNGDataPlotPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JList ivjJList1 = null;
	private cbit.plot.PlotPane ivjplotPane = null;
	private cbit.util.DefaultListSelectionModelFixed ivjdefaultListSelectionModelFixed = null;
	private javax.swing.JScrollPane ivjReferenceDataListScrollPane = null;
	private cbit.vcell.modelopt.gui.DataSource fieldDataSource = null;
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
public cbit.vcell.modelopt.gui.DataSource getDataSource() {
	return fieldDataSource;
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
	java.util.Vector nameList = new java.util.Vector();
	Vector selectedValues = new Vector();
	cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = (cbit.vcell.solver.ode.ODESolverResultSet)getDataSource().getSource();
	int t_index = odeSolverResultSet.findColumn("t");
	selectedValues.add(odeSolverResultSet.extractColumn(t_index));
	
	for (int selectedIndex = 0; selectedIndex < getbngDataPlotListModel().getSize(); selectedIndex++){
		if (((DefaultListSelectionModelFixed)listSelectionEvent.getSource()).isSelectedIndex(selectedIndex)){
			DataReference dataReference = (DataReference)getbngDataPlotListModel().getElementAt(selectedIndex);
			if (dataReference.getDataSource().getSource() instanceof cbit.vcell.solver.ode.ODESolverResultSet){
				odeSolverResultSet = (cbit.vcell.solver.ode.ODESolverResultSet)dataReference.getDataSource().getSource();
				for (int i = 0; i < odeSolverResultSet.getColumnDescriptions().length; i++){
					if (i == t_index){
						continue;
					}
					if (odeSolverResultSet.getColumnDescriptions(i).getName().equals(dataReference.getIdentifier())){
						nameList.add(odeSolverResultSet.getColumnDescriptions(i).getName());
						selectedValues.add(odeSolverResultSet.extractColumn(i));
						break;
					}
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

	cbit.plot.Plot2D plot2D = new cbit.plot.SingleXPlot2D(null,"Time", names, dataValues,labels, visibleFlags);
	getplotPane().setPlot2D(plot2D);

	return;
}


/**
 * Sets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @param dataSource The new value for the property.
 * @see #getDataSource
 */
public void setDataSource(cbit.vcell.modelopt.gui.DataSource dataSource) {
	cbit.vcell.modelopt.gui.DataSource oldValue = fieldDataSource;
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D467151E96F7F3FABCDD3B472658129EE3EDCBB7A4251B6EA96D320D4D3AA7C9359BC2C935A99E4DCA9B1ACDF36C4A6E5A9E7774B1C0E49A9209DAEBB74412EC92A10E93902288C47995C6069FE706F1C40CBEE69EB38F5E4C1B4CBC840C485E7BFD6FFB73985EB01A53F04E653D6FFEFF775E6F7EFEEF3803FFDED3D6ECBA48F145E51C7AF72A1863DA5F653807DF58758706EBBA1AEB66F4FF
	2F833C42DD9AAB05E7AF74258D671AAB39FCD7B934C7215D866DBF427B816EE95716637012E01FCC687B4F6FBD7550527BCC1FE37B6CA56D5D7E8AF83E8CF08DF0784CB80A7BD7868E2B7894954F205844F1ABEAF04C31C0150A5B89E34A81578BB0DAC347AB702484ACEFEED2470DFEAE1DAB7AE14474922E23DB13C11EBAF7377A2C66FEDB3C269677F285AA54B9F5508EGA073A55CFFDFBF814F5621C97F996F1828D82FFB85C932FAA5D188A956B1D18E890ADF88B5B7F78C74F4718ABFA4494A909F92A40E
	2BFA8757CD8B1413F5B82EG56CCA73835633857B5F81F81D8798E437F8B1155F370DEFAAC57DCF50C71777E51D615FE4FC75F6172CE84CB77A5602F719865EB74D80C3F7C492D26BF113378CE8F4ABC957A3281F2G0AG8EDAF24D0DG2F929D787E39F76059BE26F44A41209CEA8F07BBA4DF24DF7689923CB7B7C30F0AF388DEB96263B8FA4EA3810B81BDCB811D973863F9D4A6698D6C4CB39C8FF0798F5EED5AEBA0137C740FCCF68B15054F9213895D7B5E04FB27D8966A64423DAFF27A3DE73E79E4C919
	415E2B521EACC9D55749506DDDE04133BEC44EFA933C972B322078B7883E66F88CCF75B31D688C365B20EF5DF1269B1E1799AD765235DCE57D978C65107A708AD339D525419ACF4BE9227BF6AB5D532C51F20E6083BABC25E5A321915BA950D788C075EB3D8D6D557EAE588F40B200D400AC00BC0042F7190E6D6DF977B6F5EC080FA89DD3226493A29C1D3B3E6790BC0599A5425BC2D1058FF985B79F9179B1C908F26A1847FAD086E38B667DAB51FDAF6C51A30404882F0069426AFCD0D004C8140E595C738A0E
	09882457A5868558A09866583A5BFA8E40D362234AF05847ABC2EC846DFF2687E5F21DF7430A30G3CD3397C6ABC4A2B3CAE57DC8DD0D74764B0560B6B75899170974D4DF6D91E1C8A3B1093EED99D4AB90B780EAEF89F2DE3B27EE59F62B6C0BBDFDBE7F56FFE5DBADD22974967A3334C462A48F96D44B56BF34D2D75CCF71E77B05D69FA69FE4E7264678DF5277C1EA7CBC66BD51FDB9FEFC3B5E4ED24AF8D7A326B195FFFE05C134036989DC6FD2D8970463E631104FB203FADDBE26E6D6CC165FD319F794B87
	3AF6839CBE41E41E57DBCDF538030F8ACCEB87A320F002AFF682144E3301CAF5DF147BEBG759A8FF652D7876BFAG1681241FE0B2FC422F672FDE37466D507F5AED49705985FB1813486ECEE5C9E575C39B46AB61BA044CAEA1AC04FC7092D57B1E30611E2ABD0F4B5A1C4D849F867B6F84030BEA782DB634D1DED1A2625814A2B885C591C37ED87F4F49DE512931F04CF5303D1E33A1DF72B4583FB67E57B6349D2F9C8A89645034A9EA7FAEE23B02A4840168CEF9AA24D07CEF4EE31EB3A94C2A3E63FF4E3F40
	216DCBD36807E86E10493F08BCC444A028823AB22E3A816D7AF1AD77C1480173469C28881E658D4CB6277F0F59661BB73E412D3F9AB6994518542F7E0DA950C0ED66E6C34CB6A9CD4F779953DEC4FC080A0FD2DA371DC5DF54BFA5A9E2D41E0AF885164990FDGFE0F9C0127CF9867E108DD0CAACE18CFA4C67C76B9F1C670F16A588F76C172480D057CACA34271E49835291583C8D79A501B8970E8833329309D7B199FD30D29D316540D202F2A916597749F5449E0FB83155F4BDA7F6A857D3E46D8BF5D67CABF
	5ADCD478F0CA0070018E37CFBBC7DF7F7E38BE37FA2634DF6A47B315F43E7A5AFB3867B2ADD7CDEF243EF03D3AEF45FBF46F4A13B82EC7E463B25E2363B640B38F20886008B66E70849BB7FA127AD565A49D17F2120E5BCE46BD230DCBBDC9774BD447BFFA126ECB4FF7D7AD6A9C49E3A7786B7C0CB5BA8DF6535CFC1EE42BB4D7BDAD162B63AD27D8CEAAC8F8D6BEE85F84D8F50A696793AF311CF455E4BA674A4CABC914F3E71C22F4641C0A69E74D0A69922A8BCF8D621E15CD909B1B189EBC532F0F0B6D5128
	6C95C9C8D64F606E813D7ED25C1786FE8F4FF1B192D5EC60E0E658F95DBB0074E24CD6512ABCBA2ED8B9FDDEBF250898BB1368B765FD6E1C1E776CFDF3269793705E58C4F9F6B545F8EFDA71DFA51406EF151F646E303650AC0965E25FA0B99A2E378A765884C0ED30D2ECC33E750CD809F76A8C70A13F602B51E9011538E66B822E3B91D5CB69DC6D69A6B62197C87DD537FEB57B6DAC57D26B66188C579EE8D3E534690135DC6B7C87A6231A662EFD81D369E9AABB4B69188CF3F2371A8AG288C42E285C7FD4F
	8C0C49BC4DE430D9C49F2D8444A8758B7B853DDFA804F1954D100FB533713F93DFDE44A72377BF5A7574E66DDDE1C8EF606B29262EE6CA27549C23B77B692D261C27993D6D930C5EEC98D3246D1FBE4168256D1C8914FF1012211EF765A4BBEFCBCB2E59574266CECF227EC3D64ABB097BE63E785EC5F84A4F7DC7747C14734B8D79D971CB3FA92D05723121C52743BF5EEA3283509C620C98DFC7D5344672386A5645B99D2DAFFECAFCCBE3AB2DAF4AB4FC8141776A701477C90971BE8AFD6BDBD9CEFC49013E6D
	4D77F34DEDG9E00B040CA004C7719CF299DEC6012549C36901482A1DEF2AEC89ED86EBF087204F82F36F78CA29D4422B816ABBE62C47D5A84FB9682F0E7A0DF84E8BA43E81898CACAC3770CA18DD44E5FDCD42FFA4EAC142D925726737EE9513CA3F16D5438F9141F5706B136E4C25FA60082G6EEC2E79CD00B6G5FD94657F7075ECF4A975A3FCB6E84DCAF444E4E78FCCB23757AA2DD0A87E39ED3124ECBB93BF09E75ED9BEB716E4BA0AE227BD27335EB377479DA5AD9DA0FBD8A4FFCG9ABBED823B67A9BA
	4B74F2159B65E7E94BB57782DC8338893086A09BE0FB9B13DF45050EE472EBE7892FCB56DE8D04C8E551B37171E4B81CF45E6EB623F907A6BF1EDE3E39E8DE3CDED6369B69255903F22D013EAE00D1009900B400AC007CF6A6578FAE345C26DE3693B1AE96A82D4307C9DE3487562D6C009A0D60CECCA22E915A768ED62FED13909706F60A06FB1660B2BAE82C21388FA5233A9567AD50570E5B2B0563016E719943BDCA625A37DB674503FAFF61C8DC57098E633ACEF69857F5D30ED7A89E56505F7A307EB94752
	F55FF2C7623A6FF1E7E2BAFF62B426B35FE9CC673FB9172673D9E7A2BADFA372FF6C50BEB2BF9A9522C479B033D37B1ED17092FFD1ADBA4087850E4573DF844C6A18EEC56E2C5776BDBA1C18C76B30B10F0D43183353F2AF1E0F166143DADF4CAEEC0A90C4F26818AEB2DF61A3FEE1119C7A48FC5A17E83EEBB8112CA82F770B0C57A1C5A631137AECFFB74A4B40E7BB3D912896C9554A6E5FD3EF217DA11DB99D2C263962A3F9C7A758AE002F1359650030147F3B5939589EA99FDFF6A7167DD75D46323F4F1DD8
	76FF6FCEAE7BFFF4A7167D436E64325F68461AE5CC740991353A26B23FB52117F93DF6967D13BA3D6BBF216DF5401318777F759873DE6DC94C7BD1CFF25E6BBC09F9BF65C94EFB1BE7E93D13C43C4B1178D9F9CA813F5F09BA73E91165GAB211D8E1083D050C9F381351E1BC6190EC778A0AC678C8BDEEECB97498DA6595CAEE8FBGE6GD281323AD87E5E7796099D6A258B45BDE7C1FA9D423890414B88749424280AE9B94DCF2A027A7C243A1B525485CFCFB7536DFBEEE1BC88C3BB85A035FB31AEE7986056
	9B60728CF0B4EFFF0258D5E1B74D5BCBB47C4F88FE378ECF73F92F0F55C16567F24D52B996DB850145569C40513177096C7BDC9160ACBDCC663F303E2A7785D6FEDA0102D9463BA3BBBDC06A87F611980E73FDBD4C8F3CCE6A30B4E8FF9BE0D38F3B5B18F91B557799DFDB4B75E6985FEDECF74D198AFA28AC4AFBE335514E492DA63A27BC49766C027E1B3D083BE7D2DF03A26FBF07FFD674AF247F71857D2827351C3E3FD85277A33D9F166A7B9B9674176966D35EB63E45F85B33B113137EA1A56137528DAACF
	056AF3E5416A62021F5DD06FAEF68CEA77C9646E82B5373919DD9AD27EDDC1F61FD4EDE3B23FFF8EE56E01F69860AE9BBB7307081FDE836DAC8DE71EC75CA6E86F54F0E384D7FA9EF460BC43E593DC9B34BD9A2E14C3DCG5AA99A6E000F69D56AF9E6971BFDD8F3EF505ACF13B65E33513668CB740D61CE8177B04E55BE75975BA3BE3FC320BE682640FC44962063B9000ABE10A1003D0F717A2D24B514B17D8FFD4CF93B6FF89E75A73B085DCAFD541F94EB78BD84BF2343D35FF1F402790E9468AB6CE375D965
	884A44EE4FB587G16812482E4816459D98D3143D347A529A1DCF2588E81D052250314BFE5D1FD138C280D7EE0AF3351EFFF6F6F382C3F6ACAF83FD8E8D77DCFBFFD566CD8DD0CCF4A5FD7AEA3FFCD5067821881D889308EA03B1F71F76C120572576D93314A643D81F22B4A18F30B42F48C0DC10D72F6A948F84B69B70887C6B8F2C62F5FA23120BF7E6C5EA17852017833FBF00E1DDDA574A983ACDE7E64964A041FD264A88907D12EF0G47BFFF0B5561DB34315573D863DF97A20A6865A59ABEBB784810AC09
	5ED9F5DE8B71A17873974BA01BB7BB0FF28C489171069CD2964F6454391FA7F35BD1A73539BF1EC73A3D32B4958C758ABC68C456A00E7D77F974AF995A38D7C9EE9A0EE0100F88BE27F8C35056EDA4BEEC8F0CAD9F02F8BEC464642BDEC2FF9A07285C695A6E3D1847101B0CFA168F2F9518CFDEB144E252D3C2FCCC7A410218D4B19E9F1370BCE27DE74663E3526F0A757D617178184466D3DD7A4D84273D1BA6E36F47A77ED2FEB1318FBA77096EC1ED6CCFA309F3646A91639C796D91637A53B232F49D5CB812
	388EEE9951FF7FD4FFCFB00AE73381F4E1BBC06905DC73E1G4B85669F9E9BF903A37E4140B7F47B7C1823632FB09436FF4665443CBEF8B979EFB732AF63FD75182CA8F2905757EA1A4BF8DE0A9CA6C80AFB64B24AA8A27A838A9D2A62FFF499F5DC9246891A467EB049C5FA01AF5F85AD1718E379404C8596830F5E0A69DF1D6E7D3BF31F3CAE66FE22FBD01FD0C67C0F921992E82CD9B32D7F6DDC536FBFE5781D99E15D856ADB3254671EBFFC31182E691847BAD17F85C8D709DB5D5D8E172D335DFE55596998
	345BBB5A9DD75B6D975BAFB9B51AB2669B16185FBB68300D8C8E380CD620730B89A76AEF6584BDBFF59F59CDA5897859297241B9E27C54B970B70D4BFD1CC6DBB97137A1E13A1A159756AE912CGFF60D3F362D423253A3E71E4AD9D5BC3F2470BC2D4D13F618BAC0ED75D77952E6A4FED26D792502228B42CF1446A0818DEFCFD413DC49E0CA9F4ACFDB7287E36E1B431AD6E9ECDEE0B45D748F7D630A157ECD82D1DDF3802B1C59E9F0F8A1A4D3FF825D2435922C3F2D4441A1B4A642F0309635B9AE213FB0017
	F2A70BEF33F355CB70556824FCD35FB5CDE2D9AF60FCCE662F3EFC6598BC5D7C041A29E1DAA3C708478264D0C48EC39AB0CB31648B263AD6E49C4FAEC5DBE74715B716D8873FE3B84185077CCC6F9E3F2C573B4AAFCAA616474473102A72304E99BBEBCA43B6BF723E8570C5G07DD3966BA00AE00D100B040DDAE4DAF7BEA16200F964044B7C7F8B124D0FF77FDA22FF530466700B00A7BE7024B865CB6B8B02A7F99E2CC7FAF4EAD217F276A6AAC5570CF557F3B67D0B7BA857C0E46F4632600BCEA710A0ADC6D
	7BB449BB4490288BAF09B748A7FEAAD55AFFFD9C570B2AF2D63F7511DCC17D8D4E78DB09E4G5169BC50BBB8B6A1F895976F5766BC6BCFEC933F76A737095F7A0D639397D89C1F4A824671694500BEBED1DCE5002E9B51AEFCA97ED0G6D075EC57573A15ECFE2B72D417937ABE2723BA87A14G45D74D3C9443778A38BF3469EF3CCB7981F8E67C0AAB0BC6387D0B4F347D33BF5D1CF1356B2009B359389782E4320DAB1B4655E7702710FF8BDA3DBF7AA1C7E223A91B8235015AF93D8D6CDE6ECD98CB368FD35D
	AF9A0E3701A1AF4A264E8DF5291B697A1A3163CB1C33B348CB92E467136DA19F1EF9472CA2447221E1AF2E9FE5E354CE5AB76AC53AA7F4F37E9FD0CB878865FBF3A4C091GGE4AFGGD0CB818294G94G88G88GE7FBB0B665FBF3A4C091GGE4AFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFA91GGGG
**end of data**/
}
}