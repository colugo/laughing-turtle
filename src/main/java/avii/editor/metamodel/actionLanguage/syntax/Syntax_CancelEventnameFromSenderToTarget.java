/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_CancelEventnameFromSenderToTarget implements IVisitableActionLanguage {

	private static final String regex = "CANCEL " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" FROM " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" TO " + //
			ActionLanguageSyntaxHelper.GenericName + //
			ActionLanguageSyntaxHelper.EOL;

	private String _Eventname;
	private String _Sender;
	private String _Target;

	public String get_Eventname() {
		return _Eventname;
	}

	public String get_Sender() {
		return _Sender;
	}

	public String get_Target() {
		return _Target;
	}

	public IActionLanguageSyntax populateSyntax(String line) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Eventname = m.group(1);
		_Sender = m.group(2);
		_Target = main.java.avii.util.Text.removeTrailingSemiColon(m.group(3));
		return this;
	}

	@Override
	public String toString() {
		return "CANCEL " + _Eventname + " FROM " + _Sender + " TO " + _Target + ";";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String getRegex() {
		return regex;
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setEventName(String newEventName) {
		this._Eventname = newEventName;
	}

}
