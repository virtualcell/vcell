package cbit.vcell.field;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.zip.DataFormatException;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import cbit.image.ImageException;
import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.FieldDataWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.solvers.CartesianMesh;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JMenuItem;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.FileFilters;
import org.vcell.util.gui.ProgressDialogListener;
import org.vcell.util.gui.SwingDispatcherSync;

public class FieldDataGUIPanel extends JPanel{

	private volatile Thread threadState;
    public void stopThread() {
    	threadState = null;
    }

	final int modePslidExperimentalData = 0;
	final int modePslidGeneratedModel = 1;
	//
	public static final String LIST_NODE_VAR_LABEL = "Variables";
	//
	private JPopupMenu jPopupMenu = new JPopupMenu();
	private static final int COPY_CSV = 0;
	private static final int COPY_NL = 1;
	private static final int COPY_CRNL = 2;
	private static final int COPY_SPACE = 3;
	//
	private class InitializedRootNode {
		private String rootNodeString;
		public InitializedRootNode(String argRNS){
			rootNodeString = argRNS;
		}
		public String toString(){
			return rootNodeString;
		}
	}
	
	private class FieldDataMainList {
		public ExternalDataIdentifier externalDataIdentifier;
		public String extDataAnnot;
		public FieldDataMainList(ExternalDataIdentifier argExternalDataIdentifier,String argExtDataAnnot){
			externalDataIdentifier = argExternalDataIdentifier;
			extDataAnnot = argExtDataAnnot;
		}
		public String toString(){
			return externalDataIdentifier.getName();
		}
	}
	
	private class FieldDataVarMainList {
		public FieldDataVarMainList(){
		}
		public String toString(){
			return LIST_NODE_VAR_LABEL;
		}
	}

	private class FieldDataVarList {
		public DataIdentifier dataIdentifier;
		private String descr;
		public FieldDataVarList(DataIdentifier argDataIdentifier){
			dataIdentifier = argDataIdentifier;
			if(dataIdentifier.getVariableType().compareEqual(VariableType.VOLUME)||
					dataIdentifier.getVariableType().compareEqual(VariableType.VOLUME_REGION)){
				descr = "(Vol"+(dataIdentifier.isFunction()?"Fnc":"")+") "+dataIdentifier.getName();
			}else if(dataIdentifier.getVariableType().compareEqual(VariableType.MEMBRANE) ||
					dataIdentifier.getVariableType().compareEqual(VariableType.MEMBRANE_REGION)){
				descr = "(Mem"+(dataIdentifier.isFunction()?"Fnc":"")+") "+dataIdentifier.getName();
			}else{
				descr = "(---"+(dataIdentifier.isFunction()?"Fnc":"")+") "+dataIdentifier.getName();
			}
		}
		public String toString(){
			return descr;
		}
	}
	
	private class FieldDataTimeList {
		public double[] times;
		private String descr;
		public FieldDataTimeList(double[] argTimes){
			times = argTimes;
			descr = 
				"Times ( "+times.length+" )   Begin="+times[0]+
				"   End="+times[times.length-1];
		}
		public String toString(){
			return descr;
		}
	}
	
	private class FieldDataIDList {
		public KeyValue key = null;
		private String descr;
		public FieldDataIDList(KeyValue k){
			key = k;
			descr ="Key (" + k + ")";
		}
		public String toString(){
			return descr;
		}
	}
	
	private class FieldDataISizeList {
		public ISize isize;
		private String descr;
		public FieldDataISizeList(ISize arg){
			isize = arg;
			descr ="Size ( "+
			isize.getX()+" , "+
			isize.getY()+" , "+
			isize.getZ()+" )";
		}
		public String toString(){
			return descr;
		}
	}

	private class FieldDataOriginList {
		public Origin origin;
		private String descr;
		public FieldDataOriginList(Origin arg){
			origin = arg;
			descr ="Origin ( "+
			origin.getX()+" , "+
			origin.getY()+" , "+
			origin.getZ()+" )";
		}
		public String toString(){
			return descr;
		}
	}

	private class FieldDataExtentList {
		public Extent extent;
		private String descr;
		public FieldDataExtentList(Extent arg){
			extent = arg;
			descr ="Extent ( "+
			extent.getX()+" , "+
			extent.getY()+" , "+
			extent.getZ()+" )";
		}
		public String toString(){
			return descr;
		}
	}


	private FieldDataWindowManager fieldDataWindowManager;
	//
	private javax.swing.JButton ivjJButtonFDDelete = null;
	private javax.swing.JButton ivjJButtonFDFromFile = null;
	private javax.swing.JButton ivjJButtonFDFromSim = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JButton ivjJButtonFDView = null;
	private javax.swing.JTree ivjJTree1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JButton ivjJButtonFDCopyRef = null;
	private JButton jButtonFindRefModel = null;
	private JPanel jPanel = null;
	private JButton jButtonCreateGeom = null;
	private JPanel jPanel1 = null;
	private JButton jButtonViewAnnot = null;
	class IvjEventHandler implements ActionListener,TreeExpansionListener, javax.swing.event.TreeSelectionListener {
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDDelete()) 
			connEtoC7(e);
		if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDCopyRef()) 
			connEtoC8(e);
		if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDFromFile()) 
			connEtoC9(e);
		if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDView()) 
			connEtoC10(e);
		if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDFromSim()) 
			connEtoC11(e);
	};
		public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {};
		public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
			if (event.getSource() == FieldDataGUIPanel.this.getJTree1()) 
				connEtoC6(event);
		};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == FieldDataGUIPanel.this.getJTree1()) 
				connEtoC2(e);
		};
	};

/**
 * FieldDataGUIPanel constructor comment.
 */
public FieldDataGUIPanel() {
	super();
	initialize();
}

public static String inputStrictName(String initalValue,String message)throws UserCancelException{
	String strictName = initalValue;
	while(true){
		strictName =
			PopupGenerator.showInputDialog((Component)null, message, strictName);
		String fixedVarName = TokenMangler.fixTokenStrict(strictName);
		if(!strictName.equals(fixedVarName)){
			int result =
				PopupGenerator.showComponentOKCancelDialog(null, null,
					"Special characters were removed.\n"+
					"Is the value "+fixedVarName+" alright?");
			if(result == JOptionPane.OK_OPTION){
				strictName = fixedVarName;
			}else{
				continue;
			}
		}
		return strictName;
	}
	
}

