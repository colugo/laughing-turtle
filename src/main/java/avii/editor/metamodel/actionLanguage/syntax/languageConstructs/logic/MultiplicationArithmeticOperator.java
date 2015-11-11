/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IMultiplyDivideOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException.LogicTreeValidationExceptionCodes;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicArithmeticParentNode;

public class MultiplicationArithmeticOperator extends BaseLogicArithmeticParentNode implements IMultiplyDivideOperatorToken, ILogicParentNode {
	String regex = "^\\*";

	public String getRegex() {
		return regex;
	}
	
	@Override
	public Object calculateValue(Object left, Object right) {
		//left and right can only be numbers
		return this.asDouble(left) * this.asDouble(right);
	}
	
	@Override
	protected void areDatatypesValidForOperator(IEntityDatatype left, IEntityDatatype right) {
		if(!areLeftAndRightNumericDatatypes(left,right))
		{
			addError(this, LogicTreeValidationExceptionCodes.e0006);
		}
	}
}
