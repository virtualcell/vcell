package org.vcell.cli.testsupport;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class OmexTestingDatabase {

    public enum TestDataRepo {
        vcell, sysbio
    }
    public enum TestCollection {
        VCELL_BIOMD(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/misc-projects"),
        VCELL_BSTS_VCML(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/vcml"),
        VCELL_BSTS_SBML_CORE(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/sbml-core"),
        VCELL_BSTS_SYNTHS(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/synths"),
        SYSBIO_BIOMD(TestDataRepo.sysbio, "omex_files");

        public final TestDataRepo repo;
        public final String pathPrefix;

        TestCollection(TestDataRepo repo, String pathPrefix) {
            this.repo = repo;
            this.pathPrefix = pathPrefix;
        }
    }

    public static OmexTestCase queryOmexTestCase(List<OmexTestCase> omexTestCases, String path) throws NoSuchElementException {
        return omexTestCases.stream().filter(tc -> tc.matchFileSuffix(path)).findFirst().orElseThrow();
    }

    // read a newline-delimited json file into a list of OmexTextCase objects
    public static List<OmexTestCase> loadOmexTestCases() throws IOException {
        List<OmexTestCase> testCases = new ArrayList<>();
        String fileName = "test_cases.ndjson";
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = OmexTestingDatabase.class.getResourceAsStream("/"+fileName);){
            if (inputStream == null) {
                throw new FileNotFoundException("file not found! " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                try (MappingIterator<OmexTestCase> it = objectMapper.readerFor(OmexTestCase.class).readValues(reader)) {
                    while (it.hasNext()) {
                        testCases.add(it.next());
                    }
                }
            }
        }
        return testCases;
    }

}