/**
 * connEtoC1:  (FieldDataGUIPanel.initialize() --> FieldDataGUIPanel.fieldDataGUIPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.fieldDataGUIPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JTree1.treeSelection.valueChanged(javax.swing.event.TreeSelectionEvent) --> FieldDataGUIPanel.jTree1_ValueChanged(Ljavax.swing.event.TreeSelectionEvent;)V)
 * @param arg1 javax.swing.event.TreeSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.TreeSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTree1_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


private void connEtoC6(javax.swing.event.TreeExpansionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTree1_TreeExpanded(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFDDelete_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFDCopyRef_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private void copyMethod(int copyMode){
	String delimiter = "";
	if(copyMode == COPY_NL){
		delimiter = "\n";
	}else if(copyMode == COPY_CRNL){
		delimiter = "\r\n";
	}else if(copyMode == COPY_CSV){
		delimiter = ",";
	}else if(copyMode == COPY_SPACE){
		delimiter = " ";
	}
	String copyString = "";
	javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
	if(selPath != null){
		javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
		if(lastPathComponent.equals(getJTree1().getModel().getRoot())){
			int childCount = lastPathComponent.getChildCount();
			for(int i=0;i<childCount;i+= 1){
				if(i != 0){
					copyString+=delimiter;
				}
				copyString+=
					((FieldDataMainList)((DefaultMutableTreeNode)lastPathComponent.getChildAt(i)).getUserObject()).externalDataIdentifier.getName();
			}					
		}else if(lastPathComponent.getUserObject() instanceof FieldDataOriginList){
			Origin origin = ((FieldDataOriginList)lastPathComponent.getUserObject()).origin;
			copyString = origin.getX()+delimiter+origin.getY()+delimiter+origin.getZ();
		}else if(lastPathComponent.getUserObject() instanceof FieldDataExtentList){
			Extent extent = ((FieldDataExtentList)lastPathComponent.getUserObject()).extent;
			copyString = extent.getX()+delimiter+extent.getY()+delimiter+extent.getZ();
		}else if(lastPathComponent.getUserObject() instanceof FieldDataISizeList){
			ISize isize = ((FieldDataISizeList)lastPathComponent.getUserObject()).isize;
			copyString = isize.getX()+delimiter+isize.getY()+delimiter+isize.getZ();
		}else if(lastPathComponent.getUserObject() instanceof FieldDataTimeList){
			double[] times = ((FieldDataTimeList)lastPathComponent.getUserObject()).times;
			for(int i=0;i<times.length;i+= 1){
				if(i != 0){
					copyString+=delimiter;
				}
				copyString+= times[i]+"";
			}
		}else if(lastPathComponent.getUserObject() instanceof FieldDataMainList){
			ExternalDataIdentifier extDataID =
				((FieldDataMainList)lastPathComponent.getUserObject()).externalDataIdentifier;
			copyString = extDataID.getName();
		}else if(lastPathComponent.getUserObject() instanceof FieldDataVarList){
			DataIdentifier dataIdentifier =
				((FieldDataVarList)lastPathComponent.getUserObject()).dataIdentifier;
			copyString = dataIdentifier.getName();
		}else if(lastPathComponent.getUserObject() instanceof FieldDataVarMainList){
			int childCount = lastPathComponent.getChildCount();
			for(int i=0;i<childCount;i+= 1){
				if(i != 0){
					copyString+=delimiter;
				}
				copyString+=
					((FieldDataVarList)((DefaultMutableTreeNode)lastPathComponent.getChildAt(i)).getUserObject()).dataIdentifier.getName();
			}
		}
		if(copyString.length() > 0 ){
			VCellTransferable.sendToClipboard(copyString);
		}
	}
}

private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFDFromFile_ActionPerformed(arg1,null,null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFDView_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFDFromSim_ActionPerformed(arg1);
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
private void fieldDataGUIPanel_Initialize() {

	getJTree1().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);

	JMenuItem copyMenuItem = new JMenuItem("Copy");
	copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			copyMethod(COPY_SPACE);
		}
	});
	JMenuItem copyCSVMenuItem = new JMenuItem("Copy w/ Commas");
	copyCSVMenuItem.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			copyMethod(COPY_CSV);
		}
	});
	JMenuItem copyNewLineMenuItem = new JMenuItem("Copy w/ LF");
	copyNewLineMenuItem.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			copyMethod(COPY_NL);
		}
	});
	JMenuItem copyReturnNewLineMenuItem = new JMenuItem("Copy w/ CRLF");
	copyReturnNewLineMenuItem.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			copyMethod(COPY_CRNL);
		}
	});

}

public void updateJTree(final RequestManager clientRequestManager){
	
	if(clientRequestManager == null){
		DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode("No Info Available");
		getJTree1().setModel(new DefaultTreeModel(emptyNode));
	}else{
		DefaultMutableTreeNode startupNode =
			new DefaultMutableTreeNode("Gathering Field Data Information... (Please wait)");
		getJTree1().setModel(new DefaultTreeModel(startupNode));
		Runnable gatherInfo = new Runnable() {
			public void run(){
				try{
					
						FieldDataDBOperationResults fieldDataDBOperationResults =
						clientRequestManager.getDocumentManager().fieldDataDBOperation(
								FieldDataDBOperationSpec.createGetExtDataIDsSpec(
										clientRequestManager.getDocumentManager().getUser()));
						
						ExternalDataIdentifier[] externalDataIdentifierArr =
							fieldDataDBOperationResults.extDataIDArr;
						String[] extDataAnnotArr = fieldDataDBOperationResults.extDataAnnotArr;
						
						TreeMap<ExternalDataIdentifier, String> sortedExtDataIDTreeMap =
							new TreeMap<ExternalDataIdentifier, String>(
									new Comparator<ExternalDataIdentifier>(){
									public int compare(ExternalDataIdentifier o1, ExternalDataIdentifier o2) {
										return o1.getName().compareToIgnoreCase(o2.getName());
									}
								}									
							);
						for(int i=0;i<externalDataIdentifierArr.length;i+= 1){
							sortedExtDataIDTreeMap.put(externalDataIdentifierArr[i],extDataAnnotArr[i]);
						}
					final DefaultMutableTreeNode rootNode =
						new DefaultMutableTreeNode(new InitializedRootNode(
								"Field Data Info"+(externalDataIdentifierArr.length==0?" (None Defined)":"")));
					
					Iterator<Entry<ExternalDataIdentifier, String>> sortIter =
						sortedExtDataIDTreeMap.entrySet().iterator();
					while(sortIter.hasNext()){
						Entry<ExternalDataIdentifier, String> entry = sortIter.next();
//					for(int i=0;i<externalDataIdentifierArr.length;i+= 1){
						javax.swing.tree.DefaultMutableTreeNode mainNode =
							new javax.swing.tree.DefaultMutableTreeNode(
									new FieldDataMainList(entry.getKey(),entry.getValue()));
						mainNode.add(new DefaultMutableTreeNode(new FieldDataVarMainList()));
						rootNode.add(mainNode);
					}
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							getJTree1().setModel(new DefaultTreeModel(rootNode));
						}
					});
				}catch(Exception e){
					final DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode("Error Getting Field Data Information");
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							getJTree1().setModel(new DefaultTreeModel(errorNode));
						}
					});
					e.printStackTrace();
					PopupGenerator.showErrorDialog("Error Getting Field Data Info\n"+e.getMessage());
				}
			}
		};
		new Thread(gatherInfo).start();
	}
}


/**
 * Return the JButtonFDCopyRef property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFDCopyRef() {
	if (ivjJButtonFDCopyRef == null) {
		try {
			ivjJButtonFDCopyRef = new javax.swing.JButton();
			ivjJButtonFDCopyRef.setName("JButtonFDCopyRef");
			ivjJButtonFDCopyRef.setText("Copy Func");
			ivjJButtonFDCopyRef.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFDCopyRef;
}

/**
 * Return the JButtonFDDelete property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFDDelete() {
	if (ivjJButtonFDDelete == null) {
		try {
			ivjJButtonFDDelete = new javax.swing.JButton();
			ivjJButtonFDDelete.setName("JButtonFDDelete");
			ivjJButtonFDDelete.setText("Delete");
			ivjJButtonFDDelete.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFDDelete;
}

/**
 * Return the JButtonFDFromFile property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFDFromFile() {
	if (ivjJButtonFDFromFile == null) {
		try {
			ivjJButtonFDFromFile = new javax.swing.JButton();
			ivjJButtonFDFromFile.setName("JButtonFDFromFile");
			ivjJButtonFDFromFile.setText("From File...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFDFromFile;
}


/**
 * Return the JButtonFDFromSim property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFDFromSim() {
	if (ivjJButtonFDFromSim == null) {
		try {
			ivjJButtonFDFromSim = new javax.swing.JButton();
			ivjJButtonFDFromSim.setName("JButtonFDFromSim");
			ivjJButtonFDFromSim.setText("From Simulation Data...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFDFromSim;
}


/**
 * Return the JButtonFDView property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFDView() {
	if (ivjJButtonFDView == null) {
		try {
			ivjJButtonFDView = new javax.swing.JButton();
			ivjJButtonFDView.setName("JButtonFDView");
			ivjJButtonFDView.setText("View...");
			ivjJButtonFDView.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFDView;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Create New Field Data");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(ivjLocalBorder);
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,7};
			ivjJPanel1.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsJButtonFDFromFile = new java.awt.GridBagConstraints();
			constraintsJButtonFDFromFile.gridx = 1; constraintsJButtonFDFromFile.gridy = 1;
			constraintsJButtonFDFromFile.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonFDFromFile.weightx = 1.0;
			constraintsJButtonFDFromFile.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonFDFromFile(), constraintsJButtonFDFromFile);

			java.awt.GridBagConstraints constraintsJButtonFDFromSim = new java.awt.GridBagConstraints();
			constraintsJButtonFDFromSim.gridx = 2; constraintsJButtonFDFromSim.gridy = 1;
			constraintsJButtonFDFromSim.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonFDFromSim.weightx = 1.0;
			constraintsJButtonFDFromSim.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonFDFromSim(), constraintsJButtonFDFromSim);

			final JButton fromPslidExperimentalButton = new JButton();
			fromPslidExperimentalButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					getPslidSelections(modePslidExperimentalData);
				}
			});
			fromPslidExperimentalButton.setText("PSLID Experimental");
			final GridBagConstraints gridBagConstraintsE = new GridBagConstraints();
			gridBagConstraintsE.insets = new Insets(4, 4, 4, 4);
			gridBagConstraintsE.gridy = 2;
			gridBagConstraintsE.gridx = 1;
			ivjJPanel1.add(fromPslidExperimentalButton, gridBagConstraintsE);
			
			final JButton fromPslidGeneratedButton = new JButton();
			fromPslidGeneratedButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					getPslidSelections(modePslidGeneratedModel);
				}
			});
			fromPslidGeneratedButton.setText("PSLID Generated Model");
			final GridBagConstraints gridBagConstraintsG = new GridBagConstraints();
			gridBagConstraintsG.insets = new Insets(4, 4, 4, 4);
			gridBagConstraintsG.gridy = 2;
			gridBagConstraintsG.gridx = 2;
			ivjJPanel1.add(fromPslidGeneratedButton, gridBagConstraintsG);
			
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}


/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.gridy = 0;
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
constraintsJPanel1.gridheight = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJButtonFDDelete = new java.awt.GridBagConstraints();
			constraintsJButtonFDDelete.gridx = 1; constraintsJButtonFDDelete.gridy = 0;
			constraintsJButtonFDDelete.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonFDDelete.weightx = 0.0;
			constraintsJButtonFDDelete.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsJButtonFDView = new java.awt.GridBagConstraints();
			constraintsJButtonFDView.gridx = 1; constraintsJButtonFDView.gridy = 1;
			constraintsJButtonFDView.gridwidth = 1;
			constraintsJButtonFDView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonFDView.insets = new java.awt.Insets(4, 4, 4, 4);
 			ivjJPanel2.add(getJButtonFDDelete(), constraintsJButtonFDDelete);
			ivjJPanel2.add(getJButtonFDView(), constraintsJButtonFDView);
			ivjJPanel2.add(getJPanel(), gridBagConstraints21);
			ivjJPanel2.add(getJPanel12(), gridBagConstraints4);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
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
			ivjJTree1.setBounds(0, 0, 516, 346);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
			ivjJTree1.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(MouseEvent e) {
			        maybeShowPopup(e);
			    }

			    public void mouseReleased(MouseEvent e) {
			        maybeShowPopup(e);
			    }
			    private void maybeShowPopup(MouseEvent e) {
			        if (e.isPopupTrigger()) {
			            jPopupMenu.show(e.getComponent(),
			                       e.getX(), e.getY());
			        }
			    }
			});
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
	getJTree1().addTreeSelectionListener(ivjEventHandler);
	getJTree1().addTreeExpansionListener(ivjEventHandler);
	getJButtonFDDelete().addActionListener(ivjEventHandler);
	getJButtonFDCopyRef().addActionListener(ivjEventHandler);
	getJButtonFDFromFile().addActionListener(ivjEventHandler);
	getJButtonFDView().addActionListener(ivjEventHandler);
	getJButtonFDFromSim().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("FieldDataGUIPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(676, 430);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 0;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);
		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


public boolean isInitialized(){
	DefaultMutableTreeNode rootNode =
		(DefaultMutableTreeNode)getJTree1().getModel().getRoot();
	if(rootNode == null){
		return false;
	}
	return (rootNode.getUserObject() instanceof InitializedRootNode);
}
/**
 * Comment
 */
