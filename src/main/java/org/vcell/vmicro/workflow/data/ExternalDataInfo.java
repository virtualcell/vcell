/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.data;

import org.vcell.util.document.ExternalDataIdentifier;

/**
 */
public class ExternalDataInfo {
	
	private ExternalDataIdentifier externalDataIdentifier = null;
	private String filename = null;
	
	/**
	 * Constructor for ExternalDataInfo.
	 * @param externalDataIdentifier ExternalDataIdentifier
	 * @param filename String
	 */
	public ExternalDataInfo(ExternalDataIdentifier externalDataIdentifier, String filename) {
		super();
		this.externalDataIdentifier = externalDataIdentifier;
		this.filename = filename;
	}
	/**
	 * Method getExternalDataIdentifier.
	 * @return ExternalDataIdentifier
	 */
	public ExternalDataIdentifier getExternalDataIdentifier() {
		return externalDataIdentifier;
	}
	/**
	 * Method getFilename.
	 * @return String
	 */
	public String getFilename() {
		return filename;
	}
	
}
