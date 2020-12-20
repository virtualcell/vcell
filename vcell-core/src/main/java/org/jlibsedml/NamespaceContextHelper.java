package org.jlibsedml;
/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://jaxp.dev.java.net/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jaxp.dev.java.net/CDDLv1.0.html
 * If applicable add the following below this CDDL HEADER
 * with the fields enclosed by brackets "[]" replaced with
 * your own identifying information: Portions Copyright
 * [year] [name of copyright owner]
 */

/*
 * $Id: NamespaceContextHelper.java,v 1.2 2006-03-28 20:54:02 ndw Exp $
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.
 */



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the javax.xml.namespace.NamespaceContext interface.

 * <p>
 * There are four ways to instantiate this class:
 * </p>
 * 
 * <ol>
 * <li>The no-argument constructor produces an initially empty namespace
 * context.</li>
 * <li>Another constructor takes a prefix and URI and produces a namespace
 * context with that binding.</li>
 * <li>A constructor that takes a hash of namespace/uri pairs
 * and produces a namespace context with those initial bindings.</li>
 * <li>A constructor taking an org.jdom.Document and an XPathTarget. This constructor tries
 *  to match XPath prefixes to namespaces  and add them to the Namespace lookup.  </li>
 * </ol>
 * 
 * <p>
 * After the object has been instantiated, you can call the
 * {@link #add(String,String)} method to add additional bindings to the
 * namespace context. A number of rules are  designed to make sure that the context remains coherent:
 * </p>
 * 
 * <ul>
 * <li>Namespace bindings can only be added, not removed.</li>
 * <li>Once a prefix is bound, its binding cannot be changed.</li>
 * <li>The XML restrictions on the 'xml' prefix, the 'xmlns' prefix, and their
 * respective namespace URIs are enforced.</li>
 * <li>Namespace prefixes must be valid NCNames (or "" for the default
 * namespace). Note that unprefixed element and attribute names in an XPath
 * expression can <em>never</em> match a name that's in a namespace. In
 * particular, setting the default namespace won't have that effect.</li>
 * </ul>
 * 
 * <p>
 * Even with these rules, you can't assume that the context is thread safe.
 * Don't allow it to be changed while someone else is reading it.
 * </p>
 * 

 * 
 * @author <a href="mailto:Norman.Walsh@Sun.COM">Norman Walsh</a>
 * @author Richard Adams</a>
 *
 
 */
 public class NamespaceContextHelper implements NamespaceContext {
     Logger log = LoggerFactory.getLogger(NamespaceContextHelper.class);
	private Map<String, String> ns = new HashMap<String, String>();

	/**
	 * Creates a new instance of NamespaceContextHelper.
	 * 
	 * <p>
	 * Creates an empty namespace context.
	 * </p>
	 */
	 NamespaceContextHelper() {
	}

	/**
	 * Creates a new instance of NamespaceContextHelper.
	 * 
	 * <p>
	 * Creates a namespace context with the bindings specified in
	 * <code>initialNamespaces</code>.
	 * </p>
	 */
	 NamespaceContextHelper( Map<String, String> initialNamespaces) {
		for (String key: initialNamespaces.keySet()) {		
			String uri =  initialNamespaces.get(key);
			add(key, uri);
		}
	}

	/**
	 * Creates a new instance of NamespaceContextHelper.
	 * 
	 * <p>
	 * Creates a namespace context with the specified <code>prefix</code> bound
	 * to <code>uri</code>.
	 * </p>
	 */
	 NamespaceContextHelper(String prefix, String uri) {
		add(prefix, uri);
	}

	/**
	 * Adds a new prefix/uri binding to the namespace context.
	 * 
	 * @throws NullPointerException
	 *             if the <code>prefix</code> or <code>uri</code> is
	 *             <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if the caller attempts to change the binding of
	 *             <code>prefix</code>, if the caller attempts to bind the
	 *             prefix "<code>xml</code>" or the namespace "
	 *             <code>http://www.w3.org/XML/1998/namespace</code>"
	 *             incorrectly, if the caller attempts to bind the prefix "
	 *             <code>xmlns</code>" or the namespace "
	 *             <code>http://www.w3.org/2000/xmlns</code>", or if the
	 *             <code>prefix</code> is not a valid NCName.
	 */
	 void add(String prefix, String uri) {
		if (prefix == null || uri == null) {
			throw new NullPointerException(
					"Null prefix or uri passed to NamespaceContextHelper");
		}

		if (ns.containsKey(prefix)) {
			String curURI = (String) ns.get(prefix);
			if (uri.equals(curURI)) {
				return;
			}
			throw new IllegalArgumentException(
					"Attempt to change binding in NamespaceContextHelper");
		}

		if ("xml".equals(prefix)
				&& !"http://www.w3.org/XML/1998/namespace".equals(uri)) {
			throw new IllegalArgumentException(
					"The prefix 'xml' can only be bound to 'http://www.w3.org/XML/1998/namespace' in NamespaceContextHelper");
		}

		if ("http://www.w3.org/XML/1998/namespace".equals(uri)
				&& !"xml".equals(prefix)) {
			throw new IllegalArgumentException(
					"The namespace 'http://www.w3.org/XML/1998/namespace' can only have the prefix 'xml' in NamespaceContextHelper");
		}

		if ("xmlns".equals(prefix)
				|| "http://www.w3.org/2000/xmlns".equals(uri)) {
			throw new IllegalArgumentException(
					"Neither the prefix 'xmlns' nor the URI 'http://www.w3.org/2000/xmlns' can be bound in NamespaceContextHelper");
		}

		if ("".equals(prefix)) {
			ns.put(prefix, uri);
		} else {
			if (prefix.matches("\\w[^ :/]*")) {
				ns.put(prefix, uri);
			} else {
				throw new IllegalArgumentException(
						"Prefix is not a valid NCName in NamespaceContextHelper");
			}
		}
	}

	/** Implements the NamespaceContext getNamespaceURI method. */
	public String getNamespaceURI(String prefix) {
		return ns.get(prefix);
	}
	private Set<Namespace> nss;

	/**
	 * Attempts to match up prefixes in target with URIs in the document. It does this by:
	 * <ul>
	 * <li> If a namespace prefix in the model matches the XPath prefix, then that namespace is used.
	 * <li> A model namespace with no prefix is used if the URI contains the XPath prefix as a subsequence.
	 * </ul>
	 * @param doc A JDOM document
	 */
	public NamespaceContextHelper(org.jdom.Document doc) {
		Iterator it = doc.getDescendants(new ElementFilter());
		nss = new HashSet<Namespace>();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			Namespace ns = el.getNamespace();

			if (ns != null && !nss.contains(ns)) {
				nss.add(ns);
			}
			List<Attribute> atts = el.getAttributes();
			for ( Attribute att: atts){
				if(!att.getNamespace().equals(Namespace.NO_NAMESPACE)){
					nss.add(att.getNamespace());
				}
			}
		}
		for (Namespace ns : nss) {
			log.debug("Namespace {}", ns.getURI());
		}	
		return;
	}
	
	public void process (XPathTarget target) {		
		Set<String> xpathPrefixes = target.getXPathPrefixes();
		for (String prefix : xpathPrefixes) {
			for (Namespace ns : nss) {
				if ( ((ns.getPrefix().toLowerCase().equals(prefix.toLowerCase()) 
						|| "".equals(ns.getPrefix()) 
						    && ns.getURI().toLowerCase().contains(prefix.toLowerCase()))
						    ||
						    "".equals(ns.getPrefix()) 
						    && replace(ns.getURI().toLowerCase()).contains(prefix.toLowerCase()))
						&& getNamespaceURI(prefix) == null) {
					add(prefix, ns.getURI());
				}
			}
		}
		
	}
	
	private String replace(String lowerCase) {
		return lowerCase.replaceAll("/", "_");
	}

	public boolean isAllXPathPrefixesMapped(XPathTarget target){
		Set<String> xpathPrefixes = target.getXPathPrefixes();
		for (String prefix : xpathPrefixes) {
			if(ns.get(prefix)==null){
				return false;
			}
		}
		return true;
	}

	
	/** Implements the NamespaceContext getPrefix method. */
	public String getPrefix(String namespaceURI) {
		if (ns.containsValue(namespaceURI)) {
			Iterator<String> keys = ns.keySet().iterator();
			while (keys.hasNext()) {
				String pfx = keys.next();
				String uri = ns.get(pfx);
				if (namespaceURI.equals(uri)) {
					return pfx;
				}
			}
		}
		return null;
	}

	/**
	 * Implements a <emph>NON STANDARD</emph> method for finding all of the
	 * prefixes in the namespace context.
	 * 
	 * <p>
	 * Returns an iterator over all of the prefixes in the namespace context.
	 * Note that multiple prefixes may be bound to the same URI.
	 * </p>
	 */
	public Iterator getPrefixes() {
		return getPrefixes(null);
	}

	/** Implements the NamespaceContext getPrefixes method. */
	public Iterator getPrefixes(String namespaceURI) {
		return new NSIterator(ns, namespaceURI);
	}

	/**
	 * Implements a <emph>NON STANDARD</emph> method for finding all of the
	 * namespace URIs in the namespace context.
	 * 
	 * <p>
	 * Returns an iterator over all of the namespace URIs in the namespace
	 * context. Note that each namespace URI is returned exactly once, even if
	 * it is bound to several different prefixes.
	 * </p>
	 */
	public Iterator getNamespaceURIs() {
		// Make sure each URI is returned at most once...
		Map<String, String> uriHash = new HashMap<String, String>();
		
		for  (String pfx:ns.keySet()) {
			
			String uri = ns.get(pfx);
			if (!uriHash.containsKey(uri)) {
				uriHash.put(uri, pfx);
			}
		}

		return new NSIterator(uriHash, null);
	}

	/** Implements the Iterator interface over namespace bindings. */
	private class NSIterator implements Iterator {
		private Iterator<String> keys;

		public NSIterator(Map<String, String> hash, String value) {
			keys = hash.keySet().iterator();
			if (value != null) {
				// We have to copy the hash to get only the keys that have the
				// specified value
				Map<String, String> vHash = new HashMap<String, String>();
				while (keys.hasNext()) {
					String key = keys.next();
					String val = hash.get(key);
					if (val.equals(value)) {
						vHash.put(key, val);
					}
				}
				keys = vHash.keySet().iterator();
			}
		}

		public boolean hasNext() {
			return keys.hasNext();
		}

		public String next() {
			return keys.next();
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"Cannot remove prefix in NamespaceContextHelper");
		}
	}

}
