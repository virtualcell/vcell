package cbit.vcell.model.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import org.vcell.util.gui.LineBorderBean;
import org.vcell.util.gui.TitledBorderBean;

import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionCanvas;
import cbit.vcell.model.ReactionDescription;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RXParticipantResolverPanel extends JPanel implements ActionListener{
	
	private JComboBox[] speciesAssignmentJCB = null;
	private JComboBox[] structureAssignmentJCB = null;
	private JLabel ivjResolveHighlightJLabel;
	private JScrollPane ivjJScrollPane1;
	private ReactionCanvas ivjReactionCanvas1;
	private JScrollPane ivjJScrollPane2;
	private JPanel ivjRXParticipantsJPanel;
	private ReactionDescription resolvedReaction = null;
	private Species[] speciesOrder = null;
//	private Model pasteToModel;
//	private Model fromModel;

	public RXParticipantResolverPanel() {
		initialize();
	}
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if(resolvedReaction.isFluxReaction()){
			if(e.getSource() == speciesAssignmentJCB[0]){
				if(speciesAssignmentJCB[1].getSelectedIndex() != speciesAssignmentJCB[0].getSelectedIndex()){
					speciesAssignmentJCB[1].setSelectedIndex(speciesAssignmentJCB[0].getSelectedIndex());
				}
			}else if(e.getSource() == speciesAssignmentJCB[1]){
				if(speciesAssignmentJCB[0].getSelectedIndex() != speciesAssignmentJCB[1].getSelectedIndex()){
					speciesAssignmentJCB[0].setSelectedIndex(speciesAssignmentJCB[1].getSelectedIndex());
				}
			}
		}
	}

	public ReactionDescription getResolvedReaction() {
		return resolvedReaction;
	}
	public JComboBox[] getSpeciesAssignmentJCB() {
		return speciesAssignmentJCB;
	}
	public JComboBox[] getStructureAssignmentJCB() {
		return structureAssignmentJCB;
	}
//	public void setPasteToModel(Model model) {
//		pasteToModel = model;
//	}
//	public void setFromModel(Model model) {
//		fromModel = model;
//	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/5/2003 2:50:56 PM)
	 * @param dbfr cbit.vcell.dictionary.ReactionDescription
	 */
	public void setupRX(ReactionDescription dbfr,Model pasteToModel,Structure pastToStructure) {

		resolvedReaction = dbfr;
		if(resolvedReaction != null){

			if(speciesAssignmentJCB != null){
				for(int i=0;i<speciesAssignmentJCB.length;i+= 1){
					speciesAssignmentJCB[i].removeActionListener(this);
				}
			}
			if(structureAssignmentJCB != null){
				for(int i=0;i<structureAssignmentJCB.length;i+= 1){
					structureAssignmentJCB[i].removeActionListener(this);
				}
			}

			getReactionCanvas1().setReactionCanvasDisplaySpec(resolvedReaction.toReactionCanvasDisplaySpec());

			getRXParticipantsJPanel().removeAll();
//			java.awt.Insets zeroInsets = new java.awt.Insets(0,0,0,0);
			java.awt.Insets fourInsets = new java.awt.Insets(4,4,4,4);
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbc.insets = fourInsets;
			gbc.gridx = 0;
			gbc.gridy = 0;

			javax.swing.JLabel rxjlabel = new javax.swing.JLabel("RX Elements");
			//rxjlabel.setForeground(java.awt.Color.white);
			//rxjlabel.setOpaque(true);
			//rxjlabel.setBackground(java.awt.Color.white);
			getRXParticipantsJPanel().add(rxjlabel,gbc);
			
			//gbc.insets = zeroInsets;
			for(int i=0;i<resolvedReaction.elementCount();i+= 1){
				gbc.gridy = i+1;
				javax.swing.JLabel jlabel =
					new javax.swing.JLabel(
						resolvedReaction.getReactionElement(i).getPreferredName()+
						(resolvedReaction.isFluxReaction() && resolvedReaction.getFluxIndexOutside()==i?" (Outside)":"") +
						(resolvedReaction.isFluxReaction() && resolvedReaction.getFluxIndexInside()==i?" (Inside)":""));
				//jlabel.setOpaque(true);
				//jlabel.setBackground(java.awt.Color.white);
				//jlabel.setForeground(java.awt.Color.black);
				getRXParticipantsJPanel().add(jlabel,gbc);
			}

			//gbc.insets = fourInsets;
			gbc.gridx = 1;
			gbc.gridy = 0;

			speciesAssignmentJCB = new javax.swing.JComboBox[resolvedReaction.elementCount()];
			
			DefaultListCellRenderer speciesListCellRenderer = new DefaultListCellRenderer(){
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					// TODO Auto-generated method stub
					return super.getListCellRendererComponent(list,
							(value instanceof Species?"Existing "+((Species)value).getCommonName():value),
							index, isSelected,
							cellHasFocus);
				}
			};
			javax.swing.JLabel rspjlabel = new javax.swing.JLabel("Assign to Model Species");
			//rspjlabel.setForeground(java.awt.Color.white);
			//rspjlabel.setOpaque(true);
			//rspjlabel.setBackground(java.awt.Color.white);
			getRXParticipantsJPanel().add(rspjlabel,gbc);
			//getRXParticipantsJPanel().add(new javax.swing.JLabel("Resolve to Model Species"),gbc);
			
//			speciesOrder = new Species[fromModel.getSpecies().length+1];
//			speciesOrder[0] = null;
//			for(int j=0;j<fromModel.getSpecies().length;j+= 1){
//				speciesOrder[j+1] = fromModel.getSpecies(j);
//			}
			speciesOrder = new Species[pasteToModel.getSpecies().length+1];
			speciesOrder[0] = null;
			for(int j=0;j<pasteToModel.getSpecies().length;j+= 1){
				speciesOrder[j+1] = pasteToModel.getSpecies(j);
			}
			for(int i=0;i<resolvedReaction.elementCount();i+= 1){
				javax.swing.JComboBox jcb = new javax.swing.JComboBox();
				jcb.setRenderer(speciesListCellRenderer);
				speciesAssignmentJCB[i] = jcb;
				jcb.addItem("New Species");
				for(int j=1;j<speciesOrder.length;j+= 1){
					jcb.addItem(/*"Existing "+*/speciesOrder[j]/*.getCommonName()*/);
				}
				gbc.gridy = i+1;
				getRXParticipantsJPanel().add(jcb,gbc);
//				jcb.setEnabled(false);
			}
			
			gbc.gridx = 2;
			gbc.gridy = 0;
			structureAssignmentJCB = new javax.swing.JComboBox[resolvedReaction.elementCount()];

			DefaultListCellRenderer structureListCellRenderer = new DefaultListCellRenderer(){
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					// TODO Auto-generated method stub
					return super.getListCellRendererComponent(list,
							(value instanceof Structure?((Structure)value).getName():value),
							index, isSelected,
							cellHasFocus);
				}
			};

			javax.swing.JLabel rstjlabel = new javax.swing.JLabel("Assign to Model Compartment");
			//rstjlabel.setForeground(java.awt.Color.white);
			//rstjlabel.setOpaque(true);
			//rstjlabel.setBackground(java.awt.Color.white);
			getRXParticipantsJPanel().add(rstjlabel,gbc);
			//getRXParticipantsJPanel().add(new javax.swing.JLabel("Resolve to Model Compartment"),gbc);
