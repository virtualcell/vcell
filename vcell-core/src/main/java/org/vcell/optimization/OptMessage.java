package org.vcell.optimization;

import java.io.Serializable;

public class OptMessage implements Serializable {
    public final String optID;
    public OptMessage(String optID){
        this.optID = optID;
    }

    public static class OptCommandMessage extends OptMessage {
        public OptCommandMessage(String optID){
            super(optID);
        }
    }

    public static class OptResponseMessage extends OptMessage {
        public final OptCommandMessage commandMessage;
        public OptResponseMessage(OptCommandMessage commandMessage){
            super((commandMessage!=null)?commandMessage.optID:null);
            this.commandMessage = commandMessage;
        }
        public OptResponseMessage(String optID, OptCommandMessage commandMessage){
            super(optID);
            this.commandMessage = commandMessage;
        }
    }

    public static class OptErrorResponseMessage extends OptResponseMessage {
        public final String errorMessage;
        public OptErrorResponseMessage(OptCommandMessage optCommandMessage, String errorMessage){
            super(optCommandMessage);
            this.errorMessage = errorMessage;
        }
        public String toString() {
            return super.toString() + "(id="+optID+", errorMessage='"+errorMessage+"')";
        }
    }

    //
    // job submit command/response
    //
    public static class OptJobRunCommandMessage extends OptCommandMessage {
        public final String optProblemJsonString;
        public OptJobRunCommandMessage(String optProblemJsonString) {
            super(null);
            this.optProblemJsonString = optProblemJsonString;
        }
        public String toString() {
            return super.toString() + "(id="+optID+"')";
        }
    }
    public static class OptJobRunResponseMessage extends OptResponseMessage {
        public OptJobRunResponseMessage(String optID, OptJobRunCommandMessage optJobRunCommandMessage){
            super(optID, optJobRunCommandMessage);
        }
        public String toString() {
            return super.toString() + "(id="+optID+")";
        }
    }

    //
    // job stop command/response
    //
    public static class OptJobStopCommandMessage extends OptCommandMessage {
        public OptJobStopCommandMessage(String optID){
            super(optID);
        }
        public String toString() {
            return super.toString() + "(id="+optID+"')";
        }
    }
    public static class OptJobStopResponseMessage extends OptResponseMessage {
        public OptJobStopResponseMessage(OptJobStopCommandMessage optJobStopCommandMessage){
            super(optJobStopCommandMessage);
        }
        public String toString() {
            return super.toString() + "(id="+optID+"')";
        }
    }

    //
    // job status command/response
    //
    public static class OptJobQueryCommandMessage extends OptCommandMessage {
        public OptJobQueryCommandMessage(String optID){
            super(optID);
        }
        public String toString() {
            return super.toString() + "(id="+optID+"')";
        }
    }
    public enum OptJobMessageStatus {
        QUEUED,
        FAILED,
        COMPLETE,
        RUNNING
    }
    public static class OptJobStatusResponseMessage extends OptResponseMessage {
        public final OptJobMessageStatus status;
        public final String statusMessage;
        public OptJobStatusResponseMessage(OptJobQueryCommandMessage optJobQueryCommandMessage,
                                           OptJobMessageStatus status, String statusMessage) {
            super(optJobQueryCommandMessage);
            this.status = status;
            this.statusMessage = statusMessage;
        }
        public String toString() {
            return super.toString() + "(id="+optID+", status='"+status+", statusMessage='"+statusMessage+"')";
        }
    }
    public static class OptJobSolutionResponseMessage extends OptResponseMessage {
        public final String optRunJsonString;
        public OptJobSolutionResponseMessage(OptJobQueryCommandMessage optJobQueryCommandMessage,
                                           String optRunJsonString) {
            super(optJobQueryCommandMessage);
            this.optRunJsonString = optRunJsonString;
        }
        public String toString() {
            return super.toString() + "(id="+optID+"')";
        }
     }
}
