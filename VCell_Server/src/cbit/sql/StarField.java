package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class StarField extends Field {
/**
 * StarField constructor comment.
 * @param columnName java.lang.String
 * @param sqlType java.lang.String
 * @param sqlConstraints java.lang.String
 */
public StarField(Table argTable) {
	super("*", null, null);
	setTableName(argTable.getTableName());
}
}
