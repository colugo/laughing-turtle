package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class CouldNotIdentifyInstanceSetValidationError extends BaseActionLanguageValidationError {

	private String _instanceSetName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public CouldNotIdentifyInstanceSetValidationError(String instanceSetName, int line, IActionLanguageSyntax syntax)
	{
		this._instanceSetName = instanceSetName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Couldn't identify instance set called '" + _instanceSetName + "' -> '" + _syntax + "'";
	}

}
