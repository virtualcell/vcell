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
package cbit.vcell.model.gui;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.User;

import cbit.vcell.desktop.controls.AsynchClientTask;
import cbit.vcell.dictionary.*;
import cbit.vcell.dictionary.database.DictionaryQueryResults;
import cbit.vcell.client.task.ClientTaskDispatcher;
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
			super();
			this.likeString = argLikeString;
			this.isBound = argIsBound;
			this.restrictSearch = argRestrictSearch;
			this.fsType = argFsType;
			this.bOnlyUser = argOnlyUser;
		}
		public String getTaskName() {
			return "searching for "+fsType.getName();
		}
		public int getTaskType() {
			return cbit.vcell.desktop.controls.AsynchClientTask.TASKTYPE_NONSWING_BLOCKING;
		}
		public void run(java.util.Hashtable hash) throws Exception{
			
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
		public boolean skipIfAbort() {
			return true;
		}
		public boolean skipIfCancel(UserCancelException e) {
			return true;
		}
	}

	//
	
	//
	class PostSearchUpdateTask extends AsynchClientTask {
		javax.swing.JList selectedJList = null;
		public PostSearchUpdateTask(javax.swing.JList argSelectedJList){
			this.selectedJList = argSelectedJList;
		}
		public String getTaskName() {
			return "Updating Interface";
		}
		public int getTaskType() {
			return cbit.vcell.desktop.controls.AsynchClientTask.TASKTYPE_SWING_BLOCKING;
		}
		public void run(java.util.Hashtable hash) throws Exception{
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
		public boolean skipIfAbort() {
			return true;
		}
		public boolean skipIfCancel(UserCancelException e) {
			return true;
		}
	}

	//
	
	//
	class DBFormalSpeciesComparator implements java.util.Comparator {
		private String likeString = null;
		
		public int compare(Object o1, Object o2){
			
			DBFormalSpecies dbfs1 = (DBFormalSpecies)o1;
			String pfn1 = dbfs1.getFormalSpeciesInfo().getPreferredName();
			
			DBFormalSpecies dbfs2 = (DBFormalSpecies)o2;
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
			cbit.vcell.dictionary.FormalSpeciesInfo fsi = ((DBFormalSpecies)value).getFormalSpeciesInfo();
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
	private java.util.Hashtable jlistData = new java.util.Hashtable();
	private java.util.Hashtable jlistQuery = new java.util.Hashtable();
	private java.util.Hashtable jlistScope = new java.util.Hashtable();
	private java.util.Hashtable jlistInfoMessage = new java.util.Hashtable();
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
	private cbit.vcell.dictionary.database.DictionaryQueryResults fieldDictionaryQueryResults = null;
	private javax.swing.JLabel ivjSearchResultInfoJLabel = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
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
					 DBFormalSpecies[] dbFormalSpecies = (DBFormalSpecies[])(jlistData.get(selectionJList));
					String query = (String)(jlistQuery.get(selectionJList));
					javax.swing.ButtonModel scope = (javax.swing.ButtonModel)jlistScope.get(selectionJList);
					boolean bBound = (scope != getDictionaryJRadioButton().getModel());
					boolean bOnlyUser = (scope == getMyModelJRadioButton().getModel());
					
					DictionaryQueryResults dqr = null;
					if(bBound){
						if(getDocumentManager().getUser() == null){
							throw new RuntimeException("Query results for 'My Model' can't be bound to null user");
						}
						User user = (bOnlyUser?getDocumentManager().getUser():null);
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
					cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error:\n"+e.getMessage());
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
public cbit.vcell.dictionary.database.DictionaryQueryResults getDictionaryQueryResults() {
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
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
			ivjJLabel1.setText("Enter Text to Search For - *(star) matches any character  (Press  Search to Execute)");
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
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new org.vcell.util.gui.TitledBorderBean();
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
			ivjSearchJButton.setContentAreaFilled(false);
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
			String query = (String)jlistQuery.get(selectedJList);
			bSearchOn = !filter.equals(query);
		}
		if(jlistScope.get(selectedJList) != null){
			bSearchOn = bSearchOn || ((javax.swing.ButtonModel)jlistScope.get(selectedJList) != getButtonGroup1().getSelection());
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
private void search() {

	if(getDocumentManager() == null){
		return;
	}
	
	String activeTab = null;
	activeTab = getSNBJTabbedPane().getTitleAt(getSNBJTabbedPane().getSelectedIndex());

	String likeString = getFilterJTextField().getText();
	
	StringBuffer sb = new StringBuffer(likeString);
	for(int i=0;i<sb.length();i+= 1){
		if(sb.charAt(i) == '*'){
			sb.replace(i,i+1,"%");
		}
	}

	likeString = sb.toString();
	
	if(likeString.indexOf("%") == -1 && likeString.indexOf("_") == -1){
		likeString = "%"+likeString+"%";
	}
	//The character "%" matches any string of zero or more characters except null.
	//The character "_" matches any single character.
	//A wildcard character is treated as a literal if preceded by the character designated as the escape character.
	//Default ESCAPE character for VCell = '/' defined in DictionaryDbDriver.getDatabaseSpecies

	
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
	java.util.Hashtable hashTemp = new java.util.Hashtable();
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
	getFilterJTextField().setText((jlistQuery.get(selectedJList) != null?(String)jlistQuery.get(selectedJList):null));
	getSearchResultInfoJLabel().setText((jlistInfoMessage.get(selectedJList) != null?(String)jlistInfoMessage.get(selectedJList):null));

	if(jlistScope.get(selectedJList) != null){
		getButtonGroup1().setSelected((javax.swing.ButtonModel)jlistScope.get(selectedJList),true);
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
private void setDictionaryQueryResults(cbit.vcell.dictionary.database.DictionaryQueryResults dictionaryQueryResults) {
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
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G730171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16C3D8DD814D596B8CE09C5CAAD9595959595A535E4D4D4D8D8D4D414D42CD1D1D14B96ED34D151C6ADED343D82A6B638D1D221D22212E2A222D222E221E221212292D059864A70A183B34E8CB0FC283FF36FFD4F1D77EBBE9C257F6F79BFBF1E67BC43BD771CFB4EBD77635C77FE8A858B03E64E48AA900499B38571EF5D8CC1C8364C909272EF1B08B83F3EA61DA079DB84701170E2ED8E7C86C1DC605F
	1AF5794242AD39908E07F064534D3A4F607F39C2F2FB268192F4A0A79E62AE0A7F3B60DC4E9B6DA8E7968D077F10873F7F8508G9C794DB1B6017C589F8AC5FC2208C7181185B8CA73628FC5A22E8E422DG81G21C6C69F8D3F332964719B2AC43A7DE70788698F1A336610F4A4E9A2141898EF1D78DBAC7CFBC61A09484A2F4B93F93CA09C81C0B21FADEC5FD1863F558DBD1D5F9EBC50EDAD9FB828B7984AFB0D6DFAC3F9E7FF77068D7BCC7A035DFA4B1EFE3DF928212D8FE28F1374DA6AF2A93FA0D4C2B803
	620E9DA4B242607F9800A40E4F69A478EAF3132E9140E046FCEDDAD6AD6A5DB2F22B105CF96F6C8F9D644B5B4C7293EA3667ABE079016CF9348C56F690DB27C0DC1E25C9D78CD08B508E608D3000167D867DD7703B7900F52B313757583759E45AE2E8B76FA479047FB7EC00989137D7FF50E8EE9784D63E1F5615E968638C98DF49F173B11B042C45328E5DBBD9C83C697C2CD99AB6C98C99498A34B0DB04DB6CB6E132FFF3A8BB5EA22F0BF2594D02D43670521AD9B3B5E497BD3EE6D60A18CE26C4F60D151475
	3FE89DA8027F899F29278C7FFEBB41FBCB702CDE0E2774A49C82F1857DD8B7DA7E0B3A5467DCA1642F3EC0538E81F77BE6F975B39D827A153AB4F61134237B194C55DC17FE0A2F98306319AE9F50FACA420190D7EAEB52317AD55DC55AE978D013AE8EA085C09806FA86D08D50B00CF5EC1CFE151BF52C214DEC5D525FEDE8571B8546DB5D71A97C6AEDD6F35B76BE0B352D6F203E294D5C5DF640203788A24D368EE20383B23E9D346E9F8499BF687B7466B6EB37318FD2EF6B55DB75E68B235957719121B16BE9
	ECE3F72F9E84741A84CC77570E3970EBE833D8FFB23537D975F68A96FF30031449C0DB93248889407F4CAE5D5D44DEA6483F8FC0G374333B43D9F75E668ADB6EC28B79AFB7ACD0DA4A7C24AB0313315769D2170FF74B05AD8FF08604AC6206D0EE0BA1FF4FEACC927367BA0D13F4DBC04ED6C5D03A46FADC0EF83708388810886C89A41FA74EA8B56235AB95789E50F1D27D90F7C20CEE70E307AD3B02AECCFF96DC48E51359A6276830C828482C40E229F18F830D523DD3508BA39EA7B28BF79BFC97C0D5660E3
	322E561415AD36E3D732E60A697CD7ECF764D727C1474A65519ED24609104F8C0042A3D8B68D1D452C2EEFE9336831F66FB6C34554375BCB0A697BCBD72BD857BE24E142AF881FGFCA64AA16D66F30035GCBC83AGDB81960A3A4D9273C674A5FE38C7A03D95EB2B4BG0EF15B0CD0D99520E78B0097C088C09CC086C07151A6DD9D0089A0GA08AA085A07FD8132E8A20954087A08CA081C07002FEG208140869084906305E51E5F69B5C3DDBE33C51D67B8496F82B13F8B457CAC917917A94AE015184735CE
	4A93ED650E0FE9F6132E47CD3AE3EE520DA5303230284A826DCAEC7C25E02F774A7249964B67F3517E1FC9ECAFCD0F1463CA319C9D6902E56B0E5EAB5C24DB6EA65D6A7F8B4A02758D5FF7913B6A8774FD56DA3DC95F578E7FD844381DDD447E221F6BB0F21EFD94EF823F38959C110563FF6EA2F6EF33DA4D5D877A2D7AFDFA2B353B2F539E5FC6E5D97A8F186CAE95E5F5F511B2B78E02DF64743DDD44279CB4767569E9A745D944F8A375E9FA033E97145EEA6C6F33B2FCFFB79977776807C41FBA547D3EC0FC
	22211F78E7B6168EEBA3754AE46E6EEDA3E3E533D528B905783B09F505929B25C17FC13E898AA02E749474D303CB50CF158CDFA3C47DEE4A529AFB85DC7527AC1FD318DF88BC456E2718CE078EE96BEE223ED544DB182E5682E2D75560DE4076B03D062435255FEAB5767DE0B6761BD6B37CB32DC45FF66341FE1258EACE5FB11316838821E625031E55E25C6B3F2A635608F19F7ECF9D37D674BDF93DC407C848E79CC04AA968FBD28F11F89C9708CEE72B51A02681F195275271DFFB0144F642294C6E647FA09A
	6F4563ED9076D345AF51E3FC8A0405F14A78989E1F8FF1252A780E8E0C2F023886C9BC4B4713BD79B46F077B75B05CA383249FF97DCA6A79D89157A456B5961F52C36A1AC1B236CA9BC7E45E53055F142D9036818408F293460963E4EFC2F7FAB752D5C33891406A4D46B6BE707B8560CB47933ABD0710AED90C4F74E669960CE769C5D03A45BDC8E79D4F7C2E6FF8A6B7E83CD4EEB4274BB92DC9D784D0FD9A9BCF351C4664B23A338CC85785F8AF11AED0C557580BF4C1000F9069A2A5F44CAE737B10AEC11417
	F69A532F7AF4269F237B9B2773BA1D495326BB4F08F48127B3F9CABAE63F6DA6248BBB1D454740EF92C00E8F0CC7GD8FA279FC63ACA9F96DF6F4368FDFD24F2FFB4A3DD088F9B3FC4A968185CD7B8DD02981F6643462F39E7C869A6D9102E6A8CC657F00696DD1B151747995A69A51CC168BE696776BB030D258260B784A086201C53BD4F69CAA630F1D7B57C568398A6C869EE98C03A5189EC0C668F3F41936C69B17DC68611AEFA822B4F0993D8BDAD1A58242B84E0F4D1B6BE67B211695FBA115159A6CA6B73
	59C3C867BB110D7F14751915475AE1248B1E4862A3455F7809D23AD7B81D604B628B455FCADFC6477A65B71693DFC167E38E358D34594AAD03606FB6EC58A119FBF913FEBF917A96DF1CE3D9D4CF7ACABF8807GC47922DF794B9C1CE3716F899192DFDABD4B515CD10228C7262F5D2F882FEDFE1F7545CDFDC4E67519D0C6E7E2BFFC50A07D4E5BEC31988FF653CFCC310F7A38D76AF7986E335E8F6037235BEC31EE0701018D7B334FFB093E649BD4C40B7638FA4992C1BABFC56724A4EEE9EB77C03721FBD85F
	AE4EE19C79DE1A5F488F0FE4699C643775CC16CF2FB36D790DB2B44EE2F3811368FC8571D1D1901F81D0F19641A75379BF52D7B2F9875722BC3F612B045673AEF2A84FFA961363FF16DDDE59510C19CC5E8EBD4A0B05781CBF213C5375C49E6953985DEB1C2E9AE82C1C2E2CC3CEF7E687520582CDB227FBD0C117CA67C2082D0B7C205DGB05F396D3054F77A7A31EF75F078E5FEFBDF779A78ED238E371C7DB4684D9DC6F32F3E3D14545C7236C1EBB99D8316EF26515B487F4B31DC6753711EC55FE6BE58D53A
	3CC940B928AB665608B2E3A7DFA1D49F7BDF16561C204F07DDD9B1FEE27B7213145F5987326381D85E423A392D4E86DB1D0DF9CB2E1766AD74EC16377833B1EF7FEEA7759647B88C377190AD772EEE8B53731F1B25FAC64C72555433EBD2C0D64A59CC3F7CF36CFA06869D480E8AC2BD078F211E2DC093F08E6A7137C35231D63900E3ADDD8F790E517791370F757E3AAF247A64360D57544777254E2CB8D10F8C09BE25679C482EBE87757931876BC44D3950AF1E0BF345855F12DFBA66656D7355DF89EDD71B65
	7BEE03D5EFAEDD7E8B53671D4FD11F125AE021AA3DC7D39F6A974F957D4739927B4CF9FBE694G5367159E34CF29BF74577ED80EB79B256598G78C800A4FF345FA57A7F8A4A31A55A50E64072E4B2EEB5200CF460AFBB8FE5188DCA996D9067F39E361FA33D48978838F84E175625644BBB9F624EC73E2DFDE863DA400D1E0FBADF5BC76CE66836D877215E655462DB3B5A7ABA4536A5FA8531F955CB09D9ABE3E5E321F3DFF45ECFDF3BC59C6B17563B13424665CD621C2891758C879DD3397EF7F0FC4985B0AE
	3CG71E693627D8197FB81666BC90A6F6E6B36F637117E3AF4B97BB6AA6D10FE9B6584000DG9A813A8210B77B709CDE0E7AF6748F4C97AEBB0C6B90DE8128C358E154A10260F8BA1F19C91FF5109A325CE2852BB8B6AA0B1177D92BBE4867FD56D6E31F953EE2CB51CED6B34AAC3D906A680558E63E5DC370FD50AF3699766AAD7D86AB237F540275A6GE863AEC49D0FD814F6E269D474AD065F3676F6F979EDBEE001AF6903D66691B7120E14259F47534F3D28C9D7F391263F4E2ACC1F51CF30A23D8DE843AE
	C2DBFE5B0F78944095DF0C69DC55CF5ACF1B39F3B52B6F090724753D85683C81C2B87DC37D727EF374E76C9F3CE243054239E7BA6C9FA2AFE67DC27245767EA17D328359393C7F4C9BC09DAB83E17C9808B2AD83F29DDF5154B11853DFBCA06F931E6269C6GCD9A275BBCA8CFF7D737D63A2517208D038731EED4834EFA892623331D188F757B000CB55A0DDB8D5D87FBCA37C36133B6314306EDA2146B70128D73128C3822CBD10706215952BED2407EF1F8C86AE3D89A378DE1BE9A015F77D2CCFB7FB0622381
	1749533EE7D83BAEEF5ECB6664AC4A39075F37CBA81BC4191F0EE05A1517C159DE465B4948073CAD929D9B07CC70F14C526F9B7ED49DC7FAFFBA50E073057FD62D1185DFE61FEF0F107C0F40560EAE24636C584B146BDBA1941FF619F2FDEBD9BD2EEF958441F7EC902EF75CFB1874FB29904E3D9C5A95C095C08B006DF29CDFDF55CB66FD1D2EA7ED07DE485C57E658A71BG43F51DBEE22B9E7D10987E3DCF6A3F4535D38B293B7EA0B38CA086A089C03882F578FB1FCB9D3659B4F5E0F63E2E1D5839788A57EB
	A0880C6FF64AD7F59CFC2CDFD97E0BF42DBC67239DD9598E7A15AEB1ED5B9576FE0559A451CAEC928178F800D4001CAB1BF4A5D722CDAA0CE52EEC02EBF722B1D81E9A741E5962FF7A63358563FB53C3F9599D1E49BB3743B3F917FBA86F1FDD4AB6DCFB25BC0DD6C51871895DCA3EF2C558DB41477A158F77117A2171ED4E3C2F00E312D1BAA6A175B170CAEC6FEB68BCC6BC0453GF20321BE81548134837884E35DBAE25C642AEED1A750E85C8A38BA2331E72BF2AE1B677396D5BE1520ED1FE817FCA141DA76
	793234C85BBE0DB0D27A3EDBEFE8E776D930937DD3D4B05A679ABAFF138161022BE07C8DD087E0G7081883D8A6D93E75C624ABE1BF1312051487F55B0924B2B57A14FECF416CB3E682B64FCAC1FDF50F925C4084BG483F9A728A50GE0G703B9A73395834514DFA40C6866A8CB2BD7774F856C6FED771A95BC85855DA793B144EF345C0DCB2C0A6C06124A6DDB5C08B4068A44C1F553459D57E78B686F76A7A9BAAFD5DAB479986D7FC0113346A7A34651FE957F572ADA52E7BE475C93FED42A731F95DA478CD
	87E0EDA15508EDA1AF845715273A54C9BBAF53DC7255064879181E830734745C8F34269054739A836A6943759C576B191EE73B640B5254F3D327161E89C01B4A75DC560DFA66DC03FAEE6F732C8DB42B783ED0042B2F51624BB7FAA66F8B951FD2DE23263CE88F65CDF7A94F20A94FE672CC5EE9AA3EF2C5587B9A2D3636A2172C4DEB7915368387746D646301571166434A3556C0C8B38AA07E9A2CA32D9DD8C7D22F4179F3EFBADFDAF2AD74D5G0D57E25DA9BB6C4EBEBE4335F63DC55F2B62F34FC651AABE25
	0D3C2E55629BB6FBA64FC74527149720A92F4862D95B7F4AA5DF18263C47BD14176C12AFC6D31E1F55B3F9173A64CB5214D75B6F193C3DAE79B22E55EAD31FEEA1EDDD23CD6DEDEB6FB66200163529D94714ED0A4C17677F994630FF46B6B5378757242A7F0CE33AAFC649D8408AE1BF009000E80094001C50A6DDE9A80E85E2CCAE47F41231006BB15D47831E593258A5DFCD281C0F653D2487477BAD2158EFA4F928432BAE797CB4F598CF7D9EC9A3046BF06D20E7FDC2040A2FDCD907C33578F65B88DF06A24E
	99B03E57EC1E596ADD17FCF957E971C58DF9E61707D5FCCA3B14E84ABBB06C19BC130ACFA92FDAD3DE5108E7723ED271A965B5AA64317D3C9FEDA7F54FGF1DEG8C776A8E028B06F064F5D89FAF2BA77DC89204B38172A7C37F81D08350B899E9A6D39A32FEEF1B4CFCB7D91BA77329811CE6E5FD2B188E51A173BAF5FDA369F21DA57F67CFF6DEB7EBA471CCD69D1515AD62E6EA70C8775C4E9164B6F5A6CB3A671675038F9FF63CEF6E31435A7B661EBE2C3DEF6EEF07BFE2F8C8C33A5B9C63DFBA6CFCDF5D6B
	079D6F2B5BE8F12C67960B361EF596EDBDF7D91C6B59E4F124676734DC9E4FA263B6F3FF1F353BD7CF13E67ECDEFA37BBF41F36C6D6FE3EE0524EED14E4F89785D3A7F8819530F180CBE69954B9AC16D133635F7DB0DE6CC24D15FE65E5D51419D92D9E1C33D132D0EED7432D55BC62FDB35ED74AF2BF39B3DE7F5EE2387BE7D1072DBACFA8B1DB428ED333609F1AFDA49FED655FE48DD60ED85DC8BD85EBDDB2459E6B5EE2D64ED635525DA7DEFBC58B085A093A07FFAF9FB934FBA8CB8364D5681ED5BEC9F20EB
	0046FE73C13DB27F8D8305BC4EBEA858EE557712EC087BC8A93F354D5C29D7592F157233B8C77C5D830EEC4CEC748A37D1035558301A5B28F827B39FD5F23D3ABFE272EE9AF4EC235B8635ED94B56858C6778F3A36D15C20E39BBDB1685AC6C90324CFBB505D5EED96D71318EDEEBDA429BFBBABF91A25C30E7338EAC8BB0FDF8FB94EE375106BBCEE9BF21C477AA157F97CE948F9BD70217BB2E9BD68B09BFB372E96538E99F61C576B073573FA7330633CC68E3B4E6B3D430E739ABB6CBA2F0F8DBB4F6BD786E9
	1DA77DA56BFBBB37B978DE5FF750ECB498446FF5B6EE5D500763562A6B71DBDC20FB8BCC907681883A9E7D7E6B2A798AA9C4E834A526E73ACC4F35DC7664C3B8EE0F653227523DA6B9E1CD3AA2004AB0BEAEF1BA37D69F66C876ED3DD25915DCF6FFA7663BB58CE5EF24FB3182A09C869043E517B91D0FCFF2A85B2BCF5AEF5B77EA7FE0146257F07CBBB27CDA0E3F5EA4ED3FDF8B78DD2833E8FD972603BE39B760F7611F8EE3BE0BEF40FCDA689E3696881BGFCEE40FC6ABB1C65B36886C7EB3299F46EA67C86
	3676BA0B63593953D889DE5C6BC069C9B899626AEE44B16BD62AAB596799FAA32EB33FF2D8EA13EF38CDEEB0CB716BB9FE1FC5EAAB3BED67D825F83BED6F17616DE5B452AF6BC74023B0FBFEF78C6D99F5A35A33126E9F4B00F07ECD70DDFD935A33D0F3CE3C66A6E647461B9C791E52D1A77D722803FEF954C93FBC6AC63FBC6A24DF9EF523DF9E5576BD5B8764F6DCA37A656554B6DD8A9B0C0AE1BF788DD64409FB088665692D95531BC05BE80402A7D68CA7E958DA5C63B7A8AD7FF582FED3F550BD9C694017
	930EF5F1691014360A27712FA1394FD9AB62AF9C165651F576BABAA245FF4D71255B491996C35B10315FAA885E61343EED47B6DE88618A0096GC3B8EB6B2C3F3F015A24435C568B5DBC9D84375ECCF8E77E083C31C01F84A0G3E982066E64CD37BBC32270AED9E342F9332384AAD4436BB6A5B8E584F12D465D39C5DB002797C3E06541DFDF43F07FDAD16455D3E40CE0FE7CEAE7B74D3CE4F76E4112A4C64605911670EC87774045C4C729BFBB36E6FFB2BDBABBE69E67CFEE8BDC2F4623ED39CCF33321D390C
	16AD50665C02FDCFB94565C3380263C61661B7F4AD477D79A84179C3B80463566D645F5A37E05B7FCE35F60110F7AB14412D727AD889E156E7DA296F2D3B15751944DE8CFF1462DBA4F856173EEF44FDC5DD9097FBAB6A14EBC21DAAA2D0271B9C2E19B682CD9740E804DDAF3F8854E9B55D1B999C2154E9A34507C7A8F53A076F970D063852A928533D865449F78A6AB45C69C827B020091EA237D342945469FA6A2BD327A8F51AC27119D314BAED6EC21D72EE833FF79B77B95D28D37CED68B337DAF0AEB563B6
	6C632F207BA7ABEE87FF8550FABB6A6FAFDBAF30815E77F63B4EC137BB7219AFD29F9CFE3BD27F57A9BE7AF6257EC3FC5FF9BC44D5C5227E239D28FFE0A46A749F6ADFD3A11CB735C9D7B695716CFCBD5987CB7650123D3A62DCFBC78B564D290E67E74A94F1A4FDC734D2E0B22E61B23466F024344ABE5A9D997B2792DB7CC2478F44EE75BC4F35472465938478F0C9BEE2B472AB6EA723BEBDF12AF2CC93CE7169D315E31A8D07F0CC13F787542FBB30FCD6513969F888A7F31CFF8F16D99A4755D0DC4D1DCD3A
	46BB91B701623AA0EC6338DBB92FB7471DC2475171A44CF11753FA1D89617CA8447D0362CAA0DC49F13B8FE1FAB5D1FCEF1F8D77D7B23B1ECBFD6199GFED1386730F3FB1EE86BA040A5C421AFDA5DAA5B03C2F6D0EE587003393BFDCBDBE7BD75EC2C0DDD3F9D4BAA7FAEECE34F50B46BA05C8AE03B8B4FF8581662990F502B2F9062DE50BEC31354F8A44B6FAED61E61F7C97632AFBF104DE42E7891E526C1FC695D8477730FD2BFC2725F164566DDD97CC4D9BC799E1BBDC39ABFD596CF74FDACC79A7F1AAC1E
	58E825484F72B66DCB4CDB6634B041F03337433BE5CCF70B67D7445F6207AF1C317F02067745F3CB9D8E4EADD12F4A72BF6FA876EB31F7E3DF37729062F26E41722FBFCA776142E0207A9EF2874FF5A4A87A6EED4B68DD96E477F51FF533D95F86C38732C3955B606F4B5046DE409BF38F411D332CD64C037FBD6A76B6D3CC9BA184E888FCA22693F98FEBEBB3388EF79F156E0DE5F21FBC4A6BD3B44A5D35936516C42B65E2FAA83FAA5AAE17606A236DF2D9DBE1E730DAGEF0A46769354475BA347C5D1DC8804
	A323391FB392A3467030B78D137317629D974696C1DD66478BB19A79347741DF1E94990E7AE4B666DCE842B1E79268128ED0F0AF74B7G0D7722FD9E24F54E8AE1DF0EAB607DDA9047D5D2DCB40493B86E884525C2D8388FED370E1EA54963614DB46C45ED7BC04F4948B7821123366DF3A7D50652464C36098634ED8968D0855082E08388388F6DF3B61D570800F09C47BD5D0936CD6638F7A8AE777EA6DD517DE8271B3B085DCAF978CE9A0E60F67C214BD19E894F09661949E8F5A8435D3100B3E0F67C259B6D
	D88BFA37G58G82GA2F85E6BD47B9692A4F9CC5548AF9B9FFCCA4F3E88B14AF1DDB1456747A847F5A1A69C5795C31CF78C7F4E20E3199488E7F25C35F42CD77CG34278790373C8B4B366581547D95FAE6B104077FC543E42F968B9F533B2A2B0477444AB140250C93A9CF563F1F5B017DFB34063E58F74F92E5A5GCD6A832CFFA7E161C125DF59D7CF4E53EEB5761A0C7DFD6D5259D2719E057D4477ED6B9B9E6A554B67D2D97C9D8B481D818DE623D55F5DA7A3E071078F93D995827A7E20C169DE792A8FCE
	553CF302C05E03224F97FFFD5F3BF086AB7FDF69FCD48B608D8FE21DF808FEE779C1B818636A391F0AF9104F2952B1EB61B468CBG76CFC37CED94CF4E0213F363642CB743579CF2D62604DF9A2E924305530E67BB61E727B2145F8778AD61EEDD9267ABFA64631B86B26DA25629BF77E01D7252509B4FB260D8459FE88860D8A5E41A32CEB5D3FF05E76758D8252F8747AA6153F04CF539C2A74BAC3BCE8FF31D34E0068B1DE2D4BA859CD20F1F2EBF04FB726239CE915DEA5D9F617BBB13B95D0CC3F25DEF4F35
	6B5EF188F5CF573067CC973A6746AAF53F568643F3611BAA2CAF3D5B5D599F282EE7A39E72059FB71FB8DF3103342982C9FE98BE02620BA5F866971E675FD7E59067970BFB3AAE1DCDFCD7BA04739F02EF98009A00D6005107F06EAB658869E31C6E67EAB41A6A7583FA03E4B91B652F6247634D9F6B4B6E1A055FB16137DD8BE306DA0767707D9EE275A0CC7C0D083DF08679E579DB2D23FBD4A0AE932078E1681BG6A815A9F6667FF0E0A67FF48F6004DD6EB5B41AEFA43815FDC502D9F3423490428383F7728
	D65EEC8F0BFD6A436A7A497856E84E1B850974619AFC4CA60F7CAA6D5F73AFB2E4E199AB69E344B4629F36FF37327ABE300C34110EB603453EE6F74FB27CE64BFD24C9371052417F027D0F3543B672A9A96A7237BCAC1F22BFDFA954BED11B354401AEB50F08F3700F58F529EB2C1C4D74C9BB2A566758CE54A7C6D31F346F18BE53C8F2E2BB6E6DA377D0D85B8EEC933BA1319F9A5F077D0B908761B8BA67A15B472F5D27BFC179A03D46EE2BC14F643D7A21DD5E9B746CA47AE6F2C800693E4D446F720843B901
	A3A6651C40A732EF7E6BCD4AB981E2C7FB7C5FCC4AB9013532B905390715F38248AF2EC99C52364D5B3C0F766336D951635AB6BF76287B7B83BD18D6D09C7663ECDECAEE43259F59ED58E7A3EDDA9C7F50F3964C06D93DE843B0EE438D3D4EEDE868F5EE43CBFB1D5BB02E57398DC3BA35ED98550979CE61B67CE727EB9BFE5C25364D760F6D36B1D8C9E363E3C7EA9DEC9FDE5D686FB338FDA6F7AB6D733AAC7FC95D4E6D7371A125FDB64B6CDBFB4811FD58F870F7BA47DD7117A6DD6DDFF00C782C954708DDFF
	41B1DF51E17A8D4843CBE9185C91A00EA1CD4E46EB046FF8FC09F67FFB3FD399A77ADDA84EA99DA6034083C6B3DD7894575E787AFAAC64A399A0FFFA132E8420FABA36193BBA54EBF6897CCE055669EC4D4E863F3E53595D8AECDCD45305632260690E3FDDF0DC9481B43153197FA36124694AF151C71B885F3EDDDBE407B2D87D0B6E407619BADD3BFFF65CE71FCA5022EFDED5C0670F9F05B14723680B7F19C36ED1ECEABB245E2187C868EFCDFA33F508E1698DEA223E66E3EAFB1F41773937BE4A6CED03DFDF
	0060C7515E4986353D5F6173A2B10FB2FBA741EF7A23EC5FAA337741BE34F7DE3C363D71FECA7AAD8EB415G68A36B6215761E3B1598E667909DD549B66D0B637905E48C3B59E02084967936FE116652D9E49D54FEDB2B1C0815DDA25F575F986F4C578791F1E23A63FB24BE1A4557FDE60F3F0F0D574D7DFA967EFBB706194C8B781C30AF48E47C544D086D71669D767F6F107C7F527613B987265D97E46D98CB992CCECF1A6DF87F4564595A7BAF42E7EB6F9D3DED3673FD3EF74FF63C4F770159527BB3D99B5C
	B80F746B51D0AEC9GB90FB5698A814A9E4376587C813927041D42D50E0B3735F7127D9F64F6EBAB4A5F20F31C574DBA57F7E26F5091BFFB4088F53617244F7704D27FE0B51AA8D25CA3C257F34C5D1DDDD6C60A77A468687EF5FD87C5B3DF7475D1DC232FFB8C7D53C5C751BFB5F25CF636FE8561F00E9B60EBDAD10F61F7D34BFC014BFBD9C76E23556C3340CABB5A865AF69FB8845F3C0DED1D3C5E45AC13FE8364BF8EFE60F1673E0D499D370074BBD5C05B88E0837083888608840883C88648G48FF824686
	G55G8DG6DGD6G9F0040A730FC2F1E335C116E0094975AE8A11B5B3AC9B10B7AEF58A555BF8C520BFE42397E2CEF886C433E01FDC7447CA6DD2FAADE31DBF33DAA6337612C84B17D4CA76C5F9115709D417459A1BB0BD811G631084F774496F1177DDC3BC4C521DA5BB5B5A82E99A5CCA7709CEF9BA63DD1E4157CEE7402276CFE772F104D78273CF81709B1A404ED7B07F7426154FE5EA680B7318FCAE93E8D293A4F319CFAA7D53054D76FDB72B45B905CEFEA7D19E5057BE09EB367F33913F2238630F0E
	F35B37D0873DC55F56A77A236BADB8FE9EFD9257ED37523B0B42A19C8B108C4076A73EE5D56ECF4C043862274037BE056B48C2A74EF354834E7BA9DC172C6B44B1F364D3A8E7005226C3B86FE9E8FFCFA36D61AEFE766D69639D5F84132D32E29BF9C0367F518AE979BC6D3C6E30B6D9D9C07E67FDBC9B6660D90D8252DF73BBF868750C7B00302F13657F06E3D8A601A0AB1E67696DE37CDCFAA2667FCC015056C2F8BF00B5916DDCB3C3E9E7DF088B8308CEC4BBAF69C5BBA7822E60991453534B77CCBC03F2AE
	24345E908E848867347EFDFC7D7899F76D0C630F7B3F305B79EF32FD11B94F028FFB56391D99DF20EC9E268CF8EADC7009E79A66B976F1BB663976F17B6651FBB441B7111B0C982EE59EE967460E8E0B1E7B525F68DEBA065BEEE9B0DA3AC9E9337C5FDACC724F5A7A8DC7312DB7EA682FDC37102E07935BF7BD2BEC6B2D0A7957279659E51D4967DF2D8EE44D164872819A82B87F9A782CF28D7C2FF46EA9C444B3DCB245C509B8E60B8B96B83679258BDC5BFC52826DF145F58B54630A9B97E80FABA296C84795
	8CB7F58131E1A7784996CEF7B7477D4CF17753B4892E393B5D5A45718FD13D883ECECF84A27ED12A57A04535083884896EE791774C82D2EFA04BDDC6B372BE370014DFC73741A05E79C4E93A7B209AF1FB3CCCF56CB635C974CE63381FF9D96C5925AE1F26DD5272E163127045244FC884DC86C051DF21CF862883E87DAB0EB53C0ACA8547E38D361B47BE54E0325ACCA81F4C5708FBE9CDA81F4C81B15C973DEA3AA53DD2BA56EF5DEC41FE4B8AFA057E957B22F7AD58EFA57F957B2D95548F95A5C1DEG6A1310
	F6E9BF76DB6DC9476B9FF6168882DFEBF0F916BDB0C9EBEF4008CBBE653D8F62DA03CBBEA9C4A4615E48DB299DE21314EB8DD1941F1024DCEB58EEC59BA7C3DC5D5F502FFC542F74AB21909787107CB734ED85BFC71179B7AC07B769DE454A67208CG5A1F433922779D6E75F45E6F97F84837733879181D7EC14799234FA96D7786457BBC27345FA89F4378C3DC864F6F006CFE2C7D4FB769CC4FBB77D72C9DA4F5285B708B9D52B64C523F7518E77571DE17FC5A7571BFAE7924607DBC564799B368391A671576
	7C0062031FD75A73DC816D9986F18549D89FFFD50DF3EC909784901E0C75B16990564758E42C0FE96CCC518B504F81D43E0065335C65BDB85A75EA1D07FC266366E3F61AC36F2A2DFFC1E93FCFA93E6585257DAE63E33DF6080B61793DD036A73D6C6FE0573F3BD39FB3FA5475715D9EE9FDE4381B8E2969A28EC969D899BD4777A1B67E9D6717CC6413DC0C3F43BE7F74F7E7F3DE814B8481776F35ADA5F996475954E5B3FC11ED0E9D0FAEDB741FD397BB9E23C4AFF6BDC6F9E83176986551456AB1CA42E26DB1
	4A330B25E394D6CE379FD73FEF2FDF8FF8481765724E946DFAF999759B51FFD7564B900A0F7B3B32DE2E66FEA611543D9731DE2E105D21928C7808971D57CBF6F6BAEAAEABDBC3F71FFEABFB010D573BE9F4FC908B69A4F1B9D3C9510BF93ECF727FEE6B584FDBF60D298C56BF16297CF5AA643DA025C9D71A027D63B91D5836EAD230FF7C9FBDAFE003309FC0C88A5A6BEB197F0C82FCFC0A3DDCD2D254E5444A3F1B261719225C83E721783C1714FB605E61E79B0AA04E6BA5141F56E9AF2337A57F1F5AB97665
	F571184AE0F5744CAEF5FFF9CE173ADF7D0476FD49E0178CEE1BD9B36C7648177CFF21B0763639F1CCE53037D987E670F7EA7E415EE6AD025F0AFF30BAD7778F2C4F8F0DE0FDEE7D8756670B0F5271860443GE27E01B6FBC1B6FF10847874FF586BF06E4B0E6AF3BBBD67D4743272AC080962ABA5F8D61F5F6A4133A075909776B24A1F5BE7AF27A2497FD7750EFD195DB126B2D8BD8D6BD357671B7B54635697E57BF08AD221FFCAF5E77E679ED979D583CF03DBFCD7C97A7A49127F577D817DFE43184AE0B65E
	522F367B8F7DD23B33F92267685DACD6B231AB60986AF67209AF36399F781BC06D292C4D0D422F1FE86310D4ECF3F77073F1D1295866FC69DD6D99904EFF854A73952C7397283E41EADE115C8D740A23B6B71D7A0A2ED714BEA411792AD714BE241D3F376283F1292F70FD2F545FB53C8A5F212FA2EE816DE3FDA09C48F19771F300D12F226E468E79BE6AA19A4EFF8D43B3BADC156D096F231E6FD246096CFFE07522CCD0570BE749B293FECF4C40F1DBA5645D709A560397E7609CF570EBD88F4A68FDC00990CE
	8748CBC37C3FD790FC89042B819A53500653D5F7DE99526CB673CAF3D4BF6652F9F37FB465BD700BA9BEC202E7F9381C7A8492CE04383A57315CB36937C6B804E3B8EE9ABBE382E1611F085B510B7523641F287BA103767E69810A37821DBFC0B8274FF4FAC664646C1F7E44B0167B2719AD7EF3D45D5FE49FD30FDB1E52789E7C2B6CFB10150D299F6BD7BC58AA6F8D2CDF917DD83FEA5F40FA74AE259D0530BFC0A8471FCA7777C5C3B891A07D8D3479BD2AEF223CF43BBD0B539D552F9C7AE6C4E53A32FE4D25
	7816F4E57DBAE7G6B57A8442524E33D19CC7B1F46B71BF4DDEFA2EEA56FEBFC5FC4BD93BA647B2C1EE36FC4CD67F54DE9BFF0F276D90DF3AA63646C33BA58295E77B35C295CE7157826F21F551AAE753A6A76AE64CBFB132D2B663E85634437981F3856520D6B9F15EF9D7FBE2B3A37A45F36749BEB2BD179DE8E115588F4DDEFE1D9DDB543336F701BBC64CBF548672511E7FB9D8F26FD357FDB4ABAFE9345C73C252C63EB32F89F8AF1F57F42793D9DAB697A6F3F146F3C77D0FCF106721DF721ACCF8C7BC2DC
	613F31DD3CC44772416F40777FBB086BBA02EDA5719D346F3CE32E6FB1AC25B4C2E6132E98208620BD136DA3734EC41AC9C7DB05BF6A9E433B29ACECC3E3F50F2138B72DC75D463E627B04F1EFDDF026F2EF5DF3BD6AB676B6670B48E4EDAC8EFE13C5DB32B656560BEDACB35371B9DBC7FB6B725F3537B1F6374787FB156F7990D9C56F625D9F37EE520A2F6071570FE845573E4B7D334BBBFB355B193707FCD39D72353C6B2CFD76D31FE2F8D759BE3DA95E6FDDE57B4C66BEA89C620A7F036DF3BAFD874877BF
	4A76798645E77CC759BEFD0630FD363F5724CB931A453678949D770FE6B56B7C329077F3BF364FF040317CFEB0683AFD4E23B489401381C0760594E6B76B2AG76E7E3BA81832D429F55BEEF22327E1876F9EF273A1DA5F29FB88A79A76D4C9FFEC33219EDD8BB2B653EAC8A70DA6D4C599E21F86041F64646D7B7723D70C990474A7EF0970E458AF31AF58DG0CF6CA972E377AG0E0D39DE2434D190CEGC863344FF57373A9B31B45797D99E41B30A8F7043F39D0B39353E2EF9E5AA06C8790B2936BD7F69621
	8D9DE6FBDAD8D9DD1BC5EC55B252243381788104GC483A40DA04D94CAC3F822A02DA4G617DE6DDB1C04D7BD83F2A32DA355A24181E2B360D32497FC962EF34869F1335CFD3D63622DEB9825C1BAC2DFBBA8DBAF14EE1A511C546B09F8A6C6CGD9BFA17500EC82A66B7F2B84B607CE6EC1A37D70E6002DG8B45F4E709FA91D9E4B3C98FC033407A843268F20C4BBACCE53502CDFDG42G92G04D94D3AD20086G9BC090C08CC09AC0E1AE1481C097009FC084C092C06EEC2877GADGDEGA1GF1G993331
	3C5EAF731A214E6FECD167B9CE723BC04C6FC2B1BFCBC47EE58A7B2D92733856C9D9202D5C697F1B5D246BF1136E181BF4E3892CAC3AD5E501F6A5B6A60BF9D8E7156513AD164F67227DBF1358DE1A9EA94715E2B9BA52854B569D3DD738C9375CCD3A557F971405E84BD135BF79EC947DC971874C1FD441EFBDG699359BD88470EA0CDBB60EDG81G91G8904070E25F654AA5F6AA57C99BC5EF4CCF98F9B094F53B50BEB76CF6E515A1BD88C7115BACCE3DD39722DE3C2D34F637F592BA523D5077E46FE164D
	2AC3BF364800FEAC94F0491C365B00FE2CA88FFD4F851436914286G6FBC24BD071FEB8B4CC3BFD6C206F2229F5B3CB7D72C5F711C6755CD4813B787F1FF9F41BD8935F3502F457723DFA3FBEA44FDC2832E7D5A84CAC3F8C6A12DC000C800C4004CB958CF4E26E399E5DD999B3FD622A9EBEC7CDA42B0117547783537292C227FB66BEA818CG7EG11G49GF99FC27D85588F608D908A908F1089D0128F6D8C408A9088908D108AD0709154A100F6GDF00700F303C4E9A7AFFFE6D7F7B3E1415456B2A3278FF
	FE6D0F87713E950D3394397CCCC162C74C2F2543EF6E474D349FE5FE4D4477C194833E922091408A607FB17A146EFE25DFA37C61BC3EEDD069F3C8FC8C0F3FF3C46933C8FC62476877CEAD545ABB198671B9F3B10DB7BB34E4944DC59F16433F45AAG57B5977DC9BD7F968B1A0BBEEC1852A690DE005C82246D675FE245856843AC247A619E42635AA359D200FD56206A6DCF775A57F88F796EB8EEBEB64771EBB71DC3A930FF7B0AEB7A94EF136045376E394D3DA1AE056777C559F9207ACF1AF52D1F283F4155
	FAFBB93DB35D2A1186536FCE7ABD6C73C933E24F41B40A8F1260195EFB781E038808AB7A946B580CECE99D93CEE5FBA6497FC1B4FEA50F77821EGGF97C3C2FB0BE846222D471F77078B808CBD1456FDF01719990177F19BDBEED9C1DD7D809EB4E4590C766E82BC41A04F10CAE401B5045EEC5BA9B4413F9DD3F4F58F7FDB07C127D2A2563895D115DC897AE46470A69A6FF46EC9DC169F6B5A0DD46E76C3B155CBBCE77130E174ACD63F4790570CD8BD0D74866925A8B19DCC6B72192692C007795690AD5F48D
	DFA3DDA86023C53A9A899D334B275FA0DD32A8AF331059257AF4297D1E61F4F57318BCED3A3357A1DD6BBCA6CFC9476C372D0A5BF99E0B77035F9000D80094G165E3875C817A946974CE7743EBED239BFECC03A46796C7BBF4A27C9C647643E44698CE2BA5E70CB66EEF24F1052DD39916982C53AF0CD3A5F2A11AE56C1FA89E7903A0FB6F1BB4FE7F3911970CB56ED7396B46B4AB9DD7B9E24ABDA4066ADAA61172C71564A68E60F707CAEE0F39C86788D17244774BBEF9069C697307A6C3F0055532209CD3AF8
	781F51BDE5C33A1485CCFF2108519596C96B731F07102E2C084D1FA86BB3AB0F1D43C8D7DB446C32DF7C0D1FA8251B4573E19563FD455FCADF110E76BB5F0DE2FF998271C9C568F7CE9FC59FD578B97A281BA9ED83043BG3CBEC75A2B0EA0EDG60587D950F4A6E7FA66381793D2CB30FBA3F17F51DACDEFDAFEB37ACDEFAAFAB536914DA7C260C63FACEBA4667B53FC05CC7FB79FE4AAFB01FF9F40D5A86E1BF0090CE5BD70E5F0BD19C371C1F294B7882FDCD274B37F425D03190791639FCFFD65BC7FD6DA1DF
	5FF173B19F26237B53EB97AAFDF28145B7ACD47A64CB78377CFE080B62793DC0361F3E2438D9D7D56C0ECF7EFEAF612BD750360AE1AB7C7AA862985F9B1B34729BA85206E948E6FCAF53BDAE510A7884B11C2A4147FC777481256FCEFDD16ADB7F436305C54D3AC2GF97C65FC8CDB8AF1B52A786FED985FGF1DD2A78E49EEF03B8BFC9BC6BA3B673BEA208484F067ED324117B6E172D5C57C2BCD97349DF4C66AECA96CBFD77673DC8D7A94657AFE66936AF1676F52FF2BA5BE276EDCE7ABAA2D76E3B7D7A104E
	8F68C2GE2963379126445D21FBCE5G6952811F370451A52A680E71BE3B8CE8EAC53ACC899D334B34A3FC9F5D92A6EFF489330B5CA71FF39469E216B0F95AF45F9FC33A24A5CC1E120E596F5F1CAEC30C4FAF81DB3F5624AB055F5A92745DD5FCEFED0B98EFAAE174F25F1D496942CA589C87715DD2BAA67752AC1E8FB11DA478A573D3F21F5C124D759369723F54227BB88769CA3F54CE0F795A7BB8DD4D17EC3E2585FE3BGFC3E147AE4FBFD8978124D4D0441EF94C0120CAE12571734AF59BCCE6ED228FFCB
	6D69B17DCEB3A2DD55D2D61F9B1632FACAFC375FD2745D53CCC8973C14699FA95245AF15566740435C47AFE5F3C44A7A4C4A63DBB352894B18DD8A45DF396F4E60F495E2FC1D78AB775D957CBCC6BB4487AEC31FE43322BF0EDF06BE6DEACA1BD78AF68128AEC55A737B11363194FDF7F43FF35F7DD63FF35FDDBA605CF77FB2605CF78771FDE33EDC4F00C17433519C7756887A63CC0E3B2D9067EAAB17E35E37D2FFEE0030B7C060F224AD63E7A942177335EF8F3FEF8BBD645B6361F7712B6C7D0C65CA9F7CAF
	0A4FD82E7441DE7C3BB867ABE8E3DFE1FE07E55F453100CF7A4A398FE6735F8FF762791CF41ED6DB374073F5C8727F9B8E6F04BE9940E464F50F7DBE6D7FF4EB6C1B146D07E4388B0C3827C2D801363917FE5F2B5F74788B45B7G1D95A00053B7733D26E3772607094B980BB7BDD83DEB4C46FD5D111037F4000A336877ECB6599FC966G98DD4DD7C8D7D6866305B224BB729521A35F580C6E85CE9788B4711CEE718AB95D3895C8173792FC67CA240BD250BDC77738117C0F82CDB800F847AEFD572D0DDE6DD5
	4EFE9A74668E2339D75FDE0AAFE314533307651BE9B4FD99F4B9667B4D2DA8BF9552ACA947F4A7E5133A51EEBC58CF2EF6E2384E5D747E072EEE8B633DFAB7726E87BEFF4EBB27E1360437DC40F956F58D240C70FEC81646E08366AB9678F3D6E1FE676D41342B81E7DD0573CCFDFB24FDF200FF13AE946262D6216CF33FA2F8F617B0E4165E0A06722D15F27DEF2AC4994940DFD401B2EEDC25145188F1368A54ED68EB647385DCB4672BA8D372E5C2DC71EA647B6E9B4CEBA5608C2BD1672B3F2901DFC337453A
	8F75AE9FA0173BEC6DEA6B6B944BD1FCE9C3AC4AFAA9B1ABD1711D26F5A8A39852CF6432EF637802B5B0B6D903783EAA44FB83AEF28D6A74D815641CEB77303EF4B91BEF3EF20FF2DFEABA70942EED56152DC55E772A6688767B1C3777356BED827A4CA56B79DE6B3528C368FADE3781974E53193B01546D0354886596EB1B5509C1D80C3CEE1BB6E03A2910E6C9A54A7BF8A32F3700732FC4F9C39B15F9E674311CBE96E8F33EC67A35555A7427D5733A8B34562FD16E06CD088F85DCB2CF6772CD788E8E8B6F5A
	A32F27E77236D074CD332E719B64CB5FA367FBE3D84E37E60457D3600966FC970EA09F233BE3906D1489B4156B106ECB1B27FD8BAB6BF69B1635E19D5AA0FB88F58A86DCA217F5083EF9475BAD6FB37C0754FDC67C902F4BD5D0172BB06D01E1DE178197D905E9BFB32CDDCE95A3C81F8E34E56B117E0291FAD79EFDB70E48A53771D8C4DF34ED54337153BE8F79FC0F1B0F0D2F169FA1F9EBDF2F9CF72D21F853FA65386BF6BE3FB68AF1C96B513FDFFB44B33DCF2BF546D73B41519CC628665D8FADA23D41A15F
	27FB15FCDE92DAFF8DBEE68FAFBA668E59502CB87FB50162A3A4F8316F68477A9B83F1E59B51CE16F2AD3D7DA0BE98A082A0F62356DCCF39667BFDC9A2ED7AC6355EEC6CF634874F6A66D6238E9F758ABC5D79127F2FF4FAD667C40149089CD399629917DE75387626DE753876B7BAD7DAGF6A92D46FDB013A82E9A4276B74E62776278371E53858C93DCA00443B86E03EF89AE9A42719C376EDB64CD6238ACBA97DD318946F11B9077A34535C0380B63EC2348EB5D04FD571703D82F22811739894701878669D9
	4045C8974369AA3F05B14437C8B7133E25964369FAED3C1F851AC4CEF72D02CE477B63026F20AF7D8E696A0664F407F8DF698DB4115FE1DFF92B7544461F853CBD25C31AE5B5182EEDD0BD7E1C55AB77EF5F77A2EFBB7085F25E6BFA35471FCF742AFD49E7FD182FF8604F5B0C791D5207E95782EEF4B30E717EEBD40E714837D078E6143D40C6705A634FAB87647ABFB7407B92609736200CEB8EABE510EF2E2AAD28DB882F8375007366FC2F75AA794877DAA467CBBF02F90D83DC6ED654795723B542499AFF5E
	FE94E554C07A362DA85BF88C71E100CB6178A582620BEB1BF5AD35285384C1693399BEDCF0BC36CC90F03C61D70B69374D4074E3GA7EC4374E3E61C5858724BACCC378A52B4EDC3F977E7E15904GAE094BAB4F569E031C423FBB8B3FEF56B5FC0F74A98E68B766A03D8F50C6FD0FF2DF7E8C7199002B78815359DB08E3BDD6C73A8A15E7D648D908AECE1F20E8E31F71F6B08AB4611C6EE81FBC5D4CFE2DF4D3B97D55FD72F4DF60698AF5D0376B102E5EA8CF77854DF4CDF5186F6B0D3CDF82DCA4CF67D35309
	0DDDFFB2E1DDCA673252799BDAE55B615BFFBBEFBF0735472E638F2B7B1BFB8F233E01409F3F9D536EB4A3BEEF87780F9D187643E66DFA3048423F1900B61C537BD89C0FDD3FB96E7BF798DF2D07FCC7BC3C6FE7BE758929BB14E357A5941F31C3B9F68D65734BB9BFC2DD7E9147C397FBF80E6AAA27FC31BFBA9A03DEB228B9169369539D723D23BA1BCEA0373ED9D784D081D0DB2FB5F63C33D04A5782B486891DD73D23B16F2D540E7E754ABDBF77D0FCE83DF24F4F76FE5C73938DF125BB513E2BB577F4796E
	E4F203F6BA4AEFBA3966D27C7FDD497F277D81732F170D298C71EDC40D7957733B2563D436BF6E7CF212F7F666EA32914FDC05EB588D6BBBBFF325F8E3AEE627242D50F91A9C03F41EA6FCD293DDFF0B873A141D38F7305F28C5A36CEA56956CC21AD2CD1AB2923F8B6B41AF59249E94DF617E59F04657C5792A0E1B6F7B2FBC13577815E772DE76D05E5B9E4ABBE305E772AED861193C14154AF6DCFB25BC0DD6C518713D2562ABD704DBF6E975DB6FECF555AFEB4B1B6DF4CE0100C9D3DE68EE4F645D66124FC7
	D35E11864F6CB9D1452734E710263C05FBBC4B5FCA17FC918AF92CCF7059236C93483E3CB820CD5E05EB167FDA036327CC5ECFCC2974CC4F7BDD72D56E5652731615DAFA5683ED6BEE547325D52827EDB76AF96CEB4F74BC53A5DF18261E66ACADBDE300B6116B59D9464723DC4FED5FF8D64F76297814773016B5E855333CF51E491B2F62D34A2B55149765213C07DD4A5B2FA94FDA65193CD3D5FC650A3035C12B5C6FD01DD9A377C77882EDD08316FBFCB616FBC4836E9318C23FE953A01C3B27D9D73C876BC3
	69FAF7463BD5FB6C3AB03E9BD5FC6E65BBCA45274CF77D9EADBB8FEC70CC1E370ACFA92FDDD3DE61C64F5A73D217FC231A7226FBA8AF49A51F3F26BC5FEA4F64DD6412AFD4D3DE0D667EC45772F639640B5614F72E077E6DF217FC091A722AB43F47DC4B7B5EA5DF3A263CE855F723FB6DE13A0A2FDC914E5B2BA52F5BC678CA762A3FB39C01B87E9A724CAE1F39642B525473A9951FFBF6C9D171A96D5220A9EFF458B3F993D4FCCAF9DD1A723ED155977764552A7814723CB4652DBAA2707F2BA47F5FB5BA765F
	5DCF0D298C760DFDC9AD2E2B846C453516FB68DE7A9488E7F0DC6AB1244B59073815F4AD289142DD9C973A0F5F1F368FFD6CE97D727B537CE8186C0190FDCD7F585F1F365A298CF777BDB9376504F234DB4EAD38DEF6AB45A502ADB2390DF2D6210D729B91D73D1560DAA1EC6538ED747C018F046D6F271979DB71810DF8E6F08AB9460A7BF5E819485F851F3897770126B4621C679DFB71DD9AB61772725E93F947BC3351BE1742523FFED8BA0F4B7438EF9847E539BF219E0B3E153F0F13C21E479667F7F6FD0B
	BA957E246EF733DD68D47613D227F923EA5BD40C22CED5DC278323F21DF4EB6CBA7DE994F52A535049551B697BA5BA31FA39E25033F90B75031E4DDBBC6C213CC48F65D9EC1E49BB4566193C0F06BC1337F048B3F9A68F7D51D1177E286BA7ADFFB45F657C3C363CD217FCDE1A726ADD3E0B21AD6F9717FC811A72BED76D6DF04F1EABD4FCCAFB06E94A3BD3F3AF09EBF98F39640BD148133EF7C47A06241F507F258CF82643DBAE798A1B34F4C8BA0CBAD4B4218E15AE77ACE96B3045A5DF17268ECB79DB72DEDC
	07FF2B52F22F6CF30FAA7914F39791CDDAE5FF170A4FBDF90F38149727A92FDF35CF4BBDF963D4FCCAF9A91A72168B1E49DB2762ABD70405E6ADF94F2878B46BE433F2AE25D3F5C72AFBFA762B7814FA16E96A39A24BB3F96BD5FCCAF9351A729ED271A9C1DB5E53AE7976EB4ABBBD5BB3F967396433EA4A5B1C63193C5DAE79FCB5653D7E19E7725EF349975C2C55D79D3462D92248E66C2B54FE4ABD9D54FECA89691ABA2C9D4073D0F9AD28434B9E6A7036CB3E5A96AD9D0A86707C55FE2E434D2ABBB45CEBCB
	7728784A95E17F962D327F053ECD9C5AF23C73B07FB6F9E62B5C432E7822B4751C2662F34FAECF2A7814F6C9501457EB76CC5E080ACFA9AFCDD35EB2555D6D6E497BDA452714177B33163C62FE017F3FDC727F0DFF40BBA1537E00F7C2A279DD3D45BF6377FFB85D4FE902309747FDE26373AB9C7771A85259679D86A9AE91F0191CAE1A6F214D7F85F1675131E0AB042D9C77B8BDE7698361C00EFB166A928561F80E7BED88534B78857B0329F45F0B7A5D0587A8BECABC83CC4E6D0ABE40693C4C49F9F7214B29
	0CD90A30E76FAE30B7316BFE154E5574F722FDDAFE4535293F9A5117047C0A73B765646AB7310F2DB6E09F1B74AB2EDDED20FBF68B77B76BAAG6A76E3DA579951A7346EC7DA365FAEG42E1GB11C3649425365340DF48FD861EF10AEC05DEFB87F31301C24F9505857374DEA5CDAA9607C491ADA44B7D80D8D2BB97E795D927A1DF67A70869D52F7180D3DDBD70B7826BD52F456703C7F3E9AF5EC7D8DF57CF28D4D8F0443GE2FE43FA133946D93F1C741B76FC51DCA12DD2AAFBAD177D6ACA34E5BA177DEEB9
	11DD763FE6DDAD407E7F216CD7D7B913ED7D1FA35937FCAD15DD49E5DF1B057976799F4AB6513D2CD190CEGC863328DE54EE4677E6EC87668B752B235175D56F5D27C9A0E7F178C3F16637FDCA5AD6B2FF99E1E6D47BC947D0EF94822F9E8043081407BF74C4313CE4719019AF9E0FB6DD652FD42E13FAB77602D2778185F15FB706E24EB3E344EC3DCEDAB76DB5F5173F4649CFFC8AB56791756CB737B8D4F6FF59B2478759C3FFB23548EF63B4D2E16626DF63BC706375BFFF0132C0DB4E09BF9F00F9C3FC644
	9BC7647835A27E1DC1E97A6B846C173C283F0804FC46723C568CC9E92B38AE9F8D4B5BAB26FD533054866BB8FD6F0894CF6A821BA38D2F45B95224D6577336D55172B75453DB15733617174B67E3771026A34A4A96D0165066F88FA04A123E57CE7085EDCAD935FB55779A7E3A175FD958A65E578B3F75GE46F8153631EFD28C73B83BD70EECA3A97D1F17F3BCD254743659FB15BDA75FD564DE6FD5B775D86B213AE08F369A965B8276F8D3CCC07852BD007G8D9D14F353CADB042AF45837D5BD07FFE8AB4A0D
	E8437A343FCF3E16F0C69F6A916B409633A436D03EEB1F2452630A3DC4770EB603457E865694D25D84BC2BC8651B7B752CAFBA76AD7A5854B67431095F5869FF981553C70DA2FD498124FFEF1D1DFE520836BF5C532F6D8F4775EA7B4346BEEDFF78D61F529F0A5F0ACE67EC779FF05457AF9CD47AB8165E8BCE3FE5CC8E536BB92274DB6EFC4BFABBCCEF75D1EDDF14F1D45B97059E5376C52D02529731BEAFDD407EBD60G5651CBE7E8776F8732347B771CEC6D7E7D0EEC6D7E5D16235D3F1796AA7BF7F1CC2A
	394FBFD434D914C35BA96A905496F1ED1A4E8547AB78D244F0A67C669F14473174969B157E0425370826D7226029924375879D6917EA5476C307695910D660B39D44B2D9FDD85B8FE519357DD030D95B8F7D4F226D079E6D47FE48DBC3DF6CDFE68A5AFEA86020329FDA2ED897DCCC2A0BA82B4C0632029D48F25677C628E41DB56A3C7F8F61F778C59F447E7FE93EB619202143CC09FCAD9DD2D5BA049ED6777F779CC639993C9CDFA9D43FE914B38FFDE6DEFBB3FD532884FE2BG9A5A116F4DC1352F4D33A11F
	351D79DADF788DEAE76F4F30FCDA07B01F916D2EFD2D72CD2358F6E5BEFF6F147BAF5FCEEEC787694F969C7B2F54F6659AEF32EC2EAB836273746E5C053759619DFE4540DF29C8238E42ECBCBC19FEAF6E57AB4FE7C7D03CC12FBC1FDD434F040EC2DC129E4769B7BA3C9F373623D95782E068101C1D69C079DD743C16FF07D23E196203BB14727F4D4F0805C3DCE1A74A7F55611B6923C06357A94FFFF0A74A0F215F35911DCA7953A9BE26D3A97F97AB4ACF00386AAE145F3E5A117CA020896F124B0F69C27997
	52EF5644AE257CABA9BE35CBA9FF459A141F89F12D5D781DB2F7AD4E9FC4F723CEEF284E8F703E9EE8B281728F5975AAB904BA9DDEC5E4D49DD26AB4C271F50714BAE52FC21DDAA0AE72906AB433024FD37520CE57BBDC2728851A169E45DCF28F6A749A3D47472BC729535B946F572354E9001F7B8C02389C836AB42E8CF56AB220CE4F4B46CE3E008FB2587588B7B87A46E4B2E28CCA1D3CA8BE412054A9176B1482F1753D28D3DAB66A94560BBA2D255F296AFB223E23F8F26F8D39B3075C4D4370774E987BFB
	221E1DB11677C4B11F51349EFD0650476F8B316119D5C6F39D7DCEBD83A03F8F672CDE28657723776138FC5B86128C0B2F94B03E0547F7ED94F8590C5B1BA74A750378981E6EB43E9FCAB062DC455B14368A428DGDDC624CD5F07B23C0CA8E3DA35A0206FF82FDC7AEDA25E813F8AF9A2B84F2B1B6CBC27ECD5735C466F1ACD63BC9FFDA770F2727ED6DA1FCBCDC0E3F247075CAB3B8B2581F85ADD70C95F2DA67557E642BA1DF998F1019C37056FC70B829CABCB5D8C34EFB227BBC6575052CD527BA0C22DFCCF
	61E1446D26EBCC0D906E62388BA9EE7430749E0A7B8678BCB82733D11D93A11C46F1D514AE478CFE450C38D3A82E4CAC3DDFE2B2CF2F5A4C4F851B64F574D29356D1DFB3560F1C81AC37C0B316DBE50D3D0E7EE64378F89E5F3C59DE976E9CD557053CA34813E7C11EE335F61EA02B1407557532F9588F55824F288575334E43FD9661962C6B3973E9BF84619CEB332E480A6796EF1F2FBCDBD189F1ADG86AB1E392E34E11B7286DC2495650C5A304C532DA867DACADB565F2C2B85585F4F6F149E427CD97BB1FF
	D7EC335BECAA3F3BB114475F7B3D406B66BBC675D9D141243E5324054E25A7C39A197DD84ED798789A48G626E2638CA88975D0FFDF2B8BD53DF4A43F750B039D716053F6FF53426C0F85C699BDD375D5F9D4AF0377FF55D4E9F20362C033C37F2FB4CB5713BD6B8EEAD4585C0B81463C6194DA1AC5C07F66ABBCC6C144743B69666F6938E1F4C35B4A2C7ED37F9A7D506525662BEDC3A1E928FF9CF65760834703BA1879117C8F1A5900E0CC61BBCEC25774470F0828D13BB10D93875242CB712749D572D2113A2
	43D1BDE4F99F216BB9D5107786EE0FFBD7230D5AB96E850A7311596832B5F29BDDC343640D87F1BF771A3137510E13A243110D58FBB8DF2D45BEB790729F869087108610EF63F7A951395E8A8857F1DCE3B95AF1BF47D9A94E8B427E9CB71562C2EC52B6BA38CA5EC6CF21E1DFEE5B592B463E0DAEBE29B214B6E636AD2AC05BC6C27E63G52G72071AF595C3E81F7B685ADB83043BB8AE1F0F5DC7B92E0262020624BE624D95F29F719E8DC7F3BB0EE60F3D0FB84F210C1361A3189DAFAAC3BB06C37EE308CDG
	F2071BF52543E81FFF5331D79D0477F35C95F44F0097047DB9EE915FB390B1EC37936D7F9EC0BB1E464273B07E3F8E7308C0F8CF4C0E4BDC4AB891FB32F925D9B9B82F14BBF27C77D5940DA8672DFA57137FABF8B85A16853F5FD4E11E7EA1BB7BDF8A7C559AF25536D8AF5B4759GBC6DEE711D213ACF50BD5BDF60A1DF5CF17331EF795EFDE4DCEA1358D37C7627F8EF891E0DF95F33FFC7C2DC7A88564B3777C965370C4278F6549DBB6D116D9F70829EFF377864FB55C300A752859FEBF397FF2B9EAF87FD2B
	9EAFA70E2A697EB6AA25E37D414F7C8D37B8101F8C10F324D9D782D08B40E8661A797B2500338184GC4GA4F01A1F7B10A683F0F9C71BF5E5G75G0623788D36F8063C0E1FEF154F555EE345B665FB74784F88859DD536290E81759921A38378ED934A758A9B106B714C40091C1F0AD2697127FE755C7B557C6CC0AC57E337063EAC8FA4BE115385A8743D176B1B2AE1B7D7671804E3CAFDEFB6497B1E59323389F9C0DFFC4C1DBAFE40A6652B841EBA3778BE121D9D588FBCA63778564CF3550784A6E90DDFEA
	DC72A977680B63EE17FCD270BE06F32523741BBD6018324F9ACF71414714FD56FF67E31F9586F185DE780D7F386A9B5F86F1C1G61DE7C5D08E15C4B936B055F72B3687D09B9274078872072946C83B758BC6B3B6BBC64BB653879181D3E20F7F6561F22345FB20AEFB9C5E93FB0BE4F518EF1B1BC3F574A4EC9171D8AF6BD551DFAF6376C5E2FC66069F241A76E77B1FCG3F5636835B442EC06C8BCEB7E09B9F05F4726388EE0A6C7EAA6D741E24FC10DEE3375520E738A423360C8CA34A7065B23EB239163143
	246E3B5A785D00012772FB67CDEADD4E30E86BF2398579C339AE0FDADC6B7206C5AD631C555AB22EDD0DB2A2390C44552EE5E42E164AE0ED28E1AD4EE1451E0AEF2F782CD53E3D72498C695BA9372DD53E3DC26A1FBDBEF52D726D15353237DB3E28D43E3D027CCC4FC5655A79DEDF0E794E9907796EA9F71D6F09656AF23E0227D5B08E4BB93EDCED1FAD95FC4F4DB8344FF005F37B04D6B8374F33954E6D133F5A39FD1E4952364F1BD918277D5CBE4B32DD5B6707ECF53E234AF0ACE16079FE35CC196F57E579
	AADE69BC5FFBD7AA733DD9E6B76F154E737D5100FA7D3EF4G7397BC0E2D5FC742EF9CC0BAFF6FE00A467AFD5CA072250CE36B7702B7789FEF4647469ED5FC7D3E4C5B75DE090869CD3AD80085E238465B5ED7B31BD665E0DD2A77467962149F84814730B3362947356FEFD30FFF9B84B55DCF02140E756516FD827FFFD4727F4CFDE3FF96610BB115A14EFFB42A6D70C9235ADEDD2B54F43D2B54F40F7729691E6E1352097B767A319C3D3D31ED5C2AFA2FCA5EA73C24FA2FCA5EB696285E2B127789BBD56FD5A1
	BF53E93D99F50A64BA9DB6BB5769CA33F31D9EB3BB57E9164539CE477A78DA8B57693ABE67FD48DF7B1C6B741151F99F325568DC27F4FEC62EECBC36C15DF6C140BE46FFC03D0733FC07BDFEEABF6E49E461A38372BD1AEF71F536F61E7E1E9F85DE1F0A8754F56C4B81F5DD9C3229690E5A54F425C754F4AB0F28697C687ADAA06894B69E4708274EA7FA317FA7CA7EAF18BF767D434AB11511E8A4F9FEFDBE3EE19789F9EE85DCBC7C260CE7FD39F09AFEC744B5E019951E065F911752B9168688F781F81D466F
	9D165D8794G7850536C3EA16AB4C77BB57A69F7C97CE94AB38127D0FC2A844F4675338671CCC04E69204369A83FE448DECEABA57F5FE29B7BB2FBE4CCE530FAFA57103ABE5FB7242E4FCFF62A691E6D1452097BE98D72767A21C99E36A9429196F9782E5598E6B678E92D347CE321DC12CEF7677B2821DC1E6E14F2F93ABFD4C853CD07B4F3FD5CC9B7AFDB1E4E3DCE4F5DE8812BEF8D7C3D02A2105B68435FBC64F3B43EBE58B6DE23345190CE84C87741B5770CC17C964DBB23D9D7FB8626D143575CC74F40B4
	7A68F7EBB804E38112B9EDB7DFF34FBC837BCFEB3D3D1EEF121D452E1C502C2B1B604EF72D37EC1F55FE60B13945772D6C5C29B7708438A74F6559E5ED3E37E57B2DC3C1D614DB725E114DE34583CF0A8BBED6FFA64D237B9B4840DFC0FFF741CE01570B798B30CCB3A13D4A09D8CE8796E03DB0CC44B21DC4E903A19C89904F71E58BE99F87619C5FE6DD11AF76E50F296E442D74356BDC676B28AF7D34116E9B75E5FD669C0E7F12623DA4F81607E05A1713F0B044651F09EBBCB7527923D1887BF15C947A1EE7
	B804E3B82E6A882E85251E093A0F8CEA1F9FBD1D622B4FEA566D8798BD8B69678C0E7D795125CEE51C1C73230635586F12312038E7FEAD763BE4FC49F07F2BD0539D2C1052B11D1BDC5E8D23BD2F596E12CFFBDE739217FCB259E7613C66C1DA2EE1E7A967658C949FF916F2DE6E4D81ECB7B190D776A72C877F51FCC740F55966F9B88F394D43F94867E87F6C77A7E5FEDF267840BFA973EB657310A190176707796D134DC31AG6F6D674EDAD0E30E5A3F7F1C239E87DC5C2F2EE76C6DDEF9FD7C492C267B452C
	264B6FD74B7D24DFAD7763C1B55DE7036AEF7062851E15DB3907FC1D8E79829C581D156B42A2D2FE21FE4ABE351C62637C14FD6AF58B30CFCD07385633797CFFB141471E4D68D773F4AE21782AF36CF8719DC36A9BC8B89862AA7C79BD0EF4CC9EF9DE33AE6EBC447570B73D524F437AD554C866F112G17C9764ACDEE5215G54GB4CE46BEF98525A9BC3FD9D7855082B0FABEBB5F91F8BE1FAF5F57AA26C3EC12F91D3A5FA569627F0D127F73A7BB6F23EBA47162980D4A42FED7EBBF73EC497FF394E56CCC16
	CE72BF736F8F8C2B674C120615E7DEA24FD71EF9099BD14F193DB84257D34FE7F3E6A9701BA95A124D196DBE02F3E68597B8BE4B64684CCB698576FE053D91543EC379C69011D5F9813E0BD53CC9AB3E0E47CF5825953F7F82FEF7859D5F1237766CEFC8F78F8A3C2C4C127F5F99987BEF2C8227BE7C6448C8315973743A647F61BF609B725CBF609BF23C46B764840DEF4823F48C9BBA4C768FB37D3EACA2F53CE524C9E783708388810886C89A417E241252909E9354A3BF00B000B800348BB01D1B9634EA6857
	A22667AA9FA81B7C1FA47EC6EB70097BB4B5E51176B55B059C82F8860D7C0FFD0DCE03CE1CB3AAA632489C7707821B1F1B8BC0565BBF831887C0AEF97D9C001CB9A56D1AECA25F8A30D0CCF7162897113588208720D9E0BEE09940B1AE6BCAAAABBFG7AA600D6G9F00B00084G6142E6DDA9C083008DA088A086A08D2070A270C7GDDGFEG91GC9G3997C33FG5082608590F2B116D755C22F996A7C4E96F51E63A43F8B447CAE947333C464DF26305FAAB10FEB1D14855A4A1D3A5E6CA6DD0F1BF4475C24
	9BCB906758D4E501F6A5B67ED23057D9E579E40B657339E87F4FA4361726C74AF125D80E0EF44132F5C76F95EE52ADF713EE757F85E541EC193DC7BD8E184F5F36083BD89C8740EFA6G6993591E29D379DD94850150CE816AG3AGFC8251B71FB6AC754D21E733FEB80447DB9A2571A1B7337848C05CA3725E20F4ED934F0144C1FCB2CF23FB11B48DDFBFC6134963AF9F541211FF894EF3940E60BCC749A5B84F75A1FFCFF9BF607CB9EDB3BF6B9CFB894EFD0C2734B917B66B0AGAAAFC5DAAFFEF6267ED21C
	673AED17A060BA4545BBF2457A6D4DF91AB771B59E0E3BEB976E01C93F9473B4DAA44D13F0AA5BBBC37E8F2271A1BC3E6C32E6DDAD00BC7E474FB13E91628C2A78B49EBF8AF17E2A78333E4078E0080B1444270D235F1045B8AF9FFB999B0BA70BB48963180F8B70A6F43943C8D79284FDBA50D587B1FF5BG3F647B3EF4BCDD476AC73AD6B15E964452758BE2F2A3A81DF5G69020318BFA1F7AF50FDC56325F2BF9D646B86C0978F109E44FCFC7E65CCAE23FBE3AD5295833E6AF2C69727221BD209F4AD00B709
	F4E992BAE617E3DFA31D5F65CCDE48654CAE5527B7C96C777DB7C817FEB91327CD77EFCE17F785132724E3761B3C0E5B798AB19EFE9B497FG7E04172617DA05F4A1E2FC14C86F6BA3159B369E6904AB19DF0E72E91251B139FBB7A0DD6115AC1D0AAB5918AA778CA9DD5EC6242B9369DAB5696E63F4B68769A51CC168062B391D2FE4E30410AB59FCDD847C16F33ABB36A3DD4C15ECBC11843FE4EEAFCDC6173C1367B7180DBD8A03595C9F2647748B28C73A52E0D61FEB02D9BDAD1A582473027F995DB3BCBD7F
	E026FF28C8979DAC2D4F13F7A1DDC2B09B57A86BB3AB0F065DC817964C6C12FB957B0D1FA8257B200157E7B13ECA7C2D749569E87F37E58F76FF0D906FFB9576C59376E07F97FD9576FF9114B6934285D7B76B4A2EC65A90FE363576EADC9BFEFC2FF2ED38CD365E1C3BCF39B6BCDB36763BE11FF2ED78319CE93CE11FF2EDF8658C695A70D4FE6F7FA857735AC66CF343B9EE9DBFAB19FAB55FF7C8677BCAA6C17DGE81C0434C10B309FB7F0DC42A07287CF6277343AFCDFC68A091C6F13D14F660916F848377F
	3879443372548F26CDD24EA7E651F72BB3A7A96793A7F29F1B9B82F68B417C0E176D1F0D83FCF208BB6BA3C1BB88DF0602B66F9A96AE065F4AEB346E735C2F792ED11DC83B7F9A35EC46F75FAE42E7D244FB0B61G8DBE166FC70AF09DAE94E8122F41B75B73BEA76BFC447FB1BA7367FC3D6EDA28EF57A25D7514AE1D53FD7685527982CDB427EB7CC2CEF7B3274B841A4ABFA35DBF95F4850B72443AEBG1A60BF733DF3C3A776D67C6AE1141F88E9960472FD751F1336A2FFAB7E4CFE79FB53317C1D79FA6073
	663CEB87345F0A7FEDC03A9F1025F149G66AB927853C3B13F5F7177584B2EEB5635DF07E3ACFF5938113C479E88F1D157216CFBBFA7F86D37621FD9AB573F2892E54483FF5EE414714D91250CDA086B1A0C3ABD4779C681974E79420714FC299017FFBDFFE37EEB4CEB896076DF0FBAFF78CD0DF0325E0AB7FE03B27CA17DD8AEFB49BA446704C1DF990678A7B85E8A3850B054695BAA02173E114D467E8F48467EB981CD3AE460493D81FC698D485BD56578BDF95FAA6C6BABEFC09DBED94F6BB66002F9BA236B
	CF6CBD79A7F93A0910E6410DA8EFE4832F3700733E9165BD37C119E7C67F75C6240F845AF4CE9F38D11B7EB5CEDFF693545D1BD06E1555088F84DC7CCD184E871B709E81968E59AE2F27AFEEC73E3CF078EE88C73EDDBB64FC8F6F14734D59456BA97004F3BE6BAE64E3F4F755231DD2012664E624AB5DF9E26F3DF76E647BE2EFC69B3C4FF57287DCAC17553BCB7B3D778BF6297B0CA7F6713AFC8B5465DBB06D215D3CAE83AE7496CCBBE937F6B92DEDC07AE420AD3A9569AFEAF07C5EFB1D4B776F347DD21307
	FCFFBAEEBE665F4B68193446DB15FE7FEB0AEF3DD569776F60E3CB8344455D0AFE7FBA8F4F5E5D2DFA3FCD8A15910E7C77750D1AFEDF245F6F102FDA355EEA1250FAEB70097B43287F8908D05E67F30C62C3A21477794C627BF5A2A02EE88A5A692AC5DAFAFBC13CBFC088C064944DB7B1B4572563C45A64A9EA3D599CFA65886EAB4C2DC69D5E9B95F83A391237684E7E835E223B768FF80BAED0E34F65659AFBAEEF25F3B899E03B3C5BF03DE9460F84D7FC1B740E180E9FF1DD330253D9E81F6585E1FFBB0E4E
	7F045CA63D1BA6E193BF274C691E21F3C23937C33D389DF1CBF753337937CB6F1219389BF9EBEE67FBF20AC8BDB87EF7291E27FC477FAE156DF34F641D7639E772BE7F42B3F9AB3E70CC5E6D9E4AFB40C3F973D56D5F3D778ECAD5FC650A305F6DDA7D46BA277D2BE3F95BDC7205E84A1B6E613BE2C9AE7922B465DD295AAF641EBDEFD071A96D1920A9EF1B4B7DC18E5E7DF64917261047465113E563E872BEB919574B0D043EA39267ABDA79FB6F151158CFAC76703D778A17FC06C8ADBDB70CE869698D3481DC
	4F5EA328E79857B345C3BD5FF249172229678C1B161E42D49877CFC5BDDF9EC2BD4B26221E13D56F4B3BD74FA2D5FC4AFDEF6DD3B55F787270FD79F1AE5F179F5514375C43775E5739FC6F5DDFD35EB39E4AFBD945D72E8807CE55AA77E455DA9459E7948D34893C5CAF67651EB695678D6B687CC2699D4D3A9A0016BB30BE1C2DFAF7DE8BCCF7A85FA8DB6061FB6F4BDD3E776EFD07161D7F6A213CD7DC4A8B5414F70A6A1DF8A5E84B7B13CB3EF0CDF99BBDFC6FFD3BCB3ED8CDF9EFFAA8AF5B65FB6F491A722E
	7170FD79DBDD72E5EA4AFBE53BE7725EF149D7F027163C393BBC133748A5DF19263C7B6BBDEB8F0F29784A1569E84AB36CA4FC7B6FD4FF1FB982F1BF3107F6196F124F242967B3AABEB77BE3951F52AEBE1A720E6D76CC5E19AABE253CA0CDF96BD5E7995C1377030ACFA9AFC2D35E1A3D827FFF23647F68BDE37F0D7D4C184A904FF78D6337E95C1D7C8EB43A96D79CA5FD6FFD59CF7C5BB98AF17B687A1C97047DB9EE3A7DBE2FA8FE06E3C07E5E7BF89AA67348ACBC4769FE4F13735E7BD227B25CBD1B615C16
	110B50EE76F757DE22385C3B1AF525F7210D3ED90CB62A66B89B351BAF0403B8AE1D2EDF065FA5FDC36CE77E5EDB74DDB8FF79708E751DBFAF6C40F55F023B112E70C7791DBFC7FEBC11BB0ACA6FD65E79B3E127F46ED63C27034F89D7F2BDA6EC126B715AA65423766E633F3329C525C77AAE35BDF43BD10FF62E47265DF2BD2E580DFAD8B574F0F587120FCA0FBD0B345F0969636FA7495F0919BD207DCE4CD3835A6F440C4A5EF5313F1B96D301FB157C6F46716FD92B69F98188A783E45E0DEDCF6F745D3402
	FB9C3D6B713303F7534CA3388EDFFA8F4ABE052EB137C2588660F78F4AB6BBFDABBA44216C058E5ECDDBEF43FCC7F2596F5173B269904E0BEE5615C4236C34A1E7322B239D49FE61EBE9195A4BAED4761E1A7DFD18D6876F2695B8F8B76DD17E26EAC3B46661911A879F888781C470BCDC6DB48FF19AF9E0F35D1174DBA8B9DAF9C6BA0662B32315E724F771B3528577C2995E0B7DD388DD5BA3FBE1926F45B63557413BE91FBAF8B7ADC20A173C2BE3163D27E637DB1903F753122A355FCDAB59217DEE5A0EDD5A
	6F267D27DE7B3D1AF1349FCB3B175C1B0AF95D3ACB7B3D1A025D5A6F555CA2FD97D172DE0D29C17B3D1A34E1FE46633E63FFB72D74BEE5FF7468A2F9FF776DCF18FE25C67A4A7EDF79BECC1DAA7DC3B6759E56D378BCCD63FDEC8F2B81FE3D6EE36703189EAF717BDF829C68616C2D34D0159E42E267EF654CDF0C3EB162BE54E167E27EC619068E2E5ECACBD2697027A135AF3CFA88652672BA2433494B44445F8D926EBF7E37528A6ED76A1139C3FD4F46B33B85FB7B5BA93FD723183F15D6FCBF6E1795765869
	976F1653BF3A9B69DBB87D9249B879218AC7FD149F5087AB7298F1BF1EEF4B22EF20445E2FDC274BE3EF295C2FDC2773DF0D6BF429905790436FA4DF03632570986CB7879D6E81CBG1AD4G61813BDE858F20CEEFD2BFD67A00D22777A83E6A8125CE270F22CEF590977AG6AF486BF671C7FA06A742D43732315C0D377205CCE7B9FC41DAC743EBA5303CA1D0ED03C5703CA1DE68E21CEFE90177EA06AB40BFF8734CCC31D92E4F36B2300771BE657A3F81AA37F32154A08182654E9A74547CCD36A74188DF5CA
	00386AD8FED7B57F16880AC51D6EE677F8285ECCF91861A7B7533DBBE40F9043B772F3DEE377E6CAAF17B1F6EF265CD905FDC7DAAC5F3730D37E9EC59875D164BD8A61A11C8BDDB4026DB37FA1EC1F3F50BB2AD83C71A70C2F6371C79A85DEB657707346DE909F4153FD1D5FB51176900ED3965272ADFD986AA6C04B43C8BBFF98E5189EC6997F79C99030CFD93DD87D66455145489342F996B55BF9268E29F9AE677BC513B8CFADA99A311C3E143D19D278C8332E6C91F776B4A64866AAEA002751859F2B3F49F4
	9C5E8534DE0FE01DAE67775D7BF35CAE0A0BF8C4FA5FFD2E623E7B0FE9186C11E7610BD60F7DFD77B71D94990E6AB9BB3FF26E9A9C9F44C27E13G0438E6DDB1C0CD9C5A67ACDA07DBA1EC633804913423AF47E5D0DC980423B9EEB3BDAB1290A73D6F7E1DD179FD77B30F10B0B9D7A04ECD1D543D8D5A775DDFFDD2E5A8ED4CEC9BF484ED1BCA727F9730ABC08DC06BDF50BEBF31B99C8887F05CE5FC0EA114636E2438383FC86F3B9F9A125FF7BF0E06CD5C0E3A21313F6F3E44210C931D53315B718BFE0EBB99
	721F81D0B83DD9D78550B29D6D739A7DEE33C15817639E21B68B03F0B4476D62FEB6E5BA5AE97B107C3E7BFDB4CC56B3D978E917E7F9CF7C3E7BF417B2CE441EECEC9B4F4779D50F9E7FFD77750FAA47360FD751736BBCFC7FCE791D53CBE5633C9660B7E848D55B427A13144F8BF87C5D623B5E433BC326FA48771607F70E1CCB7DF448234A3D0201949F7E28F22F6017C3B88608023812F82C178BE5FBCBFCG9F986F0E1D46AD16720581CF34DBFCD74B721BGBC29AE78D81B2B56386FFE3346FD772F6DD653
	256FD65FF576CE05763D30F379FD4142E3F8AF6CB6B76E8B56EB5C97BCE58D9F37BD0667F6DEDF637C1E49A5AE6E8B7E4945FD41272FF5F427A353732A916DFCCF65E7B76BF83EDF9BF11D6F8FC7555F48CBC7B12D1647F01C5BB22A364FFD7CDCE797374F732B1C5B67939777857F60623E60218A6776B158346D63ED43BC85F17B5CE2F3ED1F47ED6AFC578E6338BB0C675BBC2C4C377C2E4F60E16779FEFCD819EF79DD1F39A34E737D7E207A6E134F6D777EBE466EBE919EEF5695GA473B387FF5718AFFA17
	5B256CF1B6DFD48B3FADA29F73A3FABEAFE6FA7C786F8B76FEDCF9DF70CC7EFD6277B8FEB7F47EAC885887EC9AD1778BDBC6547D47441F54F4FEBF296FAA3AD0F21F6495127FD7B89C4B1C8CE0B2EA47D406383EB52C364346E1353DAEDA2C263BEC311AEE2A469D2DF75B547D72D70ABBA46F102DED44C059A6EA5497F53B0B9F115FA139E9D41EEE186C3D16B4C8B36789F7523D58A6CFE73B4B3D19CAE07EF89BBFABD588F29B1E406F6CB387311F71F9823F490B6877FB940493G521E403393F61F1E1BG5F
	8489FCEF9F1F2334A5E09A97D35AB08847GA4A5485F05A06DA6A3815B4D04DF6C757983D97EAA1EEC5655BE690E3FFEEC0D7A8E312756286F903BFF151AAEF615140E6971234BBD1B5AF75F7D6C12CF7B6E3BF30E6B6E3916A7F1AE4F0F161561C965386CC20A37BD299C17AD363F3D89F1A9CF6238EC0553F5A147FD43FA8F794C474D477275A1BD0BD87F14B23F05945F7214B23F1773BA5B8EF1B1CFE95F95DC76B45873E9F76A59A20DFEEA29C6BFF54DA01E3D305FDDF53E64BE274BA47F177E81F7BBED7E
	83E4FCB2EC4F5397127FA77D81BEAA6A8F70D1B7EA147D2D3232A7F75A4C95D6EFF994BA3850D70419D6617DFD740818D578E83341D03C2F2B4D2CEFAF26A7CEADD6E1764EA17E7F025AEEFA2A2D4DBCD4DC3A4BD83CD7EF311A99EA3945ACFC484F52ED6F33682D162401359B321F3FB2BBCB4859AE3CFF40E8359AFB378BB986FD07F53BB0535C5D59853F59D62349E6821DF224E0B9AC9C15E388D805D98DE623D55F5D87CAEF6B9B9E6A55DB055C2D46DE13313F2FDD1B67A4E2E5FF0EB4EC5C56E7551B0B9B
	75B6EB3155D84CCCDB7C3D51DC3C32782BD20B354D3C3C38374DFA30CBEFA9EE6B9BAABE8866EEBBC8D80ACB9B4CFA0B2598F900FB1BCDFF305F2ADFEE95BEE5C830F83F413A3D2F4338233E6DG944A89674EAAFCE05751AAFC09E50ADAF4006690535B86B54324BF588DEAB79A0F47A4EE0955E140EC6C05A8D28F3152FDE02FF40E12DA045C5D86E248522F081925D6DEFEB26CD465E9490EF9457C7FB3D676E7954A1B3B74E63D447E9A7533D65FC18F497E1F3DFF77F9D0F905AEC1846E67B6B42C7C8FDA37
	812A976E88EAB4D430BEEE0275EF516B0D04C4C81B2284CAAFE828EA841DDDB631A18EFCF74C06246B5DE70098544F1C5C306EA8685C33EB8FDF9D7C7061501BA71F0EBEF979657DA7A0E7572E030F3EDC86C6431BAF8F5F3CBE786E49A9E0E5F0686717271F9BA06720CDEEC8BE8AB27FB3CC8DD492A217F594646ED7C8FAGD0CB8788B6B129F0B6E1GG8CF481GD0CB818294G94G88G88G730171B4B6B129F0B6E1GG8CF481G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1
	F4E1D0CB8586GGGG81G81GBAGGGF0E1GGGG
**end of data**/
}
}