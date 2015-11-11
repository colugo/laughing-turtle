package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityRelation;

public class ReflexiveEntityRelationMustHaveDifferingVerbPhrasesValidationError implements IValidationError{
	private String _relationName;

	public ReflexiveEntityRelationMustHaveDifferingVerbPhrasesValidationError(EntityRelation relation) {
		this._relationName = relation.getName();
	}
	
	public String explainError() {
		return "Relation '"+_relationName+"' is reflexive and must have verb phrases.";
	}

}
