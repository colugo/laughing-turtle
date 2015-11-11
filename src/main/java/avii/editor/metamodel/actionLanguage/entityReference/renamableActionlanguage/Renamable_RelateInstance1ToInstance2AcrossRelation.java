package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_RelateInstance1ToInstance2AcrossRelation;

public class Renamable_RelateInstance1ToInstance2AcrossRelation extends BaseRenamableActionLanguage {
	Syntax_RelateInstance1ToInstance2AcrossRelation _syntax;
	
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_RelateInstance1ToInstance2AcrossRelation) syntax;
	}

	public void renameRelation(String oldRelationName, String newRelationName) {
		this._syntax.setRelationName(newRelationName);
	}

}
