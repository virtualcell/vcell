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

problem.numX = 30;
problem.numY = 30;

problem.initMask();

solution = frapSolution;
solution.problem = problem;
solution.buildMatrix();
%%

solution.initialFluorescence = solution.getGaussianBleachPattern(0.63, 0.63, 80.0);
%%

solution.plotInitialConditions();

solution.solutionTimes = 0 : 0.2 : 1;
solution.solve();

solution.animateSolution(solution.getSolutionT(),1);


solution.computeEigenvalues(6);
solution.animateEigenfunctions(0.001);

projections = solution.getProjections(solution.initialFluorescence); %numOfEigenValues * 1, eigenVectors' * initialFluorescence
%%

projectedInitial = projections'*solution.eigenVectors';% 1*n (non-zero mask points)
%%

solution.plotMesh(projectedInitial-solution.initialFluorescence',6,7,true);
pause
solution.plotMesh(projectedInitial,6,7,false);
solution.plotMesh(solution.initialFluorescence,1,3,false);

