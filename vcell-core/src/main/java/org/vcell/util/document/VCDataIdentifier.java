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


import cbit.vcell.export.server.*;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifierOldStyle;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Insert the type's description here.
 * Creation date: (9/19/2003 5:16:48 PM)
 * @author: Anuradha Lakshminarayana
 */
@Schema( // Including One of destroys inheritance, and instead generates a factory class
        discriminatorMapping = {
                @DiscriminatorMapping(value = "VCSimulationDataIdentifier", schema = VCSimulationDataIdentifier.class),
        },
        discriminatorProperty = "type",
        oneOf = {VCSimulationDataIdentifier.class, ExternalDataIdentifier.class, MergedDataInfo.class, VCSimulationDataIdentifierOldStyle.class}
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",  // Discriminator field
        visible = true
)
@JsonSubTypes({@JsonSubTypes.Type(value = VCSimulationDataIdentifier.class, name = "VCSimulationDataIdentifier")})
public interface VCDataIdentifier {
/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 5:20:17 PM)
 * @return java.lang.String
 */
String getID();

KeyValue getDataKey();
/**
 * Insert the method's description here.
 * Creation date: (9/22/2003 11:31:01 AM)
 * @return cbit.vcell.server.User
 */
User getOwner();
}
