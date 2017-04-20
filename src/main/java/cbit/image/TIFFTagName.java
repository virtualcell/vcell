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

class TIFFTagName 
{
   final static int TIFFTAG_SUBFILETYPE=254;	/* subfile data descriptor */
   final static int TIFFTAG_OSUBFILETYPE=255;	/* +kind of data in subfile */
   final static int TIFFTAG_IMAGEWIDTH=256;	/* image width in pixels */
   final static int TIFFTAG_IMAGELENGTH=257;	/* image height in pixels */
   final static int TIFFTAG_BITSPERSAMPLE=258;	/* bits per channel (sample) */
   final static int TIFFTAG_COMPRESSION=259;	/* data compression technique */
   final static int TIFFTAG_PHOTOMETRIC=262;	/* photometric interpretation */
   final static int TIFFTAG_THRESHHOLDING=263;	/* +thresholding used on data */
   final static int TIFFTAG_CELLWIDTH=264;	/* +dithering matrix width */
   final static int TIFFTAG_CELLLENGTH=265;	/* +dithering matrix height */
   final static int TIFFTAG_FILLORDER=266;	/* data order within a byte */
   final static int TIFFTAG_DOCUMENTNAME=269;	/* name of doc. image is from */
   final static int TIFFTAG_IMAGEDESCRIPTION=270;	/* info about image */
   final static int TIFFTAG_MAKE=271;	/* scanner manufacturer name */
   final static int TIFFTAG_MODEL=272;	/* scanner model name/number */
   final static int TIFFTAG_STRIPOFFSETS=273;	/* offsets to data strips */
   final static int TIFFTAG_ORIENTATION=274;	/* +image orientation */
   final static int TIFFTAG_SAMPLESPERPIXEL=277;	/* samples per pixel */
   final static int TIFFTAG_ROWSPERSTRIP=278;	/* rows per strip of data */
   final static int TIFFTAG_STRIPBYTECOUNTS=279;	/* bytes counts for strips */
   final static int TIFFTAG_MINSAMPLEVALUE=280;	/* +minimum sample value */
   final static int TIFFTAG_MAXSAMPLEVALUE=281;	/* +maximum sample value */
   final static int TIFFTAG_XRESOLUTION=282;	/* pixels/resolution in x */
   final static int TIFFTAG_YRESOLUTION=283;	/* pixels/resolution in y */
   final static int TIFFTAG_PLANARCONFIG=284;	/* storage organization */
   final static int TIFFTAG_PAGENAME=285;	/* page name image is from */
   final static int TIFFTAG_XPOSITION=286;	/* x page offset of image lhs */
   final static int TIFFTAG_YPOSITION=287;	/* y page offset of image lhs */
   final static int TIFFTAG_FREEOFFSETS=288;	/* +byte offset to free block */
   final static int TIFFTAG_FREEBYTECOUNTS=289;	/* +sizes of free blocks */
   final static int TIFFTAG_GRAYRESPONSEUNIT=290;	/* $gray scale curve accuracy */
   final static int TIFFTAG_GRAYRESPONSECURVE=291;	/* $gray scale response curve */
   final static int TIFFTAG_GROUP3OPTIONS=292;	/* 32 flag bits */
   final static int TIFFTAG_GROUP4OPTIONS=293;	/* 32 flag bits */
   final static int TIFFTAG_RESOLUTIONUNIT=296;	/* units of resolutions */
   final static int TIFFTAG_PAGENUMBER=297;	/* page numbers of multi-page */
   final static int TIFFTAG_COLORRESPONSEUNIT=300;	/* $color curve accuracy */
   final static int TIFFTAG_TRANSFERFUNCTION=301;	/* !colorimetry info */
   final static int TIFFTAG_SOFTWARE=305;	/* name & release */
   final static int TIFFTAG_DATETIME=306;	/* creation date and time */
   final static int TIFFTAG_ARTIST=315;	/* creator of image */
   final static int TIFFTAG_HOSTCOMPUTER=316;	/* machine where created */
   final static int TIFFTAG_PREDICTOR=317;	/* prediction scheme w/ LZW */
   final static int TIFFTAG_WHITEPOINT=318;	/* image white point */
   final static int TIFFTAG_PRIMARYCHROMATICITIES=319;	/* !primary chromaticities */
   final static int TIFFTAG_COLORMAP=320;	/* RGB map for pallette image */
   final static int TIFFTAG_HALFTONEHINTS=321;	/* !highlight+shadow info */
   final static int TIFFTAG_TILEWIDTH=322;	/* !rows/data tile */
   final static int TIFFTAG_TILELENGTH=323;	/* !cols/data tile */
   final static int TIFFTAG_TILEOFFSETS=324;	/* !offsets to data tiles */
   final static int TIFFTAG_TILEBYTECOUNTS=325;	/* !byte counts for tiles */
   final static int TIFFTAG_BADFAXLINES	=326;	/* lines w/ wrong pixel count */
   final static int TIFFTAG_CLEANFAXDATA=327;	/* regenerated line info */
   final static int TIFFTAG_CONSECUTIVEBADFAXLINES=328;	/* max consecutive bad lines */
   final static int TIFFTAG_INKSET=332;	/* !inks in separated image */
   final static int TIFFTAG_INKNAMES=333;	/* !ascii names of inks */
   final static int TIFFTAG_DOTRANGE=336;	/* !0% and 100% dot codes */
   final static int TIFFTAG_TARGETPRINTER=337;	/* !separation target */
   final static int TIFFTAG_EXTRASAMPLES=338;	/* !info about extra samples */
   final static int TIFFTAG_SAMPLEFORMAT=339;	/* !data sample format */
   final static int TIFFTAG_SMINSAMPLEVALUE=340;	/* !variable MinSampleValue */
   final static int TIFFTAG_SMAXSAMPLEVALUE=341;	/* !variable MaxSampleValue */
   final static int TIFFTAG_JPEGPROC=512;	/* !JPEG processing algorithm */
   final static int TIFFTAG_JPEGIFOFFSET=513;	/* !pointer to SOI marker */
   final static int TIFFTAG_JPEGIFBYTECOUNT=514;	/* !JFIF stream length */
   final static int TIFFTAG_JPEGRESTARTINTERVAL=515;	/* !restart interval length */
   final static int TIFFTAG_JPEGLOSSLESSPREDICTORS=517;	/* !lossless proc predictor */
   final static int TIFFTAG_JPEGPOINTTRANSFORM=518;	/* !lossless point transform */
   final static int TIFFTAG_JPEGQTABLES	=519;	/* !Q matrice offsets */
   final static int TIFFTAG_JPEGDCTABLES=520;	/* !DCT table offsets */
   final static int TIFFTAG_JPEGACTABLES=521;	/* !AC coefficient offsets */
   final static int TIFFTAG_YCBCRCOEFFICIENTS=529;	/* !RGB -> YCbCr transform */
   final static int TIFFTAG_YCBCRSUBSAMPLING=530;	/* !YCbCr subsampling factors */
   final static int TIFFTAG_YCBCRPOSITIONING=531;	/* !subsample positioning */
   final static int TIFFTAG_REFERENCEBLACKWHITE=532;	/* !colorimetry info */
   /* tags 32995-32999 are private tags registered to SGI */
   final static int TIFFTAG_MATTEING=32995;	/* $use ExtraSamples */
   final static int TIFFTAG_DATATYPE=32996;     /* Old SampleFormat  */
   final static int TIFFTAG_IMAGEDEPTH=32997;	/* z depth of image */
   final static int TIFFTAG_TILEDEPTH=32998;	/* z depth/data tile */

