package cbit.vcell.pslid;

import javax.swing.text.*;
import javax.swing.text.html.*;


/**
 * HTML parsing proceeds by calling a callback for
 * each and every piece of the HTML document.  This
 * callback class prints an indented structural 
 * listing of the HTML data.
 */
class HTMLParseLister extends HTMLEditorKit.ParserCallback
{
    int indentSize = 0;

    protected void indent() { 
        indentSize += 3;
    }
    protected void unIndent() {
        indentSize -= 3; if (indentSize < 0) indentSize = 0;
    }

    protected void pIndent() {
        for(int i = 0; i < indentSize; i++) System.out.print(" ");
    }

    public void handleText(char[] data, int pos) {
        pIndent();
        System.out.println("Text(" + data.length + " chars)");
    }

    public void handleComment(char[] data, int pos) {
        pIndent();
        System.out.println("Comment(" + data.length + " chars)");
    }

    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        pIndent();
        System.out.println("Tag start(<" + t.toString() + ">, " +
                           a.getAttributeCount() + " attrs)");
        indent();
    }

    public void handleEndTag(HTML.Tag t, int pos) {
        unIndent();
        pIndent();
        System.out.println("Tag end(</" + t.toString() + ">)");
    }

    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        pIndent();
        System.out.println("Tag(<" + t.toString() + ">, " +
                           a.getAttributeCount() + " attrs)");
    }

    public void handleError(String errorMsg, int pos){
        System.out.println("Parsing error: " + errorMsg + " at " + pos);
    }
}    

