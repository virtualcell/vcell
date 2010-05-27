package cbit.vcell.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

public class Event implements Matchable, Serializable {
	public static class EventAssignment implements Matchable, Serializable {
		private Variable variable = null;
		private Expression assignmentExpression = null;
		public EventAssignment(Variable variable, Expression assignment) {
			super();
			this.variable = variable;
			this.assignmentExpression = assignment;
		}
		public EventAssignment(MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
			read(mathdesc, tokens);
		} 
		
		private void read(MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
			String token = null;
			token = tokens.nextToken();			
			variable = mathdesc.getVariable(token);
			if (variable == null || !(variable instanceof VolVariable)) {
				throw new MathException("'" + token + "' in EventAssignment is not valid. An event assignment target must be a VolVariable.");
			}
			assignmentExpression = MathFunctionDefinitions.fixFunctionSyntax(tokens);
		}
		public Object getVCML() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\t" + VCML.EventAssignment + " " + variable.getName() + " " + assignmentExpression.infix() + ";\n");
			return buffer.toString();
		}
		
		public boolean compareEqual(Matchable obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof EventAssignment)) {
				return false;
			}
			EventAssignment eventAssignment = (EventAssignment)obj;
			
			if (!Compare.isEqual(variable, eventAssignment.variable)) {
				return false;
			}
			if (!Compare.isEqual(assignmentExpression, eventAssignment.assignmentExpression)) {
				return false;
			}

			return true;
		}
		public final Variable getVariable() {
			return variable;
		}
		public final Expression getAssignmentExpression() {
			return assignmentExpression;
		}
		public void bind(MathDescription mathDescription) throws ExpressionBindingException {
			variable = mathDescription.getVariable(variable.getName());
			assignmentExpression.bindExpression(mathDescription);
		}	
	}
	public static class Delay implements Matchable, Serializable {
		private boolean bUseValuesFromTriggerTime;
		private Expression durationExpression = null;
		
		public Delay(boolean bUseValuesFromTriggerTime, Expression durationExpression) {
			this.bUseValuesFromTriggerTime = bUseValuesFromTriggerTime;
			this.durationExpression = durationExpression;
		}
		public Delay(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
			read(tokens);
		} 		
		private void read(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
			String token = null;
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)){
				throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
			}			
			while (tokens.hasMoreTokens()){
				token = tokens.nextToken();
				if (token.equalsIgnoreCase(VCML.EndBlock)){
					break;
				}			
				if(token.equalsIgnoreCase(VCML.UseValuesFromTriggerTime))
				{
					token = tokens.nextToken();
					bUseValuesFromTriggerTime = Boolean.valueOf(token).booleanValue();
					continue;
				}
				if (token.equalsIgnoreCase(VCML.Duration)) {
					durationExpression = MathFunctionDefinitions.fixFunctionSyntax(tokens);
					continue;
				}				
				else throw new MathFormatException("unexpected identifier "+token);
			}		

		}
		public Object getVCML() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\t" + VCML.Delay + " " + VCML.BeginBlock + "\n");
			buffer.append("\t\t" + VCML.UseValuesFromTriggerTime + " " + bUseValuesFromTriggerTime + "\n");
			buffer.append("\t\t" + VCML.Duration  + " " + durationExpression.infix() + ";\n");
			buffer.append("\t" + VCML.EndBlock + "\n");
			return buffer.toString();
		}
		public boolean compareEqual(Matchable obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Delay)) {
				return false;
			}
			Delay delay = (Delay)obj;
			
			if (bUseValuesFromTriggerTime != delay.bUseValuesFromTriggerTime) {
				return false;
			}
			if (!Compare.isEqual(durationExpression, delay.durationExpression)) {
				return false;
			}

			return true;
		}
		public final boolean useValuesFromTriggerTime() {
			return bUseValuesFromTriggerTime;
		}
		public final Expression getDurationExpression() {
			return durationExpression;
		}
		public void bind(MathDescription mathDescription) throws ExpressionBindingException {
			durationExpression.bindExpression(mathDescription);
		}		
	}
	private String name;
	private Expression triggerExpression = null;
	private Delay delay = null;
	private ArrayList<EventAssignment> eventAssignmentList = new ArrayList<EventAssignment>();
	
	public Event(String name, MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
		this.name = name;
		read(mathdesc, tokens);
	}	
	
	private void read(MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
		eventAssignmentList.clear();
		String token = null;
		token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}			
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}			
			if(token.equalsIgnoreCase(VCML.Trigger))
			{
				triggerExpression = MathFunctionDefinitions.fixFunctionSyntax(tokens);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.Delay)) {
				delay = new Delay(tokens);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.EventAssignment)) {
				EventAssignment eventAssignment = new EventAssignment(mathdesc, tokens);
				eventAssignmentList.add(eventAssignment);
				continue;
			}
			else throw new MathFormatException("unexpected identifier "+token);

		}		
	}
	public Event(String name, Expression triggerExpression, Delay delay, ArrayList<EventAssignment> eventAssignmentList) {
		super();
		this.name = name; 
		this.triggerExpression = triggerExpression;
		this.delay = delay;
		this.eventAssignmentList = eventAssignmentList;
	}


	public final Expression getTriggerExpression() {
		return triggerExpression;
	}
	
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VCML.Event + " " + name + " " + VCML.BeginBlock + "\n");
		buffer.append("\t" + VCML.Trigger + " " + triggerExpression.infix() + ";\n");
		if (delay != null) {
			buffer.append(delay.getVCML());
		}
		for (EventAssignment eventAssignment : eventAssignmentList) {
			buffer.append(eventAssignment.getVCML());
		}
		
		buffer.append(VCML.EndBlock);
		return buffer.toString();
	}
	public final Delay getDelay() {
		return delay;
	}
	public boolean compareEqual(Matchable obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Event)) {
			return false;
		}
		
		Event event = (Event) obj;		
		if (!Compare.isEqual(getName(),event.getName())){
			return false;
		}
		if (!Compare.isEqual(triggerExpression, event.triggerExpression)) {
			return false;
		}
		if (!Compare.isEqualOrNull(delay, event.delay)) {
			return false;
		}
		if (!Compare.isEqual(eventAssignmentList, event.eventAssignmentList)) {
			return false;
		}
		return true;
	}

	public Iterator<EventAssignment> getEventAssignments() {
		return eventAssignmentList.iterator();
	}

	public int getNumEventAssignments() {
		return eventAssignmentList.size();
	}
	
	public void bind(MathDescription mathDescription) throws ExpressionBindingException {
		triggerExpression.bindExpression(mathDescription);
		if (delay != null) {
			delay.bind(mathDescription);
		}
		for (EventAssignment eventAssignment : eventAssignmentList) {
			eventAssignment.bind(mathDescription);
		}
		
	}
	
	public String getName() {
		return name;
	}
}
