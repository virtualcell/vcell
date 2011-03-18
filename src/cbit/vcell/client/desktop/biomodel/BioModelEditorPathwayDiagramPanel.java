package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphPane;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphTool;

@SuppressWarnings("serial")
public class BioModelEditorPathwayDiagramPanel extends DocumentEditorSubPanel implements PathwayListener {
	
	private static final Dimension TOOLBAR_BUTTON_SIZE = new Dimension(28, 28);
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	private GraphPane graphPane;
	private PathwayGraphTool graphCartoonTool;
	private JTextArea sourceTextArea = null;
	private JButton bringItInButton = null; // wei's code
	private ConversionPanel conversionPanel = new ConversionPanel(); // wei's code
	
	private class EventHandler implements ActionListener, ListSelectionListener, AnnotationEventListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			// wei's code
			if(e.getSource() == bringItInButton){
				bringItIn();
			}
			// done
		}
		public void valueChanged(ListSelectionEvent e) {
		}
		public void annotationChanged(AnnotationEvent annotationEvent) {
			refreshInterface();
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(GraphModel.PROPERTY_NAME_SELECTED)) {
				Object[] selectedObjects = (Object[]) evt.getNewValue();
				setSelectedObjects(selectedObjects);
			}			
		}
	}
	
	public BioModelEditorPathwayDiagramPanel() {
		super();
		initialize();
	}
	
	private JButton createToolBarButton(ToolBarButton toolButton) {
		JButton button = new JButton();
		button.setMaximumSize(TOOLBAR_BUTTON_SIZE);
		button.setPreferredSize(TOOLBAR_BUTTON_SIZE);
		button.setMinimumSize(TOOLBAR_BUTTON_SIZE);
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setToolTipText(toolButton.shortDescrString);
		button.setIcon(toolButton.icon);
		return button;
	}

	private JToolBar createToolBar(ToolBarButton[] layouts, int orientation) {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(new javax.swing.border.EtchedBorder());
		toolBar.setOrientation(orientation);
		for (int i = 0; i < layouts.length; i ++) {
			toolBar.add(createToolBarButton(layouts[i]));
		}
		return toolBar;
	}
	
	private static enum ToolBarButton {
		select("Select", "Select", "Select parts of the graph", VCellIcons.pathwaySelectIcon),
		zoomin("Zoom In", "Zoom In", "Make graph look bigger", VCellIcons.pathwayZoomInIcon),
		zoomout("Zoom Out", "Zoom Out", "Make graph look smaller", VCellIcons.pathwayZoomOutIcon),
		random("Random", "Random Layout", "Reconfigure graph randomly", VCellIcons.pathwayRandomIcon),
		circular("Circular", "Circular Layout", "Reconfigure graph circular", VCellIcons.pathwayCircularIcon),
		annealed("Annealed", "Annealed Layout", "Reconfigure graph by annealing", VCellIcons.pathwayAnnealedIcon),
		levelled("Levelled", "Levelled Layout", "Reconfigure graph in levels", VCellIcons.pathwayLevelledIcon),
		relaxed("Relaxed", "Relaxed Layout", "Reconfigure graph by relaxing", VCellIcons.pathwayRelaxedIcon),
		reactions_only("Reactions Only", "Reactions Only", "Show only Reactions", VCellIcons.pathwayReactionsOnlyIcon),
		reaction_network("Reaction Network", "Reaction Network", "Reaction Network", VCellIcons.pathwayReactionNetworkIcon),
		components("Components", "Components", "Reactions, entities and components", VCellIcons.pathwayComponentsIcon),
		
		reaction("Reaction", "Biochemical Reaction", "Biochemical Reaction", VCellIcons.pathwayReactionIcon),
		transport("Transport", "Transport", "Transport", VCellIcons.pathwayTransportIcon),
		reaction_wt("Reaction WT", "Biochemical Reaction with Transport", "Biochemical Reaction with Transport", VCellIcons.pathwayReactionWtIcon),
		entity("Entity", "Physical Entity", "Physical Entity", VCellIcons.pathwayEntityIcon),
		small_molecule("Small Molecule", "Small Molecule", "Small Molecule", VCellIcons.pathwaySmallMoleculeIcon),
		protein("Protein", "Protein", "Protein", VCellIcons.pathwayProteinIcon),
		complex("Complex", "Complex", "Complex", VCellIcons.pathwayComplexIcon),
		participant("Participant", "Participant", "Physical Entity Participant", VCellIcons.pathwayParticipantsIcon);


		String name, shortDescrString, longDescription;
		Icon icon;
		private ToolBarButton(String name, String shortDescrString,
				String longDescription, Icon icon) {
			this.name = name;
			this.shortDescrString = shortDescrString;
			this.longDescription = longDescription;
			this.icon = icon;
		}		
	}

	private void initialize() {
		ToolBarButton[] layouts = new ToolBarButton[] {
				ToolBarButton.select,
				ToolBarButton.zoomin,
				ToolBarButton.zoomout,
				ToolBarButton.random,
				ToolBarButton.circular,
				ToolBarButton.annealed,
				ToolBarButton.levelled,
				ToolBarButton.relaxed,
				ToolBarButton.reactions_only,
				ToolBarButton.reaction_network,
				ToolBarButton.components,				
		};

		ToolBarButton[] interactions = new ToolBarButton[] {
				ToolBarButton.reaction,
				ToolBarButton.transport,
				ToolBarButton.reaction_wt,
				ToolBarButton.entity,
				ToolBarButton.small_molecule,
				ToolBarButton.protein,
				ToolBarButton.complex,
				ToolBarButton.participant,
		};
		
		JToolBar layoutToolBar = createToolBar(layouts, javax.swing.SwingConstants.VERTICAL);
		JToolBar nodesToolBar = createToolBar(interactions, javax.swing.SwingConstants.HORIZONTAL);
		sourceTextArea = new JTextArea();		
		graphPane =  new GraphPane();
		graphCartoonTool = new PathwayGraphTool();
		graphCartoonTool.setGraphPane(graphPane);		
		
		// wei's code: add a button for demo. please redesign it for better User interface
		JPanel layoutPanel = new JPanel();
		bringItInButton = new JButton("Bring It In");
		bringItInButton.addActionListener(eventHandler);
		layoutPanel.add(nodesToolBar, BorderLayout.WEST);
		layoutPanel.add(bringItInButton, BorderLayout.EAST);	
		// done
		
		JPanel graphTabPanel = new JPanel(new BorderLayout());
		JScrollPane graphScrollPane = new JScrollPane(graphPane);
		graphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		graphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		graphTabPanel.add(graphScrollPane, BorderLayout.CENTER);
		graphTabPanel.add(layoutToolBar, BorderLayout.WEST);
		graphTabPanel.add(layoutPanel, BorderLayout.NORTH);// wei's code
// wei		graphTabPanel.add(nodesToolBar, BorderLayout.NORTH);
		
		JPanel sourceTabPanel = new JPanel(new BorderLayout());
		sourceTabPanel.add(new JScrollPane(sourceTextArea), BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Diagram", graphTabPanel);
		tabbedPane.addTab("BioPAX Source", sourceTabPanel);
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
	}


	private void refreshInterface() {
		if (bioModel == null) {
			return;
		}
		try {
			PathwayModel pathwayModel = bioModel.getPathwayModel();
			GraphModel oldModel = graphPane.getGraphModel();
			if (oldModel != null) {
				oldModel.removePropertyChangeListener(eventHandler);
			}
			PathwayGraphModel pathwayGraphModel = new PathwayGraphModel();
			pathwayGraphModel.addPropertyChangeListener(eventHandler);
			graphPane.setGraphModel(pathwayGraphModel);
			pathwayGraphModel.setPathwayModel(pathwayModel);
			sourceTextArea.setText("======Summary View========\n\n"+pathwayModel.show(false)+"\n"+"======Detailed View========\n\n"+pathwayModel.show(true)+"\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
	}

	public void setBioModel(BioModel bioModel) {
		if (this.bioModel == bioModel) {
			return;
		}
		if (bioModel!=null){
			bioModel.getPathwayModel().removePathwayListener(this);
		}
		this.bioModel = bioModel;
		if (this.bioModel!=null){
			this.bioModel.getPathwayModel().addPathwayListener(this);
		}
		conversionPanel.setBioModel(bioModel);
		refreshInterface();
	}

	public void pathwayChanged(PathwayEvent event) {
		if (bioModel==null){
			sourceTextArea.setText("");
		}else{
			PathwayModel pathwayModel = bioModel.getPathwayModel();
			sourceTextArea.setText("======Summary View========\n\n"+pathwayModel.show(false)+"\n"+"======Detailed View========\n\n"+pathwayModel.show(true)+"\n");
		}
	}
	
	// wei's code
	public Object[] getSelectedBioPaxObjects(){
		return graphCartoonTool.getGraphModel().getSelectedObjects();
	}
	
	private void bringItIn(){
		if(graphCartoonTool.getGraphModel().getSelectedObjects().length < 1){
			return;
		}
		ArrayList<BioPaxObject> selectedObjects = new ArrayList<BioPaxObject>();
		for(int i = 0; i < graphCartoonTool.getGraphModel().getSelectedObjects().length; i++){
			if((graphCartoonTool.getGraphModel().getSelectedObjects()[i]) instanceof BioPaxObject)
				selectedObjects.add((BioPaxObject) (graphCartoonTool.getGraphModel().getSelectedObjects()[i]));			
		}
		if(selectedObjects.size() < 1){
			return;
		}
        //Create and set up the content pane.
        conversionPanel.setOpaque(true); //content panes must be opaque
        conversionPanel.setBioPaxObjects(selectedObjects);
        int returnCode = DialogUtils.showComponentOKCancelDialog(this, conversionPanel, "Convert to BioModel");
		if (returnCode == JOptionPane.OK_OPTION) {
			conversionPanel.bringItIn();
		}
	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		conversionPanel.setSelectionManager(selectionManager);
	}
	// done
}
