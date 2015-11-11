/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;


import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IBracketToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicNode;

public class OpeningBracket extends BaseLogicNode implements IBracketToken {
	String regex = "^\\(";
	
	public String getRegex() {
		return regex;
	}
}
