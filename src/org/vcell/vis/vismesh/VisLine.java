package org.vcell.vis.vismesh;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.vis.mapping.chombo.ChomboVisMembraneIndex;

public class VisLine implements ChomboVisMembraneIndex, Matchable {
	
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

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof VisLine){
			VisLine other = (VisLine) obj;
			if (p1 != other.p1){
				return false;
			}
			if (p2 != other.p2){
				return false;
			}
			if (chomboIndex != other.chomboIndex){
				return false;
			}
			return true;
		}
		return false;
	}

}
