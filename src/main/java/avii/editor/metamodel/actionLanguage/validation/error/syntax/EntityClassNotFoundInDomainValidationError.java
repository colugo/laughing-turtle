package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EntityClassNotFoundInDomainValidationError extends BaseActionLanguageValidationError {

	private String _className;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public EntityClassNotFoundInDomainValidationError(String className, int line, IActionLanguageSyntax syntax)
	{
		this._className = className;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Could not find an EntityClass called '" + _className + "' -> '" + _syntax + "'";
	}

}
