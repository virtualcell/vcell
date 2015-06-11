package org.vcell.vis.vismesh;

public class VisLine implements ChomboVisMembraneIndex {
	
	private final int p1;
	private final int p2;
	private final int chomboIndex;
	
	public VisLine(int p1, int p2, int chomboIndex) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.chomboIndex = chomboIndex;
	}

	public int getP1() {
		return p1;
	}

	public int getP2() {
		return p2;
	}

	@Override
	public int getChomboIndex() {
		return chomboIndex;
	}
	
}
