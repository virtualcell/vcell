/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.miriam;

/*   MIRIAMQualifier  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Wrapper for a MIRIAM property
 */

import org.openrdf.model.URI;
import org.sbpax.schemas.MIRIAM;
import org.vcell.sybil.models.AnnotationQualifier;
import org.vcell.sybil.util.keys.KeyOfOne;

public class MIRIAMQualifier extends KeyOfOne<URI> implements AnnotationQualifier {
	public static final MIRIAMQualifier BIO_encodes = new MIRIAMQualifier(MIRIAM.BioProperties.encodes,"(bio) encodes");
	public static final MIRIAMQualifier BIO_hasPart = new MIRIAMQualifier(MIRIAM.BioProperties.hasPart,"(bio) hasPart");
	public static final MIRIAMQualifier BIO_hasProperty = new MIRIAMQualifier(MIRIAM.BioProperties.hasProperty,"(bio) hasProperty");
	public static final MIRIAMQualifier BIO_hasVersion = new MIRIAMQualifier(MIRIAM.BioProperties.hasVersion,"(bio) hasVersion");
	public static final MIRIAMQualifier BIO_is = new MIRIAMQualifier(MIRIAM.BioProperties.is,"(bio) is");
	public static final MIRIAMQualifier BIO_isDescribedBy = new MIRIAMQualifier(MIRIAM.BioProperties.isDescribedBy,"(bio) isDescribedBy");
	public static final MIRIAMQualifier BIO_isEncodedBy = new MIRIAMQualifier(MIRIAM.BioProperties.isEncodedBy,"(bio) isEncodedBy");
	public static final MIRIAMQualifier BIO_isHomologTo = new MIRIAMQualifier(MIRIAM.BioProperties.isHomologTo,"(bio) isHomologOf");
	public static final MIRIAMQualifier BIO_isPartOf = new MIRIAMQualifier(MIRIAM.BioProperties.isPartOf,"(bio) isPartOf");
	public static final MIRIAMQualifier BIO_isPropertyOf = new MIRIAMQualifier(MIRIAM.BioProperties.isPropertyOf,"(bio) isPropertyOf");
	public static final MIRIAMQualifier BIO_isVersionOf = new MIRIAMQualifier(MIRIAM.BioProperties.isVersionOf,"(bio) isVersionOf");
	public static final MIRIAMQualifier BIO_occursIn = new MIRIAMQualifier(MIRIAM.BioProperties.occursIn,"(bio) occursIn");
	public static final MIRIAMQualifier MODEL_is = new MIRIAMQualifier(MIRIAM.ModelProperties.is,"(model) is");
	public static final MIRIAMQualifier MODEL_isDerivedFrom = new MIRIAMQualifier(MIRIAM.ModelProperties.isDerivedFrom,"(model) isDerivedBy");
	public static final MIRIAMQualifier MODEL_isDescribedBy = new MIRIAMQualifier(MIRIAM.ModelProperties.isDescribedBy,"(model) isDescribedBy");

	private String description = null;
		
	private MIRIAMQualifier(URI property, String description) {
		super(property);
		this.description = description;
	}
	
	public URI getProperty() { return a(); }
	
	public String getLocalName(){
		return getProperty().getLocalName();
	}

	public String getNameSpace(){
		return getProperty().getNamespace();
	}
	
	public String getDescription(){
		return description;
	}

}
