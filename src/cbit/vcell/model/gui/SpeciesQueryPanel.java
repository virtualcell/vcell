/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.ButtonModel;
import javax.swing.JList;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.TitledBorderBean;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.DictionaryQueryResults;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesInfo;
import cbit.vcell.model.FormalSpeciesType;
/**
 * Insert the type's description here.
 * Creation date: (2/4/2003 6:52:51 PM)
 * @author: Frank Morgan
 */
public class SpeciesQueryPanel extends javax.swing.JPanel {

	//
	class SearchTask extends AsynchClientTask {
		private String likeString;
		private boolean isBound;
		private int restrictSearch;
		private FormalSpeciesType fsType;
		private boolean bOnlyUser;
		
		public SearchTask(String argLikeString,boolean argIsBound,int argRestrictSearch,FormalSpeciesType argFsType, boolean argOnlyUser){
			super("searching for "+argFsType.getName(), TASKTYPE_NONSWING_BLOCKING);
			this.likeString = argLikeString;
			this.isBound = argIsBound;
			this.restrictSearch = argRestrictSearch;
			this.fsType = argFsType;
			this.bOnlyUser = argOnlyUser;
		}
		public void run(Hashtable<String, Object> hash) throws Exception{
			
				int rowLimit = 200;
				
				DBFormalSpecies[] databaseSpecies =
					getDocumentManager().getDatabaseSpecies(likeString,isBound,fsType,restrictSearch,rowLimit,bOnlyUser);
					
				Boolean isTruncated = null;
				if(databaseSpecies != null){
					isTruncated = new Boolean(false);
					int counter = 0;
				    for(int i = 0; i < databaseSpecies.length;i+= 1){
					    counter+= databaseSpecies[i].getFormalSpeciesInfo().getNames().length;
				    }
				    if(counter == rowLimit){
					    isTruncated = new Boolean(true);
					    if(databaseSpecies.length > 1){
						    //Throw out last because it might not have all aliasnames beacause of truncation
						    //DBFormalSpecies[] shortDatabaseSpecies = new DBFormalSpecies[databaseSpecies.length-1];
						    DBFormalSpecies[] shortDatabaseSpecies =
						    	(DBFormalSpecies[])java.lang.reflect.Array.newInstance(
							    	databaseSpecies[0].getClass(),databaseSpecies.length-1);;
						    System.arraycopy(databaseSpecies,0,shortDatabaseSpecies,0,shortDatabaseSpecies.length);
						    databaseSpecies = shortDatabaseSpecies;
					    }
				    }
				}
				if(databaseSpecies != null){
					hash.put("sqpSearch",databaseSpecies);
					hash.put("isTruncated",isTruncated);
					hash.put("likeString",likeString);
				}
				//Condition 1 - No results.  Nothing put in hash
				//Condition 2 - Results with no rowLimit reached. sqpSearch and isTruncated(false) put in hash
				//Condition 3 - Results with rowLimit reached.  sqpSearch and isTruncated(true) put in hash
		}
	}

	//
	
	//
	class PostSearchUpdateTask extends AsynchClientTask {
		javax.swing.JList selectedJList = null;
		public PostSearchUpdateTask(javax.swing.JList argSelectedJList){
			super("Updating Interface", TASKTYPE_SWING_BLOCKING);
			this.selectedJList = argSelectedJList;
		}
		public void run(Hashtable<String, Object> hash) throws Exception{
				DBFormalSpecies[] dbFormalSpecies = null;
				if(hash.containsKey("sqpSearch")){
					dbFormalSpecies = (DBFormalSpecies[])hash.get("sqpSearch");
				}
				Boolean isTruncated = null;
				if(hash.containsKey("isTruncated")){
					isTruncated = (Boolean)hash.get("isTruncated");
				}
				String likeString = null;
				if(hash.containsKey("likeString")){
					likeString = (String)hash.get("likeString");
				}
				//Create InfoMessage
				String infoMessage = null;
				if(dbFormalSpecies != null){
					infoMessage = dbFormalSpecies.length+" items found"+(isTruncated.booleanValue()?" (search limit exceeded - results truncated)":"");
				}else{
					infoMessage = "No matches found";
				}
				getSearchResultInfoJLabel().setText(infoMessage);
				//
				if(dbFormalSpecies == null){
					searchStateRemove(selectedJList);
				}else{
					searchStateAdd(selectedJList,dbFormalSpecies,getFilterJTextField().getText(),getButtonGroup1().getSelection(),infoMessage);
				}

				dbfsComparator.setLikeString(likeString);
				if(dbFormalSpecies != null){
					java.util.Arrays.sort(dbFormalSpecies,dbfsComparator);
				}
				
				selectedJList.setListData((dbFormalSpecies != null?dbFormalSpecies:new DBFormalSpecies[0]));
				
				getSearchJButton().setEnabled(hasFilter());
		}
	}

	//
	
	//
	class DBFormalSpeciesComparator implements Comparator<DBFormalSpecies> {
		private String likeString = null;
		
