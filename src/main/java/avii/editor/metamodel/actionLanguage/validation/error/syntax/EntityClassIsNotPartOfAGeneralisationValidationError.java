package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EntityClassIsNotPartOfAGeneralisationValidationError extends BaseActionLanguageValidationError {

	private String _className;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public EntityClassIsNotPartOfAGeneralisationValidationError(String className, int line, IActionLanguageSyntax syntax)
	{
		this._className = className;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ".EntityClass called '" + _className + "' is not part of a generalisation -> '" + _syntax + "'";
	}

}
