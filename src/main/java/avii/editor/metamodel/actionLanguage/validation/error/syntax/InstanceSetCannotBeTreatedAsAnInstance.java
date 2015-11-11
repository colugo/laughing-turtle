package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class InstanceSetCannotBeTreatedAsAnInstance extends BaseActionLanguageValidationError {

	private String _instanceSetName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public InstanceSetCannotBeTreatedAsAnInstance(String instanceSetName, int line, IActionLanguageSyntax syntax)
	{
		this._instanceSetName = instanceSetName;
		this._line = line;
		this._syntax = syntax;
	}
	
	public String explainError() {
		return "Line : " + _line + ". Instance set :  '" + _instanceSetName + "' cannot be treated as an instance. -> '" + _syntax + "'";
	}

}
