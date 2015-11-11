/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceDestroyBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceDestroyActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_ReclassifyInstanceToClass implements IActionLanguageSyntax, IInstanceCreateActionLanguage, IInstanceDestroyActionLanguage,
		IVisitableActionLanguage {

	private static final String regex = "RECLASSIFY TO " + //
			ActionLanguageSyntaxHelper.GenericName + //
			ActionLanguageSyntaxHelper.EOL;

	private String _Class;

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Class = m.group(1);
		return this;
	}

	public String getRegex() {
		return regex;
	}

	@Override
	public String toString() {
		return "RECLASSIFY TO " + _Class + ";";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String get_Class() {
		return _Class;
	}

	public InstanceDestroyBean getDestroyInstance() {
		return new InstanceDestroyBean(this.get_Instance());
	}

	public InstanceCreateBean getCreateInstance() {
		InstanceCreateBean bean = new InstanceCreateBean(this.get_Instance(), _Class);
		bean.itsEffectiveFromTheNextLine();
		return bean;
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setClassName(String newName) {
		this._Class = newName;
	}

	public String get_Instance() {
		return "self";
	}

}
