function [ solution finalSourceTerms objFuncVal ] = frap_PDE( refSolution, solutionTimes, r, rTimes)
% [solution, finalSourceTerms, objFuncVal] = frap_PDE(refSolution, solutionTimes, r, rTimes)
%
%     Computes the solution to a PDE will full spatial and temporal
%     discretization (e.g. for N computational elements, we have N bases).
%     The initial conditions are taken from first time point in the
%     refSolution.  
%
%     The model is hard-coded with all parameters known. Maybe later we 
%     will pass in a function pointer to give us the reaction terms.  This
%     is a simultaneous method, where the unknowns are the source terms at
%     each computational element, at each time, for each variable
%
% input:
%      refSolution - (from VCell), a matrix where mat(i,j) is reference
%                      solution of the ith time, jth computational element
%         refTimes - (from VCell) uniform time array for refSolution.
%                r - a cell array of matrices for basis response functions 
%                      where mat(i,j) is solution of the ith time, jth 
%                      computational element.
%           rTimes - nonuniform time array for r (nonuniform because of 
%                      diffusion-scheduling).
%
% output:
%         solution - cell array of solutions to each variable.
% finalSourceTerms - cell array of rates for each variable.
%       objFuncVal - final objective funciton value.
%
% usage: 
%    refSolution = {u1 u2 u3};
%    r = {r00 r01 r02 r03 r04 r05 r06 r07 r08 r09 r10};
%    [solution,rates,val] = frap_PDE(refSolution, u1_times, r, r00_times);
%
    numBases = size(r,2);
    numTimes = size(solutionTimes,2);
    numVariables = size(refSolution,2);
    
    % resample basis response functions back to uniform time step.
    r_interp = cell(1,numBases);
    for i=1:numBases
        r_interp{i} = interp1(rTimes,r{i},solutionTimes,'cubic','extrap');
    end

    % initialize to zero solution, initial conditions should be supplied 
    % by source terms at t=0 (constrained by the initial conditions)
    % here we do not assume that diffusion is in equilibrium.
    u = cell(1,numVariables);
    uBase = cell(numVariables,numBases);
    uRates = cell(numVariables,numBases);
    for i=1:numVariables
        u{i} = zeros(numTimes,numBases);
        uRates{i} = zeros(numTimes,numBases);
        for j=1:numBases
            uBase{i,j} = zeros(numTimes,numBases);
        end
    end

    % here we assume that since the initial conditions are absolutely
    % known, then we dont include them as degrees of freedom.  The
    % parameters are ordered 
    Np = numBases*(numTimes-1)*numVariables;
    weights = zeros(1,Np);
% load tempPDESolution.mat tempP;
% weights = tempP;
    count = 0;
    previousP = zeros(size(weights));
    options = optimset('GradObj','off','MaxFunEvals',100000);
   [X FVAL] = fminunc(@objFunc,weights,options);
%     error = objFunc(weights);
    solution = u;
    finalSourceTerms = X;
    objFuncVal = FVAL;
    return;

    %=====================================================================
    % Objective Function
    %
    %=====================================================================
    function [ objVal ] = objFunc(p)
        %
        % we assume that the initial conditions are known, so wij() at
        % initial time point is not a parameter, instead it is known.
        % where wij is the basis weight function for the ith variable and
        % the jth basis function
        %
        % where p = [w11...w1Nmes, w21 ... w2N, w1 ... wVN) where V is 
        % the number of variables and N is the number of basis functions 
        % and wij is a 1x(T-1) array where T is the number of times.
%        deltaP = p - previousP;

        startIndexOfTimeFunctions = 1;
        for i=1:numVariables
            uRates{i} = zeros(numTimes,numBases);
            for j=1:numBases
                startIndex = 1 + (i-1)*((numTimes-1)*numBases) + (j-1)*(numTimes-1);
                endIndex = startIndex + (numTimes-1) - 1;
