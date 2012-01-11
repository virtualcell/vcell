function [ solution finalSourceTerms objFuncVal ] = frap_PDE_linear( refSolution, solutionTimes, r, rTimes)
% [solution, finalSourceTerms, objFuncVal] = frap_PDE_linear(refSolution, solutionTimes, r, rTimes)
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
%    load BioSensors_1D_linearFRAP_Cell.mat
%    [solution,rates,val] = frap_PDE_linear(exactSolution, exactTimes, basisResponses, basisTimes);
%
skip=10;
refSolution{1} = refSolution{1}(1:skip:end,:);
refSolution{2} = refSolution{2}(1:skip:end,:);
solutionTimes = solutionTimes(:,1:skip:end);

S_exact = modelRate(refSolution,solutionTimes);


deltaT = solutionTimes(2)-solutionTimes(1);
    numBases = size(r,2);
    numTimes = size(solutionTimes,2);
    numVariables = size(refSolution,2);
    diffusionRate = [1 1e-10];
    
    % resample basis response functions back to uniform time step.
    r_interp = cell(numVariables,numBases);
    for i=1:numVariables
        for j=1:numBases
             r_interp{i,j} = interp1((1.0/diffusionRate(i))*rTimes,r{j},solutionTimes,'cubic','extrap');
        end
    end
    % initialize to zero solution, initial conditions should be supplied 
    % by source terms at t=0 (constrained by the initial conditions)
    % here we do not assume that diffusion is in equilibrium.
    u = cell(1,numVariables);
    uRates = cell(numVariables,numBases); % uRates is the source term applied to a basis
    uBase = cell(numVariables,numBases);  % uBase is the partial solution for each basis
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
    Np = numBases*numTimes*numVariables;
    weights = zeros(1,Np);
% load tempSolution.mat tempP;
% weights = tempP;
    count = 0;
    previousP = zeros(size(weights));
    useGradient = 'off';
    options = optimset('GradObj',useGradient,'MaxFunEvals',10000000,'Display','iter');
   [X FVAL exitflag output grad] = fminunc(@objFunc,weights,options);
