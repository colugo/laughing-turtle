package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EntityRelationNotFoundInDomainValidationError extends BaseActionLanguageValidationError {

	private String _relationName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public EntityRelationNotFoundInDomainValidationError(String relationName, int line, IActionLanguageSyntax syntax)
	{
		this._relationName = relationName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Could not find an EntityRelation called '" + _relationName + "' -> '" + _syntax + "'";
	}

}
