/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_FailAssertion implements IActionLanguageSyntax, IVisitableActionLanguage {

	private static final String regex = ActionLanguageSyntaxHelper.FailAssertion + //
			ActionLanguageSyntaxHelper.LogicExpression + //
			ActionLanguageSyntaxHelper.EOL;

	private LogicExpressionTree _LogicExpressionTree = null;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {
		String logicString;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		logicString = m.group(1);
		_LogicExpressionTree = new LogicExpressionTree(logicString);
		return this;
	}

	@Override
	public String toString() {
		return "FAIL" + _LogicExpressionTree.getRawLogic() + ";";
	}

	public LogicExpressionTree get_Logic() {
		return _LogicExpressionTree;
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setNewLogic(String newLogicString) {
		_LogicExpressionTree = new LogicExpressionTree(newLogicString);
	}

}
