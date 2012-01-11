function [ initialConditions sourceTerms jacobian ] = modelfunction(modelParameters,numBases,uMat,solutionTimes)
%modelfunction Summary of this function goes here
%  Detailed explanation goes here
    if nargin == 2
        %
        % get initial conditions (as a function of the parameters
        %
        initialConditions = [ones(1,numBases) 2*ones(1,numBases)];
 %       initialConditions = 0.1*[1:numBases*2];
    else
        Kon = 1e-4;
        Koff = 1e-4;
        ub = 2.0;
    %         uMat = [uMat(1,:) ; uMat(1:end-1,:)];                           % introducing forward euler
    %         uMat = [0.5*(uMat(1:end-1,:) + uMat(2:end,:)) ; uMat(end,:)];   % introducing crank nicholson (almost)
        u1 = uMat(:,1:end/2);
        u2 = uMat(:,end/2+1:end);
        reactionRate = Kon*ub*u1 - Koff*u2;
        blTimes = (solutionTimes>=1).*(solutionTimes<=1.19);
        blSpace = [1 1 1 1 1 0 0 0 0 0 0 ];
        bleachMagnitude = 110*0.08;
        bleach = bleachMagnitude*blTimes'*blSpace;

        sourceTerms = [ -reactionRate - bleach.*u1, reactionRate - bleach.*u2];
        initialConditions = [];
        jacobian = [];
        if nargout == 3
            one = ones(size(u1));
            %                J11                 J12        J21            J22
            jacobian = [-Kon*ub*one - bleach, Koff*one, Kon*ub*one, -Koff*one - bleach];
        end  
    end    
end

