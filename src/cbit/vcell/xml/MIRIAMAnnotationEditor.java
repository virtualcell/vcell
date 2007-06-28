package cbit.vcell.xml;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.xml.MIRIAMHelper.DescriptiveHeirarchy;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JLabel;

public class MIRIAMAnnotationEditor extends JPanel implements ActionListener{

	private JScrollPane jScrollPane = null;
	private JTable jTableMIRIAM = null;
	private JButton jButtonOK = null;
	private JPanel jPanel = null;
	private JButton jButtonAdd = null;
	private JButton jButtonDelete = null;
	
	private Vector<ActionListener> actionListenerV = new Vector<ActionListener>();
	public static final String ACTION_OK ="OK";  //  @jve:decl-index=0:
	public static final String ACTION_ADD ="Add...";
	public static final String ACTION_EDIT ="Edit...";
	public static final String ACTION_DELETE ="Delete Annotation";
	
	Vector<MIRIAMHelper.MIRIAMTableRow> rowData;  //  @jve:decl-index=0:
	private JButton jButtonEditAnnotation = null;
	Vector<Integer> rowMapV;
	private JButton jButtonCopy = null;


	/**
	 * This method initializes 
	 * 
	 */
	public MIRIAMAnnotationEditor() {
		super();
		initialize();
		jButtonAdd.setVisible(false);
		jButtonEditAnnotation.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		if(jTableMIRIAM.getCellEditor() != null){
			jTableMIRIAM.getCellEditor().stopCellEditing();
		}
		for (int i = 0; i < actionListenerV.size(); i++) {
			actionListenerV.get(i).actionPerformed(e);
		}
	}

