package main.java.avii.editor.metamodel.actionLanguage.validation;

import java.util.ArrayList;

import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.IActionLanguageValidationError;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public interface IActionLanguageValidatorVisitor extends IActionLanguageVisitor {

	public void validatingForState(EntityState theState);
	public ArrayList<IActionLanguageValidationError> getValidationErrors();
	public void postValidate();
	public void setProcedure(EntityProcedure entityProcedure);
}
