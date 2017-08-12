package org.vcell.vmicro.op.display;

import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.vcell.optimization.ConfidenceInterval;
import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.optimization.ProfileSummaryData;
import org.vcell.optimization.gui.ConfidenceIntervalPlotPanel;
import org.vcell.optimization.gui.ProfileDataPanel;
import org.vcell.util.DescriptiveStatistics;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.vcell.opt.Parameter;

public class DisplayProfileLikelihoodPlotsOp {
	
	public void displayProfileLikelihoodPlots(ProfileData[] profileData, String title, WindowListener listener){
		//put plotpanes of different parameters' profile likelihoods into a base panel
		JPanel basePanel= new JPanel();
    	basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		for(int i=0; i<profileData.length; i++)
		{
			ConfidenceIntervalPlotPanel plotPanel = new ConfidenceIntervalPlotPanel();
			plotPanel.setProfileSummaryData(getSummaryFromProfileData(profileData[i]));
			plotPanel.setBorder(new EtchedBorder());
			String paramName = "";
			if(profileData[i].getProfileDataElements().size() > 0)
			{
				paramName = profileData[i].getProfileDataElements().get(0).getParamName();
			}
			ProfileDataPanel profileDataPanel = new ProfileDataPanel(plotPanel, paramName);
			basePanel.add(profileDataPanel);
		}
		JScrollPane scrollPane = new JScrollPane(basePanel);
    	scrollPane.setAutoscrolls(true);
    	scrollPane.setPreferredSize(new Dimension(620, 600));
    	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	//show plots in a dialog
		JFrame jframe = new JFrame();
		jframe.setTitle(title);
		jframe.getContentPane().add(scrollPane);
		jframe.setSize(500,500);
		if (listener!=null){
			jframe.addWindowListener(listener);
		}
		jframe.setVisible(true);
 	}

