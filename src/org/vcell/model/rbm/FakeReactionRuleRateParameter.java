package org.vcell.model.rbm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vcell.util.TokenMangler;

import cbit.vcell.mapping.MD5;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.ReactionRule;

public class FakeReactionRuleRateParameter {
	
	private static final String uniqueIdRoot = "ruleParameter";
	
	private final String reactionNameHash;
	private final RbmKineticLawParameterType parameterType;
	public final String fakeParameterName;
	
	public FakeReactionRuleRateParameter(ReactionRule reactionRule, RbmKineticLawParameterType parameterType) {
		this.reactionNameHash = getHash(reactionRule);
		this.parameterType = parameterType;
		this.fakeParameterName = uniqueIdRoot+"_"+parameterType.name()+"_"+reactionNameHash+"_"+TokenMangler.fixTokenStrict(reactionRule.getName());
	}
	
	private FakeReactionRuleRateParameter(String reactionNameHash, RbmKineticLawParameterType parameterType, String fakeParameterName) {
		this.reactionNameHash = reactionNameHash;
		this.parameterType = parameterType;
		this.fakeParameterName = fakeParameterName;
	}
	
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof FakeReactionRuleRateParameter)){
			return false;
		}
		FakeReactionRuleRateParameter other = (FakeReactionRuleRateParameter)obj;
		return fakeParameterName.equals(other.fakeParameterName);
	}
	
	private String getHash(ReactionRule reactionRule){
		return MD5.md5(reactionRule.getName(), 5);
	}
	
	@Override
	public int hashCode(){
		return fakeParameterName.hashCode();
	}
	
	@Override
	public String toString(){
		return fakeParameterName;
	}
	
	public RbmKineticLawParameterType getParameterType(){
		return this.parameterType;
	}
	
	public boolean isReactionRule(ReactionRule reactionRule){
		if (this.reactionNameHash.equals(getHash(reactionRule))){
			return true;
		}
		return false;
	}

	/**
	 * OK, learning to use Java regular expressions.  Overkill of course.
	 */
	public static FakeReactionRuleRateParameter fromString(String key) {
		String PREFIX = uniqueIdRoot;
		String REACTION_NAME_HASH = "hash";
		String HASH_PATTERN = "\\p{XDigit}*";
		String TYPE = "type";
		String TYPE_PATTERN = "\\p{Alpha}\\p{Alnum}*";
		String MANGLED_NAME = "name";
		String NAME_PATTERN = "[0-9a-zA-Z_]*";
		// ruleParameter_MassActionForwardRate_39da498_reaction_1
		// 
		Pattern p = Pattern.compile(PREFIX+"_(?<"+TYPE+">"+TYPE_PATTERN+")_(?<"+REACTION_NAME_HASH+">"+HASH_PATTERN+")_(?<"+MANGLED_NAME+">"+NAME_PATTERN+")");
		Matcher m = p.matcher(key);
		if (m.matches()){
			m.reset();
			m.find();
			String type = m.group(TYPE);
			String reactionNameHash = m.group(REACTION_NAME_HASH);
			String mangledName = m.group(MANGLED_NAME);
			return new FakeReactionRuleRateParameter(reactionNameHash, RbmKineticLawParameterType.valueOf(type), key);
		}else{
			return null;
		}
	}
	
}
