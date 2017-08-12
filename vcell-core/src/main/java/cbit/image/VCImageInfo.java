/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;

/**
 * This type was created in VisualAge.
 */
public class VCImageInfo implements VersionInfo {
	private ISize size = null;
	private Extent extent = null;
	private Version version = null;
	private GIFImage browseData = null;
	private VCellSoftwareVersion softwareVersion = null;
/**
 * This method was created in VisualAge.
 * @param version cbit.sql.Version
 * @param size ISize
 * @param extent Extent
 */
public VCImageInfo(Version aVersion, ISize aISize, Extent aExtent,GIFImage argBrowseData,VCellSoftwareVersion softwareVersion) {
	this.version = aVersion;
	this.size = aISize;
	this.extent = aExtent;
	this.browseData = argBrowseData;
	this.softwareVersion = softwareVersion;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof VCImageInfo){
		if (!getVersion().getVersionKey().equals(((VCImageInfo)object).getVersion().getVersionKey())){
			return false;
		}
		return true;
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.GIFImage
 */
public GIFImage getBrowseGif() {
	return browseData;
}
/**
 * This method was created in VisualAge.
 * @return cbit.util.Extent
 */
public Extent getExtent() {
	return extent;
}
/**
 * This method was created in VisualAge.
 * @return cbit.util.ISize
 */
public ISize getISize() {
	return size;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
public Version getVersion() {
	return version;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
public int hashCode() {
	return getVersion().getVersionKey().hashCode();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	try {
		String browseImgInfo = null;
		if(getBrowseGif() != null){
			ISize browseSize = getBrowseGif().getSize();
			browseImgInfo = "Xs="+browseSize.getX()+" Ys="+browseSize.getY();
		}else{
			browseImgInfo = "none";
		}
		return "VCImageInfo[numPixels="+getISize()+", extent="+getExtent()+", BrowseImg=("+browseImgInfo+"), version="+version+"]" ;
	}catch (Throwable e){
		return "exception in VCImageInfo.toString(): "+e.getMessage();
	}
}
public VersionableType getVersionType() {
	return VersionableType.VCImage;
}
public VCellSoftwareVersion getSoftwareVersion() {
	return softwareVersion;
}
}
