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

import java.awt.Container;
import java.awt.Window;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Objects;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgrammingException;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCAssert;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:09:07 PM)
 * @author: Ion Moraru
 */
public abstract class AsynchClientTask {
	public final static int TASKTYPE_NONSWING_BLOCKING = 1;
	public final static int TASKTYPE_SWING_NONBLOCKING = 2;
	public final static int TASKTYPE_SWING_BLOCKING = 3;

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 7:18:33 PM)
 * @return boolean
 */
	private boolean bSkipIfAbort = true;
	private boolean bSkipIfCancel = true;
	private int taskType = TASKTYPE_NONSWING_BLOCKING;
	private String taskName = "";
	private ClientTaskStatusSupport clientTaskStatusSupport = null;
	private boolean bShowProgressPopup = true;
	//private AsynchClientTask[] followupTasks = null;
	
	/**
	 * defaults showPopup, skip if abort, skip if cancel to true
	 * @param name
	 * @param taskType
	 */
	public AsynchClientTask(String name, int taskType) {
		this(name, taskType, true, true, true);
	}
	
	/**
	 * defaults skip if abort, skip if cancel to true
	 * @param name
	 * @param taskType
	 * @param bShowPopup
	 */
	public AsynchClientTask(String name, int taskType, boolean bShowPopup) {
		this(name, taskType, bShowPopup, true, true);
	}
	
	/**
	 * defaults showPopup, skip if abort, skip if cancel to true
	 * @param name
	 * @param taskType
	 * @param skipIfAbort
	 * @param skipIfCancel
	 */
	public AsynchClientTask(String name, int taskType, boolean skipIfAbort, boolean skipIfCancel) {
		this(name, taskType, true, skipIfAbort, skipIfCancel);
	}
	
	public AsynchClientTask(String name, int taskType, boolean bShowPopup, boolean skipIfAbort, boolean skipIfCancel) {
		this.taskName = name;
		this.taskType = taskType;
		this.bShowProgressPopup = bShowPopup;
		this.bSkipIfAbort = skipIfAbort;
		this.bSkipIfCancel = skipIfCancel;
	}
	
	public final boolean skipIfAbort() {
		return bSkipIfAbort;
	}
	
	public ClientTaskStatusSupport getClientTaskStatusSupport() {
		return clientTaskStatusSupport;
	}
	
	/**
	 * fetch and type convert object from hash
	 * @param hashTable not null
	 * @param key name (not null)
	 * @param clzz required type, not null
	 * @param required throw exception if required and not present 
	 * @return object cast to correct type or possibly null if !required
	 * @throws ProgrammingException if key not of required type 
	 * @throws ProgrammingException required is true and key not present 
	 */
	@SuppressWarnings("unchecked")
	static protected <T> T fetch(Hashtable<String, Object> hashTable, String key, Class<T> clzz, boolean required) {
		Object obj = hashTable.get(key);
		if (obj != null) {
			Class<? extends Object> oclzz = obj.getClass();
			if (clzz.isAssignableFrom(oclzz)) {
				return (T) obj;
			}
			throw new ProgrammingException("object " + obj + " of type " + oclzz.getName() + " not instance of " + clzz.getName());
		}
		if (required) {
			throw new ProgrammingException("key " + key + " not found in async hashtable");
		}
		return null;
	}
	
	/**
	 * typesafe (verified) fetch of object from hash
	 * @param hashTable non null
	 * @param keyInfo non null
	 * @return object of correct type
	 */
	static protected Object fetch(Hashtable<String, Object> hashTable, KeyInfo keyInfo) {
		Object obj = hashTable.get(keyInfo.name);
		VCAssert.assertTrue(obj != null, "ClientTaskDispatcher failed to verify object");
		VCAssert.assertTrue(keyInfo.clzz.isAssignableFrom(obj.getClass()),"ClientTaskDispatcher failed to verify type");
		return  obj;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/31/2004 7:18:33 PM)
	 * @return boolean
	 */
	public boolean skipIfCancel(UserCancelException exc) {
		return bSkipIfCancel;
	}
	
	public final int getTaskType() {
		return taskType;
	}

	public final String getTaskName() {
		return taskName;
	}
	
	public abstract void run(Hashtable<String, Object> hashTable) throws Exception;

	public void setClientTaskStatusSupport(ClientTaskStatusSupport clientTaskStatusSupport) {
		this.clientTaskStatusSupport = clientTaskStatusSupport;
	}
	public boolean showProgressPopup() {
		return bShowProgressPopup;
	}
	
	/**
	 * set final window to be raised
	 * @param hashTable non null
	 * @param cw non null
	 * @throws ProgrammingException if more than one set in the same hash
	 */
	protected void setFinalWindow(Hashtable<String, Object> hashTable,ChildWindow cw) {
		Objects.requireNonNull(cw);
		ClientTaskDispatcher.setFinalWindow(hashTable,() -> cw.toFront( ) );
	}
	
	/**
	 * set final window to be raised
	 * @param hashTable non null
	 * @param cntr non null
	 * @throws ProgrammingException if more than one set in the same hash
	 */
	protected void setFinalWindow(Hashtable<String, Object> hashTable,Container cntr) {
		Objects.requireNonNull(cntr);
		Window w= GuiUtils.getWindowForComponent(cntr);
		if (w != null) {
			ClientTaskDispatcher.setFinalWindow(hashTable,() -> w.toFront( ) );
			return;
		}
		throw new ProgrammingException("Container " + cntr.getName() + " has no window parent");
	}
	
	/**
	 * return list of keys need by task
	 * @return non-null Collection
	 */
	protected Collection<KeyInfo> requiredKeys( ) {
		return Collections.emptyList();
	}
	
	/**
	 * required hash key name and type
	 */
	public static final class KeyInfo {
		public final String name;
		final Class<?> clzz;
		/**
		 * @param name non null
		 * @param clzz non null
		 */
		protected KeyInfo(String name, Class<?> clzz) {
			this.name = name;
			this.clzz = clzz;
			Objects.requireNonNull(name);
			Objects.requireNonNull(clzz);
		}
	}
	
	
}
