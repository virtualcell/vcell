import argparse
import copy
import os
import subprocess
import COPASI

import sys
import traceback
from random import random

from __builtin__ import isinstance

from vcellopt.ttypes import CopasiOptimizationMethod
from vcellopt.ttypes import CopasiOptimizationParameter
from vcellopt.ttypes import OptimizationParameterType
from vcellopt.ttypes import OptProblem
from vcellopt.ttypes import ParameterDescription
from vcellopt.ttypes import ReferenceVariable
from vcellopt.ttypes import ReferenceVariableType
from vcellopt.ttypes import TApplicationException
from vcellopt.ttypes import OptimizationMethodType
from vcellopt.ttypes import CopasiOptimizationParameter

from thrift.TSerialization import TBinaryProtocol
from thrift.TSerialization import deserialize
from thrift.TSerialization import serialize


#import sys

def main():
    #sys.setrecursionlimit(100000)
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument("optfile",help="filename of input optimization file")
        parser.add_argument("resultfile",help="filename of optimization results")
        args = parser.parse_args()

        f_optfile = open(args.optfile, "rb")
        blob_opt = f_optfile.read()
        print("read "+str(len(blob_opt))+" bytes from "+args.optfile)
        f_optfile.close()

        optProblem = OptProblem()
        protocol_factory = TBinaryProtocol.TBinaryProtocolFactory
        deserialize(optProblem, blob_opt, protocol_factory = protocol_factory())
        print("done with deserialization")
        #print(str(args.resultfile))
        #print(str(optProblem))
        COPASI.CCopasiRootContainer.init()
        dataModel = COPASI.CCopasiRootContainer.addDatamodel()
        sbmlFile = optProblem.mathModelSbmlFile
        dataModel.importSBML(sbmlFile)
        #sbmlFile = "C:\\COPASI-4.19.140-Source\\copasi\\bindings\\python\\examples\\exampleDeni.xml"
        #dataModel.importSBML(sbmlFile)
        print("data model loaded")

        dictionary = dict()
        dictionary[OptimizationMethodType.EvolutionaryProgram] = COPASI.CTaskEnum.EvolutionaryProgram
        dictionary[OptimizationMethodType.SRES] = COPASI.CTaskEnum.SRES
        dictionary[OptimizationMethodType.GeneticAlgorithm] = COPASI.CTaskEnum.GeneticAlgorithm
        dictionary[OptimizationMethodType.GeneticAlgorithmSR] = COPASI.CTaskEnum.GeneticAlgorithmSR
        dictionary[OptimizationMethodType.HookeJeeves] = COPASI.CTaskEnum.HookeJeeves
        dictionary[OptimizationMethodType.LevenbergMarquardt] = COPASI.CTaskEnum.LevenbergMarquardt
        dictionary[OptimizationMethodType.NelderMead] = COPASI.CTaskEnum.NelderMead
        dictionary[OptimizationMethodType.ParticleSwarm] = COPASI.CTaskEnum.ParticleSwarm
        dictionary[OptimizationMethodType.RandomSearch] = COPASI.CTaskEnum.RandomSearch
        dictionary[OptimizationMethodType.SimulatedAnnealing] = COPASI.CTaskEnum.SimulatedAnnealing
        dictionary[OptimizationMethodType.SteepestDescent] = COPASI.CTaskEnum.SteepestDescent
        dictionary[OptimizationMethodType.Praxis] = COPASI.CTaskEnum.Praxis
        dictionary[OptimizationMethodType.TruncatedNewton] = COPASI.CTaskEnum.TruncatedNewton


        methodParamDict = dict()
        methodParamDict[OptimizationParameterType.Number_of_Generations] = "Number of Generations"
        methodParamDict[OptimizationParameterType.Number_of_Iterations] = "Number of Iterations"
        methodParamDict[OptimizationParameterType.Population_Size] = "Population Size"
        methodParamDict[OptimizationParameterType.Random_Number_Generator] = "Random Number Generator"
        methodParamDict[OptimizationParameterType.Seed] = "Seed"
        methodParamDict[OptimizationParameterType.IterationLimit] = "Iteration Limit"
        methodParamDict[OptimizationParameterType.Tolerance] = "Tolerance"
        methodParamDict[OptimizationParameterType.Rho] = "Rho"
        methodParamDict[OptimizationParameterType.Scale] = "Scale"
        methodParamDict[OptimizationParameterType.Swarm_Size] = "Swarm Size"
        methodParamDict[OptimizationParameterType.Std_Deviation] = "Std Deviation"
        methodParamDict[OptimizationParameterType.Start_Temperature] = "Start Temperature"
        methodParamDict[OptimizationParameterType.Cooling_Factor] = "Cooling Factor"
        methodParamDict[OptimizationParameterType.Pf] = "Pf"

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
struct CopasiOptimizationParameter {
	1: required OptimizationParameterType dataType;
	2: required double value;
}
typedef list<CopasiOptimizationParameter> CopasiOptimizationParameterList
struct CopasiOptimizationMethod {
	1: required OptimizationMethodType optimizationMethodType;
	2: required CopasiOptimizationParameterList optimizationParameterList;
}
struct OptProblem {
	1: required FilePath mathModelSbmlFile;
	2: required int numberOfOptimizationRuns;
	3: required ParameterDescriptionList parameterDescriptionList;
	4: required ReferenceVariableList referenceVariableList;
	5: required FilePath experimentalDataFile;
	6: required CopasiOptimizationMethod optimizationMethod;
}
        '''
        fitTask = dataModel.addTask(COPASI.CTaskEnum.parameterFitting)
        assert(isinstance(fitTask,COPASI.CFitTask))
        assert (fitTask != None)
        assert (fitTask.__class__ == COPASI.CFitTask)
        fitProblem = fitTask.getProblem()
        assert(isinstance(fitProblem,COPASI.COptProblem))

        # fitMethodA = fitTask.getMethod()
        # for thing in fitTask.getValidMethods():
        #     print thing
        copasiFitMethod = dictionary[optProblem.optimizationMethod.optimizationMethodType]
        result = fitTask.setMethodType(copasiFitMethod)
        assert result == True
        num_lines = sum(1 for line in open(optProblem.experimentalDataFile))
        #with open(optProblem.experimentalDataFile, 'r') as f:
        #    lines = f.read().splitlines()
        #    lastLine = lines[-1]
        #num_lines = len(lines)
        #print(lastLine)
        #y = lastLine.strip().split(",")
        #endTime = float(y[0])
        # ------------------------------------------------------------------------------
        '''
        <variable type="independent" name="t"/>
        <variable type="dependent" name="C_cyt"/>
        <variable type="dependent" name="RanC_cyt"/>
        '''
        referenceVariableList = optProblem.referenceVariableList
        assert(isinstance(referenceVariableList, list))
        size = len(referenceVariableList)   # one independent (time), all the other dependent

        experiment = COPASI.CExperiment(dataModel)
        assert experiment != None
        # tell COPASI where to find the data
        experiment.setFileName(optProblem.experimentalDataFile)     # ex: C:\\TEMP\\ggg\\testing_61.csv

        # we have to tell COPASI that the data for the experiment is a komma separated list
        experiment.setSeparator(",")
        # the data start in row 1 and goes to row 'num_lines'
        experiment.setFirstRow(1)
        experiment.setLastRow(num_lines)
        experiment.setHeaderRow(1)
        experiment.setExperimentType(COPASI.CTaskEnum.timeCourse)
        assert experiment.getExperimentType() == COPASI.CTaskEnum.timeCourse
        experiment.setNumColumns(size)

        objectMap = experiment.getObjectMap()
        assert (isinstance(objectMap, COPASI.CExperimentObjectMap))
        assert objectMap != None
        result = objectMap.setNumCols(size)
        assert result == True
        objectMap.setRole(0, COPASI.CExperiment.time)
        assert objectMap.getRole(0) == COPASI.CExperiment.time

        model = dataModel.getModel()
        assert model != None
        assert (isinstance(model, COPASI.CModel))
        timeReference = model.getValueReference()
        assert timeReference != None
        daString = timeReference.getCN().getString()
        objectMap.setObjectCN(0, daString)

        keyFactory = COPASI.CCopasiRootContainer.getKeyFactory()
        assert keyFactory != None
        for i in range(1, size):        # start at 1, skip time which is already done
            objectMap.setRole(i, COPASI.CExperiment.dependent)
            referenceVariable = referenceVariableList[i]
            assert referenceVariable != None
            assert (isinstance(referenceVariable, ReferenceVariable))
            modelValues = model.getModelValues()
            assert(isinstance(modelValues,COPASI.ModelValueVectorN))
            refVarName = referenceVariable.varName
            metab = modelValues.getByName(refVarName)
            particleReference = metab.getValueReference()
            print("copasi var name is " + particleReference.getObjectName())
            daString = particleReference.getCN().getString()
            objectMap.setObjectCN(i, daString)

        print("last not ignored column " + str(objectMap.getLastNotIgnoredColumn()))
        experimentSet = fitProblem.getParameter("Experiment Set")
        assert experimentSet != None
        experimentSet.addExperiment(experiment)         # addExperiment makes a copy
        assert experimentSet.getExperimentCount() == 1
        experiment = experimentSet.getExperiment(0)     # need to get the correct instance
        assert experiment != None
        # -------------------------------------------------------------------------------------
        '''
        <parameterDescription>
            <parameter name="Kf" low="0.1" high="10.0" init="5.0" scale="5.0"/>
            <parameter name="Kr" low="100.0" high="10000.0" init="500.0" scale="500.0"/>
        </parameterDescription>
        '''
        optimizationItemGroup = fitProblem.getParameter("OptimizationItemList")
        parameterDescriptionList = optProblem.parameterDescriptionList
        assert (isinstance(parameterDescriptionList, list))
        size = len(parameterDescriptionList)
        for parameterDescription in parameterDescriptionList:
            assert(isinstance(parameterDescription,ParameterDescription))
            print(parameterDescription)
            #parameterReference = copasiVar->getObject(cObjName);
            copasiParameterObjectName = COPASI.CCopasiObjectName(parameterDescription.name)
            assert(isinstance(copasiParameterObjectName,COPASI.CCopasiObjectName))
            print(str(type(copasiParameterObjectName)) + " :::: " + str(copasiParameterObjectName))
            parameterReference = model.getModelValue(copasiParameterObjectName)
            print(type(parameterReference))
            assert(isinstance(parameterReference,COPASI.CModelValue))
            daThing = parameterReference.getCN()
            fitItem = COPASI.CFitItem(dataModel)
            assert fitItem != None
            assert (isinstance(fitItem, COPASI.CFitItem))
            fitItem.setObjectCN(daThing)
            fitItem.setStartValue(parameterDescription.initialValue)
            fitItem.setLowerBound(COPASI.CCopasiObjectName(str(parameterDescription.minValue)))
            fitItem.setUpperBound(COPASI.CCopasiObjectName(str(parameterDescription.maxValue)))
            # todo: what about scale?
            # add the fit item to the correct parameter group
            optimizationItemGroup.addParameter(fitItem)
        # -------------------------------------------------------------------------------------
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
        fitMethod = fitTask.getMethod()
        assert (isinstance(fitMethod, COPASI.COptMethod))
        copasiOptimizationParameterList = optProblem.optimizationMethod.optimizationParameterList
        assert(isinstance(copasiOptimizationParameterList, list))
        size = len(copasiOptimizationParameterList)
        for optParameter in copasiOptimizationParameterList:
            assert(isinstance(optParameter,CopasiOptimizationParameter))
            print methodParamDict[optParameter.dataType]
            fitParameter = fitMethod.getParameter(methodParamDict[optParameter.dataType])
            assert (isinstance(fitParameter, COPASI.CCopasiParameter))
            fitParameter.setDblValue(optParameter.value)
        # --------------------------------------------------------------------------------------
        for i in range(0, optProblem.numberOfOptimizationRuns):
            result = True
            print ("This can take some time...")
            result = fitTask.processWithOutputFlags(True, COPASI.CCopasiTask.NO_OUTPUT)
            if result == False:
                sys.stderr.write("An error occured while running the Parameter estimation.\n")
                dataModel.saveModel('c:\\temp\\ggg\\test.cps', True)
                sys.stderr.write(fitTask.getProcessWarning())
                sys.stderr.write(fitTask.getProcessError())
                # check if there are additional error messages
                if COPASI.CCopasiMessage.size() > 0:
                    # print the messages in chronological order
                    sys.stderr.write(COPASI.CCopasiMessage.getAllMessageText(True))
                return 1
            assert result == True
            '''
            print("solution size " + str(fitProblem.getSolutionVariables().size()))
            optItem1 = fitProblem.getOptItemList()[0]
            optItem2 = fitProblem.getOptItemList()[1]
            print ("value for ", optItem1.getObject().getCN().getString(), ": ", fitProblem.getSolutionVariables().get(0))
            print ("value for ", optItem2.getObject().getCN().getString(), ": ", fitProblem.getSolutionVariables().get(1))
            '''
            leastError = 1e8
            currentFuncValue = fitProblem.getSolutionValue()
            if (currentFuncValue < leastError) or (i == 0):         # current run has the smallest error so far
                bestObjectiveFunction = currentFuncValue
                numObjFuncEvals = fitProblem.getFunctionEvaluations()
                optItemSize = fitProblem.getOptItemSize()

                paramNames = []
                paramValues = []
                for j in range(0, optItemSize):
                    optItem = fitProblem.getOptItemList()[j]
                    paramName = optItem.getObject().getCN().getRemainder().getRemainder().getElementName(0)
                    paramNames.append(paramName)
                    paramValue = fitProblem.getSolutionVariables().get(j)
                    paramValues.append(paramValue)
                leastError = currentFuncValue
        # ---------------------------------------------------------------------
        import xml.etree.cElementTree as ET
        root = ET.Element("optSolverResultSet")
        for paramName in paramNames:
            ET.SubElement(root, "parameter", name=paramName)
        doc = ET.SubElement(root, "bestOptRunResultSet", bestObjectiveFunction=str(bestObjectiveFunction),
                            numObjectiveFunctionEvaluations=str(numObjFuncEvals), status="unknown")
        for j in range(0, optItemSize):
            paramName = paramNames[j]
            paramValue = paramValues[j]
            text = "parameter name=\"" + paramName + "\" bestValue=\"" + str(paramValue) + "\""
            ET.SubElement(doc, text)
        tree = ET.ElementTree(root)
        tree.write("c:\\temp\\ggg\\myresults.xml")

    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0],e_info[1],e_info[2],file=sys.stdout)
        sys.stderr.write("exception: "+str(e_info[0])+": "+str(e_info[1])+"\n")
        sys.stderr.flush()
        sys.exit(-1)
    else:
        sys.exit(0)

if __name__ == '__main__':
    main()
