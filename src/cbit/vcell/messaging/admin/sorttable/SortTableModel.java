package cbit.vcell.messaging.admin.sorttable;
/**
 * Insert the type's description here.
 * Creation date: (9/11/2003 10:50:03 AM)
 * @author: Fei Gao
 */
public interface SortTableModel extends javax.swing.table.TableModel {
	public void setSortPreference(SortPreference sortPreference);

	public SortPreference getSortPreference();


	public boolean isSortable(int col);


	public void resortColumn();
}