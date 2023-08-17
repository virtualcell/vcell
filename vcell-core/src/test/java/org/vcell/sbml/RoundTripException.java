package org.vcell.sbml;


public class RoundTripException extends Exception {

    protected boolean wasTheExpectedResult;
    protected SEDMLExporterNightlyRoundTrip.SEDML_FAULT faultType;
    public RoundTripException(boolean wasTheExpectedResult, SEDMLExporterNightlyRoundTrip.SEDML_FAULT faultType){
        super();
        this._localConstructor(wasTheExpectedResult, faultType);
    }

    public RoundTripException(String message, boolean wasTheExpectedResult,
                              SEDMLExporterNightlyRoundTrip.SEDML_FAULT faultType){
        super(message);
        this._localConstructor(wasTheExpectedResult, faultType);
    }

    public RoundTripException(String message, Throwable cause, boolean wasTheExpectedResult,
                              SEDMLExporterNightlyRoundTrip.SEDML_FAULT faultType){
        super(message, cause);
        this._localConstructor(wasTheExpectedResult, faultType);
    }

    public RoundTripException(Throwable cause, boolean wasTheExpectedResult,
                              SEDMLExporterNightlyRoundTrip.SEDML_FAULT faultType){
        super(cause);
        this._localConstructor(wasTheExpectedResult, faultType);
    }

    protected RoundTripException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                                 boolean wasTheExpectedResult, SEDMLExporterNightlyRoundTrip.SEDML_FAULT faultType ){
        super(message, cause, enableSuppression, writableStackTrace);
        this._localConstructor(wasTheExpectedResult, faultType);
    }

    private void _localConstructor(boolean wasTheExpectedResult, SEDMLExporterNightlyRoundTrip.SEDML_FAULT faultType){
        this.wasTheExpectedResult = wasTheExpectedResult;
        this.faultType = faultType;
    }
}
