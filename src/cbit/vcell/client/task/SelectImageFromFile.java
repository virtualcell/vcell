package cbit.vcell.client.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

import org.vcell.util.Extent;

import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.DatabaseWindowManager.ImageHelper;
import cbit.vcell.geometry.GeometrySpec;

public class SelectImageFromFile extends AsynchClientTask {

	private final int PIXEL_CLASS_WARNING_LIMIT = 10;
	
	public SelectImageFromFile() {
		super("read image from file", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
	}
	@Override
	public void run(Hashtable<String, Object> hashTable) throws Exception {
		ClientTaskStatusSupport pp = getClientTaskStatusSupport();
		File imageFile = (File)hashTable.get("imageFile");
		VCImage vcImage = null;
		long fileSize = imageFile.length();
		pp.setMessage("Reading file "+imageFile.getName()+" size="+fileSize);
		if(fileSize > GeometrySpec.IMAGE_SIZE_LIMIT){
			throw new cbit.image.ImageException("File "+imageFile.getName()+" size="+fileSize+"\ntoo large.  Size must be less than "+GeometrySpec.IMAGE_SIZE_LIMIT);
		}
		// Get file bytes
		byte[] fileBytes = new byte[(int)fileSize];
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(imageFile);
			fis.read(fileBytes);
		}finally{
			if(fis != null){fis.close();}
		}
		// Parse bytes
		try{
			ImageHelper ih = null;
			if(imageFile.getName().toLowerCase().endsWith(".gif")){
				pp.setMessage("Parsing file "+imageFile.getName()+" size="+fileSize+" as gif");
				ih = DatabaseWindowManager.convertGIF(fileBytes);
				
			}else if(imageFile.getName().toLowerCase().endsWith(".tif") || imageFile.getName().toLowerCase().endsWith(".tiff")){
				pp.setMessage("Parsing file "+imageFile.getName()+" size="+fileSize+" as tif");
				ih = DatabaseWindowManager.convertTIF(fileBytes);
					
			}else if(	imageFile.getName().toLowerCase().endsWith(".zip")){
				pp.setMessage("Parsing file "+imageFile.getName()+" size="+fileSize+" as zip");
				ih = DatabaseWindowManager.convertZIP(fileBytes,pp);
			}else{
				throw new Exception("File name "+imageFile.getName()+" not recognized, must end with .gif,.tif,.tiff -or- zip containing gif,tif for each Z-slice");
			}
	
			vcImage = new VCImageUncompressed(null,ih.imageData,new Extent(ih.xsize,ih.ysize,ih.zsize),ih.xsize,ih.ysize,ih.zsize);
			
			pp.setMessage("Finished loading "+imageFile.getName()+" ("+vcImage.getNumX()+","+vcImage.getNumY()+","+vcImage.getNumZ()+")");
		}catch(Throwable e){
			throw new cbit.image.ImageException("Error loading image "+imageFile.getAbsolutePath()+"\n"+(e.getMessage() == null?e.getClass().getName():e.getMessage()));
		}
	
		if(vcImage.getPixelClasses().length > PIXEL_CLASS_WARNING_LIMIT){
			PopupGenerator.showInfoDialog(
						"Warning: IMAGE "+imageFile.getName()+" has "+vcImage.getPixelClasses().length+
						" distinct values, all will be assigned regions.\n"+
						"If this is unexpected, process the IMAGE to remove noise or unwanted values and re-load");
		}
		hashTable.put("vcImage", vcImage);
	}

}
