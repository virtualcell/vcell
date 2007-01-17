package cbit.vcell.webparser;

import java.util.*;
import javax.swing.tree.*;
import org.jdom.Attribute;
import org.jdom.Element;
/**
 * Insert the type's description here.
 * Creation date: (1/7/2003 12:29:14 PM)
 * @author: Frank Morgan
 */
public class WSPJtreePanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTree ivjJTree1 = null;
/**
 * WSPJtreePanel constructor comment.
 */
public WSPJtreePanel() {
	super();
	initialize();
}
/**
 * WSPJtreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public WSPJtreePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * WSPJtreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public WSPJtreePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * WSPJtreePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public WSPJtreePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (1/7/2003 2:03:18 PM)
 * @param element org.jdom.Element
 */
public void addJdomElement(org.jdom.Element el) {

	DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) getJTree1().getModel().getRoot();
	addJdomElement(el,dmtn);


	
	//javax.swing.tree.DefaultTreeModel treeModel = new javax.swing.tree.DefaultTreeModel(dmtn);
	//getJTree1().setModel(treeModel);

	//getJTree1().expandPath(new TreePath(dmtn.getPath()));






	
	//DefaultMutableTreeNode child = (DefaultMutableTreeNode)dmtn.getChildAt(3);
	
	//DefaultTreeModel treeModel = (DefaultTreeModel)getJTree1().getModel();
	//treeModel.insertNodeInto(dmtnLocal,dmtn,dmtn.getChildCount());

	//getJTree1().scrollPathToVisible(new TreePath(child.getPath()));
}
/**
 * Insert the method's description here.
 * Creation date: (1/7/2003 2:03:18 PM)
 * @param element org.jdom.Element
 */
