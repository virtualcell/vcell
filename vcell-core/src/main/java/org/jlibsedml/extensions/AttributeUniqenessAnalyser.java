package org.jlibsedml.extensions;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class AttributeUniqenessAnalyser {
	
	/**
	 * Attempts to find an attribute name/value pair that will uniquely identify 
	 * an element amongst its siblings. If there is more than 1 such attribute found,
	 * preference will be given to an attribue with name 'id' ( case insensitive)  else the first suitable attribute will be returned.
	 * @param el 
	 * @return An {@link AttDataObj}, or  <code>null</code> if no such attribute could
	 *  be found.
	 */
	public AttDataObj getUniqueAttributeForElement(Element el) {
		List<Element> sibs  = getSiblings(el);
		Iterator it = sibs.iterator();
		Map<AttDataObj, Integer> map = new TreeMap<AttDataObj, Integer>();
		while (it.hasNext()){
			Element sib = (Element)it.next();
			List<Attribute> atts =sib.getAttributes();
			for (Attribute att:atts){
				// only consider atts that our element has
				if(el.getAttribute(att.getName(),att.getNamespace())== null){
					continue;
				}
				AttDataObj key = new AttDataObj();
				key.name=att.getName();
				key.ns=att.getNamespace();
				key.val=att.getValue();
				key.el =sib;
				key.att=att;
				if(map.containsKey(key)){
					int incremented = map.get(key);
					incremented++;
					map.put(key, incremented);
				}else {
					map.put(key, 1);
				}
			}
		}
		int currMin = Integer.MAX_VALUE;
		AttDataObj currAtt = null;
		for (AttDataObj key: map.keySet()){
			if (map.get(key) < currMin && key.el==el){
				currAtt = key;
				currMin = map.get(key);
			}else if (map.get(key) == 1 && key.el==el){
				if(!currAtt.name.equalsIgnoreCase("id")){
					currAtt = key;
					currMin = map.get(key);
				}
			}
		}
		
		
		if (currAtt!=null && currMin ==1){
			return currAtt;
		}else {
			return null;
		}
			
	}
	
	

	/**
	 * Gets the index of an element amongst its siblings. This method is namespace aware and 
	 * thus the index will be for elements with the same element name and namespace.
	 * If this element cannot be found, the method returns -1. 
	 * @param el A non-null JDom Element that is part of a Document (i.e., not standalone ).
	 * @return A 1-based index, for insertion into an XPAth statement.
	 */
	public int getIndexForElementAmongstSiblings (Element el){
		List<Element> sibs = getSiblings ( el);
		if(sibs.size() == 1 && el==sibs.get(0)){
			return -1;
		}
		int indx = 0;
		for (int i = 0; i < sibs.size();i++){
			Element sib = sibs.get(i);
			if(sib.getNamespace()!=null &&sib.getNamespace().equals(el.getNamespace()) &&
					sib.getName().equals(el.getName())){
				indx++;
			}
			if(sib == el){
				return indx;
			}
		}
		return -1;
	}
	// gets sibling elements, regardless of namespace.
	List<Element> getSiblings (Element element) {
		if(element.getParentElement()!=null){
			Element parent = element.getParentElement();
			List siblings = parent.getContent(new ElementFilter());
			return siblings;
		}else {
			return Collections.EMPTY_LIST;
		}
		
	}
}
