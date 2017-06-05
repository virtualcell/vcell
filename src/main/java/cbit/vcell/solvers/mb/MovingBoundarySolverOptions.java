/*
 * Copyright (C) 1999-2017 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package cbit.vcell.solvers.mb;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class MovingBoundarySolverOptions implements Matchable, Serializable {
	public static final double DEFAULT_FRONT_TO_NODE_RATIO = 1;
	public static final int DEFAULT_REDISTRIBUTION_FREQUENCY = 5;

	public enum RedistributionMode {
		NO_REDIST("None", 0),
		EXPANSION_REDIST("Expansion", 1),
		FULL_REDIST("Full", 2);

		private String label;
		private int value;

		private RedistributionMode(String label, int value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public int getValue() {
			return value;
		}
	};
	
	public enum ExtrapolationMethod
	{
		NEAREST_NEIGHBOR("Nearest Neighbor", 1),
		;
		private String label;
		private int value;
		
		private ExtrapolationMethod(String label, int value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public int getValue() {
			return value;
		}
	}

	public enum RedistributionVersion { // only applicable when FULL_REDIST
		NOT_APPLICABLE("", 0),
		ORDINARY_REDISTRIBUTE("Ordinary", 1),
		EQUI_BOND_REDISTRIBUTE("Equi-Bond", 2);

		private String label;
		private int value;

		private RedistributionVersion(String label, int value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public int getValue() {
			return value;
		}
	};

	private double frontToNodeRatio = DEFAULT_FRONT_TO_NODE_RATIO;
	private RedistributionMode redistributionMode = RedistributionMode.FULL_REDIST;
	private RedistributionVersion redistributionVersion = RedistributionVersion.EQUI_BOND_REDISTRIBUTE;
	private int redistributionFrequency = DEFAULT_REDISTRIBUTION_FREQUENCY;
	private ExtrapolationMethod extrapolationMethod = ExtrapolationMethod.NEAREST_NEIGHBOR;

	public MovingBoundarySolverOptions()
	{
		
	}
	
	public MovingBoundarySolverOptions(double frontToNodeRatio,
			RedistributionMode redistributionMode, RedistributionVersion redistributionVersion, int redistributionFrequency,
			ExtrapolationMethod extrapolationMethod) {
		super();
		this.frontToNodeRatio = frontToNodeRatio;
		this.redistributionMode = redistributionMode;
		this.redistributionVersion = redistributionVersion;
		this.redistributionFrequency = redistributionFrequency;
		this.extrapolationMethod = extrapolationMethod;
	}

	public MovingBoundarySolverOptions(MovingBoundarySolverOptions movingBoundarySolverOptions) {
		this.frontToNodeRatio = movingBoundarySolverOptions.frontToNodeRatio;
		this.redistributionMode = movingBoundarySolverOptions.redistributionMode;
		this.redistributionVersion = movingBoundarySolverOptions.redistributionVersion;
		this.redistributionFrequency = movingBoundarySolverOptions.redistributionFrequency;
		this.extrapolationMethod = movingBoundarySolverOptions.extrapolationMethod;
	}
	
	
	public MovingBoundarySolverOptions(CommentStringTokenizer tokenizer) throws DataAccessException {
		super();
		readVCML(tokenizer);
	}	

	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.MovingBoundarySolverOptions)) {
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
			if (token.equalsIgnoreCase(VCML.FrontToNodeRatio))
			{
				token = tokens.nextToken();
				frontToNodeRatio = Double.parseDouble(token);
			}
			else if (token.equalsIgnoreCase(VCML.RedistributionFrequency))
			{
				token = tokens.nextToken();
				redistributionFrequency = Integer.parseInt(token);
			}
			else if (token.equalsIgnoreCase(VCML.RedistributionMode))
			{
				token = tokens.nextToken();
				redistributionMode = RedistributionMode.valueOf(token);
			}
			else if (token.equalsIgnoreCase(VCML.RedistributionVersion))
			{
				token = tokens.nextToken();
				redistributionVersion = RedistributionVersion.valueOf(token);
			}
			else if (token.equalsIgnoreCase(VCML.ExtrapolationMethod))
			{
				token = tokens.nextToken();
				extrapolationMethod = ExtrapolationMethod.valueOf(token);
			}
			else
			{
				throw new DataAccessException("unexpected token " + token);
			}
		}
	}

	public ExtrapolationMethod getExtrapolationMethod() {
		return extrapolationMethod;
	}

	public void setExtrapolationMethod(ExtrapolationMethod extrapolationMethod) {
		this.extrapolationMethod = extrapolationMethod;
	}

	public double getFrontToNodeRatio() {
		return frontToNodeRatio;
	}

	public void setFrontToNodeRatio(double frontToNodeRatio) {
		this.frontToNodeRatio = frontToNodeRatio;
	}

	public int getRedistributionFrequency() {
		return redistributionFrequency;
	}

	public void setRedistributionFrequency(int redistributionFrequency) {
		this.redistributionFrequency = redistributionFrequency;
	}

	public RedistributionMode getRedistributionMode() {
		return redistributionMode;
	}

	public void setRedistributionMode(RedistributionMode redistributionMode) {
		this.redistributionMode = redistributionMode;
	}

	public RedistributionVersion getRedistributionVersion() {
		return redistributionVersion;
	}

	public void setRedistributionVersion(RedistributionVersion redistributionVersion) {
		this.redistributionVersion = redistributionVersion;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MovingBoundarySolverOptions)) {
			return false;
		}
		MovingBoundarySolverOptions rhs = (MovingBoundarySolverOptions)obj;
		if (frontToNodeRatio != rhs.frontToNodeRatio)
		{
			return false;
		}
		if (redistributionMode != rhs.redistributionMode)
		{
			return false;
		}
		if (redistributionMode == RedistributionMode.FULL_REDIST)
		{
			if (redistributionVersion != rhs.redistributionVersion)
			{
				return false;
			}
		}
		if (redistributionFrequency != rhs.redistributionFrequency)
		{
			return false;
		}
		if (extrapolationMethod != rhs.extrapolationMethod)
		{
			return false;
		}
		
			
		return true;
	}
	
	public String getVCML() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(VCML.MovingBoundarySolverOptions + " " + VCML.BeginBlock + "\n");
		buffer.append("\t" + VCML.FrontToNodeRatio + " " + frontToNodeRatio + "\n");
		buffer.append("\t" + VCML.RedistributionMode + " " + redistributionMode + "\n");
		buffer.append("\t" + VCML.RedistributionVersion + " " + redistributionVersion + "\n");
		buffer.append("\t" + VCML.RedistributionFrequency + " " + redistributionFrequency + "\n");
		buffer.append("\t" + VCML.ExtrapolationMethod + " " + extrapolationMethod + "\n");
		return buffer.toString();
	}
}