private void jTree1_ValueChanged(javax.swing.event.TreeSelectionEvent treeSelectionEvent) {
	getJButtonFDDelete().setEnabled(false);
	getJButtonFDView().setEnabled(false);
	getJButtonFDCopyRef().setEnabled(false);
	getJButtonFindRefModel().setEnabled(false);
	getJButtonViewAnnot().setEnabled(false);
	getJButtonCreateGeom().setEnabled(false);
	javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
	if(selPath != null){
		javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
//		System.out.println("count="+selPath.getPathCount()+"  "+(lastPathComponent != null?lastPathComponent.toString():"null"));
		if(lastPathComponent.getUserObject() instanceof FieldDataMainList){
			getJButtonFDDelete().setEnabled(true);
			getJButtonFDView().setEnabled(fieldDataWindowManager != null);
			getJButtonFindRefModel().setEnabled(true);
			getJButtonViewAnnot().setEnabled(true);
		}else if (lastPathComponent.getUserObject() instanceof FieldDataVarList){
			getJButtonFDCopyRef().setEnabled(true);
			getJButtonCreateGeom().setEnabled(true);
		}
	}
}

private void jButtonFDFromSim_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	try{
		final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();

		final FieldDataWindowManager.SimInfoHolder simInfoHolder = fieldDataWindowManager.selectSimulationFromDesktop(this);
		if(simInfoHolder == null){
			PopupGenerator.showErrorDialog(
					"Please open a Bio or Math model containing the spatial (non-compartmental)\nsimulation you wish to use to create a new Field Data");
			return;
		}
		//Check that the sim is in a state that can be copied
		SimulationStatus simStatus =
			clientRequestManager.getServerSimulationStatus(simInfoHolder.simInfo);
		if(simStatus != null && (simStatus.isRunning() || simStatus.isStartRequested())){
			throw new Exception("Can't copy 'running' simulation data from sim '"+simInfoHolder.simInfo.getName()+"'");
		}

		final FieldDataFileOperationSpec fdos =
			FieldDataFileOperationSpec.createCopySimFieldDataFileOperationSpec(
					null,
					(simInfoHolder.simInfo.getParentSimulationReference() != null?
							simInfoHolder.simInfo.getParentSimulationReference():
							simInfoHolder.simInfo.getSimulationVersion().getVersionKey()
					), 
					simInfoHolder.simInfo.getOwner(),
					simInfoHolder.jobIndex,
					clientRequestManager.getDocumentManager().getUser());

		AsynchClientTask CreateFDFromSim = new AsynchClientTask("Create Field Data from Simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			public void run(Hashtable<String, Object> hash) throws Exception {
				FieldDataFileOperationResults fdor =
					clientRequestManager.getDocumentManager().fieldDataFileOperation(
							FieldDataFileOperationSpec.createInfoFieldDataFileOperationSpec(
									(simInfoHolder.simInfo.getParentSimulationReference() != null?
											simInfoHolder.simInfo.getParentSimulationReference():
											simInfoHolder.simInfo.getSimulationVersion().getVersionKey()
									),
									simInfoHolder.simInfo.getOwner(),
									simInfoHolder.jobIndex));
				//Create (non-editable) info for display
				fdos.origin = fdor.origin;
				fdos.extent = fdor.extent;
				fdos.isize = fdor.iSize;
				fdos.times = fdor.times;
				fdos.varNames = new String[fdor.dataIdentifierArr.length];
				for(int i=0;i<fdor.dataIdentifierArr.length;i+= 1){
					fdos.varNames[i] =
						(fdor.dataIdentifierArr[i].getVariableType().equals(VariableType.VOLUME)?"(vol) ":"")+
						(fdor.dataIdentifierArr[i].getVariableType().equals(VariableType.MEMBRANE)?"(mbr) ":"")+
						fdor.dataIdentifierArr[i].getName();
				}
				addNewExternalData(clientRequestManager, fdos, simInfoHolder.simInfo.getName(), true);
			}
		};

		AsynchClientTask tasks[] = new AsynchClientTask[] { CreateFDFromSim};
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		ClientTaskDispatcher.dispatch(this,hash,tasks,false);
		
	}catch(UserCancelException e){
		return;
	}catch(Exception e){
		PopupGenerator.showErrorDialog("Error creating Field Data from simulation\n"+e.getMessage());
	}
}

