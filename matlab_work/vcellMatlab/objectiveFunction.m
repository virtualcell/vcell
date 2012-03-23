function [ objVal ] = objectiveFunction(this, varargin )
%OBJECTIVEFUNCTION Summary of this function goes here
%   Detailed explanation goes here
            p = varargin{1};
            Kon = p(1);
            Koff = p(2);
            D = p(3);
            u1_fract = p(4);
            u2_fract = p(5);
            u3_fract = p(6);
            u1 = this.sampledAbsProjections(1,1)*u1_fract;
            u2 = this.sampledAbsProjections(1,2)*u2_fract;
            u3 = this.sampledAbsProjections(1,3)*u3_fract;
            v1 = this.sampledAbsProjections(1,1)*(1-u1_fract);
            v2 = this.sampledAbsProjections(1,2)*(1-u2_fract);
            v3 = this.sampledAbsProjections(1,3)*(1-u3_fract);
            b = 4*Koff*D*abs(this.sampledEigenvalues);
            a = D*abs(this.sampledEigenvalues)+(Kon+Koff)*ones(3,1);
            w1 = 0.5*(a+sqrt(a.*a-b));
            w2 = 0.5*(a-sqrt(a.*a-b));
            t = this.solution.solutionTimes;
            f = ((u1*exp(-w1(1)*t)+v1*exp(-w2(1)*t))-this.sampledAbsProjections(:,1)).^2 + ...
                ((u2*exp(-w1(2)*t)+v2*exp(-w2(2)*t))-this.sampledAbsProjections(:,2)).^2 + ...
                ((u3*exp(-w1(3)*t)+v3*exp(-w2(3)*t))-this.sampledAbsProjections(:,3)).^2
            objVal = sum(f)

end

