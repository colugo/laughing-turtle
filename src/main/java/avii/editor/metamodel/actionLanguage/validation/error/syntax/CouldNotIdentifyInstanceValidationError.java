package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class CouldNotIdentifyInstanceValidationError extends BaseActionLanguageValidationError {

	private String _instanceName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public CouldNotIdentifyInstanceValidationError(String instanceName, int line, IActionLanguageSyntax syntax)
	{
		this._instanceName = instanceName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Couldn't identify instance called '" + _instanceName + "' -> '" + _syntax + "'";
	}

}
