package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class InvalidDelayUnitsValidationException extends BaseActionLanguageValidationError {

	
	private int _lineNumber;
	private String _delayUnits;
	private IActionLanguageSyntax _lineSyntax;

	public InvalidDelayUnitsValidationException(IActionLanguageSyntax lineSyntax, int lineNumber, String delayUnits) {
		this._lineSyntax = lineSyntax;
		this._lineNumber = lineNumber;
		this._delayUnits = delayUnits;
	}
	
	public String explainError()
	{
		return "line : " + _lineNumber + ":" + this._lineSyntax + " -> ' Invalid units for delay :" + _delayUnits + "'. Valid Units are - MilliSecond, Second, Minute, Hour, Day";
	}

}
