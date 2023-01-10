package org.vcell.sbml;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Predicate;


@Category(Fast.class)
public class VcmlTestSuiteFiles {

    private final static String[] allTestFiles = new String[]{
            "lumped_reaction_no_size_in_rate.vcml",
            "lumped_reaction_proper_size_in_rate.vcml",
            "lumped_reaction_local_size_in_rate.vcml",
            "__export_adv_test.vcml",
            "biomodel_100596964.vcml",
            "biomodel_100961371.vcml",
            "biomodel_101962320.vcml",
            "biomodel_101963252.vcml",
            "biomodel_101981216.vcml",
            "biomodel_102061382.vcml",
            "biomodel_102802266.vcml",
            "biomodel_105608907.vcml",
            "biomodel_10829774.vcml",
            "biomodel_113655498.vcml",
            "biomodel_116704767.vcml",
            "biomodel_116898182.vcml",
            "biomodel_116898345.vcml",
            "biomodel_116929912.vcml",
            "biomodel_116929971.vcml",
            "biomodel_116930032.vcml",
            "biomodel_12119723.vcml",
            "biomodel_123269393.vcml",
            "biomodel_123269480.vcml",
            "biomodel_123465498.vcml",
            "biomodel_123465505.vcml",
            "biomodel_124562627.vcml",
            "biomodel_12522025.vcml",
            "biomodel_12522025_spatial.vcml",
            "biomodel_13714636.vcml",
            "biomodel_13717231.vcml",
            "biomodel_13717282.vcml",
            "biomodel_13736736.vcml",
            "biomodel_145545992.vcml",
            "biomodel_14647285.vcml",
            "biomodel_147699816.vcml",
            "biomodel_148700996.vcml",
            "biomodel_149491513.vcml",
            "biomodel_154208982.vcml",
            "biomodel_154210963.vcml",
            "biomodel_154961582.vcml",
            "biomodel_155016832.vcml",
            "biomodel_156134818.vcml",
            "biomodel_158495696.vcml",
            "biomodel_16404713.vcml",
            "biomodel_16763273.vcml",
            "biomodel_16804037.vcml",
            "biomodel_168717401.vcml",
            "biomodel_169993006.vcml",
            "biomodel_17028306.vcml",
            "biomodel_17098642.vcml",
            "biomodel_171423478.vcml",
            "biomodel_171423486.vcml",
            "biomodel_171423593.vcml",
            "biomodel_171423851.vcml",
            "biomodel_171423920.vcml",
            "biomodel_171423957.vcml",
            "biomodel_172076998.vcml",
            "biomodel_17257105.vcml",
            "biomodel_17263179.vcml",
            "biomodel_17326658.vcml",
            "biomodel_174341395.vcml",
            "biomodel_176334674.vcml",
            "biomodel_185577495.vcml",
            "biomodel_188880263.vcml",
            "biomodel_18894555.vcml",
            "biomodel_189321805.vcml",
            "biomodel_189512756.vcml",
            "biomodel_189513183.vcml",

            "biomodel_200301029.vcml",
            "biomodel_200301683.vcml",
            "biomodel_200965116.vcml",
            "biomodel_200999311.vcml",
            "biomodel_201022999.vcml",
            "biomodel_20253928.vcml",
            "biomodel_203052143.vcml",
            "biomodel_203656156.vcml",
            "biomodel_205406319.vcml",
            "biomodel_206022012.vcml",
            "biomodel_20754836.vcml",
            "biomodel_209284198.vcml",
            "biomodel_211839191.vcml",
            "biomodel_217669650.vcml",
            "biomodel_220138697.vcml",
            "biomodel_220138948.vcml",
            "biomodel_22403233.vcml",
            "biomodel_22403238.vcml",
            "biomodel_22403244.vcml",
            "biomodel_22403250.vcml",
            "biomodel_22403358.vcml",
            "biomodel_22403576.vcml",
            "biomodel_22523922.vcml",
            "biomodel_225440511.vcml",
            "biomodel_22681429.vcml",
            "biomodel_229605883.vcml",
            "biomodel_232498815.vcml",
            "biomodel_26454052.vcml",
            "biomodel_26454463.vcml",
            "biomodel_26455186.vcml",
            "biomodel_26581203.vcml",
            "biomodel_26928347.vcml",
            "biomodel_27071354.vcml",
            "biomodel_27072412.vcml",
            "biomodel_27072419.vcml",
            "biomodel_27072426.vcml",
            "biomodel_27087758.vcml",
            "biomodel_27088050.vcml",
            "biomodel_27088120.vcml",
            "biomodel_27192647.vcml",
            "biomodel_27192717.vcml",
            "biomodel_28136576.vcml",
            "biomodel_28138132.vcml",
            "biomodel_28139443.vcml",
            "biomodel_28625786.vcml",
            "biomodel_28730491.vcml",
            "biomodel_2912851.vcml",
            "biomodel_2913730.vcml",
            "biomodel_2915537.vcml",
            "biomodel_2917738.vcml",
            "biomodel_2917788.vcml",
            "biomodel_2917999.vcml",
            "biomodel_2930915.vcml",
            "biomodel_2962862.vcml",
            "biomodel_29897263.vcml",

            "biomodel_31523791.vcml",
            "biomodel_31584491.vcml",
            "biomodel_32288619.vcml",
            "biomodel_32568171.vcml",
            "biomodel_32568356.vcml",
            "biomodel_32579611.vcml",
            "biomodel_34826524.vcml",
            "biomodel_34855932.vcml",
            "biomodel_35789302.vcml",
            "biomodel_36053554.vcml",
            "biomodel_36230715.vcml",
            "biomodel_36275161.vcml",
            "biomodel_38086434.vcml",
            "biomodel_40882931.vcml",
            "biomodel_40883478.vcml",
            "biomodel_40883509.vcml",
            "biomodel_43726934.vcml",
            "biomodel_47429473.vcml",
            "biomodel_49411430.vcml",

            "biomodel_50584157.vcml",
            "biomodel_55178308.vcml",
            "biomodel_55396830.vcml",
            "biomodel_59280306.vcml",
            "biomodel_59361239.vcml",
            "biomodel_60113862.vcml",
            "biomodel_60203358.vcml",
            "biomodel_60227051.vcml",
            "biomodel_60647264.vcml",
            "biomodel_60647373.vcml",
            "biomodel_60705749.vcml",
            "biomodel_60705777.vcml",
            "biomodel_60799209.vcml",
            "biomodel_61340695.vcml",
            "biomodel_61414583.vcml",
            "biomodel_61629922.vcml",
            "biomodel_61680876.vcml",
            "biomodel_61699798.vcml",
            "biomodel_62467093.vcml",
            "biomodel_62477836.vcml",
            "biomodel_62585003.vcml",
            "biomodel_62849940.vcml",
            "biomodel_63307133.vcml",
            "biomodel_6436213.vcml",
            "biomodel_65182838.vcml",
            "biomodel_65183024.vcml",
            "biomodel_65183094.vcml",
            "biomodel_65183128.vcml",
            "biomodel_65311813.vcml",
            "biomodel_66264206.vcml",
            "biomodel_66264973.vcml",
            "biomodel_66265579.vcml",

            "biomodel_74924130.vcml",
            "biomodel_7681482.vcml",
            "biomodel_77305266.vcml",
            "biomodel_7803961.vcml",
            "biomodel_7803976.vcml",
            "biomodel_81284732.vcml",
            "biomodel_81992349.vcml",
            "biomodel_82065439.vcml",
            "biomodel_82250339.vcml",
            "biomodel_82798486.vcml",
            "biomodel_82799056.vcml",
            "biomodel_82799247.vcml",
            "biomodel_82799266.vcml",
            "biomodel_83091496.vcml",
            "biomodel_83446023.vcml",
            "biomodel_83932776.vcml",
            "biomodel_83932806.vcml",
            "biomodel_84069156.vcml",
            "biomodel_84235320.vcml",
            "biomodel_84275910.vcml",
            "biomodel_84982474.vcml",
            "biomodel_84982814.vcml",
            "biomodel_84985561.vcml",
            "biomodel_89712092.vcml",
            "biomodel_89712092_nonspatial.vcml",

            "biomodel_52337206_nonspatial.vcml", // MassActionSolver

            "biomodel_91133993.vcml",
            "biomodel_91134220.vcml",
            "biomodel_91134296.vcml",
            "biomodel_91134339.vcml",
            "biomodel_91141200.vcml",
            "biomodel_91141358.vcml",
            "biomodel_91147280.vcml",
            "biomodel_91162809.vcml",
            "biomodel_91162818.vcml",
            "biomodel_91164078.vcml",
            "biomodel_91164682.vcml",
            "biomodel_91986407.vcml",
            "biomodel_92354366.vcml", // public, not published
            "biomodel_9254662.vcml",
            "biomodel_92705462.vcml",
            "biomodel_93313420.vcml",
            "biomodel_93386467.vcml",
            "biomodel_94538871.vcml",
            "biomodel_94891280.vcml",
            "biomodel_95094548.vcml",
            "biomodel_95177642.vcml",
            "biomodel_95674618.vcml",
            "biomodel_95675197.vcml",
            "biomodel_95675441.vcml",
            "biomodel_95676312.vcml",
            "biomodel_95682290.vcml",
            "biomodel_95686613.vcml",
            "biomodel_95693513.vcml",
            "biomodel_95693624.vcml",
            "biomodel_95706942.vcml",
            "biomodel_95707047.vcml",
            "biomodel_9590643.vcml",
            "biomodel_98139292.vcml",
            "biomodel_98139299.vcml",
            "biomodel_98147638.vcml",
            "biomodel_98150237.vcml",
            "biomodel_98174143.vcml",
            "biomodel_98296160.vcml",

            "biomodel_165181964.vcml",
            "biomodel_111113048_smaller.vcml",

            // more public models
            "biomodel_92383390.vcml",
            "biomodel_92830796.vcml",
            "biomodel_92839940.vcml",
            "biomodel_92845118.vcml",
            "biomodel_92847452.vcml",
            "biomodel_92908902.vcml",
            "biomodel_92932169.vcml",
            "biomodel_92942045.vcml",
            "biomodel_92944917.vcml",
            "biomodel_92946371.vcml",
            "biomodel_92951324.vcml",
            "biomodel_92952350.vcml",
            "biomodel_92955722.vcml",
            "biomodel_92967115.vcml",
            "biomodel_92968671.vcml",
            "biomodel_92981603.vcml",

    };

    public static String[] getVcmlTestCases() {
        Predicate<String> testFilter = t -> true;

        return Arrays.stream(allTestFiles).filter(testFilter).toArray(String[]::new);
    }

    public static InputStream getVcmlTestCase(String testFile) {
        if (!Arrays.stream(allTestFiles).anyMatch(file -> file.equals(testFile))) {
            throw new RuntimeException("file not found for VCell Published Test Suite test "+testFile);
        }
        try {
            return getFileFromResourceAsStream("vcml_published/"+testFile);
        }catch (FileNotFoundException e){
            try {
                return getFileFromResourceAsStream("vcml_testmodels/"+testFile);
            }catch (FileNotFoundException e2){
                throw new RuntimeException("failed to find test case file in vcml_published/ and vcml_testmodels/: "+e2.getMessage(), e2);
            }
        }
    }

     private static InputStream getFileFromResourceAsStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = VcmlTestSuiteFiles.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Test
    public void test_read_sbmlFile() {
        InputStream inputStream = getVcmlTestCase(allTestFiles[0]);
        Assert.assertTrue(inputStream != null);
    }

}
