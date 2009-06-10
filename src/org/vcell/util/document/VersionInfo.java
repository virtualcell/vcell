package org.vcell.util.document;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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

}
