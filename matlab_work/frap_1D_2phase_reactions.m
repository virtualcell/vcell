function [ finalWeights objFuncVal ] = frap_1D_2phase_reactions( data, dataTimes, refTimes, u, uZ)
% [finalWeights, objFuncVal] = frap_1D_2phase_reactions(data, dataTimes, refTimes, u, uZ)
%
% input:
%     data is an roi matrix, rows for each time, columns for each ROI
%     u is a cell array of matrices for 3D ROI averages of solution for each mode (the average solution over that ROI).
%     uZ is a cell array of matrices for 2D ROI averages of simulated image for each mode (same format as data)
%     dataTimes - is the linearly sampled time array for experimental data (data)
%     refTimes - is the log-scaled times for the mode reference solutions (both u and uZ).
%
% output:
%     finalWeights - time weighting vectors for u0 and u1 (and u2)
%     objFuncVal - objective function value
%
% usage: 
%    u = {u0 u1 u2 u3 u4 u5 u6 u7 u8 u9};
%    uZ = {u0_Z u1_Z u2_Z u3_Z u4_Z u5_Z u6_Z u7_Z u8_Z u9_Z};
%    [p,val] = frap_1D_2phase_reactions(F_all_Z,F_all_Z_times,u0_times,u, uZ);
%
    numModes = size(u,2);
    numDataTimes = size(dataTimes,2);
    
    for i=1:numModes
        u_interp{i} = interp1(refTimes,u{i},dataTimes,'cubic','extrap');
        u_interp_immobile{i} = interp1(refTimes,u{i},dataTimes,'cubic','extrap');
        u_mobile{i} = zeros(size(data));
        u_immobile{i} = zeros(size(data));
        u_interpZ{i} = interp1(refTimes,uZ{i},dataTimes,'cubic','extrap');
        u_interpZ_immobile{i} = interp1(refTimes,uZ{i},dataTimes,'cubic','extrap');
        u_mobileZ{i} = zeros(size(data));
        u_immobileZ{i} = zeros(size(data));
    end
    mobileRates = zeros(numDataTimes,numModes);
    immobileRates = zeros(numDataTimes,numModes);
    for i=1:size(u,2)
        u_interp_immobile{i} = zeros(size(data));
        u_interpZ_immobile{i} = zeros(size(data));
        for j=1:length(dataTimes)
            u_interp_immobile{i}(j,:) = u{i}(1,:);
            u_interpZ_immobile{i}(j,:) = uZ{i}(1,:);
        end
    end

    bleachMask = zeros(size(data));
    for i=1:length(dataTimes)
        for j=1:numModes
            if i>=10*5 && i<=20*5 && j>=8 && j<=11
                bleachMask(i,j) = 1.0;
            end
        end
    end

    % give more 'control points' near discontinuities (around 10 and 20)
    protocolTimes = [0 9.9 linspace(10,20,15) (20+ 8*sqrt(logspace(-2,2,12)))];
    numExtraParameters = 0;
    Np = size(protocolTimes,2);
    weights = zeros(1,numExtraParameters+2*numModes*(Np-2));  % Np-2 to allow for zeros for all source terms ... 20 for 10 modes x 2 phases (immobile and mobile)
fprintf('weights has size %d by %d\n', size(weights,1), size(weights,2));
%    load p_0.0925_coarse.mat p;
%    weights = p;
%    load p_4.5_coarse_10_modes_2phases.mat p;
%     load p_1.4602_coarse_10_modes_2phases.mat p;
%     load p_1.44893_coarse_10_modes_2phases.mat p;
%      load p_1.43256_coarse_10_modes_2phases.mat p;
%load p_4.0776_10modes_19reg_Reactions.mat p;
%weights = p;

load tempSolution.mat tempP;
weights = tempP;
    count = 0;
    previousP = zeros(size(weights));
    options = optimset('GradObj','off','MaxFunEvals',10000000);
   [X FVAL] = fminunc(@objFunc,weights,options);
