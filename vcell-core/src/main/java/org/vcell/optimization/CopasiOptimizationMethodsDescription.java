/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import java.util.HashMap;
import java.util.Map;

import cbit.vcell.opt.CopasiOptimizationMethod.CopasiOptimizationMethodType;
import cbit.vcell.opt.CopasiOptimizationParameter.CopasiOptimizationParameterType;

/**
 * The Copasi optimization method description is a shortened description from Copasi User Manual.
 * The shortened description is written in html format, which is displayed on CopasiOptimizatinMethodsHelpPanel.
 * The Copasi documentation can be found: http://www.copasi.org/tiki-index.php?page=UserManual&structure=DocumentationNew
 * 
 * @author Tracy LI
 * Created in Sept. 2011
 * @version 1.0
 */
public class CopasiOptimizationMethodsDescription
{
	private static String Description_NumGenerations = "<b>" + CopasiOptimizationParameterType.Number_of_Generations.getDisplayName() + ":</b> the number of generations the algorithm shall evolve the population.";
	private static String Description_NumIteration = "<b>" + CopasiOptimizationParameterType.Number_of_Iterations.getDisplayName() + ":</b> the number of parameter sets to be drawn before the algorithm stops.";
	private static String Description_PopulationSize = "<b>" + CopasiOptimizationParameterType.Population_Size.getDisplayName() + ":</b> the number of individuals that survive after each generation.";
	private static String Description_RndNumGenerator = "<b>" + CopasiOptimizationParameterType.Random_Number_Generator.getDisplayName() + ":</b> an enumeration value to determine which random number generator this method shall use.";
	private static String Description_Seed = "<b>" + CopasiOptimizationParameterType.Seed.getDisplayName() + ":</b> the seed for the random number generator.";
	private static String Description_IterationLimit = "<b>" + CopasiOptimizationParameterType.IterationLimit.getDisplayName() + ":</b> the maximum number of iterations the method is to perform.";
	private static String Description_Tolerance = "<b>" + CopasiOptimizationParameterType.Tolerance.getDisplayName() + ":</b> the tolerance with which the solution shall be determined.";
	private static String Description_Rho = "<b>" + CopasiOptimizationParameterType.Rho.getDisplayName() + ":</b> a value in (0, 1) determining the factor with which the steps size is reduced between iterations.";
	private static String Description_Scale = "<b>" + CopasiOptimizationParameterType.Scale.getDisplayName() + ":</b> a positive number and determines the size of the initial simplex.";
	private static String Description_SwarmSize = "<b>" + CopasiOptimizationParameterType.Swarm_Size.getDisplayName() + ":</b> the number of particles in the swarm.";
	private static String Description_StdDeviation = "<b>" + CopasiOptimizationParameterType.Std_Deviation.getDisplayName() + ":</b> an alternative termination criteria. If the standard deviation of the values of the objective function of each particle and the standard deviation of the best positions is smaller than the provided value the algorithm stops.";
	private static String Description_StartTemperature = "<b>" + CopasiOptimizationParameterType.Start_Temperature.getDisplayName() + ":</b> initial temperature of the system.";
	private static String Description_CoolingFactor = "<b>" + CopasiOptimizationParameterType.Cooling_Factor.getDisplayName() + ":</b> Rate by which the temperature is reduced from one cycle to the next, given by the formula: Tnew=Told*\'Cooling Factor\'.";
	private static String Description_Pf = "<b>" + CopasiOptimizationParameterType.Pf.getDisplayName() + ":</b> a numerical value in the interval (0, 1) determining the chance that individuals either outside the parameter boundaries or violating the constraints are compared during the selection.";
	
	private static final String Description_PARAMETERS_TO_BE_SET = "<p><u><b>Input Parameters:<b></u>";
	private static final String Description_REFERENCES = "<u><b>References:<b></u> &nbsp;";
	
