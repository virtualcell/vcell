classdef frapSolution < handle
    %FRAPSOLUTION Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        % problem to solve
        problem
        
        % discretization
        xarray
        yarray
        A_matrix
        numMeshPoints
        
        % initial total fluorescence in mesh indices
        initialFluorescence
        
        % solution
        solutionTimes
        solutionValues
    end
    
    methods
        function plotInitialConditions(this)
            plotMeshPrivate(this,this.problem.mask, this.initialFluorescence, this.problem.numX, this.problem.numY);
        end
        
        function plotSolution(this,timeIndex)
            plotMeshPrivate(this,this.problem.mask, this.solutionValues(timeIndex,1:this.numMeshPoints)+this.solutionValues(timeIndex,this.numMeshPoints+1:2*this.numMeshPoints), this.problem.numX, this.problem.numY);
        end
        
        function plotMeshPrivate(this, mask, var, numX, numY )
            mesh = zeros(numX,numY);
            for maskj=1:numY
                for maski=1:numX
                    if (mask(maski,maskj)>0)
                        mesh(maski,maskj) = var(mask(maski,maskj));
                    end
                end
            end


            meshmin = min(min(mesh));
            meshmax = max(max(mesh));
            normmesh = (mesh-meshmin)/(meshmax-meshmin)*200;
            figure(1)
            imagesc(normmesh);
            figure(3)
            surf(normmesh);
            figure(1)
        end

        function solve(this)
            Kon = this.problem.Kon;
            Koff = this.problem.Koff;
            y_init = [Koff/(Kon+Koff)*this.initialFluorescence ; Kon/(Koff+Kon)*this.initialFluorescence];
            options = odeset('RelTol',1e-5,'AbsTol',1e-6);
            [this.solutionTimes, this.solutionValues] = ode15s(@(t,y) this.frapPdeRHS_local(t,y),this.solutionTimes,y_init,options);
        end
        
        function [ yp ] = frapPdeRHS_local(this, t, y)
            %ode15s(@frapPdeRHS);
            % y = [M I]
            D = this.problem.D;
            Kon = this.problem.Kon;
            Koff = this.problem.Koff;
            A_laplacian = this.A_matrix; 
            n=length(y)/2;
            yp = zeros(size(y));
            yp(n+1:2*n) = Kon*y(1:n)-Koff*y(n+1:2*n);
            yp(1:n) = -yp(n+1:2*n);  % - kon*y(1:n) + koff*y(n+1:2*n)
            yp(1:n) = yp(1:n) + D*A_laplacian*y(1:n);
        end
        
        function init = getGaussianBleachPattern(this, bleachCenterX, bleachCenterY, bleachRadiusFactor)
            % use ERF (error function) for a Circle convolved with a Gaussian
            x = this.xarray-bleachCenterX;  % center at (0.7,0.7)
            y = this.yarray-bleachCenterY;  % center at (0.7,0.7)
            r_squared = x.*x+y.*y;
            init = erf((r_squared'*bleachRadiusFactor));
        end
        
        function this = buildMatrix(this)
            numX = this.problem.numX;
            numY = this.problem.numY;
            mask = this.problem.mask;
            iarray = 0 * (1:5*numX*numY); % initialized with a little extra room
            jarray = 0 * (1:5*numX*numY); % initialized with a little extra room
            sarray = 0 * (1:5*numX*numY); % initialized with a little extra room
            this.xarray = 0 * (1:numX*numY); % initialized with a little extra room
            this.yarray = 0 * (1:numX*numY); % initialized with a little extra room
            elementIndex = 1;
            numOffDiagonals_x = 0;
            numOffDiagonals_y = 0;
            indexDiag = 0;
            D = this.problem.D;
            h_x = this.problem.domainSizeX/(this.problem.numX-1);
            lambda_x = D/h_x/h_x;
            h_y = this.problem.domainSizeY/(this.problem.numY-1);
            lambda_y = D/h_y/h_y;
            for j=1:numY
                for i=1:numX
                    if (mask(i,j)>0)
                        indexDiag = indexDiag+1;
                        if mask(i,j) ~= indexDiag
                            display('indexing error');
                        end
                        % count number of nonzeros
                        numOffDiagonals_x = 0; % diagonal
                        numOffDiagonals_y = 0; % diagonal
                        if (j>1) && mask(i,j-1)>0 
                            numOffDiagonals_y = numOffDiagonals_y + 1;   
                        end;
                        if (j<numY) && mask(i,j+1)>0 
                            numOffDiagonals_y = numOffDiagonals_y + 1;   
                        end;
                        if (i>1)  && mask(i-1,j)>0
                            numOffDiagonals_x = numOffDiagonals_x + 1;   
                        end;
                        if (i<numX)  && mask(i+1,j)>0
                            numOffDiagonals_x = numOffDiagonals_x + 1;   
                        end;

                        % minus Y
                        if (j>1)  && mask(i,j-1)>0
                            iarray(elementIndex) = indexDiag;
                            jarray(elementIndex) = mask(i,j-1); %indexDiag - numX;
                            sarray(elementIndex) = lambda_y;
                            elementIndex = elementIndex + 1;
                        end

                        % minus X
                        if  i>1 && mask(i-1,j)>0
                            iarray(elementIndex) = indexDiag;
                            jarray(elementIndex) = mask(i-1,j); %indexDiag - 1;
                            sarray(elementIndex) = lambda_x;
                            elementIndex = elementIndex + 1;
                        end

                        % Diagonal
                        iarray(elementIndex) = indexDiag;
                        jarray(elementIndex) = indexDiag;
                        sarray(elementIndex) = - numOffDiagonals_x * lambda_x - numOffDiagonals_y * lambda_y;
                        this.xarray(indexDiag) = (i-1)*h_x;
                        this.yarray(indexDiag) = (j-1)*h_y;
                        elementIndex = elementIndex + 1;

                        % positive X
                        if (i<numX) && mask(i+1,j)>0
                            iarray(elementIndex) = indexDiag;
                            jarray(elementIndex) = mask(i+1,j); %indexDiag + 1;
                            sarray(elementIndex) = lambda_x;
                            elementIndex = elementIndex + 1;
                        end

                        % positive Y
                        if (j<numY) && mask(i,j+1)>0
                            iarray(elementIndex) = indexDiag;
                            jarray(elementIndex) = mask(i,j+1); %indexDiag + numX;
                            sarray(elementIndex) = lambda_y;
                            elementIndex = elementIndex + 1;
                        end
            %         else
            %            % Diagonal
            %             iarray(elementIndex) = indexDiag;
            %             jarray(elementIndex) = indexDiag;
            %             sarray(elementIndex) = 1;S
            %             elementIndex = elementIndex + 1;
                    end
                end
            end
            this.numMeshPoints = indexDiag;
            iarray = iarray(1:elementIndex-1);
            jarray = jarray(1:elementIndex-1);
            sarray = sarray(1:elementIndex-1);
            this.xarray = this.xarray(1:this.numMeshPoints);
            this.yarray = this.yarray(1:this.numMeshPoints);
            this.A_matrix = sparse(iarray,jarray,sarray);

        end
    end
    
end

