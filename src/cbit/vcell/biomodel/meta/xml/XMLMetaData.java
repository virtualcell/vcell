/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta.xml;

import org.jdom.Namespace;

import cbit.vcell.xml.XMLTags;

/**
 * Turns meta data into a JDOM Element
 * @author ruebenacker
 *
 */

public class XMLMetaData {
	
	public static final Namespace rdfNameSpace = Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI);
	
	public static final String VCMETADATA_TAG				= "vcmetadata";
	public static final String URI_BINDING_LIST_TAG			= "uriBindingList";
	public static final String URI_BINDING_TAG				= "uriBinding";
	public static final String URI_ATTR_TAG					= "uri";
	public static final String VCID_ATTR_TAG				= "vcid";
	public static final String NONRDF_ANNOTATION_LIST_TAG	= "nonrdfAnnotationList";
	public static final String NONRDF_ANNOTATION_TAG		= "nonrdfAnnotation";
	public static final String FREETEXT_TAG					= "freetext";
	public static final String NOTES_TAG					= "notes";
	public static final String ANNOTATION_LIST_TAG			= "annotationList";
	
}
//public String toVCML(){
//
// old metadata serialization (stored with the object)
//
// <species name="calcium" description="this is the description ....">
//    <notes>
//        <xhtml> </xhtml>
//    </notes>
//    <annotation>
//       <cellDesigner: ... />
//        ...
//       <rdf></rdf>
//    </annotation>
// </species>

//
//reaction5
//
// new metadata serialization (all stored in one place)
//
//
// <vcmetadata>
//    <listOfNotes>
//        <note reference="model/reaction:buffering_SPACE_/species:calcium">
//            <xhtml> </xhtml>
//        </note>
//        <note reference="metaID6778889">
//            <xhtml> </xhtml>
//        </note>
//    </listOfNotes>
//    <listOfNonRDFAnnotations>
//        <annotationElement reference="metaID0303030">
//	         <cellDesigner:stuff xmlns=l......>
//           </cellDesigner:stuff>
//        </annnotationElement>
//        <annotationElement reference="metaID494949">
//	         <cellDesigner:stuff xmlns=l......>
//           </cellDesigner:stuff>
//        </annnotationElement>
//    </listOfNonRDFAnnotations>
//    <rdf:>
//     </rdf>
// </vcmetadata>
//


//}
