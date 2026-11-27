package cbit.vcell.client.desktop;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.desktop.ModelExplorerDetailsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The welcome window's right edge: a thin strip that, clicked, opens the selected model's details beside the lists
 * (what the Model Explorer shows of it), about doubling the window's width; clicked again, it closes them and the window
 * shrinks back. Only while logged in, when there are lists to select from.
 */
@SuppressWarnings("serial")
public class WelcomeDetailsSide extends JPanel {
	private static final int STRIP_WIDTH = 14;
	private static final int MIN_DETAILS_WIDTH = 240; // the window can't be narrowed to squeeze the details below this

	private final ModelExplorerDetailsPanel details;
	private final Supplier<Object> selection; // the model selected in the tab showing (null: none)
	private final Runnable resized; // after it widens or narrows the window
	private final Strip strip = new Strip();
	private boolean open = false;
	private int detailsWidth = -1; // as last open (-1: never yet, so the welcome panel's width)
	private int welcomeWidth = -1; // the welcome panel's width while the details are open: it keeps it, the details get the rest
	private int shiftedLeft = 0; // how far opening moved the window left (no room at the right), to move it back on closing
	private Object shown = null;

	public WelcomeDetailsSide(ModelExplorerDetailsPanel details, Supplier<Object> selection, Runnable resized) {
		super(new BorderLayout());
		this.details = details;
		this.selection = selection;
		this.resized = resized;
		this.setName("WelcomeDetailsSide");
		this.setOpaque(false);
		this.strip.addActionListener(e -> this.setOpen(!this.open, true));
		this.add(this.strip, BorderLayout.WEST);
		this.add(this.details, BorderLayout.CENTER);
		this.details.setVisible(false);
		this.showOpenness();
	}

	/** Lays the welcome panel and this side by side: the welcome panel its width, this the rest. */
	public static JComponent besideWelcome(JComponent welcome, WelcomeDetailsSide side) {
		JPanel content = new JPanel(new Beside(welcome, side));
		content.add(welcome);
		content.add(side);
		return content;
	}

	public boolean isOpen() {
		return this.open;
	}

	/** Shown only while there are lists to select from; hidden, the details close (the window is fitted after, by whoever hid it). */
	public void setAvailable(boolean available) {
		if (!available) this.setOpen(false, false);
		this.setVisible(available);
	}

	/** The selection changed: the details follow it while open. */
	public void selectionChanged() {
		if (!this.open) return;
		Object selected = this.selection.get();
		if (Objects.equals(selected, this.shown)) return; // a list refreshed, keeping its selection: no reloading the diagram
		this.shown = selected;
		this.details.show(selected, null);
	}

	/**
	 * Opens or closes the details. With {@code resize}, the window widens by the details' width (moving left if there's no
	 * room at the right), or narrows by it.
	 */
	public void setOpen(boolean open, boolean resize) {
		if (open == this.open) return;
		Window window = SwingUtilities.getWindowAncestor(this);
		Component welcome = this.getParent() == null ? null : this.getParent().getComponent(0);
		int before = this.open ? this.details.getWidth() : 0;
		this.open = open;
		if (open) {
			this.welcomeWidth = null == welcome ? -1 : welcome.getWidth();
			if (this.detailsWidth <= 0) this.detailsWidth = Math.max(MIN_DETAILS_WIDTH, this.welcomeWidth);
		} else {
			if (before > 0) this.detailsWidth = before; // as its user left it, for next time
			this.welcomeWidth = -1;
			this.shown = null;
			this.details.show(null, null); // stops any diagram loading
		}
		this.details.setVisible(open);
		this.showOpenness();
		if (open) this.selectionChanged();
		if (resize && null != window && window.isShowing()) this.resizeWindow(window, open ? this.detailsWidth : -before);
		this.revalidate();
		this.repaint();
		if (open) SwingUtilities.invokeLater(this.strip::requestFocusInWindow); // the strip kept the focus; it's in the same place
	}

	private void resizeWindow(Window window, int change) {
		Rectangle bounds = window.getBounds();
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int width = Math.min(screen.width, bounds.width + change);
		int x = bounds.x;
		if (change > 0) {
			int overRight = x + width - (screen.x + screen.width);
			this.shiftedLeft = Math.max(0, Math.min(overRight, x - screen.x));
			x -= this.shiftedLeft;
		} else {
			x += this.shiftedLeft;
			this.shiftedLeft = 0;
		}
		window.setBounds(x, bounds.y, width, bounds.height);
		window.validate();
		this.resized.run();
	}

