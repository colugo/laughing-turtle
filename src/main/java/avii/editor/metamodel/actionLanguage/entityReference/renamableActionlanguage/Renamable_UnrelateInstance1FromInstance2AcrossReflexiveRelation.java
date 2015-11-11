package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation;

public class Renamable_UnrelateInstance1FromInstance2AcrossReflexiveRelation extends BaseRenamableActionLanguage {

	private Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation _syntax;
	
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation) syntax;
	}

	public void renameRelation(String oldRelationName, String newRelationName) {
		this._syntax.setRelationName(newRelationName);
	}

}
