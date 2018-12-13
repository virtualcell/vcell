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

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.Hashtable;

import org.vcell.model.rbm.gui.NetworkConstraintsPanel;
import org.vcell.model.rbm.gui.ValidateConstraintsPanel;
import org.vcell.util.ClientTaskStatusSupport;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.server.bionetgen.BNGExecutorService;

public class ReturnBNGOutput extends AsynchClientTask {
	private static final String message = "Return BioNetGen output to requester...";
	
	private final BNGExecutorService bngService;
	private SimulationContext sc;
	private NetworkConstraintsPanel owner;
	
	public ReturnBNGOutput(BNGExecutorService bngService) {
		this(bngService, null, null);
	}
	public ReturnBNGOutput(BNGExecutorService bngService, SimulationContext sc, NetworkConstraintsPanel owner) {
		super(message, TASKTYPE_SWING_NONBLOCKING, true, true);
		this.bngService = bngService;
		this.sc = sc;
		this.owner = owner;
	}

public void run(Hashtable<String, Object> hashTable) throws Exception {
	if(!bngService.isStopped()) {
		broadcastRun();
	}
	BNGOutputSpec outputSpec = (BNGOutputSpec)hashTable.get("outputSpec");
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		if(callback == owner) {
			// if it's called from the Edit / Test Constraints button we'll do it only after validation
			continue;
		}
		callback.updateBioNetGenOutput(outputSpec);
	}
	long endTime = System.currentTimeMillis();
	long delta = endTime - bngService.getStartTime();
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		if(callback == owner) {
			continue;
		}
		String t = new DecimalFormat("#.#").format((double)delta/1000);
		String s = "Total run time: " + t + " s.";
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, s);
		callback.setNewCallbackMessage(tcm);
		tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskEnd, "");
		callback.setNewCallbackMessage(tcm);
	}
	
	// asking the user to validate the new constraints we just tested; done only once 
	if(sc!=null && owner != null) {
		String t = new DecimalFormat("#.#").format((double)delta/1000);
		String s = "Total run time: " + t + " s.";
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, s);
		owner.setNewCallbackMessage(tcm);
		if(sc.getNetworkConstraints().isTestConstraintsDifferent()) {
			// only if they changed
			tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskEndNotificationOnly, "");
			owner.setNewCallbackMessage(tcm);
			owner.updateLimitExceededWarnings(outputSpec);
			validateConstraints(outputSpec);
		} else {
			owner.updateLimitExceededWarnings(outputSpec);
			owner.updateOutputSpecToSimulationContext(outputSpec);
			tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskEnd, "");
			owner.setNewCallbackMessage(tcm);
			String string = "The Network constraints are unchanged.";
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, string);
			sc.firePropertyChange("appendToConsole", "", tcm);
			if(owner.getIssueManager() != null) {
				owner.getIssueManager().setDirty();
			}
			owner.refreshInterface();
		}
	}
//	BNGOutputFileParser.printBNGNetOutput(outputSpec);			// prints all output to console
}

private void validateConstraints(BNGOutputSpec outputSpec) {
	ValidateConstraintsPanel panel = new ValidateConstraintsPanel(owner);
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(owner);
	ChildWindow childWindow = childWindowManager.addChildWindow(panel, panel, "Apply the new constraints?");
	Dimension dim = new Dimension(380, 210);
	childWindow.pack();
	panel.setChildWindow(childWindow);
	childWindow.setPreferredSize(dim);
	childWindow.showModal();
	if(panel.getButtonPushed() == ValidateConstraintsPanel.ActionButtons.Apply) {
		System.out.println("pressed APPLY from task");
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskEndAdjustSimulationContextFlagsOnly, "");
		owner.setNewCallbackMessage(tcm);
		String string = "Updating the network constraints with the test values.";
		System.out.println(string);
		sc.getNetworkConstraints().updateConstraintsFromTest();
		owner.updateOutputSpecToSimulationContext(outputSpec);
		tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, string);
		sc.firePropertyChange("appendToConsole", "", tcm);
		return;
	} else {
		System.out.println("pressed CANCEL from task");
		owner.updateOutputSpecToSimulationContext(null);

		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
		sc.appendToConsole(tcm);
		String string = "The Network constraints were not updated with the test values.";
		tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, string);
		sc.firePropertyChange("appendToConsole", "", tcm);
//		owner.refreshInterface();
		return;
	}
}

@Override
public ClientTaskStatusSupport getClientTaskStatusSupport() {
	return super.getClientTaskStatusSupport();
}

@Override
public void setClientTaskStatusSupport(ClientTaskStatusSupport clientTaskStatusSupport) {
	super.setClientTaskStatusSupport(clientTaskStatusSupport);
}

private void broadcastRun() {
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskStart, message);
		callback.setNewCallbackMessage(tcm);
	}
}

}
