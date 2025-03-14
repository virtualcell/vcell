package cbit.vcell.mapping.gui;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionCartoon;
import cbit.vcell.graph.SpeciesContextSpecLargeShape;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.graph.gui.SsldLargeShapePanel;
import cbit.vcell.graph.gui.ZoomShapeIcon;
import cbit.vcell.mapping.MolecularInternalLinkSpec;
import cbit.vcell.mapping.SiteAttributesSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.Model;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.ExpressionException;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static cbit.vcell.mapping.SpeciesContextSpec.PROPERTY_NAME_LINK_SELECTED_IN_SHAPE;
import static cbit.vcell.mapping.SpeciesContextSpec.PROPERTY_NAME_SITE_SELECTED_IN_SHAPE;

public class MolecularStructuresPropertiesPanel extends DocumentEditorSubPanel {

    private BioModel bioModel = null;
    private SpeciesContextSpec speciesContextSpec = null;
    private EventHandler eventHandler = new EventHandler();

    private JScrollPane scrollPane;		            // shapePanel lives inside this
    private LargeShapePanel shapePanel = null;
    private SpeciesContextSpecLargeShape scsls;     // the real thing

    private MolecularComponentPattern mcpSelected = null;
    private MolecularInternalLinkSpec milsSelected = null;

    private JButton zoomLargerButton = null;
    private JButton zoomSmallerButton = null;


    private class EventHandler implements ActionListener, PropertyChangeListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == getZoomLargerButton()) {
                System.out.println("Zoom to larger (Zoom In)");
                boolean ret = shapePanel.zoomLarger();
                getZoomLargerButton().setEnabled(ret);
                getZoomSmallerButton().setEnabled(true);
                updateShape();
            } else if (e.getSource() == getZoomSmallerButton()) {
                System.out.println("Zoom to smaller (Zoom Out)");
                boolean ret = shapePanel.zoomSmaller();
                getZoomLargerButton().setEnabled(true);
                getZoomSmallerButton().setEnabled(ret);
                updateShape();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getSource() instanceof SpeciesContextSpec && evt.getPropertyName().equals(SpeciesContextSpec.PROPERTY_NAME_SITE_ATTRIBUTE)) {
                updateShape();
            } else if(evt.getSource() instanceof SpeciesContextSpec && evt.getPropertyName().equals(SpeciesContextSpec.PROPERTY_NAME_LINK_LENGTH)) {
                updateShape();
            } else if(evt.getSource() instanceof SpeciesContextSpec && evt.getPropertyName().equals(SpeciesContextSpec.PROPERTY_NAME_SITE_SELECTED_IN_TABLE)) {
                Object o = evt.getNewValue();
                if(o instanceof MolecularComponentPattern mcp) {
                    mcpSelected = mcp;
                } else {
                    mcpSelected = null;
                }
                updateShape();
            } else if(evt.getSource() instanceof SpeciesContextSpec && evt.getPropertyName().equals(SpeciesContextSpec.PROPERTY_NAME_LINK_SELECTED_IN_TABLE)) {
                Object o = evt.getNewValue();
                if(o instanceof MolecularInternalLinkSpec mils) {
                    milsSelected = mils;
                } else {
                    milsSelected = null;
                }
                updateShape();
            }
        }
    }


    public MolecularStructuresPropertiesPanel() {
        super();
        initialize();
    }

    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        if (exception instanceof ExpressionException){
            javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("--------- UNCAUGHT EXCEPTION --------- in SpeciesContextSpecPanel");
        exception.printStackTrace(System.out);
    }

    private void initialize() {
        try {
            shapePanel = new LargeShapePanel() {        // glyph (shape) panel
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (speciesContextSpec == null || speciesContextSpec.getSpeciesContext() == null) {
                        return;
                    }
                    scsls = new SpeciesContextSpecLargeShape(speciesContextSpec, shapePanel, speciesContextSpec,
                            mcpSelected, milsSelected, issueManager);
                    scsls.paintSelf(g);
                }
                @Override
                public DisplayMode getDisplayMode() {
                    return DisplayMode.other;
                }
                @Override
                public ReactionCartoon.RuleAnalysisChanged hasStateChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern) {
                    return ReactionCartoon.RuleAnalysisChanged.UNCHANGED;
                }
                @Override
                public ReactionCartoon.RuleAnalysisChanged hasStateChanged(MolecularComponentPattern molecularComponentPattern) {
                    return ReactionCartoon.RuleAnalysisChanged.UNCHANGED;
                }
                @Override
                public ReactionCartoon.RuleAnalysisChanged hasBondChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern) {
                    return ReactionCartoon.RuleAnalysisChanged.UNCHANGED;
                }
                @Override
                public ReactionCartoon.RuleAnalysisChanged hasBondChanged(MolecularComponentPattern molecularComponentPattern) {
                    return ReactionCartoon.RuleAnalysisChanged.UNCHANGED;
                }
                @Override
                public ReactionCartoon.RuleAnalysisChanged hasNoMatch(String reactionRuleName, MolecularTypePattern mtp) {
                    return ReactionCartoon.RuleAnalysisChanged.UNCHANGED;
                }
                @Override
                public ReactionCartoon.RuleAnalysisChanged hasNoMatch(MolecularTypePattern molecularTypePattern) {
                    return ReactionCartoon.RuleAnalysisChanged.UNCHANGED;
                }
                @Override
                public RuleParticipantSignature getSignature() {
                    return null;
                }
                @Override
                public GroupingCriteria getCriteria() {
                    return null;
                }
                @Override
                public boolean isViewSingleRow() {
                    return true;
                }
            };
            shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    // this will fire if the mouse moves over the panel, we may need it for drag and drop
                    super.mouseMoved(e);
