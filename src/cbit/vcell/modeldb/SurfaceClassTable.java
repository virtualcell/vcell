/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import java.util.Iterator;
import java.util.Set;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.Table;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;

public class SurfaceClassTable extends Table {
	private static final String TABLE_NAME = "vc_surfaceclass";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field name		 		= new Field("name",				"varchar(255)",	"NOT NULL");
	public final Field geometryRef		= new Field("geometryRef",		"integer",		"NOT NULL "+GeometryTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field subVolumeRef1	= new Field("subVolumeRef1",	"integer",	SubVolumeTable.REF_TYPE);
	public final Field subVolumeRef2	= new Field("subVolumeRef2",	"integer",	SubVolumeTable.REF_TYPE);
	
	private final Field fields[] = {name,geometryRef,subVolumeRef1,subVolumeRef2};
	
	public static final SurfaceClassTable table = new SurfaceClassTable();
/**
 * ModelTable constructor comment.
 */
private SurfaceClassTable() {
	super(TABLE_NAME);
	addFields(fields);
}

public String getSQLValueList(InsertHashtable hash, KeyValue key, Geometry geom, SurfaceClass surfaceClass,KeyValue geomKey) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append("'" + surfaceClass.getName() + "',");
	buffer.append(geomKey + ",");
	Set<SubVolume> subvolumeSet = surfaceClass.getAdjacentSubvolumes();
	Iterator<SubVolume> subvolIter = subvolumeSet.iterator();
	KeyValue subVolRef1 = null;
	KeyValue subVolRef2 = null;
	if(subvolIter.hasNext()){
		SubVolume subVolume = subvolIter.next();
		subVolRef1 = hash.getDatabaseKey(subVolume);
		if(subVolRef1 == null){
			subVolRef1= subVolume.getKey();
		}
	}
	if(subvolIter.hasNext()){
		SubVolume subVolume = subvolIter.next();
		subVolRef2 = hash.getDatabaseKey(subVolume);
		if(subVolRef2 == null){
			subVolRef2= subVolume.getKey();
		}
	}
	buffer.append(subVolRef1 + ",");
	buffer.append(subVolRef2);

	buffer.append(")");
	
	return buffer.toString();
}
}
