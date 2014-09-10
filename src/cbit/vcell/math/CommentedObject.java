package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.Commented;

/**
 * a default implementation of Commented
 * @author gweatherby
 *
 */
public abstract class CommentedObject implements Serializable, Commented {
	
	private String beforeComment;
	private String afterComment;
	
	/**
	 * initial to no comments
	 */
	public CommentedObject() {
		beforeComment = null;
		afterComment = null;
	}

	@Override
	public String getBeforeComment() {
		return beforeComment;
	}
	
	@Override
	public void setBeforeComment(String beforeComment) {
		this.beforeComment = beforeComment;
	}
	
	@Override
	public String getAfterComment() {
		return afterComment;
	}
	
	@Override
	public void setAfterComment(String afterComment) {
		this.afterComment = afterComment;
	}
}
