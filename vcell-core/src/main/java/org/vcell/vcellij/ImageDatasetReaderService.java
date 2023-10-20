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

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;


public class ImageDatasetReaderService {
	
	private static ImageDatasetReaderService service;

	private ImageDatasetReaderService() {
	}
	
	public static synchronized ImageDatasetReaderService getInstance(){
		if (service == null){
			service = new ImageDatasetReaderService();
		}
		return service;
	}
	
	public ImageDatasetReader getImageDatasetReader() {
		try {
			ServiceLoader<ImageDatasetReader> imageDatasetReaders = ServiceLoader.load(ImageDatasetReader.class);
			for (ImageDatasetReader imageDatasetReader : imageDatasetReaders){
				return imageDatasetReader;
			}
			return null;
		} catch (ServiceConfigurationError serviceError){
			throw new RuntimeException("imageDatasetReader provider configuration error: "+serviceError.getMessage(),serviceError);
		}
	}
	
}
