package org.vcell.cli.run;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Predicate;


@Tag("Fast")
public class BSTSBasedTestSuiteFiles {

    private final static String[] allTestFiles = new String[]{
        "misc-projects/BIOMD0000000005.omex",
        "vcml/Powers-Pflugers-Arch-2016-Drosophila-synaptic-strength.omex",
        "sbml-core/Caravagna-J-Theor-Biol-2010-tumor-suppressive-oscillations.omex",
        "sbml-core/Parmar-BMC-Syst-Biol-2017-iron-distribution.omex",
        "sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-NRM.omex",
        "sbml-core/Ciliberto-J-Cell-Biol-2003-morphogenesis-checkpoint-Fehlberg.omex",
        "sbml-core/Szymanska-J-Theor-Biol-2009-HSP-synthesis.omex",
        "sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-SSA.omex",
        "sbml-core/Ciliberto-J-Cell-Biol-2003-morphogenesis-checkpoint-continuous.omex",
        "sbml-core/Tomida-EMBO-J-2003-NFAT-translocation.omex",
        "sbml-core/Vilar-PNAS-2002-minimal-circardian-clock.omex",
        "sbml-core/Edelstein-Biol-Cybern-1996-Nicotinic-excitation.omex",
        "sbml-core/Varusai-Sci-Rep-2018-mTOR-signaling-LSODA-LSODAR-SBML.omex",
        "sbml-core/Elowitz-Nature-2000-Repressilator.omex",
        "sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-continuous.omex",
        "synths/docker_image/SingularityImageExecutesSimulationsSuccessfully/1.execution-should-succeed.omex",
        "synths/results_report/SimulatorGeneratesReportsOfSimulationResults/1.execution-should-succeed.omex",
        "synths/combine_archive/WhenACombineArchiveHasAMasterFileSimulatorOnlyExecutesThisFile/1.execution-should-succeed.omex",
        "synths/combine_archive/WhenACombineArchiveHasNoMasterFileSimulatorExecutesAllSedDocuments/1.execution-should-succeed.omex",
        "synths/combine_archive/CombineArchiveHasSedDocumentsWithSameNamesInDifferentInNestedDirectories/1.execution-should-succeed.omex",
        "synths/combine_archive/CombineArchiveHasSedDocumentsInNestedDirectories/1.execution-should-succeed.omex",
        "synths/log/SimulatorReportsTheStatusOfTheExecutionOfSedDocuments/1.execution-should-succeed.omex",
        "synths/log/SimulatorReportsTheStatusOfTheExecutionOfSedOutputs/1.execution-should-succeed.omex",
        "synths/log/SimulatorReportsTheStatusOfTheExecutionOfCombineArchives/1.execution-should-succeed.omex",
        "synths/log/SimulatorReportsTheStatusOfTheExecutionOfSedTasks/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/3.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/1.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithMultipleSubTasks/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorProducesMultiplePlots/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorProducesMultiplePlots/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithChanges/1.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithChanges/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsDataGeneratorsWithDifferentShapes/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithFunctionalRanges/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsDataSetsWithDifferentShapes/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithVectorRanges/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsComputeModelChanges/1.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsComputeModelChanges/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsMultipleReportsPerSedDocument/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorProducesLogarithmic2DPlots/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorProducesLogarithmic2DPlots/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorCanResolveModelSourcesDefinedByUriFragmentsAndInheritChanges/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsModelsSimulationsTasksDataGeneratorsAndReports/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsModelAttributeChanges/1.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsModelAttributeChanges/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithNestedRepeatedTasks/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithLogarithmicUniformRanges/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithLinearUniformRanges/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsUniformTimeCoursesWithNonZeroInitialTimes/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsMultipleTasksPerSedDocument/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsAlgorithmParameters/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsUniformTimeCoursesWithNonZeroOutputStartTimes/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithFunctionalRangeVariables/1.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithFunctionalRangeVariables/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorProducesLinear2DPlots/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorProducesLinear2DPlots/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithNestedFunctionalRanges/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/2.execution-should-succeed.omex",
        "synths/sedml/SimulatorCanResolveModelSourcesDefinedByUriFragments/1.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsSubstitutingAlgorithms/3.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsSubstitutingAlgorithms/4.execution-should-succeed.omex",
        "synths/sedml/SimulatorSupportsSubstitutingAlgorithms/1.execute-should-fail.omex",
        "synths/sedml/SimulatorSupportsSubstitutingAlgorithms/2.execution-should-succeed.omex"
    };

    public static String[] getBSTSTestCases() {
        Predicate<String> testFilter = t -> true;

        return Arrays.stream(allTestFiles).filter(testFilter).toArray(String[]::new);
    }

    public static InputStream getBSTSTestCase(String testFile) {
        if (!Arrays.stream(allTestFiles).anyMatch(file -> file.equals(testFile))) {
            throw new RuntimeException("file not found for VCell Published Test Suite test "+testFile);
        }
        try {
            return getFileFromResourceAsStream(testFile);
        }catch (FileNotFoundException e){
            throw new RuntimeException("failed to find test case file '"+testFile+"': " + e.getMessage(), e);
        }
    }

     private static InputStream getFileFromResourceAsStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = BSTSBasedTestSuiteFiles.class.getResourceAsStream("/bsts-omex/"+fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Test
    public void test_read_BSTS_omex_file() {
        InputStream inputStream = getBSTSTestCase(allTestFiles[0]);
        Assertions.assertTrue(inputStream != null);
    }

}
