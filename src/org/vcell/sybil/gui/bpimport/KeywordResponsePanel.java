package org.vcell.sybil.gui.bpimport;

/*   KeywordResponsePanel  --- by Oliver Ruebenacker, UCHC --- March 2009 to January 2010
 *   Panel to display the response to a keyword search
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.models.tree.pckeyword.PathwayTreeSelectionListener;
import org.vcell.sybil.models.tree.pckeyword.ResponseTreeManager;
import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.event.Resetable;
import org.vcell.sybil.util.gui.ButtonFormatter;
import org.vcell.sybil.util.http.pathwaycommons.search.PCIDPathwayRequest;
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;

public class KeywordResponsePanel extends ResponsePanel {
	
	private static final long serialVersionUID = 5569708707128075620L;
	
	@SuppressWarnings("serial")
	public static class GetPathwayAction extends SpecAction implements Accepter<Pathway>, Resetable,
	ItemListener {

		protected ImportManager importManager;
		protected Pathway pathway;
		protected boolean requestSmelting;
		
		public GetPathwayAction(ImportManager importMan) {
			super(new ActionSpecs("Get Selected Pathway", "Download selected pathway", 
					"Download Pathway from Pathway Commons"));
			this.importManager = importMan;
			setEnabled(false);
		}

		public void accept(Pathway pathway) {
			this.pathway = pathway;
			setEnabled(pathway != null);
		}

		public void reset() {
			pathway = null;
			setEnabled(false);
		}

		public void setRequestSmelting(boolean requestSmelting) { 
			this.requestSmelting = requestSmelting; 
		}
		
		public void actionPerformed(ActionEvent e) {
			importManager.performSearch(new PCIDPathwayRequest(pathway.primaryId()), requestSmelting);
		}

		public void itemStateChanged(ItemEvent event) {
			requestSmelting = (event.getStateChange() == ItemEvent.SELECTED);
		}
		
	}
	
	protected ImportManager importManager;
	protected ResponseTreeManager treeManager = new ResponseTreeManager();
	protected JTree responseTree = new JTree(treeManager.tree());

	public KeywordResponsePanel(ImportManager importManNew, PCKeywordResponse responseNew) {
		super(responseNew);
		importManager = importManNew;
		treeManager.accept(responseNew);
		setLayout(new BorderLayout());
		add(new JScrollPane(responseTree));
		GetPathwayAction getPathwayAction = new GetPathwayAction(importManNew);
		responseTree.getSelectionModel()
		.addTreeSelectionListener(new PathwayTreeSelectionListener(getPathwayAction));
		JToolBar getPathwayToolbar = new JToolBar();
		JButton buttonGet = new JButton(getPathwayAction);
		ButtonFormatter.format(buttonGet);
		getPathwayToolbar.add(buttonGet);
		JCheckBox checkBox = new JCheckBox("Try to Fix Non-Standard Data", true);
		checkBox.addItemListener(getPathwayAction);
		getPathwayAction.setRequestSmelting(checkBox.isSelected());
		getPathwayToolbar.add(checkBox);
		add(getPathwayToolbar, "North");
	}
	
	public PCKeywordResponse response() { return (PCKeywordResponse) super.response(); }
	
}