package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityRelation;

public class ReflexiveEntityRelationMustHaveVerbPhrasesValidationError implements IValidationError{
	private String _relationName;

	public ReflexiveEntityRelationMustHaveVerbPhrasesValidationError(EntityRelation relation) {
		this._relationName = relation.getName();
	}
	
	public String explainError() {
		return "Relation '"+_relationName+"' is reflexive and must have verb phrases that are different from one another.";
	}

}
