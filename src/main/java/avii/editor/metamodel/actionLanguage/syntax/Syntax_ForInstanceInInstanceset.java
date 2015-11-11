/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateFromInstanceSetActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_ForInstanceInInstanceset implements IActionLanguageSyntax, IInstanceCreateFromInstanceSetActionLanguage, IVisitableActionLanguage {

	private static final String regex = "FOR " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" IN " + ActionLanguageSyntaxHelper.GenericName + //
			" DO$";

	private String _Instance;
	private String _Instanceset;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Instance = m.group(1);
		_Instanceset = m.group(2);
		return this;
	}

	@Override
	public String toString() {
		return "FOR " + _Instance + " IN " + _Instanceset + " DO";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String get_Instance() {
		return _Instance;
	}

	public String get_Instanceset() {
		return _Instanceset;
	}

	public InstanceCreateBean getCreateInstance() {
		return new InstanceCreateBean(_Instance, _Instanceset);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

}
