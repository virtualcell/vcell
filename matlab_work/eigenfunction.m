% N=100;
% M=100;
% % set up tridiagonal matrix for 1D diffusion
% A=gallery('tridiag',-ones(N-1,1),2*ones(N,1),-ones(N-1,1));
% A(N,N)=1;
% A(1,1)=1;
% 
% [V,D] = eigs(A,M,'SA');
% 
% display(diag(D)')
% 
% Dreciprical = diag(eye(M)*(ones(M,1)./(diag(D)+1)));
% 
% % 1/(lambda+1) diagonal matrix
% plot(V*(Dreciprical.^3))
% 



domainSize = 1;
problemSize = 300;
numSmallEigenvalues = 150;
numLargeEigenvalues = 0;
D = 1;
radius = problemSize/2-3;
h = domainSize/(problemSize-1);
lambda = D/h/h;

numX = problemSize;
numY = problemSize*2;
mask = zeros(numX,numY);
maskIndex = 1;
for maskj=1:numY
    for maski=1:numX
        if (((maski-(numX/2.0))*(maski-(numX/2.0)) + (maskj-(numY/2.0))*(maskj-(numY/2.0))<radius*radius) && not( ((maski-(numX/4.0))*(maski-(numX/4.0)) + (maskj-(numY/4.0))*(maskj-(numY/4.0))<(0.8*radius*radius))))
            mask(maski,maskj) = maskIndex;
            maskIndex = maskIndex+1;
        end
    end
end


display('populating sparse matrix (efficient way)');
tic

iarray = 0 * (1:5*numX*numY); % initialized with a little extra room
jarray = 0 * (1:5*numX*numY); % initialized with a little extra room
sarray = 0 * (1:5*numX*numY); % initialized with a little extra room
elementIndex = 1;
numOffDiagonals = 0;
indexDiag = 0;
for j=1:numY
    for i=1:numX
        if (mask(i,j)>0)
            indexDiag = indexDiag+1;
            if mask(i,j) ~= indexDiag
                display('indexing error');
            end
            % count number of nonzeros
            numOffDiagonals = 0; % diagonal
            if (j>1) & mask(i,j-1)>0 
                numOffDiagonals = numOffDiagonals + 1;   
            end;
            if (j<numY) & mask(i,j+1)>0 
                numOffDiagonals = numOffDiagonals + 1;   
            end;
            if (i>1)  & mask(i-1,j)>0
                numOffDiagonals = numOffDiagonals + 1;   
            end;
            if (i<numX)  & mask(i+1,j)>0
                numOffDiagonals = numOffDiagonals + 1;   
            end;

            % minus Y
            if (j>1)  & mask(i,j-1)>0
                iarray(elementIndex) = indexDiag;
                jarray(elementIndex) = mask(i,j-1); %indexDiag - numX;
                sarray(elementIndex) = lambda;
                elementIndex = elementIndex + 1;
            end

            % minus X
            if  i>1 & mask(i-1,j)>0
                iarray(elementIndex) = indexDiag;
                jarray(elementIndex) = mask(i-1,j); %indexDiag - 1;
                sarray(elementIndex) = lambda;
                elementIndex = elementIndex + 1;
            end

            % Diagonal
            iarray(elementIndex) = indexDiag;
            jarray(elementIndex) = indexDiag;
            sarray(elementIndex) = - numOffDiagonals * lambda;
            elementIndex = elementIndex + 1;

            % positive X
            if (i<numX) & mask(i+1,j)>0
                iarray(elementIndex) = indexDiag;
                jarray(elementIndex) = mask(i+1,j); %indexDiag + 1;
                sarray(elementIndex) = lambda;
                elementIndex = elementIndex + 1;
            end

            % positive Y
            if (j<numY) & mask(i,j+1)>0
                iarray(elementIndex) = indexDiag;
                jarray(elementIndex) = mask(i,j+1); %indexDiag + numX;
                sarray(elementIndex) = lambda;
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
iarray = iarray(1:elementIndex-1);
jarray = jarray(1:elementIndex-1);
sarray = sarray(1:elementIndex-1);

matrix2 = sparse(iarray,jarray,sarray);
% full(matrix2)
toc
% figure(1);
% spy(matrix);
% figure(2);
% spy(matrix2);
% full(matrix)
% full(matrix2)
% 
%nonzeros(matrix-matrix2)
matrix = matrix2;
V1=0;
D1=0;
V2=0;
D2=0;
if (numSmallEigenvalues>0)
    display(sprintf('\nstarting eigs() %d smallest', numSmallEigenvalues));    
    tic();
    [V1,D1] = eigs(matrix,numSmallEigenvalues,'SM');
    toc();
    display(sprintf('done with eigs() %d smallest', numSmallEigenvalues));
   
    figure(3);
    for ii = 1:numSmallEigenvalues
        % if not masked, reshape is OK, otherwise
%         mesh = reshape(V1(:,ii),numX,numY);

        mesh = zeros(numX,numY);
        for maskj=1:numY
            for maski=1:numX
                if (mask(maski,maskj)>0)
                    mesh(maski,maskj) = V1(mask(maski,maskj),ii);
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
       % pause();
        pause(0.5);
        display(sprintf('eigenvalue(%d)=%g',ii,D1(ii,ii)));
    end
end
if (numLargeEigenvalues>0)
    display(sprintf('\nstarting with eigs() %d largest', numLargeEigenvalues));
    tic();
    [V2,D2] = eigs(matrix,numLargeEigenvalues,'LM');
    toc();
    display(sprintf('done with eigs() %d largest', numLargeEigenvalues));
end
% if (numSmallEigenvalues>0)
%     display(sprintf('\nsmallest'));
%     spdiags(D1)
% end
% if (numLargeEigenvalues>0)
%     display(sprintf('\nlargest'));
%     spdiags(D2)
% end




