/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui.exporter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

import org.vcell.util.VCAssert;
/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:10:05 PM)
 * @author: Anuradha Lakshminarayana
 */
public class FileFilters {
	
	public static List<FileFilter> supports(SelectorExtensionFilter.Selector ... selectors) {
		VCAssert.assertValid(selectors);
		VCAssert.assertTrue(selectors.length > 0, "must provide at least one selector");
		List<SelectorExtensionFilter> first = map.get(selectors[0]);
		VCAssert.assertValid(first);
		ArrayList<FileFilter> rval = new ArrayList<>(); 
		rval.addAll(first); //copy first list
		for (int i = 1; i < selectors.length;i++) {
			List<SelectorExtensionFilter> additional = map.get(selectors[i]);
			rval.retainAll(additional); //filter by remaining 
		}
		return rval;
	}
	
	private static Map<SelectorExtensionFilter.Selector,List<SelectorExtensionFilter> > map;
	static {
		map = new HashMap<>( );
		//create array for each enum for simplicity; 
		for (SelectorExtensionFilter.Selector s : SelectorExtensionFilter.Selector.values()) {
			map.put(s, new ArrayList<SelectorExtensionFilter>( ));
		}
	}
	
	public static final FileFilter FILE_FILTER_EXE		= new ExtensionFilter(".exe", 	"Windows Executables (*.exe)");

	public static final FileFilter FILE_FILTER_AVS		= new ExtensionFilter(".avs", 	"AVS Unstructured Cell Data (*.avs)");
	public static final FileFilter FILE_FILTER_STL		= new ExtensionFilter(".stl", 	"Stereolithography (STL) file (*.stl)");
	public static final FileFilter FILE_FILTER_PLY		= new ExtensionFilter(".ply", 	"Stanford Polygon Format(*.ply)");
	public static final FileFilter FILE_FILTER_GEOMIMAGES = new ExtensionFilter(new String[] {".gif", ".tif", ".tiff", ".zip"},"images (.gif .tif .tiff .zip)");
	public static final FileFilter FILE_FILTER_FIELDIMAGES = new ExtensionFilter(new String[] {".gif", ".tif", ".tiff", ".zip", ".lsm"},"images/datasets (.gif .tif .tiff .zip .lsm)");
	
	public static final FileFilter FILE_FILTER_HDF5 		= new ExtensionFilter(new String[] {".hdf5",".hdf"},	"HDF5 Files (.hdf5 .hdf)");
	public static final FileFilter FILE_FILTER_CSV 		= new ExtensionFilter(".csv",	"CSV Files (*.csv)");
	public static final FileFilter FILE_FILTER_MOV 		= new ExtensionFilter(".mov",	"MOV Files (*.mov)");
	public static final FileFilter FILE_FILTER_GIF 		= new ExtensionFilter(".gif",	"GIF Files (*.gif)");
	public static final FileFilter FILE_FILTER_JPEG 		= new ExtensionFilter(".jpeg",	"JPEG Files (*.jpeg)");
	public static final FileFilter FILE_FILTER_NRRD 	= new ExtensionFilter(".nrrd", 	"NRRD Files (*.nrrd)");
	public static final FileFilter FILE_FILTER_ZIP 		= new ExtensionFilter(".zip", 	"ZIP Files (*.zip)");
	public static final FileFilter FILE_FILTER_EXTERNALDOC	= new ExtensionFilter(new String[] {".xml",".vcml",".sbml", ".cellml", ".vfrap", ".bngl", ".omex", ".sedx", ".sedml"}, "Model Formats (.xml .vcml .sbml .cellml .vfrap .bngl .omex .sedx .sedml)");
	public static final FileFilter FILE_FILTER_XML2 		= new ExtensionFilter(new String[] {".xml",".vcml",".sbml", ".cellml", ".vfrap"}, "XML Files (.xml .vcml .sbml .cellml .vfrap)");
	
	public static final FileFilter FILE_FILTER_VCML		= new VCMLExtensionFilter(); 
	public static final FileFilter FILE_FILTER_PDF 		= new PDFExtensionFilter(); 
	public static final FileFilter FILE_FILTER_MATLABV6 = new MatlabExtensionFilter(); 
//	public static final FileFilter FILE_FILTER_SBML_12	= new SbmlExtensionFilter(1, 2, false);
//	public static final FileFilter FILE_FILTER_SBML_21	= new SbmlExtensionFilter(2, 1, false);
//	public static final FileFilter FILE_FILTER_SBML_22	= new SbmlExtensionFilter(2, 2, false);
//	public static final FileFilter FILE_FILTER_SBML_23	= new SbmlExtensionFilter(2, 3, false);
//	public static final FileFilter FILE_FILTER_SBML_24	= new SbmlExtensionFilter(2, 4, false);
//	public static final FileFilter FILE_FILTER_SBML_31_CORE = new SbmlExtensionFilter(3, 1, false);
//	public static final FileFilter FILE_FILTER_SBML_31_SPATIAL = new SbmlExtensionFilter(3, 1, true);
	public static final FileFilter FILE_FILTER_SBML_32_CORE = new SbmlExtensionFilter(3, 2, false);
	public static final FileFilter FILE_FILTER_SBML_32_SPATIAL = new SbmlExtensionFilter(3, 2, true);
	public static final FileFilter FILE_FILTER_SEDML    = new SedmlExtensionFilter(); 
	public static final FileFilter FILE_FILTER_OMEX     = new OmexExtensionFilter();
	public static final FileFilter FILE_FILTER_CELLML	= new CellMLExtensionFilter(); 
	public static final FileFilter FILE_FILTER_SMOLDYN_INPUT = new SmoldynExtensionFilter(); 
	public static final FileFilter FILE_FILTER_NFSIM = new NfsimExtensionFilter(); 
	public static final ExtensionFilter FILE_FILTER_BNGL = new BnglExtensionFilter(); 
	
	static void register(SelectorExtensionFilter f) {
		if (f.selectors != null) {
			for (SelectorExtensionFilter.Selector s : f.selectors) {
				VCAssert.assertValid(map.get(s));
				map.get(s).add(f);
			}
		}
	}

	
	
	
}
