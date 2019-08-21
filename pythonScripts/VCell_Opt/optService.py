import argparse
import sys
import traceback
from __builtin__ import isinstance

import COPASI
import vcellopt.ttypes as VCELLOPT
import tempfile
import os
import os.path

from thrift.TSerialization import TTransport
from thrift.TSerialization import TBinaryProtocol
from thrift.TSerialization import deserialize
from thrift.TSerialization import serialize


def main():
    #sys.setrecursionlimit(100000)
    try:
        #--------------------------------------------------------------------------
        # parse input and deserialize the (thrift) optimization problem
        #--------------------------------------------------------------------------
        parser = argparse.ArgumentParser()
        parser.add_argument("optfile",help="filename of input optimization file")
        parser.add_argument("resultfile",help="filename of optimization results")
        args = parser.parse_args()
        assert isinstance(args.resultfile, str)
        resultFile = args.resultfile

        f_optfile = open(args.optfile, "rb")
        blob_opt = f_optfile.read()
        print("read "+str(len(blob_opt))+" bytes from "+args.optfile)
        f_optfile.close()

        '''
        struct OptProblem {
	        1: required string mathModelSbmlFile;
	        2: required int numberOfOptimizationRuns;
	        3: required ParameterDescriptionList parameterDescriptionList;
	        4: required ReferenceVariableList referenceVariableList;
	        5: required string experimentalDataCSV;
	        6: required CopasiOptimizationMethod optimizationMethod;
        }
        '''
        vcellOptProblem = VCELLOPT.OptProblem()
        protocol_factory = TBinaryProtocol.TBinaryProtocolFactory
        deserialize(vcellOptProblem, blob_opt, protocol_factory = protocol_factory())
        print("done with deserialization")

        #-----------------------------------------------------------------------------
        # add CDataModel
        #
        assert(COPASI.CCopasiRootContainer.getRoot() != None)
        # create a datamodel
        dataModel = COPASI.CCopasiRootContainer.addDatamodel();
        assert(isinstance(dataModel,COPASI.CCopasiDataModel))
        assert(COPASI.CCopasiRootContainer.getDatamodelList().size() == 1)

        try:
            #sbmlFile = "C:\\COPASI-4.19.140-Source\\copasi\\bindings\\python\\examples\\exampleDeni.xml"
            sbmlString = vcellOptProblem.mathModelSbmlContents
            dataModel.importSBMLFromString(str(sbmlString))
            print("data model loaded")
        except:
            e_info = sys.exc_info()
            traceback.print_exception(e_info[0],e_info[1],e_info[2],file=sys.stdout)
            sys.stderr.write("exception: error importing sbml file: "+str(e_info[0])+": "+str(e_info[1])+"\n")
            sys.stderr.flush()
            return -1

        model = dataModel.getModel()
        assert(isinstance(model,COPASI.CModel))
        model.compileIfNecessary()

        printModel(model)

        mathContainer = model.getMathContainer()
        assert(isinstance(mathContainer,COPASI.CMathContainer))
        # dataModel.saveModel("optModel.cps", True)


        #---------------------------------------------------------------------------
        # add CFitTask
        #---------------------------------------------------------------------------
        fitTask = dataModel.addTask(COPASI.CTaskEnum.parameterFitting)
        assert(isinstance(fitTask, COPASI.CFitTask))

        '''
        enum OptimizationMethodType {
            EvolutionaryProgram,
            SRES,
            GeneticAlgorithm,
            GeneticAlgorithmSR,
            HookeJeeves,
            LevenbergMarquardt,
            NelderMead,
            ParticleSwarm,
            RandomSearch,
            SimulatedAnnealing,
            SteepestDescent,
            Praxis,
            TruncatedNewton
        }
        '''
        methodTypeDict = dict()
        methodTypeDict[VCELLOPT.OptimizationMethodType.EvolutionaryProgram] = COPASI.CTaskEnum.EvolutionaryProgram
        methodTypeDict[VCELLOPT.OptimizationMethodType.SRES] = COPASI.CTaskEnum.SRES
        methodTypeDict[VCELLOPT.OptimizationMethodType.GeneticAlgorithm] = COPASI.CTaskEnum.GeneticAlgorithm
        methodTypeDict[VCELLOPT.OptimizationMethodType.GeneticAlgorithmSR] = COPASI.CTaskEnum.GeneticAlgorithmSR
        methodTypeDict[VCELLOPT.OptimizationMethodType.HookeJeeves] = COPASI.CTaskEnum.HookeJeeves
        methodTypeDict[VCELLOPT.OptimizationMethodType.LevenbergMarquardt] = COPASI.CTaskEnum.LevenbergMarquardt
        methodTypeDict[VCELLOPT.OptimizationMethodType.NelderMead] = COPASI.CTaskEnum.NelderMead
        methodTypeDict[VCELLOPT.OptimizationMethodType.ParticleSwarm] = COPASI.CTaskEnum.ParticleSwarm
        methodTypeDict[VCELLOPT.OptimizationMethodType.RandomSearch] = COPASI.CTaskEnum.RandomSearch
        methodTypeDict[VCELLOPT.OptimizationMethodType.SimulatedAnnealing] = COPASI.CTaskEnum.SimulatedAnnealing
        methodTypeDict[VCELLOPT.OptimizationMethodType.SteepestDescent] = COPASI.CTaskEnum.SteepestDescent
        methodTypeDict[VCELLOPT.OptimizationMethodType.Praxis] = COPASI.CTaskEnum.Praxis
        methodTypeDict[VCELLOPT.OptimizationMethodType.TruncatedNewton] = COPASI.CTaskEnum.TruncatedNewton

        #
        # set CFitMethod
        #
        copasiFitMethodType = methodTypeDict[vcellOptProblem.optimizationMethod.optimizationMethodType]
        if (copasiFitMethodType not in fitTask.getValidMethods()):
            print "fit method not allowed"
            return 1
        fitTask.setMethodType(copasiFitMethodType)
        fitMethod = fitTask.getMethod()
        assert(isinstance(fitMethod,COPASI.COptMethod))

        '''
        enum OptimizationParameterType {
            Number_of_Generations,
            Number_of_Iterations,
            Population_Size,
            Random_Number_Generator,
            Seed,
            IterationLimit,
            Tolerance,
            Rho,
            Scale,
            Swarm_Size,
            Std_Deviation,
            Start_Temperature,
            Cooling_Factor,
            Pf
        }
        '''
        methodParamDict = dict()
        methodParamDict[VCELLOPT.OptimizationParameterType.Number_of_Generations] = "Number of Generations"
        methodParamDict[VCELLOPT.OptimizationParameterType.Number_of_Iterations] = "Number of Iterations"
        methodParamDict[VCELLOPT.OptimizationParameterType.Population_Size] = "Population Size"
        methodParamDict[VCELLOPT.OptimizationParameterType.Random_Number_Generator] = "Random Number Generator"
        methodParamDict[VCELLOPT.OptimizationParameterType.Seed] = "Seed"
        methodParamDict[VCELLOPT.OptimizationParameterType.IterationLimit] = "Iteration Limit"
        methodParamDict[VCELLOPT.OptimizationParameterType.Tolerance] = "Tolerance"
        methodParamDict[VCELLOPT.OptimizationParameterType.Rho] = "Rho"
        methodParamDict[VCELLOPT.OptimizationParameterType.Scale] = "Scale"
        methodParamDict[VCELLOPT.OptimizationParameterType.Swarm_Size] = "Swarm Size"
        methodParamDict[VCELLOPT.OptimizationParameterType.Std_Deviation] = "Std Deviation"
        methodParamDict[VCELLOPT.OptimizationParameterType.Start_Temperature] = "Start Temperature"
        methodParamDict[VCELLOPT.OptimizationParameterType.Cooling_Factor] = "Cooling Factor"
        methodParamDict[VCELLOPT.OptimizationParameterType.Pf] = "Pf"

        #
        # set FitMethod parameters
        #
        '''
        <CopasiOptimizationMethod name="Evolutionary Programming">
            <CopasiOptimizationParameter name="Number of Generations" value="200.0" dataType="int"/>
            <CopasiOptimizationParameter name="Population Size" value="20.0" dataType="int"/>
            <CopasiOptimizationParameter name="Random Number Generator" value="1.0" dataType="int"/>
            <CopasiOptimizationParameter name="Seed" value="0.0" dataType="int"/>
        </CopasiOptimizationMethod>


        <Method name="Evolutionary Programming" type="EvolutionaryProgram">
            <Parameter name="Number of Generations" type="unsignedInteger" value="444"/>
            <Parameter name="Population Size" type="unsignedInteger" value="77"/>
            <Parameter name="Random Number Generator" type="unsignedInteger" value="5"/>
            <Parameter name="Seed" type="unsignedInteger" value="44"/>
        </Method>
        '''
        vcellOptParamList = vcellOptProblem.optimizationMethod.optimizationParameterList
        assert(isinstance(vcellOptParamList, list))
        for vcellOptParam in vcellOptParamList:
            assert(isinstance(vcellOptParam,VCELLOPT.CopasiOptimizationParameter))
            print methodParamDict[vcellOptParam.paramType]
            fitMethod.removeParameter(methodParamDict[vcellOptParam.paramType])
            if (vcellOptParam.dataType == VCELLOPT.OptimizationParameterDataType.INT):
                fitMethod.addParameter(methodParamDict[vcellOptParam.paramType], COPASI.CCopasiParameter.INT)
                fitParameter = fitMethod.getParameter(methodParamDict[vcellOptParam.paramType])
                assert (isinstance(fitParameter, COPASI.CCopasiParameter))
                fitParameter.setIntValue(int(vcellOptParam.value))
            else:
                fitMethod.addParameter(methodParamDict[vcellOptParam.paramType], COPASI.CCopasiParameter.DOUBLE)
                fitParameter = fitMethod.getParameter(methodParamDict[vcellOptParam.paramType])
                assert (isinstance(fitParameter, COPASI.CCopasiParameter))
                fitParameter.setDblValue(vcellOptParam.value)

        #
        # get FitProblem
        #
        fitProblem = fitTask.getProblem()
        assert(isinstance(fitProblem, COPASI.COptProblem))  # works for all COPASI builds >= 140
        #assert(isinstance(fitProblem, COPASI.CFitProblem)) # not CFitProblem in COPASI build 140
        #fitProblem.setRandomizeStartValues(True)

        experimentSet = fitProblem.getParameter("Experiment Set")
        assert(isinstance(experimentSet,COPASI.CExperimentSet))
        assert(experimentSet.getExperimentCount() == 0)

        # first experiment
        experiment = COPASI.CExperiment(dataModel)
        assert(isinstance(experiment,COPASI.CExperiment))
        experiment.setIsRowOriented(True)

        # Use the TemporaryFile context manager for easy clean-up
        tmpExportCSVFile = tempfile.NamedTemporaryFile(delete=False)
        varNameList = list(v.varName for v in vcellOptProblem.referenceVariableList)
        csvString = ", ".join(map(str, varNameList)) + "\n"
        for row in vcellOptProblem.experimentalDataSet.rows:
            csvString += ", ".join(map(str, row.data)) + "\n"
        num_lines = len(vcellOptProblem.experimentalDataSet.rows) + 1
        tmpExportCSVFile.write(csvString)
        tmpExportCSVFile.close()

        experiment.setFileName(str(tmpExportCSVFile.name))
        experiment.setFirstRow(1)
        experiment.setKeyValue("Experiment_1")
        experiment.setLastRow(num_lines)
        experiment.setHeaderRow(1)
        experiment.setSeparator(",")
        experiment.setExperimentType(COPASI.CTaskEnum.timeCourse)
        experiment.setNormalizeWeightsPerExperiment(True)
        vcellReferenceVariableList = vcellOptProblem.referenceVariableList
        assert(isinstance(vcellReferenceVariableList, list))
        num_ref_variables = len(vcellReferenceVariableList)
        experiment.setNumColumns(num_ref_variables) # one independent (time), all the other dependent

        # experiment object map
        objectMap = experiment.getObjectMap()
        assert (isinstance(objectMap, COPASI.CExperimentObjectMap))
        result = objectMap.setNumCols(num_ref_variables)
        assert result == True

        # map time column to model time
        '''
        <variable type="independent" name="t"/>
        '''
        result = objectMap.setRole(0, COPASI.CExperiment.time)
        assert (result==True)
        assert objectMap.getRole(0) == COPASI.CExperiment.time
        timeReference = model.getValueReference()
        #timeReference = model.getObject(COPASI.CCopasiObjectName("Reference=Time"))
        assert(timeReference!=None)
        assert(isinstance(timeReference,COPASI.CCopasiObject))
        objectMap.setObjectCN(0,timeReference.getCN().getString())
        # getObjectCN returns a string whereas getCN returns a CCopasiObjectName
        assert(objectMap.getObjectCN(0)==timeReference.getCN().getString())

        # map rest of data columns as dependent variables
        '''
        <variable type="dependent" name="C_cyt"/>
        <variable type="dependent" name="RanC_cyt"/>
        '''
        for refIndex in range(1,num_ref_variables):  # skip first columnn (time)
            refVar = vcellReferenceVariableList[refIndex]
            assert(isinstance(refVar,VCELLOPT.ReferenceVariable))
            modelValue = getModelValue(model,str(refVar.varName))
            assert(isinstance(modelValue,COPASI.CModelValue))
            objectMap.setRole(refIndex, COPASI.CExperiment.dependent)
            modelValueReference = modelValue.getObject(COPASI.CCopasiObjectName("Reference=Value"))
            assert(isinstance(modelValueReference,COPASI.CCopasiObject))
            print "modelValue CN is "+str(modelValue.getCN())+", modelValueReference CN is "+str(modelValueReference.getCN())
            objectMap.setObjectCN(refIndex, modelValueReference.getCN().getString())

        experimentSet.addExperiment(experiment)         # addExperiment makes a copy
        assert experimentSet.getExperimentCount() == 1
        experiment = experimentSet.getExperiment(0)     # need to get the correct instance
        assert(isinstance(experiment,COPASI.CExperiment))

        #---------------------------------------------------------------------------------------
        # define CFitItems
        #---------------------------------------------------------------------------------------
        '''
        <parameterDescription>
            <parameter name="Kf" low="0.1" high="10.0" init="5.0" scale="5.0"/>
            <parameter name="Kr" low="100.0" high="10000.0" init="500.0" scale="500.0"/>
        </parameterDescription>
        '''
        assert(fitProblem.getOptItemSize()==0)
        vcellParameterDescriptionList = vcellOptProblem.parameterDescriptionList
        for vcellParam in vcellParameterDescriptionList:
            assert(isinstance(vcellParam,VCELLOPT.ParameterDescription))
            paramModelValue = getModelValue(model,str(vcellParam.name))
            assert(isinstance(paramModelValue,COPASI.CModelValue))
            paramModelValueRef = paramModelValue.getInitialValueReference()
            assert(isinstance(paramModelValueRef,COPASI.CCopasiObject))
            fitItem = COPASI.CFitItem(dataModel)
            assert(isinstance(fitItem,COPASI.CFitItem))
            fitItem.setObjectCN(paramModelValueRef.getCN())
            fitItem.setStartValue(vcellParam.initialValue)
            fitItem.setLowerBound(COPASI.CCopasiObjectName(str(vcellParam.minValue)))
            fitItem.setUpperBound(COPASI.CCopasiObjectName(str(vcellParam.maxValue)))
            # todo: what about scale?
            # add the fit item to the correct parameter group
            optimizationItemGroup = fitProblem.getParameter("OptimizationItemList")
            assert(isinstance(optimizationItemGroup,COPASI.CCopasiParameterGroup))
            optimizationItemGroup.addParameter(fitItem)
            # addParameter makes a copy of the fit item, so we have to get it back
            #fitItem = optimizationItemGroup.getParameter(0)
            #assert(isinstance(fitItem,COPASI.CFitItem))
        model.compileIfNecessary()
        #print optimizationItemGroup.printToString()  # debug anyway, not present in COPASI build 140

        # --------------------------------------------------------------------------------------
        #  Run the optimization (N times)
        # --------------------------------------------------------------------------------------
        leastError = 1e8
        paramNames = []
        paramValues = []
        numObjFuncEvals = 0
        numParamsToFit = fitProblem.getOptItemSize()

        # Create intermediate results file
        dir_path = os.path.dirname(os.path.realpath(resultFile))
        interresults=os.path.join(dir_path,"interresults.txt")
        with open(interresults, 'a') as foutput:
            foutput.write(str(vcellOptProblem.numberOfOptimizationRuns)+"\n")
            foutput.close()
            
        for i in range(0, vcellOptProblem.numberOfOptimizationRuns):
            result = True
            try:
                print ("This can take some time...")
                initialize = (i==0)
                result = fitTask.processWithOutputFlags(initialize, COPASI.CCopasiTask.NO_OUTPUT)  # NO_OUTPUT
            except:
                print "Unexpected error:", sys.exc_info()[0]
                return 1

            if result == False:
                sys.stderr.write("An error occured while running the Parameter estimation.\n")
                # dataModel.saveModel('test_failed.cps', True)
                sys.stderr.write("fitTask warning: '" + str(fitTask.getProcessWarning()) + "'")
                sys.stderr.write("fitTask error: '" + str(fitTask.getProcessError()) + "'")
                # check if there are additional error messages
                if COPASI.CCopasiMessage.size() > 0:
                    # print the messages in chronological order
                    sys.stderr.write(COPASI.CCopasiMessage.getAllMessageText(True))
                return 1

            currentFuncValue = fitProblem.getSolutionValue()
            print "currFuncValue = " + str(currentFuncValue)
            if (currentFuncValue < leastError) or (i == 0):         # current run has the smallest error so far
                bestObjectiveFunction = currentFuncValue
                numObjFuncEvals = fitProblem.getFunctionEvaluations()

                paramNames = []
                paramValues = []
                for j in range(0, numParamsToFit):
                    optItem = fitProblem.getOptItemList()[j]
                    paramName = optItem.getObject().getCN().getRemainder().getRemainder().getElementName(0)
                    paramNames.append(paramName)
                    paramValue = fitProblem.getSolutionVariables().get(j)
                    paramValues.append(paramValue)
                    print "param " + paramName + " --> " + str(paramValue)
                assert isinstance(currentFuncValue, float)
                leastError = currentFuncValue

            with open(interresults, 'a') as foutput:
                foutput.write(str(i)+" "+str(leastError)+" "+str(numObjFuncEvals)+"\n")
                foutput.close()

        #result = dataModel.saveModel('test_succeeded.cps', True)
        #assert(result==True)

        optRun = VCELLOPT.OptRun()
        optRun.optProblem = vcellOptProblem
        optRun.statusMessage = "complete"
        optRun.status = VCELLOPT.OptRunStatus.Complete
        optResultSet = VCELLOPT.OptResultSet()
        optResultSet.numFunctionEvaluations = numObjFuncEvals
        optResultSet.objectiveFunction = leastError
        optResultSet.optParameterValues = []
        paramValueDict = dict(zip(paramNames,paramValues))
        for paramName in paramNames:
            optParameterValue = VCELLOPT.OptParameterValue(paramName,paramValueDict[paramName])
            optResultSet.optParameterValues.append(optParameterValue)
        optRun.optResultSet = optResultSet

        protocol_factory = TBinaryProtocol.TBinaryProtocolFactory
        optRunBlob = serialize(vcellOptProblem, protocol_factory = protocol_factory())
        transportOut = TTransport.TMemoryBuffer()
        protocolOut = TBinaryProtocol.TBinaryProtocol(transportOut)
        optRun.write(protocolOut)
        with open(resultFile, 'wb') as foutput:
            foutput.write(transportOut.getvalue())
            foutput.close()

        # writeOptSolverResultSet(resultFile, leastError, numObjFuncEvals, paramNames, paramValues)

    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0],e_info[1],e_info[2],file=sys.stdout)
        sys.stderr.write("exception: "+str(e_info[0])+": "+str(e_info[1])+"\n")
        sys.stderr.flush()
        return -1
    else:
        return 0
    finally:
        os.unlink(tmpExportCSVFile.name)


