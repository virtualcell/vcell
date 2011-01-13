package cbit.vcell.client.desktop.biomodel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 ©*/
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;

@SuppressWarnings("serial")
public abstract class DocumentEditorTreeCellRenderer extends DefaultTreeCellRenderer {
	protected Font regularFont = null;
	protected Font boldFont = null;
	
	private JTree ownerTree;
	private static Icon outputFunctionIcon = new ImageIcon(DocumentEditorTreeCellRenderer.class.getResource("/icons/function_icon.png"));
	
	public DocumentEditorTreeCellRenderer(JTree tree) {
		super();
		ownerTree = tree;
		setPreferredSize(new Dimension(150,30));
		setBorder(new EmptyBorder(0, 2, 0, 0));
	}
	
	public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (regularFont == null) {
			regularFont = getFont();
			boldFont = regularFont.deriveFont(Font.BOLD);
		}
		Font font = regularFont;
		Icon icon = null;
    	String labelText = null;
    	String toolTipPrefix = "";
    	String toolTipSuffix = "";
		if (value instanceof LinkNode){
			LinkNode ln = (LinkNode)value;
			String link = ln.getLink();
			String text = ln.getText();
			String qualifier = ln.getMiriamQualifier().getDescription();
			if (link != null) {
				String colorString = (sel)?"white":"blue";
				toolTipPrefix = "double-click to open link " + link;
				labelText = "<html>"+qualifier+"&nbsp;<font color=\""+colorString+"\"><a href=" + link + ">" + text + "</a></font></html>";
			}else{
				String colorString = (sel)?"white":"black";
				labelText = "<html>"+qualifier+"&nbsp;<font color=\""+colorString+"\">" + text + "</font></html>";
			}
		} else if (value instanceof BioModelNode) {
	        BioModelNode node = (BioModelNode)value;
	        Object userObj = node.getUserObject();
	        if (userObj instanceof Model) {
	    		labelText = "Biological Model";
	    		font = boldFont;
	    	} else if (userObj instanceof SimulationContext) {		// --- root: application name
	    		font = boldFont;
	    		labelText = ((SimulationContext)userObj).getName();
	    		toolTipPrefix = "Application: ";
	    	} else if (userObj instanceof DocumentEditorTreeFolderNode) {		// --- 1st level folders
	    		DocumentEditorTreeFolderNode folder = (DocumentEditorTreeFolderNode)userObj;
	    		DocumentEditorTreeFolderClass folderClass = folder.getFolderClass();
	    		if (folder.isFirstLevel()
	    				||folderClass == DocumentEditorTreeFolderClass.SIMULATIONS_NODE
	    				|| folderClass == DocumentEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE
	    				|| folderClass == DocumentEditorTreeFolderClass.ANALYSIS_NODE) {
	    			font = boldFont;
	    		} 
	    		labelText = folder.getName();
	    	} else if (userObj instanceof SpeciesContext) { 	// --- species context
	    		labelText = ((SpeciesContext)userObj).getName();
	    		toolTipPrefix = "Species: ";
	    	} else if (userObj instanceof SpeciesContextSpec) { 	// --- species context
	    		labelText = ((SpeciesContextSpec)userObj).getSpeciesContext().getName();
	    		toolTipPrefix = "Species Parameters: ";
	        } else if (userObj instanceof ModelParameter) {		// --- global parameter
	        	labelText = ((ModelParameter)userObj).getName();
	        	toolTipPrefix = "Global Parameter: ";
	        } else if (userObj instanceof SimpleReaction) {		// --- simple reaction
	        	labelText = ((ReactionStep)userObj).getName();
	        	toolTipPrefix = "Simple Reaction: ";
	        } else if (userObj instanceof FluxReaction) {		// --- flux reaction
	        	labelText = ((ReactionStep)userObj).getName();
	        	toolTipPrefix = "Flux Reaction: ";
	        } else if (userObj instanceof ReactionSpec) {
	        	labelText = ((ReactionSpec)userObj).getReactionStep().getName();
	        	toolTipPrefix = "Reaction Settings: ";
	        } else if (userObj instanceof DataSymbol) {			// --- field data
	        	labelText = ((DataSymbol)userObj).getName();
	        	toolTipPrefix = "Dataset: ";
				toolTipSuffix = ((FieldDataSymbol)userObj).getExternalDataIdentifier().getName();
	        } else if (userObj instanceof BioEvent) {			// --- event
	        	BioEvent bioEvent = (BioEvent)userObj;
	        	SimulationContext simulationContext = bioEvent.getSimulationContext();
				if (simulationContext.getGeometry() != null && simulationContext.getGeometry().getDimension() > 0 
						|| simulationContext.isStoch()) {
					setEnabled(false);
					setDisabledIcon(this.getClosedIcon());
				} else {
					labelText = bioEvent.getName();
					toolTipPrefix = "Event: ";
				}
	    	} else if (userObj instanceof Simulation) {
	        	labelText = ((Simulation)userObj).getName();
	        	toolTipPrefix = "Simulation: ";
	    	} else if (userObj instanceof AnalysisTask) {
	    		labelText = ((AnalysisTask)userObj).getName();
	    		toolTipPrefix = "Analysis Task: ";
	        } else if (userObj instanceof AnnotatedFunction) {
	        	labelText = ((AnnotatedFunction)userObj).getName();
	        	toolTipPrefix = "Output Function: ";
	        	icon = outputFunctionIcon;
	        } else if (userObj instanceof Structure) {
	        	labelText = ((Structure)userObj).getName();
	        	toolTipPrefix = ((Structure)userObj).getTypeName() + ": ";
	        }
		}
		setIcon(icon);
		setFont(font);
		setText(labelText);
		if (toolTipSuffix.length() == 0) {
			toolTipSuffix = labelText;
		}
		setToolTipText(toolTipPrefix + toolTipSuffix);
        return this;
    }

	@Override
	protected void paintComponent(Graphics g) {
		if (getForeground() == getTextSelectionColor()) {
			String text = getText();
			if (text != null) {
				FontMetrics metrics = getFontMetrics(getFont());
				// empty border 2
				int startX = 2 + (getIcon() == null ? 0 : getIcon().getIconWidth() + getIconTextGap());
				int startY = 0; //You probably have some vertical offset to add here.
				int length = metrics.stringWidth(text);
				int height = ownerTree.getRowHeight();
				g.fillRect(startX + length + 1, startY, getWidth() - length - startX, height);
			}
		}
		super.paintComponent(g);
	}
}
