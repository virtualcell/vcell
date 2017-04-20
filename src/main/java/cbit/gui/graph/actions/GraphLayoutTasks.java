/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.actions;

import java.awt.Component;
import java.util.Hashtable;

import cbit.gui.graph.GraphLayoutManager;
import cbit.gui.graph.GraphView;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

public class GraphLayoutTasks {
	
	public static class PerformLayoutTask extends AsynchClientTask {

		private static final int TASKTYPE = AsynchClientTask.TASKTYPE_NONSWING_BLOCKING;
		private static final boolean SKIP_IF_CANCEL = true;
		private static final boolean SKIP_IF_ABORT = true;
		private static final boolean B_SHOW_POPUP = true;
		
		protected final GraphLayoutManager manager;
		protected final GraphView graphView;
		protected final GraphLayoutManager.VCellTaskClient client;
		
		public PerformLayoutTask(GraphLayoutManager manager, GraphView graphView, String layoutName) {
			super(createTaskName(layoutName), TASKTYPE, B_SHOW_POPUP, SKIP_IF_ABORT, SKIP_IF_CANCEL);
			this.manager = manager;
			this.graphView = graphView;
			this.client = new GraphLayoutManager.VCellTaskClient(graphView, layoutName, this);
		}

		public static String createTaskName(String layoutName) {
			return "Performing Graph Layout " + layoutName;
		}
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			manager.layout(client);
			graphView.saveNodePositions();
		}
		
	}
	
	public static class ShowLayoutTask extends AsynchClientTask {

		private static final String TASKNAME = "Repainting Graph";
		private static final int TASKTYPE = AsynchClientTask.TASKTYPE_SWING_BLOCKING;
		private static final boolean SKIP_IF_CANCEL = false;
		private static final boolean SKIP_IF_ABORT = false;
		private static final boolean B_SHOW_POPUP = false;
		
		protected GraphView graphView;
		
		public ShowLayoutTask(GraphView graphView) {
			super(TASKNAME, TASKTYPE, B_SHOW_POPUP, SKIP_IF_ABORT, SKIP_IF_CANCEL);
			this.graphView = graphView;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			graphView.repaint();
		}
		
	}
	
	public static AsynchClientTask[] getTaskList(GraphLayoutManager manager, GraphView graphView,
			String layoutName) {
		return new AsynchClientTask[] {
				new PerformLayoutTask(manager, graphView, layoutName), 
				new ShowLayoutTask(graphView)
		};
	}
	
	public static void dispatchTasks(final Component requester, GraphLayoutManager manager, 
			GraphView graphView, String layoutName) {
		ClientTaskDispatcher.dispatch(requester, new Hashtable<String, Object>(), 
				getTaskList(manager, graphView, layoutName), true, true, true, null, true);
	}
	
}
