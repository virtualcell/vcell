classdef paramestFew_realProblem < handle
    %PARAMESTFEW Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        solution
        sortedInitialProjections
        sampledAbsProjections
        sampledEigenvalues
        logProjections
        normalizedProjections
    end
    
    methods
        
        function optimize(this)
            % choose eigenvectors to use (skip first eigenVector---corresponding the eigenValue=0)
            allAbsProjections = abs(this.solution.projection(:,2:size(this.solution.projection,2)));
%             allAbsProjections = abs(this.solution.projection);
            % make eigen values are still consistent to the allAbsProjections(it gets rid of the first vector corresponding to eigenValue = 0)
            nonZeroEigenValues = this.solution.eigenValues(2:length(this.solution.eigenValues)); 
%             nonZeroEigenValues = this.solution.eigenValues(2:length(this.solution.eigenValues));
            this.sortedInitialProjections = sort(allAbsProjections(1,:)); % sort projection on each eigen vector at time=0
            numTimes = length(this.solution.problem.timeStamps);
            N = this.solution.numMeshPoints;
            numModesToUse = 3;
            this.sampledAbsProjections = zeros(numTimes,numModesToUse);
            this.sampledEigenvalues = zeros(numModesToUse,1);
            
            % pick 3 from the end (the three largest projections for non-zero eigenvales).
            for i = 1:numModesToUse
                goodProjection = this.sortedInitialProjections(length(this.sortedInitialProjections) + 1 - i);
                display(sprintf('goodProjection(%d) = %d', i, goodProjection));    
                for j = 1:length(this.sortedInitialProjections)
                    if (allAbsProjections(1,j)==goodProjection)
                        this.sampledAbsProjections(:,i) = allAbsProjections(:,j);
                        this.sampledEigenvalues(i) = nonZeroEigenValues(j);
                    end
                end
            end
            
            % compute log of corresponding projections, selected 3 projections
            % this.logProjections = log(this.sampledAbsProjections);
            % compute normalized projections (selected 3 projections)
            this.normalizedProjections = zeros(numTimes,numModesToUse);
            for i = 1 : numTimes
                for j = 1 : numModesToUse
                    this.normalizedProjections(i,j) = this.sampledAbsProjections(i,j)/this.sampledAbsProjections(1,j);
                end
            end
            this.logProjections = log(this.normalizedProjections);
            % The plot of the integration over eigenVectors*fluorscence
            % goes flat at the tail, that's a prediction of the slower decay rate. 
            % We then can fit alpha2 and omega2 by this tree plots.
            %
            %Integration_overSpace(eigenVector*fluorscence) = alpha1*EXP(omega1*t) + alpha2*EXP(omega2*t)
            %However, if we look at the flat part of plot, only the slower decay omega2 has influence.
            %So, basically we want to fit Integration_overSpace(eigenVector*fluorscence) = alpha2*EXP(omega2*t)
            %for the convenience we take log at the both side, then we have
            %log(Integration_overSpace(eigenVector*fluorscence)) = log(alpha2) + omega2*t (think about Y(t) = p2 + p1*t)
            %
            % .... The number of points should be properly chosen, with the slower decay info.
            numLastPoints = 17;
            t1 = numTimes-numLastPoints;
            t2 = numTimes;
            % P1(1)[p2(1), p3(1)] is the Omega2, the slower decay rate, and p1(2)[p2(2), p3(2)] is log(alpha2)
