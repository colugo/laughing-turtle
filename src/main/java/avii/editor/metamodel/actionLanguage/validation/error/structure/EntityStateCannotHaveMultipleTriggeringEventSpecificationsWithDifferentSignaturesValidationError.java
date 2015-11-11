package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EntityStateCannotHaveMultipleTriggeringEventSpecificationsWithDifferentSignaturesValidationError implements IValidationError {

	private EntityClass _class = null;
	private EntityState _state = null;
	private EntityEventSpecification _spec1 = null;
	private EntityEventSpecification _spec2 = null;
	
	public EntityStateCannotHaveMultipleTriggeringEventSpecificationsWithDifferentSignaturesValidationError(EntityClass theClass, EntityState theState, EntityEventSpecification theSpec1, EntityEventSpecification theSpec2)
	{
		this._class = theClass;
		this._state = theState;
		this._spec1 = theSpec1;
		this._spec2 = theSpec2;
	}
	
	public String explainError() {
		return "EntityClass '" + this._class.getName() + "' has state '" + this._state.getName() + "' with a triggering event '" + this._spec1.getName() + "' and '" + this._spec2.getName() + "'";
	}

}