//                    Point overWhat = e.getPoint();
//                    Object overObject = scsls.contains(overWhat);
//                    if(overObject != null) {
//                        System.out.println("MouseMotionAdapter: over something");
//                    }
                }
            });
            shapePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if(speciesContextSpec == null) {
                        throw new RuntimeException("speciesContextSpec is null");
                    }
                    Point overWhat = e.getPoint();
                    Object overObject = scsls.contains(overWhat);   // check if we clicked inside a site oval or link line
                    if(overObject instanceof MolecularInternalLinkSpec mils) {
                        milsSelected = mils;
                        // redraw the whole shape to highlight the selected site oval
                        updateShape();
                        // we tell the table in the upper panel to update the selected row
                        speciesContextSpec.firePropertyChange(PROPERTY_NAME_LINK_SELECTED_IN_SHAPE, null, milsSelected);
                    } else if(overObject instanceof SiteAttributesSpec sas) {
                        mcpSelected = sas.getMolecularComponentPattern();
                        // redraw the whole shape to highlight the selected link line
                        updateShape();
                        // we tell the table in the upper panel to update the selected row
                        speciesContextSpec.firePropertyChange(PROPERTY_NAME_SITE_SELECTED_IN_SHAPE, null, mcpSelected);
                    }
                }
            });
            shapePanel.setPreferredSize(new Dimension(2000, 800));
            shapePanel.setBackground(new Color(0xe0e0e0));
            shapePanel.setZoomFactor(-2);
            shapePanel.setEditable(false);

            scrollPane = new JScrollPane(shapePanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            // -----------------------------------------------------------------------------------------

            JPanel optionsPanel = new JPanel();     // ------- left panel with zoom buttons
            optionsPanel.setLayout(new GridBagLayout());

            getZoomSmallerButton().setEnabled(true);
            getZoomLargerButton().setEnabled(true);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(4,4,0,10);
            gbc.anchor = GridBagConstraints.WEST;
            optionsPanel.add(getZoomLargerButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(4,4,4,10);
            gbc.anchor = GridBagConstraints.WEST;
            optionsPanel.add(getZoomSmallerButton(), gbc);

//            gbc = new GridBagConstraints();
//            gbc.gridx = 0;
//            gbc.gridy = 2;
//            gbc.anchor = GridBagConstraints.WEST;
//            gbc.insets = new Insets(4, 4, 4, 10);
//            optionsPanel.add(new JLabel("Reaction Radius"), gbc);
//
//            gbc = new GridBagConstraints();
//            gbc.gridx = 1;
//            gbc.gridy = 2;
//            gbc.anchor = GridBagConstraints.WEST;
//            gbc.insets = new Insets(4, 4, 4, 10);
//            optionsPanel.add(new JLabel("2 nm"), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;          // make this 3 if we show reaction radius here
            gbc.weightx = 1;
            gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 4, 4, 10);
            optionsPanel.add(new JLabel(""), gbc);

            // ------------------------------------------------------------------------------------------
            JPanel containerOfScrollPanel = new JPanel();
            containerOfScrollPanel.setLayout(new BorderLayout());
            containerOfScrollPanel.add(optionsPanel, BorderLayout.WEST);
            containerOfScrollPanel.add(scrollPane, BorderLayout.CENTER);

            setLayout(new BorderLayout());
            add(containerOfScrollPanel, BorderLayout.CENTER);
            setBackground(Color.white);
            setName("MolecularStructuresPropertiesPanel");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void updateInterface() {
        if(speciesContextSpec == null || speciesContextSpec.getSpeciesContext() == null) {
            return;
        }
        updateShape();
    }

    private void updateShape() {
        if(speciesContextSpec == null || speciesContextSpec.getSpeciesContext() == null) {
            return;
        }
        scsls = new SpeciesContextSpecLargeShape(speciesContextSpec, shapePanel, speciesContextSpec,
                mcpSelected, milsSelected, issueManager);

//        shapePanel.setPreferredSize(scsls.getMaxSize());

        shapePanel.repaint();
    }

    public void setSpeciesContextSpec(SpeciesContextSpec scSpec) {
        SpeciesContextSpec oldValue = this.speciesContextSpec;
        if(oldValue != null) {
            oldValue.removePropertyChangeListener(eventHandler);
        }
        if(scSpec == null) {
            this.speciesContextSpec = null;
        } else {
            this.speciesContextSpec = scSpec;
            scSpec.addPropertyChangeListener(eventHandler);
        }
//        getSpeciesContextSpecParameterTableModel().setSpeciesContextSpec(scSpec);

        mcpSelected = null;
        milsSelected = null;
        updateInterface();
    }
    public void setBioModel(BioModel newValue) {
        if (bioModel == newValue) {
            return;
        }
        bioModel = newValue;
        if(bioModel == null) {
            return;
        }
        Model model = bioModel.getModel();
        if(model != null & model.getRbmModelContainer().getMolecularTypeList().size() > 0) {
//            splitPaneHorizontal.setDividerLocation(165);
        } else {
            // since we have no molecular types we initialize a much smaller shape panel
            // because we can only show a trivial shape (circle)
//            splitPaneHorizontal.setDividerLocation(195);
        }
    }


    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        SpeciesContextSpec speciesContextSpec = null;
        if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof SpeciesContextSpec) {
           speciesContextSpec = (SpeciesContextSpec) selectedObjects[0];
            if(speciesContextSpec.provenance != SpeciesContextSpec.Provenance.LangevinSpecs) {
                return;
            }
        }
        setSpeciesContextSpec(speciesContextSpec);
    }

    private JButton getZoomLargerButton() {
        if (zoomLargerButton == null) {
            zoomLargerButton = new JButton();		// "+"
//		ResizeCanvasShape.setCanvasNormalMod(zoomLargerButton, ResizeCanvasShape.Sign.expand);
            ZoomShapeIcon.setZoomMod(zoomLargerButton, ZoomShapeIcon.Sign.plus);
            zoomLargerButton.addActionListener(eventHandler);
        }
        return zoomLargerButton;
    }
    private JButton getZoomSmallerButton() {
        if (zoomSmallerButton == null) {
            zoomSmallerButton = new JButton();		// -
//		ResizeCanvasShape.setCanvasNormalMod(zoomSmallerButton, ResizeCanvasShape.Sign.shrink);
            ZoomShapeIcon.setZoomMod(zoomSmallerButton, ZoomShapeIcon.Sign.minus);
            zoomSmallerButton.addActionListener(eventHandler);
        }
        return zoomSmallerButton;
    }
}
