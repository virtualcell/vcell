package cbit.vcell.vcml;

import java.util.Comparator;

import org.jdom.Element;
/**
 Utility class
 * Creation date: (7/25/2003 3:38:36 PM)
 * @author: Rashad Badrawi
 */
 
	public class ElementComparator implements Comparator {

		private String attName;
		
			public ElementComparator(String attName) {

			this.attName = attName;
		}
		public int compare(Object o1, Object o2) {

			String val1 = ((Element)o1).getAttributeValue(attName);
			String val2 = ((Element)o2).getAttributeValue(attName);

			if (val1 == null || val2 == null)
				return 0;
				
			return val1.compareTo(val2);
		}
	/**
	 * 
	 * Indicates whether some other object is &quot;equal to&quot; this
	 * Comparator.  This method must obey the general contract of
	 * <tt>Object.equals(Object)</tt>.  Additionally, this method can return
	 * <tt>true</tt> <i>only</i> if the specified Object is also a comparator
	 * and it imposes the same ordering as this comparator.  Thus,
	 * <code>comp1.equals(comp2)</code> implies that <tt>sgn(comp1.compare(o1,
	 * o2))==sgn(comp2.compare(o1, o2))</tt> for every object reference
	 * <tt>o1</tt> and <tt>o2</tt>.<p>
	 *
	 * Note that it is <i>always</i> safe <i>not</i> to override
	 * <tt>Object.equals(Object)</tt>.  However, overriding this method may,
	 * in some cases, improve performance by allowing programs to determine
	 * that two distinct Comparators impose the same order.
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> only if the specified object is also
	 *		a comparator and it imposes the same ordering as this
	 *		comparator.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see java.lang.Object#hashCode()
	 */
public boolean equals(Object obj) {
	return false;
}
		public boolean equals(Object o1, Object o2) {

			return ((Element)o1).getAttributeValue(attName).
					equals(((Element)o2).getAttributeValue(attName));
		}
}