	public static final Map<CopasiOptimizationMethodType, String> copasiMethodToHTMLInfoMap= new HashMap<CopasiOptimizationMethodType, String>();
	static{
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.EvolutionaryProgram, 
				// Evolutionary Programming 
				"<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.EvolutionaryProgram.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES +
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Evolutionary.Programming&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.EvolutionaryProgram.getDisplayName() + "</I></a><br><br>" +
			    "Evolutionary programming (EP) is a computational technique that mimics evolution and is based on reproduction "+
			    "and selection. An EP algorithm is composed of individuals that reproduce and compete, each one is a potential "+
			    "solution to the (optimization) problem and is represented by a \'genome\' where each gene corresponds to one "+
			    "adjustable parameter. At each generation of the EP, each individual reproduces asexually, i.e. divides into two "+
			    "individuals. One of these contains exactly the same \'genome\' as the parent while the other suffers some mutations "+
			    "(the parameter values of each gene change slightly). At the end of the generation, the algorithm has double the "+
			    "number of individuals. Then each of the individuals is confronted with a number of others to count how many does "+
			    "it outperform (the number of wins is the number of these competitors that represent worse solutions than itself)."+
			    "All the individuals are ranked by their number of wins, and the population is again reduced to the original number of "+
			    "individuals by eliminating those which have worse fitness (solutions)."+
			     
				Description_PARAMETERS_TO_BE_SET +
				"<ul>"+
				"<li>" + Description_NumGenerations + "</li>"+
				"<li>" + Description_PopulationSize + "</li>"+
				"<li>" + Description_Seed + "</li>"+
				"</ul>"+
				
		       
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.SRES, 
				// Evolutionary Strategy (SRES)
			     "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.SRES.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Evolutionary.Strategy.SRES&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.SRES.getDisplayName() + "</I></a><br><br>" +
				"Evolutionary Strategies with Stochastic Ranking (SRES) [Runarsson00] is similar to . However, a parent has multiple "+
			    "offsprings during each generation. Each offspring will contain a recombination of genes with another parent and "+
				"additional mutations. The algorithm assures that each parameter value will be within its boundaries. But constraints "+
			    "to the solutions may be violated."+
				
				 Description_PARAMETERS_TO_BE_SET+
					"<ul>"+
					"<li>" + Description_NumGenerations + "</li>"+
					"<li>" + Description_PopulationSize + "</li>"+
					"<li>" + Description_RndNumGenerator + "</li>"+
					"<li>" + Description_Seed + "</li>"+
					"<li>" + Description_Pf + "</li>"+
					"</ul>"+
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.GeneticAlgorithm, 
			//  Genetic Algorithm
			     "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.GeneticAlgorithm.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Genetic.Algorithm&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.GeneticAlgorithm.getDisplayName() + "</I></a><br><br>" +
			     "The genetic algorithm (GA) [Baeck97 Baeck93 Michalewicz94 Mitchell95] is a computational technique that mimics evolution "+
			     "and is based on reproduction and selection. A GA is composed of individuals that reproduce and compete, each one is a "+
			     "potential solution to the (optimization) problem and is represented by a \'genome\' where each gene corresponds to one "+
			     "adjustable parameter. At each generation of the GA, each individual is paired with one other at random for reproduction."+
			     "Two offspring are produced by combining their genomes and allowing for \'cross-over\', i.e., the two new individuals have "+
			     "genomes that are formed from a combination of the genomes of their parents. Also each new gene might have mutated, i.e. "+
			     "the parameter value might have changed slightly. At the end of the generation, the algorithm has double the number of "+
			     "individuals. Then each of the individuals is confronted with a number of others to count how many does it outperform (the "+
			     "number of wins is the number of these competitors that represent worse solutions than itself). All the individuals are "+
			     "ranked by their number of wins, and the population is again reduced to the original number of individuals by eliminating "+
			     "those which have worse fitness (solutions)."+
			     
					Description_PARAMETERS_TO_BE_SET +
					"<ul>"+
					"<li>" + Description_NumGenerations + "</li>"+
					"<li>" + Description_PopulationSize + "</li>"+
					"<li>" + Description_RndNumGenerator + "</li>"+
					"<li>" + Description_Seed + "</li>"+
					"</ul>" +
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.GeneticAlgorithmSR, 
				// Genetic Algorithm SR
			     "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.GeneticAlgorithmSR.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Genetic.Algorithm.SR&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.GeneticAlgorithmSR.getDisplayName() + "</I></a><br><br>"+
			     "The genetic algorithm with stochastic ranking is very similar to the before described with tournament selection. With "+
			     "two exception which are the mutations are not forced to be within the boundaries and the selection is done through a bubble "+
			     "sort with a random factor."+
			     
					 Description_PARAMETERS_TO_BE_SET+
					 "<ul>"+
					 "<li>" + Description_NumGenerations + "</li>"+
					 "<li>" + Description_PopulationSize + "</li>"+
					 "<li>" + Description_RndNumGenerator + "</li>"+
					 "<li>" + Description_Seed + "</li>"+
					 "<li>" + Description_Pf + "</li>"+
					 "</ul>" +
					 
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.HookeJeeves, 
				// Hooke & Jeeves
			     "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.HookeJeeves.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Hooke.Jeeves&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.HookeJeeves.getDisplayName() + "</I></a><br><br>"+
			     "The method of Hooke and Jeeves is a direct search algorithm that searches for the minimum of a nonlinear function without "+
			     "requiring (or attempting to calculate) derivatives of the function. Instead it is based on a heuristic that suggests a descent "+
			     "direction using the values of the function calculated in a number of previous iterations."+
			     
				 	Description_PARAMETERS_TO_BE_SET+
				  	"<ul>"+
					"<li>" + Description_IterationLimit + "</li>"+
					"<li>" + Description_Tolerance + "</li>"+
					"<li>" + Description_Rho + "</li>"+
					"</ul>" +
					
			     "</html>");
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.LevenbergMarquardt, 
				// Levenberg - Marquardt
			     "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.LevenbergMarquardt.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Levenberg.Marquardt&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.LevenbergMarquardt.getDisplayName() + "</I></a><br><br>"+
			     "Levenberg-Marquardt is a gradient descent method. It is a hybrid between the steepest descent and the Newton methods."+
			     
