/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.db;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.message.server.ServiceSpec;
import cbit.vcell.message.server.ServiceSpec.ServiceStartupType;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.message.server.ServiceStatus;
import cbit.vcell.message.server.ServiceStatus.ServiceStatusType;
import cbit.vcell.message.server.htc.HtcJobID;

public class ServiceTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_service";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	private static final String[] serverIDAndTypeAndOrdinalUniqueConstraint =
		new String[] {
		"service_sid_type_ord_unique UNIQUE(serverID,type,ordinal)"};
	
	public final Field serverID	= new Field("serverID",	"varchar(20)",	"NOT NULL");
	public final Field type		= new Field("type",	"varchar(64)",	"NOT NULL");
	public final Field ordinal	= new Field("ordinal",	"integer",	"NOT NULL");
	public final Field startupType = new Field("startupType", "integer", "NOT NULL");
	public final Field memoryMB	= new Field("memoryMB",	"integer",	"NOT NULL");	
	public final Field date		= new Field("lastUpdate", "date",	"NOT NULL");
	public final Field status	= new Field("status", "integer", "NOT NULL");
	public final Field statusMsg = new Field("statusMsg", "varchar(2048)",	"NOT NULL");
	public final Field pbsjobid		= new Field("pbsjobid",	"varchar(128)",	"");
	
	private final Field fields[] = {serverID, type, ordinal, startupType, memoryMB, date, status, statusMsg, pbsjobid};
	
	public static final ServiceTable table = new ServiceTable();

/**
 * ModelTable constructor comment.
 */
private ServiceTable() {
	super(TABLE_NAME, serverIDAndTypeAndOrdinalUniqueConstraint);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return VCImage
 * @param rset ResultSet
 * @param log SessionLog
 */
public ServiceStatus getServiceStatus(ResultSet rset) throws SQLException {	
	//serverID
	String parsedServerIDString = rset.getString(serverID.toString());
	VCellServerID parsedServerID = VCellServerID.getServerID(parsedServerIDString);
	//type
	String parsedType = rset.getString(type.toString());
	//ordinal
	int parsedOrdinal = rset.getInt(ordinal.toString());
	//startupType
	int parsedStartupType = rset.getInt(startupType.toString());
	//memoryMB
	int parsedMemoryMB = rset.getInt(memoryMB.toString());
	//date
	java.util.Date parsedDate = rset.getTimestamp(date.toString());
	if (rset.wasNull()) {
		parsedDate = null;
	}
	//status
	int parsedStatus = rset.getInt(status.toString());
	//statusMsg
	String parsedStatusMsg = rset.getString(statusMsg.toString());
	if (rset.wasNull()) {
		parsedStatusMsg = null;
	}
	//host
	HtcJobID parsedHtcJobId = null;
	String parsedHtcJobDatabaseString = rset.getString(pbsjobid.toString());
	if (!rset.wasNull() && parsedHtcJobDatabaseString!=null && parsedHtcJobDatabaseString.length()>0) {
		parsedHtcJobId = HtcJobID.fromDatabase(parsedHtcJobDatabaseString);
	}
	ServiceStatus serviceStatus = new ServiceStatus(new ServiceSpec(parsedServerID, ServiceType.fromName(parsedType), parsedOrdinal, ServiceStartupType.fromDatabaseNumber(parsedStartupType), parsedMemoryMB), 
			parsedDate, ServiceStatusType.fromDatabaseNumber(parsedStatus), parsedStatusMsg, parsedHtcJobId);
	
	return serviceStatus;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLUpdateList(ServiceStatus serviceStatus){

	StringBuffer buffer = new StringBuffer();

	//serverID
	buffer.append(serverID + "='" + serviceStatus.getServiceSpec().getServerID() + "',");
	//type
	buffer.append(type + "='" + serviceStatus.getServiceSpec().getType() + "',");
	//ordinal
	buffer.append(ordinal + "=" + serviceStatus.getServiceSpec().getOrdinal() + ",");
	//startupType
	buffer.append(startupType + "=" + serviceStatus.getServiceSpec().getStartupType().getDatabaseNumber() + ",");
	//memory
	buffer.append(memoryMB + "=" + serviceStatus.getServiceSpec().getMemoryMB() + ",");
	//date
	buffer.append(date + "=sysdate,");
	//status
	buffer.append(status + "=" + serviceStatus.getStatus().getDatabaseNumber() + ",");
	//statusMsg
	buffer.append(statusMsg + "='" + serviceStatus.getStatusMsg() + "',");
	//host
	buffer.append(pbsjobid + "=");
	if (serviceStatus.getHtcJobId() != null){
		buffer.append("'" + serviceStatus.getHtcJobId().toDatabase() + "'");
	} else {
		buffer.append("null");
	}
	
	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, ServiceStatus serviceStatus) {

	StringBuffer buffer = new StringBuffer();
	
	buffer.append("(");
	buffer.append(key + ",");
	//serverID
	buffer.append("'" + serviceStatus.getServiceSpec().getServerID() + "',");
	//type
	buffer.append("'" + serviceStatus.getServiceSpec().getType() + "',");
	//ordinal
	buffer.append(serviceStatus.getServiceSpec().getOrdinal() + ",");
	//startupType
	buffer.append(serviceStatus.getServiceSpec().getStartupType().getDatabaseNumber() + ",");
	//memory
	buffer.append(serviceStatus.getServiceSpec().getMemoryMB() + ",");
	//date
	buffer.append("sysdate,");
	//status
	buffer.append(serviceStatus.getStatus().getDatabaseNumber() + ",");
	//statusMsg
	buffer.append("'" + serviceStatus.getStatusMsg() + "',");
	//host
	if (serviceStatus.getHtcJobId() != null){
		buffer.append("'" + serviceStatus.getHtcJobId().toDatabase() + "'");
	} else {
		buffer.append("null");
	}
	buffer.append(")");
	
	return buffer.toString();
}
}
