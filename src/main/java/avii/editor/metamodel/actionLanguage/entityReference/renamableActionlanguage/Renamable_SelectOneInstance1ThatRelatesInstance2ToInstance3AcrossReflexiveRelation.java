package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation;

public class Renamable_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation extends BaseRenamableActionLanguage {

	private Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation _syntax;
	
	@Override
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation) syntax;
	}

	@Override
	public void renameRelation(String oldRelationName, String newRelationName) {
		this._syntax.setRelationName(newRelationName);
	}

}
