package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class GenericLogicExpressionFailureValidationError extends BaseActionLanguageValidationError {

	
	private IActionLanguageSyntax _lineSyntax;
	private int _lineNumber;
	private String _validationMessage;

	public GenericLogicExpressionFailureValidationError(String message, IActionLanguageSyntax lineSyntax, int lineNumber) {
		this._lineSyntax = lineSyntax;
		this._lineNumber = lineNumber;
		this._validationMessage = message;
	}
	
	public String explainError()
	{
		return "line : " + _lineNumber + " -> There is some error in the logic'" + _lineSyntax + "' -> " + _validationMessage;
	}

}
