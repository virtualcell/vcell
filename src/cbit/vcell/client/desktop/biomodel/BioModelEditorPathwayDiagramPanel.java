package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom.Element;
import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.GraphPane;
import org.vcell.sybil.gui.graph.Shape;
import org.vcell.sybil.gui.graph.SybilGraphTool;
import org.vcell.sybil.models.annotate.Model2JDOM;
import org.vcell.sybil.models.graph.GraphCreationMethod;
import org.vcell.sybil.models.graph.GraphModelSelectionInfo;
import org.vcell.sybil.models.graph.manipulator.GraphManipulationException;
import org.vcell.sybil.models.graph.manipulator.categorizer.GraphGrouper;
import org.vcell.sybil.models.graph.manipulator.categorizer.ReactionsManipulator;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagCreator;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SBWrapper;
import org.vcell.sybil.util.graphlayout.LayoutType;
import org.vcell.sybil.util.gui.ButtonFormatter;
import org.vcell.util.gui.DialogUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

@SuppressWarnings("serial")
public class BioModelEditorPathwayDiagramPanel extends BioModelEditorSubPanel {
	
	private static final Dimension TOOLBAR_BUTTON_SIZE = new Dimension(28, 28);
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	private GraphPane graphPane;
	private SybilGraphTool graphCartoonTool;
	protected GraphModelSelectionInfo dataRelations;
	private JTextArea sourceTextArea = null;
	private FileManager fileManager = null;
	
	private class EventHandler implements ActionListener, ListSelectionListener, AnnotationEventListener {

		public void actionPerformed(ActionEvent e) {
		}
		public void valueChanged(ListSelectionEvent e) {
		}
		public void annotationChanged(AnnotationEvent annotationEvent) {
			refreshInterface();
		}
	}
	
	public BioModelEditorPathwayDiagramPanel() {
		super();
		initialize();
	}
	
	private JButton createToolBarButton(String[] attributes) {
		JButton button = new JButton();
		ButtonFormatter.format(button);
		button.setMaximumSize(TOOLBAR_BUTTON_SIZE);
		button.setPreferredSize(TOOLBAR_BUTTON_SIZE);
		button.setMinimumSize(TOOLBAR_BUTTON_SIZE);
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setToolTipText(attributes[1]);
		button.setIcon(new ImageIcon(getClass().getResource("/sybil/images/" + attributes[3])));
		return button;
	}

	private JToolBar createToolBar(String[][] layouts, int orientation) {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(new javax.swing.border.EtchedBorder());
		toolBar.setOrientation(orientation);
		for (int i = 0; i < layouts.length; i ++) {
			toolBar.add(createToolBarButton(layouts[i]));
		}
		return toolBar;
	}
	
