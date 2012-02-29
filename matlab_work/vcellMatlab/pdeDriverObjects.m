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

problem.numX = 60;
problem.numY = 100;

problem.initMask();

solution = frapSolution;
solution.problem = problem;
solution.buildMatrix();
solution.initialFluorescence = solution.getGaussianBleachPattern(0.63, 0.63, 80.0);

solution.plotInitialConditions();

display('solving pde');
tic

solution.solutionTimes = 0 : 0.2 : 1;
solution.solve();

toc

display('plotting results');

solution.plotSolution(2);
solution.plotSolution(3);
solution.plotSolution(4);
solution.plotSolution(5);
solution.plotSolution(6);

