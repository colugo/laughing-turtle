/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;


import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IBooleanOperatorToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicNode;


public class WhiteSpace extends BaseLogicNode implements IBooleanOperatorToken {
	String regex = "^\\s+";
	
	public String getRegex() {
		return regex;
	}
}
