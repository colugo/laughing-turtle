/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;


import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IEqualityOperatorToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicEqualityParentNode;

public class LessThenEqualityToken extends BaseLogicEqualityParentNode implements IEqualityOperatorToken {
	String regex = "^(<)(?!=)";
	
	public String getRegex() {
		return regex;
	}
	
	@Override
	public boolean calculateValue(double left, double right) {
		return left < right;
	}
	
	@Override
	public boolean calculateValue(String left, String right) {
		return left.compareTo(right) < 0;
	}
	
}
