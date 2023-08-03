/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.helpersetup;

import java.util.Scanner;

public class SiteArrayParser {
    
    /**
     * This method takes in a string and expands it following some rules. 
     * The string can only contain numbers and brackets of various types.
     * A number by itself says we should create a site of the type indicated
     * by the number.  A number followed by a number in parentheses should be 
     * expanded like so: 5(6) = 5 5 5 5 5 5.  A string of numbers braces should
     * be repeated like so:  
     * [ 7(3) 5 4(2) ](3) = 7(3) 5 4(2) 7(3) 5 4(2) 7(3) 5 4(2)
     *                    = 7 7 7 5 4 4 7 7 7 5 4 4 7 7 7 5 4 4
     * 
     * Optionally, the string could include information on the initial 
     * state of the site by specifying a letter corresponding to the state
     * just after the number corresponding to the type but before the number
     * in parentheses.  So 
     * 2b(3) = 2b 2b 2b
     * 
     * We'll define several helper methods along the way.
     */
    
    // Returns true if the single entry is in the form Mx or Mx(N), where 
    // M is an integer, x is a string of lowercase letters all of the same 
    // type (aaaa or b or aa are all acceptable provided they correspond to an
    // initial state).
    private static boolean checkNumberInParentheses(String string){
        boolean passed = true;
        if(string.length()<3){
            // System.out.println("Paren False 1.");
            passed = false;
        } else if(string.charAt(0) != '(' || string.charAt(1) == '0' || string.charAt(string.length()-1) != ')'){
            // System.out.println("Paren False 2.");
            passed = false;
        } else {
            for(int i=1;i<string.length()-1;i++){
                if((int)string.charAt(i) < 48 || (int)string.charAt(i) > 57){
                    // System.out.println("Paren False 3. Given " + string.charAt(i));
                    passed = false;
                    break;
                }
            }
        }
        return passed;
    }
    
    private static boolean checkEntry(String entry){
        boolean passed = true;
        char [] chars = entry.toCharArray();
        // First character must be number between 1 and 9 inclusive
        if((int)chars[0] > 48 && (int)chars[0]<58){
            int i=1;
            while(i < chars.length){
                if((int)chars[i] > 47 && (int)chars[i] < 58){
                    i++;
                } else {
                    break;
                }
            }
            // If the next character is not "(", then we should have hit 
            // a string of lowercase letters, all the same letter
            if(i != chars.length){
                char next = chars[i];
                // System.out.println(next);
                while(i < chars.length){
                    if(chars[i] == '('){
                        break;
                    } else if(chars[i] == next){
                        i++;
                    } else {
                        // System.out.println("False 1.");
                        passed = false;
                        break;
                    }
                }
            }
            // If we haven't reached the end of the line, then we've hit a "("
            // and should have numbers
            if(i != chars.length){
                if(!checkNumberInParentheses(entry.substring(i))){
                    passed = false;
                }
            } 
        } else {
            // System.out.println("False 6.");
            passed = false;
        }
        return passed;
    }
    
    private static boolean checkEntries(String entries){
        Scanner sc = new Scanner(entries);
        boolean passed = true;
        while(sc.hasNext()){
            if(!checkEntry(sc.next())){
                // System.out.println("False in checkEntries()");
                passed = false;
                break;
            }
        }
        sc.close();
        return passed;
    }
    