%                if length(find(deltaP(startIndex:endIndex)))>0
                    w = [refSolution{i}(1,j) p(1,startIndex:endIndex)];   % extract wij and prepend with initial conditions
       %            w = [refSolution{i}(1,j) zeros(size(p(1,startIndex:endIndex)))];   % extract wij and prepend with initial conditions
                    temp = conv2(r_interp{j},w');
                    uBase{i,j} = temp(1:numTimes,:);
                    uRates{i}(:,j) = w';
%                 end
            end
        end

        for i=1:numVariables
            u{i} = zeros(size(refSolution{i}));
            for j=1:numBases
                u{i} = u{i} + uBase{i,j};
            end
        end

        % we may still want to have a non-negativity penalty
        negativePenalty = 0;
%         for i=1:numVariables
%             negativeValues = 0.5*(abs(u{i})-u{i});
%             error = mean(mean(negativeValues.*negativeValues));
%             negativePenalty = negativePenalty + error*error + error;
%         end
%         negativePenalty = negativePenalty * 100000;


        Kon = 1;
        Koff = 0.2;
        Kdeg = 0;
        bindingReactionRate = Kon*u{1}.*u{1}.*u{2} -Koff*u{3};
        degradationRate = Kdeg*u{2}.*u{2};
        
        rateError1 = uRates{1} - (-bindingReactionRate);
        rateError2 = uRates{2} - (-bindingReactionRate-degradationRate);
        rateError3 = uRates{3} - (bindingReactionRate);
        
        penalty1 = 1*mean(mean(rateError1.*rateError1));
        penalty2 = 1*mean(mean(rateError2.*rateError2));
        penalty3 = 1*mean(mean(rateError3.*rateError3));

        diff1 = refSolution{1}-u{1};
        error1 = 1000*mean(mean(diff1.*diff1));
        diff2 = refSolution{2}-u{2};
        error2 = 1000*mean(mean(diff2.*diff2));
        diff3 = refSolution{3}-u{3};
        error3 = 1000*mean(mean(diff3.*diff3));
        
        % some regularization
        energy = 0; % 100*mean(mean(uRates{1}.*uRates{1}+uRates{2}.*uRates{2}+uRates{3}.*uRates{3}));
        
        
        % 
        % give user feedback
        %
        modcount = mod(count,1000);
        if (modcount==1)
            figure(1);
            image(50*[u{1} u{2} u{3}]);
            figure(2);
            image(50*[refSolution{1} refSolution{2} refSolution{3}]);
            figure(3);
            image(50*[abs(diff1) abs(diff2) abs(diff3)]);
            figure(4);
            image((50*10+log(1e-20+abs([uRates{1} uRates{2} uRates{3}]))))
            figure(5);
            image(50*[uBase{1,1} uBase{1,2} uBase{1,3} uBase{1,4} uBase{1,5} uBase{1,6} uBase{1,7} uBase{1,8} uBase{1,9} uBase{1,10};
                       uBase{2,1} uBase{2,2} uBase{2,3} uBase{2,4} uBase{2,5} uBase{2,6} uBase{2,7} uBase{2,8} uBase{2,9} uBase{2,10};
                       uBase{3,1} uBase{3,2} uBase{3,3} uBase{3,4} uBase{3,5} uBase{3,6} uBase{3,7} uBase{3,8} uBase{3,9} uBase{3,10};
                ]);
            figure(6);
            image(50*[r{1} r{2} r{3} r{4} r{5} r{6} r{7} r{8} r{9} r{10} r{11}]);
        end

% pause(100);


        if (mod(count,1000)==0)
           tempVal = penalty1+penalty2+penalty3;
           tempP = p;
           save tempSolution.mat tempP tempVal;
        end
        count = count + 1;
        obj = penalty1+penalty2+penalty3;
        fprintf('leaving, count=%g, penalty1=%g, penalty2=%g, penalty3=%g, obj=%g, error1=%g, error2=%g, error3=%g\n',count,penalty1,penalty2,penalty3,obj,error1,error2,error3);
        previousP = p;
        objVal = obj;
     end
end
