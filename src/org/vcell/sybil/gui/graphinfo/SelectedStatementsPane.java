package org.vcell.sybil.gui.graphinfo;

/*   SelectedStatementsPane  --- by Oliver Ruebenacker, UCHC --- February 2008 to March 2010
 *   Creates a panel to display the selected Resources of a graph
 */

import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.vcell.sybil.models.graph.GraphModelSelectionInfo;
import org.vcell.sybil.rdf.NodeUtil;
import org.vcell.sybil.rdf.TokenCategory;
import org.vcell.sybil.rdf.compare.StatementComparatorBioPAX2;
import org.vcell.sybil.util.table.StatementsPOTableModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


public class SelectedStatementsPane extends JPanel implements GraphModelSelectionInfo.Listener {

	private static final long serialVersionUID = -4664538180745479527L;

	protected GraphModelSelectionInfo graphModelSelectionInfo;
	protected StatementsPOTableModel tableModel = 
		new StatementsPOTableModel(new StatementComparatorBioPAX2());
	protected JLabel label = new JLabel();
	protected JTable table = new SelectedStatementsTable(tableModel);
	protected JScrollPane scrollPane = new JScrollPane(table);
	
	public SelectedStatementsPane() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(label);
		add(scrollPane);
	}
	
	public void setGraphModelSelectionInfo(GraphModelSelectionInfo gmsi) {
		if(gmsi != this.graphModelSelectionInfo) {
			if(gmsi != null) { gmsi.listeners().remove(this); }
			this.graphModelSelectionInfo = gmsi;			
			if(gmsi != null) { gmsi.listeners().add(this); }
			update();
		}
	}
	
	public void selectionChanged(Resource resource) { update(); }
	public void dataChanged() { update(); }

	public void update() {
		if(graphModelSelectionInfo != null) {
			Set<Statement> statementsPO = graphModelSelectionInfo.selectedStatements(TokenCategory.SUBJECT);
			tableModel.setStatements(statementsPO);
			Resource resource = graphModelSelectionInfo.selectedResource();
			if(resource != null) {
				String name = NodeUtil.toString(resource);
				label.setText("<html><font size=large color=blue>" + name + "</font></html>");
			} else {
				label.setText("<html><font color=blue>" +
						"Select a graph element from the left pane to display properties</font></html>");
			}
		}
	}

}
