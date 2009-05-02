package cbit.vcell.model.gui;
import org.vcell.util.UserCancelException;

import cbit.vcell.dictionary.*;
import cbit.vcell.client.task.AsynchClientTask;
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
			return AsynchClientTask.TASKTYPE_NONSWING_BLOCKING;
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
			return AsynchClientTask.TASKTYPE_SWING_BLOCKING;
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
	D0CB838494G88G88GF7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16CFD8DD814D576B89595A59595959695956D52C6656ED2715B580DB6EA59228D014AADAC5AE5F7590DF6292835E25B178F15B1CCDA295842C4C5C5A5C4C1C5C585C5C325C4C5C5C583E594E4189906C1799860FF6E3D6F3973FE4D0713747FBD7F674F731CE738671EF34F3967FE1C7B5ECF21E0FE50744C2C82C1481CAE08FF6BB285A139AFD368519BFE09386A25F1BAC17237G60E3615F8DB9705BG
	F175B7476B7205479BF2A15C8A618E7FF85DE7707FDCA123B343G89BA1013GF17F5A3CE43BF3B99FF6211C19B4FC13BE8FFEBF82E8839C798D055FFC6181FD21087796718819D900ABA5B4CF690BC4DC9A5064822E96203914519B6017A9F06906EA112E755A90A17DA9CB56EC120EA4CD049811B7CD7CAD965E4B8CD8C6E43DC575A4BC2590EE83A0194F96F66EAA035F1A266E531BCF1E6832161FBD29B7984AFBCC9DFAC379697E2E8D9B761B75A73B74FDFB7B75164126F6235ED0301C243771D4AE659704
	C4C8AB0862161DA1B2DA60FFB3004FF2445F5FC3704970FF86C0418A4C572625B5225EAD438F8B892BFBF266B848D7598A161F669576FCF51C191A75DFDA863B4F90DB7BC1DC98C0B4C0AAC0DED91C2E8C608BDA767B3A1643EF5D896BB6D3CF0F49D8E7B6EFB5F4D83EA179047FB7EC00989137CFFF52E4699084D63E6F6A4BB474F1860CEF66297365E3B6892944328E5DF73F1078739B33E6EA58A4B1E4B82B3E0C5922354CEE93A63B5621ECDB193CAE4AE59F9224323D3FFDAEFB3A066C200D4FE57B096984
	CBE4271411323E035601A8781F7011FA4A7013A83E6CABBB1E554B1594CF42CD90977195560D160FD017461CBB057CD5B7E95AA160573ED92565CC075AF225AE9F98C95A06F2A6F39557E53E096027C870CC17B0DACFC938FEE51CAE36A2CE476AD7330134535655F13A81GBF009000E800E400E92B310E5D5235524DBA5654EE31EE6D6FB2F468AD8263EDBE73A97C6A872C16761D46BEEB3B71247EE03B252B7D04C15FA708B4474E909B1C10711D20F57FA44878DAEF54DB5A2DDDA6A3245E5E23376AADFD0C
	267B4C4704462227314DDDBDFA9050E396B05D3EB3F3091D5A7B2C874C9D6DD63D1D02458F1FA1E5F2367DA0248889407F4CAE97D1FB95D64669AAGEAAB518E6F5074F66BAD50DBEC5850E8B2F5771B1BC9CE843FCAE267C3346FE8067F8D15E86391A341A5C0B8152773D577FFA46954F71DA46A37DB86310D7D1866DD280A539583D483B48198GFC2A309E7D3D856BD17D6C7B043267EE502CC785B54FE587D7317A93D125ECCF8FD1B9C457E4084BD99327AB81288768D803FEE04D19B60DF6A508BA39EA7B
	28BF795FC77CB5E870B1D9CD1A323245F66CCA56F4B11D0F44F6C7FEBF5420E365729AEDAB5E104FA00048B5D8B61D5D452C2EEFED6F53E36D5EE3010A296F30179453B74754A65635B9B4CC78856193004FC4B9245D4C832884D8C452855886B0DF54ED2618B722AF71435D82692DD8DBDD8AB046ED33184A1A82FA8AEB63F425GCDG83GC1G51GE9G056B203E82F48278810483A48164D6C3DD86E8817082888108851886D03CBECE57GE0DE0FE57E0B9E2FCCF57968C41DE7BB496F97E2FE670B79D9A4
	72AFD51441CAB10F15CE4A93ED650E0FB964A6DD371BF4E3EE520DA7303218E7D416855A1558F831E02F774A7249964BE71EE87F4FA4361726C74A71AB319C9D6902E56B0E5EAB5C24DB66A65D2A7F85E5417A066386E2D77DD93D51DA2FB76B0D9D70CF1F9827B7907B0BFE6E140973F4D33C997C62B6F0C4FD9C5FEBA0F6EF37DAADDDA77A2D7A7DFA2B354BF85A9EBFC8E575751FB05BDDAA4A72B210B2B71D833F4869AFB5901FF252E4B46AE9A745D944784B29CF539B74BD2074B6D33F514A70D77510F1FF
	37FED0742957764C9008CFB474937F4C4652135AC93DB2DB3AFA5A49D859E295D25693FFE7BCD5A831D1G6048B7C1847C462EC7BFF5EE917A291221FB04486F4CD9DAE32F003B2F492A58407CC27D863B1FE2BADDEC54565D077AD6915F47F43596903B2A867782360738DA125656FE2B55E47C5AE26AB72FE27829EDC45F8E5349FE1258AACEFFEABAAD8790C24DCA87BD2B4438B70E2A63D60BF1F30E2B63AAC55FD352CBF46800FC8EG78EDC05F13E3A271B8AE901D4EB613C1CC836226EC20633FB385925B
	05ECE0F6A77FB7ECA4714B3BB13E9842559B15714F70F8BF88072862AD860C8F07F02CAABE3F876313A0BCCD924F727106A91F663D37DF8F43BDB2C05A4D6B57B453FF94F187453A466285B329EB864958AA2006486CB161B7E5AE040B81EAEB18DC2F9AF11C3C09502DB2A3DDB260B3G0AB63131CD857CFE8978D8CA77BE2773956303B731F4A3B631745AB6933A18DE24AB5A4C7CEE55E6A637E133D42E5102F421000F82C85E4C46D34296A61751D577A1DDBE60CB37B03AFA955D871C2E8170EDA2DD07040E
	59A54A0AF4DEDB183C00ADCC3F64ADCCBFC6B7219F69CAEB19BCED3AADE7112E361649D352B17B3D4569DAEAD93C99FEFDGC2812281D8FA2B8610AED10CCF97692B6A24F25F66F4CDF5EC7C52D9A727E3F2EF9A64799563836A5878F5120CEE6F9052A509F453B469BE9C666531D5BBBD2F2D046ED1CED73A150D25EA613789408CD04E692E32A1DD4CD6B66ECA065FF40002EDD23A23A3C8D7320D0D51EA6037F11BBDBD265F62D124B3ECE37559FB9B2B27D1701B8840684E0C71B917EDCC7F5CFAC6D7DCAF2D
	4F9B85242B2AE763BFE5FDE665B11553B55633788E7157D6AF253BA99369C24478C871B7D124E37D72DB4B082F2073B1E75A4F368F14771D83FF37E143AE495C4B947A7DC4A72F36639CCB8E7D0E2E06F093C067F674AB13E7639C0BFFF7085036A7A15B515C1157F6711BFE3BE46E685039EC56979BADC4E6B24425EF47FEF858AC7D4E2B6B6BB31D6C221F18E29F353CD76AF798EEE56FAC78BD55E569336E040141G76E7EBFA093E649BD4C40B76785122C502F4FE0A4EC9C95C52362E33DD062EA1FD07B807
	B132C31A5F08B9A3D99FBA48EF6E8E164F529D767C7AADFFA71B4D856C6D4E9318EF6B0478A000A9BB887E6AEEB28EA0FDA513F7329265798D5DAD345DF033C3F9C5DFB3B9B5DF5B6525BFD7A54ACB67722C909F7AB54ADBE5A072C81F4668A69A10AE99E80A9A106EF9855D3A9E24EB869ADFCEE76D11535DC83F6F082D2300A60350D15F5955AF751DD5BB5937FAAB7CB23F7DAF53EA78ED278E371C7DB469AD27CC169EFDC7A92939656D672C65F48CD8DEC72337137F17E139FE5EC96C5727EF371C6CACDDF6
	D040B928BB6756E23FF07F1DC24D5871AC2DB9C11FB91DD9661DE27B5AE937E74B6429D986G1637A3E6EEAB20A95A05F97B0DECDC503C0B654D368B73965CCD6AAD0EF1986ECFE6E2B7EBE7D79F53734DBA291E61B3FDB5756C7CF1C0165FAE26DF78AE3B1E75C9D333DA12D0CFB64EA1F2F2F7C3FF399B751850AB9DEB158BB856FA3017FC47680D446DE33D3F6FCB29BE396D17EA6A637B5753D9833B199EC13B6D7AC4024D92385D5E6E45BA1182B4450DB8D7DC30197C52B1AFEF1FFFBFCAE8BB5B7BF6F499
	2CFACB6932A3CC1F776721BEA57541C2F5FA3726BE54AFB60A7E23512E0FD53FA65B870069F313856D938BB4395FE0B97E3ECFDA0E35006FG7079866D773561A3C1B936C49BAE6C4372E4B25A7BD0C6A070A7F099B71A15B2723E853F76AD361F25D6642B871C6DDB64AB68D1720511B84E773A95ED1C8A3812BD28F3F3BF311B212B4F3A9F75AE27965F5659EEBCAD36AD51AB084D2BD1CA4CDA99AB1BBEBA77C5677D7475DB4531FEE923BBA9ECDCF6D01C936DC7BDDBC1C77FBD28FF67D94447GAE278971C5
	6790DF83B8EB93666B920A6FB2F6D93B5AC9FFDD3A0CFD9BCDB1C83F0DC201FE8AC086C07EDE643D6F5CECDE0E7A8E748F4C97CEBE076B9025FBD107A38328C39B4171F49E9BA0FD56C9EA4872BEABD84531D1D90C3C4FDAB9AB1F77D95BCCC6ABFC4516221D3E9CC41931A0AFF79F36194DFB895E887DE23BE11F3E2F5FE0E5741103D8EFEA01F6E09F6A38F4C8E9A716CE05F1A17C36F7F4484B2F6EC49FFCC91F34B20F3811F424AC7D21A1CCFF92241D4253FFD515BE232F9AC67A627DF13A167DE84B940E77
	83DC74FECCE70F0D341FF64B69D52C3EA79A2575DDE80E531581B4B5A37DB91B3C7F349D467E41ABA6CCA81CFB15437E212319758B3E4D767E41771529D9C12FE0FF75108DF5CC841A0283A8F35E08DCC77FDEAD9D9BB97D7691F91F60BD02693681CDG27FBCD116EB413D63A318750065F0EE25DC886DC51C1CC6741516F67C37DE61131C607E91B216BE4F769CEA8FC56A652C731CDB49FC49D2E9F433C7882AE0A6B705E18CE5AC78A58BF1688D29F43526890B09F9940DFF588531E1109F88360028FE15ABD
	195AF539EE9F19136BD34EBDFC37D3C2F9D014994953CE04F48BDAF8BB491A435BA25131F9508C9F47ACFD6350276AB8527B5301861BAF1C2ADAA3EBEC314F3737C97EC7E0EBC7B6BA4E3636A85737A6D0FCC00BF2FDEB96DF5F0A00385243385EB1351F74FB7E901E84908D1084A09C81FFFB8447573F6CA5733ECE5713F6C2AFE4B1369B764BA640F0DD47C2EC552D9F944367ACD27FAD2E1D1EA3F53786E436G1881FCGC2388E9FD8DC6A30FDC0D387E667C35D444E51C7DC2F01A0B0BEBD65CBBA8FBE562F
	ACBBA2DDAB4F79F8D7D63603FEA57F28581F9C3577AB4CA6698344A6ED00378178830482449CC51BB474153932892E5D0946E0F9FA4FC3DB649A4E57960C6FF603E7726E73D05E269E4F646D6E71CCDE20C95906D30F4A5348ED1507995FCFD4FC650AF019020F75ABF376137A2171ED4E3C2F00E312207EDC31BE5637E2FB5FC567D9EC908EG18849083108610F78C46134730EE5D503749D55D22CE2059348DF08DA6D377B665DCB64FE71B59559E84ED7B98DD72B59D53324F6252A2ED7BB443C8E9C7175E50
	416C73419974CF1D4750BE0F51F923A088C7GA4G2481949C875BGB49FC77B2474EDF5E51FBADCACE8B671FFB50C44723A3C57B39BD53B64B39C177331FC2E2573D95E909784908E10GB08D20603BB8DD75F7184F8D560DEE5683B6B2D0E71069793ECACF775A084E622A0D34FC27153F0768BC1C9962FC810281A281128104B6784EE9437C79DA6BDC650FEFE3F0272E5F2A52573DF23C57A5DFFD1BD6DDFFFA59E75AF51DFCCB096BBECBAC52EF1B56B6B62F6B833F01G2CAD64DB31AD0435613A7269BE4F
	72526F12AFD5119726674DC6ADBDF35A63F4056D2867B3E65433229D757C4A6A191E9BDD72F536EB69595E2D252797507AF3BDF777201E21DC4F37D4725CEB83197DCA3EAF956164F6AD3EDF28785C13976DD2DE0626BC63D94F6459D4FCCAF985A73478961E73CCDE050A2FDC91AEBB215556D6641235F9AD3F52FE62043E03FCBC70BA525F2FDCEB2D07B4BB81ECA7F81B63F5447F844E1F0750F1FA8C041381B2CEE05DF9DE253396941C346BAD7A5E814FECE4D471A9EDD4FAD20B2FE450B3F92BD5FCCAF935
	1A721ED471A9C1DBDE02CB3E96CDF9DEC31E493B5AA51FD9D35E06E14F64B5386473511477F78F65E538648BBA295526BE5DCA5A3AC61B5A575E51E54281ADEBD337CFD736A9B2DF9E8E69461C44B6B55704EBD249A7F1CC37162E0D95F5403886208940G6087908A905B01E301A42B4BB11DE4AC60FACC97E1734C16CF3964CB691073313CBF5D0B63FDC10F7D4615A31E699060122FDA2F2543CA6A1FC99ACDDC0766D14F7A0493AA3EF2E59D56EB713DCB790294F14E0071CD9C734CD6BFF649972629679941
	B33B8C287814F6095114171769193C4FD5FCCAF9491A721E74D0DE02CBF9998AF9ECBFEFBADD0FAEB89527AB85E038491D845788610ED3D89FAF267BE8FDA09C8C908E908310821041E92E25B4E47D3E78B4735DE4ED1E4C27561EC61AFC3A7F1524C3F488BE252EEFB192DC06647FF08DDAA924C862192C95D4D636081B2E41A35DF3BBDB105B5419AC691EDB568F7E395F713E39577B35774D3D552F3DEF6E5D7E0F999E52106EB647784C334E7755694EBA5ED7377F1CE3BD0F1C5356336D1C361E274EB95753
	F84E111E73E839BC1FC546ED16FE23352BC7CF13E67ECDBFC076FF02675857EFE4EE05245E271C1F93703B551AC966745BCE23CF1ACE570A14BEE9FBC71755E441C41A756D16BD27CEF107C4D658D06F0C8147B64A9E543611EED05BC6F3861D5B6833C167B67A6D27F3A8FFDF1F3E0FCE9A5437DB5B45383FD849FED655FE48EF415B8A389690EF5209B45B2D26EDD53CEDDC7715D67FEB839B7A818483041F1637B7712C4310E35B9C9D5236CD7B90DD83B475DBCE6A15796F9CAA64F176C141CE2B3E07E4C35C
	C7CA792D6D1653FA157D86A8BF0BF344FF5110A39BB39B5DE4C69BB5D9CDCD2B380D323B1D792818536A7E0849FBFA58310D620635EDB4E558310DDE99F6ED2314E147B67A4730EB9BCD9BA6FD5A092E0EAE0B381A44EC535EAB29BF5FD471B4EBC79C67716B916DBCFEB362B80F4DA32E73F8EC44F19EBBC6DC6731E744F9BDD8ED79906B41A90B29E75BAAB16D48D147F97D4D28F6DE1F99F51C5704D157F91DBA6AB82F49232E737A7A28733CA6D824F51E74172C6FBD3D5D41777A7E13961341A0FE2F33F16B
	F18B0EDB13CE6337F8895D0BD15899272BGE868C43F7F0953F92336CEF5DBE2FA5E56A75533124B0EB362385D5A0932DF257B21C2A19C851048E547B91D93CFF7A83B43AA15DD45E5CF6C46FC67F6216C6F685E2EDA8837G183BD0F6D30FB359BEDD0EE4AF6F17765B763D5AEF48702BB97E6E33D2FCA5479FBEA7ED3FEB847CAE7C641C56F7E1A068B3298B3F8B6F6B47FCC673FCAE2279944E44698A81AA4E7072F51A4F06B30E56E40B685E3956B3EC6DF5A64717D23CD502E7EB327753399E9276053834B3
	B8E6FD1D6EA5A17BBC1B3BF11D79268129CD56F21B9C9D146257F37C3F2478EF6C36FDF8C80A375B36F7D80A371751E21B2C9F810F426C7942F434E7E7B75A73A53A4FAD884261G315DE84FC255BA9735D9B733E3C637A35FD3ABB8691785877D326024DF965C681785A77D3260C63FACE87B1E3794F6DCAD7A65D83AAE12EF105B20C48CD743EF23A2CE5CC3B4A2CF2FD2CC2F0A768FED8A9E2B9876B1B8EA2393C625653FCE40EF2A7CB1D24E014097EA403A98BBA6252D66E95CA148FDCE25085F16A92D236B
	6CF5D406DF43719F1EA6E7DA8C6D0326FE2BA0147590BD961E46B69E89FACC8190GDF50435ABA6B6FFFFA0E48BFE5E96F01EE1E8E02F30DF46FD0A772DA015E87A084A09AA02587735471DF32270AED9E342F9332382A2D44363B9A5BCF584F12D467D39C5DB002795CD1CB6A4EFE3A5F433E964B629E7942CE0FE7CEEE7F74D3CE4F76E4112A4C64605911B7B325FBFA1A0CAC3FD6A35F5FE7540A77B1627743C0A651097BCEF1BC4D4A761E2FE85982ED28917B1EB80A8BA769F35C7CF27C06CE6538ED347D57
	1862F4CDA644FD5E4D3F35CD5876CFBADC578A831AE8133CBEA6C21875198154772619D81FC96C4570F7D23CE03663D9DF3A1C6F1B4C07B82B99F5AA3322CED34C28538BC6C7BAE5GCDFEEF1C2E24572ED7F5AF6AF40076EF0D3DCA1D3E237856DE25CEFFB121CE86080B6DC51D7EEAC61D2AAC2853ED8E4767ADC0E330486D64E5C11D62298F7337A8F51ACA71419625CEFA3EEFBB8C628A7BD02753BD28132D8FFD66B767F0AEB5288F7B78C83ADFF48A0453GF22D287F4FE4EBC645002F325AF5EE30BA7219
	B7D1FDDA2DCA7D6F20F803D5297FB22E3F8D62122C3C1C397E757D2853C26AF77DA19C861040716594CF764112BD34E42FAE43071DE9413A596FF8FEA6C191676F04D68ACC46B3DC06569C0E14D659C73BA3A367ACBD03B1C0ECC16C164E731CB8DDDABE8DC057FA562E37792C237259C377E0FB1FD50EE90ED2FC60D9651866D5B30EE942A02E709C164FCABAAFEF0330AF47791B304C82B86EAFF48EBB8542999C370A0E4D738762F4458308E363F542DB46F19751F1340D04B9EEB7EDEB41908E67B8FF0A0B01
	F0A2473D4B5BE44AG5F5BB7067BAB19DDB7D1DFF885C075A06EB95CF5BACF34F583603C8651972DEA136DC1A1BBA8B7EC785A525531357DF4A375EC2C0DDDF79A4BAAFC1037B11AE69A04F307A04FC3F846E3E0891E71887D511DC273EE6DB3B4C94DA3D955C32CBCDB076CFB4A864ECC4DE2B2BF6AC21981909FBBC4F0B5DDD2BFC2725F1E4566DDD93CD196CF3E47C8415B6303E471C45F67F22471F132F8E2232FC4FE16372797E35EB21E1EA4989E74F6F837CC6130F8FEC57C75CB1E18597C6321EC715C52
	A987671628D7E579DF16057D1AF5182F579B9197BA0C655F1AC5776142E0A0F9185C417384A191FD77DF3F22F7D9105D57C6EB1DC55F8EC38732C3955BE0F6B95A3854865FD0B602DBDBDEAF66214626EEEF534534911A000640A7E2BA9DB65656B2398EC726CB7746B2393D53F9FD62F2D7F5235C988D39189E4ACF1248A538F409DC56D606E85B93C620FD0FE07B093237C70ECB2638A688F70CF0BFC74FBB18F9B804064979CBF1ED3E2FC5D017797982C95FF19F3C730248F054A733B167B6AB0EB9FDC017C0
	008800C4008CEE1F3F53BAD7B48A3EF294F1553CCFEC6038960AB3C0580B636E20B8FF88070C226D0E5233C1E1BC2C27E1F2E61505DFB7DD08FCA390B9EA5B4EB0DFC899CA9BB35B8A3DE85B9850A189C09803FE9220F68C6D73A09D0B36C1F800637E4D47273E9CB75FC0F013A09CB506F67ABDBD43964B43FF2461B6EE47EE0323BC921E6F1BE7A6435B218CF747824E0059F134876D188AFA8BDEE0C300DA00B6AF4C6BBE553E93AFAFFB9E7D3D9C0D9B2A68F91D90AF6538EEB34507FBA947F5CF7273CB5190
	D7F691164FEF693C0D9F0403B9AE046E5B0C06F0A247ADB5E0598A97236E3752F2EB62619F53B0592B4542AB9D5A980170FE3FF2ECF0A9637B14A76B5F3775E07FEE5050977B6E1922ACAF20713F18756FA49CF231522F6CEFA467E93719FA4C26FEE307F436D41C37EBA53EEF3BF1E830C7AF1FCBE5713F7A025C9950E4B1D975DDC699810B7FE200482A905077871D136E152F1EF531661D938442447CC50B3F991F5C1C494A7FAD6A7704CB401FDF02F562BDBA262B06F0A3478DF2BFE53E846B4181BA160D04
	F092C00EB7625BA9BEDDBCB7CE4EFAB37CDF1C76F104DF9AA6E9FEA4266FFE3D91FA1D49D0FE9F6037043BF5C91C2FB04947B71D64C834D8276EB5E11DAA75561EB3427AC22086E88860D82549DBD927BE2063FABCBB4746AA1FD8F02C526A0DE32E860BDC27899F5AF56A3754BB480F341EBB52492C52A940289EBFBDE044BDF9B62E538933DA77C13E8F5517535DEB166B9E1FE757BD3F97F58F543067F4973ACFD2695EBC8643F3E1EDB53F0B6B34BB7B8355756C3807FCB71DB79F6B7F2F6BA4C60910640761
	EF2178E889DE748B7C7BAA81622AAF45BDDD8F4DA63EAB904261G31GA9G39174569CAAE4339AFA10B74B1CE77F3B51B4C0D7A33FA03E4B91B65EFEE5779660F75E50F4D446F1830DF7CC4883F2C5E61B97C6A4BD8BDE891FFCB7FB8B1137C327C6D1CC37728C0DCB0C0B4C092C0BAC05E657C7CCF16F87E07EC872833DA5BCFF6529B8E786602AE7DB9BB1ACC080A739FD9DAF9AB3E1C69D1F5393AFE0AF5CCF35E2CC124EF5560E3B6F9762834FF4F3F591005E52C24B70BE958AE37FF37327A7EF9B9E9A327
	5A8DFD76B53B5365784DB689686733B297289F907FD8BB245B92C4DDDE4D4372A93A77AE217E057A2CC58EF4C991F52876316BD250D31C4D7471192E5667C4B76AE3765152A7ED8B53670F44BD0A6D78DA8B69F42D6DA7360B5D10588F5DE3417EA584528A77216BAB32FD7C5AFD7A7B148F52EB6E329A74CC5E0C823B3C3768F9DF744D6410G533D4B4A6F72388267846E30AA6784BE11FD7347DB15F382440E7678697D4AB901CA591C423AFE651CG72B3DD271B34EDB34F0436293E82ED3347645AB6A6133A
	3F3F0C2755F085766361A6358DB7FFE23721F10034E9F17CC34FD9B09BAE69C59B36F09B9E6FF5EEC33FDE67B67CF52FF39B2677BA37E1CC37368D133BB15FFE5C061F9ADC5BF02DC1ED1B76CF6D36B1D8493C8D9FBBD26BE07B886EC1FF9F446D935723344F9B327C67981D5BE72DD1E91FBA19FDBB0D0E6C434603DDF40EFB8A6813FA850E91D386F00C18FFA50E79B67453EFC09E2E27E1F2C7G8B47D81D0D57885F7978926D7E77CFCEE5FC5F6FC2D6FEC97DE46D6D0449C297BE595A5BB13E3EEE05FC7882
	048344GA4DF09ED6651B36AB53B674FA0DF6E8436E6D78C3FD593585D8AECDC345900632246890E3FDDF0DC5486B456894C7F11304F8465386863CD04EF7F37DBE507B2D87D3376E07B741F205DBFBB6E33EFA6472CC45F7CF5A11DBF06B4D2A620AF1E25A377A89EECBF235E2187C868EF4DFA0BF510E1698DEA223E33E6286D3DE086371BAF33F7B17CD681B47A223DBF6ED55B3B144F0B18FD193DFD60B750176DDBE5767663E368B0DFEDFB637D1474DB9CE8928150C72679AA6DBDF79B3157B703F4D4A55B
	34AF2E894FA79DE71D41C0897A645B7AF1DF4CCC520158EFEB15933132CB647B7AB3FC1D797AF0D2C4E23AE596290FE671EDDF58631FB452713A25DF4F42FEE68CB319F53CBF2F3A8A6ABA653F04CC6B639E79CE7B7FF7C87E0FB9FDA167409C748563AA035569C7E7BB5EFF7164EC6D7D97B13335770E464DF63E4F7745590E77793EBADBFAFFA6EB037B6711FE5DG656283908A908910F09536C753C7641E92F68AD7B9AE5E5EF11A6C7FA037DBDBD17E3EB90E73FAF80E6BBB31CF4CA1FE7604896AEC8FC91F
	6F891DC34A4BEAB2D32438C7E48E3111256BF427151162BD89F368BC2F7E94C5B3DF34BD8B5768532EC27F7413AC74CF999CF71AFE37B6DE9D27EB3D1A2FF399112E73EA7CEEB21363B6387FB71F5CC72B59E70115F6351FED5FF3628CFC73B6371F66754ED0AE7D868807F4E32EF66E5B185CDB6613FEA789E8B3G0A2F01EFB900C6008E0081GDF00A000F000D800E400E9GF9FEF13AA2008A00FABFAC5FDFFD3C4C116E0094975AE8A1DB5A3BC8B1637D3C86297EAD101E414F397E2CEF0834E05F403EA322
	0FC9572B0AD76C51DC2F1AF6ECA84BCBCCBF584F7E9DA140F704F84ECE3617F08A50243A25CF15C95EF75D4A4362F98159F9EB615AB8DD41356E247BFE37BC1DFB1C6E05F31C0E57B935FF3A090FA3CA2FE57E2996FE1B2FE567AB18FF7A77G1F4B54509767B179DCA65078DFAB194B3CD6691FA69E326F3BD9A54EA9DCFA0E6F01G7A54EBF14D7678G71AB0ABB7E68B837E3ABF550DB756DC6519FBD436FFBAA398E57ED5B69FDCF2D9036827882307D0985034A7D094190978D10F89D2EA3FFF08667B95281
	D7F6BD2ECB6E6846B1F34775A847CA77AF84C2B88CA006531A8C7C6C5B7567BB3E09A0479444B6B2A8AB73A2FFE8FB7E4E6B8EEB130D0564FF5E4733E18E1E55A8A47DB53F0307DE4F389F880D27D97EF74E40B22987D9B6FF4C5335B37839F4FF4C7F621904B69542B9B7C05FF0835AF94ACC251D2BA02E8540F0835A3929976D6C8D38089BD0CEC0AF5FB3F1834AF91252165D8875932075C624FD4C42570FEFF4574EB87EF8E5215D4E2F4A76C506C2DA11B7BA37B3635BA1EB5B8940136202CFBC53B04F310F
	BBB14F350F6B1EC76F51845FC4EEB2E2B873BC524ECD27CE756939AF6D1F174FF1BB7B1ACCFDDD2434D97E1FADA179E7ED7D31ACEC6B999A7AAB57AD1A946B5279B7A95B7A28D1BE7F7A66E23B2CFB0DA82B68A6EDD9BA092C8A20A100732F75B7A95740AF21F3CFCDA21E61FCA82ED344B1DBBCB05F314D9F1A6F5A660F4E579ED7BCB1DFBD2EF8EA3E7638A2F63EF4DC41F04F4DA7B6BC8DFE3205533D48F107B96E951AA6419D6A6A30F6F27C1F28DE845F20A782917F9A556B9C45350838B4896E3008FBFBBE
	29B71065CE1385F95F1BCF4A6FD41741A05E79C4E93A0CD00D38BD320B090EDD66F6095E399CF71817C515C1DDBE6B8D5272E1631218A5244F7086DC90C094C0A2C09AC0EEG0EB5820ACB8547E38D361B47BE54E032AE3522FCB2DF43F0D7DAD1BE1983E2382D3DEA3A2FFB25F42C5FFA749C76DBC520D7F3G76C5B379DEAF5FG6C3756D0BF948561C400F4CEDBB1087DD65E4473758FFAE282317F196072AE027A09DAFB83A63A64D35E7BA01EF9F06B6E8304360938B772B9EA876BC465DA434B946FB5D139
	56F0FAGED6C8BF1E993512F2CD4796F661BE14C8A60FBB35AB636877743875F0C6570D697C9AF914269GF93760DCD10E5333D80E7B7D7FF848377B3C79181DD268B8236496257D5E20780ADB14769B6AC67B55C0DC904F2FD5B6664E890C539586BA77D72C9D7875285B708DBD52B64C525FB343337AF84CA51FF6FD3C51A51F944A8231BECE2463165AC025BD6F247846C025BDD74CC4FB36C0DCC4A0564737D4631C62DB63F48DG2D37E2FD6C6E437AE83D956B63BC3AB7B39442D1G0937E2799CF4F90714
	F63D6A70106F567366E3F65AC06F5FCD3FD5E93FED94AF5C2634DF8C9F6B65C11C79B64C6F1332BD698900CF394D1D7AB84D242E0F7FB2C96BA3439D6BD5531D68155231B2FA136FC34C388D6717FC48E7AD4EF971BD09D537BB1B731AC4269E457DFB6DCBC81E45F1B6F5598CDFB4B05B0EC717AD7A4F6716B89E23A4AEF1BDC67953926DB14AEBCB54E31434A55AE314F716C847A82C1C1EB82FFE5FDE3F62BD644BD35DF963DE3D3C077A8D436D4AFA79B30A9F38DDD9AFB7F2BF638DF1A93773373BE4F752B4
	8645695A021C57CBF6F6BAF2AEABDBC317D13F0D3D40466B5DDF69F72A95527189C2B9AF11EB0B45BCA7CB7E6F99987FF94B4B46D5866B9FF7297C35BF64BD82A0B6887B475B78DAEDCA90760F9F50338545F744692A811A6EC0FBBDA77B9E6E843C6D8EFB39785D21AEA3D67E2769B7FD709D4ABDF03D949FA64133FA71EE97FE83C7C1DC691DA87F6F5D76B27A2764FF2F6E71AF2F0963AA035551899AE3F5BF033ADF3D0F4E857802DD0238EDEE1FE937C708647FD5B3475FB63B46D586FB1B7523DC7ECE4D1D
	6CED56A8781DF2A72BF3E9F7E2FDB6F2DF12FB9756671A69F43C816196G73DDE8B3336CFEBE9F40875EE52F43136EF2D41F2F256307283B14E7C1EE2578C4891E5567C57CACC8BA51A1986557DA6C6534D372FF1C53BDEF97264CDE9BD7992C1ECE35286B739FAC6AF16BBFE57BF0A240BE31416E4C7F3CAA5B07138CBC535C62FBC0525707CB7E2F7B817A7DA363AA035938F1D0ED773D03D23B33F9A2E1004E5D1109DD8147D0714424E21B5373B70B726EE6ED2E84FE2B6FE6B6EE3A9B5B5C6F7979384E3B31
	4D7D185EEB9F8461F000583B314E5F257A86CB39DBF2B7505D0E5A1C053E1F127FA3258F31D1FC710F14BEA4073F37D281F17EBF423520D47A5DB48D42F9BFC65CA3F4FCD98161FA0E5B404FC1F57E98F56FB9A35FC7FD0E0643F97C3F3B46FF9FF56199D7B23E4F7E87D6AF22E6286B45BB6429C551366BF2F15C1688F9AF8841FAB0160BF3540DA1D88F22A82DB704838142B8FEE6854147C0B899A0A384ED58275A13D7F00F5DE62577B82A9FE1340F2F39C7F98F7C63945FA64133BCEC25F89276063834FB30
	5CAF275F0B2DBF81BF7193449D2473D57E908E6138677A31FE447C043FBDEE565EBFFD9745975D9B272B81E83D97714BFE007D53DB1D4A783E7B2719ADBE4AD677B7F333556316E9C60D6FC623140E154D5503D83FECE02B30FB31FEBDBF0875AB75DE2CC7DF50FE26A494EC8B509C0A78A07AD60481425EG0121E87317D55FC4E121F6FBC607BA2ADF73693B9B09214A7AD5CE7142FD4A7AF55F90562F9208733B8F6B4DAF68F8BB83427977A3EEBDBF33DBF5BF6AB950A35FE7F5B1FBA70A07E7750C7FBE2BC5
	CEE5DC18FDD659C6753E1F4F797D47384F4A7BFE65BE2BB823FADD752F1CAF60FE362EBA89FE236EE7FCEC7DE32F9957BF926FBF7FFDD6E977CB3EED69B7D6026C9B0B3C17C3E4E5GDD7E83D8D61BF2BD1C2F74104F5BA1DF69835A750255659A9D6DC39FD056715D945F7600320EBF154B7BD008CBFBG67778E2D226B3F13146F3C8FD0FC34844F5219CDFD8189D77DB4CE9779B3EC97D7327D9DE1707D9F06B8DD8E36956FB07E6E73F45777987E165204GCFB4C08AC05E03EC9FD95903C853B03DCD78216E
	B1BCC9E5E19B9A2FFB8C45FDE5FD6AB676E01FF2EFDD63034A3DF5BDD6F59B736667B95B9EE4EDECGFEFDC5DB32B6165A0FEDAC78C14767EC9D6D2D8BFF505E46585D9E41AD4A77FC082C2887716E0FE76B346227707857463462D3F9397B3B3C83D93B1D5D69A15FCB8E7904071C354F6B29CFA9F8C859BE6F24786A07146DF38177C12D90977D90364FB47A7ED155434A7679B1FD13A868E1E57B7C5188364F3C70B8DD407FE0DBFC1B0E7BCB9E01EF02C790E79D4476597A885AF771306B76D9C5E93C00A768
	9136AFA492FE13GF2A2106667C3ED428F55BEFFC7E57DB06DF3F2373A1D25703B75CBA2D8BB2B015F2688E69B564E1A78F9474E08735FA3E40B3037B3B63E5A474FFC79C460BE110FCDB8960B845C34889C0B9DB661FAEB45AFF14CF5B9256D043097C040AF1176E2B3BF1F72CB1C5F7FAFF94EDD141BB704F2D3F8DAB368DAC6712F20EE81B47D8A6B57AD749B2339126DE9914738BAE2AB21AACED78CD08D5084E0G7029C21A3D140670F4C2DABEGA1G51GA93FC21A585CB62DB6A926672AED23EC723F0F
	78EB50609367DFB4E5E5AB6A15A3403D49523A7721861D383E370A48A2E318B982BBBBC056CFC8BDA01B39497ABFA98CB207CE6EC1A37DF09D40B60079E23AB3C53D08AC32C10ACC3E9E92D81F3094E00C4B9A2532F29F0D53D5G34GF88104G4482CC83A80E0C53B5G188182GA281D2G729F837B81348178GCC821882A07CBACED78A5084B0G9074EBAC2F3995DE196A7C6AC41DE7BB496F97E2FE670B79D9A472AFD558EF251847CAA7E50136F2277FBF64A6DD371BF4E3EE520DA73032B82BAA8B34AB
	31B1590004F5D6D9BE62DC94AD0F25E299216D256911F27CCAACC7C73AE0593A23778AB7691639C9376AFFC1D908E78D32547EA4BC8B7DC9742F19BFC9025F74DF33FE145D03D01C03B4F90FC3BB852885E88370FA9C47D2AF6FD23E55CB7803F87C47B314773011783047F14DBE6C00565E44E808CF64E9542FD63EF5CCE852F97CFFACDAB2F223505F3C5E077E24A88A7DD828957DD8B360FCB9ED3A957DD8D4947A1E0574EE068CC27384FC9FBC0134734E229F2BFF827DD88DB9DAAB7A3117DAF2457AED63BC
	B336A2CF9847AD94F0CFC26A9368575E9CC43FC6765408EBCEC32E7DDA8825A1BCA5CF46696A81BAG3C81021FC41A25F4AC232CAB63635736EA4A9A9F3F764EA811754378354F46082CA830E9AAC041EFE0BC865081608B9086108810938D758520994086908C9083108ED074DBA89FG8300BFC084C0B2C05ED3D0FF81DA1F42728A9D797FFE6D7FFEDF4A4AE24E687F77EB7F774B0259B2EFC06D57968D20BF71FE0A7935C0781D74946BC719DF1B40676822811F881081D074B434612751272C30A97D9A61EF
	657136E1254FA171E69EBFF5CC6933C83C775368770A96E86D1D8C0078D01E4615BDDAB2221EC69F16EFC09FB685F079B168CFDA8C68439AE25007DDC6E93DA09CGB00953DE444FFEC647208F1BC00AD82C6F079C5E892A8542B358E7FD26F937316B763558C33E63674D4766B85E275F28854FA877A86550B51AE2891E597CAA7E7DDB86F1FEBC3F174B6E9FCF87FC6E6F5CD9FB7E58697BD7C59AE9B07D066877F0456F14FB8EAE2378065FA9779C64D9F04FC19B44C57D8E6B58C41D340E05ECE0FBA6497F8D
	9BC97C93BC3E74D9987FBD2B0CDFD20E71CD9057290AFF1A478FC01C1FAA3EEBA54687C1DC38A4BE200644BF398A571C231FE5F334C9A20DD78D232B5DC4685AF6A1DDF1ACFCDF064198A516FD57B742AF592F9ACB69DA76A3DD2B98EF0DE5697A4632745AB6933AE54DC897944B3EDB493D63F4BF69E6295C27B9DDB850458224453239043C49CCAE233B339A690AGDFB51951C52B68BA57A3DDB3608DA2DD0A040E5925FC835279CEE67202A7B37D1237C86D77B627CB1B4C64E9535D339169F26318BCA59D33
	5F319AEE67B896DF8D3FCDGD6GBFG165EAD1B10AED80C0F90692B6A24F23F5B0CF499F16C7B3F33CECE4764E6EEC13A02F896DF964F66EEA649689E61F475A2DD2BA6DDFFAD52D99D246735155603BAEE67F8B697919C4F56ED4360371C53FDF088692262593CC5A27C12B55ED4995D76B11E5FF8B647D110405600B1BD265F5AD124ABC9E07539A601555328EDB016047F995DB0CF4FAF01699FA252C5A6C86BF32300F48989EC7EC4D91FD9F97C1D5325A630781C67582F2DDECA9712496B3398DFA57EA6A2
	9D6DF71232303FEC02F81F67506F14E6230F0AFC8EFD5441ECC2BB8D42794F030E4F7333FEB9C8DB7BBC5E0FE14DF1FEAF6B0FE4716AFBD92748625577324E1A6E68DED62653A73B711BF2006B59B0034FEBF2DC6A813E1F72F94C67843AC6DC7C82549300269710F6F8B5FEAFF6F25CCEFE26AE688574B5EF3AFCC7D68AD3B85F4FDC3E6B2A6D23A2BD64FB773C79188F3B11FEEF273E20744937D37C349714BE39349F6BD84E94E883D378BD573277D3E3G1FB4459D1F1C54C278529534393FE761A2782D783D
	5639098F37EA6537DE24ED793D23EF6425745DBA03A25ECB8C7BEB70B15F1DB624745D7EC1D25FDA444728A110C6646F15710F7278D808CBD145F770B170B4084BFFD1991FB502714590D7AD09977BF65EB7B53C4856EF5AC49A396F3EEF187BDA08A7EBBE61AF32390B1897253E7B7EF3C817A846270B69663DA46D6B4686380FFA09FD1B133E0E48157B6E85FCBFDBB550B58118DFE273A53EAFC9FD32F5846982819FA652F92B68B670BEBB817029A2DD30040E5965329C3E0FAE1149ABC9E47A49FD72B75311
	4E1C4864E953E54EC0BA1FC4A6CFC9476C77A0278B92634361176CD70B055F54C4745DEF4DC4BAE1AA0BAF1C4A68653E7BE13E8737E5AA1B63A03EDBCA4764D6FD4973A12663B3154DCF49FD725FB9DD10C8972EC997B29F69E29D2447FCEDEBB15225CCE573AD424B008728F8D96A13F596242BFD194D4D3440EFA7000F0C6E85CE9770B21B4719843F11127418FEF774A1DD524B2CBECFFB1955D3623B2BDFC15F7DE7AB52B53E42746FF8055159DE115667077B390FFF054D91A96BB3AB0F43E711AE6495969F
	A97E4AFD77DC5E0E260871E9622F5CF76F6667B17212203DA521CF3A1E1F2D34A521CFFB0C5206C1B886A019537EF490E9B31250F73FB2685CF7FFBC645CF7EF9BF26E3B7B071C7B6E93B6745DD52F221EBF9FC6BFEB603882817DF1B047B5AF40395A44D7B16FFF237E3C608FB00684287F83BF8F4F3F2DDB7F00BE69D303E73E3444C33EE3674D47FC6DFB6C7D0CBFA8FDF0B65DC79B7487258F7665F3912190177FC74C6FE532B3A2D6407B7C511DB3E5C6A31E4F8964E9E51A851E2FD9127F2F3F2077F7EB5B
	B1F4DCE530BC5FE6D66F073C4B2C5EB7D9BA04FBAAC238ED3C68773D7ACD8FDF0A1F86F4C549707D1E0C787FD8467FCD0F95DC46F83C69416ADD2A8E77F5F7C05E8281266C205FED33487EC8B28740683EA9C73A8420497B9352DDD9CE6852B85D1B1C2E9EE8EC1CEE65CAB91D5FCA248B831AD4CE770C026E9FF40F9B49FF491F61BB6F4F38172AEC97F9FB2C1DDE6DD54EFE1A7416D3A6CB0F3E2394DF46A827E78F4B6BE8B4FD99F499667B0ADDA85F9F520C6169EE19C56AC60769E4BF395A0961366E2777BF
	F4F675B15E0B76A3EF4EDF603B72AF481B32DFA761AD97F01E75BF4D240C70FEC816463EE64C179578C37F0279CDEF46341381D7140273CCBB8FC87B645AEFE03CGF183A9A8BB281C6059DD4210D9FAAB9A4A772D166B7FF4B54A70857EA8AE6329CA250C8C08AB7EAB6AF66DFA642B821C612F4857D021648B0638E84EF7E88366B591F085FFC31DFF352196FE8DDDFD567D28F779D9F2394B364EF663E9319C4517B64422EC14923392E5B23E5C08B29AA1FD6F3F216CB80E0FGDCBA47DFD603783257E05C70
	9A6A74FA0D641CEB57103EF4991BEF9EBD205C179A88BC11G891C7733CD33857BFD4EBB0D9DFAF2980679320D1B78DE6B57D00728CD3CEE27C25DCE45F4D6ECA6F57BA4B5C2F91F355D6A44A0ACC6DE37FDB6E33A7E10E6CCAA4ADB3E05575B57215E3E0E722E5D224CB323CF6674D6208D65743BEB3569EF2B65F597E80B7E0EF27755A13E99F03EFF47F47E278E5F41915B4FA1F9BDDD5A424FAD82CF86673B3AC54E37F2CC4EE79C6375749FD0CF7F01FC4F71F7F7985DF723E827E020C964F4CF0EFA5A3730
	321EBE06E5DD1006B68866BAB5824EBB8DE5FDC847C73C5D72BEE30D206EB37A85DE1701BF1D277DD9A62F4BEFC0DDFE83539E4D54AE27491CBE90E893B8FDF5963DAB0F3E9BC764125BF87AC4DF74E74D3B1DDD7B30F48F793E4A76EC5C75941D834BFBD3B96E0A277842B715632E56EC9CF715C01C4F1B685FF767F82677673B1D71253E69E88E639B4D3B9F043798FD41DB0E785EB82064AB155056E870B1FB7C1B0E391B5ED21E7F5259685EF489DE6C7B07317E1AA1AE61ADBEEEDA2D25F7F5FA1C2E91208D
	401A2EB557732D667BFDBEA2EDE03ADAEFF13F3A954F6ACECAC79D827B851E6E5D127F4B7B47FFDC7B75384A904F331CD50FEBB71FD50FEB7FC9774BC400DDE253F19FCC5DE102CBCE173EF1E6BC0C635FF4CE17B0CAF075EF43377B5B08DBC8775A98A0EC6338BDDB11570763B6536F3CA990CE65384B59FD926F407F6FA0AECCC05E22F7306F5A466B1581F0416F60B850EB181E85FC91697E4C69920126601FC83748C65FFDFA8969A658F8BF8BB45E1CAEDAC137EC8469A200A61D531D9A1153790C713EF59A
	742D53302F9C9C7EFE634F67F9FE82A14D841E6E0D436A71E740B939FFFB015FB31877AEFCAF3F0B3C1B8734471F1D83EADF92B200793281FF583B7C5D39C1CCBB95F0A56F61986F76C165980FFC8B353E0732575B88DEFB7CF9FCC42E7F2D5C76E6608F61B2BA8614B248B7D752FB285B49D164CB87DC597B4877932B120FFC2FF53C0FFC8F64E0DE87GB769FD547953693542059AFF1E1D0EB2D2A07D628CBE661A0178964079F17C4B9C9F8DB861DF28537619CA1F4D7026190E4716574F4471C6753FB07D22
	DCCC5F8C38901E7EA5395FEFEC194453CD02B48BBFC0F9473E4432E9821C4F87A8AF6ACB6DB148E77311BE92E827F17A51798E46AC1C3E625FF13A4EFF235CCB0B919F8438A97F46F4D20BF12C476AC85682651995F296824685A27DDB3D72BA3A5A026916GCDAB271BE81127FB17CDABDDFFCE7F18C51E2E405F138D811AA4CEF73ACF1E6E3F863552AD4C0C97737DD49F6F17G571109692C32FE3F312B590AF5A9104B1A534F3F7D81171705324E76EB0FDD037A557DCDD2BF6ADB8F7C36ACCC7B6233088F83
	DCAACF7B2FE7356BC14DB97E4D148D65120D74F71DF3BCF65DFB5E776FB03E6FBC648B70703E1FB5346F7507BC49472E1BA9BEC802E736788D1FDF8E05387C1CF8F1BC7410076728CE5B1C71D9A1FDED3E70E14D311CC89F68106F3F02965FA4200F821882102A60E5FC0797C8790469713A0269F63A5269EAF94C0E47695820E6FA3CE24F4F990AEF166019FD5306F14F0F8162E22723FD1F543C3B38EA86135BB043D1FE059E017F2F137CFF1D4B375C3E8FB099770E2B8CB63E3BD563DE3DBBE5776A317DF177
	13AECA3CBFF54B901E39EA55309B56F7FE66CA71461CF906242D50F91AC7E5EF5934363175B79B507981B01A0A33DAB4A1909F41E97EE7D00BA601444F40FA70260E540368A36E1F8D976B41AC4217F45EFC7B4BBD1357DE6E193CE99E4A1B3152B3F9B72E74CCDE3007725EDE25EC4729C795FB96DA353EDD33D55F3C650A30B0D32B5F3AE6172BFED9DB5E2DAE798AB565DD3E5FB3F9B738642B501457526C19BDCF29781476EC5014771E0A4F3D7C652A66CC14502610477A04138714FD825917B7G343EGEC
	7CB4E79D0E1F02E7E2BFB1395AB3BDDFF149172829676FD6E969198E34393928E7E6956AD91C0BFA5E345EB3BD6FF64957122B2567CC1D161EE6207566FA0ED57071A8577358864F6AD9370ACFF98FEBC22ED6BDAB5968193C55AABE253CD4CDF9D3BC1477E71772F2F4DA72FCEBBC13F7330A2FDC91AE52E9157BC45519B5F2FFC49550B668305CD378B7ED9B6058BE01EF6937E7G04A781C46B30BE54EEF2E73C1BA4510571BD2562F3AF5FD3D4FC4AFC27EB5A796A4D1E498BD471A965654D5212D731453376
	3C59A5DF09263C3FF9A8EF1ACB3E9ACDF941351E497B19CB3EE6CDF9ADF51E49BB65124F20A96F2B964F64EDF449672DA94FBA66193C0BDD7285EA4A530FFA569E7AD4FC650AF018263CD9D44F18D96A6F8CC74078FE6221DD42DD72A5E96AB92C7AAEF64FAED7287814F61926A9EFDE26E772D62878147272BF5412770207727E64D2DE29263CD862C6447FFFAF797FC47678FFF78F0F2B8C760D3DEAB72E2B54C27E992E036E257703F09047DDB58369C2B9AE77A03DBF8D4279F9083B7C903FBFAD8FFD6CF41B
	7C7E34B9B4CC76C008E34761713FBFED12D3996E6EFBF2EE4B1BD6235DC20D38EE95C5F15EE00BE0EE230FAB51C6619C973B1360F2E7476B0AE6A3CECF4F9FD4C0587EFE5AF57C2D78DA4033B91149646830B8FF7A91DD1F133F8B3E78B06E83751B0DF31EAD0771DD9AB617F269616F730EF970EC7BDC8ACB7F35D169BCAE53A30B6FED184475D83FD57EBE0E3006640569F4EAAB6A94B9DB5D6FE63B50A9C1255381C1ED9B0300BAA5F11DEE9364BAADDAEB5769E981F5CA535049551B69B99F59F5E275F26730
	E773960706BD1B37C875D05EFB9E4A3B5666193C5BED1E49DBB962193CCDA31E493BDA65275D9C172B784A95617C0F347CD12266FC39EBF9FFF349D72AA9EF2A4BF78B34653D66122FD6D35E7D2A758F776C992162D35A33C5D3DE134B758EEDF947DD721995725837692B437CDE2F0F50BF5EB562198EBFF54997292943AD832843942E43BFDC6E19525661DFAE797267E85A012F95164EC19D9ED46D1BF22F6C9FD771A967AE5A66E8153DED3AE7722ED471A9658DE84A2BD06D53F2CF5EE6951FD21E1F263CE4
	8F653D2962ABD704C3B465854CF4E7AEA5F20EF2AEE5216A0ED477742CD071A975CC50547331F91E49FBD6452714172AA96F64171E352DDE17FCB9791A5FE473BD133742A5DF11263C97BC1477A717FCD51A72A694FBA6EF22CB3E46FC2D3EEAAEBF1F54110FFDD535669EC457BA6CF049972829430BB6BC8F9546F5186172AC32368E85AE79D2B5F5289A4273D7B99F238E312A345CEBCBAF2B784A95611A0F354A3E1F1EEBEE7E78FC67E1BE69774CD60BDC72F5EA6A79E7951FFBF6C9D771A96D6225A94F672C
	E772EED471A96585E84A5B2A9A5F39A72FC9452714B7C9D35E46C1017FDFAF797F199F601D10BF7FG6F040C5A703BBE7AE37C7EFF1676BB057F0957157E87F1951C2E4A0E93104EBE6FF08345F983AE18537D016F218D6738C3F4AC18BBB7DED7B497F1E6FA4E328242759C779A55259342B60EBBB30269854D457E20036E07D13F3BE02678CE718CB0B9374B706FB81D1739B06FAE4C310D7F3B8BD369DE15343952391AD97C2DFB21G57264CC368CB1A8AF07EE647BAD21FD97CBFAD58477A94605A555BF43F
	DEA40427G247134BA4F22CF487D84E99F20FB5CEAA15C82E07E84E9BF66FD378F271DC377A8C5C2F88AC05AA7B87FD1361A24F952E4B4EE371A36D589B8FF7264AE44B7D9CDCD2BB87ED77BA5745F58692FE97E9069CFD9CCBD5BD60978D58724692C66F9BF3786F54C7D94F55C3416668742ADG66CF311E952CF356AF7BFC2ABDDFB4D748DDAF15DD49E54F6077FF84F2591FD492598990CE8548790C7B5DAAE7320BBEF3A47BF7B259D5DC76969D663B62B314BDFD96EDAF9076828860323DAB1C491E64D036
	7F86E9195A4B6E68C6A9FEB5474F291162ABB97EF1A97E1BB5BC8F47EC1807A81E07CF69FE648C889794466B4A8AB18F2FBB7DCE28AFF434876F57F48D3725D039876FF794EFAED46E41EB26F8DA67A1AE351077F974BC9DB9475F74DF2C73191B2579DD4B737B048C3F1E63CFEF115A41EE378535D23C5DEEAF4970F67BDFD3A7EBA3CD58C6BADA647855A27E5EB1B93ED244870ECA53A79D8A4B6B82BA675A817934723C3EBAA6252D663A7CCC1037D7CC3BB9D3EA03F51C7E23ACA91E5485B6C75A309B67C8FD
	7E6BFA5EB6C97106FA60FF157336772F164F47F69115C5D941B3D0D60806AC653C35743DF6020FD0491AFFD0FD2F615A037C4EC22027777542EFBAG59FB4074386D906A117739369EF8B7A55D0B28387F3D78F3259EAF2D7E1859562AB7DA6BAC7A769DDD86B213AE08F369EF2F46B97DB260E5BA14D6228E359ABAA867261536E8D66970171D6AB97C8FF6225C364F31BE59AC723504FBAC28075501ADE6CAEC21FC575EC725C74DE1227B29F6C31F7D8D2C4924A90A717FE5E7B8AD7DFA56975D446F79757F9C
	FDEC7A86BB7DA5021CBEC3C07A98CE7FD90D1D7E09B1EDFFB857266D8F8B86347D611F86347D616503CAFFA80EBB1D4E5966FC61282FFFF1C46963D8FAC1CE771697BACCEFC90E52EF097AB93DE32A4CE1FA29B95A3E68675335FD5169995A3E680B19CADF447A3C87E6E27FDE7B85565143395A7D7B34F95A7D7B4DDFEA776F4D73357B770F0A357B77DF97AB7BF7E63BC24D32ED96ED566950F6A60B52AF3074DE216BA5B6851F1F988E065FF0C59CCBEFD31F521F307456533BB1E394BCC9E2B85D21FEB32D5A
	FE48174E776696456B8A0B30CC9A7B357D503C335AFE6851335AFEE8701C369F3A41067DD0D911F6BFC478278B5AFE2836C8598FFD2DD8975C30C960323E66329A9D48F256773629E4058A4E7B7FA7856C078DC5587F3F4B57A63DB4F418AE112F25033FCA8763003A7F3F145FBF94444B7172856AB70DEE63F76C06819DF953A886FE13G26F13EAC1B5A577E5706FCC573182F2D025F86GF20609657322914CE75BBC573ED6792611F51EB21F69BDF27F3540486D68A0FD1D6058FF794FD32E71764B6E198D02
	78B00DF4254078B29C4E95C683FF22A20DB4883371F0973DDFBF674BF845796CBE0AAF106059B879FDFE2E2C8462FC3E44F1FA0FEAEE88A195E80479B02E1FAFB9BBB39F65AF22E70BEA66AB65FFC5710D7315729F64E7D2DBA1AEF2BE4A7F54617D3CA545713A6AE2F97E9B0BD17EA5743B362DD8A97FAA0AB797AB65FF424F53FAC1DCF2B14A372DF1A43FE181FC67AF104BB7AFC079BF255F2C5E8B14729F21F87F85CA795B56227CE0084BDD085FA9AB56617C01E1A16A74216619F95A5783CDB0C078C23BDE
	B18BD127AB6837EC52C225CE57D3FC5AC225CEDFD422CE4222F8DD47A254E9DE951F27DA04BA556B9C56932091CA94F349A52893FBF73434C429534794DFDD22546943D928D38344051620CE37D420CE790BD127933273E8D500EFD8EC5723F53123EF4CAB69B72BF931D2A7FF0A77DA2C5429E8A66A6487F1690BD1271CDC5429E5896A94CD3FC75577C4A5D0BC3977065C19C36E66E178A33963FFCFD4DF6EF85E9345FC06F1937A0C10A55877D7737BD4984D3774BB758A0070A5B8E7B5381B5F0F3E84476547
	B613AA49621F63F73E8ACBB15EEB0B404B667841BCD1EEB5441B17E23ABFBA4875D90AF3958914B6894253G72CB117659C3A8233494E57C31D69050F764D5CA3FCD447738AA11270D734C58EA6779E4171A67C5FE57ECG67D936CD606594A83B5FB41650143A63C3B2E57B9E2781CF5EB267FC62DB3043D8FF0B17E11DFEF8GF1759C77AFAB62BA81A70ECDE622FDFDB95DCA1ADEA0046D77C134F299211C6E9DBA0F1481617C65082B25734CA590365FC35143671E2BB81D9F3D6F509B42819C57C467BDC3A1
	9C49F193A9AE8142767BA51E64773AA5AF6767022D72BA7ABFD62C23D5AB30FEBCE9636F502C40F2EB2C375751819EEF6371266D763AB0D5D0570565197C1D9A4EF3D3031D67443014075575A797E0BF148ABCA5E5285FC78BF11FC5EB995675BB9752FE884221GD1E5F8EE7140A265590AC408933E02343E42B357F5B6ECD3E5006B788A65DCE143B28F7C8A65BCCCE993A01C8A10D30E34BF9B417C9515E37EFE3153EEB376769D09EF6671D3F7893CEE7A8E294F0ADEBB243E53E4AEFDFB48975288AE4732BF
	564757C0B8EE046292A19CB506FD2F0916FDAC8F8F52B039D7D6ECD7D6C7EB8A04471D3E51F55B7D5C218CF77BDF576D5C1B2EA124C15EF3D722BDFEF7165F3542F1FF2738DA88B7F35CBA7A66E4A704C3C679BB5A8344CEE1BCDC49425CEE0F1C3B10EBE9C40E5AEE8997D4065256AC6FDF517AE6033C7BF3FB1C335F8D49F1B7D1DC8C04BBC65086D7507BE94CBCFCBD8DB7F19BFD61F4CD4CDDA069BB2EDBD597C40623FA4872FEBDDD4FC9023CCF6376183A86ED14D741FD8845D5D4C8ED7470DA390D9E23E1
	72460338EFF66D785B684485116148C66CBD1C2D6B304F2D077C37G8CG84G04F37BAC24F33DD3A01C46F10655E8471CD508732538D28857F05CF3945734CA5AC62F2914375109B4CC5E45E0618515635FC6B7DCD099CA9BB35BD6D521EDBBA07F83G81G61GD338FD0E523537E9904EDF0D383BF5E85B920EFB02629AD6CBFD446C8A390F780C068D5C0E9F4F9AFF9F315C210C8B61A3189DFFD601F6EC057C1B09CDGA68144F27B8CD27F1F86611CCA44AD20770215C238066302465036ED1578DD64B713
	76FF97235DEEE661B87E1640CCD7F50570FEBFBBEEF4A9637B58134DABFD110B73CA13AA4F7F3E0A28CA653C55251B487F95BC2C9F4B025F35551827D159FD0231401F2CA1D7ED0B97E57BBF2781CFDE15BBFC9D875D597F296633F848F76FF97331EF7906C3E4DCDADC253CABE7AF4517C970EC4C7B227DBB9262822B305EA69C124A97564078F60DBBF6BADEA965AB851E9A3778DE114D87B681CF078BBE5666FE3ED5BDDE8E5F2A9EAF97E62A6966E5CA69D8FFD04067E887C03EAFC0A8C08CC0AAG23799DFF
	8BAEEFADFC378154823481F82DC51A02FE248982DC98C082C0BAC041BA7C861BBAD3DE47F78C4B67EA7B0631CDD52DBB7FB3C28D6B14EDEA76087A0CD04988FE5BB4F33D8E0D48757066F352DA606A7CD427CA0F9F8F2A675EFFB5085FF2D62E4769A1353E237C8DEFEFCEB7E9C82E6FD4FEF70E3F065DDC1DE38AD1699BEB1577BDCF4B665F4200BEDAC30E3A0E0F48789201A74DAD3E1532F90E1C6AF8DDE135BBFCB18BDC75A175933546AFAF3A64D36E51E7FCF3971ECF1FD5D60DF3251FAE227B202A15FD56
	FC0AEF2CD676D9A18B314FEA013808EA7C46B72B3E710B57476B9AGDA5763777907D93817473A9E3F65BF22F36421900E82C8DC0FFD6057CE6F7DF2EC77839E72DDFD5EFC4CCEAB683D11696B15762B22F8E10352FE3F64739CF990E75E0079FDD8F666A68170A99B5C29E772379CB300A7FF23F3BE562E16774DA26550FEE23B589508FD41D67E2EF28924936EC3F0563EC2176979D3BEC82F394BEA50B35CADE735E57C7CAC4A2862B27EF456350C696754FDD751B9CC2BFEA376C95F1ED36BD2BF242DCBAB6F
	531A39AED78F3956657EA1350C1F2C5116716BB5A823034BC8DF63DAC641DA298CF1EF66BA1C4332C2DA6CED1420F54A37D7BE49143E1D9237CE79768A29FF76781CEA655BAB153237DB56D6AB5FDEC1FE26677A555A795E3B9A739D4A73FD49EA57793EEB353A1CFF41530A60651C3ADAED1FA3D5FC4F8D374FF5D54E6D93D5655CBE6FD4B937CF699A67763168346DF3258E7314D3037609503936CF22CE1D6FA995B816A82841FC4FD8254C779B32FCD52FF21E6FB32B14792E1359ADF01573FC7B59546B77BF
	66E33C469A36FE5F813F83G017C4D026AD1757A7D1ED164732BE16B77A1709BA17231314743E3B876C828F13DD722EDC21C4E8A700598CE293177554C260D39D81752EBF03ED8E894849C435E30DBBD2E3DF937FA7C7B468CB55DBBB324F42CAF7F16BC310D7D3A647F240363FF96615DF115A16E75BF2436C35AA1353D7AAB55F4C315EA3AF7ACEA3A77ADD2BAD60E9B78DAC959A6EC9B961B73BEA158663CEDBCEFF35EA7640EB8EA9BCC2774815429036BD426FAC3CB2E5371C167BA798DBA576957034EF55A
	504F57DA38CE1D7D4E7B101B7B1D6B94536F3C8F79205F39CE417C0CDC42A6EC030B3E91846CE3B60E2877F06E78569E7F5CA06E49E4619B0664FBB4BF626967ED46743B76883CBE7D41262EE3FF31296B223F865DC48D3A9D9A5F423BB355F44B697ADABD685432994708C5E42B1158DE97CB7E3FFF6178778F3F99D7995E25A44F9396619BF69D1067DC40596057EFB36B4BC3B663F74465FC6DBCF2B3FEC79CB8C2465553A01C3FA5DED73A85ED16AFFB6B2396704DDB6C3E21F30B237D9A97536F925B966519
	002BA95EDF02E7637A82FEAFEFA8446557227CD5A376F25AA0797FF127E707AECC19A50E2B8CF1CDEFC4DD1F63C75475193D7DA6276BB3CA69447D34FD72767A54D9F938721CBC5CB7A48F7FFE8D06453DE63273BCD6A8971FDAF73E0FBAD74B531D3CDA1E6EF73233BA011066A437523DC3A7CFE72A53F3B7DA406A5B337CCD09A8101BD10BED6333917EEDD507ED43C83FC58D907686882C43B577F32378AD9A863854BACC6351D17483A5DBB10D57A9EDAB042DG3EDB791D08E358960337E27F695BE42F67FB
	E5E7699301A6ED2BBB5F35DBE45F63B95B62F5055B5C61FBD736973D8CF8EA5D625B6272BC2EB65FBD327D564DA02B53ADF9F3E5FCB66071F341476ACF5DC27A6DC7867E827A3B87768A3CDE04AD42B28D06749237E1B94DAC41FAD1D00FE55AD8C267F8A05C81E0637849CBE99F8761D00028FA6C4BFC8FA9ED14D8EF57B92D5ED1DF7A8C6D1BF33633BEF3B647A7D2FC29844F72F01462C93891624237639A4FD2BAFFD432A3DED73D83F165744D71D6881BB96E3E9CDC8B725F013A7B8CEB1F9F752778E4204B
	79BADED772B552FF7E831C9F2DF4AA63421C9F75DE077DAE998BB25C956B305FA563CB06BBDB25269B2E1252B11D1FF3B9CF26BD2F79C717FC5A731AC5AE6F2211497E9A67B57FC07B3C162F1573F22FD1FC47574AF9B96B88369BB344A570FAB0223A57513D321D60A1DF54F97331FC3553B7012A9B1479BDC371758D4A7C3E457B7EA6088BEB60E7A8E577C7956E0C57156DF4E7AD28B3D76D5FFBF255630003B6F5BDBBEAD357477CC1B55DA703EA3A48E1355CA706B546539AF4ABE5F4AC7FCFAC722C5C62BD
	644BABF144D76B406E2CDC23E85F5D3CD3592746D37C40CEE51F3A2F847B54C0084B5D056DE441B202376EE2742BF8BADBA9BEC902975FF9223E01049BF7476B26B4E21FFB979D13F7FC9B2F9B78166FAF61EFFA85FE0B75AB7E8819477181DCB059AB879083108210F18A7B64BFD01AC860C9829076C03F3C071D6F285F0369349FE99353A1B689BE256EF7E3A4388C497F619A34B27BCA62457D7AD496763BDA7B19F5127FE7AB4A58192C8FA57FB37F3EB9CBBDE7F6A0CBF96625E30F724C4B76EC751CD9EBB6
	DFCF5D43664C7C60B7D834A51BB30B4A41B93308BD0E4FB2B9BA73923B475E2F30B702BEE8D23E91C4E4A59279B43E3ACEABBE0D475F35CFABBE27896BD4A59D5F1237766CEFC827C83E2B5E107CBFB4B27E5FD8577FGB2CE0F5973E4167C7F6E68784B7E4F5878FFC366886A3ED9A7287B70C5F48C5BDC49768FB37D1EAAA1F5DC280A539583D483B48198GFC2A30FE7C1E52901EC228C755GADG83G81CD18CECBC91B06FE02181E2BFC20EC723F0F78EB5060934FC6E84AA26DCB67C28E81BC03C67E473E
	66C38DBAA6EB69B2A20B4CF14F91587C5CDCG325E7E9940FF81D6824C83A0E7CEC93B2E83A08784660B694E9475A23296GF4831049E9628316820CF1D98DD4D6B858B489A0F7EF3C2E822085408BA084A096E09AC0713EF8DD830099A0GA092A085A0FFBF148FC09B008F40A400A9GC2F33C2E942029994BEB6AD22FCCF5FEF5224E331D64778BB13F73457CAC927917AA6C37D24CE3251332C0DB39D3578F39C9576DA65D181BF463892CACEACB15E501F6A5B6DEAC586B2C32FC32457219A75A7FB3096D25
	6911F27CCAACC7C73AE0593A23778AB7691639C9376AFFC1D9B0DB7E6210FA9CB079909F87B40B63G788D86A0FDA25BB3357EB052C4GBE96A08DA07FG346183681B0B32253E39F9976B071BF87C72235278A6A30B6FB800FBC4BE33A97708107881087765E9FC34DC1AC655CEC6934C639F9C561291FEG67B92E4A42F90E1883B84F654D5FD34EB9982F2BB90834B159B84FE5BD08F39F6FD15AD088C781A4F25A37F378DA60C11C670A5B87C1F11DE24D5EDC31FE179DC29ED39DDF6361389777619E1840C3
	1827850B25F98A5940764E107F9BB61278EFF8FC82702482486353F8FC861153220C3FE1894617C0DC0DAA3EF2A946B7C2DC07A4BE20062EABAD43F9F9EB8B9B0B7B0AB4DEB54C4755EEA2F437E5A1DD8C441371FBF28B7337536017FC5F47D23AC78611AE77B00BAFBE4C522DBE4C6436EDA6F43EC3C857F8187993F277825DD734D9AA37FC182F9BG1D8DA070B07371610719DCC677E1B552C583BEC9249BD051CD66F4429198B39CE1F48192BAE6171B56A3DD7591A62F698833CB7216B8097D0EEFC03A40A3
	CC1EB65DFFB6A2DD5891A6CFC9476C77A4270B41F8784DA07F9F057285E06965ECC23A2623AC3E73A8232F2A134A0D66F4A1C719DF6E2C0B1351B139E7B6A3DD24184E1423ECCCB5C9C6D732856952C43A5CD6AD3A17B9DDF12BF6FADEDB895DF5355C4E2DEC0C50544A666B5A60371C5335B4A11D39150DA7FC60174C6D8548687E35176737150DBDA2DB595C9F2647745B339F69E2DBD9FDCEE9E575B4EADB1C2E74987413144E3F9969EA0EB17D1B0FB1BA43B1E9FD569FC0BA2FE3ECDC232C4F2CBCE69EC43A
	00E34CAE1344DFDB3D146EB1CE97AD46A7093F09C8C77B3FF78FE17F178171D54731AF2AEB417E4FF09C7BBFD38BBDB38C61880084CE7B9D3F6BBF75B82E8DDFF5D839B65CAEDBEFFE7430F2EDD8A7DB7BFD6330F2ED78399CE97C22A34A35612FB225EB437B3379DE356FD04F7623586736F25C9E3E7E6F7F9D66B3042E1F44C0B899A00353EEDF0E7DF8C19B62D2799AF1E39B0EBF2F4BBA1F799EEF4EF7254BFB2D15407882BC641BFC5EFCEC7E47C85F278EE8D34EA71E237860B665FC221003F5EC9231DBBB
	6677BD599D5A830077EDF7E7FDE443DE429724208D934351701B2808E3FCD64DF70D52C45A1C93EA59629E78FD042FD091DFA606EBB57844F96345388E578CB43EG6C4D761FAEA66BFC447F09EFCBAF616BF5C01341691A1750B7CB8EA3DD84272BBA897D47C924FBD7C1F7E4A93F5B81E892B95DCDCB65F47FC36771C85DAD680857B5F660FC51192C6F77D67CEF32D03EB7249941537D4DE2BA8FA8FBAB7E6EC179FB53497CBD77F460AB53A36F6EA16D37627B07247B81453BBD06B0DF9D409F28477C9E62F7
	EEA4GAE6F940E31C2E563C6729EFBBD44F51E6276D8C2705AEF453F3DCE2EFFD5B54A3081FF9817111E2D14118AF17927D13777B8DF8960DACFA31FC5D07279C3DCB8670BDC0FF90D81DCCEA76A3CF4C32DF0215E0A3FFC834A2801742D1DA8FB63C6440782AE19637F4171C5DD606F3AD02703B584AFFDA31B0D7DDF9D160E7DC31B62F43E40B389A00A73FA55B8FECFBE38867B7A44AE54612BCD3CEE1F013AFD8653715F747D5E13FF03276B8DE9C61CC1F957EF667596F0E55DA86F3D4D4ABCB37AEF36A0FD
	875086F27A30AD5A74B3EBF95D855ABC834AFD04636B81E7B3E0BA8B6B709E81965E5AA42F2797B5A1DF987024F23E14A6B95FDF764A79166C637534876AE98F72DD358F79985D40FE3413BF5044F03AD776FF3F775E3FEC667BE20DE803DFB623CEB500339AD1D649816D775E3FBD206EB32EBE406BB270A77334AB8E723AEC023AEC4234EFBA28DDCEFF63743EC09B4569779EF27C5E7B3FD5EB676E796F59AE5F4D53665BF15EFC4C3F7F110E7FB2CCCA3F1FCA7139E6255F6FEFC13FDFGF183E6FEDF73E14F
	74EEF1BADEC9B4BB725F270EEA7AFD11BE275791DF33EADD3AD0C2DB2641476CD1C4570BEBFB157779AC217826DE65FDBE77737DBAED9097550BF65A35DCCB6FD28BFC4781B481F4D83446B9EFE85E73B7A0527ADA54FA33B974FB32F0DF6124F41C9F27D7D060B9A9497FB3F246FFFDE17E384A905FC51B2EDEDF78F43AFAFDE1AF1D4389825B05D9789DD09497ED115E91A36C43F54DA91C4E17521576C13975A1EE821D7FE96A135ECD13D6474FA9F33AF7E8FA13A09C45F1DB76533379FD523BE40277A3EFCA
	9F561F2145249E1C7F3BD4BE146F7C5F251A3744B3F9AB16F8A66F579E4A0B75D0DE5BD24F649917FAA66F51E54AF66C5EFB874F28784A9561EA2BD63F710C4B6FD6EDF9AF3964EB521477B78F5F411A66122FD3D35EA32A3B7D5C3367EFD5FCCAFBFAE94ABB66613BE95DAE7982947258B87AC959B81A3CCFCE6675A681ED149567AB8656723132957B098D9E3E775E60122F20DFCB4FE919DAFA1681EDEDBF6A39A28775EC69C7BD858F5FFB7F48A51F1F261E0F0EEA6999823491DC4FFB78DB0E89DC4FA7D572
	5C2BE789AABE653E373C33DA756CAA8F5F173F45657B72A51A726ABCFC6FFD0F4B775EEBB4653D6D213CEC176F3DB71F55AA770CE165DA9459E7E4GDA2F33D86E8F713D5481E7F15EF0865DB7978B6194G619C5607FBD4FAE8C161B9659BE5159E3E77DE67723D7732F3DAF67E270772263BFC6F3DDED35EC4951F9234657D58A5DF2B263C7D9E3E775E6E124F2AA9EF36076F3DFF61124FD7D35EE39E4AFB56A5DF30263C4B1BBC13F703CB3E88CDF9E57BBC133741A5DF02263C517D1E3587DF95DF39B29DCD
	F90B1A89DF4E40793EB376F833E7F6196C122FF0C0CBCF7F831E5965CE951F52AE951A72562A6E5CF1CFDE3D0ACFA92FC1D3DE1A0772BEF0A92FCDD35EDF49E72E787F1B127F07DB467F9B5BFFDCE530EFD3D3B6FE1B8E8C707BAF69DADC7400743D77170F72EFE7CE57CD5767CA87615BF990F1FF637730368D220FDDB8A2FF6F3D0C0649BCB28B07B9BD6FF6E15EFBFF46298CF74FE6B837E57BF2341B7D5D352BA9EE9258A2165B682565E823E40E7B084E6FD48D456B9A069017CB6D5BBAA4FDC32C073F77E6
	9842794B4E3D6ABBFF4676623AEF84277B59DE791DBF1F6D7BBEF794458EA96F7C39EB2FF46ED63C7FE1AF7F2661FA5CD9A757E3A6FF57BDF5687C6FEC9206D5F7B66D573833E9BF6A11B74C57BC764B75F8E4BF6AD1B4FC7EF7A0D52874F8E71976BBB1B9595A6F44AC9852FEA7664DA16DF7E27C07355FCDCB2A423DCAB543B87E7D519AFADEG423EG41437C1C4CDAE7FEA6C2438ECCF6EF35763BE995D9388E9F4BE5EF21FB33F2ED713AE200EA9B4A2ED03D53AC05A61BA3596B9C3C1B764FD14CF7078DE5
	7F121E238904F098C08C17FD3F60CCF632C3597FDAAFADD3FB59C5495ED3333F8FB3A0FBCF4D7EBECC1903F75306C6B08F53F89E12699CF44588748DGEDA31807C71C1E319A98F1B487BEC43F05FCC714E724AF23786091659969BC0AA7618808AB9E457EA9122E6D113DB05E23FCBDF613763BBA658E5ECD0B1562A56F6ADCA9FBCF4DEE37FA876F26CD2B55FEB76DD7CD5A6F261D5C277DEE5A0332378B6D6F55EC20EBDB8123645ED43EFE582C7DDE4D5387345F2B69B9287DDE4D52C35A6F55DC164D4FF80C
	1E7F3BE931234A7EE8E0393C3FFB7BA8261F28113E327FD73E8F1326CA7FE20D77DBA6723D239923EC8FEB41D83C2EF40C1D8FE2FAE40E72F74A464E7F2D3466B1259EF7AFF77ED64E234B51B7360D218EEFAE67EF14E968606A2DB49F158E0B33543E30A68B657A0FE19D1AEFD35C9B485F8D8AF1E00B1992DBA85F1A09D069F163DE75BD9BEF6F97F87C643D72FBB52A79DBE951E338DF3670001DFE63FEB9FD6AFE24973C10FE63C1C140F1571F9C3E0DD98D740DDE72BC36F96179364F699BA8D6AF65BADD89
	FBCB45CB39CE973A8657697CA1EE1A9776D17EEBF13C54FA91761BEB9C4E09FB810DBFC048C5F63DA2AEC21DFECE7DD86CC5CA1DFEC971C997A9F55A1E0DBA25C1DC73452853577C1CF3784528D30643732309C013F6315CCEB91720CE2B84A223709225CE9BA93E749225CE0F0F21CE559097F8893F4F183F85ACF823CEE3B27FD28278EAEF3B9E0D5E0E7CCB8BDDCBED73D66AF402624D5ECA1D7E4657D83DA0AE599BF50A635F928D1722CEDE5497295FCC71E5785371F46F8E59A344707F61E72D466F4D1495
	DC46783D1972C28D769D811772FD8B4D7277A8CED39FC55E2388399467C29F4E467699FEA936CF8B3D230A45AFE94578B49EFF837D4EE5E573983F773074B298FBDC06696A789D28811761B8E59DAD5FD88827G8817A36D6AD114D1F8B94A787408A0E01F727CF2751B970BD6A0CF9367D9FF544E33BFCB4D73A05FAF6A43F9DADB85DECE1BE4EF26C482CD42656E6CE9FCC3B6E71482BC99AE78D87D4D206370FC9F70F7BED827F7707B6EEBB86E94453579C86F3BAFD65CF73F1C06499EF9967E591A713F6F7E
	698BA243D1BDE767D76EDD0B6383AB645F87A084A09AA0055B27117A4D5CABE06CF98562EE4CC2BBD6F15CC3945782E183477D1B623C2E105EF77FBF59727B6E9F2761A4EE5B5D5963FF5FFD4785152134B1336D719C342DBF49BFC0B4C08AC06E15E81F39749E18D28857F25CB1FEA7FBB3470DD05C4015527B6E5789727B6E6BE8181C3B9057E69D1EC13DF0775D7F41210C6FBB27E337E32200F674057C8781C482A4818893F8FD247DE1B1042BB86EAC35D98B048D9C77B67733FE9350CE47C764775DF752F0
	8637E31A4B7B203E7FFD77BA1767213F0FBD5958762F7CCE6C248967FF5FFD7A846558766FB5F45E0507C71B65F7CEC7487624893E600FFC5D696FD734CA79CA012746AD3E5FF8F807316B3BD93479BEBA2FBBD68830F1583DEC2F1B2FF22FE09845377AAA778AEE62F72CF4C2DC0CAF564BF532B1EB45D5B06E3A4A9DBB152E107235G0F41AD3EDF486C6485BC7EAE78D81BEB56386F7E3046FD77394DEA3A594D6A3B4E66BA38AFF8853FAFB864AA3C9776189B77850FD0BE793D30135772F15BD5F8EEC76762
	3E600DAE6E8B6ED927BC37A33FE77276F50E6EF49467CC323473BD4C4FEE2671FC5F1765BA5F4FE8FCA3A77334042BF11C7BE9365ABE5E7CDCE77E55E81F77AB1D5B67AB977785FF67623E60EB2B1C5BE77918033B3947B0CF8DD7733B39475C381BFBCC1D6F2CCC9CF73770FCD7E4BA3F6B73C42673FC7BE7AA73AD3F6B7349CC67793EEFC4FD77C954883F7777EAF677C9887CC6107F7939012B465473C5415CAE89D7337922D478952EE1FC4C0F64E5229FA93C667C6F8BAE3BC6F9DF70677C7B247A9A7CEE98
	BBC62A20F8AFE2363ADF18122D6EBF36362A693EEED55FD574D349DDA73F107C3F6D87381324F5DCE530BCFFBB2A36437ED1353D362CD053552FD053CD56382375F91B3ADF5E2A38C372F9595A06994A56DB233E285B5DF5D972BBA45F4F12279BADFB2FA5G528CF5AB5D43E372F4E6E61E2F7FE67EB8F58467CBA2C16E34EB70BB3BEA947B198ABF7CA6FF1C4E0BF4C2588BA0408F4FCE440C21CF1F843894BFCC23003FA3DEFCAD2631154EA535C0588C60F3AD52EE626FD286DD0B6D664E63767ABCDFB6BE18
	82B429573A632FDFDF2B3EC36C4D356ABB44DE29D453A5D7CA6944F5FB1763A46D3B6F8417F73AE95FFD377E3C6E02932E43393CF534CC8A2ED30E4BEAA93E78BA65386C377CAE38B208733B8E47E5F1E31E0DA713BC647B2F5335B4ADE079FA08562BF4D5FE9F25F861FAE5FE9B844CEF9E44192F477CEE153523844027DC6FCEBDDB2B51CFED5468271AC6716C057D6E2ACD127B1CF6C87EFF7687385BA965871091A158737404647FEF059F605E2CF115A10E7FB56E73BEAE3B4F1B5CE9B3D75839A39EBA3850
	570569D6E146FEFAC44CAAFCDCE7B0946F6FEC3768BB0A690953BE2B207BE6107F7FC5FD97BD5556EE99ACAE7D56D43CCF5FE735B05432BE0BB0071F255BE96C53DB7B124ED6EE487E63DD59D9C24ECEE146891355EA6A59A9649874272CBB0569162E531D701BEDB519874C20D30E947AFA05D1B90600D51859E4B1D975DDC6D0FA3BF1E830C7EF95F23719FA4C26FEE307B64F85444A7E9CE9583C5DE855DB0A1B758356E22B29181936F80749D27CD5717252BEEB3BE5D9F1CF3B75E4273E2F385DB8D8FC924C
	5DFE12309417B6D974FDFD4548835C5B8774A77B2D7AE5D661D306840B779B2CBB0D27CC3B9A5BCFC021FC6F5CD905D9F69D2D42E2ACD3546294E88EB1BD6DD0B34C7A13DD20F63369FCCC6216588FB1E0B175C0942907D869E659AB1D232496A0F717019832F4B9B133544A4BAE041D2ABDAD59F12F187FAFE3E5FFD6217CD0275E221758DF23FE566BCF51C332A6E35F7F2A2C7CA0A6225F390230EC0C6C5BA0FE21757FCCD023210275F1932CFF0BDEEFF0C8C15A94B2D0FAC1C3D5A3686C32098DE5A741FB7E
	0062A5C08C6AE7CEEEB8F692F46E5935072F8EFE78F0684D13CFC71F3C7C727E931033EB5741C7DFAE8323614D1707EFDE9FFC77649430B2B874734B534F8D10F350007D0D13A073BF4354C0A5A1F2CD27C06EFE052487GD0CB8788905B1FA68EE1GG8CF481GD0CB818294G94G88G88GF7FBB0B6905B1FA68EE1GG8CF481G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC8E1GGGG
**end of data**/
}
}