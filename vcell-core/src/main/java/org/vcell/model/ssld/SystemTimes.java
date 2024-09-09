package org.vcell.model.ssld;

import org.vcell.util.springsalad.IOHelp;

import java.util.Scanner;
import java.io.*;

public class SystemTimes {

    /* ********** Strings for system IO ****************************/
    public static final String TOTALTIME = "Total time";
    public static final String DT = "dt";
    public static final String DTSPRING = "dt_spring";
    public static final String DTDATA = "dt_data";
    public static final String DTIMAGE = "dt_image";
    public static final String [] ALL_TIME_LABELS = {TOTALTIME, DT, DTSPRING,
            DTDATA, DTIMAGE};

    /* *********** All times stored as double values, in seconds ********/
    private double totalTime;
    private double dt;
    private double dtspring;
    private double dtdata;
    private double dtimage;

    /* *********** Constructor just sets some defaults *****************/
    public SystemTimes() {
        totalTime = 1e-2;
        dt = 1e-8;
        dtspring = 1e-9;
        dtdata = 1e-4;
        dtimage = 1e-4;
    }

    public double getTotalTime() {
        return totalTime;
    }
    public double getdt() {
        return dt;
    }
    public double getdtspring() {
        return dtspring;
    }
    public double getdtdata() {
        return dtdata;
    }
    public double getdtimage() {
        return dtimage;
    }

    public void loadData(String dataString) {
        // System.out.println(dataString);
        Scanner sc = new Scanner(dataString);
        while(sc.hasNextLine()){
            String [] next = sc.nextLine().split(":");
            switch(next[0]){
                case TOTALTIME:
                    totalTime = Double.parseDouble(next[1]);
                    break;
                case DT:
                    dt = Double.parseDouble(next[1]);
                    break;
                case DTSPRING:
                    dtspring = Double.parseDouble(next[1]);
                    break;
                case DTDATA:
                    dtdata = Double.parseDouble(next[1]);
                    break;
                case DTIMAGE:
                    dtimage = Double.parseDouble(next[1]);
                    break;
                default:
                    System.out.println("SystemTimes loadData received"
                            + " unexpected input line. Input = " + IOHelp.printArray(next));
            }
        }
        sc.close();
    }

}
