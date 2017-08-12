package org.vcell.chombo;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class TimeInterval implements Matchable, Serializable {

	private double startingTime;
	private double endingTime;
	private double timeStep;
	private double outputTimeStep;
	private static double eps = 1e-12;
	
	public TimeInterval(double startingTime, double endingTime, double timeStep, double outputTimeStep) throws IllegalArgumentException
	{
		super();
		this.startingTime = startingTime;
		validate(endingTime, timeStep, outputTimeStep);
		this.endingTime = endingTime;
		this.timeStep = timeStep;
		this.outputTimeStep = outputTimeStep;
	}
	
	public static TimeInterval getDefaultTimeInterval()
	{
		return new TimeInterval(0, 1, 0.1, 0.1);
	}
	
	private void validate(double endingTime, double timeStep, double outputTimeStep) throws IllegalArgumentException
	{
		if (endingTime > 0 && timeStep > 0 && !isMultiple(endingTime - startingTime, timeStep))
		{
			throw new IllegalArgumentException("[Starting, Ending] interval length has to be a multiple of time step");
		}
		if (endingTime > 0 && outputTimeStep > 0 && !isMultiple(endingTime - startingTime, outputTimeStep))
		{
			throw new IllegalArgumentException("[Starting, Ending] interval length has to be a multiple of output time step");
		}
		if (timeStep > 0 && outputTimeStep > 0 && !isMultiple(outputTimeStep, timeStep))
		{
			throw new IllegalArgumentException("Output interval has to be a multiple of time step");
		}
		if (endingTime > 0 && outputTimeStep > 0 && outputTimeStep - (endingTime - startingTime) > eps)
		{
			throw new IllegalArgumentException("Output time step can not be greater than [Starting, Ending] interval length.");
		}
	}
	
	public TimeInterval(CommentStringTokenizer tokens) throws DataAccessException {
		readVCML(tokens);
	}
	
	public TimeInterval(TimeInterval ti) {
		this.startingTime = ti.startingTime;
		this.endingTime = ti.endingTime;
		this.timeStep = ti.timeStep;
		this.outputTimeStep = ti.outputTimeStep;
	}

	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		try {
			String token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.TimeInterval)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
				}
			}
			
			while (tokens.hasMoreTokens()) {
				token = tokens.nextToken();
				if (token.equalsIgnoreCase(VCML.EndBlock)) {
					break;
				}
				else if (token.equalsIgnoreCase(VCML.StartingTime)) {
					token = tokens.nextToken();
					startingTime = Double.valueOf(token);
				}
				else if (token.equalsIgnoreCase(VCML.EndingTime)) {
					token = tokens.nextToken();
					endingTime = Double.valueOf(token);
				}
				else if (token.equalsIgnoreCase(VCML.TimeStep)) {
					token = tokens.nextToken();
					timeStep = Double.valueOf(token);
				}
				else if (token.equalsIgnoreCase(VCML.OutputTimeStep)) {
					token = tokens.nextToken();
					outputTimeStep = Double.valueOf(token);
				}
				else
				{
					throw new DataAccessException("unexpected identifier " + token);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			throw new DataAccessException("line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
		}
	}
	
	public int getKeepEvery() throws IllegalArgumentException
	{
		double d = Math.round(outputTimeStep/timeStep);
		int keepEvery = (int)d;
		if (Math.abs(outputTimeStep - d * timeStep) > eps)
		{
			throw new IllegalArgumentException("Output interval has to be a multiple of time step.");
		}
		return keepEvery;
	}
	
	private boolean isMultiple(double multiple, double interval)
	{
		double d = Math.round(multiple/interval);
		int m = (int)d;
		return Math.abs(multiple - m * interval) < eps;
	}
	
	public String getVCML() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(VCML.TimeInterval + " " + VCML.BeginBlock + "\n");
		buffer.append("\t" + VCML.StartingTime + " " + startingTime + "\n");
		buffer.append("\t" + VCML.EndingTime + " " + endingTime + "\n");
		buffer.append("\t" + VCML.TimeStep + " " + timeStep + "\n");
		buffer.append("\t" + VCML.OutputTimeStep + " " + outputTimeStep + "\n");
		buffer.append(VCML.EndBlock+"\n");
		return buffer.toString();
	}

	public double getEndingTime() {
		return endingTime;
	}

	public void setEndingTime(double endingTime) {
		validate(endingTime, timeStep, outputTimeStep);
		this.endingTime = endingTime;
	}

	public double getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(double timeStep) {
		validate(endingTime, timeStep, outputTimeStep);
		this.timeStep = timeStep;
	}

	public double getOutputTimeStep() {
		return outputTimeStep;
	}

	public void setOutputTimeStep(double outputTimeStep) {
		validate(endingTime, timeStep, outputTimeStep);
		this.outputTimeStep = outputTimeStep;
	}

	public double getStartingTime() {
		return startingTime;
	}

	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof TimeInterval)) {
			return false;
		}
		TimeInterval ti = (TimeInterval) object;
		return startingTime == ti.startingTime
				&& endingTime == ti.endingTime
				&& timeStep == ti.timeStep
				&& outputTimeStep == ti.outputTimeStep;
	}
}
