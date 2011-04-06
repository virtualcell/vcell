package org.vcell.sybil.util.category;

/*   CatOneParan  --- by Oliver Ruebenacker, UCHC --- October 2008 to November 2009
 *   A category with one parameter
 */

public abstract class CategoryOneParam<C extends Category<C>, P1> extends CategoryNoParams<C> {
	
	protected P1 p1;
	
	public CategoryOneParam(Class<?> c, P1 p1) { super(c); this.p1 = p1; }
	
	public P1 p1() { return p1; }
	
	@Override
	public int hashCode() { 
		int p1HashCode = p1 != null ? p1.hashCode() : 0;
		return super.hashCode() + p1HashCode; 
	}
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof CategoryOneParam<?, ?>) {
			Class<?> c2 = ((CategoryOneParam<?, ?>) o).c();
			boolean cEqualsC2 = c.equals(c2);
			Object p12 = ((CategoryOneParam<?, ?>) o).p1();
			boolean p1EqualsP12 = (p1 == null && p12 == null) || (p1 != null && p1.equals(p12));
			return cEqualsC2 && p1EqualsP12;
		}
		return false;
	}
	
	@Override
	public String toString() { 
		String cString = super.toString();
		String p1String = p1 != null ? p1.toString() : "null";
		return "(" + cString + ", " + p1String + ")"; 
	}

}
