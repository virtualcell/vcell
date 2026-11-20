package org.vcell.documentation;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;

/**
 * A small circled "?" beside a part of a window that a help page explains.
 *
 * <p>With a page ({@link #forPage}), a click opens VCell Help at it. With a short explanation
 * ({@link #setExplanation}), the explanation is its tooltip and shows in a popup under it when
 * clicked; when there is a page too, the popup ends with a "More in VCell Help" link to it.
 */
public final class HelpButton extends JButton {
	private final JLabel explanationLabel = new JLabel();
	private final JPopupMenu popup = new JPopupMenu();
	private final JButton moreButton = new JButton("More in VCell Help");
	private final String subject;
	private Supplier<String> pageId = () -> null;
	private String explanation = null;
	private long popupHiddenAt = 0; // when the popup last closed
	private int leadingGap = 0;

	/**
	 * @param name    the component name (tests and the debug bridge find it by this)
	 * @param subject what it explains, as a phrase: "the reaction diagram"; its accessible name is
	 *                "Help on " + subject
	 */
	public HelpButton(String name, String subject) {
		super(new HelpIcon());
		this.subject = subject;
		this.setName(name);
		this.getAccessibleContext().setAccessibleName("Help on " + subject);
		this.setContentAreaFilled(false);
		this.setFocusPainted(false);
		this.setRolloverEnabled(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.setMargin(new java.awt.Insets(0, 0, 0, 0));
		this.setLeadingGap(0);
		this.addFocusListener(new FocusAdapter() { // a focus ring: the button draws no other
			@Override
			public void focusGained(FocusEvent e) {
				HelpButton.this.updateBorder();
			}

			@Override
			public void focusLost(FocusEvent e) {
				HelpButton.this.updateBorder();
			}
		});
		this.explanationLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
		this.moreButton.setName(name + "More");
		this.moreButton.getAccessibleContext().setAccessibleDescription("Opens VCell Help at the page on " + subject);
		this.moreButton.addActionListener(e -> {
			this.popup.setVisible(false);
			this.openPage();
		});
		this.popup.setName(name + "Popup");
		this.popup.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				HelpButton.this.popupHiddenAt = System.currentTimeMillis();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		this.addActionListener(e -> this.clicked());
		this.updateToolTip();
	}

	/** A button that opens VCell Help at a page (a {@link HelpIds} constant). */
	public static HelpButton forPage(String name, String subject, String pageId) {
		HelpButton button = new HelpButton(name, subject);
		button.setPage(pageId);
		return button;
	}

	/** A button that opens VCell Help at the page this answers when clicked (null: the contents). */
	public static HelpButton forPage(String name, String subject, Supplier<String> pageId) {
		HelpButton button = new HelpButton(name, subject);
		button.setPage(pageId);
		return button;
	}

	/** The page a click opens, a {@link HelpIds} constant; null for none. */
	public void setPage(String pageId) {
		this.setPage(() -> pageId);
	}

	/** The page a click opens, asked for at the click; it may answer null (the help's contents). */
	public void setPage(Supplier<String> pageId) {
		this.pageId = pageId != null ? pageId : () -> null;
		this.updateToolTip();
	}

	/**
	 * A short explanation (HTML): the tooltip, and a popup under the button when clicked, ending
	 * with a link to the page when there is one. Null makes a click open the page directly.
	 */
	public void setExplanation(String html) {
		this.explanation = html;
		this.explanationLabel.setText(html);
		this.updateToolTip();
	}

	/** Space before the "?" (pixels), inside the button, so it sits apart from the text it follows. */
	public void setLeadingGap(int gap) {
		this.leadingGap = gap;
		this.updateBorder();
	}

	private void updateBorder() {
		Border gap = BorderFactory.createEmptyBorder(0, this.leadingGap, 0, 0);
		Border ring;
		if (this.isFocusOwner()) {
			Color focus = UIManager.getColor("Focus.color");
			ring = BorderFactory.createLineBorder(focus != null ? focus : VCellColors.getDimForeground());
		} else {
			ring = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		}
		this.setBorder(BorderFactory.createCompoundBorder(gap, ring));
	}

	/** The page a click opens now; null when none. */
	public String page() {
		return this.pageId.get();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (this.popup != null) javax.swing.SwingUtilities.updateComponentTreeUI(this.popup);
	}

	private void updateToolTip() {
		if (this.explanation != null) {
			this.setToolTipText(this.explanation);
		} else {
			this.setToolTipText("Open VCell Help on " + this.subject);
		}
	}

	private void clicked() {
		if (this.explanation == null) {
			this.openPage();
			return;
		}
		// the press of a click on the button while the popup shows closes it; that click is done then
		if (System.currentTimeMillis() - this.popupHiddenAt < 300) return;
		this.popup.removeAll();
		JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		content.add(this.explanationLabel, BorderLayout.CENTER);
		boolean hasPage = this.pageId.get() != null;
		if (hasPage) {
			JPanel linkRow = new JPanel(new BorderLayout());
			linkRow.setOpaque(false);
			linkRow.setBorder(BorderFactory.createEmptyBorder(0, 8, 6, 8));
			linkRow.add(this.moreButton, BorderLayout.WEST);
			content.add(linkRow, BorderLayout.SOUTH);
		}
		this.popup.add(content);
		this.popup.show(this, 0, this.getHeight());
		if (hasPage) this.moreButton.requestFocusInWindow();
	}

	private void openPage() {
		String page = this.pageId.get();
		if (page == null) {
			HelpViewer.showHelp();
		} else {
			HelpViewer.showHelp(page);
		}
	}

	/** A small circled question mark, about as tall as the text, in the dim text color. */
	private static final class HelpIcon implements Icon {
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int size = this.getIconWidth();
			g2.setColor(c.isEnabled() ? VCellColors.getDimForeground() : UIManager.getColor("Label.disabledForeground"));
			g2.setStroke(new BasicStroke(Math.max(1f, size / 14f)));
			g2.drawOval(x, y, size - 1, size - 1);
			g2.setFont(c.getFont().deriveFont(Font.BOLD, size * 0.7f));
			FontMetrics metrics = g2.getFontMetrics();
			g2.drawString("?", x + (size - metrics.stringWidth("?")) / 2f, y + (size - metrics.getHeight()) / 2f + metrics.getAscent());
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return VCellFonts.ems(1.15f);
		}

		@Override
		public int getIconHeight() {
			return this.getIconWidth();
		}
	}

