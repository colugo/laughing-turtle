package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException;

public class LogicTreeValidationError extends BaseActionLanguageValidationError {

	private int _lineNumber;
	private IActionLanguageSyntax _syntax;
	private String _message;

	public LogicTreeValidationError(int lineNumber, IActionLanguageSyntax syntax, LogicTreeValidationException validationError)
	{
		this._lineNumber = lineNumber;
		this._syntax = syntax;
		this._message = validationError.toString();
	}
	
	@Override
	public String explainError() {
		return "Line : " + this._lineNumber + ": " + this._message + " : " + this._syntax.toString();
	}
	
}
