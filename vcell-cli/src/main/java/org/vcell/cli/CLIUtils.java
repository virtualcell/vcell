package org.vcell.cli;

import com.google.common.io.Files;

import java.io.*;
import java.util.Objects;

public class CLIUtils {
//    private String tempDirPath = null;
    private String extractedOmexPath = null;

    public CLIUtils() {

    }

    @SuppressWarnings("UnstableApiUsage")
    public String getTempDir() {
        return Files.createTempDir().getAbsolutePath();
    }


    public static boolean removeDirs(File f) {
        try {
            deleteRecursively(f);
        } catch(IOException ex) {
            System.err.println("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    public static boolean makeDirs(File f) {
        if(f.exists()) {
            boolean isRemoved = removeDirs(f);
            if(!isRemoved)
                return false;
        }
        return f.mkdirs();
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                deleteRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    public static void convertIDAtoCSV(File f) {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find IDA file, failed with err: " + e.getMessage());
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            System.err.println("Unable to read line, failed with err: " + e.getMessage());
        }
        StringBuilder sb = new StringBuilder();
        while(line != null) {
            sb.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {
                System.err.println("Unable to read line, failed with err: " + e.getMessage());
            }
        }
        String fileAsString = sb.toString();
        fileAsString = fileAsString.replace("\t", ",");
        fileAsString = fileAsString.replace(":\n", "\n");
        fileAsString = fileAsString.replace(":", ",");

        f.delete();
        try {
            PrintWriter out = new PrintWriter(f);
            out.print(fileAsString);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find path, failed with err: "+e.getMessage());
        }
    }
}
