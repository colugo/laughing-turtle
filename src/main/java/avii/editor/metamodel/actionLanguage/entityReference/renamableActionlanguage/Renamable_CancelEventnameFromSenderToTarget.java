package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CancelEventnameFromSenderToTarget;

public class Renamable_CancelEventnameFromSenderToTarget extends BaseRenamableActionLanguage {
	private Syntax_CancelEventnameFromSenderToTarget _syntax;
	
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_CancelEventnameFromSenderToTarget) syntax;
	}

	public void renameEvent(String newEventName) {
		this._syntax.setEventName(newEventName);
	}

}
