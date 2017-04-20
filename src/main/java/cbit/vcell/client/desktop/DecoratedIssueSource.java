package cbit.vcell.client.desktop;

import org.vcell.util.Issue.IssueOrigin;

import cbit.vcell.client.desktop.biomodel.SelectionManager;

/**
 * IssueSource which manages GUI navigation
 *
 */
public interface DecoratedIssueSource extends IssueOrigin {
	@Override
	public String getDescription( );
	
	/**
	 * return String describing where user should navigate to 
	 * @return non-null String
	 */
	public String getSourcePath( );
	
	/**
	 * navigate to Issue specific location
	 * @param selectionManager
	 */
	public void activateView(SelectionManager selectionManager);
}