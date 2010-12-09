package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.vcell.util.gui.JToolBarToggleButton;

import cbit.gui.graph.CartoonTool.Mode;
import cbit.gui.graph.GraphEmbeddingManager;
import cbit.gui.graph.GraphPane;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Model;

@SuppressWarnings("serial")
public class ReactionCartoonEditorPanel extends JPanel implements ActionListener {
	public static final Dimension TOOL_BAR_SEPARATOR_SIZE = new Dimension(5,10);
	public static final String PROPERTY_NAME_FLOATING = "Floating";
	private static final Dimension TOOL_BAR_BUTTON_SIZE = new Dimension(28, 28);
	private GraphPane graphPane = null;
	private JToolBar toolBar = null;
	private JToolBarToggleButton speciesButton = null;
	private JToolBarToggleButton fluxButton = null;
	private JToolBarToggleButton lineButton = null;
	private JToolBarToggleButton lineDirectedButton = null;
	private JToolBarToggleButton lineCatalystButton = null;
	private JToolBarToggleButton selectButton = null;
	private JToolBarToggleButton stepButton = null;
	protected List<JToolBarToggleButton> modeButtons = null;
	private ButtonGroup modeButtonGroup = null;
	private JScrollPane scrollPane = null;
	private JButton annealLayoutButton = null;
	private JButton circleLayoutButton = null;
	private JButton levellerLayoutButton = null;
	private JButton randomLayoutButton = null;
	private JButton relaxerLayoutButton = null;
	private JButton zoomInButton = null;
	private JButton zoomOutButton = null;
	private JButton glgLayoutJButton = null;
	private ReactionCartoon reactionCartoon = null;
	private ReactionCartoonTool reactionCartoonTool = null;

	private boolean bFloatingRequested = false;
	private JButton floatRequestButton = null;
	
	public ReactionCartoonEditorPanel() {
		super();
		initialize();
	}

