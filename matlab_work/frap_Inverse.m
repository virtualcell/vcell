function [ solution fluorescence finalSourceTerms objFuncVal animation] = frap_inverse( refSolution, refImage, numVariables, solutionTimes, r, ir, rTimes, modelfunct)
% [solution, image, finalSourceTerms, objFuncVal, animation] = frap_PDE_indexing(refSolution, refImage, numVariables, solutionTimes, r, ir, rTimes, modelfunct)
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
%         refImage - (from VCell ... or microscope), a matrix where
%                      mat(i,j) is the pixel at the ith time, jth ROI.
%     numVariables - number of Variables in the PDE.
%         refTimes - (from VCell) uniform time array for refSolution/refImage.
%                r - a cell array of matrices for basis response functions 
%                      where mat(i,j) is solution of the ith time, jth 
%                      computational element.
%               ir - a cell array of matrices for basis image response 
%                      functions where mat(i,j) is the fluorescence of the
%                      ith time, jth computational element.
%           rTimes - nonuniform time array for r (nonuniform because of 
%                      diffusion-scheduling).
%        modelfunc - handle to nonlinear model function
%                    [ exactsolution sourceTerms jacobian ] = modelfunct(uMat,solutionTimes)
%
% output:
%         solution - matrix of solutions to each variable over time.
%            image - matrix of fluorescence in each ROI over time.
% finalSourceTerms - matrix of rates for each variable over time.
%       objFuncVal - final objective funciton value.
%
% usage: 
%    load ../workingDir/VirtualMicroscopy/InverseProblem_FRAP_nobinding_dirac.mat
%    t=clock; [solution,rates,val,movie] = frap_Inverse(exactSolution,expData,2,exactTimes,basisResponses,basisResponses2DConv,basisTimes,@modelfunction_inverse); etime(clock,t)
%

    skip=1;
    solutionTimes = solutionTimes(:,1:skip:end);

refSolutionMat = [ refSolution{1} refSolution{3} ];
%    refSolutionMat = [];
%    if (length(refSolution)>0)
%        for i=1:length(refSolution)
%            refSolution{i} = refSolution{i}(1:skip:end,:);
%            refSolutionMat = [ refSolutionMat refSolution{i} ];
%        end
%    end
    
    if (length(refImage)>0)
        refImage = refImage(1:skip:end,:);
    end
    
    deltaT = solutionTimes(2)-solutionTimes(1);
    endTime = solutionTimes(end);
    numBases = size(r,2);
    numROIs = size(ir{1},2);
    numTimes = size(solutionTimes,2);
    diffusionRate = [20 1e-10];
%    diffusionRate = [1e-10 1e-10];
    
    fprintf('numVars=%g, numTimes=%g, numBases=%g, num unknowns=%g\n',numVariables,numTimes,numBases,(numVariables*numBases*numTimes));
%     %
%     % let's use a single matrix for initial conditions
%     %
%     initMat = zeros(1,numVariables*numBases);
%     for i=1:numVariables
%         initMat(1, (i-1)*numBases+1 : i*numBases) = refSolution{i}(1,:);
%     end

    % resample basis response functions back to uniform time step.
    r_interpMat = zeros(numTimes,numVariables*numBases*numBases);
    startingColIndex = 1;
    for i=1:numVariables
        for j=1:numBases
%  r_interpFine = interp1((1.0/diffusionRate(i))*rTimes,r{j},linspace(0,endTime,numTimes*100),'cubic','extrap');
%  r_new = reshape(sum(reshape(r_interpFine,[100,numTimes,numBases]))/100,[numTimes,numBases]);
%  r_interpMat(:,startingColIndex:startingColIndex+numBases-1) = r_new;
             r_interpMat(:,startingColIndex:startingColIndex+numBases-1) = interp1((1.0/diffusionRate(i))*rTimes{j},r{j},solutionTimes,'cubic','extrap');
             startingColIndex = startingColIndex + numBases;
        end
    end
    % resample image basis response functions back to uniform time step.
    ir_interpMat = zeros(numTimes,numVariables*numBases*numROIs);
    startingColIndex = 1;
    for i=1:numVariables
        for j=1:numBases
             ir_interpMat(:,startingColIndex:startingColIndex+numROIs-1) = interp1((1.0/diffusionRate(i))*rTimes{j},ir{j},solutionTimes,'cubic','extrap');
             startingColIndex = startingColIndex + numROIs;
        end
    end
    
