/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.xml;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;

/**
This is a freely available utility class contributed from the JDOM mailing list. 
I added the two getMatchingElement() methods to it.
If given a Document, the class will iterate starting with the root element of the document. If
given an element, it will ignore the root element. This can be easily fixed in the code
(author provided):
	public JDOMTreeWalker(Element element, Filter filter) {
		this._filter = filter;
		//_stack.push(element.getContent());
		List  list = new LinkedList();
		list.add(element);
		_stack.push(list)
		_next = _stack.getNext();
	}

 * This class provides an Iterator implementation that performs a 
 * pre-order, depth-first traversal of a JDOM Document or a JDOM subtree.
 * <p>
 * An optional {@link Filter} may be supplied with the constructor. 
 * Every node is visited, but only those nodes accepted by the filter 
 * will be returned.
 * <p>
 * Removing nodes is <strong>not</strong> supported.
 *
 * @author pernorrman@telia.com
 */

public class JDOMTreeWalker implements Iterator<Element> {
    
	private Stack _stack = new Stack();

	private Element _next;

	private Filter _filter;


    /**
     * In order to keep track of the position in a list of child elements,
     * we use a stack of iterators. Each time we descend to the next level,
     * the list of child elements is pushed on to the stack and we iterate
     * on that list. When the list is exhausted, we pop up and resume iteration
     * from where we left.
     */
	@SuppressWarnings("serial")
	private class Stack extends LinkedList<Iterator<Element>> {
		Iterator<Element> iter;

		public void push(List<Element> list) {
			add(0, iter = list.iterator());
		}

		private void pop0() {
			if (size() > 0) {
				this.remove(0);
			}
			iter = size() > 0 ? (Iterator<Element>) get(0) : null;
		}

		public Element getNext() {
			if (iter == null) {
				return null;
			}

			while (true) {
				while (iter.hasNext()) {
					Element node = iter.next();
					if (node instanceof Element) {
						@SuppressWarnings("unchecked")
						List<Element> list = ((Element) node).getContent();
						if (list.size() > 0) {
							push(list);
						}
					}
					if (_filter == null || _filter.matches(node)) {
						return node;
					}
				}
				pop0();
				if (iter == null){
					return null;
				}
			}
		}
	} /* class Stack */

	/**
	 * Create an ElementWalker for a JDOM Document with an empty filter, i.e.
	 * all elements will be returned during iteration.
	 */
	public JDOMTreeWalker(Document document) {
		this(document, null);
	}


	/**
	 * Create an ElementWalker for a JDOM Document with the
	 * specified filter.
	 */
	public JDOMTreeWalker(Document document, Filter filter) {
		this._filter = filter;
		@SuppressWarnings("unchecked")
		List<Element> list = document.getContent();
		_stack.push(list);
		_next = _stack.getNext();
	}


	/**
	 * Create an ElementWalker for a JDOM Element with an empty filter, i.e.
	 * every node will be returned.
	 */
	public JDOMTreeWalker(Element element) {
		this(element, null);
	}


	/**
	 * Create an ElementWalker for a JDOM Element with the
	 * specified filter.
	 */
	public JDOMTreeWalker(Element element, Filter filter) {
		//if (filter instanceof ElementFilter)
			//filterTree(element, filter);
		this._filter = filter;
		@SuppressWarnings("unchecked")
		List<Element> list = element.getContent();
		_stack.push(list);
		_next = _stack.getNext();
		
	}


	private void filterTree(Element root, Filter filter) {

		@SuppressWarnings("unchecked")
		ArrayList<Element> list = new ArrayList<Element>(root.getContent(filter));
		for (int i = 0; i < list.size(); i++) {
			Element temp = (Element)list.get(i);
			filterTree(temp, filter);
		}
	}


	public ArrayList<Element> getAllMatchingElements(String attName, String attValue) {

		ArrayList<Element> list = new ArrayList<Element>();
		Element temp;
		while (hasNext()) {
			temp = (Element)next();
			if (temp.getAttributeValue(attName).equals(attValue))
				list.add(temp);
		}

		return list;
	}


	public ArrayList<Element> getAllMatchingElements(String attName, org.jdom.Namespace ns, String attValue) {

		ArrayList<Element> list = new ArrayList<Element>();
		Element temp;
		while (hasNext()) {
			temp = (Element)next();
			String value = temp.getAttributeValue(attName, ns);
			if (value != null && value.equals(attValue))
				list.add(temp);
		}

		return list;
	}


	public Element getMatchingElement(String attName, String attValue) {

		Element temp;
		while (hasNext()) {
			temp = (Element)next();
			if (temp.getAttributeValue(attName).equals(attValue))
				return temp;
		}

		return null;
	}


	public Element getMatchingElement(String attName, org.jdom.Namespace ns, String attValue) {

		Element temp;
		String value = null;
		while (hasNext()) {
			temp = (Element)next();
			value = temp.getAttributeValue(attName, ns);
			if (value != null && value.equals(attValue))
				return temp;
		}

		return null;
	}


	//
	// Iterator implementation
	//

	public boolean hasNext() {
		return _next != null;
	}


	public Element next() {
		if (_next == null) {
			throw new NoSuchElementException();
		}

		Element object = _next;
		_next = _stack.getNext();

		return object;
	}


	public void remove() {
        throw new UnsupportedOperationException("Removal is not supported!");
	}
}