	private void showOpenness() {
		String does = this.open ? "Hides the model details" : "Shows the selected model's details beside the lists";
		this.strip.setToolTipText(does);
		this.strip.getAccessibleContext().setAccessibleName(this.open ? "Hide Model Details" : "Show Model Details");
		this.strip.getAccessibleContext().setAccessibleDescription(does);
		this.strip.repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		// only as tall as the strip needs: the welcome panel decides the window's height
		return new Dimension(STRIP_WIDTH + (this.open ? this.detailsWidth : 0), 0);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(STRIP_WIDTH + (this.open ? MIN_DETAILS_WIDTH : 0), 0);
	}

	// The thin button at the window's edge: a line at its left, and a chevron pointing the way the details go
	private final class Strip extends JButton {
		private boolean hovered = false;

		Strip() {
			this.setName("WelcomeDetailsToggle");
			this.setContentAreaFilled(false);
			this.setBorderPainted(false);
			this.setFocusPainted(false);
			this.setRolloverEnabled(true);
			this.setMargin(new Insets(0, 0, 0, 0));
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			this.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override public void mouseEntered(java.awt.event.MouseEvent e) { Strip.this.hovered = true; Strip.this.repaint(); }
				@Override public void mouseExited(java.awt.event.MouseEvent e) { Strip.this.hovered = false; Strip.this.repaint(); }
			});
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(STRIP_WIDTH, 0);
		}

		@Override
		public Dimension getMinimumSize() {
			return this.getPreferredSize();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int width = this.getWidth(), height = this.getHeight();
			Color background = this.getParent().getBackground();
			if (this.getModel().isPressed()) g2.setColor(VCellColors.blend(background, this.getForeground(), 0.16f));
			else if (this.hovered) g2.setColor(VCellColors.blend(background, this.getForeground(), 0.08f));
			else g2.setColor(VCellColors.blend(background, this.getForeground(), 0.03f));
			g2.fillRect(0, 0, width, height);
			Color line = UIManager.getColor("Separator.foreground");
			g2.setColor(null == line ? VCellColors.blend(background, this.getForeground(), 0.25f) : line);
			g2.drawLine(0, 0, 0, height);
			if (WelcomeDetailsSide.this.open) g2.drawLine(width - 1, 0, width - 1, height);
			if (this.isFocusOwner()) {
				Color focus = UIManager.getColor("Focus.color");
				g2.setColor(null == focus ? UIManager.getColor("List.selectionBackground") : focus);
				g2.setStroke(new BasicStroke(2f));
				g2.drawRect(2, 2, width - 4, height - 4);
			}
			// the chevron: right (›) to open, left (‹) to close
			int middleX = width / 2, middleY = height / 2, half = 5, depth = 3;
			int tip = WelcomeDetailsSide.this.open ? middleX - depth / 2 - 1 : middleX + depth / 2 + 1;
			int back = WelcomeDetailsSide.this.open ? tip + depth : tip - depth;
			g2.setColor(this.hovered ? this.getForeground() : VCellColors.getDimForeground());
			g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawPolyline(new int[] { back, tip, back }, new int[] { middleY - half, middleY, middleY + half }, 3);
			g2.dispose();
		}
	}

	// The welcome panel at its width (as it was when the details opened), and the side the rest; a narrower window
	// squeezes the details to their least, then the welcome panel. The welcome panel alone decides the height.
	private static final class Beside implements LayoutManager {
		private final Component welcome;
		private final WelcomeDetailsSide side;

		Beside(Component welcome, WelcomeDetailsSide side) {
			this.welcome = welcome;
			this.side = side;
		}

		@Override public void addLayoutComponent(String name, Component component) {}
		@Override public void removeLayoutComponent(Component component) {}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return this.size(parent, this.welcome.getPreferredSize(), this.side.getPreferredSize());
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return this.size(parent, this.welcome.getMinimumSize(), this.side.getMinimumSize());
		}

		private Dimension size(Container parent, Dimension welcome, Dimension side) {
			Insets insets = parent.getInsets();
			int width = welcome.width + (this.side.isVisible() ? side.width : 0);
			return new Dimension(width + insets.left + insets.right, welcome.height + insets.top + insets.bottom);
		}

		@Override
		public void layoutContainer(Container parent) {
			Insets insets = parent.getInsets();
			int width = parent.getWidth() - insets.left - insets.right, height = parent.getHeight() - insets.top - insets.bottom;
			int sideWidth = 0;
			if (this.side.isVisible()) {
				int least = this.side.getMinimumSize().width;
				if (!this.side.open) sideWidth = least;
				else {
					int welcomeWidth = this.side.welcomeWidth > 0 ? this.side.welcomeWidth : this.welcome.getPreferredSize().width;
					sideWidth = Math.max(least, width - welcomeWidth);
				}
				sideWidth = Math.min(sideWidth, width);
			}
			this.welcome.setBounds(insets.left, insets.top, width - sideWidth, height);
			this.side.setBounds(insets.left + width - sideWidth, insets.top, sideWidth, height);
		}
	}
}
