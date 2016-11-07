/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;
import javax.swing.filechooser.FileFilter;
/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:10:05 PM)
 * @author: Anuradha Lakshminarayana
 */
public interface FileFilters {
	//public static final FileFilter FILE_FILTER_RTF 		= new ExtensionFilter(".rtf",	"RTF Files (*.rtf)");
	public static final FileFilter FILE_FILTER_PDF 		= new ExtensionFilter(".pdf",	"Report (*.pdf)");
	//public static final FileFilter FILE_FILTER_HTM 		= new ExtensionFilter(".htm",	"HTML Files (*.htm)");
	public static final FileFilter FILE_FILTER_MATLABV6 = new ExtensionFilter(".m",		"Matlab v6.0 ode function (*.m)");
	public static final FileFilter FILE_FILTER_CSV 		= new ExtensionFilter(".csv",	"CSV Files (*.csv)");
	public static final FileFilter FILE_FILTER_MOV 		= new ExtensionFilter(".mov",	"MOV Files (*.mov)");
	public static final FileFilter FILE_FILTER_GIF 		= new ExtensionFilter(".gif",	"GIF Files (*.gif)");
	public static final FileFilter FILE_FILTER_JPEG 		= new ExtensionFilter(".jpeg",	"JPEG Files (*.jpeg)");
	public static final FileFilter FILE_FILTER_NRRD 	= new ExtensionFilter(".nrrd", 	"NRRD Files (*.nrrd)");
	public static final FileFilter FILE_FILTER_ZIP 		= new ExtensionFilter(".zip", 	"ZIP Files (*.zip)");
//	public static final FileFilter FILE_FILTER_XML 		= new ExtensionFilter(new String[] {".xml",".vcml",".sbml", ".cellml", ".vfrap"}, "XML Files (.xml .vcml .sbml .cellml .vfrap)");
	public static final FileFilter FILE_FILTER_XML 		= new ExtensionFilter(new String[] {".xml",".vcml",".sbml", ".vfrap"}, "XML Files (.xml .vcml .sbml .vfrap)");
	public static final FileFilter FILE_FILTER_VCML		= new ExtensionFilter(new String[] {".vcml"},"VCML format (.vcml)");
	public static final FileFilter FILE_FILTER_SBML_12	= new ExtensionFilter(new String[] {".xml",".sbml"},"SBML format <Level1,Version2>  (.xml .sbml)");
	public static final FileFilter FILE_FILTER_SBML_21	= new ExtensionFilter(new String[] {".xml",".sbml"},"SBML format <Level2,Version1>  (.xml .sbml)");
	public static final FileFilter FILE_FILTER_SBML_22	= new ExtensionFilter(new String[] {".xml",".sbml"},"SBML format <Level2,Version2>  (.xml .sbml)");
	public static final FileFilter FILE_FILTER_SBML_23	= new ExtensionFilter(new String[] {".xml",".sbml"},"SBML format <Level2,Version3>  (.xml .sbml)");
	public static final FileFilter FILE_FILTER_SBML_24	= new ExtensionFilter(new String[] {".xml",".sbml"},"SBML format <Level2,Version4>  (.xml .sbml)");
	public static final FileFilter FILE_FILTER_SBML_31_CORE = new ExtensionFilter(new String[] {".xml",".sbml"},"SBML format <Level3,Version1> Core (.xml .sbml)");
	public static final FileFilter FILE_FILTER_SBML_31_SPATIAL = new ExtensionFilter(new String[] {".xml",".sbml"},"SBML format <Level3,Version1> Spatial <Version1> (.xml .sbml)");
	public static final FileFilter FILE_FILTER_SEDML    = new ExtensionFilter(new String[] {".xml",".sedml","sedx"},"SEDML format <Level1,Version2>  (.xml .sedml .sedx(archive))");
	public static final FileFilter FILE_FILTER_CELLML	= new ExtensionFilter(new String[] {".xml",".cellml"},	"CELLML format (*.xml .cellml)");
	public static final FileFilter FILE_FILTER_SMOLDYN_INPUT = new ExtensionFilter(new String[] {".smoldynInput", ".txt"},"Smoldyn Input Files (.smoldynInput .txt)");
	public static final FileFilter FILE_FILTER_AVS		= new ExtensionFilter(".avs", 	"AVS Unstructured Cell Data (*.avs)");
	public static final FileFilter FILE_FILTER_STL		= new ExtensionFilter(".stl", 	"Stereolithography (STL) file (*.stl)");
	public static final FileFilter FILE_FILTER_PLY		= new ExtensionFilter(".ply", 	"Stanford Polygon Format(*.ply)");
	public static final FileFilter FILE_FILTER_BNGL		= new ExtensionFilter(".bngl", 	"BioNetGen (BNGL) file (*.bngl)");
	public static final FileFilter FILE_FILTER_GEOMIMAGES = new ExtensionFilter(new String[] {".gif", ".tif", ".tiff", ".zip"},"images (.gif .tif .tiff .zip)");
	public static final FileFilter FILE_FILTER_FIELDIMAGES = new ExtensionFilter(new String[] {".gif", ".tif", ".tiff", ".zip", ".lsm"},"images/datasets (.gif .tif .tiff .zip .lsm)");
}
