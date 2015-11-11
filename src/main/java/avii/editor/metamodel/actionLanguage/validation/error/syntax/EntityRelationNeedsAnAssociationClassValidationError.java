package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EntityRelationNeedsAnAssociationClassValidationError extends BaseActionLanguageValidationError {

	private String _relationName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	private String _associationClass;
	public EntityRelationNeedsAnAssociationClassValidationError(String relationName, String associationClass, int line, IActionLanguageSyntax syntax)
	{
		this._relationName = relationName;
		this._associationClass = associationClass;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". EntityRelation called '" + _relationName + " must create an association class of type '"+_associationClass+"' -> '" + _syntax + "'";
	}

}
