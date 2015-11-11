package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class InstanceCannotBeTreatedAsAnInstanceSet extends BaseActionLanguageValidationError {

	private String _instanceName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public InstanceCannotBeTreatedAsAnInstanceSet(String instanceName, int line, IActionLanguageSyntax syntax)
	{
		this._instanceName = instanceName;
		this._line = line;
		this._syntax = syntax;
	}
	
	public String explainError() {
		return "Line : " + _line + ". Instance :  '" + _instanceName + "' cannot be treated as an instance set. -> '" + _syntax + "'";
	}

}
