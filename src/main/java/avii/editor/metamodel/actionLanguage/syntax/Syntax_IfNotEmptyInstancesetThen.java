/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IOpenIfSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_IfNotEmptyInstancesetThen implements IActionLanguageSyntax, IVisitableActionLanguage, IOpenIfSyntax {

	private static final String regex = "IF NOT EMPTY " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" THEN$";
	private String _Instance;

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Instance = m.group(1);
		return this;
	}

	public String getRegex() {
		return regex;
	}

	@Override
	public String toString() {
		return "IF NOT EMPTY " + _Instance + " THEN";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String get_Instance() {
		return _Instance;
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

}
