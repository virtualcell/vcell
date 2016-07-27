package org.vcell.model.rbm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.graph.AbstractComponentShape;
import cbit.vcell.graph.RulesShapePanel;
import cbit.vcell.graph.ZoomShape;

public class ViewReactionRulesShapesPanel extends DocumentEditorSubPanel {

	private EventHandler eventHandler = new EventHandler();

	RulesShapePanel shapePanel = null;
	List<AbstractComponentShape> rrShapeList = new ArrayList<AbstractComponentShape>();

	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;

	private final DocumentEditorSubPanel owner;


	private class EventHandler implements ActionListener, ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getZoomLargerButton()) {
				boolean ret = shapePanel.zoomLarger();
				getZoomLargerButton().setEnabled(ret);
				getZoomSmallerButton().setEnabled(true);
				updateShape();
				
			} else if (e.getSource() == getZoomSmallerButton()) {
				boolean ret = shapePanel.zoomSmaller();
				getZoomLargerButton().setEnabled(true);
				getZoomSmallerButton().setEnabled(ret);
				updateShape();
			}
		}
		
		/*
		 *  TODO: (Warning) this code heavily borrows from RbmUtils.parseReactionRule and ReactionRuleEditorPropertiesPanel
		 *  Make sure to keep them in sync
		 */
		@Override
		public void valueChanged(ListSelectionEvent e) {
		}
	}
	
	public ViewReactionRulesShapesPanel(DocumentEditorSubPanel owner) {
		super();
		this.owner = owner;
		initialize();
	}
	
	private void initialize() {
		try {
			setName("ViewGeneratedReactionsPanel");
			setLayout(new GridBagLayout());

			shapePanel = new RulesShapePanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					for(AbstractComponentShape stls : rrShapeList) {
						stls.paintSelf(g);
					}
				}
			};
			Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
			shapePanel.setLayout(new GridBagLayout());
			shapePanel.setBackground(Color.white);

			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	public void updateShape() {
		
	}
	private JButton getZoomLargerButton() {
		if (zoomLargerButton == null) {
			zoomLargerButton = new JButton();		// "+"
//			ResizeCanvasShape.setCanvasNormalMod(zoomLargerButton, ResizeCanvasShape.Sign.expand);
			ZoomShape.setZoomMod(zoomLargerButton, ZoomShape.Sign.plus);
			zoomLargerButton.addActionListener(eventHandler);
		}
		return zoomLargerButton;
	}
	private JButton getZoomSmallerButton() {
		if (zoomSmallerButton == null) {
			zoomSmallerButton = new JButton();		// -
//			ResizeCanvasShape.setCanvasNormalMod(zoomSmallerButton, ResizeCanvasShape.Sign.shrink);
			ZoomShape.setZoomMod(zoomSmallerButton, ZoomShape.Sign.minus);
			zoomSmallerButton.addActionListener(eventHandler);
		}
		return zoomSmallerButton;
	}
	
	private void handleException(java.lang.Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}

	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
	}

}
