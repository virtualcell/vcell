package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
/**
 * This type was created in VisualAge.
 */
public class VCImageInfo implements VersionInfo {
	private ISize size = null;
	private Extent extent = null;
	private Version version = null;
	private GIFImage browseData = null;
/**
 * This method was created in VisualAge.
 * @param version cbit.sql.Version
 * @param size ISize
 * @param extent Extent
 */
public VCImageInfo(Version aVersion, ISize aISize, Extent aExtent,GIFImage argBrowseData) {
	this.version = aVersion;
	this.size = aISize;
	this.extent = aExtent;
	this.browseData = argBrowseData;
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
}
