package org.vcell.util.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A row of buttons, ending in one that stretches (a search field, say), that wraps rather than insisting on its width:
 * the items go left to right, one of them (the last, unless another is named) filling what's left of its line, and
 * whatever doesn't fit goes to the next line. Its least width is its widest item's, so a narrow window can be narrow: the row grows taller instead of
 * pushing back on whatever holds it (a split pane, say, which would otherwise squeeze its other side).
 */
public class ButtonRowLayout implements LayoutManager {
	private final int gap;
	private final int stretchMinimum; // the stretching item's least width on a line shared with others
	private int lastLines = 1;
	private Component stretching = null; // the item that fills its line's spare width; null: the last one

	/**
	 * @param gap pixels between items and between lines
	 * @param stretchMinimum how narrow the last item may get before it takes a line of its own
	 */
	public ButtonRowLayout(int gap, int stretchMinimum) {
		this.gap = gap;
		this.stretchMinimum = stretchMinimum;
	}

	/** Names the item that fills its line's spare width (rather than the last). */
	public void setStretching(Component component) {
		this.stretching = component;
	}

	/** A row led by a "Search" label and its field, which stretches to fill its line, then these buttons. */
	public static JPanel withSearch(List<? extends JComponent> buttons, JComponent searchField) {
		ButtonRowLayout layout = new ButtonRowLayout(8, 220);
		JPanel row = new JPanel(layout);
		JPanel search = new JPanel(new java.awt.BorderLayout(6, 0));
		search.setName("SearchGroup");
		JLabel label = new JLabel("Search");
		label.setLabelFor(searchField);
		search.add(label, java.awt.BorderLayout.WEST);
		search.add(searchField, java.awt.BorderLayout.CENTER);
		row.add(search); // first: what's looked for, before what's done to it
		for (JComponent button : buttons) row.add(button);
		layout.setStretching(search);
		row.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
		return row;
	}

	@Override public void addLayoutComponent(String name, Component component) {}
	@Override public void removeLayoutComponent(Component component) {}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		int width = target.getWidth() > 0 ? target.getWidth() : Integer.MAX_VALUE; // unknown yet: one line
		return this.size(target, width, true);
	}

	@Override
	public Dimension minimumLayoutSize(Container target) {
		Insets insets = target.getInsets();
		int widest = 0;
		for (Component component : ButtonRowLayout.visible(target)) widest = Math.max(widest, component.getMinimumSize().width);
		Dimension preferred = this.preferredLayoutSize(target);
		return new Dimension(widest + insets.left + insets.right, preferred.height); // the widest item; the rest wraps
	}

	@Override
	public void layoutContainer(Container target) {
		Insets insets = target.getInsets();
		int available = Math.max(0, target.getWidth() - insets.left - insets.right);
		List<List<Component>> lines = this.lines(target, available);
		int y = insets.top;
		for (List<Component> line : lines) {
			int lineHeight = 0;
			for (Component component : line) lineHeight = Math.max(lineHeight, component.getPreferredSize().height);
			// the stretching item, wherever it is on the line, takes the line's spare width
			int spare = available;
			for (Component component : line) spare -= this.lineWidth(target, component, available);
			spare -= this.gap * (line.size() - 1);
			int x = insets.left;
			for (int i = 0; i < line.size(); i++) {
				Component component = line.get(i);
				Dimension preferred = component.getPreferredSize();
				int width = this.lineWidth(target, component, available);
				if (this.isStretching(target, component)) width += Math.max(0, spare);
				component.setBounds(x, y + (lineHeight - preferred.height) / 2, width, preferred.height);
				x += width + this.gap;
			}
			y += lineHeight + this.gap;
		}
		// a change in the number of lines changes the row's height: ask its container to make room (once, not each layout)
		if (lines.size() != this.lastLines) {
			this.lastLines = lines.size();
			SwingUtilities.invokeLater(() -> {
				target.invalidate();
				Container parent = target.getParent();
				if (parent instanceof JComponent component) component.revalidate();
			});
		}
	}

	// the items, line by line, for this width
	List<List<Component>> lines(Container target, int available) {
		List<List<Component>> lines = new ArrayList<>();
		List<Component> line = new ArrayList<>();
		int used = 0;
		for (Component component : ButtonRowLayout.visible(target)) {
			// the stretching item shares a line only with room for at least its minimum (a search field prefers little)
			int width = this.isStretching(target, component) ? Math.max(this.stretchMinimum, component.getPreferredSize().width) : component.getPreferredSize().width;
			int needed = line.isEmpty() ? width : used + this.gap + width;
			if (!line.isEmpty() && needed > available) {
				lines.add(line);
				line = new ArrayList<>();
				needed = width;
			}
			line.add(component);
			used = needed;
		}
		if (!line.isEmpty()) lines.add(line);
		return lines;
	}

	private Dimension size(Container target, int width, boolean preferred) {
		Insets insets = target.getInsets();
		int available = width == Integer.MAX_VALUE ? Integer.MAX_VALUE : Math.max(0, width - insets.left - insets.right);
		List<List<Component>> lines = this.lines(target, available);
		int widest = 0;
		int height = 0;
		for (List<Component> line : lines) {
			int lineWidth = 0;
			int lineHeight = 0;
			for (Component component : line) {
				Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
				lineWidth += size.width + (lineWidth > 0 ? this.gap : 0);
				lineHeight = Math.max(lineHeight, size.height);
			}
			widest = Math.max(widest, lineWidth);
			height += lineHeight;
		}
		height += Math.max(0, lines.size() - 1) * this.gap;
		return new Dimension(widest + insets.left + insets.right, height + insets.top + insets.bottom);
	}

	// an item's width on its line, before the stretching one gets the spare
	private int lineWidth(Container target, Component component, int available) {
		int preferred = component.getPreferredSize().width;
		if (this.isStretching(target, component)) return Math.min(available, Math.max(this.stretchMinimum, preferred));
		return Math.min(preferred, available);
	}

	private boolean isStretching(Container target, Component component) {
		if (this.stretching != null) return this.stretching == component;
		List<Component> visible = ButtonRowLayout.visible(target);
		return !visible.isEmpty() && visible.get(visible.size() - 1) == component;
	}

	private static List<Component> visible(Container target) {
		List<Component> visible = new ArrayList<>();
		for (Component component : target.getComponents()) {
			if (component.isVisible()) visible.add(component);
		}
		return visible;
	}
}