%     % combine with shifted for trapazoidal integration.
%     r_interpMat = [r_interpMat(1,:) ; 0.5*(r_interpMat(2:end,:) + r_interpMat(1:end-1,:))];  
    
    % initialize to zero solution and zero rates
    uMat = zeros(numTimes,numVariables*numBases);
    iMat = zeros(numTimes,numROIs);
    uRateMat = zeros(numTimes,numVariables*numBases);

    % here we assume that since the initial conditions are absolutely
    % known, then we dont include them as degrees of freedom.  The
    % parameters are ordered 
    Np = numBases*numTimes*numVariables;
    weights = zeros(1,Np);
    
    if (length(refSolutionMat) > 0)
        S_exact = modelfunct(weights(1,numBases*numTimes+1:end),numBases,refSolutionMat,solutionTimes);
    else
        S_exact = [];
    end
    
    % load tempSolution.mat tempP;
% weights = tempP;
    count = 0;
    previousP = zeros(size(weights));
    refreshSolution=1;
    useGradient = 'on';
    largeScale = 'off';
    mov = avifile('example2.avi','COMPRESSION','Indeo3');
    options = optimset('GradObj',useGradient,'LargeScale',largeScale,'TolFun',1e-9,'MaxFunEvals',10000000,'Display','iter');  %,'DerivativeCheck','off');
   [X FVAL exitflag output grad] = fminunc(@objFunc,weights,options);
