/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.atoms;

import java.io.DataOutputStream;
import java.io.IOException;
/**
 * This type was created in VisualAge.
 */
public class BaseMediaInformation extends MediaInformation {

	protected BaseMediaHeader baseMediaHeader;
	protected HandlerReference handlerReference;
	protected DataInformation dataInformation;
	protected SampleTable sampleTable;
	
/**
 * This method was created in VisualAge.
 * @param vmhd VideoMediaInformationHeader
 * @param hdlr HandlerReference
 * @param dinf DataInformation
 * @param stbl SampleTable
 */
public BaseMediaInformation(BaseMediaHeader gmhd, HandlerReference hdlr, DataInformation dinf, SampleTable stbl) {
	baseMediaHeader = gmhd;
	handlerReference = hdlr;
	dataInformation = dinf;
	sampleTable = stbl;
	size = 8 + gmhd.size + hdlr.size;
	if (dinf != null) size += dinf.size;
	if (stbl != null) size += stbl.size;
}


/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		baseMediaHeader.writeData(out);
		handlerReference.writeData(out);
		if (dataInformation != null) dataInformation.writeData(out);
		if (sampleTable != null) sampleTable.writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
