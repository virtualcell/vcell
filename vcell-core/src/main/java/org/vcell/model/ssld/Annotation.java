package org.vcell.model.ssld;

import java.util.Scanner;
import java.io.PrintWriter;

public class Annotation {

    private String annotation;

    public Annotation() {
        annotation = "";
    }

    public Annotation(String annotation) {
        this.annotation = annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    public String getAnnotation() {
        return annotation;
    }

    public void printAnnotation(PrintWriter p) {
        Scanner sc = new Scanner(annotation);
        sc.useDelimiter("\\n");
        while(sc.hasNext()) {
            p.println(sc.next());
        }
        sc.close();
    }

    @Override
    public String toString() {
        return annotation;
    }

}