//			StructureTopology structTopology = getModel().getStructureTopology();
			for(int i=0;i<resolvedReaction.elementCount();i+= 1){
				javax.swing.JComboBox jcb = new javax.swing.JComboBox();
				jcb.setRenderer(structureListCellRenderer);
				structureAssignmentJCB[i] = jcb;
				for (int j = 0; j < pasteToModel.getStructures().length; j++) {
					jcb.addItem(pasteToModel.getStructures()[j]);
				}
				structureAssignmentJCB[i].setSelectedItem(pastToStructure);
//				if(resolvedReaction.isFluxReaction() && resolvedReaction.isFlux(i) && resolvedReaction.getFluxIndexOutside() == i){
//					jcb.addItem(structTopology.getOutsideFeature((Membrane)getStructure())/*.getName()*/);
//					jcb.setEnabled(false);
//				}else if(resolvedReaction.isFluxReaction() && resolvedReaction.isFlux(i) && resolvedReaction.getFluxIndexInside() == i){
//					jcb.addItem((structTopology).getInsideFeature((Membrane)getStructure())/*.getName()*/);
//					jcb.setEnabled(false);
//				}else{
//					jcb.addItem(getStructure()/*.getName()*/);
//					if(getStructure() instanceof Membrane){
//						jcb.addItem(structTopology.getOutsideFeature((Membrane)getStructure())/*.getName()*/);
//						jcb.addItem(structTopology.getInsideFeature((Membrane)getStructure())/*.getName()*/);
//					}else{
//						jcb.setEnabled(false);
//					}
//				}
				gbc.gridy = i+1;
				getRXParticipantsJPanel().add(jcb,gbc);
			}

			for(int i=0;i<resolvedReaction.elementCount();i+= 1){
				speciesAssignmentJCB[i].addActionListener(this);
				structureAssignmentJCB[i].addActionListener(this);
			}
		}
	}

	
	private void initialize() {
	try {
		LineBorderBean ivjLocalBorder10 = new LineBorderBean();
		TitledBorderBean ivjLocalBorder9 = new TitledBorderBean();
		ivjLocalBorder9.setTitleFont(getFont().deriveFont(Font.BOLD));
		ivjLocalBorder9.setBorder(ivjLocalBorder10);
		ivjLocalBorder9.setTitle("Resolve Reaction Participants with Model");
//		ivjResolverJPanel = new javax.swing.JPanel();
		setName("RXParticipantResolverPanel");
		setBorder(ivjLocalBorder9);
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsResolveHighlightJLabel = new java.awt.GridBagConstraints();
		constraintsResolveHighlightJLabel.gridx = 0; constraintsResolveHighlightJLabel.gridy = 2;
		constraintsResolveHighlightJLabel.gridwidth = 2;
		constraintsResolveHighlightJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getResolveHighlightJLabel(), constraintsResolveHighlightJLabel);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.gridwidth = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
		constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
		constraintsJScrollPane2.gridwidth = 2;
		constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane2.weightx = 1.0;
		constraintsJScrollPane2.weighty = 1.0;
		constraintsJScrollPane2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane2(), constraintsJScrollPane2);
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {2}
		// user code end
		handleException(ivjExc);
	}
}
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}
	/**
	 * Return the ResolveHighlightJLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JLabel getResolveHighlightJLabel() {
		if (ivjResolveHighlightJLabel == null) {
			try {
				ivjResolveHighlightJLabel = new javax.swing.JLabel();
				ivjResolveHighlightJLabel.setName("ResolveHighlightJLabel");
				ivjResolveHighlightJLabel.setText(" ");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjResolveHighlightJLabel;
	}
	/**
	 * Return the JScrollPane1 property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JScrollPane getJScrollPane1() {
		if (ivjJScrollPane1 == null) {
			try {
				ivjJScrollPane1 = new javax.swing.JScrollPane();
				ivjJScrollPane1.setName("JScrollPane1");
				getJScrollPane1().setViewportView(getReactionCanvas1());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPane1;
	}
	/**
	 * Return the ReactionCanvas1 property value.
	 * @return cbit.vcell.model.gui.ReactionCanvas
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private ReactionCanvas getReactionCanvas1() {
		if (ivjReactionCanvas1 == null) {
			try {
				LineBorderBean ivjLocalBorder12 = new LineBorderBean();
				TitledBorderBean ivjLocalBorder11 = new TitledBorderBean();
				ivjLocalBorder11.setTitleFont(getFont().deriveFont(Font.BOLD));
				ivjLocalBorder11.setBorder(ivjLocalBorder12);
				ivjLocalBorder11.setTitle("Reaction Stoichiometry");
				ivjReactionCanvas1 = new ReactionCanvas() {

					@Override
					public Dimension getPreferredSize() {
						// TODO Auto-generated method stub
						return new Dimension(100, 100);
					}

//					@Override
//					public Dimension getMinimumSize() {
//						// TODO Auto-generated method stub
//						return getPreferredSize();
//					}
					
				};
				ivjReactionCanvas1.setName("ReactionCanvas1");
				ivjReactionCanvas1.setBorder(ivjLocalBorder11);
				ivjReactionCanvas1.setLocation(0, 0);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjReactionCanvas1;
	}
	/**
	 * Return the JScrollPane2 property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JScrollPane getJScrollPane2() {
		if (ivjJScrollPane2 == null) {
			try {
				ivjJScrollPane2 = new javax.swing.JScrollPane();
				ivjJScrollPane2.setName("JScrollPane2");
				getJScrollPane2().setViewportView(getRXParticipantsJPanel());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPane2;
	}
	/**
	 * Return the JPanel3 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JPanel getRXParticipantsJPanel() {
		if (ivjRXParticipantsJPanel == null) {
			try {
				LineBorderBean ivjLocalBorder14 = new LineBorderBean();
//				ivjLocalBorder14.setThickness(2);
				TitledBorderBean ivjLocalBorder13 = new TitledBorderBean();
				ivjLocalBorder13.setTitleFont(getFont().deriveFont(Font.BOLD));
				ivjLocalBorder13.setBorder(ivjLocalBorder14);
				ivjLocalBorder13.setTitle("Assign Reaction Participants To Model");
				ivjRXParticipantsJPanel = new javax.swing.JPanel();
				ivjRXParticipantsJPanel.setName("RXParticipantsJPanel");
				ivjRXParticipantsJPanel.setBorder(ivjLocalBorder13);
				ivjRXParticipantsJPanel.setLayout(new java.awt.GridBagLayout());
				ivjRXParticipantsJPanel.setBounds(0, 0, 459, 47);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRXParticipantsJPanel;
	}

}
