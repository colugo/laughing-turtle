package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EntityStateCannotHaveMultipleExitingTransitionsForTheSameEventSpecificationValidationError implements IValidationError {

	private EntityState _state;
	private EntityEventSpecification _spec;

	public EntityStateCannotHaveMultipleExitingTransitionsForTheSameEventSpecificationValidationError(EntityState theState, EntityEventSpecification theSpec)
	{
		this._state = theState;
		this._spec = theSpec;
	}
	
	public String explainError() {
		String message = "Class : '" + this._state.getOwningClass().getName() + "', State : '" + this._state.getName() + "' has multiple exiting transitions for event : '" + this._spec.getName() + "'";
		return message;
	}

}
