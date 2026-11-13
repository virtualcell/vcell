package cbit.vcell.client.desktop.biomodel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * The navigation column's look: each row a rounded rectangle the column's full width, rather than a tree line with
 * carets. A row's level shows in its height and type instead of an indent: the model tallest and bold, its sections
 * tall and bold, theirs medium and plain, and theirs short and italic, set in by a small tab. A section with subsections has a chevron on the right; clicking the section opens it (and its subsections drop down beneath),
 * clicking it again once it's open folds them away, and clicking the chevron only folds or unfolds. It stays a JTree,
 * so its keyboard use (arrows, Left and Right to fold) and everything that follows its selection are unchanged.
 */
public final class NavigationTreeStyle {
	private NavigationTreeStyle() {}

	private static final String HOVER_ROW = "NavigationTreeStyle.hoverRow";
	private static final Color CLEAR = new Color(0, 0, 0, 0);
	static final int CHEVRON_WIDTH = 22; // the strip at a row's right end that folds without choosing

	/** Gives the tree this look; call from its updateUI (after super's), so it survives theme changes. */
	public static void install(JTree tree) {
		tree.setUI(new NavigationTreeUI());
		tree.setRootVisible(true);
		tree.setShowsRootHandles(false);
		tree.setToggleClickCount(0); // clicks fold and unfold as above, not on double-click
		tree.setLargeModel(false);
		tree.setRowHeight(0); // each row as tall as its rectangle needs, at any text size
	}

	/** The renderer, wrapped to draw its row as a rectangle; one already wrapped is returned as it is. */
	public static TreeCellRenderer wrap(TreeCellRenderer renderer) {
		if (renderer == null || renderer instanceof RowRenderer) return renderer;
		return new RowRenderer(renderer);
	}

	// is the row's node a section that folds: it has subsections, and isn't the model itself (always open)
	static boolean folds(JTree tree, TreePath path) {
		return path != null && path.getPathCount() > 1 && !tree.getModel().isLeaf(path.getLastPathComponent());
	}