		public int compare(DBFormalSpecies dbfs1, DBFormalSpecies dbfs2){
			
			String pfn1 = dbfs1.getFormalSpeciesInfo().getPreferredName();
			String pfn2 = dbfs2.getFormalSpeciesInfo().getPreferredName();

			if(likeString != null && pfn1.toLowerCase().indexOf(likeString) != -1 && pfn2.toLowerCase().indexOf(likeString) == -1){
				return -1;
			}
			if(likeString != null && pfn1.toLowerCase().indexOf(likeString) == -1 && pfn2.toLowerCase().indexOf(likeString) != -1){
				return 1;
			}
			return pfn1.compareToIgnoreCase(pfn2);
		}
		public void setLikeString(String argLikeString){
			if(argLikeString == null){
				likeString = null;
				return;
			}
			StringBuffer sb = new StringBuffer();
			boolean bSpecialFlag = false;
			for(int i=0;i<argLikeString.length();i+=1){
				if(	argLikeString.charAt(i) != '*' &&
					argLikeString.charAt(i) != '%' &&
					argLikeString.charAt(i) != '_' &&
					argLikeString.charAt(i) != '?'){
						if(bSpecialFlag){
							likeString = null;
							return;
						}
						sb.append(argLikeString.charAt(i));
				}else if(sb.length() != 0){
					bSpecialFlag = true;
				}
			}
			likeString = (sb.length() != 0?sb.toString().toLowerCase():null);
			System.out.println(likeString);
		}
	}

	//
	//
	
	class SpeciesListCellRenderer extends javax.swing.JLabel implements javax.swing.ListCellRenderer,java.io.Serializable {
				
		public SpeciesListCellRenderer() {
			super();
		   	noFocusBorder = new javax.swing.border.EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
		}
	
		public java.awt.Component getListCellRendererComponent(
			javax.swing.JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus)
		{
			
			setComponentOrientation(list.getComponentOrientation());
			
		if (isSelected) {
		    setBackground(list.getSelectionBackground());
		    setForeground(list.getSelectionForeground());
		}
		else {
		    setBackground(list.getBackground());
		    setForeground(list.getForeground());
		}

		if (value instanceof javax.swing.Icon) {
		    setIcon((javax.swing.Icon)value);
		}
		else {
			FormalSpeciesInfo fsi = ((DBFormalSpecies)value).getFormalSpeciesInfo();
			String special = "";
			if(fsi instanceof ProteinInfo){
				ProteinInfo info = (ProteinInfo)fsi;
				special = special + (info.getMolecularWeight() != ProteinInfo.UNKNOWN_MW?" MW="+info.getMolecularWeight()+" ":"");
				special = special + (info.getAccession() != null?" accession["+info.getAccession()+"] ":"");
				special = special + (info.getOrganism() != null?" "+info.getOrganism()+" ":"");
			}else if(fsi instanceof CompoundInfo){
				CompoundInfo info = (CompoundInfo)fsi;
				special = special + (info.getMolecularWeight() != -1?" MW="+((double)((int)(info.getMolecularWeight()*100)))/100+" ":"");
				special = special + (info.getCasID() != null?" casID["+info.getCasID()+"] ":"");
				special = special + (info.getFormula() != null?" "+info.getFormula()+" ":"");
			}else if(fsi instanceof EnzymeInfo){
				EnzymeInfo info = (EnzymeInfo)fsi;
				special = special + (info.getSysname() != null?" sysName["+info.getSysname()+"] ":"");
				special = special + (info.getReaction() != null?" << "+info.getReaction()+" >> ":"");
			}
			
			if(fsi != null){
				String displayString = "";
				for(int i = 0;i < fsi.getNames().length;i+= 1){
					displayString = displayString + (i != 0?" , ":"") + fsi.getNames()[i];
				}
				setText(" ["+fsi.getFormalID()+"]   "+displayString+(special.length() > 0?"   ("+special+") ":""));
			}else{
				setText("Error");
			}
		}

		setEnabled(list.isEnabled());
		setFont(list.getFont());
		//setBorder((cellHasFocus) ? javax.swing.UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

		return this;
		}
	}
	protected static javax.swing.border.Border noFocusBorder;
	//
	//
	public static final String TAB_COMPOUND_TITLE = "Compound";
	public static final String TAB_ENZYME_TITLE = "Enzyme";
	public static final String TAB_PROTEIN_TITLE = "Protein";
	//
	public static final long SEARCHABLE_COMPOUND = 1;
	public static final long SEARCHABLE_ENZYME = 2;
	public static final long SEARCHABLE_PROTEIN = 4;
	public static final long SEARCHABLE_ALL = SEARCHABLE_COMPOUND | SEARCHABLE_ENZYME | SEARCHABLE_PROTEIN;
	//
	//
	
