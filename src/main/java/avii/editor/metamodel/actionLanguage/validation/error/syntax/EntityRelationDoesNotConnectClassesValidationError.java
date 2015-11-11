package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class EntityRelationDoesNotConnectClassesValidationError extends BaseActionLanguageValidationError {

	private int _line;
	private IActionLanguageSyntax _syntax;
	private EntityRelation _relation;
	private EntityClass _classA;
	private EntityClass _classB;
	
	public EntityRelationDoesNotConnectClassesValidationError(int line, IActionLanguageSyntax syntax, EntityRelation relation, EntityClass classA, EntityClass classB)
	{
		this._relation = relation;
		this._classA = classA;
		this._classB = classB;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		String message = "Line : " + _line + ".Relation '" + _relation.getName() + "' does not connect '" + _classA.getName() + "' to '" + _classB.getName() + "' -> '" + _syntax + "'";
		if(this._relation.isReflexive() && ( this._classA.isGeneralisation() || this._classB.isGeneralisation() ))
		{
			message += ". Ensure that the verb phrase is correct for the specific generalisation class in the relationship.";
		}
		return message;
	}

}
