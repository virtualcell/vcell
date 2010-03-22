package org.vcell.sybil.gui.port;

/*   ClassesPanel  --- by Oliver Ruebenacker, UCHC --- June 2008 to January 2010
 *   An extension of JPanel to select ontology classes for the StuffClassUnited assumption.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.gui.util.GUIUtil;
import org.vcell.sybil.gui.util.LinePanel;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.models.sbbox.util.SBPAXUtil;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.collections.BiHashMap;
import org.vcell.sybil.util.collections.BiMap;
import org.vcell.sybil.util.gui.ToolBar;
import org.vcell.sybil.util.sets.SetOfNone;
import org.vcell.sybil.workers.port.StageInitWorker;

public class ClassesPanel extends JPanel {

	private static final long serialVersionUID = -6064010872247581476L;
	protected BiMap<JRadioButton, ClassesPanel.GeneralSelection> generalSelections;
	protected ButtonGroup generalSelectionsButtonGroup;
	protected BiMap<JCheckBox, RDFType> generalSelectionsMap;

	protected PortPanel parent;
	protected SBWorkView view;
	protected LinePanel panelCenter = new LinePanel();
	protected JPanel panelClassesSelect;

	public static class GeneralSelection {
		public static final ClassesPanel.GeneralSelection All = new GeneralSelection();
		public static final ClassesPanel.GeneralSelection Some = new GeneralSelection();
		public static final ClassesPanel.GeneralSelection None = new GeneralSelection();
	}

	public ClassesPanel(SBWorkView view, PortPanel parent) {
		this.parent = parent;
		this.view = view;
		setLayout(new BorderLayout());

		panelCenter.addLine(new JLabel("<html><h2>Specify substances with modifications</h2>" +
				"<br>Here you can select which substances may occur in multiple variations <br>" +
				" and may therefore lead to multiple species in a model (even if in same compartment " +
				"(e.g. varying phosphorylation states).<br>This will set default options " +
		"which can be overriden later."));

		generalSelectionsButtonGroup = new ButtonGroup();
		generalSelections = new BiHashMap<JRadioButton, ClassesPanel.GeneralSelection>();
		generalSelections.put(new JRadioButton("<html><b> None </b></hmtl>"), GeneralSelection.None);
		generalSelections.put(new JRadioButton("<html><b> Some </b></hmtl>"), GeneralSelection.Some);
		generalSelections.put(new JRadioButton("<html><b> All </b></hmtl>"), GeneralSelection.All);
		generalSelectionsButtonGroup.add(generalSelections.getKey(GeneralSelection.None));
		generalSelectionsButtonGroup.add(generalSelections.getKey(GeneralSelection.Some));
		generalSelectionsButtonGroup.add(generalSelections.getKey(GeneralSelection.All) );
		panelCenter.addLine(generalSelections.getKey(GeneralSelection.None),
				new JLabel("<html> - None of the substances is considered modifable. <br>" + 
						"Choose this option if none of the substances involved " + 
						"may occur in different states. </html>")); 
		panelCenter.addLine(generalSelections.getKey(GeneralSelection.Some), 
				new JLabel("<html> <b>(Recommended)</b> Some types of substances are considered " + 
						"modifiable. <br>" +
						"In most cases, proteins, DNA, RND and complexes are considered modifiable, " + 
						"but not metabolites. </html>"));  
		generalSelectionsMap = new BiHashMap<JCheckBox, RDFType>();
		panelClassesSelect = new JPanel();
		populateSelections();
		panelCenter.addLine(panelClassesSelect);
		panelCenter.addLine(generalSelections.getKey(GeneralSelection.All),
				new JLabel("<html> All of the substances are considered modifiable. <br>" + 
						"If any substance can occur in states multiple states, " + 
						"choose this option.</html>")); 
		add(panelCenter, BorderLayout.CENTER);
		add(new ClassesToolBar(view, this), BorderLayout.SOUTH);
		reset();
	}

	protected void populateSelections() {
		generalSelectionsMap.clear();
		if(view != null) {
			for(RDFType substanceType : SBPAXUtil.getSubstanceTypes(view.box())) {
				generalSelectionsMap.put(new JCheckBox(substanceType.label()), substanceType);	
			}			
		}
		panelClassesSelect.removeAll();
		for(Map.Entry<JCheckBox, RDFType> entry : generalSelectionsMap.entrySet()) {
			panelClassesSelect.add(entry.getKey());
		}
		panelClassesSelect.revalidate();
	}

	protected static class ClassesToolBar extends ToolBar {

		private static final long serialVersionUID = -6154028842726732906L;

		static protected class ResetAction extends SpecAction {

			private static final long serialVersionUID = 3082428209639831221L;
			protected ClassesPanel panelClasses;

			public ResetAction(ClassesPanel panelClassesNew) {
				super(new ActionSpecs("Reset", "Reset Panel", "Reset Panel", 
				"files/reset.gif"));
				panelClasses = panelClassesNew;
			}

			public void actionPerformed(ActionEvent event) { panelClasses.reset(); }

		}

		static protected class NextAction extends SpecAction {

			private static final long serialVersionUID = -9126068580382992664L;
			protected ClassesPanel panelClasses;
			protected SBWorkView view;

			public NextAction(SBWorkView view, ClassesPanel panelClassesNew) {
				super(new ActionSpecs("Process and Proceed", "Process and Proceed", 
						"Process input and proceed to next stage", 
				"files/forward.gif"));
				this.view = view;
				panelClasses = panelClassesNew;
			}

			public void actionPerformed(ActionEvent event) { 
				view.unmodifiableTypes().addAll(panelClasses.selectedSubstanceClasses());
				StageInitWorker worker = new StageInitWorker(view, panelClasses.parent);
				worker.run(panelClasses);
			}

		}

		public ClassesToolBar(SBWorkView view, ClassesPanel panelParent) {
			setName("toolBarInput");
			add(new ResetAction(panelParent));
			add(new NextAction(view, panelParent));
		}

	}

	protected boolean isSelected(GeneralSelection genSelect) {
		JRadioButton button = generalSelections.getKey(genSelect);
		return button != null && button.isSelected();
	}

	public Set<RDFType> selectedSubstanceClasses() {
		if(isSelected(GeneralSelection.None)) { return new HashSet<RDFType>(generalSelectionsMap.values()); }
		else if(isSelected(GeneralSelection.Some)) { return GUIUtil.unselectedValues(generalSelectionsMap); }
		return new SetOfNone<RDFType>();
	}

	public void reset() {
		Set<RDFType> defaultUSTs = SBPAXUtil.getDefaultUSTs(view.box());
		for(Map.Entry<JCheckBox, RDFType> entry : generalSelectionsMap.entrySet()) {
			entry.getKey().setSelected(!defaultUSTs.contains(entry.getValue()));
		}
		generalSelections.getKey(GeneralSelection.Some).setSelected(true);		
	}

}