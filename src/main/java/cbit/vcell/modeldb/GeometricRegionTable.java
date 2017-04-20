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

import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.units.VCUnitSystem;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class GeometricRegionTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_geometricregion";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";
	public static final int TYPE_VOLUME = 0;
	public static final int TYPE_SURFACE = 1;
	public static final String VOLUME1_NAME_COLUMN = "volume1Name";
	public static final String VOLUME2_NAME_COLUMN = "volume2Name";

	public final Field name			= new Field("name",				"VARCHAR(255)",	"NOT NULL");
	public final Field type			= new Field("regiontype",		"integer",		"NOT NULL");
	public final Field geometryRef	= new Field("geometryRef",		"integer",		"NOT NULL "+GeometryTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field size			= new Field("regionSize",		"NUMBER",		"NOT NULL");
	public final Field sizeUnit		= new Field("sizeUnit",			"VARCHAR(50)",	"NOT NULL");
	//
	// for membraneGeometryRegions
	//
	public final Field volRegion1	= new Field("volRegion1",		"integer",		GeometricRegionTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field volRegion2	= new Field("volRegion2",		"integer",		GeometricRegionTable.REF_TYPE+" ON DELETE CASCADE");
	//
	// for volumeGeometryRegions:
	//
	public final Field regionID		= new Field("regionID",			"integer",		"");
	public final Field subVolumeRef	= new Field("subvolumeRef",		"integer",		SubVolumeTable.REF_TYPE+" ON DELETE CASCADE");
	
	private final Field fields[] = {name,type,geometryRef,size,sizeUnit,volRegion1,volRegion2,regionID,subVolumeRef};
	
	public static final GeometricRegionTable table = new GeometricRegionTable();
/**
 * ModelTable constructor comment.
 */
private GeometricRegionTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, SurfaceGeometricRegion surfaceRegion, KeyValue volRegion1Key, KeyValue volRegion2Key, KeyValue geomKey) throws DataAccessException {
	StringBuffer buffer = new StringBuffer();
	
	if (volRegion1Key == null){
		throw new IllegalArgumentException("can't save surfaceGeometryRegion, volume region neighbor 1 key is null");
	}
	if (volRegion2Key == null){
		throw new IllegalArgumentException("can't save surfaceGeometryRegion, volume region neighbor 2 key is null");
	}
	
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append("'" + surfaceRegion.getName() + "',");
	buffer.append(TYPE_SURFACE + ",");
	buffer.append(geomKey + ",");
	buffer.append(surfaceRegion.getSize() + ",");
	buffer.append("'" + surfaceRegion.getSizeUnit().getSymbol() + "',");
	buffer.append(volRegion1Key + ",");
	buffer.append(volRegion2Key + ",");
	buffer.append("NULL,");
	buffer.append("NULL");
	buffer.append(")");
	
	return buffer.toString();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, VolumeGeometricRegion volumeRegion, KeyValue subvolumeKey, KeyValue geomKey) throws DataAccessException {
	StringBuffer buffer = new StringBuffer();

	if (subvolumeKey == null){
		throw new IllegalArgumentException("can't save volumeGeometryRegion, subvolume key is null");
	}
	
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append("'" + volumeRegion.getName() + "',");
	buffer.append(TYPE_VOLUME + ",");
	buffer.append(geomKey + ",");
	buffer.append(volumeRegion.getSize() + ",");
	buffer.append("'" + volumeRegion.getSizeUnit().getSymbol() + "',");
	buffer.append("NULL,");
	buffer.append("NULL,");
	buffer.append(volumeRegion.getRegionID() + ",");
	buffer.append(subvolumeKey + ")");
	
	return buffer.toString();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
public SurfaceGeometricRegion getSurfaceRegion(java.sql.ResultSet rset, VolumeGeometricRegion volumeRegions[], VCUnitSystem unitSystem, SessionLog log) throws java.sql.SQLException, DataAccessException {

	String _name = rset.getString(this.name.toString());
	double _size = rset.getBigDecimal(this.size.toString()).doubleValue();
	String _sizeUnitSymbol = rset.getString(this.sizeUnit.toString());
	cbit.vcell.units.VCUnitDefinition _sizeUnit = unitSystem.getInstance(_sizeUnitSymbol);

	//
	// get neighboring regions
	//
	String _volRegion1Name = rset.getString(VOLUME1_NAME_COLUMN);
	String _volRegion2Name = rset.getString(VOLUME2_NAME_COLUMN);
	VolumeGeometricRegion _volumeRegion1 = null;
	VolumeGeometricRegion _volumeRegion2 = null;
	for (int i = 0; i < volumeRegions.length; i++){
		if (volumeRegions[i].getName().equals(_volRegion1Name)){
			_volumeRegion1 = volumeRegions[i];
		}
		if (volumeRegions[i].getName().equals(_volRegion2Name)){
			_volumeRegion2 = volumeRegions[i];
		}
	}

		
	SurfaceGeometricRegion surfaceRegion = new SurfaceGeometricRegion(_name,_size,_sizeUnit);
	
	surfaceRegion.addAdjacentGeometricRegion(_volumeRegion1);
	_volumeRegion1.addAdjacentGeometricRegion(surfaceRegion);

	surfaceRegion.addAdjacentGeometricRegion(_volumeRegion2);
	_volumeRegion2.addAdjacentGeometricRegion(surfaceRegion);
	
	return surfaceRegion;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
public VolumeGeometricRegion getVolumeRegion(java.sql.ResultSet rset, cbit.vcell.geometry.Geometry geometry, SessionLog log) throws java.sql.SQLException, DataAccessException {
	String _name = rset.getString(this.name.toString());
	double _size = rset.getBigDecimal(this.size.toString()).doubleValue();
	String _sizeUnitSymbol = rset.getString(this.sizeUnit.toString());
	cbit.vcell.units.VCUnitDefinition _sizeUnit = geometry.getUnitSystem().getInstance(_sizeUnitSymbol);

	int _regionID = rset.getInt(this.regionID.toString());
	KeyValue _subvolumeKey = new KeyValue(rset.getBigDecimal(this.subVolumeRef.toString()));
	
	//
	// find subvolume with correct key
	//
	cbit.vcell.geometry.SubVolume _subVolume = null;
	cbit.vcell.geometry.SubVolume subvolumes[] = geometry.getGeometrySpec().getSubVolumes();
	for (int i = 0; i < subvolumes.length; i++){
		if (subvolumes[i].getKey().compareEqual(_subvolumeKey)){
			_subVolume = subvolumes[i];
			break;
		}
	}

	VolumeGeometricRegion volumeRegion = new VolumeGeometricRegion(_name,_size,_sizeUnit,_subVolume,_regionID);
	
	return volumeRegion;
}
}
