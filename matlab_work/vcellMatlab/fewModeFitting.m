problem = frapProblem;

problem.D = 1;
problem.Kon = 0.2;
problem.Koff = 0.3;

problem.domainSizeX = 1;
problem.domainSizeY = 1;
problem.cell1Radius = problem.domainSizeX/3;
problem.cell1CenterX = problem.domainSizeX/2;
problem.cell1CenterY = problem.domainSizeX/2;
problem.cell2Radius = problem.domainSizeX/3.5;
problem.cell2CenterX = problem.domainSizeX/3;
problem.cell2CenterY = problem.domainSizeX/3;

problem.numX = 100;
problem.numY = 100;

problem.initMask();

solution = frapSolution;
solution.problem = problem;
solution.buildMatrix();
solution.initialFluorescence = solution.getGaussianBleachPattern(0.63, 0.63, 80.0); % 1*n (num of non-zero mask points)

solution.plotInitialConditions();

solution.solutionTimes = 0 : 0.05 : 1;
solution.solve(); % artificial data, solutionValues m(num time points) * 2n(2 times of non-zero mask points, for mobile and immobile)

solution.animateSolution(solution.getSolutionT(),0.1);

solution.computeEigenvalues(10);
solution.animateEigenfunctions(0.2);

solution.projectFluorescence() %solution total (num time points * non-zero mask points) * eigenVector(non-zero mask points * num eigen values)
solution.projection % created by projectFluorescence(), num time points * num eigen values

paramest = paramestFew;
paramest.solution = solution;
paramest.optimize();


% projections = solution.getProjections(solution.initialFluorescence);
% projectedInitial = projections'*solution.eigenVectors';
% solution.plotMesh(projectedInitial-solution.initialFluorescence',6,7,true);
% pause
% solution.plotMesh(projectedInitial,6,7,false);
% solution.plotMesh(solution.initialFluorescence,1,3,false);
% 
