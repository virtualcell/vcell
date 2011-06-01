/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.quicktime;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import cbit.vcell.export.quicktime.atoms.*;
/**
 * This type was created in VisualAge.
 */
public interface MediaSample {
	public static class MediaSampleStream{
		private byte[] dataBytes = null;
		public MediaSampleStream(byte[] inData){
			dataBytes = inData;
		}
		public void writeBytes(OutputStream out) throws IOException{
			InputStream inputStream = null;
			byte[] bufferBytes = new byte[10000];
			if(dataBytes != null){
				inputStream = new ByteArrayInputStream(dataBytes);
			}
			if(inputStream != null){
				try{
					int bytesRead;
					while ((bytesRead = inputStream.read(bufferBytes,0,bufferBytes.length)) != -1) {
						out.write(bufferBytes,0,bytesRead);
					}
				}finally{
					inputStream.close();
				}
			}
		}
	}
/**
 * This method was created in VisualAge.
 * @return byte[]
 */
MediaSampleStream getDataInputStream();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getDataFormat();
/**
 * This method was created in VisualAge.
 * @return int
 */
int getDuration();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getMediaType();
/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
SampleDescriptionEntry getSampleDescriptionEntry();
/**
 * This method was created in VisualAge.
 * @return int
 */
int getSize();
/**
 * This method was created in VisualAge.
 * @return boolean
 */
boolean isKeyFrame();
}
