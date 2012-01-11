function [ regularTimes ] = unchirpTimes(chirpedTimes, S, deltaT )
% [regularTimes] = chirpedTimes(chirpedTimes,S,deltaT)
%
% input:
%     chirpedTImes is a vector of transformed times (e.g. timeSpan = [0 .1 .3 .6])
%     S, deltaT are the scalars diffusive scaling
%     deltaT is the simulation time step
%
% output:
%     regular is the vector of transformed times
%
% the following formulas hold
%   D = S(t+deltaT),
%   tau = S/2*(t*t+deltaT*t)
%   t = 1/2(sqrt(deltaT*deltaT + 8*tau/s) - deltaT)
%
%   where t is the "real" time and tau is the transformed time
%
regularTimes = 0.5*(sqrt(deltaT*deltaT + 8*chirpedTimes/S) - deltaT);