	private DBFormalSpeciesComparator dbfsComparator = new DBFormalSpeciesComparator();
	private Hashtable<JList, DBFormalSpecies[]> jlistData = new Hashtable<JList, DBFormalSpecies[]>();
	private Hashtable<JList, String> jlistQuery = new Hashtable<JList, String>();
	private Hashtable<JList, ButtonModel> jlistScope = new Hashtable<JList, ButtonModel>();
	private Hashtable<JList, String> jlistInfoMessage = new Hashtable<JList, String>();
	private boolean bIgnoreSelection = false;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroup1 = null;
	private javax.swing.JTabbedPane ivjSNBJTabbedPane = null;
	private javax.swing.JButton ivjSearchJButton = null;
	private javax.swing.JTextField ivjFilterJTextField = null;
	private javax.swing.JList ivjCompoundJList = null;
	private javax.swing.JScrollPane ivjCompoundJScrollPane = null;
	private javax.swing.JList ivjEnzymeJList = null;
	private javax.swing.JScrollPane ivjEnzymeJScrollPane = null;
	private javax.swing.JList ivjProteinJList = null;
	private javax.swing.JScrollPane ivjProteinJScrollPane = null;
	private javax.swing.JRadioButton ivjAllModelsJRadioButton = null;
	private javax.swing.JRadioButton ivjDictionaryJRadioButton = null;
	private javax.swing.JRadioButton ivjMyModelJRadioButton = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.text.Document ivjdocument1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel2 = null;
	private javax.swing.ListSelectionModel ivjselectionModel3 = null;
	private cbit.vcell.dictionary.DictionaryQueryResults fieldDictionaryQueryResults = null;
	private javax.swing.JLabel ivjSearchResultInfoJLabel = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private long fieldSearchableTypes = SEARCHABLE_ALL;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.DocumentListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SpeciesQueryPanel.this.getSearchJButton()) 
				connEtoC4(e);
			if (e.getSource() == SpeciesQueryPanel.this.getFilterJTextField()) 
				connEtoM7(e);
		};
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == SpeciesQueryPanel.this.getdocument1()) 
				connEtoM4(e);
		};
		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == SpeciesQueryPanel.this.getdocument1()) 
				connEtoM4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SpeciesQueryPanel.this.getFilterJTextField() && (evt.getPropertyName().equals("document"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == SpeciesQueryPanel.this.getProteinJList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == SpeciesQueryPanel.this.getEnzymeJList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == SpeciesQueryPanel.this.getCompoundJList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == SpeciesQueryPanel.this.getButtonGroup1() && (evt.getPropertyName().equals("selection"))) 
				connEtoM8(evt);
			if (evt.getSource() == SpeciesQueryPanel.this && (evt.getPropertyName().equals("searchableTypes"))) 
				connEtoC5(evt);
		};
		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == SpeciesQueryPanel.this.getdocument1()) 
				connEtoM4(e);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == SpeciesQueryPanel.this.getSNBJTabbedPane()) 
				connEtoC6(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == SpeciesQueryPanel.this.getselectionModel1()) 
				connEtoC1(e);
			if (e.getSource() == SpeciesQueryPanel.this.getselectionModel2()) 
				connEtoC2(e);
			if (e.getSource() == SpeciesQueryPanel.this.getselectionModel3()) 
				connEtoC3(e);
		};
	};

/**
 * SpeciesQueryPanel constructor comment.
 */
public SpeciesQueryPanel() {
	super();
	initialize();
}

/**
 * SpeciesQueryPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SpeciesQueryPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * SpeciesQueryPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SpeciesQueryPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * SpeciesQueryPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SpeciesQueryPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Insert the method's description here.
 * Creation date: (4/5/2003 12:03:17 AM)
 */
private void clearAllSelections() {
	
	getCompoundJList().clearSelection();
	getEnzymeJList().clearSelection();
	getProteinJList().clearSelection();
}


