package org.vcell.chombo;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.solver.Simulation;

public class RefinementRoi implements Serializable, Matchable {
	public enum RoiType
	{
		Membrane,
		Volume
	}
	
	public final static int defaultTagsGrow = 2;
	private final static int noTagsGrow = 0;
	private Expression roiExpression = null;
	private int tagsGrow = defaultTagsGrow;
	private RoiType type = null;
	private int level = 0;
	
	public RefinementRoi(RoiType type, int level, int tagsGrow, String roi) throws ExpressionException {
		if (roi == null || roi.isEmpty())
		{
			throw new ExpressionException("ROI cannot be empty");
		}
		this.type = type;
		this.level  = level;
		this.tagsGrow = tagsGrow;
		setRoiExpression(roi);
	}
	
	public RefinementRoi(CommentStringTokenizer tokens) throws DataAccessException {
		readVCML(tokens);
	}

	public RefinementRoi(RefinementRoi roi) {
		type = roi.type;
		level = roi.level;
		tagsGrow = roi.tagsGrow;
		if (roi.roiExpression != null)
		{
			roiExpression = new Expression(roi.roiExpression);
		}
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getVCML() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(VCML.RefinementRoi + " " + VCML.BeginBlock + "\n");
		buffer.append(VCML.RefinementRoiType + " " + type + "\n");
		buffer.append(VCML.Level + " " + level + "\n");
		buffer.append(VCML.TagsGrow + " " + tagsGrow + "\n");
		if (roiExpression != null)
		{
			buffer.append("\t" + VCML.ROIExpression + " " + roiExpression.infix() + ";\n");
		}

		buffer.append(VCML.EndBlock+"\n");
			
		return buffer.toString();
	}
	
	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		try {
			String token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.RefinementRoi)) {
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
				else if (token.equalsIgnoreCase(VCML.RefinementRoiType)) {
					token = tokens.nextToken();
					type = RoiType.valueOf(token);
				}
				else if (token.equalsIgnoreCase(VCML.Level)) {
					token = tokens.nextToken();
					level = Integer.parseInt(token);
				}
				else if (token.equalsIgnoreCase(VCML.TagsGrow)) {
					token = tokens.nextToken();
					tagsGrow = Integer.parseInt(token);
				}
				else if (token.equalsIgnoreCase(VCML.ROIExpression))
				{
					token = tokens.readToSemicolon();
					try {
						setRoiExpression(token);
					} catch (ExpressionException e) {
						System.err.println("Invalid " + VCML.ROIExpression + ": " + token);
					}
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

	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof RefinementRoi)) {
			return false;
		}
		RefinementRoi rl = (RefinementRoi) object;
		if (rl.level != level) {
			return false;
		}
		if (!Compare.isEqualOrNull(roiExpression, rl.roiExpression))
		{
			return false;
		}
		if (tagsGrow != rl.tagsGrow)
		{
			return false;
		}
		return true;
	}
	public Expression getRoiExpression() {
		return roiExpression;
	}

	public void setRoiExpression(String roiExp) throws ExpressionException {
		Expression exp = null;
		if (roiExp != null)
		{
			roiExp = roiExp.trim();
			if (roiExp.length() > 0)
			{
				exp = new Expression(roiExp);
				exp.bindExpression(new SimpleSymbolTable(new String[] { "x", "y", "z" } ));
			}
		}
		this.roiExpression = exp;
	}
		
	public void enableTagsGrow(boolean bEnabled)
	{
		tagsGrow = bEnabled ? defaultTagsGrow : noTagsGrow;
	}
	
	public boolean isTagsGrowEnabled()
	{
		return tagsGrow == defaultTagsGrow;
	}
	
	public int getTagsGrow()
	{
		return tagsGrow;
	}

	public int getLevel() {
		return level;
	}
	
	public static RefinementRoi createNewRoi(RoiType roiType, int nextLevel) throws ExpressionException
	{
		return new RefinementRoi(roiType, nextLevel, defaultTagsGrow, roiType == RoiType.Membrane ? "1.0" : "0.0");
	}

	public RoiType getType() {
		return type;
	}
	
	public double getDx(Simulation simulation)
	{
		Extent extent = simulation.getMathDescription().getGeometry().getExtent();
		ISize levelSize = simulation.getSolverTaskDescription().getChomboSolverSpec().getLevelSamplingSize(simulation.getMeshSpecification().getSamplingSize(), level);
		return extent.getX()/levelSize.getX();
	}
}
