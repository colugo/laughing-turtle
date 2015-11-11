/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceSelectRelatedByBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceSelectRelatedByActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IActionLanguageRelation;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IRelationList;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper.ENUM_ANY_MANY;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.RelationList;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic implements IInstanceSelectRelatedByActionLanguage, IVisitableActionLanguage {
	private static final String regex = "SELECT" + //
			ActionLanguageSyntaxHelper.AnyMany + //
			ActionLanguageSyntaxHelper.GenericName + //
			" RELATED BY " + //
			ActionLanguageSyntaxHelper.RelationCain + //
			" WHERE " + //
			ActionLanguageSyntaxHelper.LogicExpression + //
			ActionLanguageSyntaxHelper.EOL;

	private ENUM_ANY_MANY _AnyMany;
	private String _Instance1;
	private RelationList relationList;
	private LogicExpressionTree _LogicExpressionTree;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {
		String logicString;
		String relationString;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_AnyMany = m.group(1).equals("ANY") ? ENUM_ANY_MANY.ANY : ENUM_ANY_MANY.MANY;
		_Instance1 = m.group(2);
		relationString = m.group(3);
		logicString = m.group(4);
		_LogicExpressionTree = new LogicExpressionTree(logicString);
		relationList = new RelationList(relationString);

		return this;
	}

	@Override
	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append("SELECT " + _AnyMany + " " + _Instance1 + " RELATED BY ");

		output.append(relationList.GetInitialInstance() + "->");
		for (IActionLanguageRelation relation : relationList) {
			output.append("" + relation.get_Name() + "");
			if (relation.get_VerbPhrase() != null && relation.get_VerbPhrase() != "") {
				output.append(".\"" + relation.get_VerbPhrase() + "\"");
			}
			output.append("->");
		}
		if (!relationList.isEmpty()) {
			output.setLength(output.length() - 2);
		}
		output.append(" WHERE " + _LogicExpressionTree.getRawLogic() + ";");

		return output.toString();
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String get_Instance1() {
		return _Instance1;
	}

	public ENUM_ANY_MANY get_AnyMany() {
		return _AnyMany;
	}

	public IRelationList get_Relations() {
		return relationList;
	}

	public LogicExpressionTree get_Logic() {
		return _LogicExpressionTree;
	}

	public InstanceSelectRelatedByBean getSelectBean() {
		Boolean isInstanceSet = false;
		if (_AnyMany.equals(ENUM_ANY_MANY.MANY)) {
			isInstanceSet = true;
		}
		InstanceSelectRelatedByBean selectBean = new InstanceSelectRelatedByBean(_Instance1, relationList.GetInitialInstance(), isInstanceSet);
		Iterator<IActionLanguageRelation> iterator = relationList.iterator();
		while (iterator.hasNext()) {
			IActionLanguageRelation relation = iterator.next();
			selectBean.AddRelation(relation.get_Name());
		}
		return selectBean;
	}
	
	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setRelationName(String oldRelationName, String newRelationName) {
		this.relationList.renameRelation(oldRelationName, newRelationName);	
	}

	public void setNewLogic(String newLogicString) {
		_LogicExpressionTree = new LogicExpressionTree(newLogicString);
	}

}