	/**
	 * The tabs with this button at the right end of their tab row, over the empty space there;
	 * put the result where the tabs would go.
	 */
	public JComponent besideTabs(JTabbedPane tabs) {
		return new TabsWithHelp(tabs, this);
	}

	private static final class TabsWithHelp extends JLayeredPane {
		private final JTabbedPane tabs;
		private final HelpButton button;

		TabsWithHelp(JTabbedPane tabs, HelpButton button) {
			this.tabs = tabs;
			this.button = button;
			this.setName(tabs.getName() == null ? null : tabs.getName() + "WithHelp");
			this.add(tabs, JLayeredPane.DEFAULT_LAYER);
			this.add(button, JLayeredPane.PALETTE_LAYER);
		}

		@Override
		public void doLayout() {
			int width = this.getWidth();
			this.tabs.setBounds(0, 0, width, this.getHeight());
			Dimension size = this.button.getPreferredSize();
			int y = 3;
			if (this.tabs.getTabCount() > 0) {
				this.tabs.doLayout();
				java.awt.Rectangle tab = this.tabs.getBoundsAt(0);
				if (tab != null) y = tab.y + (tab.height - size.height) / 2;
			}
			this.button.setBounds(width - size.width - 4, Math.max(0, y), size.width, size.height);
		}

		@Override
		public Dimension getPreferredSize() {
			return this.tabs.getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize() {
			return this.tabs.getMinimumSize();
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize(); // never stretched by a BoxLayout
	}
}
