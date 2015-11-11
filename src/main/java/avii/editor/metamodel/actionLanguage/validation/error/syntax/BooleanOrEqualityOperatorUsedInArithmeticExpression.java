package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

public class BooleanOrEqualityOperatorUsedInArithmeticExpression extends BaseActionLanguageValidationError {

	@Override
	public String explainError()
	{
		return "Boolean and equality operators (AND, OR, ==, >, <, <=, >= ,!=) cannot be used in an arithmetic expression.";
	}
	
}