private void jButtonFDView_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(fieldDataWindowManager == null){
		System.out.println("No FieldDataViewManager available for View action");
		return;
	}
	javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
	if(selPath != null){
		javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
		if(lastPathComponent.getUserObject() instanceof FieldDataMainList){
			ExternalDataIdentifier edi = ((FieldDataMainList)lastPathComponent.getUserObject()).externalDataIdentifier;
				fieldDataWindowManager.viewData(edi);
		}
	}
}


public static FieldDataFileOperationSpec createFDOSFromImageFile(File imageFile,boolean bCropOutBlack) throws DataFormatException,ImageException{
	ImageDataset imagedataSet = null;
	final FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	try{
		imagedataSet = ImageDatasetReader.readImageDataset(imageFile.getAbsolutePath(),null);
		if (imagedataSet!=null && bCropOutBlack){
//			System.out.println("FieldDataGUIPanel.jButtonFDFromFile_ActionPerformed(): BEFORE CROPPING, size="+imagedataSet.getISize().toString());
			imagedataSet = imagedataSet.crop(imagedataSet.getNonzeroBoundingRectangle());
//			System.out.println("FieldDataGUIPanel.jButtonFDFromFile_ActionPerformed(): AFTER CROPPING, size="+imagedataSet.getISize().toString());
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new DataFormatException(e.getMessage());
	}
	//[time][var][data]
	int numXY = imagedataSet.getISize().getX()*imagedataSet.getISize().getY();
	int numXYZ = imagedataSet.getSizeZ()*numXY;
	fdos.variableTypes = new VariableType[imagedataSet.getSizeC()];
	fdos.varNames = new String[imagedataSet.getSizeC()];
	short[][][] shortData =
		new short[imagedataSet.getSizeT()][imagedataSet.getSizeC()][numXYZ];
	for(int c=0;c<imagedataSet.getSizeC();c+= 1){
		fdos.variableTypes[c] = VariableType.VOLUME;
		fdos.varNames[c] = "Channel"+c;
		for(int t=0;t<imagedataSet.getSizeT();t+=1){
			int zOffset = 0;
			for(int z=0;z<imagedataSet.getSizeZ();z+=1){
				UShortImage ushortImage = imagedataSet.getImage(z,c,t);
				System.arraycopy(ushortImage.getPixels(), 0, shortData[t][c], zOffset, numXY);
//				shortData[t][c] = ushortImage.getPixels();
				zOffset+= numXY;
			}
		}
	}
	fdos.shortSpecData = shortData;
	fdos.times = imagedataSet.getImageTimeStamps();
	if(fdos.times == null){
		fdos.times = new double[imagedataSet.getSizeT()];
		for(int i=0;i<fdos.times.length;i+= 1){
			fdos.times[i] = i;
		}
	}

	fdos.origin = new Origin(0,0,0);
	fdos.extent = (imagedataSet.getExtent()!=null)?(imagedataSet.getExtent()):(new Extent(1,1,1));
	fdos.isize = imagedataSet.getISize();
	
	return fdos;
}


private void jButtonFDFromFile_ActionPerformed(java.awt.event.ActionEvent actionEvent,final FieldDataFileOperationSpec argfdos,final String arginitFDName) {
	
	final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();
		
	try{
	
		AsynchClientTask importImageTask = new AsynchClientTask("Import image", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			public void run(Hashtable<String, Object> hash) throws Exception {
				FieldDataFileOperationSpec fdos = null;
				String initFDName = null;
				
				if (argfdos == null) {
					File imageFile = DatabaseWindowManager.showFileChooserDialog(FileFilters.FILE_FILTER_FIELDIMAGES, clientRequestManager.getUserPreferences());
					initFDName = imageFile.getName();
					//read VCell Special
					DatabaseWindowManager.ImageHelper imageHelper = null;
					//				if(imagedataSet == null){
					try {
						fdos = new FieldDataFileOperationSpec();
						imageHelper = DatabaseWindowManager.readFromImageFile(
								getClientTaskStatusSupport(), imageFile);
						fdos.times = new double[] { 0 };
						fdos.varNames = new String[] { "variableName" };
						fdos.origin = new Origin(0, 0, 0);
						fdos.extent = new Extent(1, 1, 1);
						fdos.isize = new ISize(imageHelper.xsize,
								imageHelper.ysize, imageHelper.zsize);
						short[] shortPixels = new short[imageHelper.imageData.length];
						for (int i = 0; i < shortPixels.length; i += 1) {
							shortPixels[i] = (short) (0x000000FF & imageHelper.imageData[i]);
						}
						fdos.shortSpecData = new short[][][] { { shortPixels } };
						fdos.variableTypes = new VariableType[] { VariableType.VOLUME };
					} catch (Exception e) {
						System.out
								.println("VCell simple image import couldn't read "
										+ e.getMessage());
						fdos = null;
					}
					//				}
					//read BioFormat
					if (fdos == null) {
						try {
							fdos = createFDOSFromImageFile(imageFile,true);
						} catch (DataFormatException e) {
							System.out.println("BioFormat couldn't read "
									+ e.getMessage());
							fdos = null;
						}
					}
					if(fdos == null){
						throw new Exception("Neither BioFormats or VCell can read image "+imageFile.getAbsolutePath());
					}
				}else{
					fdos = argfdos;
					initFDName = arginitFDName;
				}
				
				fdos.owner = clientRequestManager.getDocumentManager().getUser();
				fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
				addNewExternalData(clientRequestManager, fdos, initFDName, false);
			}
		};
		//
		//Execute Field Data Info - JTree tasks
		//
		AsynchClientTask tasks[] = new AsynchClientTask[] {importImageTask};
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		ClientTaskDispatcher.dispatch(this,hash,tasks,false);
	}catch(UserCancelException e){
		return;
	}
}

private FieldDataFileOperationSpec getExternalDataInfoFromUser(
		FieldDataInfoPanel fdip,
		RequestManager clientRequestManager) throws UserCancelException{
	FieldDataFileOperationSpec tempFDOS;
	while(true){
		tempFDOS = new FieldDataFileOperationSpec();
		if(PopupGenerator.showComponentOKCancelDialog(FieldDataGUIPanel.this, fdip, "Field Data Info") == JOptionPane.OK_OPTION){
			//Check values
			try{
				tempFDOS.extent = fdip.getExtent();
			}catch(Exception e){
				PopupGenerator.showErrorDialog("Problem with Extent values. Please re-enter.\n"+e.getMessage()+"\nTry Again.");
				continue;
				}
			try{
				tempFDOS.origin = fdip.getOrigin();
			}catch(Exception e){
				PopupGenerator.showErrorDialog("Problem with Origin values. Please re-enter.\n"+e.getMessage()+"\nTry Again.");
				continue;
			}
			try{
				tempFDOS.varNames = fdip.getVariableNames();
			}catch(Exception e){
				PopupGenerator.showErrorDialog("Problem with Variable names. Please re-enter.\n"+e.getMessage()+"\nTry Again.");
				continue;
			}
			tempFDOS.annotation = fdip.getAnnotation();
			tempFDOS.times = fdip.getTimes();
			try{
				if(fdip.getFieldName() == null ||
					fdip.getFieldName().length() == 0 ||
					!fdip.getFieldName().equals(TokenMangler.fixTokenStrict(fdip.getFieldName()))){
					throw new Exception("Field Data names can contain only letters,digits and underscores");
				}
				//Check to see if this name is already used
				DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)getJTree1().getModel().getRoot();
				for(int i=0;i<rootNode.getChildCount();i+= 1){
					ExternalDataIdentifier extDataID =
						((FieldDataMainList)((DefaultMutableTreeNode)rootNode.getChildAt(i)).getUserObject()).externalDataIdentifier;
					if(fdip.getFieldName().equals(extDataID.getName())){
						throw new Exception("New Field Data name "+fdip.getFieldName()+" already used.");
					}
				}
				//save Database
				tempFDOS.specEDI =
					clientRequestManager.getDocumentManager().fieldDataDBOperation(
							FieldDataDBOperationSpec.createSaveNewExtDataIDSpec(
									clientRequestManager.getDocumentManager().getUser(),
									fdip.getFieldName(),tempFDOS.annotation)).extDataID;
			}catch(Exception e){
				PopupGenerator.showErrorDialog("Error saving Field Data Name to Database. Try again.\n"+e.getMessage());
				continue;
			}
			return tempFDOS;
		}else{
			throw UserCancelException.CANCEL_GENERIC;
		}

	}
	
}

