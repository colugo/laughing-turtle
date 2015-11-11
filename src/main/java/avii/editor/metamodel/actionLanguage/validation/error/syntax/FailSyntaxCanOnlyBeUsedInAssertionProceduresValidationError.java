package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

public class FailSyntaxCanOnlyBeUsedInAssertionProceduresValidationError extends BaseActionLanguageValidationError {

	@Override
	public String explainError() {
		return "FAIL syntax can only be used in assertion procedures.";
	}

}
