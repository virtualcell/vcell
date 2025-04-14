/*
 * Copyright (C) 1999-2017 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vcellij;

import cbit.vcell.VirtualMicroscopy.BioformatsImageImplNew;


public class ImageDatasetReaderService {
	
	private static ImageDatasetReaderService service;
	private final BioformatsImageImplNew bioformatsImageImpl;

	private ImageDatasetReaderService() {
		this.bioformatsImageImpl = new BioformatsImageImplNew();
	}
	
	public static synchronized ImageDatasetReaderService getInstance(){
		if (service == null){
			service = new ImageDatasetReaderService();
		}
		return service;
	}
	
	public ImageDatasetReader getImageDatasetReader() {
		return this.bioformatsImageImpl;
	}
	
}
