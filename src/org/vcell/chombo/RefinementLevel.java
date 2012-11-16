package org.vcell.chombo;

import java.io.Serializable;
import java.util.ArrayList;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class RefinementLevel implements Serializable, Matchable {
	private int refineRatio = 2;

	private ArrayList<ChomboBox> boxList = new ArrayList<ChomboBox>();
	
	public RefinementLevel() {
	}
	
	public RefinementLevel(int ratio) {
		refineRatio  = ratio;
	}
	
	public RefinementLevel(CommentStringTokenizer tokens) throws DataAccessException {
		readVCML(tokens);
	}
	
	private void addBox(ChomboBox b) {
		boxList.add(b);		
	}
	
	private void deleteBox(int index) {
		if (index >= boxList.size()) {
			throw new ArrayIndexOutOfBoundsException("RefinementLevel::set(int, Box) : " + index + " " + boxList.size());
		}
		boxList.remove(index);
	}

	public int getRefineRatio() {
		return refineRatio;
	}
	
	public void setRefineRatio(int refineRatio) {
		this.refineRatio = refineRatio;
	}

	private int getNumBoxes() {
		return boxList.size();
	}
	
	private ChomboBox getBox(int index) {
		if (index >= boxList.size()) {
			throw new ArrayIndexOutOfBoundsException("RefinementLevel::set(int, Box) : " + index + " " + boxList.size());
		}
		return boxList.get(index);
	}
	
	private void set(int index, ChomboBox b) {
		if (index >= boxList.size()) {
			throw new ArrayIndexOutOfBoundsException("RefinementLevel::set(int, Box) : " + index + " " + boxList.size());
		} 

		boxList.set(index, b);
	}
	
	public String getVCML() {
	//      RefinementLevel {
		//      RefineRatio 2
		//      ChomboBox 1 2 3 4
		//      ChomboBox 1 2 3 4	
	//      }
		StringBuilder buffer = new StringBuilder();

		buffer.append(VCML.RefinementLevel + " " + VCML.BeginBlock + "\n");
		buffer.append(VCML.RefineRatio + " " + refineRatio + "\n");
		for (ChomboBox box : boxList) 
		{
			buffer.append(box.getVCML());
		}

		buffer.append(VCML.EndBlock+"\n");
			
		return buffer.toString();
	}
	
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		//      RefinementLevel {
		//      RefineRatio 2
		//      NumOfBoxes 2
		//      ChomboBox 1 2 3 4
		//      ChomboBox 1 2 3 4	
		//      }
		try {
			String token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.RefinementLevel)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new DataAccessException(
						"unexpected token " + token + " expecting " + VCML.BeginBlock); 
				}
			}
			
			while (tokens.hasMoreTokens()) {
				token = tokens.nextToken();
				if (token.equalsIgnoreCase(VCML.EndBlock)) {
					break;
				}
				if (token.equalsIgnoreCase(VCML.RefineRatio)) {
					token = tokens.nextToken();
					refineRatio = Integer.parseInt(token);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.ChomboBox)) {
					ChomboBox cb = new ChomboBox(tokens);
					boxList.add(cb);
					continue;
				}
				throw new DataAccessException("unexpected identifier " + token);
			}
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			throw new DataAccessException("line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
		}
	}

	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return (true);
		}
		if (!(object instanceof RefinementLevel)) {
			return false;
		}
		RefinementLevel rl = (RefinementLevel) object;
		if (rl.refineRatio != refineRatio) {
			return false;
		}
		if (rl.getNumBoxes() != getNumBoxes()) {
			return false;
		}			
		for (int boxIndex = 0; boxIndex < boxList.size(); boxIndex ++) {
			if (!boxList.get(boxIndex).compareEqual(rl.boxList.get(boxIndex))) {
				return false;
			}
		}
		return true;
	}
}
