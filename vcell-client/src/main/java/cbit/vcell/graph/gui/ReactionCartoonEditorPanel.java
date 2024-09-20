/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph.gui;

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

import javax.swing.AbstractButton;
import javax.swing.Box;
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
import javax.swing.SwingUtilities;

import org.vcell.util.graphlayout.ExpandCanvasLayouter;
import org.vcell.util.graphlayout.GenericLogicGraphLayouter;
import org.vcell.util.graphlayout.RandomLayouter;
import org.vcell.util.graphlayout.ShrinkCanvasLayouter;
import org.vcell.util.graphlayout.SimpleElipticalLayouter;
import org.vcell.util.graphlayout.energybased.EnergyMinimizingLayouter;
import org.vcell.util.gui.JToolBarToggleButton;
import org.vcell.util.gui.ViewPortStabilizer;

import cbit.gui.graph.GraphLayoutManager;
import cbit.gui.graph.gui.CartoonTool.Mode;
import cbit.gui.graph.gui.GraphPane;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.ReactionCartoon;
import cbit.vcell.graph.ReactionCartoonFull;
import cbit.vcell.graph.ReactionCartoonMolecule;
import cbit.vcell.graph.ReactionCartoonRule;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Model;

@SuppressWarnings("serial")
public class ReactionCartoonEditorPanel extends JPanel implements ActionListener {
    public static final Dimension TOOL_BAR_SEPARATOR_SIZE = new Dimension(10, 0);
    public static final Dimension NARROW_SEPARATOR_SIZE = new Dimension(2, 0);
    public static final Dimension WIDE_SEPARATOR_SIZE = new Dimension(15, 0);
    public static final String PROPERTY_NAME_FLOATING = "Floating";
    public static final Dimension TOOL_BAR_BUTTON_SIZE = new Dimension(28, 28);
    private GraphPane graphPane = null;
    private JToolBar toolBar = null;
    private JToolBarToggleButton speciesButton = null;
    private JToolBarToggleButton fluxReactionButton = null;
    private JToolBarToggleButton lineDirectedButton = null;
    //	private JToolBarToggleButton lineCatalystButton = null;
    private JToolBarToggleButton selectButton = null;
    protected List<JToolBarToggleButton> modeButtons = null;
    protected List<JToolBarToggleButton> viewButtons = null;
    private final ButtonGroup modeButtonGroup = new ButtonGroup();
    private final ButtonGroup viewButtonGroup = new ButtonGroup();

    protected List<JToolBarToggleButton> sizeOptionsButtons = null;
    private final ButtonGroup sizeOptionsButtonGroup = new ButtonGroup();

    private JScrollPane scrollPane = null;
    protected ViewPortStabilizer viewPortStabilizer = null;
    private JButton annealLayoutButton = null;
    private JButton circleLayoutButton = null;
    private JButton levellerLayoutButton = null;
    private JButton randomLayoutButton = null;
    private JButton relaxerLayoutButton = null;
    private JButton zoomInButton = null;
    private JButton zoomOutButton = null;
    private JButton glgLayoutJButton = null;
    private JButton shrinkCanvasButton = null;
    private JButton expandCanvasButton = null;
    private final ReactionCartoonFull reactionCartoonFull = new ReactionCartoonFull();
    private final ReactionCartoonMolecule reactionCartoonMolecule = new ReactionCartoonMolecule();
    private final ReactionCartoonRule reactionCartoonRule = new ReactionCartoonRule();
    private ReactionCartoon currentReactionCartoon = this.reactionCartoonFull;    // for simplicity we initialize with the full cartoon

    private final ReactionCartoonTool reactionCartoonTool = new ReactionCartoonTool();

    private boolean bFloatingRequested = false;
    private JButton floatRequestButton = null;

    public final static String IMAGE_PATH = "/images/layout/";
    private final static Icon randomLayoutIcon = loadIcon(IMAGE_PATH + "random.gif");
    private final static Icon circleLayoutIcon = loadIcon(IMAGE_PATH + "circular.gif");
    private final static Icon annealedLayoutIcon = loadIcon(IMAGE_PATH + "annealed.gif");
    private final static Icon levelledLayoutIcon = loadIcon(IMAGE_PATH + "levelled.gif");
    private final static Icon relaxedLayoutIcon = loadIcon(IMAGE_PATH + "relaxed.gif");
    private final static Icon glgLayoutIcon = loadIcon(IMAGE_PATH + "glg3.gif");
    private final static Icon shrinkLayoutIcon = loadIcon(IMAGE_PATH + "shrink.gif");
    private final static Icon expandLayoutIcon = loadIcon(IMAGE_PATH + "expand.gif");