	//getting a profileSummary for each parameter that has acquired a profile likelihood distribution
	ProfileSummaryData getSummaryFromProfileData(ProfileData profileData) 
	{
		ArrayList<ProfileDataElement> profileElements = profileData.getProfileDataElements();
		int dataSize = profileElements.size();
		double[] paramValArray = new double[dataSize];
		double[] errorArray = new double[dataSize];
		if(dataSize >0)
		{
			//profile likelihood curve
			String paramName = profileElements.get(0).getParamName();
			//find the parameter to locate the upper and lower bounds
			Parameter parameter = null;
			Parameter[] bestParameters = profileElements.get(0).getBestParameters();
			for(int i=0; i<bestParameters.length; i++)
			{
				if(bestParameters[i] != null && bestParameters[i].getName().equals(paramName))
				{
					parameter = bestParameters[i];
				}
			}
//			double upperBound = (parameter == null)? 100 : parameter.getUpperBound();
//			double lowerBound = (parameter == null)? 0 : parameter.getLowerBound();
//			double logUpperBound = (upperBound == 0)? 0: Math.log10(upperBound);
//			double logLowerBound = (lowerBound == 0)? 0: Math.log10(lowerBound);
			for(int i=0; i<dataSize; i++)
			{
				paramValArray[i] = profileElements.get(i).getParameterValue();
				errorArray[i] = profileElements.get(i).getLikelihood();
			}
			PlotData dataPlot = new PlotData(paramValArray, errorArray);
			//get confidence interval line
			//make array copy in order to not change the data orders afte the sorting
			double[] paramValArrayCopy = new double[paramValArray.length];
			System.arraycopy(paramValArray, 0, paramValArrayCopy, 0, paramValArray.length);
			double[] errorArrayCopy = new double[errorArray.length];
			System.arraycopy(errorArray, 0, errorArrayCopy, 0, errorArray.length);
			DescriptiveStatistics paramValStat = DescriptiveStatistics.CreateBasicStatistics(paramValArrayCopy);
			DescriptiveStatistics errorStat = DescriptiveStatistics.CreateBasicStatistics(errorArrayCopy);
			double[] xArray = new double[2];
			double[][] yArray = new double[ConfidenceInterval.NUM_CONFIDENCE_LEVELS][2];
			//get confidence level plot lines
			xArray[0] = paramValStat.getMin() -  (Math.abs(paramValStat.getMin()) * 0.2);
			xArray[1] = paramValStat.getMax() + (Math.abs(paramValStat.getMax()) * 0.2) ;
			for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
			{
				yArray[i][0] = errorStat.getMin() + ConfidenceInterval.DELTA_ALPHA_VALUE[i];
				yArray[i][1] = yArray[i][0];
			}
			PlotData confidence80Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_80]);
			PlotData confidence90Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_90]);
			PlotData confidence95Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_95]);
			PlotData confidence99Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_99]);
			//generate plot2D data
			Plot2D plots = new Plot2D(null,null,new String[] {"profile Likelihood Data", "80% confidence", "90% confidence", "95% confidence", "99% confidence"}, 
					                  new PlotData[] {dataPlot, confidence80Plot, confidence90Plot, confidence95Plot, confidence99Plot},
					                  new String[] {"Profile likelihood of " + paramName, "Log base 10 of "+paramName, "Profile Likelihood"}, 
					                  new boolean[] {true, true, true, true, true});
			//get the best parameter for the minimal error
			int minErrIndex = -1;
			for(int i=0; i<errorArray.length; i++)
			{
				if(errorArray[i] == errorStat.getMin())
				{
					minErrIndex = i;
					break;
				}
			}
			double bestParamVal = Math.pow(10,paramValArray[minErrIndex]);
			//find confidence interval points
			ConfidenceInterval[] intervals = new ConfidenceInterval[ConfidenceInterval.NUM_CONFIDENCE_LEVELS];
			//half loop through the errors(left side curve)
			int[] smallLeftIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS]; 
			int[] bigLeftIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS];
			for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
			{
				smallLeftIdx[i] = -1;
				bigLeftIdx[i] = -1;
				for(int j=1; j < minErrIndex+1 ; j++)//loop from bigger error to smaller error
				{
					if((errorArray[j] < (errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i])) &&
					   (errorArray[j-1] > (errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i])))
					{
						smallLeftIdx[i]= j-1;
						bigLeftIdx[i]=j;
						break;
					}
				}
			}
			//another half loop through the errors(right side curve)
			int[] smallRightIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS]; 
			int[] bigRightIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS];
			for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
			{
				smallRightIdx[i] = -1;
				bigRightIdx[i] = -1;
				for(int j=(minErrIndex+1); j<errorArray.length; j++)//loop from bigger error to smaller error
				{
					if((errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i]) < errorArray[j] &&
					   (errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i]) > errorArray[j-1])
					{
						smallRightIdx[i]= j-1;
						bigRightIdx[i]=j;
						break;
					}
				}
			}
			//calculate intervals
			for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
			{
				double lowerBound = Double.NEGATIVE_INFINITY;
				boolean bLowerBoundOpen = true;
				double upperBound = Double.POSITIVE_INFINITY;
				boolean bUpperBoundOpen = true;
				if(smallLeftIdx[i] == -1 && bigLeftIdx[i] == -1)//no lower bound
				{
					lowerBound = parameter.getLowerBound();
					bLowerBoundOpen = false;
				}
				else if(smallLeftIdx[i] != -1 && bigLeftIdx[i] != -1)//there is a lower bound
				{
					//x=x1+(x2-x1)*(y-y1)/(y2-y1);
					double x1 = paramValArray[smallLeftIdx[i]];
					double x2 = paramValArray[bigLeftIdx[i]];
					double y = errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i];
					double y1 = errorArray[smallLeftIdx[i]];
					double y2 = errorArray[bigLeftIdx[i]];
					lowerBound = x1+(x2-x1)*(y-y1)/(y2-y1);
					lowerBound = Math.pow(10,lowerBound);
					bLowerBoundOpen = false;
				}
				if(smallRightIdx[i] == -1 && bigRightIdx[i] == -1)//no upper bound
				{
					upperBound = parameter.getUpperBound();
					bUpperBoundOpen = false;
				}
				else if(smallRightIdx[i] != -1 && bigRightIdx[i] != -1)//there is a upper bound
				{
					//x=x1+(x2-x1)*(y-y1)/(y2-y1);
					double x1 = paramValArray[smallRightIdx[i]];
					double x2 = paramValArray[bigRightIdx[i]];
					double y = errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i];
					double y1 = errorArray[smallRightIdx[i]];
					double y2 = errorArray[bigRightIdx[i]];
					upperBound = x1+(x2-x1)*(y-y1)/(y2-y1);
					upperBound = Math.pow(10,upperBound);
					bUpperBoundOpen = false;
				}
				intervals[i] = new ConfidenceInterval(lowerBound, bLowerBoundOpen, upperBound, bUpperBoundOpen);
			}
			return new ProfileSummaryData(plots, bestParamVal, intervals, paramName);
		}
		return null;
	}
	
}
