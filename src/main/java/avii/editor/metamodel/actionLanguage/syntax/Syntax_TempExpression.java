/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.ITempDeclarationActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_TempExpression implements IActionLanguageSyntax, ITempDeclarationActionLanguageSyntax, IVisitableActionLanguage {

	private static final String regex = ActionLanguageSyntaxHelper.NotAnInstanceAttribute + //
			ActionLanguageSyntaxHelper.Equals + //
			ActionLanguageSyntaxHelper.LogicExpression + //
			ActionLanguageSyntaxHelper.EOL;

	private String _Name;
	private LogicExpressionTree _LogicExpressionTree = null;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {
		String logicString;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Name = m.group(1);
		logicString = m.group(2);
		_LogicExpressionTree = new LogicExpressionTree(logicString);
		return this;
	}

	@Override
	public String toString() {
		return "" + _Name + " = " + _LogicExpressionTree.getRawLogic() + ";";
	}

	public LogicExpressionTree get_Logic() {
		return _LogicExpressionTree;
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String getTempName() {
		return _Name;
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setNewLogic(String newLogicString) {
		_LogicExpressionTree = new LogicExpressionTree(newLogicString);
	}

}
