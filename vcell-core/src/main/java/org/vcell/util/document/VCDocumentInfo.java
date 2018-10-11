/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import java.util.ArrayList;

import org.vcell.util.document.VCDocument.VCDocumentType;

public interface VCDocumentInfo extends VersionInfo {

	default PublicationInfo[] getPublicationInfos() {
		return new PublicationInfo[0];
	}
	default void addPublicationInfo(PublicationInfo publicationInfo) {
		//do nothing, intended to be overriden
	}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
boolean equals(Object object);


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
Version getVersion();


/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
int hashCode();


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String toString();


VCDocumentType getVCDocumentType();

}
