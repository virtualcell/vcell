package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
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
	public static final Dimension TOOL_BAR_SEPARATOR_SIZE = new Dimension(10,0);
	public static final String PROPERTY_NAME_FLOATING = "Floating";
	public static final Dimension TOOL_BAR_BUTTON_SIZE = new Dimension(28, 28);
	private GraphPane graphPane = null;
	private JToolBar toolBar = null;
	private JToolBarToggleButton speciesButton = null;
//	private JToolBarToggleButton fluxButton = null;
//	private JToolBarToggleButton lineButton = null;
	private JToolBarToggleButton lineDirectedButton = null;
	private JToolBarToggleButton lineCatalystButton = null;
	private JToolBarToggleButton selectButton = null;
//	private JToolBarToggleButton stepButton = null;
	protected List<JToolBarToggleButton> modeButtons = null;
	private ButtonGroup modeButtonGroup = new ButtonGroup();
	private JScrollPane scrollPane = null;
	private JButton annealLayoutButton = null;
	private JButton circleLayoutButton = null;
	private JButton levellerLayoutButton = null;
	private JButton randomLayoutButton = null;
	private JButton relaxerLayoutButton = null;
	private JButton zoomInButton = null;
	private JButton zoomOutButton = null;
	private JButton glgLayoutJButton = null;
	private final ReactionCartoon reactionCartoon = new ReactionCartoon();
	private final ReactionCartoonTool reactionCartoonTool = new ReactionCartoonTool();

	private boolean bFloatingRequested = false;
	private JButton floatRequestButton = null;
	
	public final static String IMAGE_PATH = "/sybil/images/layout/";
	private final static Icon randomLayoutIcon = loadIcon(IMAGE_PATH + "random.gif");
	private final static Icon circleLayoutIcon = loadIcon(IMAGE_PATH + "circular.gif");
	private final static Icon annealedLayoutIcon = loadIcon(IMAGE_PATH + "annealed.gif");
	private final static Icon levelledLayoutIcon = loadIcon(IMAGE_PATH + "levelled.gif");
	private final static Icon relaxedLayoutIcon = loadIcon(IMAGE_PATH + "relaxed.gif");

	
	public ReactionCartoonEditorPanel() {
		super();
		initialize();
	}

	private static Icon loadIcon(String fileName) {
		return new ImageIcon(ReactionCartoonEditorPanel.class.getResource(fileName));
	}
	
	public void actionPerformed(ActionEvent event) {
		try {
			Object source = event.getSource();
			if (getModeButtons().contains(source))
				getReactionCartoonTool().setModeString(event.getActionCommand());
			else if (source == getRandomLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.OldLayouts.RANDOMIZER);
			else if (source == getAnnealLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.OldLayouts.ANNEALER);
			else if (source == getCircleLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.OldLayouts.CIRCULARIZER);
			else if (source == getRelaxerLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.OldLayouts.RELAXER);
			else if (source == getLevellerLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.OldLayouts.LEVELLER);
			else if (source == getZoomInButton())
				getReactionCartoon().getResizeManager().zoomIn();
			else if (source == getZoomOutButton())
				this.getReactionCartoon().getResizeManager().zoomOut();
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
				annealLayoutButton = createToolBarButton();
				annealLayoutButton.setName("AnnealLayoutButton");
				annealLayoutButton.setToolTipText("Annealing Layout");
				annealLayoutButton.setIcon(annealedLayoutIcon);
				annealLayoutButton.setActionCommand("AnnealLayout");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return annealLayoutButton;
	}

	private JButton getCircleLayoutButton() {
		if (circleLayoutButton == null) {
			try {
				circleLayoutButton = createToolBarButton();
				circleLayoutButton.setName("CircleLayoutButton");
				circleLayoutButton.setToolTipText("Circular Layout");
				circleLayoutButton.setIcon(circleLayoutIcon);
				circleLayoutButton.setActionCommand("CircleLayout");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return circleLayoutButton;
	}

	public DocumentManager getDocumentManager() {
		return getReactionCartoonTool().getDocumentManager();
	}

//	private JToolBarToggleButton getFluxButton() {
//		if (fluxButton == null) {
//			try {
//				fluxButton = new JToolBarToggleButton();
//				fluxButton.setName("FluxButton");
//				fluxButton.setToolTipText("Flux Tool");
//				fluxButton.setText("");
//				fluxButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
//				fluxButton.setActionCommand(Mode.FLUX.getActionCommand());
//				fluxButton.setIcon(new ImageIcon(getClass().getResource("/images/flux.gif")));
//				fluxButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
//				fluxButton.setEnabled(true);
//				fluxButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
//			} catch (Throwable throwable) {
//				handleException(throwable);
//			}
//		}
//		return fluxButton;
//	}

	private JButton getGlgLayoutJButton() {
		if (glgLayoutJButton == null) {
			try {
				glgLayoutJButton = createToolBarButton();
				glgLayoutJButton.setName("GlgLayoutJButton");
				glgLayoutJButton.setToolTipText("Layout GLG");
				glgLayoutJButton.setText("GLG");
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
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
				toolBar.setName("toolBar");
				toolBar.setFloatable(false);
				toolBar.setOrientation(SwingConstants.HORIZONTAL);
				toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
				toolBar.add(getSelectButton(), getSelectButton().getName());
				toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
				toolBar.add(getSpeciesButton(), getSpeciesButton().getName());
				//toolBar.add(getStepButton(), getStepButton().getName());
				//toolBar.add(getFluxButton(), getFluxButton().getName());
				//toolBar.add(getLineButton(), getLineButton().getName());
				toolBar.add(getLineDirectedButton(), getLineDirectedButton().getName());
				toolBar.add(getLineCatalystButton(), getLineCatalystButton().getName());
				toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
				toolBar.add(getZoomInButton(), getZoomInButton().getName());
				toolBar.add(getZoomOutButton(), getZoomOutButton().getName());
				toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
				toolBar.add(getRandomLayoutButton(), getRandomLayoutButton().getName());
				toolBar.add(getCircleLayoutButton(), getCircleLayoutButton().getName());
				toolBar.add(getAnnealLayoutButton(), getAnnealLayoutButton().getName());
				toolBar.add(getLevellerLayoutButton(), getLevellerLayoutButton().getName());
				toolBar.add(getRelaxerLayoutButton(), getRelaxerLayoutButton().getName());
				toolBar.add(getGlgLayoutJButton(), getGlgLayoutJButton().getName());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return toolBar;
	}

	private JButton createToolBarButton() {
		JButton button = new JButton();
		button.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
		button.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
		button.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
		button.setMargin(new Insets(2, 2, 2, 2));
//		button.setFont(new Font("Arial", 1, 10));
		button.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		return button;
	}
	
	private JButton getLevellerLayoutButton() {
		if (levellerLayoutButton == null) {
			try {
				levellerLayoutButton = createToolBarButton();
				levellerLayoutButton.setName("LevellerLayoutButton");
				levellerLayoutButton.setToolTipText("Leveller Layout");
				levellerLayoutButton.setIcon(levelledLayoutIcon);
				levellerLayoutButton.setActionCommand("LevellerLayout");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return levellerLayoutButton;
	}

//	private JToolBarToggleButton getLineButton() {
//		if (lineButton == null) {
//			try {
//				lineButton = new JToolBarToggleButton();
//				lineButton.setName("LineButton");
//				lineButton.setToolTipText("RX Connection Tool");
//				lineButton.setText("");
//				lineButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
//				lineButton.setActionCommand(Mode.LINE.getActionCommand());
//				lineButton.setIcon(new ImageIcon(getClass().getResource("/images/line.gif")));
//				lineButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
//				lineButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
//			} catch (Throwable throwable) {
//				handleException(throwable);
//			}
//		}
//		return lineButton;
//	}

	private JToolBarToggleButton getLineDirectedButton() {
		if (lineDirectedButton == null) {
			try {
				lineDirectedButton = createModeButton("LineButton", "RX Connection Tool", 
						Mode.LINEDIRECTED, loadIcon("/images/lineDirected.gif"));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return lineDirectedButton;
	}

	private JToolBarToggleButton getLineCatalystButton() {
		if (lineCatalystButton == null) {
			try {
				lineCatalystButton = createModeButton("LineCatalystButton", "Set a catalyst", 
						Mode.LINECATALYST, loadIcon("/images/lineCatalyst.gif"));
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
				randomLayoutButton = createToolBarButton();
				randomLayoutButton.setName("RandomLayoutButton");
				randomLayoutButton.setToolTipText("Random Layout");
				randomLayoutButton.setIcon(randomLayoutIcon);
				randomLayoutButton.setActionCommand("RandomLayout");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return randomLayoutButton;
	}

	public ReactionCartoon getReactionCartoon() { return reactionCartoon; }
	private ReactionCartoonTool getReactionCartoonTool() { return reactionCartoonTool; }

	private JButton getRelaxerLayoutButton() {
		if (relaxerLayoutButton == null) {
			try {
				relaxerLayoutButton = createToolBarButton();
				relaxerLayoutButton.setName("RelaxerLayoutButton");
				relaxerLayoutButton.setToolTipText("Relaxer Layou");
				relaxerLayoutButton.setIcon(relaxedLayoutIcon);
				relaxerLayoutButton.setActionCommand("RelaxerLayout");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return relaxerLayoutButton;
	}

	private JToolBarToggleButton getSelectButton() {
		if (selectButton == null) {
			try {
				selectButton = createModeButton("SelectButton", "Select Tool", 
						Mode.SELECT, loadIcon("/images/select.gif"));
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
			//modeButtons.add(getStepButton());
			//modeButtons.add(getFluxButton());
			//modeButtons.add(getLineButton());
			modeButtons.add(getLineDirectedButton());
			modeButtons.add(getLineCatalystButton());
		}
		return modeButtons;
	}
	
	private JToolBarToggleButton createModeButton(String name, String toolTip, Mode mode, Icon icon) {
		JToolBarToggleButton button = new JToolBarToggleButton();
		button.setName(name);
		button.setToolTipText(toolTip);
		button.setActionCommand(mode.getActionCommand());
		button.setIcon(icon);
		setToolBarButtonSizes(button);
		return button;
	}

	private JToolBarToggleButton getSpeciesButton() {
		if (speciesButton == null) {
			try {
				speciesButton = createModeButton("SpeciesButton", "Species Tool", 
						Mode.SPECIES, loadIcon("/images/species.gif"));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return speciesButton;
	}

//	private JToolBarToggleButton getStepButton() {
//		if (stepButton == null) {
//			try {
//				stepButton = new JToolBarToggleButton();
//				stepButton.setName("StepButton");
//				stepButton.setToolTipText("Reaction Tool");
//				stepButton.setText("");
//				stepButton.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
//				stepButton.setActionCommand(Mode.STEP.getActionCommand());
//				stepButton.setIcon(new ImageIcon(getClass().getResource("/images/step.gif")));
//				stepButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
//				stepButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
//			} catch (Throwable throwable) {
//				handleException(throwable);
//			}
//		}
//		return stepButton;
//	}

	private JButton getZoomInButton() {
		if (zoomInButton == null) {
			try {
				zoomInButton = createToolBarButton();
				zoomInButton.setName("ZoomInButton");
				zoomInButton.setToolTipText("Zoom In");
				zoomInButton.setActionCommand("ZoomIn");
				zoomInButton.setIcon(loadIcon("/images/zoomin.gif"));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return zoomInButton;
	}
	
	private JButton getFloatRequestButton() {
		if (floatRequestButton == null) {
			try {
				floatRequestButton = new JButton();
				floatRequestButton.setText("\u21b1");
				floatRequestButton.setName("FloatingButton");
				floatRequestButton.setFont(floatRequestButton.getFont().deriveFont(Font.BOLD));
				floatRequestButton.setToolTipText("\u21b1 Float");
				setToolBarButtonSizes(floatRequestButton);
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
				zoomOutButton = createToolBarButton();
				zoomOutButton.setName("ZoomOutButton");
				zoomOutButton.setToolTipText("Zoom Out");
				zoomOutButton.setActionCommand("ZoomOut");
				zoomOutButton.setIcon(loadIcon("/images/zoomout.gif"));
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
		ButtonModel selection = modeButtonGroup.getSelection();
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
			
			
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			panel.add(getJToolBar(), gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			panel.add(getFloatRequestButton(), gbc);
			
			panel.setBorder(new EtchedBorder());
			
			add(panel, BorderLayout.NORTH);
			initConnections();
			//getModeButtonGroup().add(getStepButton());
			//getModeButtonGroup().add(getFluxButton());
			//getModeButtonGroup().add(getLineButton());
			modeButtonGroup.add(getLineDirectedButton());
			modeButtonGroup.add(getLineCatalystButton());
			modeButtonGroup.add(getSelectButton());
			modeButtonGroup.add(getSpeciesButton());
			getReactionCartoonTool().setReactionCartoon(getReactionCartoon());
			getReactionCartoonTool().setGraphPane(getGraphPane());
			getReactionCartoonTool().setButtonGroup(modeButtonGroup);
			getGraphPane().setGraphModel(getReactionCartoon());
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}
	
	protected void setToolBarButtonSizes(AbstractButton button) {
		button.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
		button.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
		button.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
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
	
	public final void setFloatingRequested(boolean newValue) {
		boolean oldValue = bFloatingRequested;
		this.bFloatingRequested = newValue;
		floatRequestButton.setVisible(!bFloatingRequested);
		firePropertyChange(PROPERTY_NAME_FLOATING, oldValue, newValue);
	}

}