package cbit.vcell.mapping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import cbit.vcell.client.desktop.biomodel.SimulationConsolePanel;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;

/*
 *  we simplify the (sometimes complex, multirow, verbose) messages we receive from various places,
 *  break them at need into multiple simpler messages and we store them in the console notification list,
 *  in a simple, easy to display format
 * 
 */
public class TaskCallbackProcessor {
	
	private final SimulationContext sc;
	private ArrayList<TaskCallbackMessage> consoleNotificationList = new ArrayList<TaskCallbackMessage>();
	private int currentIterationSpecies = 0;
	private int previousIterationSpecies = 0;
	private boolean needAdjustIterations = false;
	
	public TaskCallbackProcessor(SimulationContext simulationContext) {
		this.sc = simulationContext;
	}

	public final ArrayList<TaskCallbackMessage> getConsoleNotificationList() {
		return consoleNotificationList;
	}
	public void appendToConsole(TaskCallbackMessage message) {
		if(message.getStatus() == TaskCallbackStatus.Clean) {
			consoleNotificationList.clear();	// clear console notification list
		}
		process(message);
	}
	public void playConsoleNotificationList() {
		for(TaskCallbackMessage message : consoleNotificationList) {
			sc.firePropertyChange("appendToConsole", "", message);
		}
	}
	
	private void process(TaskCallbackMessage newCallbackMessage) {
		TaskCallbackStatus status = newCallbackMessage.getStatus();
		String string = newCallbackMessage.getText();
//		System.out.println(string);
		try {
		switch(status) {
		case Clean:			// clean console, display task initialization message
			previousIterationSpecies = 0;
			currentIterationSpecies = 0;
			needAdjustIterations = false;
			TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
			consoleNotificationList.add(tcm);
			sc.firePropertyChange("appendToConsole", "", tcm);
			if(string != null && !string.isEmpty()) {
				tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, string);
				consoleNotificationList.add(tcm);
				sc.firePropertyChange("appendToConsole", "", tcm);
			}
			break;
		case TaskStart:			// display task initialization message
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, string);
			consoleNotificationList.add(tcm);
			sc.firePropertyChange("appendToConsole", "", tcm);
			break;
		case TaskEnd:
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, string);
			consoleNotificationList.add(tcm);
			sc.firePropertyChange("appendToConsole", "", tcm);
			if(previousIterationSpecies>0 && currentIterationSpecies>0 && currentIterationSpecies!=previousIterationSpecies) {
				String s = SimulationConsolePanel.getInsufficientIterationsMessage();
				tcm = new TaskCallbackMessage(TaskCallbackStatus.Warning, s);
				consoleNotificationList.add(tcm);
				sc.firePropertyChange("appendToConsole", "", tcm);
				sc.setInsufficientIterations(true);
			}
			break;
		case TaskStopped:		// by user
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, string);
			consoleNotificationList.add(tcm);
			sc.firePropertyChange("appendToConsole", "", tcm);
			previousIterationSpecies = 0;	// we can't evaluate the max iterations anymore
			currentIterationSpecies = 0;
			break;
		case Notification:		// normal notification, just display the string
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, string);
			consoleNotificationList.add(tcm);
			sc.firePropertyChange("appendToConsole", "", tcm);
			break;
		case Detail:			// specific details, string will be processed, details extracted, formatted, etc
			processDetail(string);
			break;
		case DetailBatch:		// like above, but all details arrive in just one single shot, we can do some post processing
			processDetail(string);
			String s = "";
			if(previousIterationSpecies != currentIterationSpecies) {
				s = SimulationConsolePanel.getInsufficientIterationsMessage();
				tcm = new TaskCallbackMessage(TaskCallbackStatus.Warning, s);
				consoleNotificationList.add(tcm);
				sc.firePropertyChange("appendToConsole", "", tcm);
				sc.setInsufficientIterations(true);
				needAdjustIterations = true;
			}
			break;
		case Error:	
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, string);
			consoleNotificationList.add(tcm);
			sc.firePropertyChange("appendToConsole", "", tcm);
			break;
		case Warning:
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Warning, string);
			consoleNotificationList.add(tcm);
			sc.firePropertyChange("appendToConsole", "", tcm);
			break;
		default:
			break;
		}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	public void processDetail(String string) throws BadLocationException {
		String split[];
		split = string.split("\\n");
		for(String s : split) {
			if(s.startsWith("CPU TIME: total"))  {
				TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, s);
				consoleNotificationList.add(tcm);
				sc.firePropertyChange("appendToConsole", "", tcm);
			} else if (s.startsWith("Iteration")) {
				String species = "species";
				s = "    " + s.substring(0, s.indexOf("species") + species.length());
				TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, s);
				consoleNotificationList.add(tcm);
				sc.firePropertyChange("appendToConsole", "", tcm);
				checkMaxIterationConsistency(s);
			}
		}
	}
	private void checkMaxIterationConsistency(String s) {
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(s);
		try {
		for(int i=0; matcher.find(); i++) {
			if(i==2) {
				previousIterationSpecies = currentIterationSpecies;
				currentIterationSpecies = Integer.parseInt(matcher.group());
			}
		}
		} catch(NumberFormatException nfe) {
			
		}
	}
	
}
