package org.vcell.vmicro.workflow.task;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.util.FastMath;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;

public class FitBleachSpot extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> normalizedImages;
	public final DataInput<ROI> bleachROI;
	//
	// outputs
	//
	public final DataOutput<Double> bleachRadius_ROI;
	public final DataOutput<Double> centerX_ROI;
	public final DataOutput<Double> centerY_ROI;
	public final DataOutput<Double> bleachRadius_GaussianFit;
	public final DataOutput<Double> bleachFactorK_GaussianFit;
	public final DataOutput<Double> centerX_GaussianFit;
	public final DataOutput<Double> centerY_GaussianFit;
	

	public FitBleachSpot(String id){
		super(id);
		normalizedImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"normalizedImages", this);
		bleachROI = new DataInput<ROI>(ROI.class,"bleachROI", this);
		addInput(normalizedImages);
		addInput(bleachROI);

		bleachRadius_ROI = new DataOutput<Double>(Double.class,"bleachRadiusROI_ROI",this);
		centerX_ROI = new DataOutput<Double>(Double.class,"centerX_ROI",this);
		centerY_ROI = new DataOutput<Double>(Double.class,"centerY_ROI",this);
		bleachRadius_GaussianFit = new DataOutput<Double>(Double.class,"bleachRadius_GaussianFit",this);
		bleachFactorK_GaussianFit = new DataOutput<Double>(Double.class,"bleachFactorK_GaussianFit",this);
		centerX_GaussianFit = new DataOutput<Double>(Double.class,"centerX_GaussianFit",this);
		centerY_GaussianFit = new DataOutput<Double>(Double.class,"centerY_GaussianFit",this);
		addOutput(bleachRadius_ROI);
		addOutput(centerX_ROI);
		addOutput(centerY_ROI);
		addOutput(bleachRadius_GaussianFit);
		addOutput(bleachFactorK_GaussianFit);
		addOutput(centerX_GaussianFit);
		addOutput(centerY_GaussianFit);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		ROI roi = context.getData(bleachROI);
		
		//
		// find initial guess by centroid from bleach ROI and total area of bleach ROI (assuming a circle of radius R)
		//
		int nonzeroPixelsCount = roi.getNonzeroPixelsCount();
		ISize size = roi.getISize();
		if (size.getZ()>1){
			throw new RuntimeException("expecting 2D bleach region ROI");
		}
		
		short[] pixels = roi.getBinaryPixelsXYZ(1);
		long numPixelsInROI = 0;
		Extent extent = roi.getRoiImages()[0].getExtent();
		double totalX_um = 0.0;
		double totalY_um = 0.0;
		double total_I = 0.0;
		double total_J = 0.0;
		int pixelIndex = 0;
		for (int i=0;i<size.getX();i++){
			double x = extent.getX()*(i+0.5)/(size.getX()-1);
			for (int j=0;j<size.getY();j++){
				if (pixels[pixelIndex] != 0){
					double y = extent.getY()*(j+0.5)/(size.getY()-1);
					totalX_um += x;
					totalY_um += y;
					total_I += i;
					total_J += j;
					numPixelsInROI++;
				}
				pixelIndex++;
			}
		}
		Origin origin = roi.getRoiImages()[0].getOrigin();
		double roiCenterX_um = origin.getX() + totalX_um/numPixelsInROI;
		double roiCenterY_um = origin.getY() + totalY_um/numPixelsInROI;
		double roiCenterI_pixelscale = total_I/numPixelsInROI;
		double roiCenterJ_pixelscale = total_J/numPixelsInROI;
		
		// Area = PI * R^2
		// R = sqrt(Area/PI)
		double roiBleachSpotArea_um2 = (extent.getX()*extent.getY()*numPixelsInROI)/(size.getX()*size.getY());
		double roiBleachRadius_um = Math.sqrt(roiBleachSpotArea_um2/Math.PI);
		double roiBleachRadius_pixelscale = Math.sqrt(numPixelsInROI/Math.PI);
		
		context.setData(bleachRadius_ROI,roiBleachRadius_um);
		context.setData(centerX_ROI,roiCenterX_um);
		context.setData(centerY_ROI,roiCenterY_um);
		
		GaussianFitResults results = fitToGaussian(roiCenterI_pixelscale, roiCenterJ_pixelscale, roiBleachRadius_pixelscale*roiBleachRadius_pixelscale, (FloatImage)context.getData(normalizedImages).getAllImages()[0]);
		context.setData(bleachFactorK_GaussianFit,results.K);
		context.setData(bleachRadius_GaussianFit, Math.sqrt(results.radius2));
		context.setData(centerX_GaussianFit, origin.getX() + extent.getX()*(results.centerI+0.5)/size.getX());
		context.setData(centerY_GaussianFit, origin.getY() + extent.getY()*(results.centerJ+0.5)/size.getY());
	}
	
	public static class GaussianFitResults {
		final double centerI;
		final double centerJ;
		final double radius2;
		final double K;
		final double high;
		final double objectiveFunctionValue;
		
		private GaussianFitResults(double centerI, double centerJ, double radius2, double k, double high, double objectiveFunctionValue) {
			super();
			this.centerI = centerI;
			this.centerJ = centerJ;
			this.radius2 = radius2;
			this.K = k;
			this.high = high;
			this.objectiveFunctionValue = objectiveFunctionValue;
		}
		
		public String toString(){
			return "GaussianFitResults: center=("+centerI+","+centerJ+"), radius2="+radius2+", K="+K+", high="+high+", objectiveFunctionValue="+objectiveFunctionValue;
		}

	}

	public static GaussianFitResults fitToGaussian(double init_center_i, double init_center_j, double init_radius2, FloatImage image) {
		//
		// do some optimization on the image (fitting to a Gaussian)
		// set initial guesses from ROI operation.
		//

		ISize imageSize = image.getISize();
		
		final int num_i = imageSize.getX();
		final int num_j = imageSize.getY();
		final float[] floatPixels = image.getFloatPixels();
		
		
		//
		// initial guess based on previous fit of ROI
		// do gaussian fit in index space for center and standard deviation (later to translate it back to world coordinates)
		//
		final int window_size = (int) Math.sqrt(init_radius2)*4;
//		final int window_min_i = 0;       // (int) Math.max(0, Math.floor(init_center_i - window_size/2));
//		final int window_max_i = num_i-1; // (int) Math.min(num_i-1, Math.ceil(init_center_i + window_size/2));
//		final int window_min_j = 0;       // (int) Math.max(0, Math.floor(init_center_j - window_size/2));
//		final int window_max_j = num_j-1; // (int) Math.min(num_j-1, Math.ceil(init_center_j + window_size/2));
		final int window_min_i = (int) Math.max(0, Math.floor(init_center_i - window_size/2));
		final int window_max_i = (int) Math.min(num_i-1, Math.ceil(init_center_i + window_size/2));
		final int window_min_j = (int) Math.max(0, Math.floor(init_center_j - window_size/2));
		final int window_max_j = (int) Math.min(num_j-1, Math.ceil(init_center_j + window_size/2));

		final int PARAM_INDEX_CENTER_I = 0;
		final int PARAM_INDEX_CENTER_J = 1;
		final int PARAM_INDEX_K = 2;
		final int PARAM_INDEX_HIGH = 3;
		final int PARAM_INDEX_RADIUS_SQUARED = 4;
		final int NUM_PARAMETERS = 5;
		double[] initParameters = new double[NUM_PARAMETERS];
		initParameters[PARAM_INDEX_CENTER_I] = init_center_i;
		initParameters[PARAM_INDEX_CENTER_J] = init_center_j;
		initParameters[PARAM_INDEX_HIGH] = 1.0;
		initParameters[PARAM_INDEX_K] = 10;
		initParameters[PARAM_INDEX_RADIUS_SQUARED] = init_radius2;

		PowellOptimizer optimizer = new PowellOptimizer(1e-4, 1e-1);
		MultivariateFunction func = new MultivariateFunction() {
			
			@Override
			public double value(double[] point) {
				double center_i = point[PARAM_INDEX_CENTER_I];
				double center_j = point[PARAM_INDEX_CENTER_J];
				double high = point[PARAM_INDEX_HIGH];
				double K = point[PARAM_INDEX_K];
				double radius2 = point[PARAM_INDEX_RADIUS_SQUARED];
				
				double error2 = 0;
				
				for (int j=window_min_j; j<=window_max_j; j++){
					//double y = j - center_j;
					double y = j;
					for (int i=window_min_i; i<=window_max_i; i++){
						//double x = i - center_i;
						double x = i;
						double modelValue = high - FastMath.exp(-K * FastMath.exp( - 2*(x*x + y*y) / radius2));
						double imageValue = floatPixels[j*num_i + i];
						double error = modelValue - imageValue;
						error2 += error*error;
					}
				}
				System.out.println(new GaussianFitResults(center_i, center_j, radius2, K, high, error2));
				return error2;
			}
		};
		
		PointValuePair pvp = optimizer.optimize(
				new ObjectiveFunction(func),
				new InitialGuess(initParameters),
				new MaxEval(100000),
				GoalType.MINIMIZE);
		
		double[] fittedParamValues = pvp.getPoint();
		double fitted_center_i = fittedParamValues[PARAM_INDEX_CENTER_I];
		double fitted_center_j = fittedParamValues[PARAM_INDEX_CENTER_J];
		double fitted_radius2 = fittedParamValues[PARAM_INDEX_RADIUS_SQUARED];
		double fitted_K = fittedParamValues[PARAM_INDEX_K];
		double fitted_high = fittedParamValues[PARAM_INDEX_HIGH];
		double objectiveFunctionValue = pvp.getValue();
		return new GaussianFitResults(fitted_center_i,fitted_center_j,fitted_radius2,fitted_K, fitted_high, objectiveFunctionValue);
	}
	
	public static void main(String[] args){
		try {
			int numX = 100;
			int numY = 100;
			double center_i = 52.44;
			double center_j = 51.39;
			double K = 30;
			double high = 0.9;
			double sigma2 = 4;
			float[] pixels = new float[numX*numY];
			Origin origin = new Origin(0,0,0);
			Extent extent = new Extent(1,1,1);
			FloatImage image = new FloatImage(pixels, origin, extent, numX, numY, 1);
			
			int index = 0;
			for (int j=0;j<numY;j++){
				for (int i=0;i<numX;i++){
					double radius = ((i-center_i)*(i-center_i) + (j-center_j)*(j-center_j));
					pixels[index++] = (float) (high - FastMath.exp(-K*FastMath.exp(-radius/sigma2)));
				}
			}
			
			double init_center_i = 50;
			double init_center_j = 50;
			double init_sigma2 = 2;
			GaussianFitResults fitResults = fitToGaussian(init_center_i, init_center_j, init_sigma2, image);
			System.out.println(fitResults);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
