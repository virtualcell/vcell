package org.vcell.sybil.util.category;

/*   CatThreeParams  --- by Oliver Ruebenacker, UCHC --- October 2008 to November 2009
 *   A category with three parameters
 */

public abstract class CategoryThreeParams<C extends Category<C>, P1, P2, P3> extends CategoryTwoParams<C, P1, P2> {
	
	protected P3 p3;
	
	public CategoryThreeParams(Class<?> c, P1 p1, P2 p2, P3 p3) { super(c, p1, p2); this.p3 = p3; }
	
	public P3 p3() { return p3; }
	
	@Override
	public int hashCode() { 
		int p3HashCode = p3 != null ? p3.hashCode() : 0;
		return super.hashCode() + p3HashCode; 
	}
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof CategoryThreeParams<?, ?, ?, ?>) {
			Object c2 = ((CategoryThreeParams<?, ?, ?, ?>) o).c();
			boolean aEqualsA2 = (c == null && c2 == null) || (c != null && c.equals(c2));
			Object p12 = ((CategoryThreeParams<?, ?, ?, ?>) o).p1();
			boolean p1EqualsP12 = (p1 == null && p12 == null) || (p1 != null && p1.equals(p12));
			Object p22 = ((CategoryThreeParams<?, ?, ?, ?>) o).p2();
			boolean p2EqualsP22 = (p2 == null && p22 == null) || (p2 != null && p2.equals(p22));
			Object p32 = ((CategoryThreeParams<?, ?, ?, ?>) o).p3();
			boolean p3EqualsP32 = (p3 == null && p32 == null) || (p3 != null && p3.equals(p32));
			return aEqualsA2 && p1EqualsP12 && p2EqualsP22 && p3EqualsP32;
		}
		return false;
	}
	
	@Override
	public String toString() { 
		String cString = c.toString();
		String p1String = p1 != null ? p1.toString() : "null";
		String p2String = p2 != null ? p2.toString() : "null";
		String p3String = p3 != null ? p3.toString() : "null";
		return "(" + cString + ", " + p1String + ", " + p2String + ", " + p3String + ")"; 
	}

}