    private static boolean checkBracketEntry(String bracketEntry){
        boolean passed = true;
        // Make sure this entry starts with a bracket!
        if(!(bracketEntry.charAt(0)=='[')){
            passed = false;
        } else {
            String dropLeading = bracketEntry.substring(1);
            String [] part = dropLeading.split("]");
            if(part.length != 2){
                passed = false;
            } else if(!checkEntries(part[0])){
                passed = false;
            } else if(!checkNumberInParentheses(part[1])){
                passed = false;
            }
        }
        return passed;
    }
    
    
    // I thought of a better way to check all brackets.  First just strip
    // out the left brackets.  The strip out the right brackets and replace
    // them with a number.  Then just run the checkentries method on everything
    // that is left. First just do a check to make sure that there are the same
    // number of left and right brackets.
    public static boolean checkInput(String nestedBrackets){
        boolean passed;
        int leftbrackets = 0;
        int rightbrackets = 0;
        for(int i=0;i<nestedBrackets.length();i++){
            char c = nestedBrackets.charAt(i);
            if(c == '['){
                leftbrackets++;
            } else if(c==']'){
                rightbrackets++;
            }
        }
        if(leftbrackets != rightbrackets || nestedBrackets.length() == 0){
            // System.out.println("Bracket number unequal or length = 0.");
            passed = false;
        } else {
            Scanner sc0 = new Scanner(nestedBrackets);
            sc0.useDelimiter("\\[");
            StringBuilder sb0 = new StringBuilder();
            while(sc0.hasNext()){
                sb0.append(sc0.next().trim()).append(" ");
            }
            // System.out.println("sb0 = \"" + sb0.toString() + "\"");
            Scanner sc1 = new Scanner(sb0.toString());
            sc1.useDelimiter("\\]\\(");
            StringBuilder sb1 = new StringBuilder();
            sb1.append(sc1.next().trim()).append(" ");
            // Put back in '5('
            while(sc1.hasNext()){
                sb1.append("5(").append(sc1.next().trim()).append(" ");
            }
            // System.out.println("sb1 = \"" + sb1.toString() + "\"");
            passed = checkEntries(sb1.toString());
            // System.out.println("After checkEntries: passed = " + passed);
        }
        return passed;
    }
    // We will always check the entry before giving it to this method, so we 
    // won't do any form checking here.
    private static String expandEntry(String entry){
        
        int index = entry.indexOf('(');
        if(index < 0){
            return entry + " ";
        } else {
            StringBuilder sb = new StringBuilder();
            int number = Integer.parseInt(entry.substring(index+1,entry.length()-1));
            String repeat = entry.substring(0, index);
            for(int i=0;i<number;i++){
                sb.append(repeat).append(" ");
            }
            return sb.toString();
        }
    }
    
    private static String expandEntries(String entries){
        Scanner sc = new Scanner(entries);
        StringBuilder sb = new StringBuilder();
        while(sc.hasNext()){
            sb.append(expandEntry(sc.next()));
        }
        return sb.toString();
    }
    
    private static String expandBracketEntry(String bracketEntry){
        String [] split = bracketEntry.split("]");
        String entries = split[0].substring(1);
        String expandedEntries = expandEntries(entries);
        int repeats = Integer.parseInt(split[1].substring(1,split[1].length()-1));
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<repeats;i++){
            sb.append(expandedEntries);
        }
        return sb.toString();
    }
    
    private static String expandInnerMostBracket(String input){
        int rightindex = input.indexOf(']');
        int leftindex = -1;
        int closingParen;
        if(rightindex < 0){
            return input;
        } else {
            closingParen = input.indexOf(')', rightindex);
            for(int i=rightindex-1;i>-1;i--){
                if(input.charAt(i) == '['){
                    leftindex = i;
                    break;
                }
            }
        }
        
        
        // Since the input was already checked, we know both indices are good.
        String out =  input.substring(0,leftindex).trim() + " " 
                + expandBracketEntry(input.substring(leftindex, closingParen+1)).trim() ;
        if(closingParen + 1 < input.length()){
            out += " " + input.substring(closingParen+1).trim();
        }
        return out;
    }
    
    public static String expandInput(String input){
        String old = input;
        String current = old;
        do{
            old = current;
            current = expandInnerMostBracket(current);
        } while(!old.equals(current));
        // I we had entries outside of brackets then they weren't expanded yet.
        return expandEntries(current);
    }
    
    /**
     * In order to assign letters to the state names in the site array 
     * editor, it will help to have a method which take in an integer and 
     * returns a string of lowercase letters according to the following rule:
     * input is 0-25, just return a through z
     * input 26-51, return aa, bb, ..., or zz
     * and continuing in that pattern.
     */
    
    public static String lowercaseString(int index){
        int repeats = 1 + index/26;
        int letterInt = index%26;
        int asciiValue = letterInt + 97;
        char letter = (char)asciiValue;
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<repeats;i++){
            sb.append(letter);
        }
        return sb.toString();
    }
    
}