private void jButtonFDDelete_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();

	
	javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
	final javax.swing.tree.DefaultMutableTreeNode mainNode =
		(javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
	final FieldDataMainList fieldDataMainList = (FieldDataMainList)mainNode.getUserObject();
	
	if(!fieldDataMainList.externalDataIdentifier.getOwner().equals(
		clientRequestManager.getDocumentManager().getUser())){
		DialogUtils.showErrorDialog("Delete failed: User "+clientRequestManager.getDocumentManager().getUser().getName()+
				"does not own FieldData '"+fieldDataMainList.externalDataIdentifier.getName()+"'");
	}
	if(PopupGenerator.showComponentOKCancelDialog(
			this, new JLabel("Delete "+fieldDataMainList.externalDataIdentifier.getName()+"?"),
			"Confirm Delete") != JOptionPane.OK_OPTION){
		return;
		
	}
	AsynchClientTask CheckRemoveFromDBTask = new AsynchClientTask("Check Field Data references in DB", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hash) throws Exception {
			if(fieldDataWindowManager.findReferencingModels(fieldDataMainList.externalDataIdentifier, false)){
				throw new Exception("Cannot delete Field Data '"+fieldDataMainList.externalDataIdentifier.getName()+
						"' because it is referenced in a Model(s) or Function(s) file.");
			}
		}
	};
	AsynchClientTask RemoveNodeTreeTask = new AsynchClientTask("Remove FieldData tree node", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hash) throws Exception {
			((DefaultTreeModel)getJTree1().getModel()).removeNodeFromParent(mainNode);
			if(((DefaultMutableTreeNode)getJTree1().getModel().getRoot()).getChildCount() == 0){
				updateJTree(clientRequestManager);
			}
		}
	};
	AsynchClientTask RemoveFromDiskAndDBTask = new AsynchClientTask("Remove Field Data from Disk and DB", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hash) throws Exception {
			//Remove from Disk
			FieldDataMainList fieldDataMainList = (FieldDataMainList)mainNode.getUserObject();
			FieldDataFileOperationSpec fdos = 
				FieldDataFileOperationSpec.createDeleteFieldDataFileOperationSpec(
					fieldDataMainList.externalDataIdentifier);
			clientRequestManager.getDocumentManager().fieldDataFileOperation(fdos);
			//Remove from DB
			fieldDataWindowManager.deleteExternalDataIdentifier(fieldDataMainList.externalDataIdentifier);
		}
	};
	//
	//Execute Field Data Info - JTree tasks
	//
	AsynchClientTask tasks[] = new AsynchClientTask[] { CheckRemoveFromDBTask,RemoveFromDiskAndDBTask,RemoveNodeTreeTask};
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	ClientTaskDispatcher.dispatch(this,hash,tasks,false);

}

private void jButtonFDCopyRef_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
	javax.swing.tree.DefaultMutableTreeNode varNode =
		(javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
	DefaultMutableTreeNode mainNode = (DefaultMutableTreeNode)varNode.getParent().getParent();
	Enumeration<DefaultMutableTreeNode> children = mainNode.children();
	double[] times = null;
	while(children.hasMoreElements()){
		DefaultMutableTreeNode child = children.nextElement();
		if(child.getUserObject() instanceof FieldDataTimeList){
			times = ((FieldDataTimeList)child.getUserObject()).times;
			break;
		}
	}

	int begIndex = 0;
	if(times != null && times.length > 1){
		String[] timesStr = new String[times.length];
		for(int i=0;i<times.length;i+= 1){
			timesStr[i] = times[i]+"";
		}
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp,BoxLayout.X_AXIS);
		jp.setLayout(bl);
		JComboBox jcBeg = new JComboBox(Arrays.asList(timesStr).toArray(new String[0]));
		jp.add(jcBeg);
		
		if(PopupGenerator.showComponentOKCancelDialog(this, jp,
				(actionEvent.getSource() == getJButtonFDCopyRef()?
					"Select Field Data time for function":
					"Select Field Data time for New Geometry"
				)
				) ==JOptionPane.OK_OPTION){
			begIndex = jcBeg.getSelectedIndex();
		}
	}
	if(actionEvent.getSource() == getJButtonFDCopyRef()){
		String fieldFunctionReference =
			SimulationData.createCanonicalFieldFunctionSyntax(
					((FieldDataMainList)mainNode.getUserObject()).externalDataIdentifier.getName(),
					((FieldDataVarList)varNode.getUserObject()).dataIdentifier.getName(),
					times[begIndex],((FieldDataVarList)varNode.getUserObject()).dataIdentifier.getVariableType().getTypeName());
	
		VCellTransferable.sendToClipboard(fieldFunctionReference);
	}else if(actionEvent.getSource() == getJButtonCreateGeom()){
		try {
			PDEDataContext pdeDataContext =
				fieldDataWindowManager.getPDEDataContext(((FieldDataMainList)mainNode.getUserObject()).externalDataIdentifier);
			pdeDataContext.setVariableAndTime(((FieldDataVarList)varNode.getUserObject()).dataIdentifier.getName(), pdeDataContext.getTimePoints()[begIndex]);
			double[] data = pdeDataContext.getDataValues();
			byte[] segmentedData = new byte[data.length];
			Vector<Double> distinctValues = new Vector<Double>();
			int index = -1;
			for (int i = 0; i < data.length; i++) {
				if((index = distinctValues.indexOf(data[i])) == -1){
					index = distinctValues.size();
					distinctValues.add(data[i]);
					if(distinctValues.size() > 256){
						throw new Exception("FieldData "+
								((FieldDataMainList)mainNode.getUserObject()).externalDataIdentifier.getName()+
								" has more than 256 distinct values.");
					}
				}
				segmentedData[i] = (byte)index;
			}
			fieldDataWindowManager.newDocument(
				new VCDocument.GeomFromFieldDataCreationInfo(
					VCDocument.GEOMETRY_DOC,VCDocument.GEOM_OPTION_FIELDDATA,
					segmentedData,
					new ISize(pdeDataContext.getCartesianMesh().getSizeX(),pdeDataContext.getCartesianMesh().getSizeY(),pdeDataContext.getCartesianMesh().getSizeZ()),
					pdeDataContext.getCartesianMesh().getExtent(),
					null,
					"Created from Field Data "+
					((FieldDataMainList)mainNode.getUserObject()).externalDataIdentifier.getName()+":\n"+
					"Variable="+pdeDataContext.getVariableName()+" Time="+pdeDataContext.getTimePoint()
				)
			);
		} catch (Exception e) {
			e.printStackTrace();
			PopupGenerator.showErrorDialog("Error creating Geometry\n"+e.getMessage());
		}
	}
	
}