			     	Description_PARAMETERS_TO_BE_SET +
			     	"<ul>"+
					"<li>" + Description_IterationLimit + "</li>"+
					"<li>" + Description_Tolerance + "</li>"+
					"</ul>" +
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.NelderMead, 
				// Nelder - Mead
			     "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.NelderMead.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Evolutionary.Strategy.SRES&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.NelderMead.getDisplayName() + "</I></a><br><br>"+
			     "This method also known as the simplex method is due to Nelder and Mead [Nelder65]. A simplex is a polytope of N+1 vertices in "+
			     "N dimensions. The objective function is evaluated at each vertex. Dependent on these calculated values a new simplex is "+
			     "constructed. The simplest step is to replace the worst point with a point reflected through the centroid of the remaining N points."+
			     "If this point is better than the best current point, then we can try stretching exponentially out along this line. On the other hand, "+
			     "if this new point isn't much better than the previous value then we are stepping across a valley, so we shrink the simplex towards "+
			     "the best point."+
			     
			     	Description_PARAMETERS_TO_BE_SET +
			     	"<ul>"+
					"<li>" + Description_IterationLimit + "</li>"+
					"<li>" + Description_Tolerance + "</li>"+
					"<li>" + Description_Scale + "</li>"+
					"</ul>" +
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.ParticleSwarm, 
				// Particle Swarm
			     "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.ParticleSwarm.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Particle.Swarm&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.ParticleSwarm.getDisplayName() + "</I></a><br><br>"+
			     "The particle swarm optimization method suggested by Kennedy and Eberhart [Kennedy95] is inspired by a flock of birds or a school of "+
			     "fish searching for food. Each particle has a position Xi and a velocity Vi in the parameter space. Additionally, it remembers its best "+
			     "achieved objective value O and position Mi. Dependent on its own information and the position of its best neighbor (a random subset of "+
			     "particles of the swarm) a new velocity is calculated. With this information the position is updated. "+
			     
			     Description_PARAMETERS_TO_BE_SET +
			     	"<ul>"+
					"<li>" + Description_IterationLimit + "</li>"+
					"<li>" + Description_SwarmSize + "</li>"+
					"<li>" + Description_StdDeviation + "</li>"+
					"<li>" + Description_RndNumGenerator + "</li>"+
					"<li>" + Description_Seed + "</li>"+
					"</ul>"+
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.Praxis, 
				// Praxis
		         "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.Praxis.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Praxis&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.Praxis.getDisplayName() + "</I></a><br><br>"+
		         "Praxis is a direct search method (for a review see [Swann72]) that searches for the minimum of a nonlinear function without requiring "+
			     "(or attempting to calculate) derivatives of that function. Praxis was developed by Brent after the method proposed by Powell. The "+
		         "inspiration for Praxis was the well-known method of minimising each adjustable parameter (direction) at a time - the principal axes method."+
			     "In Praxis directions are chosen that do not coincide with the principal axes, in fact if the objective function is quadratic then these "+
		         "will be conjugate directions, assuring a fast convergence rate."+
			     
