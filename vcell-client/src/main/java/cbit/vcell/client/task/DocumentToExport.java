/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;

import java.util.Hashtable;
import java.util.NoSuchElementException;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.GeometryInfo;

/**
 * Insert the type's description here. Creation date: (5/31/2004 6:03:16 PM)
 * 
 * @author: Ion Moraru
 */
public class DocumentToExport extends AsynchClientTask {
	/**
	 * key for hashtable
	 */
	public static final String EXPORT_DOCUMENT = "documentToExport";
	public DocumentToExport() {
		super("Fetching document to be exported", TASKTYPE_NONSWING_BLOCKING);
	}

	/**
	 * Insert the method's description here. Creation date: (5/31/2004 6:04:14
	 * PM)
	 * 
	 * @param hashTable
	 *            java.util.Hashtable
	 * @param clientWorker
	 *            cbit.vcell.desktop.controls.ClientWorker
	 */
	@Override
	public void run(Hashtable<String, Object> hashTable)
			throws java.lang.Exception {
		TopLevelWindowManager topLevelWindowManager = extractRequired(hashTable, TopLevelWindowManager.class,"topLevelWindowManager");
		VCDocument doc = null;
		if (topLevelWindowManager instanceof DocumentWindowManager) {
			doc = ((DocumentWindowManager) topLevelWindowManager)
					.getVCDocument();
		} else if (topLevelWindowManager instanceof DatabaseWindowManager) {
			DocumentManager documentManager = extractRequired(hashTable, DocumentManager.class,CommonTask.DOCUMENT_MANAGER.name);
			VCDocumentInfo documentInfo = ((DatabaseWindowManager) topLevelWindowManager)
					.getPanelSelection();
			if (documentInfo instanceof BioModelInfo) {
				BioModelInfo bmi = (BioModelInfo) documentInfo;
				doc = documentManager.getBioModel(bmi);
			} else if (documentInfo instanceof MathModelInfo) {
				MathModelInfo mmi = (MathModelInfo) documentInfo;
				doc = documentManager.getMathModel(mmi);
			} else if (documentInfo instanceof GeometryInfo) {
				GeometryInfo gmi = (GeometryInfo) documentInfo;
				doc = documentManager.getGeometry(gmi);
			}
			if (doc == null) {
				throw new IllegalStateException("export called on DatabaseWindowManager with selection " + documentInfo);
			}
		}
		if (doc != null) {
			hashTable.put(EXPORT_DOCUMENT, doc);
		} else {
			throw new UnsupportedOperationException(
					"TopLevelWindowManager subclass "
							+ topLevelWindowManager.getClass().getName()
							+ " does not support exporting to document");
		}
	}
	
	/**
	 * extract required element from hashtable
	 * @param ht source to search
	 * @param clzz required type
	 * @param name 
	 * @return T
	 * @throws NoSuchElementException if not present or of wrong type
	 */
	@SuppressWarnings("unchecked")
	private static <T> T extractRequired(Hashtable<String,Object> ht, Class<?> clzz, String name){
		Object o = ht.get(name);
		if (o != null) {
			if (clzz.isAssignableFrom(o.getClass( ))) {
				return (T)o;
			}
			throw new NoSuchElementException("item " + name + " type " + o.getClass().getName( )
					+ " not of required type " + clzz.getName( ));
		}
		throw new NoSuchElementException("requred item " + name + " not present"); 
	}
}