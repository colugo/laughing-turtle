package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class UnknownValidationError extends BaseActionLanguageValidationError {

	
	private IActionLanguageSyntax _lineSyntax;
	private int _lineNumber;
	private String _message = null;

	public UnknownValidationError(IActionLanguageSyntax lineSyntax, int lineNumber) {
		this._lineSyntax = lineSyntax;
		this._lineNumber = lineNumber;
	}
	
	public UnknownValidationError(String message) {
		this._message = message;
	}
	
	
	public String explainError()
	{
		if(this._message == null)
		{
			return "line : " + _lineNumber + " -> '" + _lineSyntax + "'";
		}
		return "Unknown Error : " + _message;
	}

}
