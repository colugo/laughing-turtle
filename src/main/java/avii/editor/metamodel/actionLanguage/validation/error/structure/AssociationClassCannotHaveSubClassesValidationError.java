package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;

public class AssociationClassCannotHaveSubClassesValidationError implements IValidationError {

	private EntityClass _association;

	public AssociationClassCannotHaveSubClassesValidationError(EntityClass theAssociation)
	{
		this._association = theAssociation;
	}
	
	public String explainError() {
		String message = "Relation '" + this._association.getAssociationRelation().getName() + "' has association class '" + this._association.getName() + "' which has sub classes.";
		return message;
	}

}
