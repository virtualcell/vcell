package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.vcell.util.gui.GuiUtils;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.gui.GraphPane;

/**
 * A titled, view-only copy of a diagram, shrunk to fit: its own graph model on the same document, with no tool to edit
 * it. Clicking it, or its Open button, opens the diagram itself.
 * <p><b>Currently unused</b>: it was made for the Biochemistry Summary, which nothing opens since the Biochemistry node
 * was removed. Kept for reuse: any diagram can be previewed this way.</p>
 */
@SuppressWarnings("serial")
public class DiagramPreview extends JPanel {
	private static final String DIAGRAM = "diagram";
	private static final String EMPTY = "empty";

	private final GraphModel graphModel;
	private final BooleanSupplier isEmpty;
	private final GraphPane graphPane = new GraphPane() {
		@Override
		public boolean isFocusTraversable() {
			return false; // the Open button is its keyboard way in (GraphPane asks for focus whatever its focusable flag says)
		}
	};
	private final CardLayout cards = new CardLayout();
	private final JPanel body = new JPanel(this.cards);
	private boolean fitQueued = false;

	/**
	 * @param title the diagram's name, above it
	 * @param graphModel the copy's own graph model, already given the document
	 * @param open opens the diagram itself
	 * @param emptyText shown instead of the diagram while <code>isEmpty</code>
	 */
	public DiagramPreview(String name, String title, GraphModel graphModel, Runnable open, String emptyText, BooleanSupplier isEmpty) {
		super(new BorderLayout(0, 4));
		this.setName(name);
		this.graphModel = graphModel;
		this.isEmpty = isEmpty;

		JButton openButton = new JButton("Open");
		openButton.setName(name + "OpenButton");
		openButton.setToolTipText("Open the " + title);
		openButton.addActionListener(e -> open.run());
		JPanel titleRow = new JPanel(new BorderLayout(8, 0));
		titleRow.add(GuiUtils.createBoldJLabel(title), BorderLayout.WEST);
		titleRow.add(openButton, BorderLayout.EAST);
		this.add(titleRow, BorderLayout.NORTH);

		this.graphPane.setName(name + "Pane");
		this.graphPane.setGraphModel(graphModel);
		this.graphPane.setToolTipText("Click to open the " + title);
		this.graphPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.graphPane.setFocusable(false);
		this.graphPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) open.run();
			}
		});
		JScrollPane scroll = new JScrollPane(this.graphPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				DiagramPreview.this.queueFit();
			}
		});
		JLabel empty = new JLabel(emptyText, SwingConstants.CENTER) {
			@Override
			public void updateUI() { // the theme's secondary text color, again whenever the theme changes
				super.updateUI();
				this.setForeground(UIManager.getColor("Label.disabledForeground"));
			}
		};
		empty.setName(name + "EmptyLabel");
		JPanel emptyPanel = new JPanel(new BorderLayout());
		emptyPanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground") == null ? java.awt.Color.GRAY : UIManager.getColor("Separator.foreground")));
		emptyPanel.add(empty, BorderLayout.CENTER);
		this.body.add(scroll, DiagramPreview.DIAGRAM);
		this.body.add(emptyPanel, DiagramPreview.EMPTY);
		this.add(this.body, BorderLayout.CENTER);
	}

	/** Redraws from the document, as the diagram is now; shapes get the source's positions, if there's one to copy. */
	public void refresh(GraphModel positionsFrom) {
		boolean empty = this.isEmpty.getAsBoolean();
		this.cards.show(this.body, empty ? DiagramPreview.EMPTY : DiagramPreview.DIAGRAM);
		if (empty) return;
		this.graphModel.refreshAll();
		if (positionsFrom != null) DiagramPreview.copyPositions(positionsFrom, this.graphModel);
		this.graphPane.updateAll();
		this.queueFit();
	}

	GraphPane getGraphPane() {
		return this.graphPane;
	}

	// each shape where the same object's shape is in the source (a diagram whose layout isn't saved with the document)
	static void copyPositions(GraphModel from, GraphModel to) {
		for (Shape shape : to.getShapes()) {
			Shape source = from.getShapeFromModelObject(shape.getModelObject());
			if (source == null) continue;
			Point position = source.getSpaceManager().getRelPos();
			shape.getSpaceManager().setRelPos(new Point(position));
		}
	}

	// shrinks the diagram to the space it has, once the layout settles
	private void queueFit() {
		if (this.fitQueued) return;
		this.fitQueued = true;
		SwingUtilities.invokeLater(() -> {
			this.fitQueued = false;
			if (!this.graphPane.isShowing()) return;
			this.graphPane.zoomToFit();
		});
	}
}
