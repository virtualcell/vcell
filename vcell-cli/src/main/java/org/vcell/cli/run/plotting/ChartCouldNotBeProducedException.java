package org.vcell.cli.run.plotting;

public class ChartCouldNotBeProducedException extends RuntimeException {
    public ChartCouldNotBeProducedException(){
        super();
    }

    public ChartCouldNotBeProducedException(String message){
        super(message);
    }

    public ChartCouldNotBeProducedException(Throwable cause){
        super(cause);
    }

    public ChartCouldNotBeProducedException(String message, Throwable cause){
        super(message, cause);
    }
}