	public void actionPerformed(ActionEvent event) {
		try {
			Object source = event.getSource();
			if (getModeButtons().contains(source))
				getReactionCartoonTool().setModeString(event.getActionCommand());
			else if (source == getRandomLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.RANDOMIZER);
			else if (source == getAnnealLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.ANNEALER);
			else if (source == getCircleLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.CIRCULARIZER);
			else if (source == getRelaxerLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.RELAXER);
			else if (source == getLevellerLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.LEVELLER);
			else if (source == getZoomInButton())
				this.zoomInButton_ActionPerformed();
			else if (source == getZoomOutButton())
				this.zoomOutButton_ActionPerformed();
			else if (source == getGlgLayoutJButton())
				getReactionCartoonTool().layoutGlg();
			else if (source == getFloatRequestButton()) 
				setFloatingRequested(!bFloatingRequested);
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}

	public void cleanupOnClose() {
		getReactionCartoon().cleanupAll();
	}

	private JButton getAnnealLayoutButton() {
		if (annealLayoutButton == null) {
			try {
				annealLayoutButton = new JButton();
				annealLayoutButton.setName("AnnealLayoutButton");
				annealLayoutButton.setToolTipText("Layout Annealing");
				annealLayoutButton.setText("anl");
				annealLayoutButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				annealLayoutButton.setActionCommand("AnnealLayout");
				annealLayoutButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				annealLayoutButton.setFont(new Font("Arial", 1, 10));
				annealLayoutButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				annealLayoutButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return annealLayoutButton;
	}

	private ButtonGroup getModeButtonGroup() {
		if (modeButtonGroup == null) {
			try {
				modeButtonGroup = new ButtonGroup();
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return modeButtonGroup;
	}

	private JButton getCircleLayoutButton() {
		if (circleLayoutButton == null) {
			try {
				circleLayoutButton = new JButton();
				circleLayoutButton.setName("CircleLayoutButton");
				circleLayoutButton.setToolTipText("Layout Circular");
				circleLayoutButton.setText("crc");
				circleLayoutButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				circleLayoutButton.setActionCommand("CircleLayout");
				circleLayoutButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				circleLayoutButton.setFont(new Font("Arial", 1, 10));
				circleLayoutButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				circleLayoutButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return circleLayoutButton;
	}

	public DocumentManager getDocumentManager() {
		return getReactionCartoonTool().getDocumentManager();
	}

	private JToolBarToggleButton getFluxButton() {
		if (fluxButton == null) {
			try {
				fluxButton = new JToolBarToggleButton();
				fluxButton.setName("FluxButton");
				fluxButton.setToolTipText("Flux Tool");
				fluxButton.setText("");
				fluxButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				fluxButton.setActionCommand(Mode.FLUX.getActionCommand());
				fluxButton.setIcon(new ImageIcon(getClass().getResource("/images/flux.gif")));
				fluxButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				fluxButton.setEnabled(true);
				fluxButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return fluxButton;
	}

	private JButton getGlgLayoutJButton() {
		if (glgLayoutJButton == null) {
			try {
				glgLayoutJButton = new JButton();
				glgLayoutJButton.setName("GlgLayoutJButton");
				glgLayoutJButton.setToolTipText("Layout GLG");
				glgLayoutJButton.setText("glg");
				glgLayoutJButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				glgLayoutJButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				glgLayoutJButton.setFont(new Font("Arial", 1, 10));
				glgLayoutJButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				glgLayoutJButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return glgLayoutJButton;
	}

	private GraphPane getGraphPane() {
		if (graphPane == null) {
			try {
				graphPane = new GraphPane();
				graphPane.setName("GraphPane");
				graphPane.setBounds(0, 0, 372, 364);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return graphPane;
	}

	private JScrollPane getJScrollPane() {
		if (scrollPane == null) {
			try {
				scrollPane = new JScrollPane();
				scrollPane.setName("JScrollPane1");
				scrollPane.setPreferredSize(new Dimension(22, 396));
//				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//				scrollPane.setMinimumSize(new Dimension(22, 396));
				getJScrollPane().setViewportView(getGraphPane());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return scrollPane;
	}

	private JToolBar getJToolBar() {
		if (toolBar == null) {
			try {
				toolBar = new JToolBar();
				toolBar.setName("JToolBar1");
				toolBar.setFloatable(false);
				toolBar.setBorder(new EtchedBorder());
				toolBar.setOrientation(SwingConstants.HORIZONTAL);
				getJToolBar().add(getSelectButton(), getSelectButton().getName());
				getJToolBar().addSeparator(TOOL_BAR_SEPARATOR_SIZE);
				getJToolBar().add(getSpeciesButton(), getSpeciesButton().getName());
				getJToolBar().add(getStepButton(), getStepButton().getName());
				getJToolBar().add(getFluxButton(), getFluxButton().getName());
				getJToolBar().add(getLineButton(), getLineButton().getName());
				getJToolBar().add(getLineDirectedButton(), getLineDirectedButton().getName());
				getJToolBar().add(getLineCatalystButton(), getLineCatalystButton().getName());
				getJToolBar().addSeparator(TOOL_BAR_SEPARATOR_SIZE);
				getJToolBar().add(getZoomInButton(), getZoomInButton().getName());
				getJToolBar().add(getZoomOutButton(), getZoomOutButton().getName());
				getJToolBar().add(getRandomLayoutButton(), getRandomLayoutButton().getName());
				getJToolBar().add(getCircleLayoutButton(), getCircleLayoutButton().getName());
				getJToolBar().add(getAnnealLayoutButton(), getAnnealLayoutButton().getName());
				getJToolBar().add(getLevellerLayoutButton(), getLevellerLayoutButton().getName());
				getJToolBar().add(getRelaxerLayoutButton(), getRelaxerLayoutButton().getName());
				getJToolBar().add(getGlgLayoutJButton(), getGlgLayoutJButton().getName());
				getJToolBar().add(getFloatRequestButton(), getFloatRequestButton().getName());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return toolBar;
	}

	private JButton getLevellerLayoutButton() {
		if (levellerLayoutButton == null) {
			try {
				levellerLayoutButton = new JButton();
				levellerLayoutButton.setName("LevellerLayoutButton");
				levellerLayoutButton.setToolTipText("Layout Leveler");
				levellerLayoutButton.setText("lev");
				levellerLayoutButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				levellerLayoutButton.setActionCommand("LevellerLayout");
				levellerLayoutButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				levellerLayoutButton.setFont(new Font("Arial", 1, 10));
				levellerLayoutButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				levellerLayoutButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return levellerLayoutButton;
	}

	private JToolBarToggleButton getLineButton() {
		if (lineButton == null) {
			try {
				lineButton = new JToolBarToggleButton();
				lineButton.setName("LineButton");
				lineButton.setToolTipText("RX Connection Tool");
				lineButton.setText("");
				lineButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				lineButton.setActionCommand(Mode.LINE.getActionCommand());
				lineButton.setIcon(new ImageIcon(getClass().getResource("/images/line.gif")));
				lineButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				lineButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return lineButton;
	}

	private JToolBarToggleButton getLineDirectedButton() {
		if (lineDirectedButton == null) {
			try {
				lineDirectedButton = new JToolBarToggleButton();
				lineDirectedButton.setName("LineButton");
				lineDirectedButton.setToolTipText("RX Connection Tool");
				lineDirectedButton.setText("");
				lineDirectedButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				lineDirectedButton.setActionCommand(Mode.LINEDIRECTED.getActionCommand());
				lineDirectedButton.setIcon(
						new ImageIcon(getClass().getResource("/images/lineDirected.gif")));
				lineDirectedButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				lineDirectedButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return lineDirectedButton;
	}

	private JToolBarToggleButton getLineCatalystButton() {
		if (lineCatalystButton == null) {
			try {
				lineCatalystButton = new JToolBarToggleButton();
				lineCatalystButton.setName("LineCatalystButton");
				lineCatalystButton.setToolTipText("Set a catalyst");
				lineCatalystButton.setText("");
				lineCatalystButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				lineCatalystButton.setActionCommand(Mode.LINECATALYST.getActionCommand());
				lineCatalystButton.setIcon(new ImageIcon(
						getClass().getResource("/images/lineCatalyst.gif")));
				lineCatalystButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				lineCatalystButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return lineCatalystButton;
	}

	public Model getModel() {
		return getReactionCartoon().getModel();
	}

	private JButton getRandomLayoutButton() {
		if (randomLayoutButton == null) {
			try {
				randomLayoutButton = new JButton();
				randomLayoutButton.setName("RandomLayoutButton");
				randomLayoutButton.setToolTipText("Layout Random");
				randomLayoutButton.setText("rnd");
				randomLayoutButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				randomLayoutButton.setActionCommand("RandomLayout");
				randomLayoutButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				randomLayoutButton.setFont(new Font("Arial", 1, 10));
				randomLayoutButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				randomLayoutButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return randomLayoutButton;
	}

	public ReactionCartoon getReactionCartoon() {
		if (reactionCartoon == null) {
			try {
				reactionCartoon = new ReactionCartoon();
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return reactionCartoon;
	}

	private ReactionCartoonTool getReactionCartoonTool() {
		if (reactionCartoonTool == null) {
			try {
				reactionCartoonTool = new ReactionCartoonTool();
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return reactionCartoonTool;
	}

	private JButton getRelaxerLayoutButton() {
		if (relaxerLayoutButton == null) {
			try {
				relaxerLayoutButton = new JButton();
				relaxerLayoutButton.setName("RelaxerLayoutButton");
				relaxerLayoutButton.setToolTipText("Layout Relaxer");
				relaxerLayoutButton.setText("rlx");
				relaxerLayoutButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				relaxerLayoutButton.setActionCommand("RelaxerLayout");
				relaxerLayoutButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				relaxerLayoutButton.setFont(new Font("Arial", 1, 10));
				relaxerLayoutButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				relaxerLayoutButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return relaxerLayoutButton;
	}

	private JToolBarToggleButton getSelectButton() {
		if (selectButton == null) {
			try {
				selectButton = new JToolBarToggleButton();
				selectButton.setName("SelectButton");
				selectButton.setToolTipText("Select Tool");
				selectButton.setText("");
				selectButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				selectButton.setActionCommand(Mode.SELECT.getActionCommand());
				selectButton.setSelected(true);
				selectButton.setIcon(new ImageIcon(getClass().getResource("/images/select.gif")));
				selectButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				selectButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return selectButton;
	}
	
	protected List<JToolBarToggleButton> getModeButtons() {
		if(modeButtons == null) {
			modeButtons = new ArrayList<JToolBarToggleButton>();
			modeButtons.add(getSelectButton());
			modeButtons.add(getSpeciesButton());
			modeButtons.add(getStepButton());
			modeButtons.add(getFluxButton());
			modeButtons.add(getLineButton());
			modeButtons.add(getLineDirectedButton());
			modeButtons.add(getLineCatalystButton());
		}
		return modeButtons;
	}

	private JToolBarToggleButton getSpeciesButton() {
		if (speciesButton == null) {
			try {
				speciesButton = new JToolBarToggleButton();
				speciesButton.setName("SpeciesButton");
				speciesButton.setToolTipText("Species Tool");
				speciesButton.setText("");
				speciesButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				speciesButton.setActionCommand(Mode.SPECIES.getActionCommand());
				speciesButton.setIcon(new ImageIcon(getClass().getResource("/images/species.gif")));
				speciesButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				speciesButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return speciesButton;
	}

	private JToolBarToggleButton getStepButton() {
		if (stepButton == null) {
			try {
				stepButton = new JToolBarToggleButton();
				stepButton.setName("StepButton");
				stepButton.setToolTipText("Reaction Tool");
				stepButton.setText("");
				stepButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				stepButton.setActionCommand(Mode.STEP.getActionCommand());
				stepButton.setIcon(new ImageIcon(getClass().getResource("/images/step.gif")));
				stepButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				stepButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return stepButton;
	}

	private JButton getZoomInButton() {
		if (zoomInButton == null) {
			try {
				zoomInButton = new JButton();
				zoomInButton.setName("ZoomInButton");
				zoomInButton.setToolTipText("Zoom In");
				zoomInButton.setText("");
				zoomInButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				zoomInButton.setActionCommand("ZoomIn");
				zoomInButton.setIcon(new ImageIcon(getClass().getResource("/images/zoomin.gif")));
				zoomInButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				zoomInButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				zoomInButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return zoomInButton;
	}
	
	private JButton getFloatRequestButton() {
		if (floatRequestButton == null) {
			try {
				floatRequestButton = new JButton("\u21b1");
				floatRequestButton.setName("FloatingButton");
				floatRequestButton.setToolTipText("\u21b1 Float");
				floatRequestButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				floatRequestButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				floatRequestButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				floatRequestButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return floatRequestButton;
	}

	private JButton getZoomOutButton() {
		if (zoomOutButton == null) {
			try {
				zoomOutButton = new JButton();
				zoomOutButton.setName("ZoomOutButton");
				zoomOutButton.setToolTipText("Zoom Out");
				zoomOutButton.setText("");
				zoomOutButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
				zoomOutButton.setActionCommand("ZoomOut");
				zoomOutButton.setIcon(new ImageIcon(getClass().getResource("/images/zoomout.gif")));
				zoomOutButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				zoomOutButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return zoomOutButton;
	}

	// TODO centralize exception handling
	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in CartoonPanel");
		exception.printStackTrace(System.out);
	}

	private void initConnections() throws Exception {
		for(JToolBarToggleButton modeButton : getModeButtons()) {
			modeButton.addActionListener(this);
		}
		ButtonModel selection = getModeButtonGroup().getSelection();
		if(selection != null) {
			getReactionCartoonTool().setModeString(selection.getActionCommand());
		} else {
			getReactionCartoonTool().setMode(Mode.SELECT);
		}
		getRandomLayoutButton().addActionListener(this);
		getAnnealLayoutButton().addActionListener(this);
		getCircleLayoutButton().addActionListener(this);
		getRelaxerLayoutButton().addActionListener(this);
		getLevellerLayoutButton().addActionListener(this);
		getZoomInButton().addActionListener(this);
		getZoomOutButton().addActionListener(this);
		getGlgLayoutJButton().addActionListener(this);
		getFloatRequestButton().addActionListener(this);
	}

	private void initialize() {
		try {
			setName("CartoonPanel");
			setPreferredSize(new Dimension(54, 425));
			setLayout(new BorderLayout());
			setSize(472, 422);
//			setMinimumSize(new Dimension(54, 425));
			add(getJScrollPane(), BorderLayout.CENTER);
			add(getJToolBar(), BorderLayout.NORTH);
			initConnections();
			getModeButtonGroup().add(getStepButton());
			getModeButtonGroup().add(getFluxButton());
			getModeButtonGroup().add(getLineButton());
			getModeButtonGroup().add(getLineDirectedButton());
			getModeButtonGroup().add(getLineCatalystButton());
			getModeButtonGroup().add(getSelectButton());
			getModeButtonGroup().add(getSpeciesButton());
			getReactionCartoonTool().setReactionCartoon(getReactionCartoon());
			getReactionCartoonTool().setGraphPane(getGraphPane());
			getReactionCartoonTool().setButtonGroup(getModeButtonGroup());
			getGraphPane().setGraphModel(getReactionCartoon());
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}

	public static void main(String[] args) {
		try {
			Frame frame = new Frame();
			ReactionCartoonEditorPanel aReactionCartoonEditorPanel;
			aReactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
			frame.add("Center", aReactionCartoonEditorPanel);
			frame.setSize(aReactionCartoonEditorPanel.getSize());
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Panel");
			exception.printStackTrace(System.out);
		}
	}

	public void setDocumentManager(DocumentManager documentManager) {
		getReactionCartoonTool().setDocumentManager(documentManager);
	}

	public void setModel(Model model) {
		getReactionCartoon().setModel(model);
	}

	public void setStructureSuite(StructureSuite structureSuite) {
		getReactionCartoon().setStructureSuite(structureSuite);
	}
	
	private void zoomInButton_ActionPerformed() {
		if (getReactionCartoon()!=null){
			switch (getReactionCartoon().getZoomPercent()){
			case 195: {
				// already at top, do nothing
				break;
			}
			case 156: {
				getReactionCartoon().setZoomPercent(195);
				break;
			}
			case 125: {
				getReactionCartoon().setZoomPercent(156);
				break;
			}
			case 100: {
				getReactionCartoon().setZoomPercent(125);
				break;
			}
			case 80: {
				getReactionCartoon().setZoomPercent(100);
				break;
			}
			case 64: {
				getReactionCartoon().setZoomPercent(80);
				break;
			}
			case 50: {
				getReactionCartoon().setZoomPercent(64);
				break;
			}
			case 40: {
				getReactionCartoon().setZoomPercent(50);
				break;
			}
			case 30: {
				getReactionCartoon().setZoomPercent(40);
				break;
			}
			case 20: {
				getReactionCartoon().setZoomPercent(30);
				break;
			}
			case 10: {
				getReactionCartoon().setZoomPercent(20);
				break;
			}
			default: {
				getReactionCartoon().setZoomPercent(100);
				break;
			}
			}
		}
	}

	private void zoomOutButton_ActionPerformed() {
		if (getReactionCartoon()!=null){
			switch (getReactionCartoon().getZoomPercent()){
			case 195: {
				getReactionCartoon().setZoomPercent(156);
				break;
			}
			case 156: {
				getReactionCartoon().setZoomPercent(125);
				break;
			}
			case 125: {
				getReactionCartoon().setZoomPercent(100);
				break;
			}
			case 100: {
				getReactionCartoon().setZoomPercent(80);
				break;
			}
			case 80: {
				getReactionCartoon().setZoomPercent(64);
				break;
			}
			case 64: {
				getReactionCartoon().setZoomPercent(50);
				break;
			}
			case 50: {
				getReactionCartoon().setZoomPercent(40);
				break;
			}
			case 40: {
				getReactionCartoon().setZoomPercent(30);
				break;
			}
			case 30: {
				getReactionCartoon().setZoomPercent(20);
				break;
			}
			case 20: {
				getReactionCartoon().setZoomPercent(10);
				break;
			}
			case 10: {
				// can't zoom out any further
				break;
			}
			default: {
				getReactionCartoon().setZoomPercent(100);
				break;
			}
			}
		}
	}
	
	private final void setFloatingRequested(boolean newValue) {
		boolean oldValue = bFloatingRequested;
		this.bFloatingRequested = newValue;
		getFloatRequestButton().setText(bFloatingRequested ? "\u21b5" : "\u21b1");
		getFloatRequestButton().setToolTipText(bFloatingRequested ? "\u21b5 Dock" : "\u21b1 Float");
//		System.out.println("Set floating: " + bFloatingRequested);
		firePropertyChange(PROPERTY_NAME_FLOATING, oldValue, newValue);
	}
	
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		Debug.printComponentHierarchy(this);
//	}
}