private void addJdomElement(org.jdom.Element el,DefaultMutableTreeNode parentDMTN) {
	
	//DefaultMutableTreeNode childDMTN = new DefaultMutableTreeNode(el.getName());
	//String elText = el.getText();
	//if (elText != null && !elText.equals("")) {
	//	dmtnLocal.add(new DefaultMutableTreeNode(elText));
	//}

	StringBuffer childRootSB = new StringBuffer(el.getName()+":");
    Iterator atts = el.getAttributes().iterator();
    while (atts.hasNext()) {
        Attribute att = (Attribute) atts.next();
        childRootSB.append(" "+att.getName()+"="+att.getValue());
        //DefaultMutableTreeNode node = new DefaultMutableTreeNode("@" + att.getName());
        //node.add(new DefaultMutableTreeNode(att.getValue()));
        //parentDMTN.add(node);
    }

    Vector v = new Vector();
    Iterator iter = el.getChildren().iterator();
	while (iter.hasNext()) {
        Element nextEl = (Element) iter.next();
        if(nextEl.getName().equals("Id")){
	        childRootSB.append(" "+"id="+nextEl.getAttributeValue("type")+"("+nextEl.getAttributeValue("value")+")");
        }else{
	        v.add(nextEl);
        }
    }

	DefaultMutableTreeNode childRoot = new DefaultMutableTreeNode(childRootSB.toString());
	((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(childRoot,parentDMTN,parentDMTN.getChildCount());

	for(int i = 0;i < v.size();i+= 1){
		Element nextEl = (Element) v.get(i);
		addJdomElement(nextEl, childRoot);
	}
    //Iterator iter = el.getChildren().iterator();

    //while (iter.hasNext()) {
    //    Element nextEl = (Element) iter.next();
	//	addJdomElement(nextEl, childRoot);
    //}
    //parentDMTN.add(dmtnLocal);

}
/**
 * connEtoC1:  (WSPJtreePanel.initialize() --> WSPJtreePanel.wSPJtreePanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.wSPJtreePanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC714A8AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFDD0D45795E7BA1A58562618A199533189495814D61A5806F61C8E5321A9193155BFECB529C9E8E3A6561A8931A431D3BB650F370BD6A408EBA456DA540D628A32A2C4A238DF30AC08303B308914501656BC58A7ECDC76ED5EBE848DE9FA4E3D6F3EFDACEF17442AB3A7F76FB97773FC7E6EA3DC65314CDD86E3A54799F6F14A3FE68347659F67382FD4BE7B9A63E59E3114F11AFFEF816D6726AE6C
	04F6A548F6137ECB76F2E8AB00D6832F90286528254C4415B88EA9FC9374AB145FC886A3479510B1CF759E5270E714F1AB214D87CABB06E30EBA2A94F9B574F10C835A40B1BAF69C5ABD64F48BEDAD4A3861A5CFF35BFFB6E35C0BFBE956E734DA19DB2834E6EE279195D1421D2B2C01569138BA669A9D89497557C605F038FECA980972D2CC10EC36777B83BD32A48881BEA204074DF83E625ED2B2974E877D2D04A77721CEB26177AA20F5E6F6461F6FF5A8E748387AB497F52FC9FAC66BDB74EC013762E78C5C
	33F9A7F79C576FF214AB7352C13E9A081E652B8EF6160E636CAC19BE9CBB837D8C20EC15BF66C7FEBE748B00369FE7E7F41EE2E79C7AF8AB37E36481633EA4E7ACBFC14F56F4A2FEC6733622123F923FA83123ED23A04BG4A82DA8BD488D4DEEDA97B9B488EF0BF3AF09ADA77086C91A7A64408BB9AED8F87A53F989442705BE6830942FBCF989525A047D11FB374D9F54E43283AFA363FA1513B3D4FB19F49FE6FB9CE166FA1F6CF3CDB4E137FB536A8EB782A63F71B29C63DEDA5368D43EF0322EB4AFF157073
	B4FCEA2B9B040F7DF5A0EBBA4974BCD4414E625B391EB3B5BEE85C2DF316250F5FEBDCFAD2090B137133D09D7F0A44D88170F7G19AC16329A20CBC063962663D25B3979F45C9D1185A942077BA75151657649D0B8A8C80A2E7D0ED43A56A3BA6FDF1FFB9E55CB56B92D0D8A9E4B5455CBC156C263828B5547B2CBDCAFF46F0C24FB67DA667AC605EA43CA92936BAC0936BDCA781BAC0936FD5847ECDB843201D3101F4878E74978D5279257798D612FBE15384E041F2D3391E40E9ACB99356F47AE4CFD19504F85
	5AGD488D4DE8BF686F254B29B775B4EFE46B88AE86CCB67165B8FC2ABCC4B925F9D0949FCE4D498642590BF9296E21CB266CDBB6AF5E4563C43A43EC7E10FAEA1A2C83C9C92A330BABFA100B345681893767DB8C69208F4A0B4A1408693D10E2DDBE7BF00314447643FC7033CAC44C7D079B9BB7A44B5FE90D60485607762DA545721BE4CF34370FB3A1669AA2B95F96B203FD5554BA376B7207502F38BE11B2DA3B40A4760256B4CC73775E1ADAB2EE5B139ED0879DE475EDFF356A79F50753D626A341D832729
	9F05CFA77A5E8E32E646E9D69F537A06583CDAFD5F7BFF086EF11F3AC749ADDE9F096A77209BFDB4876E358E2868B453710BF6B3753DF6BEA6B0EF6B1340D104E0DC63F46C96875692611A9011BB04289089420F18E26B179DFB62FEF8C5D4FD64B542473C6481C709297CBFBA76D3BE2C2175EAA6B7B8D14E4B32949A191405FEC116C31131787CBDCEBCCBECD64A0BA9F7BD540EF7AD2D331419019A6A58DD6FF69E507813CF942FCEC687B0D2943FDCC16AEF870C9F2EE376BF6CC7FFC8D37B55249F17E77B18
	6DD2914E3FF97FF862B36DF1333EC377F81E5C4B2076DFA27DE56ABDE37EA1CE9FC75C3AB8D8CC76389DB1D0217A4DC2D7F29FDE6C5277613BDD7ABE3C54155AC7B3DDFABECAE55FF4A1F69127A29A1F4FF6E13E9B95A39101841EBA052378F65BG5EA39489496F0702E3025C9F3AA1G8E2EC35FBDCBF2EABE7C5ED84772615489168F2BEFBC4365DD3E210B1716FE63BEE303157AE707B51E8F69193E530A31A4040589D014C71C0C48147F9849578ABFD6C2F835C3BE1CDBB38BE6DB713CA1BE8CE72D29B51F
	B963B24F96CFE9FB173BB50387EB9BE8BCFBBDF806A01CED9AA85D4A6279EC9B4AD9BCABC94BA30643D4FDA05BEEC55D5486AAB5774D3252F7CE5CA72A5CA63413705124G65939302D7384EEA207B0D8459A09F1E0C5756BA776B5006B53904720F366B6B2D3A9D636A2A3AFECD7B1FB92C11F4CDEA6715BC2E9915C29310FAB1B9AA6F2D479D5A7716638C70007F8134B2901D1B5D00EF12D9AA067EE69C4BD575323758E2E56EF2254DBDC37551D00F63AE7631F748D91577EF42EB067F7CE49743FAFE13764D
	960C5D2D6BDFC8F2BDDDFFD1FDA286CE6B40F533003F2A1E597521F6ED1EF647E262E808C090C5376FFA343EC7F9039EFC6CDE89C9B14043C1E11A59E75803E7C50C2230959D2F28F3F2F3B1D7E7B0A40B92AB031E29CE4C93832287E052A5B031C78D4D4110B045B1BDFCF2D17BAE4C5DF7277175A4FAD82B5C3FD023074DAF971550B33DD60B76A56F578F79EB7CF4FDEC8A72094D5623F92FDA1D254A9D2CE759DBF0280D448774E700B200A8AE6D6CADD746E683EFB3900D53EC425746A55D7FEB6D28339481
	295C772183ED6CBE0F2C679C1F0E6B3EB1976D130CD58DCADEE9503CB17F79CAC994C8F9533D2D75213CCDF7A47521F4E50D658D09BE943C08BA49837E068666C33F746C5778903CBF819A0FC1F42AF52023044621527FE247AE96079C43CC8FF6E15E588AEB6EEC3414D581B5818DB432BDCC1D733E4DBA27D33C4D9A1D096F9C39B10E9396B54E458C749D321658FBD9E3627B64A9424FEECCFC1F7C270D3DCFF2C1E6FE1B55F4EFEF627E8B5E1E3DE7E6C21F4E7B471CF975897D7C04F9D41FFF38047927GE4
	BB00CC67ACE5D620GD098A87D9C536B65CE3D581C2D57E401994711F44F669E740D1530F61E3AFEEE67DE0DEF8444D0C4D67DA2F4917331A80546C291460B905E28A811E2CDF95F6ACA0EA3D6F66963089FF46963085C2E54D8B83FAB994E2038759249896B60FE1B01A86FA4294F25CDE06FA6E66FDF34E16D69D07B8524BF2C7603ED1F85E74EC738FEF20C7749AD59A399C6BCC2E4BFBD380F68B39693E244D1BAF819D7E43F155F64F4B0D6AFBCFC15F58FF0DF685D230C6914C5CF23EA87C3F7F21B17F66B
	5B3C229BFDADA6CECA23C222DD5F6CBE244A629E5CAD8B93F89C85C71079B2AF815ECB1CFF124C2732E4736B3BD3E3D4C78F660AE9355FC67A57D5DDDCF6226F07796B62A484C9E1937A56B5A743A1E950DF8A14873421093E1BE8AD796E945640AB92BF815B75C705D1EE75BBB857514A66B6C17F9250B850A2202CF7D8FD3A2B9273D5CF7F2884D88E4BD0A345BFFB5DDA7CE3BACF77F3C09BB84FEA5E7DCE76EDF59CF8196759F93654637D545ADA4FCF49B6DB3B081851C7AFC76B6B9692CBB8FF63F9D6DFAB
	DD38262959D2D68354564C70753405557D6C0757F3C3F67D6F411B866EB48EB5531818E90E57AE5F3622923A67C8AB5BF395480B1A11175E2A452C2817D7B81AE7297C71D9F234F16DAC7976D9F2BC2F5B2015DB5A3472DD1A7974EEEBCE323B952FF91E8BFF7F2E245F3ADBDA681D0615B62FBF47B09E3B341B6AFD2C7388270BE962564539C48F1DEE06BBB3DA186E059E547DDA68EF86AAEEE139EA791C5AB99F51F95F3B49F9AFFD6EF934A6EFA13ED3F90156E4034A7F9D611BB5FC6A3F07DD4CFF2DA0CB3F
	403E0F3D6AC55F5F8EFD138D7C90288D688320999B2BEBCF74B4F073544D81B16A038496561499FA3F31561B7B5E7B635D4CCFF2FE78AC376A0E5E2458B75D26FC87D75A2177F28CF1EC30500F775B8432E2A0335DD2568264838A5A597D76FA2D74FE88535D324C0F0E1397G3B9C0272B89B138E3DDB0FBB555D266D49F056534477965B93F1568B043F4C1E08332E76B01C158DB2130379EA210BD37D68771A5FE6572D2805297D7642ED5D035A65CF5E64F55168532F0B25BEFDACD461CBDD2F8E7A12FFD3B9
	6253FED321FE352697731C8C36C8874A81DA8DD460E0BE3542F71C7A140EBFF5864730AE63D7F5196DFF17BF79DD6F714F7FAD7EFEBF3ED3C7C4D996A7F0FD46DF6EC7FB49E214B0950C69C79D81949D1769D0057F289F73E4D838C258946F71EE0677F628FE77DC8F623BEAA76B3FC87AADEA7F231E5B659B71BD3FF45B76A8D175313EB739CD1E691D5FA6AF103F818B204B016BD185B7ED6DA56F00ABD7E202EA7B17FBCDAA2FBB9690E3A144DBE1A76A7F63EEF30A338EB9E90E21786639B338F64FGBC329B
	59B72A43DEEDBD4E0171084BB7BA99A6DA5A0DF3BC827E2D0F655EA901655E2A959B382AE3BDC60C9D3D5CDB286CFF4895479F58AA5F1B867E426956E8BC21D21B5F75325A6CF3311AA484704E8B201F89148FD4GD484D45A8A6FE52026D696E71F76615F6687798F67FEE981A685E2A456A49E1EE354F7863D2972F620F5EE5E26FBC583497DE1AAB03FBF248574E3740E405C987DF2C0BFC6178434B1CAF977AA6BCA6A030372178590DBD10C6D67A37C9849DFB42EA73CAC2E03F0DF0A2197F8998EDE843CE5
	C039C085C0452D6C7FA5783617E14A2AB606A9B7FA93B16592CE0B894BFC091852B0CB6E70A5E24A2F4F12C7FC0918124D2776FCB425BD7568FFD0CB8788FD3F2A7E108CGG6CA2GGD0CB818294G94G88G88GC714A8AEFD3F2A7E108CGG6CA2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4A8CGGGG
**end of data**/
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
			getJScrollPane1().setViewportView(getJTree1());
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
			ivjJTree1.setBounds(0, 0, 78, 72);
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
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("WSPJtreePanel");
		setLayout(new java.awt.BorderLayout());
		setSize(160, 120);
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
		WSPJtreePanel aWSPJtreePanel;
		aWSPJtreePanel = new WSPJtreePanel();
		//
		javax.swing.tree.DefaultMutableTreeNode rootNode = new javax.swing.tree.DefaultMutableTreeNode("VCell Elements");
		javax.swing.tree.DefaultTreeModel treeModel = new javax.swing.tree.DefaultTreeModel(rootNode);
		aWSPJtreePanel.getJTree1().setModel(treeModel);
		javax.swing.tree.DefaultMutableTreeNode root_child1 = new javax.swing.tree.DefaultMutableTreeNode("root_child1");
		treeModel.insertNodeInto(root_child1,rootNode,0);
		javax.swing.tree.DefaultMutableTreeNode child1_child1 = new javax.swing.tree.DefaultMutableTreeNode("child1_child1");
		treeModel.insertNodeInto(child1_child1,root_child1,0);


		org.jdom.Element rootElement = new org.jdom.Element("rootElement");
		rootElement.addContent(new Element("childElement"));
		aWSPJtreePanel.addJdomElement(rootElement);
	
		javax.swing.tree.TreePath rootPath = new javax.swing.tree.TreePath(rootNode.getPath());
		aWSPJtreePanel.getJTree1().expandPath(rootPath);
		//
		frame.setContentPane(aWSPJtreePanel);
		frame.setSize(aWSPJtreePanel.getSize());
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
private void wSPJtreePanel_Initialize() {
	DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode("Parsed Web Data");
	((DefaultTreeModel)(getJTree1().getModel())).setRoot(dmtn);
}
}
