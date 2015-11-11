package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityState;

public class InitialStateCannotHaveActionLanguageValidationError implements IValidationError {

	EntityState _state;
	
	public InitialStateCannotHaveActionLanguageValidationError(EntityState theState)
	{
		this._state = theState;
	}
	
	public String explainError() {
		String message = "Class '" + this._state.getOwningClass().getName() + "' has initial state '" + this._state.getName() + "' with Action Language, and is not allowed.";
		return message;
	}

}