/**
 * connEtoC1:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SpeciesQueryPanel.createDBSpecies(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.createDBSpecies(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (selectionModel2.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SpeciesQueryPanel.createDBSpecies(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.createDBSpecies(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (selectionModel3.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SpeciesQueryPanel.createDBSpecies(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.createDBSpecies(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (SearchJButton.action.actionPerformed(java.awt.event.ActionEvent) --> SpeciesQueryPanel.search()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.search();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (SpeciesQueryPanel.searchableTypes --> SpeciesQueryPanel.searchableTypesMode()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.searchableTypesMode();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (SNBJTabbedPane.change.stateChanged(javax.swing.event.ChangeEvent) --> SpeciesQueryPanel.searchContext()V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.searchContext();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (SpeciesQueryPanel.initialize() --> SpeciesQueryPanel.speciesQueryPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.speciesQueryPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  ( (SpeciesQueryPanel,initialize() --> SNBJTabbedPane,selectedIndex).normalResult --> SpeciesQueryPanel.searchContext()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9() {
	try {
		// user code begin {1}
		// user code end
		this.searchContext();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (SpeciesQueryPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getAllModelsJRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (SpeciesQueryPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getDictionaryJRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (SpeciesQueryPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getMyModelJRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM4:  (document1.document. --> SearchJButton.enabled)
 * @param evt javax.swing.event.DocumentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(javax.swing.event.DocumentEvent evt) {
	try {
		// user code begin {1}
		// user code end
		getSearchJButton().setEnabled(this.hasFilter());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (SpeciesQueryPanel.initialize() --> SNBJTabbedPane.selectedIndex)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getSNBJTabbedPane().setSelectedIndex(0);
		connEtoC9();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM7:  (FilterJTextField.action.actionPerformed(java.awt.event.ActionEvent) --> SearchJButton.doClick(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSearchJButton().doClick(68);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (ButtonGroup1.selection --> SearchJButton.enabled)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSearchJButton().setEnabled(this.hasFilter());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (FilterJTextField.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getdocument1() != null)) {
				getFilterJTextField().setDocument(getdocument1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (FilterJTextField.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setdocument1(getFilterJTextField().getDocument());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (ProteinJList.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getselectionModel1() != null)) {
				getProteinJList().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (ProteinJList.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setselectionModel1(getProteinJList().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (EnzymeJList.selectionModel <--> selectionModel2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getselectionModel2() != null)) {
				getEnzymeJList().setSelectionModel(getselectionModel2());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (EnzymeJList.selectionModel <--> selectionModel2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setselectionModel2(getEnzymeJList().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetSource:  (CompoundJList.selectionModel <--> selectionModel3.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getselectionModel3() != null)) {
				getCompoundJList().setSelectionModel(getselectionModel3());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (CompoundJList.selectionModel <--> selectionModel3.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setselectionModel3(getCompoundJList().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void createDBSpecies(javax.swing.event.ListSelectionEvent listSelectionEvent) {

	if(bIgnoreSelection){
		return;
	}
	if(getDocumentManager() != null){		
		if(!listSelectionEvent.getValueIsAdjusting()){
			 if(!((javax.swing.ListSelectionModel)listSelectionEvent.getSource()).isSelectionEmpty()){
				 try{
					 javax.swing.JList selectionJList = null;
					 
					 if(getCompoundJList().getSelectionModel() == listSelectionEvent.getSource()){
						 selectionJList = getCompoundJList();
					 }else if(getEnzymeJList().getSelectionModel() == listSelectionEvent.getSource()){
						 selectionJList = getEnzymeJList();
					 }else if(getProteinJList().getSelectionModel() == listSelectionEvent.getSource()){
						 selectionJList = getProteinJList();
					 }else{
						 throw new RuntimeException("Unknown ListSelectionModel");
					 }

					 int[] selection = new int[1];
					 selection[0] = selectionJList.getSelectedIndex();
					 DBFormalSpecies[] dbFormalSpecies = jlistData.get(selectionJList);
					String query = jlistQuery.get(selectionJList);
					ButtonModel scope = jlistScope.get(selectionJList);
					boolean bBound = (scope != getDictionaryJRadioButton().getModel());
					boolean bOnlyUser = (scope == getMyModelJRadioButton().getModel());
					
					DictionaryQueryResults dqr = null;
					if(bBound){
						if(getDocumentManager().getUser() == null){
							throw new RuntimeException("Query results for 'My Model' can't be bound to null user");
						}
						org.vcell.util.document.User user = (bOnlyUser?getDocumentManager().getUser():null);
						dqr = new DictionaryQueryResults(query,user,(DBSpecies[])dbFormalSpecies,selection);
						
					}else{
						dqr = new DictionaryQueryResults(query,dbFormalSpecies,selection);
					}
					setDictionaryQueryResults(dqr);
					//Clear other selections
					bIgnoreSelection = true;
					if(getCompoundJList() != selectionJList){
						getCompoundJList().clearSelection();
					}
					if(getEnzymeJList() != selectionJList){
						getEnzymeJList().clearSelection();
					}
					if(getProteinJList() != selectionJList){
						getProteinJList().clearSelection();
					}
					bIgnoreSelection = false;
					
					
				 }catch(Exception e){
					e.printStackTrace();
					PopupGenerator.showErrorDialog(this,"Error:\n"+e.getMessage(), e);
				 }
			 }else{
			 	setDictionaryQueryResults(null);
			 }
		}

	}

}


/**
 * Return the AllModelsJRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAllModelsJRadioButton() {
	if (ivjAllModelsJRadioButton == null) {
		try {
			ivjAllModelsJRadioButton = new javax.swing.JRadioButton();
			ivjAllModelsJRadioButton.setName("AllModelsJRadioButton");
			ivjAllModelsJRadioButton.setText("All Shared Models");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAllModelsJRadioButton;
}

/**
 * Return the ButtonGroup1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.ButtonGroupCivilized getButtonGroup1() {
	if (ivjButtonGroup1 == null) {
		try {
			ivjButtonGroup1 = new org.vcell.util.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup1;
}


/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getCompoundJList() {
	if (ivjCompoundJList == null) {
		try {
			ivjCompoundJList = new javax.swing.JList();
			ivjCompoundJList.setName("CompoundJList");
			ivjCompoundJList.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCompoundJList;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getCompoundJScrollPane() {
	if (ivjCompoundJScrollPane == null) {
		try {
			ivjCompoundJScrollPane = new javax.swing.JScrollPane();
			ivjCompoundJScrollPane.setName("CompoundJScrollPane");
			getCompoundJScrollPane().setViewportView(getCompoundJList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCompoundJScrollPane;
}

/**
 * Return the DictionaryJRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDictionaryJRadioButton() {
	if (ivjDictionaryJRadioButton == null) {
		try {
			ivjDictionaryJRadioButton = new javax.swing.JRadioButton();
			ivjDictionaryJRadioButton.setName("DictionaryJRadioButton");
			ivjDictionaryJRadioButton.setSelected(true);
			ivjDictionaryJRadioButton.setText("Dictionary (No Restriction)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDictionaryJRadioButton;
}

/**
 * Gets the dictionaryQueryResults property (cbit.vcell.dictionary.DictionaryQueryResults) value.
 * @return The dictionaryQueryResults property value.
 * @see #setDictionaryQueryResults
 */
