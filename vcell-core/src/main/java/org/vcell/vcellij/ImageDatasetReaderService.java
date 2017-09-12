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

import java.util.List;
import java.util.ServiceConfigurationError;

import org.scijava.SciJava;

/**
 * Service for database connection factory
 */
public class ImageDatasetReaderService {
	
	private static ImageDatasetReaderService service;
	private SciJava scijava;
	
	private ImageDatasetReaderService() {
		scijava = new SciJava();
	}
	
	public static synchronized ImageDatasetReaderService getInstance(){
		if (service == null){
			service = new ImageDatasetReaderService();
		}
		return service;
	}
	
	public ImageDatasetReader getImageDatasetReader() {
		try {
			List<ImageDatasetReader> imageDatasetReaders = scijava.plugin().createInstancesOfType(ImageDatasetReader.class);
			if (imageDatasetReaders.size()>0){
				return imageDatasetReaders.get(0);
			}else{
				return null;
			}
		} catch (ServiceConfigurationError serviceError){
			serviceError.printStackTrace();
			throw new RuntimeException("imageDatasetReader provider configuration error: "+serviceError.getMessage(),serviceError);
		}
	}
	
}
