package cbit.vcell.opt;

import java.util.List;

import org.vcell.util.Issue;

/**
 * Insert the type's description here.
 * Creation date: (3/3/00 12:08:00 AM)
 * @author: 
 */
public abstract class ObjectiveFunction {
/**
 * ObjectiveFunction constructor comment.
 * @param name java.lang.String
 * @param exp cbit.vcell.parser.Expression
 */
public ObjectiveFunction() {
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 2:30:00 PM)
 * @param issueList java.util.Vector
 */
public abstract void gatherIssues(List<Issue> issueList);


/**
 * Insert the method's description here.
 * Creation date: (8/2/2005 2:12:06 PM)
 * @return java.lang.String
 */
public abstract String getVCML();
}