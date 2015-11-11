package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelations;

public class Renamable_SelectAnyManyInstancesRelatedByRelations extends BaseRenamableActionLanguage {

	Syntax_SelectAnyManyInstancesRelatedByRelations _syntax;
	
	@Override
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_SelectAnyManyInstancesRelatedByRelations) syntax;
	}

	@Override
	public void renameRelation(String oldRelationName, String newRelationName) {
		this._syntax.setRelationName(oldRelationName, newRelationName);
	}

}
