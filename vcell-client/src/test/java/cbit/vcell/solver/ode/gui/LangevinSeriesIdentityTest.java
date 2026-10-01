package cbit.vcell.solver.ode.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.ColorUtil;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class LangevinSeriesIdentityTest {

	@Test
	public void fullIndexIsStableAndSdDoesNotConsumeASlot() {
		LangevinSeriesIdentity identity = new LangevinSeriesIdentity();
		Map<String, Color> colors = new LinkedHashMap<>();
		identity.assignIfAbsent("ACS", colors);
		identity.assignIfAbsent("ACO", colors);
		Color acs = colors.get("ACS");
		colors.put("SD", new Color(acs.getRed(), acs.getGreen(), acs.getBlue(), 60));
		identity.assignIfAbsent("SD", colors);

		assertEquals(ColorUtil.CVD_SAFE_LIGHT[0], colors.get("ACS"));
		assertEquals(ColorUtil.seriesColor(1), colors.get("ACO"));
		assertFalse(identity.hasIndex("SD"));
		assertEquals(60, colors.get("SD").getAlpha());

		for (int i = 0; i < 30; i++) {
			identity.assignIfAbsent("m" + i, colors);
		}
		assertEquals(2, identity.index("m0"));
		assertEquals(8, identity.index("m6"));
		assertEquals(ColorUtil.seriesColor(8), colors.get("m6"));
		assertEquals(26, identity.index("m24"));
		identity.assignIfAbsent("m6", colors);
		assertEquals(8, identity.index("m6"));
	}

	@Test
	public void customColorKeepsItsColorAndReceivesAFullIndex() {
		LangevinSeriesIdentity identity = new LangevinSeriesIdentity();
		Map<String, Color> colors = new LinkedHashMap<>();
		colors.put("custom", Color.RED);
		identity.assignIfAbsent("custom", colors);
		identity.assignIfAbsent("next", colors);

		assertEquals(Color.RED, colors.get("custom"));
		assertEquals(0, identity.index("custom"));
		assertEquals(1, identity.index("next"));
		assertEquals(ColorUtil.seriesColor(1), colors.get("next"));
	}
}
