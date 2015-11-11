package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationError;
import main.java.avii.editor.metamodel.entities.EntityState;

public interface IActionLanguageValidationError extends IValidationError {
	public EntityState getThrowingState();
	public void setThrowingState(EntityState theState);
}
