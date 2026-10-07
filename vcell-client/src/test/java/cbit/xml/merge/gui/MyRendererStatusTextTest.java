package cbit.xml.merge.gui;

import cbit.xml.merge.NodeInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The merge tree says a node's status as a word on the label, in an ink that reads on the
 * papers the tree paints. A selected row keeps the look-and-feel selection foreground.
 */
@Tag("Fast")
public class MyRendererStatusTextTest {

	// The papers a row can sit on: the white tree background, the #e8edff striping and the
	// #FDFCDC hover.
	private static final Color[] PAPERS = {
			Color.white, new Color(0xe8, 0xed, 0xff), new Color(0xFD, 0xFC, 0xDC) };

	@BeforeAll
	static void headless() {
		System.setProperty("java.awt.headless", "true");
	}

	@Test
	public void everyStatusShowsItsWordInAReadableInk() {
		assertStatus(NodeInfo.STATUS_NEW, "new", true);
		assertStatus(NodeInfo.STATUS_NEW, "new", false);
		assertStatus(NodeInfo.STATUS_REMOVED, "removed", true);
		assertStatus(NodeInfo.STATUS_REMOVED, "removed", false);
		assertStatus(NodeInfo.STATUS_CHANGED, "changed", true);
		assertStatus(NodeInfo.STATUS_CHANGED, "changed", false);
	}

	private void assertStatus(int status, String word, boolean attribute) {
		JLabel label = render(status, attribute, false);
		String kind = (attribute ? "attribute" : "element") + " status " + status;
		assertTrue(label.getText().startsWith(word + ":"), kind + " label was '" + label.getText() + "'");
		assertTrue(label.getText().contains("species"), kind + " label lost the node name");
		assertNotNull(label.getIcon(), kind + " lost its icon");
		assertNotNull(label.getToolTipText(), kind + " lost its tooltip");
		for (Color paper : PAPERS) {
			double ratio = contrast(label.getForeground(), paper);
			assertTrue(ratio >= 4.5, kind + " ink on " + hex(paper) + " was " + ratio + ":1");
		}
	}

	@Test
	public void normalAndProblemNodesKeepThePlainLabel() {
		for (int status : new int[] { NodeInfo.STATUS_NORMAL, NodeInfo.STATUS_PROBLEM }) {
			assertNull(MyRenderer.statusWord(status));
			assertNull(MyRenderer.statusInk(status));
			JLabel label = render(status, false, false);
			assertEquals("species", label.getText());
			assertEquals(new DefaultTreeCellRenderer().getTextNonSelectionColor(), label.getForeground());
		}
	}

	@Test
	public void selectedRowKeepsTheLookAndFeelSelectionForeground() {
		Color selectionForeground = new DefaultTreeCellRenderer().getTextSelectionColor();
		assertNotNull(selectionForeground);
		for (int status : new int[] { NodeInfo.STATUS_NEW, NodeInfo.STATUS_REMOVED, NodeInfo.STATUS_CHANGED }) {
			JLabel label = render(status, false, true);
			assertEquals(selectionForeground, label.getForeground(),
					"selected status " + status + " overpainted the selection foreground");
			assertTrue(label.getText().startsWith(MyRenderer.statusWord(status) + ":"),
					"selected status " + status + " lost its word");
		}
	}

	private static JLabel render(int status, boolean attribute, boolean selected) {
		NodeInfo node = new NodeInfo("species", "42", status, attribute);
		return (JLabel) new MyRenderer().getTreeCellRendererComponent(
				new JTree(), node, selected, false, true, 0, false);
	}

	private static String hex(Color c) {
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}

	/** WCAG 2.x contrast ratio, 1..21. */
	static double contrast(Color a, Color b) {
		double la = luminance(a), lb = luminance(b);
		return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
	}

	private static double luminance(Color c) {
		return 0.2126 * linear(c.getRed()) + 0.7152 * linear(c.getGreen()) + 0.0722 * linear(c.getBlue());
	}

	private static double linear(int channel) {
		double s = channel / 255.0;
		return s <= 0.04045 ? s / 12.92 : Math.pow((s + 0.055) / 1.055, 2.4);
	}
}
