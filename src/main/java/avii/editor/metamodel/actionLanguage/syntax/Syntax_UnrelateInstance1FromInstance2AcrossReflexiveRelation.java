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

public class Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation implements IVisitableActionLanguage {

	private static final String regex = "UNRELATE " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" FROM " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" ACROSS " + //
			ActionLanguageSyntaxHelper.ReflexiveRelation + //
			ActionLanguageSyntaxHelper.EOL;

	private String _Instance1;
	private String _Instance2;
	private String _Relation;
	private String _Verb;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Instance1 = m.group(1);
		_Instance2 = m.group(2);
		;
		_Relation = m.group(3);
		_Verb = m.group(4);

		return this;
	}

	@Override
	public String toString() {
		return "UNRELATE " + _Instance1 + " FROM " + _Instance2 + " ACROSS " + _Relation + ".\"" + _Verb + "\";";
	}

	public boolean matchesLine(String line) {
		if (line.contains(">")) {
			return false;
		}
		return line.matches(regex);
	}

	public String get_Instance1() {
		return _Instance1;
	}

	public String get_Instance2() {
		return _Instance2;
	}

	public String get_Relation() {
		return _Relation;
	}

	public String get_Verb() {
		return _Verb;
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setRelationName(String newRelationName) {
		this._Relation = newRelationName;
	}

}
