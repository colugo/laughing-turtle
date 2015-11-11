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

public class Syntax_AttributeExpression implements IActionLanguageSyntax, IVisitableActionLanguage {

	private static final String regex = ActionLanguageSyntaxHelper.InstanceAttributeRegex + //
			ActionLanguageSyntaxHelper.Equals + //
			ActionLanguageSyntaxHelper.LogicExpression + //
			ActionLanguageSyntaxHelper.EOL;

	private String _name;
	private String _attribute;

	private LogicExpressionTree _logicExpressionTree = null;

	public String get_Attribute() {
		return _attribute;
	}

	public String get_Instance() {
		return _name;
	}

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {
		String logicString;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_name = m.group(1);
		_attribute = m.group(2);
		logicString = m.group(3);
		_logicExpressionTree = new LogicExpressionTree(logicString);
		return this;
	}

	public LogicExpressionTree get_Logic() {
		return _logicExpressionTree;
	}

	@Override
	public String toString() {
		return "" + _name + "." + _attribute + " = " + _logicExpressionTree.getRawLogic()+ ";";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setNewLogic(String newLogicString) {
		_logicExpressionTree = new LogicExpressionTree(newLogicString);
	}

	public void setAttributeName(String theNewAttributeName) {
		this._attribute = theNewAttributeName;
	}

	public void setInstanceName(String name) {
		this._name = name;		
	}

}
