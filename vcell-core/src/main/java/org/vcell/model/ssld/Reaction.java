package org.vcell.model.ssld;

import java.util.Scanner;

public abstract class Reaction {

    private final Annotation annotation = new Annotation();

    public abstract String getName();
    public abstract void setName(String name);
    public abstract String writeReaction();
    public abstract void loadReaction(SsldModel model, Scanner sc);

    public void setAnnotationString(String annotationString) {
        this.annotation.setAnnotation(annotationString);
    }
    public Annotation getAnnotation() {
        return annotation;
    }
    public String getAnnotationString() {
        return annotation.getAnnotation();
    }

}
