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

import java.io.Serializable;

import org.vcell.util.Immutable;
/**
 * This type was created in VisualAge.
 */
public interface VersionInfo extends Immutable, Serializable {
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
Version getVersion();
VersionableType getVersionType();
VCellSoftwareVersion getSoftwareVersion();

}
