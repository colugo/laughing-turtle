package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class InstanceNameFromSelectIsAnyAndShouldBeASingleValidationError extends BaseActionLanguageValidationError {

	private String _instanceName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public InstanceNameFromSelectIsAnyAndShouldBeASingleValidationError(String instanceName, int line, IActionLanguageSyntax syntax)
	{
		this._instanceName = instanceName;
		this._line = line;
		this._syntax = syntax;
	}
	
	public String explainError() {
		return "Line : " + _line + ". Instance sets from ANY selects should not end in 's' :  '" + _instanceName + "' -> '" + _syntax + "'";
	}

}
