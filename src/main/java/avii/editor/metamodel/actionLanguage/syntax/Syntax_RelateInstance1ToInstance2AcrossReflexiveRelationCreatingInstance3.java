/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateAssociationBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateAssociationActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3 implements IInstanceCreateAssociationActionLanguage,
		IVisitableActionLanguage {

	private static final String regex = "RELATE " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" TO " + //
			ActionLanguageSyntaxHelper.GenericName + //
			" ACROSS " + //
			ActionLanguageSyntaxHelper.ReflexiveRelation + //
			" CREATING " + //
			ActionLanguageSyntaxHelper.GenericName + //
			ActionLanguageSyntaxHelper.EOL;

	private String _VerbPhrase;
	private String _Instance1;
	private String _Instance3;

	public String get_VerbPhrase() {
		return _VerbPhrase;
	}

	public String get_Instance1() {
		return _Instance1;
	}

	public String getRegex() {
		return regex;
	}

	public String get_Instance3() {
		return _Instance3;
	}

	public String get_Instance2() {
		return _Instance2;
	}

	public String get_Relation() {
		return _Relation;
	}

	private String _Instance2;
	private String _Relation;

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Instance1 = m.group(1);
		_Instance2 = m.group(2);
		_Relation = m.group(3);
		_VerbPhrase = m.group(4);
		_Instance3 = m.group(5);
		return this;
	}

	@Override
	public String toString() {
		return "RELATE " + _Instance1 + " TO " + _Instance2 + " ACROSS " + _Relation + ".\"" + _VerbPhrase + "\" CREATING " + _Instance3 + ";";
	}

	public boolean matchesLine(String line) {
		if (line.contains(">")) {
			return false;
		}
		return line.matches(regex);
	}

	public InstanceCreateAssociationBean getInstanceCreateBean() {
		return new InstanceCreateAssociationBean(_Instance3, _Relation);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setRelationName(String newRelationName) {
		this._Relation = newRelationName;
	}

}
