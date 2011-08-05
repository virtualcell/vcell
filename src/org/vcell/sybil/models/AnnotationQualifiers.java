/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models;

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.dublincore.DublinCoreQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;

public class AnnotationQualifiers {
	public static final Set<MIRIAMQualifier> MIRIAMMODEL_all;
	public static final Set<MIRIAMQualifier> MIRIAMBIO_all;
	public static final Set<MIRIAMQualifier> MIRIAM_all;
	public static final Set<DublinCoreQualifier.DateQualifier> DC_date_all;
	public static final Set<DublinCoreQualifier> DC_all;
	public static final Set<AnnotationQualifier> all;
	static {
		//
		// MIRIAM qualifiers
		//
		MIRIAMBIO_all = new HashSet<MIRIAMQualifier>();
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_encodes);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_hasPart);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_hasProperty);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_hasVersion);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_is);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_isDescribedBy);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_isEncodedBy);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_isHomologTo);
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_isPartOf);			
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_isPropertyOf);			
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_isVersionOf);			
		MIRIAMBIO_all.add(MIRIAMQualifier.BIO_occursIn);			
		MIRIAMMODEL_all = new HashSet<MIRIAMQualifier>();
		MIRIAMMODEL_all.add(MIRIAMQualifier.MODEL_is);
		MIRIAMMODEL_all.add(MIRIAMQualifier.MODEL_isDerivedFrom);
		MIRIAMMODEL_all.add(MIRIAMQualifier.MODEL_isDescribedBy);
		MIRIAM_all = new HashSet<MIRIAMQualifier>();
		MIRIAM_all.addAll(MIRIAMBIO_all);
		MIRIAM_all.addAll(MIRIAMMODEL_all);

		//
		// Dublin Core qualifiers
		//
		DC_date_all = new HashSet<DublinCoreQualifier.DateQualifier>();
		DC_date_all.add(DublinCoreQualifier.created);
		DC_all = new HashSet<DublinCoreQualifier>();
		DC_all.addAll(DC_date_all);

		//
		// All qualifiers
		//
		all = new HashSet<AnnotationQualifier>();
		all.addAll(MIRIAM_all);
		all.addAll(DC_all);
	}
}
