/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.xml;

/*   XMLUtil  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Useful static methods for dealing with XML DOM documents
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DOMUtil {

	protected static DocumentBuilder builder;

	protected static void initBuilder() throws ParserConfigurationException {
		if(builder == null) {
			DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
			builder = factory.newDocumentBuilder();
		}
	}

	public static Document parse(InputStream is) 
	throws ParserConfigurationException, SAXException, IOException {
		initBuilder();
		return builder.parse(is);
	}

	public static Document parse(String text) 
	throws SAXException, IOException, ParserConfigurationException {
		initBuilder();
		return builder.parse(new ByteArrayInputStream(text.getBytes()));
	}
	
	public static void serialize(Document document, OutputStream out) throws IOException {
		OutputFormat format = new OutputFormat();
		XMLSerializer serializer = new XMLSerializer(out, format);
		DOMSerializer domSerializer = serializer.asDOMSerializer();
		domSerializer.serialize(document);
	}
	
	public static List<Element> childElements(Node node, String tag) {
		List<Element> elements = new Vector<Element>();
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); ++i) {
			Node childNode = childList.item(i);
			if(childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if(childElement.getTagName().equals(tag)) { elements.add(childElement); }
			}
		}
		return elements;
	}
	
	public static List<String> childContents(Node node, String tag) {
		List<String> contents = new Vector<String>();
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); ++i) {
			Node childNode = childList.item(i);
			if(childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if(childElement.getTagName().equals(tag)) { contents.add(childElement.getTextContent()); }
			}
		}
		return contents;
	}
	
	public static Element firstChildElement(Node node, String tag) {
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); ++i) {
			Node childNode = childList.item(i);
			if(childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if(childElement.getTagName().equals(tag)) { return childElement; }
			}
		}
		return null;
	}
	
	public static String firstChildContent(Node node, String tag) {
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); ++i) {
			Node childNode = childList.item(i);
			if(childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if(childElement.getTagName().equals(tag)) { return childElement.getTextContent(); }
			}
		}
		return "";
	}
	
}
