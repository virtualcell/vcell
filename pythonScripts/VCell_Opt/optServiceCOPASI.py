import sys
import traceback
from __builtin__ import isinstance

import COPASI
import vcellopt.ttypes as VCELLOPT
import tempfile
import os


def process(vcell_opt_problem):
    assert(isinstance(vcell_opt_problem, VCELLOPT.OptProblem))

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
    try:

        #
        # add CDataModel
        #_
        assert(COPASI.CCopasiRootContainer.getRoot() is not None)
        # create a datamodel
        data_model = COPASI.CCopasiRootContainer.addDatamodel()
        assert(isinstance(data_model, COPASI.CCopasiDataModel))
        assert(COPASI.CCopasiRootContainer.getDatamodelList().size() == 1)

        try:
            sbml_string = vcell_opt_problem.mathModelSbmlContents
            data_model.importSBMLFromString(str(sbml_string))
            print("data model loaded")
        except:
            e_info = sys.exc_info()
            traceback.print_exception(e_info[0], e_info[1], e_info[2], file=sys.stdout)
            sys.stderr.write("exception: error importing sbml file: "+str(e_info[0])+": "+str(e_info[1])+"\n")
            sys.stderr.flush()
            return -1

        model = data_model.getModel()
        assert(isinstance(model, COPASI.CModel))
        model.compileIfNecessary()

        print_model(model)

        math_container = model.getMathContainer()
        assert(isinstance(math_container, COPASI.CMathContainer))
        # data_model.saveModel("optModel.cps", True)

        #
        # add CFitTask
        #
        fit_task = data_model.addTask(COPASI.CTaskEnum.parameterFitting)
        assert(isinstance(fit_task, COPASI.CFitTask))

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
        method_type_dict = dict()
        method_type_dict[VCELLOPT.OptimizationMethodType.EvolutionaryProgram] = COPASI.CTaskEnum.EvolutionaryProgram
        method_type_dict[VCELLOPT.OptimizationMethodType.SRES] = COPASI.CTaskEnum.SRES
        method_type_dict[VCELLOPT.OptimizationMethodType.GeneticAlgorithm] = COPASI.CTaskEnum.GeneticAlgorithm
        method_type_dict[VCELLOPT.OptimizationMethodType.GeneticAlgorithmSR] = COPASI.CTaskEnum.GeneticAlgorithmSR
        method_type_dict[VCELLOPT.OptimizationMethodType.HookeJeeves] = COPASI.CTaskEnum.HookeJeeves
        method_type_dict[VCELLOPT.OptimizationMethodType.LevenbergMarquardt] = COPASI.CTaskEnum.LevenbergMarquardt
        method_type_dict[VCELLOPT.OptimizationMethodType.NelderMead] = COPASI.CTaskEnum.NelderMead
        method_type_dict[VCELLOPT.OptimizationMethodType.ParticleSwarm] = COPASI.CTaskEnum.ParticleSwarm
        method_type_dict[VCELLOPT.OptimizationMethodType.RandomSearch] = COPASI.CTaskEnum.RandomSearch
        method_type_dict[VCELLOPT.OptimizationMethodType.SimulatedAnnealing] = COPASI.CTaskEnum.SimulatedAnnealing
        method_type_dict[VCELLOPT.OptimizationMethodType.SteepestDescent] = COPASI.CTaskEnum.SteepestDescent
        method_type_dict[VCELLOPT.OptimizationMethodType.Praxis] = COPASI.CTaskEnum.Praxis
        method_type_dict[VCELLOPT.OptimizationMethodType.TruncatedNewton] = COPASI.CTaskEnum.TruncatedNewton

        #
        # set CFitMethod
        #
        copasi_fit_method_type = method_type_dict[vcell_opt_problem.optimizationMethod.optimizationMethodType]
        if copasi_fit_method_type not in fit_task.getValidMethods():
            print "fit method not allowed"
            return 1
        fit_task.setMethodType(copasi_fit_method_type)
        fit_method = fit_task.getMethod()
        assert(isinstance(fit_method, COPASI.COptMethod))

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
        method_param_dict = dict()
        method_param_dict[VCELLOPT.OptimizationParameterType.Number_of_Generations] = "Number of Generations"
        method_param_dict[VCELLOPT.OptimizationParameterType.Number_of_Iterations] = "Number of Iterations"
        method_param_dict[VCELLOPT.OptimizationParameterType.Population_Size] = "Population Size"
        method_param_dict[VCELLOPT.OptimizationParameterType.Random_Number_Generator] = "Random Number Generator"
        method_param_dict[VCELLOPT.OptimizationParameterType.Seed] = "Seed"
        method_param_dict[VCELLOPT.OptimizationParameterType.IterationLimit] = "Iteration Limit"
        method_param_dict[VCELLOPT.OptimizationParameterType.Tolerance] = "Tolerance"
        method_param_dict[VCELLOPT.OptimizationParameterType.Rho] = "Rho"
        method_param_dict[VCELLOPT.OptimizationParameterType.Scale] = "Scale"
        method_param_dict[VCELLOPT.OptimizationParameterType.Swarm_Size] = "Swarm Size"
        method_param_dict[VCELLOPT.OptimizationParameterType.Std_Deviation] = "Std Deviation"
        method_param_dict[VCELLOPT.OptimizationParameterType.Start_Temperature] = "Start Temperature"
        method_param_dict[VCELLOPT.OptimizationParameterType.Cooling_Factor] = "Cooling Factor"
        method_param_dict[VCELLOPT.OptimizationParameterType.Pf] = "Pf"

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
        vcell_opt_param_list = vcell_opt_problem.optimizationMethod.optimizationParameterList
        assert(isinstance(vcell_opt_param_list, list))
        for vcellOptParam in vcell_opt_param_list:
            assert(isinstance(vcellOptParam, VCELLOPT.CopasiOptimizationParameter))
            print method_param_dict[vcellOptParam.paramType]
            fit_method.removeParameter(method_param_dict[vcellOptParam.paramType])
            if vcellOptParam.dataType == VCELLOPT.OptimizationParameterDataType.INT:
                fit_method.addParameter(method_param_dict[vcellOptParam.paramType], COPASI.CCopasiParameter.INT)
                fit_parameter = fit_method.getParameter(method_param_dict[vcellOptParam.paramType])
                assert (isinstance(fit_parameter, COPASI.CCopasiParameter))
                fit_parameter.setIntValue(int(vcellOptParam.value))
            else:
                fit_method.addParameter(method_param_dict[vcellOptParam.paramType], COPASI.CCopasiParameter.DOUBLE)
                fit_parameter = fit_method.getParameter(method_param_dict[vcellOptParam.paramType])
                assert (isinstance(fit_parameter, COPASI.CCopasiParameter))
                fit_parameter.setDblValue(vcellOptParam.value)

        #
        # get FitProblem
        #
        fit_problem = fit_task.getProblem()
        assert(isinstance(fit_problem, COPASI.COptProblem))  # works for all COPASI builds >= 140
        # assert(isinstance(fit_problem, COPASI.CFitProblem)) # not CFitProblem in COPASI build 140
        # fit_problem.setRandomizeStartValues(True)

        experiment_set = fit_problem.getParameter("Experiment Set")
        assert(isinstance(experiment_set, COPASI.CExperimentSet))
        assert(experiment_set.getExperimentCount() == 0)

        # first experiment
        experiment = COPASI.CExperiment(data_model)
        assert(isinstance(experiment, COPASI.CExperiment))
        experiment.setIsRowOriented(True)

        # Use the TemporaryFile context manager for easy clean-up
        tmp_export_csv_file = tempfile.NamedTemporaryFile(delete=False)
        var_name_list = list(v.varName for v in vcell_opt_problem.referenceVariableList)
        csv_string = ", ".join(map(str, var_name_list)) + "\n"
        for row in vcell_opt_problem.experimentalDataSet.rows:
            csv_string += ", ".join(map(str, row.data)) + "\n"
        num_lines = len(vcell_opt_problem.experimentalDataSet.rows) + 1
        tmp_export_csv_file.write(csv_string)
        tmp_export_csv_file.close()

        experiment.setFileName(str(tmp_export_csv_file.name))
        experiment.setFirstRow(1)
        experiment.setKeyValue("Experiment_1")
        experiment.setLastRow(num_lines)
        experiment.setHeaderRow(1)
        experiment.setSeparator(",")
        experiment.setExperimentType(COPASI.CTaskEnum.timeCourse)
        experiment.setNormalizeWeightsPerExperiment(True)
        vcell_reference_variable_list = vcell_opt_problem.referenceVariableList
        assert(isinstance(vcell_reference_variable_list, list))
        num_ref_variables = len(vcell_reference_variable_list)
        result = experiment.setNumColumns(num_ref_variables)   # one independent (time), all the other dependent
        assert result is True

        # experiment object map
        object_map = experiment.getObjectMap()
        assert isinstance(object_map, COPASI.CExperimentObjectMap)
        result = object_map.setNumCols(num_ref_variables)
        assert result is True

        # map time column to model time
        '''
        <variable type="independent" name="t"/>
        '''
        result = object_map.setRole(0, COPASI.CExperiment.time)
        assert (result is True)
        assert object_map.getRole(0) == COPASI.CExperiment.time
        time_reference = model.getValueReference()
        # time_reference = model.getObject(COPASI.CCopasiObjectName("Reference=Time"))
        assert(time_reference is not None)
        assert(isinstance(time_reference, COPASI.CCopasiObject))
        object_map.setObjectCN(0, time_reference.getCN().getString())
        # getObjectCN returns a string whereas getCN returns a CCopasiObjectName
        assert(object_map.getObjectCN(0) == time_reference.getCN().getString())

        # map rest of data columns as dependent variables
        '''
        <variable type="dependent" name="C_cyt"/>
        <variable type="dependent" name="RanC_cyt"/>
        '''
        for refIndex in range(1, num_ref_variables):  # skip first columnn (time)
            ref_var = vcell_reference_variable_list[refIndex]
            assert(isinstance(ref_var, VCELLOPT.ReferenceVariable))
            model_value = getmodelvalue(model, str(ref_var.varName))
            assert(isinstance(model_value, COPASI.CModelValue))
            object_map.setRole(refIndex, COPASI.CExperiment.dependent)
            model_value_reference = model_value.getObject(COPASI.CCopasiObjectName("Reference=Value"))
            assert(isinstance(model_value_reference, COPASI.CCopasiObject))
            print "model_value CN is "+str(model_value.getCN()) + \
                  ", model_value_reference CN is "+str(model_value_reference.getCN())
            object_map.setObjectCN(refIndex, model_value_reference.getCN().getString())

        experiment_set.addExperiment(experiment)         # addExperiment makes a copy
        assert experiment_set.getExperimentCount() == 1
        experiment = experiment_set.getExperiment(0)     # need to get the correct instance
        assert(isinstance(experiment, COPASI.CExperiment))

        #
        # define CFitItems
        #
        '''
        <parameterDescription>
            <parameter name="Kf" low="0.1" high="10.0" init="5.0" scale="5.0"/>
            <parameter name="Kr" low="100.0" high="10000.0" init="500.0" scale="500.0"/>
        </parameterDescription>
        '''
        assert(fit_problem.getOptItemSize() == 0)
        vcell_parameter_description_list = vcell_opt_problem.parameterDescriptionList
        for vcellParam in vcell_parameter_description_list:
            assert(isinstance(vcellParam, VCELLOPT.ParameterDescription))
            param_model_value = getmodelvalue(model, str(vcellParam.name))
            assert(isinstance(param_model_value, COPASI.CModelValue))
            param_model_value_ref = param_model_value.getInitialValueReference()
            assert(isinstance(param_model_value_ref, COPASI.CCopasiObject))
            fit_item = COPASI.CFitItem(data_model)
            assert(isinstance(fit_item, COPASI.CFitItem))
            fit_item.setObjectCN(param_model_value_ref.getCN())
            fit_item.setStartValue(vcellParam.initialValue)
            fit_item.setLowerBound(COPASI.CCopasiObjectName(str(vcellParam.minValue)))
            fit_item.setUpperBound(COPASI.CCopasiObjectName(str(vcellParam.maxValue)))
            # todo: what about scale?
            # add the fit item to the correct parameter group
            optimization_item_group = fit_problem.getParameter("OptimizationItemList")
            assert(isinstance(optimization_item_group, COPASI.CCopasiParameterGroup))
            optimization_item_group.addParameter(fit_item)
            # addParameter makes a copy of the fit item, so we have to get it back
            # fit_item = optimization_item_group.getParameter(0)
            # assert(isinstance(fit_item,COPASI.CFitItem))
        model.compileIfNecessary()
        # print optimization_item_group.printToString()  # debug anyway, not present in COPASI build 140

        # --------------------------------------------------------------------------------------
        #  Run the optimization (N times)
        # --------------------------------------------------------------------------------------
        least_error = 1e8
        param_names = []
        param_values = []
        num_obj_func_evals = 0
        num_params_to_fit = fit_problem.getOptItemSize()

        for i in range(0, vcell_opt_problem.numberOfOptimizationRuns):
            try:
                print ("This can take some time...")
                initialize = (i == 0)
                result = fit_task.processWithOutputFlags(initialize, COPASI.CCopasiTask.NO_OUTPUT)  # NO_OUTPUT
            except:
                print "Unexpected error:", sys.exc_info()[0]
                return 1

            if result is False:
                sys.stderr.write("An error occured while running the Parameter estimation.\n")
                # data_model.saveModel('test_failed.cps', True)
                sys.stderr.write("fit_task warning: '" + str(fit_task.getProcessWarning()) + "'")
                sys.stderr.write("fit_task error: '" + str(fit_task.getProcessError()) + "'")
                # check if there are additional error messages
                if COPASI.CCopasiMessage.size() > 0:
                    # print the messages in chronological order
                    sys.stderr.write(COPASI.CCopasiMessage.getAllMessageText())
                return 1

            current_func_value = fit_problem.getSolutionValue()
            print "currFuncValue = " + str(current_func_value)
            if (current_func_value < least_error) or (i == 0):         # current run has the smallest error so far
                num_obj_func_evals = fit_problem.getFunctionEvaluations()

                param_names = []
                param_values = []
                for j in range(0, num_params_to_fit):
                    opt_item = fit_problem.getOptItemList()[j]
                    param_name = opt_item.getObject().getCN().getRemainder().getRemainder().getElementName(0)
                    param_names.append(param_name)
                    param_value = fit_problem.getSolutionVariables().get(j)
                    param_values.append(param_value)
                    print "param " + param_name + " --> " + str(param_value)
                assert isinstance(current_func_value, float)
                least_error = current_func_value
        # result = data_model.saveModel('test_succeeded.cps', True)
        # assert(result==True)

        opt_run = VCELLOPT.OptRun()
        opt_run.optProblem = vcell_opt_problem
        opt_run.statusMessage = "complete"
        opt_run.status = VCELLOPT.OptRunStatus.Complete
        opt_result_set = VCELLOPT.OptResultSet()
        opt_result_set.numFunctionEvaluations = num_obj_func_evals
        opt_result_set.objectiveFunction = least_error
        opt_result_set.optParameterValues = []
        param_value_dict = dict(zip(param_names, param_values))
        for param_name in param_names:
            opt_parameter_value = VCELLOPT.OptParameterValue(param_name, param_value_dict[param_name])
            opt_result_set.optParameterValues.append(opt_parameter_value)
        opt_run.optResultSet = opt_result_set
        return opt_run

    finally:
        os.unlink(tmp_export_csv_file.name)


def getmodelvalue(model, object_name):
    model_values = model.getModelValues()
    assert (isinstance(model_values, COPASI.ModelValueVectorN))
    return model_values.getByName(object_name)


def print_model(model):
    assert (isinstance(model, COPASI.CModel))

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
    model_values = model.getModelValues()
    assert (isinstance(model_values, COPASI.ModelValueVectorN))
    print "ModelValues size = " + str(model_values.size())
    for index in range(0, model_values.size()):
        model_value = model_values.get(index)
        assert (isinstance(model_value, COPASI.CModelValue))
        print "model_value [{0}]: objectName = {1}, commonName = {2}, displayName = {3}".format(
            str(index), str(model_value.getObjectName()), str(model_value.getCN()),
            str(model_value.getObjectDisplayName()))

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

