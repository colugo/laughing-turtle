package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CreateInstanceFromClass;

public class Renamable_CreateInstanceFromClass extends BaseRenamableActionLanguage {

	private Syntax_CreateInstanceFromClass _syntax = null;
	
	public void renameClass(String newName) {
		this._syntax.setClass(newName);
	}

	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_CreateInstanceFromClass) syntax;
	}
}
