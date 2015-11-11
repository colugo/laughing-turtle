package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EntityClassNotFoundInRelationValidationError extends BaseActionLanguageValidationError {

	private String _className;
	private int _line;
	private IActionLanguageSyntax _syntax;
	private String _relationName;
	public EntityClassNotFoundInRelationValidationError(String className, int line, IActionLanguageSyntax syntax, String relationName)
	{
		this._relationName = relationName;
		this._className = className;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Could not find a class called '" + _className + "' in relation '" + _relationName + "' -> '" + _syntax + "'";
	}

}
