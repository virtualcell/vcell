package org.vcell.sybil.util.category;

/*   CatNoParams  --- by Oliver Ruebenacker, UCHC --- October 2008 to November 2009
 *   Category with no parameters.
 */

public abstract class CategoryNoParams<C extends Category<C>> extends SimpleCategory<C> implements Category<C> {
	
	protected Class<?> c;
	
	public CategoryNoParams(Class<?> c) { this.c = c; }
	
	@Override
	public Class<?> c() { return c; }
	@Override
	public int hashCode() { return c.hashCode(); }
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof CategoryNoParams<?>) {
			Class<?> c2 = ((CategoryNoParams<?>) o).c();
			return c.equals(c2);
		}
		return false;
	}
	
	@Override
	public String toString() { return "(" +  c.toString() + ")"; }

}
