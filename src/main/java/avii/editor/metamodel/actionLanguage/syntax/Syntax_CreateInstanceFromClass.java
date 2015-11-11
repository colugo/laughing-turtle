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
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_CreateInstanceFromClass implements IInstanceCreateActionLanguage, IVisitableActionLanguage {

	private static final String regex = "CREATE " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" FROM " + //
			ActionLanguageSyntaxHelper.GenericName + //
			ActionLanguageSyntaxHelper.EOL;

	private String _instance;
	private String _Class;

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_instance = m.group(1);
		_Class = m.group(2);
		return this;
	}

	@Override
	public String toString() {
		return "CREATE " + _instance + " FROM " + _Class + ";";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String get_Instance() {
		return _instance;
	}

	public String get_Class() {
		return _Class;
	}

	public String getRegex() {
		return regex;
	}

	public InstanceCreateBean getCreateInstance() {
		return new InstanceCreateBean(_instance, _Class);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setClass(String newName) {
		this._Class = newName;
	}

	public void setInstance(String instanceName) {
		this._instance = instanceName;		
	}

}