private void jTree1_TreeExpanded(final javax.swing.event.TreeExpansionEvent treeExpansionEvent) {
	if(fieldDataWindowManager == null){
		return;
	}
	//
	//Determine if we need to get Info
	//
	javax.swing.tree.TreePath expPath = null;
	try{
		expPath = treeExpansionEvent.getPath();
		if(expPath != null){
			DefaultMutableTreeNode mainNode = (javax.swing.tree.DefaultMutableTreeNode)expPath.getLastPathComponent();
//			if(mainNode.equals(getJTree1().getModel().getRoot())){
//				System.out.println("Root Node expanded");
//			}
			if(mainNode.getUserObject() instanceof FieldDataMainList){
				if(mainNode.getChildCount() > 1){//Already populated
					return;
				}
				refreshMainNode(mainNode);
			}else{
				return;
			}
		}else{
			return;
		}
	}catch(Exception e){
		PopupGenerator.showErrorDialog("Error getting Field Data Info\n"+e.getMessage());
		return;
	}
	
}

public void refreshExternalDataIdentifierNode(ExternalDataIdentifier refreshEDI){
	DefaultMutableTreeNode root = (DefaultMutableTreeNode)getJTree1().getModel().getRoot();
	if(root != null){
		int childCount = root.getChildCount();
		for(int i=0;i<childCount;i+= 1){
			DefaultMutableTreeNode mainNode = (DefaultMutableTreeNode)root.getChildAt(i);
			FieldDataMainList fieldDataMainList = (FieldDataMainList)mainNode.getUserObject();
			if(fieldDataMainList.externalDataIdentifier.equals(refreshEDI)){
				refreshMainNode(mainNode);
				return;
			}
		}
	}
}

