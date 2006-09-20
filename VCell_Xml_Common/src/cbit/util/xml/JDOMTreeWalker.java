package cbit.util.xml;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.filter.ElementFilter;
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

public class JDOMTreeWalker implements Iterator {
    
	private Stack _stack = new Stack();

	private Object _next;

	private Filter _filter;


    /**
     * In order to keep track of the position in a list of child elements,
     * we use a stack of iterators. Each time we descend to the next level,
     * the list of child elements is pushed on to the stack and we iterate
     * on that list. When the list is exhausted, we pop up and resume iteration
     * from where we left.
     */
	private class Stack extends LinkedList {
		Iterator iter;

		public void push(List list) {
			add(0, iter = list.iterator());
		}

		private void pop() {
			if (size() > 0) {
				this.remove(0);
			}
			iter = size() > 0 ? (Iterator) get(0) : null;
		}

		public Object getNext() {
			if (iter == null) {
				return null;
			}

			while (true) {
				while (iter.hasNext()) {
					Object node = iter.next();
					if (node instanceof Element) {
						List list = ((Element) node).getContent();
						if (list.size() > 0) {
							push(((Element)node).getContent());
						}
					}
					if (_filter == null || _filter.matches(node)) {
						return node;
					}
				}
				pop();
				if (iter == null){
					return null;
				}
			}
		}
		
		/*public Object getNext() {
			if (iter == null) {
				return null;
			}

			while (iter.hasNext()) {
				Object node = iter.next();
				if (node instanceof Element) {
					List list = ((Element) node).getContent();
					if (list.size() > 0) {
						push(((Element)node).getContent());
					}
				}
				if (_filter == null || _filter.matches(node)) {
					//return node;
				}
			}
			pop();
			return getNext();
		}*/
        

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
		_stack.push(document.getContent());
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
		_stack.push(element.getContent());
		_next = _stack.getNext();
		
	}


	private void filterTree(Element root, Filter filter) {

		ArrayList list = new ArrayList(root.getContent(filter));
		for (int i = 0; i < list.size(); i++) {
			Element temp = (Element)list.get(i);
			filterTree(temp, filter);
		}
	}


	public ArrayList getAllMatchingElements(String attName, String attValue) {

		ArrayList list = new ArrayList();
		Element temp;
		while (hasNext()) {
			temp = (Element)next();
			if (temp.getAttributeValue(attName).equals(attValue))
				list.add(temp);
		}

		return list;
	}


	public ArrayList getAllMatchingElements(String attName, org.jdom.Namespace ns, String attValue) {

		ArrayList list = new ArrayList();
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


	public Object next() {
		if (_next == null) {
			throw new NoSuchElementException();
		}

		Object object = _next;
		_next = _stack.getNext();

		return object;
	}


	public void remove() {
        throw new UnsupportedOperationException("Removal is not supported!");
	}
}