%     error = objFunc(weights);
    fprintf('output structure, msg = %s\n',output.message);
    output
    solution = uMat;
    fluorescence = iMat;
    finalSourceTerms = X;
    objFuncVal = FVAL;
    mov = close(mov);
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
        if (length(find(deltaP))>2)
            refreshSolution = 1;
        end
        %deltaT = 1e2;
        if (refreshSolution)            
            initMat = modelfunct(weights(1,numBases*numTimes+1:end),numBases);
            
            % set initial conditions to uMat matrix (response to initial conditions)
            % set initial fluorescence to iMat matrix (fluorescence response to initial conditions).
            % Assumes fluorescence is simply the summed over all variables equally.
            uMat = zeros(numTimes,numVariables*numBases);
            iMat = zeros(numTimes,numROIs);
            startIndexU = 1;
            startIndexR = 1;
            startIndexIR = 1;
            startIndexI = 1;
            for i=1:numVariables
                for j=1:numBases
                    uMat(:,startIndexU:startIndexU+numBases-1) = uMat(:,startIndexU:startIndexU+numBases-1) + initMat(1,startIndexI)*r_interpMat(:,startIndexR:startIndexR+numBases-1);
                    iMat = iMat + initMat(1,startIndexI)*ir_interpMat(:,startIndexIR:startIndexIR+numROIs-1);
                    startIndexI = startIndexI + 1;
                    startIndexR = startIndexR + numBases;
                    startIndexIR = startIndexIR + numROIs;
                end
                startIndexU = startIndexU + numBases;
            end

            % add solution and fluorescence due to source terms
            startIndexU = 1;
            startIndexR = 1;
            startIndexIR = 1;
            startIndexW = 1;
            for i=1:numVariables
                for j=1:numBases
                    startIndex = 1 + (i-1)*(numTimes*numBases) + (j-1)*numTimes;
                    endIndex = startIndex + numTimes - 1;
                    w = p(1,startIndex:endIndex);         % extract wij
                    
                    temp = deltaT*conv2(r_interpMat(:,startIndexR:startIndexR+numBases-1),w');
                    uMat(:,startIndexU:startIndexU+numBases-1) = uMat(:,startIndexU:startIndexU+numBases-1) + temp(1:numTimes,:);
                    startIndexR = startIndexR + numBases;
                    
                    temp = deltaT*conv2(ir_interpMat(:,startIndexIR:startIndexIR+numROIs-1),w');
                    iMat = iMat + temp(1:numTimes,:);
                    startIndexIR = startIndexIR + numROIs;
                    
                    uRateMat(:,startIndexW) = w';
                    startIndexW = startIndexW + 1;
                end
                startIndexU = startIndexU + numBases;
            end
            refreshSolution = 0;
        else
            indices = find(deltaP);
            for pIndex=1:length(indices)
                [timeIndex,baseIndex,varIndex] = ind2sub([numTimes numBases numVariables],indices(pIndex));
                
                startIndexR = (varIndex-1)*numBases*numBases+(baseIndex-1)*numBases+1;
                deltaU = deltaT*deltaP(indices(pIndex))*r_interpMat(1:numTimes-timeIndex+1,startIndexR:startIndexR+numBases-1);
                uMat(timeIndex:numTimes,(varIndex-1)*numBases+1:varIndex*numBases) = uMat(timeIndex:numTimes,(varIndex-1)*numBases+1:varIndex*numBases) + deltaU;
                
                startIndexIR = (varIndex-1)*numBases*numROIs+(baseIndex-1)*numROIs+1;
                deltaU = deltaT*deltaP(indices(pIndex))*r_interpMat(1:numTimes-timeIndex+1,startIndexIR:startIndexIR+numROIs-1);
                uMat(timeIndex:numTimes,:) = uMat(timeIndex:numTimes,:) + deltaU;
                
                uRateMat(timeIndex,(varIndex-1)*numBases+baseIndex) = p(indices(pIndex));
            end
        end

        if strcmp(useGradient,'on')
            [ dummyICMat SMat jacobianMat ] = modelfunct(weights(1,numBases*numTimes+1:end),numBases,uMat,solutionTimes);
        else
            [ dummyICMat SMat] = modelfunct(weights(1,numBases*numTimes+1:end),numBases,uMat,solutionTimes);
        end
        
        
% need to add gradient due to image error
        
        rateErrorMat = SMat - uRateMat;
        gradMat2 = zeros(size(p));
        if strcmp(useGradient,'on')
            index = 0;
            gradMat2 = -rateErrorMat;
            for ip=1:numVariables
                rateErrorJacobian2 = zeros(numTimes,numBases);
                for i=1:numVariables
                    startJac2 = (i-1)*numBases*numVariables + (ip-1)*numBases + 1;
                    startRateErrorJac2 = (i-1)*numBases+1;
                    rateErrorJacobian2 = rateErrorJacobian2 + rateErrorMat(:,startRateErrorJac2:startRateErrorJac2+numBases-1).*jacobianMat(:,startJac2:startJac2+numBases-1);
                end
                for jp=1:numBases
                    startInterp_ip_jp = (ip-1)*numBases*numBases+(jp-1)*numBases + 1;
                    index = (ip-1)*numBases + jp;
                    for kp=1:numTimes
                        for n=kp:numTimes
                            gradMat2(kp,index) = gradMat2(kp,index)+deltaT*sum(r_interpMat(n-kp+1,startInterp_ip_jp:startInterp_ip_jp+numBases-1).*rateErrorJacobian2(n,:));
                        end
                    end
                end
            end
            gradMat2 = (gradMat2(:)')*2/(numVariables*numBases*numTimes); % scale by 2/IMN
            
        end
        penalty = 1*mean(mean(rateErrorMat.*rateErrorMat));

        if (length(refSolutionMat)>0)
            refDiffMat = refSolutionMat - uMat;
            rmsError = sqrt(mean(mean(refDiffMat.*refDiffMat)));
            maxError = max(max(abs(refDiffMat)));
        else
            refDiffMat = [];
            rmsError = [];
            maxError = [];
        end
        
        if (length(refImage)>0)
            imageDiffMat = refImage - iMat;
            imageRmsError = sqrt(mean(mean(imageDiffMat.*imageDiffMat)));
            imageMaxError = max(max(abs(imageDiffMat)));
        else
            imageDiffMat = [];
            imageRmsError = [];
            imageMaxError = [];
        end

        
        % 
        % give user feedback
        %
        modcount = 1; %mod(count,length(p));
        if (modcount==1)
%             figure(3);
%             image([25*uMat(:,1:end/2), 25*uMat(:,end/2+1:end); 25*refSolutionMat(:,1:end/2), 25*refSolutionMat(:,end/2+1:end); 25*abs(refDiffMat); 50*abs(uRateMat); 50*abs(SMat); 10*(10+log(abs(rateErrorMat)))]);
            figure(11);
            image([0.001*iMat, 25*uMat(:,1:end/2), 25*uMat(:,end/2+1:end) 50*abs(uRateMat); 0.001*refImage, 25*refSolutionMat(:,1:end/2), 25*refSolutionMat(:,end/2+1:end) 50*abs(SMat); 0.001*abs(imageDiffMat) 25*abs(refDiffMat) 10*(10+log(abs(rateErrorMat)))]);
            mov = addframe(mov,getframe(gca));
            %image([0 0 0 ; 1 1 1 ; 0 0 0 ; 1 1 1]);
%             image([50*uMat(:,1:11), 25*uMat(:,12:end); 50*refSolutionMat(:,1:11), 25*refSolutionMat(:,12:end); 3000*abs(refDiffMat);  3000*0.5*(abs(uMat)-uMat)]);
%             figure(9);
%             figure(10);
%             image(50*abs([uRateMat; SMat; S_exact; 50*abs(rateErrorMat)]));
        end

        obj = penalty;
%         if (mod(count,1000)==0)
%            tempVal = obj;
%            tempP = p;
%            save tempSolution.mat tempP tempVal;
%         end
        count = count + 1;
%if mod(count,1000)==0
        fprintf('leaving, count=%g, #changed=%g, obj=%g, RMSError=%g, maxError=%g, imgRmsError=%g, imgMaxError=%g\n',count,length(find(deltaP)),obj,rmsError,maxError,imageRmsError,imageMaxError);
%end
        previousP = p;
        objVal = obj;
        gradient = gradMat2;
    end
 
end

