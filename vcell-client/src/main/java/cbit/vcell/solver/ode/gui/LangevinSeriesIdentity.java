package cbit.vcell.solver.ode.gui;

import org.vcell.util.ColorUtil;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stable series identity for Langevin plots.
 * {@link ColorUtil#seriesDash(int)} receives the full assignment index.
 * Palette-slot division is not the identity.
 */
final class LangevinSeriesIdentity {
	private int nextIndex;
	private final Map<String, Integer> indexByName = new LinkedHashMap<>();

	void reset() {
		nextIndex = 0;
		indexByName.clear();
	}

	void assignIfAbsent(String name, Map<String, Color> colors) {
		if (indexByName.containsKey(name)) {
			return;
		}
		// SD is an envelope of ACS. It keeps deriveEnvelopeColor and does not take a series index.
		if ("SD".equals(name) && colors.containsKey(name)) {
			return;
		}
		int seriesIndex = nextIndex;
		nextIndex++;
		indexByName.put(name, seriesIndex);
		colors.putIfAbsent(name, ColorUtil.seriesColor(seriesIndex));
	}

	boolean hasIndex(String name) {
		return indexByName.containsKey(name);
	}

	int index(String name) {
		Integer seriesIndex = indexByName.get(name);
		return seriesIndex == null ? 0 : seriesIndex;
	}
}