public cbit.vcell.dictionary.DictionaryQueryResults getDictionaryQueryResults() {
	return fieldDictionaryQueryResults;
}


/**
 * Return the document1 property value.
 * @return javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.text.Document getdocument1() {
	// user code begin {1}
	// user code end
	return ivjdocument1;
}


/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the EnzymeJList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getEnzymeJList() {
	if (ivjEnzymeJList == null) {
		try {
			ivjEnzymeJList = new javax.swing.JList();
			ivjEnzymeJList.setName("EnzymeJList");
			ivjEnzymeJList.setBounds(0, 0, 281, 138);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnzymeJList;
}


/**
 * Return the EnzymeJScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getEnzymeJScrollPane() {
	if (ivjEnzymeJScrollPane == null) {
		try {
			ivjEnzymeJScrollPane = new javax.swing.JScrollPane();
			ivjEnzymeJScrollPane.setName("EnzymeJScrollPane");
			ivjEnzymeJScrollPane.setEnabled(true);
			getEnzymeJScrollPane().setViewportView(getEnzymeJList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnzymeJScrollPane;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFilterJTextField() {
	if (ivjFilterJTextField == null) {
		try {
			ivjFilterJTextField = new javax.swing.JTextField();
			ivjFilterJTextField.setName("FilterJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterJTextField;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Species name containing text (* = any string):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
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
			ivjLocalBorder.setTitle("Search For");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(ivjLocalBorder);
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSNBJTabbedPane = new java.awt.GridBagConstraints();
			constraintsSNBJTabbedPane.gridx = 0; constraintsSNBJTabbedPane.gridy = 0;
			constraintsSNBJTabbedPane.gridwidth = 3;
			constraintsSNBJTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSNBJTabbedPane.weightx = 1.0;
			constraintsSNBJTabbedPane.weighty = 1.0;
			constraintsSNBJTabbedPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSNBJTabbedPane(), constraintsSNBJTabbedPane);
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
			TitledBorderBean ivjLocalBorder1 = new TitledBorderBean();
			ivjLocalBorder1.setTitle("Restrict Search for Formal Species To");
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setBorder(ivjLocalBorder1);
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMyModelJRadioButton = new java.awt.GridBagConstraints();
			constraintsMyModelJRadioButton.gridx = 0; constraintsMyModelJRadioButton.gridy = 0;
			constraintsMyModelJRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getMyModelJRadioButton(), constraintsMyModelJRadioButton);

			java.awt.GridBagConstraints constraintsAllModelsJRadioButton = new java.awt.GridBagConstraints();
			constraintsAllModelsJRadioButton.gridx = 1; constraintsAllModelsJRadioButton.gridy = 0;
			constraintsAllModelsJRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getAllModelsJRadioButton(), constraintsAllModelsJRadioButton);

			java.awt.GridBagConstraints constraintsDictionaryJRadioButton = new java.awt.GridBagConstraints();
			constraintsDictionaryJRadioButton.gridx = 2; constraintsDictionaryJRadioButton.gridy = 0;
			constraintsDictionaryJRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getDictionaryJRadioButton(), constraintsDictionaryJRadioButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			getJPanel3().add(getJPanel1(), constraintsJPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}

/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getMyModelJRadioButton() {
	if (ivjMyModelJRadioButton == null) {
		try {
			ivjMyModelJRadioButton = new javax.swing.JRadioButton();
			ivjMyModelJRadioButton.setName("MyModelJRadioButton");
			ivjMyModelJRadioButton.setSelected(false);
			ivjMyModelJRadioButton.setText("My Models");
			ivjMyModelJRadioButton.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMyModelJRadioButton;
}

/**
 * Return the ProteinJList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getProteinJList() {
	if (ivjProteinJList == null) {
		try {
			ivjProteinJList = new javax.swing.JList();
			ivjProteinJList.setName("ProteinJList");
			ivjProteinJList.setBounds(0, 0, 281, 138);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProteinJList;
}


/**
 * Return the ProteinJScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getProteinJScrollPane() {
	if (ivjProteinJScrollPane == null) {
		try {
			ivjProteinJScrollPane = new javax.swing.JScrollPane();
			ivjProteinJScrollPane.setName("ProteinJScrollPane");
			getProteinJScrollPane().setViewportView(getProteinJList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProteinJScrollPane;
}


/**
 * Gets the searchableTypes property (long) value.
 * @return The searchableTypes property value.
 * @see #setSearchableTypes
 */
