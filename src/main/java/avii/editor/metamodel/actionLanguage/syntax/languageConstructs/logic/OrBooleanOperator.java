/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;


import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IBooleanOperatorToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicBooleanParentNode;

public class OrBooleanOperator extends BaseLogicBooleanParentNode implements IBooleanOperatorToken {
	String regex = "^ OR ";
	
	public String getRegex() {
		return regex;
	}
	
	@Override
	public Object calculateValue(Object left, Object right) {
		// left and right can only be bools
		return this.asBoolean(left) || this.asBoolean(right);
	}
}