    public ReactionCartoonEditorPanel() {
        super();
        this.initialize();
    }

    private static Icon loadIcon(String fileName) {
        return new ImageIcon(ReactionCartoonEditorPanel.class.getResource(fileName));
    }

    public void actionPerformed(ActionEvent event) {
        try {
            Object source = event.getSource();
            if (this.getModeButtons().contains(source)) {
                this.getReactionCartoonTool().setModeString(event.getActionCommand());
            } else if (this.getViewButtons().contains(source)) {
                String command = event.getActionCommand();
                this.setViewMode(command);
            } else if (this.getSizeOptionsButtons().contains(source)) {
                String command = event.getActionCommand();
                this.setSizeMode(command);
            } else if (source == this.getHighlightCatalystsButton()) {
                this.currentReactionCartoon.setHighlightCatalystMode(this.getHighlightCatalystsButton().isSelected());
                this.currentReactionCartoon.refreshAll();
            } else if (source == this.getRandomLayoutButton())
                this.getReactionCartoonTool().layout(RandomLayouter.LAYOUT_NAME);
            else if (source == this.getAnnealLayoutButton())
                this.getReactionCartoonTool().layout(GraphLayoutManager.OldLayouts.ANNEALER);
            else if (source == this.getCircleLayoutButton())
                this.getReactionCartoonTool().layout(SimpleElipticalLayouter.LAYOUT_NAME);
            else if (source == this.getRelaxerLayoutButton())
                this.getReactionCartoonTool().layout(GraphLayoutManager.OldLayouts.RELAXER);
            else if (source == this.getLevellerLayoutButton())
                this.getReactionCartoonTool().layout(GraphLayoutManager.OldLayouts.LEVELLER);
            else if (source == this.getZoomInButton()) {
                this.viewPortStabilizer.saveViewPortPosition();
                this.reactionCartoonFull.getResizeManager().zoomIn();    // we zoom all cartoons simultaneously
                this.reactionCartoonMolecule.getResizeManager().zoomIn();
//				reactionCartoonRule.getResizeManager().zoomIn();
//				reactionCartoon.getResizeManager().zoomIn();		// we zoom only the current cartoon
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ReactionCartoonEditorPanel.this.viewPortStabilizer.restoreViewPortPosition();
                    }
                });
            } else if (source == this.getZoomOutButton()) {
                this.viewPortStabilizer.saveViewPortPosition();
                this.reactionCartoonFull.getResizeManager().zoomOut();
                this.reactionCartoonMolecule.getResizeManager().zoomOut();
//				reactionCartoonRule.getResizeManager().zoomOut();				
//				reactionCartoon.getResizeManager().zoomOut();				
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ReactionCartoonEditorPanel.this.viewPortStabilizer.restoreViewPortPosition();
                    }
                });
            } else if (source == this.getGlgLayoutJButton())
