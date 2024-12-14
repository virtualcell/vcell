package org.vcell.cli.testsupport;

public class OmexTestCase {

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
    public enum Status {
        PASS,
        FAIL,
        SKIP
    }

    public TestCollection test_collection;
    public String file_path;
    public Boolean should_fail;
    public Status known_status;
    public FailureType known_failure_type;
    public String known_failure_desc;

    // from csv
    public OmexTestCase(String[] csvLine) {
        test_collection = TestCollection.valueOf(csvLine[0]);
        file_path = csvLine[1];
        should_fail = Boolean.parseBoolean(csvLine[2]);
        if (csvLine[3].trim().isEmpty()) {
            known_status = null;
        } else {
            known_status = Status.valueOf(csvLine[3]);
        }
        if (csvLine[4].trim().isEmpty()) {
            known_failure_type = null;
        } else {
            known_failure_type = FailureType.valueOf(csvLine[4]);
        }
        known_failure_desc = parseCsvStringField(csvLine[5]);
    }

    // to csv
    public String[] toCsvLine() {
        return new String[] {
                test_collection.toString(),
                file_path,
                should_fail.toString(),
                (known_status !=null) ? known_status.toString() : "",
                (known_failure_type !=null) ? known_failure_type.toString() : "",
                printCsvStringField(known_failure_desc)
        };
    }

    @Override
    public String toString() {
        return "OmexTestCase{" +
                "test_collection=" + test_collection +
                ", file_path='" + file_path + '\'' +
                ", should_fail=" + should_fail +
                ", known_status=" + known_status +
                ", known_failure_type=" + known_failure_type +
                ", known_failure_desc="+((known_failure_desc!=null)?('\'' + known_failure_desc + '\''):null) +
                '}';
    }
    private String parseCsvStringField(String str) {
        str = str.strip();
        // strip leading and trailing double quotes
        if (str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length()-1);
        }
        // strip escaped double quotes
        str = str.replace("\"\"", "\"");
        if (str.isEmpty()){
            return null;
        }
        return str;
    }

    private String printCsvStringField(String str) {
        if (str == null){
            return "";
        }
        return "\"" + str.replace("\"", "\"\"") + "\"";
    }
}
