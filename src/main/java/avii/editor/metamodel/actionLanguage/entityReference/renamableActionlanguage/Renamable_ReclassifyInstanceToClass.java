package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ReclassifyInstanceToClass;

public class Renamable_ReclassifyInstanceToClass extends BaseRenamableActionLanguage {
	private Syntax_ReclassifyInstanceToClass _syntax;
	
	
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_ReclassifyInstanceToClass) syntax;
	}

	public void renameClass(String newName) {
		this._syntax.setClassName(newName);
	}

}