public long getSearchableTypes() {
	return fieldSearchableTypes;
}


/**
 * Return the SearchJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSearchJButton() {
	if (ivjSearchJButton == null) {
		try {
			ivjSearchJButton = new javax.swing.JButton();
			ivjSearchJButton.setName("SearchJButton");
			ivjSearchJButton.setText("Search");
			ivjSearchJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSearchJButton;
}

/**
 * Return the SearchResultInfoJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSearchResultInfoJLabel() {
	if (ivjSearchResultInfoJLabel == null) {
		try {
			ivjSearchResultInfoJLabel = new javax.swing.JLabel();
			ivjSearchResultInfoJLabel.setName("SearchResultInfoJLabel");
			ivjSearchResultInfoJLabel.setText("SearchResultInfoJLabel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSearchResultInfoJLabel;
}


/**
 * Return the selectionModel1 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Return the selectionModel2 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel2() {
	// user code begin {1}
	// user code end
	return ivjselectionModel2;
}


/**
 * Return the selectionModel3 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel3() {
	// user code begin {1}
	// user code end
	return ivjselectionModel3;
}


/**
 * Return the JTabbedPane3 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getSNBJTabbedPane() {
	if (ivjSNBJTabbedPane == null) {
		try {
			ivjSNBJTabbedPane = new javax.swing.JTabbedPane();
			ivjSNBJTabbedPane.setName("SNBJTabbedPane");
			ivjSNBJTabbedPane.insertTab("Compound", null, getCompoundJScrollPane(), null, 0);
			ivjSNBJTabbedPane.setEnabledAt(0, true);
			ivjSNBJTabbedPane.insertTab("Enzyme", null, getEnzymeJScrollPane(), null, 1);
			ivjSNBJTabbedPane.setEnabledAt(1, true);
			ivjSNBJTabbedPane.insertTab("Protein", null, getProteinJScrollPane(), null, 2);
			ivjSNBJTabbedPane.setEnabledAt(2, true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSNBJTabbedPane;
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
 * Comment
 */
private boolean hasFilter() {
	//
	if(getDocumentManager() == null){
		return false;
	}
	//
	String filter = (getFilterJTextField().getText() != null?getFilterJTextField().getText():null);
	boolean bSearchOn = (filter != null && filter.length() > 0);
	//
	javax.swing.JList selectedJList = (javax.swing.JList)(((javax.swing.JScrollPane)(getSNBJTabbedPane().getSelectedComponent())).getViewport().getView());
	if(bSearchOn){
		if(jlistQuery.get(selectedJList) != null && filter != null){
			String query = jlistQuery.get(selectedJList);
			bSearchOn = !filter.equals(query);
		}
		if(jlistScope.get(selectedJList) != null){
			bSearchOn = bSearchOn || (jlistScope.get(selectedJList) != getButtonGroup1().getSelection());
		}
	}
	//
	selectedJList.setEnabled(true);
	if(bSearchOn){
		//disable current results
		if(jlistData.get(selectedJList) != null){
			clearAllSelections();
			selectedJList.setEnabled(false);
		}
	}
	//
	return bSearchOn;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getSearchJButton().addActionListener(ivjEventHandler);
	getFilterJTextField().addPropertyChangeListener(ivjEventHandler);
	getProteinJList().addPropertyChangeListener(ivjEventHandler);
	getEnzymeJList().addPropertyChangeListener(ivjEventHandler);
	getCompoundJList().addPropertyChangeListener(ivjEventHandler);
	getSNBJTabbedPane().addChangeListener(ivjEventHandler);
	getFilterJTextField().addActionListener(ivjEventHandler);
	getButtonGroup1().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SpeciesQueryPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(618, 304);

		java.awt.GridBagConstraints constraintsFilterJTextField = new java.awt.GridBagConstraints();
		constraintsFilterJTextField.gridx = 0; constraintsFilterJTextField.gridy = 1;
		constraintsFilterJTextField.gridwidth = 2;
		constraintsFilterJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsFilterJTextField.weightx = 1.0;
		constraintsFilterJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFilterJTextField(), constraintsFilterJTextField);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.gridwidth = 3;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 3;
		constraintsJPanel3.gridwidth = 3;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel3.weightx = 1.0;
		constraintsJPanel3.weighty = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel3(), constraintsJPanel3);

		java.awt.GridBagConstraints constraintsSearchJButton = new java.awt.GridBagConstraints();
		constraintsSearchJButton.gridx = 2; constraintsSearchJButton.gridy = 1;
		constraintsSearchJButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSearchJButton(), constraintsSearchJButton);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
		constraintsJPanel2.gridwidth = 3;
		constraintsJPanel2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsSearchResultInfoJLabel = new java.awt.GridBagConstraints();
		constraintsSearchResultInfoJLabel.gridx = 0; constraintsSearchResultInfoJLabel.gridy = 4;
		constraintsSearchResultInfoJLabel.gridwidth = 3;
		constraintsSearchResultInfoJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSearchResultInfoJLabel(), constraintsSearchResultInfoJLabel);
		initConnections();
		connEtoM5();
		connEtoM2();
		connEtoM3();
		connEtoM1();
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
		SpeciesQueryPanel aSpeciesQueryPanel;
		aSpeciesQueryPanel = new SpeciesQueryPanel();
		frame.setContentPane(aSpeciesQueryPanel);
		frame.setSize(aSpeciesQueryPanel.getSize());
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

