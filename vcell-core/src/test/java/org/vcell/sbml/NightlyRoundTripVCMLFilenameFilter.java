package org.vcell.sbml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

public class NightlyRoundTripVCMLFilenameFilter implements FilenameFilter {

    private static Set<String> exemptTests;

    // Static Initializer, equivalent to a static "contractor"
    static {
        Set<String> superSet = new HashSet<>();
        superSet.addAll(NightlyRoundTripVCMLFilenameFilter.slowTestSet());
        superSet.addAll(NightlyRoundTripVCMLFilenameFilter.outOfMemorySet());
        superSet.addAll(NightlyRoundTripVCMLFilenameFilter.largeFileSet());
        NightlyRoundTripVCMLFilenameFilter.exemptTests = superSet;
    }
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".vcml") && !NightlyRoundTripVCMLFilenameFilter.exemptTests.contains(name);
    }

    static Set<String> slowTestSet() {
        Set<String> slowModels = new HashSet<>();
        slowModels.add("biomodel_101981216.vcml");    // 29s
        slowModels.add("biomodel_147699816.vcml");    // 23s
        slowModels.add("biomodel_17326658.vcml");     // 24s
        slowModels.add("biomodel_188880263.vcml");    // 89s
        slowModels.add("biomodel_200301029.vcml");    // 124s (and fails with java.lang.OutOfMemoryError: GC overhead limit exceeded)
        slowModels.add("biomodel_200301683.vcml");    // 37s
        slowModels.add("biomodel_28625786.vcml");     // 57s
        slowModels.add("biomodel_34826524.vcml");     // 129s
        slowModels.add("biomodel_47429473.vcml");     // 194s
        slowModels.add("biomodel_59361239.vcml");     // 116s
        slowModels.add("biomodel_60799209.vcml");     // 41s
        slowModels.add("biomodel_61699798.vcml");     // 69s
        slowModels.add("biomodel_62467093.vcml");     // 23s
        slowModels.add("biomodel_62477836.vcml");     // 30s
        slowModels.add("biomodel_62585003.vcml");     // 24s
        slowModels.add("biomodel_66264206.vcml");     // 45s
        slowModels.add("biomodel_93313420.vcml");     // 137s
        slowModels.add("biomodel_9590643.vcml");      // 40s
        return slowModels;
    }

    static Set<String> outOfMemorySet() {
        Set<String> outOfMemoryModels = new HashSet<>();
        outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
        outOfMemoryModels.add("biomodel_200301029.vcml"); // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded (and 124s)
        outOfMemoryModels.add("biomodel_26455186.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
        outOfMemoryModels.add("biomodel_27192647.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
        outOfMemoryModels.add("biomodel_27192717.vcml");  // FAULT.OUT_OF_MEMORY) - Java heap space: failed reallocation of scalar replaced objects
        return outOfMemoryModels;
    }

    public static Set<String> largeFileSet() {
        Set<String> largeFiles = new HashSet<>();
        largeFiles.add("biomodel_101963252.vcml");
        return largeFiles;
    }
}
