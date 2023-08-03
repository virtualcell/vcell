/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.helpersetup;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class IOHelp {

    public final static DecimalFormat [] DF = new DecimalFormat[]{new DecimalFormat("0."),
        new DecimalFormat("0.0"), new DecimalFormat("0.00"), new DecimalFormat("0.000"),
        new DecimalFormat("0.0000"), new DecimalFormat("0.00000"),
        new DecimalFormat("0.000000"), new DecimalFormat("0.0000000"),
        new DecimalFormat("0.00000000")
    };
    
    public final static DecimalFormat scientificFormat = new DecimalFormat("0.00#E0");
    
    public final static String ERROR = "ERROR";
    public final static String SEPARATOR = System.getProperty("file.separator");
    
    // This method should be used when we know that the next few entries of the
    // scanner are a name in quotes, and we want to extract that collection of 
    // individual strings as a single string, and we want to drop the quotes. 
    public static String getNameInQuotes(Scanner sc){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb  = new StringBuilder();
        String s = sc.next();
        char quote = '"';
        char singlequote = '\'';
        char rightbracket = '}';
        if(!(s.charAt(0) == quote || s.charAt(0) == singlequote)){
            if(s.charAt(0) == rightbracket){
                return s;
            } else if(s.equals("***")){
                return s;
            } else {
                System.out.println("Helper.getNameInQuotes() was started on " + s 
                        + ", a string that did not begin with a quote.");
                return ERROR;
            }
        } else {
            s = s.substring(1,s.length());
            // Now look to see if it has a trailing quote
            if(s.charAt(s.length()-1) == quote || s.charAt(s.length()-1) == singlequote){
                s = s.substring(0,s.length()-1);
                return s;
            } else {
                sb.append(s);
                while(sc.hasNext()){
                    String s1 = sc.next();
                    if(s1.charAt(s1.length()-1) == quote || s1.charAt(s1.length()-1) == singlequote){
                        s1 = s1.substring(0,s1.length()-1);
                        sb.append(" ").append(s1);
                        break;
                    } else {
                        sb.append(" ").append(s1);
                    }
                }
                return sb.toString();
            }
        }
        // </editor-fold>
    }
    
    public static Scanner makeScanner(ArrayList<String> stringArray){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb = new StringBuilder();
        for (String string : stringArray) {
            sb.append(string);
            sb.append("\n");
        }
        return new Scanner(sb.toString());
        // </editor-fold>
    }
    
    public static File setFileType(File file, String type){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        String filename = file.getName();
        if(filename.length() > 4){
            if(filename.charAt(filename.length()-4) == '.'){
                String end = filename.substring(filename.length() - 3);
                if(!end.equals(type)){
                    filename = filename.substring(0,filename.length()-3) + type;
                }
            } else {
                filename = filename + "." + type;
            }
        } else {
            filename = filename + "." + type;
        }
        
        return new File(file.getParentFile(), filename);
        // </editor-fold>
    }
    
    public static String printArray(Object [] objects){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb = new StringBuilder("{");
        for(int i = 0;i<objects.length;i++){
            if(i != objects.length - 1){
                sb.append(objects[i].toString()).append(", ");
            } else {
                sb.append(objects[i].toString()).append("}");
            }
        }
        return sb.toString();
        // </editor-fold>
    }
    
    public static String printArray(double [] array){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=0;i<array.length;i++){
            if(i!=array.length-1){
                sb.append(Double.toString(array[i])).append(", ");
            } else {
                sb.append(Double.toString(array[i])).append("}");
            }
        }
        return sb.toString();
        // </editor-fold>
    }
    
    // Print without braces
    public static String printArrayList(ArrayList<Double> list, int decimalDigits){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb = new StringBuilder();
        for(Double d : list){
            sb.append(DF[decimalDigits].format(d)).append(" ");
        }
        return sb.toString();
        // </editor-fold>
    }
    
    public static void removeRecursive(Path path) throws IOException {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(Files.exists(path)){
            Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                                                throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc)
                                                                throws IOException {
                    // try to delete the file anyway, even if its attributes
                    // could not be read, since delete-only access is
                    // theoretically possible
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                                                                throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed; propagate exception
                        throw exc;
                    }
                }
            });
        }
        // </editor-fold>
    }
    
}
