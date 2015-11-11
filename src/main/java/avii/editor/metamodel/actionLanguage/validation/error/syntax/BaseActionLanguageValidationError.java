package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.metamodel.entities.EntityState;

public abstract class BaseActionLanguageValidationError implements IActionLanguageValidationError {

	private EntityState _throwingState = null;
	
	public abstract String explainError();

	public EntityState getThrowingState() {
		return this._throwingState;
	}

	public void setThrowingState(EntityState theState) {
		this._throwingState = theState;
	}

}