/**
 * Comment
 */
private void search() {

	if(getDocumentManager() == null){
		return;
	}
	
//	String activeTab = getSNBJTabbedPane().getTitleAt(getSNBJTabbedPane().getSelectedIndex());

	String likeString = BeanUtils.convertToSQLSearchString(getFilterJTextField().getText());

	
	boolean bOnlyUser = getMyModelJRadioButton().isSelected();
	javax.swing.JList selectedJList = (javax.swing.JList)(((javax.swing.JScrollPane)(getSNBJTabbedPane().getSelectedComponent())).getViewport().getView());
	FormalSpeciesType fsType;
	int restrictSearch;
	if(selectedJList == getCompoundJList()){
		
		fsType = FormalSpeciesType.compound;
		restrictSearch =
			FormalSpeciesType.COMPOUND_ALIAS |
			FormalSpeciesType.COMPOUND_CASID |
			FormalSpeciesType.COMPOUND_FORMULA |
			FormalSpeciesType.COMPOUND_KEGGID;
			
	}else if (selectedJList == getEnzymeJList()){
		
		fsType = FormalSpeciesType.enzyme;
		restrictSearch =
			FormalSpeciesType.ENZYME_ALIAS |
			FormalSpeciesType.ENZYME_ECNUMBER |
			FormalSpeciesType.ENZYME_REACTION |
			FormalSpeciesType.ENZYME_SYSNAME |
			FormalSpeciesType.ENZYME_CASID;
			
	}else if(selectedJList == getProteinJList()){
		
		fsType = FormalSpeciesType.protein;
		restrictSearch =
			FormalSpeciesType.PROTEIN_ALIAS |
			FormalSpeciesType.PROTEIN_ACCESSION |
			FormalSpeciesType.PROTEIN_ORGANISM |
			FormalSpeciesType.PROTEIN_SWISSPROTID |
			FormalSpeciesType.PROTEIN_KEYWORD |
			FormalSpeciesType.PROTEIN_DESCR;
			
	}else{
		throw new RuntimeException("Unknown selectedJlist");
	}
		
	//	
	Hashtable<String, Object> hashTemp = new Hashtable<String, Object>();
	SearchTask searchTask = new SearchTask(likeString,!getDictionaryJRadioButton().isSelected(),restrictSearch,fsType,bOnlyUser);
	PostSearchUpdateTask postSearchUpdateTask = new PostSearchUpdateTask(selectedJList);
	ClientTaskDispatcher.dispatch(this,hashTemp,new AsynchClientTask[] { searchTask , postSearchUpdateTask},true);
}


/**
 * Comment
 */
private void searchableTypesMode() {

	if((getSearchableTypes() & (~SEARCHABLE_ALL)) != 0){
		throw new IllegalArgumentException("Illegal searchableType="+getSearchableTypes());
	}
	getSNBJTabbedPane().setEnabledAt(getSNBJTabbedPane().indexOfTab(TAB_COMPOUND_TITLE),((getSearchableTypes() & SEARCHABLE_COMPOUND) != 0));
	getSNBJTabbedPane().setEnabledAt(getSNBJTabbedPane().indexOfTab(TAB_ENZYME_TITLE),((getSearchableTypes() & SEARCHABLE_ENZYME) != 0));
	getSNBJTabbedPane().setEnabledAt(getSNBJTabbedPane().indexOfTab(TAB_PROTEIN_TITLE),((getSearchableTypes() & SEARCHABLE_PROTEIN) != 0));

	for(int i =0;i < getSNBJTabbedPane().getTabCount();i+= 1){
		if(getSNBJTabbedPane().isEnabledAt(i)){
			getSNBJTabbedPane().setSelectedIndex(i);
			break;
		}
	}
}


/**
 * Comment
 */