def writeOptSolverResultSet(resultFile, leastError, numObjFuncEvals, paramNames, paramValues):
    import xml.etree.cElementTree as ET

    numParamsToFit = len(paramNames)
    root = ET.Element("optSolverResultSet")
    for paramName in paramNames:
        ET.SubElement(root, "parameter", name=paramName)

    doc = ET.SubElement(root, "bestOptRunResultSet", bestObjectiveFunction=str(leastError),
                        numObjectiveFunctionEvaluations=str(numObjFuncEvals), status="unknown")

    for j in range(0, numParamsToFit):
        paramName = paramNames[j]
        paramValue = paramValues[j]
        text = "parameter name=\"" + paramName + "\" bestValue=\"" + str(paramValue) + "\""
        ET.SubElement(doc, text)

    tree = ET.ElementTree(root)
    tree.write(resultFile)
    

def getModelValue(model, objectName):
    modelValues = model.getModelValues()
    assert (isinstance(modelValues, COPASI.ModelValueVectorN))
    return modelValues.getByName(objectName)


def printModel(model):
    #
    # print all of the model metabolites
    #
    metabs = model.getMetabolites()
    assert (isinstance(metabs, COPASI.MetabVector))
    print "Metabolites size = " + str(metabs.size())
    for index in range(0, metabs.size()):
        metab = metabs.get(index)
        assert (isinstance(metab, COPASI.CMetab))
        print "metabolite [{0}]: objectName = {1}, commonName = {2}, displayName = {3}".format(
            str(index), str(metab.getObjectName()), str(metab.getCN()), str(metab.getObjectDisplayName()))

    #
    # print all of the model values
    #
    modelValues = model.getModelValues()
    assert (isinstance(modelValues, COPASI.ModelValueVectorN))
    print "ModelValues size = " + str(modelValues.size())
    for index in range(0, modelValues.size()):
        modelValue = modelValues.get(index)
        assert (isinstance(modelValue, COPASI.CModelValue))
        print "modelValue [{0}]: objectName = {1}, commonName = {2}, displayName = {3}".format(
            str(index), str(modelValue.getObjectName()), str(modelValue.getCN()),
            str(modelValue.getObjectDisplayName()))

    #
    # print all of the model compartments
    #
    compartments = model.getCompartments()
    assert (isinstance(compartments, COPASI.CompartmentVectorNS))
    print "Compartments size = " + str(compartments.size())
    for index in range(0, compartments.size()):
        compartment = compartments.get(index)
        assert (isinstance(compartment, COPASI.CCompartment))
        print "compartment [{0}]: objectName = {1}, commonName = {2}, displayName = {3}".format(
            str(index), str(compartment.getObjectName()), str(compartment.getCN()),
            str(compartment.getObjectDisplayName()))


if __name__ == '__main__':
    main()
