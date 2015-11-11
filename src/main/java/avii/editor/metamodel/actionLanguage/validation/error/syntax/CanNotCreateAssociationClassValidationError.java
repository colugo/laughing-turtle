package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class CanNotCreateAssociationClassValidationError extends BaseActionLanguageValidationError {

	private String _className;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public CanNotCreateAssociationClassValidationError(String className, int line, IActionLanguageSyntax syntax)
	{
		this._className = className;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Can not create an instance of association class : '" + _className + "' -> '" + _syntax + "'";
	}

}
