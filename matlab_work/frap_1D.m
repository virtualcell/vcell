function [ finalWeights objFuncVal ] = frap_1D( data, dataTimes, u0, u1, refTimes )
% [finalWeights, objFuncVal] = frap_1D(data, dataTimes, u0, u1, refTimes)
%
% input:
%     data roi matrix, rows for each time, columns for each ROI
%     u0, u1 same format as ROI matrix.
%     dataTimes, refTimes - time arrays for data and reference solutions
%
% output:
%     finalWeights - time weighting vectors for u0 and u1 (and u2)
%     objFuncVal - objective function value
%
% usage: 
%    [p,val] = frap_1D(F_all_Z,F_all_Z_times,u0_Z,u1_Z,u0_Z_times);
%
    u0_interp = interp1(refTimes,u0,dataTimes,'cubic','extrap');
    u1_interp = interp1(refTimes,u1,dataTimes,'cubic','extrap');
    u2_interp = u0_interp;
    for i=1:size(u0_interp,1)
        u2_interp(i,:) = u0_interp(1,:);
    end
    mobilFraction = 0.5;
    % give more 'control points' near discontinuities (around 10 and 20)
    protocolTimes = [0 9.9 linspace(10,20,15) (20+ 8*sqrt(logspace(-2,2,12)))];
    Np = size(protocolTimes,2);
    u0_weight = zeros(1,Np-2);
    u1_weight = zeros(1,Np-2);
    u2_weight = zeros(1,Np-2);
    weights = [ u0_weight u1_weight u2_weight ];
fprintf('weights has size %d by %d\n', size(weights,1), size(weights,2));
%    load p_0.0925_coarse.mat p;
%    weights = p;
    count = 0;
    options = optimset('GradObj','off','MaxFunEvals',100000);
   [X FVAL] = fminunc(@objFunc,weights,options);
%     error = objFunc(weights);
    finalWeights = X;
    objFuncVal = FVAL;
    return;

    function [ objVal ] = objFunc(p)
        solution = zeros(size(data));
        %initialize data to "initial conditions" from data
        N = size(data,1);
        for i=1:N
            solution(i,:)=data(1,:);
        end
        Npp = Np-2;
        % the leading "0 0" in each vector are for time points 0 and 9.9
        % (no stimulus before the stimulus).
% fprintf('p has size %d by %d\n', size(p,1), size(p,2));
% fprintf('protocolTimes has size %d by %d\n', size(protocolTimes,1), size(protocolTimes,2));
        w0 = interp1(protocolTimes,[0 0 p(1:Npp)],dataTimes,'nearest','extrap');
        w1 = interp1(protocolTimes,[0 0 p(Npp+1:2*Npp)],dataTimes,'nearest','extrap');
        w2 = interp1(protocolTimes,[0 0 p(2*Npp+1:3*Npp)],dataTimes,'nearest','extrap');
%         fprintf('data has size %d by %d\n', size(data,1), size(data,2));
%         fprintf('u0_interp has size %d by %d\n', size(u0_interp,1), size(u0_interp,2));
%         fprintf('u1_interp has size %d by %d\n', size(u1_interp,1), size(u1_interp,2));
%         fprintf('u2_interp has size %d by %d\n', size(u2_interp,1), size(u2_interp,2));
%         fprintf('weights has size %d by %d\n', size(w,1), size(w,2));
        u0_input = conv2(w0,[1],u0_interp);
%         fprintf('u0_input has size %d by %d\n', size(u0_input,1), size(u0_input,2));
        u0_input = u0_input(1:N,:);
%         fprintf('... after trim ... u0_input has size %d by %d\n', size(u0_input,1), size(u0_input,2));
        
        u1_input = conv2(w1,[1],u1_interp);
%         fprintf('u1_input has size %d by %d\n', size(u1_input,1), size(u1_input,2));
        u1_input = u1_input(1:N,:);
%         fprintf('... after trim ... u1_input has size %d by %d\n', size(u1_input,1), size(u1_input,2));
 
        u2_input = conv2(w2,[1],u2_interp);
%         fprintf('u2_input has size %d by %d\n', size(u2_input,1), size(u2_input,2));
        u2_input = u2_input(1:N,:);
%         fprintf('... after trim ... u2_input has size %d by %d\n', size(u2_input,1), size(u2_input,2));
        solution = solution + u0_input + u1_input + u2_input;
        modcount = mod(count,100);
        diff = data-solution;
        if (modcount==0)
            figure(1)
            plot(dataTimes,w0,'r',dataTimes,w1,'g',dataTimes,w2,'b');
           figure(3);
           image(solution);
           figure(5);
           image(diff);
        end
%         figure(4);
%         image(data);
        count = count + 1;
        fitError = mean(mean(diff.*diff));
        energyPenalty = mean(p.*p);
        fprintf('leaving, count = %d, fitError = %g, penalty = %g\n',count,fitError,energyPenalty);
        objVal = fitError + energyPenalty;
     end
end
