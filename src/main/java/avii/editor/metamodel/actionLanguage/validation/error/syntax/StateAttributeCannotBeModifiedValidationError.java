package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

public class StateAttributeCannotBeModifiedValidationError extends BaseActionLanguageValidationError {

	@Override
	public String explainError() {
		return "The 'state' attribute of all Classes cannot be written to.";
	}

}
