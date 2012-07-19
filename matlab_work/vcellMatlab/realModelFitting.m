problem = frapRealProblem;

problem.D = 1;

problem.numX = 52;
problem.numY = 52;
problem.domainSizeX = 1;
problem.domainSizeY = 1;
% problem.domainSizeX = 104.28;
% problem.domainSizeY = 74.36;
problem.loadProblemFromData();
%%

problem.expFluorescence;

solution = frapSolution;
solution.problem = problem;

solution.buildMatrix(); % sparse matrix based on mask

solution.computeEigenvalues(20);
solution.eigenValues

%solution.animateEigenfunctions(0.2);
%%
solution.projectExpFluorescence(); %solution total (num time points * non-zero mask points) * eigenVector(non-zero mask points * num eigen values)
solution.projection % created by projectFluorescence(), num time points * num eigen values
%%

paramest = paramestFew_realProblem;
paramest.solution = solution;
paramest.optimize();


% projections = solution.getProjections(solution.initialFluorescence);
% projectedInitial = projections'*solution.eigenVectors';
% solution.plotMesh(projectedInitial-solution.initialFluorescence',6,7,true);
% pause
% solution.plotMesh(projectedInitial,6,7,false);
% solution.plotMesh(solution.initialFluorescence,1,3,false);
% 
