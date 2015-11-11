package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstanceFromInstancesOfClass;

public class Renamable_SelectAnyManyInstancesFromInstancesOfClass extends BaseRenamableActionLanguage {

	private Syntax_SelectAnyManyInstanceFromInstancesOfClass _syntax = null;
	
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_SelectAnyManyInstanceFromInstancesOfClass) syntax;
	}

	public void renameClass(String newName) {
		this._syntax.setClassName(newName);
	}

}
