/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Matchable;
import org.vcell.util.Token;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

@SuppressWarnings("serial")
public class Event extends CommentedBlockObject implements Matchable, Serializable, VCMLProvider, IssueSource {

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
			if (variable == null) { 
				throw new MathException("'" + token + "' in EventAssignment is not valid. No such variable is present");
			}
			if (!(variable instanceof VolVariable)) {
				throw new MathException("'" + token + "' of type " + variable.getClass().getSimpleName() + " in EventAssignment is not valid. An event assignment target must be a VolVariable.");
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
			if (mathDescription != null) {
				if (variable != null) {
					variable = mathDescription.getVariable(variable.getName());
					if (assignmentExpression != null) {
						assignmentExpression.bindExpression(mathDescription);
					}
					else {
						throw new IllegalStateException("assignmentExpression is null");
					}
				}
				else {
					throw new IllegalStateException("variable is null");
				}
			}
			else {
					throw new IllegalArgumentException("null mathDescription passed");
			}
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
	private Expression triggerExpression = null;
	private Delay delay = null;
	private ArrayList<EventAssignment> eventAssignmentList = new ArrayList<EventAssignment>();
	
	/*
	public Event(String name, MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
		this.name = name;
		read(mathdesc, tokens);
	}	
	*/
	public Event(String name, Expression triggerExpression, Delay delay, ArrayList<EventAssignment> eventAssignmentList) {
		super(name);
		this.triggerExpression = triggerExpression;
		this.delay = delay;
		this.eventAssignmentList = eventAssignmentList;
	}
	/**
	 * construct from token string
	 * @param token start token (must equal {@link VCML#Event} 
	 * @param mathdesc parent
	 * @param tokens token stream 
	 * @throws MathException bad VCML, et al
	 * @throws ExpressionException bad expressions in VCML
	 * @throws IllegalArgumentException if token != Event 
	 */
	public Event(Token token, MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
		super(token,tokens);
		parseBlock(mathdesc, tokens);
		/*
		if (!token.getValue().equalsIgnoreCase(VCML.Event)) {
			throw new IllegalArgumentException("Invalid start token " + token + " for Event");
		}
		name = tokens.nextToken();
		setBeforeComment(token.getBeforeComment());
		read(mathdesc, tokens);
		*/
	}
	
	@Override
	protected String startToken() {
		return VCML.Event; 
	}
	

	@Override
	protected void parse(MathDescription mathdesc, String tokenString, CommentStringTokenizer tokens) throws MathException, ExpressionException {
		if(tokenString.equalsIgnoreCase(VCML.Trigger))
		{
			triggerExpression = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			return;
		}
		if (tokenString.equalsIgnoreCase(VCML.Delay)) {
			delay = new Delay(tokens);
			return;
		}
		if (tokenString.equalsIgnoreCase(VCML.EventAssignment)) {
			EventAssignment eventAssignment = new EventAssignment(mathdesc, tokens);
			eventAssignmentList.add(eventAssignment);
			return;
		}
		throw new MathFormatException("unexpected identifier "+tokenString);
	}

	/*
	private void read(MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
		eventAssignmentList.clear();
		String beginBlockString= tokens.nextToken();
		if (!beginBlockString.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+beginBlockString+" expecting "+VCML.BeginBlock);
		}			
		while (tokens.hasMoreTokens()){
			Token token  = tokens.next();
			String tokenString = token.getValue(); 
			if (tokenString.equalsIgnoreCase(VCML.EndBlock)){
				setAfterComment(token.getAfterComment());
				break;
			}			
			if(tokenString.equalsIgnoreCase(VCML.Trigger))
			{
				triggerExpression = MathFunctionDefinitions.fixFunctionSyntax(tokens);
				continue;
			}
			if (tokenString.equalsIgnoreCase(VCML.Delay)) {
				delay = new Delay(tokens);
				continue;
			}
			if (tokenString.equalsIgnoreCase(VCML.EventAssignment)) {
				EventAssignment eventAssignment = new EventAssignment(mathdesc, tokens);
				eventAssignmentList.add(eventAssignment);
				continue;
			}
			else throw new MathFormatException("unexpected identifier "+tokenString);

		}		
	}
	*/
	



	public final Expression getTriggerExpression() {
		return triggerExpression;
	}
	
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VCML.Event + " " + getName() + " " + VCML.BeginBlock + "\n");
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
		if (!super.compareEqual(obj)) {
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
	
	public void flatten(MathSymbolTable simSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {

		triggerExpression = Equation.getFlattenedExpression(simSymbolTable,triggerExpression,bRoundCoefficients);
		if (delay!=null && delay.durationExpression!=null){
			delay.durationExpression = Equation.getFlattenedExpression(simSymbolTable,delay.durationExpression,bRoundCoefficients);
		}
		for (EventAssignment eventAssignment : eventAssignmentList){
			eventAssignment.assignmentExpression = Equation.getFlattenedExpression(simSymbolTable,eventAssignment.assignmentExpression,bRoundCoefficients);
		}
	}
}
