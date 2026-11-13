package org.vcell.util.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class ButtonRowLayoutTest {

	private static JButton button(int width) {
		JButton button = new JButton("b");
		button.setPreferredSize(new Dimension(width, 26));
		button.setMinimumSize(new Dimension(width, 26));
		return button;
	}

	private static JPanel row() {
		return ButtonRowLayout.withSearch(List.of(button(150), button(200), button(100)), new JTextField(10));
	}

	@Test
	public void itsLeastWidthIsItsWidestItemNotTheirSum() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JPanel row = ButtonRowLayoutTest.row();
			int widestPlusBorder = 200 + 8;
			assertEquals(widestPlusBorder, row.getMinimumSize().width, "a narrow window can be narrow");
		});
	}

	@Test
	public void wideItsOneLineNarrowItWraps() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JPanel row = ButtonRowLayoutTest.row();
			ButtonRowLayout layout = (ButtonRowLayout) row.getLayout();
			assertEquals(1, layout.lines(row, 2000).size(), "everything fits");
			assertEquals(2, layout.lines(row, 220 + 8 + 150 + 8 + 200 + 8 + 50).size(), "the search leads; the last button wraps");
			assertEquals(4, layout.lines(row, 230).size(), "the search, then each button alone");
		});
	}

	@Test
	public void theSearchFillsWhatsLeftOfItsLine() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JPanel row = ButtonRowLayoutTest.row();
			row.setSize(1000, 40);
			row.doLayout();
			java.awt.Component search = row.getComponent(0);
			assertEquals("SearchGroup", search.getName(), "the search comes first");
			java.awt.Component lastButton = row.getComponent(3);
			assertEquals(1000 - 4, lastButton.getX() + lastButton.getWidth(), "the search takes the spare width: the buttons end at the right edge");
			row.setSize(300, 200);
			row.doLayout();
			assertTrue(row.getComponent(1).getY() > row.getComponent(0).getY(), "narrow: the buttons wrap under the search");
		});
	}
}
