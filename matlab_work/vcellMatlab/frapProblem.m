classdef frapProblem < handle
    %FRAPPROBLEM Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        % model parameters
        D
        Kon
        Koff
        
        % cell geometry
        domainSizeX
        domainSizeY
        cell1Radius
        cell1CenterX
        cell1CenterY
        cell2Radius
        cell2CenterX
        cell2CenterY
        
        % cell geometry sampling
        mask
        numX
        numY

    end
    
    methods
        function initMask(this)
            this.mask = zeros(this.numX,this.numY);
            maskIndex = 1;
            R1 = this.cell1Radius;
            R2 = this.cell2Radius;
            for maskj=1:this.numY
                y = maskj*this.domainSizeY/this.numY;
                for maski=1:this.numX
                    x = maski*this.domainSizeX/this.numX;
                    R1_x = x - this.cell1CenterX;
                    R1_y = y - this.cell1CenterY;
                    R2_x = x - this.cell2CenterX;
                    R2_y = y - this.cell2CenterY;
                    if ((R1_x*R1_x + R1_y*R1_y)<R1*R1) && not( (R2_x*R2_x + R2_y*R2_y)<(R2*R2))
                        this.mask(maski,maskj) = maskIndex;
                        maskIndex = maskIndex+1;
                    end
                end
            end
        end
    end
    
end

