package org.vcell.model.ssld;

import java.util.Scanner;

public class BoxGeometry {

    public final static String LX = "L_x";
    public final static String LY = "L_y";
    public final static String LZ_IN = "L_z_in";
    public final static String LZ_OUT = "L_z_out";
    public final static String NPARTX = "Partition Nx";
    public final static String NPARTY = "Partition Ny";
    public final static String NPARTZ = "Partition Nz";

    // All distances in nm
    private double x;
    private double y;
    private double zin; // Intracellular distance
    private double zout; // Extracellular distance

    private final int [] npart = new int[3];        // Number of partitions in each direction
    private final double [] dpart = new double[3];  // Size of partition

    public BoxGeometry() {
        // Assign default values
        x = 100.0;
        y = 100.0;
        zin = 90.0;
        zout = 10.0;

        for(int i=0;i<3;i++){
            npart[i] = 10;
            dpart[i] = 10.0;
        }
    }

    public void loadData(String dataString) {
        Scanner sc = new Scanner(dataString);
        while(sc.hasNextLine()) {
            String [] next = sc.nextLine().split(":");
            switch(next[0]) {
                case LX:
                    x = 1000 * Double.parseDouble(next[1].trim());
                    break;
                case LY:
                    y = 1000 * Double.parseDouble(next[1].trim());
                    break;
                case LZ_OUT:
                    zout = 1000 * Double.parseDouble(next[1].trim());
                    break;
                case LZ_IN:
                    zin = 1000 * Double.parseDouble(next[1].trim());
                    break;
                case NPARTX:
                    npart[0] = Integer.parseInt(next[1].trim());
                    setDpart(0);
                    break;
                case NPARTY:
                    npart[1] = Integer.parseInt(next[1].trim());
                    setDpart(1);
                    break;
                case NPARTZ:
                    npart[2] = Integer.parseInt(next[1].trim());
                    setDpart(2);
                    break;
                default:
                    System.out.println("BoxGeometry loadData received "
                            + "unexpected input. Input = " + SsldUtils.printArray(next));
            }
        }
        sc.close();
    }

    private void setDpart(int i) {
        switch(i) {
            case 0:
                dpart[0] = x / npart[0];
                break;
            case 1:
                dpart[1] = y / npart[1];
                break;
            case 2:
                dpart[2] = (zin + zout) / npart[2];
                break;
            default:
                System.out.println("Invalid input in BoxGeometry.setDpart(i).  Got i = " + i);
        }
    }


}
