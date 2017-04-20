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

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.vcell.geometry.*;
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class CurveTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_curve";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	//public final Field curveData 	= new Field("curveData",	"long raw",		"NOT NULL");
	public final Field curveData 	= new Field("curveData",	"CLOB",		"NOT NULL");
	public final Field filamentRef	= new Field("filamentRef",	"integer",		"NOT NULL "+FilamentTable.REF_TYPE+" ON DELETE CASCADE");
	
	private final Field fields[] = {curveData,filamentRef};
	
	public static final CurveTable table = new CurveTable();
/**
 * Insert the method's description here.
 * Creation date: (8/3/00 5:17:07 PM)
 */
public CurveTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 6:02:38 PM)
 * @return java.awt.Shape
 * @param pathIterator byte[]
 */
public static cbit.vcell.geometry.Curve decodeCurve(String encodedShape) throws Exception {
	cbit.vcell.geometry.Curve rCurve = null;
	try {
		org.vcell.util.CommentStringTokenizer st = new org.vcell.util.CommentStringTokenizer(encodedShape);
		String curveType = st.nextToken();
		Class classType = Class.forName(curveType);
		Object oCurve = classType.newInstance();
		String curveClosed = st.nextToken();
		if (oCurve instanceof ControlPointCurve) {
			cbit.vcell.geometry.ControlPointCurve cpc = (cbit.vcell.geometry.ControlPointCurve)oCurve;
			int cpCount = new Integer(st.nextToken()).intValue();
			for (int c = 0; c < cpCount; c += 1) {
				double x = new Double(st.nextToken()).doubleValue();
				double y = new Double(st.nextToken()).doubleValue();
				double z = new Double(st.nextToken()).doubleValue();
				cpc.appendControlPoint(new Coordinate(x, y, z));
			}
			rCurve = cpc;
		}
		if (rCurve == null) {
			throw new RuntimeException("Couldn't decode curve " + curveType);
		}
		rCurve.setClosed(curveClosed.equals("Closed"));
	} catch (Throwable e) {
		throw new Exception(e.toString());
	}
	return rCurve;
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 6:01:29 PM)
 * @param shape java.awt.Shape
 */
public static String encodeCurve(cbit.vcell.geometry.Curve curve) {
	StringBuffer encodedVals = new StringBuffer();
	encodedVals.append(curve.getClass().getName() + " ");//CurveType
	encodedVals.append(curve.isClosed()+" ");//Curve open or closed
	if (curve instanceof ControlPointCurve) {
		ControlPointCurve cpc = (ControlPointCurve) curve;
		encodedVals.append(cpc.getControlPointCount()+" ");//ControlPointCount
		for (int c = 0; c < cpc.getControlPointCount(); c += 1) {
			Coordinate cpcCoord = cpc.getControlPoint(c);
			encodedVals.append(cpcCoord.getX() + " " + cpcCoord.getY() + " "+cpcCoord.getZ()+" ");
		}
	} else{
		throw new RuntimeException("Can't encode curvetype "+curve.getClass().getName());
	}/*else {
		double[] segments = new double[6];
		java.awt.geom.PathIterator pi = shape.getPathIterator(null);
		encodedVals.append(pi.getWindingRule() + " ");
		while (!pi.isDone()) {
			int segType = pi.currentSegment(segments);
			encodedVals.append(segType + " ");
			switch (segType) {
				case java.awt.geom.PathIterator.SEG_MOVETO :
					encodedVals.append(segments[0] + " " + segments[1] + " ");
					break;
				case java.awt.geom.PathIterator.SEG_LINETO :
					encodedVals.append(segments[0] + " " + segments[1] + " ");
					break;
				case java.awt.geom.PathIterator.SEG_QUADTO :
					encodedVals.append(segments[0] + " " + segments[1] + segments[2] + " " + segments[3] + " ");
					break;
				case java.awt.geom.PathIterator.SEG_CUBICTO :
					encodedVals.append(segments[0] + " " + segments[1] + segments[2] + " " + segments[3] + segments[4] + " " + segments[5] + " ");
					break;
				case java.awt.geom.PathIterator.SEG_CLOSE :
					//No points
					break;
			}
			pi.next();
		}
	}*/
	return encodedVals.toString();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key,KeyValue filamentKey) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append("EMPTY_CLOB()" + ",");
	buffer.append(filamentKey + ")");
	
	return buffer.toString();
}
}
