/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.viewers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class DirectoryMaker {
    
    public static DecimalFormat [] df = new DecimalFormat[]{new DecimalFormat("0."),
        new DecimalFormat("0.0"), new DecimalFormat("0.00"), new DecimalFormat("0.000"),
        new DecimalFormat("0.0000")};
    
    public static void makeDirectory(String name, int ntot){
        File dir = new File(name + "_FOLDER");
        dir.mkdir();
        File file = new File(dir, name + ".txt");
        PrintWriter p = null;
        try{
            p = new PrintWriter(new FileWriter(file), true);
            p.println("THIS WOULD BE THE INPUT FILE.");
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            if(p!=null){
                p.close();
            }
        }
        File header = new File(dir, name + "_HEADER.txt");
        p = null;
        try{
            p = new PrintWriter(new FileWriter(header), true);
            DirectoryMaker.writeHeader(p, ntot, 0.0001);
        } catch(IOException ie){
            ie.printStackTrace();
        } finally {
            if(p != null){
                p.close();
            }
        }
        double [] start1 = new double[]{-90,-90,90};
        double [] stop1 = new double[]{90,90,10};
        
        double [] start2 = new double[]{-90,-90,-90};
        double [] stop2 = new double[]{90,90,-10};
        File imageFile;
        for(int i=0;i<=ntot;i++){
            imageFile = new File(dir, name + numberPad(i) + ".txt");
            p = null;
            try{
                p = new PrintWriter(new FileWriter(imageFile), true);
                
                p.print("ID: " + 100000 + " " +  4.2 + " WHITE ");
                p.println(DirectoryMaker.lineCoordinates(ntot, i, start1, stop1));
                
                p.print("ID: " + 100001 + " " + 3.5 + " MAGENTA ");
                p.println(DirectoryMaker.circleCoordinates(ntot, i, 35, 10, 3));
                
                p.print("ID: " + 100002 + " " +  4.2 + " WHITE ");
                p.println(DirectoryMaker.lineCoordinates(ntot, i, start2, stop2));
                
                p.print("ID: " + 100003 + " " + 3.5 + " MAGENTA ");
                p.println(DirectoryMaker.circleCoordinates(ntot, i, 35, -10, 6));
                
                p.println("Link " + 100000 + " : " + 100001);
                p.println("Link " + 100001 + " : " + 100003);
                p.println("Link " + 100002 + " : " + 100003);
            } catch(IOException e){
                e.printStackTrace();
            } finally {
                if(p != null){
                    p.close();
                }
            }
        }
    }
    
    public static String numberPad(int i){
        String s = null;
        if(i<0){
            System.out.println("Oops, number should be non-negative.");
        } else if(i<10){
            s = "_000" + i;
        } else if(i<100){
            s = "_00" + i;
        } else if(i<1000){
            s = "_0" + i;
        } else if(i<10000){
            s = "_" + i;
        } else {
            System.out.println("Keep it under 10000 files, please.");
        }
        return s;
    }
    
    public static void writeHeader(PrintWriter p, int ntot, double dt){
        p.println("TotalTime " + ntot*dt);
        p.println("dtimage " + dt);
        p.println("xsize 100");
        p.println("ysize 100");
        p.println("zmin 10");
        p.println("zmax 100");
    }
    
    public static String lineCoordinates(int ntot, int n, double [] start, double [] stop){
        double [] coord = new double[3];
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<3;i++){
            coord[i] = (stop[i]-start[i])*((double)n/(double)ntot) + start[i];
            sb.append(df[4].format(coord[i])).append(" ");
        }
        return sb.toString();
    }
    
    public static String circleCoordinates(int ntot, int n, double radius, double height, double rotations){
        double [] coord = new double[3];
        double theta = 2.0 * Math.PI * rotations * (double)n/(double)ntot;
        coord[0] = radius*Math.cos(theta);
        coord[1] = radius*Math.sin(theta);
        coord[2] = height;
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<3;i++){
            sb.append(df[4].format(coord[i])).append(" ");
        }
        return sb.toString();
    }
    
    public static void main(String [] args){
        DirectoryMaker.makeDirectory("FirstTry", 100);
    }
    
}
