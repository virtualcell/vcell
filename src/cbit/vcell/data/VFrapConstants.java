/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.data;

import cbit.vcell.data.DataSymbol.DataSymbolType;

/**
 * vFrap and vCell name equivalences
 * Creation date: (09/14/2010)
 * 
 * @author: Dan Vasilescu
 */
public class VFrapConstants
{
	//menus
	public static final String ADD_VFRAP_DATASET_MENU = "Add Dataset (normal images) from vFrap";
	public static final String ADD_VFRAP_SPECIALS_MENU = "Add Special (computed) images from vFrap";
	public static final String ADD_ASSOCIATE_EXISTING_FD_MENU = "Associate with existing Field Data item";
	public static final String ADD_PSF_MENU = "Import Point Spread Function";
	public static final String ADD_IMAGE_FILE_MENU = "Import from .lsm file";
	public static final String ADD_COPY_FROM_BIOMODEL_MENU = "Copy from existing biomodel";
	public static final String DELETE_DATA_SYMBOL = "Delete data symbol";
	
	// importing related - the names of the special objects
    public static final String PREBLEACH_AVG_TAG = "prebleach_avg";
    public static final String POSTBLEACH_FIRST_TAG = "postbleach_first";
    public static final String POSTBLEACH_LAST_TAG = "postbleach_last";
    public static final String BLEACHED_MASK_TAG = "bleached_mask";
    public static final String CELL_MASK_TAG = "cell_mask";
    public static final String RING1_MASK_TAG = "ring1_mask";
    public static final String RING2_MASK_TAG = "ring2_mask";
    public static final String RING3_MASK_TAG = "ring3_mask";
    public static final String RING4_MASK_TAG = "ring4_mask";
    public static final String RING5_MASK_TAG = "ring5_mask";
    public static final String RING6_MASK_TAG = "ring6_mask";
    public static final String RING7_MASK_TAG = "ring7_mask";
    public static final String RING8_MASK_TAG = "ring8_mask";
    

	public static final String[] tokenNames = {
                                 PREBLEACH_AVG_TAG,
                                 POSTBLEACH_FIRST_TAG,
                                 POSTBLEACH_LAST_TAG,
                                 BLEACHED_MASK_TAG, 
                                 CELL_MASK_TAG, 
                                 RING1_MASK_TAG,
                                 RING2_MASK_TAG,
                                 RING3_MASK_TAG,
                                 RING4_MASK_TAG,
                                 RING5_MASK_TAG,
                                 RING6_MASK_TAG,
                                 RING7_MASK_TAG,
                                 RING8_MASK_TAG
                                 };

	
	
	public enum SymbolEquivalence {
		PREBLEACH_AVG		(PREBLEACH_AVG_TAG,		"PrebleachAvg",		DataSymbolType.VFRAP_PREBLEACH_AVG),
		POSTBLEACH_FIRST	(POSTBLEACH_FIRST_TAG,	"PostbleachFirst",	DataSymbolType.VFRAP_FIRST_POSTBLEACH),
		POSTBLEACH_LAST		(POSTBLEACH_LAST_TAG,	"PostbleachLast",	DataSymbolType.VFRAP_FIRST_POSTBLEACH),
		BLEACHED_MASK		(BLEACHED_MASK_TAG,		"BleachedMask",		DataSymbolType.VFRAP_ROI), 
		CELL_MASK			(CELL_MASK_TAG,			"CellMask",			DataSymbolType.VFRAP_ROI), 
		RING1_MASK			(RING1_MASK_TAG,		"Ring1Mask",		DataSymbolType.VFRAP_ROI),
		RING2_MASK			(RING2_MASK_TAG,		"Ring2Mask",		DataSymbolType.VFRAP_ROI),
		RING3_MASK			(RING3_MASK_TAG,		"Ring3Mask",		DataSymbolType.VFRAP_ROI),
		RING4_MASK			(RING4_MASK_TAG,		"Ring4Mask",		DataSymbolType.VFRAP_ROI),
		RING5_MASK			(RING5_MASK_TAG,		"Ring5Mask",		DataSymbolType.VFRAP_ROI),
		RING6_MASK			(RING6_MASK_TAG,		"Ring6Mask",		DataSymbolType.VFRAP_ROI),
		RING7_MASK			(RING7_MASK_TAG,		"Ring7Mask",		DataSymbolType.VFRAP_ROI),
		RING8_MASK			(RING8_MASK_TAG,		"Ring8Mask",		DataSymbolType.VFRAP_ROI);

		private final String tokenName;			// internal name as used in vFrap
		private final String symbolName;		// the name we give to the symbol
		private final DataSymbolType dataSymbolType;
		SymbolEquivalence(String tokenName, String symbolName, DataSymbolType dataSymbolType) {
			this.tokenName = tokenName;
			this.symbolName = symbolName;
			this.dataSymbolType = dataSymbolType;
		}
		public String getVFrapTokenName() {
			return tokenName;
		}
		public String getVFrapSymbolName() {
			return symbolName;
		}
		public DataSymbolType getDataSymbolType() {
			return dataSymbolType;
		}
	    public static DataSymbolType typeFromToken(String tokenName) {
	        for (SymbolEquivalence se : SymbolEquivalence.values()){
	        	if (se.getVFrapTokenName().equals(tokenName)){
	        		return se.getDataSymbolType();
	        	}
	        }
	        return DataSymbolType.UNKNOWN;
	    }
	    // TODO: not in use yet, see vFrapXmlHelper::CreateSaveVFrapDataSymbols()
	    public static String nameFromToken(String tokenName) {
	        for (SymbolEquivalence se : SymbolEquivalence.values()){
	        	if (se.getVFrapTokenName().equals(tokenName)){
	        		return se.getVFrapSymbolName();
	        	}
	        }
	        return tokenName;	// fallback nicely and use what we have
	    }
	}

//	public enum VFrapImageTypes {
//        PREBLEACH_AVG_TAG		("prebleach_avg",		"prebleach_avg"),
//        POSTBLEACH_FIRST_TAG	("postbleach_first",	"postbleach_first"),
//        POSTBLEACH_LAST_TAG		("postbleach_last",		"postbleach_last"),
//        BLEACHED_MASK_TAG		("bleached_mask",		"bleached_mask"), 
//        CELL_MASK_TAG			("cell_mask",			"cell_mask"), 
//        RING1_MASK_TAG			("ring1_mask",			"ring1_mask"),
//        RING2_MASK_TAG			("ring1_mask",			"ring1_mask"),
//        RING3_MASK_TAG			("ring1_mask",			"ring1_mask"),
//        RING4_MASK_TAG			("ring1_mask",			"ring1_mask"),
//        RING5_MASK_TAG			("ring1_mask",			"ring1_mask"),
//        RING6_MASK_TAG			("ring1_mask",			"ring1_mask"),
//        RING7_MASK_TAG			("ring1_mask",			"ring1_mask"),
//        RING8_MASK_TAG			("ring1_mask",			"ring1_mask");
//    private final String displayName;
//    private final String internalName; // DON'T Change
//    VFrapImageTypes(String displayName, String internalName) {
//        this.displayName = displayName;
//        this.internalName = internalName;
//    }
//	}



}