	/** Draws rows as rectangles across the column, without carets or lines, and folds sections on click. */
	static final class NavigationTreeUI extends BasicTreeUI {
		private final ComponentListener viewportResized = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				NavigationTreeUI.this.widthChanged();
			}
		};
		private JViewport viewport = null;
		private final MouseAdapter hover = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				NavigationTreeUI.this.setHoverRow(NavigationTreeUI.this.rowAt(e.getX(), e.getY()));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				NavigationTreeUI.this.setHoverRow(-1);
			}
		};

		@Override
		protected void installDefaults() {
			super.installDefaults();
			this.setLeftChildIndent(0);
			this.setRightChildIndent(0); // no indent: every row the column's width; its level shows in its height and type
		}

		@Override
		protected void installListeners() {
			super.installListeners();
			this.tree.addMouseListener(this.hover);
			this.tree.addMouseMotionListener(this.hover);
			this.tree.addHierarchyListener(e -> this.followViewport());
			this.followViewport();
		}

		@Override
		protected void uninstallListeners() {
			this.tree.removeMouseListener(this.hover);
			this.tree.removeMouseMotionListener(this.hover);
			if (this.viewport != null) this.viewport.removeComponentListener(this.viewportResized);
			this.viewport = null;
			super.uninstallListeners();
		}

		// rows span the column's width, so they're laid out again whenever it changes
		private void followViewport() {
			JViewport now = this.tree.getParent() instanceof JViewport parent ? parent : null;
			if (now == this.viewport) return;
			if (this.viewport != null) this.viewport.removeComponentListener(this.viewportResized);
			this.viewport = now;
			if (now != null) now.addComponentListener(this.viewportResized);
			this.widthChanged();
		}

		private void widthChanged() {
			if (this.treeState != null) this.treeState.invalidateSizes();
			this.updateSize();
			this.tree.repaint();
		}

		private int availableWidth() {
			return this.viewport != null ? this.viewport.getExtentSize().width : this.tree.getWidth();
		}

		@Override
		protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
			return new NodeDimensionsHandler() {
				@Override
				public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
					Rectangle bounds = super.getNodeDimensions(value, row, depth, expanded, size);
					if (bounds == null) return null;
					int fill = NavigationTreeUI.this.availableWidth() - bounds.x - 4; // to the column's right edge
					if (fill > bounds.width) bounds.width = fill;
					return bounds;
				}
			};
		}

		// no carets and no lines: a section's chevron is drawn in its row
		@Override protected void paintExpandControl(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {}
		@Override protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {}
		@Override protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {}
		@Override protected boolean isLocationInExpandControl(TreePath path, int mouseX, int mouseY) { return false; }

		/**
		 * A click on a section: on its chevron, folds or unfolds it and nothing more; elsewhere, chooses it and opens
		 * it, or, if it was already chosen, folds or unfolds it.
		 */
		@Override
		protected void selectPathForEvent(TreePath path, MouseEvent event) {
			boolean section = SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1 && NavigationTreeStyle.folds(this.tree, path);
			if (section && this.onChevron(path, event.getX())) {
				this.toggle(path);
				return;
			}
			boolean wasChosen = this.tree.isPathSelected(path);
			super.selectPathForEvent(path, event);
			if (!section || event.isShiftDown() || event.isControlDown() || event.isMetaDown()) return;
			if (wasChosen) this.toggle(path);
			else this.tree.expandPath(path);
		}

		private boolean onChevron(TreePath path, int x) {
			Rectangle bounds = this.tree.getPathBounds(path);
			return bounds != null && x >= bounds.x + bounds.width - NavigationTreeStyle.CHEVRON_WIDTH;
		}

		private void toggle(TreePath path) {
			if (this.tree.isExpanded(path)) this.tree.collapsePath(path);
			else this.tree.expandPath(path);
		}

		private int rowAt(int x, int y) {
			int row = this.tree.getClosestRowForLocation(x, y);
			Rectangle bounds = row < 0 ? null : this.tree.getRowBounds(row);
			return bounds != null && bounds.contains(x, y) ? row : -1;
		}

		private void setHoverRow(int row) {
			Object before = this.tree.getClientProperty(NavigationTreeStyle.HOVER_ROW);
			if (before instanceof Integer old && old == row) return;
			this.tree.putClientProperty(NavigationTreeStyle.HOVER_ROW, row);
			this.repaintRow(before instanceof Integer old ? old : -1);
			this.repaintRow(row);
		}

		private void repaintRow(int row) {
			Rectangle bounds = row < 0 ? null : this.tree.getRowBounds(row);
			if (bounds != null) this.tree.repaint(bounds);
		}
	}

	/** A row: the window's own renderer (its icon, name and count) inside a rounded rectangle, with a chevron. */
	@SuppressWarnings("serial")
	static final class RowRenderer extends JPanel implements TreeCellRenderer {
		private final TreeCellRenderer delegate;
		private final JLabel chevron = new JLabel();
		private boolean chosen = false;
		private boolean hovered = false;
		private boolean focused = false;
		private Color rowBackground = Color.WHITE;
		private int inset = 0; // a small tab before the deepest rows' rectangles

		RowRenderer(TreeCellRenderer delegate) {
			super(new BorderLayout(4, 0));
			this.delegate = delegate;
			this.setOpaque(false);
			if (delegate instanceof Component component) this.add(component, BorderLayout.CENTER); // themed with the row
			this.chevron.setPreferredSize(new java.awt.Dimension(NavigationTreeStyle.CHEVRON_WIDTH - 8, 1));
			this.add(this.chevron, BorderLayout.EAST);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			// the delegate draws text and icon only: the rectangle shows the choice and focus
			Component content = this.delegate.getTreeCellRendererComponent(tree, value, false, expanded, leaf, row, false);
			if (content.getParent() != this) {
				this.removeAll();
				this.add(content, BorderLayout.CENTER);
				this.add(this.chevron, BorderLayout.EAST);
			}
			if (content instanceof JComponent component) component.setOpaque(false);
			if (content instanceof DefaultTreeCellRenderer label) { // it would fill its own box inside the rectangle
				label.setBackgroundNonSelectionColor(NavigationTreeStyle.CLEAR);
				label.setBackgroundSelectionColor(NavigationTreeStyle.CLEAR);
				label.setBorderSelectionColor(null);
				label.setBackground(NavigationTreeStyle.CLEAR);
			}
			this.chosen = selected;
			this.focused = tree.hasFocus();
			this.rowBackground = tree.getBackground() != null ? tree.getBackground() : Color.WHITE;
			this.hovered = Integer.valueOf(row).equals(tree.getClientProperty(NavigationTreeStyle.HOVER_ROW));
			Color text = selected ? NavigationTreeStyle.chosenText(tree) : tree.getForeground();
			content.setForeground(text);
			TreePath path = tree.getPathForRow(row);
			int level = NavigationTreeStyle.level(tree, value, path);
			content.setFont(NavigationTreeStyle.fontFor(tree.getFont(), level));
			int padding = NavigationTreeStyle.paddingFor(level);
			this.inset = NavigationTreeStyle.insetFor(level);
			this.setBorder(BorderFactory.createEmptyBorder(padding, 10 + this.inset, padding, 6));
			boolean folds = path != null ? NavigationTreeStyle.folds(tree, path) : !leaf && row > 0;
			this.chevron.setIcon(folds ? new ChevronIcon(expanded, text) : null);
			this.chevron.setToolTipText(null);
			String name = content instanceof JLabel label ? label.getText() : String.valueOf(value);
			this.getAccessibleContext().setAccessibleName(name);
			return this;
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics.create();
			try {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color background = this.rowBackground;
				Color fill = this.chosen ? NavigationTreeStyle.chosenFill(this.focused)
						: NavigationTreeStyle.shade(background, this.hovered ? 0.14 : 0.07);
				int arc = 8;
				g.setColor(fill);
				int width = this.getWidth() - 1 - this.inset;
				g.fillRoundRect(this.inset, 1, width, this.getHeight() - 3, arc, arc);
				if (!this.chosen) {
					g.setColor(NavigationTreeStyle.shade(background, 0.18));
					g.drawRoundRect(this.inset, 1, width, this.getHeight() - 3, arc, arc);
				}
			} finally {
				g.dispose();
			}
			super.paintComponent(graphics);
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			return this.delegate instanceof JComponent component ? component.getToolTipText() : null;
		}
	}

	/** A small chevron: pointing down when the section is open, right when it's folded. */
	private record ChevronIcon(boolean open, Color color) implements Icon {
		@Override public int getIconWidth() { return 10; }
		@Override public int getIconHeight() { return 10; }

		@Override
		public void paintIcon(Component c, Graphics graphics, int x, int y) {
			Graphics2D g = (Graphics2D) graphics.create();
			try {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(this.color);
				g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				Path2D.Double chevron = new Path2D.Double();
				if (this.open) {
					chevron.moveTo(x + 1.5, y + 3.5);
					chevron.lineTo(x + 5, y + 7);
					chevron.lineTo(x + 8.5, y + 3.5);
				} else {
					chevron.moveTo(x + 3.5, y + 1.5);
					chevron.lineTo(x + 7, y + 5);
					chevron.lineTo(x + 3.5, y + 8.5);
				}
				g.draw(chevron);
			} finally {
				g.dispose();
			}
		}
	}

	/** How deep a row is: 0 for the model, 1 for its sections, and so on. */
	static int level(JTree tree, Object value, TreePath path) {
		if (path != null) return path.getPathCount() - 1;
		return value instanceof javax.swing.tree.TreeNode node ? NavigationTreeStyle.depth(node) : 0;
	}

	private static int depth(javax.swing.tree.TreeNode node) {
		int depth = 0;
		for (javax.swing.tree.TreeNode parent = node.getParent(); parent != null; parent = parent.getParent()) depth++;
		return depth;
	}

	/** A level's type: the model a size larger and bold, its sections bold, theirs plain, and deeper rows italic. */
	static java.awt.Font fontFor(java.awt.Font base, int level) {
		return switch (level) {
			case 0 -> base.deriveFont(java.awt.Font.BOLD, base.getSize2D() + 2);
			case 1 -> base.deriveFont(java.awt.Font.BOLD);
			case 2 -> base.deriveFont(java.awt.Font.PLAIN);
			default -> base.deriveFont(java.awt.Font.ITALIC);
		};
	}

	/** A level's tab before its rectangle: only the deepest rows (level 3 on) start a little in. */
	static int insetFor(int level) {
		return level >= 3 ? 14 : 0;
	}

	/** A level's space above and below its text, so rows shorten as they go deeper. */
	static int paddingFor(int level) {
		return switch (level) {
			case 0 -> 9;
			case 1 -> 7;
			case 2 -> 4;
			default -> 2;
		};
	}

	// the theme's selection, dimmed while the column doesn't have the focus
	static Color chosenFill(boolean focused) {
		Color active = UIManager.getColor("Tree.selectionBackground");
		if (active == null) active = new Color(0x2F6FD6);
		if (focused) return active;
		Color inactive = UIManager.getColor("Tree.selectionInactiveBackground");
		return inactive != null ? inactive : active.darker();
	}

	static Color chosenText(JTree tree) {
		Color text = UIManager.getColor("Tree.selectionForeground");
		return text != null ? text : tree.getForeground();
	}

	/** The background, moved towards its contrast (lighter on dark, darker on light) by <code>amount</code>, 0 to 1. */
	static Color shade(Color background, double amount) {
		double luminance = (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;
		int target = luminance < 0.5 ? 255 : 0;
		return new Color(
				(int) Math.round(background.getRed() + (target - background.getRed()) * amount),
				(int) Math.round(background.getGreen() + (target - background.getGreen()) * amount),
				(int) Math.round(background.getBlue() + (target - background.getBlue()) * amount));
	}
}
