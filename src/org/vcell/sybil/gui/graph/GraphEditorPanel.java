package org.vcell.sybil.gui.graph;

/*   SybilGraphModelPanel  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Panel for Graph, including buttons
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.vcell.sybil.gui.PopupManager;
import org.vcell.sybil.gui.graphinfo.SelectedResourcesPane;
import org.vcell.sybil.gui.graphinfo.SelectedStatementsPane;
//import org.vcell.sybil.gui.util.Sizer;
import org.vcell.sybil.models.graph.GraphModelSelectionInfo;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.actions.ActionMap;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.graph.components.GraphCompActions;
import org.vcell.sybil.actions.graph.layout.GraphLayoutActions;
import org.vcell.sybil.util.enumerations.SmartEnum;
import org.vcell.sybil.util.gui.ButtonFormatter;

public class GraphEditorPanel extends JPanel {
	
	private static final long serialVersionUID = 9089742614946693029L;
	protected SBBox box;
	private GraphPane graphPane;
	protected JScrollPane graphScrollPane;
	protected SelectedResourcesPane resourcesPane;
	protected SelectedStatementsPane statementsPane;
	protected JScrollPane resourcesScrollPane;
	protected JScrollPane statementsScrollPane;
	protected JSplitPane innerSplitPane;
	protected JSplitPane outerSplitPane;
	private JToolBar layoutToolBar;
	private JToolBar nodesToolBar;
	private SybilGraphTool graphCartoonTool;
	private PopupManager popupManager;
	protected GraphModelSelectionInfo dataRelations;
		
	public GraphEditorPanel(SBBox box, ActionMap actionMapNew) {
		super();
		this.box = box;
		graphPane = createGraphPane();
		graphPane.setGraph(new Graph(box));
		graphCartoonTool = new SybilGraphTool();
		graphCartoonTool.setGraphPane(graphPane);
		setLayout(new BorderLayout());
		layoutToolBar = createLayoutToolBar(actionMapNew);
		nodesToolBar = createNodesToolBar(actionMapNew);
		graphScrollPane = createGraphScrollPane(graphPane);
		graphScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		graphScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dataRelations = new GraphModelSelectionInfo(graphPane.graph().model());
		resourcesPane = createResourcesPane();
		// resourcesScrollPane = new JScrollPane(resourcesPane);
		statementsPane = createStatementsPane();
		//statementsScrollPane = new JScrollPane(statementsPane);
		innerSplitPane = createInnerSplitPane(graphScrollPane, resourcesPane);
		outerSplitPane = createOuterSplitPane(innerSplitPane, statementsPane);
		add(outerSplitPane, "Center");
		add(layoutToolBar, "West");
		add(nodesToolBar, "North");
		popupManager = new PopupManager(actionMapNew);
		graphPane.addMouseListener(popupManager);
	}

	private GraphPane createGraphPane() {
		GraphPane pane = new GraphPane();
		pane.setName("GraphPane");
		pane.setBounds(0, 0, 372, 364);
		return pane;	
	}

	private JScrollPane createGraphScrollPane(GraphPane graphPane) {
		JScrollPane pane = new JScrollPane();
		pane.setName("JScrollPane1");
		pane.setViewportView(graphPane);
		return pane;
	}
	
	protected SelectedResourcesPane createResourcesPane() {
		SelectedResourcesPane pane = new SelectedResourcesPane(box, "<html>" +
				"<font color=blue>Selected graph nodes and edges</font></html>", 
				"Select graph elements to display properties");
		pane.setDataRelations(dataRelations);
		return pane;
	}
	
	protected SelectedStatementsPane createStatementsPane() {
		SelectedStatementsPane pane = new SelectedStatementsPane();
		pane.setGraphModelSelectionInfo(dataRelations);
		return pane;
	}
	
	protected JScrollPane createGraphInfoScrollPane2(JPanel infoPane) {
		return new JScrollPane(infoPane);
	}
	
	protected JSplitPane createInnerSplitPane(JComponent panel1, JComponent panel2) {
		JSplitPane pane =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2);
		//pane.setDividerLocation((int)(0.6*Sizer.initialSize().getWidth()));
		return pane;
	}
	
	protected JSplitPane createOuterSplitPane(JComponent panel1, JComponent panel2) {
		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel1, panel2);
		//pane.setDividerLocation((int)(0.6*Sizer.initialSize().getHeight()));
		return pane;
	}
	
	private JButton createLayoutToolBarButton(Action action) {
		JButton button = new JButton(action);
		ButtonFormatter.format(button);
		button.setMaximumSize(new Dimension(28, 28));
		button.setPreferredSize(new Dimension(28, 28));
		button.setFont(new Font("Arial", 1, 10));
		button.setMinimumSize(new Dimension(28, 28));
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setToolTipText((String)action.getValue(Action.SHORT_DESCRIPTION));
		if(button.getIcon() != null) { button.setText(null); }
		return button;
	}

	private JToolBar createLayoutToolBar(ActionMap actionMapNew) {
		JToolBar toolBar = new JToolBar();
		toolBar.setName("layoutToolBar");
		toolBar.setFloatable(false);
		toolBar.setBorder(new javax.swing.border.EtchedBorder());
		toolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
		SmartEnum<BaseAction> actions = actionMapNew.actions(GraphLayoutActions.class);
		while(actions.hasMoreElements()) {
			toolBar.add(createLayoutToolBarButton(actions.nextElement()));
			if(actions.isAtInternalBoundary()) { toolBar.addSeparator(); }
		}
		return toolBar;
	}

	private JButton nodesToolBarButton(Action action) {
		JButton button = new JButton(action);
		ButtonFormatter.format(button);
		button.setMaximumSize(new Dimension(28, 28));
		button.setPreferredSize(new Dimension(28, 28));
		button.setFont(new Font("Arial", 1, 10));
		button.setMinimumSize(new Dimension(28, 28));
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setToolTipText((String)action.getValue(Action.SHORT_DESCRIPTION));
		if(button.getIcon() != null) { button.setText(null); }
		return button;
	}

	private JToolBar createNodesToolBar(ActionMap actionMapNew) {
		JToolBar toolBar = new JToolBar();
		toolBar.setName("nodesToolBar");
		toolBar.setFloatable(false);
		toolBar.setBorder(new javax.swing.border.EtchedBorder());
		toolBar.setOrientation(javax.swing.SwingConstants.HORIZONTAL);
		SmartEnum<BaseAction> actions = actionMapNew.actions(GraphCompActions.class);
		while(actions.hasMoreElements()) {
			toolBar.add(nodesToolBarButton(actions.nextElement()));
			if(actions.isAtInternalBoundary()) { toolBar.addSeparator(); }
		}
		return toolBar;
	}

  	public GraphPane graphPane() { return graphPane; }
	public Graph graph() { return graphPane().graph(); }
	
}