%     error = objFunc(weights);
    finalWeights = X;
    objFuncVal = FVAL;
    return;

    function [ objVal ] = objFunc(p)
        % where p(1) is the mobile fraction
        % w1(t) mobile samples ... wN(t) mobile control points samples
        % w1(t) immobile samples ... wN(t) immobile control points samples
        deltaP = p - previousP;
        %initialize data to "initial conditions" from data
        N = size(data,1);
        mobileFractionParameter = p(1);
        mobileFractionParameter = 0.5;
        %
        % scale rest of parameters by 1e-6 so that they are order 1
        %
        
        
        %
%         KonParameter = p(2);
%         KoffParameter = p(3);
%         startIndexOfTimeFunctions = 4;
startIndexOfTimeFunctions = numExtraParameters + 1;
        Npp = Np-2;
        % the leading "0 0" in each vector are for time points 0 and 9.9
        % (no stimulus before the stimulus).
        for i=1:size(u_interp,2)
            startIndex = startIndexOfTimeFunctions + (i-1)*Npp;
            endIndex = startIndex + Npp - 1;
%fprintf('mobile ... i = %g, startIndex %g, endIndexd %g\n',i,startIndex,endIndex);
            if length(find(deltaP(startIndex:endIndex)))>0
                w = interp1(protocolTimes,[0 0 p(startIndex:endIndex)],dataTimes,'nearest','extrap');
                u_mobile{i} = conv2(w,[1],u_interp{i});
                u_mobile{i} = u_mobile{i}(1:N,:);
                u_mobileZ{i} = conv2(w,[1],u_interpZ{i});
                u_mobileZ{i} = u_mobileZ{i}(1:N,:);
                mobileRates(:,i) = w;
            end
        end
        %
        % the following are the immobile sources (no need to convolve just
        % add scaled versions of first row of mode
        %

        for i=1:size(u_interp,2)
            startIndex = startIndexOfTimeFunctions + (numModes + i-1)*Npp;
            endIndex = startIndex + Npp - 1;
% fprintf('i = %g, N = %g, Np = %g, Npp = %g, size(u_interp)=%g, size(p)=%g\n',i,N,Np,Npp,size(u_interp,2),size(p,2));
% fprintf('immobile ... i=%g, startIndex %g, endIndexd %g, startIndexOfTimeFunctions=%g\n',i,startIndex,endIndex,startIndexOfTimeFunctions);
            if length(find(deltaP(startIndex:endIndex)))>0
                w = interp1(protocolTimes,[0 0 p(startIndex:endIndex)],dataTimes,'nearest','extrap');
                u_immobile{i} = conv2(w,[1],u_interp_immobile{i});
                u_immobile{i} = u_immobile{i}(1:N,:);
                u_immobileZ{i} = conv2(w,[1],u_interpZ_immobile{i});
                u_immobileZ{i} = u_immobileZ{i}(1:N,:);
                immobileRates(:,i) = w;
            end        
        end

        mobileSolution = zeros(size(data));
        immobileSolution = zeros(size(data));
        mobileRates = zeros(size(data));
        immobileRates = zeros(size(data));
        simImage = zeros(size(data));
        % initialize solution
        for i=1:N
            mobileSolution(i,:) = ones(1,numModes); %mobileFractionParameter*data(1,:);
            immobileSolution(i,:) = ones(1,numModes); %(1-mobileFractionParameter)*data(1,:);
            simImage(i,:)=data(1,:);
        end
        for i=1:size(u_interp,2)
            mobileSolution = mobileSolution + u_mobile{i};
            immobileSolution = immobileSolution + u_immobile{i};
            simImage = simImage + u_mobile{i} + u_immobileZ{i};
        end
                              
        %
        % compute error in image
        %
        diff = data-simImage;
        fitError = mean(mean(diff.*diff));

        %
        % compute penalty due to regularization
        %
        energyPenalty = 0;  % 1000*mean(p(2:end).*p(2:end));

        %
        % compute physical penalty
        %
        volumeFractionPenalty = 0;
        if mobileFractionParameter<0
            volumeFractionPenalty = 100 + (mobileFractionParameter*mobileFractionParameter - mobileFractionParameter)*10000
        end        
        if mobileFractionParameter>1
            volumeFractionPenalty = 100 + ((mobileFractionParameter-1)*(mobileFractionParameter-1) + (mobileFractionParameter-1))*10000
        end
        
        %
        % negative concentration penalty - look at the L_infinity norm (max
        % negative value).
        %
        % .... we should probably use a lagrange multiplier for this
        % inequality rather than a penalty ......
        %
        negativeMobile = abs(mobileSolution)-mobileSolution;
        negMobileError = mean(mean(negativeMobile.*negativeMobile));
        negativeImmobile = abs(immobileSolution)-immobileSolution;
        negImmobileError = mean(mean(negativeImmobile.*negativeImmobile));
        negativePenalty = 100000*(negMobileError + negMobileError + negMobileError + negImmobileError*negImmobileError + negImmobileError);

        %
        % assume that the data has already been corrected for bleach while
        % monitoring ... if that is the case, anything outside the
        % bleaching protocol should adhere strictly to the binding
        % reactions and anything inside the bleaching should adhere to both
        % the known laser bleaching and the binding reactions.
        %
        % mobile fluorescent pool
        % ... calculate rates based on pool solutions.
        B = 1;
        Kf = 1;
        Kr = 1.0;
        bindingReactionRate = Kf*B*mobileSolution -Kr*immobileSolution;
        
        
        
        
        %%
        % we need a one-to-one correspondance between the solution and the
        % source terms (e.g. the 3D ROIs should be the same as the 3D basis
        % functions ... or we need an accurate mapping like the 3D rois are
        % just a further decomposition of the basis functions.
        %
        % the computation betw
        % 
        
        
        
        
        mobileRateError = mobileRates - ( -bindingReactionRate - bleachMask.*mobileSolution);
        immobileRateError = immobileRates - (bindingReactionRate - bleachMask.*immobileSolution);
        mobileRatePenalty = 10000*mean(mean(mobileRateError.*mobileRateError));
        immobileRatePenalty = 10000*mean(mean(immobileRateError.*immobileRateError));
        
        %
        % 
        % give user feedback
        %
        modcount = mod(count,100000);
        if (modcount==0)
%             figure(1)
%             plot(dataTimes,w0,'r',dataTimes,w1,'g',dataTimes,w2,'b');

%            figure(1);
%            image(simImage);
%            figure(2);
%            image(30*mobileSolution);
%            figure(3);
%            image(30*immobileSolution);
%            figure(4);
%            image(30*diff);
%            figure(5);
%            plot(dataTimes,mean(immobileSolution(:,1:3)'),dataTimes,mean(immobileSolution(:,4:7)'));
%            figure(6);
%            plot(dataTimes,mean(mobileSolution(:,1:3)'),dataTimes,mean(mobileSolution(:,4:7)'));
%            figure(7);
%            plot(dataTimes,mean(data(:,1:3)'),dataTimes,mean(data(:,4:7)'),dataTimes,mean(simImage(:,1:3)'),dataTimes,mean(simImage(:,4:7)'));
        end
        if (mod(count,1000)==0)
           tempVal = fitError + energyPenalty + volumeFractionPenalty + negativePenalty + mobileRatePenalty + immobileRatePenalty;
           tempP = p;
           save tempSolution.mat tempP tempVal;
        end
%         figure(4);
%         image(data);
        count = count + 1;
        fprintf('leaving, count=%g, mobileFrac=%g, fitErr=%g, energy=%g, volFract=%g, negative=%g, mobileRate=%g, immobileRate=%g\n',count,mobileFractionParameter,fitError,energyPenalty,volumeFractionPenalty,negativePenalty,mobileRatePenalty,immobileRatePenalty);
        previousP = p;
        objVal = fitError + energyPenalty + volumeFractionPenalty + negativePenalty + mobileRatePenalty + immobileRatePenalty;
     end
end
