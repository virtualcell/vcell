package org.vcell.cli.testsupport;

public class OmexTestCase {

    public enum Status {
        PASS,
        FAIL,
        SKIP
    }

    public OmexTestingDatabase.TestCollection test_collection;
    public String file_path;
    public Boolean should_fail;
    public Status known_status;
    public FailureType known_failure_type;
    public String known_failure_desc;

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

    public boolean matchFileSuffix(String path) {
        String fullPath = test_collection.pathPrefix + "/" + file_path;
        return fullPath.endsWith(path);
    }
}
