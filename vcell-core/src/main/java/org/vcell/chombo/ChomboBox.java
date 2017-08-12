package org.vcell.chombo;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class ChomboBox implements Serializable, Matchable {
	private ISize boxLo;
	private ISize boxHi;
	
	public ChomboBox(ISize lo, ISize hi) {
		super();
		this.boxLo = lo;
		this.boxHi = hi;
	}
	
	public ChomboBox(CommentStringTokenizer tokens) throws DataAccessException {
		super();
		readVCML(tokens);
	}
	
	public ISize getLo() {
		return boxLo;
	}
	public ISize getHi() {
		return boxHi;
	}
	
	public static ChomboBox fromString(String str) {
		StringTokenizer st = new StringTokenizer(str);
		if (st.countTokens() != 4 && st.countTokens() != 6) {
			throw new RuntimeException("ChombBox::fromString(), wrong format");
		}
		
		if (st.countTokens() == 4) { // 2d
			ISize lo = new ISize(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 1);
			ISize hi = new ISize(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 1);
			return new ChomboBox(lo, hi);
		} else { // 3d
			ISize lo = new ISize(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
			ISize hi = new ISize(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
			return new ChomboBox(lo, hi);			
		}
	}
	
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.ChomboBox)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException(
					"unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		boxLo = new ISize(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()), 1);
		boxHi = new ISize(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()), 1);
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof ChomboBox)) {
			return false;
		}
		ChomboBox b = (ChomboBox)obj;
		if (!boxLo.compareEqual(b.boxLo)) {
			return false;
		}
		if (!boxHi.compareEqual(b.boxHi)) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return boxLo.getX() + " " + boxLo.getY() + " " + boxLo.getZ() + " " + boxHi.getX() + " " + boxHi.getY() + " " + boxHi.getZ();
	}
	
	public String getVCML() {
		//  ChomboBox 1 2 3 4
		return VCML.ChomboBox + " " + toString() + "\n";
	}
	
	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return true;
		}
		if (object != null && object instanceof ChomboBox) {
			ChomboBox cb = (ChomboBox) object;
			if (!cb.boxLo.compareEqual(boxLo)) {
				return false;
			}
			if (!cb.boxHi.compareEqual(boxHi)) {
				return false;
			}
		}
		return true;
	}	
}