%            [P1 S1] = polyfit(this.solution.problem.timeStamps(t1:t2),this.logProjections(t1:t2,1),1);
%            [P2 S2] = polyfit(this.solution.problem.timeStamps(t1:t2),this.logProjections(t1:t2,2),1);
%            [P3 S3] = polyfit(this.solution.problem.timeStamps(t1:t2),this.logProjections(t1:t2,3),1);
            % Use 3 omega2s to solve D, Kon, Koff (omega2 (also omega1) can be calculated by functions of D, Kon and Koff), expand one of them 
            % as Square(omega2)-omega2*D*lamda + omega2*(Kon+Koff) = -Koff*D*lamda, and so on and so forth, let x= Koff*D, y=D and z=Kon+Koff
            % we have the linear system with 3 equations
            % lamda1*x - omega2(1)*lamda1*y - omega2(1)*z = -square(omega2(1))
            % lamda2*x - omega2(2)*lamda2*y - omega2(2)*z = -square(omega2(2))
            % lamda3*x - omega2(3)*lamda3*y - omega2(3)*z = -square(omega2(3))
%            linearSystem = [abs(this.sampledEigenvalues(1)) -abs(this.sampledEigenvalues(1))*abs(P1(1)) -abs(P1(1)) ;
%                            abs(this.sampledEigenvalues(2)) -abs(this.sampledEigenvalues(2))*abs(P2(1)) -abs(P2(1)) ;
%                            abs(this.sampledEigenvalues(3)) -abs(this.sampledEigenvalues(3))*abs(P3(1)) -abs(P3(1))]
%            rhs = [ -P1(1)*P1(1) ; -P2(1)*P2(1) ; -P3(1)*P3(1) ]
%            x = linsolve(linearSystem,rhs);
%            D_init = x(2)
%            Koff_init = x(1)/x(2)
%            Kon_init = x(3)-Koff_init
            
            % exp(P1(2)) is alpha2
            % since data are not normalized, we have alpha1 + alpha2 = projection0 (means initial condition at t=0)
            % let fract = 1-alpha2/projection0, we have later in the objective function
            % alpha1 = fract*projection0, alpha2 = projection0 * (1-fract)
%             u1_fract = 1 - exp(P1(2))/abs(this.sampledAbsProjections(1,1));
%             u2_fract = 1 - exp(P2(2))/abs(this.sampledAbsProjections(1,2));
%             u3_fract = 1 - exp(P3(2))/abs(this.sampledAbsProjections(1,3));
            % use normalized data
 
            
            %commented for bruteForce fitting
%            u1_fract = 1 - exp(P1(2));
%            u2_fract = 1 - exp(P2(2));
%            u3_fract = 1 - exp(P3(2));
            
            options = optimset('GradObj','off','LargeScale','off','TolFun',1e-9,'MaxFunEvals',10000000,'Display','notify','FunValCheck','on'); %'iter'
%            X_init = [Kon_init Koff_init D_init u1_fract u2_fract u3_fract];  % commented for btureForce fitting
            Kon_init = 50;
            Koff_init = 50;
            D_init = 50;
            alpha2_1 = 0.5;
            alpha2_2 = 0.5;
            alpha2_3 = 0.5;
            X_init = [Kon_init Koff_init D_init alpha2_1 alpha2_2 alpha2_3];
            % use constrained minimization function
            A = [];
            b = [];
            Aeq = [];
            beq = [];
            lb = [0,0,0,0,0,0];
            ub = [100,100,100,1,1,1];
            nonlcon = [];
            X = fmincon(@(p) bruteForceObjectiveFunction(this,p),X_init, A, b, Aeq, beq, lb, ub, nonlcon, options); % constrained
            % X = fminunc(@(p) objectiveFunction(this,p),X_init,options); % unconstrained
            disp(['Kon = ' num2str(X(1))]);
            disp(['Koff = ' num2str(X(2))]);
            disp(['D = ' num2str(X(3))]);
 %           disp(['u1_fract = ' num2str(X(4))]); %commented for bruteforce fitting
 %           disp(['u2_fract = ' num2str(X(5))]); %commented for bruteforce fitting
 %           disp(['u3_fract = ' num2str(X(6))]); %commented for bruteforce fitting
            disp(['alpha2_1 = ' num2str(X(4))]); 
            disp(['alpha2_2 = ' num2str(X(5))]);
            disp(['alpha2_3 = ' num2str(X(6))]);
            
        end
     
    end
end

