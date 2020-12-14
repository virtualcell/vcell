package org.jlibsedml.extensions;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/*
 * Similar to the org.jdom.Attribute but with equals/hashcode reimplemented
 * to use the data as a test for equlaity, not reference based equality. 
 */
class AttDataObj implements Comparable<AttDataObj> {
	
	String name;
	Namespace ns;
	String val;
	Element el;
	Attribute att;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ns == null) ? 0 : ns.hashCode());
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttDataObj other = (AttDataObj) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ns == null) {
			if (other.ns != null)
				return false;
		} else if (!ns.equals(other.ns))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	public int compareTo(AttDataObj o) {
		int c = o.name.compareTo(name);
		if (c ==0){
			c=o.val.compareTo(val);
		}
		return c;
	}
	
	
	
}
