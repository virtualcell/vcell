package cbit.vcell.graph.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;

import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionSpeciesCopy;
import cbit.vcell.model.Structure;

public class StructurePasteMappingPanel extends JPanel {
	
	public static final String CHOOSE_STRUCT = "Choose Structure";
	public static final String MAKE_NEW = "Make New Structure";
	
	private ReactionSpeciesCopy rsCopy;
	private Model modelTo;
	private Structure structTo;
	private Vector<Issue> issueVector;
	private IssueContext issueContext;

	private JTextPane issuesPane = new JTextPane();

	private Map<Structure, JComboBox<String>> structureMap = new LinkedHashMap<> ();	// key: struct from
	
	private EventHandler eventHandler = new EventHandler();
	private class EventHandler implements ActionListener, PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JComboBox) {
				System.out.println(e.getSource().getClass().getSimpleName() + ": " + e.getActionCommand());
				recalculateIssues();
			}
		}
	}
	
	public StructurePasteMappingPanel(ReactionSpeciesCopy rsCopy,
			Model modelTo, Structure structTo, Vector<Issue> issueVector,
			IssueContext issueContext) {

		this.rsCopy = rsCopy;
		this.modelTo = modelTo;
		this.structTo = structTo;
		this.issueVector = issueVector;
		this.issueContext = issueContext;

		initialize();
	}

	private void initialize() {
		
		Structure structFrom = rsCopy.getFromStructure();
		
		List<String> membranesTo = new ArrayList<> ();
		List<String> compartmentsTo = new ArrayList<> ();
		
		membranesTo.add(CHOOSE_STRUCT);
		membranesTo.add(MAKE_NEW);
		compartmentsTo.add(CHOOSE_STRUCT);
		compartmentsTo.add(MAKE_NEW);
		
		for(Structure struct : modelTo.getStructures()) {
			if(struct == structTo) {
				continue;
			}
			if(struct instanceof Membrane) {
				membranesTo.add(struct.getName());
			} else {
				compartmentsTo.add(struct.getName());
			}
		}
		
		for(Structure from : rsCopy.getStructuresArr()) {
			
			if(from == structFrom) {
				continue;	// skip the structure from where we copy, it's already mapped
			}
			JComboBox<String> comboTo = new JComboBox<>();
			if(from instanceof Membrane) {
				comboTo.setModel(new DefaultComboBoxModel(membranesTo.toArray()));
				int index = membranesTo.indexOf(from.getName());
				comboTo.setSelectedIndex(index >= 0 ? index : 0);
			} else {
				comboTo.setModel(new DefaultComboBoxModel(compartmentsTo.toArray()));
				int index = compartmentsTo.indexOf(from.getName());
				comboTo.setSelectedIndex(index >= 0 ? index : 0);
			}
			structureMap.put(from, comboTo);
			comboTo.addActionListener(eventHandler);
		}
		
		JPanel structPanel = new JPanel();
		
		//structPanel.setPreferredSize(new Dimension(220, 180));
		structPanel.setLayout(new GridBagLayout());
		
		int gridy = 0;		// header
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(10,10,10,10);	// top, left, bottom, right
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		structPanel.add(new JLabel("<html><b>Structures to Paste</b></html>"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(10,10,10,10);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		structPanel.add(new JLabel("<html><b>Mapped to: </b></html>"), gbc);

		gridy++;	// the first row shows the source and destination structures (not customizable)
		gbc = new GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(1,10,1,10);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		structPanel.add(new JLabel("<html>" + structFrom.getName() + "</html>"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(1,12,1,10);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		structPanel.add(new JLabel("<html>" + structTo.getName() + "</html>"), gbc);
		
		for (Map.Entry<Structure, JComboBox<String>> entry : structureMap.entrySet()) {
			
			gridy++;	// the first row shows the source and destination structures (not customizable)
			gbc = new GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new Insets(1,10,2,10);
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			structPanel.add(new JLabel("<html>" + entry.getKey().getName() + "</html>"), gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.insets = new Insets(2,10,1,10);
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			structPanel.add(entry.getValue(), gbc);
		}
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 4;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		structPanel.add(new JLabel(""), gbc);
		
//		JPanel issuesPanel = new JPanel();
//		issuesPanel.setLayout(new BorderLayout());
//		issuesPanel.add(issuesPane, BorderLayout.CENTER);
		
		JScrollPane upper = new JScrollPane(structPanel);
		upper.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		upper.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JScrollPane lower = new JScrollPane(issuesPane);
		lower.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		lower.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, lower);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);
		add(splitPane, BorderLayout.CENTER);
		
		recalculateIssues();
	}
	
	private void recalculateIssues() {
		String issues = "";
		issueVector.clear();
		
		Set<String> duplicateCheck = new HashSet<>();
		duplicateCheck.add(structTo.getName());
		for (Map.Entry<Structure, JComboBox<String>> entry : structureMap.entrySet()) {
			if(entry.getValue().getSelectedItem().equals(CHOOSE_STRUCT) || entry.getValue().getSelectedItem().equals(MAKE_NEW)) {
				continue;
			}
			if(duplicateCheck.contains(entry.getValue().getSelectedItem())) {
				String msg = "Multiple Structures to be Pasted are mapped to the same destination " + entry.getValue().getSelectedItem();
				Issue issue = new Issue(modelTo, issueContext, IssueCategory.CopyPaste, msg, Issue.Severity.WARNING);
				issueVector.add(issue);
				issues += msg + "\n";
			} else {
				duplicateCheck.add((String)(entry.getValue().getSelectedItem()));
			}
		}
		for (Map.Entry<Structure, JComboBox<String>> entry : structureMap.entrySet()) {
			if(entry.getValue().getSelectedItem().equals(CHOOSE_STRUCT)) {
				String msg = "Map each Structure to be Pasted to an existing Structure or create a new one";
				Issue issue = new Issue(modelTo, issueContext, IssueCategory.CopyPaste, msg, Issue.Severity.ERROR);
				issueVector.add(issue);
				issues += msg + "\n";
				break;
			}
		}
		issuesPane.setText(issues);

	}

	
	
	public Map<Structure, JComboBox<String>> getStructureMap() {
		return structureMap;
	}
	
	public boolean hasErrors() {
		for(Issue issue : issueVector) {
			if(issue.getSeverity() == Issue.Severity.ERROR) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
}
