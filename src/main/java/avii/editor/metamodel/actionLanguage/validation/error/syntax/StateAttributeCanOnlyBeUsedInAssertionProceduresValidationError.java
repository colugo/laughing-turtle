package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

public class StateAttributeCanOnlyBeUsedInAssertionProceduresValidationError extends BaseActionLanguageValidationError {

	@Override
	public String explainError() {
		return "The 'state' attribute of all Classes can only be used in assertion procedures.";
	}

}
