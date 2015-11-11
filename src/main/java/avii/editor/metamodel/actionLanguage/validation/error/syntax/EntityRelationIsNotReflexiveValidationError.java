package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EntityRelationIsNotReflexiveValidationError extends BaseActionLanguageValidationError {

	private int _line;
	private IActionLanguageSyntax _syntax;
	private String _relationName;
	public EntityRelationIsNotReflexiveValidationError(int line, IActionLanguageSyntax syntax, String relationName)
	{
		this._relationName = relationName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ".Relation '" + _relationName + " is not reflexive! ' -> '" + _syntax + "'";
	}

}