	private void initialize() {
		String[][] layouts = new String[][] {
				new String[]{"Select", "Select", "Select parts of the graph", "layout/select.gif"},
				new String[]{"Zoom In", "Zoom In", "Make graph look bigger", "layout/zoomin.gif"},
				new String[]{"Zoom Out", "Zoom Out", "Make graph look smaller", "layout/zoomout.gif"},
				new String[]{"Random", "Random Layout", "Reconfigure graph randomly", "layout/random.gif"},
				new String[]{"Circular", "Circular Layout", "Reconfigure graph circular", "layout/circular.gif"},
				new String[]{"Annealed", "Annealed Layout", "Reconfigure graph by annealing", "layout/annealed.gif"},
				new String[]{"Levelled", "Levelled Layout", "Reconfigure graph in levels", "layout/levelled.gif"},
				new String[]{"Relaxed", "Relaxed Layout", "Reconfigure graph by relaxing", "layout/relaxed.gif"},
				new String[]{"Reactions Only", "Reactions Only", "Show only Reactions",	"layout/level1.gif"},
				new String[]{"Reaction Network", "Reaction Network", "Reaction Network", "layout/level2.gif"},
				new String[]{"Components", "Components", "Reactions, entities and components", "layout/level3.gif"},								
		};

		String[][] interactions = new String[][] {
				new String[]{"Reaction", "Biochemical Reaction", "Biochemical Reaction", 
						"biopax/biochemicalReaction.gif"},
				new String[]{"Transport", "Transport", "Transport", "biopax/transport.gif"},
				new String[]{"Reaction WT", "Biochemical Reaction with Transport", 
						"Biochemical Reaction with Transport", 
						"biopax/transportWithBiochemicalReaction.gif"},
				new String[]{"Entity", "Physical Entity", "Physical Entity", "biopax/entity.gif"},
				new String[]{"Small Molecule", "Small Molecule", "Small Molecule", "biopax/smallMolecule.gif"},
				new String[]{"Protein", "Protein", "Protein", "biopax/protein.gif"},
				new String[]{"Complex", "Complex", "Complex", "biopax/complex.gif"},
				new String[]{"Participant", "Participant", "Physical Entity Participant", "biopax/modification.gif"},
		};
		
		JToolBar layoutToolBar = createToolBar(layouts, javax.swing.SwingConstants.VERTICAL);
		JToolBar nodesToolBar = createToolBar(interactions, javax.swing.SwingConstants.HORIZONTAL);
		sourceTextArea = new JTextArea();		
		graphPane =  new GraphPane();
		graphCartoonTool = new SybilGraphTool();
		graphCartoonTool.setGraphPane(graphPane);	
		
		JPanel graphTabPanel = new JPanel(new BorderLayout());
		graphTabPanel.add(new JScrollPane(graphPane), BorderLayout.CENTER);
		graphTabPanel.add(layoutToolBar, BorderLayout.WEST);
		graphTabPanel.add(nodesToolBar, BorderLayout.NORTH);
		
		JPanel sourceTabPanel = new JPanel(new BorderLayout());
		sourceTabPanel.add(new JScrollPane(sourceTextArea), BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Diagram", graphTabPanel);
		tabbedPane.addTab("BioPax Source", sourceTabPanel);
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
	}

	private void createGraph(Graph graph, SBBox sbbox) throws InterruptedException, GraphManipulationException {
		Model model = sbbox.getRdf();
		StmtIterator stmtIter = model.listStatements();
		RDFGraphCompTagCreator<Shape, Graph> tag = new RDFGraphCompTagCreator<Shape, Graph>(model, graph, 
				new GraphCreationMethod("Sybil Graph Factory"));
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			RDFNode theObject = statement.getObject();
			if(theObject.isResource()) {
				Resource subject = statement.getSubject();
				Resource object = (Resource) theObject;
				graph.addEdge(sbbox, new SBWrapper(sbbox, subject), new SBWrapper(sbbox, object), statement, tag);									
			}
		}
		GraphGrouper<Shape, Graph> grouper = new GraphGrouper<Shape, Graph>(fileManager.evaluator());
		grouper.applyToGraph(graph);
		ReactionsManipulator<Shape, Graph> manip = new ReactionsManipulator<Shape, Graph>(fileManager.evaluator());
		manip.setCollapseParticipants(true);
//		manip.applyToGraph(graph);
		graph.layoutGraph(LayoutType.Randomizer);
		graph.layoutGraph(LayoutType.Annealer);
		graph.updateView();
	}

	private void refreshInterface() {
		if (bioModel == null) {
			return;
		}
		try {
			VCMetaData vcMetaData = bioModel.getVCMetaData();
			SBBox sbbox = vcMetaData.getSBbox();
			Graph graph = new Graph(sbbox);
			graphPane.setGraph(graph);
			createGraph(graph, sbbox);
			
			Model2JDOM model2jdom = new Model2JDOM();
			model2jdom.addModel(vcMetaData.getSBbox().getRdf(), vcMetaData.getBaseURI());
			Element root = model2jdom.root();
			sourceTextArea.setText(XmlUtil.xmlToString(root));
		} catch (Exception ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
	}

	public void setBioModel(BioModel bioModel) {
		if (this.bioModel == bioModel) {
			return;
		}
		this.bioModel = bioModel;
		fileManager = new FileManager(bioModel);
		bioModel.getVCMetaData().addAnnotationEventListener(eventHandler);
		refreshInterface();
	}
}