//				System.out.println("GLG Layout has been removed");
                this.getReactionCartoonTool().layout(GenericLogicGraphLayouter.LAYOUT_NAME);
            else if (source == this.getShrinkCanvasButton())
                this.getReactionCartoonTool().layout(ShrinkCanvasLayouter.LAYOUT_NAME);
            else if (source == this.getExpandCanvasButton())
                this.getReactionCartoonTool().layout(ExpandCanvasLayouter.LAYOUT_NAME);
            else if (source == this.getFloatRequestButton())
                this.setFloatingRequested(!this.bFloatingRequested);
        } catch (Throwable throwable) {
            this.handleException(throwable);
        }
    }

    @Deprecated
    public void cleanupOnClose() {
        this.reactionCartoonFull.cleanupAll();
        this.reactionCartoonMolecule.cleanupAll();
        this.reactionCartoonRule.cleanupAll();
    }

    private JButton getAnnealLayoutButton() {
        if (this.annealLayoutButton == null) {
            try {
                this.annealLayoutButton = this.createToolBarButton();
                this.annealLayoutButton.setName("AnnealLayoutButton");
                this.annealLayoutButton.setToolTipText("Annealing Layout");
                this.annealLayoutButton.setIcon(annealedLayoutIcon);
                this.annealLayoutButton.setActionCommand("AnnealLayout");
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.annealLayoutButton;
    }

    private JButton getCircleLayoutButton() {
        if (this.circleLayoutButton == null) {
            try {
                this.circleLayoutButton = this.createToolBarButton();
                this.circleLayoutButton.setName("CircleLayoutButton");
                this.circleLayoutButton.setToolTipText("Circular Layout");
                this.circleLayoutButton.setIcon(circleLayoutIcon);
                this.circleLayoutButton.setActionCommand("CircleLayout");
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.circleLayoutButton;
    }

    public DocumentManager getDocumentManager() {
        return this.getReactionCartoonTool().getDocumentManager();
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
        if (this.glgLayoutJButton == null) {
            try {
                this.glgLayoutJButton = this.createToolBarButton();
                this.glgLayoutJButton.setName("GlgLayoutJButton");
                this.glgLayoutJButton.setToolTipText("Layout GLG");
                this.glgLayoutJButton.setIcon(glgLayoutIcon);
                this.glgLayoutJButton.setEnabled(true);
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.glgLayoutJButton;
    }

    private JButton getShrinkCanvasButton() {
        if (this.shrinkCanvasButton == null) {
            try {
                this.shrinkCanvasButton = this.createToolBarButton();
                this.shrinkCanvasButton.setName("ShrinkCanvasButton");
                ResizeCanvasShapeIcon.setCanvasNormalMod(this.shrinkCanvasButton, ResizeCanvasShapeIcon.Sign.shrink);
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.shrinkCanvasButton;
    }

    private JButton getExpandCanvasButton() {
        if (this.expandCanvasButton == null) {
            try {
                this.expandCanvasButton = this.createToolBarButton();
                this.expandCanvasButton.setName("ExpandCanvasButton");
                ResizeCanvasShapeIcon.setCanvasNormalMod(this.expandCanvasButton, ResizeCanvasShapeIcon.Sign.expand);
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.expandCanvasButton;
    }

    private GraphPane getGraphPane() {
        if (this.graphPane == null) {
            try {
                this.graphPane = new GraphPane();
                this.graphPane.setName("GraphPane");
                this.graphPane.setBounds(0, 0, 372, 364);
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.graphPane;
    }

    private JScrollPane getJScrollPane() {
        if (this.scrollPane == null) {
            try {
                this.scrollPane = new JScrollPane();
                this.scrollPane.setName("JScrollPane1");
                this.scrollPane.setPreferredSize(new Dimension(22, 396));
                this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//				scrollPane.setMinimumSize(new Dimension(22, 396));
                this.getJScrollPane().setViewportView(this.getGraphPane());
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.scrollPane;
    }

    private JToolBar getJToolBar() {
        if (this.toolBar == null) {
            try {
                this.toolBar = new JToolBar();
                this.toolBar.setName("toolBar");
                this.toolBar.setFloatable(false);
                this.toolBar.setOrientation(SwingConstants.HORIZONTAL);
                this.toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
                this.toolBar.add(this.getSelectButton(), this.getSelectButton().getName());
                this.toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
                this.toolBar.add(this.getStructureButton(), this.getStructureButton().getName());
                this.toolBar.add(this.getSpeciesButton(), this.getSpeciesButton().getName());
                this.toolBar.add(this.getLineDirectedButton(), this.getLineDirectedButton().getName());
//				toolBar.add(getLineCatalystButton(), getLineCatalystButton().getName());
                this.toolBar.add(this.getFluxReactionButton(), this.getFluxReactionButton().getName());
                //toolBar.add(getStepButton(), getStepButton().getName());
                //toolBar.add(getFluxButton(), getFluxButton().getName());
                //toolBar.add(getLineButton(), getLineButton().getName());
                this.toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
                this.toolBar.add(this.getZoomInButton(), this.getZoomInButton().getName());
                this.toolBar.add(this.getZoomOutButton(), this.getZoomOutButton().getName());
                this.toolBar.add(this.getExpandCanvasButton(), this.getExpandCanvasButton().getName());
                this.toolBar.add(this.getShrinkCanvasButton(), this.getShrinkCanvasButton().getName());
                this.toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
                this.toolBar.add(this.getRandomLayoutButton(), this.getRandomLayoutButton().getName());
//				toolBar.add(getEdgeTugLayoutButton(), getEdgeTugLayoutButton().getName());
                this.toolBar.add(this.getCircleLayoutButton(), this.getCircleLayoutButton().getName());
                this.toolBar.add(this.getAnnealLayoutButton(), this.getAnnealLayoutButton().getName());
                this.toolBar.add(this.getLevellerLayoutButton(), this.getLevellerLayoutButton().getName());
                this.toolBar.add(this.getRelaxerLayoutButton(), this.getRelaxerLayoutButton().getName());
                this.toolBar.add(this.getGlgLayoutJButton(), this.getGlgLayoutJButton().getName());
                this.toolBar.addSeparator(TOOL_BAR_SEPARATOR_SIZE);
                this.toolBar.add(this.getUngroupButton(), this.getUngroupButton().getName());
                this.toolBar.add(this.getGroupMoleculeButton(), this.getGroupMoleculeButton().getName());
//				toolBar.add(getGroupRuleButton(), getGroupRuleButton().getName());

                this.toolBar.addSeparator(WIDE_SEPARATOR_SIZE);
                this.toolBar.add(this.getHighlightCatalystsButton(), this.getHighlightCatalystsButton().getName());
                this.toolBar.addSeparator(NARROW_SEPARATOR_SIZE);
                this.toolBar.add(this.getEqualSizeButton(), this.getEqualSizeButton().getName());
                this.toolBar.add(this.getSizeByWeightButton(), this.getSizeByWeightButton().getName());
                this.toolBar.add(this.getSizeByLengthButton(), this.getSizeByLengthButton().getName());

                this.toolBar.add(Box.createHorizontalGlue());
                this.toolBar.add(this.getFloatRequestButton());
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.toolBar;
    }

    private JButton createToolBarButton() {
        JButton button = new JButton();
        setToolBarButtonSizes(button);
        button.setMargin(new Insets(2, 2, 2, 2));

        return button;
    }

    private JButton getLevellerLayoutButton() {
        if (this.levellerLayoutButton == null) {
            try {
                this.levellerLayoutButton = this.createToolBarButton();
                this.levellerLayoutButton.setName("LevellerLayoutButton");
                this.levellerLayoutButton.setToolTipText("Leveller Layout");
                this.levellerLayoutButton.setIcon(levelledLayoutIcon);
                this.levellerLayoutButton.setActionCommand("LevellerLayout");
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.levellerLayoutButton;
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
        if (this.lineDirectedButton == null) {
            try {
//				JToolBarToggleButton button = new JToolBarToggleButton();
//				ReactionToolShapeIcon.setPlainReactionToolMod(button);
//				button.setActionCommand(Mode.LINEDIRECTED.getActionCommand());
//				lineDirectedButton = button;
                this.lineDirectedButton = this.createModeButton("LineButton", "RX Connection Tool", Mode.LINEDIRECTED, loadIcon("/images/lineDirected.gif"));
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.lineDirectedButton;
    }

    //	private JToolBarToggleButton getLineCatalystButton() {
//		if (lineCatalystButton == null) {
//			try {
////				JToolBarToggleButton button = new JToolBarToggleButton();
////				ReactionToolShapeIcon.setCatalystToolMod(button);
////				button.setActionCommand(Mode.LINECATALYST.getActionCommand());
////				lineCatalystButton = button;
//				lineCatalystButton = createModeButton("LineCatalystButton", "Set a catalyst", Mode.LINECATALYST, loadIcon("/images/lineCatalyst.gif"));
//			} catch (Throwable throwable) {
//				handleException(throwable);
//			}
//		}
//		return lineCatalystButton;
//	}
    private JToolBarToggleButton getFluxReactionButton() {
        if (this.fluxReactionButton == null) {
            try {
//				JToolBarToggleButton button = new JToolBarToggleButton();
//				ReactionToolShapeIcon.setFluxTransportToolMod(button);
//				button.setActionCommand(Mode.FLUX.getActionCommand());
//				fluxReactionButton = button;
                this.fluxReactionButton = this.createModeButton("FluxReactionButton", "FluxReaction Tool", Mode.FLUX, loadIcon("/images/fluxItem.gif"));
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.fluxReactionButton;
    }

    public Model getModel() {
        return this.currentReactionCartoon.getModel();
    }

    private JButton getRandomLayoutButton() {
        if (this.randomLayoutButton == null) {
            try {
                this.randomLayoutButton = this.createToolBarButton();
                this.randomLayoutButton.setName("RandomLayoutButton");
                this.randomLayoutButton.setToolTipText("Random Layout");
                this.randomLayoutButton.setIcon(randomLayoutIcon);
                this.randomLayoutButton.setActionCommand("RandomLayout");
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.randomLayoutButton;
    }

    public ReactionCartoon getReactionCartoonFull() {
        return this.reactionCartoonFull;
    }

    public ReactionCartoon getReactionCartoonMolecule() {
        return this.reactionCartoonMolecule;
    }

    public ReactionCartoon getReactionCartoonRule() {
        return this.reactionCartoonRule;
    }

    public ReactionCartoon getReactionCartoon() {
        return this.currentReactionCartoon;
    }

    private ReactionCartoonTool getReactionCartoonTool() {
        return this.reactionCartoonTool;
    }

    private JButton getRelaxerLayoutButton() {
        if (this.relaxerLayoutButton == null) {
            try {
                this.relaxerLayoutButton = this.createToolBarButton();
                this.relaxerLayoutButton.setName("RelaxerLayoutButton");
                this.relaxerLayoutButton.setToolTipText("Relaxer Layou");
                this.relaxerLayoutButton.setIcon(relaxedLayoutIcon);
                this.relaxerLayoutButton.setActionCommand("RelaxerLayout");
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.relaxerLayoutButton;
    }

    private JToolBarToggleButton getSelectButton() {
        if (this.selectButton == null) {
            try {
                this.selectButton = this.createModeButton("SelectButton", "Select Tool",
                        Mode.SELECT, loadIcon("/images/select.gif"));
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.selectButton;
    }

    protected List<JToolBarToggleButton> getModeButtons() {
        if (this.modeButtons == null) {
            this.modeButtons = new ArrayList<JToolBarToggleButton>();
            this.modeButtons.add(this.getSelectButton());
            this.modeButtons.add(this.getSpeciesButton());
            this.modeButtons.add(this.getFluxReactionButton());
            //modeButtons.add(getStepButton());
            //modeButtons.add(getFluxButton());
            //modeButtons.add(getLineButton());
            this.modeButtons.add(this.getLineDirectedButton());
//			modeButtons.add(getLineCatalystButton());
            this.modeButtons.add(this.getStructureButton());
        }
        return this.modeButtons;
    }

    protected List<JToolBarToggleButton> getViewButtons() {
        if (this.viewButtons == null) {
            this.viewButtons = new ArrayList<JToolBarToggleButton>();
            this.viewButtons.add(this.getUngroupButton());
            this.viewButtons.add(this.getGroupMoleculeButton());
//			viewButtons.add(getGroupRuleButton());
        }
        return this.viewButtons;
    }

    protected List<JToolBarToggleButton> getSizeOptionsButtons() {
        if (this.sizeOptionsButtons == null) {
            this.sizeOptionsButtons = new ArrayList<JToolBarToggleButton>();
            this.sizeOptionsButtons.add(this.getEqualSizeButton());
            this.sizeOptionsButtons.add(this.getSizeByWeightButton());
            this.sizeOptionsButtons.add(this.getSizeByLengthButton());
        }
        return this.sizeOptionsButtons;
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
        if (this.speciesButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                SpeciesToolShapeIcon.setSpeciesToolMod(button);
                button.setActionCommand(Mode.SPECIES.getActionCommand());
                this.speciesButton = button;
//				speciesButton = createModeButton("SpeciesButton", "Species Tool", Mode.SPECIES, loadIcon("/images/species.gif"));
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.speciesButton;
    }

    private JToolBarToggleButton structureButton;

    private JToolBarToggleButton getStructureButton() {
        if (this.structureButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                StructureToolShapeIcon.setStructureToolMod(button);
                button.setActionCommand(Mode.STRUCTURE.getActionCommand());
                this.structureButton = button;
//				structureButton = createModeButton("StructureButton", "Structure Tool", Mode.STRUCTURE, loadIcon("/images/feature.gif"));
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.structureButton;
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
        if (this.zoomInButton == null) {
            try {
                this.zoomInButton = this.createToolBarButton();
                this.zoomInButton.setName("ZoomInButton");
                ZoomShapeIcon.setZoomToolbarMod(this.zoomInButton, ZoomShapeIcon.Sign.plus);
                this.zoomInButton.setActionCommand("ZoomIn");
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.zoomInButton;
    }

    private JButton getZoomOutButton() {
        if (this.zoomOutButton == null) {
            try {
                this.zoomOutButton = this.createToolBarButton();
                this.zoomOutButton.setName("ZoomOutButton");
                ZoomShapeIcon.setZoomToolbarMod(this.zoomOutButton, ZoomShapeIcon.Sign.minus);
                this.zoomOutButton.setActionCommand("ZoomOut");
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.zoomOutButton;
    }

    private JButton getFloatRequestButton() {
        if (this.floatRequestButton == null) {
            try {
                this.floatRequestButton = this.createToolBarButton();
                this.floatRequestButton.setText("\u21b1");
                this.floatRequestButton.setName("FloatingButton");
                this.floatRequestButton.setFont(this.floatRequestButton.getFont().deriveFont(Font.BOLD));
                this.floatRequestButton.setToolTipText("\u21b1 Float");
            } catch (Throwable ivjExc) {
                this.handleException(ivjExc);
            }
        }
        return this.floatRequestButton;
    }
// ------------------------------------------------------------------------------------

    private JToolBarToggleButton highlightCatalystsButton = null;

    private JToolBarToggleButton getHighlightCatalystsButton() {
        if (this.highlightCatalystsButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                HighlightCatalystShapeIcon.setStructureToolMod(button);
                button.setActionCommand(Mode.HIGHLIGHTCATALYST.getActionCommand());
                this.highlightCatalystsButton = button;
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.highlightCatalystsButton;
    }

    private JToolBarToggleButton equalSizeButton = null;

    private JToolBarToggleButton getEqualSizeButton() {
        if (this.equalSizeButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                SpeciesSizeShapeIcon.setSpeciesSizeShapeMod(button, SpeciesSizeShapeIcon.Kind.equal);
                button.setActionCommand(Mode.EQUALSIZE.getActionCommand());
                this.equalSizeButton = button;
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.equalSizeButton;
    }

    private JToolBarToggleButton sizeByWeightButton = null;

    private JToolBarToggleButton getSizeByWeightButton() {
        if (this.sizeByWeightButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                SpeciesSizeShapeIcon.setSpeciesSizeShapeMod(button, SpeciesSizeShapeIcon.Kind.weight);
                button.setActionCommand(Mode.SIZEBYWEIGHT.getActionCommand());
                this.sizeByWeightButton = button;
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.sizeByWeightButton;
    }

    private JToolBarToggleButton sizeByLengthButton = null;

    private JToolBarToggleButton getSizeByLengthButton() {
        if (this.sizeByLengthButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                SpeciesSizeShapeIcon.setSpeciesSizeShapeMod(button, SpeciesSizeShapeIcon.Kind.length);
                button.setActionCommand(Mode.SIZEBYLENGTH.getActionCommand());
                this.sizeByLengthButton = button;
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.sizeByLengthButton;
    }


    // TODO centralize exception handling
    private void handleException(Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION --------- in CartoonPanel");
        exception.printStackTrace(System.out);
    }

    private void initConnections() throws Exception {
        for (JToolBarToggleButton modeButton : this.getModeButtons()) {
            modeButton.addActionListener(this);
        }
        for (JToolBarToggleButton viewButton : this.getViewButtons()) {
            viewButton.addActionListener(this);
        }
        ButtonModel selection = this.modeButtonGroup.getSelection();
        if (selection != null) {
            this.getReactionCartoonTool().setModeString(selection.getActionCommand());
        } else {
            this.getReactionCartoonTool().setMode(Mode.SELECT);
        }
        this.getRandomLayoutButton().addActionListener(this);
        this.getAnnealLayoutButton().addActionListener(this);
        this.getCircleLayoutButton().addActionListener(this);
        this.getRelaxerLayoutButton().addActionListener(this);
        this.getLevellerLayoutButton().addActionListener(this);
        this.getZoomInButton().addActionListener(this);
        this.getZoomOutButton().addActionListener(this);
        this.getGlgLayoutJButton().addActionListener(this);
        this.getShrinkCanvasButton().addActionListener(this);
        this.getExpandCanvasButton().addActionListener(this);
        this.getFloatRequestButton().addActionListener(this);

        this.getHighlightCatalystsButton().addActionListener(this);
        for (JToolBarToggleButton sizeButton : this.getSizeOptionsButtons()) {
            sizeButton.addActionListener(this);
        }

    }

    private void initialize() {
        try {
            this.setName("CartoonPanel");
            this.setPreferredSize(new Dimension(54, 425));
            this.setLayout(new BorderLayout());
            this.setSize(472, 422);
//			setMinimumSize(new Dimension(54, 425));
            this.add(this.getJScrollPane(), BorderLayout.CENTER);
            this.viewPortStabilizer = new ViewPortStabilizer(this.getJScrollPane());

            this.add(this.getJToolBar(), BorderLayout.NORTH);
            this.initConnections();
            //getModeButtonGroup().add(getStepButton());
            //getModeButtonGroup().add(getFluxButton());
            //getModeButtonGroup().add(getLineButton());
            this.modeButtonGroup.add(this.getLineDirectedButton());
//			modeButtonGroup.add(getLineCatalystButton());
            this.modeButtonGroup.add(this.getSelectButton());
            this.modeButtonGroup.add(this.getSpeciesButton());
            this.modeButtonGroup.add(this.getFluxReactionButton());
            this.modeButtonGroup.add(this.getStructureButton());
            this.viewButtonGroup.add(this.getUngroupButton());
            this.viewButtonGroup.add(this.getGroupMoleculeButton());
            this.sizeOptionsButtonGroup.add(this.getEqualSizeButton());
            this.sizeOptionsButtonGroup.add(this.getSizeByWeightButton());
            this.sizeOptionsButtonGroup.add(this.getSizeByLengthButton());

//			viewButtonGroup.add(getGroupRuleButton());
            this.getReactionCartoonTool().setReactionCartoon(this.currentReactionCartoon);
            this.getReactionCartoonTool().setGraphPane(this.getGraphPane());
            this.getReactionCartoonTool().setButtonGroup(this.modeButtonGroup);
            this.getReactionCartoonTool().setButtonGroup(this.viewButtonGroup);
//			getGraphPane().setGraphModel(reactionCartoonFull);
//			getGraphPane().setGraphModel(reactionCartoonMolecule);
//			getGraphPane().setGraphModel(reactionCartoonRule);
            this.getGraphPane().setGraphModel(this.currentReactionCartoon);
            this.refreshButtons();
            ButtonModel m = this.getEqualSizeButton().getModel();
            this.sizeOptionsButtonGroup.setSelected(m, true);
//			setViewMode(Mode.GROUP.getActionCommand());
        } catch (Throwable throwable) {
            this.handleException(throwable);
        }
    }

    public static void setToolBarButtonSizes(AbstractButton button) {
        button.setPreferredSize(TOOL_BAR_BUTTON_SIZE);
        button.setMinimumSize(TOOL_BAR_BUTTON_SIZE);
        button.setMaximumSize(TOOL_BAR_BUTTON_SIZE);
    }

    private JToolBarToggleButton groupMoleculeButton;

    private JToolBarToggleButton getGroupMoleculeButton() {
        if (this.groupMoleculeButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                GroupMoleculeToolShape.setMod(button);
                button.setActionCommand(Mode.GROUPMOLECULE.getActionCommand());
                this.groupMoleculeButton = button;
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.groupMoleculeButton;
    }

    //	private JToolBarToggleButton groupRuleButton;
//	private JToolBarToggleButton getGroupRuleButton() {
//		if (groupRuleButton == null) {
//			try {
//				JToolBarToggleButton button = new JToolBarToggleButton();
//				GroupRuleToolShape.setMod(button);
//				button.setActionCommand(Mode.GROUPRULE.getActionCommand());
//				groupRuleButton = button;
//			} catch (Throwable throwable) {
//				handleException(throwable);
//			}
//		}
//		return groupRuleButton;
//	}
    private JToolBarToggleButton ungroupButton;

    private JToolBarToggleButton getUngroupButton() {
        if (this.ungroupButton == null) {
            try {
                JToolBarToggleButton button = new JToolBarToggleButton();
                UngroupToolShapeIcon.setMod(button);
                button.setActionCommand(Mode.UNGROUP.getActionCommand());
                this.ungroupButton = button;
            } catch (Throwable throwable) {
                this.handleException(throwable);
            }
        }
        return this.ungroupButton;
    }

    private void setViewMode(String command) {
        System.out.println("ReactionCartoonEditorPanel, setViewMode");
        if (command.equalsIgnoreCase(Mode.GROUPMOLECULE.getActionCommand())) {    // group participants by signature
            this.currentReactionCartoon = this.reactionCartoonMolecule;
            this.getReactionCartoonTool().setReactionCartoon(this.currentReactionCartoon);
            this.getGraphPane().setGraphModel(this.currentReactionCartoon);
            this.currentReactionCartoon.refreshAll();
            this.currentReactionCartoon.setSelectedObjects(new Object[]{});
        } else if (command.equalsIgnoreCase(Mode.GROUPRULE.getActionCommand())) {    // group rules with similar participant signatures
            this.currentReactionCartoon = this.reactionCartoonRule;
            this.getReactionCartoonTool().setReactionCartoon(this.currentReactionCartoon);
            this.getGraphPane().setGraphModel(this.currentReactionCartoon);
            this.currentReactionCartoon.refreshAll();
            this.currentReactionCartoon.setSelectedObjects(new Object[]{});
        } else if (command.equalsIgnoreCase(Mode.UNGROUP.getActionCommand())) {    // show participants individually
            this.currentReactionCartoon = this.reactionCartoonFull;
            this.getReactionCartoonTool().setReactionCartoon(this.currentReactionCartoon);
            this.getGraphPane().setGraphModel(this.currentReactionCartoon);
            this.currentReactionCartoon.refreshAll();
            this.currentReactionCartoon.setSelectedObjects(new Object[]{});
        }
    }

    private void setSizeMode(String command) {
        System.out.println("ReactionCartoonEditorPanel, setSizeMode");
        if (command.equalsIgnoreCase(Mode.EQUALSIZE.getActionCommand())) {
            this.currentReactionCartoon.setSizeMode(ReactionCartoon.SpeciesSizeOptions.normal);
        } else if (command.equalsIgnoreCase(Mode.SIZEBYWEIGHT.getActionCommand())) {
            this.currentReactionCartoon.setSizeMode(ReactionCartoon.SpeciesSizeOptions.weight);
        } else if (command.equalsIgnoreCase(Mode.SIZEBYLENGTH.getActionCommand())) {
            this.currentReactionCartoon.setSizeMode(ReactionCartoon.SpeciesSizeOptions.length);
        }
        this.currentReactionCartoon.refreshAll();
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
                }

            });
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of java.awt.Panel");
            exception.printStackTrace(System.out);
        }
    }

    public void setDocumentManager(DocumentManager documentManager) {
        this.getReactionCartoonTool().setDocumentManager(documentManager);
    }

    public void setModel(Model model) {
        this.reactionCartoonFull.setModel(model);
        this.reactionCartoonMolecule.setModel(model);
        this.reactionCartoonRule.setModel(model);
        this.refreshButtons();
        if (this.getModel() != null) {
            if (!this.getModel().getRbmModelContainer().getReactionRuleList().isEmpty()) {
                this.getGroupMoleculeButton().setSelected(true);    // select by default the grouped by molecule button
                this.setViewMode("groupmolecule");                // show by default the groupmolecule view
            } else {
                this.getUngroupButton().setSelected(true);        // select by default the ungrouped by molecule button
                this.setViewMode("ungroup");                        // show by default the ungrouped view
            }
        }
    }

    public void selectedObjectsChanged() {
        this.refreshButtons();
    }

    private void refreshButtons() {
        if (this.getModel() != null) {
            if (!this.getModel().getRbmModelContainer().getReactionRuleList().isEmpty()) {
                this.getGroupMoleculeButton().setVisible(true);
//				getGroupRuleButton().setVisible(true);
                this.getUngroupButton().setVisible(true);
            } else {
                this.getGroupMoleculeButton().setVisible(false);
//				getGroupRuleButton().setVisible(false);
                this.getUngroupButton().setVisible(false);
            }
        } else {
            this.getGroupMoleculeButton().setVisible(false);
//			getGroupRuleButton().setVisible(false);
            this.getUngroupButton().setVisible(false);
        }
    }

    public void setStructureSuite(StructureSuite structureSuite) {
        this.reactionCartoonFull.setStructureSuite(structureSuite);
        this.reactionCartoonMolecule.setStructureSuite(structureSuite);
        this.reactionCartoonRule.setStructureSuite(structureSuite);
    }

    public void specialLayout() {
        //if(getModel() != null && getModel().getDiagrams() != null && getModel().getDiagrams().length == 1 && getModel().getDiagrams()[0].getNodeList().size() == 0){
        try {
            this.getReactionCartoonTool().layout(GenericLogicGraphLayouter.LAYOUT_NAME, false);
        } catch (Exception e) {
            System.out.println("Error:  " + this.getClass().getName() + " setStructureSuite(...)->reactioncartoontool.layout(...)");
            e.printStackTrace();
        }
        //}
    }

    public final void setFloatingRequested(boolean newValue) {
        boolean oldValue = this.bFloatingRequested;
        this.bFloatingRequested = newValue;
        this.floatRequestButton.setVisible(!this.bFloatingRequested);
        this.firePropertyChange(PROPERTY_NAME_FLOATING, oldValue, newValue);
    }

}
