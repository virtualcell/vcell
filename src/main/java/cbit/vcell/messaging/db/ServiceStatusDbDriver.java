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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.message.server.ServiceStatus;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 8:54:31 AM)
 * @author: Fei Gao
 */
public class ServiceStatusDbDriver {
	private static final ServiceTable serviceTable = ServiceTable.table;

/**
 * LocalDBManager constructor comment.
 */
public ServiceStatusDbDriver() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2003 11:22:44 AM)
 * @param con java.sql.Connection
 * @param sql java.lang.String
 */
private int executeUpdate(Connection con, String sql) throws SQLException {
	Statement s = con.createStatement();
	try {
		return s.executeUpdate(sql);
	} finally {
		s.close();
	}	
}

public void insertServiceStatus(Connection con, ServiceStatus serviceStatus, KeyValue key) throws SQLException {
	if (serviceStatus == null){
		throw new IllegalArgumentException("simulationJobStatus cannot be null");
	}
	String sql = "INSERT INTO " + serviceTable.getTableName() + " " + serviceTable.getSQLColumnList() + " VALUES " 
		+ serviceTable.getSQLValueList(key, serviceStatus);

	//log.print(sql);			
	executeUpdate(con,sql);
}

public void deleteServiceStatus(Connection con, ServiceStatus serviceStatus, KeyValue key) throws SQLException {
	if (serviceStatus == null){
		throw new IllegalArgumentException("ServiceStatus cannot be null");
	}
	String sql = "delete from " + serviceTable.getTableName() + " where " + serviceTable.serverID + "='" + serviceStatus.getServiceSpec().getServerID() 
		+ "' and " + serviceTable.type + "='" + serviceStatus.getServiceSpec().getType() + "' and " + serviceTable.ordinal + "=" + serviceStatus.getServiceSpec().getOrdinal();

	//log.print(sql);			
	executeUpdate(con,sql);
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public ServiceStatus getServiceStatus(Connection con, VCellServerID serverID, ServiceType type, int ordinal, boolean lockRowForUpdate) throws SQLException {
	String sql = "select * from " + serviceTable.getTableName() + " where "	
		+ serviceTable.serverID.getQualifiedColName() + "='" + serverID + "'" 
		+ " AND " + serviceTable.type.getQualifiedColName() + "='" + type.getName() + "'"
		+ " AND " + serviceTable.ordinal.getQualifiedColName() + "=" + ordinal;
		
	if (lockRowForUpdate){
		sql += " FOR UPDATE OF " + serviceTable.getTableName() + ".id";
	}
	//log.print(sql);
	Statement stmt = con.createStatement();
	ServiceStatus serviceStatus = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			serviceStatus = serviceTable.getServiceStatus(rset);
		}
	} finally {
		stmt.close();
	}
	return serviceStatus;
}

public List<ServiceStatus> getAllServiceStatus(Connection con) throws SQLException {
	String sql = "select * from " + serviceTable.getTableName() + " where " + serviceTable.serverID + "='" + VCellServerID.getSystemServerID() + "'"
		+ " order by " + serviceTable.serverID + "," + serviceTable.type + "," + serviceTable.ordinal;
		
	List<ServiceStatus> services = new ArrayList<ServiceStatus>();
	Statement stmt = con.createStatement();
	ServiceStatus serviceStatus = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			serviceStatus = serviceTable.getServiceStatus(rset);
			services.add(serviceStatus);
		}
	} finally {
		stmt.close();
	}
	return services;
}


public void updateServiceStatus(Connection con, ServiceStatus serviceStatus) throws SQLException {
	if (serviceStatus == null || con == null){
		throw new IllegalArgumentException("Improper parameters for updateServiceStatus()");
	}
	
	String sql = "UPDATE " + serviceTable.getTableName() +	" SET "  + serviceTable.getSQLUpdateList(serviceStatus) + 
			" WHERE " + serviceTable.serverID + "='" + serviceStatus.getServiceSpec().getServerID() + "'" +
			" AND " + serviceTable.type + "='" + serviceStatus.getServiceSpec().getType() + "'" +
			" AND " + serviceTable.ordinal + "=" + serviceStatus.getServiceSpec().getOrdinal();			
	executeUpdate(con,sql);
}
}
