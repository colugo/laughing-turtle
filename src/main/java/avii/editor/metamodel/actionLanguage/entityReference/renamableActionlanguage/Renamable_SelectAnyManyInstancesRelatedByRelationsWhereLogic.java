package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;

public class Renamable_SelectAnyManyInstancesRelatedByRelationsWhereLogic extends BaseRenamableActionLanguage {

	Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic _syntax;
	
	@Override
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic) syntax;
	}

	@Override
	public void renameRelation(String oldRelationName, String newRelationName) {
		this._syntax.setRelationName(oldRelationName, newRelationName);
	}
	
	@Override
	public void renameAttribute(String theClassNeme, String theOldAttributeName, String theNewAttributeName,
			StateInstanceLifespanManager instanceLifespanManager, int lineNumber) {
		LogicExpressionTree tree = this._syntax.get_Logic();
		
		try
		{
			String newLogicString = this.renameAttributeInLogicExpressionTree(theClassNeme, theOldAttributeName, theNewAttributeName, instanceLifespanManager, tree, lineNumber);
			this._syntax.setNewLogic(newLogicString);
		}
		catch(Exception e){}
	}
	
}
