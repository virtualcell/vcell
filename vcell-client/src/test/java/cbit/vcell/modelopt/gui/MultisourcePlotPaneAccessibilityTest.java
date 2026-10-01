package cbit.vcell.modelopt.gui;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.modelopt.DataSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.ColorUtil;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("Fast")
public class MultisourcePlotPaneAccessibilityTest {

	@Test
	public void selectedAndUnselectedRowsShowStyles() throws Exception {
		MultisourcePlotPane pane = new MultisourcePlotPane();
		SwingUtilities.invokeAndWait(() -> {
			pane.setDataSources(new DataSource[]{source("a-src", "a"), source("b-src", "b")});
			pane.getJList1ForTest().setSelectedIndex(0);
		});
		SwingUtilities.invokeAndWait(() -> {
			JList<?> list = pane.getJList1ForTest();
			assertEquals(0, list.getSelectedIndex(), "modelSize=" + list.getModel().getSize());
			JLabel selected = render(list, 0, true);
			Icon selectedIcon = selected.getIcon();
			String selectedTip = selected.getToolTipText();
			JLabel unselected = render(list, 1, false);
			assertNotNull(selectedIcon);
			assertNull(selectedTip);
			assertNotNull(unselected.getIcon());
			assertEquals("Not plotted; style shown for selection.", unselected.getToolTipText());
			assertEquals(ColorUtil.seriesColor(0), pane.getAutoContrastColorsInListOrder()[0]);
		});
	}

	@Test
	public void customColorsRemainAuthoritative() throws Exception {
		MultisourcePlotPane pane = new MultisourcePlotPane();
		DataSource[] sources = new DataSource[]{source("a-src", "a")};
		SwingUtilities.invokeAndWait(() -> pane.setDataSources(sources));
		SwingUtilities.invokeAndWait(() -> pane.setDataSources(sources, new Color[]{Color.red}));
		assertEquals(Color.red, pane.getAutoContrastColorsInListOrder()[0]);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static JLabel render(JList list, int index, boolean selected) {
		Component component = list.getCellRenderer().getListCellRendererComponent(
				list, list.getModel().getElementAt(index), index, selected, false);
		return (JLabel) component;
	}

	private static DataSource source(String name, String column) {
		RowColumnResultSet rows = new RowColumnResultSet(new String[]{"t", column});
		rows.addRow(new double[]{0, 1});
		rows.addRow(new double[]{1, 2});
		return new DataSource.DataSourceRowColumnResultSet(name, rows, false);
	}
}
