package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class DuplicateIdentifierFoundValidationError extends BaseActionLanguageValidationError {

	private String _identifierName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public DuplicateIdentifierFoundValidationError(String instanceName, int line, IActionLanguageSyntax syntax)
	{
		this._identifierName = instanceName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Found duplicate identifier called '" + _identifierName + "' -> '" + _syntax + "'";
	}

}
