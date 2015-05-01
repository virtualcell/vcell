package org.vcell.vmicro.op;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;
import org.vcell.util.Extent;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class BrownianDynamics2DSolver {
	
	public interface ParticleVisitor {
		void visit(double x, double y, boolean bFluorescent, int index);
	}
	
	public final Origin origin;
	public final Extent extent;
	public int numParticles;
	private final double[] particleX;
	private final double[] particleY;
	private final boolean[] bFluorescent;
	private double currTime;
	private final RandomGenerator simulationRng = new Well19937c();
	private final RandomGenerator imagingRng = new Well19937c();

	
	public BrownianDynamics2DSolver(Origin origin, Extent extent, int numParticles){
		this.origin = origin;
		this.extent = extent;
		this.particleX = new double[numParticles];
		this.particleY = new double[numParticles];
		this.bFluorescent = new boolean[numParticles];
		this.numParticles = numParticles;
		this.currTime = 0.0;
	}
	
	public void visit(ParticleVisitor visitor){
		for (int i=0;i<numParticles;i++){
			visitor.visit(particleX[i],particleY[i],bFluorescent[i], i);
		}
	}
	
	public void initializeUniform(boolean fluorescent){
		//
		// initialize to a uniform distribution
		//
		UniformRealDistribution uniformX = new UniformRealDistribution(simulationRng, origin.getX(), origin.getX()+extent.getX());
		UniformRealDistribution uniformY = new UniformRealDistribution(simulationRng, origin.getY(), origin.getY()+extent.getY());
		for (int i=0;i<numParticles;i++){
			particleX[i] = uniformX.sample();
			particleY[i] = uniformY.sample();
			bFluorescent[i] = fluorescent;
		}		
		currTime = 0.0;
	}
		
	public double currentTime(){
		return currTime;
	}
	
	public void bleachGuassian(double diffusion, double bleachDuration, int numSteps, double muX, double muY, double psfVar, double bleachFactor_K) throws ImageException{
		double deltaT = bleachDuration/(numSteps);
		
		for (int step=0; step<numSteps; step++){
			//
			// subject all fluorescent particles to a Gaussian shaped bleaching function.
			//
			System.out.println("bleaching particles, step="+step+", time="+currTime);
			long bleachCount = bleachGuassian0(muX, muY, psfVar, bleachFactor_K, deltaT);
			System.out.println("bleached "+bleachCount+" molecules ("+numParticles+" total particles)");
			//
			// let diffusion happen during sampling
			//
			System.out.println("simulating particles, step="+step+", time="+currTime);
			step(diffusion, deltaT);
		}
	}

	public UShortImage sampleImage(int numX, int numY, Origin origin, Extent extent, double diffusion, double integrationTime, int numSteps, double psfVar, double detectorGainAndQuantumYeild, boolean bConvolve, boolean bNoise) throws ImageException{
		double deltaT = integrationTime/(numSteps);
		
		//
		// sample the x and y coordinates for the structured grid (pixel coordinates). .. coordinates will be used lots of times (let's precompute).
		//
		double[] imageX = new double[numX];
		double[] imageY = new double[numY];
		for (int i=0;i<numX;i++){
			imageX[i] = origin.getX()+(0.5+i)*extent.getX()/numX;
		}
		for (int j=0;j<numY;j++){
			imageY[j] = origin.getY()+(0.5+j)*extent.getY()/numY;
		}
		
		// apply point spread function to gather poisson rates from all particles to all pixels.
		// then we roll the dice to sample from poission later.
		double[] particleImage = new double[numX*numY];
		Arrays.fill(particleImage, 0);
		for (int step=0; step<numSteps; step++){
			if (bConvolve){
				//
				// convolve all particles with normalized psf (each particle integrates to one over neighboring pixels) for each pixel and accumulate in lambda array 
				// (lambda is not reset so we can repeat and integrate over time)
				//
				System.out.println("convolving particles, step="+step+", time="+currTime);
				addConvolved(numX, numY, psfVar, imageX, imageY, particleImage);
			}else{
				//
				// count particles in each pixel
				//
				System.out.println("binning particles, step="+step+", time="+currTime);
				addBinned(numX, numY, origin.getX(), origin.getY(), extent.getX(), extent.getY(), particleImage);
			}
			//
			// let diffusion happen during sampling
			//
			System.out.println("simulating particles, step="+step+", time="+currTime);
			step(diffusion, deltaT);
		}

		//
		// generate sample image by generating fluorescence intensity
		// need to rescale to consider detector gain and expected number of photons from each particle. (ignore detector noise)
		//
		float[] pixels = new float[particleImage.length];
		double totalGain = detectorGainAndQuantumYeild*integrationTime/numSteps;
		for (int i=0;i<pixels.length;i++){
			float rate = (float)(particleImage[i]*totalGain);
			if (rate>1e-5){
				if (bNoise){
					pixels[i] = new PoissonDistribution(imagingRng,rate,PoissonDistribution.DEFAULT_EPSILON,PoissonDistribution.DEFAULT_MAX_ITERATIONS).sample();
				}else{
					pixels[i] = rate;
				}
			}
		}
		
		//
		// create unsigned short sample image
		//
		short[] ushortPixels = new short[pixels.length];
		for (int i=0;i<ushortPixels.length;i++){
			if (pixels[i] > 32767){
				throw new RuntimeException("pixel overflow of signed short, value = "+pixels[i]);
			}
			if (pixels[i] < 0){
				throw new RuntimeException("unexpected negative pixel, value = "+pixels[i]);
			}
			ushortPixels[i] = (short)(0xffff & (int)pixels[i]);
		}
		UShortImage sampledImage = new UShortImage(ushortPixels, origin, extent, numX, numY, 1);
		return sampledImage;
	}

	private void addConvolved(int numX, int numY, double psfVar, double[] imageX, double[] imageY, double[] convolvedParticleImage) {
		//
		// distribute each particle using a normalized Gaussian point spread function with variance psfVar.
		// we add the fluorescence to the image (can be called multiple times to accumulate fluorescence).
		//
		double gaussianNormalizationFactor = 1/(2*Math.PI*Math.sqrt(psfVar));
		int lambdaIndex = 0;
		double DISTANCE_6_SIGMA = Math.sqrt(psfVar)*6;
		for (int j=0;j<numY; j++){
			double muY = imageY[j];
			for (int i=0; i<numX; i++){
				double muX = imageX[i];
				double totalUnnormalizedParticleFluorRate = 0.0; // normalized psf 1/(2*pi*sigma) exp(-r2/sigma^2), here we defer the normalization until the end.
				for (int p=0;p<numParticles;p++){
					if (bFluorescent[p]){
						double pX = particleX[p]-muX;
						double pY = particleY[p]-muY;
						double r2 = pX*pX + pY*pY;
						if (r2 < DISTANCE_6_SIGMA){
							double particleFluorRate = FastMath.exp(-r2/psfVar);
							totalUnnormalizedParticleFluorRate += particleFluorRate;
						}
					}
				}
				double particleFluorRate = totalUnnormalizedParticleFluorRate * gaussianNormalizationFactor;
				convolvedParticleImage[lambdaIndex] += particleFluorRate; // here we add instead of setting because
				lambdaIndex++;
			}
		}
	}
	
	private long bleachGuassian0(double muX, double muY, double psfVar, double bleachFactor_K, double deltaT) {
		//
		// distribute each particle using a normalized Gaussian point spread function with variance psfVar.
		// we add the fluorescence to the image (can be called multiple times to accumulate fluorescence).
		//
		long bleachCount = 0;
		double DISTANCE_6_SIGMA = Math.sqrt(psfVar)*6;
		for (int p=0;p<numParticles;p++){
			if (bFluorescent[p]){
				double pX = particleX[p]-muX;
				double pY = particleY[p]-muY;
				double radius2 = pX*pX + pY*pY;
				if (radius2 < DISTANCE_6_SIGMA){
					double particleFluorRate = bleachFactor_K*deltaT*FastMath.exp(-radius2/psfVar);
					PoissonDistribution poisson = new PoissonDistribution(simulationRng, particleFluorRate, PoissonDistribution.DEFAULT_EPSILON, PoissonDistribution.DEFAULT_MAX_ITERATIONS);
					if (poisson.sample()>=1){ // if bleaching event happened
						bFluorescent[p] = false;
						bleachCount++;
					}
				}
			}
		}
		return bleachCount;
	}
	
	private void addBinned(int numX, int numY, double oX, double oY, double eX, double eY, double[] lambda) {
		//
		// sample each particle using a nearest neighbor (binning), 
		// we add the fluorescence to the image (can be called multiple times to accumulate fluorescence).
		//
		for (int p=0;p<numParticles;p++){
			if (bFluorescent[p]){
				double unitX = (particleX[p] - oX)/eX;
				double unitY = (particleY[p] - oY)/eY;
				int x = (int)(unitX*numX);
				int y = (int)(unitY*numY);
				if (x>=0 && x<numX && y>=0 && y<numY){
					int index = x + y*numX;
					lambda[index]++;
				}
			}
		}
	}
	
	public void step(double diffusion, double deltaT) {
		//
		// x(n+1) = x(n) + NORMAL(0,2*D*dt)
		// y(n+1) = y(n) + NORMAL(0,2*D*dt)
		//
		// Or try http://simul.iro.umontreal.ca/ssj/doc/html/umontreal/iro/lecuyer/stochprocess/BrownianMotion.html ???
		//
		NormalDistribution normalDistribution = new NormalDistribution(simulationRng, 0.0, 2*diffusion*deltaT);
		double maxX = origin.getX()+extent.getX();
		double minX = origin.getX();
		double maxY = origin.getY()+extent.getY();
		double minY = origin.getY();
		
		for (int p=0; p<numParticles; p++){
			double displacementX = normalDistribution.sample();
			double displacementY = normalDistribution.sample();
			
			double newX = particleX[p] + displacementX;
			if (newX < minX){
				newX = newX + 2*(minX - newX);
			}
			if (newX > maxX){
				newX = newX - 2*(newX - maxX);
			}

			double newY = particleY[p] + displacementY;
			if (newY < minY){
				newY = newY + 2*(minY - newY);
			}
			if (newY > maxY){
				newY = newY - 2*(newY - maxY);
			}

			particleX[p] = newX;
			particleY[p] = newY;
		}
		currTime += deltaT;
	}

	public void setFluorescence(int index, boolean b) {
		this.bFluorescent[index] = b;
	}

}
