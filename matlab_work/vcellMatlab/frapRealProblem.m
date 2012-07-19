classdef frapRealProblem < handle
    %FRAPPROBLEM Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        % model parameters
        D
        Kon
        Koff
                   
        % cell geometry sampling
        mask
        expFluorescence
        timeStamps
        numX
        numY
        domainSizeX
        domainSizeY

    end
    
    methods
        function loadProblemFromData(this)
            load('C:\VirtualMicroscopy\FRAP_3D\2DnewMoon65791575_ds1_strictTol_5s_diff0.05_withNoise.mat'); % new moon domain size 1
            load('C:\VirtualMicroscopy\FRAP_3D\2DnewMoon_ds1_mask.mat');  %new moon
            % load('C:\VirtualMicroscopy\FRAP_3D\2Dartificial64630073.mat'); %test2 image
            % loading cell mask
            this.mask = zeros(this.numX,this.numY);
            maskIndex = 1;
            for j=1:this.numY
                for i=1:this.numX
                    if (CellMask(i,j) > 0)
                        this.mask(i,j) = maskIndex;
                        maskIndex = maskIndex+1;
                    end
                end
            end
            % loading FRAP exp time stamps
            timeLen = length(ExperimentalTimeStamps);
            this.timeStamps = zeros(timeLen,1);
            for timeIdx = 1:timeLen
                this.timeStamps(timeIdx) = ExperimentalTimeStamps(timeIdx);
            end
            % loading FRAP data and save it in matrix --- numTimes x numMeshpoints(non-zero points) 
            for timeIdx = 1:timeLen
                meshIdx = 0;
                timeImg = ImageDataSet{timeIdx,1};
                for j=1:this.numY
                    for i=1:this.numX
                        if (this.mask(i,j) > 0)
                            meshIdx = meshIdx + 1;
                            if this.mask(i,j) ~= meshIdx
                                display('indexing error when creating fluorescence data');
                            end
                            this.expFluorescence(timeIdx,meshIdx) = timeImg(i,j);
                        end
                    end
                end 
            end
        end
    end
    
end

