/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IOpenIfSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_IfLogic implements IActionLanguageSyntax, IVisitableActionLanguage, IOpenIfSyntax {

	private static final String regex = "IF " + //
			ActionLanguageSyntaxHelper.LogicExpression + //
			"(?!THEN) THEN$";

	private LogicExpressionTree _LogicExpressionTree = null;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {
		String logicString;
		logicString = line.replace("IF ", "").replace(" THEN", "");
		_LogicExpressionTree = new LogicExpressionTree(logicString);
		return this;
	}

	@Override
	public String toString() {
		return "IF " + _LogicExpressionTree.getRawLogic() + " THEN";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public LogicExpressionTree get_Logic() {
		return _LogicExpressionTree;
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setNewLogic(String newLogicString) {
		_LogicExpressionTree = new LogicExpressionTree(newLogicString);
	}

}
