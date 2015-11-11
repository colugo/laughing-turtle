package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class AttributeNotFoundInClassValidationError extends BaseActionLanguageValidationError {

	private String _className;
	private int _line;
	private IActionLanguageSyntax _syntax;
	private String _attributeName;
	public AttributeNotFoundInClassValidationError(String className, int line, IActionLanguageSyntax syntax, String attributeName)
	{
		this._attributeName = attributeName;
		this._className = className;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Could not find an attribute called '" + _attributeName + "' on class '" + _className + "' -> '" + _syntax + "'";
	}

}
