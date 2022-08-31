#@VCellHelper vh

import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;

cachekey=1
lastVCellApiPort = vh.findVCellApiServerPort()
    		doc = vh.getDocument(new URL("http://localhost:"+lastVCellApiPort+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+"C_cyt"+"&"+"timepoint"+"=0.5"));//get data
    		basicStackDimensions = vh.getVCStackDims(doc);
     		double[] data = vh.getData(doc);
       		System.out.println(basicStackDimensions.getTotalSize());
       		System.out.println(data.length);

       		dims = new long[basicStackDimensions.numDimensions()];
       		basicStackDimensions.dimensions(dims);
       		img = ArrayImgs.doubles(data, dims);
            ImageJFunctions.show( img );
