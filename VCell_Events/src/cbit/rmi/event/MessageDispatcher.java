package cbit.rmi.event;

import cbit.rmi.event.PerformanceMonitorSender;
import cbit.vcell.export.ExportSender;
import cbit.vcell.simdata.DataJobSender;
import cbit.vcell.util.events.MessageListener;
import cbit.vcell.util.events.MessageSender;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 9:31:08 PM)
 * @author: IIM
 */
public interface MessageDispatcher extends MessageListener, MessageSender, WorkerEventSender, SimulationJobStatusSender, ExportSender, PerformanceMonitorSender ,DataJobSender, VCellMessageEventSender {}