	public void addActionListener(ActionListener actionListiner){
		if(!actionListenerV.contains(actionListiner)){
			actionListenerV.add(actionListiner);
		}
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.fill = GridBagConstraints.NONE;
        gridBagConstraints5.insets = new Insets(4, 4, 4, 4);
        gridBagConstraints5.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.BOTH;
        gridBagConstraints1.weighty = 1.0;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(627, 333));
        this.add(getJScrollPane(), gridBagConstraints1);
        this.add(getJPanel(), gridBagConstraints5);
			
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTableMIRIAM());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTableMIRIAM	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableMIRIAM() {
		if (jTableMIRIAM == null) {
			jTableMIRIAM = new JTable();
			jTableMIRIAM.getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						if(e.getValueIsAdjusting()){
							return;
						}
						jButtonAdd.setEnabled(false);
						jButtonDelete.setEnabled(false);
						jButtonEditAnnotation.setEnabled(false);
						if(getSelectedDescriptionHeirarchy() == null){
						//if(jTableMIRIAM.getSelectedRow() != -1 && rowData != null && rowData.get(rowMapV.get(jTableMIRIAM.getSelectedRow())).descriptiveHeirarchy == null){
							jButtonAdd.setEnabled(true);
						}
						if(getSelectedDescriptionHeirarchy() != null){
						//if(jTableMIRIAM.getSelectedRow() != -1 && rowData != null && rowData.get(rowMapV.get(jTableMIRIAM.getSelectedRow())).descriptiveHeirarchy != null){
							jButtonDelete.setEnabled(true);
							jButtonEditAnnotation.setEnabled(true);
						}
					}
				}
			);
			jTableMIRIAM.getTableHeader().setReorderingAllowed(false);
		}
		return jTableMIRIAM;
	}

	/**
	 * This method initializes jButtonOK	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText(ACTION_OK);
			jButtonOK.addActionListener(this);
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 4;
			gridBagConstraints21.gridy = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 3;
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButtonOK(), gridBagConstraints);
			jPanel.add(getJButtonAdd(), gridBagConstraints2);
			jPanel.add(getJButtonDelete(), gridBagConstraints4);
			jPanel.add(getJButtonEditAnnotation(), gridBagConstraints11);
			jPanel.add(getJButtonCopy(), gridBagConstraints21);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButtonAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText("Add Annotation...");
			jButtonAdd.addActionListener(this);
		}
		return jButtonAdd;
	}

	/**
	 * This method initializes jButtonDelete	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDelete() {
		if (jButtonDelete == null) {
			jButtonDelete = new JButton();
			jButtonDelete.setText(ACTION_DELETE);
			jButtonDelete.addActionListener(this);
		}
		return jButtonDelete;
	}

	public MIRIAMAnnotatable getSelectedMIRIAMAnnotatable(){
		int selectedrow = jTableMIRIAM.getSelectedRow();
		if(selectedrow != -1&& rowData != null && rowMapV != null){
			return rowData.get(rowMapV.get(selectedrow)).miriamAnnotatable;
			//return rowData.get(selectedrow).miriamAnnotatable;
		}
		return null;
	}
	
	public MIRIAMHelper.DescriptiveHeirarchy getSelectedDescriptionHeirarchy(){
		int selectedrow = jTableMIRIAM.getSelectedRow();
		if(selectedrow != -1 && rowData != null && rowMapV != null){
			return rowData.get(rowMapV.get(selectedrow)).descriptiveHeirarchy;
			//return rowData.get(selectedrow).descriptiveHeirarchy;
		}
		return null;
	}
	
	public void setMIRIAMAnnotation(TreeMap<MIRIAMAnnotatable, Vector<MIRIAMHelper.DescriptiveHeirarchy>> mirimaDescrHeir){
		DefaultTableModel tableModel = new DefaultTableModel(){
		    public boolean isCellEditable(int row, int column) {
		    	return false;
//		        return
//		        	(column == (MIRIAMHelper.MIRIAM_ANNOT_COLUMNS.length-1))
//		        	&&
//		        	(rowData.get(row).descriptiveHeirarchy != null);
		    }
		};

		rowData = MIRIAMHelper.getTableFormattedData(mirimaDescrHeir);
//		String[][] rowArr= new String[rowData.size()][];
//		for (int i = 0; i < rowArr.length; i++) {
//			rowArr[i] = rowData.get(i).rowData;
//		}
		Vector<String[]> rowArrV = new Vector<String[]>();
		rowMapV = new Vector<Integer>();
		HashMap<Integer, Vector<DescriptiveHeirarchy>> creatorsH =
			new HashMap<Integer, Vector<DescriptiveHeirarchy>>();
//		Vector<DescriptiveHeirarchy> latestCreator = null;
		for (int i = 0; i < rowData.size(); i++) {
			if(rowData.get(i).descriptiveHeirarchy != null && rowData.get(i).descriptiveHeirarchy.isCreatorChild()){
				Vector<DescriptiveHeirarchy> latestCreator = new Vector<DescriptiveHeirarchy>();
				creatorsH.put(rowArrV.size(),latestCreator);
				rowArrV.add(new String[] {null,null,null,"Creator "+(creatorsH.size()),rowData.get(i).rowData[4]});
				rowMapV.add(i);
				latestCreator.add(rowData.get(i).descriptiveHeirarchy);
				int index = i+1;
				while(rowData.get(index).descriptiveHeirarchy.isCreatorChild()){
					if(rowData.get(index).descriptiveHeirarchy.isSameCreator(latestCreator.firstElement())){
						rowArrV.lastElement()[4]+= ","+rowData.get(index).rowData[4];
						latestCreator.add(rowData.get(index).descriptiveHeirarchy);
						index+= 1;
						i+= 1;
					}else{
//						i=index-1;
						break;
					}
				}
//				if(!bIsCreator){//first time
//					latestCreator = new Vector<DescriptiveHeirarchy>();
//					creatorsH.put(rowArrV.size(),latestCreator);
//					rowArrV.add(new String[] {null,null,null,"Creator "+(creatorsH.size()),rowData.get(i).rowData[4]});
//				}else{
//					rowArrV.lastElement()[4]+= ","+rowData.get(i).rowData[4];
//				}
//				latestCreator.add(rowData.get(i).descriptiveHeirarchy);
//				bIsCreator = true;
			}
			else{
//				bIsCreator = false;
//				latestCreator = null;
				rowArrV.add(rowData.get(i).rowData);
				rowMapV.add(i);
			}
		}
		tableModel.setDataVector(rowArrV.toArray(new String[0][]), MIRIAMHelper.MIRIAM_ANNOT_COLUMNS);
		jTableMIRIAM.setModel(tableModel);
		jTableMIRIAM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tableModel.addTableModelListener(
			new TableModelListener(){
				public void tableChanged(TableModelEvent e) {
					DescriptiveHeirarchy descrHeir = rowData.get(e.getFirstRow()).descriptiveHeirarchy;
					MIRIAMHelper.editDescriptiveHeirarchy(
							descrHeir, (String)jTableMIRIAM.getValueAt(e.getFirstRow(), e.getColumn()));
				}	
			}
		);		
		jTableMIRIAM.getSelectionModel().setSelectionInterval(0, 0);
	}

	/**
	 * This method initializes jButtonEditAnnotation	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonEditAnnotation() {
		if (jButtonEditAnnotation == null) {
			jButtonEditAnnotation = new JButton();
			jButtonEditAnnotation.setText("Edit Annotation...");
			jButtonEditAnnotation.addActionListener(this);
		}
		return jButtonEditAnnotation;
	}

	/**
	 * This method initializes jButtonCopy	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCopy() {
		if (jButtonCopy == null) {
			jButtonCopy = new JButton();
			jButtonCopy.setText("Copy All");
			jButtonCopy.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					StringBuffer sb = new StringBuffer();
					Vector dataVector = ((DefaultTableModel)jTableMIRIAM.getModel()).getDataVector();
					for (Iterator iter = dataVector.iterator(); iter.hasNext();) {
						Vector<String> element = (Vector<String>) iter.next();
						for (int i = 0; i < element.size(); i++) {
							sb.append((i>0?" ":"")+element.get(i));
						}
						sb.append("\n");
					}
					VCellTransferable.sendToClipboard(sb.toString());
				}
			});
		}
		return jButtonCopy;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