		         Description_PARAMETERS_TO_BE_SET +
		 		 	"<ul>"+
					"<li>" + Description_Tolerance + "</li>"+
					"</ul>"+
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.RandomSearch, 
				// Random Search
		         "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.RandomSearch.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Random.Search&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.RandomSearch.getDisplayName() + "</I></a><br><br>"+
		         "Random search is an optimization method that attempts to find the optimum by testing the objective function's value on a series of "+
			     "combinations of random values of the adjustable parameters. The random values are generated complying with any boundaries selected by "+
		         "the user, furthermore, any combinations of parameter values that do not fulfill constraints on the variables are excluded. This means "+
			     "that the method is capable of handling bounds on the adjustable parameters and fulfilling constraints. For infinite number of iterations "+
		         "this method is guaranteed to find the global optimum of the objective function. In general one is interested in processing a very large "+
			     "number of iterations."+
		         
		         	Description_PARAMETERS_TO_BE_SET +
		 			"<ul>"+
					"<li>" + Description_NumIteration + "</li>"+
					"<li>" + Description_RndNumGenerator + "</li>"+
					"<li>" + Description_Seed + "</li>"+
					"</ul>" +
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.SimulatedAnnealing, 
				// Simulated Annealing
		         "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.SimulatedAnnealing.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Simulated.Annealing&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.SimulatedAnnealing.getDisplayName() + "</I></a><br><br>"+
		         "Simulated annealing is an optimization algorithm first proposed by Kirkpatrick et al. [Kirkpatrick83] and was inspired by statistical "+
			     "mechanics and the way in which perfect crystals are formed. Perfect crystals are formed by first melting the substance of interest, and "+
		         "then cooling it very slowly. At large temperatures the particles vibrate with wide amplitude and this allows a search for global optimum."+
			     "As the temperature decreases so do the vibrations until the system settles to the global optimum (the perfect crystal).\n" +
			     "The simulated annealing optimization algorithm uses a similar concept: the objective function is considered a measure of the energy of "+
			     "the system and this is maintained constant for a certain number of iterations (a temperature cycle). In each iteration, the parameters are "+
			     "changed to a nearby location in parameter space and the new objective function value calculated; if it decreased, then the new state is "+
			     "accepted, if it increased then the new state is accepted with a probability that follows a Boltzmann distribution (higher temperature means "+
			     "higher probability of accepting the new state). After a fixed number of iterations, the stopping criterion is checked; if it is not time to "+
			     "stop, then the system's temperature is reduced and the algorithm continues."+
			     
		         	Description_PARAMETERS_TO_BE_SET +
		         	"<ul>"+
					"<li>" + Description_StartTemperature + "</li>"+
					"<li>" + Description_CoolingFactor + "</li>"+
					"<li>" + Description_Tolerance + "</li>"+
					"<li>" + Description_RndNumGenerator + "</li>"+
					"<li>" + Description_Seed + "</li>"+
					"</ul>" +
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.SteepestDescent, 
				// Steepest Descent
		         "<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.SteepestDescent.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Steepest.Descent&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.SteepestDescent.getDisplayName() + "</I></a><br><br>"+
		         "Steepest descent is an optimization method that follows the direction of steepest descent on the hyper-surface of the objective function "+
			     "to find a local minimum. The direction of steepest descent is defined by the negative of the gradient of the objective function."+
		         
			     	Description_PARAMETERS_TO_BE_SET +
		 			"<ul>"+
					"<li>" + Description_IterationLimit + "</li>"+
					"<li>" + Description_Tolerance + "</li>"+
					"</ul>" +
					
			     "</html>");
		
		copasiMethodToHTMLInfoMap.put(CopasiOptimizationMethodType.TruncatedNewton, 
				"<html>"
			     + "<center><h3>" + CopasiOptimizationMethodType.TruncatedNewton.getDisplayName() + "</h3></center>" +
			     Description_REFERENCES+
			     "<a href=\"http://www.copasi.org/tiki-index.php?page=OD.Truncated.Newton&structure=OD\"><I>COPASI User Manual -- " + CopasiOptimizationMethodType.TruncatedNewton.getDisplayName() + "</I></a><br><br>"+
			     "The Truncated Newton method is a sophisticated variant of the Newton optimization method. The Newton optimization method searches for the "+
			     "minimum of a nonlinear function by following descent directions determined from the function's first and second partial derivatives. The "+
			     "Truncated Newton method does an incomplete (truncated) solution of a system of linear equations to calculate the Newton direction. This means "+
			     "that the actual direction chosen for the descent is between the steepest descent direction and the true Newton direction. "+
			     
			     	Description_PARAMETERS_TO_BE_SET +
					"&nbsp;NONE <br>" +
			     
			     "</html>");
		
	}
	
}
