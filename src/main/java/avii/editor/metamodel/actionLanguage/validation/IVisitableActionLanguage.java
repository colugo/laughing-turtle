package main.java.avii.editor.metamodel.actionLanguage.validation;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public interface IVisitableActionLanguage extends IActionLanguageSyntax {
	public void accept(IActionLanguageVisitor visitor, int lineNumber);
}
