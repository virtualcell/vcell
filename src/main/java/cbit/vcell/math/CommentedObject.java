package cbit.vcell.math;

import java.io.Serializable;
import java.util.Objects;

import org.vcell.util.Commented;
import org.vcell.util.EqualsUtil;
import org.vcell.util.Matchable;

/**
 * a default implementation of Commented
 * @author gweatherby
 *
 */
@SuppressWarnings("serial")
public abstract class CommentedObject implements Serializable, Commented, Matchable {
	
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

	@Override
	public int hashCode() {
		return Objects.hash(afterComment,beforeComment);
	}

	/**
	 * semantic equality
	 */
	@Override
	public boolean compareEqual(Matchable obj) {
		Boolean b = EqualsUtil.typeCompare(this,obj);
		if (b != null ) {
			return b;
		}
		CommentedObject other = (CommentedObject) obj;
		if (!Objects.equals(afterComment, other.afterComment))  {
			return false;
		}
		return Objects.equals(beforeComment, other.beforeComment); 
	}
	
}
