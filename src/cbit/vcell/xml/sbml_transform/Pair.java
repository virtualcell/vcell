/**
 * 
 */
package cbit.vcell.xml.sbml_transform;


public class Pair <One, Two> {
	public final One one;
	public final Two two;
	
	public Pair(One one, Two two) {
		this.one = one;
		this.two = two;
	}
	
	public String toString() {
		return "<" + (one == null ? "null" : one.toString()) + ", " + 
				(two == null ? "null" : two.toString()) + ">";
	}
	
	public int hashCode() {
		int h = 13;
		h += h *37 + (one == null ? 0 : one.hashCode());
		h += h *37 + (two == null ? 0 : two.hashCode());
		return h;
	}
	
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( null == o ) return false;
		if( ! (o instanceof Pair) ) return false;
		return equals( (Pair)o );
	}
	
	public boolean equals(Pair <One, Two> o) {
		return this.one.equals(o.one) && this.two.equals(o.two);
	}

}