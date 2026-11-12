package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class NavigationTreeStyleTest {

	// model > (Section > (Part one, Part two)), Leaf
	private final DefaultMutableTreeNode model = new DefaultMutableTreeNode("Model");
	private final DefaultMutableTreeNode section = new DefaultMutableTreeNode("Section");
	private final DefaultMutableTreeNode leaf = new DefaultMutableTreeNode("Leaf");
	private JTree tree;
	private JScrollPane scroll;

	private void build() {
		this.section.add(new DefaultMutableTreeNode("Part one"));
		this.section.add(new DefaultMutableTreeNode("Part two"));
		this.model.add(this.section);
		this.model.add(this.leaf);
		this.tree = new JTree(this.model) {
			@Override
			public void updateUI() {
				super.updateUI();
				NavigationTreeStyle.install(this);
			}

			@Override
			public void setCellRenderer(javax.swing.tree.TreeCellRenderer renderer) {
				super.setCellRenderer(NavigationTreeStyle.wrap(renderer));
			}
		};
		this.tree.setCellRenderer(new DefaultTreeCellRenderer());
		this.tree.collapsePath(this.path(this.section));
		this.scroll = new JScrollPane(this.tree);
		this.scroll.setSize(300, 400);
		this.scroll.doLayout();
		this.scroll.getViewport().doLayout();
	}

	private TreePath path(DefaultMutableTreeNode node) {
		return new TreePath(node.getPath());
	}

	// a left click at x (from the row's left, or from its right if negative), pressed and released as the mouse does
	private void click(DefaultMutableTreeNode node, int x) {
		Rectangle bounds = this.tree.getPathBounds(this.path(node));
		int at = x >= 0 ? bounds.x + x : bounds.x + bounds.width + x;
		int y = bounds.y + bounds.height / 2;
		long now = System.currentTimeMillis();
		for (int id : new int[] { MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED, MouseEvent.MOUSE_CLICKED }) {
			this.tree.dispatchEvent(new MouseEvent(this.tree, id, now, InputEvent.BUTTON1_DOWN_MASK, at, y, 1, false, MouseEvent.BUTTON1));
		}
	}

	@Test
	public void clickingASectionOpensItAndClickingItAgainFoldsIt() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to lay out a tree");
		SwingUtilities.invokeAndWait(() -> {
			this.build();
			this.click(this.section, 20);
			assertTrue(this.tree.isPathSelected(this.path(this.section)), "chosen");
			assertTrue(this.tree.isExpanded(this.path(this.section)), "and its subsections dropped down");

			this.click(this.section, 20);
			assertTrue(this.tree.isPathSelected(this.path(this.section)), "still chosen");
			assertFalse(this.tree.isExpanded(this.path(this.section)), "folded away");
		});
	}

	@Test
	public void theChevronFoldsWithoutChoosing() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to lay out a tree");
		SwingUtilities.invokeAndWait(() -> {
			this.build();
			this.click(this.leaf, 20);
			this.click(this.section, -6); // its chevron, at the row's right end
			assertTrue(this.tree.isExpanded(this.path(this.section)));
			assertTrue(this.tree.isPathSelected(this.path(this.leaf)), "the choice stays");
		});
	}

	@Test
	public void theModelStaysOpen() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to lay out a tree");
		SwingUtilities.invokeAndWait(() -> {
			this.build();
			this.click(this.model, 20);
			this.click(this.model, 20);
			assertTrue(this.tree.isExpanded(this.path(this.model)), "its sections always show");
			assertFalse(NavigationTreeStyle.folds(this.tree, this.path(this.model)));
			assertTrue(NavigationTreeStyle.folds(this.tree, this.path(this.section)));
			assertFalse(NavigationTreeStyle.folds(this.tree, this.path(this.leaf)));
		});
	}

	@Test
	public void everyRowSpansTheColumnWithoutAnIndent() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to lay out a tree");
		SwingUtilities.invokeAndWait(() -> {
			this.build();
			this.tree.expandPath(this.path(this.section));
		});
		SwingUtilities.invokeAndWait(() -> { // once the column's resize has been handled, as it is on screen
			int width = this.scroll.getViewport().getExtentSize().width;
			for (int row = 0; row < this.tree.getRowCount(); row++) {
				Rectangle bounds = this.tree.getRowBounds(row);
				assertEquals(0, bounds.x, "row " + row + " isn't indented");
				assertEquals(width - 4, bounds.width, "row " + row + " spans the column");
			}
			int model = this.tree.getRowBounds(0).height;
			int section = this.tree.getRowBounds(1).height;
			int part = this.tree.getRowBounds(2).height;
			assertTrue(model > section && section > part, "deeper rows are shorter: " + model + ", " + section + ", " + part);
		});
	}

	@Test
	public void levelsTakeTheirType() {
		Font base = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
		assertTrue(NavigationTreeStyle.fontFor(base, 0).isBold());
		assertEquals(15f, NavigationTreeStyle.fontFor(base, 0).getSize2D());
		assertTrue(NavigationTreeStyle.fontFor(base, 1).isBold());
		assertTrue(NavigationTreeStyle.fontFor(base, 2).isPlain());
		assertTrue(NavigationTreeStyle.fontFor(base, 3).isItalic());
		assertTrue(NavigationTreeStyle.paddingFor(1) > NavigationTreeStyle.paddingFor(2));
		assertEquals(0, NavigationTreeStyle.insetFor(0));
		assertEquals(0, NavigationTreeStyle.insetFor(1), "the model's sections span the column");
		assertTrue(NavigationTreeStyle.insetFor(2) > 0, "their rows start a little in");
		assertTrue(NavigationTreeStyle.insetFor(3) > NavigationTreeStyle.insetFor(2), "and theirs a little further");
		assertTrue(NavigationTreeStyle.insetFor(2) <= 8, "just enough to see");
	}

	@Test
	public void shadesMoveTowardsContrast() {
		assertTrue(NavigationTreeStyle.shade(Color.BLACK, 0.1).getRed() > 0, "lighter on dark");
		assertTrue(NavigationTreeStyle.shade(Color.WHITE, 0.1).getRed() < 255, "darker on light");
	}

	@Test
	public void aRowsOwnButtonDoesItsThingAndOnlyThat() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to lay out a tree");
		SwingUtilities.invokeAndWait(() -> {
			this.build();
			java.util.List<TreePath> performed = new java.util.ArrayList<>();
			NavigationTreeStyle.setRowAction(this.tree, new NavigationTreeStyle.RowAction(node -> node == this.section, "Add", (path, bounds) -> performed.add(path)));
			this.click(this.leaf, 20);
			// on the section's "+", just left of its chevron
			this.click(this.section, -(NavigationTreeStyle.CHEVRON_WIDTH + NavigationTreeStyle.ACTION_WIDTH / 2));
			assertEquals(java.util.List.of(this.path(this.section)), performed, "the action ran, for its row");
			assertTrue(this.tree.isPathSelected(this.path(this.leaf)), "the choice stays");
			assertFalse(this.tree.isExpanded(this.path(this.section)), "and the section isn't unfolded");

			this.click(this.leaf, -(NavigationTreeStyle.CHEVRON_WIDTH + NavigationTreeStyle.ACTION_WIDTH / 2));
			assertEquals(1, performed.size(), "a row without the action: no action");
			assertNull(NavigationTreeStyle.rowAction(this.tree, this.leaf));
		});
	}
}
