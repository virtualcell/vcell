package cbit.vcell.applets;

import cbit.vcell.solvers.SolverControllerInfo;
/**
 * Insert the type's description here.
 * Creation date: (12/9/2002 2:22:56 AM)
 * @author: Jim Schaff
 */
public class SolverControllerTableModel extends javax.swing.table.AbstractTableModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SolverControllerInfo[] fieldSolverControllerInfos = new SolverControllerInfo[0];

	private final static int COL_HOSTNAME = 0;
	private final static int COL_JOBTYPE = 1;
	private final static int COL_USER = 2;
	private final static int COL_SIMID = 3;
	private final static int COL_STATUS = 4;
	private final static int COL_STARTDATE = 5;

	private final static int NUM_COLUMNS = 6;

	private final String LABELS[] = { "Host", "Job Type", "User", "SimID", "Status", "StartDate" };
	private java.lang.String fieldSortByColumn = LABELS[COL_HOSTNAME];
/**
 * SolverControllerTableModel constructor comment.
 */
public SolverControllerTableModel() {
	super();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	switch (column){
		case COL_HOSTNAME:{
			return String.class;
		}
		case COL_JOBTYPE:{
			return String.class;
		}
		case COL_SIMID: {
			return String.class;
		}
		case COL_STARTDATE: {
			return String.class;
		}
		case COL_STATUS: {
			return String.class;
		}
		case COL_USER: {
			return String.class;
		}
		default:{
			return Object.class;
		}
	}
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return NUM_COLUMNS;
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("SolverControllerTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return (getSolverControllerInfos()!=null)?(getSolverControllerInfos().length):(0);
}
/**
 * Gets the solverControllerInfos property (cbit.vcell.solvers.SolverControllerInfo[]) value.
 * @return The solverControllerInfos property value.
 * @see #setSolverControllerInfos
 */
public cbit.vcell.solvers.SolverControllerInfo[] getSolverControllerInfos() {
	return fieldSolverControllerInfos;
}
/**
 * Gets the solverControllerInfos index property (cbit.vcell.solvers.SolverControllerInfo) value.
 * @return The solverControllerInfos property value.
 * @param index The index value into the property array.
 * @see #setSolverControllerInfos
 */
public cbit.vcell.solvers.SolverControllerInfo getSolverControllerInfos(int index) {
	return getSolverControllerInfos()[index];
}
/**
 * Gets the sortByColumn property (java.lang.String) value.
 * @return The sortByColumn property value.
 * @see #setSortByColumn
 */
public java.lang.String getSortByColumn() {
	return fieldSortByColumn;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		cbit.vcell.solvers.SolverControllerInfo solverControllerInfo = getSolverControllerInfos()[row];
		if (solverControllerInfo==null){
			return "SolverController null";
		}
		switch (col){
			case COL_HOSTNAME: {
				return solverControllerInfo.getHost();
			}
			case COL_JOBTYPE: {
				String jobType = "unknown";
				java.util.StringTokenizer tokens = new java.util.StringTokenizer(solverControllerInfo.taskType,"\"");
				tokens.nextToken();
				return tokens.nextToken();
			}
			case COL_USER: {
				return solverControllerInfo.getSimulationInfo().getVersion().getOwner().getName();
			}
			case COL_SIMID: {
				return solverControllerInfo.getSimulationInfo().getAuthoritativeVCSimulationIdentifier().getID();
			}
			case COL_STATUS: {
				return solverControllerInfo.getSolverStatus();
			}
			case COL_STARTDATE: {
				return java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT).format(solverControllerInfo.getStartDate());
			}
			default:{
				return null;
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
		return "error";
	}
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Insert the method's description here.
 * Creation date: (1/29/2003 5:47:53 PM)
 */
private void resort() {
	cbit.vcell.solvers.SolverControllerInfo infos[] = (cbit.vcell.solvers.SolverControllerInfo[])fieldSolverControllerInfos.clone();
	//
	// comparator that sorts according to "sortByColumn" name
	//
	java.util.Comparator solverControllerComparator = new java.util.Comparator(){
		public boolean equals(Object obj) {
			return this == obj;
		}
		public int compare(Object obj1, Object obj2){
			SolverControllerInfo info1 = (SolverControllerInfo)obj1;
			SolverControllerInfo info2 = (SolverControllerInfo)obj2;
			if (info1==info2){
				return 0;
			}else if (info1 == null){
				return -1;
			}else if (info2 == null){
				return 1;
			}
			if (getSortByColumn().equals(LABELS[COL_HOSTNAME])){
				String host1 = info1.getHost();
				String host2 = info2.getHost();
				host1 = (host1==null)?"":host1;
				host2 = (host2==null)?"":host2;
				return host1.compareTo(host2);
			}else if (getSortByColumn().equals(LABELS[COL_JOBTYPE])){
				java.util.StringTokenizer tokens1 = new java.util.StringTokenizer(info1.taskType,"\"");
				tokens1.nextToken();
				String jobType1 = tokens1.nextToken();
				
				java.util.StringTokenizer tokens2 = new java.util.StringTokenizer(info2.taskType,"\"");
				tokens2.nextToken();
				String jobType2 = tokens2.nextToken();
				jobType1 = (jobType1==null)?"":jobType1;
				jobType2 = (jobType2==null)?"":jobType2;

				return jobType1.compareTo(jobType2);
			}else if (getSortByColumn().equals(LABELS[COL_SIMID])){
				cbit.vcell.solver.SimulationInfo simInfo1 = info1.getSimulationInfo();
				cbit.vcell.solver.SimulationInfo simInfo2 = info2.getSimulationInfo();
				String simID1 = (simInfo1==null)?"":simInfo1.getAuthoritativeVCSimulationIdentifier().getID();
				String simID2 = (simInfo2==null)?"":simInfo2.getAuthoritativeVCSimulationIdentifier().getID();
				return simID1.compareTo(simID2);
			}else if (getSortByColumn().equals(LABELS[COL_STARTDATE])){
				java.util.Date date1 = info1.getStartDate();
				java.util.Date date2 = info2.getStartDate();
				date1 = (date1==null)?(new java.util.Date(0)):date1;
				date2 = (date2==null)?(new java.util.Date(0)):date2;
				return date1.compareTo(date2);
			}else if (getSortByColumn().equals(LABELS[COL_STATUS])){
				String status1 = (info1.getSolverStatus()!=null)?info1.getSolverStatus().toString():"";
				String status2 = (info2.getSolverStatus()!=null)?info2.getSolverStatus().toString():"";
				return status1.compareTo(status2);
			}else if (getSortByColumn().equals(LABELS[COL_USER])){
				cbit.vcell.solver.SimulationInfo simInfo1 = info1.getSimulationInfo();
				cbit.vcell.solver.SimulationInfo simInfo2 = info2.getSimulationInfo();
				String name1 = (simInfo1==null)?"":simInfo1.getVersion().getOwner().getName();
				String name2 = (simInfo2==null)?"":simInfo2.getVersion().getOwner().getName();
				return name1.compareTo(name2);
			}else{
				throw new RuntimeException("unknown column to sort by: "+getSortByColumn());
			}
		}
	};
	java.util.Arrays.sort(infos,solverControllerComparator);
	fieldSolverControllerInfos = infos;
}
/**
 * Sets the solverControllerInfos property (cbit.vcell.solvers.SolverControllerInfo[]) value.
 * @param solverControllerInfos The new value for the property.
 * @see #getSolverControllerInfos
 */
public void setSolverControllerInfos(cbit.vcell.solvers.SolverControllerInfo[] solverControllerInfos) {
	cbit.vcell.solvers.SolverControllerInfo[] oldValue = fieldSolverControllerInfos;
	fieldSolverControllerInfos = solverControllerInfos;
	firePropertyChange("solverControllerInfos", oldValue, solverControllerInfos);
	resort();
	fireTableDataChanged();
}
/**
 * Sets the sortByColumn property (java.lang.String) value.
 * @param sortByColumn The new value for the property.
 * @see #getSortByColumn
 */
public void setSortByColumn(java.lang.String sortByColumn) {
	String oldValue = fieldSortByColumn;
	fieldSortByColumn = sortByColumn;
	firePropertyChange("sortByColumn", oldValue, sortByColumn);
	resort();
	fireTableDataChanged();
}
}
