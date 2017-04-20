package cbit.vcell.solvers.smoldyn;

import java.util.ArrayList;
import java.util.List;

import org.vcell.util.VCAssert;

/**
 * record Triangular mesh, check for matching neighbor
 * @author GWeatherby
 *
 */
public class TrianglePanel {

	private final int indexes[];
	private int sideNeighbors;
	private int tipNeighbors;
	private List<TrianglePanel> seen = new ArrayList<TrianglePanel>( );

	public TrianglePanel(int a, int b, int c) {
		indexes = new int[3];
		indexes[0] = a;
		indexes[1] = b;
		indexes[2] = c;
	}

	/*
	 * analyze connection between this neighbor and this
	 */
	public void analyzeNeighbor(TrianglePanel other) {
		VCAssert.assertFalse(seen.contains(other),"already analyzed" );
		seen.add(other);
		int matches = 0;
		for (int us = 0; us < 3; us++) {
			for (int them = 0; them < 3; them++) {
				if (indexes[us] == other.indexes[them]) {
					if (++matches == 2) {
						++sideNeighbors;
						return;
					}
				}
			}
		}
		if (matches == 1) {
			++tipNeighbors;
			return;
		}
		throw new RuntimeException("not a neighbor");
	}

	public int getSideNeighbors() {
		return sideNeighbors;
	}

	public int getTipNeighbors() {
		return tipNeighbors;
	}


}
