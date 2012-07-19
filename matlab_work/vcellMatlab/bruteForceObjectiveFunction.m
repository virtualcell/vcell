function [ objVal ] = bruteForceObjectiveFunction(this, varargin )

            p = varargin{1};
            Kon = p(1);
            Koff = p(2);
            D = p(3);
            alpha2_1 = p(4);
            alpha2_2 = p(5);
            alpha2_3 = p(6);

            % use normalized projections
            alpha1_1 = (1-alpha2_1); % by using normalized data
            alpha1_2 = (1-alpha2_2); % by using normalized data
            alpha1_3 = (1-alpha2_3); % by using normalized data
            b = 4*Koff*D*abs(this.sampledEigenvalues);
            a = D*abs(this.sampledEigenvalues)+(Kon+Koff)*ones(3,1);
            w1 = 0.5*(a+sqrt(a.*a-b));
            w2 = 0.5*(a-sqrt(a.*a-b));
            t = this.solution.problem.timeStamps;
%             f = ((u1*exp(-w1(1)*t)+v1*exp(-w2(1)*t))-this.sampledAbsProjections(:,1)).^2 + ...
%                 ((u2*exp(-w1(2)*t)+v2*exp(-w2(2)*t))-this.sampledAbsProjections(:,2)).^2 + ...
%                 ((u3*exp(-w1(3)*t)+v3*exp(-w2(3)*t))-this.sampledAbsProjections(:,3)).^2
            % use normalized data
            f = ((alpha1_1*exp(-w1(1)*t)+alpha2_1*exp(-w2(1)*t))-this.normalizedProjections(:,1)).^2 + ...
                ((alpha1_2*exp(-w1(2)*t)+alpha2_2*exp(-w2(2)*t))-this.normalizedProjections(:,2)).^2 + ...
                ((alpha1_3*exp(-w1(3)*t)+alpha2_3*exp(-w2(3)*t))-this.normalizedProjections(:,3)).^2
            objVal = sum(f)

end

