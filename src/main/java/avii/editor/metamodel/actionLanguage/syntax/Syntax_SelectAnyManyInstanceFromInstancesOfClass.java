/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper.ENUM_ANY_MANY;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_SelectAnyManyInstanceFromInstancesOfClass implements IInstanceCreateActionLanguage, IVisitableActionLanguage {

	private static final String regex = "SELECT" + //
			ActionLanguageSyntaxHelper.AnyMany + //
			ActionLanguageSyntaxHelper.GenericName + //
			" FROM INSTANCES OF " + //
			ActionLanguageSyntaxHelper.GenericName + //
			ActionLanguageSyntaxHelper.EOL;

	private ENUM_ANY_MANY _AnyMany;
	private String _Instance1;
	private String _Class;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_AnyMany = m.group(1).equals("ANY") ? ENUM_ANY_MANY.ANY : ENUM_ANY_MANY.MANY;
		_Instance1 = m.group(2);
		_Class = m.group(3);

		return this;
	}

	@Override
	public String toString() {
		return "SELECT " + _AnyMany + " " + _Instance1 + " FROM INSTANCES OF " + _Class + ";";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public ENUM_ANY_MANY get_AnyMany() {
		return _AnyMany;
	}

	public String get_Class() {
		return _Class;
	}

	public String get_Instance() {
		return _Instance1;
	}

	public InstanceCreateBean getCreateInstance() {
		Boolean isInstanceSet = false;
		if (_AnyMany.equals(ENUM_ANY_MANY.MANY)) {
			isInstanceSet = true;
		}
		return new InstanceCreateBean(_Instance1, _Class, isInstanceSet);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setClassName(String newName) {
		this._Class = newName;
	}

}
