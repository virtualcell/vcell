// $Id: LineStringBuilder.java,v 1.3 2011/03/31 14:52:05 gerard Exp $
package org.vcell.util;

import edu.uchc.connjur.spectrumtranslator.CodeUtil;

/**
 * thin wrapper around {@link StringBuilder} to
 * allow adding content & newline
 * @author gerard
 * Copyright connjur.com
 *
 */
public class LineStringBuilder {
    public StringBuilder sb;

    public LineStringBuilder() {
        sb = new StringBuilder();
    }

    public LineStringBuilder(CharSequence s) {
        sb = new StringBuilder(s);
    }

    public LineStringBuilder(int capacity) {
        sb = new StringBuilder(capacity);
    }

    public LineStringBuilder(String s) {
        sb = new StringBuilder(s);
    }

    /**
     * write s, add newline
     * @param s
     */
    public void write(String s) {
        sb.append(s);
        sb.append(CodeUtil.NEWLINE);
    }

    public String toString() {
        return sb.toString();
    }

    public LineStringBuilder append(String s) {
        sb.append(s);
        return this;
    }

    public LineStringBuilder append(int i) {
        sb.append(i);
        return this;
    }

    public LineStringBuilder append(long value) {
        sb.append(value);
        return this;
    }

    public LineStringBuilder append(float value) {
        sb.append(value);
        return this;
    }

    public LineStringBuilder append(double value) {
        sb.append(value);
        return this;
    }

    public void clear() {
        sb.delete(0, sb.length());
    }

    public  LineStringBuilder newline() {
        sb.append(CodeUtil.NEWLINE);
        return this;
    }

}
/*
 * $Log: LineStringBuilder.java,v $
 * Revision 1.3  2011/03/31 14:52:05  gerard
 * delete functionality
 *
 * Revision 1.2  2011/03/24 14:50:06  gerard
 * support for copying Actors
 *
 * Revision 1.1  2011/03/11 14:07:48  gerard
 * Improve processing context;standardize format and CVS tag
 *
 */