private void searchContext() throws Exception{
	//
	javax.swing.border.Border border = getJPanel1().getBorder();
	if(border instanceof javax.swing.border.TitledBorder){
		String activeTab = null;
		activeTab = getSNBJTabbedPane().getTitleAt(getSNBJTabbedPane().getSelectedIndex());
		String newTitle = null;
		if(activeTab.equals(TAB_COMPOUND_TITLE)){
			newTitle = "KEGG - www.genome.jp/kegg";
		}else if(activeTab.equals(TAB_ENZYME_TITLE)){
			newTitle = "KEGG - www.genome.jp/kegg";
		}else if(activeTab.equals(TAB_PROTEIN_TITLE)){
			newTitle = "SWISSPROT - www.expasy.org/sprot";
		}else{
			throw new RuntimeException("Unknown active tab");
		}

		((javax.swing.border.TitledBorder)border).setTitle("Search For - "+newTitle);
		getJPanel1().repaint();
		

	}
	//
	javax.swing.JList selectedJList = (javax.swing.JList)(((javax.swing.JScrollPane)(getSNBJTabbedPane().getSelectedComponent())).getViewport().getView());
	getFilterJTextField().setText((jlistQuery.get(selectedJList) != null?jlistQuery.get(selectedJList):null));
	getSearchResultInfoJLabel().setText((jlistInfoMessage.get(selectedJList) != null?jlistInfoMessage.get(selectedJList):null));

	if(jlistScope.get(selectedJList) != null){
		getButtonGroup1().setSelected(jlistScope.get(selectedJList),true);
		getJPanel2().repaint();
	}
	//
	clearAllSelections();
	//

}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2003 2:24:20 PM)
 * @param dbFormalspecies cbit.vcell.dictionary.DBFormalSpecies[]
 * @param query java.lang.String
 * @param scope javax.swing.ButtonModel
 * @param infoMessage java.lang.String
 */
private void searchStateAdd(javax.swing.JList selectedJList,DBFormalSpecies[] dbFormalSpecies, String query, javax.swing.ButtonModel scope, String infoMessage) {
	
	jlistData.put(selectedJList,dbFormalSpecies);
	jlistQuery.put(selectedJList,getFilterJTextField().getText());
	jlistScope.put(selectedJList,getButtonGroup1().getSelection());
	jlistInfoMessage.put(selectedJList,infoMessage);
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2003 2:21:03 PM)
 * @param selectedJList javax.swing.JList
 */
private void searchStateRemove(javax.swing.JList selectedJList) {
	
	jlistData.remove(selectedJList);
	jlistQuery.remove(selectedJList);
	jlistScope.remove(selectedJList);
	jlistInfoMessage.remove(selectedJList);
}


/**
 * Sets the dictionaryQueryResults property (cbit.vcell.dictionary.DictionaryQueryResults) value.
 * @param dictionaryQueryResults The new value for the property.
 * @see #getDictionaryQueryResults
 */
private void setDictionaryQueryResults(cbit.vcell.dictionary.DictionaryQueryResults dictionaryQueryResults) {
	DictionaryQueryResults oldValue = fieldDictionaryQueryResults;
	fieldDictionaryQueryResults = dictionaryQueryResults;
	firePropertyChange("dictionaryQueryResults", oldValue, dictionaryQueryResults);
}


/**
 * Set the document1 to a new value.
 * @param newValue javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocument1(javax.swing.text.Document newValue) {
	if (ivjdocument1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdocument1 != null) {
				ivjdocument1.removeDocumentListener(ivjEventHandler);
			}
			ivjdocument1 = newValue;

			/* Listen for events from the new object */
			if (ivjdocument1 != null) {
				ivjdocument1.addDocumentListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange(CommonTask.DOCUMENT_MANAGER.name, oldValue, documentManager);
}


/**
 * Sets the searchableTypes property (long) value.
 * @param searchableTypes The new value for the property.
 * @see #getSearchableTypes
 */
public void setSearchableTypes(long searchableTypes) {
	long oldValue = fieldSearchableTypes;
	fieldSearchableTypes = searchableTypes;
	firePropertyChange("searchableTypes", new Long(oldValue), new Long(searchableTypes));
}


/**
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addListSelectionListener(ivjEventHandler);
			}
			connPtoP2SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Set the selectionModel2 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel2(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel2 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel2 != null) {
				ivjselectionModel2.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel2 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel2 != null) {
				ivjselectionModel2.addListSelectionListener(ivjEventHandler);
			}
			connPtoP3SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Set the selectionModel3 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel3(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel3 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel3 != null) {
				ivjselectionModel3.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel3 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel3 != null) {
				ivjselectionModel3.addListSelectionListener(ivjEventHandler);
			}
			connPtoP4SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Comment
 */
private void speciesQueryPanel_Initialize() {

	SpeciesListCellRenderer slcr = new SpeciesListCellRenderer();
	getCompoundJList().setCellRenderer(slcr);
	getCompoundJList().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

	getEnzymeJList().setCellRenderer(slcr);
	getEnzymeJList().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

	getProteinJList().setCellRenderer(slcr);
	getProteinJList().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

	getButtonGroup1().setSelection(getDictionaryJRadioButton().getModel());

	getSNBJTabbedPane().setTitleAt(getSNBJTabbedPane().indexOfComponent(getCompoundJScrollPane()),TAB_COMPOUND_TITLE);
	getSNBJTabbedPane().setTitleAt(getSNBJTabbedPane().indexOfComponent(getEnzymeJScrollPane()),TAB_ENZYME_TITLE);
	getSNBJTabbedPane().setTitleAt(getSNBJTabbedPane().indexOfComponent(getProteinJScrollPane()),TAB_PROTEIN_TITLE);
}

}