   private int tag;
   
   TIFFTagName(int tag)
   {
       this.tag = tag;
   }
   int getType()
   {
       return tag;
   }
   public String toString()
   {
      switch (tag){
	  case TIFFTAG_SUBFILETYPE:
	     return "NewSubfileType";
	  case TIFFTAG_OSUBFILETYPE:
	     return "SubfileType";
	  case TIFFTAG_IMAGEWIDTH:
	     return "ImageWidth";
	  case TIFFTAG_IMAGELENGTH:
	     return "ImageLength";
	  case TIFFTAG_BITSPERSAMPLE:
	     return "BitsPerSample";
	  case TIFFTAG_COMPRESSION:
	     return "Compression";
	  case TIFFTAG_PHOTOMETRIC:
	     return "PhotometricInterpretation";
	  case TIFFTAG_THRESHHOLDING:
	     return "Thresholding";
	  case TIFFTAG_CELLWIDTH:
	     return "CellWidth";
	  case TIFFTAG_CELLLENGTH:
	     return "CellLength";
	  case TIFFTAG_FILLORDER:
	     return "FillOrder";
	  case TIFFTAG_DOCUMENTNAME:
	     return "DocumentName";
	  case TIFFTAG_IMAGEDESCRIPTION:
	     return "ImageDescription";
	  case TIFFTAG_MAKE:
	     return "Make";
	  case TIFFTAG_MODEL:
	     return "Model";
	  case TIFFTAG_STRIPOFFSETS:
	     return "StripOffsets";
	  case TIFFTAG_ORIENTATION:
	     return "Orientation";
	  case TIFFTAG_SAMPLESPERPIXEL:
	     return "SamplesPerPixel";
	  case TIFFTAG_ROWSPERSTRIP:
	     return "RowsPerStrip";
	  case TIFFTAG_STRIPBYTECOUNTS:
	     return "StripByteCounts";
	  case TIFFTAG_MINSAMPLEVALUE:
	     return "MinSampleValue";
	  case TIFFTAG_MAXSAMPLEVALUE:
	     return "MaxSampleValue";
	  case TIFFTAG_XRESOLUTION:
	     return "XResolution";
	  case TIFFTAG_YRESOLUTION:
	     return "YResolution";
	  case TIFFTAG_PLANARCONFIG:
	     return "PlanarConfiguration";
	  case TIFFTAG_PAGENAME:
	     return "PageName";
	  case TIFFTAG_XPOSITION:
	     return "XPosition";
	  case TIFFTAG_YPOSITION:
	     return "YPosition";
	  case TIFFTAG_FREEOFFSETS:
	     return "FreeOffsets";
	  case TIFFTAG_FREEBYTECOUNTS:
	     return "FreeByteCounts";
	  case TIFFTAG_GRAYRESPONSEUNIT:
	     return "GrayResponseUnit";
	  case TIFFTAG_GRAYRESPONSECURVE:
	     return "GrayResponseCurve";
	  case TIFFTAG_GROUP3OPTIONS:
	     return "Group3options";
	  case TIFFTAG_GROUP4OPTIONS:
	     return "Group4options";
	  case TIFFTAG_RESOLUTIONUNIT:
	     return "ResolutionUnit";
	  case TIFFTAG_PAGENUMBER:
	     return "PageNumber";
	  case TIFFTAG_COLORRESPONSEUNIT:
	     return "ColorResponseCurves";
	  case TIFFTAG_TRANSFERFUNCTION:
	     return "TransferFunction";
	  case TIFFTAG_SOFTWARE:
	     return "Software";
	  case TIFFTAG_DATETIME:
	     return "DateTime";
	  case TIFFTAG_ARTIST:
	     return "Artist";
	  case TIFFTAG_HOSTCOMPUTER:
	     return "Hostcomputer";
	  case TIFFTAG_PREDICTOR:
	     return "Predictor";
	  case TIFFTAG_WHITEPOINT:
	     return "Whitepoint";
	  case TIFFTAG_PRIMARYCHROMATICITIES:
	     return "PrimaryChromaticities";
	  case TIFFTAG_COLORMAP:
	     return "Colormap";
	  case TIFFTAG_HALFTONEHINTS:
	     return "HalftoneHints";
	  case TIFFTAG_TILEWIDTH:
	     return "TileWidth";
	  case TIFFTAG_TILELENGTH:
	     return "TileLength";
	  case TIFFTAG_TILEOFFSETS:
	     return "TileOffsets";
	  case TIFFTAG_TILEBYTECOUNTS:
	     return "TileByteCounts";
	  case TIFFTAG_CLEANFAXDATA:
	     return "CleanFaxData";
	  case TIFFTAG_CONSECUTIVEBADFAXLINES:
	     return "ConsecutiveBadFaxLines";
	  case TIFFTAG_BADFAXLINES:
	     return "BadFaxLines";
	  case TIFFTAG_INKSET:
	     return "InkSet";
	  case TIFFTAG_INKNAMES:
	     return "InkNames";
	  case TIFFTAG_DOTRANGE:
	     return "DotRange";
	  case TIFFTAG_TARGETPRINTER:
	     return "TargetPrinter";
	  case TIFFTAG_EXTRASAMPLES:
	     return "ExtraSamples";
	  case TIFFTAG_SAMPLEFORMAT:
	     return "SampleFormat";
	  case TIFFTAG_SMINSAMPLEVALUE:
	     return "SMinSampleValue";
	  case TIFFTAG_SMAXSAMPLEVALUE:
	     return "SMaxSampleValue";
	  case TIFFTAG_JPEGPROC:
	     return "JpegProc";
	  case TIFFTAG_JPEGIFOFFSET:
	     return "JpegIfOffset";
	  case TIFFTAG_JPEGIFBYTECOUNT:
	     return "JpegIfbytecount";
	  case TIFFTAG_JPEGRESTARTINTERVAL:
	     return "JpegRestartInterval";
	  case TIFFTAG_JPEGLOSSLESSPREDICTORS:
	     return "JpegLosslessPredictors";
	  case TIFFTAG_JPEGPOINTTRANSFORM:
	     return "JpegPointtransform";
	  case TIFFTAG_JPEGQTABLES:
	     return "JpegQTables";
	  case TIFFTAG_JPEGDCTABLES:
	     return "JpegDCTables";
	  case TIFFTAG_JPEGACTABLES:
	     return "JpegACTables";
	  case TIFFTAG_YCBCRCOEFFICIENTS:
	     return "YcbcrCoefficients";
	  case TIFFTAG_YCBCRSUBSAMPLING:
	     return "YcbcrSubSampling";
	  case TIFFTAG_YCBCRPOSITIONING:
	     return "YcbcrPositioning";
	  case TIFFTAG_REFERENCEBLACKWHITE:
	     return "ReferenceBlackWhite";
	  case TIFFTAG_MATTEING:
	     return "Matteing";
	  case TIFFTAG_DATATYPE:
	     return "Datatype";
	  case TIFFTAG_IMAGEDEPTH:
	     return "ImageDepth";
	  case TIFFTAG_TILEDEPTH:
	     return "TileDepth";
	  default:
	     return "unknown tag type...";
	}
   }
}
