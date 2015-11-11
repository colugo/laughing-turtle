package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityState;

public class FirstInstructionOfGeneralisationStatemachineMustBeReclassifyValidationError implements IValidationError {

	private EntityState _state;

	public FirstInstructionOfGeneralisationStatemachineMustBeReclassifyValidationError(EntityState theState)
	{
		this._state = theState;
	}
	
	public String explainError() {
		String message = "Class : '" + this._state.getOwningClass().getName() + "', State : '" + this._state.getName() + "' is a generalisation class and the first statement is not a RECLASSIFY.";
		return message;
	}

}