private void refreshMainNode(final DefaultMutableTreeNode mainNode){
	final boolean isMainExpanded = getJTree1().isExpanded(new TreePath(mainNode.getPath()));
	final boolean isVarExpanded = getJTree1().isExpanded(
			new TreePath(((DefaultMutableTreeNode)mainNode.getLastChild()).getPath()));
	//Remove all children from Main node in a Tree safe way
	DefaultMutableTreeNode root = (DefaultMutableTreeNode)getJTree1().getModel().getRoot();
	int mainNodeIndex =
		((DefaultTreeModel)getJTree1().getModel()).getIndexOfChild(root, mainNode);
	((DefaultTreeModel)getJTree1().getModel()).removeNodeFromParent(mainNode);
	mainNode.removeAllChildren();
	final DefaultMutableTreeNode varNode = new DefaultMutableTreeNode(new FieldDataVarMainList());
	mainNode.add(varNode);
	((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(mainNode,root,mainNodeIndex);
	//
	//Create thread-safe tasks to get Field Data Info and update JTree
	//
	final String FDOR_INFO = "FDOR_INFO";
	final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();

	AsynchClientTask FieldDataInfoTask = new AsynchClientTask("Gather Field Data info", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hash) throws Exception {
			FieldDataMainList fieldDataMainList = (FieldDataMainList)mainNode.getUserObject();
			final FieldDataFileOperationResults fieldDataFileOperationResults =
				clientRequestManager.getDocumentManager().
				fieldDataFileOperation(
						FieldDataFileOperationSpec.createInfoFieldDataFileOperationSpec(
								fieldDataMainList.externalDataIdentifier.getKey(),
								clientRequestManager.getDocumentManager().getUser(),
								FieldDataFileOperationSpec.JOBINDEX_DEFAULT)
						);
			hash.put(FDOR_INFO,fieldDataFileOperationResults);
		}
	};
	
	AsynchClientTask FieldDataInfoTreeUpdate = new AsynchClientTask("Update Field Data GUI", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hash){
			try{
				FieldDataFileOperationResults fieldDataFileOperationResults =
					(FieldDataFileOperationResults)hash.get(FDOR_INFO);
				Arrays.sort(fieldDataFileOperationResults.dataIdentifierArr,
						new Comparator<DataIdentifier>(){
							public int compare(DataIdentifier o1, DataIdentifier o2) {
								return o1.getName().compareToIgnoreCase(o2.getName());
							}
					}
				);
				FieldDataMainList fieldDataMainList = (FieldDataMainList)mainNode.getUserObject();
				final DefaultMutableTreeNode isizeNode =
					new DefaultMutableTreeNode(new FieldDataISizeList(fieldDataFileOperationResults.iSize));
				final DefaultMutableTreeNode originNode =
					new DefaultMutableTreeNode(new FieldDataOriginList(fieldDataFileOperationResults.origin));
				final DefaultMutableTreeNode extentNode =
					new DefaultMutableTreeNode(new FieldDataExtentList(fieldDataFileOperationResults.extent));
				final DefaultMutableTreeNode timeNode =
					new DefaultMutableTreeNode(new FieldDataTimeList(fieldDataFileOperationResults.times));
				final DefaultMutableTreeNode idNode =
					new DefaultMutableTreeNode(new FieldDataIDList(fieldDataMainList.externalDataIdentifier.getKey()));
				((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(isizeNode,mainNode,0);
				((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(originNode,mainNode,1);
				((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(extentNode,mainNode,2);
				((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(timeNode,mainNode,3);
				((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(idNode,mainNode,4);
				for(int i=0;i<fieldDataFileOperationResults.dataIdentifierArr.length;i+= 1){
				((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(
						new DefaultMutableTreeNode(
								new FieldDataVarList(fieldDataFileOperationResults.dataIdentifierArr[i])),
						varNode, varNode.getChildCount());
				}
				if(isMainExpanded){
					getJTree1().expandPath(new TreePath(mainNode.getPath()));
				}
				if(isVarExpanded){
					getJTree1().expandPath(new TreePath(varNode.getPath()));
				}
			}catch(Throwable e){
				hash.put(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR,e);
			}
		}
	};
	
	//
	//Execute Field Data Info - JTree tasks
	//
	AsynchClientTask tasks[] = new AsynchClientTask[] { FieldDataInfoTask,FieldDataInfoTreeUpdate };
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	ClientTaskDispatcher.dispatch(this,hash,tasks,false);
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		FieldDataGUIPanel aFieldDataGUIPanel;
		aFieldDataGUIPanel = new FieldDataGUIPanel();
		frame.setContentPane(aFieldDataGUIPanel);
		frame.setSize(aFieldDataGUIPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

public void setFieldDataWindowManager(FieldDataWindowManager fdwm){
	fieldDataWindowManager = fdwm;
}

private void addNewExternalData(
		RequestManager clientRequestManager,
		final FieldDataFileOperationSpec fdos,
		final String initialExtDataName,
		final boolean isFromSimulation) throws Exception{

	fdos.specEDI = null;
	
	FieldDataInfoPanel fdip = (FieldDataInfoPanel) new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			FieldDataInfoPanel fdip = new FieldDataInfoPanel();			
			fdip.setSimulationMode(isFromSimulation);
			fdip.initISize(fdos.isize);
			fdip.initIOrigin(fdos.origin);
			fdip.initIExtent(fdos.extent);
			fdip.initTimes(fdos.times);
			fdip.initNames(TokenMangler.fixTokenStrict(initialExtDataName), fdos.varNames);
			fdip.setAnnotation(fdos.annotation);
			return fdip;
		}
	}.dispatchWithException();

	while(true){
		try{
			//Adds to Server DB while getting Info
			FieldDataFileOperationSpec tempFDOS =
				getExternalDataInfoFromUser(fdip,clientRequestManager);
			fdos.specEDI = tempFDOS.specEDI;
			try{
				if(!isFromSimulation){
					fdos.extent = tempFDOS.extent;
					fdos.origin = tempFDOS.origin;
					fdos.varNames = tempFDOS.varNames;
					fdos.times = tempFDOS.times;
					//
					//Subvolumes and Regions NOT implemented now
					//
					fdos.cartesianMesh = CartesianMesh.createSimpleCartesianMesh(
							fdos.origin, fdos.extent, fdos.isize,
							new RegionImage(
									new VCImageUncompressed(
											null,
											new byte[fdos.isize.getXYZ()],//empty regions
											fdos.extent,
											fdos.isize.getX(),fdos.isize.getY(),fdos.isize.getZ()
											),0,null,null,RegionImage.NO_SMOOTHING));
				}
				
				//Add to Server Disk
				clientRequestManager.getDocumentManager().fieldDataFileOperation(fdos);
			}catch(Exception e){
				try{
					//try to cleanup new ExtDataID
					clientRequestManager.getDocumentManager().fieldDataDBOperation(
							FieldDataDBOperationSpec.createDeleteExtDataIDSpec(fdos.specEDI));
				}catch(Exception e2){
					//ignore
				}finally{
					fdos.specEDI = null;
					e.printStackTrace();
					PopupGenerator.showErrorDialog("Error saving Field Data to server disk\n"+e.getMessage());
				}
				continue;
			}
			//Everything succeeded, Add node to tree
			try{
				DefaultMutableTreeNode root = ((DefaultMutableTreeNode)getJTree1().getModel().getRoot());
				if(root.getChildCount() == 0){
					updateJTree(fieldDataWindowManager.getLocalRequestManager());
				}else{
					final int[] alphabeticalIndexArr = new int[] {-1};
					for(int i=0;i<root.getChildCount();i+= 1){
						if((((FieldDataMainList)((DefaultMutableTreeNode)root.getChildAt(i)).getUserObject())).
							externalDataIdentifier.getName().
								compareToIgnoreCase(fdos.specEDI.getName()) > 0){
							alphabeticalIndexArr[0] = i;
							break;
						}
					}
					if(alphabeticalIndexArr[0] == -1){
						alphabeticalIndexArr[0] = root.getChildCount();
					}
					final DefaultMutableTreeNode mainNode =
						new DefaultMutableTreeNode(new FieldDataMainList(fdos.specEDI,tempFDOS.annotation));
					mainNode.add(new DefaultMutableTreeNode(new FieldDataVarMainList()));
					SwingUtilities.invokeAndWait(
						new Runnable (){
							public void run(){
								((DefaultTreeModel)getJTree1().getModel()).insertNodeInto(
										mainNode, (MutableTreeNode)(getJTree1().getModel().getRoot()), alphabeticalIndexArr[0]);
							}
						}
					);
				}
			}catch(Exception e){
				e.printStackTrace();
				PopupGenerator.showInfoDialog(
						"saving Field Data "+fdos.specEDI.getName()+" succeeded -but-\n"+
						" the GUI refresh had an error\n"+e.getMessage());
			}
			
			break;
		}catch(UserCancelException uce){
			if(fdos.specEDI != null){
				//Try to undo the DB part of External Data
				try{
					clientRequestManager.getDocumentManager().fieldDataDBOperation(
							FieldDataDBOperationSpec.
								createDeleteExtDataIDSpec(fdos.specEDI));
				}catch(Exception e){
					//Nothing else we can do
					e.printStackTrace();
				}
				//Try to undo the File part of External Data
				try{
					clientRequestManager.getDocumentManager().
						fieldDataFileOperation(
								FieldDataFileOperationSpec.
									createDeleteFieldDataFileOperationSpec(fdos.specEDI)
								);
				}catch(Exception e){
					//Nothing else we can do
					e.printStackTrace();
				}
			}
			throw uce;
		}
	}
}

/**
 * This method initializes jButtonFindRefModel	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getJButtonFindRefModel() {
	if (jButtonFindRefModel == null) {
		jButtonFindRefModel = new JButton();
		jButtonFindRefModel.setText("Model Refs...");
		jButtonFindRefModel.setEnabled(false);
		jButtonFindRefModel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
				javax.swing.tree.DefaultMutableTreeNode mainNode =
					(javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
				if(mainNode.getUserObject() instanceof FieldDataMainList){
					final ExternalDataIdentifier extDataID =
						((FieldDataMainList)mainNode.getUserObject()).externalDataIdentifier;
					new Thread(new Runnable(){
						public void run(){
							try{
								BeanUtils.setCursorThroughout(FieldDataGUIPanel.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								fieldDataWindowManager.findReferencingModels(extDataID,true);
								}catch(UserCancelException e){
									//ignore
								}catch(DataAccessException e){
									PopupGenerator.showErrorDialog(
											"Error Finding Model references for "+extDataID.getName()+"\n"+e.getMessage());
								}
								finally{
									BeanUtils.setCursorThroughout(FieldDataGUIPanel.this, Cursor.getDefaultCursor());
								}
					}
				}).start();
				}
			}
		});
	}
	return jButtonFindRefModel;
}

/**
 * This method initializes jPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getJPanel() {
	if (jPanel == null) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints2.gridy = 0;
		jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());
		jPanel.add(getJButtonCreateGeom(), gridBagConstraints2);
		jPanel.add(getJButtonFindRefModel(), gridBagConstraints);
	}
	return jPanel;
}

/**
 * This method initializes jButtonCopyInfo	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getJButtonCreateGeom() {
	if (jButtonCreateGeom == null) {
		jButtonCreateGeom = new JButton();
		jButtonCreateGeom.setEnabled(false);
		jButtonCreateGeom.setText("Create Geom");
		jButtonCreateGeom.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jButtonFDCopyRef_ActionPerformed(e);
				//fieldDataWindowManager.newDocument(VCDocument.GEOMETRY_DOC, option);
//				copyMethod(COPY_CRNL);
////				javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
////				if(selPath != null){
////					javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
////					copyMethod(lastPathComponent, copyMode);
////				}
////					String copyString = "";
////					javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
////					if(lastPathComponent.equals(getJTree1().getModel().getRoot())){
////						int childCount = lastPathComponent.getChildCount();
////						for(int i=0;i<childCount;i+= 1){
////							if(i != 0){
////								copyString+="\n";
////							}
////							copyString+=
////								((FieldDataMainList)((DefaultMutableTreeNode)lastPathComponent.getChildAt(i)).getUserObject()).externalDataIdentifier.getName();
////						}					
////					}else if(lastPathComponent.getUserObject() instanceof FieldDataOriginList){
////						Origin origin = ((FieldDataOriginList)lastPathComponent.getUserObject()).origin;
////						copyString = origin.getX()+","+origin.getY()+","+origin.getZ();
////					}else if(lastPathComponent.getUserObject() instanceof FieldDataExtentList){
////						Extent extent = ((FieldDataExtentList)lastPathComponent.getUserObject()).extent;
////						copyString = extent.getX()+","+extent.getY()+","+extent.getZ();
////					}else if(lastPathComponent.getUserObject() instanceof FieldDataISizeList){
////						ISize isize = ((FieldDataISizeList)lastPathComponent.getUserObject()).isize;
////						copyString = isize.getX()+","+isize.getY()+","+isize.getZ();
////					}else if(lastPathComponent.getUserObject() instanceof FieldDataTimeList){
////						double[] times = ((FieldDataTimeList)lastPathComponent.getUserObject()).times;
////						for(int i=0;i<times.length;i+= 1){
////							if(i != 0){
////								copyString+="\n";
////							}
////							copyString+= times[i]+"";
////						}
////					}else if(lastPathComponent.getUserObject() instanceof FieldDataMainList){
////						ExternalDataIdentifier extDataID =
////							((FieldDataMainList)lastPathComponent.getUserObject()).externalDataIdentifier;
////						copyString = extDataID.getName();
////					}else if(lastPathComponent.getUserObject() instanceof FieldDataVarList){
////						DataIdentifier dataIdentifier =
////							((FieldDataVarList)lastPathComponent.getUserObject()).dataIdentifier;
////						copyString = dataIdentifier.getName();
////					}else if(lastPathComponent.getUserObject() instanceof FieldDataVarMainList){
////						int childCount = lastPathComponent.getChildCount();
////						for(int i=0;i<childCount;i+= 1){
////							if(i != 0){
////								copyString+="\n";
////							}
////							copyString+=
////								((FieldDataVarList)((DefaultMutableTreeNode)lastPathComponent.getChildAt(i)).getUserObject()).dataIdentifier.getName();
////						}
////					}
////					if(copyString.length() > 0 ){
////						VCellTransferable.sendToClipboard(copyString);
////					}
////				}
			}
		});
	}
	return jButtonCreateGeom;
}

/**
 * This method initializes jPanel1	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getJPanel12() {
	if (jPanel1 == null) {
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.gridy = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.NONE;
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
		jPanel1 = new JPanel();
		jPanel1.setLayout(new GridBagLayout());
		jPanel1.add(getJButtonFDCopyRef(), gridBagConstraints1);
		jPanel1.add(getJButtonViewAnnot(), gridBagConstraints3);
	}
	return jPanel1;
}

/**
 * This method initializes jButtonViewAnnot	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getJButtonViewAnnot() {
	if (jButtonViewAnnot == null) {
		jButtonViewAnnot = new JButton();
		jButtonViewAnnot.setText("View Annot...");
		jButtonViewAnnot.setEnabled(false);
		jButtonViewAnnot.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				javax.swing.tree.TreePath selPath = getJTree1().getSelectionPath();
				if(selPath != null){
					javax.swing.tree.DefaultMutableTreeNode lastPathComponent =
						(javax.swing.tree.DefaultMutableTreeNode)selPath.getLastPathComponent();
					if(lastPathComponent.getUserObject() instanceof FieldDataMainList){
						PopupGenerator.showInfoDialog(
								((FieldDataMainList)(lastPathComponent.getUserObject())).extDataAnnot);
					}
				}
			}
		});
	}
	return jButtonViewAnnot;
}


private void getPslidSelections(final int mode) {
	final PSLIDPanel pslidPanel = new PSLIDPanel();
	pslidPanel.setPreferredSize(new Dimension(550,600));

	final Thread[] pslidThread = new Thread[1];
	final AsynchProgressPopup[] pp = new AsynchProgressPopup[1];
	pp[0] =
		new AsynchProgressPopup(
//				FieldDataGUIPanel.this,"Accessing PSLID Information","",true,false,
				FieldDataGUIPanel.this,"Accessing PSLID Information","",true,true,
			true,
			new ProgressDialogListener(){
				public void cancelButton_actionPerformed(EventObject newEvent) {
					pp[0].stop();
					pslidThread[0].interrupt();
					stopThread();
				}
			}
		);
	SwingUtilities.invokeLater(new Runnable(){public void run() {pp[0].startKeepOnTop();}});
	pslidThread[0] = new Thread(
		new Runnable(){
			public void run() {
//								FileOutputStream fos = null;
				try {
					threadState = Thread.currentThread();
					pslidPanel.initCellProteinList( fieldDataWindowManager.getUserPreferences(),pp[0],mode);
					if(threadState != Thread.currentThread()) {
						return;
					}
					pp[0].stop();
					if(PopupGenerator.showComponentOKCancelDialog(FieldDataGUIPanel.this, pslidPanel,"PSLID Browser") == JOptionPane.OK_OPTION){
						PSLIDPanel.PSLIDSelectionInfo pslidSelInfo =
							pslidPanel.getPSLIDSelectionInfo();
						String initFDName = pslidSelInfo.cellName+"_"+pslidSelInfo.proteinName+"_"+pslidSelInfo.imageSetID;
						System.out.println(initFDName);
						System.out.println(pslidSelInfo.proteinImageURL);
						System.out.println(pslidSelInfo.compartmentImageURL);
						jButtonFDFromFile_ActionPerformed(null, pslidSelInfo.fdos, initFDName);
						
//										File proteinImageFile = File.createTempFile("pslidProt",null);
//										proteinImageFile.deleteOnExit();
//										fos = new FileOutputStream(proteinImageFile);
//										fos.write(pslidSelInfo.proteinImage);
//										fos.close();
//										
//										File compartmentImageFile = File.createTempFile("pslidComp",null);
//										compartmentImageFile.deleteOnExit();
//										fos = new FileOutputStream(compartmentImageFile);
//										fos.write(pslidSelInfo.compartmentImage);
//										fos.close();
//
//										FieldDataFileOperationSpec fdos_protein = createFDOSFromImageFile(proteinImageFile);
//										FieldDataFileOperationSpec fdos_compartment = createFDOSFromImageFile(compartmentImageFile);
//										FieldDataFileOperationSpec fdos_composite = new FieldDataFileOperationSpec();
//										fdos_composite.variableTypes =
//											new VariableType[] {fdos_protein.variableTypes[0],fdos_compartment.variableTypes[0]};
//										fdos_composite.varNames =
//											new String[] {fdos_protein.varNames[0],fdos_compartment.varNames[0]};
//										fdos_composite.shortSpecData =
//											new short[][][] {{fdos_protein.shortSpecData[0][0],fdos_compartment.shortSpecData[0][0]}};
//										fdos_composite.times = new double[] {0};
//										fdos_composite.origin = pslidSelInfo.origin;
//										fdos_composite.extent = pslidSelInfo.extent;
//										fdos_composite.isize = pslidSelInfo.isize;
						
						//jButtonFDFromFile_ActionPerformed(null, fdos_composite,initFDName);
					}
				} catch (Exception e) {
					pp[0].stop();
					e.printStackTrace();
					if(!(e instanceof InterruptedException)){
						PopupGenerator.showErrorDialog("Error displaying PSLID Information.\n"+e.getMessage());
					}
				} finally {
//									if(fos != null){try{fos.close();}catch(Exception e){}}
					pp[0].stop();
				}
			}
		}
	);
	pslidThread[0].start();

//					//Create Timer thread to kill PSLID thread if taking too long (hang)
//					final long MAX_WAIT_TIME_MILLISEC = 45000;
//					final long MAX_WARNING_TIME_MILLISEC = 15000;
//					final javax.swing.Timer pslidBlockTimer = new javax.swing.Timer(1000,null);
//					pslidBlockTimer.addActionListener(
//						new java.awt.event.ActionListener(){
//							long startTime = System.currentTimeMillis();
//							public void actionPerformed(java.awt.event.ActionEvent e){
//								if(!pslidThread.isAlive()){
//									pslidBlockTimer.stop();
//									return;
//								}
//								long elapsedTime = System.currentTimeMillis()-startTime;
//								if(elapsedTime < MAX_WAIT_TIME_MILLISEC){
//									if(elapsedTime > MAX_WARNING_TIME_MILLISEC){
//										pp.setMessage("Waiting for PSLID information... "+((MAX_WAIT_TIME_MILLISEC-elapsedTime)/1000));
//									}
//									pslidBlockTimer.restart();
//								}else{
//									pslidBlockTimer.stop();
//									pp.stop();
//									pslidThread.interrupt();
//								}
//							}
//						}
//					);
//					pslidBlockTimer.restart();
}

}  //  @jve:decl-index=0:visual-constraint="10,10"
