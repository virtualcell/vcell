function [ finalWeights objFuncVal ] = frap_1D_2phase( data, dataTimes, refTimes, u0, u1, u2, u3, u4, u5, u6, u7, u8, u9)
% [finalWeights, objFuncVal] = frap_1D_2phase(data, dataTimes, u0, u1, refTimes)
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
%    [p,val] = frap_1D_2phase(F_all_Z,F_all_Z_times,u0_Z_times,u0_Z,u1_Z,u2_Z,u3_Z,u4_Z,u5_Z,u6_Z,u7_Z,u8_Z,u9_Z);
%
    u0_interp = interp1(refTimes,u0,dataTimes,'cubic','extrap');
    u1_interp = interp1(refTimes,u1,dataTimes,'cubic','extrap');
    u2_interp = interp1(refTimes,u2,dataTimes,'cubic','extrap');
    u3_interp = interp1(refTimes,u3,dataTimes,'cubic','extrap');
    u4_interp = interp1(refTimes,u4,dataTimes,'cubic','extrap');
    u5_interp = interp1(refTimes,u5,dataTimes,'cubic','extrap');
    u6_interp = interp1(refTimes,u6,dataTimes,'cubic','extrap');
    u7_interp = interp1(refTimes,u7,dataTimes,'cubic','extrap');
    u8_interp = interp1(refTimes,u8,dataTimes,'cubic','extrap');
    u9_interp = interp1(refTimes,u9,dataTimes,'cubic','extrap');

    u0_input = zeros(size(data));
    u1_input = zeros(size(data));
    u2_input = zeros(size(data));
    u3_input = zeros(size(data));
    u4_input = zeros(size(data));
    u5_input = zeros(size(data));
    u6_input = zeros(size(data));
    u7_input = zeros(size(data));
    u8_input = zeros(size(data));
    u9_input = zeros(size(data));
    u10_input = zeros(size(data));
    u11_input = zeros(size(data));
    u12_input = zeros(size(data));
    u13_input = zeros(size(data));
    u14_input = zeros(size(data));
    u15_input = zeros(size(data));
    u16_input = zeros(size(data));
    u17_input = zeros(size(data));
    u18_input = zeros(size(data));
    u19_input = zeros(size(data));

    % give more 'control points' near discontinuities (around 10 and 20)
    protocolTimes = [0 9.9 linspace(10,20,15) (20+ 8*sqrt(logspace(-2,2,12)))];
    Np = size(protocolTimes,2);
    weights = zeros(1,20*(Np-2));  % Np-2 to allow for zeros for all source terms ... 20 for 10 modes x 2 phases (immobile and mobile)
fprintf('weights has size %d by %d\n', size(weights,1), size(weights,2));
%    load p_0.0925_coarse.mat p;
%    weights = p;
%    load p_4.5_coarse_10_modes_2phases.mat p;
%     load p_1.4602_coarse_10_modes_2phases.mat p;
%     load p_1.44893_coarse_10_modes_2phases.mat p;
     load p_1.43256_coarse_10_modes_2phases.mat p;
    weights = p;
    count = 0;
    previousP = zeros(size(weights));
    options = optimset('GradObj','off','MaxFunEvals',100000);
   [X FVAL] = fminunc(@objFunc,weights,options);
%     error = objFunc(weights);
    finalWeights = X;
    objFuncVal = FVAL;
    return;

    function [ objVal ] = objFunc(p)
        deltaP = p - previousP;
        solution = zeros(size(data));
        %initialize data to "initial conditions" from data
        N = size(data,1);
        for i=1:N
            solution(i,:)=data(1,:);
        end
        Npp = Np-2;
        % the leading "0 0" in each vector are for time points 0 and 9.9
        % (no stimulus before the stimulus).
if length(find(deltaP(1:Npp)))>0
        w0 = interp1(protocolTimes,[0 0 p(1:Npp)],dataTimes,'nearest','extrap');
        u0_input = conv2(w0,[1],u0_interp);
        u0_input = u0_input(1:N,:);
end
if length(find(deltaP(1:Npp)))>0
        w1 = interp1(protocolTimes,[0 0 p(Npp+1:2*Npp)],dataTimes,'nearest','extrap');
        u1_input = conv2(w1,[1],u1_interp);
        u1_input = u1_input(1:N,:);
end
if length(find(deltaP(2*Npp+1:3*Npp)))>0
        w2 = interp1(protocolTimes,[0 0 p(2*Npp+1:3*Npp)],dataTimes,'nearest','extrap');
        u2_input = conv2(w2,[1],u2_interp);
        u2_input = u2_input(1:N,:);
end
if length(find(deltaP(3*Npp+1:4*Npp)))>0
        w3 = interp1(protocolTimes,[0 0 p(3*Npp+1:4*Npp)],dataTimes,'nearest','extrap');
        u3_input = conv2(w3,[1],u3_interp);
        u3_input = u3_input(1:N,:);
end
if length(find(deltaP(4*Npp+1:5*Npp)))>0
        w4 = interp1(protocolTimes,[0 0 p(4*Npp+1:5*Npp)],dataTimes,'nearest','extrap');
        u4_input = conv2(w4,[1],u4_interp);
        u4_input = u4_input(1:N,:);