%     error = objFunc(weights);
    fprintf('output structure, msg = %s\n',output.message);
    output
    solution = u;
    finalSourceTerms = X;
    objFuncVal = FVAL;
    return;

    %=====================================================================
    % Objective Function
    %
    %=====================================================================
    function [ objVal gradient ] = objFunc(p)
        %
        % we assume that the initial conditions are known, so wij() at
        % initial time point is not a parameter, instead it is known.
        % where wij is the basis weight function for the ith variable and
        % the jth basis function
        %
        % where p = [w11...w1Nmes, w21 ... w2N, w1 ... wVN) where V is 
        % the number of variables and N is the number of basis functions 
        % and wij is a 1x(T-1) array where T is the number of times.

        deltaP = p - previousP;

        startIndexOfTimeFunctions = 1;
        for i=1:numVariables
            for j=1:numBases
                startIndex = 1 + (i-1)*(numTimes*numBases) + (j-1)*numTimes;
                endIndex = startIndex + numTimes - 1;
                 if length(find(deltaP(startIndex:endIndex)))>0
                    w = p(1,startIndex:endIndex);         % extract wij
                    temp = deltaT*conv2(r_interp{i,j},w');
                    uBase{i,j} = temp(1:numTimes,:);
                    uRates{i}(:,j) = w';
                 end
            end
        end

        for i=1:numVariables
            u{i} = zeros(size(refSolution{i}));
            % homogenous response to initial conditions
            for j=1:numBases
                u{i} = u{i} + refSolution{i}(1,j)*r_interp{i,j};
            end
            % forced response to input
            for j=1:numBases
                u{i} = u{i} + uBase{i,j};
            end
            u_fast = sum(refSolution{i}(1,:).*r_interp{i,:} + uBase(i,:),2);
            maxU = max(max(abs(u{i})));
            uDiff = abs(u{i}-u_fast)/maxU;
            fprintf('maxU = %g, normalizedError = %g\n',maxU,max(max(uDiff)));
        end   

        % we may still want to have a non-negativity penalty
        negativePenalty = 0;
%         for i=1:numVariables
%             negativeValues = 0.5*(abs(u{i})-u{i});
%             error = mean(mean(negativeValues.*negativeValues));
%             negativePenalty = negativePenalty + error*error + error;
%         end
%         negativePenalty = negativePenalty * 100000;
% negativePenalty = 0;



        if strcmp(useGradient,'on')
            [ S jacobian ] = modelRateWithJac(u,solutionTimes);
        else
            S = modelRate(u,solutionTimes);
        end
        
        rateError{1} = S{1} - uRates{1};
        rateError{2} = S{2} - uRates{2};
%         grad = zeros(size(p));
        grad2 = zeros(size(p));
        if strcmp(useGradient,'on')
            index = 0;
%             for ip=1:numVariables
%                 for jp=1:numBases
%                     for kp=1:numTimes
%                         index = index + 1;
%                         grad(index) = -rateError{ip}(kp,jp);
%                         for i=1:numVariables
%                             for m=1:numBases
%                                 for n=kp:numTimes
%                                     grad(index) = grad(index)+deltaT*r_interp{ip,jp}(n-kp+1,m).*rateError{i}(n,m).*jacobian{i,ip}(n,m);
%                                 end
%                             end
%                         end
%                     end
%                 end
%             end
%             grad = grad*2/(numVariables*numBases*numTimes); % scale by 2/IMN
            
            %
            % 
            %
            grad2 = zeros(size(p));
            index = 0;
            for ip=1:numVariables
                rateError_ip = rateError{ip};
                for jp=1:numBases
                    r_interp_ip_jp = r_interp{ip,jp};
                    for kp=1:numTimes
                        index = index + 1;
                        grad2(index) = -rateError_ip(kp,jp);
                        for i=1:numVariables
                            rateErrorJacobian = rateError{i}.*jacobian{i,ip};
                            for n=kp:numTimes
                                grad2(index) = grad2(index)+deltaT*sum(r_interp_ip_jp(n-kp+1,:).*rateErrorJacobian(n,:));
                            end
                        end
                    end
                end
            end
            grad2 = grad2*2/(numVariables*numBases*numTimes); % scale by 2/IMN
%             maxGrad = max(max(abs(grad)));
%             gradDiff = abs(grad-grad2)/maxGrad;
%             fprintf('maxGrad = %g, normalizedError = %g\n',maxGrad,max(max(gradDiff)));
        end
        % aaa = [p'  grad']
        penalty1 = 1*mean(mean(rateError{1}.*rateError{1}));
        penalty2 = 1*mean(mean(rateError{2}.*rateError{2}));
        

        diff1 = refSolution{1}-u{1};
        error1 = 1000*mean(mean(diff1.*diff1));
        diff2 = refSolution{2}-u{2};
        error2 = 1000*mean(mean(diff2.*diff2));
        
        % some regularization
        energy = 0; % 100*mean(mean(uRates{1}.*uRates{1}+uRates{2}.*uRates{2}+uRates{3}.*uRates{3}));
        
        
        % 
        % give user feedback
        %
%         modcount = mod(count,length(p));
%         if (modcount==1)
%             figure(1);
%             image(50*[u{1}                      u{2}; 
%                       refSolution{1}            refSolution{2};
%                       abs(diff1)                abs(diff2);
%                       0.5*(abs(u{1})-u{1})      0.5*(abs(u{2})-u{2})]);
%             figure(2);
%             image(50*abs([uRates{1}             uRates{2};
%                           S{1}                  S{2}; 
%                           S_exact{1}            S_exact{2}; 
%                           abs(rateError{1})     abs(rateError{2})
%                           ]));
%             figure(3);
%             image(50*[uBase{1,1} uBase{1,2} uBase{1,3} uBase{1,4} uBase{1,5} uBase{1,6} uBase{1,7} uBase{1,8} uBase{1,9} uBase{1,10};
%                       uBase{2,1} uBase{2,2} uBase{2,3} uBase{2,4} uBase{2,5} uBase{2,6} uBase{2,7} uBase{2,8} uBase{2,9} uBase{2,10};
%                 ]);
%             figure(4);
%             image(50*[r{1} r{2} r{3} r{4} r{5} r{6} r{7} r{8} r{9} r{10} r{11}]);
%         end

% pause(100);


        obj = mean([penalty1 penalty2]); % +negativePenalty;
%         if (mod(count,1000)==0)
%            tempVal = obj;
%            tempP = p;
%            save tempSolution.mat tempP tempVal;
%         end
        count = count + 1;
%        fprintf('leaving, count=%g, #changed=%g, penalty1=%g, penalty2=%g, negPenalty=%g, obj=%g, error1=%g, error2=%g\n',count,length(find(deltaP)),penalty1,penalty2,negativePenalty,obj,error1,error2);
        previousP = p;
        objVal = obj;
        gradient = grad2;
    end
 
    function [ sourceTerms ] = modelRate(u,solutionTimes)
        Kon = 1;
        Koff = 0.5;
        ub = 1.0;
        reactionRate = Kon*ub*u{1} - Koff*u{2};
        blTimes = (solutionTimes>=0.5).*(solutionTimes<1.0);
%        blSpace = [1 1 1 1 1 0.5 0 0 0 0 0];
        blSpace = [1 1 1 1 1 0 0 0 0 0 0];
        bleach = blTimes'*blSpace;
% fprintf('size uRates{1}=[%g,%g]\n',size(uRates{1},1),size(uRates{1},2));
% fprintf('size u{1}=[%g,%g]\n',size(u{1},1),size(u{1},2));
% fprintf('size u{2}=[%g,%g]\n',size(u{2},1),size(u{2},2));
% fprintf('size rate=[%g,%g]\n',size(rate,1),size(rate,2));
% fprintf('size bleach=[%g,%g]\n',size(bleach,1),size(bleach,2));

        sourceTerms{1} = -reactionRate - bleach.*u{1};
        sourceTerms{2} =  reactionRate - bleach.*u{2};
    end

    function [ sourceTerms jacobian ] = modelRateWithJac(u,solutionTimes)
        Kon = 1;
        Koff = 0.5;
        ub = 1.0;
        reactionRate = Kon*ub*u{1} - Koff*u{2};
        blTimes = (solutionTimes>=0.5).*(solutionTimes<1.0);
%        blSpace = [1 1 1 1 1 0.5 0 0 0 0 0];
        blSpace = [1 1 1 1 1 0 0 0 0 0 0];
        bleach = blTimes'*blSpace;
% fprintf('size uRates{1}=[%g,%g]\n',size(uRates{1},1),size(uRates{1},2));
% fprintf('size u{1}=[%g,%g]\n',size(u{1},1),size(u{1},2));
% fprintf('size u{2}=[%g,%g]\n',size(u{2},1),size(u{2},2));
% fprintf('size rate=[%g,%g]\n',size(rate,1),size(rate,2));
% fprintf('size bleach=[%g,%g]\n',size(bleach,1),size(bleach,2));

        sourceTerms{1} = -reactionRate - bleach.*u{1};
        sourceTerms{2} =  reactionRate - bleach.*u{2};
        
        one = ones(size(u{1}));
        
        jacobian{1,1} = -Kon*ub*one - bleach;
        jacobian{1,2} =  Koff*one;
        jacobian{2,1} =  Kon*ub*one;
        jacobian{2,2} =  -Koff*one - bleach;
        
    end
end
