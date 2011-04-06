package org.vcell.sybil.util.category;

/*   CatTwoParams  --- by Oliver Ruebenacker, UCHC --- October 2008 to November 2009
 *   A category with two parameters
 */

public abstract class CategoryTwoParams<C extends Category<C>, P1, P2> extends CategoryOneParam<C, P1> {
	
	protected P2 p2;
	
	public CategoryTwoParams(Class<?> c, P1 p1, P2 p2) { super(c, p1); this.p2 = p2; }
	
	public P2 p2() { return p2; }
	
	@Override
	public int hashCode() { 
		int p2HashCode = p2 != null ? p2.hashCode() : 0;
		return super.hashCode() + p2HashCode; 
	}
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof CategoryTwoParams<?, ?, ?>) {
			Object c2 = ((CategoryTwoParams<?, ?, ?>) o).c();
			boolean aEqualsA2 = (c == null && c2 == null) || (c != null && c.equals(c2));
			Object p12 = ((CategoryTwoParams<?, ?, ?>) o).p1();
			boolean p1EqualsP12 = (p1 == null && p12 == null) || (p1 != null && p1.equals(p12));
			Object p22 = ((CategoryTwoParams<?, ?, ?>) o).p2();
			boolean p2EqualsP22 = (p2 == null && p22 == null) || (p2 != null && p2.equals(p22));
			return aEqualsA2 && p1EqualsP12 && p2EqualsP22;
		}
		return false;
	}
	
	@Override
	public String toString() { 
		String cString = c.toString();
		String p1String = p1 != null ? p1.toString() : "null";
		String p2String = p2 != null ? p2.toString() : "null";
		return "(" + cString + ", " + p1String + ", " + p2String + ")"; 
	}

}
