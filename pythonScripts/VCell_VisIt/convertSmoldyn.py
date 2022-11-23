    #convertSmoldyn.py
    
    #usage convertSmoldyn.py
    # /path/to/SimID_xxxxxxxxxxx_0_
    # /path/to/outputfolder/ImageOutputRootFileName
    # {2|3 dimension}
    # {sets beginTimeIndex, index 0 is beginning}
    # {sets endTimeIndex, last index numTimes-1, must be > endTimeIndex}
    # particle sphere size
    # frame size X
    # frame size Y
    # {0=all variables, 1 or more means number of specified variables} varname1 varname2 

    
    # by Ed Boyce - March 15, 2011
    # University of Connecticut Health Center
    # Center for Cell Analysis and Modeling
    
    # Edit March 30, 2011 by Frank Morgan
    # 1.  Added 2 command line options to specify begin and end time indices to allow
    # creation of smaller movies from subsets of time.
    # 2.  Add try/except to main body of script because java exec(...) was not
    # returning if the script had an error.
    
import sys, os, tokenize, string, commands
try:
    #argStart=sys.argv.index('convertSmoldyn.py')
    argStart = 0;
    
    for argidx in range(len(sys.argv)):
        if (sys.argv[argidx].endswith('convertSmoldyn.py')):
            argStart=sys.argv.index(sys.argv[argidx])
            
    
    inputFilePathRoot=sys.argv[argStart+1]
    outputImageFileRoot=sys.argv[argStart+2]
    if sys.argv[argStart+3]=="2":
        dimension="2"
    else:
        dimension="3"
    
    vcellBeginTimeIndex = int(sys.argv[argStart+4]);
    vcellEndTimeIndex = int(sys.argv[argStart+5]);
    print "vcellBeginTimeIndex="+str(vcellBeginTimeIndex)+" vcellEndTimeIndex="+str(vcellEndTimeIndex); 
    if vcellEndTimeIndex < vcellBeginTimeIndex:
        raise ValueError('vcellEndTimeIndex must be >= vcellBeginTimeIndex')
    
    pointSphereSize = int(sys.argv[argStart+6])
    frameXSize = int(sys.argv[argStart+7])
    frameYSize = int(sys.argv[argStart+8])
    
    particleIncludeList=[]
    includeVarNumber=int(sys.argv[argStart+9])
    if (includeVarNumber>0):
        for vari in range(1,includeVarNumber+1):
            particleIncludeList.append(sys.argv[(argStart+9+vari)])  
    
    simID=inputFilePathRoot[inputFilePathRoot.find("SimID"):]
    
    

    particleTypeList=[]
    particleTypeNumber=0
    fileIterator=1
    print "inputFilePathRoot=" + inputFilePathRoot+"\n"
    print "SimID = "+simID+"\n"
    
    inputFilePathRootPartition= os.path.split(inputFilePathRoot)[1]
    outputFileRoot=os.path.join(outputImageFileRoot,inputFilePathRootPartition)
    print "starting with: " + (inputFilePathRoot+('%03d' %fileIterator)+".smoldynOutput")
    while os.path.exists(inputFilePathRoot+('%03d' %fileIterator)+".smoldynOutput"):
        if fileIterator < (vcellBeginTimeIndex+1) or fileIterator > (vcellEndTimeIndex+1):
            print "Skipping "+inputFilePathRoot+('%03d' %fileIterator)+".smoldynOutput"
            fileIterator = fileIterator+1;
            continue;
        print "Processing "+inputFilePathRoot+('%03d' %fileIterator)+".smoldynOutput"
        ifile=open((inputFilePathRoot+('%03d' %fileIterator)+".smoldynOutput"),'r')
        ofile=open(outputFileRoot+'p3d-'+('%03d' %fileIterator),'w')
        print "Outputting "+ofile.name
        ofile.write('x y z particle\n')
        for line in ifile.readlines():
            lineTokens=string.split(line)
            parenPos=string.find(lineTokens[0],'(')
            pType=lineTokens[0][0:parenPos]
            if (particleTypeList.count(pType)==0):
                particleTypeList.append(pType)
                particleTypeNumber=particleTypeNumber+1
            if (dimension=="2"):
                if (includeVarNumber==0):
                    outputLine=lineTokens[1]+' '+lineTokens[2]+' 0 '+str(particleTypeList.index(pType)+1)+'\n'
                else:
                    if (particleIncludeList.count(pType)==1):    
                        outputLine=lineTokens[1]+' '+lineTokens[2]+' 0 '+str(particleIncludeList.index(pType)+1)+'\n'
            else:
                 if (includeVarNumber==0):
                     outputLine=lineTokens[1]+' '+lineTokens[2]+' '+lineTokens[3]+' '+str(particleTypeList.index(pType)+1)+'\n'
                 else:
                     if (particleIncludeList.count(pType)==1):
                         outputLine=lineTokens[1]+' '+lineTokens[2]+' '+lineTokens[3]+' '+str(particleIncludeList.index(pType)+1)+'\n'
            if (includeVarNumber==0):
                ofile.write(outputLine)
            else:
                if (particleIncludeList.count(pType)==1):
                    ofile.write(outputLine)
        ifile.close()
        ofile.close()
        fileIterator=fileIterator+1
    
    print("Done processing Smoldyn data into Point3D.  Now opening databases in Visit.")
    
    
    # Open and plot the mesh
    
    meshFilePathRoot=inputFilePathRoot[:inputFilePathRoot.index('__')+1]
    
    #print("Opening the .log file: "+ meshFilePathRoot+".log\n")
    visitMeshDB=meshFilePathRoot+".log"
    print("Opening .log file "+visitMeshDB)
    success=OpenDatabase(visitMeshDB,0,"VCellMTMD_1.0")
    AddPlot("Mesh", "membrMesh")
    
    meshAtts=MeshAttributes()
    meshAtts.legendFlag = 0
    meshAtts.smoothingLevel = meshAtts.High
    meshAtts.pointSize=0.01
    meshAtts.pointSizePixels=1
    meshAtts.opacity=0.2
    #meshAtts.opaqueColor = (255,255,255,30)
    
    SetPlotOptions(meshAtts)
    
    aaMesh=AnnotationAttributes()
    aaMesh.axes3D.xAxis.label.visible = 0
    aaMesh.axes3D.yAxis.label.visible = 0
    aaMesh.axes3D.zAxis.label.visible = 0
    aaMesh.databaseInfoFlag = 0 
    aaMesh.userInfoFlag = 0
    aaMesh.legendInfoFlag = 0
    SetAnnotationAttributes(aaMesh)
    
    AddOperator("Smooth", 1)
    SmoothOperatorAtts = SmoothOperatorAttributes()
    SmoothOperatorAtts.numIterations = 40
    SmoothOperatorAtts.relaxationFactor = 0.03
    SmoothOperatorAtts.convergence = 0
    SmoothOperatorAtts.maintainFeatures = 0
    SmoothOperatorAtts.featureAngle = 45
    SmoothOperatorAtts.edgeAngle = 15
    SmoothOperatorAtts.smoothBoundaries = 0
    SetOperatorOptions(SmoothOperatorAtts, 1)
    DrawPlots()
    
    
    # Open and plot the points
    
    print("Opening database series: " + outputFileRoot+"p3d-*")
    visitDB=outputFileRoot+"p3d-* database"
    success=OpenDatabase(visitDB,0,"Point3D_1.0")
    
    
    if (success==1):
        print("Succeeded\n")
    else:
        print("Failed\n")
    AddPlot("Pseudocolor","particle")
    
    # All attribute settings from here on apply to the points plot
    SetActivePlots(1);
    
    if dimension=="2":
        AddOperator("Slice")
        sa = SliceAttributes()
        sa.project2d = 1
        sa.originType = sa.Point
        sa.normal = (0,0,1)
        sa.upAxis = (0,1,0)
        sa.originPoint = (0,0,0)
        SetOperatorOptions(sa)
    
    
    DrawPlots()
    
    pc=PseudocolorAttributes()
    pc.pointType=4
    pc.pointSizePixels = pointSphereSize
    if (includeVarNumber==0):
        pc.max=(len(particleTypeList))
    else:
        pc.max=(len(particleIncludeList))
    if (pc.max==1):
        pc.max=2
    pc.maxFlag=1
    pc.min=1
    pc.minFlag=1
    
    if (includeVarNumber==0):
        pc.max = max (pc.max,len(particleTypeList))
    else:
        pc.max = max (pc.max,len(particleIncludeList))    
              
    SetPlotOptions(pc)
    
    s=SaveWindowAttributes()
    s.outputToCurrentDirectory=0
    s.outputDirectory = outputImageFileRoot
    s.fileName=simID+"-visitOutputFrame"
    
    # 2=JPEG from the possible enumerated list: format = # BMP, CURVE, JPEG, OBJ, PNG, POSTSCRIPT, POVRAY, PPM, RGB, STL, TIFF, ULTRA, VTK, PLY 
    s.format=2 
    s.resConstraint = s.NoConstraint
    s.width = frameXSize
    s.height = frameYSize
    SetSaveWindowAttributes(s)
    
    aa=AnnotationAttributes()
    aa.axes3D.xAxis.label.visible = 0
    aa.axes3D.yAxis.label.visible = 0
    aa.axes3D.zAxis.label.visible = 0
    aa.axesArray.axes.title.visible = 0
    aa.databaseInfoFlag = 0
    aa.userInfoFlag = 0
    SetAnnotationAttributes(aa)
    
    # Setup the legend
    # Plot0001 is the plot id. You can obtain of from GetPlotList()
    L = GetAnnotationObject("Plot0001")
    
    if (includeVarNumber==0):
        L.suppliedLabels = tuple(particleTypeList)
        L.suppliedValues = tuple(range(1,len(particleTypeList)+1))
    else:
        L.suppliedLabels = tuple(particleIncludeList)
        L.suppliedValues = tuple(range(1,len(particleIncludeList)+1))
    
    L.drawLabels = L.Labels
    L.controlTicks = 0
    L.drawMinMax = 0
    L.position=(0,0.4)
    L.orientation = 1
    
    # Make the frames
    for ts in range(TimeSliderGetNStates()):
        SetTimeSliderState(ts)
        DrawPlots()
        SaveWindow()
    
    #print("Doing ffmpeg processing now\n")
    #s=commands.getoutput("ls /home/VCELL/visit/visitframes")
    #print(s+"\n") 
    #ffmpegCommand="ffmpeg -f image2 -r 2 -i /home/VCELL/visit/visitframes/visit%04d.png -vcodec mpeg4 -s vga -vframes 1000 -r 24 -b 200000 "+outputfilename
    #s1=commands.getoutput(ffmpegCommand)
    #print(s1+"\n")
except:
    sys.exit(1)

sys.exit(0);