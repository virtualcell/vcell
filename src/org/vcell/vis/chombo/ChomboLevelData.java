package org.vcell.vis.chombo;

public class ChomboLevelData {
	
	private final int level;
	private final int fractionComponentIndex;
	private final double[] data;
	private final int[] offsets;

	public ChomboLevelData(int level, int fractionComponentIndex, double[] data, long[] offsets){
		this.level = level;
		this.fractionComponentIndex = fractionComponentIndex;
		this.data = data;
		this.offsets = new int[offsets.length];
		for (int i=0;i<offsets.length;i++){
			this.offsets[i] = (int)offsets[i];
		}
	}

	public ChomboLevelData(int level, int fractionComponentIndex, double[] data, int[] offsets){
		this.level = level;
		this.fractionComponentIndex = fractionComponentIndex;
		this.data = data;
		this.offsets = offsets;
	}

	public int getLevel() {
		return level;
	}

	public double[] getData() {
		return data;
	}

	public int[] getOffsets() {
		return offsets;
	}
	
	public int getFractionComponentIndex(){
		return fractionComponentIndex;
	}

	public double getCellFraction(ChomboLevel chomboLevel, int boxNumber, int boxIndex) {
        int boxOffset = getOffsets()[boxNumber];
        int boxSize = chomboLevel.getBoxes().get(boxNumber).getSize();
        double fraction = getData()[boxOffset+(fractionComponentIndex*boxSize) + boxIndex];
        return fraction;
	}
}