end
if length(find(deltaP(5*Npp+1:6*Npp)))>0
        w5 = interp1(protocolTimes,[0 0 p(5*Npp+1:6*Npp)],dataTimes,'nearest','extrap');
        u5_input = conv2(w5,[1],u5_interp);
        u5_input = u5_input(1:N,:);
end
if length(find(deltaP(6*Npp+1:7*Npp)))>0
        w6 = interp1(protocolTimes,[0 0 p(6*Npp+1:7*Npp)],dataTimes,'nearest','extrap');
        u6_input = conv2(w6,[1],u6_interp);
        u6_input = u6_input(1:N,:);
end
if length(find(deltaP(7*Npp+1:8*Npp)))>0
        w7 = interp1(protocolTimes,[0 0 p(7*Npp+1:8*Npp)],dataTimes,'nearest','extrap');
        u7_input = conv2(w7,[1],u7_interp);
        u7_input = u7_input(1:N,:);
end
if length(find(deltaP(8*Npp+1:9*Npp)))>0
        w8 = interp1(protocolTimes,[0 0 p(8*Npp+1:9*Npp)],dataTimes,'nearest','extrap');
        u8_input = conv2(w8,[1],u8_interp);
        u8_input = u8_input(1:N,:);
end
if length(find(deltaP(9*Npp+1:10*Npp)))>0
        w9 = interp1(protocolTimes,[0 0 p(9*Npp+1:10*Npp)],dataTimes,'nearest','extrap');
        u9_input = conv2(w9,[1],u9_interp);
        u9_input = u9_input(1:N,:);
end


        %
        % the following are the immobile sources (no need to convolve just
        % add scaled versions of first row of mode
        %
        numDataTimes = size(dataTimes,1);

if length(find(deltaP(10*Npp+1:11*Npp)))>0
        w10 = interp1(protocolTimes,[0 0 p(10*Npp+1:11*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u10_input(i,:) = w10(i)*u10_input(1,:);
        end
end        
if length(find(deltaP(11*Npp+1:12*Npp)))>0
        w11 = interp1(protocolTimes,[0 0 p(11*Npp+1:12*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u11_input(i,:) = w11(i)*u11_input(1,:);
        end
end        
if length(find(deltaP(12*Npp+1:13*Npp)))>0
        w12 = interp1(protocolTimes,[0 0 p(12*Npp+1:13*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u12_input(i,:) = w12(i)*u12_input(1,:);
        end
end        
if length(find(deltaP(13*Npp+1:14*Npp)))>0
        w13 = interp1(protocolTimes,[0 0 p(13*Npp+1:14*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u13_input(i,:) = w13(i)*u13_input(1,:);
        end
end        
if length(find(deltaP(14*Npp+1:15*Npp)))>0
        w14 = interp1(protocolTimes,[0 0 p(14*Npp+1:15*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u14_input(i,:) = w14(i)*u14_input(1,:);
        end
end        
if length(find(deltaP(15*Npp+1:16*Npp)))>0
        w15 = interp1(protocolTimes,[0 0 p(15*Npp+1:16*Npp)],dataTimes,'nearest','extrap');
       for i=1:numDataTimes
           u15_input(i,:) = w15(i)*u15_input(1,:);
        end
end        
if length(find(deltaP(16*Npp+1:17*Npp)))>0
        w16 = interp1(protocolTimes,[0 0 p(16*Npp+1:17*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u16_input(i,:) = w16(i)*u16_input(1,:);
        end
end        
if length(find(deltaP(17*Npp+1:18*Npp)))>0
        w17 = interp1(protocolTimes,[0 0 p(17*Npp+1:18*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u17_input(i,:) = w17(i)*u17_input(1,:);
        end
 end        
if length(find(deltaP(18*Npp+1:19*Npp)))>0
        w18 = interp1(protocolTimes,[0 0 p(18*Npp+1:19*Npp)],dataTimes,'nearest','extrap');
         for i=1:numDataTimes
           u18_input(i,:) = w18(i)*u18_input(1,:);
        end
end        
if length(find(deltaP(19*Npp+1:20*Npp)))>0
        w19 = interp1(protocolTimes,[0 0 p(19*Npp+1:20*Npp)],dataTimes,'nearest','extrap');
        for i=1:numDataTimes
           u19_input(i,:) = w19(i)*u19_input(1,:);
        end
end


        solution = solution + u0_input + u1_input + u2_input + u3_input + u4_input + u5_input + u6_input + u7_input + u8_input + u9_input;
        solution = solution + u10_input + u11_input + u12_input + u13_input + u14_input + u15_input + u16_input + u17_input + u18_input + u19_input;
                              
        modcount = mod(count,100);
        diff = data-solution;
        if (modcount==0)
%             figure(1)
%             plot(dataTimes,w0,'r',dataTimes,w1,'g',dataTimes,w2,'b');
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
        previousP = p;
        objVal = fitError + energyPenalty;
     end
end
