classdef paramestFew < handle
    %PARAMESTFEW Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        solution
        sortedInitialProjections
        sampledAbsProjections
        sampledEigenvalues
        logProjections
    end
    
    methods
        
        function optimize(this)
            % choose eigenvectors to use (skip first one)
            allAbsProjections = abs(this.solution.projection(:,2:size(this.solution.projection,2)));
            this.sortedInitialProjections = sort(allAbsProjections(1,:));
            numTimes = length(this.solution.solutionTimes);
            N = this.solution.numMeshPoints;
            numModesToUse = 3;
            this.sampledAbsProjections = zeros(numTimes,numModesToUse);
            this.sampledEigenvalues = zeros(numModesToUse,1);
            
            % pick 3 from the end (the three largest projections for
            % non-zero eigenvales).
            for i = 1:numModesToUse
                goodProjection = this.sortedInitialProjections(length(this.sortedInitialProjections) + 1 - i);
                display(sprintf('goodProjection(%d) = %d', i, goodProjection));    
                for j = 1:length(this.sortedInitialProjections)
                    if (allAbsProjections(1,j)==goodProjection)
                        this.sampledAbsProjections(:,i) = allAbsProjections(:,j);
                        this.sampledEigenvalues(i) = this.solution.eigenValues(j);
                    end
                end
            end
            
            % compute log of corresponding projections
            this.logProjections = log(this.sampledAbsProjections);
            
            
            % fit this log with a linear function of time (find slope)
            % slope
            %
            % .... FOR NOW ... CHOOSE LAST 5 points ..... REVISIT >>>>>>>
            numLastPoints = 5;
            t1 = numTimes-numLastPoints;
            t2 = numTimes;
            % P is the Omega2, the slower decay rate
            [P1 S1] = polyfit(this.solution.solutionTimes(t1:t2),this.logProjections(t1:t2,1),1);
            [P2 S2] = polyfit(this.solution.solutionTimes(t1:t2),this.logProjections(t1:t2,2),1);
            [P3 S3] = polyfit(this.solution.solutionTimes(t1:t2),this.logProjections(t1:t2,3),1);
            linearSystem = [abs(this.sampledEigenvalues(1)) -abs(this.sampledEigenvalues(1))*abs(P1(1)) -abs(P1(1)) ;
                            abs(this.sampledEigenvalues(2)) -abs(this.sampledEigenvalues(2))*abs(P2(1)) -abs(P2(1)) ;
                            abs(this.sampledEigenvalues(3)) -abs(this.sampledEigenvalues(3))*abs(P3(1)) -abs(P3(1))]
            rhs = [ -P1(1)*P1(1) ; -P2(1)*P2(1) ; -P3(1)*P3(1) ]
            x = linearSystem\rhs;
            D_init = x(2)
            Koff_init = x(1)/x(2)
            Kon_init = x(3)-Koff_init
            
            
            D_init = this.solution.problem.D
            Koff_init = this.solution.problem.Koff
            Kon_init = this.solution.problem.Kon
            
            % set up linear system in terms of slopes
            % alpha = Koff*D
            % beta  = D
            % gamma = Kon + Koff
            u1_fract = 1 - exp(P1(2))/abs(this.sampledAbsProjections(1,1));
            u2_fract = 1 - exp(P2(2))/abs(this.sampledAbsProjections(1,2));
            u3_fract = 1 - exp(P3(2))/abs(this.sampledAbsProjections(1,3));
            
            u1_fract
            u2_fract
            u3_fract
            
             options = optimset('GradObj','off','LargeScale','off','TolFun',1e-9,'MaxFunEvals',10000000,'Display','iter');  %,'DerivativeCheck','off');
             %[X] = fminunc(@(p) this.objFunc(p),[Kon_init Koff_init D_init u1_fract u2_fract u3_fract],options);
             X_init = [Kon_init Koff_init D_init u1_fract u2_fract u3_fract];
%              X = fminunc(@(varargin) objFunc(this,varargin),X_init,options)
             X = fminunc(@(p) objectiveFunction(this,p),X_init,options)
             
%         %     error = objFunc(weights);
%             fprintf('output structure, msg = %s\n',output.message);
%             output
%             solution = uMat;
%             fluorescence = iMat;
%             finalSourceTerms = X;
%             objFuncVal = FVAL;
%             mov = close(mov);
        end
        %=====================================================================
        % Objective Function
        %
        %=====================================================================
%         function [ objVal ] = objFunc(this,varargin)
%             nargin
%             p = varargin{1};
%             Kon = p(1);
%             Koff = p(2);
%             D = p(3);
%             u1_fract = p(4);
%             u2_fract = p(5);
%             u3_fract = p(6);
%             u1 = this.sampledAbsProjections(1,1)*u1_fract;
%             u2 = this.sampledAbsProjections(1,2)*u2_fract;
%             u3 = this.sampledAbsProjections(1,3)*u3_fract;
%             v1 = this.sampledAbsProjections(1,1)*(1-u1_fract);
%             v2 = this.sampledAbsProjections(1,2)*(1-u2_fract);
%             v3 = this.sampledAbsProjections(1,3)*(1-u3_fract);
%             b = 4*Koff*D*abs(this.sampledEigenvalues);
%             a = D*abs(this.sampledEigenvalues)+(Kon+Koff)*ones(3,1);
%             w1 = 0.5*(a+sqrt(a.*a-b));
%             w2 = 0.5*(a-sqrt(a.*a-b));
%             t = this.solution.solutionTimes;
%             f = ((u1*exp(-w1(1)*t)+v1*exp(-w2(1)*t))-this.sampledAbsProjections(:,1))^2 + ((u2*exp(-w1(2)*t)+v2*exp(-w2(2)*t))-this.sampledAbsProjections(:,2))^2 + ((u3*exp(-w1(3)*t)+v3*exp(-w2(3)*t))-this.sampledAbsProjections(:,3))^2;
%             objVal = sum(f)
%             
%             % get shirts for JANICE.
%         end
    end
end

