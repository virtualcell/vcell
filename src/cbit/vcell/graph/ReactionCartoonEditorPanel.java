package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import cbit.gui.graph.GraphEmbeddingManager;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.vcell.util.gui.ButtonGroupCivilized;
import org.vcell.util.gui.JToolBarToggleButton;

import cbit.gui.graph.GraphPane;
import cbit.gui.graph.CartoonTool.Mode;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class ReactionCartoonEditorPanel extends JPanel implements ActionListener, PropertyChangeListener {
	private static final Dimension TOOL_BAR_BUTTON_SIZE = new Dimension(28, 28);
	private JPanel featureSizePanel = null;
	private GraphPane graphPane = null;
	protected transient PropertyChangeSupport propertyChange;
	private boolean connPtoP1Aligning = false;
	private JPanel panel = null;
	private JToolBar toolBar = null;
	private ButtonModel selection = null;
	private JToolBarToggleButton fluxButton = null;
	private JToolBarToggleButton lineButton = null;
	private JToolBarToggleButton selectButton = null;
	private JToolBarToggleButton stepButton = null;
	private ButtonGroupCivilized buttonGroupCivilized = null;
	private JScrollPane scrollPane = null;
	private JLabel label3 = null;
	private JButton annealLayoutButton = null;
	private JButton circleLayoutButton = null;
	private JLabel label4 = null;
	private JButton levellerLayoutButton = null;
	private JButton randomLayoutButton = null;
	private JButton relaxerLayoutButton = null;
	private JButton zoomInButton = null;
	private JButton zoomOutButton = null;
	private JButton glgLayoutJButton = null;
	private Structure structure = null;
	private JToolBarToggleButton speciesButton = null;
	private DocumentManager documentManager = null;
	private ReactionCartoon reactionCartoon = null;
	private ReactionCartoonTool reactionCartoonTool = null;
	private Model model = null;

	public ReactionCartoonEditorPanel() {
		super();
		initialize();
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == getRandomLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.RANDOMIZER);
			if (e.getSource() == getAnnealLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.ANNEALER);
			if (e.getSource() == getCircleLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.CIRCULARIZER);
			if (e.getSource() == getRelaxerLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.RELAXER);
			if (e.getSource() == getLevellerLayoutButton())
				getReactionCartoonTool().layout(GraphEmbeddingManager.LEVELLER);
			if (e.getSource() == getZoomInButton())
				zoomInButton_ActionPerformed();
			if (e.getSource() == getZoomOutButton())
				zoomOutButton_ActionPerformed();
			if (e.getSource() == getGlgLayoutJButton())
				getReactionCartoonTool().layoutGlg();
		} catch (Throwable throwable1) {
			handleException(throwable1);
		}
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public void cleanupOnClose() {
		getReactionCartoon().cleanupAll();
	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
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

	private ButtonGroupCivilized getButtonGroupCivilized() {
		if (buttonGroupCivilized == null) {
			try {
				buttonGroupCivilized = new ButtonGroupCivilized();
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return buttonGroupCivilized;
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
		return documentManager;
	}

	private JPanel getFeatureSizePanel() {
		if (featureSizePanel == null) {
			try {
				featureSizePanel = new JPanel();
				featureSizePanel.setName("FeatureSizePanel");
				featureSizePanel.setPreferredSize(new Dimension(22, 396));
				featureSizePanel.setLayout(new java.awt.BorderLayout());
				featureSizePanel.setMinimumSize(new Dimension(22, 396));
				getFeatureSizePanel().add(getJPanel1(), "South");
				getFeatureSizePanel().add(getJScrollPane1(), "Center");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return featureSizePanel;
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
				fluxButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/flux.gif")));
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

	private JLabel getJLabel3() {
		if (label3 == null) {
			try {
				label3 = new JLabel();
				label3.setName("JLabel3");
				label3.setText(" ");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return label3;
	}

	private JLabel getJLabel4() {
		if (label4 == null) {
			try {
				label4 = new JLabel();
				label4.setName("JLabel4");
				label4.setText(" ");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return label4;
	}

	private JPanel getJPanel1() {
		if (panel == null) {
			try {
				panel = new JPanel();
				panel.setName("JPanel1");
				panel.setLayout(new java.awt.GridBagLayout());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return panel;
	}

	private JScrollPane getJScrollPane1() {
		if (scrollPane == null) {
			try {
				scrollPane = new JScrollPane();
				scrollPane.setName("JScrollPane1");
				scrollPane.setPreferredSize(new Dimension(22, 396));
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.setMinimumSize(new Dimension(22, 396));
				getJScrollPane1().setViewportView(getGraphPane());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return scrollPane;
	}

	private JToolBar getJToolBar1() {
		if (toolBar == null) {
			try {
				toolBar = new javax.swing.JToolBar();
				toolBar.setName("JToolBar1");
				toolBar.setFloatable(false);
				toolBar.setBorder(new EtchedBorder());
				toolBar.setOrientation(SwingConstants.VERTICAL);
				getJToolBar1().add(getSelectButton(), getSelectButton().getName());
				getJToolBar1().add(getSpeciesButton(), getSpeciesButton().getName());
				getJToolBar1().add(getStepButton(), getStepButton().getName());
				getJToolBar1().add(getFluxButton(), getFluxButton().getName());
				getJToolBar1().add(getLineButton(), getLineButton().getName());
				getJToolBar1().addSeparator(new Dimension(5,10));
				getJToolBar1().add(getJLabel3(), getJLabel3().getName());
				getJToolBar1().add(getZoomInButton(), getZoomInButton().getName());
				getJToolBar1().add(getZoomOutButton(), getZoomOutButton().getName());
				getJToolBar1().add(getJLabel4(), getJLabel4().getName());
				getJToolBar1().add(getRandomLayoutButton(), getRandomLayoutButton().getName());
				getJToolBar1().add(getCircleLayoutButton(), getCircleLayoutButton().getName());
				getJToolBar1().add(getAnnealLayoutButton(), getAnnealLayoutButton().getName());
				getJToolBar1().add(getLevellerLayoutButton(), getLevellerLayoutButton().getName());
				getJToolBar1().add(getRelaxerLayoutButton(), getRelaxerLayoutButton().getName());
				getJToolBar1().add(getGlgLayoutJButton(), getGlgLayoutJButton().getName());
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
				lineButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/line.gif")));
				lineButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				lineButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return lineButton;
	}

	public Model getModel() {
		return model;
	}

	protected PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new PropertyChangeSupport(this);
		};
		return propertyChange;
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

	private ReactionCartoon getReactionCartoon() {
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
				selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select.gif")));
				selectButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				selectButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return selectButton;
	}

	private javax.swing.ButtonModel getSelection() {
		return selection;
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
				speciesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/species.gif")));
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
				stepButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/step.gif")));
				stepButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				stepButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return stepButton;
	}

	public Structure getStructure() {
		return structure;
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
				zoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomin.gif")));
				zoomInButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				zoomInButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
				zoomInButton.setMargin(new Insets(2, 2, 2, 2));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return zoomInButton;
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
				zoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomout.gif")));
				zoomOutButton.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
				zoomOutButton.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return zoomOutButton;
	}

	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in CartoonPanel");
		exception.printStackTrace(System.out);
	}

	private void initConnections() throws java.lang.Exception {
		getButtonGroupCivilized().addPropertyChangeListener(this);
		getRandomLayoutButton().addActionListener(this);
		getAnnealLayoutButton().addActionListener(this);
		getCircleLayoutButton().addActionListener(this);
		getRelaxerLayoutButton().addActionListener(this);
		getLevellerLayoutButton().addActionListener(this);
		getZoomInButton().addActionListener(this);
		getZoomOutButton().addActionListener(this);
		getGlgLayoutJButton().addActionListener(this);
		this.addPropertyChangeListener(this);
		try {
			if (connPtoP1Aligning == false) {
				connPtoP1Aligning = true;
				setSelection(getButtonGroupCivilized().getSelection());
				connPtoP1Aligning = false;
			}
		} catch (Throwable throwable) {
			connPtoP1Aligning = false;
			handleException(throwable);
		}
		try {
			if ((getSelection() != null)) {
				getReactionCartoonTool().setModeString(getSelection().getActionCommand());
			}
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}

	private void initialize() {
		try {
			setName("CartoonPanel");
			setPreferredSize(new Dimension(54, 425));
			setLayout(new java.awt.BorderLayout());
			setSize(472, 422);
			setMinimumSize(new Dimension(54, 425));
			add(getFeatureSizePanel(), "Center");
			add(getJToolBar1(), "West");
			initConnections();
			getButtonGroupCivilized().add(getStepButton());
			getButtonGroupCivilized().add(getFluxButton());
			getButtonGroupCivilized().add(getLineButton());
			getButtonGroupCivilized().add(getSelectButton());
			getButtonGroupCivilized().add(getSpeciesButton());
			getReactionCartoonTool().setReactionCartoon(getReactionCartoon());
			getReactionCartoonTool().setGraphPane(getGraphPane());
			getReactionCartoonTool().setButtonGroup(getButtonGroupCivilized());
			getGraphPane().setGraphModel(getReactionCartoon());
		} catch (Throwable throwable2) {
			handleException(throwable2);
		}
	}

	private boolean isMembrane(Structure structure) {
		return (structure instanceof Membrane);
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
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Panel");
			exception.printStackTrace(System.out);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getButtonGroupCivilized() && (evt.getPropertyName().equals("selection")))
			try {
				if (connPtoP1Aligning == false) {
					connPtoP1Aligning = true;
					setSelection(getButtonGroupCivilized().getSelection());
					connPtoP1Aligning = false;
				}
			} catch (Throwable throwable1) {
				connPtoP1Aligning = false;
				handleException(throwable1);
			}
		if (evt.getSource() == this && (evt.getPropertyName().equals("structure")))
			try {
				getReactionCartoon().setStructure(this.getStructure());
				try {
					getFluxButton().setEnabled(this.isMembrane(getReactionCartoon().getStructure()));
				} catch (Throwable throwable) {
					handleException(throwable);
				}
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		if (evt.getSource() == this && (evt.getPropertyName().equals("documentManager")))
			try {
				getReactionCartoonTool().setDocumentManager(this.getDocumentManager());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		if (evt.getSource() == this && (evt.getPropertyName().equals("model")))
			try {
				getReactionCartoon().setModel(this.getModel());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public void setDocumentManager(DocumentManager documentManager) {
		DocumentManager oldValue = this.documentManager;
		this.documentManager = documentManager;
		firePropertyChange("documentManager", oldValue, documentManager);
	}

	public void setModel(Model model) {
		Model oldValue = this.model;
		this.model = model;
		firePropertyChange("model", oldValue, model);
	}

	private void setSelection(javax.swing.ButtonModel newValue) {
		if (selection != newValue) {
			try {
				selection = newValue;
				try {
					if (connPtoP1Aligning == false) {
						connPtoP1Aligning = true;
						if ((getSelection() != null)) {
							getButtonGroupCivilized().setSelection(getSelection());
						}
						connPtoP1Aligning = false;
					}
				} catch (Throwable throwable) {
					connPtoP1Aligning = false;
					handleException(throwable);
				}
				try {
					if ((getSelection() != null)) {
						getReactionCartoonTool().setModeString(getSelection().getActionCommand());
					}
				} catch (Throwable throwable) {
					handleException(throwable);
				}
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		};
	}

	public void setStructure(Structure structure) {
		Structure oldValue = this.structure;
		this.structure = structure;
		firePropertyChange("structure", oldValue, structure);
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
}