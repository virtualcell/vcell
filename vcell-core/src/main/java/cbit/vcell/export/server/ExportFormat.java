package cbit.vcell.export.server;

/**
 * supported export formats
 */
public enum ExportFormat {
	//refactored from ExportConstants
	
	CSV("Comma delimited ASCII files (*.csv)", true) ,
	HDF5("HDF5 files (*.csv)", true) ,
	QUICKTIME("QuickTime movie files (*.mov)", true),
	GIF("GIF89a image files (*.gif)", true),
	ANIMATED_GIF("Animated GIF files (*.gif)", true),
	FORMAT_JPEG("JPEG image files (*.jpg)", true),
	NRRD("Nearly raw raster data (*.nrrd)", true),
	UCD("UCD (*.ucd)", false),
	VTK_UNSTRUCT("VTK Unstructured (*.vtu)", false),
	VTK_IMAGE("VTK Image (*.vtk)", false),
	PLY("Stanford Poly Texture (*.ply)", false),
//	IMAGEJ("ImageJ direct", false)
	; 
	ExportFormat(String label, boolean requiresFollowOn ) {
		this.label = label;
		this.needFollowOn = requiresFollowOn;
	}
	
	/**
	 * @return true if selecting requires an options Dialog 
	 * */
	public boolean requiresFollowOn() {
		return needFollowOn;
	}

	private final String label;
	private final boolean needFollowOn;
	
	@Override
	public String toString( ) {
		return label;
	}
	
	
}