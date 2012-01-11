function [ chirpedTimes ] = chirpTimes(regularTimes, S, deltaT )
% [chirpedTimes] = chirpedTimes(regularTimes,S,deltaT)
%
% input:
%     regularTimes is a vector of untransformed times (e.g. regularTimes = [0 1 2 3 4 5])
%     S, deltaT are the scalars diffusive scaling
%     deltaT is the simulation time step
%
% output:
%     chirpedTimes is the vector of transformed times
%
% the following formulas hold
%   D = S(t+deltaT),
%   tau = S/2*(t*t+deltaT*t)
%   t = 1/2(sqrt(deltaT*deltaT + 8*tau/s) - deltaT)
%
%   where t is the "real" time and tau is the transformed time
%
chirpedTimes = S/2.0*(regularTimes.*regularTimes+deltaT*regularTimes);
