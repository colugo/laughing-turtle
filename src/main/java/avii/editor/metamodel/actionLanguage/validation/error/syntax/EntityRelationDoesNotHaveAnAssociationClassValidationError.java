package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EntityRelationDoesNotHaveAnAssociationClassValidationError extends BaseActionLanguageValidationError {

	private String _relationName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public EntityRelationDoesNotHaveAnAssociationClassValidationError(String relationName, int line, IActionLanguageSyntax syntax)
	{
		this._relationName = relationName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". EntityRelation called '" + _relationName + " does not have an association class -> '" + _syntax + "'";
	}

}
