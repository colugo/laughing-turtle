package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;

public class InitialStateCannotHaveTriggeringEventValidationError implements IValidationError {

	private EntityClass _class = null;
	private EntityState _state = null;
	private EntityEventSpecification _spec = null;
	
	public InitialStateCannotHaveTriggeringEventValidationError(EntityClass theClass, EntityState theState, EntityEventSpecification theSpec)
	{
		this._class = theClass;
		this._state = theState;
		this._spec = theSpec;
	}
	
	public String explainError() {
		return "EntityClass '" + this._class.getName() + "' has initial state '" + this._state.getName() + "' with a triggering event '" + this._spec.getName() + "'";
	}

}
